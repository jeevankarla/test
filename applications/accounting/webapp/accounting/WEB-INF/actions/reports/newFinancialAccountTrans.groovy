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
		glAccountId = parameters.finAccountId;
		glAccount = delegator.findOne("GlAccount", [glAccountId : glAccountId], false);
		isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
		context.isDebitAccount = isDebitAccount;
		context.glAccount = glAccount;
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
	monthBegin = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(effectiveDate), timeZone, locale);
	monthEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(effectiveDate), timeZone, locale);
	
	fromDateStr = UtilDateTime.toDateString(effectiveDate ,"MMMM dd, yyyy");
	context.put("fromDateStr",fromDateStr);

	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_MONTH"));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthBegin)));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(monthEnd)));
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
    if (currentTimePeriod){
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
		
		partyId = "";
		paymentId = "";
		accountName = "";
		if(UtilValidate.isNotEmpty(glAcctgTrialBalanceList)){
			acctgTransIt = glAcctgTrialBalanceList[0];
			acctgTransAndEntries = acctgTransIt.acctgTransAndEntries;
			if(UtilValidate.isNotEmpty(acctgTransAndEntries)){
				for(j=0; j<acctgTransAndEntries.size(); j++){
					acctgTransEntry = acctgTransAndEntries[j];
					openingBalance = closingBalance;
					
					paymentId = acctgTransEntry.paymentId;
					payment = delegator.findOne("Payment", [paymentId : paymentId], false);
					if(UtilValidate.isNotEmpty(payment)){
						partyId = payment.partyIdFrom;
					}
					if(partyId == "Company"){
						partyId = payment.partyIdTo;
					}
					
					finAccountId = "";
					finAccount = [:];
					
					if(UtilValidate.isEmpty(paymentId)){
					finAccountList = delegator.findList("FinAccountTrans", EntityCondition.makeCondition(["reasonEnumId" : "FATR_CONTRA"]), null, null, null, true);
						if(UtilValidate.isNotEmpty(finAccountList)){
							finAccountId = finAccountList[0].finAccountId;
							finAccount = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
							accountName = finAccount.finAccountName;
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
					if(UtilValidate.isNotEmpty(paymentId)){
						acctgTransEntryMap["partyId"] = partyId;
					}else{
						acctgTransEntryMap["partyId"] = accountName;
					}
					acctgTransEntryMap["openingBalance"] = openingBalance;
					acctgTransEntryMap["debitAmount"] = debitAmount;
					acctgTransEntryMap["creditAmount"] = creditAmount;
					acctgTransEntryMap["closingBalance"] = closingBalance;
					
					tempAcctgTransMap = [:];
					tempAcctgTransMap.putAll(acctgTransEntryMap);
					financialAcctgTransList.add(tempAcctgTransMap);
					
					
					if((j == ((acctgTransAndEntries.size())-1)) && (isMonthEnd == "N")){
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
			}	
		}		
		context.financialAcctgTransList = financialAcctgTransList;
        context.glAcctgTrialBalanceList = glAcctgTrialBalanceList;
		context.paymentInvType = paymentInvType;
    }
}
//for each day in cash Book
dayFinAccountTransList= [];
getDayTot = "N";
financialAcctgTransList.each{ dayFinAccount ->
	dayFinAccountMap = [:];
	if(context.get("fromDateStr") == dayFinAccount.transactionDate){
		getDayTot = "Y";
		dayFinAccountMap["transactionDate"] = dayFinAccount.transactionDate;
		dayFinAccountMap["paymentId"] = dayFinAccount.paymentId;
		dayFinAccountMap["partyId"] = dayFinAccount.partyId;
		dayFinAccountMap["openingBalance"] = dayFinAccount.openingBalance;
		dayFinAccountMap["debitAmount"] = dayFinAccount.debitAmount;
		dayFinAccountMap["creditAmount"] = dayFinAccount.creditAmount;
		dayFinAccountMap["closingBalance"] = dayFinAccount.closingBalance;
		tempDayTotalsMap = [:];
		tempDayTotalsMap.putAll(dayFinAccountMap);
		dayFinAccountTransList.add(tempDayTotalsMap);
	}
	if((getDayTot == "Y") && (dayFinAccount.get("paymentId") == "DAY TOTAL")){
		getDayTot = "N";
		dayFinAccountMap["transactionDate"] = dayFinAccount.transactionDate;
		dayFinAccountMap["paymentId"] = dayFinAccount.paymentId;
		dayFinAccountMap["partyId"] = dayFinAccount.partyId;
		dayFinAccountMap["openingBalance"] = dayFinAccount.openingBalance;
		dayFinAccountMap["debitAmount"] = dayFinAccount.debitAmount;
		dayFinAccountMap["creditAmount"] = dayFinAccount.creditAmount;
		dayFinAccountMap["closingBalance"] = dayFinAccount.closingBalance;
		tempDayTotalsMap = [:];
		tempDayTotalsMap.putAll(dayFinAccountMap);
		dayFinAccountTransList.add(tempDayTotalsMap);
	}
}
context.dayFinAccountTransList = dayFinAccountTransList;







