import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

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
hideSearch ="Y";
paymentIds = FastList.newInstance();
boothRouteIdsMap=[:];
onlyCurrentDues = Boolean.TRUE;
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
if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}
isRetailer = false;
if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null) {
		Debug.logInfo("Booth '" + facilityId + "' does not exist!","");
		context.errorMessage = "Booth '" + facilityId + "' does not exist!";
		return;
	}
	if(facility.facilityTypeId == "BOOTH"){
		isRetailer = true;
	}
}
if(parameters.paymentMethodTypeId){
	paymentMethodTypeId = parameters.paymentMethodTypeId;
}else{
	paymentMethodTypeId = context.paymentMethodTypeId;
}
Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();

SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
try {
	paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + paymentDate, "");
   
}
JSONObject boothsDuesDaywiseJSON = new JSONObject();

Map resultCtx = ByProductNetworkServices.getPartyProfileDafult(dctx, UtilMisc.toMap("boothIds", []));
Map paymentTypeFacilityMap = (Map)resultCtx.get("paymentTypeFacilityMap");
filterFacilityList = [];
if(paymentMethodTypeId){
	filterFacilityList =  paymentTypeFacilityMap.get(paymentMethodTypeId);
}
if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(parameters.onlyCurrentDues){
	onlyCurrentDues = Boolean.parseBoolean(parameters.onlyCurrentDues);
}
if(statusId =="PAID"){
	invoiceStatusId = "INVOICE_PAID";	
}else{
	invoiceStatusId = null;
}
if(parameters.paymentIds){
	paymentIds = parameters.paymentIds;	
}

partyProfileDefault = delegator.findList("PartyProfileDefault", null, UtilMisc.toSet("defaultPayMeth", "partyId"), null, null, false);
paymentTypeParties = EntityUtil.getFieldListFromEntityList(partyProfileDefault, "partyId", true);
paymentTypes = EntityUtil.getFieldListFromEntityList(partyProfileDefault, "defaultPayMeth", true);
paymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, paymentTypes), null, null, null, false);
context.paymentMethodType = paymentMethodType;

boothPaymentsList=[];
condList = [];
condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS, "FNACT_ACTIVE"));
condList.add(EntityCondition.makeCondition("finAccountTypeId" ,EntityOperator.EQUALS, "BANK_ACCOUNT"));
condList.add(EntityCondition.makeCondition("ownerPartyId" ,EntityOperator.IN, paymentTypeParties));
condList.add(EntityCondition.makeCondition("finAccountCode" ,EntityOperator.NOT_EQUAL, null));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
accountList = delegator.findList("FinAccount", cond, ["finAccountCode", "finAccountName", "ownerPartyId"] as Set, null, null ,false);
checkList = [];
accountNameList = [];
accountList.each{ eachAcc ->
	accCode = eachAcc.finAccountCode;
	if(!checkList.contains(accCode)){
		checkList.add(accCode);
		accountNameList.add(eachAcc);
	}
}
accountNameFacilityIds = [];
if(parameters.finAccountCode != "AllBanks"){
	accountNameFacility = EntityUtil.filterByCondition(accountList, EntityCondition.makeCondition("finAccountCode", EntityOperator.EQUALS, parameters.finAccountCode));
	accountNameFacilityIds = EntityUtil.getFieldListFromEntityList(accountNameFacility, "ownerPartyId", true);
}

context.putAt("accountNameList", accountNameList);
stopListing = true;
if(parameters.paymentMethodTypeId == "CASH_PAYIN"){
	stopListing = false;
}
if(hideSearch == "N" || stopListing){
	if (statusId == "PAID") {
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId , paymentMethodTypeId:paymentMethodTypeId , paymentIds : paymentIds]);
		boothTempPaymentsList = boothsPaymentsDetail["paymentsList"];
		boothRouteIdsMap=boothsPaymentsDetail["boothRouteIdsMap"];
	}
	else {
		
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPayments( delegator ,dispatcher, userLogin ,paymentDate , invoiceStatusId,facilityId ,paymentMethodTypeId , onlyCurrentDues);
		boothTempPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
	}
	finalFilterList = [];
	filterFacilityList.each{ eachBankFacilityId ->
		if(!accountNameFacilityIds || (accountNameFacilityIds &&  accountNameFacilityIds.contains(eachBankFacilityId))){
			finalFilterList.add(eachBankFacilityId);
		}
	}
	boothPaymentsInnerList = [];
	boothPaymentsList = [];
	invoicesTotalAmount =0;
	invoicesTotalDueAmount = 0;
	if (statusId != "PAID" ) {
		
		if(paymentMethodTypeId == "CASH_PAYIN" && isRetailer){
			boothPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
		}
		else{
			boothTempPaymentsList.each{boothPay ->
				facilityId = boothPay.get("facilityId");
				if(paymentMethodTypeId == "CHALLAN_PAYIN"){
					if(finalFilterList && finalFilterList.contains(facilityId)){
						boothPaymentsInnerList.add(boothPay);
						if (statusId != "PAID") {
							invoicesTotalAmount = invoicesTotalAmount+boothPay.get("grandTotal");
							invoicesTotalDueAmount = invoicesTotalDueAmount+boothPay.get("totalDue");
						}
					}
				}
				else{
					if(!filterFacilityList || (filterFacilityList && filterFacilityList.contains(facilityId))){
						boothPaymentsInnerList.add(boothPay);
						if (statusId != "PAID") {
							invoicesTotalAmount = invoicesTotalAmount+boothPay.get("grandTotal");
							invoicesTotalDueAmount = invoicesTotalDueAmount+boothPay.get("totalDue");
						}
					}
				}
			}
			boothPaymentsList.addAll(boothPaymentsInnerList);
			boothsPaymentsDetail["invoicesTotalAmount"] =invoicesTotalAmount;
			boothsPaymentsDetail["invoicesTotalDueAmount"] =invoicesTotalDueAmount;
		}
		
	}
	if(statusId == "PAID"){
		boothPaymentsList = boothsPaymentsDetail["paymentsList"];
		if(paymentMethodTypeId == "CHALLAN_PAYIN"){
			axisBankPayments = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId , paymentMethodTypeId:"AXISHTOH_PAYIN" , paymentIds : paymentIds]);
			axisPaymentsList = axisBankPayments["paymentsList"];
			boothPaymentsList.addAll(axisPaymentsList);
		}
	}
	context.boothPaymentsList = boothPaymentsList;	
	context.paymentDate= paymentDate;
	context.paymentTimestamp= paymentTimestamp;
	context.invoicesTotalAmount = UtilFormatOut.formatCurrency(boothsPaymentsDetail["invoicesTotalAmount"], context.get("currencyUomId"), locale);
	if(boothsPaymentsDetail["invoicesTotalDueAmount"] != null){
		context.invoicesTotalDueAmount = UtilFormatOut.formatCurrency(boothsPaymentsDetail["invoicesTotalDueAmount"], context.get("currencyUomId"), locale);
	}
	// now get the past dues breakup
	boothsDuesDayWise = [:];
	
	if (!onlyCurrentDues) {
		boothPaymentsList.each { booth ->
			boothDuesDetail = ByProductNetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId:booth.facilityId]);	
			duesList = boothDuesDetail["boothDuesList"];
			JSONArray boothDuesList= new JSONArray();
			duesList.each { due ->
				JSONObject dueJSON = new JSONObject();
				dueJSON.put("supplyDate", UtilDateTime.toDateString(due.supplyDate, "dd MMM, yyyy"));
				dueJSON.put("amount", due.amount);
				dueJSON.put("amount", UtilFormatOut.formatCurrency(due.amount, context.get("currencyUomId"), locale));
				boothDuesList.add(dueJSON);
			}
			JSONObject boothDuesMap = new JSONObject();
			boothDuesMap.put("totalAmount", UtilFormatOut.formatCurrency(boothDuesDetail["totalAmount"], context.get("currencyUomId"), locale));
			boothDuesMap.put("boothDuesList", boothDuesList);
			boothsDuesDaywiseJSON.put(booth.facilityId, boothDuesMap);
		}
	}	
}
context.boothsDuesDaywiseJSON = boothsDuesDaywiseJSON;
context.statusId = statusId;
context.boothRouteIdsMap=boothRouteIdsMap;


