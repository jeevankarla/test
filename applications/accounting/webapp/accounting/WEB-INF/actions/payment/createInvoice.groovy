import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;
actionSubTabButtonValue=parameters.actionSubTabButtonValue;
paymentId=parameters.paymentId;
context.paymentId=paymentId;
partyIdFrom=parameters.partyIdTo;
partyId=parameters.partyIdFrom;
String invoiceId=null;
String finAccountId="";
if(UtilValidate.isNotEmpty(parameters.invoiceId)){
	invoiceId=parameters.invoiceId;
}
if(UtilValidate.isEmpty(partyIdFrom)){
	partyIdFrom = parameters.partyIdFrom;
	partyId = parameters.partyId;
}
if(UtilValidate.isNotEmpty(parameters.finAccountId)){
	finAccountId=parameters.finAccountId;
}
if((UtilValidate.isNotEmpty(actionSubTabButtonValue) && actionSubTabButtonValue!="updateInvoice") || UtilValidate.isEmpty(parameters.invoiceId)){
	Map<String, Object> createInvoiceContext = FastMap.newInstance();
	createInvoiceContext.put("partyId", partyId);
	createInvoiceContext.put("partyIdFrom", partyIdFrom);
	createInvoiceContext.put("invoiceDate", UtilDateTime.nowTimestamp());
	createInvoiceContext.put("dueDate", UtilDateTime.nowTimestamp());
	if((UtilValidate.isNotEmpty(screenflag)) && screenflag=="OutgoingAdvancesPaid"){
		createInvoiceContext.put("invoiceTypeId", "ADMIN_OUT");
	}
	if((UtilValidate.isNotEmpty(screenflag)) && screenflag=="IncomingAdvancesReceived"){
		createInvoiceContext.put("invoiceTypeId", "MIS_INCOME_IN");
	}
	//createInvoiceContext.put("referenceNumber", referenceNumber);
	//createInvoiceContext.put("description", description);
	createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
	createInvoiceContext.put("userLogin", userLogin);
	
	
	serviceResults = [:];
	try {
		serviceResults = dispatcher.runSync("createInvoice",createInvoiceContext);
		if(ServiceUtil.isError(resultCtx)){
			Debug.logError("Problem in Creating Invoice", module);
			return ServiceUtil.returnError("Problem in Creating Invoice");
		}
	} catch (Exception e) {
	}
	invoiceId = (String)serviceResults.get("invoiceId");
}
context.invoiceId=invoiceId;
context.invoiceDate=UtilDateTime.nowDateString("yyyy-MM-dd");
if((UtilValidate.isNotEmpty(screenflag)) && screenflag=="OutgoingAdvancesPaid"){
	context.invoiceTypeId="ADMIN_OUT";
	parameters.invoiceTypeId="ADMIN_OUT";
}
if((UtilValidate.isNotEmpty(screenflag)) && screenflag=="IncomingAdvancesReceived"){
	context.invoiceTypeId="MIS_INCOME_IN";
	parameters.invoiceTypeId="MIS_INCOME_IN";
}

parameters.invoiceId=invoiceId;
parameters.finAccountId=finAccountId;

