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
		facilityId = eachInvoice.facilityId
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
if(parameters.routeId !="All-Routes"){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.routeId));
}
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
//routeList = delegator.findList("Facility",condition,null,null,null,false);
routeIdsList = (ByProductNetworkServices.getRoutesByAMPM(dctx ,UtilMisc.toMap("supplyType" ,"AM"))).get("routeIdsList");
//Debug.log("routeIdsList==============="+routeIdsList);
routeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.IN ,routeIdsList),null,null,null,false);

dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime);
context.putAt("dayBegin", dayBegin);
dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);

Map boothWiseSaleMap= FastMap.newInstance();

List amShipmentIds = ByProductNetworkServices.getDayShipmentIds(delegator ,dayBegin,dayEnd,"AM",null);
List pmShipmentIds = ByProductNetworkServices.getDayShipmentIds(delegator ,dayBegin,dayEnd,"PM",null);
amBoothTotals=[:];
pmBoothTotals=[:];
if(UtilValidate.isNotEmpty(amShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:amShipmentIds,fromDate:dayBegin, thruDate:dayEnd]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		amBoothTotals = dayTotals.get("boothTotals");
	}
}
toDayAmStr=UtilDateTime.toDateString(estimatedDeliveryDateTime, "dd-MMM-yyyy");
prevDayPmStr=UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(estimatedDeliveryDateTime, -1), "dd-MMM-yyyy");
//pmShipments
if(UtilValidate.isNotEmpty(pmShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:pmShipmentIds,fromDate: UtilDateTime.addDaysToTimestamp(dayBegin, -1), thruDate: UtilDateTime.addDaysToTimestamp(dayEnd, -1)]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		pmBoothTotals = dayTotals.get("boothTotals");
	}
}
						
routeWiseMap =[:];
facilityBankMap =[:];
for(int i=0; i< routeList.size();i++){
	route = routeList.get(i);
	routeId=route.facilityId;
	boothIdsList=[];
	getBoothRes=ByProductNetworkServices.getRouteBooths(delegator , UtilMisc.toMap("routeId",routeId));
	boothsList =getBoothRes.get("boothsList");
	boothIdsList = getBoothRes.get("boothIdsList");
	boothsList = EntityUtil.filterByDate(boothsList, estimatedDeliveryDateTime, "openedDate", "closedDate", true);
	
	 partyProfileFacilityMap=ByProductNetworkServices.getPartyProfileDafult(dispatcher.getDispatchContext(),[boothIds:boothIdsList]).get("partyProfileFacilityMap");
	boothSaleMap=[:];
	boothsList.each{ boothObj ->
		boothId=boothObj.facilityId;
		paymentMethodId=partyProfileFacilityMap.get(boothId);
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
			boothSaleMap[boothId]=boothAmPmMap;
			finAcccountRes=ByProductNetworkServices.getFacilityFinAccountInfo(dctx,[facilityId:boothId]);
			if(UtilValidate.isNotEmpty(finAcccountRes.get("accountInfo"))){
			accountInfo=finAcccountRes.get("accountInfo");
			if(UtilValidate.isNotEmpty(accountInfo.finAccountCode)){
				facilityBankMap[boothId]=accountInfo.finAccountCode;
				}
				
			}
			
		}
		boothAmPmMap["rentVal"] = rentAmt;
	}
	if(UtilValidate.isNotEmpty(boothSaleMap)){
		routeWiseMap[routeId]=boothSaleMap;
	}
}

context.put("routeWiseMap",routeWiseMap);
context.put("facilityBankMap",facilityBankMap);