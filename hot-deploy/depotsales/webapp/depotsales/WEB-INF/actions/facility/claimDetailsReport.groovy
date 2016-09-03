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

claimFromDate=parameters.claimFromDate;
claimThruDate=parameters.claimThruDate;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
finalList = [];
def sdf = new SimpleDateFormat("yyyy, MMM dd");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(claimFromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(claimThruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
fromDateTime = UtilDateTime.getDayStart(fromDateTime);
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
finalList = [];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
invoices = delegator.find("Invoice",condition,null,UtilMisc.toSet("invoiceId"),null,null);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoices, "invoiceId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
InvoiceItem = delegator.findList("InvoiceItem",condition, null, null, null, false );
if(UtilValidate.isNotEmpty(InvoiceItem)){
	for(i=0; i<InvoiceItem.size(); i++){
		
		 quantity =0;
		 temMap = [:];
		 invoiceDate = "";
		 productName = "";
		 subsidyAmt = 0;
		 categoryname= "";
		 sNo = i+1;
		 temMap.put("sNo", sNo);
		 eachInvoiceItem = InvoiceItem[i];
		 invoiceItemTypeId=eachInvoiceItem.get("invoiceItemTypeId");
		 invoiceId = eachInvoiceItem.get("invoiceId");
		 invoiceItemSeqId = eachInvoiceItem.get("invoiceItemSeqId");
		 if(invoiceItemTypeId.equals("INV_FPROD_ITEM")){
			 invoice = delegator.findOne("Invoice",["invoiceId":eachInvoiceItem.get("invoiceId")],false);
			 invoiceDate = UtilDateTime.toDateString(invoice.invoiceDate,"dd/MM/yyyy");
			 temMap.put("invoiceDate", invoiceDate);
			 partyId = invoice.partyId;
			 userAgency = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
			 temMap.put("userAgency", userAgency);
			 city = "";
			 partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
			 if(UtilValidate.isNotEmpty(partyPostalAddress)){
				 if(UtilValidate.isNotEmpty(partyPostalAddress.city)){
					 city=partyPostalAddress.city;
				 }
			 }
			 temMap.put("city", city);
			 productId = eachInvoiceItem.get("productId");
			 productDetails = delegator.findOne("Product",["productId":productId],false);
			 if(UtilValidate.isNotEmpty(productDetails)){
				 productName=productDetails.description;
			 }
			 temMap.put("productName", productName);
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId",EntityOperator.NOT_EQUAL,null));
			 conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 productCategoryAndMember = delegator.findList("ProductCategoryAndMember",condition, null, null, null, false );
			 productCategoryAndMember= EntityUtil.getFirst(productCategoryAndMember);
			 if(UtilValidate.isNotEmpty(productCategoryAndMember) && (productCategoryAndMember.description)){
				 categoryname= productCategoryAndMember.description;
			 }
			 temMap.put("categoryname", categoryname);
			 quantity = eachInvoiceItem.get("quantity");
			 temMap.put("quantity", quantity);
			 amount = eachInvoiceItem.get("amount");
			 value= quantity*amount;
			 temMap.put("value", value);
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceId",EntityOperator.EQUALS,invoiceId));
			 conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
			 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			 condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 invoiceSubsidyDetails = delegator.findList("InvoiceItem",condition, null, null, null, false );
			 invoiceSubsidyDetails= EntityUtil.getFirst(invoiceSubsidyDetails);
			 if(UtilValidate.isNotEmpty(invoiceSubsidyDetails) && (invoiceSubsidyDetails.amount)){
				 subsidyAmt= (invoiceSubsidyDetails.amount)*(-1);
			 }
			 temMap.put("subsidyAmt", subsidyAmt);
			 serviceCharg= subsidyAmt*0.05;
			 temMap.put("serviceCharg", serviceCharg);
			 claimTotal = subsidyAmt +serviceCharg;
			 temMap.put("claimTotal", claimTotal);
			 finalList.add(temMap);
		 }
		
	}
}
context.finalList = finalList;

