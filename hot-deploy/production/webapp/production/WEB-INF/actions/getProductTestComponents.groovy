
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import javolution.util.FastList;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.List;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;

dctx = dispatcher.getDispatchContext();
Map resultReturn = ServiceUtil.returnSuccess();
String productId = parameters.get("productId");
String productCategoryId = parameters.get("productCategoryId");

List productCatList = FastList.newInstance();
if(UtilValidate.isEmpty(productId)){
	productId = "_NA_";
}
if(UtilValidate.isEmpty(productCategoryId)){
	if(!productId.equalsIgnoreCase("_NA_")){
		List prodCatCondList = FastList.newInstance();
		prodCatCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		prodCatCondList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.LIKE,"QC_%"));
		EntityCondition prodCatCondition = EntityCondition.makeCondition(prodCatCondList);
		productCatList = delegator.findList("ProductCategoryMember",prodCatCondition,null,null,null,false);
	}
	
	//productCategoryId = "_NA_";
}
if(UtilValidate.isEmpty(productCatList)){
	GenericValue productCatDetails = delegator.makeValue("ProductCategoryMember");
	productCatDetails.set("productCategoryId", "_NA_");
	productCatList.add(productCatDetails);
}

if(productId.equalsIgnoreCase("_NA_") && productCategoryId.equalsIgnoreCase("_NA_")){
	Debug.logError("Please Selcet Valid productId or category :","");
	resultReturn = ServiceUtil.returnError("Please Selcet Valid productId or category :");
	return resultReturn;
}

GenericValue productDetails = delegator.findOne("Product",[productId : productId],false);
//ProductTestComponent


List<String> productCatIds = EntityUtil.getFieldListFromEntityList(productCatList, "productCategoryId", false);
if(productCatIds.size()>1){
	productCatIds.remove("_NA_");
	
}
List productTestComponentCondList = FastList.newInstance();
List conditionList = FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
conditionList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,productCatIds));
EntityCondition productTestComponentCondition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.OR);
if(productCatIds.contains("_NA_")){
	productTestComponentCondition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
}
List<GenericValue> productTestComponents = delegator.findList("ProductTestComponent",productTestComponentCondition,null,null,null,false);
List productTestComponentsResult = FastList.newInstance();  

productTestComponentsResult = EntityUtil.filterByCondition(productTestComponents,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
if(UtilValidate.isEmpty(productTestComponentsResult)){
	productTestComponentsResult = productTestComponents;
}

if(UtilValidate.isNotEmpty(productTestComponentsResult)){
	productTestComponentsResult = UtilMisc.sortMaps(productTestComponentsResult, UtilMisc.toList("sequenceId"))
}
Map productTestComponentDetails = FastMap.newInstance();
productTestComponentDetails.put("productTestComponents",productTestComponentsResult);
if(UtilValidate.isEmpty(productTestComponentsResult)){
	
	resultReturn = ServiceUtil.returnError("No components found");
	return resultReturn
}
productTestComponentDetails.put("productName",productDetails.get("productName"));
resultReturn.putAt("productTestComponentDetails", productTestComponentDetails);
return resultReturn;




