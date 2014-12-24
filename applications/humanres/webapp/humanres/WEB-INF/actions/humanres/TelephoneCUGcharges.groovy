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

if (parameters.customTimePeriodId == null) {
	return;
}

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

JSONArray headItemsJson = new JSONArray();

JSONObject newObj = new JSONObject();

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
		deductionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	}
	deductionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS,"PAYROL_DD_TEL_CHG"));
	//deductionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDayBegin));
	deductionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDayBegin), EntityOperator.AND,
		EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDayEnd)));
	deductionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDayEnd)));
	typeCondition=EntityCondition.makeCondition(deductionList,EntityOperator.AND);
	partyDeductionDetails = delegator.findList("PartyDeduction", typeCondition , null, null, null, false);
	if(UtilValidate.isNotEmpty(partyDeductionDetails)){
		partyDeductionDetails.each{ deductionEmpl->
			if(UtilValidate.isNotEmpty(telephoneCUGPartyDetails)){
				telephoneCUGPartyDetails.each{ partyDetails->
					emplId = partyDetails.get("partyId");
					if(emplId.equals(deductionEmpl.get("partyIdTo"))){
						eligibilityAmount = partyDetails.get("rateAmount");
						partyId = deductionEmpl.get("partyIdTo");
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
			}else{
				eligibilityAmount = "";
				partyId = deductionEmpl.get("partyIdTo");
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
	}else{
		if(UtilValidate.isNotEmpty(telephoneCUGPartyDetails)){
			partyDetails = EntityUtil.getFirst(telephoneCUGPartyDetails);
			eligibilityAmount = partyDetails.get("rateAmount");
		}else{
			eligibilityAmount = "";
		}
		partyName=PartyHelper.getPartyName(delegator, partyId, false);
		newObj.put("id",partyId+"["+partyName+"]");
		newObj.put("employeeId",partyId);
		newObj.put("periodId",parameters.customTimePeriodId);
		newObj.put("actualAmount","");
		newObj.put("eligibilityAmount",eligibilityAmount);
		newObj.put("excessAmount","");
		headItemsJson.add(newObj);
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
context.headItemsJson=headItemsJson;

