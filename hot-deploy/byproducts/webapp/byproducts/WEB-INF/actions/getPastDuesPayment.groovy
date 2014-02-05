
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.text.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.regex.*;
import java.io.*;

invoiceList = [];
conditionList = [];
statusList = [];
invoiceIds = [];
myList = [];
myMap = [:];
paymentList = [];
paymentList = [];
dctx = dispatcher.getDispatchContext();
/*partyId = parameters.partyId;*/
facilityId = parameters.facilityId;
statusId = parameters.statusId;
partyId = "";
if(facilityId){
	facilityParty = delegator.findOne("Facility",["facilityId": facilityId], false);
	if(facilityParty){
		partyId = facilityParty.getAt("ownerPartyId");
	}
}
if(statusId=="PAID" && UtilValidate.isNotEmpty(partyId)){
	selectFields = ["paymentId", "partyIdFrom", "paymentDate","amount","paymentRefNum"] as Set;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS , "SALES_PAYIN"));
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PMNT_RECEIVED"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	payments = delegator.findList("Payment", condition, selectFields, ["-paymentDate"], null, false);
	payments.each{eachItem ->
		paymentMap = [:];
		paymentId = eachItem.getAt("paymentId");
		partyIdFrom = eachItem.getAt("partyIdFrom");
		paymentDate = eachItem.getAt("paymentDate");
		paymentRefNum = eachItem.getAt("paymentRefNum");
		amount = eachItem.getAt("amount");
		paymentMap.putAt("facilityId", facilityId);
		paymentMap.putAt("paymentId", paymentId);
		paymentMap.putAt("paymentRefNum", paymentRefNum);
		paymentMap.putAt("partyIdFrom", partyIdFrom);
		paymentMap.putAt("paymentDate", paymentDate);
		paymentMap.putAt("amount", amount);
		paymentList.add(paymentMap);
	}
	context.put("paymentList",paymentList);
}
Pattern escaper = Pattern.compile("([^0-9.])");
organizationPartyId = parameters.organizationPartyId;
if(UtilValidate.isNotEmpty(partyId) && statusId=="NOT_PAID"){
	statusList = ["INVOICE_CANCELLED","INVOICE_PAID","INVOICE_WRITEOFF"];
	fieldsToSelectIndItem = ["invoiceId","invoiceDate"] as Set;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , partyId));
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, organizationPartyId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN , statusList));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceList = delegator.findList("Invoice", condition, fieldsToSelectIndItem, null, null, false);
	invoiceIds = EntityUtil.getFieldListFromEntityList(invoiceList,"invoiceId",true);
	totalAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: invoiceIds, organizationPartyId: organizationPartyId, userLogin: userLogin]);
	
	//if (totalAmount && totalAmount.invoiceRunningTotal != "Rs0.00") {
		dues = escaper.matcher(totalAmount.invoiceRunningTotal).replaceAll("");
		PastDueInvoicestotalAmount = totalAmount.invoiceRunningTotal;
		myMap.putAt("totalDues", PastDueInvoicestotalAmount);
		myMap.putAt("duesPay", dues);
		myMap.putAt("partyId",partyId);
		myMap.putAt("facilityId", facilityId);
		myList.add(myMap);
		context.put("myList", myList);
	//}
	duesList = [];
	duesDayTotals = BigDecimal.ZERO;
	invoiceList.each{eachItem ->
		dueMap = [:];
		invoiceId = eachItem.getAt("invoiceId");
		invoiceIdList = [];
		invoiceIdList.add(invoiceId);
		invoiceDate = eachItem.getAt("invoiceDate");
		invoiceAmount = dispatcher.runSync("getInvoiceTotal", [invoiceId: invoiceId, userLogin: userLogin]);
		invoiceBalance = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: invoiceIdList, organizationPartyId: organizationPartyId, userLogin: userLogin]);
		if(invoiceAmount){
			dayDues = escaper.matcher(invoiceBalance.invoiceRunningTotal).replaceAll("");
			dayDues = new BigDecimal(dayDues);
			duesDayTotals = duesDayTotals.add(dayDues)
			invoiceTotal = invoiceAmount.amountTotal;
			dueMap.putAt("invoiceId", invoiceId);
			dueMap.putAt("invoiceDate", UtilDateTime.toDateString(invoiceDate));
			dueMap.putAt("invoiceTotal", duesDayTotals);
			duesList.add(dueMap);
		}
	}
	JSONObject partyDuesJSON = new JSONObject();
	partyDuesJSON.put(partyId, duesList);
	context.partyDuesJSON = partyDuesJSON;
}