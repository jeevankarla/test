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
hideSearch ="Y";
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

if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}
isRetailer = false;
isRoute = false;
GenericValue facility = null;
if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null) {
		Debug.logInfo("Booth '" + facilityId + "' does not exist!","");
		context.errorMessage = "Booth '" + facilityId + "' does not exist!";
		return;
	}
	if(facility.facilityTypeId == "BOOTH"){
		isRetailer = true;
		if(facility.categoryTypeEnum == "CR_INST"){
			return;
		}
	}
	if(facility.facilityTypeId == "ROUTE"){
		isRoute = true;
		duesByParty = Boolean.FALSE;
	}
	/*else{
		context.facilityId = facilityId
	}*/
}
else{
	duesByParty = Boolean.FALSE;
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


partyProfileDefault = delegator.findList("PartyProfileDefault", null, null, null, null, false);
partyProfileDefault = EntityUtil.filterByDate(partyProfileDefault, UtilDateTime.nowTimestamp());
paymentTypeParties = EntityUtil.getFieldListFromEntityList(partyProfileDefault, "partyId", true);
paymentTypes = EntityUtil.getFieldListFromEntityList(partyProfileDefault, "defaultPayMeth", true);
paymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, paymentTypes), null, null, null, false);

payMethList = EntityUtil.filterByCondition(paymentMethodType, EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, parameters.paymentMethodTypeId));

paymentMethodDesc = (EntityUtil.getFirst(payMethList)).get("description");
context.paymentMethodType = paymentMethodType;

boothPaymentsList=[];
condList = [];
condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS, "FNACT_ACTIVE"));
condList.add(EntityCondition.makeCondition("finAccountTypeId" ,EntityOperator.EQUALS, "BANK_ACCOUNT"));
condList.add(EntityCondition.makeCondition("ownerPartyId" ,EntityOperator.IN, paymentTypeParties));
condList.add(EntityCondition.makeCondition("finAccountCode" ,EntityOperator.NOT_EQUAL, null));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
accountList = delegator.findList("FinAccount", cond, ["finAccountId","finAccountCode", "finAccountName", "ownerPartyId"] as Set, null, null ,false);
checkList = [];
accountNameList = [];
//JSONObject partyFinAccMap = new JSONObject();
partyFinAccMap = [:];	
accountList.each{ eachAcc ->
	partyFinAccMap.put(eachAcc.ownerPartyId,eachAcc);
}
//this is for drop down
condList.clear();
condList.add(EntityCondition.makeCondition("attrName" ,EntityOperator.EQUALS, "COLLECTION_ACCOUNT"));
condList.add(EntityCondition.makeCondition("attrValue" ,EntityOperator.EQUALS, "Y"));
cond1 = EntityCondition.makeCondition(condList, EntityOperator.AND);
accountAttrList = delegator.findList("FinAccountAttribute", cond1, ["finAccountId"] as Set, null, null ,false);
finAccountIds = EntityUtil.getFieldListFromEntityList(accountAttrList, "finAccountId", true);
accountNameList = delegator.findList("FinAccount", EntityCondition.makeCondition("finAccountId", EntityOperator.IN, finAccountIds), null,null,null,false);

context.partyFinAccMap = partyFinAccMap;
accountNameFacilityIds = [];
if(parameters.finAccountCode != "AllBanks"){
	accountNameFacility = EntityUtil.filterByCondition(accountList, EntityCondition.makeCondition("finAccountCode", EntityOperator.EQUALS, parameters.finAccountCode));
	accountNameOwnerIds = EntityUtil.getFieldListFromEntityList(accountNameFacility, "ownerPartyId", true);
	facilityList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, accountNameOwnerIds), null, null, null, false);
	accountNameFacilityIds = EntityUtil.getFieldListFromEntityList(facilityList, "ownerPartyId", true);
}

context.putAt("accountNameList", accountNameList);
stopListing = true;
if(parameters.paymentMethodTypeId == "CASH_PAYIN" || parameters.paymentMethodTypeId == "CHALLAN_PAYIN"){
	stopListing = false;
}
if(hideSearch == "N" || stopListing){
	if (statusId == "PAID") {
		boothsPaymentsDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId , paymentMethodTypeId:paymentMethodTypeId , paymentIds : paymentIds, isByParty: duesByParty]);
		boothTempPaymentsList = boothsPaymentsDetail["paymentsList"];
		boothRouteIdsMap=boothsPaymentsDetail["boothRouteIdsMap"];
	}
	else {
		if(!duesByParty){
			boothsPaymentsDetail = ByProductNetworkServices.getBoothPayments( delegator ,dispatcher, userLogin ,paymentDate , invoiceStatusId,facilityId ,paymentMethodTypeId , onlyCurrentDues);
		}else{
			boothsPaymentsDetail = ByProductNetworkServices.getBoothPayments( delegator ,dispatcher, userLogin ,paymentDate , invoiceStatusId,facilityId ,paymentMethodTypeId , onlyCurrentDues, duesByParty, Boolean.TRUE);
		}
		boothTempPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
	}
	Debug.log("boothTempPaymentsList ####################"+boothTempPaymentsList);
	finalFilterList = [];
	filterFacilityList.each{ eachBankFacilityId ->
		if(!accountNameFacilityIds || (accountNameFacilityIds &&  accountNameFacilityIds.contains(eachBankFacilityId))){
			finalFilterList.add(eachBankFacilityId);
		}
	}
	/*Debug.log("#######filterFacilityList################"+filterFacilityList);*/
	boothPaymentsInnerList = [];
	boothPaymentsList = [];
	invoicesTotalAmount =0;
	axisHostTotalAmount =0;
	invoicesTotalDueAmount = 0;
	exclList = [];
	if (statusId != "PAID" ) {
		
		if(paymentMethodTypeId == "CASH_PAYIN" && isRetailer){
			boothPaymentsList = boothsPaymentsDetail["boothPaymentsList"];
		}
		else{
			boothTempPaymentsList.each{boothPay ->
				facilityId = boothPay.get("facilityId");
				if(paymentMethodTypeId == "CHALLAN_PAYIN"){
					if(finalFilterList && finalFilterList.contains(facilityId)){
						if(duesByParty){
							exclList.add(facilityId);
							boothPaymentsInnerList.add(boothPay);
							invoicesTotalAmount = invoicesTotalAmount+boothPay.get("grandTotal");
							invoicesTotalDueAmount = invoicesTotalDueAmount+boothPay.get("totalDue");
						}else{
							boothPaymentsInnerList.add(boothPay);
						}
					}
				}
				else{
					if(!filterFacilityList || (filterFacilityList && filterFacilityList.contains(facilityId))){
						boothPaymentsInnerList.add(boothPay);
						invoicesTotalAmount = invoicesTotalAmount+boothPay.get("grandTotal");
						invoicesTotalDueAmount = invoicesTotalDueAmount+boothPay.get("totalDue");
					}
				}
			}
			if(paymentMethodTypeId == "CASH_PAYIN" && isRoute){
				facilityGroup = delegator.findList("FacilityGroupMember", EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, parameters.facilityId), ["facilityId"] as Set, ["sequenceNum"], null, false);
				facilityGroupSequenceIds = EntityUtil.getFieldListFromEntityList(facilityGroup, "facilityId", true);
				orderedPayList = [];
				facilityGroupSequenceIds.each{ eachSeqFacId ->
					boothPaymentsInnerList.each{eachPayEntry ->
						if(eachPayEntry.facilityId == eachSeqFacId){
							orderedPayList.addAll(eachPayEntry);
						}
					}
				}
				boothPaymentsInnerList.clear();
				boothPaymentsInnerList.addAll(orderedPayList);
			}
			if(paymentMethodTypeId == "CHALLAN_PAYIN" && parameters.finAccountCode != "AllBanks" && !parameters.facilityId){
				accountNameFacilityIds.each{ eachRetailer ->
					if(!exclList.contains(eachRetailer)){
						tempMap = [:];
						tempMap.facilityId = eachRetailer;
						tempMap.routeId = "";
						tempMap.paymentMethodTypeDesc = paymentMethodDesc;
						tempMap.grandTotal = 0;
						tempMap.totalDue = 0;
						tempMap.supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
						boothPaymentsInnerList.add(tempMap);
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
			axisHostTotalAmount+=axisBankPayments["invoicesTotalAmount"];
			axisPaymentsList = axisBankPayments["paymentsList"];
			if(parameters.finAccountCode != "AllBanks"){
				tempPaidList = [];
				boothPaymentsList.each{eachEntry ->
					if(accountNameFacilityIds.contains(eachEntry.facilityId)){
						tempPaidList.addAll(eachEntry);
					}
				}
				boothPaymentsList.clear();
				boothPaymentsList.addAll(tempPaidList);
			}
			boothPaymentsList.addAll(axisPaymentsList);
		}
	}
	if(isRetailer && statusId != "PAID" && (boothPaymentsList.size()==0)){
		/*isRetailerExists = false;
		boothPaymentsList.each{ eachItem ->
			if(eachItem.facilityId == facilityId){
				isRetailerExists = true;
			}
		}*/
		//if(!isRetailerExists){
			payMethDescription = "";
			partyDefaults = EntityUtil.filterByCondition(partyProfileDefault, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facility.ownerPartyId));
			if(partyDefaults){
				partyDefault = EntityUtil.getFirst(partyDefaults);
				payType = partyDefault.get("defaultPayMeth");
				payMethDescList = EntityUtil.filterByCondition(paymentMethodType, EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, payType));
				if(payMethDescList){
					payMethDesc = EntityUtil.getFirst(payMethDescList);
					payMethDescription = payMethDesc.get("description");
				}
			}
			tempMap = [:];
			tempMap.facilityId = facilityId;
			tempMap.routeId = "";
			tempMap.paymentMethodTypeDesc = payMethDescription;
			tempMap.grandTotal = 0;
			tempMap.totalDue = 0;
			tempMap.supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			boothPaymentsList.add(tempMap);
		//}
		
	}
	context.boothPaymentsList = boothPaymentsList;	
	context.paymentDate= paymentDate;
	context.paymentTimestamp= paymentTimestamp;
	context.invoicesTotalAmount = UtilFormatOut.formatCurrency((boothsPaymentsDetail["invoicesTotalAmount"]+axisHostTotalAmount), context.get("currencyUomId"), locale);
	if(boothsPaymentsDetail["invoicesTotalDueAmount"] != null){
		context.invoicesTotalDueAmount = UtilFormatOut.formatCurrency(boothsPaymentsDetail["invoicesTotalDueAmount"], context.get("currencyUomId"), locale);
	}
	// now get the past dues breakup
	boothsDuesDayWise = [:];
	
	/*if (!onlyCurrentDues) {
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
	}	*/
}
context.boothsDuesDaywiseJSON = boothsDuesDaywiseJSON;
context.statusId = statusId;
context.boothRouteIdsMap=boothRouteIdsMap;


