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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.base.util.*
import org.ofbiz.minilang.SimpleMapProcessor
import org.ofbiz.content.ContentManagementWorker
import org.ofbiz.content.content.ContentWorker
import org.ofbiz.content.data.DataResourceWorker
import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;

if (!thruDate) {
    thruDate = UtilDateTime.nowTimestamp();
}
if (!glFiscalTypeId) {
    return;
}

// Setup the divisions for which the report is executed
List partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, 'GROUP_ROLLUP');
partyIds.add(organizationPartyId);

// Get the group of account classes that will be used to position accounts in the proper section of the financial statement
GenericValue assetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "ASSET"), true);
List assetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(assetGlAccountClass);
GenericValue contraAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CONTRA_ASSET"), true);
List contraAssetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(contraAssetGlAccountClass);
GenericValue liabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "LIABILITY"), true);
List liabilityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(liabilityGlAccountClass);
GenericValue equityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "EQUITY"), true);
List equityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(equityGlAccountClass);
GenericValue currentAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CURRENT_ASSET"), true);
List currentAssetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(currentAssetGlAccountClass);
GenericValue longtermAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "LONGTERM_ASSET"), true);
List longtermAssetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(longtermAssetGlAccountClass);
GenericValue currentLiabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CURRENT_LIABILITY"), true);
List currentLiabilityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(currentLiabilityGlAccountClass);
GenericValue accumDepreciationGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "ACCUM_DEPRECIATION"), true);
List accumDepreciationAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(accumDepreciationGlAccountClass);
GenericValue accumAmortizationGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "ACCUM_AMORTIZATION"), true);
List accumAmortizationAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(accumAmortizationGlAccountClass);

// Find the last closed time period to get the fromDate for the transactions in the current period and the ending balances of the last closed period
Map lastClosedTimePeriodResult = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", organizationPartyId, "onlyFiscalPeriods", true, "findDate", new Date(thruDate.getTime()),"userLogin", userLogin));
//Debug.logInfo("lastClosedTimePeriodResult=" + lastClosedTimePeriodResult,"");

Timestamp fromDate = (Timestamp)lastClosedTimePeriodResult.lastClosedDate;
if (!fromDate) {
    return;
}
// Need to start from the next day if we're starting from the lastClosedTimePeriod
if (lastClosedTimePeriodResult.lastClosedTimePeriod) {
	fromDate = UtilDateTime.getNextDayStart(fromDate);
}
//Debug.logError("fromDate=" + fromDate,"");
GenericValue lastClosedTimePeriod = (GenericValue)lastClosedTimePeriodResult.lastClosedTimePeriod;
// Get the opening balances of all the accounts
Map assetOpeningBalances = [:];
Map contraAssetOpeningBalances = [:];
Map currentAssetOpeningBalances = [:];
Map longtermAssetOpeningBalances = [:];
Map liabilityOpeningBalances = [:];
Map currentLiabilityOpeningBalances = [:];
Map equityOpeningBalances = [:];
if (lastClosedTimePeriod) {
    List timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, assetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
	
//Debug.logError("timePeriodAndExprs=" + timePeriodAndExprs,"");
	
    List lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        assetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
//Debug.logError("assetOpeningBalances=" + assetOpeningBalances,"");
	
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, contraAssetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        contraAssetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, liabilityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
//Debug.logInfo("timePeriodAndExprs=" + timePeriodAndExprs,"");
//Debug.logInfo("lastTimePeriodHistories=" + lastTimePeriodHistories,"");
	lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        liabilityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, equityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        equityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentAssetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        currentAssetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, longtermAssetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        longtermAssetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        currentLiabilityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
}

List balanceTotalList = [];

List mainAndExprs = FastList.newInstance();
mainAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
mainAndExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
mainAndExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
mainAndExprs.add(EntityCondition.makeCondition("acctgTransTypeId", EntityOperator.NOT_EQUAL, "PERIOD_CLOSING"));
mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, thruDate));

Debug.logInfo("mainAndExprs=" + mainAndExprs,"");

// as an optimization fetch the totals for all account classes in one shot
transactionTotals = [];
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);

// ASSETS
// account balances
accountBalanceList = [];
glAccountClassIds = [];
balanceTotal = BigDecimal.ZERO;
//List assetAndExprs = FastList.newInstance(mainAndExprs);
//assetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, assetAccountClassIds));
//glAccountClassIds = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(assetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalAssets = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, assetAccountClassIds)])
glAccountClassIds = EntityUtil.getFieldListFromEntityList(transactionTotalAssets, "glAccountClassId", true);
//Debug.logInfo("transactionTotalAssets.size=" + transactionTotalAssets.size(),"");
//Debug.logInfo("transactionTotalAssets=" + transactionTotalAssets,"");
//Debug.logInfo("glAccountClassIds=" + glAccountClassIds,"");

transactionTotalsMap = [:];
transactionTotalsMap.putAll(assetOpeningBalances);
transactionTotalAssets.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("balance", BigDecimal.ZERO);
    }
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
    // assets are accounts of class DEBIT: the balance is given by debits minus credits plus any ending balance
    BigDecimal balance = (BigDecimal)accountMap.get("balance"); 
	if (transactionTotal.debitCreditFlag =='D') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.assetAccountBalanceList = accountBalanceList;
context.assetAccountBalanceList.add(UtilMisc.toMap("accountName", "TOTAL ASSETS", "balance", balanceTotal));
context.assetBalanceTotal = balanceTotal;


// CURRENT ASSETS
// account balances
accountBalanceList = [];
//transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
//List currentAssetAndExprs = FastList.newInstance(mainAndExprs);
//currentAssetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentAssetAccountClassIds));
//transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(currentAssetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalCurrAssets = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentAssetAccountClassIds)])

transactionTotalsMap = [:];
transactionTotalsMap.putAll(currentAssetOpeningBalances);
transactionTotalCurrAssets.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("balance", BigDecimal.ZERO);
    }
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
    // assets are accounts of class DEBIT: the balance is given by debits minus credits plus any ending balance
    BigDecimal balance = (BigDecimal)accountMap.get("balance"); 
	if (transactionTotal.debitCreditFlag =='D') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
	accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.currentAssetBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingCurrentAssets", "balance", balanceTotal));

// LONGTERM ASSETS
// account balances
accountBalanceList = [];
//transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
//List longtermAssetAndExprs = FastList.newInstance(mainAndExprs);
//longtermAssetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, longtermAssetAccountClassIds));
//transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(longtermAssetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalLongtermAssets = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, longtermAssetAccountClassIds)])

transactionTotalsMap = [:];
transactionTotalsMap.putAll(longtermAssetOpeningBalances);
transactionTotalLongtermAssets.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
	if (!accountMap) {
		accountMap = UtilMisc.makeMapWritable(transactionTotal);
		accountMap.remove("debitCreditFlag");
		accountMap.remove("amount");
		accountMap.put("balance", BigDecimal.ZERO);
	}
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
	// assets are accounts of class DEBIT: the balance is given by debits minus credits plus any ending balance
	BigDecimal balance = (BigDecimal)accountMap.get("balance");
	if (transactionTotal.debitCreditFlag =='D') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
	accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.longtermAssetBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingLongTermAssets", "balance", balanceTotal));

// CONTRA ASSETS
// account balances
accountBalanceList = [];
//transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
//List contraAssetAndExprs = FastList.newInstance(mainAndExprs);
//contraAssetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, contraAssetAccountClassIds));
//transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(contraAssetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalContraAssets = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, contraAssetAccountClassIds)])

transactionTotalsMap = [:];
transactionTotalsMap.putAll(contraAssetOpeningBalances);
transactionTotalContraAssets.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
	if (!accountMap) {
		accountMap = UtilMisc.makeMapWritable(transactionTotal);
		accountMap.remove("debitCreditFlag");
		accountMap.remove("amount");
		accountMap.put("balance", BigDecimal.ZERO);
	}
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
	// assets are accounts of class CREDIT: the balance is given by credits minus debits plus any ending balance
	BigDecimal balance = (BigDecimal)accountMap.get("balance");
	if (transactionTotal.debitCreditFlag =='C') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
	accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
//context.contraAssetAccountBalanceList = accountBalanceList;
context.assetAccountBalanceList.addAll(accountBalanceList);
context.assetAccountBalanceList.add(UtilMisc.toMap("accountName", "TOTAL ACCUMULATED DEPRECIATION", "balance", balanceTotal));
context.contraAssetBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingLongTermAssetsAtCost", "balance", (context.longtermAssetBalanceTotal - context.contraAssetBalanceTotal)));

// LIABILITY
// account balances
accountBalanceList = [];
//transactionTotals = [];
glAccountClassIdsLiability = [];
balanceTotal = BigDecimal.ZERO;
//List liabilityAndExprs = FastList.newInstance(mainAndExprs);
//liabilityAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, liabilityAccountClassIds));
//transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(liabilityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
//glAccountClassIdsLiability= delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(liabilityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalLiabilities = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, liabilityAccountClassIds)])
glAccountClassIdsLiability = EntityUtil.getFieldListFromEntityList(transactionTotalLiabilities, "glAccountClassId", true);

//Debug.logInfo("liabilityAccountClassIds=" + liabilityAccountClassIds,"");
//Debug.logInfo("transactionTotalLiabilities=" + transactionTotalLiabilities,"");
//Debug.logInfo("liabilityOpeningBalances=" + liabilityOpeningBalances,"");

transactionTotalsMap = [:];
transactionTotalsMap.putAll(liabilityOpeningBalances);
transactionTotalLiabilities.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
	if (!accountMap) {
		accountMap = UtilMisc.makeMapWritable(transactionTotal);
		accountMap.remove("debitCreditFlag");
		accountMap.remove("amount");
		accountMap.put("balance", BigDecimal.ZERO);
	}
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
	// assets are accounts of class CREDIT: the balance is given by credits minus debits plus any ending balance
	BigDecimal balance = (BigDecimal)accountMap.get("balance");
	if (transactionTotal.debitCreditFlag =='C') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
	accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.liabilityAccountBalanceList = accountBalanceList;
context.liabilityAccountBalanceList.add(UtilMisc.toMap("accountName", "TOTAL LIABILITIES", "balance", balanceTotal));
context.liabilityBalanceTotal = balanceTotal;


// CURRENT LIABILITY
// account balances
accountBalanceList = [];
//transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
//List currentLiabilityAndExprs = FastList.newInstance(mainAndExprs);
//currentLiabilityAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds));
//transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(currentLiabilityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalCurrLiabilities = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds)])
//Debug.logInfo("currentLiabilityAccountClassIds=" + currentLiabilityAccountClassIds,"");
//Debug.logInfo("transactionTotalCurrLiabilities=" + transactionTotalCurrLiabilities,"");
//Debug.logInfo("currentLiabilityOpeningBalances=" + currentLiabilityOpeningBalances,"");

transactionTotalsMap = [:];
transactionTotalsMap.putAll(currentLiabilityOpeningBalances);
transactionTotalCurrLiabilities.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
	if (!accountMap) {
		accountMap = UtilMisc.makeMapWritable(transactionTotal);
		accountMap.remove("debitCreditFlag");
		accountMap.remove("amount");
		accountMap.put("balance", BigDecimal.ZERO);
	}
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
	// assets are accounts of class CREDIT: the balance is given by credits minus debits plus any ending balance
	BigDecimal balance = (BigDecimal)accountMap.get("balance");
	if (transactionTotal.debitCreditFlag =='C') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
	accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.currentLiabilityBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingCurrentLiabilities", "balance", balanceTotal));


// EQUITY
// account balances
accountBalanceList = [];
//transactionTotals = [];
glAccountClassIdsEquity = [];
balanceTotal = BigDecimal.ZERO;
//List equityAndExprs = FastList.newInstance(mainAndExprs);
//equityAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, equityAccountClassIds));
//transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(equityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId","glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
//glAccountClassIdsEquity = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(equityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountClassId"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalEquities = EntityUtil.filterByAnd(transactionTotals, [EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, equityAccountClassIds)])
glAccountClassIdsEquity = EntityUtil.getFieldListFromEntityList(transactionTotalEquities, "glAccountClassId", true);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(equityOpeningBalances);
transactionTotalEquities.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
	if (!accountMap) {
		accountMap = UtilMisc.makeMapWritable(transactionTotal);
		accountMap.remove("debitCreditFlag");
		accountMap.remove("amount");
		accountMap.put("balance", BigDecimal.ZERO);
	}
	// always initialize to zero; balance field is only what we care about
	accountMap.put("D", BigDecimal.ZERO);
	accountMap.put("C", BigDecimal.ZERO);
	// assets are accounts of class CREDIT: the balance is given by credits minus debits plus any ending balance
	BigDecimal balance = (BigDecimal)accountMap.get("balance");
	if (transactionTotal.debitCreditFlag =='C') {
		balance = balance.add(transactionTotal.amount);
	}
	else {
		balance = balance.subtract(transactionTotal.amount);
	}
	accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
// Add the "retained earnings" account
//Map netIncomeResult = dispatcher.runSync("prepareIncomeStatement", UtilMisc.toMap("organizationPartyId", organizationPartyId, "glFiscalTypeId", glFiscalTypeId, "fromDate", fromDate, "thruDate", thruDate,"userLogin", userLogin));
//BigDecimal netIncome = (BigDecimal)netIncomeResult.totalNetIncome;
BigDecimal netIncome = UtilAccounting.getTotalNetIncome(delegator, organizationPartyId, lastClosedTimePeriodResult.lastClosedTimePeriod, transactionTotals);
GenericValue retainedEarningsAccount = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId", "RETAINED_EARNINGS", "organizationPartyId", organizationPartyId), true);
if (retainedEarningsAccount) {
    GenericValue retainedEarningsGlAccount = retainedEarningsAccount.getRelatedOne("GlAccount");
    transactionTotalsMap.put(retainedEarningsGlAccount.glAccountId, UtilMisc.toMap("glAccountClassId", retainedEarningsGlAccount.glAccountClassId,"glAccountId", retainedEarningsGlAccount.glAccountId,"accountName", retainedEarningsGlAccount.accountName, "accountCode", retainedEarningsGlAccount.accountCode, "balance", netIncome));
	glAccountClassIdsEquity.add(UtilMisc.toMap("glAccountClassId","RETAINED_EARNINGS"));
}
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.equityAccountBalanceList = accountBalanceList;
context.equityAccountBalanceList.add(UtilMisc.toMap("accountName", "TOTAL EQUITIES", "balance", balanceTotal));
context.equityBalanceTotal = balanceTotal;

context.liabilityEquityBalanceTotal = context.liabilityBalanceTotal + context.equityBalanceTotal
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingTotalLiabilitiesAndEquities", "balance", context.liabilityEquityBalanceTotal));

context.balanceTotalList = balanceTotalList;
openTree = false;
context.openTree = openTree;
context.glAccountClassIds = glAccountClassIds;
context.glAccountClassIdsLiability=glAccountClassIdsLiability;
context.glAccountClassIdsEquity=glAccountClassIdsEquity;
//Debug.logInfo(context.assetAccountBalanceList,"");

