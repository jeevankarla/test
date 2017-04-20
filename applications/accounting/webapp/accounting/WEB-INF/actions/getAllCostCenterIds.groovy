import org.ofbiz.base.util.UtilDateTime;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;

import javolution.util.FastList;

import org.ofbiz.base.util.*;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javolution.util.FastMap;

import java.text.ParseException;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

costCenter=parameters.costCenterId;
partyFrom=parameters.partyIdFrom;
List costCenterList=[];
fromDateStr=parameters.fromDate;
thruDateStr=parameters.thruDate;
if(UtilValidate.isNotEmpty(costCenter)&&UtilValidate.isEmpty(fromDateStr)){
	context.errorMessage = "From Date Should not be empty :";
	return "error";
}
if(UtilValidate.isNotEmpty(costCenter)&&UtilValidate.isEmpty(thruDateStr)){
	context.errorMessage = "Thru Date Should not be empty";
	return "error";
}
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
Timestamp fromDateTs = null;
if(fromDateStr){
	try {
		fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
	} catch (ParseException e) {
	}
}
Timestamp thruDateTs = null;
if(thruDateStr){
	try {
		thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
	} catch (ParseException e) {
	}
}
fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);

if("All".equals(costCenter)){
	condList=[];
	if(UtilValidate.isNotEmpty(partyFrom)){
		condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,partyFrom));
	}
	condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
	condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	List roWiseBranchLists = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	List<String> branchList=EntityUtil.getFieldListFromEntityList(roWiseBranchLists, "partyIdTo", true)
	for(String branch in branchList){
		String tmp = branch.trim();
		costCenterList.add(tmp);
	}
	//costCenterList.addAll(branchList);
	costCenterList.addAll(partyFrom);
	
}else{
	costCenterList.add(costCenter);
}

parameters.costCenterList=costCenterList;
context.costCenterList=costCenterList;

condList1=[];
/*if(organizationPartyId){
	condList1.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS,"Company"));
}*/
if(parameters.glAccountId){
	condList1.add(EntityCondition.makeCondition("glAccountId" , EntityOperator.EQUALS,parameters.glAccountId));
}
if(parameters.glFiscalTypeId){
	condList1.add(EntityCondition.makeCondition("glFiscalTypeId" , EntityOperator.EQUALS,parameters.glFiscalTypeId));
}
if(parameters.paymentId){
	condList1.add(EntityCondition.makeCondition("paymentId" , EntityOperator.EQUALS,parameters.paymentId));
}
if(parameters.isPosted){
	condList1.add(EntityCondition.makeCondition("isPosted" , EntityOperator.EQUALS,parameters.isPosted));
}
if(costCenterList){
	condList1.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN,costCenterList));
}
if(parameters.partyId){
	condList1.add(EntityCondition.makeCondition("partyId" , EntityOperator.EQUALS,parameters.partyId));
}
if(parameters.invoiceId){
	condList1.add(EntityCondition.makeCondition("invoiceId" , EntityOperator.EQUALS,parameters.invoiceId));
}
if(parameters.productId){
	condList1.add(EntityCondition.makeCondition("productId" , EntityOperator.EQUALS,parameters.productId));
}
if(parameters.acctgTransId){
	condList1.add(EntityCondition.makeCondition("acctgTransId" , EntityOperator.EQUALS,parameters.acctgTransId));
}
if(parameters.workEffortId){
	condList1.add(EntityCondition.makeCondition("workEffortId" , EntityOperator.EQUALS,parameters.workEffortId));
}
if(parameters.shipmentId){
	condList1.add(EntityCondition.makeCondition("shipmentId" , EntityOperator.EQUALS,parameters.shipmentId));
}
if(fromDate){
		condList1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
if(thruDate){
	condList1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
}


EntityCondition testCond = EntityCondition.makeCondition(condList1,EntityOperator.AND);
Debug.log("testCond============"+testCond);

Set fieldToSelect =UtilMisc.toSet("glAccountId","acctgTransId","acctgTransEntrySeqId","transactionDate","acctgTransTypeId","glFiscalTypeId");
fieldToSelect.add("invoiceId");
fieldToSelect.add("paymentId");
fieldToSelect.add("workEffortId");
fieldToSelect.add("shipmentId");
fieldToSelect.add("costCenterId");
fieldToSelect.add("purposeTypeId");
fieldToSelect.add("partyId");
fieldToSelect.add("isPosted");
fieldToSelect.add("postedDate");
fieldToSelect.add("debitCreditFlag");
fieldToSelect.add("amount");
fieldToSelect.add("transDescription");
List acctgTransEntryList = delegator.findList("AcctgTransAndEntries", testCond, fieldToSelect, null, null, false);
Debug.log("acctgTransEntryList==========="+acctgTransEntryList.size());
context.acctgTransEntryList=acctgTransEntryList;
