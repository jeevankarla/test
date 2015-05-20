import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.humanres.HumanresHelperServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;


fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
reportTypeFlag = parameters.reportTypeFlag;

SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
Timestamp fromDateTs = null;
if(fromDateStr){
	try {
		fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
	} catch (ParseException e) {
	}
}
Timestamp thruDateTs = null;
if(thruDateStr){
	try {
		thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
	} catch (ParseException e) {
	}
}
fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);
context.fromDate = fromDate;
context.thruDate = thruDate;


periodBillingId = "";
customTimePeriodId = "";
List monthConditionList=[];
monthConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
monthConditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
monthConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("GENERATED","APPROVED")));
monthConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
monthConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDate)));
monthCondition=EntityCondition.makeCondition(monthConditionList,EntityOperator.AND);
monthPeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", monthCondition , null, null, null, false );
if(UtilValidate.isNotEmpty(monthPeriodList)){
	monthPeriodList = EntityUtil.getFirst(monthPeriodList);
	customTimePeriodId = monthPeriodList.get("customTimePeriodId");
	periodBillingId = monthPeriodList.get("periodBillingId");
}

loanConList=[];
loanConList.add(EntityCondition.makeCondition("isExternal", EntityOperator.EQUALS,"Y"));
loanCondition=EntityCondition.makeCondition(loanConList,EntityOperator.AND);
loanTypeList=delegator.findList("LoanType",loanCondition,null,null,null,false);

loanTypeEmplMap = [:];

if(UtilValidate.isNotEmpty(periodBillingId)){
	if(UtilValidate.isNotEmpty(loanTypeList)){
		loanTypeList.each{ loanType->
			loanTypeDesc = "";
			loanTypeId = loanType.get("loanTypeId");
			loanTypeDesc = loanType.description;
			employeeLoanMap = [:];
			
			List conditionList=[];
			conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, loanTypeId));
			conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			activeLoanList = delegator.findList("Loan", condition , null, null, null, false );
			if(UtilValidate.isNotEmpty(activeLoanList)){
				activeLoanList.each{ activeLoan->
					principalInstNum = "";
					principalAmount = "";
					employeeLoanDetails = [:];
					loanId = "";
					
					employeeId = activeLoan.get("partyId");
					loanId = activeLoan.get("loanId");
					List headerConditionList=[];
					headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
					headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
					headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
					headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
					if(UtilValidate.isEmpty(headerIdsList)){
						principalAmount = activeLoan.get("principalAmount");
						List loanRecConditionList=[];
						loanRecConditionList.add(EntityCondition.makeCondition("loanId", EntityOperator.EQUALS, loanId));
						loanRecCondition = EntityCondition.makeCondition(loanRecConditionList,EntityOperator.AND);
						loanRecList = delegator.findList("LoanRecovery", loanRecCondition, null, null, null, false);
						if(UtilValidate.isNotEmpty(loanRecList)){
							loanRecList = EntityUtil.getFirst(loanRecList);
							principalInstNum = loanRecList.get("principalInstNum");
						}
						employeeLoanDetails.put("recoveryAmount", principalAmount);
						employeeLoanDetails.put("principalInstNum", principalInstNum);
						if(UtilValidate.isNotEmpty(employeeLoanDetails)){
							employeeLoanMap.put(employeeId, employeeLoanDetails);
						}
					}
				}
			}
			if(UtilValidate.isNotEmpty(employeeLoanMap)){
				loanTypeEmplMap.put(loanTypeDesc,employeeLoanMap);
			}
		}
	}
}

context.put("loanTypeEmplMap",loanTypeEmplMap);

