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
import org.ofbiz.party.party.PartyHelper;



if(parameters.boothId){
	parameters.boothId = parameters.boothId.toUpperCase();
}
if(UtilValidate.isNotEmpty(parameters.productStoreIdFrom)){
	parameters.productStoreId = parameters.productStoreIdFrom;
	productStoreId=parameters.productStoreIdFrom;
}
boothId = parameters.boothId;

subscriptionTypeId = parameters.subscriptionTypeId;
productSubscriptionTypeId = parameters.productSubscriptionTypeId;
shipmentTypeId = parameters.shipmentTypeId;
dctx = dispatcher.getDispatchContext();
effectiveDate = parameters.effectiveDate;
priceTypeId=parameters.priceTypeId;
changeFlag=parameters.changeFlag;

productCatageoryId=parameters.productCatageoryId;
partyId = parameters.partyId;

//Quotas handling

resultCtx = dispatcher.runSync("getPartySchemeEligibility",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
schemesMap = resultCtx.get("schemesMap");

productCategoryQuotasMap = [:];
if(UtilValidate.isNotEmpty(schemesMap.get("TEN_PERCENT_MGPS"))){
	productCategoryQuotasMap = schemesMap.get("TEN_PERCENT_MGPS");
}

// Get Scheme Categories
schemeCategoryIds = [];
productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);

condsList = [];
//condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
if(effectiveDate){
condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
	  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
 }
  prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), null, null, null, true);
	//productCategoriesList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productCategoryId", true);
  JSONObject productQuotaJSON=new JSONObject();
  for(int i=0; i<prodCategoryMembers.size(); i++){
	  schemeProdId = (prodCategoryMembers.get(i)).get("productId");
	  schemeCatId = (prodCategoryMembers.get(i)).get("productCategoryId");
	  if(productCategoryQuotasMap.containsKey(schemeCatId)){
		  quota = (productCategoryQuotasMap.get(schemeCatId)).get("availableQuota");
		  productQuotaJSON.put(schemeProdId, quota);
	  }
  }
  context.productQuotaJSON = productQuotaJSON;
  request.setAttribute("productQuotaJSON", productQuotaJSON);
  
return "success";
