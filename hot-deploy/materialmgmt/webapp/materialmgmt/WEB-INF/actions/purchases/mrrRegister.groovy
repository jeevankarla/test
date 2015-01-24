import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
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
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

fromDate=parameters.fromDateMr;
thruDate=parameters.thruDateMr;
productId=parameters.productId;

fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

condList =[];
condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
shipmentReceiptList = delegator.findList("ShipmentReceipt", cond, null,null, null, false);
shipmentMap=[:];
shipmentMap["totalInvoiceAmt"]=BigDecimal.ZERO;
shipmentMap["totalPaidAmt"]=BigDecimal.ZERO;

mrrList=[];
shipmentReceiptList.each{shipmentData->
   shipmentDetailMap=[:];
   shipmentDetailMap["receiptId"]=shipmentData.receiptId;
   shipmentDetailMap["datetimeReceived"]=shipmentData.datetimeReceived;
   shipmentDetailMap["shipmentId"]=shipmentData.shipmentId;

      cList =[];
   cList.add(EntityCondition.makeCondition("statusId",  EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITOFF")));
   cList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentData.shipmentId));
    con = EntityCondition.makeCondition(cList,EntityOperator.AND);
   invoiceDetails = delegator.findList("Invoice", con, null,null, null, false);
   if(UtilValidate.isNotEmpty(invoiceDetails)){
	   invoiceDetails=EntityUtil.getFirst(invoiceDetails);
	   
   shipmentDetailMap["invoiceId"]=invoiceDetails.invoiceId;
   shipmentDetailMap["invoiceDate"]=invoiceDetails.invoiceDate;
   shipmentDetailMap["dueDate"]=invoiceDetails.dueDate;
   
   invoiceId=invoiceDetails.invoiceId;
   invoiceAmount= InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
   if(UtilValidate.isNotEmpty(invoiceAmount)){
	   shipmentDetailMap.put("invoiceAmount",invoiceAmount);
	   shipmentMap["totalInvoiceAmt"]+=invoiceAmount;
	   }
   invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : invoiceId]);     
   if(UtilValidate.isNotEmpty(invoice)){
    invoiceToApply = InvoiceWorker.getInvoiceNotApplied(invoice);
	if(UtilValidate.isNotEmpty(invoiceToApply)){
		shipmentDetailMap.put("invoiceToApply",invoiceToApply);
		}
   }
   if(UtilValidate.isNotEmpty(invoiceAmount)){
	   if(UtilValidate.isNotEmpty(invoiceToApply)){
		   paidAmount=invoiceAmount-invoiceToApply;
		   shipmentDetailMap.put("paidAmount",paidAmount);
		   shipmentMap["totalPaidAmt"]+=paidAmount;
		   
	   }
   }
   
   //get PartyId from Role for vendor and dept
   vendorDeptDetails = delegator.findList("OrderRole",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , shipmentData.orderId)  , null, null, null, false );
   
   vendorDetails = EntityUtil.filterByCondition(vendorDeptDetails, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
   vendorDetails=EntityUtil.getFirst(vendorDetails);
   if(UtilValidate.isNotEmpty(vendorDetails.partyId)){
    partyId=vendorDetails.partyId;
    shipmentDetailMap.put("partyId",partyId);
    partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	shipmentDetailMap.put("partyName",partyName);
     }
   deptDetails = EntityUtil.filterByCondition(vendorDeptDetails, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ISSUE_TO_DEPT"));
   deptDetails=EntityUtil.getFirst(deptDetails);
   if(UtilValidate.isNotEmpty(deptDetails)){
   if(UtilValidate.isNotEmpty(deptDetails.partyId)){
    partyId=deptDetails.partyId;
    shipmentDetailMap.put("partyId",partyId);
    partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	shipmentDetailMap.put("deptName",deptName);
      }}
   mrrList.addAll(shipmentDetailMap);
   }
}
context.shipmentMap=shipmentMap;
context.mrrList=mrrList;


