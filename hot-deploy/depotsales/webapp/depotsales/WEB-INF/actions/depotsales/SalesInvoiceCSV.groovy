import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
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
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;




salesInvoiceCSVList = [];


invoiceListItr = invoices;
while ((eachInvoice = invoiceListItr.next()) != null) {
	
	
	invoiceDetailMap = [:];
	invoiceDetailMap.put("invoiceId","");
	invoiceDetailMap.put("invoiceTypeId","");
	invoiceDetailMap.put("invoiceDate","");
	invoiceDetailMap.put("statusId","");
	invoiceDetailMap.put("description","");
	
	invoiceDetailMap.put("partyIdFrom","");
	invoiceDetailMap.put("partyIdTo","");
	invoiceDetailMap.put("tallyRefNo","");
	/*invoiceDetailMap.put("referenceNumber","");*/
	invoiceDetailMap.put("quantity","");
	String invoiceId= "";
	String tallyRefNo = "";
	String orderId = "";
	invoiceId = eachInvoice.invoiceId;
	
	
	//tally ref num in sales invoice
	tallyRefNo = eachInvoice.referenceNumber;
	//checking for tally ref no in purchase invoice
	if(UtilValidate.isEmpty(tallyRefNo)){
		List orderAssoc = delegator.findByAnd("OrderAssoc", UtilMisc.toMap("toOrderId", orderId,"orderAssocTypeId","BackToBackOrder"));
		if(UtilValidate.isNotEmpty(orderAssoc)){
			String poOrderId = EntityUtil.getFirst(orderAssc).orderId;
			List orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId) , null, null, null, false );
			if(UtilValidate.isNotEmpty(orderItemBilling)){
				GenericValue orderItem = EntityUtil.getFirst(orderItemBilling);
				String purInvId = orderItem.invoiceId;
				GenericValue invoice =  delegator.findOne("Invoice", [invoiceId : purInvId], false);
				if(UtilValidate.isNotEmpty(invoice)){
					tallyRefNo = invoice.referenceNumber;
				}
			}
		}
	}
	//checking for tally ref no in order
	if(UtilValidate.isEmpty(tallyRefNo)){
		List orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId) , null, null, null, false );
		if(UtilValidate.isNotEmpty(orderItemBilling)){
			GenericValue orderItem = EntityUtil.getFirst(orderItemBilling);
			orderId = orderItem.orderId;
			GenericValue orderHeader = delegator.findOne("OrderHeader", ["orderId" : orderId], false);
			if(UtilValidate.isNotEmpty(orderHeader)){
				tallyRefNo = orderHeader.tallyRefNo;
			}
		}
	}
	
	
	
	invoiceDetailMap.put("invoiceId",invoiceId);
	invoiceDetailMap.put("invoiceTypeId",eachInvoice.invoiceTypeId);
	invoiceDetailMap.put("invoiceDate",eachInvoice.invoiceDate);
	invoiceDetailMap.put("statusId",eachInvoice.statusId);
	invoiceDetailMap.put("description",eachInvoice.description);
	invoiceDetailMap.put("partyIdFrom",eachInvoice.partyIdFrom);
	invoiceDetailMap.put("partyId",eachInvoice.partyId);
	invoiceDetailMap.put("tallyRefNo",tallyRefNo);
	//invoiceDetailMap.put("referenceNumber",eachInvoice.referenceNumber);
	quantity = 0;
	List invoiceItems = delegator.findByAnd("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId,"invoiceItemTypeId","INV_FPROD_ITEM"));
	qtyList = [];
	if(UtilValidate.isNotEmpty(invoiceItems)){
		qtyList = EntityUtil.getFieldListFromEntityList(invoiceItems, "quantity", false);
		for(int j=0 ;j<qtyList.size();j++){
			quantity = quantity + qtyList.get(j);
		}
	}
	invoiceDetailMap.put("quantity",quantity);
	salesInvoiceCSVList.add(invoiceDetailMap);
	
	
	
}
invoiceListItr.close();
context.salesInvoiceCSVList = salesInvoiceCSVList;
Debug.log("====================csvList size======="+salesInvoiceCSVList.size());

