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

dctx = dispatcher.getDispatchContext();
sortBy = UtilMisc.toList("sequenceNum");
context.dctx=dctx;
allChanges= false;
fromDate = null;
thruDate = null;
reportTypeFlag = parameters.reportTypeFlag;
benefitTypeIds = [];
dedTypeIds = [];

if (reportTypeFlag.equals("BenDedAllChcekList")) {
	allChanges = true;
}

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
if(reportTypeFlag.equals("BenDedMyChcekList")){
	try {
		if (parameters.BenDedMyfromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.BenDedMyfromDate).getTime()));
		}
		if (parameters.BenDedMythruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.BenDedMythruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}else if(reportTypeFlag.equals("BenDedAllChcekList")){
	try {
		if (parameters.BenDedAllfromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.BenDedAllfromDate).getTime()));
		}
		if (parameters.BenDedAllthruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.BenDedAllthruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}

context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

dayBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);

if(UtilValidate.isNotEmpty(context.reportFlag) && (context.reportFlag).equals("summary")){
	sortBy = UtilMisc.toList("description");
}
benefitTypeList = delegator.findList("BenefitType", null, null, sortBy, null, false);
benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
context.benefitTypeIds=benefitTypeIds;
//getting deductions

deductionTypeList = delegator.findList("DeductionType", null, null, sortBy, null, false);
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
context.dedTypeIds=dedTypeIds;

employementIds = [];
Map emplInputMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", dayBegin);
emplInputMap.put("thruDate", dayEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

conditionList = [];
if(!allChanges){
	conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
}
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employementIds));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
conditionList.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	/*EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)*/
   ], EntityOperator.OR));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
benefitsItemsList = delegator.findList("PartyBenefit", condition, null, ["lastUpdatedStamp"], null, false);
benefitList = [];
if(UtilValidate.isNotEmpty(benefitTypeIds)){
	benefitTypeIds.each{ benefit->
		if(UtilValidate.isNotEmpty(benefitsItemsList)){
			benefitsItemsList.each{ benefitItem ->
				if(benefit.equals(benefitItem.get("benefitTypeId"))){
					if(benefitList.contains(benefit)){
						
					}else{
						benefitList.add(benefit);
					}
				}
			}
		}
	}
}

conditionList1 = [];
if(!allChanges){
	conditionList1.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
}
conditionList1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employementIds));
conditionList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
conditionList1.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	/*EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)*/
   ], EntityOperator.OR));
condition1 = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
deductionItemsList = delegator.findList("PartyDeduction", condition1, null, ["lastUpdatedStamp"], null, false);
deductionList = [];
if(UtilValidate.isNotEmpty(dedTypeIds)){
	dedTypeIds.each{ deduction->
		if(UtilValidate.isNotEmpty(deductionItemsList)){
			deductionItemsList.each{ deductionItem ->
				if(deduction.equals(deductionItem.get("deductionTypeId"))){
					if(deductionList.contains(deduction)){
						
					}else{
						deductionList.add(deduction);
					}
				}
			}
		}
	}
}
context.put("benefitList",benefitList);
context.put("deductionList",deductionList);
context.put("benefitsItemsList",benefitsItemsList);
context.put("deductionItemsList",deductionItemsList);

