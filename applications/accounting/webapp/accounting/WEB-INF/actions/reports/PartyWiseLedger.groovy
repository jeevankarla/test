import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityFindOptions;
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

List glAccountIds=FastList.newInstance();
glAccountTypeDefaultList = delegator.findList("GlAccountTypeDefault",EntityCondition.makeCondition("glAccountTypeId",EntityOperator.IN,UtilMisc.toList("ACCOUNTS_RECEIVABLE","ACCOUNTS_PAYABLE")),null,null,null,false);
glAccountIds=EntityUtil.getFieldListFromEntityList(glAccountTypeDefaultList, "glAccountId", true);
if(UtilValidate.isNotEmpty(parameters.interUnitFalg) && parameters.interUnitFalg=="InterUnit"){
   glAccountIds.add("119000");
}

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
		List otherPartyRoles=delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,roleTypeList),UtilMisc.toSet("partyId"),null,null,false);
		rolePartyIds = EntityUtil.getFieldListFromEntityList(otherPartyRoles, "partyId", true);
		List conList=FastList.newInstance();
		conList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
		conList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
		conList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_IN,rolePartyIds));
		conList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
		conList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
		EntityCondition con=EntityCondition.makeCondition(conList,EntityOperator.AND);
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		fieldToSelect = UtilMisc.toSet("partyId");
		EntityListIterator acctgTransEntryPartyIds=delegator.find("AcctgTransAndEntries",con,null,fieldToSelect,null,efo);
		partyIds=EntityUtil.getFieldListFromEntityListIterator(acctgTransEntryPartyIds, "partyId", true);
	}else{
		List roleTypeList=FastList.newInstance();
		List roleTypeAttr=delegator.findList("RoleTypeAttr",EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"ACCOUNTING_ROLE"),null,null,null,false);
	    if(roleTypeAttr){
			List roleTypeIdAttr = EntityUtil.filterByCondition(roleTypeAttr, EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,parameters.roleTypeId));
			GenericValue roleTypeValue = EntityUtil.getFirst(roleTypeIdAttr);
			if(UtilValidate.isNotEmpty(roleTypeValue.get("priority"))){
				if(roleTypeValue.get("priority")==1){
					List partyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,parameters.roleTypeId),UtilMisc.toSet("partyId"),null,null,false);
					partyIds = EntityUtil.getFieldListFromEntityList(partyRole, "partyId", true);
				}else{
					String  roleType = parameters.roleTypeId;
					fieldToSelect = UtilMisc.toSet("partyId");
					EntityListIterator partyRole = delegator.find("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleType),null,fieldToSelect,null,null);
					List priorityList = EntityUtil.filterByCondition(roleTypeAttr, EntityCondition.makeCondition("priority",EntityOperator.LESS_THAN,roleTypeValue.get("priority")));
					List partyRoleNotList = EntityUtil.getFieldListFromEntityList(priorityList, "roleTypeId", true);
					while (partyRole.hasNext()){
						GenericValue party = partyRole.next();
						partyId=party.partyId;
						List partyRoles= delegator.findList("PartyRole",EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId),UtilMisc.toSet("roleTypeId"),null,null,false);
						partyRoleTypeIds = EntityUtil.getFieldListFromEntityList(partyRoles, "roleTypeId", true);
						Boolean addParty = true;
						if(UtilValidate.isNotEmpty(partyRoleTypeIds)){
							for(partyRoleTypestr in partyRoleTypeIds){
								if(partyRoleNotList.contains(partyRoleTypestr)){
									addParty = false;
									break;
								}
								
							}
						}
						if(addParty){
							partyIds.add(partyId);
						}
					}
					partyRole.close();
				}
			}
	    }	
	}
}

if(UtilValidate.isNotEmpty(parameters.partyId)){
	partyIds.clear();
	partyIds.add(parameters.partyId);
}

if(UtilValidate.isEmpty(parameters.roleTypeId) && UtilValidate.isEmpty(parameters.partyId)){
	List conList=FastList.newInstance();
	conList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	conList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
	conList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
	EntityCondition con=EntityCondition.makeCondition(conList,EntityOperator.AND);
	EntityFindOptions efo = new EntityFindOptions();
    efo.setDistinct(true);
	fieldToSelect = UtilMisc.toSet("partyId");
	EntityListIterator acctgTransEntryForPartyIds=delegator.find("AcctgTransAndEntries",con,null,fieldToSelect,null,efo);
	partyIds=EntityUtil.getFieldListFromEntityListIterator(acctgTransEntryForPartyIds, "partyId", true);
}

	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
if(UtilValidate.isEmpty(partyIds)){
	return;
}

if(UtilValidate.isNotEmpty(partyIds)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds)));
					  
}
/*if(UtilValidate.isNotEmpty(rolePartyIds)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.NOT_IN,rolePartyIds)));
					  
}*/
conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fieldToSelect = UtilMisc.toSet("isPosted","partyId","acctgTransId","acctgTransEntrySeqId","transactionDate","invoiceId");
fieldToSelect.add("paymentId");
fieldToSelect.add("glAccountId");
fieldToSelect.add("debitCreditFlag");
fieldToSelect.add("acctgTransTypeId");
fieldToSelect.add("amount");
acctgTransIter = delegator.find("AcctgTransAndEntries",condition,null,fieldToSelect,null,null);
partyMap=[:];
openingBalMap=[:];
List mapsList = FastList.newInstance();
Map interUnitMap = FastMap.newInstance();
Map acctgReceiveMap = FastMap.newInstance();
Map acctgPayMap = FastMap.newInstance();
Map unAppAmtMap = FastMap.newInstance();
Map closingUnAppMap = FastMap.newInstance();
List paymentGlAccountType=FastList.newInstance();
List PaymentGlAccountIds=FastList.newInstance();
 		paymentGlAccountType=delegator.findList("PaymentGlAccountTypeMap",null,null,null,null,false);
		PaymentGlAccountTypeIds=EntityUtil.getFieldListFromEntityList(paymentGlAccountType,"glAccountTypeId", true);
		unAppAmtMap = GeneralLedgerServices.getAcctgTransBalance(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"fromDate",fromDate,"thruDate",thruDate,"glAccountTypeIds",PaymentGlAccountTypeIds));
		PaymentGlAccountTypeIds.add("ACCOUNTS_RECEIVABLE");
		PaymentGlAccountTypeIds.add("ACCOUNTS_PAYABLE");
//		acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_RECEIVABLE","acctgTransTypeId","INCOMING_PAYMENT"));
//		acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE","acctgTransTypeId","OUTGOING_PAYMENT"));
		acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeIds",PaymentGlAccountTypeIds));
		if(UtilValidate.isNotEmpty(parameters.interUnitFalg) && parameters.interUnitFalg=="InterUnit"){
			interUnitMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000"));
		}
		if(UtilValidate.isNotEmpty(acctgReceiveMap)){
			mapsList.add(acctgReceiveMap);
		}
		if(UtilValidate.isNotEmpty(acctgPayMap)){
			mapsList.add(acctgPayMap);
		}
		if(UtilValidate.isNotEmpty(interUnitMap)){
			mapsList.add(interUnitMap);
		}
		
	partyIds.each{partyId->
		partyId=partyId.toUpperCase();
		
		if(UtilValidate.isEmpty(partyMap[partyId])){
			tempList=[];
			partyMap[partyId]=tempList;
		}
		mapsList.each{map->
			Map tempResultMap=map.openingBalMap;
			credit=0; debit=0;
			if(UtilValidate.isNotEmpty(tempResultMap)){
				Map resultMap=tempResultMap.get(partyId);
				if(UtilValidate.isNotEmpty(resultMap)){
					credit=resultMap.get("credit");
					debit=resultMap.get("debit");
					balance=0;
					balance = debit-credit;
					if(UtilValidate.isEmpty(openingBalMap[partyId])){
						openingBalMap[partyId]=balance;
					}else{
						existBal=0;
						existBal=openingBalMap[partyId];
						openingBalMap[partyId]=existBal+balance;
					}
				}else{
				openingBalMap[partyId]=0;
				}
			}
		}
		Map tempUnAppMap = unAppAmtMap.openingBalMap;
		unAppCredit=0; unAppDebit=0;
		if(UtilValidate.isNotEmpty(tempUnAppMap)){
			Map resultUnAppMap = tempUnAppMap.get(partyId);
			if(UtilValidate.isNotEmpty(resultUnAppMap)){
				unAppCredit = resultUnAppMap.get("credit");
				unAppDebit = resultUnAppMap.get("debit");
				value=0;
				value = unAppDebit-unAppCredit;
				if(UtilValidate.isEmpty(closingUnAppMap[partyId])){
					closingUnAppMap[partyId]=value;
				}else{
					existBal=0;
					existBal=closingUnAppMap[partyId];
					closingUnAppMap[partyId]=existBal+value;
				}
				
			}else{
				closingUnAppMap[partyId]=0;
			}
		}
	}
		if(acctgTransIter){
//			TransItr = acctgTransList.iterator();
			while (acctgTransIter.hasNext()) {
				GenericValue transEntry = acctgTransIter.next();
//			transEntryList.each{transEntry->
				partyId=transEntry.partyId;
					partyId=partyId.toUpperCase();
					
					tempMap=[:];
					tempMap.acctgTransId=transEntry.acctgTransId;
					tempMap.acctgTransEntrySeqId=transEntry.acctgTransEntrySeqId;
					tempMap.transactionDate=transEntry.transactionDate;
					tempMap.acctgTransTypeId=transEntry.acctgTransTypeId;
				//	tempMap.description=transEntry.description;
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
//					tempMap.postedDate=transEntry.postedDate;
					debit=0;credit=0;
					if(transEntry.debitCreditFlag=="C"){
						credit=transEntry.amount;
					}
					if(transEntry.debitCreditFlag=="D"){
						debit=transEntry.amount;
					}
					tempMap.debit=debit;
					tempMap.credit=credit;
				if(UtilValidate.isEmpty(partyMap[partyId])){
					tempList=[];
					tempList.add(tempMap);
					partyId=transEntry.partyId;
					partyId=partyId.toUpperCase();
					tempList=UtilMisc.sortMaps(tempList, UtilMisc.toList("transactionDate"));
					partyMap[partyId]=tempList;
				}else{
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
context.closingUnAppMap=closingUnAppMap;
if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
	partyLedgerCsv=[];
	partyLedgerAbsCsv=[];
	partyLedgerDetailedAbsCsv=[];
	grdOpenDebit=0;grdOpenCredit=0;grdCurrDebit=0;grdCurrCredit=0;
	grdDebit=0;grdCredit=0;
	grdUnAppDebit=0;grdUnAppCredit=0;
	finalMap=[:];
	for(Map.Entry partyDetails : partyMap.entrySet()){
		partyId = partyDetails.getKey();
		partyVal = partyDetails.getValue();
		tempMap=[:];
		tempAbsMap=[:];
		tempDetailedAbsMap=[:];
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
		if(UtilValidate.isNotEmpty(openingBalMap.get(partyId))){
		openBal=openingBalMap.get(partyId);
		}
		if(openBal>=0){
			openDebit=openBal;
		}else{
		   openCredit=-(openBal);
		}
		grdOpenDebit=grdOpenDebit+openDebit;
		grdOpenCredit=grdOpenCredit+openCredit;
		unAppAmt=0;unAppDebit=0;unAppCredit=0;
		if(UtilValidate.isNotEmpty(closingUnAppMap.get(partyId))){
			unAppAmt=closingUnAppMap.get(partyId);
		}
		if(unAppAmt>=0){
			unAppDebit=unAppAmt;
		}else{
			unAppCredit=-(unAppAmt);
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
			/*invoiceDes="";
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
			}*/
			tempTransMap.glAccDescription=acctgTrans.description;
			
			tempTransMap.invoiceId=invoiceId;
			tempTransMap.partyId=partyId;
			tempTransMap.paymentId=paymentId;
			tempTransMap.isPosted=acctgTrans.isPosted;
//			tempTransMap.postedDate=acctgTrans.postedDate;
			debit=0;credit=0;
			credit=acctgTrans.credit;
			debit=acctgTrans.debit;
			totDebit=totDebit+debit;
			totCredit=totCredit+credit;
			tempTransMap.credit=credit;
			tempTransMap.debit=debit;
			partyLedgerCsv.add(tempTransMap);
		}
		//For Detailed Abs report
		grdCurrDebit=grdCurrDebit+totDebit;
		grdCurrCredit=grdCurrCredit+totCredit;
		tempDetailedAbsMap.partyId=partyId;
		tempDetailedAbsMap.name=name;
		tempDetailedAbsMap.openDebit=openDebit;
		tempDetailedAbsMap.openCredit=openCredit;
		tempDetailedAbsMap.currDebit=totDebit;
		tempDetailedAbsMap.currCredit=totCredit;
		tempDetailedAbsMap.unAppDebit=unAppDebit;
		tempDetailedAbsMap.unAppCredit=unAppCredit;
		if((openDebit+totDebit+unAppDebit)-(openCredit+totCredit+unAppCredit)>0){
			tempDetailedAbsMap.finalValue=(openDebit+totDebit+unAppDebit)-(openCredit+totCredit+unAppCredit)+"(Dr)";
		}else if((openDebit+totDebit+unAppDebit)-(openCredit+totCredit+unAppCredit)<0){
			tempDetailedAbsMap.finalValue=-((openDebit+totDebit+unAppDebit)-(openCredit+totCredit+unAppCredit))+"(Cr)";
		}
		partyLedgerDetailedAbsCsv.add(tempDetailedAbsMap);
		
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
		if(bal>=0){
			clsDebit=bal;
		}else{
			clsCredit=-(bal);
		}
		tempMap.debit=clsDebit;
		tempMap.credit=clsCredit;
		partyLedgerCsv.add(tempMap);
		tempMap=[:];
		tempMap.glAccDescription="UN-APPLIED AMOUNT :";
		grdUnAppDebit=grdUnAppDebit+unAppDebit;
		grdUnAppCredit=grdUnAppCredit+unAppCredit;
		tempMap.debit=unAppDebit;
		tempMap.credit=unAppCredit;
		partyLedgerCsv.add(tempMap);
		//for abstract csv
		tempAbsMap.debit=clsDebit;
		tempAbsMap.credit=clsCredit;
		tempAbsMap.unAppDebit= unAppDebit;
		tempAbsMap.unAppCredit=unAppCredit;
		finalAmt=0;
		finalAmt=(clsDebit+unAppDebit)-(clsCredit+unAppCredit);
		if(finalAmt>0){
			finalAmt=finalAmt+"(Dr)";
		}else if(finalAmt<0){
			finalAmt=-(finalAmt)+"(Cr)";
		}
		tempAbsMap.finalAmt=finalAmt;
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
	tempFinalAbsMap.unAppDebit =grdUnAppDebit;
	tempFinalAbsMap.unAppCredit =grdUnAppCredit;
	grdfinalAmt=0;
	grdfinalAmt=(grdDebit+grdUnAppDebit)-(grdCredit+grdUnAppCredit);
	if(grdfinalAmt>0){
		grdfinalAmt=grdfinalAmt+"(Dr)";
	}else if(grdfinalAmt<0){
		grdfinalAmt=-(grdfinalAmt)+"(Cr)";
	}
	tempFinalAbsMap.finalAmt=grdfinalAmt;
	partyLedgerAbsCsv.add(tempFinalAbsMap);
	
	finalMap=[:];
	balance=0;clsGrdDebit=0;clsGrdCredit=0;
	finalMap.glAccDescription="CLOSING GRAND TOTAL :";
	balance=grdDebit-grdCredit;
	if(balance>=0){
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
	
	//for Detailed Abs
	tempDetailedAbsMap=[:];
	tempDetailedAbsMap.name="TOTALS  :";
	tempDetailedAbsMap.openDebit=grdOpenDebit;
	tempDetailedAbsMap.openCredit=grdOpenCredit;
	tempDetailedAbsMap.currDebit=grdCurrDebit;
	tempDetailedAbsMap.currCredit=grdCurrCredit;
	tempDetailedAbsMap.unAppDebit=grdUnAppDebit;
	tempDetailedAbsMap.unAppCredit=grdUnAppCredit;
	if((grdOpenDebit+grdCurrDebit+grdUnAppDebit)-(grdOpenCredit+grdCurrCredit+grdUnAppCredit)>0){
		tempDetailedAbsMap.finalValue=(grdOpenDebit+grdCurrDebit+grdUnAppDebit)-(grdOpenCredit+grdCurrCredit+grdUnAppCredit)+"(Dr)";
	}else if((grdOpenDebit+grdCurrDebit+grdUnAppDebit)-(grdOpenCredit+grdCurrCredit+grdUnAppCredit)<0){
		tempDetailedAbsMap.finalValue=-((grdOpenDebit+grdCurrDebit+grdUnAppDebit)-(grdOpenCredit+grdCurrCredit+grdUnAppCredit))+"(Cr)";
	}
	partyLedgerDetailedAbsCsv.add(tempDetailedAbsMap);
	context.partyLedgerDetailedAbsCsv=partyLedgerDetailedAbsCsv;
}



