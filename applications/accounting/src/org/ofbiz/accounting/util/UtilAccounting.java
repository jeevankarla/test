/*******************************************************************************
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
 *******************************************************************************/

package org.ofbiz.accounting.util;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.sql.Timestamp;

import org.ofbiz.accounting.AccountingException;
import org.ofbiz.accounting.period.PeriodServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;


public class UtilAccounting {
    public static String module = UtilAccounting.class.getName();
    private static final int TAX_DECIMALS = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
    private static final int TAX_ROUNDING = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");

    /**
     * Get the GL Account for a product or the default account type based on input. This replaces the simple-method service
     * getProductOrgGlAccount. First it will look in ProductGlAccount using the primary keys productId and
     * productGlAccountTypeId. If none is found, it will look up GlAccountTypeDefault to find the default account for
     * organizationPartyId with type glAccountTypeId.
     *
     * @param   productId                  When searching for ProductGlAccounts, specify the productId
     * @param   glAccountTypeId            The default glAccountTypeId to look for if no ProductGlAccount is found
     * @param   organizationPartyId        The organization party of the default account
     * @return  The account ID (glAccountId) found
     * @throws  AccountingException        When the no accounts found or an entity exception occurs
     */
    public static String getProductOrgGlAccountId(String productId,
            String glAccountTypeId, String organizationPartyId, Delegator delegator)
        throws AccountingException {

        GenericValue account = null;
        try {
            // first try to find the account in ProductGlAccount
            account = delegator.findByPrimaryKeyCache("ProductGlAccount",
                    UtilMisc.toMap("productId", productId, "glAccountTypeId", glAccountTypeId, "organizationPartyId", organizationPartyId));
        } catch (GenericEntityException e) {
            throw new AccountingException("Failed to find a ProductGLAccount for productId [" + productId + "], organization [" + organizationPartyId + "], and productGlAccountTypeId [" + glAccountTypeId + "].", e);
        }

        // otherwise try the default accounts
        if (account == null) {
            try {
                account = delegator.findByPrimaryKeyCache("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId", glAccountTypeId, "organizationPartyId", organizationPartyId));
            } catch (GenericEntityException e) {
                throw new AccountingException("Failed to find a GlAccountTypeDefault for glAccountTypeId [" + glAccountTypeId + "] and organizationPartyId [" + organizationPartyId+ "].", e);
            }
        }

        // if no results yet, serious problem
        if (account == null) {
            throw new AccountingException("Failed to find any accounts for  productId [" + productId + "], organization [" + organizationPartyId + "], and productGlAccountTypeId [" + glAccountTypeId + "] or any accounts in GlAccountTypeDefault for glAccountTypeId [" + glAccountTypeId + "] and organizationPartyId [" + organizationPartyId+ "]. Please check your data to make sure that at least a GlAccountTypeDefault is defined for this account type and organization.");
        }

        // otherwise return the glAccountId
        return account.getString("glAccountId");
    }

    /**
     * As above, but explicitly looking for default account for given type and organization
     *
     * @param   glAccountTypeId         The type of account
     * @param   organizationPartyId     The organization of the account
     * @return  The default account ID (glAccountId) for this type
     * @throws  AccountingException     When the default is not configured
     */
    public static String getDefaultAccountId(String glAccountTypeId, String organizationPartyId, Delegator delegator) throws AccountingException {
        return getProductOrgGlAccountId(null, glAccountTypeId, organizationPartyId, delegator);
    }

    /**
     * Little method to figure out the net or ending balance of a GlAccountHistory or GlAccountAndHistory value, based on what kind
     * of account (DEBIT or CREDIT) it is
     * @param account - GlAccountHistory or GlAccountAndHistory value
     * @return balance - a BigDecimal
     */
    public static BigDecimal getNetBalance(GenericValue account, String debugModule) {
        try {
            return getNetBalance(account);
        } catch (GenericEntityException ex) {
            Debug.logError(ex.getMessage(), debugModule);
            return null;
        }
    }
    public static BigDecimal getNetBalance(GenericValue account) throws GenericEntityException {
        GenericValue glAccount = account.getRelatedOne("GlAccount");
        BigDecimal balance = BigDecimal.ZERO;
        if (isDebitAccount(glAccount)) {
            balance = account.getBigDecimal("postedDebits").subtract(account.getBigDecimal("postedCredits"));
        } else if (isCreditAccount(glAccount)) {
            balance = account.getBigDecimal("postedCredits").subtract(account.getBigDecimal("postedDebits"));
        }
        return balance;
    }

    public static List getDescendantGlAccountClassIds(GenericValue glAccountClass) throws GenericEntityException {
        List glAccountClassIds = FastList.newInstance();
        getGlAccountClassChildren(glAccountClass, glAccountClassIds);
        return glAccountClassIds;
    }
    private static void getGlAccountClassChildren(GenericValue glAccountClass, List glAccountClassIds) throws GenericEntityException {
        glAccountClassIds.add(glAccountClass.getString("glAccountClassId"));
        List glAccountClassChildren = glAccountClass.getRelatedCache("ChildGlAccountClass");
        Iterator glAccountClassChildrenIt = glAccountClassChildren.iterator();
        while (glAccountClassChildrenIt.hasNext()) {
            GenericValue glAccountClassChild = (GenericValue) glAccountClassChildrenIt.next();
            getGlAccountClassChildren(glAccountClassChild, glAccountClassIds);
        }
    }
    
    // Similar logic to prepareIncomeStatement but here the AccountingTransactinoEntry totals are
    // already available in an aggregated form (grouped together by account ids)
    public static BigDecimal getTotalNetIncome(Delegator delegator, String organizationPartyId, GenericValue lastClosedTimePeriod, List<GenericValue> transactionTotals) throws GenericEntityException {
        BigDecimal totalNetIncome = BigDecimal.ZERO;
        GenericValue accountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "EXPENSE"), false);
        List<String> expenseAccountClassIds = getDescendantGlAccountClassIds(accountClass);
        accountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "REVENUE"), false);
        List<String> revenueAccountClassIds = getDescendantGlAccountClassIds(accountClass);       
        accountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "INCOME"), false);
        List<String> incomeAccountClassIds = getDescendantGlAccountClassIds(accountClass);

        // Carry over any retained earnings from last closed time period
        if (lastClosedTimePeriod != null) {
            GenericValue retainedEarningsAccount = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("organizationPartyId", organizationPartyId, "glAccountTypeId", "RETAINED_EARNINGS" ), false);   
            GenericValue retainedEarningsAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap("organizationPartyId", organizationPartyId, "customTimePeriodId", lastClosedTimePeriod.getString("customTimePeriodId"), "glAccountId", retainedEarningsAccount.getString("glAccountId")), false);
            if (retainedEarningsAccountHistory != null) {
            	totalNetIncome = totalNetIncome.add(retainedEarningsAccountHistory.getBigDecimal("endingBalance"));
            }       
        }
        List<String> accountClassIds = expenseAccountClassIds;
        accountClassIds.addAll(revenueAccountClassIds);
        accountClassIds.addAll(incomeAccountClassIds);
        List<GenericValue> transactionTotalsFiltered = EntityUtil.filterByCondition(transactionTotals, EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, accountClassIds));
    	for( GenericValue transactionTotal : transactionTotalsFiltered) {
    		BigDecimal amount = transactionTotal.getBigDecimal("amount");
            GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", transactionTotal.getString("glAccountId")), false);
            if ((isDebitAccount(glAccount) && transactionTotal.getString("debitCreditFlag").equals("C")) ||
            	(isCreditAccount(glAccount) && transactionTotal.getString("debitCreditFlag").equals("D")) ||
            	isExpenseAccount(glAccount)) {
            	amount = amount.negate();
                Debug.logInfo("glAccount=" + glAccount, module); 
                Debug.logInfo("amount=" + amount, module);            	                
            } 
        	totalNetIncome = totalNetIncome.add(amount);            
    	}
        return totalNetIncome;
    }    
    public static Map getLastClosedGlBalance(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	String organizationPartyId =(String)context.get("organizationPartyId");
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	String glAccountId = (String)context.get("glAccountId");
    	String periodTypeId = (String)context.get("periodTypeId");
    	String isTrailBalance=(String) context.get("isTrailBalance");
    	 String costCenterId = (String) context.get("costCenterId");
         String segmentId = (String) context.get("segmentId");
         List<String> roBranchList = (List) context.get("roBranchList");
    	String lastClosedPeriodId = null;
    	List<GenericValue>  openingGlHistory = FastList.newInstance();
    	GenericValue lastClosedPeriod =null;
    	Timestamp lastClosedDate = null;
    	// Carry over any retained earnings from last closed time period
        if (customTimePeriodId != null) {
            GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);   
            /*GenericValue retainedEarningsAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap("organizationPartyId", organizationPartyId, "customTimePeriodId", lastClosedTimePeriod.getString("customTimePeriodId"), "glAccountId", retainedEarningsAccount.getString("glAccountId")), false);
            if (retainedEarningsAccountHistory != null) {
            	totalNetIncome = totalNetIncome.add(retainedEarningsAccountHistory.getBigDecimal("endingBalance"));
            } */ 
            Map lastClosedCtx = FastMap.newInstance();
            lastClosedCtx.put("organizationPartyId", organizationPartyId);
            lastClosedCtx.put("periodTypeId", customTimePeriod.getString("periodTypeId"));
            lastClosedCtx.put("findDate", customTimePeriod.getDate("fromDate"));
            lastClosedCtx.put("onlyFiscalPeriods", Boolean.TRUE);
            
            Map lastClosedPeriodResult = PeriodServices.findLastClosedDate(dctx, lastClosedCtx);
            lastClosedPeriod = (GenericValue)lastClosedPeriodResult.get("lastClosedTimePeriod");
            lastClosedDate=(Timestamp)lastClosedPeriodResult.get("lastClosedDate");
            Debug.log("lastClosedPeriod======fromLastClosed====="+lastClosedPeriod);
            if(UtilValidate.isNotEmpty(lastClosedPeriod)){
            	lastClosedPeriodId = lastClosedPeriod.getString("customTimePeriodId");
            	//It is for trail balance since we are showing closing balance only for adjacent closed periods
            	if(UtilValidate.isEmpty(isTrailBalance)||("Y".equals(isTrailBalance))){
	            	if(((UtilDateTime.getIntervalInDays(UtilDateTime.toTimestamp(lastClosedPeriod.getDate("thruDate")) ,UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) != 1))){
            		Map result = FastMap.newInstance();
                	result.put("openingGlHistory", openingGlHistory);
                    return result;
        		}
            }
       }
         
        }
    	if(UtilValidate.isNotEmpty(lastClosedPeriodId)){
    		
    		GenericValue assetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","ASSET"), true);
    		List assetAccountClassIds = getDescendantGlAccountClassIds(assetGlAccountClass);
    		
    		GenericValue contraAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","CONTRA_ASSET"), true);
    		List contraAssetAccountClassIds = getDescendantGlAccountClassIds(contraAssetGlAccountClass);
    		
    		GenericValue liabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","LIABILITY"), true);
    		List liabilityAccountClassIds = getDescendantGlAccountClassIds(liabilityGlAccountClass);
    		
    		
    		GenericValue equityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","EQUITY"), true);
    		List equityAccountClassIds = getDescendantGlAccountClassIds(equityGlAccountClass);
    		
    		
    		
    		List condList = FastList.newInstance();
    		condList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS,organizationPartyId));
    		condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,lastClosedPeriodId));
    		if(UtilValidate.isNotEmpty(glAccountId)){
    			condList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS,glAccountId));
    		}
    		 if(costCenterId!=null){
    			 condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.EQUALS, costCenterId));
    	     	}
    	     	if(roBranchList!=null){
    	     		condList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,roBranchList));
    	     	}
    	     	if(segmentId!=null){
	    	     	if(segmentId.equals("YARN_SALE")){
	    	     		condList.add(EntityCondition.makeCondition("segmentId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
	    	    	 }else{
	    	    		 condList.add(EntityCondition.makeCondition("segmentId", EntityOperator.EQUALS, segmentId));
	    	    	 }
    	     	}
			    
    		
    		List orCondList = FastList.newInstance();
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,assetAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,contraAssetAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,liabilityAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,equityAccountClassIds)));
    		condList.add(EntityCondition.makeCondition(orCondList,EntityOperator.OR));
    		
    		openingGlHistory = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND),
                    null,null, null, false);
    	}
    	Map result = FastMap.newInstance();
    	result.put("openingGlHistory", openingGlHistory);
    	result.put("lastClosedDate", lastClosedDate);
    	result.put("lastClosedPeriodId", lastClosedPeriodId);
        return result;
    }
    
    public static Map getLastClosedGlBalanceForParty(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	String organizationPartyId =(String)context.get("organizationPartyId");
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	String partyId = (String)context.get("partyId");
    	String glAccountId = (String)context.get("glAccountId");
    	String lastClosedPeriodId = null;
    	List<GenericValue>  openingGlHistory = FastList.newInstance();
    	GenericValue lastClosedPeriod =null;
       
    	lastClosedPeriodId=customTimePeriodId;
    	if(UtilValidate.isNotEmpty(lastClosedPeriodId)){
    		
    		GenericValue assetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","ASSET"), true);
    		List assetAccountClassIds = getDescendantGlAccountClassIds(assetGlAccountClass);
    		assetAccountClassIds.add("OB_TB");    		
    		GenericValue contraAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","CONTRA_ASSET"), true);
    		List contraAssetAccountClassIds = getDescendantGlAccountClassIds(contraAssetGlAccountClass);
    		
    		GenericValue liabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","LIABILITY"), true);
    		List liabilityAccountClassIds = getDescendantGlAccountClassIds(liabilityGlAccountClass);
    		
    		
    		GenericValue equityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","EQUITY"), true);
    		List equityAccountClassIds = getDescendantGlAccountClassIds(equityGlAccountClass);
    		
    		
    		
    		List condList = FastList.newInstance();
    		condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
    		condList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS,organizationPartyId));
    		condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,lastClosedPeriodId));
    		if(UtilValidate.isNotEmpty(glAccountId)){
    			condList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS,glAccountId));
    		}
    		
    		/*List orCondList = FastList.newInstance();
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,assetAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,contraAssetAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,liabilityAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,equityAccountClassIds)));
    		condList.add(EntityCondition.makeCondition(orCondList,EntityOperator.OR));*/
    		openingGlHistory = delegator.findList("GlAccountAndPartyHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND),
                    null,null, null, false);
    	}
    	Map result = FastMap.newInstance();
    	result.put("openingGlHistory", openingGlHistory);
        return result;
    }
    
    public static Map getLastClosedGlBalanceForCostCenter(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	String organizationPartyId =(String)context.get("organizationPartyId");
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	String costCenterId = (String)context.get("costCenterId");
    	String segmentId = (String)context.get("segmentId");
    	String glAccountId = (String)context.get("glAccountId");
    	String isTrailBalance=(String) context.get("isTrailBalance");
    	List<String> roBranchList = (List) context.get("roBranchList");
    	String lastClosedPeriodId = null;
    	List<GenericValue>  openingGlHistory = FastList.newInstance();
    	GenericValue lastClosedPeriod =null;
    	Timestamp lastClosedDate = null; 
     	if (customTimePeriodId != null) {
     		if(UtilValidate.isNotEmpty(isTrailBalance) && ("Y".equals(isTrailBalance))){
	             GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);   
	             /*GenericValue retainedEarningsAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap("organizationPartyId", organizationPartyId, "customTimePeriodId", lastClosedTimePeriod.getString("customTimePeriodId"), "glAccountId", retainedEarningsAccount.getString("glAccountId")), false);
	             if (retainedEarningsAccountHistory != null) {
	             	totalNetIncome = totalNetIncome.add(retainedEarningsAccountHistory.getBigDecimal("endingBalance"));
	             } */ 
	             Map lastClosedCtx = FastMap.newInstance();
	             lastClosedCtx.put("organizationPartyId", organizationPartyId);
	             lastClosedCtx.put("periodTypeId", customTimePeriod.getString("periodTypeId"));
	             lastClosedCtx.put("findDate", customTimePeriod.getDate("fromDate"));
	             lastClosedCtx.put("onlyFiscalPeriods", Boolean.TRUE);
	             
	             Map lastClosedPeriodResult = PeriodServices.findLastClosedDate(dctx, lastClosedCtx);
	             lastClosedPeriod = (GenericValue)lastClosedPeriodResult.get("lastClosedTimePeriod");
	             lastClosedDate=(Timestamp)lastClosedPeriodResult.get("lastClosedDate");
	             if(UtilValidate.isNotEmpty(lastClosedPeriod)){
	             	lastClosedPeriodId = lastClosedPeriod.getString("customTimePeriodId");
	             	//It is for trail balance since we are showing closing balance only for adjacent closed periods
	             	if(UtilValidate.isEmpty(isTrailBalance)||("Y".equals(isTrailBalance))){
	             		if(((UtilDateTime.getIntervalInDays(UtilDateTime.toTimestamp(lastClosedPeriod.getDate("thruDate")) ,UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) != 1))){
	 	            		Map result = FastMap.newInstance();            		
	 	                	result.put("openingGlHistory", openingGlHistory);
	 	                    return result;
	 	        		}
	             	}
	             }
	            
     		}
     		else{
     			lastClosedPeriodId = customTimePeriodId;
     		}
         }
    	
    	if(UtilValidate.isNotEmpty(lastClosedPeriodId)){
    		
    		GenericValue assetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","ASSET"), true);
    		List assetAccountClassIds = getDescendantGlAccountClassIds(assetGlAccountClass);
    		assetAccountClassIds.add("OB_TB");    		
    		GenericValue contraAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","CONTRA_ASSET"), true);
    		List contraAssetAccountClassIds = getDescendantGlAccountClassIds(contraAssetGlAccountClass);
    		
    		GenericValue liabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","LIABILITY"), true);
    		List liabilityAccountClassIds = getDescendantGlAccountClassIds(liabilityGlAccountClass);
    		
    		
    		GenericValue equityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId","EQUITY"), true);
    		List equityAccountClassIds = getDescendantGlAccountClassIds(equityGlAccountClass);
    		
    		
    		
    		List condList = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(costCenterId)){
    			condList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.EQUALS,costCenterId));
    		}
    		if(UtilValidate.isNotEmpty(roBranchList)){
    			condList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,roBranchList));
    		}
    		if(UtilValidate.isNotEmpty(segmentId)){
    			if(segmentId.equals("YARN_SALE")){
    				condList.add(EntityCondition.makeCondition("segmentId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
    			}
    			else{
    				condList.add(EntityCondition.makeCondition("segmentId",EntityOperator.EQUALS,segmentId));
    			}
    		}
    		condList.add(EntityCondition.makeCondition("organizationPartyId",EntityOperator.EQUALS,organizationPartyId));
    		condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,lastClosedPeriodId));
    		if(UtilValidate.isNotEmpty(glAccountId)){
    			condList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.EQUALS,glAccountId));
    		}
    		
    		/*List orCondList = FastList.newInstance();
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,assetAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,contraAssetAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,liabilityAccountClassIds)));
    		orCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountClassId",EntityOperator.IN,equityAccountClassIds)));
    		condList.add(EntityCondition.makeCondition(orCondList,EntityOperator.OR));*/
    		openingGlHistory = delegator.findList("GlAccountAndPartyHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND),
                    null,null, null, false);
    	}
    	Map result = FastMap.newInstance();
    	result.put("openingGlHistory", openingGlHistory);

        return result;
    }    

    /**
     * Recurses up payment type tree via parentTypeId to see if input payment type ID is in tree.
     */
    private static boolean isPaymentTypeRecurse(GenericValue paymentType, String inputTypeId) throws GenericEntityException {

        // first check the parentTypeId against inputTypeId
        String parentTypeId = paymentType.getString("parentTypeId");
        if (parentTypeId == null) {
            return false;
        }
        if (parentTypeId.equals(inputTypeId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isPaymentTypeRecurse(paymentType.getRelatedOne("ParentPaymentType"), inputTypeId);
    }


    /**
     * Checks if a payment is of a specified PaymentType.paymentTypeId.  Return false if payment is null.  It's better to use the
     * more specific calls like isTaxPayment().
     */
    public static boolean isPaymentType(GenericValue payment, String inputTypeId) throws GenericEntityException {
        if (payment == null) {
            return false;
        }

        GenericValue paymentType = payment.getRelatedOneCache("PaymentType");
        if (paymentType == null) {
            throw new GenericEntityException("Cannot find PaymentType for paymentId " + payment.getString("paymentId"));
        }

        String paymentTypeId = paymentType.getString("paymentTypeId");
        if (inputTypeId.equals(paymentTypeId)) {
            return true;
        }

        // recurse up tree
        return isPaymentTypeRecurse(paymentType, inputTypeId);
    }
    
    public static boolean isPaymentMethodType(GenericValue payment, String inputTypeId) throws GenericEntityException {
        if (payment == null) {
            return false;
        }

        String paymentMethodTypeId = payment.getString("paymentMethodTypeId");
        if (paymentMethodTypeId == null) {
            throw new GenericEntityException("Cannot find PaymentMethod Type for paymentId " + payment.getString("paymentId"));
        }

        if (paymentMethodTypeId.contains(inputTypeId)) {
            return true;
        }
        return false;
    }

    

    public static boolean isTaxPayment(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "TAX_PAYMENT");
    }

    public static boolean isDisbursement(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "DISBURSEMENT");
    }

    public static boolean isReceipt(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "RECEIPT");
    }


    /**
     * Determines if a glAccountClass is of a child of a certain parent glAccountClass.
     */
    public static boolean isAccountClassClass(GenericValue glAccountClass, String parentGlAccountClassId) throws GenericEntityException {
        if (glAccountClass == null) return false;

        // check current class against input classId
        if (parentGlAccountClassId.equals(glAccountClass.get("glAccountClassId"))) {
            return true;
        }

        // check parentClassId against inputClassId
        String parentClassId = glAccountClass.getString("parentClassId");
        if (parentClassId == null) {
            return false;
        }
        if (parentClassId.equals(parentGlAccountClassId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isAccountClassClass(glAccountClass.getRelatedOneCache("ParentGlAccountClass"), parentGlAccountClassId);
    }

    /**
     * Checks if a GL account is of a specified GlAccountClass.glAccountClassId.  Returns false if account is null.  It's better to use the
     * more specific calls like isDebitAccount().
     */
    public static boolean isAccountClass(GenericValue glAccount, String glAccountClassId) throws GenericEntityException {
        if (glAccount == null) {
            return false;
        }

        GenericValue glAccountClass = glAccount.getRelatedOneCache("GlAccountClass");
        if (glAccountClass == null) {
            throw new GenericEntityException("Cannot find GlAccountClass for glAccountId " + glAccount.getString("glAccountId"));
        }

        return isAccountClassClass(glAccountClass, glAccountClassId);
    }


    public static boolean isDebitAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "DEBIT");
    }

    public static boolean isCreditAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "CREDIT");
    }

    public static boolean isAssetAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "ASSET");
    }

    public static boolean isLiabilityAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "LIABILITY");
    }

    public static boolean isEquityAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "EQUITY");
    }

    public static boolean isIncomeAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "INCOME");
    }

    public static boolean isRevenueAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "REVENUE");
    }

    public static boolean isExpenseAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "EXPENSE");
    }

    /**
     * Recurses up invoice type tree via parentTypeId to see if input invoice type ID is in tree.
     */
    private static boolean isInvoiceTypeRecurse(GenericValue invoiceType, String inputTypeId) throws GenericEntityException {

        // first check the invoiceTypeId and parentTypeId against inputTypeId
        String invoiceTypeId = invoiceType.getString("invoiceTypeId");
        String parentTypeId = invoiceType.getString("parentTypeId");
        if (parentTypeId == null || invoiceTypeId.equals(parentTypeId)) {
            return false;
        }
        if (parentTypeId.equals(inputTypeId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isInvoiceTypeRecurse(invoiceType.getRelatedOne("ParentInvoiceType"), inputTypeId);
    }

    /**
     * Checks if a invoice is of a specified InvoiceType.invoiceTypeId. Return false if invoice is null. It's better to use
     * more specific calls like isPurchaseInvoice().
     */
    public static boolean isInvoiceType(GenericValue invoice, String inputTypeId) throws GenericEntityException {
        if (invoice == null) {
            return false;
        }

        GenericValue invoiceType = invoice.getRelatedOneCache("InvoiceType");
        if (invoiceType == null) {
            throw new GenericEntityException("Cannot find InvoiceType for invoiceId " + invoice.getString("invoiceId"));
        }

        String invoiceTypeId = invoiceType.getString("invoiceTypeId");
        if (inputTypeId.equals(invoiceTypeId)) {
            return true;
        }

        // recurse up tree
        return isInvoiceTypeRecurse(invoiceType, inputTypeId);
    }
    

    public static boolean isPurchaseInvoice(GenericValue invoice) throws GenericEntityException {
        return isInvoiceType(invoice, "PURCHASE_INVOICE");
    }

    public static boolean isSalesInvoice(GenericValue invoice) throws GenericEntityException {
        return isInvoiceType(invoice, "SALES_INVOICE");
    }

    public static boolean isTemplate(GenericValue invoice) throws GenericEntityException {
        return isInvoiceType(invoice, "TEMPLATE");
    }
    
    
    public static Map<String, Object> checkEnableAccounting(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> serviceContext= (Map)context.get("serviceContext");
        String invoiceId = null;
        String paymentId = null;
        if(UtilValidate.isNotEmpty(serviceContext)){
        	 invoiceId = (String)serviceContext.get("invoiceId");
             paymentId = (String)serviceContext.get("paymentId");
        }
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        if(UtilValidate.isEmpty(invoiceId)){
        	 invoiceId = (String)context.get("invoiceId");
        }
        if(UtilValidate.isEmpty(paymentId)){
        	paymentId = (String)context.get("paymentId");
       }
        Boolean conditionReply = Boolean.TRUE;
        
        Boolean invConditionReply = Boolean.TRUE;
        Boolean pmConditionReply = Boolean.TRUE;
        try {
        	if(UtilValidate.isNotEmpty(invoiceId)){
        		GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId) ,true);
        		if(UtilValidate.isNotEmpty(invoice.get("isEnableAcctg")) && (invoice.getString("isEnableAcctg")).equals("N") ){
        			conditionReply = Boolean.FALSE ;
        			invConditionReply = Boolean.FALSE;
        		}
        	}
        	if(UtilValidate.isNotEmpty(paymentId)){
        		conditionReply = Boolean.TRUE;
        		GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId) ,true);
        		if(UtilValidate.isNotEmpty(payment.get("isEnableAcctg")) && (payment.getString("isEnableAcctg")).equals("N") ){
        			conditionReply = Boolean.FALSE ;
        			pmConditionReply = Boolean.FALSE;
        		}
        	}
        	
    	 GenericValue tenantConfigEnableAccounting = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","ACCOUNTING", "propertyName","enableAccounting"), true);
    	 if (UtilValidate.isNotEmpty(tenantConfigEnableAccounting) && (tenantConfigEnableAccounting.getString("propertyValue")).equals("Y")) {
			 conditionReply = Boolean.TRUE ;				
		 }else{
			 conditionReply = Boolean.FALSE ;
		 }

        	/*if(UtilValidate.isNotEmpty(paymentId) && UtilValidate.isNotEmpty(invoiceId) && (!(!invConditionReply && !pmConditionReply)) && !conditionReply){
        		Debug.logError("===mismatch invoice and payment accounting configuration==="+result ,module);
        		return ServiceUtil.returnError("mismatch invoice and payment accounting configuration");
        		
        	}*/
           
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Invoice for Invoice ID" + invoiceId, module);
            return ServiceUtil.returnError("Problem getting Invoice for Invoice ID" + invoiceId);
        }
        result.put("conditionReply", conditionReply);
        Debug.logInfo("result============="+result , module);
        return result;
    }
    
    public static Map<String, Object> setIsEnableAccountingForInvoiceOrPayment(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String invoiceId = (String)context.get("invoiceId");
        String paymentId = (String)context.get("paymentId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
        	if(UtilValidate.isNotEmpty(invoiceId)){
        		GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId) ,true);
        		invoice.set("isEnableAcctg", "N");
        		delegator.store(invoice);
        		return result;
        	}
        	if(UtilValidate.isNotEmpty(paymentId)){
        		GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId) ,true);
        		payment.set("isEnableAcctg", "N");
        		delegator.store(payment);
        		return result;
        	}
           
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Invoice for Invoice ID" + invoiceId, module);
            return ServiceUtil.returnError("Problem getting Invoice for Invoice ID" + invoiceId);
        }
        return result;
    }

  public static Map getExclusiveTaxRate(BigDecimal rate, BigDecimal taxPercent) {
	   Map<String ,Object> rateMap = FastMap.newInstance();
	   BigDecimal exRate = BigDecimal.ZERO;
	   BigDecimal taxAmount = BigDecimal.ZERO;
        try {
        	//(rate*100/(100+taxPercent)
        	exRate = (rate.multiply(new BigDecimal(100))).divide((new BigDecimal(100)).add(taxPercent), TAX_DECIMALS,TAX_ROUNDING);
        	taxAmount = rate.subtract(exRate);
        } catch (Exception ex) {
            Debug.logError(ex.getMessage(), module);
            return null;
        }
        rateMap.put("rate", exRate);
        rateMap.put("taxAmount", taxAmount);
        return rateMap;
    }
  
  public static Map getInclusiveTaxRate(BigDecimal rate, BigDecimal taxPercent) {
	  Map<String ,Object> rateMap = FastMap.newInstance();
	   BigDecimal incRate = BigDecimal.ZERO;
	   BigDecimal taxAmount = BigDecimal.ZERO;
      try {
      	//(((rate*taxPercent)/100)+rate)
    	  incRate = ((rate.multiply(taxPercent)).divide((new BigDecimal(100)), TAX_DECIMALS,TAX_ROUNDING)).add(rate);
      	taxAmount = incRate.subtract(rate);
      } catch (Exception ex) {
          Debug.logError(ex.getMessage(), module);
          return null;
      }
      rateMap.put("rate", incRate);
      rateMap.put("taxAmount", taxAmount);
     return rateMap;
  }  
  
}
