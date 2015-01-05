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
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(maxIntervalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}

partyIds=[];
if(categoryType.equals("ICE_CREAM_NANDINI")){
   nandiniPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "IC_WHOLESALE"]).get("partyIds");
   partyIds.addAll(nandiniPartyIds);
}
if(categoryType.equals("ICE_CREAM_AMUL")){
   amulPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "EXCLUSIVE_CUSTOMER"]).get("partyIds");
   partyIds.addAll(amulPartyIds);
}

reportTypeFlag = parameters.reportTypeFlag;
taxType = parameters.taxType;

partyWiseVatSaleMap=[:];
partyWiseCstSaleMap=[:];
invoiceList=[];
if(UtilValidate.isNotEmpty(partyIds)){
	invoiceTaxMap = SalesInvoiceServices.getInvoiceSalesTaxItems(dctx, [partyIds:partyIds,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceTaxMap");
	invoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceIdTotals");
	invoiceTotals.each{ eachInvoice ->
	if(UtilValidate.isNotEmpty(invoiceTaxMap) && invoiceTaxMap.containsKey(eachInvoice.getKey())){
		invoiceId=eachInvoice.getKey();
		invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
		invoicePartyId = invoiceDetails.partyId;
		partyPostalAddress = delegator.findByAnd("PartyAndPostalAddress", [partyId: invoicePartyId]);
		address = EntityUtil.getFirst(EntityUtil.filterByDate(partyPostalAddress));
		if(address){
			// get state
			state= address.stateProvinceGeoId
		 }
		invoiceDate=invoiceDetails.invoiceDate;
		invoiceDateStr=UtilDateTime.toDateString(invoiceDate ,"dd/MM/yyyy");
		partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : invoicePartyId, partyIdentificationTypeId : "TIN_NUMBER"], false);
		if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
			idValue = partyIdentificationDetails.idValue;
		}
		if(UtilValidate.isNotEmpty(invoicePartyId)){
			partyName = PartyHelper.getPartyName(delegator, invoicePartyId, false);
		}
		invoiceSequenceList = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId) , null, null, null, false);
		if(UtilValidate.isNotEmpty(invoiceSequenceList)){
			invoiceSequence = EntityUtil.getFirst(invoiceSequenceList);
			if(UtilValidate.isNotEmpty(invoiceSequence)){
				invoiceSequenceId = invoiceSequence.sequenceId;
			}
		}
	 if(UtilValidate.isNotEmpty(taxType)&& taxType.equals("VAT")&&invoiceTaxMap.get(eachInvoice.getKey()).containsKey("VAT_SALE") ){
		quantity=0;
		basicRevenue=0;
		bedRevenue=0;
		vatRevenue=0;
		cstRevenue=0;
		total=0;
		totalMap=[:];
		quantity = eachInvoice.getValue().get("total");
		vatRevenue =eachInvoice.getValue().get("vatRevenue");
		total = eachInvoice.getValue().get("totalRevenue");
		totalMap["invoice"]=eachInvoice.getKey();
		totalMap["invoiceSequenceId"]=invoiceSequenceId;
		totalMap["invoiceDate"]=invoiceDateStr;
		//for shipping to party details
		invoice = delegator.findOne("Invoice", [invoiceId : eachInvoice.getKey()], false);
		shippingtemp=InvoiceWorker.getInvoiceShippingParty(invoice);
		totalMap["partyName"]=shippingtemp.partyName;
		totalMap["partyId"]=shippingtemp.partyId;
		//Debug.log("shippingtemp.partyName========================"+shippingtemp.partyId);
		totalMap["state"]=state;
		totalMap["idValue"]=idValue;
		totalMap["quantity"]=quantity;
		totalMap["basicValue"]=total-vatRevenue;
		totalMap["tax"]=vatRevenue;
		totalMap["total"]=total;
		if(quantity != 0){
			partyWiseVatSaleMap.put(eachInvoice.getKey(), totalMap);
		}
		invoiceList.add(totalMap);
	  }
	 if(UtilValidate.isNotEmpty(taxType)&& taxType.equals("CST")&& invoiceTaxMap.get(eachInvoice.getKey()).containsKey("CST_SALE") ){
		 quantity=0;
		 basicRevenue=0;
		 bedRevenue=0;
		 vatRevenue=0;
		 cstRevenue=0;
		 total=0;
		 totalMap=[:];
		 quantity = eachInvoice.getValue().get("total");
		 cstRevenue = eachInvoice.getValue().get("cstRevenue");
		 total = eachInvoice.getValue().get("totalRevenue");
		 totalMap["invoice"]=eachInvoice.getKey();
		 totalMap["invoiceSequenceId"]=invoiceSequenceId;
		 totalMap["invoiceDate"]=invoiceDateStr;
		 //for shipping to party details
		 invoice = delegator.findOne("Invoice", [invoiceId : eachInvoice.getKey()], false);
		 shippingtemp=InvoiceWorker.getInvoiceShippingParty(invoice);
		 totalMap["partyName"]=shippingtemp.partyName;
		 totalMap["partyId"]=shippingtemp.partyId;
		 totalMap["state"]=state;
		 totalMap["idValue"]=idValue;
		 totalMap["quantity"]=quantity;
		 totalMap["basicValue"]=total-cstRevenue;
		 totalMap["tax"]=cstRevenue;
		 totalMap["total"]=total;
		 if(quantity != 0){
			 partyWiseCstSaleMap.put(eachInvoice.getKey(), totalMap);
		 }
		 invoiceList.add(totalMap);
	   }
	 }
	}
}
context.invoiceList=invoiceList;

