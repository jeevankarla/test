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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;

import org.ofbiz.product.product.ProductWorker;

import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;



prodList = [];
try{
 productCategoryList = delegator.findByAnd("ProductCategory", UtilMisc.toMap("productCategoryTypeId","BYPROD"));
 productCategoryIdsList = EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
	productList = ProductWorker.getProductsByCategoryList(delegator, productCategoryIdsList, null);
	 prodIdsList = [];
	for(GenericValue product : productList){
		String productId = product.getString("productId");
		if(!prodIdsList.contains(productId)){
			prodIdsList.add(productId);
			prodList.add(product);
		}
	}
	
}catch(Exception e){
Debug.logError(e, "Unable get the products" );
}

productIds = EntityUtil.getFieldListFromEntityList(prodList, "productId", true);

Map result = (Map)MaterialHelperServices.getProductUOM(delegator, productIds);
uomLabelMap = result.get("uomLabel");
productUomMap = result.get("productUom");


//	resultMap = MaterialHelperServices.getMaterialProducts(dctx, context);
//	prodList = resultMap.get("productList");
JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
//context.productList = prodList;
JSONObject productUOMJSON = new JSONObject();
JSONObject uomLabelJSON=new JSONObject();

prodList.each{eachItem ->
JSONObject newObj = new JSONObject();
newObj.put("value",eachItem.productId);
newObj.put("label","[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
productItemsJSON.add(newObj);
productIdLabelJSON.put(eachItem.productId, "[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
productLabelIdJSON.put("[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName, eachItem.productId);

if(productUomMap){
	uomId = productUomMap.get(eachItem.productId);
	if(uomId){
		productUOMJSON.put(eachItem.productId, uomId);
		uomLabelJSON.put(uomId, uomLabelMap.get(uomId));
	}
}
}

//Debug.log("====productUOMJSON=="+productUOMJSON+"====uomLabelJSON==="+uomLabelJSON);

context.productUOMJSON = productUOMJSON;
context.uomLabelJSON = uomLabelJSON;

context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productLabelIdJSON = productLabelIdJSON;