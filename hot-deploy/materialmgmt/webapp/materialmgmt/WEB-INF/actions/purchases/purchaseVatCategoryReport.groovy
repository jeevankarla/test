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
reportTypeFlag = parameters.reportTypeFlag;
//Debug.log("reportTypeFlag==="+reportTypeFlag+"==fromDate="+fromDate);
dctx = dispatcher.getDispatchContext();
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
context.fromDate = dayBegin;
context.thruDate = dayEnd;
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
/*if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}*/
vatReturnMap=[:];
vatReturnList=[];
productIds=null;
conditionList =[];
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
InvoiceList = delegator.findList("InvoiceAndItem", condition, null,null, null, false);
invoiceIds = EntityUtil.getFieldListFromEntityList(InvoiceList, "invoiceId", true); 
BigDecimal totVatAmount=BigDecimal.ZERO;
if(UtilValidate.isNotEmpty(invoiceIds)){
	exprList=[];
	if(reportTypeFlag=="ELIGIBLE"){
		exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "ELIGIBLE"));
	}else{
		exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "INELIGIBLE"));
	}
	exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "VAT_CATEGORY"));
	exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	exprList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	             EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	productCatDetails = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
	if(UtilValidate.isNotEmpty(productCatDetails)){
	productIds = EntityUtil.getFieldListFromEntityList(productCatDetails, "productId", true);
	} 
if(UtilValidate.isNotEmpty(productIds)){
   if(UtilValidate.isNotEmpty(InvoiceList)){
	  sNo=0;
	  InvoiceList.each{eachInvoiceItem->
		invoiceProdMap=FastMap.newInstance();
		
		invoiceId=eachInvoiceItem.invoiceId;
		productId=eachInvoiceItem.productId;
		vatAmount=eachInvoiceItem.vatAmount;
		invoiceDate=eachInvoiceItem.invoiceDate;
		invoiceQty=eachInvoiceItem.quantity;
		if(!"Company".equals(eachInvoiceItem.partyIdFrom)){
			partyId=eachInvoiceItem.partyIdFrom	;
		}else{
		partyId=eachInvoiceItem.partyId	;
		}
		

		if(UtilValidate.isNotEmpty(productId) && productIds.contains(productId) && UtilValidate.isNotEmpty(vatAmount)){
			//totVatAmount=invoiceQty*vatAmount;
			totVatAmount=totVatAmount+vatAmount;
			invoiceProdMap.invoiceId=invoiceId;
			invoiceProdMap.productId=productId;
			invoiceProdMap.vatAmount=vatAmount;
			invoiceProdMap.invoiceDate=invoiceDate;
			if(UtilValidate.isNotEmpty(productId)){
			   partyName =  PartyHelper.getPartyName(delegator, partyId, false);
			   invoiceProdMap.partyId=partyId;
			   invoiceProdMap.partyName=partyName;
		    }
	       vatReturnList.addAll(invoiceProdMap);
			sNo=sNo+1;
			vatReturnMap.put(sNo, invoiceProdMap);
			
		}
		
	}
 }
}
}

context.vatReturnMap=vatReturnMap;
context.totVatAmount=totVatAmount;

// Purchase vat category Report for CSV
invoiceProdMap.partyName="TotalVatAmount";
invoiceProdMap.vatAmount=totVatAmount;
vatReturnList.addAll(invoiceProdMap);

context.vatReturnList=vatReturnList;

