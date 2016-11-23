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

import javolution.util.FastList;
acctgTransId = "";
accountingTransEntryList = [];
accountingTransEntries = [:];

conditionList=[];
//finding on AcctgTrans for payment
if(UtilValidate.isNotEmpty(parameters.paymentId)){
	//for paymentId
	if(UtilValidate.isNotEmpty(parameters.paymentId)){
		conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS,parameters.paymentId));
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

if(UtilValidate.isNotEmpty(acctgTransList) && acctgTransList.size()>0){
	acctgTrans = acctgTransList[1];
	if(UtilValidate.isNotEmpty(acctgTrans)){
		payAcctgTransId = acctgTrans.acctgTransId
	}
}
GenericValue payFinAccntTransSequenceEntry;
if(UtilValidate.isNotEmpty(payAcctgTransId) && (payAcctgTransId != acctgTransId)){
	payAccountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : payAcctgTransId] , false);
	payFinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, payAccountingTransEntries.finAccountTransId), null, null, null, false));
}
payFinAccntTransSequence = "";
if(UtilValidate.isNotEmpty(payFinAccntTransSequenceEntry)){
	payFinAccntTransSequence = payFinAccntTransSequenceEntry.transSequenceNo;
}
context.payFinAccntTransSequence = payFinAccntTransSequence;
context.put("payAccountingTransEntries",payAccountingTransEntries);
if(UtilValidate.isNotEmpty(payAcctgTransId)){
	payAccountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , payAcctgTransId)  , null, null, null, false );
}
context.put("payAccountingTransEntryList",payAccountingTransEntryList);
