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

userLogin= context.userLogin;
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
boothId = parameters.boothId;
dctx = dispatcher.getDispatchContext();
fromDateTime=UtilDateTime.nowTimestamp();
thruDateTime=UtilDateTime.nowTimestamp();

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayStart = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.dayStart=dayStart;
context.dayEnd=dayEnd;
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);

if(maxIntervalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
routeIdsList =[];
shipmentIds = [];

productNames = [:];

shipments = [];
routeIds=[];
conditionList=[];
/*if(parameters.routeId !="All-Routes"){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.routeId));
}
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
routeList = delegator.findList("Facility",condition,null,null,null,false);*/

Map boothWiseSaleMap= FastMap.newInstance();

List amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator ,dayStart,dayEnd,"AM");
List pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator ,dayStart,dayEnd,"PM");
amBoothTotals=[:];
pmBoothTotals=[:];

AMboothDeatilMap=ByProductNetworkServices.getBoothRoute(dispatcher.getDispatchContext(),[boothId:boothId, "subscriptionTypeId":"AM", "supplyDate":fromDateTime, isByParty:Boolean.TRUE]).get("boothDetails");
PMboothDeatilMap=ByProductNetworkServices.getBoothRoute(dispatcher.getDispatchContext(),[boothId:boothId, "subscriptionTypeId":"PM","supplyDate":fromDateTime, isByParty:Boolean.TRUE]).get("boothDetails");
if(UtilValidate.isNotEmpty(AMboothDeatilMap)){
	context.AMRouteId = AMboothDeatilMap.get("routeId");
}
if(UtilValidate.isNotEmpty(PMboothDeatilMap)){
	context.PMRouteId = PMboothDeatilMap.get("routeId");
}


amBoothDayTotals=[:];
pmBoothDayTotals=[:]
AmRoutes=[:];
tempAmlist=[];
finalAmMap=[:];
if(UtilValidate.isNotEmpty(amShipmentIds)){



	amShipmentIds.each{ eachShipmentId ->
		tempMap=[:];
		shipment = delegator.findOne("Shipment", [shipmentId : eachShipmentId], false);
		//Debug.log("shipment====DATE===================="+shipment.estimatedShipDate);
		//Debug.log("shipment========================"+shipment.routeId);
		estimatedShipDate=UtilDateTime.toDateString(shipment.estimatedShipDate ,"dd/MM/yy");
		
		
  
		tempMap.put("estimatedShipDate", estimatedShipDate);
		tempMap.put("routeId",shipment.routeId);
		tempAmlist.add(tempMap);
		AmRoutes.put(estimatedShipDate,shipment.routeId);	
	}
	//Debug.log("templist======================="+templist);

    routeList=[];
	tempAmlist.each{ eachtemp ->
		
		routeListLocal = [];
		if(finalAmMap.containsKey(eachtemp.estimatedShipDate)){
			routeListLocal = finalAmMap[eachtemp.estimatedShipDate];
		}
		routeListLocal.add(eachtemp.routeId);
		
		tempList=[];
		tempList.addAll(routeListLocal);
		
		finalAmMap.put(eachtemp.estimatedShipDate, tempList);
	}
	context.finalAmMap=finalAmMap;
	context.AmRoutes=AmRoutes;
	//Debug.log("AmRoutes========================"+AmRoutes);
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:[boothId],shipmentIds:amShipmentIds,fromDate:dayStart, thruDate:dayEnd]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		amBoothDayTotals = dayTotals.get("dayWiseTotals");
	}
}
PmRoutes=[:];
tempPmlist=[];
finalPmMap=[:];
//pmShipments
if(UtilValidate.isNotEmpty(pmShipmentIds)){
	pmShipmentIds.each{ eachShipmentId ->
		tempMap=[:];
		shipment = delegator.findOne("Shipment", [shipmentId : eachShipmentId], false);
		//Debug.log("shipment========================"+shipment.routeId);
		estimatedShipDate=UtilDateTime.toDateString(shipment.estimatedShipDate ,"dd/MM/yy");
		//Debug.log("shipment========================"+estimatedShipDate);
		tempMap.put("estimatedShipDate", estimatedShipDate);
		tempMap.put("routeId",shipment.routeId);
		tempPmlist.add(tempMap);
		PmRoutes.put(estimatedShipDate,shipment.routeId);	
	}
	context.PmRoutes=PmRoutes;

	routeList=[];
	tempPmlist.each{ eachtemp ->
		
		routeListLocal = [];
		if(finalPmMap.containsKey(eachtemp.estimatedShipDate)){
			routeListLocal = finalPmMap[eachtemp.estimatedShipDate];
		}
		routeListLocal.add(eachtemp.routeId);
		
		tempList=[];
		tempList.addAll(routeListLocal);
		
		finalPmMap.put(eachtemp.estimatedShipDate, tempList);
	}
	context.finalPmMap=finalPmMap;
	//Debug.log("PmRoutes========================"+PmRoutes);
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:[boothId],shipmentIds:pmShipmentIds,fromDate: dayStart, thruDate: dayEnd]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		pmBoothDayTotals = dayTotals.get("dayWiseTotals");
	}
}


routeWiseMap =[:];

boothSalesMap=[:];
allDaySaleMap=[:];
allDaySaleMap=[:];

obAmount=BigDecimal.ZERO;
closingBal=BigDecimal.ZERO;
// all  invoiceTypes in slaes
exprList=[];
exprList.clear();
exprList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,["ROUNDING_ADJUSTMENT", "INV_FPROD_ITEM", "VAT_SALE","EMPSUBSID_ADJUSTMENT"]));
exprList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
conditionInvRole = EntityCondition.makeCondition(exprList, EntityOperator.AND);
allInvoiceTypesList = delegator.findList("InvoiceItemTypeMapInvoiceType", conditionInvRole ,null, null, null, false );
invoiceTypeList= EntityUtil.getFieldListFromEntityList(allInvoiceTypesList,"invoiceItemTypeId", true);

facility = delegator.findOne("Facility", ["facilityId" :boothId], true);
exprList=[];
exprList.clear();
exprList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,invoiceTypeList));
exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,facility.ownerPartyId));
exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionInvRole = EntityCondition.makeCondition(exprList, EntityOperator.AND);
allInvoiceIdsList = delegator.findList("InvoiceAndItem", conditionInvRole , null, null, null, false );
allinvoiceamount=0;
shopeeInvoiceList=[];

allInvoiceIdsList.each{ eachinvoice ->
	tempinvoiceDetailMap=[:];
	invoice = delegator.findOne("Invoice", [invoiceId : eachinvoice.invoiceId], false);
	totalAmount = InvoiceWorker.getInvoiceTotal(invoice);
	allinvoiceamount=allinvoiceamount+totalAmount;
	tempinvoiceDetailMap.put("invoiceId", eachinvoice.invoiceId);
	tempinvoiceDetailMap.put("dueDate", eachinvoice.invoiceDate);
	tempinvoiceDetailMap.put("invoiceItemTypeId", eachinvoice.invoiceItemTypeId);
	description="";
	if("SERTAX_SALE".equals(eachinvoice.invoiceItemTypeId)){
		invoiceType=delegator.findOne("InvoiceType",[invoiceTypeId : eachinvoice.invoiceTypeId] , false);
		description=invoiceType.description;
		
	}else{
	invItemType = delegator.findOne("InvoiceItemType", [invoiceItemTypeId : eachinvoice.invoiceItemTypeId], false);
	description=invItemType.description;
	}
	tempinvoiceDetailMap.put("description", description);
	tempinvoiceDetailMap.put("invoiceTypeId", eachinvoice.invoiceTypeId);
	tempinvoiceDetailMap.put("amount", totalAmount);
	shopeeInvoiceList.add(tempinvoiceDetailMap);
}
context.shopeeInvoiceList=shopeeInvoiceList;

boolean enablePartywiseDues = Boolean.FALSE;
try {
	GenericValue tenantConfigEnablePartyDueTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap(	"propertyTypeEnumId", "RT_MKTG", "propertyName","enablePartyWiseDues"), true);
	if (UtilValidate.isNotEmpty(tenantConfigEnablePartyDueTrack)&& (tenantConfigEnablePartyDueTrack.getString("propertyValue")).equals("Y")) {
	enablePartywiseDues = Boolean.TRUE;
	}
} catch (GenericEntityException e) {
// TODO: handle exception
Debug.logError(e, module);
}
for(int j=0 ; j < (UtilDateTime.getIntervalInDays(dayStart,dayEnd)+1); j++){
	Timestamp saleDate = UtilDateTime.addDaysToTimestamp(dayStart, j);
	Timestamp saleDate1 = UtilDateTime.addDaysToTimestamp(dayStart, j);
	dayLmsTotalQty = 0;
	dayTotalRevenue=BigDecimal.ZERO;
	curntDay=UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd");
	curntDay1=UtilDateTime.toDateString(saleDate1 ,"dd/MM/yy");
	
	//oldPmnts = EntityUtil.filterByCondition(shipment, EntityCondition.makeCondition("estimatedShipDate",EntityOperator.EQUALS, curntDay1));
	subscriptionType="";
	curntDaySalesMap=[:];
	if(UtilValidate.isNotEmpty(amBoothDayTotals.getAt(curntDay))){
		subscriptionType="AM";
		BoothRoutes = ByProductNetworkServices.getBoothShipment(delegator,UtilMisc.toMap("boothId",boothId,"effectiveDate",saleDate,"subscriptionType",subscriptionType));
		routes=BoothRoutes.get("shippedRouteIds");
		curntDaySalesMap["AMroutes"]=routes;
		
		curntDaySalesMap["AM"]=amBoothDayTotals.getAt(curntDay).get("productTotals");
		
		dayTotalRevenue=dayTotalRevenue.add(amBoothDayTotals.getAt(curntDay).get("totalRevenue"));
	}
	if(UtilValidate.isNotEmpty(pmBoothDayTotals.getAt(curntDay))){
		subscriptionType="PM";
		BoothRoutes = ByProductNetworkServices.getBoothShipment(delegator,UtilMisc.toMap("boothId",boothId,"effectiveDate",saleDate,"subscriptionType",subscriptionType));
		routes=BoothRoutes.get("shippedRouteIds");
		curntDaySalesMap["PMroutes"]=routes;
		
		curntDaySalesMap["PM"]=pmBoothDayTotals.getAt(curntDay).get("productTotals");
		dayTotalRevenue=dayTotalRevenue.add(pmBoothDayTotals.getAt(curntDay).get("totalRevenue"));
	}
	if(j==0){
		if(shopeeInvoiceList){
		dayTotalRevenue +=allinvoiceamount;
		}
	}	
	
//	if(UtilValidate.isNotEmpty(BoothRoutes)){
//		boothRouteIdsMap=(Map)BoothRoutes.get("boothRouteIdsMap");//to get routeIds
//	}
	
	//obAmount =	( ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: saleDate , facilityId:boothId, isByParty:Boolean.TRUE])).get("openingBalance");
	
	
	
	//if(UtilValidate.isNotEmpty(curntDaySalesMap)){
		curntDaySalesMap["totalRevenue"]=dayTotalRevenue;
		reciepts = BigDecimal.ZERO;
		boothPaidDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [fromDate:saleDate ,thruDate:saleDate , facilityId:boothId, isByParty:Boolean.TRUE]);
		
		paymentList=[];
		
		if(UtilValidate.isNotEmpty(boothPaidDetail)){
			reciepts = boothPaidDetail.get("invoicesTotalAmount");
			if(UtilValidate.isNotEmpty(boothPaidDetail.get("boothAllPaymentsList"))){
				boothPaymentsList=boothPaidDetail.get("boothAllPaymentsList")
				boothPaymentsList.each{ eachpayment ->
				tempMap=[:];
				tempMap.put("partyIdFrom", eachpayment.get("partyIdFrom"));
				tempMap.put("paymentMethodTypeId", eachpayment.get("paymentMethodTypeId"));
				tempMap.put("paymentId", eachpayment.get("paymentId"));
				tempMap.put("amount", eachpayment.get("amount"));
				paymentList.add(tempMap);
				}
				curntDaySalesMap["paymentDetails"] = paymentList;
				
			}
		}
		curntDaySalesMap["PaidAmt"] = ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));

		if(j==0){//Opeinig Balance called only  for firstDay  in whole period
			obAmount = 0;
			if(enablePartywiseDues){
			obAmount = (ByProductNetworkServices.getOpeningBalanceForParty(dctx , [userLogin: userLogin ,saleDate: saleDate, partyId: facility.ownerPartyId])).get("openingBalance");
			}else{
			obAmount =	(ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: saleDate , facilityId:boothId, isByParty:Boolean.TRUE])).get("openingBalance");
			}
			if(UtilValidate.isEmpty(curntDaySalesMap)){
					openingbal=obAmount;
					context.openingbal=openingbal;
					}
			closingBal=obAmount+dayTotalRevenue-reciepts;

		}else{
			obAmount=closingBal;
			closingBal=obAmount+dayTotalRevenue-reciepts;
		}
		curntDaySalesMap["OpeningBal"]=((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
		curntDaySalesMap["ClosingBal"]=((new BigDecimal(obAmount+dayTotalRevenue-reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
		allDaySaleMap[curntDay1]=curntDaySalesMap;
	//}
}
	
boothSalesMap[boothId]=allDaySaleMap;
context.put("boothSalesMap",boothSalesMap);
//context.putAt("routeWiseTotalCrates", routeWiseTotalCrates);
