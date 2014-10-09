import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import javolution.util.FastMap;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
		parameters["customTimePeriodId"]= parameters.getAt("shedCustomTimePeriodId");
	}

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(parameters.unitId)){
	Debug.logError("unitId Cannot Be Empty","");
	context.errorMessage = "No Unit Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);
shedId =null;
if(UtilValidate.isNotEmpty(parameters.shedId)){
	shedId = parameters.shedId;
}
if(UtilValidate.isNotEmpty(context.get("shedId"))){
	shedId=context.get("shedId");
}
context.putAt("shedId", shedId);
context.putAt("customTimePeriodId", parameters.customTimePeriodId);
String unitId = parameters.unitId ;
List unitsList = FastList.newInstance();
if(UtilValidate.isNotEmpty(unitId)){
	unitsList.add(unitId);
}

if(unitsList.size()<1){
		unitsList = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId:shedId,userLogin:userLogin]).get("unitsList");
}
centerIds=[];
if(UtilValidate.isNotEmpty(unitsList)){
	unitsList.each{ unit->
		unitCentersList = ProcurementNetworkServices.getUnitAgents(dctx,UtilMisc.toMap("unitId",unit ));
		centersList = unitCentersList.get("agentsList");
		facilityIds=EntityUtil.getFieldListFromEntityList(centersList, "facilityId", true);
		centerIds.addAll(facilityIds);
	}
}

conditionList=[];
if(UtilValidate.isNotEmpty(centerIds)){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("centerId", EntityOperator.IN, centerIds)));
}
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,parameters.statusId)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId)));
if(UtilValidate.isNotEmpty(unitsList)){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("shedId", EntityOperator.IN ,unitsList)));
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List listValues =FastList.newInstance();
 listValues = delegator.findList("ProcBillingValidation",condition,null,null,null,false); 
context.listValue=listValues;


negativeAmtList = EntityUtil.filterByAnd(listValues, [validationTypeId : "NEGATIVE_AMOUNT"]);
qtySnfFatCheckList = EntityUtil.filterByAnd(listValues, [validationTypeId : "QTYSNFFAT_CHECK"]);
outLierList = EntityUtil.filterByAnd(listValues, [validationTypeId : "QTY_OUTLIER"]);
checkCodeList = EntityUtil.filterByAnd(listValues, [validationTypeId : "CHECKCENTER_CODE"]);
qtySnfFatFinalList =[];
//This is for Snf and Fat check
if(UtilValidate.isNotEmpty(qtySnfFatCheckList)){
qtySnfFatCheckList.each{ qtySnfFatCheck ->
	Map qtySnfFatCheckMap = FastMap.newInstance();
	tempMap =[:];
	tempMap.putAll(qtySnfFatCheck)
	qtySnfFatCheckMap.putAll(tempMap);
	
	orderId =qtySnfFatCheck.getAt("orderId");
	orderItemSeqId =qtySnfFatCheck.getAt("orderItemSeqId");
	
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	qtySnfFinalList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
	if(UtilValidate.isNotEmpty(qtySnfFinalList)){
		qtySnfList =EntityUtil.getFirst(qtySnfFinalList);
		tempMap1=[:];
		tempMap1.putAll(qtySnfList);
		qtySnfFatCheckMap.putAll(tempMap1);
		qtySnfFatFinalList.add(qtySnfFatCheckMap);
	}
	
}
}
// this is for quantity out lier
outLierFinalList =[];
if(UtilValidate.isNotEmpty(outLierList)){
outLierList.each{ outLiers ->
	Map outLierCheckMap = FastMap.newInstance();
	tempOutMap =[:];
	tempOutMap.putAll(outLiers)
	outLierCheckMap.putAll(tempOutMap);
	
	orderId =outLiers.getAt("orderId");
	orderItemSeqId =outLiers.getAt("orderItemSeqId");
	
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	outLierQtyList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
	if(UtilValidate.isNotEmpty(outLierQtyList)){
		qtyOutLier =EntityUtil.getFirst(outLierQtyList);
		tempMap2=[:];
		tempMap2.putAll(qtyOutLier);
		outLierCheckMap.putAll(tempMap2);
		outLierFinalList.add(outLierCheckMap);
	}
	
}
}

// this is for Check Code
checkCodeFinalList =[];
if(UtilValidate.isNotEmpty(checkCodeList)){
checkCodeList.each{ checkCode ->
	Map CheckCodeMap = FastMap.newInstance();
	tempOutMap =[:];
	tempOutMap.putAll(checkCode)
	CheckCodeMap.putAll(tempOutMap);
	
	orderId =checkCode.getAt("orderId");
	orderItemSeqId =checkCode.getAt("orderItemSeqId");
	
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	checkCodeDetailsList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
	if(UtilValidate.isNotEmpty(checkCodeDetailsList)){
		checkCodes =EntityUtil.getFirst(checkCodeDetailsList);
		tempMap2=[:];
		tempMap2.putAll(checkCodes);
		CheckCodeMap.putAll(tempMap2);
		checkCodeFinalList.add(CheckCodeMap);
	}
	
}
}
context.putAt("checkCodeFinalList", checkCodeFinalList);
context.putAt("negativeAmtList", negativeAmtList);
context.putAt("qtySnfFinalList", qtySnfFatFinalList);
context.putAt("outLierFinalList", outLierFinalList);

