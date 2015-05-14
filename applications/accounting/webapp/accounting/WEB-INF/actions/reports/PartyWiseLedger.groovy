import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
//List acctgTransList = FastList.newInstance();
List acctgTransIds = FastList.newInstance();
List finalList = FastList.newInstance();
List acctgTransEntryList = FastList.newInstance();
List partyIds = FastList.newInstance();
List acctgPartyIds = FastList.newInstance();
List opeinigBalPartyId= FastList.newInstance();
EntityListIterator acctgTransList=null;
EntityListIterator acctgTransIter=null;
List nonRolePartyIds=FastList.newInstance();
List rolePartyIds=FastList.newInstance();
if(UtilValidate.isNotEmpty(parameters.roleTypeId)){
	if(parameters.roleTypeId=="NONROLE"){
		List roleTypeList=FastList.newInstance();
		List roleTypeAttr=delegator.findList("RoleTypeAttr",EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"ACCOUNTING_ROLE"),null,null,null,false);
		roleTypeList=EntityUtil.getFieldListFromEntityList(roleTypeAttr, "roleTypeId", true);
		List partyRoles=delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,roleTypeList),null,null,null,false);
		rolePartyIds= EntityUtil.getFieldListFromEntityList(partyRoles, "partyId", true);
	}else{
		List partyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,parameters.roleTypeId),null,null,null,false);
		partyIds = EntityUtil.getFieldListFromEntityList(partyRole, "partyId", true);
	}
}

if(UtilValidate.isNotEmpty(parameters.partyId)){
	partyIds.add(parameters.partyId);
}
List glAccountIds=FastList.newInstance();
 glAccountTypeDefaultList = delegator.findList("GlAccountTypeDefault",EntityCondition.makeCondition("glAccountTypeId",EntityOperator.IN,UtilMisc.toList("ACCOUNTS_RECEIVABLE","ACCOUNTS_PAYABLE")),null,null,null,false);
 glAccountIds=EntityUtil.getFieldListFromEntityList(glAccountTypeDefaultList, "glAccountId", true);
if(UtilValidate.isNotEmpty(parameters.interUnitFalg) && parameters.interUnitFalg=="InterUnit"){
	glAccountIds.add("119000");
}
if(UtilValidate.isEmpty(parameters.roleTypeId) && UtilValidate.isEmpty(parameters.partyId)){
	List conList=FastList.newInstance();
	conList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	conList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
	EntityCondition con=EntityCondition.makeCondition(conList,EntityOperator.AND);
	List acctgTransEntryPartyWiseSums=delegator.findList("AcctgTransEntryPartyWiseSums",con,UtilMisc.toSet("partyId"),null,null,false);
	partyIds=EntityUtil.getFieldListFromEntityList(acctgTransEntryPartyWiseSums, "partyId", true);
}

	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));


if(UtilValidate.isNotEmpty(partyIds)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds)));
					  
}
if(UtilValidate.isNotEmpty(rolePartyIds)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.NOT_IN,rolePartyIds)));
					  
}
conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
acctgTransList = delegator.find("AcctgTransAndEntries",condition,null,null,null,null);
acctgTransIter = delegator.find("AcctgTransAndEntries",condition,null,null,null,null);
partyMap=[:];
openingBalMap=[:];

	acctgPartyIds = EntityUtil.getFieldListFromEntityListIterator(acctgTransList, "partyId", true);
	acctgTransList.close();
	partyIds.each{partyId->
		if(acctgPartyIds.contains(partyId)==false){
			partyId=partyId.toUpperCase();
			opeinigBalPartyId.add(partyId);
			if(UtilValidate.isEmpty(partyMap[partyId])){
				tempList=[];
				partyMap[partyId]=tempList;
			}
		}
	}
	if(UtilValidate.isEmpty(partyIds)){
		partyIds=acctgPartyIds;
	}
	partyIds.each{partyId->
			partyId=partyId.toUpperCase();
		Map acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyId",partyId,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_RECEIVABLE"));
		Map acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyId",partyId,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE"));
		openingBalMap[partyId]=acctgReceiveMap.get("openingBalance")+acctgPayMap.get("openingBalance");
	}
		if(acctgTransIter){
//			TransItr = acctgTransList.iterator();
			while (acctgTransIter.hasNext()) {
				GenericValue transEntry = acctgTransIter.next();
				partyId=transEntry.partyId;
					partyId=partyId.toUpperCase();
				if(UtilValidate.isEmpty(partyMap[partyId])){
					tempList=[];
					tempMap=[:];
					tempMap.acctgTransId=transEntry.acctgTransId;
					tempMap.acctgTransEntrySeqId=transEntry.acctgTransEntrySeqId;
					tempMap.transactionDate=transEntry.transactionDate;
					tempMap.acctgTransTypeId=transEntry.acctgTransTypeId;
					tempMap.description=transEntry.description;
					description="";
					invoiceId=null;
					paymentId=null;
					if(UtilValidate.isNotEmpty(transEntry.invoiceId)){
						invoiceId=transEntry.invoiceId;
					}
					if(UtilValidate.isNotEmpty(transEntry.paymentId)){
						paymentId=transEntry.paymentId;
					}
					tempMap.paymentId=paymentId;		
					tempMap.invoiceId=invoiceId;
					if(UtilValidate.isNotEmpty(transEntry.glAccountId)){
						tempMap.glAccountId=transEntry.glAccountId;
					}
					invoiceDes="";
					if(UtilValidate.isNotEmpty(invoiceId)){
						invoiceDetails = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
						invoiceDes=invoiceDetails.description;
					}
					description=invoiceDes;
					paymentDes="";
					if(UtilValidate.isNotEmpty(paymentId)){
						paymentDetails = delegator.findOne("Payment", [paymentId : paymentId], false);
						paymentDes=paymentDetails.comments;
					}
					if(UtilValidate.isNotEmpty(description) && UtilValidate.isNotEmpty(paymentDes)){
						description=description+" And "+paymentDes;
					}else if(UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(paymentDes)){
						description=paymentDes;
					}
					if(UtilValidate.isEmpty(description)){
						glAccount=delegator.findOne("GlAccount",[glAccountId:transEntry.glAccountId],true);
						description = glAccount.description;
					}
				    
					tempMap.description=description;
//					tempMap.partyId=transEntry.partyId;
//					partyName =  PartyHelper.getPartyName(delegator, transEntry.partyId, false);
//					tempMap.name=partyName;
					tempMap.isPosted=transEntry.isPosted;
					tempMap.postedDate=transEntry.postedDate;	
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
					partyId=transEntry.partyId;
					partyId=partyId.toUpperCase();
					tempList=UtilMisc.sortMaps(tempList, UtilMisc.toList("transactionDate"));
					partyMap[partyId]=tempList;
				}else{
						tempMap=[:];
						tempMap.acctgTransId=transEntry.acctgTransId;
						tempMap.acctgTransEntrySeqId=transEntry.acctgTransEntrySeqId;
						tempMap.transactionDate=transEntry.transactionDate;
						tempMap.acctgTransTypeId=transEntry.acctgTransTypeId;
						tempMap.description=transEntry.description;
						
						if(UtilValidate.isNotEmpty(transEntry.glAccountId)){
							tempMap.glAccountId=transEntry.glAccountId;
						}
						description="";
						invoiceId=null;
						paymentId=null;
						if(UtilValidate.isNotEmpty(transEntry.invoiceId)){
							invoiceId=transEntry.invoiceId;
						}
						if(UtilValidate.isNotEmpty(transEntry.paymentId)){
							paymentId=transEntry.paymentId;
						}
						tempMap.paymentId=paymentId;
						tempMap.invoiceId=invoiceId;
						
						invoiceDes="";
						if(UtilValidate.isNotEmpty(invoiceId)){
							invoiceDetails = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
							invoiceDes=invoiceDetails.description;
						}
						description=invoiceDes;
						paymentDes="";
						if(UtilValidate.isNotEmpty(paymentId)){
							paymentDetails = delegator.findOne("Payment", [paymentId : paymentId], false);
							paymentDes=paymentDetails.comments;
						}
						if(UtilValidate.isNotEmpty(description) && UtilValidate.isNotEmpty(paymentDes)){
							description=description+" And "+paymentDes;
						}else if(UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(paymentDes)){
							description=paymentDes;
						}
						if(UtilValidate.isEmpty(description)){
							glAccount=delegator.findOne("GlAccount",[glAccountId:transEntry.glAccountId],true);
							description = glAccount.description;
						}
						tempMap.description=description;
//						tempMap.partyId=transEntry.partyId;
//						partyName =  PartyHelper.getPartyName(delegator, transEntry.partyId, false);
//						tempMap.name=partyName;
						tempMap.isPosted=transEntry.isPosted;
						tempMap.postedDate=transEntry.postedDate;
						debit=0;credit=0;
						if(transEntry.debitCreditFlag=="C"){
							credit=transEntry.amount;
						}
						if(transEntry.debitCreditFlag=="D"){
							debit=transEntry.amount;
						}
						tempMap.debit=debit;
						tempMap.credit=credit;
						partyId=transEntry.partyId;
						partyId=partyId.toUpperCase();
						tempList=partyMap[partyId];
						tempList.add(tempMap);
						tempList=UtilMisc.sortMaps(tempList, UtilMisc.toList("transactionDate"));
						partyMap[partyId]=tempList;
				}
			}
			acctgTransIter.close();
}
context.partyMap=partyMap;
context.openingBalMap=openingBalMap;
if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
	partyLedgerCsv=[];
	partyLedgerAbsCsv=[];
	grdDebit=0;grdCredit=0;
	finalMap=[:];
	for(Map.Entry partyDetails : partyMap.entrySet()){
		partyId = partyDetails.getKey();
		partyVal = partyDetails.getValue();
		tempMap=[:];
		tempAbsMap=[:];
		partyName=PartyHelper.getPartyName(delegator, partyId, false);
		name=partyName+"["+partyId+"]";
		tempMap.glAccountId="PARTY NAME :";
		tempMap.partyId=partyId;
		tempMap.glAccDescription=name;
		//for abstract csv
		tempAbsMap.partyId=partyId;
		tempAbsMap.name=name;
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
			}
			glAccDescription="";
			invoiceId=null;
			paymentId=null;
			if(UtilValidate.isNotEmpty(acctgTrans.invoiceId)){
				invoiceId=acctgTrans.invoiceId;
			}
			if(UtilValidate.isNotEmpty(acctgTrans.paymentId)){
				paymentId=acctgTrans.paymentId;
			}
			invoiceDes="";
			if(UtilValidate.isNotEmpty(invoiceId)){
				invoiceDetails = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
				invoiceDes=invoiceDetails.description;
			}
			glAccDescription=invoiceDes;
			paymentDes="";
			if(UtilValidate.isNotEmpty(paymentId)){
				paymentDetails = delegator.findOne("Payment", [paymentId : paymentId], false);
				paymentDes=paymentDetails.comments;
			}
			if(UtilValidate.isNotEmpty(glAccDescription) && UtilValidate.isNotEmpty(paymentDes)){
				glAccDescription=glAccDescription+" And "+paymentDes;
			}else if(UtilValidate.isEmpty(glAccDescription) && UtilValidate.isNotEmpty(paymentDes)){
				glAccDescription=paymentDes;
			}
			if(UtilValidate.isEmpty(glAccDescription)){
				glAccount=delegator.findOne("GlAccount",[glAccountId:acctgTrans.glAccountId],true);
				glAccDescription = glAccount.description;
			}
			tempTransMap.glAccDescription=glAccDescription;
			
			tempTransMap.invoiceId=invoiceId;
			tempTransMap.partyId=partyId;
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
		totDebit=totDebit+openDebit;
		totCredit=totCredit+openCredit;
		tempMap.debit=totDebit;
		grdDebit=grdDebit+totDebit;
		tempMap.credit=totCredit;
		grdCredit=grdCredit+totCredit;
		partyLedgerCsv.add(tempMap);
		tempMap=[:];
		tempMap.partyId=partyId;
		tempMap.glAccDescription="CLOSING TOTAL :";
		bal=0;clsDebit=0;clsCredit=0;
		bal=totDebit-totCredit;
		if(bal>0){
			clsDebit=bal;
		}else{
			clsCredit=-(bal);
		}
		tempMap.debit=clsDebit;
		tempMap.credit=clsCredit;
		partyLedgerCsv.add(tempMap);
		//for abstract csv
		tempAbsMap.debit=clsDebit;
		tempAbsMap.credit=clsCredit;
		partyLedgerAbsCsv.add(tempAbsMap);
	}
	finalMap.glAccDescription="GRAND TOTAL :";
	finalMap.partyId=partyId;
	finalMap.debit=grdDebit;
	finalMap.credit=grdCredit;
	partyLedgerCsv.add(finalMap);
	//for abstract csv
	tempFinalAbsMap=[:];
	tempFinalAbsMap.name="TOTAL :";
	tempFinalAbsMap.debit=grdDebit;
	tempFinalAbsMap.credit=grdCredit;
	partyLedgerAbsCsv.add(tempFinalAbsMap);
	
	finalMap=[:];
	balance=0;clsGrdDebit=0;clsGrdCredit=0;
	finalMap.glAccDescription="CLOSING GRAND TOTAL :";
	balance=grdDebit-grdCredit;
	if(balance>0){
		clsGrdDebit=balance;
	}else{
		clsGrdCredit=-(balance);
	}
	finalMap.partyId=partyId;
	finalMap.debit=clsGrdDebit;
	finalMap.credit=clsGrdCredit;
	partyLedgerCsv.add(finalMap);
	context.partyLedgerCsv=partyLedgerCsv;
	
	tempFinalAbsMap=[:];
	tempFinalAbsMap.name="NET AMOUNT :";
	tempFinalAbsMap.debit=clsGrdDebit;
	tempFinalAbsMap.credit=clsGrdCredit;
	partyLedgerAbsCsv.add(tempFinalAbsMap);
	context.partyLedgerAbsCsv=partyLedgerAbsCsv;
}



