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
import net.sf.json.JSONObject;

dctx = dispatcher.getDispatchContext();
// get the shed id based on userLogin 
// for now allways first shed 
shedList = ProcurementNetworkServices.getSheds(delegator);
shedId = "";
if(shedList){	
	shedId = EntityUtil.getFirst(shedList).get("facilityId");
}
if(UtilValidate.isNotEmpty(context.getAt("shedId"))){
	shedId = context.get("shedId");
	} 

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
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
tenantConfigList = delegator.findList("TenantConfiguration",tenantConfigCondition,["propertyName","propertyValue"]as Set,null,null,false);
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

Debug.log("tenantConfigCondition====>"+tenantConfigCondition);
context.recentChangeJson = recentChangeJson;