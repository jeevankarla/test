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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.util.UtilAccounting;
import java.math.BigDecimal;
import com.ibm.icu.util.Calendar;
import org.ofbiz.base.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.party.party.PartyHelper;

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
   effectiveDate = null;
   effectiveDateStr = parameters.fromDate;
   if (UtilValidate.isEmpty(effectiveDateStr)) {
	   effectiveDate = UtilDateTime.nowTimestamp();
   }
   else{
	   def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   try {
		   effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
	   } catch (ParseException e) {
		   Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, "");
	   }
   }
   //monthBegin = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(effectiveDate), timeZone, locale);
   monthBegin = UtilDateTime.addDaysToTimestamp(effectiveDate, -1);
   //monthEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(effectiveDate), timeZone, locale);
   
   fromDateStr = UtilDateTime.toDateString(effectiveDate ,"MMMM dd, yyyy");
   fromDateTimestamp=UtilDateTime.getDayStart(effectiveDate);
   context.put("fromDateStr",fromDateStr);
   conditionList = [];
   conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
   conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_MONTH"));
   conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthBegin)));
   conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(monthBegin)));
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
   financialAcctgTransList = [];
   if (currentTimePeriod && (UtilValidate.isNotEmpty(glAccountId))){
	   context.currentTimePeriod = currentTimePeriod;
	   customTimePeriodStartDate = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(currentTimePeriod.fromDate), timeZone, locale);
	   customTimePeriodEndDate = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(currentTimePeriod.fromDate), timeZone, locale);
	   
	   Calendar calendarTimePeriodStartDate = UtilDateTime.toCalendar(customTimePeriodStartDate);
	   glAcctgTrialBalanceList = [];
	   BigDecimal totalOfYearToDateDebit = BigDecimal.ZERO;
	   BigDecimal totalOfYearToDateCredit = BigDecimal.ZERO;
	   isPosted = parameters.isPosted;

	   while (customTimePeriodEndDate <= UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(currentTimePeriod.thruDate), 1)){
		   if ("ALL".equals(isPosted)) {
			   isPosted = "";
		   }
		   /*acctgTransEntriesAndTransTotal = dispatcher.runSync("getAcctgTransEntriesAndTransTotal",
			   [customTimePeriodStartDate : fromDateTimestamp, customTimePeriodEndDate : UtilDateTime.getDayEnd(fromDateTimestamp), organizationPartyId : organizationPartyId, glAccountId : glAccountId, isPosted : isPosted, userLogin : userLogin]);*/
			acctgTransEntriesAndTransTotal = dispatcher.runSync("getAcctgTransEntriesAndTransTotal",
				   [customTimePeriodStartDate : customTimePeriodStartDate, customTimePeriodEndDate : customTimePeriodEndDate, organizationPartyId : organizationPartyId, glAccountId : glAccountId, isPosted : isPosted, userLogin : userLogin]);
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
	   }
	   
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
	   
	   isNew = "Y";
	   isMonthEnd = "N";
	   
	   for(i=0; i<glAcctgTrialBalanceList.size(); i++){
		   acctgTransIt = glAcctgTrialBalanceList[i];
		   acctgTransAndEntries = glAcctgTrialBalanceList[i].acctgTransAndEntries;
		   for(j=0; j<acctgTransAndEntries.size(); j++){
			   acctgTransEntry = acctgTransAndEntries[j];
			   openingBalance = closingBalance;
			   
			   // Get InvoiceItem type based on paymentId
			   paymentId = acctgTransEntry.paymentId;
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
			   
			   // Prepare List for CSV
			   
			   debitAmount = BigDecimal.ZERO;
			   creditAmount = BigDecimal.ZERO;
			   
			   if(acctgTransEntry.debitCreditFlag == "D"){
				   debitAmount = acctgTransEntry.amount;
			   }
			   if(acctgTransEntry.debitCreditFlag == "C"){
				   creditAmount = acctgTransEntry.amount;
			   }
			   closingBalance = (openingBalance+debitAmount-creditAmount);
			   
			   transactionDate = acctgTransEntry.transactionDate;
			   transactionDateStr=UtilDateTime.toDateString(transactionDate ,"MMMM dd, yyyy");
			   
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
			   
			   acctgTransEntryMap = [:];
			   acctgTransEntryMap["transactionDate"] = transactionDateStr;
			   acctgTransEntryMap["paymentId"] = paymentId;
			   if(UtilValidate.isNotEmpty(invItemType.description)){
				   acctgTransEntryMap["description"] = invItemType.description;
			   }
			   acctgTransEntryMap["openingBalance"] = openingBalance;
			   acctgTransEntryMap["debitAmount"] = debitAmount;
			   acctgTransEntryMap["creditAmount"] = creditAmount;
			   acctgTransEntryMap["closingBalance"] = closingBalance;
			   
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
				   tempDayTotalMap = [:];
				   tempDayTotalMap.putAll(dayWiseTotMap);
				   financialAcctgTransList.add(tempDayTotalMap);
			   }
			   isMonthEnd = "N";
		   }
			   
		   totClosingBalance = (totOpeningBalance+(acctgTransIt.debitTotal)-(acctgTransIt.creditTotal));
		   
		   if( ((acctgTransIt.debitTotal) == 0) && ((acctgTransIt.creditTotal) == 0) ){
		   }else{
			   isMonthEnd = "Y";
			   dayWiseTotMap = [:];
			   dayWiseTotMap["paymentId"] = "DAY TOTAL";
			   dayWiseTotMap["openingBalance"] = dayTotalOB;
			   dayWiseTotMap["debitAmount"] = dayTotalDebit;
			   dayWiseTotMap["creditAmount"] = dayTotalCredit;
			   dayWiseTotMap["closingBalance"] = dayTotalCB;
			   tempDayTotalMap = [:];
			   tempDayTotalMap.putAll(dayWiseTotMap);
			   financialAcctgTransList.add(tempDayTotalMap);
		   
			   acctgTransTotals = [:];
			   acctgTransTotals["paymentId"] = "MONTH TOTAL";
			   acctgTransTotals["openingBalance"] = totOpeningBalance;
			   acctgTransTotals["debitAmount"] = acctgTransIt.debitTotal;
			   acctgTransTotals["creditAmount"] = acctgTransIt.creditTotal;
			   acctgTransTotals["closingBalance"] = totClosingBalance;
			   tempTransTotalsMap = [:];
			   tempTransTotalsMap.putAll(acctgTransTotals);
			   financialAcctgTransList.add(tempTransTotalsMap);
		   }
		   
		   totOpeningBalance = totClosingBalance;
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
