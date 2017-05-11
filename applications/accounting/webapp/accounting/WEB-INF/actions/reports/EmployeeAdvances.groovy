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
condList = [];
roId = parameters.division;
context.roId = roId;
segmentId = parameters.segment;
branchList = [];
condList.clear();
if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company")){
	condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,roId));
	condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
	condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	List roWiseBranchaList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(roWiseBranchaList)){
		branchList= EntityUtil.getFieldListFromEntityList(roWiseBranchaList,"partyIdTo", true);
		branchList.add(roId);
	}
	
}
condList.clear();

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
ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"EMPLOYEE_ADV")],EntityOperator.AND);
finAccountTypes=delegator.findList("FinAccountType",ecl,null,null,null,false);
finAccountTypeIds = EntityUtil.getFieldListFromEntityList(finAccountTypes, "finAccountTypeId", true);
conditionList =[];
if(UtilValidate.isEmpty(parameters.finAccountTypeId) || "ALL".equalsIgnoreCase(parameters.finAccountTypeId)){
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountTypeId",EntityOperator.IN,finAccountTypeIds)],EntityOperator.AND));
}else{
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountTypeId",EntityOperator.EQUALS,parameters.finAccountTypeId)],EntityOperator.AND));
}
//	conditionList.add(EntityCondition.makeCondition("finAccountTypeId",EntityOperator.EQUALS,parameters.finAccountTypeId));
if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,parameters.partyId));
}
EntityCondition condition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);

finAccountList=delegator.findList("FinAccount",condition,null,null,null,false);
finAccountTypeIdsMap=[:];
//EmployeeAdvDetails=[];
finAccountDetailedMap=[:];
finAccountTypeIdList=[];
List detailTempList=FastList.newInstance();

finAccntDetailedCsv=[];
finAccountDetailedMap=[:];
Debug.log("finacctlist===="+finAccountList);
finAccountList.each{finAccountTypeId->
	List tempList=FastList.newInstance();
	
	detailTempMap=[:];
	tempMap=[:];
	
	openBalanceDebit=0;
	openBalanceCredit=0;
	currentDebit=0;
	currentCredit=0;
	closingDebit=0;
	closingCredit=0;
	balance=0;
	tempMap.finAccountTypeId=finAccountTypeId.finAccountTypeId;
	tempMap.partyId=finAccountTypeId.ownerPartyId;
	detailTempMap.finAccountTypeId=finAccountTypeId.finAccountTypeId;
	detailTempMap.partyId=finAccountTypeId.ownerPartyId;
	partyName=PartyHelper.getPartyName(delegator, finAccountTypeId.ownerPartyId, false);
	tempMap.Name=partyName;
	detailTempMap.Name=partyName;
	
	finAccntMap=[:]
	finAccntMap["finAccountTypeId"]=finAccountTypeId.finAccountTypeId;
	finAccntMap["Name"]=partyName;
	finAccntMap["partyId"]=finAccountTypeId.ownerPartyId;
	Map finAccTransMap;
	if(roId.equals("Company") && segmentId.equals("All")){
		finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccountTypeId.finAccountId,"transactionDate",fromDate, "costCenterId", null, "segmentId", null));
	}
	else if(roId.equals("Company") && !segmentId.equals("All")){
		finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccountTypeId.finAccountId,"transactionDate",fromDate, "costCenterId", null, "segmentId", segmentId));
	}
	else if(!roId.equals("Company") && segmentId.equals("All")){
		finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccountTypeId.finAccountId,"transactionDate",fromDate, "roBranchList", branchList, "segmentId", null));
	}
	else{
		finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccountTypeId.finAccountId,"transactionDate",fromDate, "roBranchList", branchList, "segmentId", segmentId));
	}
	/*if(UtilValidate.isNotEmpty(finAccTransMap)){
		if(UtilValidate.isNotEmpty(finAccTransMap.get("withDrawal"))){
			openBalanceCredit=finAccTransMap.get("withDrawal");
		}
		if(UtilValidate.isNotEmpty(finAccTransMap.get("deposit"))){
			openBalanceDebit=finAccTransMap.get("deposit");
		}
	}*/
	balance=finAccTransMap.get("openingBalance");
	if(balance>0){
		openBalanceDebit=balance;
		openBalanceCredit=0;
	}else{
		openBalanceCredit=-(balance);
		openBalanceDebit=0;
	}
	tempMap.openBalanceDebit=openBalanceDebit;
	tempMap.openBalanceCredit=openBalanceCredit;
	detailTempMap.openBalanceDebit=openBalanceDebit;
	detailTempMap.openBalanceCredit=openBalanceCredit;
	
	finAccntMap["openBalanceDebit"]=openBalanceDebit;
	finAccntMap["openBalanceCredit"]=openBalanceCredit;
	finAccntDetailedCsv.addAll(finAccntMap);
	exprList = [];
	
	if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company"))
		exprList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, branchList));
	if(!segmentId.equals("All") && !segmentId.equals("YARN_SALE"))
		exprList.add(EntityCondition.makeCondition("segmentId" , EntityOperator.EQUALS, segmentId));
	if(segmentId.equals("YARN_SALE"))
		exprList.add(EntityCondition.makeCondition("segmentId" , EntityOperator.IN, UtilMisc.toList("YARN_SALE", "DEPOT_YARN_SALE")));
	exprList.add(EntityCondition.makeCondition("finAccountId",EntityOperator.EQUALS,finAccountTypeId.finAccountId));
	exprList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	exprList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"FINACT_TRNS_CANCELED"));
	EntityCondition cond = EntityCondition.makeCondition(exprList ,EntityOperator.AND);
	
	/*cond=EntityCondition.makeCondition([EntityCondition.makeCondition("finAccountId",EntityOperator.EQUALS,finAccountTypeId.finAccountId),
										 EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate),
										 EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate),
										 EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"FINACT_TRNS_CANCELED")],EntityOperator.AND);*/
	
	finAccountTranses=delegator.findList("FinAccountTrans",cond,null,null,null,false);
	DaywiseMap=[:];
	if(UtilValidate.isNotEmpty(finAccountTranses)){
		finAccountTranses.each{finAccntTrans->
			List newList=FastList.newInstance();
			debit=0;
			credit=0;
			
			newTempMap=[:];
			newTempMap.transactionDate=finAccntTrans.transactionDate;
			if(UtilValidate.isNotEmpty(finAccntTrans.finAccountTransTypeId) && finAccntTrans.finAccountTransTypeId=="WITHDRAWAL"){
				currentCredit+=finAccntTrans.get("amount");
				credit=finAccntTrans.get("amount");
			}
			
			if(UtilValidate.isNotEmpty(finAccntTrans.finAccountTransTypeId) && finAccntTrans.finAccountTransTypeId=="DEPOSIT"){
				currentDebit+=finAccntTrans.get("amount");
				debit=finAccntTrans.get("amount");
			}
			newTempMap.credit=credit;
			newTempMap.debit=debit;
			newList.add(newTempMap);
			
			//adding DetaildTo CSV
			finAccntTransInnerMap=[:]
			finAccntTransInnerMap["transactionDate"]=org.ofbiz.base.util.UtilDateTime.toDateString(finAccntTrans.transactionDate, "dd-MMM-yyyy");
			finAccntTransInnerMap["debit"]=debit;
			finAccntTransInnerMap["credit"]=credit;
			finAccntDetailedCsv.addAll(finAccntTransInnerMap);
			
			if(UtilValidate.isEmpty(DaywiseMap["Details"])){
				DaywiseMap["Details"]=newList;
			}else{
				List existing = FastList.newInstance();
				existing=DaywiseMap["Details"];
				existing.add(newTempMap);
				DaywiseMap["Details"]=existing;
			}
			}
	}
	
	closingCredit=openBalanceCredit+currentCredit;
	closingDebit=openBalanceDebit+currentDebit;
	tempMap.closingCredit=closingCredit;
	tempMap.closingDebit=closingDebit;
	balance=0;
	balance=closingDebit-closingCredit;
	tempMap.balance=balance;
	
	detailTempMap.list=DaywiseMap;
	detailTempList.add(detailTempMap);
	
	if(UtilValidate.isEmpty(finAccountDetailedMap[finAccountTypeId.finAccountTypeId])){
		finAccountDetailedMap[finAccountTypeId.finAccountTypeId]=detailTempList;
		detailTempList=UtilMisc.sortMaps(detailTempList, UtilMisc.toList("Name"));
	}else{
		List existing = FastList.newInstance();
		existing=finAccountDetailedMap[finAccountTypeId.finAccountTypeId];
		existing.add(detailTempMap);
		existing=UtilMisc.sortMaps(existing, UtilMisc.toList("Name"));
		finAccountDetailedMap[finAccountTypeId.finAccountTypeId]=existing;
	}
	//EmployeeAdvDetails.add(detailTempList);
	tempMap.currentDebit=currentDebit;
	tempMap.currentCredit=currentCredit;
	
	finAccntTotMap=[:]
	finAccntTotMap["Name"]="Total-"+finAccountTypeId.ownerPartyId;
	finAccntTotMap["debit"]=currentDebit;
	finAccntTotMap["credit"]=currentCredit;
	finAccntDetailedCsv.addAll(finAccntTotMap);
	
	/*balance=((openBalanceDebit+currentDebit)-(openBalanceCredit+currentCredit));*/
	if(balance>0){
		tempMap.finalClosing=balance+"(Dr)";
	}else if(balance<0){
		tempMap.finalClosing=-(balance)+"(Cr)";
	}else{
	  tempMap.finalClosing=0;
	}
	finAccntClosingMap=[:]
	finAccntClosingMap["Name"]="ClosingBal-"+finAccountTypeId.ownerPartyId;
	finAccntClosingMap["closingDebit"]=closingDebit;
	finAccntClosingMap["closingCredit"]=closingCredit;
	finAccntDetailedCsv.addAll(finAccntClosingMap);
	
	tempList.add(tempMap);
	if(UtilValidate.isEmpty(finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId])){
		finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId]=tempList;
		tempList=UtilMisc.sortMaps(tempList, UtilMisc.toList("Name"));
		finAccountTypeIdList=tempList;
	}else{
		List existing = FastList.newInstance();
		existing=finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId];
		existing.add(tempMap);
		existing=UtilMisc.sortMaps(existing, UtilMisc.toList("Name"));
		finAccountTypeIdList=existing;
		finAccountTypeIdsMap[finAccountTypeId.finAccountTypeId]=existing;
	}
}
//finAccountDetailedMap2["finAccountTypeId"]=detailTempList;
context.finAccountTypeIdsMap=finAccountTypeIdsMap;
context.finAccountDetailedMap=finAccountDetailedMap;
//Debug.log("finAccountDetailedMap====="+finAccountDetailedMap.size());
//context.detailTempList=detailTempList
//Debug.log("finAccountTypeIdsMap====="+finAccountTypeIdsMap.size());
context.finAccountTypeIdList=finAccountTypeIdList;
context.finAccntDetailedCsv=finAccntDetailedCsv;
//Debug.log("finAccountTypeIdList======================"+finAccountTypeIdList);
//Debug.log("finAccountTypeIdsMap=========================="+finAccountTypeIdsMap);
//Debug.log("finAccntDetailedCsv=========================="+finAccntDetailedCsv);
