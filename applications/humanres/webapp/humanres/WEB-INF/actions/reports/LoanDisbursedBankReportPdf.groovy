import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
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
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;


GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
context.timePeriodStart= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.timePeriodEnd= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

partyIdFrom = parameters.partyId;
dctx = dispatcher.getDispatchContext();

employmentsList = [];
/*emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
if(UtilValidate.isNotEmpty(partyIdFrom)){
	emplInputMap.put("orgPartyId", partyIdFrom);
}else{
	emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}*/

employeeLoanDisbursedMap = [:];

bankWiseEmplDetailsMap = context.get("bankWiseEmplDetailsMap")
if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap)){
	Iterator bankWiseEmplDetailsMapIter = bankWiseEmplDetailsMap.entrySet().iterator();
	while(bankWiseEmplDetailsMapIter.hasNext()){
		bankAmount = 0;
		Map.Entry bankWiseEmplDetailsMapIterEntry = bankWiseEmplDetailsMapIter.next();
		employmentsList = bankWiseEmplDetailsMapIterEntry.getValue()
		if(UtilValidate.isNotEmpty(employmentsList)){
			employmentsList.each{ employeeId->
				employeeDetailMap = [:];
				totAmount = 0;
				List conditionList=[];
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LOAN_DISBURSED"));
				conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,timePeriodStart));
				conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodEnd));
				//conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
				condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				disbursedLoanList = delegator.findList("Loan", condition , null, null, null, false );
				if(UtilValidate.isNotEmpty(disbursedLoanList)){
					disbursedLoanList.each { disbursedLoan ->
						//Debug.log("disbursedLoan==============="++"========"+disbursedLoan.get(""))
						amount = disbursedLoan.get("principalAmount");
						totAmount = totAmount + amount;
						bankAmount = bankAmount + totAmount;
						employeeDetailMap.put("totAmount", totAmount);
					}
					String partyName = PartyHelper.getPartyName(delegator, employeeId, false);
					employeeDetailMap.put("partyName", partyName);
					emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : employeeId]));
					if(UtilValidate.isNotEmpty(emplPositionAndFulfillments)){
						emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
						if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
							emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
							if(UtilValidate.isNotEmpty(emplPositionType)){
								employeePosition = emplPositionType.getString("description");
								employeeDetailMap.put("employeePosition", employeePosition);
							}
						}
					}
					
					List finAccConList=FastList.newInstance();
					finAccConList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,employeeId));
					finAccConList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
					finAccConList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
					EntityCondition finAccCond = EntityCondition.makeCondition(finAccConList, EntityOperator.AND);
					accountDetails = delegator.findList("FinAccount", finAccCond, null, null, null, false);
					if(UtilValidate.isNotEmpty(accountDetails)){
						accDetails = EntityUtil.getFirst(accountDetails);
						accNo=0;
						if(UtilValidate.isNotEmpty(accDetails))	{
							accNo= accDetails.get("finAccountCode");
							employeeDetailMap.put("accNo", accNo);
						}
					}
					if(UtilValidate.isNotEmpty(employeeDetailMap)){
						employeeLoanDisbursedMap.put(employeeId,employeeDetailMap);
					}
				}
			}
		}
		if(bankAmount == 0){
			bankWiseEmplDetailsMap.remove(bankWiseEmplDetailsMapIterEntry.getKey());
		}
	}
}



CanaraBankMap = context.get("CanaraBankMap")
if(UtilValidate.isNotEmpty(CanaraBankMap)){
	Iterator CanaraBankMapIter = CanaraBankMap.entrySet().iterator();
	while(CanaraBankMapIter.hasNext()){
		bankAmount = 0;
		Map.Entry CanaraBankMapIterEntry = CanaraBankMapIter.next();
		canaraEmploymentsList = CanaraBankMapIterEntry.getValue()
		if(UtilValidate.isNotEmpty(canaraEmploymentsList)){
			canaraEmploymentsList.each{ canaraEmployeeId->
				canaraEmployeeDetailMap = [:];
				totAmount = 0;
				List conditionList1=[];
				conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, canaraEmployeeId));
				conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LOAN_DISBURSED"));
				conditionList1.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,timePeriodStart));
				conditionList1.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodEnd));
				//conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
				condition1=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
				disbursedLoanList = delegator.findList("Loan", condition1 , null, null, null, false );
				if(UtilValidate.isNotEmpty(disbursedLoanList)){
					disbursedLoanList.each { disbursedLoan ->
						//Debug.log("disbursedLoan==============="++"========"+disbursedLoan.get(""))
						amount = disbursedLoan.get("principalAmount");
						totAmount = totAmount + amount;
						bankAmount = bankAmount + totAmount;
						canaraEmployeeDetailMap.put("totAmount", totAmount);
					}
					String canaraPartyName = PartyHelper.getPartyName(delegator, canaraEmployeeId, false);
					canaraEmployeeDetailMap.put("partyName", canaraPartyName);
					emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : canaraEmployeeId]));
					if(UtilValidate.isNotEmpty(emplPositionAndFulfillments)){
						emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
						if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
							emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
							if(UtilValidate.isNotEmpty(emplPositionType)){
								employeePosition = emplPositionType.getString("description");
								canaraEmployeeDetailMap.put("employeePosition", employeePosition);
							}
						}
					}
					
					List finAccConList1=FastList.newInstance();
					finAccConList1.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,canaraEmployeeId));
					finAccConList1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
					finAccConList1.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
					EntityCondition finAccCond1 = EntityCondition.makeCondition(finAccConList1, EntityOperator.AND);
					accountDetails = delegator.findList("FinAccount", finAccCond1, null, null, null, false);
					if(UtilValidate.isNotEmpty(accountDetails)){
						accDetails = EntityUtil.getFirst(accountDetails);
						accNo=0;
						if(UtilValidate.isNotEmpty(accDetails))	{
							accNo= accDetails.get("finAccountCode");
							canaraEmployeeDetailMap.put("accNo", accNo);
						}
					}
					if(UtilValidate.isNotEmpty(canaraEmployeeDetailMap)){
						employeeLoanDisbursedMap.put(canaraEmployeeId,canaraEmployeeDetailMap);
					}
				}
			}
		}
		if(bankAmount == 0){
			CanaraBankMap.remove(CanaraBankMapIterEntry.getKey());
		}
	}
}
context.put("employeeLoanDisbursedMap",employeeLoanDisbursedMap);

