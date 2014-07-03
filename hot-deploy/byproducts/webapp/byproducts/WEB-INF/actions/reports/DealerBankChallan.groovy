import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.accounting.invoice.*;

dctx = dispatcher.getDispatchContext();
routeIdsList =[];
shipmentIds = [];
isByParty = Boolean.TRUE;
Timestamp estimatedDeliveryDateTime = null;
if(parameters.supplyDate){
	
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		estimatedDeliveryDateTime = new java.sql.Timestamp(sdf.parse(parameters.supplyDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+parameters.supplyDate, "");
	}
	
	/*SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		estimatedDeliveryDateTime = new java.sql.Timestamp(formatter.parse(parameters.supplyDate).getTime());
		
	}catch (ParseException e) {
	Debug.logError("==unparsable Date=="+parameters.supplyDate,e.toString())
	}*/
}else{
	estimatedDeliveryDateTime=UtilDateTime.nowTimestamp();
}
context.put("estimatedDeliveryDate", estimatedDeliveryDateTime);

rentInvoicesMap = [:];
int day=UtilDateTime.getDayOfMonth(estimatedDeliveryDateTime,TimeZone.getDefault(),Locale.getDefault());
if(day==5){
	dayStart = UtilDateTime.getDayStart(estimatedDeliveryDateTime);
	dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);
	condList = [];
	condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SHOPEE_RENT"));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	condList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
	condList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	invoices = delegator.findList("Invoice", cond, null, null, null, false);
	
	invoices.each{ eachInvoice ->
		facilityId = "";
		if(isByParty){
			facilityId = eachInvoice.partyId;
		}
		else{
			facilityId = eachInvoice.facilityId;
		}
		
		invoiceAmount = InvoiceWorker.getInvoiceTotal(eachInvoice);
		if(invoiceAmount>0){
			rentInvoicesMap.put(facilityId, invoiceAmount);
		}
	}
	context.rentInvoiceAmt =  rentInvoicesMap;
	
}
productNames = [:];

shipments = [];
routeIds=[];
conditionList=[];
Debug.log("parameters.shipmentId==="+parameters.shipmentId);
if(parameters.shipmentId){
	
    if(parameters.shipmentId != "allRoutes"){
		shipment =delegator.findOne("Shipment",[shipmentId : parameters.shipmentId], false);
		parameters.routeId = shipment.routeId;
	}else{
	    parameters.routeId = "All-Routes";
	}
}

/*shipmentType = "";
if(parameters.shipmentTypeId == "PM_SHIPMENT"){
	shipmentType = "onlyPMShip";
	result = ByProductNetworkServices.getBoothsByAMPM(delegator, [effectiveDate: estimatedDeliveryDateTime]);
	amPartyBooths = result.get("amPartyBooths");
	pmPartyBooths = result.get("pmPartyBooths");
	pmPartyBooths.removeAll(amPartyBooths);
	Debug.log("#########result########"+pmPartyBooths);
		
	
}*/

routeIdsList = [];
if(parameters.routeId !="All-Routes"){
	routeIdsList.add(parameters.routeId);
}
else{
	routeIdsList = (ByProductNetworkServices.getRoutesByAMPM(dctx ,UtilMisc.toMap("supplyType" ,"AM"))).get("routeIdsList");
}
//routeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN ,routeIdsList),null,null,null,false);

dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime);
dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);
context.putAt("dayBegin", dayBegin);


Map boothWiseSaleMap= FastMap.newInstance();

List amShipmentIds = ByProductNetworkServices.getDayShipmentIds(delegator ,dayBegin,dayEnd,"AM",null);
List pmShipmentIds = ByProductNetworkServices.getDayShipmentIds(delegator ,dayBegin,dayEnd,"PM",null);

/* for challan number*/
totalShipmentIds = [];
totalShipmentIds.addAll(amShipmentIds);
totalShipmentIds.addAll(pmShipmentIds);

orderHeader = delegator.findList("OrderHeaderItemProductShipmentAndFacility", EntityCondition.makeCondition("shipmentId", EntityOperator.IN, totalShipmentIds), ["originFacilityId", "shipmentId", "ownerPartyId"] as Set, null, null, false);

orderHeaderAM = EntityUtil.filterByCondition(orderHeader, EntityCondition.makeCondition("shipmentId", EntityOperator.IN, amShipmentIds));
orderHeaderPM = EntityUtil.filterByCondition(orderHeader, EntityCondition.makeCondition("shipmentId", EntityOperator.IN, pmShipmentIds));
challanSerialNumMap = [:];

orderHeaderAM.each{eachItem ->
	if(isByParty){
		challanSerialNumMap.put(eachItem.ownerPartyId, eachItem.ownerPartyId+"-"+eachItem.shipmentId);
	}else{
		challanSerialNumMap.put(eachItem.originFacilityId, eachItem.originFacilityId+"-"+eachItem.shipmentId);
	}
	
}
orderHeaderPM.each{eachEntry ->
	if(isByParty){
		if(!challanSerialNumMap.get(eachEntry.ownerPartyId)){
			challanSerialNumMap.put(eachEntry.ownerPartyId, eachEntry.ownerPartyId+"-"+eachEntry.shipmentId);
		}
	}else{
		if(!challanSerialNumMap.get(eachEntry.originFacilityId)){
			challanSerialNumMap.put(eachEntry.originFacilityId, eachEntry.originFacilityId+"-"+eachEntry.shipmentId);
		}
	}
	
	
}
context.challanSerialNumMap = challanSerialNumMap;

/*end challan number*/
amBoothTotals=[:];
pmBoothTotals=[:];
if(UtilValidate.isNotEmpty(amShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:amShipmentIds, fromDate:dayBegin, thruDate:dayEnd, includeReturnOrders:true, isByParty: isByParty]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		amBoothTotals = dayTotals.get("boothTotals");
	}
}
//Debug.log("= =================== amBoothTotals #@@@@###########"+amBoothTotals.get("S103"));

toDayAmStr=UtilDateTime.toDateString(estimatedDeliveryDateTime, "dd-MMM-yyyy");
prevDayPmStr=UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(estimatedDeliveryDateTime, -1), "dd-MMM-yyyy");
//pmShipments
if(UtilValidate.isNotEmpty(pmShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:pmShipmentIds,fromDate: UtilDateTime.addDaysToTimestamp(dayBegin, -1), thruDate: UtilDateTime.addDaysToTimestamp(dayEnd, -1), includeReturnOrders:true, isByParty: isByParty]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		pmBoothTotals = dayTotals.get("boothTotals");
	}
}
routeWiseMap =[:];
facilityBankMap =[:];
for(int i=0; i< routeIdsList.size();i++){
	routeId=routeIdsList.get(i);
	boothIdsList=[];
	getBoothRes=ByProductNetworkServices.getRouteBooths(delegator , UtilMisc.toMap("routeId",routeId));
	boothsList =getBoothRes.get("boothsList");
	//boothIdsList = getBoothRes.get("boothIdsList");
	boothsList = EntityUtil.filterByDate(boothsList, estimatedDeliveryDateTime, "openedDate", "closedDate", true);
	profilePartyIds= [];
	ownerFacilityMap = [:];
	
	if(isByParty){
		boothIdsList = EntityUtil.getFieldListFromEntityList(boothsList, "ownerPartyId", true);
		profilePartyIds = EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true);
		boothsList.each{eachItem ->
			if(ownerFacilityMap.get(eachItem.ownerPartyId)){
				tempFacList = ownerFacilityMap.get(eachItem.ownerPartyId);
				tempFacList.add(eachItem.facilityId);
				ownerFacilityMap.put(eachItem.ownerPartyId, tempFacList);
			}
			else{
				tempList = [];
				tempList.add(eachItem.facilityId);
				ownerFacilityMap.put(eachItem.ownerPartyId, tempList);
			}
		}
	}
	else{
		boothIdsList = EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true);
		profilePartyIds.addAll(boothIdsList)
	}
	
	partyProfileFacilityMap=ByProductNetworkServices.getPartyProfileDafult(dispatcher.getDispatchContext(),[boothIds:profilePartyIds]).get("partyProfileFacilityMap");
	boothSaleMap=[:];
	boothIdsList.each{ boothId ->
		
		paymentMethodId = "";
		profileBoothId = "";
		if(isByParty){
			facList = ownerFacilityMap.get(boothId);
			if(facList){
				profileBoothId = facList.get(0);
				paymentMethodId=partyProfileFacilityMap.get(profileBoothId);
			}
		}
		else{
			paymentMethodId=partyProfileFacilityMap.get(boothId);
			profileBoothId = boothId;
		}
		
		
		rentAmt = 0;
		if(rentInvoicesMap && rentInvoicesMap.get(boothId)){
			rentAmt = rentInvoicesMap.get(boothId);
		}
		boothAmPmMap=[:];
		amBoothTotalMap=[:];
		pmBoothTotalMap=[:];
		if(UtilValidate.isNotEmpty(paymentMethodId) && (paymentMethodId=="CHALLAN_PAYIN")){
			//get Am Details
			amBoothTotalMap=amBoothTotals.get(boothId);
			
			//get Pm details
			pmBoothTotalMap=pmBoothTotals.get(boothId);
			
		}
		
		if(UtilValidate.isNotEmpty(amBoothTotalMap) || UtilValidate.isNotEmpty(pmBoothTotalMap)){//only add when amount is zero
			amDetailsMap=[:];//inner map
			amDetailsMap["date"]=toDayAmStr;
			amDetailsMap["shift"]="M";
			if(UtilValidate.isNotEmpty(amBoothTotalMap)){
				amDetailsMap["saleVal"]=amBoothTotalMap.get("totalRevenue");
			}else{
				amDetailsMap["saleVal"]=0;
			}
			
			boothAmPmMap["AM"]=amDetailsMap;
		//pm starts here
			pmDetailsMap=[:];
			pmDetailsMap["date"]=prevDayPmStr;
			pmDetailsMap["shift"]="E";
			if(UtilValidate.isNotEmpty(pmBoothTotalMap)){
				pmDetailsMap["saleVal"]=pmBoothTotalMap.get("totalRevenue");
			}else{
				pmDetailsMap["saleVal"]=0;
			}
			boothAmPmMap["PM"]=pmDetailsMap;
		}
		if(UtilValidate.isNotEmpty(boothAmPmMap)){//if Am and Pm not empty then
			boothAmPmMap["rentVal"] = rentAmt;
			boothSaleMap[boothId]=boothAmPmMap;
			
			finAcccountRes=ByProductNetworkServices.getFacilityFinAccountInfo(dctx,[facilityId: profileBoothId]);
			if(UtilValidate.isNotEmpty(finAcccountRes.get("accountInfo"))){
				accountInfo=finAcccountRes.get("accountInfo");
				if(UtilValidate.isNotEmpty(accountInfo.finAccountCode)){
					tempMap = [:];
					tempMap.put("accountCode", accountInfo.finAccountCode);
					tempMap.put("bankName", accountInfo.finAccountName);
					facilityBankMap[boothId]=tempMap;
				}
			}
		}
	}
	if(UtilValidate.isNotEmpty(boothSaleMap)){
		routeWiseMap[routeId]=boothSaleMap;
	}
}
filterRouteIdsList = [];
if(parameters.routeId =="All-Routes"){
	filterRouteIdsList = (ByProductNetworkServices.getRoutesByAMPM(dctx ,UtilMisc.toMap("supplyType" ,"AM"))).get("routeIdsList");
}
else{
	filterRouteIdsList.add(parameters.routeId);
}
filteredRouteWiseMap = [:];
Iterator routeMapIter = routeWiseMap.entrySet().iterator();
while (routeMapIter.hasNext()) {
	Map.Entry entry = routeMapIter.next();
	if(filterRouteIdsList.contains(entry.getKey())){
		filteredRouteWiseMap.put(entry.getKey(), entry.getValue());
	}
}
context.put("isByParty", isByParty);
context.put("routeWiseMap",filteredRouteWiseMap);
context.put("facilityBankMap",facilityBankMap);