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

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
userLogin= context.userLogin;
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
categoryType=parameters.categoryType;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;

def sdf = new SimpleDateFormat("dd-MMM-yyyy");
fdate="01-"+fromDate+" 00:00:00";
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fdate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: ", "");
}
tdate="01-"+thruDate+" 00:00:00";
try {
	thruDateTime = new java.sql.Timestamp(sdf.parse(tdate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: ", "");
}
fromDateTime = UtilDateTime.getMonthStart(fromDateTime);
thruDateTime = UtilDateTime.getMonthEnd(thruDateTime,TimeZone.getDefault(),Locale.getDefault());;
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);

partyIds=[];
if(categoryType.equals("ICE_CREAM_NANDINI")||categoryType.equals("All")){
   nandiniPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "IC_WHOLESALE"]).get("partyIds");
   partyIds.addAll(nandiniPartyIds);
}
if(categoryType.equals("ICE_CREAM_AMUL")||categoryType.equals("All")){
   amulPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "EXCLUSIVE_CUSTOMER"]).get("partyIds");
   partyIds.addAll(amulPartyIds);
}
if(categoryType.equals("UNITS")||categoryType.equals("All")){
	unitPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNITS"]).get("partyIds");
	partyIds.addAll(unitPartyIds);
}
if(categoryType.equals("UNION")||categoryType.equals("All")){
	unionPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNION"]).get("partyIds");
	partyIds.addAll(unionPartyIds);
}
if(categoryType.equals("DEPOT_CUSTOMER")||categoryType.equals("All")){
	depotPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "DEPOT_CUSTOMER"]).get("partyIds");
	partyIds.addAll(depotPartyIds);
}

facilityDesc = [:];
if(partyIds){
	partyIds.each{ eachParty ->
	    partyName=PartyHelper.getPartyName(delegator, eachParty, false);
		facilityDesc.putAt(eachParty, partyName);
	}
}

partyWiseLedger = [:];
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
monthList = [];
for(k = 1;k<=maxIntervalDays;k++){
	Timestamp currDay=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(dayBegin, k));
	Timestamp monthStart=UtilDateTime.getMonthStart(currDay);
	if(!monthList.contains(monthStart)){
		monthList.add(monthStart);
	}
}
if(partyIds){
	finalMap=[:];
	partyIds.each{eachParty ->
	detailList=[];
	totalBal=0;
	for(i=0;i<monthList.size();i++){
		dateMap=[:];
		Timestamp date=monthList.get(i);
		Timestamp monthBegin=UtilDateTime.getMonthStart(date);
		Timestamp monthEnd=UtilDateTime.getMonthEnd(monthBegin,TimeZone.getDefault(),Locale.getDefault());
		daywiseReceipts = ByProductNetworkServices.getPartyPaymentDetails(dctx, UtilMisc.toMap("fromDate",monthBegin,"thruDate" ,monthEnd,"partyIdsList", [eachParty])).get("partyPaidMap");
		partyTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:[eachParty], isQuantityLtrs:true,fromDate:monthBegin, thruDate:monthEnd]).get("partyTotals");
		openingBalance = (ByProductNetworkServices.getOpeningBalanceForParty( dctx , [userLogin: userLogin, saleDate: monthBegin, partyId:eachParty])).get("openingBalance");
		
		partyReturnTotals = ByProductNetworkServices.getPartyWiseReturnTotal(dctx, UtilMisc.toMap("fromDate",monthBegin,"thruDate" ,monthEnd,"partyIdsList", [eachParty])).get("partyReturnTotals");
		monthDetailMap=[:];
		
				if(UtilValidate.isNotEmpty(daywiseReceipts)){
				      amount = daywiseReceipts.get(eachParty);
				}else{
				      amount = 0;
				}
		        if(UtilValidate.isNotEmpty(partyTotals)){
				     saleAmount=partyTotals.get(eachParty).get("totalRevenue");
		        }else{
			         saleAmount=0;
		        }
			    if(UtilValidate.isNotEmpty(partyReturnTotals)){
				     returnAmount=partyReturnTotals.get(eachParty).get("totalAmount");
				     saleAmount=saleAmount-returnAmount;
				}else{
				     returnAmount=0;
				}
				closingBalance=openingBalance-amount+saleAmount;
				totalBal=totalBal+openingBalance+saleAmount+amount;
				
				if(UtilValidate.isEmpty(dateMap[monthBegin])){
					dateMap.put("openingBalance", openingBalance);
					dateMap.put("debitAmount", saleAmount);
					dateMap.put("creditAmount", amount);
					dateMap.put("closingBalance", closingBalance);
					
					tempMap = [:];
					tempMap.putAll(dateMap);
				    monthDetailMap.put(monthBegin, tempMap);
					detailList.addAll(monthDetailMap);
				}
			}
			if(totalBal!=0){
			        finalMap.putAt(eachParty, detailList);
			}
		}
	context.put("partyWiseLedger", finalMap);
}