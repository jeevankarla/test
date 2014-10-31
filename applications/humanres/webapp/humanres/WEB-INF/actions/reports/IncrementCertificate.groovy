import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.string.*;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();

GenericValue customTimePeriodId = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriodId.getDate("fromDate"));
currentDayTimeStart = UtilDateTime.getDayStart(timePeriodStart);
//Debug.l
startdate = UtilDateTime.toDateString(currentDayTimeStart);
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriodId.getDate("thruDate"));
currentDayTimeEnd = UtilDateTime.getDayEnd(timePeriodEnd);
endDate = UtilDateTime.toDateString(currentDayTimeEnd);
context.put("fromDate",timePeriodStart);
context.put("thruDate",timePeriodEnd);


def sdf = new SimpleDateFormat("MMMM dd, yyyy");

emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyId"));
employmentsIds = EntityUtil.getFieldListFromEntityList(employments, "partyId", true);
employmentList=[];
conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
payrollDetailsList = delegator.findList("PeriodBillingAndCustomTimePeriod", condition, null, null, null, false);

if(UtilValidate.isNotEmpty(payrollDetailsList)){
	if(UtilValidate.isNotEmpty(employmentsIds)){
		employmentsIds.each{ employeeId ->
			payHistoryDetails = delegator.findList("PayHistory",EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId)  , null, null, null, false );
			payHistoryDetailsList = UtilMisc.sortMaps(payHistoryDetails, UtilMisc.toList("partyIdTo"));
			
			if(UtilValidate.isNotEmpty(payHistoryDetailsList)){
				payHistoryDetailsList.each{ pay ->
					partyId=pay.get("partyIdTo");
					employmentList.add(partyId);
					
				}
			}
		}
	}
}

EmployeeFinalMap = [:];
if(UtilValidate.isNotEmpty(employmentList)){
	employmentList.each{ employee ->
		detailsMap=[:];
		iteration=1;
		String partyName = PartyHelper.getPartyName(delegator, employee, false);
		detailsMap.put("partyName",partyName);
		EmplPositionDetails = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, employee), null, null, null, false);
		if(UtilValidate.isNotEmpty(EmplPositionDetails)){
			EmplPositionDetails.each{ EmplPositionId ->
				EmployeePositionId=EmplPositionId.get("emplPositionTypeId");
				EmplPositionNames = delegator.findList("EmplPositionType", EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, EmployeePositionId), null, null, null, false);
				if(UtilValidate.isNotEmpty(EmplPositionNames)){
					EmplPositionNames.each{ EmplPosition ->
						EmployeePosition=EmplPosition.get("description");
						detailsMap.put("Designation",EmployeePosition);
					}
				}
			}
		}
		if(UtilValidate.isNotEmpty(payrollDetailsList)){
			payrollDetailsList.each { payrollDetails->
				billingId=payrollDetails.get("periodBillingId");
				TypeConditionList=[];
				TypeConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,billingId));
				TypeConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,employee));
				TypeConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS ,"PAYROL_BEN_PERS_PAY"));
				typecondition = EntityCondition.makeCondition(TypeConditionList,EntityOperator.AND);
				def orderBy = UtilMisc.toList("amount","partyIdFrom");
				PersonalPayList = delegator.findList("PayrollHeaderAndHeaderItem", typecondition, null, orderBy, null, false);
				if(UtilValidate.isNotEmpty(PersonalPayList)){
					PersonalPayList.each { PersnlPay->
						PersonalPay=PersnlPay.get("amount");
						detailsMap.put("PersonalPay",PersonalPay);
					}
				}
			}
		}
		
		payConditionList=[];
		payConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employee));
		payConditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, (currentDayTimeStart)),
			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, (currentDayTimeEnd))]));
		payCondition = EntityCondition.makeCondition(payConditionList,EntityOperator.AND);
		PayGradeHistory = delegator.findList("PayGradePayHistory", payCondition, null, ["-fromDate"], null, false);
		
		//PayGradeHistory = delegator.findList("PayGradePayHistory", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employee), null, ["-fromDate"], null, false);
		if(UtilValidate.isNotEmpty(PayGradeHistory)){
			PayGradeHistory.each{ PayGradeId ->
				if(iteration.equals(1)){
					dateOfPresentIncre=PayGradeId.get("fromDate");
					presentPayScale=PayGradeId.get("payScale");
					presentPay=PayGradeId.get("amount");
					iteration=iteration+1;
					dateOfPresentIncre = UtilDateTime.getDayStart(dateOfPresentIncre);
					dateOfPresentIncre = UtilDateTime.toDateString(dateOfPresentIncre);
					def sdf1 = new SimpleDateFormat("MM/dd/yyyy");
					try {
						if (dateOfPresentIncre) {
							dateTimestamp = new java.sql.Timestamp(sdf1.parse(dateOfPresentIncre).getTime());
							presentDateStr = UtilDateTime.toDateString(dateTimestamp,"dd-MMM-yy");
						}
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + e, "");
						context.errorMessage = "Cannot parse date string: " + e;
						return;
					}
					detailsMap.put("dateOfPresentIncre",presentDateStr);
					if(UtilValidate.isNotEmpty(presentPayScale)){
						detailsMap.put("presentPayScale",(presentPayScale.substring(0,(presentPayScale.length()/2).intValue())+" "+presentPayScale.substring(((presentPayScale.length()/2).intValue()),presentPayScale.length())));
					}
					detailsMap.put("presentPay",presentPay);
				}else
				if(iteration.equals(2)){
					dateOfLastIncre=PayGradeId.get("fromDate");
					LastPayScale=PayGradeId.get("payScale");
					LastPay=PayGradeId.get("amount");
					iteration=iteration+1;
					
					dateOfLastIncre = UtilDateTime.getDayStart(dateOfLastIncre);
					dateOfLastIncre = UtilDateTime.toDateString(dateOfLastIncre);
					def sdf2 = new SimpleDateFormat("MM/dd/yyyy");
					try {
						if (dateOfLastIncre) {
							dateTimestamp = new java.sql.Timestamp(sdf2.parse(dateOfLastIncre).getTime());
							LastDateStr = UtilDateTime.toDateString(dateTimestamp,"dd-MMM-yy");
						}
					} catch (ParseException e) {
						Debug.logError(e, "Cannot parse date string: " + e, "");
						context.errorMessage = "Cannot parse date string: " + e;
						return;
					}
					
					detailsMap.put("dateOfLastIncre",LastDateStr);
					detailsMap.put("LastPay",LastPay);
				}
			}
		}
		if(UtilValidate.isNotEmpty(detailsMap)){
			EmployeeFinalMap.put(employee,detailsMap);
		}
	}
}

context.put("EmployeeFinalMap",EmployeeFinalMap);


