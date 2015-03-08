
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;

JSONArray jsonData= new JSONArray();

if (invoiceTypeId) {
	exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();
    expr = exprBldr.AND() {
        EQUALS(parentTypeId: invoiceTypeId)    
        LESS_THAN(dueDate: UtilDateTime.nowTimestamp())
    }
 
    invoiceStatusesCondition = exprBldr.IN(statusId: ["INVOICE_APPROVED", "INVOICE_READY"])

    expr = exprBldr.AND([expr, invoiceStatusesCondition]);
    context.PastDueInvoicestotalAmount = 0;
    pastDueInvoicesIter = delegator.find("InvoiceAndType", expr, null, null, ["dueDate DESC"], null);
    invoiceIds = [];
    i = 0;
	pastDueInvoicesIter.each{invoice->
        invoiceIds.add( invoice.invoiceId);
		JSONObject inv = new JSONObject();
		id = "id_" + i++;
		inv.put("id", id);
		inv.put("invoiceId", invoice.invoiceId);		
        invoiceType=delegator.findOne("InvoiceType",[invoiceTypeId : invoice.invoiceTypeId] , false);  
		inv.put("invoiceType", invoiceType.description);
        inv.put("party", PartyHelper.getPartyName(delegator, invoice.partyId, false)); 				
		inv.put("invoiceDate", UtilDateTime.toDateString(invoice.invoiceDate,"yyyy-MM-dd"));
		inv.put("dueDate", UtilDateTime.toDateString(invoice.dueDate,"yyyy-MM-dd"));
		inv.put("overDue", UtilDateTime.getIntervalInDays(invoice.dueDate,UtilDateTime.nowTimestamp()));		
        invoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("invoiceId", invoice.invoiceId, "userLogin", userLogin));
        invoicePaymentInfo = invoicePaymentInfoList.get("invoicePaymentInfoList").get(0);
		inv.put("total", UtilFormatOut.formatCurrency(invoicePaymentInfo.amount, invoice.currencyUomId, locale));
		inv.put("outstandingAmount", UtilFormatOut.formatCurrency(invoicePaymentInfo.outstandingAmount, invoice.currencyUomId, locale));		
		inv.put("outstandingAmountRaw", invoicePaymentInfo.outstandingAmount);		
		inv.put("currencySymbol", com.ibm.icu.util.Currency.getInstance(invoice.currencyUomId).getSymbol(locale));
		if( invoicePaymentInfo.outstandingAmount > 0) {
			jsonData.add(inv);
		}
    }
	pastDueInvoicesIter.close();
    
    totalAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: invoiceIds, organizationPartyId: organizationPartyId, userLogin: userLogin]);
    if (totalAmount) {
    	context.PastDueInvoicestotalAmount = totalAmount.invoiceRunningTotal;
    }
}

context.put("jsonData", jsonData.toString());
