import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];
paymentDate ="";
paymentIds = FastList.newInstance();
boothRouteIdsMap=[:];
onlyCurrentDues = Boolean.TRUE;
duesByParty = Boolean.TRUE;
bankName = "";
if(parameters.paymentDate){
	paymentDate = parameters.paymentDate;
}else{
	paymentDate = context.paymentDate;
}

if(parameters.statusId){
	statusId = parameters.statusId;
}else{
	statusId = context.statusId;
}
facilityId = "";
if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}
condList = [];
if(facilityId != null){
	facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
	if(facilityDetail && facilityDetail.categoryTypeEnum != "CR_INST"){
		Debug.logInfo(facilityId+" is not a credit institution '","");
		context.errorMessage = facilityId+" is not a credit institution '";
		return;
	}
	ownerPartyId = facilityDetail.ownerPartyId;
	partyProfileDetail = delegator.findList("PartyProfileDefault", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId), null, null, null, false);
	partyProfileDetail = EntityUtil.filterByDate(partyProfileDetail, UtilDateTime.nowTimestamp());
	if(partyProfileDetail){
		partyPayType = EntityUtil.getFirst(partyProfileDetail);
		partyPayMeth = partyPayType.defaultPayMeth;
		if(partyPayMeth){
			condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
			condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
			accCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			finAccounts = delegator.findList("FinAccount", accCond, null, null, null ,false);
			if(finAccounts){
				finAccount = EntityUtil.getFirst(finAccounts);
				context.chequeBankName = finAccount.finAccountName;
			}
		}
	}
}
hideSearch = "Y";
isCreditParty = false;
facilityIdList = [];
condList = [];
if(facilityId){
	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
}
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
facilityCustomBill = delegator.findList("FacilityCustomBilling", cond, null, null, null, false);
facilityBills = EntityUtil.filterByDate(facilityCustomBill, UtilDateTime.nowTimestamp());
facilityIdList = EntityUtil.getFieldListFromEntityList(facilityBills, "facilityId", true);

Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();

SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
try {
	paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + paymentDate, "");
   
}
if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(parameters.onlyCurrentDues){
	onlyCurrentDues = Boolean.parseBoolean(parameters.onlyCurrentDues);
}
Debug.log("only current dues #########################3"+onlyCurrentDues);
if(statusId =="PAID"){
	invoiceStatusId = "INVOICE_PAID";	
}else{
	invoiceStatusId = null;
}
if(parameters.paymentIds){
	paymentIds = parameters.paymentIds;	
}

boothPaymentsList=[];

if(hideSearch == "N"){
	if (statusId == "PAID") {
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId, paymentIds : paymentIds, isByParty: duesByParty]);
		boothTempPaymentsList = boothsPaymentsDetail["paymentsList"];
		boothRouteIdsMap=boothsPaymentsDetail["boothRouteIdsMap"];
	}
	else {
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPayments( delegator ,dispatcher, userLogin ,paymentDate , invoiceStatusId,facilityId ,paymentMethodTypeId , onlyCurrentDues,duesByParty, Boolean.TRUE);
		boothTempPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
	}
	if(statusId != "PAID" && boothTempPaymentsList.size()== 0){
		tempMap = [:];
		tempMap.facilityId = facilityId;
		tempMap.routeId = "";
		tempMap.paymentMethodTypeDesc = "";
		tempMap.grandTotal = 0;
		tempMap.totalDue = 0;
		tempMap.supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		boothTempPaymentsList.add(tempMap);
	}
	boothPaymentsInnerList = [];
	boothPaymentsList = [];
	invoicesTotalAmount =0;
	invoicesTotalDueAmount = 0;
	exclList = [];
	if (statusId != "PAID" ) {
		
		boothTempPaymentsList.each{boothPay ->
			facilityId = boothPay.get("facilityId");
			boothPaymentsInnerList.add(boothPay);
			invoicesTotalAmount = invoicesTotalAmount+boothPay.get("grandTotal");
			invoicesTotalDueAmount = invoicesTotalDueAmount+boothPay.get("totalDue");
		}
		
		boothPaymentsList.addAll(boothPaymentsInnerList);
		boothsPaymentsDetail["invoicesTotalAmount"] =invoicesTotalAmount;
		boothsPaymentsDetail["invoicesTotalDueAmount"] =invoicesTotalDueAmount;
	}
	
	if(statusId == "PAID"){
	    boothPaymentsInnerList = boothTempPaymentsList;
	}
	context.boothPaymentsList = boothPaymentsInnerList;	
	context.paymentDate= paymentDate;
	context.paymentTimestamp= paymentTimestamp;
	if(boothsPaymentsDetail["invoicesTotalDueAmount"] != null){
		context.invoicesTotalDueAmount = UtilFormatOut.formatCurrency(boothsPaymentsDetail["invoicesTotalDueAmount"], context.get("currencyUomId"), locale);
	}
	// now get the past dues breakup
		
}
condList.clear();
condList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "COLLECTION_ACCOUNT"));
condList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "Y"));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
finAccountAttr = delegator.findList("FinAccountAttribute", cond, UtilMisc.toSet("finAccountId"), null, null, false);
collectionAccIds = EntityUtil.getFieldListFromEntityList(finAccountAttr, "finAccountId", true);
//Debug.log("collectionAccIds ########################"+collectionAccIds);
condList.clear();
if(collectionAccIds){
	condList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.NOT_IN, collectionAccIds));
}
condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
finAccCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
fundTransferAccounts = delegator.findList("FinAccount", finAccCond, null, null, null, false);
//Debug.log("fundTransferAccounts #########################################"+fundTransferAccounts);
context.fundTransferAccounts = fundTransferAccounts;
context.statusId = statusId;
context.boothRouteIdsMap=boothRouteIdsMap;


