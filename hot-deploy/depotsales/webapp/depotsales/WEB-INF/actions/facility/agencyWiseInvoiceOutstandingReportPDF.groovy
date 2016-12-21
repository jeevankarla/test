import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
 
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;
import org.ofbiz.accounting.invoice.InvoiceWorker;

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");

Timestamp fromDate;
Timestamp thruDate;

Timestamp fStMnthfromDate;
Timestamp fStMnththruDate;

Timestamp sndMnthfromDate;
Timestamp sndMnththruDate;

Timestamp thrdMnthfromDate;
Timestamp thrdMnththruDate;

Timestamp frthMnthfromDate;
Timestamp frthMnththruDate;


dateStr=parameters.AWIORDate;
reportType=parameters.reportType;

if(UtilValidate.isNotEmpty(dateStr)){
	thruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fromDate = UtilDateTime.addDaysToTimestamp(thruDate,-365);
	
	fStMnththruDate = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
	fStMnthfromDate = UtilDateTime.addDaysToTimestamp(fStMnththruDate,-30);
	
	sndMnththruDate = UtilDateTime.addDaysToTimestamp(fStMnthfromDate,-1);
	sndMnthfromDate = UtilDateTime.addDaysToTimestamp(sndMnththruDate,-30);
	
	thrdMnththruDate = UtilDateTime.addDaysToTimestamp(sndMnthfromDate,-1);
	thrdMnthfromDate = UtilDateTime.addDaysToTimestamp(thrdMnththruDate,-30);
	
	frthMnththruDate = UtilDateTime.addDaysToTimestamp(thrdMnthfromDate,-1);
	frthMnthfromDate = UtilDateTime.addDaysToTimestamp(frthMnththruDate,-30);

}

conditionList = [];

invoiceTypes = delegator.findList("InvoiceType", EntityCondition.makeCondition(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["PURCHASE_INVOICE","SALES_INVOICE"])),UtilMisc.toSet("invoiceTypeId","parentTypeId"), null, null, false);
purchaseinvoiceTypesList = EntityUtil.filterByCondition(invoiceTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
salesinvoiceTypesList = EntityUtil.filterByCondition(invoiceTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
purchaseinvoiceTypes=EntityUtil.getFieldListFromEntityList(purchaseinvoiceTypesList, "invoiceTypeId", true);
salesinvoiceTypes=EntityUtil.getFieldListFromEntityList(salesinvoiceTypesList, "invoiceTypeId", true);

conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_READY"));
if(reportType=="CREDITORS"){
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.IN, purchaseinvoiceTypes));
}else{
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.IN, salesinvoiceTypes));
}
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
invoiceAndItems = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("invoiceId","partyIdFrom","partyId","amount","quantity","invoiceDate"), null, null, false);
invoiceIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "invoiceId", true);
invoicePaymentsList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN,invoiceIds),UtilMisc.toSet("invoiceId","paymentId","amount","partyIdFrom","paymentDate","partyIdTo"), null, null, false);
if(reportType=="CREDITORS"){
	partyIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "partyIdFrom", true);
}else{
	partyIds=EntityUtil.getFieldListFromEntityList(invoiceAndItems, "partyId", true);
}
invocieList=[];
totalsMap=[:];
totfstMntInvTotals =0;
totsecMntInvTotals =0;
totthrdMntInvTotals =0;
totfrthMntInvTotals =0;
totabove180Days=0;
totallMonthsTotal=0
for(partyId in partyIds){
	tempMap=[:];
	fstMntInvTotals =0;
	secMntInvTotals =0;
	thrdMntInvTotals =0;
	frthMntInvTotals =0;
	above180Days=0;
	
	fstMntPaidTotals =0;
	secMntPaidTotals =0;
	thrdMntPaidTotals =0;
	frthMntPaidTotals =0;
	above180DaysPaid=0;
	
	allMonthsTotal=0
	if(reportType=="CREDITORS"){
		invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItems, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	}else{
		invoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	}
   
	firstMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
	secndMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
	thirdMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
	fourthMntInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
																							 EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
	above180DaysInvoiceAndItemsForParty = EntityUtil.filterByCondition(invoiceAndItemsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnthfromDate)));
	
	for(invoice in firstMntInvoiceAndItemsForParty){  
		if(UtilValidate.isNotEmpty(invoice)&& invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		fstMntInvTotals= fstMntInvTotals+total;
	}
	for(invoice in secndMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		secMntInvTotals= secMntInvTotals+total;
	}
	for(invoice in thirdMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		thrdMntInvTotals= thrdMntInvTotals+total;
	}
	for(invoice in fourthMntInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		frthMntInvTotals= frthMntInvTotals+total;
	}
	for(invoice in above180DaysInvoiceAndItemsForParty){
		if(UtilValidate.isNotEmpty(invoice) && invoice.quantity && invoice.amount){
			total = invoice.amount*invoice.quantity
		}else if(UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(invoice.amount)){
			total = invoice.amount
		}
		above180Days= above180Days+total;
	}
	invoiceItemsAndPaymentsForParty=[];
	if(reportType=="CREDITORS"){
		invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	}else{
		invoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoicePaymentsList, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	}
	
	firstMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fStMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,fStMnththruDate)));
	secndMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, sndMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,sndMnththruDate)));
	thirdMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, thrdMnthfromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thrdMnththruDate)));
	fourthMntInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, frthMnthfromDate) ,EntityOperator.AND,
		 																					EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnththruDate)));
 	above180DaysInvoiceItemsAndPaymentsForParty = EntityUtil.filterByCondition(invoiceItemsAndPaymentsForParty, EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND,
																							EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,frthMnthfromDate)));
	
	for(eachPayment in firstMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		fstMntPaidTotals=fstMntPaidTotals+total
	}
	
	for(eachPayment in secndMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		secMntPaidTotals=secMntPaidTotals+total
	}
	
	for(eachPayment in thirdMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		thrdMntPaidTotals=thrdMntPaidTotals+total
	}
	
	for(eachPayment in fourthMntInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		frthMntPaidTotals=frthMntPaidTotals+total
	}
	
	for(eachPayment in above180DaysInvoiceItemsAndPaymentsForParty){
		total = eachPayment.amount
		above180DaysPaid=above180DaysPaid+total
	}
	
	fstMntInvTotals =fstMntInvTotals-fstMntPaidTotals;
	secMntInvTotals =secMntInvTotals-secMntPaidTotals;
	thrdMntInvTotals =thrdMntInvTotals-thrdMntPaidTotals;
	frthMntInvTotals =frthMntInvTotals-frthMntPaidTotals;
	above180Days=above180Days-above180DaysPaid;

	allMonthsTotal=fstMntInvTotals+secMntInvTotals+thrdMntInvTotals+frthMntInvTotals+above180Days;
	String partyName = PartyHelper.getPartyName(delegator,partyId,false);
	tempMap.put("partyName", partyName+"["+partyId+"]");
	tempMap.put("fstMntInvTotals", fstMntInvTotals);
	tempMap.put("secMntInvTotals", secMntInvTotals);
	tempMap.put("thrdMntInvTotals", thrdMntInvTotals);
	tempMap.put("frthMntInvTotals", frthMntInvTotals);
	tempMap.put("above180Days", above180Days);
	tempMap.put("allMonthsTotal", allMonthsTotal);
	if(allMonthsTotal>0){
		invocieList.add(tempMap);
		
		totfstMntInvTotals =totfstMntInvTotals+fstMntInvTotals;
		totsecMntInvTotals =totsecMntInvTotals+secMntInvTotals;
		totthrdMntInvTotals =totthrdMntInvTotals+thrdMntInvTotals;
		totfrthMntInvTotals =totfrthMntInvTotals+frthMntInvTotals;
		totabove180Days=totabove180Days+above180Days;
		totallMonthsTotal=totallMonthsTotal+allMonthsTotal;
	}
	
}

totalsMap.put("partyName", "TOTAL");
totalsMap.put("fstMntInvTotals", totfstMntInvTotals);
totalsMap.put("secMntInvTotals", totsecMntInvTotals);
totalsMap.put("thrdMntInvTotals", totthrdMntInvTotals);
totalsMap.put("frthMntInvTotals", totfrthMntInvTotals);
totalsMap.put("above180Days", totabove180Days);
totalsMap.put("allMonthsTotal", totallMonthsTotal);
if(totallMonthsTotal>0){
	invocieList.add(totalsMap);
}

context.invocieList=invocieList;
context.reportType=reportType;







