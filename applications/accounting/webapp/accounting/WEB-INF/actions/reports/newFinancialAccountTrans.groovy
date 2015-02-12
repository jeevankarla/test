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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.util.UtilAccounting;
import java.math.BigDecimal;
import com.ibm.icu.util.Calendar;
import org.ofbiz.base.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import javolution.util.FastMap;


if (organizationPartyId) {
    onlyIncludePeriodTypeIdList = [];
    onlyIncludePeriodTypeIdList.add("FISCAL_MONTH");
    customTimePeriodResults = dispatcher.runSync("findCustomTimePeriods", [findDate : UtilDateTime.nowTimestamp(), organizationPartyId : organizationPartyId, onlyIncludePeriodTypeIdList : onlyIncludePeriodTypeIdList, userLogin : userLogin]);
	customTimePeriodList = customTimePeriodResults.customTimePeriodList;
    if (UtilValidate.isNotEmpty(customTimePeriodList)) {
        context.timePeriod = customTimePeriodList.first().customTimePeriodId;
    }
    decimals = UtilNumber.getBigDecimalScale("ledger.decimals");
    rounding = UtilNumber.getBigDecimalRoundingMode("ledger.rounding");
    context.currentOrganization = delegator.findOne("PartyNameView", [partyId : organizationPartyId], false);
	glAccountId = null;
	if (parameters.glAccountId) {
		glAccountId = parameters.glAccountId;
        glAccount = delegator.findOne("GlAccount", [glAccountId : glAccountId], false);
        isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
        context.isDebitAccount = isDebitAccount;
        context.glAccount = glAccount;
    }
	if (parameters.finAccountId) {
		finAccountId=parameters.finAccountId;
		finAccount = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);//to only showing postedGlAccountId
		if(UtilValidate.isNotEmpty(finAccount.postToGlAccountId)){
		postedGlAccount=finAccount.postToGlAccountId;
		glAccount = delegator.findOne("GlAccount", [glAccountId : postedGlAccount], false);
		glAccountId = glAccount.glAccountId;
		isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
		context.isDebitAccount = isDebitAccount;
		context.glAccount = glAccount;
		}
	}
    currentTimePeriod = null;
    BigDecimal balanceOfTheAcctgForYear = BigDecimal.ZERO;
	openingBalance = BigDecimal.ZERO;
	
	finAccountCustomTimePeriodId = null;
	effectiveFromDate = null;
	effectiveThruDate = null;
	effectiveFromDateStr = parameters.fromDate;
	effectiveThruDateStr = parameters.thruDate;
	
	if (UtilValidate.isEmpty(effectiveFromDateStr)) {
		effectiveFromDate = UtilDateTime.nowTimestamp();
	}
	else{
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			effectiveFromDate = new java.sql.Timestamp(sdf.parse(effectiveFromDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveFromDateStr, "");
		}
	}
	
	if (UtilValidate.isEmpty(effectiveThruDateStr)) {
		effectiveThruDate = effectiveFromDate;
	}
	else{
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			effectiveThruDate = new java.sql.Timestamp(sdf.parse(effectiveThruDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveThruDateStr, "");
		}
	}
	
	monthBegin = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(effectiveFromDate), timeZone, locale);
	monthEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(effectiveThruDate), timeZone, locale);
	
	dayBegin = UtilDateTime.getDayStart(effectiveFromDate);
	dayEnd = UtilDateTime.getDayEnd(effectiveThruDate);
	
	fromDateStr = null;
	fromDateStr = UtilDateTime.toDateString(effectiveFromDate ,"MMMM dd, yyyy");
	fromDateTimestamp=UtilDateTime.getDayStart(effectiveFromDate);
	context.put("fromDateStr",fromDateStr);
	
	thruDateStr = null;
	thruDateStr = UtilDateTime.toDateString(effectiveThruDate ,"MMMM dd, yyyy");
	thruDateTimestamp=UtilDateTime.getDayEnd(effectiveThruDate);
	context.put("thruDateStr",thruDateStr);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_YEAR"));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(dayBegin)));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(dayEnd)));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	customTimePeriodList = delegator.findList("CustomTimePeriod", condition, null, null, null, true);
	if(UtilValidate.isNotEmpty(customTimePeriodList)){
		finAccountCustomTimePeriodId = customTimePeriodList[0].customTimePeriodId;
	}
    if (finAccountCustomTimePeriodId) {
        currentTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : finAccountCustomTimePeriodId], false);
        previousTimePeriodResult = dispatcher.runSync("getPreviousTimePeriod", 
                [customTimePeriodId : finAccountCustomTimePeriodId, userLogin : userLogin]);
        previousTimePeriod = previousTimePeriodResult.previousTimePeriod;
        if (UtilValidate.isNotEmpty(previousTimePeriod)) {
            glAccountHistory = delegator.findOne("GlAccountHistory", 
                    [customTimePeriodId : previousTimePeriod.customTimePeriodId, glAccountId : glAccountId, organizationPartyId : organizationPartyId], false);
			if (glAccountHistory && glAccountHistory.endingBalance != null) {
				openingBalance = glAccountHistory.endingBalance;
				context.openingBalance = glAccountHistory.endingBalance;
                balanceOfTheAcctgForYear = glAccountHistory.endingBalance;
            } else {
				context.openingBalance = BigDecimal.ZERO;
            }
        }
    }
	if (UtilValidate.isNotEmpty(parameters.openingBalance)) {
		openingBalance = new BigDecimal(parameters.openingBalance);
	}
	
	financialAcctgTransList = [];
	glAcctgTrialBalanceList = [];
	paymentInvType = [:];
    if (currentTimePeriod && (UtilValidate.isNotEmpty(glAccountId))){
        context.currentTimePeriod = currentTimePeriod;
        customTimePeriodStartDate = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(currentTimePeriod.fromDate), timeZone, locale);
        customTimePeriodEndDate = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(currentTimePeriod.fromDate), timeZone, locale);
		
        Calendar calendarTimePeriodStartDate = UtilDateTime.toCalendar(customTimePeriodStartDate);
        BigDecimal totalOfYearToDateDebit = BigDecimal.ZERO;
        BigDecimal totalOfYearToDateCredit = BigDecimal.ZERO;
        isPosted = parameters.isPosted;

       // while (customTimePeriodEndDate <= UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(currentTimePeriod.thruDate), 1)){
            if ("ALL".equals(isPosted)) {
                isPosted = "";
            }
			acctgTransEntriesAndTransTotal = dispatcher.runSync("getAcctgTransEntriesAndTransTotal",
				[customTimePeriodStartDate : dayBegin, customTimePeriodEndDate : dayEnd, organizationPartyId : organizationPartyId, glAccountId : glAccountId, isPosted : isPosted, userLogin : userLogin]);
			 /*acctgTransEntriesAndTransTotal = dispatcher.runSync("getAcctgTransEntriesAndTransTotal", 
                    [customTimePeriodStartDate : customTimePeriodStartDate, customTimePeriodEndDate : customTimePeriodEndDate, organizationPartyId : organizationPartyId, glAccountId : glAccountId, isPosted : isPosted, userLogin : userLogin]);*/
            totalOfYearToDateDebit = totalOfYearToDateDebit + acctgTransEntriesAndTransTotal.debitTotal;
            acctgTransEntriesAndTransTotal.totalOfYearToDateDebit = totalOfYearToDateDebit.setScale(decimals, rounding);
            totalOfYearToDateCredit = totalOfYearToDateCredit + acctgTransEntriesAndTransTotal.creditTotal;
            acctgTransEntriesAndTransTotal.totalOfYearToDateCredit = totalOfYearToDateCredit.setScale(decimals, rounding);

            if (isDebitAccount) {
                balanceOfTheAcctgForYear = balanceOfTheAcctgForYear + acctgTransEntriesAndTransTotal.debitCreditDifference;
                acctgTransEntriesAndTransTotal.balanceOfTheAcctgForYear = balanceOfTheAcctgForYear.setScale(decimals, rounding);
            } else {
                balanceOfTheAcctgForYear = balanceOfTheAcctgForYear + acctgTransEntriesAndTransTotal.debitCreditDifference;
                acctgTransEntriesAndTransTotal.balanceOfTheAcctgForYear = balanceOfTheAcctgForYear.setScale(decimals, rounding);
            }
			
            glAcctgTrialBalanceList.add(acctgTransEntriesAndTransTotal);

            calendarTimePeriodStartDate.add(Calendar.MONTH, 1);
            Timestamp retStampStartDate = new Timestamp(calendarTimePeriodStartDate.getTimeInMillis());
            retStampStartDate.setNanos(0);
            customTimePeriodStartDate = retStampStartDate;
            customTimePeriodEndDate = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(retStampStartDate), timeZone, locale);
       // }
		closingBalance = openingBalance;
		totOpeningBalance = openingBalance;
		totYearOpeningBalance = openingBalance;
	
		
		prevDateStr = null;
		prevDateStamp = null;
		dayTotalDebit = BigDecimal.ZERO;
		dayTotalCredit = BigDecimal.ZERO;
		dayTotalOB = BigDecimal.ZERO;
		dayTotalCB = BigDecimal.ZERO;
		
		isNew = "Y";
		isMonthEnd = "N";
		
		partyId = "";
		accTransPartyId = "";
		paymentId = "";
		finAccountName = "";
		finAccountTransId = "";
		acctgTransId = "";
		partyName = "";
		paymentTypeDescription = "";
		finAccountDescription = "";
		paymentMethodTypeDes = "";
		instrumentNum = "";
		
		paymentGroupMethodTypeDes = "";
		paymentGroupTypeDes = "";
		paymentGroupRefNum = "";
		paymentGroupAmount = 0;
		paymentGroupComments = "";
		paymentMethodTypeId = "";
		
		
		dayTotalFinalMap = [:];
		newAcctgTransAndEntries = [];
		glAcctgTrialBalanceList = UtilMisc.sortMaps(glAcctgTrialBalanceList, UtilMisc.toList("transactionDate"));
		if(UtilValidate.isNotEmpty(glAcctgTrialBalanceList)){
			for(i=0; i<glAcctgTrialBalanceList.size(); i++){
				acctgTransIt = glAcctgTrialBalanceList[i];
				acctgTransAndEntries = acctgTransIt.acctgTransAndEntries;
				//newAcctgTransAndEntries.addAll(acctgTransAndEntries);
				tempList = [];
				for(j=0; j<acctgTransAndEntries.size(); j++){
					
					debitAmount = BigDecimal.ZERO;
					creditAmount = BigDecimal.ZERO;
					
					acctgTransEntry = acctgTransAndEntries[j];
					if(newAcctgTransAndEntries.contains(acctgTransEntry)){
						continue;
					}
					openingBalance = closingBalance;
					paymentId = acctgTransEntry.paymentId;
					paymentGroupId = null;
					//for group payments
					if(UtilValidate.isNotEmpty(paymentId)){
						GenericValue paymentIsValidForGroup = delegator.findOne("Payment",UtilMisc.toMap("paymentId",paymentId) , false);
					   //ignore IF it is From AR Side
					   if(!UtilAccounting.isReceipt(paymentIsValidForGroup)){
						   paymentGroupMemberList = delegator.findList("PaymentGroupMember", EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paymentId), null, null, null, false);
						   if(UtilValidate.isNotEmpty(paymentGroupMemberList)){
							   paymentGroupMemberList.each { paymentGroupMember->
								   paymentGroupId = paymentGroupMember.paymentGroupId;
								   if(UtilValidate.isNotEmpty(paymentGroupId)){
									   totalDebitAmount = 0;
									   totalCreditAmount = 0;
									   
									   newPaymentGroupMemberList = delegator.findList("PaymentGroupMember", EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS, paymentGroupId), null, null, null, false);
									   paymentGroupIds = EntityUtil.getFieldListFromEntityList(newPaymentGroupMemberList, "paymentId", true);
									   groupPaymentList = EntityUtil.filterByAnd(acctgTransAndEntries, [EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentGroupIds)]);
									   groupPaymentList.each { groupPayment ->
										   if(groupPayment.debitCreditFlag == "D"){
											   groupDebitAmount = groupPayment.amount;
											   totalDebitAmount = totalDebitAmount+groupDebitAmount;
											   debitAmount = totalDebitAmount; 
										   }
										   if(groupPayment.debitCreditFlag == "C"){
											   groupCreditAmount = groupPayment.amount;
											   totalCreditAmount = totalCreditAmount+groupCreditAmount;
											   creditAmount = totalCreditAmount;
										   }
									   }
									   newAcctgTransAndEntries.addAll(groupPaymentList);
									   
									   paymentGroup = delegator.findOne("PaymentGroup", [paymentGroupId : paymentGroupId], false);
									   if(UtilValidate.isNotEmpty(paymentGroup)){
										   paymentGroupTypeId = paymentGroup.paymentGroupTypeId;
										   paymentGroupStatus = paymentGroup.statusId;
										   if(UtilValidate.isNotEmpty(paymentMethodTypeId)){
											   paymentGroupType = delegator.findOne("PaymentGroupType", [paymentGroupTypeId : paymentGroupTypeId], false);
											   if(UtilValidate.isNotEmpty(paymentGroupType)){
												   paymentGroupTypeDes = paymentGroupType.description;
											   }
										   }
										   paymentMethodTypeId = paymentGroup.paymentMethodTypeId;
										   if(UtilValidate.isNotEmpty(paymentMethodTypeId)){
											   paymentMethodType = delegator.findOne("PaymentMethodType", [paymentMethodTypeId : paymentMethodTypeId], false);
											   if(UtilValidate.isNotEmpty(paymentMethodType)){
												   paymentGroupMethodTypeDes = paymentMethodType.description;
											   }
										   }
										   paymentGroupRefNum = paymentGroup.paymentRefNum;
										  // paymentGroupAmount = paymentGroup.amount;
										   paymentGroupComments = paymentGroup.inFavor;
									   }
								   }
							   }
						   }
					   }//end of isReceipt Logic	
					}
					paymentTransSequenceId = "";
					payment = delegator.findOne("Payment", [paymentId : paymentId], false);
					if(UtilValidate.isNotEmpty(payment)){
						partyId = payment.partyIdFrom;
						paymentMethodTypeId = payment.paymentMethodTypeId;
						if(UtilValidate.isNotEmpty(paymentMethodTypeId)){
							paymentMethodType = delegator.findOne("PaymentMethodType", [paymentMethodTypeId : paymentMethodTypeId], false);
							if(UtilValidate.isNotEmpty(paymentMethodType)){
								paymentMethodTypeDes = paymentMethodType.description;
							}
						}
						instrumentNum = payment.paymentRefNum;
						finAccountTransId = payment.finAccountTransId;
						//fin account sequence logic here
						if(UtilValidate.isNotEmpty(finAccountTransId)){
							finAccntTransSequenceList = delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, finAccountTransId), null, null, null, false);
							if(UtilValidate.isNotEmpty(finAccntTransSequenceList)){
								finAccntTransSequence = EntityUtil.getFirst(finAccntTransSequenceList);
								if(UtilValidate.isNotEmpty(finAccntTransSequence)){
									paymentTransSequenceId = finAccntTransSequence.transSequenceId;
								}
							}
						}
					}
					if(UtilValidate.isEmpty(paymentId)){
						accTransPartyId = acctgTransEntry.partyId;
					}
					if(partyId == "Company"){
						partyId = payment.partyIdTo;
					}
					
					finAccountTransId = acctgTransEntry.finAccountTransId;
					acctgTransId = acctgTransEntry.acctgTransId;
					finAccountId = "";
					finAccount = [:];
					finAccountTransTypeId = "";
					attrValue  = "";
					finAccountTrans = [:];
					finAccountTransType =[:];
					paymentComments = "";
					finAccountOwnerPartyId = "";
					finAccountPartyName = "";
					transSequenceId = "";
					finAccountMethodType = "";
					finAccountDes = "";
					finAccountTypeDes = "";
					finAccountContraNum = "";
					
					if(UtilValidate.isEmpty(paymentId)){
						finAccountTransAttr = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
						if(UtilValidate.isNotEmpty(finAccountTransAttr)){
							attrValue = finAccountTransAttr.attrValue;
							finAccountTrans = delegator.findOne("FinAccountTrans", [finAccountTransId : attrValue], false);
							if(UtilValidate.isNotEmpty(finAccountTrans)){
								finAccountTransTypeId = finAccountTrans.finAccountTransTypeId;
								reasonEnumId = finAccountTrans.reasonEnumId;
								finAccountDes = finAccountTrans.comments;
								if(UtilValidate.isNotEmpty(finAccountTrans.contraRefNum)){
									finAccountContraNum = finAccountTrans.contraRefNum;
								}
								if(UtilValidate.isNotEmpty(reasonEnumId) && reasonEnumId.equals("FATR_CONTRA")){
									finAccountMethodType = "Contra";
								}
								if(UtilValidate.isNotEmpty(finAccountTransTypeId)){
									finAccountTransType = delegator.findOne("FinAccountTransType", [finAccountTransTypeId : finAccountTransTypeId], false);
									finAccountDescription = finAccountTransType.description;
									finAccountId = finAccountTrans.finAccountId;
									finAccount = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
									if(UtilValidate.isNotEmpty(finAccount)){
										finAccountOwnerPartyId = finAccount.ownerPartyId;
										if(UtilValidate.isNotEmpty(finAccountOwnerPartyId)){
											finAccountPartyName = PartyHelper.getPartyName(delegator, finAccountOwnerPartyId, false);
										}
										finAccountTypeId = finAccount.finAccountTypeId;
										if(UtilValidate.isNotEmpty(finAccountTypeId)){
											finAccountType = delegator.findOne("FinAccountType", [finAccountTypeId : finAccountTypeId], false);
											if(UtilValidate.isNotEmpty(finAccountType)){
												finAccountTypeDes = finAccountType.description;
											}
										}
									}
									finAccountName = finAccount.finAccountName;
									//fin account sequence logic here
									finAccntTransSequenceList = delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, finAccountTransId), null, null, null, false);
									if(UtilValidate.isNotEmpty(finAccntTransSequenceList)){
										finAccntTransSequence = EntityUtil.getFirst(finAccntTransSequenceList);
										if(UtilValidate.isNotEmpty(finAccntTransSequence)){
											transSequenceId = finAccntTransSequence.transSequenceId;
										}
									}
								}
							}
						}
					}
					if(UtilValidate.isNotEmpty(partyId)){
						partyName = PartyHelper.getPartyName(delegator, partyId, false);
					}
					paymentType = [:];
					if(UtilValidate.isNotEmpty(paymentId)){
						paymentType = delegator.findOne("PaymentAndType", [paymentId : paymentId], false);
						if(UtilValidate.isNotEmpty(paymentType)){
							paymentTypeDescription = paymentType.description;
							if(UtilValidate.isNotEmpty(paymentType.comments)){
								paymentComments = paymentType.comments;
							}
						}
					}
					// Prepare List for CSV
					//if(UtilValidate.isNotEmpty(paymentGroupStatus) && paymentGroupStatus != "PAYGRP_CANCELLED"){
						/*if(tempList.contains(paymentGroupId)){
							continue;
						}
						else{
							if(paymentGroupId!=null){
								tempList.add(paymentGroupId);
							}
						}*/
					//}
					if(acctgTransEntry.debitCreditFlag == "D"){
						if(UtilValidate.isEmpty(paymentGroupId)){
							debitAmount = acctgTransEntry.amount;
						}
					}
					if(acctgTransEntry.debitCreditFlag == "C"){
						if(UtilValidate.isEmpty(paymentGroupId)){
							creditAmount = acctgTransEntry.amount;
						}
					}
					closingBalance = (openingBalance+debitAmount-creditAmount);
					
					transactionDate = acctgTransEntry.transactionDate;
					transactionDateBegin = UtilDateTime.getDayStart(transactionDate);
					transactionDateStr=UtilDateTime.toDateString(transactionDate ,"dd-MM-yy");
					if(prevDateStr == transactionDateStr){
						// Add Credit and Debit
						dayTotalDebit = debitAmount + dayTotalDebit;
						dayTotalCredit = creditAmount + dayTotalCredit;
						dayTotalCB = dayTotalOB+(dayTotalDebit)-(dayTotalCredit);
						isNew = "N";
					}
					else{
						// Handle First Entry
						// Prepare a Map For Day Totals
						// Add the map to financialAcctgTransList
						if((isNew == "N" || isNew == "MAYBE") && (isMonthEnd == "N")){
							dayWiseTotMap = [:];
							dayWiseTotMap["paymentId"] = "DAY TOTAL";
							dayWiseTotMap["openingBalance"] = dayTotalOB;
							dayWiseTotMap["debitAmount"] = dayTotalDebit;
							dayWiseTotMap["creditAmount"] = dayTotalCredit;
							dayWiseTotMap["closingBalance"] = dayTotalCB;
							//dayWiseTotMap["transactionDate"] = prevDateStr;
							
							tempDayTotalMap = [:];
							tempDayTotalMap.putAll(dayWiseTotMap);
							dayTotalFinalMap.put(prevDateStamp,tempDayTotalMap);
							//financialAcctgTransList.add(tempDayTotalMap);
						}
						dayTotalOB = openingBalance;
						dayTotalDebit = debitAmount;
						dayTotalCredit = creditAmount;
						dayTotalCB = dayTotalOB+(dayTotalDebit)-(dayTotalCredit);
					}
					isNew = "MAYBE";
					
					prevDateStr = transactionDateStr;
					
					if (UtilValidate.isNotEmpty(prevDateStr)) {
						def sdf1 = new SimpleDateFormat("dd-MM-yy");
						try {
							prevDateStamp = new java.sql.Timestamp(sdf1.parse(prevDateStr+" 00:00:00").getTime());
						} catch (ParseException e) {
							Debug.logError(e, "Cannot parse date string: " + prevDateStr, "");
						}
					}
					acctgTransEntryMap = [:];
					acctgTransEntryMap["transactionDate"] = transactionDateStr;
					acctgTransEntryMap["transactionDateStamp"] = transactionDate;
					if(UtilValidate.isNotEmpty(paymentId)){
						if(UtilValidate.isNotEmpty(paymentGroupId)){
							acctgTransEntryMap["paymentId"] = paymentGroupId;
							acctgTransEntryMap["partyId"] = " ";
							paymentgroup=delegator.findOne("PaymentGroup", ["paymentGroupId" :paymentGroupId], false);
							paymentGroupType = delegator.findOne("PaymentGroupType", [paymentGroupTypeId : paymentGroupTypeId], false);
							acctgTransEntryMap["partyName"] = paymentGroupType.description;
							acctgTransEntryMap["description"] = paymentGroupType.description;
							acctgTransEntryMap["comments"] = paymentGroupComments;
							acctgTransEntryMap["paymentMethodTypeDes"] = paymentGroupMethodTypeDes;
							acctgTransEntryMap["instrumentNum"] = paymentGroupRefNum;
						}else{
							if(UtilValidate.isNotEmpty(paymentTransSequenceId)){
								acctgTransEntryMap["paymentTransSequenceId"] = paymentTransSequenceId;
							}
							acctgTransEntryMap["paymentId"] = paymentId;
							acctgTransEntryMap["partyId"] = partyId;
							acctgTransEntryMap["partyName"] = partyName;
							acctgTransEntryMap["description"] = paymentTypeDescription;
							acctgTransEntryMap["comments"] = paymentComments;
							acctgTransEntryMap["paymentMethodTypeDes"] = paymentMethodTypeDes;
							acctgTransEntryMap["instrumentNum"] = instrumentNum;
						}
					}else{
						if(UtilValidate.isNotEmpty(transSequenceId)){
							acctgTransEntryMap["paymentTransSequenceId"] = transSequenceId;
						}
						acctgTransEntryMap["paymentId"] = acctgTransId;
						acctgTransEntryMap["partyId"] = accTransPartyId;
						if(UtilValidate.isNotEmpty(finAccountId)){
							acctgTransEntryMap["partyName"] = finAccountName;
							acctgTransEntryMap["finAccountOwnerPartyId"] = finAccountOwnerPartyId;
							acctgTransEntryMap["finAccountPartyName"] = finAccountPartyName;
							acctgTransEntryMap["paymentMethodTypeDes"] = finAccountMethodType;
							acctgTransEntryMap["instrumentNum"] = finAccountContraNum;
						}
						acctgTransEntryMap["description"] = finAccountDescription;
						acctgTransEntryMap["finAccountTypeDes"] = finAccountTypeDes;
						acctgTransEntryMap["comments"] = finAccountDes;
					}
					//acctgTransEntryMap["openingBalance"] = openingBalance;
					acctgTransEntryMap["debitAmount"] = debitAmount;
					acctgTransEntryMap["creditAmount"] = creditAmount;
					//acctgTransEntryMap["closingBalance"] = closingBalance;
					tempAcctgTransMap = [:];
					tempAcctgTransMap.putAll(acctgTransEntryMap);
					financialAcctgTransList.add(tempAcctgTransMap);
					
					
					if((i == ((glAcctgTrialBalanceList.size())-1)) && (isMonthEnd == "N")){
						dayWiseTotMap = [:];
						dayWiseTotMap["paymentId"] = "DAY TOTAL";
						dayWiseTotMap["openingBalance"] = dayTotalOB;
						dayWiseTotMap["debitAmount"] = dayTotalDebit;
						dayWiseTotMap["creditAmount"] = dayTotalCredit;
						dayWiseTotMap["closingBalance"] = dayTotalCB;
						//dayWiseTotMap["transactionDate"] = transactionDateStr;
						tempDayTotalMap = [:];
						tempDayTotalMap.putAll(dayWiseTotMap);
						//financialAcctgTransList.add(tempDayTotalMap);
						
						dayTotalFinalMap.put(transactionDateBegin,tempDayTotalMap);
					}
					isMonthEnd = "N";
				}
				totClosingBalance = (totOpeningBalance+(acctgTransIt.debitTotal)-(acctgTransIt.creditTotal));
				
				
				/*if( ((acctgTransIt.debitTotal) == 0) && ((acctgTransIt.creditTotal) == 0) ){
				}else{
					acctgTransTotals = [:];
					acctgTransTotals["paymentId"] = "MONTH TOTAL";
					acctgTransTotals["date"] = transactionDate;
					acctgTransTotals["openingBalance"] = totOpeningBalance;
					acctgTransTotals["debitAmount"] = acctgTransIt.debitTotal;
					acctgTransTotals["creditAmount"] = acctgTransIt.creditTotal;
					acctgTransTotals["closingBalance"] = totClosingBalance;
					tempTransTotalsMap = [:];
					tempTransTotalsMap.putAll(acctgTransTotals);
					financialAcctgTransList.add(tempTransTotalsMap);
				}*/
			}	
		}	
    }	
	    financialAcctgTransList = UtilMisc.sortMaps(financialAcctgTransList, UtilMisc.toList("transactionDateStamp","paymentTransSequenceId"));
		context.financialAcctgTransList	 = financialAcctgTransList;
        context.glAcctgTrialBalanceList = glAcctgTrialBalanceList;
		context.paymentInvType = paymentInvType;
}

//for each day in cash Book
int k = 0;
dayFinAccountTransList= [];
getDayTot = "N";
prevDate = null;
financialAcctgTransList.each{ dayFinAccount ->
	
	
	dayFinAccountMap = [:];
	
	transactionDate = null;
	transactionDateStr = dayFinAccount.transactionDate;
	
	if(transactionDateStr != null){
		def sdf1 = new SimpleDateFormat("dd-MM-yy");
		try {
			transactionDate = new java.sql.Timestamp(sdf1.parse(transactionDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + transactionDateStr, "");
		}
		transactionDateBegin = UtilDateTime.getDayStart(transactionDate);
		transactionDateEnd = UtilDateTime.getDayEnd(transactionDate);
		
		if(prevDate == null){
			prevDate = transactionDate;
		}
		
		currentDate = transactionDate;
		if(prevDate != currentDate){
			if(UtilValidate.isNotEmpty(dayTotalFinalMap.get(prevDate))){
				tempDayTotalsMap = [:];
				tempDayTotalsMap.putAll(dayTotalFinalMap.get(prevDate));
				dayFinAccountTransList.add(tempDayTotalsMap);
			}
			prevDate = currentDate;
		}
		if((dayBegin <= transactionDateBegin) && (dayEnd >= transactionDateEnd)){
			getDayTot = "Y";
			dayFinAccountMap["transactionDate"] = dayFinAccount.transactionDate;
			dayFinAccountMap["paymentTransSequenceId"] = dayFinAccount.paymentTransSequenceId;
			dayFinAccountMap["paymentId"] = dayFinAccount.paymentId;
			dayFinAccountMap["partyId"] = dayFinAccount.partyId;
			dayFinAccountMap["partyName"] = dayFinAccount.partyName;
			dayFinAccountMap["finAccountOwnerPartyId"] = dayFinAccount.finAccountOwnerPartyId;
			dayFinAccountMap["finAccountPartyName"] = dayFinAccount.finAccountPartyName;
			dayFinAccountMap["description"] = dayFinAccount.description;
			dayFinAccountMap["comments"] = dayFinAccount.comments;
			dayFinAccountMap["paymentMethodTypeDes"] = dayFinAccount.paymentMethodTypeDes;
			dayFinAccountMap["finAccountTypeDes"] = dayFinAccount.finAccountTypeDes;
			dayFinAccountMap["instrumentNum"] = dayFinAccount.instrumentNum;
			//dayFinAccountMap["openingBalance"] = dayFinAccount.openingBalance;
			dayFinAccountMap["debitAmount"] = dayFinAccount.debitAmount;
			dayFinAccountMap["creditAmount"] = dayFinAccount.creditAmount;
			//dayFinAccountMap["closingBalance"] = dayFinAccount.closingBalance;
			tempDayTotalsMap = [:];
			tempDayTotalsMap.putAll(dayFinAccountMap);
			dayFinAccountTransList.add(tempDayTotalsMap);
		}
	}
	if(k == ((financialAcctgTransList.size())-1)){
		if(UtilValidate.isNotEmpty(dayTotalFinalMap.get(transactionDate))){
			tempDayTotalsMap = [:];
			tempDayTotalsMap.putAll(dayTotalFinalMap.get(transactionDate));
			dayFinAccountTransList.add(tempDayTotalsMap);
			
		}
	}
	k++;
		/*if((getDayTot == "Y")){
			getDayTot = "N";
			if(UtilValidate.isNotEmpty(dayTotalFinalMap.get(transactionDate))){
				tempDayTotalsMap = [:];
				tempDayTotalsMap.putAll(dayTotalFinalMap.get(transactionDate));
				dayFinAccountTransList.add(tempDayTotalsMap);
			}
		}*/
		
	//dayFinAccountTransList = UtilMisc.sortMaps(dayFinAccountTransList, UtilMisc.toList("transactionDate"));
}

openingBal = 0;
if (UtilValidate.isNotEmpty(parameters.openingBalance)) {
	openingBal = new BigDecimal(parameters.openingBalance);
}
closingBal = openingBal;
finalFinAccntTransList = [];
if(UtilValidate.isNotEmpty(dayFinAccountTransList)){
	dayFinAccountTransList.each { dayFinAccountTrans ->
		if(UtilValidate.isNotEmpty(dayFinAccountTrans)){
			finalFinAccountTransMap = [:];
			paymentId = dayFinAccountTrans.paymentId;
			if(paymentId != "DAY TOTAL"){
				debitAmnt = dayFinAccountTrans.debitAmount;
				creditAmnt = dayFinAccountTrans.creditAmount;
				closingBal = (openingBal+debitAmnt-creditAmnt);
				tempMap = [:];
				tempMap["openingBal"] = openingBal;
				tempMap["closingBal"] = closingBal;
				if(UtilValidate.isNotEmpty(tempMap)){
					finalFinAccountTransMap.putAll(tempMap);
				}
				openingBal = closingBal;
			}
			finalFinAccountTransMap.putAll(dayFinAccountTrans);
			if(UtilValidate.isNotEmpty(finalFinAccountTransMap)){
				finalFinAccntTransList.addAll(finalFinAccountTransMap);
			}
		}
	}
}
context.dayFinAccountTransList = finalFinAccntTransList;








