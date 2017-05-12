
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.util.EntityUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

statusId = [];
invoiceTypeId = parameters.invoiceTypeId;
invoiceId = parameters.invoiceId;
partyIdFrom = parameters.partyIdFrom;
statusId = parameters.statusId;
invoiceDate = parameters.invoiceDate_fld0_value;
invoiceDate1 = parameters.invoiceDate_fld1_value;
description = parameters.description;
partyId = parameters.partyId;
invoiceDate_fld0_op = parameters.invoiceDate_fld0_op;
conditionList = [];
sdf = new SimpleDateFormat("yyyy-MM-dd");
invoiceDateTime = null;
invoiceDate1Time = null;
try {
	if (invoiceDate) {
		invoiceDateTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(invoiceDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}


try {
	if (invoiceDate1) {
		invoiceDate1Time = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(invoiceDate1).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
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
if(invoiceDate_fld0_op){
	if(invoiceDate_fld0_op.equals("greaterThanFromDayStart") || invoiceDate_fld0_op.equals("greaterThan")){
		if(invoiceDateTime)
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, invoiceDateTime));
		if(invoiceDate1Time)
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, invoiceDate1Time));
	}else{
	if(invoiceDateTime)
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.EQUALS, invoiceDateTime));
	}
}
if(partyId)
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
if(invoiceTypeId)
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId));
def orderBy = UtilMisc.toList("-invoiceDate");
invoices = delegator.find("InvoiceAndType", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, orderBy, null);
context.invoiceList = invoices;