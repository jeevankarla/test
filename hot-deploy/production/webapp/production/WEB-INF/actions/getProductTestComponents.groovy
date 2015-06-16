
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
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
resultReturn = ServiceUtil.returnSuccess();
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
	productCatList.add("_NA_");
}


if(productId.equalsIgnoreCase("_NA_") && productCategoryId.equalsIgnoreCase("_NA_")){
	Debug.logError("Please Selcet Valid productId or category :","");
	resultReturn = ServiceUtil.returnError("Please Selcet Valid productId or category :");
	return resultReturn;
}

GenericValue productDetails = delegator.findOne("Product",[productId : productId],false);
//ProductTestComponent


List<String> productCatIds = EntityUtil.getFieldListFromEntityList(productCatList, "productCategoryId", false);
List productTestComponentCondList = FastList.newInstance();
productTestComponentCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId),EntityOperator.OR,EntityCondition.makeCondition("productCategoryId",EntityOperator.IN,productCatIds)));
EntityCondition productTestComponentCondition = EntityCondition.makeCondition(productTestComponentCondList);

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
productTestComponentDetails.put("productName",productDetails.get("productName"));

resultReturn.putAt("productTestComponentDetails", productTestComponentDetails);

return resultReturn;




