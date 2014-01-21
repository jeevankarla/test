
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.NetworkServices;
import java.text.SimpleDateFormat;
import java.text.ParseException;


effectiveDate = null;
SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
try {
	effectiveDate = new java.sql.Timestamp(sdf.parse(defaultEffectiveDate).getTime());
} catch(ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
}

effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
dctx = dispatcher.getDispatchContext();

condList = [];
condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
condList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN_EQUAL_TO, effDateDayBegin)));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
prodList = delegator.findList("Product", cond, UtilMisc.toSet("productId", "description","quantityIncluded"), UtilMisc.toList("sequenceNum"), null, false);


/*crateProducts = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "CRATE_INDENT"), UtilMisc.toSet("productId"), null, null, false);
crateProdIds = EntityUtil.getFieldListFromEntityList(crateProducts, "productId", true);

productInc = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, crateProdIds), UtilMisc.toSet("productId", "quantityIncluded"), null, null, false);

qtyIncMap = [:];
if(productInc){
	productInc.each{ eachEle ->
		qtyIncMap.put(eachEle.productId, eachEle.quantityIncluded);
	}
}
*/
/*productCategoryList = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PROD_INDENT_CAT"), UtilMisc.toSet("productCategoryId"), null, null, false);
productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
prodCatList = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds), UtilMisc.toSet("productCategoryId", "productId"), null, null, false);

prodCatMap = [:];
if(prodCatList){
	prodCatList.each{ eachItemCat ->
		prodCatMap.put(eachItemCat.productId, eachItemCat.productCategoryId);
	}
}
prodCatList = EntityUtil.getFieldListFromEntityList(prodCatList, "productId", true);*/

JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON = new JSONObject();
JSONObject productQtyIncJSON = new JSONObject();
/*JSONObject productCratesJSON = new JSONObject();*/
context.productList = prodList;
prodList.each{eachItem ->
	/*if(prodCatMap.get(eachItem.productId)){
		if((prodCatMap.get(eachItem.productId)).equals("CRATE_INDENT")){
			tempQtyInc = qtyIncMap.get(eachItem.productId);
			packetQty = NetworkServices.convertCratesToPackets(tempQtyInc , 1);
			productCratesJSON.putAt(eachItem.productId, packetQty);
		}
		else{
			productCratesJSON.putAt(eachItem.productId, 1);
		}
	}*/
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label", eachItem.description);
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId,  eachItem.description);
	productLabelIdJSON.putAt(eachItem.description, eachItem.productId);
	productQtyIncJSON.putAt(eachItem.productId, eachItem.quantityIncluded);
}
context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productLabelIdJSON = productLabelIdJSON;
context.productQtyIncJSON = productQtyIncJSON;
/*context.productCratesJSON = productCratesJSON;*/
