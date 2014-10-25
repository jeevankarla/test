
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
import org.ofbiz.party.contact.ContactMechWorker;
import java.lang.Integer;

if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap))
bankWiseEmplDetailsMap=bankWiseEmplDetailsMap;
if(UtilValidate.isNotEmpty(BankAdvicePayRollMap))
BankAdvicePayRollMap=BankAdvicePayRollMap;

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
		   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
dateStr=UtilDateTime.toDateString(fromDate, "MM-dd-yyyy");

finalList=[];
if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap)){
	Iterator empIter = bankWiseEmplDetailsMap.entrySet().iterator();
	totNetAmount = 0;
	while(empIter.hasNext()){
		Map.Entry empEntry = empIter.next();
		finAccId = empEntry.getKey();
		emplIds=empEntry.getValue();
		
		List conList=FastList.newInstance();
		conList=UtilMisc.toList(
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"),
			EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccId));
			  EntityCondition cond = EntityCondition.makeCondition(conList, EntityOperator.AND);
		finAccountRoleList=delegator.findList("FinAccountRole",cond, null,null, null, false);
		
		if(UtilValidate.isNotEmpty(finAccountRoleList)){
			finAccdetails=EntityUtil.getFirst(finAccountRoleList);
			finAccountId=finAccdetails.get("finAccountId");
			finAccountDetail = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
			finAcccountCode= finAccountDetail.get("finAccountCode");
		}
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
			txnType="1";
			name="";
			acNo="";
			branchCode="1286";
			txnCode="1408";
			credit="C";
			txnCcy="104";
			ratCon="1.00";
			refNo="";
			refDocNo="";
			txnDes="BYSALARIES";
			benefIc="";
			benefName="";
			benefAdd1="";
			benefAdd2="";
			benefAdd3="";
			benefCity="";
			benefState="";
			benefCntry="";
			benefZip="";
			option="30";
			issueCode="0";
			payable="0";
			flgFdt="N";
			mis="";
			netAmt="";
			
			if(BankAdvicePayRollMap && BankAdvicePayRollMap.get(emplId)){
				name=BankAdvicePayRollMap.get(emplId).get("empName");
				acNo=BankAdvicePayRollMap.get(emplId).get("acNo");
				netAmt=BankAdvicePayRollMap.get(emplId).get("netAmt");
				finalMap.put("bankName",bankName);
				finalMap.put("emplId",emplId);
				finalMap.put("name",name);
				finalMap.put("acNo",acNo);
				finalMap.put("netAmt",netAmt);
				finalMap.put("netAmt2",netAmt);
				finalMap.put("dateStr",dateStr);
				finalMap.put("dateStr2",dateStr);
				finalMap.put("ratCon","ratCon");
				finalMap.put("txnType",txnType);
				finalMap.put("branchCode",branchCode);
				finalMap.put("txnCode",txnCode);
				finalMap.put("credit",credit);
				finalMap.put("txnCode",txnCode);
				finalMap.put("txnCode",txnCode);
				finalMap.put("txnCcy",txnCcy);
				finalMap.put("ratCon",ratCon);
				finalMap.put("refNo",refNo);
				finalMap.put("refDocNo",refDocNo);
				finalMap.put("txnDes",txnDes);
				finalMap.put("benefIc",benefIc);
				finalMap.put("benefName",benefName);
				finalMap.put("benefAdd1",benefAdd1);
				finalMap.put("benefAdd2",benefAdd2);
				finalMap.put("benefAdd3",benefAdd3);
				finalMap.put("benefCity",benefCity);
				finalMap.put("benefState",benefState);
				finalMap.put("benefCntry",benefCntry);
				finalMap.put("benefZip",benefZip);
				finalMap.put("option",option);
				finalMap.put("issueCode",issueCode);
				finalMap.put("payable",payable);
				finalMap.put("flgFdt",flgFdt);
				finalMap.put("mis",mis);
				
				totNetAmount = totNetAmount+netAmt;
			}
			if(UtilValidate.isNotEmpty(finalMap)){
				finalList.add(finalMap);
			}
		}
		
	}
	totTxnType="3";
	totBranchCode="1286";
	totTxnCode="1015";
	totDebit="D";
	totTxnCcy="104";
	totRatCon="1.00";
	totRefNo="";
	totRefDocNo="";
	totTxnDes="BYSALARIES";
	totBenefIc="";
	totBenefName="";
	totBenefAdd1="";
	totBenefAdd2="";
	totBenefAdd3="";
	totBenefCity="";
	totBenefState="";
	totBenefCntry="";
	totBenefZip="";
	totOption="30";
	totIssueCode="0";
	totPayable="0";
	totFlgFdt="N";
	totMis="";
	
	totalMap =[:];
	totalMap.put("netAmt",totNetAmount);
	totalMap.put("txnType",totTxnType);
	totalMap.put("branchCode",totBranchCode);
	totalMap.put("txnCode",totTxnCode);
	totalMap.put("acNo",finAcccountCode);
	totalMap.put("credit",totDebit);
	totalMap.put("dateStr",dateStr);
	totalMap.put("dateStr2",dateStr);
	totalMap.put("txnCcy",totTxnCcy);
	totalMap.put("netAmt2",totNetAmount);
	totalMap.put("ratCon",totRatCon);
	totalMap.put("refNo",totRefNo);
	totalMap.put("refDocNo",totRefDocNo);
	totalMap.put("txnDes",totTxnDes);
	totalMap.put("benefIc",totBenefIc);
	totalMap.put("benefName",totBenefName);
	totalMap.put("benefAdd1",totBenefAdd1);
	totalMap.put("benefAdd2",totBenefAdd2);
	totalMap.put("benefAdd3",totBenefAdd3);
	totalMap.put("benefCity",totBenefCity);
	totalMap.put("benefState",totBenefState);
	totalMap.put("benefCntry",totBenefCntry);
	totalMap.put("benefZip",totBenefZip);
	totalMap.put("option",totOption);
	totalMap.put("issueCode",totIssueCode);
	totalMap.put("payable",totPayable);
	totalMap.put("flgFdt",totFlgFdt);
	totalMap.put("mis",totMis);
	
	finalList.add(totalMap);
}
context.finalList=finalList;
