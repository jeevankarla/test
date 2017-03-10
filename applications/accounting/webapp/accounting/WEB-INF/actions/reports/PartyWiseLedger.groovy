import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityListIterator;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.accounting.payment.PaymentWorker;

import in.vasista.vbiz.humanres.HumanresService;

import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.accounting.ledger.GeneralLedgerServices;
import org.ofbiz.accounting.util.UtilAccounting;
dctx = dispatcher.getDispatchContext();
InvoiceItemDetailMap = [:];
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
reportTypeFlag = parameters.reportTypeFlag;

roId = parameters.division;
segmentId = parameters.segment;
branchList = [];
condList = [];
condList.clear();
if(UtilValidate.isNotEmpty(roId)&& !roId.equals("Company")){
	condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,roId));
	condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
	condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
	List roWiseBranchaList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(roWiseBranchaList)){
		branchList= EntityUtil.getFieldListFromEntityList(roWiseBranchaList,"partyIdTo", true);
		branchList.add(roId);
	}
	
}

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
List assetAndLiabltyGlAcctgClsIds=FastList.newInstance();
GenericValue assetGlAcc = delegator.findOne("GlAccountClass",UtilMisc.toMap("glAccountClassId", "ASSET"),false);
GenericValue liabilityGlAcc = delegator.findOne("GlAccountClass",UtilMisc.toMap("glAccountClassId", "LIABILITY"),false);
List assetGlAcctgClsIds = UtilAccounting.getDescendantGlAccountClassIds(assetGlAcc);
if(UtilValidate.isNotEmpty(assetGlAcctgClsIds)){
	assetAndLiabltyGlAcctgClsIds.addAll(assetGlAcctgClsIds);
}
List liabilityGlAcctgClsIds = UtilAccounting.getDescendantGlAccountClassIds(liabilityGlAcc);
if(UtilValidate.isNotEmpty(liabilityGlAcctgClsIds)){
	assetAndLiabltyGlAcctgClsIds.addAll(liabilityGlAcctgClsIds);
}
EntityFindOptions enfo = new EntityFindOptions();
enfo.setDistinct(true);
fieldToSelect = UtilMisc.toSet("glAccountId");
glAcctgClsCond = EntityCondition.makeCondition([EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,assetAndLiabltyGlAcctgClsIds)],EntityOperator.AND);
EntityListIterator glAccountClsIdList=delegator.find("GlAccount",glAcctgClsCond,null,fieldToSelect,null,enfo);
List newGlAccountIds=FastList.newInstance();
List newGlAcctgIds = FastList.newInstance();
newGlAccountIds=EntityUtil.getFieldListFromEntityListIterator(glAccountClsIdList,"glAccountId",true);
fieldToSelect = UtilMisc.toSet("glAccountId");
glAcctgCond = EntityCondition.makeCondition([EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,assetAndLiabltyGlAcctgClsIds),
	                                         EntityCondition.makeCondition("isControlAcctg",EntityOperator.EQUALS,"Y")],EntityOperator.AND);
EntityListIterator glAccountIdList=delegator.find("GlAccount",glAcctgCond,null,fieldToSelect,null,enfo);
if(UtilValidate.isNotEmpty(glAccountIdList)){
	newGlAcctgIds = EntityUtil.getFieldListFromEntityListIterator(glAccountIdList,"glAccountId",true);
	newGlAccountIds.removeAll(newGlAcctgIds);
}
List glAccountIds=FastList.newInstance();
/*glAccountTypeDefaultList = delegator.findList("GlAccountTypeDefault",EntityCondition.makeCondition("glAccountTypeId",EntityOperator.IN,UtilMisc.toList("ACCOUNTS_RECEIVABLE","ACCOUNTS_PAYABLE")),null,null,null,false);
glAccountIds=EntityUtil.getFieldListFromEntityList(glAccountTypeDefaultList, "glAccountId", true);
if(UtilValidate.isNotEmpty(parameters.interUnitFalg) && parameters.interUnitFalg=="InterUnit"){
   glAccountIds.add("119000");
}*/
glAccountIds.addAll(newGlAccountIds);
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
					partyIds=EntityUtil.getFieldListFromEntityListIterator(partyRole, "partyId", true);
					
					/*if(UtilValidate.isNotEmpty(partyRoleNotList)){
						List condtnList=FastList.newInstance();
						condtnList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.NOT_IN,partyRoleNotList));
						condtnList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds));
						EntityCondition cond=EntityCondition.makeCondition(condtnList,EntityOperator.AND);
						EntityListIterator partyRoleIter = delegator.find("PartyRole",cond,null,fieldToSelect,null,null);
						partyIds=EntityUtil.getFieldListFromEntityListIterator(partyRoleIter, "partyId", true);
					}*/
					/*while (partyRole.hasNext()){
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
					partyRole.close();*/
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

	
	if(UtilValidate.isNotEmpty(roId) && !roId.equals("Company")){
		conditionList.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, branchList));
	}
	if(UtilValidate.isNotEmpty(segmentId) && !segmentId.equals("All") && !segmentId.equals("YARN_SALE")){
		conditionList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.EQUALS, segmentId));
	}
	if(UtilValidate.isNotEmpty(segmentId) && segmentId.equals("YARN_SALE")){
		conditionList.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.IN, UtilMisc.toList("YARN_SALE", "DEPOT_YARN_SALE")));
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
/*conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIds));
conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
fieldToSelect = UtilMisc.toSet("isPosted","partyId","acctgTransId","acctgTransEntrySeqId","transactionDate","invoiceId");
fieldToSelect.add("paymentId");
fieldToSelect.add("glAccountId");
fieldToSelect.add("debitCreditFlag");
fieldToSelect.add("acctgTransTypeId");
fieldToSelect.add("amount");
acctgTransIter = delegator.find("AcctgTransAndEntries",condition,null,fieldToSelect,null,null);*/

//getting previous month closing balance
finalAccountingTransList=[];

conditionList1=[];
if(UtilValidate.isNotEmpty(roId) && !roId.equals("Company")){
	conditionList1.add(EntityCondition.makeCondition("costCenterId" , EntityOperator.IN, branchList));
}
if(UtilValidate.isNotEmpty(segmentId) && !segmentId.equals("All") && !segmentId.equals("YARN_SALE")){
	conditionList1.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.EQUALS, segmentId));
}
if(UtilValidate.isNotEmpty(segmentId) && segmentId.equals("YARN_SALE")){
	conditionList1.add(EntityCondition.makeCondition("purposeTypeId" , EntityOperator.IN, UtilMisc.toList("YARN_SALE", "DEPOT_YARN_SALE")));
}

conditionList1.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList1.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
if(UtilValidate.isEmpty(partyIds)){
	return;
}
if(UtilValidate.isNotEmpty(partyIds)){
	conditionList1.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIds)));
}
List extTransTypeIdsList1 = FastList.newInstance();
List extTransTypeIdsList2 = FastList.newInstance();
List assetAndLiabilityIdsList = FastList.newInstance();
 extTransTypeIdsList1=UtilMisc.toList("SALES","SALES_INVOICE","PURCHASE_INVOICE","INVOICE_APPL");
 extTransTypeIdsList2=UtilMisc.toList("RECEIPT","PAYMENT_ACCTG_TRANS","PAYMENT_APPL","OUTGOING_PAYMENT","INCOMING_PAYMENT","OB_TB");
 assetAndLiabilityIdsList=UtilMisc.toList("CURRENT_ASSET","LONGTERM_ASSET","CURRENT_LIABILITY","LONGTERM_LIABILITY","CASH_EQUIVALENT");

conditionList1.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
EntityCondition cond=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
List accntTransList=delegator.findList("AcctgTransAndEntries",cond,null,null,null,false);
 if(UtilValidate.isNotEmpty(accntTransList)){
	 //accntTransList.each{ accntDetails->
	 for(int a=0;a<accntTransList.size();a++){
		 accntDetails=accntTransList.get(a);
		 accntTransTypeId=accntDetails.acctgTransTypeId;
		 glAccntId=accntDetails.glAccountId;
		 glAccountClassId=accntDetails.glAccountClassId;
		 finAccountTransId=accntDetails.finAccountTransId;
		 glAccntDetails = delegator.findOne("GlAccount", [glAccountId : glAccntId], false);
		 if(UtilValidate.isEmpty(glAccntDetails.isControlAcctg)||(UtilValidate.isNotEmpty(glAccntDetails.isControlAcctg)&&(!"Y".equals(glAccntDetails.isControlAcctg)))){
			 if(extTransTypeIdsList1.contains(accntTransTypeId)){
				 //if(glAccntId.equals("120000")||glAccntId.equals("210000")){
				 if("ACCOUNTS_PAYABLE".equals(glAccntDetails.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccntDetails.get("glAccountTypeId"))){
					 finalAccountingTransList.addAll(accntDetails);
				 }
			 }
			 if(extTransTypeIdsList2.contains(accntTransTypeId)){
				 if(assetAndLiabilityIdsList.contains(glAccountClassId)){
					 finalAccountingTransList.addAll(accntDetails);
				 }
			 }
			 if(accntTransTypeId.equals("JOURNAL")){
				 //if(glAccntId.equals("120000")||glAccntId.equals("210000")){
				 if("ACCOUNTS_PAYABLE".equals(glAccntDetails.get("glAccountTypeId"))||"ACCOUNTS_RECEIVABLE".equals(glAccntDetails.get("glAccountTypeId"))){
					 finalAccountingTransList.addAll(accntDetails);
				 }
			 }
			 if(accntTransTypeId.equals("ADJUSTMENT")){
				 if(assetAndLiabilityIdsList.contains(glAccountClassId)){
					 finalAccountingTransList.addAll(accntDetails);
				 }
				 /*finAccntTrans = delegator.findOne("FinAccountTrans", [finAccountTransId : finAccountTransId], false);
				
				 if(UtilValidate.isNotEmpty(finAccntTrans)&&UtilValidate.isNotEmpty(finAccntTrans.finAccountId)){
					 finAccountId=finAccntTrans.finAccountId;
					 if(UtilValidate.isNotEmpty(finAccountId)){
						 finAccnt = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
						 finAccntGlAccntId=finAccnt.postToGlAccountId;
						 if(UtilValidate.isNotEmpty(finAccntGlAccntId)){
							 if(glAccntId.equals(finAccntGlAccntId)){
								 finalAccountingTransList.addAll(accntDetails);
							 }
						 }
					 }
				 }*/
			 }
			 if(accntTransTypeId.equals("CAPITALIZATION")){
					 finalAccountingTransList.addAll(accntDetails);
			 }
			 
			 
		 }
		 
	 }
 }

partyMap=[:];
openingBalMap=[:];
List mapsList = FastList.newInstance();
Map interUnitMap = FastMap.newInstance();
Map acctgReceiveMap = FastMap.newInstance();
Map acctgPayMap = FastMap.newInstance();
Map unAppAmtMap = FastMap.newInstance();
Map unAppledMap = FastMap.newInstance();
Map closingUnAppMap = FastMap.newInstance();
List paymentGlAccountType=FastList.newInstance();
List PaymentGlAccountIds=FastList.newInstance();
 		paymentGlAccountType=delegator.findList("PaymentGlAccountTypeMap",null,null,null,null,false);
		PaymentGlAccountTypeIds=EntityUtil.getFieldListFromEntityList(paymentGlAccountType,"glAccountTypeId", true);
		
		if(roId.equals("Company") && segmentId.equals("All")){
			unAppledMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"costCenterId",null, "segmentId", null));
		}
		else if(roId.equals("Company") && !segmentId.equals("All")){
			unAppledMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"costCenterId",null, "segmentId", segmentId));
		}
		else if(!roId.equals("Company") && segmentId.equals("All")){
			unAppledMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"roBranchList",branchList, "segmentId", null));
		}
		else{
			unAppledMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"roBranchList",branchList, "segmentId", segmentId));
		}
		if(roId.equals("Company") && segmentId.equals("All")){
			unAppAmtMap = GeneralLedgerServices.getAcctgTransBalance(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"fromDate",fromDate,"thruDate",thruDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"costCenterId",null, "segmentId", null));
		}
		else if(roId.equals("Company") && !segmentId.equals("All")){
			unAppAmtMap = GeneralLedgerServices.getAcctgTransBalance(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"fromDate",fromDate,"thruDate",thruDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"costCenterId",null, "segmentId", segmentId));
		}
		else if(!roId.equals("Company") && segmentId.equals("All")){
			unAppAmtMap = GeneralLedgerServices.getAcctgTransBalance(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"fromDate",fromDate,"thruDate",thruDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"roBranchList",branchList, "segmentId", null));
		}
		else{
			unAppAmtMap = GeneralLedgerServices.getAcctgTransBalance(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"fromDate",fromDate,"thruDate",thruDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"roBranchList",branchList, "segmentId", segmentId));
		}
		//unAppledMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeIds",PaymentGlAccountTypeIds,"roId",roId, "segmentId", segmentId));
		//unAppAmtMap = GeneralLedgerServices.getAcctgTransBalance(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"fromDate",fromDate,"thruDate",thruDate,"glAccountTypeIds",PaymentGlAccountTypeIds));
		PaymentGlAccountTypeIds.clear();
		PaymentGlAccountTypeIds.add("ACCOUNTS_RECEIVABLE");
		PaymentGlAccountTypeIds.add("ACCOUNTS_PAYABLE");
//		acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_RECEIVABLE","acctgTransTypeId","INCOMING_PAYMENT"));
//		acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE","acctgTransTypeId","OUTGOING_PAYMENT"));
		
		if(roId.equals("Company") && segmentId.equals("All")){
			acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE","acctgTransTypeId","OUTGOING_PAYMENT","costCenterId",null, "segmentId", null));
		}
		else if(roId.equals("Company") && !segmentId.equals("All")){
			acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE","acctgTransTypeId","OUTGOING_PAYMENT","costCenterId",null, "segmentId", segmentId));
		}
		else if(!roId.equals("Company") && segmentId.equals("All")){
			acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE","acctgTransTypeId","OUTGOING_PAYMENT","roBranchList",branchList, "segmentId", null));
		}
		else{
			acctgPayMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountTypeId","ACCOUNTS_PAYABLE","acctgTransTypeId","OUTGOING_PAYMENT","roBranchList",branchList, "segmentId", segmentId));
		}
		
		if(roId.equals("Company") && segmentId.equals("All")){
			acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","costCenterId",null, "segmentId", null));
		}
		else if(roId.equals("Company") && !segmentId.equals("All")){
			acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","costCenterId",null, "segmentId", segmentId));
		}
		else if(!roId.equals("Company") && segmentId.equals("All")){
			acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","roBranchList",branchList, "segmentId", null));
		}
		else{
			acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","roBranchList",branchList, "segmentId", segmentId));
		}
		
		//acctgReceiveMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","roId",roId, "segmentId", segmentId));
		
		if(UtilValidate.isNotEmpty(parameters.interUnitFalg) && parameters.interUnitFalg=="InterUnit"){
			if(roId.equals("Company") && segmentId.equals("All")){
				interUnitMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","costCenterId",null, "segmentId", null));
			}
			else if(roId.equals("Company") && !segmentId.equals("All")){
				interUnitMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","costCenterId",null, "segmentId", segmentId));
			}
			else if(!roId.equals("Company") && segmentId.equals("All")){
				interUnitMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","roBranchList",branchList, "segmentId", null));
			}
			else{
				interUnitMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","roBranchList",branchList, "segmentId", segmentId));
			}
			//interUnitMap = GeneralLedgerServices.getAcctgTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"partyIds",partyIds,"transactionDate",fromDate,"glAccountId","119000","roId",roId, "segmentId", segmentId));
		}
		/*if(UtilValidate.isNotEmpty(acctgReceiveMap)){
			mapsList.add(acctgReceiveMap);
		}*/
		if(UtilValidate.isNotEmpty(acctgPayMap)){
			mapsList.add(acctgPayMap);
		}
		if(UtilValidate.isNotEmpty(interUnitMap)){
			mapsList.add(interUnitMap);
		}
		
	//partyIds.each{partyId->
		for(p=0;p<partyIds.size();p++){
		partyId=partyIds.get(p);
		partyId=partyId.toUpperCase();
		
		if(UtilValidate.isEmpty(partyMap[partyId])){
			tempList=[];
			partyMap[partyId]=tempList;
		}
		finalObMap=[:];
		obBalMap=[:];
		partyObDetails = GeneralLedgerServices.getGlAccountOpeningBalanceForParty(dctx, UtilMisc.toMap("userLogin",userLogin,"partyId",partyId,"fromDate",fromDate));
		if(UtilValidate.isNotEmpty(partyObDetails)){
			//openingBalMap[partyId]=partyObDetails.get("openingBal");
			obDebit=partyObDetails.get("postedDebits");
			obCredit=partyObDetails.get("postedCredits");
			obbalDetailsMap=[:];
			obbalDetailsMap["credit"]=obCredit;
			obbalDetailsMap["debit"]=obDebit;
			obBalMap.put(partyId,obbalDetailsMap);
			if(UtilValidate.isNotEmpty(obBalMap)){
				finalObMap.put("openingBalMap",obBalMap);
				mapsList.add(finalObMap);
			}
		}
		//mapsList.each{map->
		 for(m=0;m<mapsList.size();m++){
			map=mapsList.get(m);
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
		
		/*Map tempUnAppMap = unAppAmtMap.openingBalMap;
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
		}*/
		Map tempUnAppliedMap = unAppledMap.get("openingBalMap");
		Map tempUnAppAmtMap = unAppAmtMap.get("openingBalMap");
		unAppCredit=0;unAppDebit=0;value=0;
		/*if(UtilValidate.isNotEmpty(tempUnAppliedMap)){
			Map resultMap=tempUnAppliedMap.get(partyId);
			if(UtilValidate.isNotEmpty(resultMap)){
				unAppCredit=resultMap.get("credit");
				unAppDebit=resultMap.get("debit");
			}
		}*/
		if(UtilValidate.isNotEmpty(tempUnAppAmtMap)){
			Map resultMap=tempUnAppAmtMap.get(partyId);
			if(UtilValidate.isNotEmpty(resultMap)){
				unAppCredit=unAppCredit+resultMap.get("credit");
				unAppDebit=unAppDebit+resultMap.get("debit");
			}
		}
		value=unAppDebit-unAppCredit;
		if(UtilValidate.isEmpty(closingUnAppMap[partyId])){
			closingUnAppMap[partyId]=value;
		}else{
			existBal=0;
			existBal=closingUnAppMap[partyId];
			closingUnAppMap[partyId]=existBal+value;
		}
		
	}
	if(UtilValidate.isNotEmpty(finalAccountingTransList)){		
		//finalAccountingTransList.each{transEntry->			
		 for(f=0;f<finalAccountingTransList.size();f++){
			transEntry=finalAccountingTransList.get(f);
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
					tempInvTaxMap=[:];
					if(UtilValidate.isNotEmpty(invoiceId) && UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag.equals("Ledger")){
						invoiceDetails = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
						invoiceDes=invoiceDetails.description;
						invoiceMessage = invoiceDetails.invoiceMessage;
						tempInvTaxMap["invoiceMessage"]=invoiceMessage;
						
					}
										
					if(UtilValidate.isNotEmpty(invoiceId)){
						
						EntityFindOptions enf = new EntityFindOptions();
						enf.setDistinct(true);
						List conList = FastList.newInstance();
						//fieldToSelect = UtilMisc.toSet("invoiceId");
				        conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("vatPercent", EntityOperator.NOT_EQUAL, null), EntityOperator.OR, EntityCondition.makeCondition("cstPercent", EntityOperator.NOT_EQUAL, null)));
						conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("vatAmount", EntityOperator.NOT_EQUAL, null), EntityOperator.OR, EntityCondition.makeCondition("cstAmount", EntityOperator.NOT_EQUAL, null)));
						conList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
						invoiceCond = EntityCondition.makeCondition(conList,EntityOperator.AND);
						EntityListIterator InvoiceItemListItr =delegator.find("InvoiceItem",invoiceCond,null,UtilMisc.toSet("vatPercent","vatAmount","cstPercent","cstAmount","invoiceId"),null,null);
						vatPercentMap=[:];
						cstPercentMap=[:];
						
							while (entryItem = InvoiceItemListItr.next()){
								vatPercent = entryItem.get("vatPercent");
								vatAmount = entryItem.get("vatAmount");
								cstAmount = entryItem.get("cstAmount");
								cstPercent = entryItem.get("cstPercent");
								invoiceId = entryItem.get("invoiceId");
								if(vatPercent != null){
								if(UtilValidate.isNotEmpty(vatPercentMap[vatPercent])){
									vatPercentMap.put(vatPercent,vatPercentMap[vatPercent]+vatAmount);
								}else{
								   vatPercentMap.put(vatPercent,vatAmount);
								}
								//cst 
								}else{
								if(UtilValidate.isNotEmpty(cstPercentMap[cstPercent])){
								cstPercentMap.put(cstPercent,cstPercentMap[cstPercent]+cstAmount);
								}else{
								   cstPercentMap.put(cstPercent,cstAmount);
								}
								}
							}
							
							tempInvTaxMap["vatMap"]=vatPercentMap;
							tempInvTaxMap["cstMap"]=cstPercentMap;
							invoiceMessage="";
							
							InvoiceItemDetailMap.put(invoiceId,tempInvTaxMap);
							
			     InvoiceItemListItr.close();
					
		}
						
					
					description=invoiceDes;
					paymentDes="";
					if(UtilValidate.isNotEmpty(paymentId) && UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag.equals("Ledger")){
						paymentDetails = delegator.findOne("Payment", [paymentId : paymentId], false);
						paymentDes=paymentDetails.comments;
					}
					if(UtilValidate.isNotEmpty(description) && UtilValidate.isNotEmpty(paymentDes)){
						description=description+" And "+paymentDes;
					}else if(UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(paymentDes)){
						description=paymentDes;
					}
					if(UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag.equals("Ledger")){
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
	}
	
/*	
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
					
					// invoice sequence
					
					condList.clear();
					//condList.add(EntityCondition.makeCondition("invoiceSequenceTypeId", EntityOperator.EQUALS , "INVOICE_SEQUENCE"));
					condList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS , invoiceId));
					cond2 = EntityCondition.makeCondition(condList,EntityOperator.AND);
					invsequenceList = delegator.findList("InvoiceSequence", cond2, null, null, null, false);
					if(invsequenceList){
						invsequence = EntityUtil.getFirst(invsequenceList);
						tempMap.sequenceId=invsequence.sequenceId;
					}else{
						tempMap.sequenceId="";
					}
					
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
}*/
context.partyMap=partyMap;
context.openingBalMap=openingBalMap;
context.closingUnAppMap=closingUnAppMap;
if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
	partyLedgerCsv=[];
	partyLedgerAbsCsv=[];
	partyLedgerDetailedAbsCsv=[];
	grdOpenDebit=0;grdOpenCredit=0;grdCurrDebit=0;grdCurrCredit=0;
	grdDebit=0;grdCredit=0;grdUnAppAmt=0;
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
		//partyVal.each{acctgTrans->
		 for(int p=0;p<partyVal.size();p++){
			acctgTrans=partyVal.get(p);
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
		grdDebit=grdDebit+totDebit+unAppDebit;
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
	if((grdDebit-grdCredit)>=0){
		tempFinalAbsMap.debit=grdDebit-grdCredit;
		tempFinalAbsMap.credit=0;
	}else{
		tempFinalAbsMap.debit=0;
		tempFinalAbsMap.credit=-(grdDebit-grdCredit);
	}
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
	if(grdUnAppAmt>=0){
		tempFinalAbsMap.unApplied=grdUnAppAmt+"(Dr)";
	}else{
		tempFinalAbsMap.unApplied=-grdUnAppAmt+"(Cr)";
	}
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
/*if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
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
*/
context.InvoiceItemDetailMap = InvoiceItemDetailMap;	
	