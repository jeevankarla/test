
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
import org.ofbiz.party.contact.ContactMechWorker;
import java.lang.Integer;

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
context.timePeriodStart= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.timePeriodEnd= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap))
bankWiseEmplDetailsMap=bankWiseEmplDetailsMap;
if(UtilValidate.isNotEmpty(BankAdvicePayRollMap))
CanaraBankMap=CanaraBankMap;
if(UtilValidate.isNotEmpty(BankAdvicePayRollMap))
BankAdvicePayRollMap=BankAdvicePayRollMap;

finalList=[];

if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap)){
	Iterator empIter = bankWiseEmplDetailsMap.entrySet().iterator();
	while(empIter.hasNext()){
		Map.Entry empEntry = empIter.next();
		emplIds=empEntry.getValue();
		List conditionList=[];
		if(UtilValidate.isNotEmpty(empEntry.getKey())){
			conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, empEntry.getKey()));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		bankDetails= delegator.findList("FinAccount", condition, UtilMisc.toSet("finAccountName"), null, null, false );
		if(UtilValidate.isNotEmpty(bankDetails)){
			details=EntityUtil.getFirst(bankDetails);
			bankName=details.get("finAccountName");
		}
		emplIds.each{ emplId->
			finalMap=[:];
			name="";
			acNo="";
			netAmt="";
			totAmount = 0;
//			name=BankAdvicePayRollMap.get(emplId).get("empName");
//			acNo=BankAdvicePayRollMap.get(emplId).get("acNo");
			condList=[];
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, emplId));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LOAN_DISBURSED"));
			condList.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,timePeriodStart));
			condList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodEnd));
			//conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
			cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
			disbursedLoanList = delegator.findList("Loan", cond , null, null, null, false );
			if(UtilValidate.isNotEmpty(disbursedLoanList)){
				disbursedLoanList.each { disbursedLoan ->
					amount = disbursedLoan.get("principalAmount");
					totAmount = totAmount + amount;
				}
			}
			if(totAmount != 0){
				List finAccConList1=FastList.newInstance();
				finAccConList1.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,emplId));
				finAccConList1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
				finAccConList1.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
				EntityCondition finAccCond1 = EntityCondition.makeCondition(finAccConList1, EntityOperator.AND);
				accountDetails = delegator.findList("FinAccount", finAccCond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(accountDetails)){
					accDetails = EntityUtil.getFirst(accountDetails);
					accNo=0;
					if(UtilValidate.isNotEmpty(accDetails))	{
						accNo= accDetails.get("finAccountCode");
						finalMap.put("acNo",accNo);
					}
				}
				name = PartyHelper.getPartyName(delegator, emplId, false);
				finalMap.put("bankName",bankName);
				finalMap.put("emplId",emplId);
				finalMap.put("name",name);
				finalMap.put("netAmt",totAmount);
			}
			if(UtilValidate.isNotEmpty(finalMap)){
				finalList.add(finalMap);
			}
		}
	}
}
//canara bank
if(UtilValidate.isNotEmpty(CanaraBankMap)){
	Iterator empIter = CanaraBankMap.entrySet().iterator();
	while(empIter.hasNext()){
		Map.Entry empEntry = empIter.next();
		emplIds=empEntry.getValue();
		List conditionList=[];
		if(UtilValidate.isNotEmpty(empEntry.getKey())){
			conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, empEntry.getKey()));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		bankDetails= delegator.findList("FinAccount", condition, UtilMisc.toSet("finAccountName"), null, null, false );
		if(UtilValidate.isNotEmpty(bankDetails)){
			details=EntityUtil.getFirst(bankDetails);
			bankName=details.get("finAccountName");
		}
		emplIds.each{ emplId->
			finalMap=[:];
			name="";
			acNo="";
			netAmt="";
			//name=BankAdvicePayRollMap.get(emplId).get("empName");
			//acNo=BankAdvicePayRollMap.get(emplId).get("acNo");
			totAmount = 0;
			List conditionList1=[];
			conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, emplId));
			conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LOAN_DISBURSED"));
			conditionList1.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,timePeriodStart));
			conditionList1.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,timePeriodEnd));
			//conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
			condition1=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
			disbursedLoanList = delegator.findList("Loan", condition1 , null, null, null, false );
			if(UtilValidate.isNotEmpty(disbursedLoanList)){
				disbursedLoanList.each { disbursedLoan ->
					amount = disbursedLoan.get("principalAmount");
					totAmount = totAmount + amount;
				}
			}
			if(totAmount != 0){
				List finAccConList1=FastList.newInstance();
				finAccConList1.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,emplId));
				finAccConList1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
				finAccConList1.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
				EntityCondition finAccCond1 = EntityCondition.makeCondition(finAccConList1, EntityOperator.AND);
				accountDetails = delegator.findList("FinAccount", finAccCond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(accountDetails)){
					accDetails = EntityUtil.getFirst(accountDetails);
					accNo=0;
					if(UtilValidate.isNotEmpty(accDetails))	{
						accNo= accDetails.get("finAccountCode");
						finalMap.put("acNo",accNo);
					}
				}
				name = PartyHelper.getPartyName(delegator, emplId, false);
				finalMap.put("bankName",bankName);
				finalMap.put("emplId",emplId);
				finalMap.put("name",name);
				finalMap.put("netAmt",totAmount);
			}
			if(UtilValidate.isNotEmpty(finalMap)){
				finalList.add(finalMap);
			}
		}
	}
}
context.finalList=finalList;



