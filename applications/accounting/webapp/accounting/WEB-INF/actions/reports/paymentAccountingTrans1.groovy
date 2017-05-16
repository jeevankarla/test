import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.base.util.*
import org.ofbiz.minilang.SimpleMapProcessor
import org.ofbiz.content.ContentManagementWorker
import org.ofbiz.content.content.ContentWorker
import org.ofbiz.content.data.DataResourceWorker
import java.sql.Date;
import java.sql.Timestamp;
import org.ofbiz.accounting.util.UtilAccounting;

import javolution.util.FastList;

acountingTransEntriesMap=[:];
if(UtilValidate.isNotEmpty(paymentIds)){
	paymentIds1=paymentIds;
}
Debug.log("paymentIds===="+paymentIds1);
for(paymentId in paymentIds1){
	acctgTransId = "";
	accountingTransEntryList = [];
	accountingTransEntries = [:];

conditionList=[];
//finding on AcctgTrans for payment
if(UtilValidate.isNotEmpty(paymentId)){
	//for paymentId
	if(UtilValidate.isNotEmpty(paymentId)){
		conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS,paymentId));
	}
	conditionAcctgTrans = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	//finding on AcctgTrans
	acctgTransList = delegator.findList("AcctgTrans",conditionAcctgTrans , null, null, null, false );
	if(UtilValidate.isNotEmpty(acctgTransList)){
		acctgTrans = acctgTransList[0];
		if(UtilValidate.isNotEmpty(acctgTrans)){
			acctgTransId = acctgTrans.acctgTransId
		}
	}
}
partyIdForAdd="";
Debug.log("paymentIds===="+parameters.invoiceId);
if(UtilValidate.isNotEmpty(parameters.invoiceId)){
invoice = delegator.findOne("Invoice",[invoiceId : parameters.invoiceId] , false);
if(UtilValidate.isNotEmpty(invoice)){
	if(invoice.invoiceTypeId=="ADMIN_OUT" || invoice.invoiceTypeId=="PURCHASE_INVOICE"){
		partyIdForAdd=invoice.partyId;
		
	}
	else if(invoice.invoiceTypeId=="MIS_INCOME_IN" || invoice.invoiceTypeId=="SALES_INVOICE"){
		partyIdForAdd=invoice.partyIdFrom;
		
	}
}
}

newList = [];
FinAccountTransList = [];
newList = delegator.findList("FinAccountTrans",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS , accountingTransEntries.finAccountTransId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(newList)){
	FinAccountTransList = EntityUtil.getFirst(newList);
}
context.put("FinAccountTransList",FinAccountTransList);


Debug.log("paymentId======"+paymentId);
paymentDetails = delegator.findOne("Payment",[paymentId : paymentId] , false);
if (UtilAccounting.isReceipt(paymentDetails)) {
	partyIdForAdd=paymentDetails.partyIdTo;
	context.partyIdForAdd=partyIdForAdd;
}else{
	partyIdForAdd=paymentDetails.partyIdFrom;
	context.partyIdForAdd=partyIdForAdd;
}

GenericValue finAccntTransSequenceEntry;
if(UtilValidate.isNotEmpty(acctgTransId)){
	accountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : acctgTransId] , false);
	finAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, accountingTransEntries.finAccountTransId), null, null, null, false));
}
finAccntTransSequence = "";
if(UtilValidate.isNotEmpty(finAccntTransSequenceEntry)){
	finAccntTransSequence = finAccntTransSequenceEntry.transSequenceNo;
}
context.finAccntTransSequence = finAccntTransSequence;
context.put("accountingTransEntries",accountingTransEntries);

if(UtilValidate.isNotEmpty(acctgTransId)){
	accountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , acctgTransId)  , null, null, null, false );
}
context.put("accountingTransEntryList",accountingTransEntryList);

//for Deposit
payAcctgTransId = "";
payAccountingTransEntryList = [];
payAccountingTransEntries = [:];
conditionList.clear();
//finding on AcctgTrans for payment

entryList=[];
finalMap=[:];
if(UtilValidate.isNotEmpty(acctgTransList) && acctgTransList.size()>0){
	for(int i=1 ; i < (acctgTransList.size()); i++){
	acctgTrans = acctgTransList[i];
	transType = "";
	if(UtilValidate.isNotEmpty(acctgTrans)){
		payAcctgTransId = acctgTrans.acctgTransId
		GenericValue payFinAccntTransSequenceEntry;
		if(UtilValidate.isNotEmpty(payAcctgTransId) && (payAcctgTransId != acctgTransId)){
			payAccountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : payAcctgTransId] , false);
			payFinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, payAccountingTransEntries.finAccountTransId), null, null, null, false));
			transType = payAccountingTransEntries.acctgTransTypeId;
			}
		entryList.addAll(payAccountingTransEntries);
		payFinAccntTransSequence = "";
		if(UtilValidate.isNotEmpty(payFinAccntTransSequenceEntry)){
			payFinAccntTransSequence = payFinAccntTransSequenceEntry.transSequenceNo;
		}
		context.payFinAccntTransSequence = payFinAccntTransSequence;
		context.put("payAccountingTransEntries",payAccountingTransEntries);
		
		if(UtilValidate.isNotEmpty(payAcctgTransId)){
			payAccountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , payAcctgTransId)  , null, null, null, false );
			}
			finalMap.put(transType,payAccountingTransEntryList);
		}
	}
}
//for display of sequence and Bank Name in AccountingReport in Payment overview
finAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, paymentDetails.finAccountTransId), null, null, null, false));
finAccountId="";
finAccount="";
BankName="";
if(finAccntTransSequenceEntry){
	finAccntTransSequence = finAccntTransSequenceEntry.transSequenceId;
	finAccountId=finAccntTransSequenceEntry.finAccountId;
	if(finAccountId){
			finAccount=delegator.findOne("FinAccount",[finAccountId : finAccountId] , false);
				if(finAccount.finAccountName){
					BankName=finAccount.finAccountName;
				}	
	}
}

context.finAccntTransSequence = finAccntTransSequence;
context.BankName=BankName;
context.put("finalMap",finalMap);
context.put("entryList",entryList);
acountingTransEntriesMap(paymentId,)
}
