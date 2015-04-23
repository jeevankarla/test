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


effectiveDate = parameters.effectiveDate;
partyId = parameters.partyId;
dctx = dispatcher.getDispatchContext();

SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
if(UtilValidate.isNotEmpty(effectiveDate)){
try {
	effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
}catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
	displayGrid = false;
}
	effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
	effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
}else{
	effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	effDateDayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
}
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
Map inputProductRate = FastMap.newInstance();
inputProductRate.put("partyId",partyId);
inputProductRate.put("userLogin",userLogin);
inputProductRate.put("priceDate",effDateDayBegin);

priceResultMap = [:];
	//inputProductRate.put("priceDate",effDateDayBegin);
	inputProductRate.put("productList",prodList);
	//inputProductRate.put("productCategoryId", productCatageoryId);
	//inputProductRate.put("productPriceTypeId", "BYPROD");
	priceResultMap = ByProductNetworkServices.getStoreProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	prodPriceMap = (Map)priceResultMap.get("priceMap");
request.setAttribute("prodPriceMap", prodPriceMap);


