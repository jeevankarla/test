	/*
	 * Licensed to the Apache Software Foundation (ASF) under one
	 * or more contributor license agreements.  See the NOTICE file
	 * distributed with this work for additional information
	 * regarding copyright ownership.  The ASF licenses this file
	 * to you under the Apache License, Version 2.0 (the
	 * "License"); you may not use this file except in compliance
	 * with the License.  You may obtain a copy of the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing,
	 * software distributed under the License is distributed on an
	 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	 * KIND, either express or implied.  See the License for the
	 * specific language governing permissions and limitations
	 * under the License.
	 */
	import org.ofbiz.entity.*;
	import org.ofbiz.base.util.*;
	import org.ofbiz.common.*;
	import org.ofbiz.webapp.control.*;
	import org.ofbiz.accounting.invoice.*;
	import org.ofbiz.accounting.payment.*;
	import org.ofbiz.accounting.util.UtilAccounting;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.*;
	import javolution.util.FastMap;
	import java.util.Calendar;
	import java.util.List;
	
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	
	import org.ofbiz.service.GenericServiceException;
	import org.ofbiz.service.ServiceUtil;
	
	fromDate=parameters.fromDate;
	thruDate=parameters.thruDate;
	partyfromDate=parameters.partyfromDate;
	partythruDate=parameters.partythruDate;
	partyCode = parameters.partyId;
	roleTypeId = parameters.roleTypeId;
	dctx = dispatcher.getDispatchContext();
	
	isLedgerCallFor="ArOnly";
	if(parameters.isLedgerCallFor){
		isLedgerCallFor=parameters.isLedgerCallFor;
	}
	branchId = parameters.branchId;
	partyGroup = delegator.findOne("PartyGroup",["partyId":branchId],false);
	branchName = partyGroup.groupName
	context.branchName=branchName;
	partyClassificationGroupId = parameters.partyClassificationGroupId;
	fromDateTime = null;
	thruDateTime = null;
	if(UtilValidate.isNotEmpty(partyfromDate)&& UtilValidate.isNotEmpty(partythruDate)){
		def sdf = new SimpleDateFormat("yyyy, MMM dd");
		try {
			fromDateTime = new java.sql.Timestamp(sdf.parse(partyfromDate).getTime());
			thruDateTime = new java.sql.Timestamp(sdf.parse(partythruDate).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: "+fromDate, "");
		}
		}
	dayBegin = UtilDateTime.getDayStart(fromDateTime);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime);
	context.fromDate=dayBegin;
	context.thruDate=dayEnd;
	if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");
	}
	}
	Timestamp invoiceFirstDate=null;
	Timestamp invoiceLastDate=null;
	Timestamp paymentFirstDate=null;
	Timestamp paymentLastDate=null;
	Boolean actualCurrency = new Boolean(context.actualCurrency);
	if (actualCurrency == null) {
	    actualCurrency = true;
	}
	actualCurrencyUomId = context.actualCurrencyUomId;
	if (!actualCurrencyUomId) {
	    actualCurrencyUomId = context.defaultOrganizationPartyCurrencyUomId;
	}
	findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	//get total/unapplied/applied invoices separated by sales/purch amount:
	/*
	for Company
	
	AR---->invoice   D...and...Payment C
	AP----->invoice  C....and...Payment D
	
	OB = arOB()- apOB()  //for 
	
	if OB > 0  then Ar is big( put in Debit)
	else       then is Ap  big(put in Credit)
	
	clsoing Bal=Ob+total D -  total C
	*/
	totalInvSaApplied         = BigDecimal.ZERO;
	totalInvSaNotApplied     = BigDecimal.ZERO;
	totalInvPuApplied         = BigDecimal.ZERO;
	totalInvPuNotApplied     = BigDecimal.ZERO;
	
	
	//from finHistory for InterUnit Ledger
	
	partyDebits=0;
	partyCredits=0;
	partyFinHistryDayWiseMap=[];
	if(UtilValidate.isNotEmpty(context.partyTotalDebits)){
			partyDebits=context.partyTotalDebits;
		}
	if(UtilValidate.isNotEmpty(context.partyTotalCredits)){
		partyCredits=context.partyTotalCredits;
	}
	
	if(UtilValidate.isNotEmpty(context.partyDayWiseFinHistryMap)){
		partyFinHistryDayWiseMap=context.partyDayWiseFinHistryMap;
	}
	partyWiseLedgerAbstractMap=[:]
	//Check for Party is Valid or Not
	result = [:];
	conditionList=[];
	List formatList = [];
	if(UtilValidate.isNotEmpty(branchId)){
		resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",branchId));
		Map formatMap = [:];
		partyList=[];
		if(resultCtx && resultCtx.get("partyList")){
			partyList=resultCtx.get("partyList")
			partyIdToList= EntityUtil.getFieldListFromEntityList(partyList,"partyIdTo", true);
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.EQUALS, "BRANCH_OFFICE"));
			conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, partyIdToList));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List partyClassificationList=delegator.findList("PartyClassification",condition,UtilMisc.toSet("partyId"),null,null,false);
			formatList= EntityUtil.getFieldListFromEntityList(partyClassificationList,"partyId", true);
		}
		formatList.add(branchId);
	}
	List rolePartyIds = [];
	if(UtilValidate.isNotEmpty(roleTypeId)){
		conditionList.clear();
	    conditionList.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS, "ACCOUNTING_ROLE"));
	    conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, roleTypeId));
	    condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    List roleTypeAttr=delegator.findList("RoleTypeAttr",condition,null,null,null,false);
		roleTypeList=EntityUtil.getFieldListFromEntityList(roleTypeAttr, "roleTypeId", true);
		List otherPartyRoles=delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,roleTypeList),UtilMisc.toSet("partyId"),UtilMisc.toList("partyId"),null,false);
		rolePartyIds = EntityUtil.getFieldListFromEntityList(otherPartyRoles, "partyId", true);
	}
	if(UtilValidate.isNotEmpty(partyCode)){
		List otherPartyRoles=delegator.findList("PartyRole",EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyCode),UtilMisc.toSet("partyId"),UtilMisc.toList("partyId"),null,false);
		rolePartyIds = EntityUtil.getFieldListFromEntityList(otherPartyRoles, "partyId", true);
	}
	if((UtilValidate.isNotEmpty(branchId)) && (roleTypeId == "EMPANELLED_CUSTOMER")){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN, formatList));
	    conditionList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.IN, rolePartyIds));
		conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
		if(UtilValidate.isNotEmpty(roleTypeId)){
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, roleTypeId));
		}
	    condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    List partyRelationships =delegator.findList("PartyRelationship",condition,UtilMisc.toSet("partyIdTo"),UtilMisc.toList("partyIdTo"),null,false);
		rolePartyIds = EntityUtil.getFieldListFromEntityList(partyRelationships, "partyIdTo", true);
	}
	if(UtilValidate.isNotEmpty(partyClassificationGroupId)){
		if(partyClassificationGroupId == "INDIVIDUAL_WEAVERS"){
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, rolePartyIds));
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.EQUALS, "INDIVIDUAL_WEAVERS"));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List partyClassifications =delegator.findList("PartyClassification",condition,UtilMisc.toSet("partyId"),UtilMisc.toList("partyId"),null,false);
			rolePartyIds = EntityUtil.getFieldListFromEntityList(partyClassifications, "partyId", true);
		}
		if(partyClassificationGroupId == "OTHERS"){
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, rolePartyIds));
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.NOT_EQUAL, "INDIVIDUAL_WEAVERS"));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List partyClassifications =delegator.findList("PartyClassification",condition,UtilMisc.toSet("partyId"),UtilMisc.toList("partyId"),null,false);
			rolePartyIds = EntityUtil.getFieldListFromEntityList(partyClassifications, "partyId", true);
		}
	}
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_IN_PROCESS","INVOICE_WRITEOFF","INVOICE_CANCELLED")));
	if(UtilValidate.isNotEmpty(partyfromDate)&& UtilValidate.isNotEmpty(partythruDate)){
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
		conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
		}
	if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
		conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
	}
	//adding for Abstract PartyLedger
	if((UtilValidate.isNotEmpty(rolePartyIds)) || (UtilValidate.isNotEmpty(formatList))){
			conditionList.add( EntityCondition.makeCondition([
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyId", EntityOperator.IN, rolePartyIds),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, formatList)
					],EntityOperator.AND),
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyId", EntityOperator.IN,formatList),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, rolePartyIds)
					],EntityOperator.AND)
				],EntityOperator.OR));
	}
	newInvCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<String> payOrderBy = UtilMisc.toList("invoiceDate");
	invIterator = delegator.find("InvoiceAndType", newInvCondition, null, null, payOrderBy, findOpts);
	arInvoiceDetailsMap=[:];
	arInvoiceDetailsMap.put("invTotal", BigDecimal.ZERO);
	apInvoiceDetailsMap=[:];
	apInvoiceDetailsMap.put("invTotal", BigDecimal.ZERO);
	invCounter=0;
	partyDayWiseDetailMap = [:];
	while (invoice = invIterator.next()) {
		invoiceDate=invoice.invoiceDate;
		if(invCounter==0){
			invoiceFirstDate=invoice.invoiceDate;
		}
		invCounter=invCounter+1;
		innerMap=[:];
		curntDay=UtilDateTime.toDateString(invoiceDate ,"dd-MM-yyyy");
		innerMap["partyId"]="";
		innerMap["date"]=curntDay;
		innerMap["purposeTypeId"]=invoice.purposeTypeId;
		innerMap["description"]=invoice.invoiceMessage;
		innerMap["invoiceDate"]=invoice.invoiceDate;
		innerMap["invoiceId"]=invoice.invoiceId;
		innerMap["invoiceSequenceId"]="";
		innerMap["tinNumber"]="";
		innerMap["vchrType"]="";
		innerMap["vchrCode"]="";
		innerMap["crOrDbId"]="";
		invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
		//invTotalVal=invTotalVal-vatRevenue;
		innerMap["invTotal"]=invTotalVal;
		innerMap["debitValue"]=0;
		innerMap["creditValue"]=0;
		//get Sequence for invoiceId
		//invoiceSequenceId = null;
		//for abstract PartyLedger it should ignore
			if(UtilValidate.isEmpty(context.partyIdsList)){
				if(UtilValidate.isNotEmpty(invoice.invoiceId)){
					invoiceSequenceList = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId) , null, null, null, false);
					if(UtilValidate.isNotEmpty(invoiceSequenceList)){
						invoiceSequence = EntityUtil.getFirst(invoiceSequenceList);
						if(UtilValidate.isNotEmpty(invoiceSequence)){
							invoiceSequenceId = invoiceSequence.sequenceId;
							innerMap["invoiceSequenceId"]=invoiceSequence.sequenceId+" ["+invoice.invoiceId+"]";
						}
					}else{
						innerMap["invoiceSequenceId"]=" ["+invoice.invoiceId+"]";
					}
				}
			}
	    if ("PURCHASE_INVOICE".equals(invoice.parentTypeId)) {
			
			innerMap["partyId"]=invoice.partyIdFrom;
			innerMap["vchrType"]="PURCHASE";
			innerMap["vchrCode"]="Purchase";
			if(UtilValidate.isEmpty(invoice.invoiceMessage)){
				if(UtilValidate.isEmpty(invoice.description)){
					innerMap["description"]="Purchase Invoice";
				}else{
				innerMap["description"]=invoice.description;
				}
			}
			innerMap["crOrDbId"]="C";
			innerMap["creditValue"]=invTotalVal;
			//preparing Party Wise Map here
			dayInvoiceList = [];
			dateWiseInvoiceMap = [:];
			if(partyDayWiseDetailMap.containsKey(invoice.partyIdFrom)){
				dateWiseInvoiceMap = partyDayWiseDetailMap.get(invoice.partyIdFrom);
				
				if(dateWiseInvoiceMap.containsKey(curntDay)){
					dayInvoiceList = dateWiseInvoiceMap[curntDay];
				}
			}
			dayInvoiceList.addAll(innerMap);
			tempDateWiseInvoiceList = [];
			tempDateWiseInvoiceList.addAll(dayInvoiceList)
			dateWiseInvoiceMap.put(curntDay, tempDateWiseInvoiceList);
			tempDateWiseInvMap = [:];
			tempDateWiseInvMap.putAll(dateWiseInvoiceMap);
			partyDayWiseDetailMap.put(invoice.partyIdFrom, tempDateWiseInvMap);
	    }
	    else if ("SALES_INVOICE".equals(invoice.parentTypeId)) {
			innerMap["vchrType"]="SALES";
			innerMap["vchrCode"]="Sales";
			innerMap["partyId"]=invoice.partyId;
			innerMap["crOrDbId"]="D";
			innerMap["debitValue"]=invTotalVal;
			if(UtilValidate.isEmpty(invoice.invoiceMessage)){
				if(UtilValidate.isEmpty(invoice.description)){
					innerMap["description"]="Sales Invoice";
				}else{
				innerMap["description"]=invoice.description;
				}
			}
			//preparing Party Wise Map here
			dayInvoiceList = [];
			dateWiseInvoiceMap = [:];
			if(partyDayWiseDetailMap.containsKey(invoice.partyId)){
				dateWiseInvoiceMap = partyDayWiseDetailMap.get(invoice.partyId);
				
				if(dateWiseInvoiceMap.containsKey(curntDay)){
					dayInvoiceList = dateWiseInvoiceMap[curntDay];
				}
			}
			dayInvoiceList.addAll(innerMap);
			tempDateWiseInvoiceList = [];
			tempDateWiseInvoiceList.addAll(dayInvoiceList)
			dateWiseInvoiceMap.put(curntDay, tempDateWiseInvoiceList);
			tempDateWiseInvMap = [:];
			tempDateWiseInvMap.putAll(dateWiseInvoiceMap);
			partyDayWiseDetailMap.put(invoice.partyId, tempDateWiseInvMap);
	    }
	    else {
	        Debug.logError("InvoiceType: " + invoice.invoiceTypeId + " without a valid parentTypeId: " + invoice.parentTypeId + " !!!! Should be either PURCHASE_INVOICE or SALES_INVOICE", "");
	    }
	}
	
	invIterator.close();
	//get total/unapplied/applied payment in/out total amount:
	arPaymentDetailsMap=[:];
	arPaymentDetailsMap.put("amount",BigDecimal.ZERO);
	apPaymentDetailsMap=[:];
	apPaymentDetailsMap.put("amount",BigDecimal.ZERO);
	totalPayInApplied         = BigDecimal.ZERO;
	totalPayInNotApplied     = BigDecimal.ZERO;
	totalPayOutApplied         = BigDecimal.ZERO;
	totalPayOutNotApplied     = BigDecimal.ZERO;
	payCounter=0;
	conditionList.clear();
	conditionList.add( EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_NOT_PAID"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	if(UtilValidate.isNotEmpty(partyfromDate)&& UtilValidate.isNotEmpty(partythruDate)){
		conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
		conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
		}
	if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
	conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
	}//adding for Abstract PartyLedger
	if((UtilValidate.isNotEmpty(rolePartyIds)) || (UtilValidate.isNotEmpty(formatList))){
			conditionList.add( EntityCondition.makeCondition([
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, rolePartyIds),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, formatList)
					],EntityOperator.AND),
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyIdTo", EntityOperator.IN,formatList),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, rolePartyIds)
					],EntityOperator.AND)
				],EntityOperator.OR));
	}
	newPayCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    List<String> orderBy = UtilMisc.toList("paymentDate");
	payIterator = delegator.find("PaymentAndType", newPayCondition, null, null, orderBy, findOpts);
	partyWisePaymentDetails = [:];
	while (payment = payIterator.next()) {
		paymentDate=payment.paymentDate;
		if(payCounter==0){
			paymentFirstDate=payment.paymentDate;
		}
		GenericValue paymentTemp = delegator.findOne("Payment",UtilMisc.toMap("paymentId", payment.paymentId), false);
		payCounter=payCounter+1;
		innerMap=[:];
		curntDay=UtilDateTime.toDateString(paymentDate ,"dd-MM-yyyy");
		innerMap["partyId"]="";
		innerMap["date"]=curntDay;
		innerMap["paymentDate"]=payment.paymentDate;
		innerMap["paymentId"]=payment.paymentId;
		innerMap["vchrType"]="";
		innerMap["vchrCode"]="";
		innerMap["paymentMethodTypeId"]=payment.paymentMethodTypeId;
		innerMap["description"]=payment.comments;
		innerMap["amount"]=payment.amount;
		innerMap["crOrDbId"]="";
		innerMap["debitValue"]=0;
		innerMap["creditValue"]=0;
	    if (UtilAccounting.isDisbursement(paymentTemp) || UtilAccounting.isTaxPayment(paymentTemp)) {
			innerMap["partyId"]=payment.partyIdTo;
			innerMap["vchrType"]="DISBURSEMENT";
			innerMap["crOrDbId"]="D";
			innerMap["vchrCode"]="Payment";
			innerMap["debitValue"]=payment.amount;
			if(UtilValidate.isEmpty(payment.comments)){
				innerMap["description"]="Payment Amount";
			}
			//preparing Map here
			dayPaymentList = [];
			dateWisePaymentMap = [:];
			if(partyDayWiseDetailMap.containsKey(payment.partyIdTo)){
				dateWisePaymentMap = partyDayWiseDetailMap.get(payment.partyIdTo);
				
				if(dateWisePaymentMap.containsKey(curntDay)){
					dayPaymentList = dateWisePaymentMap[curntDay];
				}
			}
			dayPaymentList.addAll(innerMap);
			tempDateWisePaymentList = [];
			tempDateWisePaymentList.addAll(dayPaymentList)
			dateWisePaymentMap.put(curntDay, tempDateWisePaymentList);
			tempDateWisePmtMap = [:];
			tempDateWisePmtMap.putAll(dateWisePaymentMap);
			partyDayWiseDetailMap.put(payment.partyIdTo, tempDateWisePmtMap);
	    }
	    else if (UtilAccounting.isReceipt(paymentTemp)) {
			innerMap["partyId"]=payment.partyIdFrom;
			innerMap["vchrType"]="RECEIPT";
			innerMap["vchrCode"]="Receipt";
			innerMap["crOrDbId"]="C";
			innerMap["creditValue"]=payment.amount;
			if(UtilValidate.isEmpty(payment.comments)){
				innerMap["description"]="Receipt Amount";
			}
			//preparing Map here
			dayPaymentList = [];
			dateWisePaymentMap = [:];
			if(partyDayWiseDetailMap.containsKey(payment.partyIdFrom)){
				dateWisePaymentMap = partyDayWiseDetailMap.get(payment.partyIdFrom);
				
				if(dateWisePaymentMap.containsKey(curntDay)){
					dayPaymentList = dateWisePaymentMap[curntDay];
				}
			}
			dayPaymentList.addAll(innerMap);
			tempDateWisePaymentList = [];
			tempDateWisePaymentList.addAll(dayPaymentList)
			dateWisePaymentMap.put(curntDay, tempDateWisePaymentList);
			tempDateWisePmtMap = [:];
			tempDateWisePmtMap.putAll(dateWisePaymentMap);
			partyDayWiseDetailMap.put(payment.partyIdFrom, tempDateWisePmtMap);
	    }
	    else {
	        Debug.logError("PaymentTypeId: " + payment.paymentTypeId + " without a valid parentTypeId: " + payment.parentTypeId + " !!!! Should be either DISBURSEMENT, TAX_PAYMENT or RECEIPT", "");
	    }
	}
	payIterator.close();	
	TreeMap newMap = new TreeMap(partyDayWiseDetailMap);
	uniquePartIds=[];
	uniquePartIds= newMap.keySet();
	openingBalanceMap = [:];
	rolePartyIds.each{eachParty->
		arOpeningBalance  =BigDecimal.ZERO;
		apOpeningBalance  =BigDecimal.ZERO;
		arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:eachParty]));
		if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
			arOpeningBalance=arOpeningBalanceRes.get("openingBalance");
		}
		apOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:eachParty,isOBCallForAP:Boolean.TRUE]));
		if(UtilValidate.isNotEmpty(apOpeningBalanceRes)){
			apOpeningBalance=apOpeningBalanceRes.get("openingBalance");
		}
		oB=arOpeningBalance-apOpeningBalance;
		//debitValue=BigDecimal.ZERO;
		//creditValue=BigDecimal.ZERO;
		tempMap = [:];
		if(oB>0){
			debitValue=oB;
			tempMap["debitValue"]=debitValue;
		}
        if(oB<0){
				   creditValue=(-1*oB);
			       tempMap["creditValue"]=creditValue;
		}	
		tempOpeningBalanceMap = [:];
		if(UtilValidate.isNotEmpty(tempMap)){
			tempOpeningBalanceMap.putAll(tempMap);
		}
		openingBalanceMap.put(eachParty,tempOpeningBalanceMap);
	}
	context.openingBalanceMap=openingBalanceMap;
	context.partyDayWiseDetailMap=newMap;
	
	
	//old format code here.
	/*fromDate=parameters.fromDate;
	thruDate=parameters.thruDate;
	partyfromDate=parameters.partyfromDate;
	partythruDate=parameters.partythruDate;
	partyCode = parameters.partyId;
	dctx = dispatcher.getDispatchContext();
	
	isLedgerCallFor="ArOnly";
	if(parameters.isLedgerCallFor){
		isLedgerCallFor=parameters.isLedgerCallFor;
	}
	branchId = parameters.branchId;
	fromDateTime = null;
	thruDateTime = null;
	if(UtilValidate.isNotEmpty(partyfromDate)&& UtilValidate.isNotEmpty(partythruDate)){
		def sdf = new SimpleDateFormat("yyyy, MMM dd");
		try {
			fromDateTime = new java.sql.Timestamp(sdf.parse(partyfromDate).getTime());
			thruDateTime = new java.sql.Timestamp(sdf.parse(partythruDate).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: "+fromDate, "");
		}
		}
	if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");
	}
	}
	Timestamp invoiceFirstDate=null;
	Timestamp invoiceLastDate=null;
	Timestamp paymentFirstDate=null;
	Timestamp paymentLastDate=null;
	
	
	partyFinHistoryMap=[:];
	
	Boolean actualCurrency = new Boolean(context.actualCurrency);
	if (actualCurrency == null) {
		actualCurrency = true;
	}
	actualCurrencyUomId = context.actualCurrencyUomId;
	if (!actualCurrencyUomId) {
		actualCurrencyUomId = context.defaultOrganizationPartyCurrencyUomId;
	}
	findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	//get total/unapplied/applied invoices separated by sales/purch amount:
	
	for Company
	
	AR---->invoice   D...and...Payment C
	AP----->invoice  C....and...Payment D
	
	OB = arOB()- apOB()  //for
	
	if OB > 0  then Ar is big( put in Debit)
	else       then is Ap  big(put in Credit)
	
	clsoing Bal=Ob+total D -  total C
	
	totalInvSaApplied         = BigDecimal.ZERO;
	totalInvSaNotApplied     = BigDecimal.ZERO;
	totalInvPuApplied         = BigDecimal.ZERO;
	totalInvPuNotApplied     = BigDecimal.ZERO;
	
	
	//from finHistory for InterUnit Ledger
	
	partyDebits=0;
	partyCredits=0;
	partyFinHistryDayWiseMap=[];
	if(UtilValidate.isNotEmpty(context.partyTotalDebits)){
			partyDebits=context.partyTotalDebits;
		}
	if(UtilValidate.isNotEmpty(context.partyTotalCredits)){
		partyCredits=context.partyTotalCredits;
	}
	
	if(UtilValidate.isNotEmpty(context.partyDayWiseFinHistryMap)){
		partyFinHistryDayWiseMap=context.partyDayWiseFinHistryMap;
	}
	partyIds=[];
	//partyIdsList
	if(UtilValidate.isNotEmpty(context.partyIdsList)){
		partyIds=context.partyIdsList;
	}
	//Abstract Ledger Map
	partyWiseLedgerAbstractMap=[:]
	//Check for Party is Valid or Not
	result = [:];
	GenericValue partyDetail = delegator.findOne("Party",UtilMisc.toMap("partyId", parameters.partyId), false);
	if (UtilValidate.isEmpty(partyDetail)) {
		Debug.logError(parameters.partyId+ "'is not a valid Party!!", "");
		context.errorMessage = parameters.partyId+"'is not a valid Party!!";
		return result;
	}
	
	
	conditionList=[];
	
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_IN_PROCESS","INVOICE_WRITEOFF","INVOICE_CANCELLED")));
	//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_WRITEOFF"));
	//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	if(UtilValidate.isNotEmpty(partyfromDate)&& UtilValidate.isNotEmpty(partythruDate)){
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
		conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
		}
	if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
	}
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, parameters.partyId))
	
	//adding for Abstract PartyLedger
	if((UtilValidate.isNotEmpty(parameters.partyId)) && (UtilValidate.isNotEmpty(parameters.branchId))){
	conditionList.add( EntityCondition.makeCondition([
					EntityCondition.makeCondition([
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId),
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId)
						],EntityOperator.AND),
					EntityCondition.makeCondition([
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,branchId),
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
						],EntityOperator.AND)
					],EntityOperator.OR));
		
	}else if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add( EntityCondition.makeCondition([
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, null)
					],EntityOperator.AND),
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
					],EntityOperator.AND)
				],EntityOperator.OR));
	}
	newInvCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
		List<String> payOrderBy = UtilMisc.toList("invoiceDate");
	tempInvIterator=null;
	invIterator = delegator.find("InvoiceAndType", newInvCondition, null, null, payOrderBy, findOpts);
	//Debug.log("==invIterator===="+invIterator+"=invExprs==="+newInvCondition);
	tempInvIterator=invIterator;
	
	invoiceIdsList=EntityUtil.getFieldListFromEntityListIterator(tempInvIterator, "invoiceId", true);
	
	Debug.log("==invoiceIdsList**************=="+invoiceIdsList+"====");
	
	invoiceDateList=EntityUtil.getFieldListFromEntityListIterator(tempInvIterator, "invoiceDate", true);
	invoiceFirstDate=invoiceDateList.getFirst();
	invoiceLastDate=invoiceDateList.getLast();
	Debug.log("==invoiceDateList**************=="+invoiceDateList+"====");
	Debug.log("==invoiceDate=FF=="+invoiceDateList.getFirst());
	Debug.log("==invoiceDate=EE=="+invoiceDateList.getLast());
	
	
	arInvoiceDetailsMap=[:];
	arInvoiceDetailsMap.put("invTotal", BigDecimal.ZERO);
	apInvoiceDetailsMap=[:];
	apInvoiceDetailsMap.put("invTotal", BigDecimal.ZERO);
	invCounter=0;
	while (invoice = invIterator.next()) {
		invoiceDate=invoice.invoiceDate;
		if(invCounter==0){
			invoiceFirstDate=invoice.invoiceDate;
		}
		invCounter=invCounter+1;
		innerMap=[:];
		curntDay=UtilDateTime.toDateString(invoiceDate ,"dd-MM-yyyy");
		//Debug.log("=curntDay=="+curntDay);
		//Debug.log("=curntDay=="+curntDay+"==invId=="+invoice.invoiceId);
		innerMap["partyId"]="";
		innerMap["date"]=curntDay;
		innerMap["purposeTypeId"]=invoice.purposeTypeId;
		innerMap["description"]=invoice.invoiceMessage;
		innerMap["invoiceDate"]=invoice.invoiceDate;
		innerMap["invoiceId"]=invoice.invoiceId;
		innerMap["invoiceSequenceId"]="";
		innerMap["tinNumber"]="";
		innerMap["vchrType"]="";
		innerMap["vchrCode"]="";
		innerMap["crOrDbId"]="";
		invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
		//invTotalVal=invTotalVal-vatRevenue;
		innerMap["invTotal"]=invTotalVal;
		innerMap["debitValue"]=0;
		innerMap["creditValue"]=0;
		//get Sequence for invoiceId
		//invoiceSequenceId = null;
		//for abstract PartyLedger it should ignore
			if(UtilValidate.isEmpty(context.partyIdsList)){
				if(UtilValidate.isNotEmpty(invoice.invoiceId)){
					invoiceSequenceList = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoice.invoiceId) , null, null, null, false);
					if(UtilValidate.isNotEmpty(invoiceSequenceList)){
						invoiceSequence = EntityUtil.getFirst(invoiceSequenceList);
						if(UtilValidate.isNotEmpty(invoiceSequence)){
							invoiceSequenceId = invoiceSequence.sequenceId;
							innerMap["invoiceSequenceId"]=invoiceSequence.sequenceId+" ["+invoice.invoiceId+"]";
						}
					}else{
						innerMap["invoiceSequenceId"]=" ["+invoice.invoiceId+"]";
					}
				}
			}
		if ("PURCHASE_INVOICE".equals(invoice.parentTypeId)) {
			innerMap["partyId"]=invoice.partyIdFrom;
			innerMap["vchrType"]="PURCHASE";
			innerMap["vchrCode"]="Purchase";
			if(UtilValidate.isEmpty(invoice.invoiceMessage)){
				if(UtilValidate.isEmpty(invoice.description)){
					innerMap["description"]="Purchase Invoice";
				}else{
				innerMap["description"]=invoice.description;
				}
			}
			innerMap["crOrDbId"]="C";
			innerMap["creditValue"]=invTotalVal;
			//preparing Map here
			dayInvoiceList=[];
			dayInvoiceList=apInvoiceDetailsMap[curntDay];
			//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayInvoiceList);
			if(UtilValidate.isEmpty(dayInvoiceList)){
				dayTempInvoiceList=[];
				dayTempInvoiceList.addAll(innerMap);
				dayInvoiceList=dayTempInvoiceList;
				apInvoiceDetailsMap.put(curntDay, dayInvoiceList);
			}else{
				 dayInvoiceList.addAll(innerMap);
				apInvoiceDetailsMap.put(curntDay, dayInvoiceList);
			}
			apInvoiceDetailsMap["invTotal"]+=invTotalVal;
			//adding Abstract Map here
			if("ApOnly"==isLedgerCallFor){
				partAbstractLedger=partyWiseLedgerAbstractMap.get(invoice.partyIdFrom);
				if(UtilValidate.isEmpty(partAbstractLedger)){
					   abstractInnerMap=[:];
					   abstractInnerMap["OB"]=0;
					   abstractInnerMap["partyId"]=invoice.partyIdFrom;
					   abstractInnerMap["invTotal"]=invTotalVal;
					   abstractInnerMap["paymentTotal"]=0;
					   abstractInnerMap["debitValue"]=0;
					   abstractInnerMap["creditValue"]=invTotalVal;
				 partyWiseLedgerAbstractMap[invoice.partyIdFrom]=abstractInnerMap;
				}else{
				Map abstractInnerMap=partyWiseLedgerAbstractMap.get(invoice.partyIdFrom);
				abstractInnerMap["invTotal"]+=invTotalVal;
				abstractInnerMap["creditValue"]+=invTotalVal;
				partyWiseLedgerAbstractMap[invoice.partyIdFrom]=abstractInnerMap;
				}
			}
		}
		else if ("SALES_INVOICE".equals(invoice.parentTypeId)) {
			innerMap["vchrType"]="SALES";
			innerMap["vchrCode"]="Sales";
			innerMap["partyId"]=invoice.partyId;
			innerMap["crOrDbId"]="D";
			innerMap["debitValue"]=invTotalVal;
			if(UtilValidate.isEmpty(invoice.invoiceMessage)){
				if(UtilValidate.isEmpty(invoice.description)){
					innerMap["description"]="Sales Invoice";
				}else{
				innerMap["description"]=invoice.description;
				}
			}
			//preparing Map here
			dayInvoiceList=[];
			dayInvoiceList=arInvoiceDetailsMap[curntDay];
			//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayInvoiceList);
			if(UtilValidate.isEmpty(dayInvoiceList)){
				dayTempInvoiceList=[];
				dayTempInvoiceList.addAll(innerMap);
				dayInvoiceList=dayTempInvoiceList;
				arInvoiceDetailsMap.put(curntDay, dayInvoiceList);
			}else{
				 dayInvoiceList.addAll(innerMap);
				arInvoiceDetailsMap.put(curntDay, dayInvoiceList);
			}
			arInvoiceDetailsMap["invTotal"]+=invTotalVal;
			//adding Abstract Map here
			if("ArOnly"==isLedgerCallFor){
			  partAbstractLedger=partyWiseLedgerAbstractMap.get(invoice.partyId);
			  if(UtilValidate.isEmpty(partAbstractLedger)){
					   abstractInnerMap=[:];
					   abstractInnerMap["OB"]=0;
					   abstractInnerMap["partyId"]=invoice.partyId;
					   abstractInnerMap["invTotal"]=invTotalVal;
					   abstractInnerMap["paymentTotal"]=0;
					   abstractInnerMap["debitValue"]=invTotalVal;
					   abstractInnerMap["creditValue"]=0;
				 partyWiseLedgerAbstractMap[invoice.partyId]=abstractInnerMap;
				}else{
				Map abstractInnerMap=partyWiseLedgerAbstractMap.get(invoice.partyId);
					abstractInnerMap["invTotal"]+=invTotalVal;
					abstractInnerMap["debitValue"]+=invTotalVal;
				partyWiseLedgerAbstractMap[invoice.partyId]=abstractInnerMap;
				}
			}
			
		}
		else {
			Debug.logError("InvoiceType: " + invoice.invoiceTypeId + " without a valid parentTypeId: " + invoice.parentTypeId + " !!!! Should be either PURCHASE_INVOICE or SALES_INVOICE", "");
		}
	}
	
	invIterator.close();
	
	//get total/unapplied/applied payment in/out total amount:
	
	arPaymentDetailsMap=[:];
	arPaymentDetailsMap.put("amount",BigDecimal.ZERO);
	apPaymentDetailsMap=[:];
	apPaymentDetailsMap.put("amount",BigDecimal.ZERO);
	
	totalPayInApplied         = BigDecimal.ZERO;
	totalPayInNotApplied     = BigDecimal.ZERO;
	totalPayOutApplied         = BigDecimal.ZERO;
	totalPayOutNotApplied     = BigDecimal.ZERO;
	
	
	payCounter=0;
	conditionList.clear();
	conditionList.add( EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_NOTPAID"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	if(UtilValidate.isNotEmpty(partyfromDate)&& UtilValidate.isNotEmpty(partythruDate)){
		conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
		conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
		}
	if(UtilValidate.isNotEmpty(fromDate)&& UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)))
	conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)))
	}//adding for Abstract PartyLedger
	if((UtilValidate.isNotEmpty(parameters.partyId)) && (UtilValidate.isNotEmpty(parameters.branchId))){
	conditionList.add( EntityCondition.makeCondition([
					EntityCondition.makeCondition([
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, parameters.partyId),
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.branchId)
						],EntityOperator.AND),
					EntityCondition.makeCondition([
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,parameters.branchId),
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
						],EntityOperator.AND)
					],EntityOperator.OR));
		
	}else if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition([
				   EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, parameters.partyId),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, null)
					], EntityOperator.AND),
				EntityCondition.makeCondition([
					EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, null),
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
					], EntityOperator.AND)
				], EntityOperator.OR));
	}
	newPayCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	
		List<String> orderBy = UtilMisc.toList("paymentDate");
	payIterator = delegator.find("PaymentAndType", newPayCondition, null, null, orderBy, findOpts);
	
	while (payment = payIterator.next()) {
		paymentDate=payment.paymentDate;
		if(payCounter==0){
			paymentFirstDate=payment.paymentDate;
		}
		GenericValue paymentTemp = delegator.findOne("Payment",UtilMisc.toMap("paymentId", payment.paymentId), false);
		payCounter=payCounter+1;
		innerMap=[:];
		curntDay=UtilDateTime.toDateString(paymentDate ,"dd-MM-yyyy");
		//Debug.log("=curntDay===in=Payment=="+curntDay+"==paymentId=");
		innerMap["partyId"]="";
		innerMap["date"]=curntDay;
		innerMap["paymentDate"]=payment.paymentDate;
		innerMap["paymentId"]=payment.paymentId;
		innerMap["vchrType"]="";
		innerMap["vchrCode"]="";
		innerMap["paymentMethodTypeId"]=payment.paymentMethodTypeId;
		innerMap["description"]=payment.comments;
		innerMap["amount"]=payment.amount;
		innerMap["crOrDbId"]="";
		innerMap["debitValue"]=0;
		innerMap["creditValue"]=0;
		
		if (UtilAccounting.isDisbursement(paymentTemp) || UtilAccounting.isTaxPayment(paymentTemp)) {
			innerMap["partyId"]=payment.partyIdTo;
			innerMap["vchrType"]="DISBURSEMENT";
			innerMap["crOrDbId"]="D";
			innerMap["vchrCode"]="Payment";
			innerMap["debitValue"]=payment.amount;
			if(UtilValidate.isEmpty(payment.comments)){
				innerMap["description"]="Payment Amount";
			}
			//preparing Map here
			dayPaymentList=[];
			dayPaymentList=apPaymentDetailsMap[curntDay];
			//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayPaymentList);
			if(UtilValidate.isEmpty(dayPaymentList)){
				dayTempPaymentList=[];
				dayTempPaymentList.addAll(innerMap);
				dayPaymentList=dayTempPaymentList;
				apPaymentDetailsMap.put(curntDay, dayPaymentList);
			}else{
				 dayPaymentList.addAll(innerMap);
				apPaymentDetailsMap.put(curntDay, dayPaymentList);
			}
			apPaymentDetailsMap.put("amount",apPaymentDetailsMap.get("amount").add(payment.amount));
			
			//adding Abstract Map here
			if("ApOnly"==isLedgerCallFor){
				partAbstractLedger=partyWiseLedgerAbstractMap.get(payment.partyIdTo);
				if(UtilValidate.isEmpty(partAbstractLedger)){
					   abstractInnerMap=[:];
					   abstractInnerMap["OB"]=0;
					   abstractInnerMap["partyId"]=payment.partyIdTo;
					   abstractInnerMap["invTotal"]=0;
					   abstractInnerMap["paymentTotal"]=payment.amount;
					   abstractInnerMap["debitValue"]=payment.amount;
					   abstractInnerMap["creditValue"]=0;
				partyWiseLedgerAbstractMap[payment.partyIdTo]=abstractInnerMap;
				}else{
				Map abstractInnerMap=partyWiseLedgerAbstractMap.get(payment.partyIdTo);
				abstractInnerMap["paymentTotal"]+=payment.amount;
				abstractInnerMap["debitValue"]+=payment.amount;
				partyWiseLedgerAbstractMap[payment.partyIdTo]=abstractInnerMap;
				}
			}
		}
		else if (UtilAccounting.isReceipt(paymentTemp)) {
			innerMap["partyId"]=payment.partyIdFrom;
			innerMap["vchrType"]="RECEIPT";
			innerMap["vchrCode"]="Receipt";
			innerMap["crOrDbId"]="C";
			innerMap["creditValue"]=payment.amount;
			if(UtilValidate.isEmpty(payment.comments)){
				innerMap["description"]="Receipt Amount";
			}
			//preparing Map here
			dayPaymentList=[];
			dayPaymentList=arPaymentDetailsMap[curntDay];
			//Debug.log("=curntDay=="+curntDay+"==dayInvoiceList=="+dayPaymentList);
			if(UtilValidate.isEmpty(dayPaymentList)){
				dayTempPaymentList=[];
				dayTempPaymentList.addAll(innerMap);
				dayPaymentList=dayTempPaymentList;
				arPaymentDetailsMap.put(curntDay, dayPaymentList);
			}else{
				 dayPaymentList.addAll(innerMap);
				arPaymentDetailsMap.put(curntDay, dayPaymentList);
			}
			arPaymentDetailsMap.put("amount",arPaymentDetailsMap.get("amount").add(payment.amount));
			
			//adding Abstract Map here
			if("ArOnly"==isLedgerCallFor){
				partAbstractLedger=partyWiseLedgerAbstractMap.get(payment.partyIdFrom);
				if(UtilValidate.isEmpty(partAbstractLedger)){
					   abstractInnerMap=[:];
					   abstractInnerMap["OB"]=0;
					   abstractInnerMap["partyId"]=payment.partyIdFrom;
					   abstractInnerMap["invTotal"]=0;
					   abstractInnerMap["paymentTotal"]=payment.amount;
					   abstractInnerMap["creditValue"]=payment.amount;
					   abstractInnerMap["debitValue"]=0;
				partyWiseLedgerAbstractMap[payment.partyIdFrom]=abstractInnerMap;
				}else{
				Map abstractInnerMap=partyWiseLedgerAbstractMap.get(payment.partyIdFrom);
				abstractInnerMap["paymentTotal"]+=payment.amount;
				abstractInnerMap["creditValue"]+=payment.amount;
				partyWiseLedgerAbstractMap[payment.partyIdFrom]=abstractInnerMap;
				}
			}
			
		}
		else {
			Debug.logError("PaymentTypeId: " + payment.paymentTypeId + " without a valid parentTypeId: " + payment.parentTypeId + " !!!! Should be either DISBURSEMENT, TAX_PAYMENT or RECEIPT", "");
		}
	}
	payIterator.close();
	//finalMap prepration
	if(UtilValidate.isEmpty(fromDate)){
		if(paymentFirstDate<invoiceFirstDate){
			fromDateTime=paymentFirstDate;
		}else{
		fromDateTime=invoiceFirstDate;
		}
	}
	if(UtilValidate.isEmpty(partyfromDate)){
		if(paymentFirstDate<invoiceFirstDate){
			fromDateTime=paymentFirstDate;
		}else{
		fromDateTime=invoiceFirstDate;
		}
	}
	dayBegin = UtilDateTime.getDayStart(fromDateTime);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime);
	context.fromDate=dayBegin;
	context.thruDate=dayEnd;
	//Debug.log("=curntDay===in=dayBegin=="+dayBegin+"==dayEnd==="+dayEnd);
	//Debug.log("=======arInvoiceDetailsMap==>"+arInvoiceDetailsMap);
	//Debug.log("=======apInvoiceDetailsMap==>"+apInvoiceDetailsMap);
	totalDays= UtilDateTime.getIntervalInDays(dayBegin,dayEnd)+1;
	dayWiseArDetailMap=[:];
	dayWiseApDetailMap=[:];
	partyDayWiseDetailMap=[:];
	for(int j=0 ; j < (totalDays); j++){
		Timestamp saleDate = UtilDateTime.addDaysToTimestamp(dayBegin, j);
		reciepts = BigDecimal.ZERO;
		totalValue = BigDecimal.ZERO;
		curntDay=UtilDateTime.toDateString(saleDate ,"dd-MM-yyyy");
		
		dayDetailMap=[:]
		dayInvoiceList=[];
		dayPaymentList=[];
		//ar map preparation
		newTempMap=[:];
		arInvList=arInvoiceDetailsMap.get(curntDay);
		arPayList=arPaymentDetailsMap.get(curntDay);
		if(UtilValidate.isNotEmpty(arInvList) || UtilValidate.isNotEmpty(arPayList)){
			newTempMap.put("invoiceList", arInvList);
			newTempMap.put("paymentList", arPayList);
			dayWiseArDetailMap[curntDay]=newTempMap;
			//preparing Anothor List
			if(UtilValidate.isNotEmpty(arInvList)){
				dayInvoiceList.addAll(arInvList);
			}
			if(UtilValidate.isNotEmpty(arPayList)){
				dayPaymentList.addAll(arPayList);
			}
		}
		//AP map preparation
		newApTempMap=[:];
		invList=apInvoiceDetailsMap.get(curntDay);
		payList=apPaymentDetailsMap.get(curntDay);
		if(UtilValidate.isNotEmpty(invList) || UtilValidate.isNotEmpty(payList)){
			newApTempMap.put("invoiceList", invList);
			newApTempMap.put("paymentList", payList);
			dayWiseApDetailMap[curntDay]=newApTempMap;
			//preparing Anothor List
			if(UtilValidate.isNotEmpty(invList)){
				dayInvoiceList.addAll(invList);
			}
			if(UtilValidate.isNotEmpty(payList)){
				dayPaymentList.addAll(payList);
			}
		}
		//adding PartyFinAccountHistory always treat them as payments
		if(UtilValidate.isNotEmpty(partyFinHistryDayWiseMap)){
			partyDayFinList=partyFinHistryDayWiseMap.get(curntDay);
			if(UtilValidate.isNotEmpty(partyDayFinList)){
				dayPaymentList.addAll(partyDayFinList);
			}
		}
		//dayWise PartyMap prepartion
		if(UtilValidate.isNotEmpty(dayInvoiceList) || UtilValidate.isNotEmpty(dayPaymentList)){
			dayDetailMap.put("invoiceList", dayInvoiceList);
			dayDetailMap.put("paymentList", dayPaymentList);
			partyDayWiseDetailMap[curntDay]=dayDetailMap;
		}
	}
	dayWiseArDetailMap["totInvoiceValue"]=arInvoiceDetailsMap.get("invTotal");
	dayWiseArDetailMap["totPaymentValue"]=arPaymentDetailsMap.get("amount");
	
	dayWiseApDetailMap["totInvoiceValue"]=apInvoiceDetailsMap.get("invTotal");
	dayWiseApDetailMap["totPaymentValue"]=apInvoiceDetailsMap.get("amount");
	context.arInvoiceDetailsMap=arInvoiceDetailsMap;
	context.apInvoiceDetailsMap=apInvoiceDetailsMap;
	
	context.arPaymentDetailsMap=arPaymentDetailsMap;
	context.apPaymentDetailsMap=apPaymentDetailsMap;
	
	context.dayWiseArDetailMap=dayWiseArDetailMap;
	context.dayWiseApDetailMap=dayWiseApDetailMap;
	
	//Debug.log("=======dayWiseArDetailMap==>"+dayWiseArDetailMap);
	//Debug.log("=======dayWiseApDetailMap==>"+dayWiseApDetailMap);
	
	arOpeningBalance  =BigDecimal.ZERO;
	arClosingBalance  =BigDecimal.ZERO;
	
	apOpeningBalance  =BigDecimal.ZERO;
	apClosingBalance  =BigDecimal.ZERO;
	
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:parameters.partyId]));
		//openingBalance2 = (in.vasista.vbiz.byproducts.ByProductNetworkServices.getOpeningBalanceForParty( dctx , [userLogin: userLogin, saleDate: dayBegin, partyId:parameters.partyId])).get("openingBalance");
		//Debug.log("=======openingBalance====openingBalance2===>"+openingBalance2);
		if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
			arOpeningBalance=arOpeningBalanceRes.get("openingBalance");
		}
		apOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:parameters.partyId,isOBCallForAP:Boolean.TRUE]));
		//Debug.log("=======arOpeningBalance======>"+arOpeningBalanceRes+"=apOpeningBalance==="+apOpeningBalanceRes);
		if(UtilValidate.isNotEmpty(apOpeningBalanceRes)){
			apOpeningBalance=apOpeningBalanceRes.get("openingBalance");
		}
	}
		
	partyTrTotalMap=[:];
	partyTrTotalMap["debitValue"]=BigDecimal.ZERO;
	partyTrTotalMap["creditValue"]=BigDecimal.ZERO;
	//so PartyTotal Debit means Ar_Invoice+Ap_Payment+(Ar OB with Minus)+ ap OB with Plus
	//             Credit means Ap_Invoice+Ar_Payment+(Ar OB with Plus)+ ap  OB with Plus
	partyTrTotalMap["debitValue"]+=(arInvoiceDetailsMap.get("invTotal")+apPaymentDetailsMap.get("amount"));
	
	partyTrTotalMap["creditValue"]+=(apInvoiceDetailsMap.get("invTotal")+arPaymentDetailsMap.get("amount"));
	
	
	
	partyTotalMap=[:];
	partyTotalMap["debitValue"]=BigDecimal.ZERO;
	partyTotalMap["creditValue"]=BigDecimal.ZERO;
	
	//adding FinHistoryTotal here
	partyTrTotalMap["debitValue"]+=(partyDebits);
	partyTrTotalMap["creditValue"]+=(partyCredits);
	
	//Debug.log("====partyTrTotalMap==First====>"+partyTrTotalMap);
	
	//compute OB logic for Credit or Debit
	partyOBMap=[:];
	partyOBMap["debitValue"]=BigDecimal.ZERO;
	partyOBMap["creditValue"]=BigDecimal.ZERO;
	
	oB=arOpeningBalance-apOpeningBalance;
	if(oB>0){
		partyOBMap["debitValue"]=oB;
		partyTotalMap["debitValue"]+=(oB);
	}else{
	  partyOBMap["creditValue"]=(-1*oB);
	  partyTotalMap["creditValue"]+=(-1*oB) ;
	}
	partyTotalMap["debitValue"]+=(partyTrTotalMap["debitValue"]) ;
	partyTotalMap["creditValue"]+=(partyTrTotalMap["creditValue"]) ;
	//compute CB logic for Credit or Debit
	partyCBMap=[:];
	partyCBMap["debitValue"]=BigDecimal.ZERO;
	partyCBMap["creditValue"]=BigDecimal.ZERO;
	cB=partyTotalMap["debitValue"]-partyTotalMap["creditValue"];
	if(cB>0){
		partyCBMap["debitValue"]=cB;
	}else{
	  partyCBMap["creditValue"]= (-1*cB);
	}
	
	context.partyOBMap=partyOBMap;
	context.partyTrTotalMap=partyTrTotalMap;
	context.partyCBMap=partyCBMap;
	
		arClosingBalance=(arOpeningBalance+arInvoiceDetailsMap.get("invTotal"))-(arPaymentDetailsMap.get("amount"));
		
		apClosingBalance=(apOpeningBalance+apInvoiceDetailsMap.get("invTotal"))-(apPaymentDetailsMap.get("amount"));
		
		//Debug.log("====FINALLL==ARRRR=arClosingBalance==>"+arClosingBalance+"=arOpeningBalance==="+arOpeningBalance);
		//Debug.log("====FINALLL=APPPP==apClosingBalance==>"+apClosingBalance+"=apOpeningBalance==="+apOpeningBalance);
		
		context.partyDayWiseDetailMap=partyDayWiseDetailMap;
		
		context.arOpeningBalance=arOpeningBalance;
		context.arClosingBalance=arClosingBalance;
		
		context.apOpeningBalance=apOpeningBalance;
		context.apClosingBalance=apClosingBalance;
		
		
		partyWiseLedgerAbstractMap.each{partyEntry->
			partyId=partyEntry.getKey();
			
			Map partyAbstractTotalInner=partyEntry.getValue();
			
			partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
			partyAbstractTotalInner.putAt("partyName",partyName);
			
			arPartyOB  =BigDecimal.ZERO;
			arPartyCB  =BigDecimal.ZERO;
			
			apPartyOB  =BigDecimal.ZERO;
			apPartyCB  =BigDecimal.ZERO;
			
			if("ArOnly"==isLedgerCallFor){
				arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:partyId]));
				if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
					arPartyOB=arOpeningBalanceRes.get("openingBalance");
				}
				arPartyCB=(arPartyOB+partyAbstractTotalInner.get("invTotal"))-(partyAbstractTotalInner.get("paymentTotal"));
				partyAbstractTotalInner.putAt("OB",arPartyOB);
				partyAbstractTotalInner.putAt("CB",arPartyCB);
			}else{
				apOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate: dayBegin, partyId:partyId,isOBCallForAP:Boolean.TRUE]));
				if(UtilValidate.isNotEmpty(apOpeningBalanceRes)){
					apPartyOB=apOpeningBalanceRes.get("openingBalance");
				}
				apPartyCB=(apPartyCB+partyAbstractTotalInner.get("invTotal"))-(partyAbstractTotalInner.get("paymentTotal"));
				partyAbstractTotalInner.putAt("OB",apPartyOB);
				partyAbstractTotalInner.putAt("CB",apPartyCB);
			}
			
		}
		context.partyWiseLedgerAbstractMap=partyWiseLedgerAbstractMap;
		//Debug.log("=====partyWiseLedgerAbstractMap================>"+partyWiseLedgerAbstractMap);
		
	//transferAmount = totalInvSaApplied.add(totalInvSaNotApplied).subtract(totalInvPuApplied.add(totalInvPuNotApplied)).subtract(totalPayInApplied.add(totalPayInNotApplied).add(totalPayOutApplied.add(totalPayOutNotApplied)));
	
	
		//for finAccountTrans
		localtotalPartyDayWiseFinHistryOpeningBal=[:];
		if(UtilValidate.isNotEmpty(context.totalPartyDayWiseFinHistryOpeningBal)){
			localtotalPartyDayWiseFinHistryOpeningBal=context.totalPartyDayWiseFinHistryOpeningBal;
		}
		//Debug.log("totalPartyDayWiseFinHistryOpeningBal===================************************"+localtotalPartyDayWiseFinHistryOpeningBal);
		FinTotalMap=[:];
		FinTotalMap["debitValue"]=BigDecimal.ZERO;
		FinTotalMap["creditValue"]=BigDecimal.ZERO;
		Debug.log("localtotalPartyDayWiseFinHistryOpeningBal=========================="+localtotalPartyDayWiseFinHistryOpeningBal);
		for(Map.Entry entry : localtotalPartyDayWiseFinHistryOpeningBal.entrySet()){
			finTransactions=entry.getValue();
					FinTotalMap["debitValue"]+=finTransactions.get("FinTransMap").get("debitValue");
					FinTotalMap["creditValue"]+=finTransactions.get("FinTransMap").get("creditValue");
		}
		partyTotalTrasnsMap=[:];
		partyTotalTrasnsMap["debitValue"]=BigDecimal.ZERO;
		partyTotalTrasnsMap["creditValue"]=BigDecimal.ZERO;
		partyTotalTrasnsMap["debitValue"]=partyTrTotalMap["debitValue"]-FinTotalMap["debitValue"];
		partyTotalTrasnsMap["creditValue"]=partyTrTotalMap["creditValue"]-FinTotalMap["creditValue"];
		partyTotalTrsMap=[:];
		partyTotalTrsMap["debitValue"]=BigDecimal.ZERO;
		partyTotalTrsMap["creditValue"]=BigDecimal.ZERO;
		partyTotalTrsMap["debitValue"]+=partyTotalTrasnsMap["debitValue"];
		partyTotalTrsMap["creditValue"]+=partyTotalTrasnsMap["creditValue"];
		if(oB>0){
			partyTotalTrsMap["debitValue"]+=(oB);
			
		}else{
		  partyTotalTrsMap["creditValue"]+=(-1*oB) ;
		}
		partyTotalCBMap=[:];
		partyTotalCBMap["debitValue"]=BigDecimal.ZERO;
		partyTotalCBMap["creditValue"]=BigDecimal.ZERO;
		tcB=partyTotalTrsMap["debitValue"]-partyTotalTrsMap["creditValue"];
		if(tcB>0){
			partyTotalCBMap["debitValue"]=tcB;
		}else{
		  partyTotalCBMap["creditValue"]= (-1*tcB);
		}
		context.partyTotalTrasnsMap=partyTotalTrasnsMap;
		context.partyTotalCBMap=partyTotalCBMap;
		context.FinTotalMap=FinTotalMap;
	*/