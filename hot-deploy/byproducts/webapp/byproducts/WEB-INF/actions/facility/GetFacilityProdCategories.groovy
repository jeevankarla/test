import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

facilityId = parameters.facilityId;
indentQtyCategoryList = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PROD_INDENT_CAT"), UtilMisc.toSet("productCategoryId"), null, null, false);
indentQtyCatList = EntityUtil.getFieldListFromEntityList(indentQtyCategoryList, "productCategoryId", true);
context.indentQtyCategoryList = indentQtyCategoryList;

productCategories = delegator.findList("ProductAndCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, indentQtyCatList), UtilMisc.toSet("productId", "primaryProductCategoryId", "productCategoryId"), UtilMisc.toList("sequenceNum"), null, false);
indentProdCatList = [];
dctx = dispatcher.getDispatchContext();


result = ByProductNetworkServices.getFacilityIndentQtyCategories(delegator, dctx.getDispatcher(),UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId));
indentQtyCatMap = result.get("indentQtyCategory");
productCategories.each{ eachItem ->
	indentCatMap = [:];
	indentCatMap.productId = eachItem.productId;
	indentCatMap.prodCategory = eachItem.primaryProductCategoryId;
	indentCatMap.presentCategory = indentQtyCatMap.get(eachItem.productId);
	indentProdCatList.add(indentCatMap);
}
context.indentProdCatList = indentProdCatList;
