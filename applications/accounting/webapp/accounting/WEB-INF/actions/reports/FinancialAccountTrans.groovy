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
	conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(dayEnd)));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(dayBegin)));
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
	
	if (UtilValidate.isNotEmpty(context.get("openingBalance"))) {
		openingBalance = context.get("openingBalance");
	}
	
	financialAcctgTransList = [];
	glAcctgTrialBalanceList = [];
	paymentInvType = [:];
	if (UtilValidate.isNotEmpty(glAccountId)){
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
		financialAcctgTransList = [];
		
		paymentInvType = [:];
		
		
		prevDateStr = null;
		dayTotalDebit = BigDecimal.ZERO;
		dayTotalCredit = BigDecimal.ZERO;
		dayTotalOB = BigDecimal.ZERO;
		dayTotalCB = BigDecimal.ZERO;
		
		totClosingBalance = BigDecimal.ZERO;
		
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
		
		addTotals = "Y";
		isRealMonthEnd = "N";
		isDayEnd = "N";
		isNew = "Y";
		isMonthEnd = "N";
		prevMonth = null;
		yearDebitTotal = 0;
		yearCreditTotal = 0;
		newAcctgTransAndEntries = [];
		
		for(i=0; i<glAcctgTrialBalanceList.size(); i++){
			acctgTransIt = glAcctgTrialBalanceList[i];
			acctgTransAndEntries = glAcctgTrialBalanceList[i].acctgTransAndEntries;
			monthDebitTotal = 0;
			monthCreditTotal = 0;
			
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
				
				paymentApplication = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition(["paymentId" : paymentId]), null, null, null, true);
				
				invoiceItemType = "";
				invItemType = [:];
				
				if(UtilValidate.isNotEmpty(paymentApplication)){
					invoiceId = paymentApplication[0].invoiceId;
					invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(["invoiceId" : invoiceId]), null, null, null, true);
					if(UtilValidate.isNotEmpty(invoiceItemList)){
						invoiceItemType = invoiceItemList[0].invoiceItemTypeId;
						invItemType = delegator.findOne("InvoiceItemType", [invoiceItemTypeId : invoiceItemType], false);
						paymentInvType.put(paymentId, invoiceItemType);
					}
				}
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
				
				// Prepare List for CSV
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
				transactionDateStr=UtilDateTime.toDateString(transactionDate ,"dd/MM/yyyy");
				transMonthStart = UtilDateTime.getMonthStart(transactionDate);
				transMonthStartStr=UtilDateTime.toDateString(transMonthStart ,"dd/MM/yyyy");
				
				if(prevMonth == null){
					prevMonth = transMonthStartStr;
				}
				if( (prevMonth == transMonthStartStr)){
					isRealMonthEnd = "N";
				}
				else{
					isRealMonthEnd = "Y";
					prevMonth = transMonthStartStr;
				}
				//changes for cash book
				acctgTransId = acctgTransEntry.acctgTransId;
				currencyUomId = acctgTransEntry.currencyUomId;
				
				yearDebitTotal = yearDebitTotal + debitAmount;
				yearCreditTotal = yearCreditTotal + creditAmount;
				
				if(prevDateStr == transactionDateStr){
						// Add Credit and Debit
						dayTotalDebit = debitAmount + dayTotalDebit;
						dayTotalCredit = creditAmount + dayTotalCredit;
						dayTotalCB = dayTotalOB+(dayTotalDebit)-(dayTotalCredit);
						isNew = "N";
				}else{
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
							dayWiseTotMap["transactionDate"] = prevDateStr;
							
							tempDayTotalMap = [:];
							tempDayTotalMap.putAll(dayWiseTotMap);
							financialAcctgTransList.add(tempDayTotalMap);
						}
						dayTotalOB = openingBalance;
						dayTotalDebit = debitAmount;
						dayTotalCredit = creditAmount;
						dayTotalCB = dayTotalOB+(dayTotalDebit)-(dayTotalCredit);
					}
					isNew = "MAYBE";
					
					prevDateStr = transactionDateStr;
					
				if(isRealMonthEnd == "Y"){
					acctgTransTotals = [:];
					acctgTransTotals["paymentId"] = "MONTH TOTAL";
					acctgTransTotals["transactionDate"] = "--";
					acctgTransTotals["openingBalance"] = totOpeningBalance;
					acctgTransTotals["debitAmount"] = monthDebitTotal;
					acctgTransTotals["creditAmount"] = monthCreditTotal;
					acctgTransTotals["closingBalance"] = totClosingBalance;
					tempTransTotalsMap = [:];
					tempTransTotalsMap.putAll(acctgTransTotals);
					financialAcctgTransList.add(tempTransTotalsMap);
					monthDebitTotal = 0;
					monthCreditTotal = 0;
					totOpeningBalance = totClosingBalance;
				}
				monthDebitTotal = monthDebitTotal + debitAmount;
				monthCreditTotal = monthCreditTotal + creditAmount;
				totClosingBalance = totOpeningBalance+monthDebitTotal-monthCreditTotal;
				
				if((j == ((acctgTransAndEntries.size())-1)) && (isMonthEnd == "N")){
					dayWiseTotMap = [:];
					dayWiseTotMap["paymentId"] = "DAY TOTAL";
					dayWiseTotMap["openingBalance"] = dayTotalOB;
					dayWiseTotMap["debitAmount"] = dayTotalDebit;
					dayWiseTotMap["creditAmount"] = dayTotalCredit;
					dayWiseTotMap["closingBalance"] = dayTotalCB;
					dayWiseTotMap["transactionDate"] = transactionDateStr;
					tempDayTotalMap = [:];
					tempDayTotalMap.putAll(dayWiseTotMap);
					financialAcctgTransList.add(tempDayTotalMap);
				}
			}
				
			//totClosingBalance = (totOpeningBalance+(acctgTransIt.debitTotal)-(acctgTransIt.creditTotal));
			
			if( ((acctgTransIt.debitTotal) == 0) && ((acctgTransIt.creditTotal) == 0) ){
			}else{
				isMonthEnd = "Y";
				acctgTransTotals = [:];
				acctgTransTotals["paymentId"] = "MONTH TOTAL";
				acctgTransTotals["transactionDate"] = "--";
				acctgTransTotals["openingBalance"] = totOpeningBalance;
				acctgTransTotals["debitAmount"] = monthDebitTotal;
				acctgTransTotals["creditAmount"] = monthCreditTotal;
				acctgTransTotals["closingBalance"] = totClosingBalance;
				tempTransTotalsMap = [:];
				tempTransTotalsMap.putAll(acctgTransTotals);
				financialAcctgTransList.add(tempTransTotalsMap);
				totClosingBalance = totOpeningBalance+monthDebitTotal-monthCreditTotal; 
			}
		}
		yearClosingBalance = ((totYearOpeningBalance+acctgTransIt.totalOfYearToDateDebit)-(acctgTransIt.totalOfYearToDateCredit));
		if( ((acctgTransIt.totalOfYearToDateDebit) == 0) && ((acctgTransIt.totalOfYearToDateCredit) == 0) ){
		}else{
			acctgTransTotals = [:];
			acctgTransTotals["paymentId"] = "YEAR TOTAL";
			acctgTransTotals["openingBalance"] = totYearOpeningBalance;
			acctgTransTotals["debitAmount"] = acctgTransIt.totalOfYearToDateDebit;
			acctgTransTotals["creditAmount"] = acctgTransIt.totalOfYearToDateCredit;
			acctgTransTotals["closingBalance"] = yearClosingBalance;
			
			tempTransTotalsMap = [:];
			tempTransTotalsMap.putAll(acctgTransTotals);
			financialAcctgTransList.add(tempTransTotalsMap);
		}
		context.financialAcctgTransList = financialAcctgTransList;
		context.glAcctgTrialBalanceList = glAcctgTrialBalanceList;
		context.paymentInvType = paymentInvType;
	}
}
