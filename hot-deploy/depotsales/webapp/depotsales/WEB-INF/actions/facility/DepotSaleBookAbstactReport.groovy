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
partyDetailsList=[];

if(UtilValidate.isEmpty(partyId)){
parentRoleTypeId="CUSTOMER_TRADE_TYPE";
if(UtilValidate.isNotEmpty(parameters.parentRoleTypeId)){
	parentRoleTypeId=parameters.parentRoleTypeId;
}
if(UtilValidate.isNotEmpty(parentRoleTypeId) && UtilValidate.isEmpty(parameters.roleTypeId) ){//to handle parentRoleTypeIds only when roleTypeId is empty
	roleTypeList = delegator.findByAnd("RoleType",["parentTypeId" :parentRoleTypeId]);
	roleTypeList.each{roleType->
		roleTypeId =roleType.roleTypeId;
		inputMap = [:];
		inputMap.put("userLogin", userLogin);
		inputMap.put("roleTypeId", roleTypeId);
		if(UtilValidate.isNotEmpty(parameters.partyStatusId)){
				inputMap.put("statusId", parameters.partyStatusId);
		}
		Map tempPartyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx, inputMap);
		if(UtilValidate.isNotEmpty(tempPartyDetailsMap)){
			tempPartyDetailsList = tempPartyDetailsMap.get("partyDetails");
			partyDetailsList.addAll(tempPartyDetailsList);
			}
		}
	}
partyIds.addAll(EntityUtil.getFieldListFromEntityList(partyDetailsList, "partyId", true));
}
else{
	partyIds.add(partyId);
}

partWiseSaleMap=[:];
if(UtilValidate.isNotEmpty(partyIds)){
	partyTaxMap = SalesInvoiceServices.getInvoiceSalesTaxItems(dctx, [partyIds:partyIds,fromDate:dayBegin, thruDate:dayEnd]).get("partyTaxMap");
	partyTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:partyIds,isShipToParty:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("partyTotals");
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
context.partWiseSaleMap=partWiseSaleMap;
Debug.log("partWiseSaleMap==="+partWiseSaleMap);



