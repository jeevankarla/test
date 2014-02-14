/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.product.product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.NullPointerException;
import java.lang.SecurityException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.network.DeprecatedNetworkServices;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.image.ScaleImage;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;


/**
 * Product Services
 */
public class ProductServices {

    public static final String module = ProductServices.class.getName();
    
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals;
    private static int rounding;
    public static final String resource_error = "OrderErrorUiLabels";
    static {
        decimals = 1;//UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
	

   // public static final String module = ProductServices.class.getName();
    public static final String resource = "ProductUiLabels";
    public static final String resourceError = "ProductErrorUiLabels";

    /**
     * Creates a Collection of product entities which are variant products from the specified product ID.
     */
    public static Map<String, Object> prodFindAllVariants(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Parent (virtual) product ID
        Map<String, Object> subContext = UtilMisc.makeMapWritable(context);
        subContext.put("type", "PRODUCT_VARIANT");
        return prodFindAssociatedByType(dctx, subContext);
    }

    /**
     * Finds a specific product or products which contain the selected features.
     */
    public static Map<String, Object> prodFindSelectedVariant(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Parent (virtual) product ID
        // * Map selectedFeatures  -- Selected features
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, String> selectedFeatures = UtilGenerics.checkMap(context.get("selectedFeatures"));
        List<GenericValue> products = FastList.newInstance();
        // All the variants for this products are retrieved
        Map<String, Object> resVariants = prodFindAllVariants(dctx, context);
        List<GenericValue> variants = UtilGenerics.checkList(resVariants.get("assocProducts"));
        for (GenericValue oneVariant: variants) {
            // For every variant, all the standard features are retrieved
            Map<String, String> feaContext = FastMap.newInstance();
            feaContext.put("productId", oneVariant.getString("productIdTo"));
            feaContext.put("type", "STANDARD_FEATURE");
            Map<String, Object> resFeatures = prodGetFeatures(dctx, feaContext);
            List<GenericValue> features = UtilGenerics.checkList(resFeatures.get("productFeatures"));
            boolean variantFound = true;
            // The variant is discarded if at least one of its standard features
            // has the same type of one of the selected features but a different feature id.
            // Example:
            // Input: (COLOR, Black), (SIZE, Small)
            // Variant1: (COLOR, Black), (SIZE, Large) --> nok
            // Variant2: (COLOR, Black), (SIZE, Small) --> ok
            // Variant3: (COLOR, Black), (SIZE, Small), (IMAGE, SkyLine) --> ok
            // Variant4: (COLOR, Black), (IMAGE, SkyLine) --> ok
            for (GenericValue oneFeature: features) {
                if (selectedFeatures.containsKey(oneFeature.getString("productFeatureTypeId"))) {
                    if (!selectedFeatures.containsValue(oneFeature.getString("productFeatureId"))) {
                        variantFound = false;
                        break;
                    }
                }
            }
            if (variantFound) {
                try {
                    products.add(delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", oneVariant.getString("productIdTo"))));
                } catch (GenericEntityException e) {
                    Map<String, String> messageMap = UtilMisc.toMap("errProductFeatures", e.toString());
                    String errMsg = UtilProperties.getMessage(resourceError,"productservices.problem_reading_product_features_errors", messageMap, locale);
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("products", products);
        return result;
    }

    /**
     * Finds product variants based on a product ID and a distinct feature.
     */
    public static Map<String, Object> prodFindDistinctVariants(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Parent (virtual) product ID
        // * String feature        -- Distinct feature name
        //TODO This service has not yet been implemented.
        return ServiceUtil.returnFailure();
    }

    /**
     * Finds a Set of feature types in sequence.
     */
    public static Map<String, Object> prodFindFeatureTypes(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Product ID to look up feature types
        Delegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        String productFeatureApplTypeId = (String) context.get("productFeatureApplTypeId");
        if (UtilValidate.isEmpty(productFeatureApplTypeId)) {
            productFeatureApplTypeId = "SELECTABLE_FEATURE";
        }
        Locale locale = (Locale) context.get("locale");
        String errMsg=null;
        Set<String> featureSet = new LinkedHashSet<String>();

        try {
            Map<String, String> fields = UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", productFeatureApplTypeId);
            List<String> order = UtilMisc.toList("sequenceNum", "productFeatureTypeId");
            List<GenericValue> features = delegator.findByAndCache("ProductFeatureAndAppl", fields, order);
            for (GenericValue v: features) {
                featureSet.add(v.getString("productFeatureTypeId"));
            }
            //if (Debug.infoOn()) Debug.logInfo("" + featureSet, module);
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errProductFeatures", e.toString());
            errMsg = UtilProperties.getMessage(resourceError,"productservices.problem_reading_product_features_errors", messageMap, locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        if (featureSet.size() == 0) {
            errMsg = UtilProperties.getMessage(resourceError,"productservices.problem_reading_product_features", locale);
            // ToDo DO 2004-02-23 Where should the errMsg go?
            Debug.logWarning(errMsg + " for product " + productId, module);
            //return ServiceUtil.returnError(errMsg);
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("featureSet", featureSet);
        return result;
    }

    /**
     * Builds a variant feature tree.
     */
    public static Map<String, Object> prodMakeFeatureTree(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Parent (virtual) product ID
        // * List featureOrder     -- Order of features
        // * Boolean checkInventory-- To calculate available inventory.
        // * String productStoreId -- Product Store ID for Inventory
        String productStoreId = (String) context.get("productStoreId");
        Locale locale = (Locale) context.get("locale");

        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        List<String> featureOrder = UtilMisc.makeListWritable(UtilGenerics.<String>checkCollection(context.get("featureOrder")));

        if (UtilValidate.isEmpty(featureOrder)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductFeatureTreeCannotFindFeaturesList", locale));
        }

        List<GenericValue> variants = UtilGenerics.checkList(prodFindAllVariants(dctx, context).get("assocProducts"));
        List<String> virtualVariant = FastList.newInstance();

        if (UtilValidate.isEmpty(variants)) {
            return ServiceUtil.returnSuccess();
        }
        List<String> items = FastList.newInstance();
        List<GenericValue> outOfStockItems = FastList.newInstance();

        for (GenericValue variant: variants) {
            String productIdTo = variant.getString("productIdTo");

            // first check to see if intro and discontinue dates are within range
            GenericValue productTo = null;

            try {
                productTo = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productIdTo));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                Map<String, String> messageMap = UtilMisc.toMap("productIdTo", productIdTo, "errMessage", e.toString());
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "productservices.error_finding_associated_variant_with_ID_error", messageMap, locale));
            }
            if (productTo == null) {
                Debug.logWarning("Could not find associated variant with ID " + productIdTo + ", not showing in list", module);
                continue;
            }

            java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

            // check to see if introductionDate hasn't passed yet
            if (productTo.get("introductionDate") != null && nowTimestamp.before(productTo.getTimestamp("introductionDate"))) {
                if (Debug.verboseOn()) {
                    String excMsg = "Tried to view the Product " + productTo.getString("productName") +
                        " (productId: " + productTo.getString("productId") + ") as a variant. This product has not yet been made available for sale, so not adding for view.";

                    Debug.logVerbose(excMsg, module);
                }
                continue;
            }

            // check to see if salesDiscontinuationDate has passed
            if (productTo.get("salesDiscontinuationDate") != null && nowTimestamp.after(productTo.getTimestamp("salesDiscontinuationDate"))) {
                if (Debug.verboseOn()) {
                    String excMsg = "Tried to view the Product " + productTo.getString("productName") +
                        " (productId: " + productTo.getString("productId") + ") as a variant. This product is no longer available for sale, so not adding for view.";

                    Debug.logVerbose(excMsg, module);
                }
                continue;
            }

            // next check inventory for each item: if inventory is not required or is available
            Boolean checkInventory = (Boolean) context.get("checkInventory");
            try {
                if (checkInventory) {
                    Map<String, Object> invReqResult = dispatcher.runSync("isStoreInventoryAvailableOrNotRequired", UtilMisc.<String, Object>toMap("productStoreId", productStoreId, "productId", productIdTo, "quantity", BigDecimal.ONE));
                    if (ServiceUtil.isError(invReqResult)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "ProductFeatureTreeCannotCallIsStoreInventoryRequired", locale), null, null, invReqResult);
                    } else if ("Y".equals(invReqResult.get("availableOrNotRequired"))) {
                        items.add(productIdTo);
                        if (productTo.getString("isVirtual") != null && productTo.getString("isVirtual").equals("Y")) {
                            virtualVariant.add(productIdTo);
                        }
                    } else {
                        outOfStockItems.add(productTo);
                    }
                } else {
                    items.add(productIdTo);
                    if (productTo.getString("isVirtual") != null && productTo.getString("isVirtual").equals("Y")) {
                        virtualVariant.add(productIdTo);
                    }
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Error calling the isStoreInventoryRequired when building the variant product tree: " + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductFeatureTreeCannotCallIsStoreInventoryRequired", locale));
            }
        }

        String productId = (String) context.get("productId");

        // Make the selectable feature list
        List<GenericValue> selectableFeatures = null;
        try {
            Map<String, String> fields = UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE");
            List<String> sort = UtilMisc.toList("sequenceNum");

            selectableFeatures = delegator.findByAndCache("ProductFeatureAndAppl", fields, sort);
            selectableFeatures = EntityUtil.filterByDate(selectableFeatures, true);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "productservices.empty_list_of_selectable_features_found", locale));
        }
        Map<String, List<String>> features = FastMap.newInstance();
        for (GenericValue v: selectableFeatures) {
            String featureType = v.getString("productFeatureTypeId");
            String feature = v.getString("description");

            if (!features.containsKey(featureType)) {
                List<String> featureList = FastList.newInstance();
                featureList.add(feature);
                features.put(featureType, featureList);
            } else {
                List<String> featureList = features.get(featureType);
                featureList.add(feature);
                features.put(featureType, featureList);
            }
        }

        Map<String, Object> tree = null;
        try {
            tree = makeGroup(delegator, features, items, featureOrder, 0);
        } catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (UtilValidate.isEmpty(tree)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resourceError, 
                    "productservices.feature_grouping_came_back_empty", locale));
        } else {
            result.put("variantTree", tree);
            result.put("virtualVariant", virtualVariant);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }

        Map<String, GenericValue> sample = null;
        try {
            sample = makeVariantSample(dctx.getDelegator(), features, items, featureOrder.get(0));
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        if (outOfStockItems.size() > 0) {
            result.put("unavailableVariants", outOfStockItems);
        }
        result.put("variantSample", sample);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

        return result;
    }

    /**
     * Gets the product features of a product.
     */
    public static Map<String, Object> prodGetFeatures(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Product ID to find
        // * String type           -- Type of feature (STANDARD_FEATURE, SELECTABLE_FEATURE)
        // * String distinct       -- Distinct feature (SIZE, COLOR)
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String productId = (String) context.get("productId");
        String distinct = (String) context.get("distinct");
        String type = (String) context.get("type");
        Locale locale = (Locale) context.get("locale");
        String errMsg=null;
        List<GenericValue> features = null;

        try {
            Map<String, String> fields = UtilMisc.toMap("productId", productId);
            List<String> order = UtilMisc.toList("sequenceNum", "productFeatureTypeId");

            if (distinct != null) fields.put("productFeatureTypeId", distinct);
            if (type != null) fields.put("productFeatureApplTypeId", type);
            features = delegator.findByAndCache("ProductFeatureAndAppl", fields, order);
            result.put("productFeatures", features);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errMessage", e.toString());
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.problem_reading_product_feature_entity", messageMap, locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
        }
        return result;
    }

    /**
     * Finds a product by product ID.
     */
    public static Map<String, Object> prodFindProduct(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Product ID to find
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String productId = (String) context.get("productId");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        if (UtilValidate.isEmpty(productId)) {
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.invalid_productId_passed", locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }

        try {
            GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            GenericValue mainProduct = product;

            if (product.get("isVariant") != null && product.getString("isVariant").equalsIgnoreCase("Y")) {
                List<GenericValue> c = product.getRelatedByAndCache("AssocProductAssoc",
                        UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"));
                //if (Debug.infoOn()) Debug.logInfo("Found related: " + c, module);
                c = EntityUtil.filterByDate(c);
                //if (Debug.infoOn()) Debug.logInfo("Found Filtered related: " + c, module);
                if (c.size() > 0) {
                    GenericValue asV = c.iterator().next();

                    //if (Debug.infoOn()) Debug.logInfo("ASV: " + asV, module);
                    mainProduct = asV.getRelatedOneCache("MainProduct");
                    //if (Debug.infoOn()) Debug.logInfo("Main product = " + mainProduct, module);
                }
            }
            result.put("product", mainProduct);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            Map<String, String> messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.problems_reading_product_entity", messageMap, locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
        }

        return result;
    }

    /**
     * Finds associated products by product ID and association ID.
     */
    public static Map<String, Object> prodFindAssociatedByType(DispatchContext dctx, Map<String, ? extends Object> context) {
        // * String productId      -- Current Product ID
        // * String type           -- Type of association (ie PRODUCT_UPGRADE, PRODUCT_COMPLEMENT, PRODUCT_VARIANT)
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String productId = (String) context.get("productId");
        String productIdTo = (String) context.get("productIdTo");
        String type = (String) context.get("type");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        Boolean cvaBool = (Boolean) context.get("checkViewAllow");
        boolean checkViewAllow = (cvaBool == null ? false : cvaBool);
        String prodCatalogId = (String) context.get("prodCatalogId");
        Boolean bidirectional = (Boolean) context.get("bidirectional");
        bidirectional = bidirectional == null ? false : bidirectional;
        Boolean sortDescending = (Boolean) context.get("sortDescending");
        sortDescending = sortDescending == null ? false : sortDescending;

        if (productId == null && productIdTo == null) {
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.both_productId_and_productIdTo_cannot_be_null", locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }

        if (productId != null && productIdTo != null) {
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.both_productId_and_productIdTo_cannot_be_defined", locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }

        productId = productId == null ? productIdTo : productId;
        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.problems_reading_product_entity", messageMap, locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }

        if (product == null) {
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.problems_getting_product_entity", locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }

        try {
            List<GenericValue> productAssocs = null;

            List<String> orderBy = FastList.newInstance();
            if (sortDescending) {
                orderBy.add("sequenceNum DESC");
            } else {
                orderBy.add("sequenceNum");
            }

            if (bidirectional) {
                EntityCondition cond = EntityCondition.makeCondition(
                        UtilMisc.toList(
                                EntityCondition.makeCondition("productId", productId),
                                EntityCondition.makeCondition("productIdTo", productId)
                       ), EntityJoinOperator.OR);
                cond = EntityCondition.makeCondition(cond, EntityCondition.makeCondition("productAssocTypeId", type));
                productAssocs = delegator.findList("ProductAssoc", cond, null, orderBy, null, true);
            } else {
                if (productIdTo == null) {
                    productAssocs = product.getRelatedCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", type), orderBy);
                } else {
                    productAssocs = product.getRelatedCache("AssocProductAssoc", UtilMisc.toMap("productAssocTypeId", type), orderBy);
                }
            }
            // filter the list by date
            productAssocs = EntityUtil.filterByDate(productAssocs);
            // first check to see if there is a view allow category and if these products are in it...
            if (checkViewAllow && prodCatalogId != null && UtilValidate.isNotEmpty(productAssocs)) {
                String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
                if (viewProductCategoryId != null) {
                    if (productIdTo == null) {
                        productAssocs = CategoryWorker.filterProductsInCategory(delegator, productAssocs, viewProductCategoryId, "productIdTo");
                    } else {
                        productAssocs = CategoryWorker.filterProductsInCategory(delegator, productAssocs, viewProductCategoryId, "productId");
                    }
                }
            }


            result.put("assocProducts", productAssocs);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.problems_product_association_relation_error", messageMap, locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }

        return result;
    }

    // Builds a product feature tree
    private static Map<String, Object> makeGroup(Delegator delegator, Map<String, List<String>> featureList, List<String> items, List<String> order, int index)
        throws IllegalArgumentException, IllegalStateException {
        //List featureKey = FastList.newInstance();
        Map<String, List<String>> tempGroup = FastMap.newInstance();
        Map<String, Object> group = new LinkedHashMap<String, Object>();
        String orderKey = order.get(index);

        if (featureList == null) {
            throw new IllegalArgumentException("Cannot build feature tree: featureList is null");
        }

        if (index < 0) {
            throw new IllegalArgumentException("Invalid index '" + index + "' min index '0'");
        }
        if (index + 1 > order.size()) {
            throw new IllegalArgumentException("Invalid index '" + index + "' max index '" + (order.size() - 1) + "'");
        }

        // loop through items and make the lists
        for (String thisItem: items) {
            // -------------------------------
            // Gather the necessary data
            // -------------------------------

            if (Debug.verboseOn()) Debug.logVerbose("ThisItem: " + thisItem, module);
            List<GenericValue> features = null;

            try {
                Map<String, String> fields = UtilMisc.toMap("productId", thisItem, "productFeatureTypeId", orderKey,
                        "productFeatureApplTypeId", "STANDARD_FEATURE");
                List<String> sort = UtilMisc.toList("sequenceNum");

                // get the features and filter out expired dates
                features = delegator.findByAndCache("ProductFeatureAndAppl", fields, sort);
                features = EntityUtil.filterByDate(features, true);
            } catch (GenericEntityException e) {
                throw new IllegalStateException("Problem reading relation: " + e.getMessage());
            }
            if (Debug.verboseOn()) Debug.logVerbose("Features: " + features, module);

            // -------------------------------
            for (GenericValue item: features) {
                String itemKey = item.getString("description");

                if (tempGroup.containsKey(itemKey)) {
                    List<String> itemList = tempGroup.get(itemKey);

                    if (!itemList.contains(thisItem))
                        itemList.add(thisItem);
                } else {
                    List<String> itemList = UtilMisc.toList(thisItem);

                    tempGroup.put(itemKey, itemList);
                }
            }
        }
        if (Debug.verboseOn()) Debug.logVerbose("TempGroup: " + tempGroup, module);

        // Loop through the feature list and order the keys in the tempGroup
        List<String> orderFeatureList = featureList.get(orderKey);

        if (orderFeatureList == null) {
            throw new IllegalArgumentException("Cannot build feature tree: orderFeatureList is null for orderKey=" + orderKey);
        }

        for (String featureStr: orderFeatureList) {
            if (tempGroup.containsKey(featureStr))
                group.put(featureStr, tempGroup.get(featureStr));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Group: " + group, module);

        // no groups; no tree
        if (group.size() == 0) {
            return group;
            //throw new IllegalStateException("Cannot create tree from group list; error on '" + orderKey + "'");
        }

        if (index + 1 == order.size()) {
            return group;
        }

        // loop through the keysets and get the sub-groups
        for (String key: group.keySet()) {
            List<String> itemList = UtilGenerics.checkList(group.get(key));

            if (UtilValidate.isNotEmpty(itemList)) {
                Map<String, Object> subGroup = makeGroup(delegator, featureList, itemList, order, index + 1);
                group.put(key, subGroup);
            } else {
                // do nothing, ie put nothing in the Map
                //throw new IllegalStateException("Cannot create tree from an empty list; error on '" + key + "'");
            }
        }
        return group;
    }

    // builds a variant sample (a single sku for a featureType)
    private static Map<String, GenericValue> makeVariantSample(Delegator delegator, Map<String, List<String>> featureList, List<String> items, String feature) {
        Map<String, GenericValue> tempSample = FastMap.newInstance();
        Map<String, GenericValue> sample = new LinkedHashMap<String, GenericValue>();
        for (String productId: items) {
            List<GenericValue> features = null;

            try {
                Map<String, String> fields = UtilMisc.toMap("productId", productId, "productFeatureTypeId", feature,
                        "productFeatureApplTypeId", "STANDARD_FEATURE");
                List<String> sort = UtilMisc.toList("sequenceNum", "description");

                // get the features and filter out expired dates
                features = delegator.findByAndCache("ProductFeatureAndAppl", fields, sort);
                features = EntityUtil.filterByDate(features, true);
            } catch (GenericEntityException e) {
                throw new IllegalStateException("Problem reading relation: " + e.getMessage());
            }
            for (GenericValue featureAppl: features) {
                try {
                    GenericValue product = delegator.findByPrimaryKeyCache("Product",
                            UtilMisc.toMap("productId", productId));

                    tempSample.put(featureAppl.getString("description"), product);
                } catch (GenericEntityException e) {
                    throw new RuntimeException("Cannot get product entity: " + e.getMessage());
                }
            }
        }

        // Sort the sample based on the feature list.
        List<String> features = featureList.get(feature);
        for (String f: features) {
            if (tempSample.containsKey(f))
                sample.put(f, tempSample.get(f));
        }

        return sample;
    }

    public static Map<String, Object> quickAddVariant(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        Locale locale = (Locale) context.get("locale");
        String errMsg=null;
        String productId = (String) context.get("productId");
        String variantProductId = (String) context.get("productVariantId");
        String productFeatureIds = (String) context.get("productFeatureIds");
        Long prodAssocSeqNum = (Long) context.get("sequenceNum");

        try {
            // read the product, duplicate it with the given id
            GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            if (product == null) {
                Map<String, String> messageMap = UtilMisc.toMap("productId", productId);
                errMsg = UtilProperties.getMessage(resourceError, 
                        "productservices.product_not_found_with_ID", messageMap, locale);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, errMsg);
                return result;
            }
            // check if product exists
            GenericValue variantProduct = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId", variantProductId));
            boolean variantProductExists = (variantProduct != null);
            if (variantProduct == null) {
                //if product does not exist
                variantProduct = GenericValue.create(product);
                variantProduct.set("productId", variantProductId);
                variantProduct.set("isVirtual", "N");
                variantProduct.set("isVariant", "Y");
                variantProduct.set("primaryProductCategoryId", null);
                //create new
                variantProduct.create();
            } else {
                //if product does exist
                variantProduct.set("isVirtual", "N");
                variantProduct.set("isVariant", "Y");
                variantProduct.set("primaryProductCategoryId", null);
                //update entry
                variantProduct.store();
            }
            if (variantProductExists) {
                // Since the variant product is already a variant, first of all we remove the old features
                // and the associations of type PRODUCT_VARIANT: a given product can be a variant of only one product.
                delegator.removeByAnd("ProductAssoc", UtilMisc.toMap("productIdTo", variantProductId,
                                                                     "productAssocTypeId", "PRODUCT_VARIANT"));
                delegator.removeByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", variantProductId,
                                                                           "productFeatureApplTypeId", "STANDARD_FEATURE"));
            }
            // add an association from productId to variantProductId of the PRODUCT_VARIANT
            Map<String, Object> productAssocMap = UtilMisc.toMap("productId", productId, "productIdTo", variantProductId,
                                                 "productAssocTypeId", "PRODUCT_VARIANT",
                                                 "fromDate", UtilDateTime.nowTimestamp());
            if (prodAssocSeqNum != null) {
                productAssocMap.put("sequenceNum", prodAssocSeqNum);
            }
            GenericValue productAssoc = delegator.makeValue("ProductAssoc", productAssocMap);
            productAssoc.create();

            // add the selected standard features to the new product given the productFeatureIds
            java.util.StringTokenizer st = new java.util.StringTokenizer(productFeatureIds, "|");
            while (st.hasMoreTokens()) {
                String productFeatureId = st.nextToken();

                GenericValue productFeature = delegator.findByPrimaryKey("ProductFeature", UtilMisc.toMap("productFeatureId", productFeatureId));

                GenericValue productFeatureAppl = delegator.makeValue("ProductFeatureAppl",
                UtilMisc.toMap("productId", variantProductId, "productFeatureId", productFeatureId,
                "productFeatureApplTypeId", "STANDARD_FEATURE", "fromDate", UtilDateTime.nowTimestamp()));

                // set the default seq num if it's there...
                if (productFeature != null) {
                    productFeatureAppl.set("sequenceNum", productFeature.get("defaultSequenceNum"));
                }

                productFeatureAppl.create();
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity error creating quick add variant data", module);
            Map<String, String> messageMap = UtilMisc.toMap("errMessage", e.toString());
            errMsg = UtilProperties.getMessage(resourceError, 
                    "productservices.entity_error_quick_add_variant_data", messageMap, locale);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
            return result;
        }
        result.put("productVariantId", variantProductId);
        return result;
    }

    /**
     * This will create a virtual product and return its ID, and associate all of the variants with it.
     * It will not put the selectable features on the virtual or standard features on the variant.
     */
    public static Map<String, Object> quickCreateVirtualWithVariants(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // get the various IN attributes
        String variantProductIdsBag = (String) context.get("variantProductIdsBag");
        String productFeatureIdOne = (String) context.get("productFeatureIdOne");
        String productFeatureIdTwo = (String) context.get("productFeatureIdTwo");
        String productFeatureIdThree = (String) context.get("productFeatureIdThree");
        Locale locale = (Locale) context.get("locale");
        
        Map<String, Object> successResult = ServiceUtil.returnSuccess();

        try {
            // Generate new virtual productId, prefix with "VP", put in successResult
            String productId = (String) context.get("productId");

            if (UtilValidate.isEmpty(productId)) {
                productId = "VP" + delegator.getNextSeqId("Product");
                // Create new virtual product...
                GenericValue product = delegator.makeValue("Product");
                product.set("productId", productId);
                // set: isVirtual=Y, isVariant=N, productTypeId=FINISHED_GOOD, introductionDate=now
                product.set("isVirtual", "Y");
                product.set("isVariant", "N");
                product.set("productTypeId", "FINISHED_GOOD");
                product.set("introductionDate", nowTimestamp);
                // set all to Y: returnable, taxable, chargeShipping, autoCreateKeywords, includeInPromotions
                product.set("returnable", "Y");
                product.set("taxable", "Y");
                product.set("chargeShipping", "Y");
                product.set("autoCreateKeywords", "Y");
                product.set("includeInPromotions", "Y");
                // in it goes!
                product.create();
            }
            successResult.put("productId", productId);

            // separate variantProductIdsBag into a Set of variantProductIds
            //note: can be comma, tab, or white-space delimited
            Set<String> prelimVariantProductIds = FastSet.newInstance();
            List<String> splitIds = Arrays.asList(variantProductIdsBag.split("[,\\p{Space}]"));
            Debug.logInfo("Variants: bag=" + variantProductIdsBag, module);
            Debug.logInfo("Variants: split=" + splitIds, module);
            prelimVariantProductIds.addAll(splitIds);
            //note: should support both direct productIds and GoodIdentification entries (what to do if more than one GoodID? Add all?

            Map<String, GenericValue> variantProductsById = FastMap.newInstance();
            for (String variantProductId: prelimVariantProductIds) {
                if (UtilValidate.isEmpty(variantProductId)) {
                    // not sure why this happens, but seems to from time to time with the split method
                    continue;
                }
                // is a Product.productId?
                GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", variantProductId));
                if (variantProduct != null) {
                    variantProductsById.put(variantProductId, variantProduct);
                } else {
                    // is a GoodIdentification.idValue?
                    List<GenericValue> goodIdentificationList = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue", variantProductId));
                    if (UtilValidate.isEmpty(goodIdentificationList)) {
                        // whoops, nothing found... return error
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "ProductVirtualVariantCreation", UtilMisc.toMap("variantProductId", variantProductId), locale));
                    }

                    if (goodIdentificationList.size() > 1) {
                        // what to do here? for now just log a warning and add all of them as variants; they can always be dissociated later
                        Debug.logWarning("Warning creating a virtual with variants: the ID [" + variantProductId + "] was not a productId and resulted in [" + goodIdentificationList.size() + "] GoodIdentification records: " + goodIdentificationList, module);
                    }

                    for (GenericValue goodIdentification: goodIdentificationList) {
                        GenericValue giProduct = goodIdentification.getRelatedOne("Product");
                        if (giProduct != null) {
                            variantProductsById.put(giProduct.getString("productId"), giProduct);
                        }
                    }
                }
            }

            // Attach productFeatureIdOne, Two, Three to the new virtual and all variant products as a standard feature
            Set<String> featureProductIds = FastSet.newInstance();
            featureProductIds.add(productId);
            featureProductIds.addAll(variantProductsById.keySet());
            Set<String> productFeatureIds = new HashSet<String>();
            productFeatureIds.add(productFeatureIdOne);
            productFeatureIds.add(productFeatureIdTwo);
            productFeatureIds.add(productFeatureIdThree);

            for (String featureProductId: featureProductIds) {
                for (String productFeatureId: productFeatureIds) {
                    if (UtilValidate.isNotEmpty(productFeatureId)) {
                        GenericValue productFeatureAppl = delegator.makeValue("ProductFeatureAppl",
                                UtilMisc.toMap("productId", featureProductId, "productFeatureId", productFeatureId,
                                        "productFeatureApplTypeId", "STANDARD_FEATURE", "fromDate", nowTimestamp));
                        productFeatureAppl.create();
                    }
                }
            }

            for (GenericValue variantProduct: variantProductsById.values()) {
                // for each variant product set: isVirtual=N, isVariant=Y, introductionDate=now
                variantProduct.set("isVirtual", "N");
                variantProduct.set("isVariant", "Y");
                variantProduct.set("introductionDate", nowTimestamp);
                variantProduct.store();

                // for each variant product create associate with the new virtual as a PRODUCT_VARIANT
                GenericValue productAssoc = delegator.makeValue("ProductAssoc",
                        UtilMisc.toMap("productId", productId, "productIdTo", variantProduct.get("productId"),
                                "productAssocTypeId", "PRODUCT_VARIANT", "fromDate", nowTimestamp));
                productAssoc.create();
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error creating new virtual product from variant products: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        return successResult;
    }

    public static Map<String, Object> updateProductIfAvailableFromShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
        if ("Y".equals(UtilProperties.getPropertyValue("catalog.properties", "reactivate.product.from.receipt", "N"))) {
            LocalDispatcher dispatcher = dctx.getDispatcher();
            Delegator delegator = dctx.getDelegator();
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String inventoryItemId = (String) context.get("inventoryItemId");

            GenericValue inventoryItem = null;
            try {
                inventoryItem = delegator.findByPrimaryKeyCache("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (inventoryItem != null) {
                String productId = inventoryItem.getString("productId");
                GenericValue product = null;
                try {
                    product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                if (product != null) {
                    Timestamp salesDiscontinuationDate = product.getTimestamp("salesDiscontinuationDate");
                    if (salesDiscontinuationDate != null && salesDiscontinuationDate.before(UtilDateTime.nowTimestamp())) {
                        Map<String, Object> invRes = null;
                        try {
                            invRes = dispatcher.runSync("getProductInventoryAvailable", UtilMisc.<String, Object>toMap("productId", productId, "userLogin", userLogin));
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }

                        BigDecimal availableToPromiseTotal = (BigDecimal) invRes.get("availableToPromiseTotal");
                        if (availableToPromiseTotal != null && availableToPromiseTotal.compareTo(BigDecimal.ZERO) > 0) {
                            // refresh the product so we can update it
                            GenericValue productToUpdate = null;
                            try {
                                productToUpdate = delegator.findByPrimaryKey("Product", product.getPrimaryKey());
                            } catch (GenericEntityException e) {
                                Debug.logError(e, module);
                                return ServiceUtil.returnError(e.getMessage());
                            }

                            // set and save
                            productToUpdate.set("salesDiscontinuationDate", null);
                            try {
                                delegator.store(productToUpdate);
                            } catch (GenericEntityException e) {
                                Debug.logError(e, module);
                                return ServiceUtil.returnError(e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> addAdditionalViewForProduct(DispatchContext dctx, Map<String, ? extends Object> context)
        throws IOException, JDOMException {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        String productContentTypeId = (String) context.get("productContentTypeId");
        ByteBuffer imageData = (ByteBuffer) context.get("uploadedFile");
        Locale locale = (Locale) context.get("locale");

        if (UtilValidate.isNotEmpty(context.get("_uploadedFile_fileName"))) {
            String imageFilenameFormat = UtilProperties.getPropertyValue("catalog", "image.filename.additionalviewsize.format");
            String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("catalog", "image.server.path"), context);
            String imageUrlPrefix = UtilProperties.getPropertyValue("catalog", "image.url.prefix");

            FlexibleStringExpander filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
            String viewNumber = String.valueOf(productContentTypeId.charAt(productContentTypeId.length() - 1));
            String viewType = "additional" + viewNumber;
            String id = productId;
            if (imageFilenameFormat.endsWith("${id}")) {
                id = productId + "_View_" + viewNumber;   
                viewType = "additional";
            }
            String fileLocation = filenameExpander.expandString(UtilMisc.toMap("location", "products", "id", id, "viewtype", viewType, "sizetype", "original"));
            String filePathPrefix = "";
            String filenameToUse = fileLocation;
            if (fileLocation.lastIndexOf("/") != -1) {
                filePathPrefix = fileLocation.substring(0, fileLocation.lastIndexOf("/") + 1); // adding 1 to include the trailing slash
                filenameToUse = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
            }

            List<GenericValue> fileExtension = FastList.newInstance();
            try {
                fileExtension = delegator.findByAnd("FileExtension", UtilMisc.toMap("mimeTypeId", (String) context.get("_uploadedFile_contentType")));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            GenericValue extension = EntityUtil.getFirst(fileExtension);
            if (extension != null) {
                filenameToUse += "." + extension.getString("fileExtensionId");
            }

            /* Write the new image file */
            String targetDirectory = imageServerPath + "/" + filePathPrefix;
            try {
                File targetDir = new File(targetDirectory);
                // Create the new directory
                if (!targetDir.exists()) {
                    boolean created = targetDir.mkdirs();
                    if (!created) {
                        String errMsg = UtilProperties.getMessage(resource, "ScaleImage.unable_to_create_target_directory", locale) + " - " + targetDirectory;
                        Debug.logFatal(errMsg, module);
                        return ServiceUtil.returnError(errMsg);
                    }
                // Delete existing image files
                // Images are ordered by productId (${location}/${id}/${viewtype}/${sizetype})
                } else if (!filenameToUse.contains(productId)) {
                    try {
                        File[] files = targetDir.listFiles(); 
                        for(File file : files) {
                            if (file.isFile()) file.delete(); 
                        }
                    } catch (SecurityException e) {
                        Debug.logError(e,module);
                    }
                // Images aren't ordered by productId (${location}/${viewtype}/${sizetype}/${id})
                } else {
                    try {
                        File[] files = targetDir.listFiles(); 
                        for(File file : files) {
                            if (file.isFile() && file.getName().startsWith(productId + "_View_" + viewNumber)) file.delete();
                        }
                    } catch (SecurityException e) {
                        Debug.logError(e,module);
                    }
                }
            } catch (NullPointerException e) {
                Debug.logError(e,module);
            }
            // Write
            try {
            File file = new File(imageServerPath + "/" + fileLocation + "." +  extension.getString("fileExtensionId"));
                try {
                    RandomAccessFile out = new RandomAccessFile(file, "rw");
                    out.write(imageData.array());
                    out.close();
                } catch (FileNotFoundException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "ProductImageViewUnableWriteFile", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
                } catch (IOException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "ProductImageViewUnableWriteBinaryData", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
                }
            } catch (NullPointerException e) {
                Debug.logError(e,module);
            }

            /* scale Image in different sizes */
            Map<String, Object> resultResize = FastMap.newInstance();
            try {
                resultResize.putAll(ScaleImage.scaleImageInAllSize(context, filenameToUse, "additional", viewNumber));
            } catch (IOException e) {
                Debug.logError(e, "Scale additional image in all different sizes is impossible : " + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductImageViewScaleImpossible", UtilMisc.toMap("errorString", e.toString()), locale));
            } catch (JDOMException e) {
                Debug.logError(e, "Errors occur in parsing ImageProperties.xml : " + e.toString(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductImageViewParsingError", UtilMisc.toMap("errorString", e.toString()), locale));
            }

            String imageUrl = imageUrlPrefix + "/" + fileLocation + "." +  extension.getString("fileExtensionId");
            /* store the imageUrl version of the image, for backwards compatibility with code that does not use scaled versions */
            Map<String, Object> result = addImageResource(dispatcher, delegator, context, imageUrl, productContentTypeId);

            if( ServiceUtil.isError(result)) {
                return result;
            }

            /* now store the image versions created by ScaleImage.scaleImageInAllSize */
            /* have to shrink length of productContentTypeId, as otherwise value is too long for database field */
            Map<String,String> imageUrlMap = UtilGenerics.checkMap(resultResize.get("imageUrlMap"));
            for( String sizeType : ScaleImage.sizeTypeList ) {
                imageUrl = imageUrlMap.get(sizeType);
                if( UtilValidate.isNotEmpty(imageUrl)) {
                    result = addImageResource(dispatcher, delegator, context, imageUrl, "XTRA_IMG_" + viewNumber + "_" + sizeType.toUpperCase());
                    if( ServiceUtil.isError(result)) {
                        return result;
                    }
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }

    private static Map<String,Object> addImageResource( LocalDispatcher dispatcher, Delegator delegator, Map<String, ? extends Object> context, 
            String imageUrl, String productContentTypeId ) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productId = (String) context.get("productId");

        if (UtilValidate.isNotEmpty(imageUrl) && imageUrl.length() > 0) {
            String contentId = (String) context.get("contentId");

            Map<String, Object> dataResourceCtx = FastMap.newInstance();
            dataResourceCtx.put("objectInfo", imageUrl);
            dataResourceCtx.put("dataResourceName", context.get("_uploadedFile_fileName"));
            dataResourceCtx.put("userLogin", userLogin);

            Map<String, Object> productContentCtx = FastMap.newInstance();
            productContentCtx.put("productId", productId);
            productContentCtx.put("productContentTypeId", productContentTypeId);
            productContentCtx.put("fromDate", context.get("fromDate"));
            productContentCtx.put("thruDate", context.get("thruDate"));
            productContentCtx.put("userLogin", userLogin);

            if (UtilValidate.isNotEmpty(contentId)) {
                GenericValue content = null;
                try {
                    content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                if (content != null) {
                    GenericValue dataResource = null;
                    try {
                        dataResource = content.getRelatedOne("DataResource");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    if (dataResource != null) {
                        dataResourceCtx.put("dataResourceId", dataResource.getString("dataResourceId"));
                        try {
                            dispatcher.runSync("updateDataResource", dataResourceCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    } else {
                        dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
                        dataResourceCtx.put("mimeTypeId", "text/html");
                        Map<String, Object> dataResourceResult = FastMap.newInstance();
                        try {
                            dataResourceResult = dispatcher.runSync("createDataResource", dataResourceCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }

                        Map<String, Object> contentCtx = FastMap.newInstance();
                        contentCtx.put("contentId", contentId);
                        contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
                        contentCtx.put("userLogin", userLogin);
                        try {
                            dispatcher.runSync("updateContent", contentCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    }

                    productContentCtx.put("contentId", contentId);
                    try {
                        dispatcher.runSync("updateProductContent", productContentCtx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }
            } else {
                dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
                dataResourceCtx.put("mimeTypeId", "text/html");
                Map<String, Object> dataResourceResult = FastMap.newInstance();
                try {
                    dataResourceResult = dispatcher.runSync("createDataResource", dataResourceCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                Map<String, Object> contentCtx = FastMap.newInstance();
                contentCtx.put("contentTypeId", "DOCUMENT");
                contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
                contentCtx.put("userLogin", userLogin);
                Map<String, Object> contentResult = FastMap.newInstance();
                try {
                    contentResult = dispatcher.runSync("createContent", contentCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                productContentCtx.put("contentId", contentResult.get("contentId"));
                try {
                    dispatcher.runSync("createProductContent", productContentCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
            }
        }
       return ServiceUtil.returnSuccess();
    }

    /**
     * Finds productId(s) corresponding to a product reference, productId or a GoodIdentification idValue
     * @param ctx the dispatch context
     * @param context productId use to search with productId or goodIdentification.idValue
     * @return a GenericValue with a productId and a List of complementary productId found
     */
    public static Map<String, Object> findProductById(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        String idToFind = (String) context.get("idToFind");
        String goodIdentificationTypeId = (String) context.get("goodIdentificationTypeId");
        String searchProductFirstContext = (String) context.get("searchProductFirst");
        String searchAllIdContext = (String) context.get("searchAllId");

        boolean searchProductFirst = UtilValidate.isNotEmpty(searchProductFirstContext) && "N".equals(searchProductFirstContext) ? false : true;
        boolean searchAllId = UtilValidate.isNotEmpty(searchAllIdContext)&& "Y".equals(searchAllIdContext) ? true : false;

        GenericValue product = null;
        List<GenericValue> productsFound = null;
        try {
            productsFound = ProductWorker.findProductsById(delegator, idToFind, goodIdentificationTypeId, searchProductFirst, searchAllId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (UtilValidate.isNotEmpty(productsFound)) {
            // gets the first productId of the List
            product = EntityUtil.getFirst(productsFound);
            // remove this productId
            productsFound.remove(0);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("product", product);
        result.put("productsList", productsFound);

        return result;
    }

    public static Map<String, Object> addImageForProductPromo(DispatchContext dctx, Map<String, ? extends Object> context)
            throws IOException, JDOMException {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productPromoId = (String) context.get("productPromoId");
        String productPromoContentTypeId = (String) context.get("productPromoContentTypeId");
        ByteBuffer imageData = (ByteBuffer) context.get("uploadedFile");
        String contentId = (String) context.get("contentId");
        Locale locale = (Locale) context.get("locale");

        if (UtilValidate.isNotEmpty(context.get("_uploadedFile_fileName"))) {
            String imageFilenameFormat = UtilProperties.getPropertyValue("catalog", "image.filename.format");
            String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("catalog", "image.server.path"), context);
            String imageUrlPrefix = UtilProperties.getPropertyValue("catalog", "image.url.prefix");

            FlexibleStringExpander filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
            String id = productPromoId + "_Image_" + productPromoContentTypeId.charAt(productPromoContentTypeId.length() - 1);
            String fileLocation = filenameExpander.expandString(UtilMisc.toMap("location", "products", "type", "promo", "id", id));
            String filePathPrefix = "";
            String filenameToUse = fileLocation;
            if (fileLocation.lastIndexOf("/") != -1) {
                filePathPrefix = fileLocation.substring(0, fileLocation.lastIndexOf("/") + 1); // adding 1 to include the trailing slash
                filenameToUse = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
            }

            List<GenericValue> fileExtension = FastList.newInstance();
            try {
                fileExtension = delegator.findList("FileExtension", EntityCondition.makeCondition("mimeTypeId", EntityOperator.EQUALS, (String) context.get("_uploadedFile_contentType")), null, null, null, false);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            GenericValue extension = EntityUtil.getFirst(fileExtension);
            if (extension != null) {
                filenameToUse += "." + extension.getString("fileExtensionId");
            }

            File makeResourceDirectory  = new File(imageServerPath + "/" + filePathPrefix);
            if (!makeResourceDirectory.exists()) {
                makeResourceDirectory.mkdirs();
            }

            File file = new File(imageServerPath + "/" + filePathPrefix + filenameToUse);

            try {
                RandomAccessFile out = new RandomAccessFile(file, "rw");
                out.write(imageData.array());
                out.close();
            } catch (FileNotFoundException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductImageViewUnableWriteFile", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
            } catch (IOException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductImageViewUnableWriteBinaryData", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
            }

            String imageUrl = imageUrlPrefix + "/" + filePathPrefix + filenameToUse;

            if (UtilValidate.isNotEmpty(imageUrl) && imageUrl.length() > 0) {
                Map<String, Object> dataResourceCtx = FastMap.newInstance();
                dataResourceCtx.put("objectInfo", imageUrl);
                dataResourceCtx.put("dataResourceName", context.get("_uploadedFile_fileName"));
                dataResourceCtx.put("userLogin", userLogin);

                Map<String, Object> productPromoContentCtx = FastMap.newInstance();
                productPromoContentCtx.put("productPromoId", productPromoId);
                productPromoContentCtx.put("productPromoContentTypeId", productPromoContentTypeId);
                productPromoContentCtx.put("fromDate", context.get("fromDate"));
                productPromoContentCtx.put("thruDate", context.get("thruDate"));
                productPromoContentCtx.put("userLogin", userLogin);

                if (UtilValidate.isNotEmpty(contentId)) {
                    GenericValue content = null;
                    try {
                        content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    if (UtilValidate.isNotEmpty(content)) {
                        GenericValue dataResource = null;
                        try {
                            dataResource = content.getRelatedOne("DataResource");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }

                        if (UtilValidate.isNotEmpty(dataResource)) {
                            dataResourceCtx.put("dataResourceId", dataResource.getString("dataResourceId"));
                            try {
                                dispatcher.runSync("updateDataResource", dataResourceCtx);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return ServiceUtil.returnError(e.getMessage());
                            }
                        } else {
                            dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
                            dataResourceCtx.put("mimeTypeId", "text/html");
                            Map<String, Object> dataResourceResult = FastMap.newInstance();
                            try {
                                dataResourceResult = dispatcher.runSync("createDataResource", dataResourceCtx);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return ServiceUtil.returnError(e.getMessage());
                            }

                            Map<String, Object> contentCtx = FastMap.newInstance();
                            contentCtx.put("contentId", contentId);
                            contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
                            contentCtx.put("userLogin", userLogin);
                            try {
                                dispatcher.runSync("updateContent", contentCtx);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return ServiceUtil.returnError(e.getMessage());
                            }
                        }

                        productPromoContentCtx.put("contentId", contentId);
                        try {
                            dispatcher.runSync("updateProductPromoContent", productPromoContentCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    }
                } else {
                    dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
                    dataResourceCtx.put("mimeTypeId", "text/html");
                    Map<String, Object> dataResourceResult = FastMap.newInstance();
                    try {
                        dataResourceResult = dispatcher.runSync("createDataResource", dataResourceCtx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    Map<String, Object> contentCtx = FastMap.newInstance();
                    contentCtx.put("contentTypeId", "DOCUMENT");
                    contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
                    contentCtx.put("userLogin", userLogin);
                    Map<String, Object> contentResult = FastMap.newInstance();
                    try {
                        contentResult = dispatcher.runSync("createContent", contentCtx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    productPromoContentCtx.put("contentId", contentResult.get("contentId"));
                    try {
                        dispatcher.runSync("createProductPromoContent", productPromoContentCtx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }
            }
        } else {
            Map<String, Object> productPromoContentCtx = FastMap.newInstance();
            productPromoContentCtx.put("productPromoId", productPromoId);
            productPromoContentCtx.put("productPromoContentTypeId", productPromoContentTypeId);
            productPromoContentCtx.put("contentId", contentId);
            productPromoContentCtx.put("fromDate", context.get("fromDate"));
            productPromoContentCtx.put("thruDate", context.get("thruDate"));
            productPromoContentCtx.put("userLogin", userLogin);
            try {
                dispatcher.runSync("updateProductPromoContent", productPromoContentCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        return ServiceUtil.returnSuccess();
    }
    
    /**
     * Finds productId(s) corresponding to a product reference, productId or a GoodIdentification idValue
     * @param ctx the dispatch context
     * @param context productId use to search with productId or goodIdentification.idValue
     * @return a GenericValue with a productId and a List of complementary productId found
     */
    public static String processChangeOrder(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        String boothId = (String) request.getParameter("boothId");
        String effectiveDateStr = (String) request.getParameter("effectiveDate");
        String productSubscriptionTypeId = "CASH";
        String shipmentTypeId = "AM_SHIPMENT";
        String productId = null;
        String quantityStr = null;
        Timestamp effectiveDate=null;
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        BigDecimal quantity = BigDecimal.ZERO;
        List<GenericValue> subscriptionList=FastList.newInstance();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        GenericValue subscription = null;
        if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
        	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");             
             try {
            	 effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
             } catch (ParseException e) {
                 Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
                // effectiveDate = UtilDateTime.nowTimestamp();
             } catch (NullPointerException e) {
                 Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
                 //effectiveDate = UtilDateTime.nowTimestamp();
             }
        }
        if (boothId == "") {
			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
			return "error";
		}
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
			try {
				GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
				if (UtilValidate.isEmpty(facility)) {
					request.setAttribute("_ERROR_MESSAGE_", "Booth" + "'"+ boothId + "'" + " does not exist");
					return "error";
				}

			} catch (GenericEntityException e) {
				Debug.logError(e, "Facility does not exist", module);
			}
			try {
             	subscriptionList=delegator.findList("SubscriptionAndFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId), null, null, null, false);
             	subscriptionList = EntityUtil.filterByDate(subscriptionList ,effectiveDate);
             	if(UtilValidate.isEmpty(subscriptionList)){
             		return "success";
             		
             	}
             	subscription = EntityUtil.getFirst(subscriptionList);
             } catch (GenericEntityException e) {
                 Debug.logError(e, "Problem getting order subscription", module);
             }
            for (int i = 0; i < rowCount; i++) {
            	List<GenericValue> subscriptionProductsList = FastList.newInstance();
                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
                if (paramMap.containsKey("productId" + thisSuffix)) {
                    productId = (String) paramMap.remove("productId" + thisSuffix);
                }
                if (paramMap.containsKey("quantity" + thisSuffix)) {
                    quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
                }
                if ((quantityStr == null) || (quantityStr.equals(""))) {
                    continue;
                }
                try {
                    quantity = new BigDecimal(quantityStr);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
                    quantity = BigDecimal.ZERO;
                } 
                List conditionList = UtilMisc.toList(
            			EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
            	conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, (String)subscription.getString("subscriptionId")));
            	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            	try {
            	subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
            	subscriptionProductsList = EntityUtil.filterByDate(subscriptionProductsList, effectiveDate);
            	if(UtilValidate.isEmpty(subscriptionProductsList)){
            		continue;
            	}
            	GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
            	Map updateSubscriptionProduct = FastMap.newInstance();
            	Map createSubscriptionProduct = FastMap.newInstance();
            	updateSubscriptionProduct.put("userLogin",userLogin);
            	updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
            	updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
            	updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
            	BigDecimal newQuantity= (subscriptionProduct.getBigDecimal("quantity")).add(quantity);
            	updateSubscriptionProduct.put("quantity", subscriptionProduct.getBigDecimal("quantity"));
            	updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
            	updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(nowTimeStamp));
            	result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
            	createSubscriptionProduct.putAll(updateSubscriptionProduct);
            	createSubscriptionProduct.put("quantity", newQuantity);
            	createSubscriptionProduct.put("fromDate", UtilDateTime.getNextDayStart(nowTimeStamp));
            	createSubscriptionProduct.put("thruDate", null);
            	
            	result.clear();
            	GenericValue checkSubscriptionProduct = delegator.findOne("SubscriptionProduct", UtilMisc.toMap("subscriptionId",subscriptionProduct.getString("subscriptionId"),"productId",subscriptionProduct.getString("productId"),
            											"productSubscriptionTypeId",productSubscriptionTypeId ,"fromDate", UtilDateTime.getNextDayStart(nowTimeStamp)), false);
            	if(checkSubscriptionProduct == null){
            		createSubscriptionProduct.put("facilityId", boothId);
            		createSubscriptionProduct.put("shipmentTypeId", shipmentTypeId); 
                	createSubscriptionProduct.put("createdByUserLogin",userLogin);
                	createSubscriptionProduct.put("createdDate",nowTimeStamp);            		
              	    createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
            	    createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp);             	  
            		result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
            	}else{            		      	
                	createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin);
                	createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp);            		
            		dispatcher.runSync("updateSubscriptionProduct",createSubscriptionProduct);
            	}           	
                
            }catch (Exception e) {
                Debug.logError(e, "Problem getting order subscription", module);
            }
        }
               
      }  
        return "success";
    }
    
    public static String processDispatchReconcilMIS(HttpServletRequest request, HttpServletResponse response) {
    	  Delegator delegator = (Delegator) request.getAttribute("delegator");
    	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	  Locale locale = UtilHttp.getLocale(request);
    	  String routeId = (String) request.getParameter("routeId");
    	  String tripId = (String) request.getParameter("tripId");
    	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
    	  String productId = null;
    	  String quantityStr = null;
    	  String sequenceNum = null;	  
    	  Timestamp effectiveDate=null;
    	  BigDecimal quantity = BigDecimal.ZERO;
    	  List conditionList =FastList.newInstance();
    	  
    	  Map<String, Object> result = ServiceUtil.returnSuccess();
    	  HttpSession session = request.getSession();
    	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	  GenericValue facility = null;
    	  List custTimePeriodList = FastList.newInstance();
    	  String shipmentId = "";
    	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
    		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");             
    		  try {
    			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
    		  } catch (ParseException e) {
    			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
    		  } catch (NullPointerException e) {
    			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
    		  }
    	  }
    	  if (routeId == "") {
    		request.setAttribute("_ERROR_MESSAGE_","Route Id is empty");
    		return "error";
    	  }
        
        // Get the parameters as a MAP, remove the productId and quantity params.
    	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
    	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
    	  if (rowCount < 1) {
    		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
    		  return "success";
    	  }
    	  try{
    		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",routeId), false);
    		  if(UtilValidate.isEmpty(facility)){
    			  request.setAttribute("_ERROR_MESSAGE_", "Route"+" '"+routeId+"'"+" does not exist");
    			  return "error";
    		  }
    	  }catch (GenericEntityException e) {
    		  Debug.logError(e, "Route does not exist", module);
    		  request.setAttribute("_ERROR_MESSAGE_", "Route"+" '"+routeId+"'"+" does not exist");
    		  return "error";
    	  }
    	  
    	  try {
    		  conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
    		  if(UtilValidate.isNotEmpty(tripId)){
    			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
    		  }
    		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
    		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
    		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate)));
  		  	  EntityCondition shipCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
  		  	  List<GenericValue> shipmentList = delegator.findList("Shipment", shipCond, UtilMisc.toSet("shipmentId"), null, null, false);
    		  if(UtilValidate.isEmpty(shipmentList)){
    			  request.setAttribute("_ERROR_MESSAGE_", "This Route has no shipments for the day");
    			  return "error";     		
    		  }
    		  shipmentId = (String)((GenericValue)EntityUtil.getFirst(shipmentList)).get("shipmentId");
    	  }catch (GenericEntityException e) {
    		  Debug.logError(e, "Problem getting Booth subscription", module);
    		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
    		  return "error";
    	  }
    	
    	  List<Map>productQtyList =FastList.newInstance();
    	  for (int i = 0; i < rowCount; i++) {
    		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
    		  
    		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
    		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
    		  if (paramMap.containsKey("productId" + thisSuffix)) {
    			  productId = (String) paramMap.get("productId" + thisSuffix);
    		  }
    		  else {
    			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
    			  return "error";			  
    		  }
    		  if (paramMap.containsKey("quantity" + thisSuffix)) {
    			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
    		  }
    		  else {
    			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
    			  return "error";			  
    		  }		  
    		  if (quantityStr.equals("")) {
    			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
    			  return "error";	
    		  }
    		  try {
    			  quantity = new BigDecimal(quantityStr);
    		  } catch (Exception e) {
    			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
    			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
    			  return "error";
    		  } 
    		  
    		  productQtyMap.put("productId", productId);
    		  productQtyMap.put("quantity", quantity);
    		  productQtyList.add(productQtyMap);
    	  }//end row count for loop
    	  
    	  Map processDispatchReconcilHelperCtx = UtilMisc.toMap("userLogin",userLogin);
    	  processDispatchReconcilHelperCtx.put("shipmentId", shipmentId);
    	  processDispatchReconcilHelperCtx.put("routeId", routeId);
    	  processDispatchReconcilHelperCtx.put("effectiveDate", effectiveDate);
    	  processDispatchReconcilHelperCtx.put("productQtyList", productQtyList);
    	  try{
    		  result = dispatcher.runSync("processDispatchReconcilHelper",processDispatchReconcilHelperCtx);
    		
    		  if (ServiceUtil.isError(result)) {
    			  String errMsg =  ServiceUtil.getErrorMessage(result);
    			  Debug.logError(errMsg , module);
    			  request.setAttribute("_ERROR_MESSAGE_",errMsg);
    			  return "error";
    		  }
    	  }catch (Exception e) {
    		  	Debug.logError(e, "Problem updating ItemIssuance for Route " + routeId, module);     
    	  		request.setAttribute("_ERROR_MESSAGE_", "Problem updating ItemIssuance for Route " + routeId);
    	  		return "error";			  
    	  }	 
    	  return "success";     
    }
    public static Map<String ,Object>  processDispatchReconcilHelper(DispatchContext dctx, Map<String, ? extends Object> context){
		  Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();       
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      Map<String, Object> result = ServiceUtil.returnSuccess();
	      String shipmentId = (String)context.get("shipmentId");
	      String routeId = (String)context.get("routeId");
	      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
	      List<Map> productQtyList = (List)context.get("productQtyList");
	      Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	      
	      try{
	    	  List conditionList = FastList.newInstance();
	    	  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	    	  conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
	    	  conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate)));
	    	  EntityCondition issueCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    	  List<GenericValue> itemIssuance = delegator.findList("ItemIssuance", issueCond, null, null, null, false);
	    	  for(int i=0; i< productQtyList.size() ; i++){
	    		  Map productQtyMap = productQtyList.get(i);
	    		  String productId = (String)productQtyMap.get("productId");
	    		  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");

	    		  List<GenericValue> prodItemIssue = (List)EntityUtil.filterByCondition(itemIssuance, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	    		  GenericValue prodItem = EntityUtil.getFirst(prodItemIssue);
	    		  if(UtilValidate.isEmpty(prodItem)){
	    			  GenericValue newEntryIssuance = delegator.makeValue("ItemIssuance");
	    			  newEntryIssuance.set("shipmentId", shipmentId);
	    			  newEntryIssuance.set("productId", productId);
	    			  newEntryIssuance.set("quantity", quantity);
	    			  newEntryIssuance.set("issuedDateTime", UtilDateTime.getDayStart(effectiveDate));
	    			  newEntryIssuance.set("issuedByUserLoginId", userLogin.get("userLoginId"));
	    			  newEntryIssuance.set("modifiedByUserLoginId", userLogin.get("userLoginId"));
	    			  newEntryIssuance.set("modifiedDateTime", nowTimestamp);
	    			  delegator.createSetNextSeqId(newEntryIssuance);
	    		  }
	    		  else{
	    			  prodItem.put("quantity", quantity);
	    			  prodItem.put("modifiedByUserLoginId", userLogin.get("userLoginId"));
	    			  prodItem.put("modifiedDateTime", nowTimestamp);
	    			  prodItem.store();
	    		  }
		  	  }
		  }catch (Exception e) {
			  Debug.logError(e, "Problem updating itemIssuance for Route " + routeId, module);		  
			  return ServiceUtil.returnError("Problem updating Dispatch Reconciliation for Route " + routeId);			  
		  }
		  return result;  
    }
    
    public static String processChangeIndentMIS(HttpServletRequest request, HttpServletResponse response) {
  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  	  Locale locale = UtilHttp.getLocale(request);
  	  String boothId = (String) request.getParameter("boothId");
  	  String routeChangeFlag = (String) request.getParameter("routeChangeFlag");
  	  String tripId = (String) request.getParameter("tripId");
  	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
  	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
  	  String subscriptionTypeId = (String) request.getParameter("subscriptionTypeId");
  	  String shipmentTypeId = "AM_SHIPMENT";
  	  if(UtilValidate.isNotEmpty(request.getParameter("shipmentTypeId"))){
  		  shipmentTypeId = (String) request.getParameter("shipmentTypeId");
  	  }
  	  String productId = null;
  	  String quantityStr = null;
  	  String sequenceNum = null;	  
  	  Timestamp effectiveDate=null;
  	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  	  BigDecimal quantity = BigDecimal.ZERO;
  	  List<GenericValue> subscriptionList=FastList.newInstance();
  	  Map<String, Object> result = ServiceUtil.returnSuccess();
  	  HttpSession session = request.getSession();
  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
  	  GenericValue subscription = null;
  	  GenericValue facility = null;
  	  List custTimePeriodList = FastList.newInstance();  	 
  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
  		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");             
  		  try {
  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
  		  } catch (ParseException e) {
  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
              // effectiveDate = UtilDateTime.nowTimestamp();
  		  } catch (NullPointerException e) {
  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
               //effectiveDate = UtilDateTime.nowTimestamp();
  		  }
  	  }
  	  if (boothId == "") {
  			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
  			return "error";
  		}
      
      // Get the parameters as a MAP, remove the productId and quantity params.
  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  	  if (rowCount < 1) {
  		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
  		  return "success";
  	  }
  	  try{
  		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
  		  if(UtilValidate.isEmpty(facility)){
  			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
  			  return "error";
  		  }
  		  //lets override productSubscriptionTypeId based on facility category
  		  if(facility.getString("categoryTypeEnum").equals("SO_INST")){
  			  productSubscriptionTypeId = "SPECIAL_ORDER";
  		  }else if(facility.getString("categoryTypeEnum").equals("CR_INST")){
  			 productSubscriptionTypeId = "CREDIT";
 		  }
  	  }catch (GenericEntityException e) {
  		  Debug.logError(e, "Booth does not exist", module);
  		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
  		  return "error";
  	  }
  	  try {
  		  List conditionList =FastList.newInstance();
  		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
		  if(subscriptionTypeId.equals("AM")){
          	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
          	
          }else{
          	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
          }
		  if(UtilValidate.isNotEmpty(tripId)){
			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
		  }
		  EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
  		  subscriptionList=delegator.findList("SubscriptionAndFacility", subCond, null, null, null, false);
  		  subscriptionList = EntityUtil.filterByDate(subscriptionList ,effectiveDate);
  		  if(UtilValidate.isEmpty(subscriptionList)){
  			  request.setAttribute("_ERROR_MESSAGE_", "Booth subscription does not exist");
  			  return "error";     		
  		  }
  		  subscription = EntityUtil.getFirst(subscriptionList);
  	  }  catch (GenericEntityException e) {
  		  Debug.logError(e, "Problem getting Booth subscription", module);
  		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
  		  return "error";	
  	  }
  	
  	  List<Map>productQtyList =FastList.newInstance();
  	  for (int i = 0; i < rowCount; i++) {
  		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
  		  
  		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
  		  if (paramMap.containsKey("productId" + thisSuffix)) {
  			  productId = (String) paramMap.get("productId" + thisSuffix);
  		  }
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
  			  return "error";			  
  		  }
  		  if (paramMap.containsKey("sequenceNum" + thisSuffix)) {
  			  sequenceNum = (String) paramMap.get("sequenceNum" + thisSuffix);
  		  }	
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing sequence number");
  			  return "error";			  
  		  }		  
  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
  		  }
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
  			  return "error";			  
  		  }		  
  		  if (quantityStr.equals("")) {
  			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
  			  return "error";	
  		  }
  		  try {
  			  quantity = new BigDecimal(quantityStr);
  		  } catch (Exception e) {
  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
  			  return "error";
  		  } 
  		  
  		  productQtyMap.put("productId", productId);
  		  productQtyMap.put("quantity", quantity);
  		  productQtyMap.put("sequenceNum", sequenceNum);
  		  productQtyList.add(productQtyMap);
  		  /*
  		  List conditionList = UtilMisc.toList(
  				  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
  		  conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, (String)subscription.getString("subscriptionId")));
  		  conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));  		  
  		  if(productSubscriptionTypeId.equals("CARD")){
  			 conditionList.add(EntityCondition.makeCondition( EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custTimePeriodList,"customTimePeriodId" ,false)) ) );
  			 //conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, UtilDateTime.getNextDayStart(nowTimeStamp)));
  		  }else{
  			conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));
  		  }
  		  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  		  try {
  			  subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
  			  subscriptionProductsList = EntityUtil.filterByDate(subscriptionProductsList, effectiveDate);
  			  if(UtilValidate.isEmpty(subscriptionProductsList)){
  				  Map createNewSubscriptionProduct = FastMap.newInstance();
  				  createNewSubscriptionProduct.put("facilityId", boothId);
  				  createNewSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
  				  createNewSubscriptionProduct.put("userLogin",userLogin);
  				  createNewSubscriptionProduct.put("subscriptionId",subscription.getString("subscriptionId"));
  				  createNewSubscriptionProduct.put("productId", productId);
  				  createNewSubscriptionProduct.put("sequenceNum", sequenceNum);				  
  				  createNewSubscriptionProduct.put("productSubscriptionTypeId", productSubscriptionTypeId);				  
  				  createNewSubscriptionProduct.put("quantity", quantity);
  				  createNewSubscriptionProduct.put("fromDate", UtilDateTime.getNextDayStart(nowTimeStamp));
  				  createNewSubscriptionProduct.put("thruDate", null);				  
  				  if((productSubscriptionTypeId.equals("SPECIAL_ORDER")) || (productSubscriptionTypeId.equals("CASH_FS"))){
  					  createNewSubscriptionProduct.put("thruDate",  UtilDateTime.getDayEnd(UtilDateTime.getNextDayStart(nowTimeStamp))); 
  				  }
  				  if(productSubscriptionTypeId.equals("CARD")){ 
  					  createNewSubscriptionProduct.put("sequenceNum", null);	
  					  createNewSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp( EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
  					  createNewSubscriptionProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")); 
  				  }
  				  createNewSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
  				  createNewSubscriptionProduct.put("createdDate",nowTimeStamp);   
  				  createNewSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
  				  createNewSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 
  				  result = dispatcher.runSync("createSubscriptionProduct",createNewSubscriptionProduct);
  				  if (ServiceUtil.isError(result)) {
  					String errMsg =  ServiceUtil.getErrorMessage(result);
  					Debug.logError(errMsg , module);
  					request.setAttribute("_ERROR_MESSAGE_",errMsg);
  					return "error";
                  }
  				  createNewSubscriptionProduct.clear();
  				  continue;
  			  }
  			  if(productSubscriptionTypeId.equals("CARD")){
  				  Map createSubscriptionProduct = FastMap.newInstance();
  				  for (GenericValue subscriptionProduct : subscriptionProductsList){
  		  			  Map updateSubscriptionProduct = FastMap.newInstance();  		  			  
  		  			  updateSubscriptionProduct.put("userLogin",userLogin);
  		  			  updateSubscriptionProduct.put("facilityId", boothId);
  		  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
  		  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
  		  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
  		  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
  		  			  updateSubscriptionProduct.put("quantity", subscriptionProduct.getBigDecimal("quantity"));
  		  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
  		  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(nowTimeStamp));  		  			 
  		  			  if(subscriptionProduct.getTimestamp("fromDate") != UtilDateTime.getNextDayStart(nowTimeStamp)){
  		  				 result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
  		  				if (ServiceUtil.isError(result)) {
  		  					String errMsg =  ServiceUtil.getErrorMessage(result);
  		  					Debug.logError(errMsg , module);
  		  					request.setAttribute("_ERROR_MESSAGE_",errMsg);
  		  					return "error";
  		                  }
  		  			  }
  		  			  
  		  			  createSubscriptionProduct.putAll(updateSubscriptionProduct);
  				  }
  				  BigDecimal newQuantity=quantity;  				  
  				  createSubscriptionProduct.put("quantity", newQuantity);
	  			  createSubscriptionProduct.put("fromDate", UtilDateTime.getNextDayStart(nowTimeStamp));
	  			  createSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
				  createSubscriptionProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId"));	  			   			  
	  			  result.clear();
	  			  //to check whether  change indent entered  for the next day
	  			  List checkSubscriptionProductList = delegator.findByAnd("SubscriptionProduct", 
	  					  UtilMisc.toMap("subscriptionId",createSubscriptionProduct.get("subscriptionId"),"productId",createSubscriptionProduct.get("productId"),
	  					  "productSubscriptionTypeId",productSubscriptionTypeId , 
	  					  "fromDate", UtilDateTime.getNextDayStart(nowTimeStamp) ,"customTimePeriodId" ,EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")));	  			  
	  			 GenericValue checkSubscriptionProduct = EntityUtil.getFirst(checkSubscriptionProductList);
	  			 if(checkSubscriptionProduct == null || !(checkSubscriptionProduct.get("customTimePeriodId").equals(EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")))){
	  				    createSubscriptionProduct.put("sequenceNum", null); 
	  				    createSubscriptionProduct.put("facilityId", boothId);
	            		createSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
	                    createSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
	              	    createSubscriptionProduct.put("createdDate",nowTimeStamp);   
	                	createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	              	    createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp);             	  
	  				    result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
	  				    if (ServiceUtil.isError(result)) {
	    					String errMsg =  ServiceUtil.getErrorMessage(result);
	    					Debug.logError(errMsg , module);
	    					request.setAttribute("_ERROR_MESSAGE_",errMsg);
	    					return "error";
	                    }
	  			  }else{
	  					if(newQuantity.compareTo(checkSubscriptionProduct.getBigDecimal("quantity")) != 0){
	  						 createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	  		            	 createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 				  
	  				  	}	                	 
	  				  result = dispatcher.runSync("updateSubscriptionProduct",createSubscriptionProduct);
	  				  if (ServiceUtil.isError(result)) {
	  					String errMsg =  ServiceUtil.getErrorMessage(result);
	  					Debug.logError(errMsg , module);
	  					request.setAttribute("_ERROR_MESSAGE_",errMsg);
	  					return "error";
	                  }
	  			  } 				  
  				
  			  }else{
  				  GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
 	  			  Map updateSubscriptionProduct = FastMap.newInstance();
 	  			  Map createSubscriptionProduct = FastMap.newInstance();
 	  			  updateSubscriptionProduct.put("userLogin",userLogin);
 	  			  updateSubscriptionProduct.put("facilityId", boothId);
 	  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
 	  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
 	  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
 	  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
 	  			  BigDecimal newQuantity=quantity;
 	  			  updateSubscriptionProduct.put("quantity", subscriptionProduct.getBigDecimal("quantity"));
 	  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
 	  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(nowTimeStamp));
 	  			  if (!productSubscriptionTypeId.equals("SPECIAL_ORDER") || !productSubscriptionTypeId.equals("CASH_FS")	) {
 	  				  // don't close out existing subscription (except for special orders)
 	  				  result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
 	  				  if (ServiceUtil.isError(result)) {
 	  					String errMsg =  ServiceUtil.getErrorMessage(result);
 	  					Debug.logError(errMsg , module);
 	  					request.setAttribute("_ERROR_MESSAGE_",errMsg);
 	  					return "error";
 	                  }
 	  			  }
 	  			  createSubscriptionProduct.putAll(updateSubscriptionProduct);
 	  			  createSubscriptionProduct.put("quantity", newQuantity);
 	  			  createSubscriptionProduct.put("fromDate", UtilDateTime.getNextDayStart(nowTimeStamp));
 	  			  if (productSubscriptionTypeId.equals("SPECIAL_ORDER") || productSubscriptionTypeId.equals("CASH_FS") ) {
 	  				  // special order and Festival Order is valid for only 1 day
 	  				  createSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.getNextDayStart(nowTimeStamp)));				  
 	  			  }
 	  			  else {
 	  				  createSubscriptionProduct.put("thruDate", null);
 	  			  }
 	  			  result.clear();
 	  			  GenericValue checkSubscriptionProduct = delegator.findOne("SubscriptionProduct", 
 	  					  UtilMisc.toMap("subscriptionId",subscriptionProduct.getString("subscriptionId"),"productId",subscriptionProduct.getString("productId"),
 	  					  "productSubscriptionTypeId",productSubscriptionTypeId , "sequenceNum", subscriptionProduct.getString("sequenceNum"), 
 	  					  "fromDate", UtilDateTime.getNextDayStart(nowTimeStamp)), false);
 	  			  if(checkSubscriptionProduct == null){
 	  				  createSubscriptionProduct.put("facilityId", boothId);
 	            		  createSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
 	                	  createSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
 	              	  createSubscriptionProduct.put("createdDate",nowTimeStamp);   
 	                	  createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
 	              	  createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp);             	  
 	  				  result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
 	  				  if (ServiceUtil.isError(result)) {
 	  					String errMsg =  ServiceUtil.getErrorMessage(result);
 	  					Debug.logError(errMsg , module);
 	  					request.setAttribute("_ERROR_MESSAGE_",errMsg);
 	  					return "error";
 	                  }
 	  			  }else{
 	  					if(newQuantity.compareTo(checkSubscriptionProduct.getBigDecimal("quantity")) != 0){
 	  						 createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
 	  		            	 createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 				  
 	  				  	}
 	                	 
 	  				 result = dispatcher.runSync("updateSubscriptionProduct",createSubscriptionProduct);
 	  				 if (ServiceUtil.isError(result)) {
 	  					String errMsg =  ServiceUtil.getErrorMessage(result);
 	  					Debug.logError(errMsg , module);
 	  					request.setAttribute("_ERROR_MESSAGE_",errMsg);
 	  					return "error";
 	                  }
 	  			  } 
  			  }  			 
  			  
  		  }catch (Exception e) {
  			  Debug.logError(e, "Problem updating subscription for booth " + boothId, module);     
  			  request.setAttribute("_ERROR_MESSAGE_", "Problem updating subscription for booth " + boothId);
  			  return "error";			  
  		  } */       
  	  }//end row count for loop
  	  
  
  	 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
  	 processChangeIndentHelperCtx.put("subscriptionId", subscription.getString("subscriptionId"));
  	 processChangeIndentHelperCtx.put("boothId", boothId);
  	 processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
  	 processChangeIndentHelperCtx.put("effectiveDate", effectiveDate);
  	 processChangeIndentHelperCtx.put("productQtyList", productQtyList);
  	 processChangeIndentHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
  	 String indentChanged = "";
  	 try{
  		 if(UtilValidate.isEmpty(routeChangeFlag)){
  			result = dispatcher.runSync("processChangeIndentHelper",processChangeIndentHelperCtx);
  		 }
  		 else{
  			 if(routeChangeFlag.equals("routeChange")){
  				result = dispatcher.runSync("processChangeRouteIndentHelper",processChangeIndentHelperCtx);
  			 }
  		 }
		 if (ServiceUtil.isError(result)) {
			String errMsg =  ServiceUtil.getErrorMessage(result);
			Debug.logError(errMsg , module);
			request.setAttribute("_ERROR_MESSAGE_",errMsg);
			return "error";
		 }
		 indentChanged = (String)result.get("indentChangeFlag");
		 
  	 }catch (Exception e) {
  			  Debug.logError(e, "Problem updating subscription for booth " + boothId, module);     
  			  request.setAttribute("_ERROR_MESSAGE_", "Problem updating subscription for booth " + boothId);
  			  return "error";			  
  		  }	 
  	  request.setAttribute("indentChangeFlag", indentChanged);
  	  return "success";     
    }
        
    public static Map<String ,Object>  processChangeIndentHelper(DispatchContext dctx, Map<String, ? extends Object> context){
		  Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();       
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      Map<String, Object> result = ServiceUtil.returnSuccess();
	      String productSubscriptionTypeId = (String)context.get("productSubscriptionTypeId");
	      String subscriptionId = (String)context.get("subscriptionId");
	      String boothId = (String)context.get("boothId");
	      String shipmentTypeId = (String)context.get("shipmentTypeId");
	      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
	      List<Map> productQtyList = (List)context.get("productQtyList");
	      String routeChangeFlag = (String)context.get("routeChangeFlag");
	      List<GenericValue> custTimePeriodList =FastList.newInstance();
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
	      boolean routeChange = false;
	      //GenericValue subscription = delegator.findOne("Subscription", UtilMisc.toMap("subscriptionId",subscriptionId), false);
	      if(productSubscriptionTypeId.equals("CARD")){
	   		 List custConditionList = UtilMisc.toList(
	 					  EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"CARD_MONTH"));
	 			  custConditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	 			  custConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(effectiveDate.getTime())));
	 			  custConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(effectiveDate.getTime())));				  
	 			  EntityCondition CustCondition = EntityCondition.makeCondition(custConditionList, EntityOperator.AND);
	 			  try{
	 				  custTimePeriodList = delegator.findList("CustomTimePeriod", CustCondition, null, null, null,false); 
	 			  }catch (GenericEntityException e) {
	 		  		  Debug.logError(e, "Problem getting custom time period", module);	 		  		  
	 		  		  return ServiceUtil.returnError("Problem getting custom time period");
	 		  	  }
	 			 
	 			  if(UtilValidate.isEmpty(custTimePeriodList)){
	 				  Debug.logError( "There no active card periods ,Please contact administrator.", module);	 				 
	 				  return ServiceUtil.returnError("There no active card periods ,Please contact administrator.");
	 			  }
	   	  }
	      if(UtilValidate.isNotEmpty(routeChangeFlag) && routeChangeFlag.equalsIgnoreCase("Y")){
	    	  routeChange = true;
	      }
	     
	      List<String> contIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"CONTINUES_INDENT" ,UtilDateTime.getDayStart(effectiveDate)), "productId", true);
	      List<String> dayIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"DAILY_INDENT" ,UtilDateTime.getDayStart(effectiveDate)), "productId", true);
  		 
	      
  		/*List<GenericValue> crateIndentProducts = ProductWorker.getProductsByCategory(delegator ,"CRATE_INDENT" ,UtilDateTime.getDayStart(effectiveDate));
  		List<GenericValue> packetIndentProducts = ProductWorker.getProductsByCategory(delegator ,"PACKET_INDENT" ,UtilDateTime.getDayStart(effectiveDate));
  		List<String> crateIndentProductList = EntityUtil.getFieldListFromEntityList( crateIndentProducts, "productId", true);
  		List<String> packetIndentProductList = EntityUtil.getFieldListFromEntityList(packetIndentProducts, "productId", true);*/
  		Map prodIndentCat = (Map)DeprecatedNetworkServices.getFacilityIndentQtyCategories(delegator, dctx.getDispatcher(),UtilMisc.toMap("userLogin", userLogin, "facilityId", boothId)).get("indentQtyCategory");
  		List crateIndentProductList = FastList.newInstance();
  		if(UtilValidate.isNotEmpty(prodIndentCat)){
  			String prodId = "";
  			String categoryType = "";
  			Iterator mapIterator = prodIndentCat.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Map.Entry entry = (Entry) mapIterator.next();
  				prodId = (String)entry.getKey();
	        	categoryType = (String)entry.getValue();
	        	if(categoryType.equals("CRATE_INDENT")){
	        		crateIndentProductList.add(prodId);
	        	}
    		}
  		}
  		List<GenericValue> subscriptionProdList =FastList.newInstance();
		List conditionList = UtilMisc.toList(
				  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
		  /*conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));*/  		  
		if(productSubscriptionTypeId.equals("CARD")){
			conditionList.add(EntityCondition.makeCondition( EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custTimePeriodList,"customTimePeriodId" ,false)) ) );
			 //conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, UtilDateTime.getNextDayStart(nowTimeStamp)));
		}else{
			/*conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));*/
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		boolean indentChanged = false;  
		try {
			List<GenericValue> products = delegator.findList("Product", null, UtilMisc.toSet("productId", "quantityIncluded"), null, null, true);
			  
			subscriptionProdList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
			subscriptionProdList = EntityUtil.filterByDate(subscriptionProdList, effectiveDate);
			
			List productsList = EntityUtil.getFieldListFromEntityList(subscriptionProdList, "productId", true);
			List activeProdList = FastList.newInstance();
			  for(int i=0; i< productQtyList.size() ; i++){
				  Map productQtyMap = productQtyList.get(i);
				  String productId = (String)productQtyMap.get("productId");
				  String sequenceNum = (String)productQtyMap.get("sequenceNum");
				  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
				  BigDecimal crateQuantity = BigDecimal.ZERO;
				  if(crateIndentProductList.contains(productId)){
					  GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId )));
					  crateQuantity = quantity;
					  quantity = DeprecatedNetworkServices.convertCratesToPackets(product.getBigDecimal("quantityIncluded"),crateQuantity);
	    		  
				  }else{
					  GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId )));
					  //crateQuantity = quantity;
					  if(UtilValidate.isNotEmpty(product)){
						  crateQuantity = DeprecatedNetworkServices.convertPacketsToCrates(product.getBigDecimal("quantityIncluded"),quantity);
					  }
	    		  
				  }
				  List<GenericValue> subscriptionProductsList =FastList.newInstance();
				  subscriptionProductsList = EntityUtil.filterByCondition(subscriptionProdList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				  /*List conditionList = UtilMisc.toList(
					  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
				  conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
				  conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));  		  
				  if(productSubscriptionTypeId.equals("CARD")){
					  conditionList.add(EntityCondition.makeCondition( EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custTimePeriodList,"customTimePeriodId" ,false)) ) );
					  //conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, UtilDateTime.getNextDayStart(nowTimeStamp)));
				  }else{
					  conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));
				  }
				  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				  try {
				  subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
				  subscriptionProductsList = EntityUtil.filterByDate(subscriptionProductsList, effectiveDate);*/
				  if(UtilValidate.isEmpty(subscriptionProductsList)){
					  indentChanged = true;
					  Map createNewSubscriptionProduct = FastMap.newInstance();
					  createNewSubscriptionProduct.put("facilityId", boothId);
					  createNewSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
					  createNewSubscriptionProduct.put("userLogin",userLogin);
					  createNewSubscriptionProduct.put("subscriptionId",subscriptionId);
					  createNewSubscriptionProduct.put("productId", productId);
					  createNewSubscriptionProduct.put("sequenceNum", sequenceNum);				  
					  createNewSubscriptionProduct.put("productSubscriptionTypeId", productSubscriptionTypeId);				  
					  createNewSubscriptionProduct.put("quantity", quantity);
					  createNewSubscriptionProduct.put("crateQuantity", crateQuantity);
					  createNewSubscriptionProduct.put("fromDate", UtilDateTime.getDayStart(effectiveDate));
					  createNewSubscriptionProduct.put("thruDate", null);				  
					  if((productSubscriptionTypeId.equals("SPECIAL_ORDER")) || (productSubscriptionTypeId.equals("CASH_FS") || dayIndentProductList.contains(productId))){
						  createNewSubscriptionProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate)); 
					  }
					  if(productSubscriptionTypeId.equals("CARD")){ 
						  createNewSubscriptionProduct.put("sequenceNum", null);	
						  createNewSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp( EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
						  createNewSubscriptionProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")); 
					  }
					  createNewSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
					  createNewSubscriptionProduct.put("createdDate",nowTimeStamp);   
					  createNewSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
					  createNewSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 
					  result = dispatcher.runSync("createSubscriptionProduct",createNewSubscriptionProduct);
					  
					  if (ServiceUtil.isError(result)) {
						String errMsg =  ServiceUtil.getErrorMessage(result);
						Debug.logError(errMsg , module);					
						return ServiceUtil.returnError(errMsg);
					  }
					  createNewSubscriptionProduct.clear();
					  continue;
				  }
				  if(productSubscriptionTypeId.equals("CARD")){
					  Map createSubscriptionProduct = FastMap.newInstance();
					  for (GenericValue subscriptionProduct : subscriptionProductsList){
			  			  Map updateSubscriptionProduct = FastMap.newInstance();  		  			  
			  			  updateSubscriptionProduct.put("userLogin",userLogin);
			  			  updateSubscriptionProduct.put("facilityId", boothId);
			  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
			  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
			  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
			  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
			  			  updateSubscriptionProduct.put("quantity", subscriptionProduct.getBigDecimal("quantity"));
			  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
			  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));  		  			 
			  			  if(subscriptionProduct.getTimestamp("fromDate") != UtilDateTime.getDayStart(effectiveDate)){
			  				 result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
			  				if (ServiceUtil.isError(result)) {
			  					String errMsg =  ServiceUtil.getErrorMessage(result);
			  					Debug.logError(errMsg , module);
			  					return ServiceUtil.returnError(errMsg);
			                  }
			  			  }
			  			  
			  			  createSubscriptionProduct.putAll(updateSubscriptionProduct);
					  }
					  BigDecimal newQuantity=quantity;  				  
					  createSubscriptionProduct.put("quantity", newQuantity);
					  createSubscriptionProduct.put("crateQuantity", crateQuantity);
	  			  createSubscriptionProduct.put("fromDate", UtilDateTime.getDayStart(effectiveDate));
	  			  createSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
				  createSubscriptionProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId"));	  			   			  
	  			  result.clear();
	  			  //to check whether  change indent entered  for the next day
	  			  List checkSubscriptionProductList = delegator.findByAnd("SubscriptionProduct", 
	  					  UtilMisc.toMap("subscriptionId",createSubscriptionProduct.get("subscriptionId"),"productId",createSubscriptionProduct.get("productId"),
	  					  "productSubscriptionTypeId",productSubscriptionTypeId , 
	  					  "fromDate", UtilDateTime.getDayStart(effectiveDate) ,"customTimePeriodId" ,EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")));	  			  
	  			 GenericValue checkSubscriptionProduct = EntityUtil.getFirst(checkSubscriptionProductList);
	  			 if(checkSubscriptionProduct == null || !(checkSubscriptionProduct.get("customTimePeriodId").equals(EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")))){
	  				    createSubscriptionProduct.put("sequenceNum", null); 
	  				    createSubscriptionProduct.put("facilityId", boothId);
	            		createSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
	                    createSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
	              	    createSubscriptionProduct.put("createdDate",nowTimeStamp);   
	                	createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	              	    createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp);             	  
	  				    result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
	  				    if (ServiceUtil.isError(result)) {
	    					String errMsg =  ServiceUtil.getErrorMessage(result);
	    					Debug.logError(errMsg , module);
	    					return ServiceUtil.returnError(errMsg);
	                    }
	  			  }else{
	  					if(newQuantity.compareTo(checkSubscriptionProduct.getBigDecimal("quantity")) != 0){
	  						 createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	  		            	 createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 				  
	  				  	}	                	 
	  				  result = dispatcher.runSync("updateSubscriptionProduct",createSubscriptionProduct);
	  				  if (ServiceUtil.isError(result)) {
	  					String errMsg =  ServiceUtil.getErrorMessage(result);
	  					Debug.logError(errMsg , module);
	  					return ServiceUtil.returnError(errMsg);
	                  }
	  			  } 				  
					
				  }else{
					  boolean createFlag = true;
					  if(productsList.contains(productId)){
						  activeProdList.add(productId);
					  }
					  
					  GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
					  Timestamp extSubsDate = (Timestamp)subscriptionProduct.get("fromDate");
					  if(extSubsDate.compareTo(UtilDateTime.getDayStart(effectiveDate)) != 0){
						  BigDecimal preQty = subscriptionProduct.getBigDecimal("quantity");
						  if(preQty.compareTo(quantity)!= 0){
							  Map updateSubscriptionProduct = FastMap.newInstance();
				  			  updateSubscriptionProduct.put("userLogin",userLogin);
				  			  updateSubscriptionProduct.put("facilityId", boothId);
				  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
				  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
				  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
				  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
				  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
				  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));
				  			  if (!productSubscriptionTypeId.equals("SPECIAL_ORDER") || !productSubscriptionTypeId.equals("CASH_FS")	) {
				  				  // don't close out existing subscription (except for special orders)
				  				  result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
				  				  if (ServiceUtil.isError(result)) {
				  					String errMsg =  ServiceUtil.getErrorMessage(result);
				  					Debug.logError(errMsg , module);
				  					return ServiceUtil.returnError(errMsg);
				                  }
				  			  }
						  }
						  else{
							  createFlag = false;
						  }
					  }
					  else{
						  int removed = delegator.removeValue(subscriptionProduct);
						  Debug.log("removed todays subscription"+subscriptionProduct);
					  }
					  if(quantity.compareTo(BigDecimal.ZERO)>0 && createFlag){
						  indentChanged = true;
						  Map createNewSubscProduct = FastMap.newInstance();
						  createNewSubscProduct.put("facilityId", boothId);
						  createNewSubscProduct.put("shipmentTypeId", shipmentTypeId);
						  createNewSubscProduct.put("userLogin",userLogin);
						  createNewSubscProduct.put("subscriptionId",subscriptionId);
						  createNewSubscProduct.put("productId", productId);
						  createNewSubscProduct.put("sequenceNum", sequenceNum);				  
						  createNewSubscProduct.put("productSubscriptionTypeId", productSubscriptionTypeId);				  
						  createNewSubscProduct.put("quantity", quantity);
						  createNewSubscProduct.put("crateQuantity", crateQuantity);
						  createNewSubscProduct.put("fromDate", UtilDateTime.getDayStart(effectiveDate));
						  if(routeChange){
							  createNewSubscProduct.put("thruDate", UtilDateTime.getDayEnd(effectiveDate));
						  }
						  else{
							  createNewSubscProduct.put("thruDate", null);  
						  }
						  				  
						  if((productSubscriptionTypeId.equals("SPECIAL_ORDER")) || (productSubscriptionTypeId.equals("CASH_FS") || dayIndentProductList.contains(productId))){
							  createNewSubscProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate)); 
						  }
						  if(productSubscriptionTypeId.equals("CARD")){ 
							  createNewSubscProduct.put("sequenceNum", null);	
							  createNewSubscProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp( EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
							  createNewSubscProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")); 
						  }
						  createNewSubscProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
						  createNewSubscProduct.put("createdDate",nowTimeStamp);   
						  createNewSubscProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
						  createNewSubscProduct.put("lastModifiedDate",nowTimeStamp); 
						  result = dispatcher.runSync("createSubscriptionProduct",createNewSubscProduct);
						  if (ServiceUtil.isError(result)) {
							String errMsg =  ServiceUtil.getErrorMessage(result);
							Debug.logError(errMsg , module);					
							return ServiceUtil.returnError(errMsg);
						  }
					  }
				  }
			  }//end of product Qty List
			  if(routeChange){
				  Timestamp nextEffDay = UtilDateTime.addDaysToTimestamp(effectiveDate, 1);
				  List condList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
				  condList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
				  condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(nextEffDay)));
				  condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
						  EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(nextEffDay))));
				  EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				  List<GenericValue> removeSubscriptionProdList = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
				  if(UtilValidate.isNotEmpty(removeSubscriptionProdList)){
					  int removedNxtDaySubs = delegator.removeAll(removeSubscriptionProdList);
					  Debug.log("removed next day subscriptions #####################"+removedNxtDaySubs);
				  }
				  
			  }
			  if(UtilValidate.isNotEmpty(activeProdList)){
				  List<GenericValue> subscriptionProdClose = EntityUtil.filterByCondition(subscriptionProdList, EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, UtilMisc.toList(activeProdList)));
				  for(int j=0;j<subscriptionProdClose.size();j++){
					  GenericValue subcProdClose = (GenericValue)subscriptionProdClose.get(j);
					  Map updateSubscriptionClose = FastMap.newInstance();
					  updateSubscriptionClose.put("userLogin",userLogin);
					  updateSubscriptionClose.put("facilityId", boothId);
					  updateSubscriptionClose.put("subscriptionId",subcProdClose.getString("subscriptionId"));
					  updateSubscriptionClose.put("productId",subcProdClose.getString("productId"));
					  updateSubscriptionClose.put("productSubscriptionTypeId",subcProdClose.getString("productSubscriptionTypeId"));
					  updateSubscriptionClose.put("sequenceNum",subcProdClose.getString("sequenceNum"));
					  updateSubscriptionClose.put("fromDate", subcProdClose.getTimestamp("fromDate"));
					  updateSubscriptionClose.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));
		  			  if (!productSubscriptionTypeId.equals("SPECIAL_ORDER") || !productSubscriptionTypeId.equals("CASH_FS")	) {
		  				  // don't close out existing subscription (except for special orders)
		  				  result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionClose);
		  				  if (ServiceUtil.isError(result)) {
		  					String errMsg =  ServiceUtil.getErrorMessage(result);
		  					Debug.logError(errMsg , module);
		  					return ServiceUtil.returnError(errMsg);
		                  }
		  			  }
				  }
				  
			  }
			  
		  }catch (Exception e) {
			  Debug.logError(e, "Problem updating subscription for booth " + boothId, module);		  
			  return ServiceUtil.returnError("Problem updating subscription for booth " + boothId);			  
		  }
		  String change = "NotChanged";
		  if(indentChanged){
			  change = "Changed";
		  }
		  result.put("indentChangeFlag", change);
		return result;  
    }
    
    public static Map<String ,Object>  processChangeRouteIndentHelper(DispatchContext dctx, Map<String, ? extends Object> context){
		  Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();       
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      Map<String, Object> result = ServiceUtil.returnSuccess();
	      String productSubscriptionTypeId = (String)context.get("productSubscriptionTypeId");
	      String subscriptionId = (String)context.get("subscriptionId");
	      String boothId = (String)context.get("boothId");
	      String shipmentTypeId = (String)context.get("shipmentTypeId");
	      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
	      List<Map> productQtyList = (List)context.get("productQtyList");
	      
	      Map inputMap = FastMap.newInstance();
	      inputMap.put("subscriptionId", subscriptionId);
	      inputMap.put("productSubscriptionTypeId", productSubscriptionTypeId);
	      inputMap.put("boothId", boothId);
	      inputMap.put("shipmentTypeId", shipmentTypeId);
	      inputMap.put("effectiveDate", effectiveDate);
	      inputMap.put("routeChangeFlag", "Y");
	      inputMap.put("productQtyList", productQtyList);
	      inputMap.put("userLogin", userLogin);
	      
	      try{
	    	  result = dispatcher.runSync("processChangeIndentHelper",inputMap);
	    	  if (ServiceUtil.isError(result)) {
	    		  Debug.logError("Error in service processChangeIndentHelper", module);
	    		  return ServiceUtil.returnError("Error in service processChangeIndentHelper");
	    	  }
	    	  Map boothDetails = (Map)(DeprecatedNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "userLogin", userLogin))).get("boothDetails");
	    	  String defaultRouteId = (String)boothDetails.get("routeId");
	    	  List<GenericValue> productCategoryList = FastList.newInstance();
	    	  List prodCatCondition = UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "CONTINUES_INDENT"));
	    	  prodCatCondition.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "MILK"));
	    	  EntityCondition prodCatCond = EntityCondition.makeCondition(prodCatCondition, EntityOperator.AND);
	    	  productCategoryList = delegator.findList("ProductAndCategoryMember", prodCatCond, null, null, null, false);
	    	  productCategoryList = EntityUtil.filterByDate(productCategoryList, effectiveDate);
	    	  List initProductList = EntityUtil.getFieldListFromEntityList(productCategoryList, "productId", true);
	    	  List<Map> initProductQtyList = FastList.newInstance();
	    	  
	    	  for(int i=0; i< productQtyList.size() ; i++){
				  Map productQtyMap = productQtyList.get(i);
				  String productId = (String)productQtyMap.get("productId");
				  if(initProductList.contains(productId)){
					  productQtyMap.put("sequenceNum", defaultRouteId);
					  initProductQtyList.add(productQtyMap);
				  }
	    	  }
	    	  if(UtilValidate.isNotEmpty(initProductQtyList)){
	    		  Map inputInitMap = FastMap.newInstance();
	    		  inputInitMap.put("subscriptionId", subscriptionId);
	    		  inputInitMap.put("productSubscriptionTypeId", productSubscriptionTypeId);
	    		  inputInitMap.put("boothId", boothId);
	    		  inputInitMap.put("shipmentTypeId", shipmentTypeId);
	    		  inputInitMap.put("userLogin", userLogin);
	    		  inputInitMap.put("routeChangeFlag", "Y");
	    		  inputInitMap.put("effectiveDate", (Timestamp)UtilDateTime.addDaysToTimestamp(effectiveDate, 1));
	    		  inputInitMap.put("productQtyList", initProductQtyList);
			      Map resultInit = dispatcher.runSync("processChangeIndentHelper",inputInitMap);
		    	  if (ServiceUtil.isError(resultInit)) {
		    		  Debug.logError("Error in service processChangeIndentHelper", module);
		    		  return ServiceUtil.returnError("Error in service processChangeIndentHelper");
		    	  }
	    	  }
	      }catch(Exception e){
	    	  Debug.logError(e, "Problem updating subscription product for booth " + boothId, module);		  
			  return ServiceUtil.returnError("Problem temporary route change for dealer" + boothId);
	      }
	      return ServiceUtil.returnSuccess();  
    }
    
    public static String processBulkCardSale(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String boothId = (String) request.getParameter("boothId");
		String counterNumber = (String) request.getParameter("counterNumber");
		String customTimePeriodId = (String) request.getParameter("customTimePeriodId");
		String paymentTypeId = (String) request.getParameter("paymentTypeId");
		Map<String, Object> boothQuantMap = FastMap.newInstance();
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		String orderDateStr = (String) request.getParameter("orderDate");
		String milkCardTypeId = null;
		String quantityStr = null;
		BigDecimal paramQuantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		if (UtilValidate.isNotEmpty(orderDateStr)) { //2011-12-25 18:09:45
       	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");             
            try {
           	 orderDate = new java.sql.Timestamp(sdf.parse(orderDateStr).getTime());
            } catch (ParseException e) {
                Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
               // effectiveDate = UtilDateTime.nowTimestamp();
            } catch (NullPointerException e) {
                Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
                //effectiveDate = UtilDateTime.nowTimestamp();
            }
        }
		if (boothId == "") {
			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
			return "error";
		}
		
		// Get the parameters as a MAP, remove the milkCardTypeId and quantity
		// params.
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			request.setAttribute("_ERROR_MESSAGE_", "No Card Types records found");	
			Debug.logWarning("No rows to process, as rowCount = " + rowCount,
					module);
			return "error";
		}
		try {	
			for (int i = 0; i < rowCount; i++) {
				List<GenericValue> subscriptionProductsList = FastList.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("milkCardTypeId" + thisSuffix)) {
					milkCardTypeId = (String) paramMap.remove("milkCardTypeId"+ thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
				}
				if ((quantityStr == null) || (quantityStr.equals(""))) {
					continue;
				}
					
				paramQuantity = new BigDecimal(quantityStr);
				
				if (paramQuantity.compareTo(BigDecimal.ZERO) < 0) {
					String errMsg = UtilProperties.getMessage(resourceError,"quantity" +paramQuantity+" cannot be negative for ", locale);
					Debug.logWarning(errMsg, module);
					request.setAttribute("_ERROR_MESSAGE_", "quantity " +paramQuantity+" cannot be negative for");	
					return "error";	
				}
				
				boothQuantMap.put(boothId, paramQuantity);

				Map<String, Object> cardContext = FastMap.newInstance();
				cardContext.put("userLogin", userLogin);   
				cardContext.put("cardDetails", boothQuantMap);
				cardContext.put("customTimePeriodId", customTimePeriodId);
				cardContext.put("milkCardTypeId", milkCardTypeId);
				cardContext.put("paymentTypeId", paymentTypeId);
				cardContext.put("counterNumber", counterNumber);
				cardContext.put("orderDate", orderDate);
				result = dispatcher.runSync("processMilkCardOrderService",cardContext);
	
				if( ServiceUtil.isError(result)) {
					String errMsg =  ServiceUtil.getErrorMessage(result);
					Debug.logWarning(errMsg , module);
					request.setAttribute("_ERROR_MESSAGE_",errMsg);
					return "error";
				}
			}// for
		} catch (Exception e) {				
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			Debug.logWarning(e.getMessage() , module);
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Cards Successfully Created");
		return "success";
	}
	   
    public static Map<String, Object> processMilkCardOrderService(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String milkCardTypeId = (String) context.get("milkCardTypeId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String bookNumber = (String) context.get("bookNumber");
		String counterNumber = (String) context.get("counterNumber");
		String paymentTypeId = (String) context.get("paymentTypeId");
		Timestamp orderDate = (Timestamp)context.get("orderDate");
		Map<String, Object> boothQuantMap = (Map<String, Object>) context.get("cardDetails");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productSubscriptionTypeId = "CARD";
		String milkCardOrderIdStr = null;
		String boothId = null;
		String shipmentTypeId = "AM_SHIPMENT";
		//::TODO:: need to add checks for AM/PM etc
		String subscriptionTypeId ="AM";
		BigDecimal mapQuantity = BigDecimal.ZERO;
		BigDecimal cardIntervalPrice = BigDecimal.ZERO;
		List<GenericValue> subscriptionProductsList = FastList.newInstance();
		List<GenericValue> subscriptionList = FastList.newInstance();
		GenericValue subscription = null;
		List<GenericValue> milkCardProductMapList = FastList.newInstance();
		List<GenericValue> productPriceList = FastList.newInstance();
		GenericValue milkCard = null;
		BigDecimal grandTotal=BigDecimal.ZERO;
		BigDecimal cardInterval=BigDecimal.ZERO;
		List<String> boothList = new ArrayList<String>(boothQuantMap.keySet());
		
		for (int i=0; i< boothList.size(); i++){
			boothId = boothList.get(i);
			mapQuantity = (BigDecimal) boothQuantMap.get(boothId);
			
			if (mapQuantity.compareTo(BigDecimal.ZERO) < 0) {
				String errMsg = "Number Of Cards " +mapQuantity+" cannot be negative ";
		        Debug.logWarning(errMsg, module);
				return ServiceUtil.returnError(errMsg);	
			}
			try {
				GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
				
				if (UtilValidate.isEmpty(facility)) {
					String errMsg = "Booth "+"'"+boothId+"'"+" does not exist";
					Debug.logWarning(errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}

				if(!("VENDOR".equals(facility.get("categoryTypeEnum")))){
					String errMsg = "Booth "+"'"+boothId+"'"+"does not accept card";
					Debug.logWarning(errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			
			} catch(GenericEntityException e){
				Debug.logError(e,"Booth "+"'"+boothId+"'"+" does not exist", e.getMessage());
				return ServiceUtil.returnError(e.getMessage());
			}
			
			try {
				  List conditionList =FastList.newInstance();
				  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
				  if(subscriptionTypeId.equals("AM")){
		            	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
		            	
		            }else{
		            	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
		            }
				EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
				subscriptionList = delegator.findList("Subscription", subCond, null, null, null,false);
				subscriptionList = EntityUtil.filterByDate(subscriptionList);
				if (UtilValidate.isEmpty(subscriptionList)) {
					String errMsg = "Booth subscription does not exist";
					Debug.logWarning(errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				subscription = EntityUtil.getFirst(subscriptionList);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Problem getting Booth subscription", e.getMessage());
				return ServiceUtil.returnError(e.getMessage());
			}

			GenericValue productPrice = null;
			BigDecimal quantityToCreate = BigDecimal.ZERO;
			BigDecimal quantityFromCard = BigDecimal.ZERO;
			BigDecimal price = BigDecimal.ZERO;
			BigDecimal tempPrice=BigDecimal.ZERO;
			BigDecimal cardPrice=BigDecimal.ZERO;
			try {
				GenericValue timePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
				if (UtilValidate.isEmpty(timePeriod)) {
					String errMsg =  "card  time period   not found ";
					Debug.logWarning(errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				Timestamp fromDate = UtilDateTime.toTimestamp(timePeriod.getDate("fromDate"));
				Timestamp thruDate = UtilDateTime.toTimestamp(timePeriod.getDate("thruDate"));
				cardInterval = new BigDecimal(UtilDateTime.getIntervalInDays(fromDate,thruDate)+1);
				milkCardProductMapList = delegator.findByAnd("MilkCardProductMap",UtilMisc.toMap("milkCardTypeId", milkCardTypeId));
				for (int j = 0; j < milkCardProductMapList.size(); j++) {
					milkCard = milkCardProductMapList.get(j);
					String qty = milkCard.getString("quantity");
					quantityFromCard = new BigDecimal(qty);
					quantityToCreate = quantityFromCard.multiply(mapQuantity);  
					//product price calculation
					productPriceList = delegator.findByAnd("ProductPrice",UtilMisc.toMap("productId",milkCard.getString("productId"),"productPriceTypeId", "CARD_PRICE"),UtilMisc.toList("-fromDate"));
					productPriceList = EntityUtil.filterByDate(productPriceList ,fromDate);
					if (UtilValidate.isEmpty(productPriceList)) {
						String errMsg =  "Product " + milkCard.getString("productId") + " is missing a price";
						Debug.logWarning(errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					productPrice = productPriceList.get(0);
					price = new BigDecimal(productPrice.getString("price"));
					tempPrice = price.multiply(quantityFromCard);
					cardPrice=cardPrice.add(tempPrice);					
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.EQUALS,productSubscriptionTypeId));
					conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS,(String) subscription.getString("subscriptionId")));
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,(String) milkCard.getString("productId")));
					conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					try {
						subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null,null, false);
						Map updateSubscriptionProduct = FastMap.newInstance();
						Map createSubscriptionProduct = UtilMisc.toMap("userLogin", userLogin);
						if (UtilValidate.isNotEmpty(subscriptionProductsList)) {
							GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
							updateSubscriptionProduct.put("userLogin",userLogin);
							updateSubscriptionProduct.put("facilityId", boothId);
							updateSubscriptionProduct.put("subscriptionId",subscription.getString("subscriptionId"));
							updateSubscriptionProduct.put("productId",milkCard.getString("productId"));
							updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
							updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
							BigDecimal quantityToUpdate = (subscriptionProduct.getBigDecimal("quantity")).add(quantityToCreate);
							updateSubscriptionProduct.put("quantity",quantityToUpdate);
						    updateSubscriptionProduct.put("customTimePeriodId",subscriptionProduct.getString("customTimePeriodId"));
							updateSubscriptionProduct.put("fromDate", fromDate);
							result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
							if (ServiceUtil.isError(result)) {
			  					String errMsg =  ServiceUtil.getErrorMessage(result);
			  					Debug.logError(errMsg , module);
			  					return ServiceUtil.returnError(errMsg);
			                  }
						} else {
							createSubscriptionProduct.putAll(updateSubscriptionProduct);
							createSubscriptionProduct.put("productId",milkCard.getString("productId"));
							createSubscriptionProduct.put("subscriptionId",subscription.getString("subscriptionId"));
							createSubscriptionProduct.put("productSubscriptionTypeId",productSubscriptionTypeId);
							createSubscriptionProduct.put("customTimePeriodId",customTimePeriodId);
							createSubscriptionProduct.put("quantity",quantityToCreate);
							createSubscriptionProduct.put("shipmentTypeId",shipmentTypeId);
							createSubscriptionProduct.put("sequenceNum",null);
							createSubscriptionProduct.put("facilityId", boothId);
							result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
							if (ServiceUtil.isError(result)) {
			  					String errMsg =  ServiceUtil.getErrorMessage(result);
			  					Debug.logError(errMsg , module);
			  					return ServiceUtil.returnError(errMsg);
			                 }
						}
						//lets update the MilkCardTotal entity here for feature validation 
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, milkCard.getString("productId")));
						conditionList.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS,"PRODUCT_VARIANT"));
						EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);						
						List productAssocList = EntityUtil.filterByDate(delegator.findList("ProductAndAssoc", cond, null, null,null, false));
						GenericValue productAssoc = EntityUtil.getFirst(productAssocList);
						if(UtilValidate.isEmpty(productAssocList)){
							String errMsg ="No Assoc found for this product==="+(String) milkCard.getString("productId");
							Debug.logWarning(errMsg , module);
							return ServiceUtil.returnError(errMsg);
						}
						GenericValue productCardTotal = delegator.findOne("MilkCardTotal", UtilMisc.toMap("customTimePeriodId", customTimePeriodId ,"productId" , productAssoc.getString("productId")), false);
						if(UtilValidate.isEmpty(productCardTotal)){
							GenericValue newEntityCardTotal = delegator.makeValue("MilkCardTotal");
							newEntityCardTotal.set("customTimePeriodId", customTimePeriodId);
							newEntityCardTotal.set("productId", productAssoc.getString("productId"));
							newEntityCardTotal.set("quantity", quantityToCreate.divide(productAssoc.getBigDecimal("quantity")));
							delegator.create(newEntityCardTotal);
						}else{
							productCardTotal.set("quantity", productCardTotal.getBigDecimal("quantity").add(quantityToCreate.divide(productAssoc.getBigDecimal("quantity"))));
							delegator.store(productCardTotal);
						}
							
					} catch (Exception e) {
						Debug.logError(e, "Problem getting Booth subscription", e.getMessage());
						return ServiceUtil.returnError(e.getMessage());
					}
				}// for
			}catch (GenericEntityException e) {
				Debug.logError(e, e.getMessage());
				return ServiceUtil.returnError(e.getMessage());
			}
			// create record in MilkCardOrder
			GenericValue cardOrder = delegator.makeValue("MilkCardOrder");
			cardOrder.set("boothId", boothId);
			cardOrder.set("customTimePeriodId", customTimePeriodId);
			cardOrder.set("orderDate", orderDate);
			cardOrder.set("issuedByPartyId", userLogin.get("partyId"));
			cardOrder.set("statusId", "ORDER_CREATED");
			cardOrder.set("createdDate", UtilDateTime.nowTimestamp());
			cardOrder.set("createdByUserLogin", userLogin.get("userLoginId"));
			cardOrder.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			cardOrder.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			cardOrder.set("bookNumber", bookNumber);
			cardOrder.set("counterNumber", counterNumber);
			cardOrder.set("paymentTypeId", paymentTypeId);
			//lets populate the sale location based on the userlogin			
			try {
				List exprListForParameters = FastList.newInstance();
	    		exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,userLogin.getString("partyId")));
	    		exprListForParameters.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "FACILITY_CASHIER"));
	    		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);    		
	    		List<GenericValue>  faclityPartyList = delegator.findList("FacilityParty", paramCond, null, null, null, false);	    		
	    		faclityPartyList = EntityUtil.filterByDate(faclityPartyList);	    		
	    		if(UtilValidate.isEmpty(faclityPartyList)){
	    			Debug.logError("you Don't have permission to create milk card Order, Facility Cashier role missing", module);
	            	return ServiceUtil.returnError("you Don't have permission to create milk card Order, Facility Cashier role missing");	    			
	    		}
	    		faclityPartyList = EntityUtil.filterByDate(faclityPartyList);	    		
	    		cardOrder.set("saleLocation", (EntityUtil.getFirst(faclityPartyList)).getString("facilityId"));
				delegator.createSetNextSeqId(cardOrder);
				milkCardOrderIdStr = (String) cardOrder.get("orderId");
			} catch (GenericEntityException e) {
				Debug.logError(e, e.getMessage());
				return ServiceUtil.returnError(e.getMessage());
			}
			//change discountdays for any discounts
			try {
				GenericValue milkCardDiscountList = delegator.findOne("MilkCardType", UtilMisc.toMap("milkCardTypeId", milkCardTypeId), false);
				BigDecimal discountDays = milkCardDiscountList.getBigDecimal("discountDays");
				if (UtilValidate.isEmpty(discountDays)) {
					cardIntervalPrice = (cardInterval.multiply(cardPrice)).setScale(0, rounding);
				}else{
					cardIntervalPrice = ((cardInterval.subtract(discountDays)).multiply(cardPrice)).setScale(0, rounding);
				}
			} catch(GenericEntityException e){
					Debug.logError(e,"milkCardTypeId "+"'"+milkCardTypeId+"'"+" does not exist", e.getMessage());
					return ServiceUtil.returnError(e.getMessage());
			}
			
			grandTotal = cardIntervalPrice.multiply(mapQuantity);
			// create records in MilkCardOrderItem
			GenericValue cardOrderItem = delegator.makeValue("MilkCardOrderItem");
			cardOrderItem.set("orderId", cardOrder.get("orderId"));
			cardOrderItem.set("milkCardTypeId", milkCardTypeId);
			cardOrderItem.set("quantity", mapQuantity);
			cardOrderItem.set("unitPrice", cardPrice);
			try {
				delegator.create(cardOrderItem);
			} catch (GenericEntityException e) {
				Debug.logError(e, e.getMessage());
				return ServiceUtil.returnError(e.getMessage());
			}			

			if(grandTotal.equals(BigDecimal.ZERO)){
				String errMsg = "No Card Types records found";
				Debug.logWarning(errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}else{
				cardOrder.set("grandTotal",grandTotal);
				try{
					cardOrder.store();
				}catch (Exception e) {				
					Debug.logError(e, e.getMessage());
					return ServiceUtil.returnError(e.getMessage());
				}
			}
		}
		return ServiceUtil.returnSuccess();
		}
    
    public static String newMilkCard(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String milkCardTypeId = (String) request.getParameter("milkCardTypeId");
		String customTimePeriodId = (String) request.getParameter("customTimePeriodId");
		String bookNumber = (String) request.getParameter("bookNumber");
		String counterNumber = (String) request.getParameter("counterNumber");
		String paymentTypeId = (String) request.getParameter("paymentTypeId");
		String orderDateStr = (String) request.getParameter("orderDate");
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		String quantityStr = null;
		BigDecimal paramQuantity = BigDecimal.ZERO;
		String paramBoothId = null;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> boothQuantMap = FastMap.newInstance();
		 if (UtilValidate.isNotEmpty(orderDateStr)) { //2011-12-25 18:09:45
        	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");             
             try {
            	 orderDate = new java.sql.Timestamp(sdf.parse(orderDateStr).getTime());
             } catch (ParseException e) {
                 Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
                // effectiveDate = UtilDateTime.nowTimestamp();
             } catch (NullPointerException e) {
                 Debug.logError(e, "Cannot parse date string: " + orderDateStr, module);
                 //effectiveDate = UtilDateTime.nowTimestamp();
             }
        }
		try {
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
			
			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
				request.setAttribute("_ERROR_MESSAGE_", "No Card Types records found");	
				return "error";
			}
			
			for (int i = 0; i < rowCount; i++) {
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("boothId" + thisSuffix)) {
					paramBoothId = (String) paramMap.remove("boothId"+ thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
				}
				if ((quantityStr == null) || (quantityStr.equals(""))) {
					continue;
				}
				if ((paramBoothId == null) || (paramBoothId.equals(""))) {
					continue;
				}
				paramQuantity = new BigDecimal(quantityStr);
				
				if (paramQuantity.compareTo(BigDecimal.ZERO) < 0) {
					String errMsg = UtilProperties.getMessage(resourceError,"quantity" +paramQuantity+" cannot be negative for ", locale);
			        Debug.logWarning(errMsg, module);
			        request.setAttribute("_ERROR_MESSAGE_", "quantity " +paramQuantity+" cannot be negative for");	
			        return "error";	
				}
				//CHECKING IF BOOTH IS REPEATED 
				if(boothQuantMap.containsKey(paramBoothId)) {
					BigDecimal oldQuant = (BigDecimal) boothQuantMap.get(paramBoothId);
					boothQuantMap.put(paramBoothId, paramQuantity.add(oldQuant));
				}
				else{
				   boothQuantMap.put(paramBoothId, paramQuantity);
				}
			}// for

			Map<String, Object> cardContext = FastMap.newInstance();
			cardContext.put("userLogin", userLogin);   
			cardContext.put("cardDetails", boothQuantMap);
			cardContext.put("customTimePeriodId", customTimePeriodId);
			cardContext.put("milkCardTypeId", milkCardTypeId);
			cardContext.put("bookNumber", bookNumber);
			cardContext.put("counterNumber", counterNumber);
			cardContext.put("paymentTypeId", paymentTypeId);
			cardContext.put("orderDate", orderDate);
			result = dispatcher.runSync("processMilkCardOrderService",cardContext);
		
			if( ServiceUtil.isError(result)) {
				String errMsg =  ServiceUtil.getErrorMessage(result);
				Debug.logWarning(errMsg , module);
				request.setAttribute("_ERROR_MESSAGE_",errMsg);
				return "error";
			}
			
		} catch (Exception e) {				
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			Debug.logWarning(e.getMessage() , module);
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Cards Successfully Created");
	    return "success";
	}
    public static String createOrUpdateCrateCanEntry(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String routeId = null;
		String cratesSentStr = null;
		String cratesReceivedStr = null;
		String cansSentStr = null;
		String cansReceivedStr = null;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);

			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
				request.setAttribute("_ERROR_MESSAGE_", "No Crate Type record found");	
				return "error";
			}
			
			for (int i = 0; i < rowCount; i++) {
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("routeId" + thisSuffix)) {
					routeId = (String) paramMap.remove("routeId"+ thisSuffix);
				}
				if (paramMap.containsKey("cratesSent" + thisSuffix)) {
					cratesSentStr = (String) paramMap.remove("cratesSent"+ thisSuffix);
				}
				if (paramMap.containsKey("cratesReceived" + thisSuffix)) {
					cratesReceivedStr = (String) paramMap.remove("cratesReceived" + thisSuffix);
				}
				if (paramMap.containsKey("cansSent" + thisSuffix)) {
					cansSentStr = (String) paramMap.remove("cansSent"+ thisSuffix);
				}
				if (paramMap.containsKey("cansReceived" + thisSuffix)) {
					cansReceivedStr = (String) paramMap.remove("cansReceived" + thisSuffix);
				}
				if((UtilValidate.isEmpty(cratesSentStr))&&(UtilValidate.isEmpty(cratesReceivedStr))&&(UtilValidate.isEmpty(cansSentStr))&&(UtilValidate.isEmpty(cansReceivedStr))) {
					continue;
				}
				BigDecimal cratesSent = BigDecimal.ZERO;
				BigDecimal cratesReceived = BigDecimal.ZERO;
				BigDecimal cansSent = BigDecimal.ZERO;
				BigDecimal cansReceived = BigDecimal.ZERO;
				if (!((cratesSentStr == null) || (cratesSentStr.equals("")))) {	
					cratesSent = new BigDecimal(cratesSentStr);
				}
				if (!((cratesReceivedStr == null) || (cratesReceivedStr.equals("")))) {
					cratesReceived = new BigDecimal(cratesReceivedStr);
				}
				if (!((cansSentStr == null) || (cansSentStr.equals("")))) {
					cansSent = new BigDecimal(cansSentStr);
				}
				if (!((cansReceivedStr == null) || (cansReceivedStr.equals("")))) {
					cansReceived = new BigDecimal(cansReceivedStr);
				}
				
				Map<String, Object> crateContext = FastMap.newInstance();
				crateContext.put("userLogin", userLogin);
				crateContext.put("routeId", routeId);
				crateContext.put("cratesSent", cratesSent);
				crateContext.put("cratesReceived", cratesReceived);
				crateContext.put("cansSent", cansSent);
				crateContext.put("cansReceived", cansReceived);
				result = dispatcher.runSync("createOrUpdateCrateCanEntryService",crateContext);
				
				if( ServiceUtil.isError(result)) {
					String errMsg =  ServiceUtil.getErrorMessage(result);
					Debug.logWarning(errMsg , module);
					request.setAttribute("_ERROR_MESSAGE_",errMsg);
					return "error";
				}
			}
		} catch (Exception e) {				
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			Debug.logWarning(e.getMessage() , module);
			return "error";
		}
	    return "success";
	}    
    
	public static Map<String, Object> createOrUpdateCrateCanEntryService(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String routeId = (String) context.get("routeId");
		BigDecimal cratesSent = (BigDecimal) context.get("cratesSent");
		BigDecimal cratesReceived = (BigDecimal) context.get("cratesReceived");
		BigDecimal cansSent = (BigDecimal) context.get("cansSent");
		BigDecimal cansReceived = (BigDecimal) context.get("cansReceived");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Timestamp currentDate = UtilDateTime.nowTimestamp();
		java.sql.Date supplyDate = new java.sql.Date(currentDate.getTime());
		
		try{
			//create or update record in CratesCansAccnt
			GenericValue  cratesCansAccnt = delegator.makeValue("CratesCansAccnt");
			cratesCansAccnt.set("routeId", routeId);
			cratesCansAccnt.set("supplyDate", supplyDate);
			cratesCansAccnt.set("cratesSent", cratesSent);
			cratesCansAccnt.set("cratesReceived", cratesReceived);
			cratesCansAccnt.set("cansSent", cansSent);
			cratesCansAccnt.set("cansReceived", cansReceived);
			cratesCansAccnt.set("createdDate", UtilDateTime.nowTimestamp());
			cratesCansAccnt.set("createdByUserLogin", userLogin.get("userLoginId"));
			cratesCansAccnt.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			cratesCansAccnt.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			
			delegator.createOrStore(cratesCansAccnt);
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}	
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> cancelMilkCardOrder(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String orderId = (String) context.get("orderId");
		String productSubscriptionTypeId = "CARD";
		String milkCardTypeId = null;
		String shipmentTypeId = "AM_SHIPMENT";
		//::TODO:: need to add checks for AM/PM etc
		String subscriptionTypeId ="AM";
		List<GenericValue> milkCardOrderItemList = FastList.newInstance();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> subscriptionList = FastList.newInstance();
		List<GenericValue> subscriptionProductList = FastList.newInstance();
		GenericValue subscription = null;
		GenericValue milkCardItem = null;
		GenericValue cardProduct = null;
		List<GenericValue> milkCardProductMapList = null;
		
		try{
			GenericValue milkCardOrder = delegator.findOne("MilkCardOrder",UtilMisc.toMap("orderId", orderId), false);
			
			if (UtilValidate.isEmpty(milkCardOrder)) {
				String errMsg = "Order does not exist "+orderId;
	            Debug.logError(errMsg, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
	        }
			
			String boothId = milkCardOrder.getString("boothId");
			String customTimePeriodId = milkCardOrder.getString("customTimePeriodId");
			GenericValue timePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
			if (UtilValidate.isEmpty(timePeriod)) {
				String errMsg = "Time Period does not exist for"+customTimePeriodId;
				Debug.logError(errMsg , module);
				return ServiceUtil.returnError( errMsg);
			}			
			Timestamp fromDate = UtilDateTime.toTimestamp(timePeriod.getDate("fromDate"));
			milkCardOrder.set("statusId", "ORDER_CANCELLED");
			delegator.store(milkCardOrder);
			List condList =FastList.newInstance();
			condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
			if(subscriptionTypeId.equals("AM")){
	            condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
	            	
	         }else{
	            condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
	         }
			EntityCondition subCond =  EntityCondition.makeCondition(condList ,EntityOperator.AND);
			subscriptionList = delegator.findList("Subscription",subCond, null, null, null,false);
			subscriptionList = EntityUtil.filterByDate(subscriptionList);
			
			if (UtilValidate.isEmpty(subscriptionList)) {
				String errMsg = "Booth subscription does not exist "+boothId;
		        Debug.logError(errMsg , module);
				return ServiceUtil.returnError(errMsg);
			}			
			
			subscription = EntityUtil.getFirst(subscriptionList);
			milkCardOrderItemList = delegator.findList("MilkCardOrderItem",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS, orderId), null, null, null,false);
			int listSize = milkCardOrderItemList.size();
			
			for (int i = 0; i < listSize; i++) {
				milkCardItem = milkCardOrderItemList.get(i);
				BigDecimal milkCardItemQuantity = milkCardItem.getBigDecimal("quantity");					
				milkCardProductMapList = delegator.findList("MilkCardProductMap",EntityCondition.makeCondition("milkCardTypeId",EntityOperator.EQUALS,milkCardItem.getString("milkCardTypeId")), null, null, null,false);
				
				if (UtilValidate.isEmpty(milkCardProductMapList)) {
					String errMsg = "Milk Card Product Maping does not exist for milkcardType"+milkCardItem.getString("milkCardTypeId");
			        Debug.logError(errMsg , module);
					return ServiceUtil.returnError(errMsg);
				}
				
				for (int j = 0; j < milkCardProductMapList.size(); j++) {
					cardProduct = milkCardProductMapList.get(j);					
					BigDecimal productMapQuantity = cardProduct.getBigDecimal("quantity");
					BigDecimal quantityToBeSubstract = milkCardItemQuantity.multiply(productMapQuantity);
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.EQUALS,productSubscriptionTypeId));
					conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS,(String) subscription.getString("subscriptionId")));
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,(String) cardProduct.getString("productId")));
					conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> subscriptionProductsList = FastList.newInstance();
					subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null,null, false);
					
					if (UtilValidate.isEmpty(subscriptionProductsList)) {
						String errMsg = UtilProperties.getMessage(resourceError," subscriptionProduct does not exit for subscriptionId " +subscription.getString("subscriptionId")+" and productId "+cardProduct.getString("productId"), locale);
				        Debug.logWarning(errMsg , module);
				        return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
					}
					
					Map updateSubscriptionProduct = FastMap.newInstance();
					GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
					updateSubscriptionProduct.put("userLogin",userLogin);
					updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
					updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
					updateSubscriptionProduct.put("productId",cardProduct.getString("productId"));					
					BigDecimal quantityToUpdate = (subscriptionProduct.getBigDecimal("quantity")).subtract(quantityToBeSubstract);
					
					if (quantityToUpdate.compareTo(BigDecimal.ZERO) < 0) {
						String errMsg = UtilProperties.getMessage(resourceError,"quantity" +quantityToUpdate+" cannot be negative for "+subscription.getString("subscriptionId")+" and productId "+cardProduct.getString("productId"), locale);
				        Debug.logWarning(errMsg, module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));	
					}
					else{
						updateSubscriptionProduct.put("quantity",quantityToUpdate);
					}
					updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
					updateSubscriptionProduct.put("fromDate", fromDate);
					updateSubscriptionProduct.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					updateSubscriptionProduct.put("lastModifiedDate", UtilDateTime.nowTimestamp());
					result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);					
					if( ServiceUtil.isError(result)) {
						String errMsg = "Unable to update SubscriptionProduct for subscriptionId "+subscription.getString("subscriptionId")+" and productId "+cardProduct.getString("productId");
				        Debug.logError(errMsg , module);
						return ServiceUtil.returnError(errMsg);
                    }
					//lets update the MilkCardTotal entity here for feature validation 
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, cardProduct.getString("productId")));
					conditionList.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS,"PRODUCT_VARIANT"));
					EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List productAssocList = EntityUtil.filterByDate(delegator.findList("ProductAndAssoc", cond, null, null,null, false));
					GenericValue productAssoc = EntityUtil.getFirst(productAssocList);
					
					GenericValue productCardTotal = delegator.findOne("MilkCardTotal", UtilMisc.toMap("customTimePeriodId", customTimePeriodId ,"productId" , productAssoc.getString("productId")), false);
					if(UtilValidate.isEmpty(productCardTotal)){
						String errMsg = "No Records found for productId ==>"+cardProduct.getString("productId") +"in MilkCardTotal Entity";
				        Debug.logError(errMsg , module);
						return ServiceUtil.returnError(errMsg);
					}					
					productCardTotal.set("quantity", productCardTotal.getBigDecimal("quantity").subtract(quantityToBeSubstract.divide(productAssoc.getBigDecimal("quantity"))));
					delegator.store(productCardTotal);
				}//for
				
			}// for
			
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}		
		return ServiceUtil.returnSuccess();
	}

	public static String editMilkCardOrder(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String orderId = (String) request.getParameter("orderId");
		String milkCardTypeId = null;
		String quantityStr = null;
		BigDecimal paramQuantity = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		List<GenericValue> milkCardProductMapList = null;
		GenericValue milkCard = null;
		Map<String, Object> cardMap = FastMap.newInstance();
		try{
			GenericValue milkCardOrder = delegator.findOne("MilkCardOrder",UtilMisc.toMap("orderId", orderId), false);
			
			if (UtilValidate.isEmpty(milkCardOrder)) {
				String errMsg = UtilProperties.getMessage(resourceError,"Order does not exist "+orderId, locale);
				Debug.logWarning(errMsg, module);
				request.setAttribute("_ERROR_MESSAGE_","Order does not exist "+orderId);
				return "error";
			}
			
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
			
			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
				request.setAttribute("_ERROR_MESSAGE_", "No Card Types records found");	
				return "error";
			}
			
			for (int i = 0; i < rowCount; i++) {
				List<GenericValue> subscriptionProductsList = FastList.newInstance();
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("milkCardTypeId" + thisSuffix)) {
					milkCardTypeId = (String) paramMap.remove("milkCardTypeId"+ thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
				}
				if ((quantityStr == null) || (quantityStr.equals(""))) {
					continue;
				}
				paramQuantity = new BigDecimal(quantityStr);
				if (paramQuantity.compareTo(BigDecimal.ZERO) < 0) {
					String errMsg = UtilProperties.getMessage(resourceError,"quantity" +paramQuantity+" cannot be negative for ", locale);
			        Debug.logWarning(errMsg, module);
			        request.setAttribute("_ERROR_MESSAGE_", "quantity" +paramQuantity+" cannot be negative for");	
			        return "error";	
				}
				cardMap.put(milkCardTypeId, paramQuantity);
			}// for
	
			Map<String, Object> cardContext = FastMap.newInstance();
			cardContext.put("userLogin", userLogin);   
			cardContext.put("cardDetails", cardMap);
			cardContext.put("orderId", orderId);
			result = dispatcher.runSync("editServiceMilkCardOrder",cardContext);
			
			if( ServiceUtil.isError(result)) {
				String errMsg = UtilProperties.getMessage(resourceError,"Service failure for editServiceMilkCardOrder", locale);
				Debug.logWarning(errMsg , module);
				request.setAttribute("_ERROR_MESSAGE_","Service failure for editServiceMilkCardOrder");
				return "error";
			}
			
		}catch (Exception e) {				
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			Debug.logWarning(e.getMessage() , module);
			return "error";
		}
		return "success";
	}

	public static Map<String, Object> editServiceMilkCardOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String orderId = (String) context.get("orderId");
		Map<String, Object> cardMap = (Map<String, Object>) context.get("cardDetails");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> milkCardOrderItemList = FastList.newInstance();
		List<GenericValue> milkCardProductMapList = null;
		GenericValue milkCard = null;
		GenericValue milkCardOrder = null;
		GenericValue cardProduct = null;
		String milkCardTypeId = null;
		GenericValue subscription = null;
		BigDecimal cardPrice = null;
		List<GenericValue> subscriptionList = FastList.newInstance();
		String productSubscriptionTypeId = "CARD";
		BigDecimal grandTotal=BigDecimal.ZERO;
		BigDecimal quantity = null;
		BigDecimal quantityToCreate = BigDecimal.ZERO;
		BigDecimal quantityFromCard = BigDecimal.ZERO;
		BigDecimal quantityToSubstract = BigDecimal.ZERO;
		//::TODO:: need to add checks for AM/PM etc
		String subscriptionTypeId ="AM";
		List<String> list = new ArrayList<String>(cardMap.keySet());
		
		try{
			 milkCardOrder = delegator.findOne("MilkCardOrder",UtilMisc.toMap("orderId", orderId), false);
		
			if (UtilValidate.isEmpty(milkCardOrder)) {
				String errMsg = UtilProperties.getMessage(resourceError,"Order does not exist "+orderId, locale);
				Debug.logWarning(errMsg, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
			}
			String boothId = milkCardOrder.getString("boothId");
			String customTimePeriodId = milkCardOrder.getString("customTimePeriodId");
			GenericValue timePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
			
			if (UtilValidate.isEmpty(timePeriod)) {
				String errMsg = UtilProperties.getMessage(resourceError,"Time Period does not exist for"+customTimePeriodId, locale);
				Debug.logWarning(errMsg , module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
			}
			
			Timestamp fromDate = UtilDateTime.toTimestamp(timePeriod.getDate("fromDate"));
			List conditionList =FastList.newInstance();
			  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
			  if(subscriptionTypeId.equals("AM")){
	            	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
	            	
	            }else{
	            	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
	            }
			EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
			subscriptionList = delegator.findList("Subscription",subCond, null, null, null,false);
			subscriptionList = EntityUtil.filterByDate(subscriptionList);
		
			if (UtilValidate.isEmpty(subscriptionList)) {
				String errMsg = UtilProperties.getMessage(resourceError,"Booth subscription does not exist "+boothId, locale);
				Debug.logWarning(errMsg , module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
			}			
			
			subscription = EntityUtil.getFirst(subscriptionList);
			milkCardOrderItemList = delegator.findList("MilkCardOrderItem",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS, orderId), null, null, null,false);

			for (int i = 0; i < milkCardOrderItemList.size(); i++) {
				
				milkCard = milkCardOrderItemList.get(i);
				milkCardTypeId = milkCard.getString("milkCardTypeId");
				cardPrice = milkCard.getBigDecimal("unitPrice");
				quantity = milkCard.getBigDecimal("quantity");

				if(milkCardTypeId.equals(list.get(i))){
					List <GenericValue> toStore = new LinkedList <GenericValue> ();
					grandTotal=grandTotal.add(cardPrice.multiply((BigDecimal) cardMap.get(milkCardTypeId)));
					GenericValue bulkCard = delegator.makeValue("MilkCardOrderItem");
					toStore.add(bulkCard);
					bulkCard.set("orderId", orderId);
					bulkCard.set("milkCardTypeId", milkCardTypeId);
					bulkCard.set("quantity", cardMap.get(milkCardTypeId));
					bulkCard.set("unitPrice", cardPrice);
					delegator.storeAll(toStore);
					milkCardProductMapList = delegator.findList("MilkCardProductMap",EntityCondition.makeCondition("milkCardTypeId",EntityOperator.EQUALS,milkCard.getString("milkCardTypeId")), null, null, null,false);
					
					if (UtilValidate.isEmpty(milkCardProductMapList)) {
						String errMsg = UtilProperties.getMessage(resourceError,"MilkCardType does not exist "+milkCard.getString("milkCardTypeId"), locale);
						Debug.logWarning(errMsg , module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
					}
					
					for (int j = 0; j < milkCardProductMapList.size(); j++) {
						cardProduct = milkCardProductMapList.get(j);
						String qty = cardProduct.getString("quantity");
						quantityFromCard = new BigDecimal(qty);
						quantityToSubstract = quantityFromCard.multiply((BigDecimal) cardMap.get(milkCardTypeId));
						quantityToCreate = quantityFromCard.multiply(quantity);
						BigDecimal quantityToAdd = quantityToSubstract.subtract(quantityToCreate);

						/*if(quantityToAdd.compareTo(BigDecimal.ZERO) != 0){
							List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.EQUALS,productSubscriptionTypeId));
							conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS,(String) subscription.getString("subscriptionId")));
							conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,(String) cardProduct.getString("productId")));
							conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
							EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							List<GenericValue> subscriptionProductsList = FastList.newInstance();
							subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null,null, false);
							
							if (UtilValidate.isEmpty(subscriptionProductsList)) {
								String errMsg = UtilProperties.getMessage(resourceError," subscriptionProduct does not exit for subscriptionId " +subscription.getString("subscriptionId")+" and productId "+cardProduct.getString("productId"), locale);
								Debug.logWarning(errMsg , module);
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));
							}
			
							Map updateSubscriptionProduct = FastMap.newInstance();
							GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
							updateSubscriptionProduct.put("userLogin",userLogin);
							updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
							updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
							updateSubscriptionProduct.put("productId",cardProduct.getString("productId"));					
							BigDecimal quantityToUpdate = (subscriptionProduct.getBigDecimal("quantity")).add(quantityToAdd);
							
							if (quantityToUpdate.compareTo(BigDecimal.ZERO) < 0) {
								String errMsg = UtilProperties.getMessage(resourceError,"quantity" +quantityToUpdate+" cannot be negative for "+subscription.getString("subscriptionId")+" and productId "+cardProduct.getString("productId"), locale);
						        Debug.logWarning(errMsg, module);
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg, locale));	
							}
							
							updateSubscriptionProduct.put("quantity",quantityToUpdate);
							updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
							updateSubscriptionProduct.put("fromDate", fromDate);
							updateSubscriptionProduct.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
							updateSubscriptionProduct.put("lastModifiedDate", UtilDateTime.nowTimestamp());
							result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
							
							if( ServiceUtil.isError(result)) {
								String errMsg = UtilProperties.getMessage(resourceError,"Unable to update SubscriptionProduct for subscriptionId "+subscription.getString("subscriptionId")+" and productId "+cardProduct.getString("productId"), locale);
								Debug.logWarning(errMsg , module);
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, errMsg+result, locale));
							}
						}//if for update only changed quantity
*/
					}//for
				}//if
			}//for
			milkCardOrder.set("grandTotal", grandTotal);
			delegator.store(milkCardOrder);	
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}

}
