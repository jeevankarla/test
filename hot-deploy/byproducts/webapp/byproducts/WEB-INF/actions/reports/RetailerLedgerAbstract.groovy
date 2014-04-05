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

boothDeatilMap=ByProductNetworkServices.getBoothRoute(dispatcher.getDispatchContext(),[boothId:boothId]).get("boothDetails");
if(UtilValidate.isNotEmpty(boothDeatilMap)){
	context.routeId=boothDeatilMap.get("routeId");
}

amBoothDayTotals=[:];
pmBoothDayTotals=[:]

if(UtilValidate.isNotEmpty(amShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(boothId),shipmentIds:amShipmentIds,fromDate:dayStart, thruDate:dayEnd]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		amBoothDayTotals = dayTotals.get("dayWiseTotals");
	}
}

//pmShipments
if(UtilValidate.isNotEmpty(pmShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(boothId),shipmentIds:pmShipmentIds,fromDate: dayStart, thruDate: dayEnd]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		pmBoothDayTotals = dayTotals.get("dayWiseTotals");
	}
}


routeWiseMap =[:];

	boothSalesMap=[:];
	allDaySaleMap=[:];
	
		obAmount=BigDecimal.ZERO;
		closingBal=BigDecimal.ZERO;
		
		for(int j=0 ; j < (UtilDateTime.getIntervalInDays(dayStart,dayEnd)+1); j++){
			Timestamp saleDate = UtilDateTime.addDaysToTimestamp(dayStart, j);
			dayLmsTotalQty = 0;
			dayTotalRevenue=BigDecimal.ZERO;
			curntDay=UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd");
			curntDaySalesMap=[:];
			if(UtilValidate.isNotEmpty(amBoothDayTotals.getAt(curntDay))){
				curntDaySalesMap["AM"]=amBoothDayTotals.getAt(curntDay).get("productTotals");
				dayTotalRevenue=dayTotalRevenue.add(amBoothDayTotals.getAt(curntDay).get("totalRevenue"));
			}
			if(UtilValidate.isNotEmpty(pmBoothDayTotals.getAt(curntDay))){
				curntDaySalesMap["PM"]=pmBoothDayTotals.getAt(curntDay).get("productTotals");
				dayTotalRevenue=dayTotalRevenue.add(pmBoothDayTotals.getAt(curntDay).get("totalRevenue"));
			}
			if(UtilValidate.isNotEmpty(curntDaySalesMap)){
				curntDaySalesMap["totalRevenue"]=dayTotalRevenue;
				reciepts = BigDecimal.ZERO;
				boothPaidDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [fromDate:saleDate ,thruDate:saleDate , facilityId:boothId]);
				if(UtilValidate.isNotEmpty(boothPaidDetail)){
					reciepts = boothPaidDetail.get("invoicesTotalAmount");
				}
				curntDaySalesMap["PaidAmt"] = ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
				if(j==0){//Opeinig Balance called only  for firstDay  in whole period
					obAmount =	( ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: saleDate , facilityId:boothId])).get("openingBalance");
					closingBal=obAmount+dayTotalRevenue-reciepts;
				}else{
					obAmount=closingBal;
					closingBal=obAmount+dayTotalRevenue-reciepts;
				}
				curntDaySalesMap["OpeningBal"]=((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
				curntDaySalesMap["ClosingBal"]=((new BigDecimal(obAmount+dayTotalRevenue-reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
				allDaySaleMap[curntDay]=curntDaySalesMap;
			}
		}
	
boothSalesMap[boothId]=allDaySaleMap;
context.put("boothSalesMap",boothSalesMap);
//context.putAt("routeWiseTotalCrates", routeWiseTotalCrates);
