import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.accounting.payment.PaymentWorker;
import in.vasista.vbiz.humanres.HumanresService;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.accounting.finaccount.FinAccountServices;
dctx = dispatcher.getDispatchContext();

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
employeeIds=[];
ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"LOAN_ACCOUNT")],EntityOperator.AND);
finAccountTypes=delegator.findList("FinAccountType",ecl,null,null,null,false);
finAccountTypeIds = EntityUtil.getFieldListFromEntityList(finAccountTypes, "finAccountTypeId", true);
conditionList =[];
if(UtilValidate.isEmpty(parameters.finAccountTypeId)){
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountTypeId",EntityOperator.IN,finAccountTypeIds)],EntityOperator.AND));
}else{
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountTypeId",EntityOperator.EQUALS,parameters.finAccountTypeId)],EntityOperator.AND));
}

if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,parameters.partyId)],EntityOperator.AND));
}
EntityCondition condition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);

finAccountList=delegator.findList("FinAccount",condition,null,null,null,false);
finAccountTypeIdsMap=[:];
finAccountList.each{finAccountTypeId->
	List tempList=FastList.newInstance();
	tempMap=[:];
	openBalanceDebit=0;
	openBalanceCredit=0;
	currentDebit=0;
	currentCredit=0;
	closingDebit=0;
	closingCredit=0;
	balance=0;
	tempMap.partyId=finAccountTypeId.ownerPartyId;
	partyName=PartyHelper.getPartyName(delegator, finAccountTypeId.ownerPartyId, false);
	tempMap.Name=partyName;
	Map finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccountTypeId.finAccountId,"transactionDate",fromDate));
	if(UtilValidate.isNotEmpty(finAccTransMap)){
		if(UtilValidate.isNotEmpty(finAccTransMap.get("withDrawal"))){
			openBalanceDebit=finAccTransMap.get("withDrawal");
		}
		if(UtilValidate.isNotEmpty(finAccTransMap.get("deposit"))){
			openBalanceCredit=finAccTransMap.get("deposit");
		}
	}
	tempMap.openBalanceDebit=openBalanceDebit;
	tempMap.openBalanceCredit=openBalanceCredit;
	cond=EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountId",EntityOperator.EQUALS,finAccountTypeId.finAccountId),
										 EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate),
										 EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate),
										 EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"FINACT_TRNS_CANCELED")],EntityOperator.AND);
	finAccountTranses=delegator.findList("FinAccountTrans",cond,null,null,null,false);
	if(UtilValidate.isNotEmpty(finAccountTranses)){
		finAccountTranses.each{finAccntTrans->
			if(UtilValidate.isNotEmpty(finAccntTrans.finAccountTransTypeId) && finAccntTrans.finAccountTransTypeId=="WITHDRAWAL"){
				currentDebit+=finAccntTrans.get("amount");
			}
			if(UtilValidate.isNotEmpty(finAccntTrans.finAccountTransTypeId) && finAccntTrans.finAccountTransTypeId=="DEPOSIT"){
				currentCredit+=finAccntTrans.get("amount");
			}
		}
	}
	tempMap.currentDebit=currentDebit;
	tempMap.currentCredit=currentCredit;
	balance=((openBalanceDebit+currentDebit)-(openBalanceCredit+currentCredit));
	if(balance>0){
		closingCredit=balance;
	}else{
		closingDebit=-(balance);
	}
	tempMap.closingCredit=closingCredit;
	tempMap.closingDebit=closingDebit;
	tempList.add(tempMap);
	if(UtilValidate.isEmpty(finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId])){
		finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId]=tempList;
	}else{
		List existing = FastList.newInstance();
		existing=finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId];
		existing.add(tempMap);
		finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId]=existing;
	}
}
context.finAccountTypeIdsMap=finAccountTypeIdsMap;