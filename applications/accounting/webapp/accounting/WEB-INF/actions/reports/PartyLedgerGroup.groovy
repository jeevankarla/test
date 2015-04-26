import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.accounting.payment.PaymentWorker;
import in.vasista.vbiz.humanres.HumanresService;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.accounting.ledger.GeneralLedgerServices;
dctx = dispatcher.getDispatchContext();


fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
reportTypeFlag = parameters.reportTypeFlag;

SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
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
context.fromDate=fromDate;
context.thruDate=thruDate;

List conditionList = FastList.newInstance();
List condList = FastList.newInstance();
List acctgTransList = FastList.newInstance();
List acctgTransIds = FastList.newInstance();
List finalList = FastList.newInstance();
List acctgTransEntryList = FastList.newInstance();
List partyIds = FastList.newInstance();
List acctgPartyIds = FastList.newInstance();

/*if(UtilValidate.isNotEmpty(parameters.partyId)){
	partyIds=parameters.partyId;
}else{
	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(fromDate)));
	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
				                                         EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(thruDate))));
	condList.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.EQUALS,parameters.groupId));	
	EntityCondition cond=EntityCondition.makeCondition(condList,EntityOperator.AND);											 
	List partyClassification=delegator.findList("PartyClassification",cond,null,null,null,false);
	if(partyClassification){
		partyIds=EntityUtil.getFieldListFromEntityList(partyClassification, "partyId", false)
	}
}*/
/*if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,parameters.partyId));
}else{
   conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
}*/
conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

acctgTransList = delegator.findList("AcctgTrans",condition,null,null,null,false);
partyMap=[:];
openingBalMap=[:];
if(acctgTransList){
	acctgTransIds = EntityUtil.getFieldListFromEntityList(acctgTransList, "acctgTransId", false);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("acctgTransId",EntityOperator.IN,acctgTransIds));
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,parameters.partyId));
	}
	EntityCondition transEntryEcl=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	acctgTransEntryList = delegator.findList("AcctgTransEntry",transEntryEcl,null,null,null,false);
	acctgPartyIds = EntityUtil.getFieldListFromEntityList(acctgTransEntryList, "partyId", true);
	acctgPartyIds.each{partyId->
		Map acctgTransMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyId",partyId,"transactionDate",fromDate));
		openingBalMap[partyId]=acctgTransMap.get("openingBalance");
	}
	acctgTransList.each{acctgTrans->
		ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("acctgTransId",EntityOperator.EQUALS,acctgTrans.acctgTransId),
			                               EntityCondition.makeCondition("glAccountTypeId",EntityOperator.EQUALS,"ACCOUNTS_RECEIVABLE")],EntityOperator.AND)
		List transEntryList = EntityUtil.filterByCondition(acctgTransEntryList, ecl);
		if(transEntryList){
			transEntryList.each{transEntry->
				if(UtilValidate.isEmpty(partyMap[acctgTrans.partyId])){
					tempList=[];
					tempMap=[:];
					tempMap.acctgTransId=transEntry.acctgTransId;
					tempMap.acctgTransEntrySeqId=transEntry.acctgTransEntrySeqId;
					tempMap.transactionDate=acctgTrans.transactionDate;
					tempMap.acctgTransTypeId=acctgTrans.acctgTransTypeId;
					tempMap.description=transEntry.description;
					
					if(UtilValidate.isNotEmpty(transEntry.glAccountId)){
						tempMap.glAccountId=transEntry.glAccountId;
					
					glAccount=delegator.findOne("GlAccount",[glAccountId:transEntry.glAccountId],true);
					tempMap.glAccDescription=glAccount.description;
					}
					invoiceId=null;
					paymentId=null;
					if(UtilValidate.isNotEmpty(acctgTrans.invoiceId)){
						invoiceId=acctgTrans.invoiceId;
					}
					if(UtilValidate.isNotEmpty(acctgTrans.paymentId)){
						paymentId=acctgTrans.paymentId;
					}
					tempMap.paymentId=paymentId;		
					tempMap.invoiceId=invoiceId;
					tempMap.partyId=acctgTrans.partyId;
					partyName =  PartyHelper.getPartyName(delegator, acctgTrans.partyId, false);
					tempMap.name=partyName;
					tempMap.isPosted=acctgTrans.isPosted;
					tempMap.postedDate=acctgTrans.postedDate;	
					debit=0;credit=0;
					if(transEntry.debitCreditFlag=="C"){
						credit=transEntry.amount;
					}
					if(transEntry.debitCreditFlag=="D"){
						debit=transEntry.amount;
					}
					tempMap.debit=debit;
					tempMap.credit=credit;
					tempList.add(tempMap);
					partyMap[acctgTrans.partyId]=tempList;
				}else{
						tempMap=[:];
						tempMap.acctgTransId=transEntry.acctgTransId;
						tempMap.acctgTransEntrySeqId=transEntry.acctgTransEntrySeqId;
						tempMap.transactionDate=acctgTrans.transactionDate;
						tempMap.acctgTransTypeId=acctgTrans.acctgTransTypeId;
						tempMap.description=transEntry.description;
						
						if(UtilValidate.isNotEmpty(transEntry.glAccountId)){
							tempMap.glAccountId=transEntry.glAccountId;
						
						glAccount=delegator.findOne("GlAccount",[glAccountId:transEntry.glAccountId],true);
						tempMap.glAccDescription=glAccount.description;
						}
						invoiceId=null;
						paymentId=null;
						if(UtilValidate.isNotEmpty(acctgTrans.invoiceId)){
							invoiceId=acctgTrans.invoiceId;
						}
						if(UtilValidate.isNotEmpty(acctgTrans.paymentId)){
							paymentId=acctgTrans.paymentId;
						}
						tempMap.paymentId=paymentId;
						tempMap.invoiceId=invoiceId;
						tempMap.partyId=acctgTrans.partyId;
						partyName =  PartyHelper.getPartyName(delegator, acctgTrans.partyId, false);
						tempMap.name=partyName;
						tempMap.isPosted=acctgTrans.isPosted;
						tempMap.postedDate=acctgTrans.postedDate;
						debit=0;credit=0;
						if(transEntry.debitCreditFlag=="C"){
							credit=transEntry.amount;
						}
						if(transEntry.debitCreditFlag=="D"){
							debit=transEntry.amount;
						}
						tempMap.debit=debit;
						tempMap.credit=credit;
						tempList=partyMap[acctgTrans.partyId];
						tempList.add(tempMap);
						partyMap[acctgTrans.partyId]=tempList;
				}
			}
		
		}
	}
}
//Debug.log("openingBalMap============"+openingBalMap);
context.partyMap=partyMap;
context.openingBalMap=openingBalMap;
if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
	partyLedgerCsv=[];
	grdDebit=0;grdCredit=0;
	finalMap=[:];
	for(Map.Entry partyDetails : partyMap.entrySet()){
		partyId = partyDetails.getKey();
		partyVal = partyDetails.getValue();
		tempMap=[:];
		partyName=PartyHelper.getPartyName(delegator, partyId, false);
		name=partyName+"["+partyId+"]";
		tempMap.glAccountId="PARTY NAME :"
		tempMap.glAccDescription=name;
		openDebit=0;openCredit=0;
		openBal=0;
		openBal=openingBalMap.get(partyId);
		if(openBal>=0){
			openDebit=openBal;
		}else{
		   openCredit=-(openBal);
		}
		tempMap.isPosted="Opening Balance";
		tempMap.credit=openCredit;
		tempMap.debit=openDebit;
		partyLedgerCsv.add(tempMap);
		totDebit=0;totCredit=0;
		partyVal.each{acctgTrans->
			tempTransMap=[:];
			tempTransMap.acctgTransId=acctgTrans.acctgTransId;
			tempTransMap.acctgTransEntrySeqId=acctgTrans.acctgTransEntrySeqId;
			tempTransMap.transactionDate=acctgTrans.transactionDate;
			tempTransMap.acctgTransTypeId=acctgTrans.acctgTransTypeId;
			tempTransMap.description=acctgTrans.description;
			if(UtilValidate.isNotEmpty(acctgTrans.glAccountId)){
				tempTransMap.glAccountId=acctgTrans.glAccountId;
			
			glAccount=delegator.findOne("GlAccount",[glAccountId:acctgTrans.glAccountId],true);
			tempTransMap.glAccDescription=glAccount.description;
			}
			invoiceId=null;
			paymentId=null;
			if(UtilValidate.isNotEmpty(acctgTrans.invoiceId)){
				invoiceId=acctgTrans.invoiceId;
			}
			if(UtilValidate.isNotEmpty(acctgTrans.paymentId)){
				paymentId=acctgTrans.paymentId;
			}
			tempTransMap.invoiceId=invoiceId;
			tempTransMap.paymentId=paymentId;
			tempTransMap.isPosted=acctgTrans.isPosted;
			tempTransMap.postedDate=acctgTrans.postedDate;
			debit=0;credit=0;
			credit=acctgTrans.credit;
			debit=acctgTrans.debit;
			totDebit=totDebit+debit;
			totCredit=totCredit+credit;
			tempTransMap.credit=credit;
			tempTransMap.debit=debit;
			partyLedgerCsv.add(tempTransMap);
		}
		tempMap=[:];
		tempMap.glAccDescription="TRANSACTION TOTAL  :";
		tempMap.debit=totDebit;
		grdDebit=grdDebit+totDebit;
		tempMap.credit=totCredit;
		grdCredit=grdCredit+totCredit;
		partyLedgerCsv.add(tempMap);
		tempMap=[:];
		tempMap.glAccDescription="CLOSING TRANSACTION TOTAL :";
		bal=0;clsDebit=0;clsCredit=0;
		bal=grdDebit-grdCredit;
		if(bal>0){
			clsDebit=bal;
		}else{
			clsCredit=-(bal);
		}
		tempMap.debit=clsDebit;
		tempMap.credit=clsCredit;
		partyLedgerCsv.add(tempMap);
	}
	finalMap.glAccDescription="GRAND TOTAL :";
	finalMap.debit=grdDebit;
	finalMap.credit=grdCredit;
	partyLedgerCsv.add(finalMap);
	finalMap=[:];
	balance=0;clsGrdDebit=0;clsGrdCredit=0;
	finalMap.glAccDescription="CLOSING GRAND TOTAL :";
	balance=grdDebit-grdCredit;
	if(balance>0){
		clsGrdDebit=balance;
	}else{
		clsGrdCredit=balance;
	}
	finalMap.debit=clsGrdDebit;
	finalMap.credit=clsGrdCredit;
	partyLedgerCsv.add(finalMap);
	context.partyLedgerCsv=partyLedgerCsv;
}



