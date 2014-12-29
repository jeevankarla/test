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
import org.ofbiz.base.util.UtilDateTime;
import java.util.Calendar;
import java.math.BigDecimal;
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

if ((parameters.customTimePeriodId).equals(null) || (parameters.customTimePeriodId).equals(" ")) {
	return;
}

JSONArray headItemsJson = new JSONArray();
JSONObject newObj = new JSONObject();

if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
	timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	fromDayBegin = UtilDateTime.getDayStart(timePeriodStart);
	thruDayEnd = UtilDateTime.getDayEnd(timePeriodEnd);
	if (UtilValidate.isEmpty(customTimePeriod)) {
		return;
	}
	
	if (UtilValidate.isNotEmpty(parameters.partyIdTo)) {
		partyId = parameters.partyIdTo;
	}
	
	List telephoneCUGList=[];
	if (UtilValidate.isNotEmpty(parameters.partyIdTo)) {
		telephoneCUGList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	}
	telephoneCUGList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS,"TELEPHONE_CUG_RATE"));
	telephoneCUGList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,fromDayBegin));
	//telephoneCUGList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDayBegin), EntityOperator.AND,
		//EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDayEnd)));
	//telephoneCUGList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		//EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDayEnd)));
	telephoneCUGCondition=EntityCondition.makeCondition(telephoneCUGList,EntityOperator.AND);
	telephoneCUGPartyDetails = delegator.findList("RateAmount", telephoneCUGCondition , null, null, null, false);
	if(UtilValidate.isNotEmpty(telephoneCUGPartyDetails)){
		List deductionList=[];
		if (UtilValidate.isNotEmpty(parameters.partyIdTo)) {
			deductionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		}
		deductionList.add(EntityCondition.makeCondition("recoveryType", EntityOperator.EQUALS,"PAYROL_DD_TEL_CHG"));
		deductionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,parameters.customTimePeriodId));
		//deductionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDayBegin));
		/*deductionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDayBegin), EntityOperator.AND,
			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDayEnd)));
		deductionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDayEnd)));*/
		typeCondition=EntityCondition.makeCondition(deductionList,EntityOperator.AND);
		employeeRecoveryDetails = delegator.findList("EmployeeRecovery", typeCondition , null, null, null, false);
		if(UtilValidate.isNotEmpty(employeeRecoveryDetails)){
			employeeRecoveryDetails.each{ employeeRecovery->
				eligibilityAmount = employeeRecovery.get("eligibilityAmount");
				actualAmount = employeeRecovery.get("actualAmount");
				excessAmount = employeeRecovery.get("recoveryAmount");
				partyId = employeeRecovery.get("partyId");
				partyName=PartyHelper.getPartyName(delegator, partyId, false);
				newObj.put("id",partyId+"["+partyName+"]");
				newObj.put("employeeId",partyId);
				newObj.put("periodId",parameters.customTimePeriodId);
				newObj.put("actualAmount",actualAmount);
				newObj.put("eligibilityAmount",eligibilityAmount);
				newObj.put("excessAmount",excessAmount);
				headItemsJson.add(newObj);
			}
		}else{
			if(UtilValidate.isNotEmpty(telephoneCUGPartyDetails)){
				telephoneCUGPartyDetails.each{ rateAmountDetails->
					eligibilityAmount = rateAmountDetails.get("rateAmount");
					partyId = rateAmountDetails.get("partyId");
					partyName=PartyHelper.getPartyName(delegator, partyId, false);
					newObj.put("id",partyId+"["+partyName+"]");
					newObj.put("employeeId",partyId);
					newObj.put("periodId",parameters.customTimePeriodId);
					newObj.put("actualAmount","");
					newObj.put("eligibilityAmount",eligibilityAmount);
					newObj.put("excessAmount","");
					headItemsJson.add(newObj);
				}
			}
		}
	}else{
		if(UtilValidate.isNotEmpty(parameters.partyIdTo)){
			partyId = parameters.partyIdTo;
			eligibilityAmount = "";
			partyName=PartyHelper.getPartyName(delegator, partyId, false);
			newObj.put("id",partyId+"["+partyName+"]");
			newObj.put("employeeId",partyId);
			newObj.put("periodId",parameters.customTimePeriodId);
			newObj.put("actualAmount","");
			newObj.put("eligibilityAmount",eligibilityAmount);
			newObj.put("excessAmount","");
			headItemsJson.add(newObj);
		}
	}
}
context.headItemsJson=headItemsJson;

