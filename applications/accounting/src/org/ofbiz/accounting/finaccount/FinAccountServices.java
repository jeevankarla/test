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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map.Entry;

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
    
    public static Map<String, Object> refundDepositContraFinAccTrans(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        BigDecimal amount = (BigDecimal) context.get("amount");
        amount = amount.abs();
        context.put("amount", amount);
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String transDateStr = (String) context.get("transactionDate");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
        Timestamp transactionDate = null;
		if (UtilValidate.isNotEmpty(transDateStr)) {
			try {
				transactionDate = new java.sql.Timestamp(sdf.parse(transDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + transDateStr,	module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + transDateStr,	module);
			}
		} 
		else {
			transactionDate = UtilDateTime.nowTimestamp();
		}
		String finAccountId = (String) context.get("finAccountId");
		context.put("transactionDate", transactionDate);
        try {
        	GenericValue finAccount = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
        	
        	if(UtilValidate.isEmpty(finAccount)){
        		return ServiceUtil.returnError("No Account with Id [" + finAccountId + "]");
        	}
        	BigDecimal acctAmt = BigDecimal.ZERO;
        	if(UtilValidate.isNotEmpty(finAccount.get("actualBalance"))){
        		acctAmt = finAccount.getBigDecimal("actualBalance");
        		acctAmt = acctAmt.abs();
        	}
        	if(amount.compareTo(acctAmt)>0){
        		return ServiceUtil.returnError("Cannot Refund more than balance amount");
        	}
        	 Map<String, Object> createResult = dispatcher.runSync("preCreateFinAccountTrans", context);
             if (ServiceUtil.isError(createResult)) {
                 return createResult;
             }
             

        } catch (Exception ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }
        result = ServiceUtil.returnSuccess("Successfully refunded the deposit of amount : "+(BigDecimal)context.get("amount"));
        return result;
    }
    
    public static Map<String, Object> createDepositFinAccountAndDeposit(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String depositPartyId = (String) context.get("depositPartyId");
        String entryType = (String) context.get("entryType");
        String finAccountIdTo = (String) context.get("finAccountIdTo");
        String contraRefNum = (String) context.get("contraRefNum");
        String comments = (String) context.get("comments");
        Timestamp transactionDate = (Timestamp) context.get("transactionDate");
        BigDecimal amount = (BigDecimal) context.get("amount");
        amount = amount.abs();
        String parentTypeId = (String) context.get("parentTypeId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Timestamp fromDate = null;
        if(UtilValidate.isEmpty(transactionDate)){
        	transactionDate = UtilDateTime.nowTimestamp();
        }
        fromDate = transactionDate;
        context.put("fromDate", fromDate);
        String finAccountTransTypeId = "DEPOSIT";
        try {
            // get the product store id and use it to generate a unique fin account code
        	 Map<String, Object> createResult = dispatcher.runSync("createFinAccount", context);
        	 
             if (ServiceUtil.isError(createResult)) {
                 return createResult;
             }
             String roleTypeId = "DEPOSITEE";
             String parentType = "DISBURSEMENT";
             if(UtilValidate.isNotEmpty(parentTypeId) && parentTypeId.equals("DEPOSIT_RECEIPT")){
            	 roleTypeId = "DEPOSITOR";
            	 parentType = "RECEIPT";
             }
             
             String finAccountId = (String)createResult.get("finAccountId");
             Map<String, Object> inputCtx = FastMap.newInstance();
             inputCtx.put("finAccountId", finAccountId);
             inputCtx.put("partyId", depositPartyId);
             inputCtx.put("fromDate", fromDate);
             inputCtx.put("roleTypeId", roleTypeId);
             inputCtx.put("userLogin", userLogin);
             createResult = dispatcher.runSync("createFinAccountRole", inputCtx);

             if (ServiceUtil.isError(createResult)) {
                 return createResult;
             }
             
             Map<String, Object> transCtxMap = FastMap.newInstance();
             transCtxMap.put("statusId", "FINACT_TRNS_CREATED");
             transCtxMap.put("entryType", entryType);
             transCtxMap.put("transactionDate", transactionDate);
             transCtxMap.put("comments", comments);
             transCtxMap.put("amount", amount);
             transCtxMap.put("userLogin", userLogin);
             transCtxMap.put("contraFinAccountId", finAccountId);
             transCtxMap.put("finAccountId", finAccountIdTo);
             transCtxMap.put("contraRefNum", contraRefNum);
             /*transCtxMap.put("parentTypeId", parentType);*/
             transCtxMap.put("finAccountTransTypeId", finAccountTransTypeId);
             createResult = dispatcher.runSync("preCreateFinAccountTrans", transCtxMap);

             if (ServiceUtil.isError(createResult)) {
                 return createResult;
             }
             

        } catch (Exception ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }
        result = ServiceUtil.returnSuccess("Successfully created account and deposit for party : "+depositPartyId);
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
    
    public static Map<String, Object> populateFinAccountTransSequence(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String finAccountId = (String) context.get("finAccountId");
        String finAccountTransTypeId = (String) context.get("finAccountTransTypeId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Timestamp nowTime = UtilDateTime.nowTimestamp();
        GenericValue finAccount;
        try {
            finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if(UtilValidate.isEmpty(fromDate)){
        	fromDate = UtilDateTime.getDayStart(nowTime);
        }
        if(UtilValidate.isEmpty(thruDate)){
        	thruDate = UtilDateTime.getDayEnd(nowTime);
        }
        if(UtilValidate.isNotEmpty(finAccountId) && UtilValidate.isEmpty(finAccount)){
        	Debug.logError("Financial Account doesn't exists with Id: "+finAccountId, module);
        	return ServiceUtil.returnError("Financial Account doesn't exists with Id: "+finAccountId);
        }
        List finAccountIdsList = FastList.newInstance();
        List condList = FastList.newInstance();
        if(UtilValidate.isEmpty(finAccountId)){
        	try{
        		condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
        		condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, UtilMisc.toList("BANK_ACCOUNT", "CASH")));
        		EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
        		List<GenericValue> finAccounts = delegator.findList("FinAccount", cond, UtilMisc.toSet("finAccountId"), null, null, false);
        		finAccountIdsList = EntityUtil.getFieldListFromEntityList(finAccounts, "finAccountId", true);
        	}catch(GenericEntityException e){
        		Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
        	}
        }
        List<String> finAccountTransIdsList = FastList.newInstance();
        try{

        	condList.clear();
        	if(UtilValidate.isNotEmpty(finAccountTransTypeId)){
        		condList.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, finAccountTransTypeId));
            }
            if(UtilValidate.isNotEmpty(finAccountId)){
            	condList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
            }
            else{
            	condList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.IN, finAccountIdsList));
            }
            condList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
            condList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, thruDate));
            EntityCondition seqCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
            List<GenericValue> finAccountTransSeqList = delegator.findList("FinAccntTransSequence", seqCond, UtilMisc.toSet("finAccountTransId"), null, null, false);
            finAccountTransIdsList = EntityUtil.getFieldListFromEntityList(finAccountTransSeqList, "finAccountTransId", true);
    	}catch(GenericEntityException e){
    		Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
    	}
        
        List<EntityExpr> exprs = FastList.newInstance();
        if(UtilValidate.isNotEmpty(finAccountTransTypeId)){
        	exprs.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, finAccountTransTypeId));
        }
        if(UtilValidate.isNotEmpty(finAccountId)){
        	exprs.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
        }
        else{
        	exprs.add(EntityCondition.makeCondition("finAccountId", EntityOperator.IN, finAccountIdsList));
        }
        if(UtilValidate.isNotEmpty(finAccountTransIdsList)){
        	exprs.add(EntityCondition.makeCondition("finAccountTransId", EntityOperator.NOT_IN, finAccountTransIdsList));
        }
        exprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
        exprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, thruDate));
        EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
        EntityListIterator eli = null;
        try {
        	eli = delegator.find("FinAccountTrans", condition, null, null, UtilMisc.toList("transactionDate"), null);
            GenericValue trans;
            while ((trans = eli.next()) != null) {
            	GenericValue newTransSeq = delegator.makeValue("FinAccntTransSequence");        	 
            	newTransSeq.set("finAccountId", trans.getString("finAccountId"));
            	newTransSeq.set("finAccountTransTypeId", trans.getString("finAccountTransTypeId"));
            	newTransSeq.set("transactionDate", trans.getTimestamp("transactionDate"));
            	newTransSeq.set("finAccountTransId", trans.getString("finAccountTransId"));
            	delegator.setNextSubSeqId(newTransSeq, "transSequenceId", 10, 1);
            	delegator.createOrStore(newTransSeq);
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

        return result;
    }
    
    public static Map<String, Object> updateFinAccountBalancesFromTransId(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String finAccountTransId = (String) context.get("finAccountTransId");
        String oldStatusId = (String) context.get("oldStatusId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue finAccountTrans = null;
        GenericValue finAccount = null;
        try{
        	finAccountTrans = delegator.findOne("FinAccountTrans", UtilMisc.toMap("finAccountTransId", finAccountTransId), false);
        	
    	}catch(GenericEntityException e){
    		Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
    	}
        if(UtilValidate.isEmpty(finAccountTrans)){
        	Debug.logError("No FinAccountTrans exists with id:"+finAccountTransId , module);
		  	return ServiceUtil.returnError("No FinAccountTrans exists with id:"+finAccountTransId);
        }
        String status = finAccountTrans.getString("statusId");
        String finAccountTransTypeId = finAccountTrans.getString("finAccountTransTypeId");
        String finAccountId = finAccountTrans.getString("finAccountId");
        
        try{
        	finAccount = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
        	
    	}catch(GenericEntityException e){
    		Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
    	}
        BigDecimal balanceUpdateAmount = BigDecimal.ZERO;
        BigDecimal actualBalance = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(finAccount.getBigDecimal("actualBalance"))){
        	actualBalance = finAccount.getBigDecimal("actualBalance");
        }
        BigDecimal availableBalance = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(finAccount.getBigDecimal("availableBalance"))){
        	availableBalance = finAccount.getBigDecimal("availableBalance");
        }
        
        BigDecimal amountForCalc = finAccountTrans.getBigDecimal("amount");
        if(finAccountTransTypeId.equals("WITHDRAWAL")){
        	amountForCalc = amountForCalc.negate();
        }
        try{
        	if(UtilValidate.isEmpty(oldStatusId)){
        		if(status.equals("FINACT_TRNS_CREATED")){
                	balanceUpdateAmount = actualBalance.add(amountForCalc);
                	finAccount.set("actualBalance", balanceUpdateAmount);
                	finAccount.store();
                }
        	}
        	else if(status.equals("FINACT_TRNS_APPROVED") && oldStatusId.equals("FINACT_TRNS_CREATED")){
                	balanceUpdateAmount = availableBalance.add(amountForCalc);
                	finAccount.set("availableBalance", balanceUpdateAmount);
                	finAccount.store();
        	}
        	else if(status.equals("FINACT_TRNS_CANCELED") && oldStatusId.equals("FINACT_TRNS_CREATED")){
        		balanceUpdateAmount = actualBalance.subtract(amountForCalc);
            	finAccount.set("actualBalance", balanceUpdateAmount);
            	finAccount.store();
        	}
        }catch(GenericEntityException e){
    		Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
    	}
        return result;
    }
    
    public static Map<String, Object> populateFinAccountBalancesFromTrans(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String finAccountId = (String) context.get("finAccountId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List conditionList = FastList.newInstance();
        EntityListIterator eli = null;        
        try{
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "FINACT_TRNS_CANCELED"));
        	if(UtilValidate.isNotEmpty(finAccountId)){
        		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
        	}
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	
            eli = delegator.find("FinAccountTrans", condition, null, null, UtilMisc.toList("finAccountId"), null);
            
            GenericValue trans;
            String finAccId = "";
            String finAccTransType = "";
            String statusId = "";
            Map finAccountBalances = FastMap.newInstance();
            while ((trans = eli.next()) != null) {
                finAccId = trans.getString("finAccountId");
                finAccTransType = trans.getString("finAccountTransTypeId");
                BigDecimal amount = trans.getBigDecimal("amount");
                BigDecimal actualAmt = BigDecimal.ZERO;
                BigDecimal availAmt = BigDecimal.ZERO;
                statusId = trans.getString("statusId");
                if(finAccTransType.equals("WITHDRAWAL")){
                	amount = amount.negate();
                }
                if(statusId.equals("FINACT_TRNS_CREATED")){
                	actualAmt = amount;
                }
                if(statusId.equals("FINACT_TRNS_APPROVED")){
                	availAmt = amount;
                	actualAmt = amount;
                }
                if(UtilValidate.isNotEmpty(finAccountBalances.get(finAccId))){
                	Map extFinMap = (Map) finAccountBalances.get(finAccId);
                	BigDecimal extActAmt = (BigDecimal)extFinMap.get("actualBalance");
                	BigDecimal extAvlAmt = (BigDecimal)extFinMap.get("availableBalance");
                	extFinMap.put("actualBalance", extActAmt.add(actualAmt));
                	extFinMap.put("availableBalance", extAvlAmt.add(availAmt));
                	finAccountBalances.put(finAccId, extFinMap);
                }
                else{
                	Map tempMap = FastMap.newInstance();
                	tempMap.put("actualBalance", actualAmt);
                	tempMap.put("availableBalance", availAmt);
                	finAccountBalances.put(finAccId, tempMap);
                }
                
            }
            Iterator tempIter = finAccountBalances.entrySet().iterator();
			String accId = "";
			while (tempIter.hasNext()) {
				Map.Entry tempEntry = (Entry) tempIter.next();
				accId = (String) tempEntry.getKey();
				Map balances = (Map) tempEntry.getValue();
				BigDecimal actBalance = (BigDecimal)balances.get("actualBalance");
				BigDecimal avlBalance = (BigDecimal)balances.get("availableBalance");
				GenericValue finAcctValue = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", accId), false);
				finAcctValue.set("actualBalance",actBalance);
				finAcctValue.set("availableBalance",avlBalance);
				finAcctValue.store();
			}
        	
    	}catch(GenericEntityException e){
    		Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
    	}finally {
            if (eli != null) {
                try {
                    eli.close();
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
        }

        return result;
    }
    
    public static Map<String, Object> createFinAcctTransSequence(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String finAccountTransId = (String) context.get("finAccountTransId");
        Map<String, Object> result = ServiceUtil.returnSuccess();
    	GenericValue finAccountTrans = null;
    	boolean createSequence = Boolean.FALSE;
        try{
        	finAccountTrans = delegator.findOne("FinAccountTrans", UtilMisc.toMap("finAccountTransId", finAccountTransId), false);
        	
    	}catch(GenericEntityException e){
    		Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
    	}
        
        try {
			GenericValue tenantConfigEnableTransSeq = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName","enableFinTransSequence"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableTransSeq) && (tenantConfigEnableTransSeq.getString("propertyValue")).equals("Y")) {
				createSequence = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
        
        if(UtilValidate.isNotEmpty(finAccountTrans) && createSequence){
        	try {
        		GenericValue newTransSeq = delegator.makeValue("FinAccntTransSequence");        	 
            	newTransSeq.set("finAccountId", finAccountTrans.getString("finAccountId"));
            	newTransSeq.set("finAccountTransTypeId", finAccountTrans.getString("finAccountTransTypeId"));
            	newTransSeq.set("transactionDate", finAccountTrans.getTimestamp("transactionDate"));
            	newTransSeq.set("finAccountTransId", finAccountTrans.getString("finAccountTransId"));
            	delegator.setNextSubSeqId(newTransSeq, "transSequenceId", 10, 1);
            	newTransSeq.create();
             } catch (GeneralException e) {
            	 Debug.logError(e, module);
                 return ServiceUtil.returnError(e.getMessage());
             }
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
						GenericValue paymentMethodDetails = delegator.findOne("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId), false);
						if(UtilValidate.isNotEmpty(paymentMethodDetails)){
							finAccountId = (String) paymentMethodDetails.get("finAccountId");
							if(UtilValidate.isEmpty(finAccountId)){
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
				}
			}catch (GenericEntityException e) {
				Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
			} 
		}
		return result;
	}
       
}
