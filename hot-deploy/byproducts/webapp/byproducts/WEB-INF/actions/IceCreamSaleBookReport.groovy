import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
categoryType=parameters.categoryType;
partyId=parameters.partyId;
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(maxIntervalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}

partyIds=[];
if((categoryType.equals("ICE_CREAM_NANDINI")&& UtilValidate.isEmpty(partyId))||categoryType.equals("All")){
   nandiniPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "IC_WHOLESALE"]).get("partyIds");
   partyIds.addAll(nandiniPartyIds);
   Debug.log("partyId==="+partyIds);
}else if(categoryType.equals("ICE_CREAM_NANDINI")&& UtilValidate.isNotEmpty(partyId)){
     partyIds.addAll(partyId);
	 Debug.log("partyId==="+partyIds);
}
if((categoryType.equals("ICE_CREAM_AMUL")&& UtilValidate.isEmpty(partyId))||categoryType.equals("All")){
   amulPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "EXCLUSIVE_CUSTOMER"]).get("partyIds");
   partyIds.addAll(amulPartyIds);
}else if(categoryType.equals("ICE_CREAM_AMUL")&& UtilValidate.isNotEmpty(partyId)){
   partyIds.addAll(partyId);
}
if((categoryType.equals("UNITS")&& UtilValidate.isEmpty(partyId))||categoryType.equals("All")){
	unitPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNITS"]).get("partyIds");
	partyIds.addAll(unitPartyIds);
}else if(categoryType.equals("UNITS")&& UtilValidate.isNotEmpty(partyId)){
   partyIds.addAll(partyId);
}
if((categoryType.equals("UNION")&& UtilValidate.isEmpty(partyId))||categoryType.equals("All")){
	unionPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNION"]).get("partyIds");
	partyIds.addAll(unionPartyIds);
}else if(categoryType.equals("UNION")&& UtilValidate.isNotEmpty(partyId)){
	partyIds.addAll(partyId);
}
if((categoryType.equals("DEPOT_CUSTOMER")&& UtilValidate.isEmpty(partyId))||categoryType.equals("All")){
	depotPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "DEPOT_CUSTOMER"]).get("partyIds");
	partyIds.addAll(depotPartyIds);
}else if(categoryType.equals("DEPOT_CUSTOMER")&& UtilValidate.isNotEmpty(partyId)){
	partyIds.addAll(partyId);
}

partWiseSaleMap=[:];
if(UtilValidate.isNotEmpty(partyIds)){
	partyTaxMap = SalesInvoiceServices.getInvoiceSalesTaxItems(dctx, [partyIds:partyIds,fromDate:dayBegin, thruDate:dayEnd]).get("partyTaxMap");
	partyTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("partyTotals");
	partyTotals.each{ eachParty ->
		ppd=0;
		vatAdj=0;
		if(UtilValidate.isNotEmpty(partyTaxMap) && partyTaxMap.containsKey(eachParty.getKey())){
			 if(partyTaxMap.get(eachParty.getKey()).containsKey("PPD_PROMO_ADJ") ){
				 ppd=partyTaxMap.get(eachParty.getKey()).get("PPD_PROMO_ADJ");
				 vatAdj=partyTaxMap.get(eachParty.getKey()).get("VAT_SALE_ADJ");
			 }
		}
		quantity=0;
		basicRevenue=0;
		bedRevenue=0;
		vatRevenue=0;
		cstRevenue=0;
		total=0;
		totalMap=[:];
		quantity = eachParty.getValue().get("total");
		basicRevenue = eachParty.getValue().get("basicRevenue");
		bedRevenue = eachParty.getValue().get("bedRevenue");
		vatRevenue =eachParty.getValue().get("vatRevenue");
		cstRevenue = eachParty.getValue().get("cstRevenue");
		total = eachParty.getValue().get("totalRevenue");
		totalMap["quantity"]=quantity;
		if(maxIntervalDays>0){
		  totalMap["average"]=quantity/maxIntervalDays;
		}else{
		 totalMap["average"]=quantity;
		}
		totalMap["basicRevenue"]=basicRevenue;
		totalMap["bedRevenue"]=bedRevenue;
		totalMap["vatRevenue"]=vatRevenue+vatAdj;
		totalMap["cstRevenue"]=cstRevenue;
		totalMap["ppd"]=ppd;
		totalMap["total"]=total+ppd+vatAdj;
		if(quantity != 0){
			partWiseSaleMap.put(eachParty.getKey(), totalMap);
		}
	  //}
	 //}
	}
}
context.categoryType=categoryType;
context.partWiseSaleMap=partWiseSaleMap;
Debug.log("partWiseSaleMap==="+partWiseSaleMap);



