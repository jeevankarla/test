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
	
		
		prevDateStr = null;
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
		paymentComments = "";
		comments = parameters.comments;
		glAcctgTrialBalanceList = UtilMisc.sortMaps(glAcctgTrialBalanceList, UtilMisc.toList("paymentId"));
		if(UtilValidate.isNotEmpty(glAcctgTrialBalanceList)){
			for(i=0; i<glAcctgTrialBalanceList.size(); i++){
				acctgTransIt = glAcctgTrialBalanceList[i];
				acctgTransAndEntries = glAcctgTrialBalanceList[i].acctgTransAndEntries;
				List removedCommentsList = new ArrayList();
				for(j=0; j<acctgTransAndEntries.size(); j++){
					acctgTransEntry = acctgTransAndEntries[j];
					openingBalance = closingBalance;
					paymentId = acctgTransEntry.paymentId;
					payment = delegator.findOne("Payment", [paymentId : paymentId], false);
					if(UtilValidate.isNotEmpty(payment)){
						partyId = payment.partyIdFrom;
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
					if(UtilValidate.isEmpty(paymentId)){
						finAccountTransAttr = delegator.findOne("FinAccountTransAttribute", [finAccountTransId : finAccountTransId, attrName : "FATR_CONTRA"], false);
						if(UtilValidate.isNotEmpty(finAccountTransAttr)){
							attrValue = finAccountTransAttr.attrValue;
							finAccountTrans = delegator.findOne("FinAccountTrans", [finAccountTransId : attrValue], false);
							finAccountTransTypeId = finAccountTrans.finAccountTransTypeId;
							finAccountTransType = delegator.findOne("FinAccountTransType", [finAccountTransTypeId : finAccountTransTypeId], false);
							finAccountDescription = finAccountTransType.description;
							finAccountId = finAccountTrans.finAccountId;
							finAccount = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
							finAccountName = finAccount.finAccountName;
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
					if(UtilValidate.isNotEmpty(comments)){
						if((comments == paymentComments)){
						}
						else{
							removedCommentsList.add(acctgTransEntry);
							continue;
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
					
					if(UtilValidate.isNotEmpty(paymentId)){
						acctgTransEntryMap["paymentId"] = paymentId;
						acctgTransEntryMap["partyId"] = partyId;
						acctgTransEntryMap["partyName"] = partyName;
						acctgTransEntryMap["description"] = paymentTypeDescription;
						acctgTransEntryMap["comments"] = paymentComments;
					}else{
						acctgTransEntryMap["paymentId"] = acctgTransId;
						acctgTransEntryMap["partyId"] = accTransPartyId;
						if(UtilValidate.isNotEmpty(finAccountId)){
							acctgTransEntryMap["partyName"] = finAccountName;
						}
						acctgTransEntryMap["description"] = finAccountDescription;
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
				acctgTransAndEntries.removeAll(removedCommentsList);
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
		context.financialAcctgTransList = financialAcctgTransList;
        context.glAcctgTrialBalanceList = glAcctgTrialBalanceList;
		context.paymentInvType = paymentInvType;
}

//for each day in cash Book
dayFinAccountTransList= [];
getDayTot = "N";
financialAcctgTransList.each{ dayFinAccount ->
	dayFinAccountMap = [:];
	
	transactionDate = null;
	transactionDateStr = dayFinAccount.transactionDate;
	if(transactionDateStr != null){
		def sdf1 = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			transactionDate = new java.sql.Timestamp(sdf1.parse(transactionDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + transactionDateStr, "");
		}
	
		transactionDateBegin = UtilDateTime.getDayStart(transactionDate);
		transactionDateEnd = UtilDateTime.getDayEnd(transactionDate);
		
		if((dayBegin <= transactionDateBegin) && (dayEnd >= transactionDateEnd)){
			getDayTot = "Y";
			dayFinAccountMap["transactionDate"] = dayFinAccount.transactionDate;
			dayFinAccountMap["paymentId"] = dayFinAccount.paymentId;
			dayFinAccountMap["partyId"] = dayFinAccount.partyId;
			dayFinAccountMap["partyName"] = dayFinAccount.partyName;
			dayFinAccountMap["description"] = dayFinAccount.description;
			dayFinAccountMap["comments"] = dayFinAccount.comments;
			dayFinAccountMap["openingBalance"] = dayFinAccount.openingBalance;
			dayFinAccountMap["debitAmount"] = dayFinAccount.debitAmount;
			dayFinAccountMap["creditAmount"] = dayFinAccount.creditAmount;
			dayFinAccountMap["closingBalance"] = dayFinAccount.closingBalance;
			tempDayTotalsMap = [:];
			tempDayTotalsMap.putAll(dayFinAccountMap);
			dayFinAccountTransList.add(tempDayTotalsMap);
		}
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
		context.dayFinAccountTransList = dayFinAccountTransList;
}



