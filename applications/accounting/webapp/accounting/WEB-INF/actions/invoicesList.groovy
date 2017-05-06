import org.ofbiz.base.util.*;
 import org.ofbiz.entity.*;
 import org.ofbiz.entity.condition.EntityCondition;
 import org.ofbiz.entity.condition.EntityOperator;
 import net.sf.json.JSONObject;
 import net.sf.json.JSONArray;
 import org.ofbiz.entity.util.EntityUtil;
 
 statusId = [];
 invoiceTypeId = parameters.invoiceTypeId;
 invoiceId = parameters.invoiceId;
 partyIdFrom = parameters.partyIdFrom;
 statusId = parameters.statusId;
 invoiceDate = parameters.invoiceDate_fld0_value;
 dueDate = parameters.dueDate;
 description = parameters.description;
 partyId = parameters.partyId;
 prefPaymentMethodTypeId = parameters.prefPaymentMethodTypeId;
 conditionList = [];
 if(invoiceId)
 	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
 if(partyIdFrom)
 	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
 if(statusId){
 	if(statusId instanceof String)
 		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
 	else
 		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, statusId));
 }
 if(invoiceDate)
 	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.EQUALS, invoiceDate));
 if(parameters.dueDate_fld0_value)
 	conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.EQUALS, parameters.dueDate_fld0_value));
 if(parameters.dueDate_fld1_value)
 	conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.EQUALS, parameters.dueDate_fld1_value));
 if(description)
 	conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, description));
 if(partyId)
 	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
 if(prefPaymentMethodTypeId)
 	conditionList.add(EntityCondition.makeCondition("prefPaymentMethodTypeId", EntityOperator.EQUALS, prefPaymentMethodTypeId));
 invoices = 	[];
 if(conditionList){
 	if(invoiceTypeId)
 		conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId));
 	invoices = delegator.findList("InvoiceAndType", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);
 }
 context.invoiceList = invoices;