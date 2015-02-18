/*
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
 */

package org.ofbiz.product.supplier;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Services for suppliers of products
 */
public class SupplierProductServices {

    public static final String module = SupplierProductServices.class.getName();
    public static final String resource = "ProductUiLabels";

    /*
     * Parameters: productId, partyId, currencyUomId, quantity
     * Result: a List of SupplierProduct entities for productId,
     *         filtered by date and optionally by partyId, ordered with lowest price first
     */
    public static Map<String, Object> getSuppliersForProduct(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> results = FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();

        GenericValue product = null;
        String productId = (String) context.get("productId");
        String partyId = (String) context.get("partyId");
        String currencyUomId = (String) context.get("currencyUomId");
        BigDecimal quantity =(BigDecimal) context.get("quantity");
        String canDropShip = (String) context.get("canDropShip");
        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            if (product == null) {
                results = ServiceUtil.returnSuccess();
                results.put("supplierProducts",null);
                return results;
            }
            List<GenericValue> supplierProducts = product.getRelatedCache("SupplierProduct");

            // if there were no related SupplierProduct entities and the item is a variant, then get the SupplierProducts of the virtual parent product
            if (supplierProducts.size() == 0 && product.getString("isVariant") != null && product.getString("isVariant").equals("Y")) {
                String virtualProductId = ProductWorker.getVariantVirtualId(product);
                GenericValue virtualProduct = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", virtualProductId));
                if (virtualProduct != null) {
                    supplierProducts = virtualProduct.getRelatedCache("SupplierProduct");
                }
            }

            // filter the list down by the partyId if one is provided
            if (partyId != null) {
                supplierProducts = EntityUtil.filterByAnd(supplierProducts, UtilMisc.toMap("partyId", partyId));
            }

            // filter the list down by the currencyUomId if one is provided
            if (currencyUomId != null) {
                supplierProducts = EntityUtil.filterByAnd(supplierProducts, UtilMisc.toMap("currencyUomId", currencyUomId));
            }

            // filter the list down by the minimumOrderQuantity if one is provided
            if (quantity != null) {
                //minimumOrderQuantity
                supplierProducts = EntityUtil.filterByCondition(supplierProducts, EntityCondition.makeCondition("minimumOrderQuantity", EntityOperator.LESS_THAN_EQUAL_TO, quantity));
            }

            // filter the list down by the canDropShip if one is provided
            if (canDropShip != null) {
                supplierProducts = EntityUtil.filterByAnd(supplierProducts, UtilMisc.toMap("canDropShip", canDropShip));
            }

            // filter the list down again by date before returning it
            supplierProducts = EntityUtil.filterByDate(supplierProducts, UtilDateTime.nowTimestamp(), "availableFromDate", "availableThruDate", true);

            //sort resulting list of SupplierProduct entities by price in ASCENDING order
            supplierProducts = EntityUtil.orderBy(supplierProducts, UtilMisc.toList("lastPrice ASC"));

            results = ServiceUtil.returnSuccess();
            results.put("supplierProducts", supplierProducts);
        } catch (GenericEntityException ex) {
            Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
        } catch (Exception ex) {
            Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
        }
        return results;
    }

    /*
     * Parameters: partyId of a supplier and productFeatures, a Collection (usually List) of product features
     * Service will convert each feature in the Collection, changing their idCode and description based on the
     * SupplierProduct entity for that supplier party and feature, and return it as convertedProductFeatures
     */
    public static Map<String, Object> convertFeaturesForSupplier(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> results = FastMap.newInstance();
        String partyId = (String) context.get("partyId");
        Collection<GenericValue> features = UtilGenerics.checkList(context.get("productFeatures"));

        try {
            if (partyId != null && UtilValidate.isNotEmpty(features)) {
                // loop through all the features, find the related SupplierProductFeature for the given partyId, and
                // substitue description and idCode
                for (GenericValue nextFeature: features) {
                    List<GenericValue> supplierFeatures = EntityUtil.filterByAnd(nextFeature.getRelated("SupplierProductFeature"),
                                                                   UtilMisc.toMap("partyId", partyId));
                    GenericValue supplierFeature = null;

                    if ((supplierFeatures != null) && (supplierFeatures.size() > 0)) {
                        supplierFeature = supplierFeatures.get(0);
                        if (supplierFeature.get("description") != null) {
                            nextFeature.put("description", supplierFeature.get("description"));
                        }
                        if (supplierFeature.get("idCode") != null) {
                            nextFeature.put("idCode", supplierFeature.get("idCode"));
                        }
                        // TODO: later, do some kind of uom/quantity conoversion with the UomConversion entity
                    }
                }
            }
            results = ServiceUtil.returnSuccess();
            results.put("convertedProductFeatures", features);
        } catch (GenericEntityException ex) {
            Debug.logError(ex, ex.getMessage(), module);
            return ServiceUtil.returnError(ex.getMessage());
        }
        return results;
    }
    //To create supplier product when creating Order..
    public static Map<String, Object> createSupplierProductFromOrder(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String) context.get("orderId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp orderDate = UtilDateTime.nowTimestamp();
		Map result = ServiceUtil.returnSuccess();
		List<GenericValue> orderItems = FastList.newInstance();
		BigDecimal unitPrice = BigDecimal.ZERO;
		String productId = null;
		try{
			if(UtilValidate.isNotEmpty(orderId)){
				
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if(UtilValidate.isNotEmpty(orderHeader)){
					orderDate = orderHeader.getTimestamp("orderDate");
				}
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> orderRole = delegator.findList("OrderRole", condition, null, null, null, false);
				if(UtilValidate.isEmpty(orderRole)){
					Debug.logError("No Vendor for the order : "+orderId, module);
					return ServiceUtil.returnError("No Vendor for the order : "+orderId);
				}
				String supplierId = (EntityUtil.getFirst(orderRole)).getString("partyId");
				if(UtilValidate.isNotEmpty(orderItems)){
					for(int i=0;i<orderItems.size();i++){
						Map<String,Object> orderItemMap = FastMap.newInstance();
						orderItemMap = (Map)orderItems.get(i);
						productId = (String) orderItemMap.get("productId");
						unitPrice = (BigDecimal) orderItemMap.get("unitPrice");
						
						List suppProdList = FastList.newInstance();
						suppProdList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
						suppProdList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
						EntityCondition suppProdCond = EntityCondition.makeCondition(suppProdList, EntityOperator.AND);
						List<GenericValue> supplierProductList = EntityUtil.filterByDate(delegator.findList("SupplierProduct", suppProdCond, null, UtilMisc.toList("-availableFromDate"), null, false));
						if(UtilValidate.isEmpty(supplierProductList)){
							Map<String,Object> suppProdMap = FastMap.newInstance();
							suppProdMap.put("userLogin",userLogin);
							suppProdMap.put("productId",productId);
							suppProdMap.put("partyId",supplierId);
							suppProdMap.put("availableFromDate",orderDate);
							suppProdMap.put("minimumOrderQuantity",BigDecimal.ZERO);
							suppProdMap.put("currencyUomId","INR");
							suppProdMap.put("supplierProductId",productId);
							suppProdMap.put("lastPrice",unitPrice);
					        Map resultMap = dispatcher.runSync("createSupplierProduct",suppProdMap);
					        if (ServiceUtil.isError(resultMap)) {
					        	Debug.logError("Problem creating supplier product for orderId :"+orderId, module);
								return ServiceUtil.returnError("Problem creating supplier product for orderId :"+orderId);
					        }
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception	
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
}
