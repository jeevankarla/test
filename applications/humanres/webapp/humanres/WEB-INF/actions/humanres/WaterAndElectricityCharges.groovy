import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;
if (parameters.customTimePeriodId == null) {
	return;
}
dctx = dispatcher.getDispatchContext();
context.put("type",parameters.type);
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;
context.timePeriodEnd= timePeriodEnd;

Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

quarterEmplIds=[];
if(UtilValidate.isNotEmpty(employementIds)){
	employementIds.each{ emplyId->
		
		empDetails = delegator.findOne("EmployeeDetail", [partyId : emplyId],true);
		if(UtilValidate.isNotEmpty(empDetails) && UtilValidate.isNotEmpty(empDetails.quarterType)){
			quarterEmplIds.add(emplyId);
		}
		
	}
}
if(UtilValidate.isNotEmpty(parameters.partyIdTo) && !quarterEmplIds.contains(parameters.partyIdTo)){
	Debug.logError("Given Employee is not a quarter employee..","");
	context.errorMessage = "Given Employee is not a quarter employee..";
}
if(UtilValidate.isNotEmpty(parameters.partyIdTo)){
	quarterEmplIds=UtilMisc.toList(parameters.partyIdTo);
}

JSONArray headItemsJson = new JSONArray();
if(UtilValidate.isNotEmpty(quarterEmplIds)){
	quarterEmplIds.each{ qtyEmpl->
		meterValue="";
		conditionList=[];
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setMaxRows(1);
		def orderBy = UtilMisc.toList("-thruDate");
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,qtyEmpl));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "TENANT"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		partyFixedAssetAssesment = delegator.findList("PartyFixedAssetAssignment", condition, null, orderBy, findOptions, false);
		
		assetId=null;
		if(UtilValidate.isNotEmpty(partyFixedAssetAssesment)){
			partyFixedAssetAssesment = EntityUtil.filterByDate(partyFixedAssetAssesment, timePeriodStart);
			if(UtilValidate.isNotEmpty(partyFixedAssetAssesment)){
				assessmentDetails = EntityUtil.getFirst(partyFixedAssetAssesment);
				assetId=assessmentDetails.fixedAssetId;
				meterConditionList=[];
				meterConditionList.add(EntityCondition.makeCondition("fixedAssetId", EntityOperator.EQUALS ,assetId));
				meterConditionList.add(EntityCondition.makeCondition("productMeterTypeId", EntityOperator.EQUALS , parameters.chargeType));
				meterConditionList.add(EntityCondition.makeCondition("readingDate", EntityOperator.GREATER_THAN_EQUAL_TO ,timePeriodStart));
				meterConditionList.add(EntityCondition.makeCondition("readingDate", EntityOperator.LESS_THAN_EQUAL_TO , timePeriodEnd));
				meterCondition = EntityCondition.makeCondition(meterConditionList,EntityOperator.AND);
				fixedAssetMeter = delegator.findList("FixedAssetMeter", meterCondition, null, null, null, false);
				if(UtilValidate.isNotEmpty(fixedAssetMeter)){
					fixedAssetMeter = EntityUtil.getFirst(fixedAssetMeter);
					meterValue=fixedAssetMeter.meterValue;
				}
			}
		}
		if(UtilValidate.isEmpty(assetId)){
			Debug.logError("Qyarter is Not assigned to this employee.."+qtyEmpl,"");
			context.errorMessage = "Qyarter is Not assigned to this employee.."+qtyEmpl;
		}
		JSONObject newObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, qtyEmpl, false);
		departmentDetails=delegator.findByAnd("Employment", [partyIdTo : qtyEmpl]);
		deptName="";
		if(departmentDetails){
			deptPartyId=departmentDetails[0].partyIdFrom;
			deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
		}
		newObj.put("id",qtyEmpl+"["+partyName+"]");
		newObj.put("partyId",qtyEmpl);
		newObj.put("periodId",parameters.customTimePeriodId);
		newObj.put(parameters.chargeType,meterValue);
		if(UtilValidate.isNotEmpty(deptName)){
			newObj.put("deptName",deptName);
		}		
		if(UtilValidate.isNotEmpty(assetId)){
			newObj.put("assetId",assetId);
			headItemsJson.add(newObj);		
		}
	}
}
context.headItemsJson=headItemsJson;