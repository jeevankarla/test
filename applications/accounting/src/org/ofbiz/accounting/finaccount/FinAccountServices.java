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

package org.ofbiz.accounting.finaccount;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.finaccount.FinAccountHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;


public class FinAccountServices {

    public static final String module = FinAccountServices.class.getName();

    public static Map<String, Object> createAccountAndCredit(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String finAccountTypeId = (String) context.get("finAccountTypeId");
        String accountName = (String) context.get("accountName");
        String finAccountId = (String) context.get("finAccountId");

        // check the type
        if (finAccountTypeId == null) {
            finAccountTypeId = "SVCCRED_ACCOUNT";
        }
        if (accountName == null) {
            if ("SVCCRED_ACCOUNT".equals(finAccountTypeId)) {
                accountName = "Customer Service Credit Account";
            } else {
                accountName = "Financial Account";
            }
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try {
            // find the most recent (active) service credit account for the specified party
            String partyId = (String) context.get("partyId");
            Map<String, String> lookupMap = UtilMisc.toMap("finAccountTypeId", finAccountTypeId, "ownerPartyId", partyId);

            // if a productStoreId is present, restrict the accounts returned using the store's payToPartyId
            String productStoreId = (String) context.get("productStoreId");
            if (UtilValidate.isNotEmpty(productStoreId)) {
                String payToPartyId = ProductStoreWorker.getProductStorePayToPartyId(productStoreId, delegator);
                if (UtilValidate.isNotEmpty(payToPartyId)) {
                    lookupMap.put("organizationPartyId", payToPartyId);
                }
            }

            // if a currencyUomId is present, use it to restrict the accounts returned
            String currencyUomId = (String) context.get("currencyUomId");
            if (UtilValidate.isNotEmpty(currencyUomId)) {
                lookupMap.put("currencyUomId", currencyUomId);
            }

            // check for an existing account
            GenericValue creditAccount;
            if (finAccountId != null) {
                creditAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
            } else {
                List<GenericValue> creditAccounts = delegator.findByAnd("FinAccount", lookupMap, UtilMisc.toList("-fromDate"));
                creditAccount = EntityUtil.getFirst(EntityUtil.filterByDate(creditAccounts));
            }

            if (creditAccount == null) {
                // create a new service credit account
                String createAccountServiceName = "createFinAccount";
                if (UtilValidate.isNotEmpty(productStoreId)) {
                    createAccountServiceName = "createFinAccountForStore";
                }
                // automatically set the parameters
                ModelService createAccountService = dctx.getModelService(createAccountServiceName);
                Map<String, Object> createAccountContext = createAccountService.makeValid(context, ModelService.IN_PARAM);
                createAccountContext.put("finAccountTypeId", finAccountTypeId);
                createAccountContext.put("finAccountName", accountName);
                createAccountContext.put("ownerPartyId", partyId);
                createAccountContext.put("userLogin", userLogin);

                Map<String, Object> createAccountResult = dispatcher.runSync(createAccountServiceName, createAccountContext);
                if (ServiceUtil.isError(createAccountResult) || ServiceUtil.isFailure(createAccountResult)) {
                    return createAccountResult;
                }

                if (createAccountResult != null) {
                    String creditAccountId = (String) createAccountResult.get("finAccountId");
                    if (UtilValidate.isNotEmpty(creditAccountId)) {
                        creditAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", creditAccountId));

                        // create the owner role
                        Map<String, Object> roleCtx = FastMap.newInstance();
                        roleCtx.put("partyId", partyId);
                        roleCtx.put("roleTypeId", "OWNER");
                        roleCtx.put("finAccountId", creditAccountId);
                        roleCtx.put("userLogin", userLogin);
                        roleCtx.put("fromDate", UtilDateTime.nowTimestamp());
                        Map<String, Object> roleResp;
                        try {
                            roleResp = dispatcher.runSync("createFinAccountRole", roleCtx);
                        } catch (GenericServiceException e) {
                            return ServiceUtil.returnError(e.getMessage());
                        }
                        if (ServiceUtil.isError(roleResp)) {
                            return roleResp;
                        }
                        finAccountId = creditAccountId; // update the finAccountId for return parameter
                    }
                }
                if (creditAccount == null) {
                    return ServiceUtil.returnError("Could not find or create a service credit account");
                }
            }

            // create the credit transaction
            Map<String, Object> transactionMap = FastMap.newInstance();
            transactionMap.put("finAccountTransTypeId", "ADJUSTMENT");
            transactionMap.put("finAccountId", creditAccount.getString("finAccountId"));
            transactionMap.put("partyId", partyId);
            transactionMap.put("amount", context.get("amount"));
            transactionMap.put("reasonEnumId", context.get("reasonEnumId"));
            transactionMap.put("comments", context.get("comments"));
            transactionMap.put("userLogin", userLogin);

            Map<String, Object> creditTransResult = dispatcher.runSync("createFinAccountTrans", transactionMap);
            if (ServiceUtil.isError(creditTransResult) || ServiceUtil.isFailure(creditTransResult)) {
                return creditTransResult;
            }
        } catch (GenericEntityException gee) {
            return ServiceUtil.returnError(gee.getMessage());
        } catch (GenericServiceException gse) {
            return ServiceUtil.returnError(gse.getMessage());
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("finAccountId", finAccountId);
        return result;
    }

    public static Map<String, Object> createFinAccountForStore(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String finAccountTypeId = (String) context.get("finAccountTypeId");

        try {
            // get the product store id and use it to generate a unique fin account code
            GenericValue productStoreFinAccountSetting = delegator.findByPrimaryKeyCache("ProductStoreFinActSetting", UtilMisc.toMap("productStoreId", productStoreId, "finAccountTypeId", finAccountTypeId));
            if (productStoreFinAccountSetting == null) {
                return ServiceUtil.returnError("No settings found for store [" + productStoreId + "] for fin account type [" + finAccountTypeId + "]");
            }

            Long accountCodeLength = productStoreFinAccountSetting.getLong("accountCodeLength");
            Long accountValidDays = productStoreFinAccountSetting.getLong("accountValidDays");
            Long pinCodeLength = productStoreFinAccountSetting.getLong("pinCodeLength");
            String requirePinCode = productStoreFinAccountSetting.getString("requirePinCode");

            // automatically set the parameters for the create fin account service
            ModelService createService = dctx.getModelService("createFinAccount");
            Map<String, Object> inContext = createService.makeValid(context, ModelService.IN_PARAM);
            Timestamp now = UtilDateTime.nowTimestamp();

            // now use our values
            String finAccountCode = FinAccountHelper.getNewFinAccountCode(accountCodeLength.intValue(), delegator);
            inContext.put("finAccountCode", finAccountCode);

            // with pin codes, the account code becomes the ID and the pin becomes the code
            if ("Y".equalsIgnoreCase(requirePinCode)) {
                String pinCode = FinAccountHelper.getNewFinAccountCode(pinCodeLength.intValue(), delegator);
                inContext.put("finAccountPin", pinCode);
            }

            // set the dates/userlogin
            if (UtilValidate.isNotEmpty(accountValidDays)){
                inContext.put("thruDate", UtilDateTime.getDayEnd(now, accountValidDays));
            }
            inContext.put("fromDate", now);
            inContext.put("userLogin", userLogin);

            // product store payToPartyId
            String payToPartyId = ProductStoreWorker.getProductStorePayToPartyId(productStoreId, delegator);
            inContext.put("organizationPartyId", payToPartyId);

            Map<String, Object> createResult = dispatcher.runSync("createFinAccount", inContext);

            if (ServiceUtil.isError(createResult)) {
                return createResult;
            }
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("finAccountId", createResult.get("finAccountId"));
            result.put("finAccountCode", finAccountCode);
            return result;
        } catch (GenericEntityException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        } catch (GenericServiceException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }
    }

    public static Map<String, Object> checkFinAccountBalance(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        String finAccountId = (String) context.get("finAccountId");
        String finAccountCode = (String) context.get("finAccountCode");

        GenericValue finAccount;
        if (finAccountId == null) {
            try {
                finAccount = FinAccountHelper.getFinAccountFromCode(finAccountCode, delegator);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        } else {
            try {
                finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        if (finAccount == null) {
            return ServiceUtil.returnError("Unable to locate financial account");
        }

        // get the balance
        BigDecimal availableBalance = finAccount.getBigDecimal("availableBalance");
        BigDecimal balance = finAccount.getBigDecimal("actualBalance");
        if (availableBalance == null) {
            availableBalance = FinAccountHelper.ZERO;
        }
        if (balance == null) {
            balance = FinAccountHelper.ZERO;
        }

        String statusId = finAccount.getString("statusId");
        Debug.log("FinAccount Balance [" + balance + "] Available [" + availableBalance + "] - Status: " + statusId, module);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("availableBalance", availableBalance);
        result.put("balance", balance);
        result.put("statusId", statusId);
        return result;
    }

    public static Map<String, Object> checkFinAccountStatus(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        String finAccountId = (String) context.get("finAccountId");

        if (finAccountId == null) {
            return ServiceUtil.returnError("Financial account ID is required for this service!");
        }

        GenericValue finAccount;
        try {
            finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
        } catch (GenericEntityException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }

        if (finAccount != null) {
            String statusId = finAccount.getString("statusId");
            if (statusId == null) statusId = "FNACT_ACTIVE";

            BigDecimal balance = finAccount.getBigDecimal("actualBalance");
            if (balance == null) {
                balance = FinAccountHelper.ZERO;
            }

            Debug.logInfo("Account #" + finAccountId + " Balance: " + balance + " Status: " + statusId, module);

            if ("FNACT_ACTIVE".equals(statusId) && balance.compareTo(FinAccountHelper.ZERO) < 1) {
                finAccount.set("statusId", "FNACT_MANFROZEN");
                Debug.logInfo("Financial account [" + finAccountId + "] has passed its threshold [" + balance + "] (Frozen)", module);
            } else if ("FNACT_MANFROZEN".equals(statusId) && balance.compareTo(FinAccountHelper.ZERO) > 0) {
                finAccount.set("statusId", "FNACT_ACTIVE");
                Debug.logInfo("Financial account [" + finAccountId + "] has been made current [" + balance + "] (Un-Frozen)", module);
            }
            try {
                finAccount.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> refundFinAccount(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String finAccountId = (String) context.get("finAccountId");
        Map<String, Object> result = null;

        GenericValue finAccount;
        try {
            finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        if (finAccount != null) {
            // check to make sure the account is refundable
            if (!"Y".equals(finAccount.getString("isRefundable"))) {
                return ServiceUtil.returnError("Account is not refunable");
            }

            // get the actual and available balance
            BigDecimal availableBalance = finAccount.getBigDecimal("availableBalance");
            BigDecimal actualBalance = finAccount.getBigDecimal("actualBalance");

            // if they do not match, then there are outstanding authorizations which need to be settled first
            if (actualBalance.compareTo(availableBalance) != 0) {
                return ServiceUtil.returnError("Available balance does not match the actual balance; pending authorizations; cannot refund FinAccount at this time.");
            }

            // now we make sure there is something to refund
            if (actualBalance.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal remainingBalance = new BigDecimal(actualBalance.toString());
                BigDecimal refundAmount = BigDecimal.ZERO;

                List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "DEPOSIT"),
                        EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
                EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);

                EntityListIterator eli = null;
                try {
                    eli = delegator.find("FinAccountTrans", condition, null, null, UtilMisc.toList("-transactionDate"), null);

                    GenericValue trans;
                    while (remainingBalance.compareTo(FinAccountHelper.ZERO) < 0 && (trans = eli.next()) != null) {
                        String orderId = trans.getString("orderId");
                        String orderItemSeqId = trans.getString("orderItemSeqId");

                        // make sure there is an order available to refund
                        if (orderId != null && orderItemSeqId != null) {
                            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId",orderId));
                            GenericValue productStore = delegator.getRelatedOne("ProductStore", orderHeader);
                            GenericValue orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                            if (!"ITEM_CANCELLED".equals(orderItem.getString("statusId"))) {

                                // make sure the item hasn't already been returned
                                List<GenericValue> returnItems = orderItem.getRelated("ReturnItem");
                                if (UtilValidate.isEmpty(returnItems)) {
                                    BigDecimal txAmt = trans.getBigDecimal("amount");
                                    BigDecimal refAmt = txAmt;
                                    if (remainingBalance.compareTo(txAmt) == -1) {
                                        refAmt = remainingBalance;
                                    }
                                    remainingBalance = remainingBalance.subtract(refAmt);
                                    refundAmount = refundAmount.add(refAmt);

                                    // create the return header
                                    Map<String, Object> rhCtx = UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN", "fromPartyId", finAccount.getString("ownerPartyId"), "toPartyId", productStore.getString("payToPartyId"), "userLogin", userLogin);
                                    Map<String, Object> rhResp = dispatcher.runSync("createReturnHeader", rhCtx);
                                    if (ServiceUtil.isError(rhResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(rhResp));
                                    }
                                    String returnId = (String) rhResp.get("returnId");

                                    // create the return item
                                    Map<String, Object> returnItemCtx = FastMap.newInstance();
                                    returnItemCtx.put("returnId", returnId);
                                    returnItemCtx.put("orderId", orderId);
                                    returnItemCtx.put("description", orderItem.getString("itemDescription"));
                                    returnItemCtx.put("orderItemSeqId", orderItemSeqId);
                                    returnItemCtx.put("returnQuantity", BigDecimal.ONE);
                                    returnItemCtx.put("receivedQuantity", BigDecimal.ONE);
                                    returnItemCtx.put("returnPrice", refAmt);
                                    returnItemCtx.put("returnReasonId", "RTN_NOT_WANT");
                                    returnItemCtx.put("returnTypeId", "RTN_REFUND"); // refund return
                                    returnItemCtx.put("returnItemTypeId", "RET_NPROD_ITEM");
                                    returnItemCtx.put("userLogin", userLogin);

                                    Map<String, Object> retItResp = dispatcher.runSync("createReturnItem", returnItemCtx);
                                    if (ServiceUtil.isError(retItResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(retItResp));
                                    }
                                    String returnItemSeqId = (String) retItResp.get("returnItemSeqId");

                                    // approve the return
                                    Map<String, Object> appRet = UtilMisc.toMap("statusId", "RETURN_ACCEPTED", "returnId", returnId, "userLogin", userLogin);
                                    Map<String, Object> appResp = dispatcher.runSync("updateReturnHeader", appRet);
                                    if (ServiceUtil.isError(appResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(appResp));
                                    }

                                    // "receive" the return - should trigger the refund
                                    Map<String, Object> recRet = UtilMisc.toMap("statusId", "RETURN_RECEIVED", "returnId", returnId, "userLogin", userLogin);
                                    Map<String, Object> recResp = dispatcher.runSync("updateReturnHeader", recRet);
                                    if (ServiceUtil.isError(recResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(recResp));
                                    }

                                    // get the return item
                                    GenericValue returnItem = delegator.findByPrimaryKey("ReturnItem",
                                            UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
                                    GenericValue response = returnItem.getRelatedOne("ReturnItemResponse");
                                    if (response == null) {
                                        throw new GeneralException("No return response found for: " + returnItem.getPrimaryKey());
                                    }
                                    String paymentId = response.getString("paymentId");

                                    // create the adjustment transaction
                                    Map<String, Object> txCtx = FastMap.newInstance();
                                    txCtx.put("finAccountTransTypeId", "ADJUSTMENT");
                                    txCtx.put("finAccountId", finAccountId);
                                    txCtx.put("orderId", orderId);
                                    txCtx.put("orderItemSeqId", orderItemSeqId);
                                    txCtx.put("paymentId", paymentId);
                                    txCtx.put("amount", refAmt.negate());
                                    txCtx.put("partyId", finAccount.getString("ownerPartyId"));
                                    txCtx.put("userLogin", userLogin);

                                    Map<String, Object> txResp = dispatcher.runSync("createFinAccountTrans", txCtx);
                                    if (ServiceUtil.isError(txResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(txResp));
                                    }
                                }
                            }
                        }
                    }
                } catch (GeneralException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                } finally {
                    if (eli != null) {
                        try {
                            eli.close();
                        } catch (GenericEntityException e) {
                            Debug.logWarning(e, module);
                        }
                    }
                }

                // check to make sure we balanced out
                if (remainingBalance.compareTo(FinAccountHelper.ZERO) == 1) {
                    result = ServiceUtil.returnSuccess("FinAccount partially refunded; not enough replenish deposits to refund!");
                }
            }
        }

        if (result == null) {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }
    public static Map<String, Object> setMassFinAccountTransStatus(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        List finAccountTransIdsList =  (List)context.get("finAccountTransIds");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String  statusId=(String)context.get("statusId");
        Map finAccountTransMap = UtilMisc.toMap("statusId",statusId);
            finAccountTransMap.put("userLogin", userLogin);
            for(int i = 0; i < finAccountTransIdsList.size(); i++){
           	 String finAccountTransId = (String) finAccountTransIdsList.get(i);
           	 finAccountTransMap.put("finAccountTransId", finAccountTransId);
           try {
           	Map finAccountTransMapResult=dispatcher.runSync("setFinAccountTransStatus", finAccountTransMap);
               if (ServiceUtil.isError(finAccountTransMapResult)){
     		  		String errMsg =  ServiceUtil.getErrorMessage(finAccountTransMapResult);
     		  		Debug.logError(errMsg , module);
     		  	    return ServiceUtil.returnError("setFinAccountTransStatus  having Problem for" + finAccountTransId);    
     		  	}
           } catch (GenericServiceException e) {
               Debug.logError(e, "setFinAccountTransStatus  having Problem for", module);
               return ServiceUtil.returnError(e.getMessage());
           } 
         } 
        return ServiceUtil.returnSuccess("Status Change Successfully for Selected FinAccountTransactions");
    }
    public static Map<String, Object> createReconsileAndUpdateFinAccountTrans(DispatchContext dctx, Map<String, Object> context) {//Simplifying Reconsilation
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        List finAccountTransIdsList =  (List)context.get("finAccountTransIds");
        String organizationPartyId =  (String)context.get("organizationPartyId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String  statusId=(String)context.get("statusId");
        String  finAccountId=(String)context.get("finAccountId");
        
      String dateGlReconciliStr= UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "-dd/MM/yyyy HH:mm:ss");
         try {
        GenericValue finAccountDetails = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
        if(UtilValidate.isEmpty(finAccountDetails)){
                 return ServiceUtil.returnError("Unable to locate financial account");
        }
        if(UtilValidate.isEmpty(organizationPartyId)){
        	organizationPartyId=finAccountDetails.getString("ownerPartyId");
        }
        Map newGlReconcilationMap = UtilMisc.toMap("statusId","GLREC_CREATED");
        newGlReconcilationMap.put("userLogin", userLogin);
        newGlReconcilationMap.put("glAccountId", finAccountDetails.getString("postToGlAccountId"));
        newGlReconcilationMap.put("glReconciliationName", finAccountDetails.getString("finAccountId")+dateGlReconciliStr);
        newGlReconcilationMap.put("description", finAccountDetails.getString("finAccountName")+dateGlReconciliStr);
        newGlReconcilationMap.put("organizationPartyId", finAccountDetails.getString("ownerPartyId"));
        newGlReconcilationMap.put("reconciledDate", UtilDateTime.nowTimestamp());
        
        String  glReconciliationId="";
    	Map createGlReconcilMapResult=dispatcher.runSync("createGlReconciliation", newGlReconcilationMap);
        if (ServiceUtil.isError(createGlReconcilMapResult)){
		  		String errMsg =  ServiceUtil.getErrorMessage(createGlReconcilMapResult);
		  		Debug.logError(errMsg , module);
		  	    return ServiceUtil.returnError("Creation of Reoncilation Having Some Problem For BankAccount:" + finAccountId);    
		  }
        glReconciliationId=(String)createGlReconcilMapResult.get("glReconciliationId");
        
        Map finAccountTransMap = UtilMisc.toMap("statusId",statusId);
            finAccountTransMap.put("userLogin", userLogin);
            //assigning ReconcilationId to FinAccountTrans batch
            for(int i = 0; i < finAccountTransIdsList.size(); i++){
           	 String finAccountTransId = (String) finAccountTransIdsList.get(i);
           	 finAccountTransMap.put("finAccountTransId", finAccountTransId);
           	// Debug.log("===finAccountTransId=="+finAccountTransId+"==finAccountId==="+finAccountId+"=glReconciliationId=="+glReconciliationId);
           	try {
               	Map assignGlRecToFinAccTransRes=dispatcher.runSync("assignGlRecToFinAccTrans",  UtilMisc.toMap("finAccountTransId",finAccountTransId,"glReconciliationId",glReconciliationId,"userLogin",userLogin));
                   if (ServiceUtil.isError(assignGlRecToFinAccTransRes)){
         		  		String errMsg =  ServiceUtil.getErrorMessage(assignGlRecToFinAccTransRes);
         		  		Debug.logError(errMsg , module);
         		  	    return ServiceUtil.returnError("Problem for Assigning of TransactionId:" + finAccountTransId+" To ReconcilationId ");    
         		  	}
               } catch (GenericServiceException e) {
                   Debug.logError(e, "Problem for Reconcilation of TransactionId:" + finAccountTransId, module);
                   return ServiceUtil.returnError(e.getMessage());
               }
             }  
            //Reconsilation starts here
            for(int i = 0; i < finAccountTransIdsList.size(); i++){
              	 String finAccountTransId = (String) finAccountTransIdsList.get(i);
              	 finAccountTransMap.put("finAccountTransId", finAccountTransId);
                  try {
                     	Map reconcileFinAccountTransRes=dispatcher.runSync("reconcileFinAccountTrans",  UtilMisc.toMap("finAccountTransId",finAccountTransId,"organizationPartyId",organizationPartyId,"userLogin",userLogin));
                         if (ServiceUtil.isError(reconcileFinAccountTransRes)){
               		  		String errMsg =  ServiceUtil.getErrorMessage(reconcileFinAccountTransRes);
               		  		Debug.logError(errMsg , module);
               		  	    return ServiceUtil.returnError("Problem for Reconcilation of TransactionId:" + finAccountTransId);    
               		  	}
                     } catch (GenericServiceException e) {
                         Debug.logError(e, "Problem for Reconcilation of TransactionId:" + finAccountTransId, module);
                         return ServiceUtil.returnError(e.getMessage());
                     }
                    
                }  
		    }catch (GenericEntityException e) {
				Debug.logError(e, module);
		        return ServiceUtil.returnError(e.getMessage());
			} 
		    catch (GenericServiceException e) {
		        Debug.logError(e, "Reconcilation Having Problem ", module);
		        return ServiceUtil.returnError(e.getMessage());
		    }
        return ServiceUtil.returnSuccess("Reconcilation Created Successfully for Selected FinAccountTransactions");
    }
    public static Map<String, Object> getFinAccountIdsListForPayment(DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = FastMap.newInstance();
		List<GenericValue> facilityPartyList = null;
		Delegator delegator = dctx.getDelegator();
		List finAccountList = FastList.newInstance();
		String paymentId = (String) context.get("paymentId");
		String finAccountId = null;
		String paymentMethodId = null;
		boolean flag;
		if(UtilValidate.isNotEmpty(paymentId)){
			try{
				GenericValue paymentDetails = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
				if(UtilValidate.isNotEmpty(paymentDetails)){
					paymentMethodId = (String) paymentDetails.get("paymentMethodId");
					if(UtilValidate.isEmpty(paymentMethodId)){
						List condList = FastList.newInstance();
						condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,"Company"));
						condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
						condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
				    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 
						finAccountList = delegator.findList("FinAccount", cond, UtilMisc.toSet("finAccountId","finAccountName"), null, null, false);
						if(UtilValidate.isNotEmpty(finAccountList)){
							flag = true;
							result.put("flag", flag);
							result.put("finAcountIdList", finAccountList);
						}
					}else{
						if("PAYMENTMETHOD4".equals(paymentMethodId) || "PAYMENTMETHOD6".equals(paymentMethodId)){
							List condList = FastList.newInstance();
							condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,"Company"));
							condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
							condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
					    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 
							finAccountList = delegator.findList("FinAccount", cond, UtilMisc.toSet("finAccountId","finAccountName"), null, null, false);
							if(UtilValidate.isNotEmpty(finAccountList)){
								flag = true;
								result.put("flag", flag);
								result.put("finAcountIdList", finAccountList);
							}
						}else{
							flag = false;
							result.put("flag", flag);
							return result;
						}
					}
				}
			}catch (GenericEntityException e) {
				Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
			} 
		}
		return result;
	}
}
