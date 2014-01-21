import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;


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
	if(UtilDateTime.getIntervalInDays(fromDateTime, thruDateTime) >32){
		return;
		
	}
	conditionList = [];
	if(parameters.facilityId){
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
	}
	if(parameters.categoryTypeEnum){
		conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,parameters.categoryTypeEnum));	
	}
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"BOOTH"));
	
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	boothList = delegator.findList("Facility",condition,null,null,null,false);
		
	allRoutesList =[];
	abstRouteList =[];
	// lets get the opening balance invoices if any
	
	boothPaymentsList=FastList.newInstance();
	boothPaidList =FastList.newInstance();
	boothDuesAbstractList=FastList.newInstance();
	agentPaymentsReportMap =[:];
	GrTotalMap =[:];
	GrTotalMap["totSaleAmt"]=BigDecimal.ZERO;
	GrTotalMap["totRecpts"]=BigDecimal.ZERO;
	GrTotalMap["totOpeningAmt"]=BigDecimal.ZERO;
	GrTotalMap["totClosing"]=BigDecimal.ZERO;
	GrTotalMap["QTY"]=BigDecimal.ZERO;	
	
	for(int i=0; i< boothList.size();i++){	
		
	 booths = boothList.get(i); 
		zeroBooths =BigDecimal.ZERO;
		obAmount =	( NetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: fromDateTime , facilityId:booths.facilityId])).get("openingBalance");
		
		boothPaymentsMap =[:];
		//for getting reciepts
		boothsPaidDetail = NetworkServices.getBoothPaidPayments( dctx , [fromDate:fromDateTime ,thruDate:thruDateTime , facilityId:booths.facilityId]);
		reciepts = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(boothsPaidDetail)){
			reciepts = boothsPaidDetail.get("invoicesTotalAmount");
		}
		boothPaymentsMap["facilityId"] = booths.facilityId;	
		boothPaymentsMap["reciepts"] = ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["totRecpts"] += ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
		//for getting saleamount
		dayTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(booths.facilityId),fromDate:fromDateTime, thruDate:thruDateTime]);
		
		
		boothPaymentsMap["agentName"]=booths.get("facilityName");
		saleAmt= dayTotals.get("totalRevenue");
		
		
		
		boothPaymentsMap["salesAmt"] =((new BigDecimal(saleAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["totSaleAmt"] +=((new BigDecimal(saleAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
				
		boothPaymentsMap["openingAmt"] = ((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
		GrTotalMap["totOpeningAmt"] += ((new BigDecimal(obAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
		closingAmt = ((new BigDecimal((boothPaymentsMap["openingAmt"]+boothPaymentsMap["salesAmt"])-(boothPaymentsMap["reciepts"]))).setScale(2,BigDecimal.ROUND_HALF_UP));
		
		/*lets get the closing balance as next day opening balance.Since the advance payment is knocking at the time of trucksheet generation*/
		boothPaymentsMap["closingAmt"] = closingAmt;
		GrTotalMap["totClosing"] += ((new BigDecimal(boothPaymentsMap["closingAmt"])).setScale(2,BigDecimal.ROUND_HALF_UP));
		zeroBooths = (boothPaymentsMap["openingAmt"]+boothPaymentsMap["salesAmt"]+boothPaymentsMap["reciepts"]+boothPaymentsMap["closingAmt"]);
		boothDuesAbstractList.add(boothPaymentsMap);
		if(zeroBooths !=0){
			agentPaymentsReportMap[booths.facilityId]=[:];
			agentPaymentsReportMap[booths.facilityId].putAll(boothPaymentsMap);
		}
	
}
context.put("boothDuesAbstractList", boothDuesAbstractList);
	
