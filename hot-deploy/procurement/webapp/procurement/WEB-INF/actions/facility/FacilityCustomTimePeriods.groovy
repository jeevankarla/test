import org.apache.derby.impl.sql.compile.OrderByList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.wsdl.Import;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
List shedList = FastList.newInstance(); 
Map shedMap= FastMap.newInstance();
isTimePeriodClosed = parameters.isTimePeriodClosed;
timePeriodsFlag=parameters.timePeriods;

resultReturn = ServiceUtil.returnSuccess();
if(UtilValidate.isNotEmpty(context.shedId) || UtilValidate.isNotEmpty(parameters.shedId)){
	shedDetails = null;
	if(context.shedId){
		shedDetails = delegator.findOne("Facility",[facilityId : context.shedId], false);
	}
	if(parameters.shedId){
		context.shedId = parameters.shedId;
		shedDetails = delegator.findOne("Facility",[facilityId : parameters.shedId], false);
	}
		shedMap.put("facilityId",shedDetails.facilityId);
		shedMap.put("facilityName",shedDetails.facilityName);
		shedMap.put("facilityCode",shedDetails.facilityCode);
		shedMap.put("facilityTypeId","SHED");
		shedList.add(shedMap);
	
}else{
		shedList.addAll(ProcurementNetworkServices.getSheds(delegator));
	}
context.putAt("relatedShedList", shedList);
JSONObject shedUnitTimePeriodsJson = new JSONObject();
JSONObject shedTimePeriodsJson = new JSONObject();
JSONObject timePeriodsJson = new JSONObject();
orderByFieldList = [];
orderByFieldList.add("-thruDate");
shedTimeperiodsMap = [:];
for(shed in shedList){
	String shedId = shed.facilityId;
	String shedCode = shed.facilityCode;
	List conditionList = FastList.newInstance();
	List<GenericValue> shedTimePeriods = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"PROC_BILL_MONTH"));
	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS ,shed.facilityId));
	if(isTimePeriodClosed && isTimePeriodClosed=="Y"){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fctpIsClosed", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("fctpIsClosed", EntityOperator.EQUALS, "N")));
	}else{
		conditionList.add(EntityCondition.makeCondition("isClosed",EntityOperator.EQUALS,"N"));
	}
	
	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	shedTimePeriods = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",condition,null,orderByFieldList,null,false);
	JSONArray  shedTimePeriodslistJSON= new JSONArray();
	int timePeriodSize=4;
	if(shedTimePeriods.size()<timePeriodSize){
		timePeriodSize = shedTimePeriods.size();
		}
	if(UtilValidate.isNotEmpty(timePeriodsFlag) && "Y".equals(timePeriodsFlag)){
		timePeriodSize=shedTimePeriods.size();
	}
	
	if(UtilValidate.isNotEmpty(shedTimePeriods)){		
		for(int i=0;i<timePeriodSize;i++){
			shedTimePeriod = shedTimePeriods.get(i);
			JSONObject shedTimePeriodJsonValue = new JSONObject();
			String fromDateStr = UtilDateTime.toDateString(shedTimePeriod.fromDate, "MMMdd");
			String thruDateStr = UtilDateTime.toDateString(shedTimePeriod.thruDate, "MMMdd yyyy");
			shedTimePeriodJsonValue.putAt("fromDate", fromDateStr);
			shedTimePeriodJsonValue.putAt("customTimePeriodId", shedTimePeriod.customTimePeriodId);
			shedTimePeriodJsonValue.putAt("thruDate", thruDateStr);
			shedTimePeriodslistJSON.add(shedTimePeriodJsonValue);
			}
	}
	shedUnitTimePeriodsJson.putAt(shedId+"_"+"timePeriods", shedTimePeriodslistJSON);
	shedUnitTimePeriodsJson.putAt(shedCode+"_"+"timePeriods", shedTimePeriodslistJSON);
	
	List shedUnitsList =FastList.newInstance();
	Map shedUnitMap = FastMap.newInstance();
	if(UtilValidate.isNotEmpty(context.unitId)){
		shedUnitMap.put("facilityId",context.unitId);
		shedUnitMap.put("facilityName",context.unitName);
		shedUnitMap.put("facilityCode",context.unitCode);
		shedUnitMap.put("facilityTypeId","UNIT");
		shedUnitsList.add(shedUnitMap);
	}else{
		shedUnitsList.addAll(ProcurementNetworkServices.getShedUnitsByShed(dctx , UtilMisc.toMap("shedId", shedId)).get("unitsDetailList"));
	}
	JSONObject unitTimePeriodsJson = new JSONObject();
	for( GenericValue shedUnit : shedUnitsList){	
		JSONObject unitJsonValue = new JSONObject();
		JSONArray  unitTimePeriodslistJSON= new JSONArray();
		List<GenericValue> unitTimePeriods = FastList.newInstance();
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"PROC_BILL_MONTH"));
		if(isTimePeriodClosed && isTimePeriodClosed=="Y"){
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fctpIsClosed", EntityOperator.EQUALS, null), EntityOperator.OR,
				EntityCondition.makeCondition("fctpIsClosed", EntityOperator.EQUALS, "N")));
		}else{
			conditionList.add(EntityCondition.makeCondition("isClosed",EntityOperator.EQUALS,"N"));
		}
		conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,shedUnit.facilityId));
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		unitTimePeriods = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",condition,null,orderByFieldList,null,false);
		timePeriodSize = 4 ;
		if(unitTimePeriods.size()<timePeriodSize){
			timePeriodSize = unitTimePeriods.size();
			}
		if(UtilValidate.isNotEmpty(timePeriodsFlag) && "Y".equals(timePeriodsFlag)){
			timePeriodSize=unitTimePeriods.size();
		}		
		if(UtilValidate.isNotEmpty(unitTimePeriods)){		
			for(int i=0;i<timePeriodSize;i++){
				unitTimePeriod = unitTimePeriods.get(i);
				JSONObject unitTimePeriodJsonValue = new JSONObject();
				String fromDateStr = UtilDateTime.toDateString(unitTimePeriod.fromDate, "MMMdd");
				String thruDateStr = UtilDateTime.toDateString(unitTimePeriod.thruDate, "MMMdd yyyy");
				unitTimePeriodJsonValue.putAt("fromDate", fromDateStr);
				unitTimePeriodJsonValue.putAt("customTimePeriodId", unitTimePeriod.customTimePeriodId);
				unitTimePeriodJsonValue.putAt("thruDate", thruDateStr);
				unitTimePeriodslistJSON.add(unitTimePeriodJsonValue);
				}
		}
		unitTimePeriodsJson.putAt(shedUnit.facilityId, unitTimePeriodslistJSON);
		unitTimePeriodsJson.putAt(shedUnit.facilityCode, unitTimePeriodslistJSON);
	}
	shedUnitTimePeriodsJson.put(shed.facilityId,unitTimePeriodsJson);
	shedUnitTimePeriodsJson.put(shed.facilityCode,unitTimePeriodsJson);
}
context.put("shedUnitTimePeriodsJson",shedUnitTimePeriodsJson);
// for run validation 
List timePeriodsList = FastList.newInstance();
context.putAt("timePeriodsList", timePeriodsList);
context.putAt("timePeriodList", timePeriodsList);
resultReturn.put("shedUnitTimePeriodsJson",shedUnitTimePeriodsJson);

//get UnitRoutes here
shedUnits = ProcurementNetworkServices.getShedUnits(dctx ,context);
shedUnitsMap = (Map)shedUnits.get("shedUnits");
List  unitsList = FastList.newInstance();
context.shedUnitsMap = shedUnitsMap;
if(UtilValidate.isNotEmpty(parameters.shedId)){
	context.putAt("shedId", parameters.shedId)
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,context);
	unitsList = (List)shedUnitsMap[parameters.shedId];
}
if(UtilValidate.isNotEmpty(context.shedId)){
	context.putAt("shedId", context.shedId)
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,context);
	unitsList = (List)shedUnitsMap[context.shedId];
}
JSONObject shedUnitsJson = new JSONObject();
Iterator mapIter = shedUnitsMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	shedId =entry.getKey();
	shedUnitMap =[:];
	shedUnitMap = shedUnitsMap[shedId];
	shedUnitsJson.put(shedId,shedUnitMap);
}

//shedUnitsJson.put("id", shedUnitsMap);
context.shedUnitsJson = shedUnitsJson;
routesList =[];
context.put("routesList", routesList);
context.put("unitsList", unitsList);
milkProductsList = [];
if(UtilValidate.isNotEmpty(context.productsList)){
	milkProductsList = context.productsList;
	tempProductMap = [:];
	tempProductMap.productName = "All";
	tempProductMap.brandName = "ALL";
	milkProductsList.add(0,tempProductMap);
}
context.putAt("milkProductsList", milkProductsList);


resultReturn.put("routesList", routesList);
resultReturn.put("unitsList", unitsList);
resultReturn.put("shedUnitsJson", shedUnitsJson);

return resultReturn;







