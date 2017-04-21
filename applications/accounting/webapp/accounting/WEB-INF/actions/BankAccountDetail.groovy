import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import org.ofbiz.base.util.UtilHttp;

date = parameters.asOnDate;
finAccountId = parameters.finAccountId;
finAccountName = parameters.finAccountName
finAccountTypeId = parameters.finAccountTypeId;
ownerPartyId = parameters.ownerPartyId;

Timestamp datee;
def sdf = new SimpleDateFormat("yyyy-MM-dd");
try {
	if (UtilValidate.isNotEmpty(date)) {
		datee = new java.sql.Timestamp(sdf.parse(date).getTime());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
context.errorMessage = "Cannot parse date string: " + e;
	return;
}
Debug.log("ownerpartyId====="+ownerPartyId);
roId= parameters.roId;
Debug.log("roid====="+roId);
context.roId=roId;
context.ownerPartyId=ownerPartyId;
context.date=datee;
if("Y".equals(parameters.noConditionFind)){
	bankAccountDetailList = [];
	conditionList = [];
	if(finAccountId){
		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.LIKE, "%"+finAccountId+"%"));
	}
	if(finAccountName){
		conditionList.add(EntityCondition.makeCondition("finAccountName", EntityOperator.LIKE, "%"+finAccountName+"%"));
	}
	if(ownerPartyId){
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
	}
	conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, finAccountTypeId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	finAccountList = delegator.findList("FinAccount", cond, null, null, null, false);
	finAccountIdList = EntityUtil.getFieldListFromEntityList(finAccountList, "finAccountId", false);
	//EntityCondition.makeCondition("finAccountId", EntityOperator.IN, finAccountIdList
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.IN, finAccountIdList));
	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, datee));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("-realisationDate");
//	Debug.log("as on datecond ===="+cond);
	finAccountTransList = delegator.findList("FinAccountTrans", cond, null, orderBy, null, false);
	
	for(GenericValue finAccountEntry : finAccountList){
		tempMap = [:];
		tempMap["finAccountId"]=finAccountEntry.finAccountId;
		tempMap["finAccountName"]=finAccountEntry.finAccountName;
		tempMap["finAccountCode"]=finAccountEntry.finAccountCode;
		tempMap["isOperative"]="N";
		tempMap["balance"] = BigDecimal.ZERO;	
		realisationDate = "";
		if("FNACT_ACTIVE".equals(finAccountEntry.statusId)){
			tempMap["isOperative"]="Y";
		}
		totalDeposits = BigDecimal.ZERO;
		totalWithdraws = BigDecimal.ZERO;
		
		newFinAccountTransList = EntityUtil.filterByCondition(finAccountTransList,EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountEntry.finAccountId));
		for(GenericValue finAccountTransEntry : newFinAccountTransList){//Transactions for each finAccount
			if("DEPOSIT".equals(finAccountTransEntry.finAccountTransTypeId)){
				totalDeposits = totalDeposits.add(finAccountTransEntry.amount);
			}
			if("WITHDRAWAL".equals(finAccountTransEntry.finAccountTransTypeId)){
				totalWithdraws = totalWithdraws.add(finAccountTransEntry.amount);
			}
		}
		tempMap["totalDeposits"]=totalDeposits;
		tempMap["totalWithdraws"]=totalWithdraws;
		tempMap["balance"] = totalDeposits.subtract(totalWithdraws);
		tempMap["balanceConfirmation"]="";
		latestRealizedFinAccTrans = EntityUtil.getFirst(newFinAccountTransList);
		if(latestRealizedFinAccTrans){
			realisationDate = latestRealizedFinAccTrans.realisationDate;
		}
		tempMap["realisationDate"] = realisationDate;
		bankAccountDetailList.add(tempMap);
	}
	context.bankAccountDetailList = bankAccountDetailList;
}

if("Y".equals(parameters.printBankAccountDetails)){
	balanceConfirmation = "";
	bankAccNo = "";
	isOperative = "";
	balance = BigDecimal.ZERO;
	realisationDate = "";
	printBankAccountDetailList = [];
	finAccountNameSplit = "";
	remarks = "";
//	Debug.log("parameters====="+parameters);
	Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	if (rowCount < 1) {
		request.setAttribute("_ERROR_MESSAGE_", "No rows to process");
		return "error";
	}
	for (int i = 0; i < rowCount; i++){
		finAccountName = "";
		String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		tempMap = [:];
		if (paramMap.containsKey("finAccountId" + thisSuffix)) {
		  finAccountId = (String) paramMap.get("finAccountId"+thisSuffix);
		}
		if (paramMap.containsKey("finAccountNameSplit" + thisSuffix)) {
		  finAccountNameSplit = (String) paramMap.get("finAccountNameSplit"+thisSuffix);
		}
		for(int j=0;j<finAccountNameSplit.size();j++){
			finAccountName = finAccountName + " " + finAccountNameSplit[j];
		}
		if (paramMap.containsKey("finAccountCode" + thisSuffix)) {
			bankAccNo = (String) paramMap.get("finAccountCode"+thisSuffix);
		  }
		if (paramMap.containsKey("isOperative" + thisSuffix)) {
			isOperative = (String) paramMap.get("isOperative"+thisSuffix);
		  }
		if (paramMap.containsKey("balance" + thisSuffix)) {
			balance = (String) paramMap.get("balance"+thisSuffix);
		  }
		if (paramMap.containsKey("balanceConfirmation" + thisSuffix)) {
			balanceConfirmation = (String) paramMap.get("balanceConfirmation"+thisSuffix);
		  }
		if (paramMap.containsKey("realisationDate" + thisSuffix)) {
			realisationDate = (String) paramMap.get("realisationDate"+thisSuffix);
		  }
		if (paramMap.containsKey("remarks" + thisSuffix)) {
			remarks = (String) paramMap.get("remarks"+thisSuffix);
		  }
		finAccountName = finAccountName.replaceAll("[,]"," ");
		tempMap["finAccountId"] = finAccountId;
		tempMap["finAccountName"] = finAccountName;
		tempMap["finAccountCode"] = bankAccNo;
		tempMap["isOperative"] = isOperative;
		BigDecimal balance = new BigDecimal(balance);
		if(balance < 0){
			tempMap["isNegBalance"] = "Y";
		}else{
			tempMap["isNegBalance"] = "N";
		}
		if(balanceConfirmation == "/"){
			balanceConfirmation = "";
		}
		if(realisationDate == "/"){
			realisationDate = "";
		}
		if(remarks == "/"){
			remarks = "";
		}
		tempMap["balance"] = balance;
		tempMap["balanceConfirmation"] = balanceConfirmation;
		tempMap["realisationDate"] = realisationDate;
		tempMap["remarks"] = remarks;
		printBankAccountDetailList.add(tempMap);
	}
	context.printBankAccountDetailList=printBankAccountDetailList;
}
