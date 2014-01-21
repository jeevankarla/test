import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.network.LmsServices;


userLogin= context.userLogin;
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());		
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());		
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");	   
	}
	conditionList = [];	
	if(parameters.facilityId){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
	}
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	routeList = delegator.findList("Facility",condition,null,null,null,false);
	
	productsList = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS ,"LMS_BULK"),["productId"] as Set,null,null,false);
	Set prodctIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(productsList, "productId", false));
	allRoutesList =[];
	abstRouteList =[];
	for(int i=0; i< routeList.size();i++){
		route = routeList.get(i);	
	conditionList.clear();	
	conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS , route.facilityId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
	//conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,"VENDOR"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	boothsList = delegator.findList("Facility",condition,null,null,null,false);
	boothPaymentsList=FastList.newInstance();
	boothPaidList =FastList.newInstance();	
	agentPaymentsReportMap =[:];
	GrTotalMap =[:];
	GrTotalMap["totSaleAmt"]=BigDecimal.ZERO;
	GrTotalMap["totRecpts"]=BigDecimal.ZERO;
	GrTotalMap["totOpeningAmt"]=BigDecimal.ZERO;
	GrTotalMap["totClosing"]=BigDecimal.ZERO;
	GrTotalMap["QTY"]=BigDecimal.ZERO;
	GrTotalMap["TRNSPTDUE"]=BigDecimal.ZERO;
	GrTotalMap["totalTrnsptDue"] = BigDecimal.ZERO;
	GrTotalMap["VHpaid"] = BigDecimal.ZERO;
	GrTotalMap["bulkQty"] = BigDecimal.ZERO;
	GrTotalMap["CARDQTY"] = BigDecimal.ZERO;
	GrTotalMap["route"]=0;	
	GrandTotAbstMap =[:];
	GrandTotAbstMap["totSaleAmt"]=BigDecimal.ZERO;
	GrandTotAbstMap["totRecpts"]=BigDecimal.ZERO;
	GrandTotAbstMap["totOpeningAmt"]=BigDecimal.ZERO;
	GrandTotAbstMap["totClosing"]=BigDecimal.ZERO;
	GrandTotAbstMap["TRNSPTDUE"]=BigDecimal.ZERO;
	GrandTotAbstMap["route"]=0;
	boothsList.each{ booths ->
		zeroBooths =BigDecimal.ZERO;		
		obAmount =	( NetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: fromDateTime , facilityId:booths.facilityId])).get("openingBalance");
		
		boothPaymentsMap =[:];
		//for getting reciepts
		boothsPaidDetail = NetworkServices.getBoothPaidPayments( dctx , [fromDate:fromDateTime ,thruDate:thruDateTime , facilityId:booths.facilityId]);
		reciepts = BigDecimal.ZERO;		
		if(UtilValidate.isNotEmpty(boothsPaidDetail)){
			reciepts = boothsPaidDetail.get("invoicesTotalAmount");
		}
		boothPaymentsMap["reciepts"] = ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["totRecpts"] += ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));			
		//for getting saleamount 
		dayTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(booths.facilityId),fromDate:fromDateTime, thruDate:thruDateTime]);
		
		productTotalsList =dayTotals.get("productTotals");		
		productTotalsList.each{ productTotal ->				
			if(prodctIdsSet.contains(productTotal.getKey())) {
				GrTotalMap["bulkQty"] += productTotal.getValue().get("total");			
			}			
		}
		Map cardTypeMap =(Map)dayTotals.get("supplyTypeTotals").get("CARD");
		boothPaymentsMap["agentName"]=booths.get("facilityName");
		saleAmt= dayTotals.get("totalRevenue");		
		quantity = dayTotals.get("totalQuantity");
		cardQty = BigDecimal.ZERO;
		if(cardTypeMap){
			saleAmt = saleAmt-(cardTypeMap.get("totalRevenue"));	
			cardQty = (cardTypeMap.get("total"));
			quantity =quantity.subtract((cardTypeMap.get("total")));
		}		
		boothPaymentsMap["quantity"] =((new BigDecimal(quantity)).setScale(1,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["QTY"] +=((new BigDecimal(quantity)).setScale(1,BigDecimal.ROUND_HALF_UP));		
		 //Total card qty
		GrTotalMap["CARDQTY"] +=((new BigDecimal(cardQty)).setScale(1,BigDecimal.ROUND_HALF_UP));
		
		boothPaymentsMap["salesAmt"] =((new BigDecimal(saleAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["totSaleAmt"] +=((new BigDecimal(saleAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
				
		boothPaymentsMap["openingAmt"] = ((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["totOpeningAmt"] += ((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));					
		closingAmt = ((new BigDecimal((boothPaymentsMap["openingAmt"]+boothPaymentsMap["salesAmt"])-(boothPaymentsMap["reciepts"]))).setScale(2,BigDecimal.ROUND_HALF_UP));
		
		/*lets get the closing balance as next day opening balance.Since the advance payment is knocking at the time of trucksheet generation
		nextDateTime = UtilDateTime.getNextDayStart(fromDateTime);		
		closingAmt =	( NetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: nextDateTime , facilityId:booths.facilityId])).get("openingBalance");		*/
		boothPaymentsMap["closingAmt"] = closingAmt;
		// for showing routes whose closing balace is not ZERO 
		if(closingAmt !=0){
			 GrandTotAbstMap["totSaleAmt"] +=((new BigDecimal(saleAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
			 GrandTotAbstMap["totRecpts"]+= ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
			 GrandTotAbstMap["totOpeningAmt"] += ((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
			 GrandTotAbstMap["totClosing"]  += ((new BigDecimal(boothPaymentsMap["closingAmt"])).setScale(2,BigDecimal.ROUND_HALF_UP));			 
		 }		
		GrTotalMap["totClosing"] += ((new BigDecimal(boothPaymentsMap["closingAmt"])).setScale(2,BigDecimal.ROUND_HALF_UP));
		zeroBooths = (boothPaymentsMap["openingAmt"]+boothPaymentsMap["salesAmt"]+boothPaymentsMap["reciepts"]+boothPaymentsMap["closingAmt"]);		
		if(zeroBooths !=0){
			agentPaymentsReportMap[booths.facilityId]=[:];
			agentPaymentsReportMap[booths.facilityId].putAll(boothPaymentsMap);	
		}			
	}
	context.put("userLogin",userLogin);
	context.put("fromDate", fromDateTime);
	context.put("thruDate", thruDateTime);	
	context.put("invoiceTypeId", "TRANSPORTER_PAYIN");
	context.put("facilityId", route.facilityId)
	transporterDuesMap = LmsServices.getTransporterDues(dctx , context);	
	transporterDueList = transporterDuesMap.get("transporterDuesList");
	BigDecimal totalTrnsptDue =BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(transporterDuesMap)){
		totalTrnsptDue =transporterDuesMap.get("invoicesTotalDueAmount");
	}
	transptInvoiceAmt=BigDecimal.ZERO;
	if(transporterDueList.size() != 0) {
		transporterDue = (Map)transporterDueList.get(0);
		if(UtilValidate.isNotEmpty(transporterDue)){
			transptInvoiceAmt=transporterDue.get("amount");
		}
	}
	GrTotalMap["TRNSPTDUE"]=((new BigDecimal(transptInvoiceAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
	GrandTotAbstMap["TRNSPTDUE"]=((new BigDecimal(transptInvoiceAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
	GrTotalMap["totalTrnsptDue"] = ((new BigDecimal(totalTrnsptDue)).setScale(2,BigDecimal.ROUND_HALF_UP));
	trnsptPaidMap = LmsServices.getTransporterPaid(dctx , context);	
		paidTransporterMap = trnsptPaidMap.get("transporterPaidMap");		
		if(UtilValidate.isNotEmpty(paidTransporterMap)){
			trnsptMapIter = paidTransporterMap.entrySet().iterator();
			while (trnsptMapIter.hasNext()) {
				Map.Entry trnsptMapEntry = trnsptMapIter.next();
				if(trnsptMapEntry.getKey().equals(route.facilityId) ){
					GrTotalMap["VHpaid"] = ((new BigDecimal(trnsptMapEntry.getValue())).setScale(2,BigDecimal.ROUND_HALF_UP));
				}
			}
		}		
			
		GrTotalMap["route"]=route.facilityId;
		GrandTotAbstMap["route"]=route.facilityId;		
		abstRouteList.add(GrandTotAbstMap);
	if(UtilValidate.isNotEmpty(agentPaymentsReportMap)){		
		agentPaymentsReportMap["routeTotals"]=GrTotalMap;		
		allRoutesList.add(agentPaymentsReportMap);
	}
	
}
	context.put("abstRouteList", abstRouteList);	
	// for getting opening cash (which is not remitted in Bank)
	List condList = FastList.newInstance();
	condList.add(EntityCondition.makeCondition("supplyDate", EntityOperator.LESS_THAN,new  java.sql.Date(fromDateTime.getTime())));
	EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("-createdDate");
	List<GenericValue> bankRemittances = delegator.findList("BankRemittance", condition, null, orderBy, null, false);
	GenericValue prevBankRemittance = EntityUtil.getFirst(bankRemittances);	
	bankOpeningValue = BigDecimal.ZERO;
	if (UtilValidate.isNotEmpty(prevBankRemittance)) {
		Map boothPaymentCtx = FastMap.newInstance();
		BigDecimal cashReceived = BigDecimal.ZERO;
		boothPaymentCtx.putAll(context);
		boothPaymentCtx.remove("facilityId");
		boothPaymentCtx.put("onlyCurrentDues",false);
		prevBankRemittanceDate =   UtilDateTime.toTimestamp(prevBankRemittance.getDate("supplyDate"));  
	    boothPaymentCtx.put("fromDate",UtilDateTime.addDaysToTimestamp(prevBankRemittanceDate, 1));
		boothPaymentCtx.put("thruDate",UtilDateTime.addDaysToTimestamp(fromDateTime, -1));
	    Map paidPaymentsMap =  NetworkServices.getBoothPaidPayments(dctx,boothPaymentCtx);
	    if(UtilValidate.isNotEmpty(paidPaymentsMap)){
			cashReceived = cashReceived.add((BigDecimal)paidPaymentsMap.get("invoicesTotalAmount"));
	    }	  
		//lets get the transporter Dues for the period
		Map transporterDuesMap = LmsServices.getTransporterDues(dctx , boothPaymentCtx);
		BigDecimal vehicleShortAmount =BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(transporterDuesMap)){
			vehicleShortAmount = (BigDecimal)transporterDuesMap.get("invoicesTotalAmount");
		}
		//to be remitted amount equals prevBankRemittanceDate closing Balance + total cash Received - vehicleShortAmount
		BigDecimal tobeRemittedAmount = (cashReceived.add(prevBankRemittance.getBigDecimal("closingBalance"))).subtract(vehicleShortAmount);
		
		if(UtilValidate.isNotEmpty(tobeRemittedAmount)){
			bankOpeningValue = tobeRemittedAmount;		
		}	
	}
	context.put("remitOpening", bankOpeningValue);
	context.put("allRoutesList", allRoutesList);
	
