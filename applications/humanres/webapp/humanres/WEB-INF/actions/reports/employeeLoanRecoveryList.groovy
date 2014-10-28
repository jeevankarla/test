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

List employeeLoanRecoveryList = [];


loanRecoveryDate = null;
loanRecoveryDateStr = parameters.loanRecoveryDate;
if (UtilValidate.isNotEmpty(loanRecoveryDateStr)) {
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		loanRecoveryDate = new java.sql.Timestamp(sdf.parse(loanRecoveryDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + loanRecoveryDateStr, "");
	}
}

loanRecoveryDateStart = null;
loanRecoveryDateEnd = null;
if(UtilValidate.isNotEmpty(loanRecoveryDate)){
	loanRecoveryDateTime = UtilDateTime.toTimestamp(loanRecoveryDate);
	loanRecoveryDateStart = UtilDateTime.getDayStart(loanRecoveryDateTime);
	loanRecoveryDateEnd = UtilDateTime.getDayEnd(loanRecoveryDateTime);
}


partyName = null;

List conditionList=[];

if(UtilValidate.isNotEmpty(parameters.loanId)){
	conditionList.add(EntityCondition.makeCondition("loanId", EntityOperator.EQUALS, parameters.loanId));
}
if(UtilValidate.isNotEmpty(loanRecoveryDate)){
	conditionList.add(EntityCondition.makeCondition("recoveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,loanRecoveryDateStart));
	conditionList.add(EntityCondition.makeCondition("recoveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,loanRecoveryDateEnd));
}

condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
emplyLoanRecoveryList = delegator.findList("LoanRecovery", condition , null, null, null, false );
if(UtilValidate.isNotEmpty(emplyLoanRecoveryList)){
	emplyLoanRecoveryList.each { emplyLoanRecovery ->
		if(UtilValidate.isNotEmpty(emplyLoanRecovery)){
			
			loanId = null;
			employeeId = null;
			loanTypeId = null;
			principalAmount = BigDecimal.ZERO;
			interestAmount = BigDecimal.ZERO;
			numInterestInst = BigDecimal.ZERO;
			numPrincipalInst = BigDecimal.ZERO;
			amount = BigDecimal.ZERO;
			customTimePeriodId = null;
			
			loanId = emplyLoanRecovery.loanId;
			loanRecoveryDate = emplyLoanRecovery.recoveryDate;
			finAccountTransId = emplyLoanRecovery.finAccountTransId;
			customTimePeriodId = emplyLoanRecovery.customTimePeriodId;
			deducteePartyId = emplyLoanRecovery.deducteePartyId;
			
			if(UtilValidate.isNotEmpty(finAccountTransId)){
				finAccountTransDetails = delegator.findOne("FinAccountTrans", [finAccountTransId : finAccountTransId], false);
				if(UtilValidate.isNotEmpty(finAccountTransDetails)){
					amount = finAccountTransDetails.amount;
				}
			}
			
			if(UtilValidate.isNotEmpty(loanId)){
				employeeLoan = delegator.findOne("Loan", [loanId : loanId], false);
				if(UtilValidate.isNotEmpty(employeeLoan)){
					partyId = employeeLoan.partyId;
					if(UtilValidate.isNotEmpty(partyId)){
						partyName = PartyHelper.getPartyName(delegator, partyId, false);
					}
					loanTypeId = employeeLoan.loanTypeId;
					principalAmount = employeeLoan.principalAmount;
					interestAmount = employeeLoan.interestAmount;
					numInterestInst = employeeLoan.numInterestInst;
					numPrincipalInst = employeeLoan.numPrincipalInst;
				}
			}
			
			employeeLoanRecoveryMap = [:];
			employeeLoanRecoveryMap["loanId"] = loanId;
			employeeLoanRecoveryMap["employeeId"] = partyId;
			employeeLoanRecoveryMap["partyName"] = partyName;
			employeeLoanRecoveryMap["loanTypeId"] = loanTypeId;
			employeeLoanRecoveryMap["principalAmount"] = principalAmount;
			employeeLoanRecoveryMap["interestAmount"] = interestAmount;
			employeeLoanRecoveryMap["numInterestInst"] = numInterestInst;
			employeeLoanRecoveryMap["numPrincipalInst"] = numPrincipalInst;
			employeeLoanRecoveryMap["loanRecoveryDate"] = UtilDateTime.toDateString(loanRecoveryDate,"dd-MM-yyyy");
			employeeLoanRecoveryMap["finAccountTransId"] = finAccountTransId;
			employeeLoanRecoveryMap["loanRecoveryAmount"] = amount;
			employeeLoanRecoveryMap["deducteePartyId"] = deducteePartyId;
			employeeLoanRecoveryMap["customTimePeriodId"] = customTimePeriodId;
			tempMap = [:];
			tempMap.putAll(employeeLoanRecoveryMap);
			if(UtilValidate.isNotEmpty(tempMap)){
				employeeLoanRecoveryList.addAll(tempMap);
			}
		}
	}
}
context.putAt("employeeLoanRecoveryList", employeeLoanRecoveryList);

