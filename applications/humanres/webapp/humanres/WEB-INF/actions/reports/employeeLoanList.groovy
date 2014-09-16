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
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;


dctx = dispatcher.getDispatchContext();

List employeeLoanList = [];

loanId = null;
partyId = null;
loanTypeId = null;
statusId = null;
description = null;
principalAmount = BigDecimal.ZERO;
interestAmount = BigDecimal.ZERO;
numInterestInst = BigDecimal.ZERO;
numPrincipalInst = BigDecimal.ZERO;
createdDate = null;
createdByUserLogin = null;
statusDescription = null;
extLoanRefNum = null;
disbDate = null;
setlDate = null;

partyName = null;

List conditionList=[];

if(UtilValidate.isNotEmpty(parameters.loanId)){
	conditionList.add(EntityCondition.makeCondition("loanId", EntityOperator.EQUALS, parameters.loanId));
}
if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
}
if(UtilValidate.isNotEmpty(parameters.loanTypeId)){
	conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, parameters.loanTypeId));
}
if(UtilValidate.isNotEmpty(parameters.statusId)){
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameters.statusId));
}
if(UtilValidate.isNotEmpty(parameters.description)){
	conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, parameters.description));
}
if(UtilValidate.isNotEmpty(parameters.extLoanRefNum)){
	conditionList.add(EntityCondition.makeCondition("extLoanRefNum", EntityOperator.EQUALS, parameters.extLoanRefNum));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
emplyLoanList = delegator.findList("Loan", condition , null, null, null, false );
if(UtilValidate.isNotEmpty(emplyLoanList)){
	emplyLoanList.each { employeeLoan ->
		if(UtilValidate.isNotEmpty(employeeLoan)){
			if(UtilValidate.isNotEmpty(employeeLoan.partyId)){
				partyName = PartyHelper.getPartyName(delegator, employeeLoan.partyId, false);
			}
			loanId = employeeLoan.loanId;
			partyId = employeeLoan.partyId;
			loanTypeId = employeeLoan.loanTypeId;
			statusId = employeeLoan.statusId;
			description = employeeLoan.description;
			extLoanRefNum = employeeLoan.extLoanRefNum;
			principalAmount = employeeLoan.principalAmount;
			interestAmount = employeeLoan.interestAmount;
			numInterestInst = employeeLoan.numInterestInst;
			numPrincipalInst = employeeLoan.numPrincipalInst;
			disbDate = employeeLoan.disbDate;
			createdDate = employeeLoan.createdDate;
			createdByUserLogin = employeeLoan.createdByUserLogin;
			setlDate = employeeLoan.setlDate;
			
			employeeLoanMap = [:];
			employeeLoanMap["loanId"] = loanId;
			employeeLoanMap["partyId"] = partyId;
			employeeLoanMap["partyName"] = partyName;
			employeeLoanMap["loanTypeId"] = loanTypeId;
			employeeLoanMap["statusId"] = statusId;
			employeeLoanMap["extLoanRefNum"] = extLoanRefNum;
			employeeLoanMap["description"] = description;
			employeeLoanMap["principalAmount"] = principalAmount;
			employeeLoanMap["interestAmount"] = interestAmount;
			employeeLoanMap["numInterestInst"] = numInterestInst;
			employeeLoanMap["numPrincipalInst"] = numPrincipalInst;
			employeeLoanMap["createdByUserLogin"] = createdByUserLogin;
			employeeLoanMap["disbDate"] = UtilDateTime.toDateString(disbDate,"dd-MM-yyyy");
			employeeLoanMap["createdDate"] = UtilDateTime.toDateString(createdDate,"dd-MM-yyyy");
			employeeLoanMap["setlDate"] = setlDate;
			tempMap = [:];
			tempMap.putAll(employeeLoanMap);
			if(UtilValidate.isNotEmpty(tempMap)){
				employeeLoanList.addAll(tempMap);
			}
		}
	}
}
context.putAt("employeeLoanList", employeeLoanList);

