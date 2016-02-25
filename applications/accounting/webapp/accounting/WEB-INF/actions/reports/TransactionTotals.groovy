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

import org.ofbiz.accounting.util.UtilAccounting;

import groovy.xml.Entity;
import javolution.util.FastList;

import java.sql.Date;


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
andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN, lastClosedDate));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, fromDate));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
//Debug.log("andCond======"+andCond);
List allPostedOpeningTransactionTotals = delegator.findList("AcctgTransEntrySums", andCond, null, UtilMisc.toList("glAccountId"), null, false);


//Debug.log("allPostedOpeningTransactionTotals======"+allPostedOpeningTransactionTotals);

andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List allPostedTransactionTotals = delegator.findList("AcctgTransEntrySums", andCond, null, UtilMisc.toList("glAccountId"), null, false);
//Debug.log("allPostedTransactionTotalItr======="+allPostedTransactionTotals.size());
glhistoryList =[];
if (allPostedTransactionTotals) {
    Map postedTransactionTotalsMap = [:]
    allPostedTransactionTotals.each { postedTransactionTotal ->
		GenericValue glAccount =null;
        Map accountMap = (Map)postedTransactionTotalsMap.get(postedTransactionTotal.glAccountId);
        if (!accountMap) {
            glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", postedTransactionTotal.glAccountId), true);
            if (glAccount) {
                boolean isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
                // Get the opening balances at the end of the last closed time period
                if (UtilAccounting.isAssetAccount(glAccount) || UtilAccounting.isLiabilityAccount(glAccount) || UtilAccounting.isEquityAccount(glAccount)) {
                    if (lastClosedTimePeriod) {
						glhistoryList.add(postedTransactionTotal.glAccountId);
                        List timePeriodAndExprs = FastList.newInstance();
                        timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, postedTransactionTotal.glAccountId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
                        lastTimePeriodHistory = EntityUtil.getFirst(delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false));
                        if (lastTimePeriodHistory) {
                            accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "openingD", BigDecimal.ZERO, "openingC", BigDecimal.ZERO, "D", BigDecimal.ZERO, "C", BigDecimal.ZERO);
							if((UtilAccounting.isDebitAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0)){
								accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")).negate());
							}
							else if(((UtilAccounting.isCreditAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") >0))){
								accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")));
						}
						else if(((UtilAccounting.isCreditAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0))){
						    accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance").negate());
						}else{
							accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance"));
						}
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
			andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, postedTransactionTotal.glAccountId));
			andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List transactionTotals = EntityUtil.filterByCondition(allPostedOpeningTransactionTotals, andCond);
			if (transactionTotals) {
				transactionTotals.each { transactionTotal ->
					UtilMisc.addToBigDecimalInMap(accountMap, "opening" + transactionTotal.debitCreditFlag, transactionTotal.amount);
				}
			}
        }
        UtilMisc.addToBigDecimalInMap(accountMap, postedTransactionTotal.debitCreditFlag, postedTransactionTotal.amount);		
		if((glAccount) && (glAccount.externalId)){		
			accountMap.put("externalId",glAccount.externalId);
		}
        postedTransactionTotalsMap.put(postedTransactionTotal.glAccountId, accountMap);
    }
	//124200
	// lets populate zero value gl for current month and which has trans in previous months
	List currentPostedGls = EntityUtil.getFieldListFromEntityList(allPostedTransactionTotals,"glAccountId", true);
	List zerosPostedBalances = EntityUtil.filterByCondition(allPostedOpeningTransactionTotals, EntityCondition.makeCondition("glAccountId",EntityOperator.NOT_IN,currentPostedGls )); 
	
	zerosPostedBalances.each { zerosPostedBalance ->
		Map accountMap = (Map)postedTransactionTotalsMap.get(zerosPostedBalance.glAccountId);
		if (!accountMap) {
			accountMap = UtilMisc.makeMapWritable(zerosPostedBalance);
			accountMap.put("openingD", BigDecimal.ZERO);
			accountMap.put("openingC", BigDecimal.ZERO);
			accountMap.put("D", BigDecimal.ZERO);
			accountMap.put("C", BigDecimal.ZERO);
			accountMap.put("balance", BigDecimal.ZERO);
		}
		GenericValue glAccount1 = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", zerosPostedBalance.glAccountId), true);
		glhistoryList.add(zerosPostedBalance.glAccountId);
		List timePeriodAndExprs = FastList.newInstance();
		timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
		timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, zerosPostedBalance.glAccountId));
		timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
		lastTimePeriodHistory = EntityUtil.getFirst(delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false));
		if (lastTimePeriodHistory) {
			accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "openingD", BigDecimal.ZERO, "openingC", BigDecimal.ZERO, "D", BigDecimal.ZERO, "C", BigDecimal.ZERO);
			if((UtilAccounting.isDebitAccount(glAccount1) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0)){
				accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")).negate());
			}
			else if(((UtilAccounting.isCreditAccount(glAccount1) && lastTimePeriodHistory.getBigDecimal("endingBalance") >0))){
				accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")));
		}
		else if(((UtilAccounting.isCreditAccount(glAccount1) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0))){
			accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance").negate());
		}else{
			accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance"));
		}
		}
		UtilMisc.addToBigDecimalInMap(accountMap, "opening" + zerosPostedBalance.debitCreditFlag, zerosPostedBalance.amount);
		postedTransactionTotalsMap.put(zerosPostedBalance.glAccountId, accountMap);
	}	
	
	 postedTotals = postedTransactionTotalsMap.values().asList();
}
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
// UNPOSTED
// Unposted transactions totals and grand totals
unpostedTotals = [];
unpostedTotalDebit = BigDecimal.ZERO;
unpostedTotalCredit = BigDecimal.ZERO;
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "N"));
andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, lastClosedDate));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, fromDate));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List allUnpostedOpeningTransactionTotals = delegator.findList("AcctgTransEntrySums", andCond, null, UtilMisc.toList("glAccountId"), null, false);

andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
andExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "N"));
andExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
andExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List allUnpostedTransactionTotals = delegator.findList("AcctgTransEntrySums", andCond, null, UtilMisc.toList("glAccountId"), null, false);

if (allUnpostedTransactionTotals) {
    Map unpostedTransactionTotalsMap = [:]
    allUnpostedTransactionTotals.each { unpostedTransactionTotal ->
    GenericValue glAccount = null;
        Map accountMap = (Map)unpostedTransactionTotalsMap.get(unpostedTransactionTotal.glAccountId);
        if (!accountMap) {
            glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", unpostedTransactionTotal.glAccountId), true);
            if (glAccount) {
                boolean isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
                // Get the opening balances at the end of the last closed time period
                if (UtilAccounting.isAssetAccount(glAccount) || UtilAccounting.isLiabilityAccount(glAccount) || UtilAccounting.isEquityAccount(glAccount)) {
                    /*if (lastClosedTimePeriod) {
                        List timePeriodAndExprs = FastList.newInstance();
                        timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, unpostedTransactionTotal.glAccountId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
                        lastTimePeriodHistory = EntityUtil.getFirst(delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false));
                        if (lastTimePeriodHistory) {
                            accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "openingD", BigDecimal.ZERO, "openingC", BigDecimal.ZERO, "D", BigDecimal.ZERO, "C", BigDecimal.ZERO);
							if((UtilAccounting.isDebitAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0)){
								accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")).negate());
							}
							else if(((UtilAccounting.isCreditAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") >0))){
								accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")));
						}
						else if(((UtilAccounting.isCreditAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0))){
						    accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance").negate());
						}else{
							accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance"));
						}
                        }
                    }*/
                }
            }
            if (!accountMap) {
                accountMap = UtilMisc.makeMapWritable(unpostedTransactionTotal);
                accountMap.put("openingD", BigDecimal.ZERO);
                accountMap.put("openingC", BigDecimal.ZERO);
                accountMap.put("D", BigDecimal.ZERO);
                accountMap.put("C", BigDecimal.ZERO);
                accountMap.put("balance", BigDecimal.ZERO);
            }
			// get opening balances
			andExprs = FastList.newInstance();
			andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, unpostedTransactionTotal.glAccountId));
			andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List transactionTotals = EntityUtil.filterByCondition(allUnpostedOpeningTransactionTotals, andCond);
			/*if (transactionTotals) {
				transactionTotals.each { transactionTotal ->
					UtilMisc.addToBigDecimalInMap(accountMap, "opening" + transactionTotal.debitCreditFlag, transactionTotal.amount);
				}
			}*/
        }
        UtilMisc.addToBigDecimalInMap(accountMap, unpostedTransactionTotal.debitCreditFlag, unpostedTransactionTotal.amount);
        if((glAccount) && (glAccount.externalId)){		
			accountMap.put("externalId",glAccount.externalId);
		}
        unpostedTransactionTotalsMap.put(unpostedTransactionTotal.glAccountId, accountMap);
    }
    unpostedTotals = unpostedTransactionTotalsMap.values().asList();
}
// Unposted grand total for Debits
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "D"));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List unpostedDebitTransactionTotals = EntityUtil.filterByCondition(allUnpostedTransactionTotals, andCond);
if (unpostedDebitTransactionTotals) {
	unpostedDebitTransactionTotals.each { transactionTotal ->
		unpostedTotalDebit = unpostedTotalDebit.add(transactionTotal.amount);
	}
}

// Unposted grand total for Credits
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "C"));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
List unpostedCreditTransactionTotals = EntityUtil.filterByCondition(allUnpostedTransactionTotals, andCond);
if (unpostedCreditTransactionTotals) {
	unpostedCreditTransactionTotals.each { transactionTotal ->
		unpostedTotalCredit = unpostedTotalCredit.add(transactionTotal.amount);
	}
}
unpostedTotals.add(["D":unpostedTotalDebit, "C":unpostedTotalCredit]);
context.unpostedTransactionTotals = unpostedTotals;

// POSTED AND UNPOSTED
// Posted and unposted transactions totals and grand totals
allTotals = [];
allTotalDebit = BigDecimal.ZERO;
allTotalCredit = BigDecimal.ZERO;
allTransactionTotals = [];
allTransactionTotals.addAll(allPostedTransactionTotals);
allTransactionTotals.addAll(allUnpostedTransactionTotals);
allOpeningTransactionTotals = [];
allOpeningTransactionTotals.addAll(allPostedOpeningTransactionTotals);
allOpeningTransactionTotals.addAll(allUnpostedOpeningTransactionTotals);

if (allTransactionTotals) {
    Map allTransactionTotalsMap = [:]
    allTransactionTotals.each { allTransactionTotal ->
        GenericValue glAccount = null;
        Map accountMap = (Map)allTransactionTotalsMap.get(allTransactionTotal.glAccountId);
        if (!accountMap) {
            glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", allTransactionTotal.glAccountId), true);
            if (glAccount) {
                boolean isDebitAccount = UtilAccounting.isDebitAccount(glAccount);
                // Get the opening balances at the end of the last closed time period
                if (UtilAccounting.isAssetAccount(glAccount) || UtilAccounting.isLiabilityAccount(glAccount) || UtilAccounting.isEquityAccount(glAccount)) {
                    if (lastClosedTimePeriod) {
                        List timePeriodAndExprs = FastList.newInstance();
                        timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, allTransactionTotal.glAccountId));
                        timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
                        lastTimePeriodHistory = EntityUtil.getFirst(delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false));
						if (lastTimePeriodHistory) {
							accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "openingD", BigDecimal.ZERO, "openingC", BigDecimal.ZERO, "D", BigDecimal.ZERO, "C", BigDecimal.ZERO);
							if((UtilAccounting.isDebitAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0)){
								accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")).negate());
							}
							else if(((UtilAccounting.isCreditAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") >0))){
								accountMap.put("openingC", (lastTimePeriodHistory.getBigDecimal("endingBalance")));
						}
						else if(((UtilAccounting.isCreditAccount(glAccount) && lastTimePeriodHistory.getBigDecimal("endingBalance") <0))){
							accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance").negate());
						}else{
							accountMap.put("openingD", lastTimePeriodHistory.getBigDecimal("endingBalance"));
						}
                        }
                    }
                }
            }
            if (!accountMap) {
                accountMap = UtilMisc.makeMapWritable(allTransactionTotal);
                accountMap.put("openingD", BigDecimal.ZERO);
                accountMap.put("openingC", BigDecimal.ZERO);
                accountMap.put("D", BigDecimal.ZERO);
                accountMap.put("C", BigDecimal.ZERO);
                accountMap.put("balance", BigDecimal.ZERO);
            }
            //
            List mainAndExprs = FastList.newInstance();
            mainAndExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "N"));
            mainAndExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, allTransactionTotal.glAccountId));
			andCond = EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND);
			List transactionTotals = EntityUtil.filterByCondition(allOpeningTransactionTotals, andCond);
            transactionTotals.each { transactionTotal ->
                UtilMisc.addToBigDecimalInMap(accountMap, "opening" + transactionTotal.debitCreditFlag, transactionTotal.amount);
            }
        }
        UtilMisc.addToBigDecimalInMap(accountMap, allTransactionTotal.debitCreditFlag, allTransactionTotal.amount);
        if((glAccount) && (glAccount.externalId)){		
			accountMap.put("externalId",glAccount.externalId);
		}
        allTransactionTotalsMap.put(allTransactionTotal.glAccountId, accountMap);
    }
    allTotals = allTransactionTotalsMap.values().asList();
}
// Posted and unposted grand total for Debits
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "D"));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
allDebitTransactionTotals = EntityUtil.filterByCondition(allTransactionTotals, andCond);
if (allDebitTransactionTotals) {
	allDebitTransactionTotals.each { transactionTotal ->
		allTotalDebit = allTotalDebit.add(transactionTotal.amount);
	}
}
// Posted and unposted grand total for Credits
andExprs = FastList.newInstance();
andExprs.add(EntityCondition.makeCondition("debitCreditFlag", EntityOperator.EQUALS, "C"));
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
allCreditTransactionTotals = EntityUtil.filterByCondition(allTransactionTotals, andCond);
if (allCreditTransactionTotals) {
	allCreditTransactionTotals.each { transactionTotal ->
		allTotalCredit = allTotalCredit.add(transactionTotal.amount);
	}
}
allTotals.add(["D":allTotalDebit, "C":allTotalCredit]);
context.allTransactionTotals = allTotals;