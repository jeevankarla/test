import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementServices;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;

dctx = dispatcher.getDispatchContext();
// get the shed id based on userLogin 
// for now allways first shed 
shedList = ProcurementNetworkServices.getSheds(delegator);
shedId = "";
tenConfigFacilityId ="";
if(UtilValidate.isNotEmpty(context.getAt("shedId"))){
	tenConfigFacilityId = context.get("shedId");
	}
if(UtilValidate.isNotEmpty(context.getAt("unitId"))){
	tenConfigFacilityId = context.getAt("unitId");
	}

if(shedList){	
	shedId = EntityUtil.getFirst(shedList).get("facilityId");
}

Map tenantConfigSettings = FastMap.newInstance();
Map tenantConfigMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(tenConfigFacilityId)){
	tenantConfigMap = dispatcher.runSync("getFacilityTenantConfigurations",UtilMisc.toMap("userLogin", userLogin, "facilityId", tenConfigFacilityId));
	if(ServiceUtil.isSuccess(tenantConfigMap)){
		tenantConfigSettings = tenantConfigMap.get("tenantConfigurationsMap");
	}
}
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
milkTypeBySeqNum = "";
milkType = delegator.findOne("FacilityAttribute",[facilityId : tenConfigFacilityId, attrName: "milkTypeBySeqNum"], false);
if(UtilValidate.isNotEmpty(milkType)){
	milkTypeBySeqNum = milkType.get("attrValue");
}
if((UtilValidate.isNotEmpty(milkTypeBySeqNum))&&(("Y".equals(milkTypeBySeqNum)))){
	procurementProductList = UtilMisc.sortMaps(procurementProductList, UtilMisc.toList("-sequenceNum"));
}
context.put("productList",procurementProductList);
JSONObject productBrandNameJson = new JSONObject();

for( GenericValue product : procurementProductList){
	productBrandNameJson.put(product.productId, product.brandName);
}
context.put("productBrandNameJson",productBrandNameJson);
shedsList = ProcurementNetworkServices.getSheds(dctx.getDelegator());
context.put("shedList",shedsList);


purchaseTimeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SUPPLY_TYPE"), null, null, null, true);
context.put("purchaseTimeList",purchaseTimeList);
tempShedsList = [];

if(UtilValidate.isNotEmpty(context.getAt("shedId"))){
	Map shedMap = [:];
	shedMap.put("facilityId",context.shedId);
	shedMap.put("facilityName",context.shedName);
	shedMap.put("facilityCode",context.shedCode);
	tempShedsList.add(shedMap);
	}else{
		tempShedsList.add(shedsList);
	}
JSONObject shedJson = new JSONObject();

for(shed in shedsList){
	shedUnitsList = ProcurementNetworkServices.getShedUnitsByShed(dctx , UtilMisc.toMap("shedId", shed.facilityId)).get("unitsDetailList");
	
	
	JSONObject unitJson = new JSONObject();
	
	for( GenericValue shedUnit : shedUnitsList){	
		JSONObject centerJson = new JSONObject();
	   UnitAgentsList = ProcurementNetworkServices.getUnitAgents(dctx , UtilMisc.toMap("unitId",shedUnit.facilityId)).get("agentsList");
		for( GenericValue agent : UnitAgentsList){
			centerJson.put(agent.getString("facilityCode"),agent.getString("facilityName"));
		}
		
		JSONObject unitJsonValue = new JSONObject();
		unitJsonValue.put("name", shedUnit.facilityName);
		unitJsonValue.put("centers", centerJson);
		unitJson.putAt(shedUnit.facilityCode, unitJsonValue);
	}
	shedJson.put(shed.facilityCode,unitJson);
}	

	context.put("shedJson" , shedJson);
JSONObject recentChangeJson = new JSONObject();
procurementEntryList = context.procurementEntryList;
if(UtilValidate.isNotEmpty(procurementEntryList)){
	recentChangeJson = procurementEntryList.get(0);
}
if(UtilValidate.isNotEmpty(recentChangeJson.quantityKgs)){
	recentChangeJson.putAt("quantity",recentChangeJson.quantityKgs);
	}
if(UtilValidate.isNotEmpty(recentChangeJson.quantityLtrs)){
	recentChangeJson.putAt("qtyLtrs",recentChangeJson.quantityLtrs);
	}
if(UtilValidate.isNotEmpty(recentChangeJson.sQuantityLtrs)){
	recentChangeJson.putAt("sQtyKgs", ProcurementNetworkServices.convertLitresToKG(new BigDecimal(recentChangeJson.sQuantityLtrs)));
}
// getting tenant configuration condition for input in Ltrs and Lactometer Reading
tenantConfigConditionList = [];
tenantConfigConditionList.add(EntityCondition.makeCondition("propertyTypeEnumId", EntityOperator.EQUALS ,"MILK_PROCUREMENT"));
tenantConfigCondition = EntityCondition.makeCondition(tenantConfigConditionList,EntityOperator.AND);
tenantConfigList = [];
tenantConfigList = delegator.findList("TenantConfiguration",tenantConfigCondition,null,null,null,false);
tenantConfigCondition = [:];
for(tenantconfig in tenantConfigList){
	tenantConfigCondition.put(tenantconfig.propertyName,tenantconfig.propertyValue);
}
context.put("tenantConfigCondition",tenantConfigCondition);
if(UtilValidate.isEmpty(recentChangeJson.qtyLtrs)){
	if(UtilValidate.isNotEmpty(recentChangeJson.quantity)){
		recentChangeJson.putAt("qtyLtrs", ProcurementNetworkServices.convertKGToLitre(recentChangeJson.quantity));
		String setScale = "Y";
		setScale = tenantConfigCondition.enableConvertKgToLtrSetScale;
		if(UtilValidate.isNotEmpty(setScale) &&("N".equalsIgnoreCase(setScale))){
			recentChangeJson.putAt("qtyLtrs", ProcurementNetworkServices.convertKGToLitreSetScale(recentChangeJson.quantity,false));
		}
	}	
}
if(UtilValidate.isNotEmpty(tenantConfigSettings)){
	for(key in tenantConfigSettings.keySet()){
		tenantConfigCondition.putAt(key, tenantConfigSettings.getAt(key)); 
	}
}
context.recentChangeJson = recentChangeJson;

conditionList=[];
if(UtilValidate.isNotEmpty(context.shedId)){
	conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, context.shedId));
}else{
	conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
}
conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, "QTYSNFFAT_CHECK"));
ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
JSONObject prodJson = new JSONObject();
	if(UtilValidate.isNotEmpty(validationRuleList)){
		validationRuleList.each{ validation->
			minFat = validation.get("minFat");
			maxFat = validation.get("maxFat");
			minSnf = validation.get("minSnf");
			maxSnf = validation.get("maxSnf");
			productId = validation.get("productId");
			productDetails=delegator.findOne("Product",[productId : productId], false);
			JSONObject prodJsonValue = new JSONObject();
			prodJsonValue.put("minFat", minFat);
			prodJsonValue.put("maxFat", maxFat+2);
			prodJsonValue.put("minSnf", minSnf+2);
			prodJsonValue.put("maxSnf", maxSnf);
			prodJsonValue.put("brandName", productDetails.brandName);
			prodJson.putAt(productId, prodJsonValue);
		}
	}
context.prodJson = prodJson.toString();
