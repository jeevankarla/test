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

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.util.UtilAccounting;
import javolution.util.FastList;

import java.sql.Date;

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

fromDate = UtilDateTime.getDayStart(effectiveFromDate);
thruDate = UtilDateTime.getDayEnd(effectiveThruDate);



if (!fromDate) {
    return;
}
if (!thruDate) {
    thruDate = UtilDateTime.nowTimestamp();
}
if (!glFiscalTypeId) {
    return;
}

fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate);
glAccountId = null;
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


//  Find the last closed time period to get the fromDate for the transactions in the current period and the ending balances of the last closed period
Map lastClosedTimePeriodResult = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", organizationPartyId, "findDate", new Date(fromDate.getTime()),"userLogin", userLogin));
Timestamp lastClosedDate = lastClosedTimePeriodResult.lastClosedDate;

GenericValue lastClosedTimePeriod = null; 
if (lastClosedDate) {
	
    lastClosedTimePeriod = (GenericValue)lastClosedTimePeriodResult.lastClosedTimePeriod;
	if(lastClosedTimePeriod && (lastClosedDate.equals(UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(lastClosedTimePeriod.thruDate))))){
		lastClosedTimePeriodResult = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", organizationPartyId, "findDate", new Date(UtilDateTime.addDaysToTimestamp(fromDate,-1).getTime()),"userLogin", userLogin));
		lastClosedDate = lastClosedTimePeriodResult.lastClosedDate;
		//Debug.log("in findLastClosedDate======"+(lastClosedDate.equals(UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(lastClosedTimePeriod.thruDate)))));
	}
	lastClosedTimePeriod = (GenericValue)lastClosedTimePeriodResult.lastClosedTimePeriod;
	lastClosedDate = UtilDateTime.getDayEnd(lastClosedDate);
}
//Debug.log("lastClosedTimePeriod======"+lastClosedTimePeriod);
//Debug.log("lastClosedDate======"+lastClosedDate);
// POSTED
// Posted transactions totals and grand totals
postedTotals = [];
postedTotalDebit = BigDecimal.ZERO;
postedTotalCredit = BigDecimal.ZERO;
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));

andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, parameters.isPosted));
andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, lastClosedDate));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, fromDate));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
//Debug.log("andCond======"+andCond);
List allPostedOpeningTransactionTotals = delegator.findList("AcctgTransEntrySums", andCond, null, UtilMisc.toList("glAccountId"), null, false);

//Debug.log("allPostedOpeningTransactionTotals======"+allPostedOpeningTransactionTotals);

andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));

andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, parameters.isPosted));
andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List allPostedTransactionTotals = delegator.findList("AcctgTransEntrySums", andCond, null, UtilMisc.toList("glAccountId"), null, false);
//Debug.log("allPostedTransactionTotalItr======="+allPostedTransactionTotals.size());
if (allPostedTransactionTotals) {
    Map postedTransactionTotalsMap = [:]
    allPostedTransactionTotals.each { postedTransactionTotal ->
        Map accountMap = (Map)postedTransactionTotalsMap.get(postedTransactionTotal.glAccountId);
        if (!accountMap) {
            GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), true);
            if (glAccount) {
                boolean isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
                // Get the opening balances at the end of the last closed time period
                if (UtilAccounting.isAssetAccount(glAccount) || UtilAccounting.isLiabilityAccount(glAccount) || UtilAccounting.isEquityAccount(glAccount)) {
                    if (lastClosedTimePeriod) {
                        List timePeriodAndExprs = FastList.newInstance();
                        timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
                        lastTimePeriodHistory = EntityUtil.getFirst(delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false));
                        if (lastTimePeriodHistory) {
                            accountMap = UtilMisc.toMap("glAccountId",glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "openingD", lastTimePeriodHistory.getBigDecimal("postedDebits"), "openingC", lastTimePeriodHistory.getBigDecimal("postedCredits"), "D", BigDecimal.ZERO, "C", BigDecimal.ZERO);
                        }
                    }
                }
            }
            if (!accountMap) {
                accountMap = UtilMisc.makeMapWritable(postedTransactionTotal);
                accountMap.put("openingD", BigDecimal.ZERO);
                accountMap.put("openingC", BigDecimal.ZERO);
                accountMap.put("D", BigDecimal.ZERO);
                accountMap.put("C", BigDecimal.ZERO);
                accountMap.put("balance", BigDecimal.ZERO);
            }
            // get opening balances
			andExprs = FastList.newInstance();
			andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
			andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List transactionTotals = EntityUtil.filterByCondition(allPostedOpeningTransactionTotals, andCond);
			if (transactionTotals) {
				transactionTotals.each { transactionTotal ->
					UtilMisc.addToBigDecimalInMap(accountMap, "opening" + transactionTotal.debitCreditFlag, transactionTotal.amount);
				}
			}
        }
        UtilMisc.addToBigDecimalInMap(accountMap, postedTransactionTotal.debitCreditFlag, postedTransactionTotal.amount);
        postedTransactionTotalsMap.put(glAccountId, accountMap);
    }
    postedTotals = postedTransactionTotalsMap.values().asList();
}
openingBalance=0;
postedTotals.each{ eachTotal ->
	bal=eachTotal.openingD-eachTotal.openingC;
	openingBalance=openingBalance+bal;
}
context.openingBalance=openingBalance;

// Posted grand total for Debits
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "D"));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List postedDebitTransactionTotals = EntityUtil.filterByCondition(allPostedTransactionTotals, andCond);
if (postedDebitTransactionTotals) {
	postedDebitTransactionTotals.each { transactionTotal ->		
        postedTotalDebit = postedTotalDebit.add(transactionTotal.amount);
	}
}
// Posted grand total for Credits
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "C"));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List postedCreditTransactionTotals = EntityUtil.filterByCondition(allPostedTransactionTotals, andCond);
if (postedCreditTransactionTotals) {
	postedCreditTransactionTotals.each { transactionTotal ->
		postedTotalCredit = postedTotalCredit.add(transactionTotal.amount);
	}
}

postedTotals.add(["D":postedTotalDebit, "C":postedTotalCredit]);
context.postedTransactionTotals = postedTotals;
