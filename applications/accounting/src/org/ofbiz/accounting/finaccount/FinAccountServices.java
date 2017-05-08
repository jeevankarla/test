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

import org.ofbiz.accounting.util.UtilAccounting;
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

import java.util.Locale;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilHttp;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.ofbiz.entity.transaction.TransactionUtil;


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
        String inFavor = (String) context.get("inFavor");
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
        	GenericValue finAccount = delegator.findOne("FinAccountAndType", UtilMisc.toMap("finAccountId", finAccountId), false);
        	
        	if(UtilValidate.isEmpty(finAccount)){
        		return ServiceUtil.returnError("No Account with Id [" + finAccountId + "]");
        	}
        	String parentTypeId = finAccount.getString("parentTypeId");
        	
        	if(UtilValidate.isNotEmpty(parentTypeId) && parentTypeId.equals("DEPOSIT_RECEIPT")){
        			context.put("finAccountTransTypeId", "DEPOSIT");
        	}
        	if(UtilValidate.isNotEmpty(parentTypeId) && parentTypeId.equals("DEPOSIT_PAID")){
    			context.put("finAccountTransTypeId", "WITHDRAWAL");
        	}
        	if(UtilValidate.isNotEmpty(parentTypeId) && parentTypeId.equals("EMPLOYEE_ADV")){
    			context.put("finAccountTransTypeId", "WITHDRAWAL");
        	}
        	
        	BigDecimal acctAmt = BigDecimal.ZERO;
        	if(UtilValidate.isNotEmpty(finAccount.get("actualBalance"))){
        		acctAmt = finAccount.getBigDecimal("actualBalance");
        		acctAmt = acctAmt.abs();
        	}
        	if(amount.compareTo(acctAmt)>0){
        		return ServiceUtil.returnError("Cannot Refund more than balance amount");
        	}
        	context.remove("inFavor");
        	Map<String, Object> createResult = dispatcher.runSync("preCreateFinAccountTrans", context);
            if (ServiceUtil.isError(createResult)) {
            	return createResult;
            }
            String finAccountTransId = (String)createResult.get("finAccountTransId");
            if(UtilValidate.isNotEmpty(inFavor)){
            	
            	GenericValue newTransAttr = delegator.makeValue("FinAccountTransAttribute");        	 
            	newTransAttr.set("finAccountTransId", finAccountTransId);
            	newTransAttr.set("attrName", "INFAVOUR_OF");
            	newTransAttr.set("attrValue", inFavor);
            	delegator.createOrStore(newTransAttr);
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
        String depositPartyId = (String) context.get("ownerPartyId");
        String entryType = (String) context.get("entryType");
        String finAccountIdTo = (String) context.get("finAccountIdTo");
        String contraRefNum = (String) context.get("contraRefNum");
        String comments = (String) context.get("comments");
        Timestamp transactionDate = (Timestamp) context.get("transactionDate");
        BigDecimal amount = (BigDecimal) context.get("amount");
        String inFavor = (String) context.get("inFavor");
        amount = amount.abs();
        String parentTypeId = (String) context.get("acctParentTypeId");
        String finAccountTypeId=(String) context.get("finAccountTypeId");
        String finAccountParentId = (String)context.get("finAccountParentId");
        String costCenterId = (String)context.get("costCenterId");
        String segmentId = (String)context.get("segmentId");
        String custRequestId="";
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Timestamp fromDate = null;
        
        if(UtilValidate.isEmpty(transactionDate)){
        	transactionDate = UtilDateTime.nowTimestamp();
        }
        fromDate = transactionDate;
        context.put("fromDate", fromDate);
        String finAccountTransTypeId = "DEPOSIT";
        try {
           
        	
             	GenericValue newTransAttr = delegator.makeValue("CustRequest"); 
             	newTransAttr.set("accParentTypeId", parentTypeId);
             	newTransAttr.set("finAccountTypeId", finAccountTypeId);
             	newTransAttr.set("finAccountParentId", finAccountParentId);
             	newTransAttr.set("roleTypeId", "EMPLOYEE");
             	newTransAttr.set("entryTypeId", "Contra");
             	newTransAttr.set("finAccountTransTypeId", finAccountTransTypeId);
             	newTransAttr.set("currencyUomId", "INR");
             	newTransAttr.set("finstatusId", "CREATED");
             	newTransAttr.set("segmentId", segmentId);
             	newTransAttr.set("costCenterId", costCenterId);
             	newTransAttr.set("fromPartyId", depositPartyId);
             	newTransAttr.set("custRequestDate", transactionDate);
             	newTransAttr.set("amount", amount);
             	newTransAttr.set("referenceNumber", contraRefNum);
             	newTransAttr.set("reason", comments);
             	newTransAttr.set("createdByUserLogin", userLogin.get("userLoginId"));
             	delegator.createSetNextSeqId(newTransAttr); 
             	delegator.createOrStore(newTransAttr);
             	custRequestId = (String) newTransAttr.get("custRequestId");
             }
         catch (Exception ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }
        result = ServiceUtil.returnSuccess("Successfully created account for party : "+depositPartyId);
        result.put("custRequestId", custRequestId);
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
        	else if(status.equals("FINACT_TRNS_CREATED") && oldStatusId.equals("FINACT_TRNS_APPROVED")){
        		//Then we have to less that transaction amount.
            	balanceUpdateAmount = availableBalance.subtract(amountForCalc);
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
        Timestamp transactionDate = (Timestamp) context.get("transactionDate");
        Timestamp transactionDateStart = UtilDateTime.getDayStart(transactionDate);
        if(UtilValidate.isEmpty(transactionDate)){
        	transactionDateStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
        }
        Timestamp previousDay = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(transactionDateStart, -1));
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List conditionList = FastList.newInstance();
        EntityListIterator eli = null;        
        BigDecimal adjustmentAmount = BigDecimal.ZERO;
        try{
        	GenericValue finAccount = null;
        	if(UtilValidate.isNotEmpty(finAccountId)){
        		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
        	}
        	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, previousDay));
        	conditionList.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "ADJUSTMENT"));
        	EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List finAccountList = delegator.findList("FinAccountTrans", cond, null, null, null, false);
        	if(UtilValidate.isNotEmpty(finAccountList)){
        		finAccount = EntityUtil.getFirst(finAccountList);
        		adjustmentAmount = (BigDecimal) finAccount.get("amount");
        	}
        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, transactionDateStart));
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
				if(UtilValidate.isNotEmpty(adjustmentAmount) && adjustmentAmount.compareTo(BigDecimal.ZERO) != 0){
					actBalance = actBalance.add(adjustmentAmount);
					avlBalance = avlBalance.add(adjustmentAmount);
				}
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
        Timestamp  realisationDate=(Timestamp)context.get("realisationDate");
        String glBatchId="";
        
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
        glBatchId=glReconciliationId;
        Map finAccountTransMap = UtilMisc.toMap("statusId",statusId);
        	finAccountTransMap.put("realisationDate", realisationDate);
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
            
            //reconciled date creation
            
            for(int i = 0; i < finAccountTransIdsList.size(); i++){
              	 String finAccountTransId = (String) finAccountTransIdsList.get(i);
              	 finAccountTransMap.put("finAccountTransId", finAccountTransId);
              	try {
                  	Map assignDtRecToFinAccTransRes=dispatcher.runSync("updateFinAccountReconsilationDate",  UtilMisc.toMap("finAccountTransId",finAccountTransId,"realisationDate",realisationDate,"userLogin",userLogin));
                  	   if (ServiceUtil.isError(assignDtRecToFinAccTransRes)){
            		  		String errMsg =  ServiceUtil.getErrorMessage(assignDtRecToFinAccTransRes);
            		  		Debug.logError(errMsg , module);
            		  	    return ServiceUtil.returnError("Problem for Assigning of Reconciliation Date:" + finAccountTransId+" To ReconcilationId ");    
            		  	}
                  } catch (GenericServiceException e) {
                      Debug.logError(e, "Problem for Reconcilation of Reconciliation Date:" + finAccountTransId, module);
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
        return ServiceUtil.returnSuccess("Reconcilation Created Successfully for Selected FinAccountTransactions To BatchId "+glBatchId);
    }
    public static Map<String, Object> getFinAccountIdsListForPayment(DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = FastMap.newInstance();
		List<GenericValue> facilityPartyList = null;
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List finAccountList = FastList.newInstance();
		String paymentId = (String) context.get("paymentId");
		String finAccountId = null;
		String paymentMethodId = null;
		boolean flag;
		String ownerPartyId=null;
		List conditionList = FastList.newInstance();
		List<GenericValue> employment = null;
		try{
			conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
			conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
			employment = delegator.findList("Employment", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);
			if(UtilValidate.isNotEmpty(employment)){
				GenericValue empDetail = EntityUtil.getFirst(employment);
				if(empDetail.getString("partyIdFrom") != "Company" || empDetail.getString("partyIdFrom") != "HO"){
					ownerPartyId = empDetail.getString("partyIdFrom");
				}
			}
			else{
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "BRANCH_EMPLOYEE"));
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				employment = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);	
				if(UtilValidate.isNotEmpty(employment)){
					GenericValue empDetail = EntityUtil.getFirst(employment);
					if(empDetail.getString("partyIdFrom") != "Company" || empDetail.getString("partyIdFrom") != "HO"){
						ownerPartyId = empDetail.getString("partyIdFrom");
					}
				}
			}
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
		}
		
		if(UtilValidate.isNotEmpty(paymentId)){
			try{
				GenericValue paymentDetails = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
				if(UtilValidate.isNotEmpty(paymentDetails)){
					paymentMethodId = (String) paymentDetails.get("paymentMethodId");
					if(UtilValidate.isEmpty(paymentMethodId)){
						List condList = FastList.newInstance();
						if(UtilValidate.isNotEmpty(ownerPartyId)){
							condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,ownerPartyId));
						}
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
								if(UtilValidate.isNotEmpty(ownerPartyId)){
									condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,ownerPartyId));
								}
								condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
								condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
						    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 
						    	List<String> orderBy = UtilMisc.toList("finAccountName");
								finAccountList = delegator.findList("FinAccount", cond, UtilMisc.toSet("finAccountId","finAccountName"), orderBy, null, false);
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
     
    public static Map<String, Object> cancelFinancialAccountReconciliationNewVbiz(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        //List finAccountTransIdsList =  (List)context.get("finAccountTransIds");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String  glReconciliationId=(String)context.get("glReconciliationId");
        List finAccountTransIdsList =  FastList.newInstance();
        List<String> glfinAccntTransPayIdsList =  FastList.newInstance();
        Map glReconciliationMap= UtilMisc.toMap("statusId","GLREC_CREATED");
        glReconciliationMap.put("userLogin", userLogin);
        glReconciliationMap.put("glReconciliationId", glReconciliationId);
        try{
        	/*Map updateGlReconciliationRes=dispatcher.runSync("updateGlReconciliation",  glReconciliationMap);
            if (ServiceUtil.isError(updateGlReconciliationRes)){
  		  		String errMsg =  ServiceUtil.getErrorMessage(updateGlReconciliationRes);
  		  		Debug.logError(errMsg , module);
  		  	    return ServiceUtil.returnError("Problem for When Updating of glReconciliationId:" + glReconciliationId);    
  		  	}*/
            //before Calling cancelFinancialAccountReconciliation we have to Update FinAccntTrans status from FINACT_TRNS_APPROVED to FINACT_TRNS_CREATED
        	List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("glReconciliationId", EntityOperator.EQUALS ,glReconciliationId));
	    	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 
	    	List<GenericValue> finAccountTransList = delegator.findList("FinAccountTrans", cond, UtilMisc.toSet("finAccountTransId","paymentId"), null, null, false);
	    	finAccountTransIdsList = EntityUtil.getFieldListFromEntityList(finAccountTransList, "finAccountTransId", true);
	    	glfinAccntTransPayIdsList =EntityUtil.getFieldListFromEntityList(finAccountTransList, "paymentId", true);
	    	Map setMassFinAccountTransStatusMap= UtilMisc.toMap("statusId","FINACT_TRNS_CREATED");
	    	setMassFinAccountTransStatusMap.put("oldStatusId", "FINACT_TRNS_APPROVED");
	    	setMassFinAccountTransStatusMap.put("finAccountTransIds", finAccountTransIdsList);
	    	setMassFinAccountTransStatusMap.put("userLogin", userLogin);
	    	Map setMassFinAccountTransStatusRes=dispatcher.runSync("setMassFinAccountTransStatus",  setMassFinAccountTransStatusMap);
            if (ServiceUtil.isError(setMassFinAccountTransStatusRes)){
  		  		String errMsg =  ServiceUtil.getErrorMessage(setMassFinAccountTransStatusRes);
  		  		Debug.logError(errMsg , module);
  		  	    return ServiceUtil.returnError("Problem  When Changing Status For finAccountTransIds Of glReconciliationId:" + glReconciliationId);    
  		  	}
            //Change Payment status  PMNT_CONFIRMED to PMNT_SENT/PMNT_RECEIVED
            for (String paymentId :glfinAccntTransPayIdsList) {  
            	GenericValue payment = delegator.findOne("Payment",UtilMisc.toMap("paymentId",paymentId) , false);
            	Map<String, Object> setPaymentStatusMap = UtilMisc.<String, Object>toMap("userLogin", userLogin);
	        	setPaymentStatusMap.put("paymentId", paymentId);
		       	if(UtilValidate.isNotEmpty(payment)){
		       		if(UtilAccounting.isReceipt(payment)){
		       			setPaymentStatusMap.put("statusId", "PMNT_RECEIVED");
		       		}
		       		if(UtilAccounting.isDisbursement(payment)){
		       			setPaymentStatusMap.put("statusId", "PMNT_SENT");
		       		}
		       	}
	            Map<String, Object> pmntResults = dispatcher.runSync("setPaymentStatus", setPaymentStatusMap);
	            if (ServiceUtil.isError(pmntResults)) {
 	            	Debug.logError(pmntResults.toString(), module);
 	            	return ServiceUtil.returnError(null, null, null, pmntResults);
 	            }
            }
            //cancel FinAccnt Reconciliation
            Map glReconciliationCancelMap= UtilMisc.toMap("userLogin",userLogin);
            glReconciliationCancelMap.put("glReconciliationId", glReconciliationId);
            Map cancelFinAccountReconciliationRes=dispatcher.runSync("cancelFinancialAccountReconciliation",  glReconciliationCancelMap);
            if (ServiceUtil.isError(cancelFinAccountReconciliationRes)){
  		  		String errMsg =  ServiceUtil.getErrorMessage(cancelFinAccountReconciliationRes);
  		  		Debug.logError(errMsg , module);
  		  	    return ServiceUtil.returnError("Problem  When Removing GlReconciliationId From glReconciliationId:" + glReconciliationId);    
  		  	}
            ///*Map updateGlReconciliationRes=dispatcher.runSync("updateGlReconciliation",  glReconciliationMap);
            glReconciliationMap.put("statusId", "GLREC_CANCELLED");
            Map updateGlReconciliationRes=dispatcher.runSync("updateGlReconciliation",  glReconciliationMap);
            if (ServiceUtil.isError(updateGlReconciliationRes)){
  		  		String errMsg =  ServiceUtil.getErrorMessage(updateGlReconciliationRes);
  		  		Debug.logError(errMsg , module);
  		  	    return ServiceUtil.returnError("Problem When Change Status To Canceled for glReconciliationId:" + glReconciliationId);    
  		  	}
            
        }catch (GenericEntityException e) {
			Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
		}catch (GenericServiceException e) {
	        Debug.logError(e, "Reconcilation Cancelation Having Problem ", module);
	        return ServiceUtil.returnError(e.getMessage());
	    }
        return ServiceUtil.returnSuccess("Reconcilation Canceld Successfully for glReconciliationId:"+glReconciliationId);
    }
    public static Map<String, Object> getFinAccountTransOpeningBalances(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String finAccountId = (String) context.get("finAccountId");
        Timestamp transactionDate = (Timestamp) context.get("transactionDate");
        String costCenterId =(String) context.get("costCenterId");
        String segmentId =(String) context.get("segmentId");
        List<String> roBranchList = (List) context.get("roBranchList");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(transactionDate, -1));
        List conditionList = FastList.newInstance();
        List<GenericValue> finAccountTransList=null;
        BigDecimal withDrawal = BigDecimal.ZERO;
        BigDecimal deposit = BigDecimal.ZERO;
        BigDecimal openingBalance = BigDecimal.ZERO;
        BigDecimal adjustmentAmount = BigDecimal.ZERO;
        try{
        	conditionList.add(EntityCondition.makeCondition("finAccountId",EntityOperator.EQUALS,finAccountId));
        	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,previousDayEnd));
        	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"FINACT_TRNS_CANCELED"));
        	if(UtilValidate.isNotEmpty(costCenterId)){
        		conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.EQUALS,costCenterId));
    		}
        	if(UtilValidate.isNotEmpty(roBranchList)){
        		conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,roBranchList));
    		}
        	if(UtilValidate.isNotEmpty(segmentId)){
    			if(segmentId.equals("YARN_SALE")){
    				conditionList.add(EntityCondition.makeCondition("segmentId",EntityOperator.IN,UtilMisc.toList("YARN_SALE","DEPOT_YARN_SALE")));
    			}
    			else{
    				conditionList.add(EntityCondition.makeCondition("segmentId",EntityOperator.EQUALS,segmentId));
    			}
    		}
        	
        	
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	finAccountTransList = delegator.findList("FinAccountTrans",condition,null,null,null,false);
        	if(UtilValidate.isNotEmpty(finAccountTransList)){
        		for(GenericValue finAccountTrans:finAccountTransList){
        			if(((String)finAccountTrans.get("finAccountTransTypeId")).equals("WITHDRAWAL") && UtilValidate.isNotEmpty((BigDecimal)finAccountTrans.get("amount"))){
        				withDrawal=withDrawal.add((BigDecimal)finAccountTrans.get("amount"));
        			}
        			if(((String)finAccountTrans.get("finAccountTransTypeId")).equals("DEPOSIT") && UtilValidate.isNotEmpty((BigDecimal)finAccountTrans.get("amount"))){
        				deposit=deposit.add((BigDecimal)finAccountTrans.get("amount"));
        			}
        			if(((String)finAccountTrans.get("finAccountTransTypeId")).equals("ADJUSTMENT") && UtilValidate.isNotEmpty((BigDecimal)finAccountTrans.get("amount"))){
        				adjustmentAmount=adjustmentAmount.add((BigDecimal)finAccountTrans.get("amount"));
        			}
        		}
        		openingBalance=(adjustmentAmount.add(deposit)).subtract(withDrawal);
        	}
        }catch (Exception e) {
	        Debug.logError(e, "Error While getting the Opening balace.!", module);
	        return ServiceUtil.returnError(e.getMessage());
	    }
        result.put("withDrawal", withDrawal);
		result.put("deposit", deposit);
		result.put("adjustmentAmount", adjustmentAmount);
		result.put("openingBalance", openingBalance);
        return result;
    }
    
public static String makeCPFFinAccTrans(HttpServletRequest request, HttpServletResponse response) {

	  Delegator delegator = (Delegator) request.getAttribute("delegator");
  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  	  Locale locale = UtilHttp.getLocale(request);
  	  Map<String, Object> result = ServiceUtil.returnSuccess();
  	  HttpSession session = request.getSession();
  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  	  BigDecimal totalAmount = BigDecimal.ZERO;
  	  List paymentIds = FastList.newInstance();
  	
  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  	  if (rowCount < 1) {
  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
		  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
  		  return "error";
  	  }
  	  String paymentMethodId = "";
  	  String paymentType = "";
  	  String finAccountId = "";
  	  String instrumentDateStr = "";
  	  String paymentDateStr = "";
  	  String paymentRefNum = "";
  	  String partyId = "";
  	  String partyIdFrom = "";
  	  String paymentGroupId = "";
  	  String paymentGroupTypeId="";
  	  String depositAmtStr="";
  	  finAccountId = (String) paramMap.get("finAccountId");
  	  instrumentDateStr = (String) paramMap.get("instrumentDate");
  	  paymentDateStr = (String) paramMap.get("paymentDate");
  	  paymentRefNum = (String) paramMap.get("paymentRefNum");
  	  paymentGroupTypeId = (String) paramMap.get("paymentGroupTypeId");
  	  depositAmtStr = (String) paramMap.get("depositAmt");
  	  Timestamp instrumentDate=UtilDateTime.nowTimestamp();
      if (UtilValidate.isNotEmpty(instrumentDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
			}
	   } 
       Timestamp paymentDate=UtilDateTime.nowTimestamp();
       if (UtilValidate.isNotEmpty(paymentDateStr)) {
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				paymentDate = new java.sql.Timestamp(sdf.parse(paymentDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ paymentDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "	+ paymentDateStr, module);
			}
	   }
        BigDecimal totInvoiceAMount = BigDecimal.ZERO;
	  	Map invoiceAmountMap = FastMap.newInstance();
	  	List invoicesList = FastList.newInstance();
	  	boolean beganTransaction = false;
        Map finaccountAmountMap = FastMap.newInstance();
        Map finTransCreationMap = FastMap.newInstance();
        List<String>finAccountTransIds = FastList.newInstance();

	  	//EntityListIterator finIdsList = null;
		for (int i = 0; i < rowCount; i++){
		  	  Map paymentMap = FastMap.newInstance();
	  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	  		  BigDecimal amount = BigDecimal.ZERO;
	  		  String amountStr = "";
	  		  BigDecimal employeeFinAmount=BigDecimal.ZERO;
	  		  BigDecimal employeerFinAmount=BigDecimal.ZERO;
	  		  BigDecimal vpfFinAmount=BigDecimal.ZERO;
	  		  if (paramMap.containsKey("empCon"+ thisSuffix)) {
	  			employeeFinAmount = new BigDecimal((String)paramMap.get("empCon"+thisSuffix));
	  		  }
	  		  if (paramMap.containsKey("emprCon"+ thisSuffix)) {
	  			employeerFinAmount = new BigDecimal((String) paramMap.get("emprCon"+thisSuffix));
	  		  }

	  		  if (paramMap.containsKey("vpfCon"+ thisSuffix)) {
	  			vpfFinAmount = new BigDecimal((String)paramMap.get("vpfCon"+thisSuffix));
	  		  }
	  		  if (paramMap.containsKey("partyId"+ thisSuffix)) {
	  			  partyId = (String) paramMap.get("partyId"+thisSuffix);
	  		  }
	  		 
	  		 try {
	  		
	  			 List conditionList = FastList.newInstance();
	  			if(UtilValidate.isNotEmpty(partyId)){
	  	  		  conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,partyId));
	  	  		  }
	  			conditionList.add(EntityCondition.makeCondition("finAccountTypeId",EntityOperator.IN,UtilMisc.toList("EMP_CONTRI","EMPR_CONTRI","VPF_CONTRI")));
	  			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
		  		  List<GenericValue> finAccList = delegator.findList("FinAccount", condition, null, null, null, false);
	  			 
		  		String employeefinAccountId =  null;
		  		List<GenericValue> employeefinAccountList = EntityUtil.filterByCondition(finAccList, EntityCondition.makeCondition("finAccountTypeId",EntityOperator.EQUALS,"EMP_CONTRI"));
	  			employeefinAccountId = EntityUtil.getFirst(employeefinAccountList).getString("finAccountId");
	  			
	  			String employerfinAccountId =  null;
	  			List<GenericValue> employerconditionList = EntityUtil.filterByCondition(finAccList, EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "EMPR_CONTRI"));
	  			employerfinAccountId = EntityUtil.getFirst(employerconditionList).getString("finAccountId");
	  			
	  			String volunteerfinAccountId =  null;
	  			List<GenericValue> volunteerfinAccountList = EntityUtil.filterByCondition(finAccList, EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "VPF_CONTRI"));
	  			volunteerfinAccountId = EntityUtil.getFirst(volunteerfinAccountList).getString("finAccountId");
	  			
	  	  		Map employeefinMap = FastMap.newInstance();
		  	  		employeefinMap.put("partyId",partyId);
		  	  	if(UtilValidate.isNotEmpty(employerfinAccountId)){   
		  	  		employeefinMap.put("finAccountId",employeefinAccountId);
		  	  	}  
		  	  		employeefinMap.put("transactionDate",paymentDate);
		  	  		employeefinMap.put("finAccountTransTypeId","WITHDRAWAL");
		  	  		employeefinMap.put("statusId","FINACT_TRNS_CREATED");
//		  	  		employeefinMap.put("entryType","Adjustment");
			  		//paymentInputMap.put("contraRefNum", contraRefNum);
		  	  		employeefinMap.put("userLogin", userLogin);
		  	  		employeefinMap.put("amount",employeeFinAmount);
	  	  		Map employeerfinMap = FastMap.newInstance();
		  	  		employeerfinMap.put("partyId",partyId);
		  	 if(UtilValidate.isNotEmpty(employerfinAccountId)){  		
		  	  		employeerfinMap.put("finAccountId",employerfinAccountId);
	  		 }
		  	  		employeerfinMap.put("transactionDate",paymentDate);
		  	  		employeerfinMap.put("statusId","FINACT_TRNS_CREATED");
		  	  		employeerfinMap.put("finAccountTransTypeId","WITHDRAWAL");
//		  	  		employeerfinMap.put("entryType","Adjustment");
		  	  		//employeerfinMap.put("contraRefNum", contraRefNum);
		  	  		employeerfinMap.put("userLogin", userLogin);
		  	  		employeerfinMap.put("amount",employeerFinAmount);
	  		
	  		
	  	  		Map vpffinMap = FastMap.newInstance();
		  	  		vpffinMap.put("partyId",partyId);
		  	  	if(UtilValidate.isNotEmpty(volunteerfinAccountId)){
		  	  		vpffinMap.put("finAccountId",volunteerfinAccountId);
		  	  	}
		  	  		vpffinMap.put("transactionDate",paymentDate);
		  	  		vpffinMap.put("finAccountTransTypeId","WITHDRAWAL");
		  	  		vpffinMap.put("statusId","FINACT_TRNS_CREATED");
//		  	  		vpffinMap.put("entryType","Adjustment");
		  	  		//vpffinMap.put("contraRefNum", contraRefNum);
		  	  		vpffinMap.put("userLogin", userLogin);
		  	  		vpffinMap.put("amount",vpfFinAmount);

		  	  	finTransCreationMap.put(employeefinAccountId,employeefinMap);
		  	  	finTransCreationMap.put(employerfinAccountId,employeerfinMap);
		  	  	finTransCreationMap.put(volunteerfinAccountId,vpffinMap);
	  		}catch (GenericEntityException e) {
	  	  		  try {
	  	  			  // only rollback the transaction if we started one...
	  	  			  TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  	  		  } catch (GenericEntityException e2) {
	  	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  	  		  }
	  	  		  Debug.logError("An entity engine error occurred while fetching data", module);
	  	  	  }
		}
	  			 
		BigDecimal depositAmt=BigDecimal.ZERO;
		 if(UtilValidate.isNotEmpty(depositAmtStr)){
		  try {
			  depositAmt = new BigDecimal(depositAmtStr);
		  } catch (Exception e) {
			  Debug.logError(e, "Problems parsing amount string: " + depositAmtStr, module);
			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + depositAmtStr);
			return "error";
		  }
	  }
		 
		Map depositFinTransMap=FastMap.newInstance();
			depositFinTransMap.put("partyId","Company");
			depositFinTransMap.put("finAccountId",finAccountId);
			depositFinTransMap.put("transactionDate",paymentDate);
	  		depositFinTransMap.put("finAccountTransTypeId","DEPOSIT");
	  		depositFinTransMap.put("statusId","FINACT_TRNS_CREATED");
//	  		depositFinTransMap.put("entryType","Adjustment");
	  		//vpffinMap.put("contraRefNum", contraRefNum);
	  		depositFinTransMap.put("userLogin", userLogin);
	  		depositFinTransMap.put("amount",depositAmt);
	  		finTransCreationMap.put(finAccountId,depositFinTransMap);
  	      try {
		  		if(UtilValidate.isNotEmpty(finTransCreationMap)){
		  			
		  			 Iterator tempIter = finTransCreationMap.entrySet().iterator();
		 			while (tempIter.hasNext()) {
		 				Entry tempEntry = (Entry) tempIter.next();
		 				String tempFinAccountId = (String) tempEntry.getKey();
		 				Map FinAccountTransMap = (Map) tempEntry.getValue();
			        	 Map<String, Object> createResult = dispatcher.runSync("createFinAccountTrans", FinAccountTransMap);
					       if (ServiceUtil.isError(createResult)) {
					       	   Debug.logError("Problems in service batchDepositContraFinAccTrans", module);
							   request.setAttribute("_ERROR_MESSAGE_", "Error in service batchDepositContraFinAccTrans");
							   return "error";
					        }
	                        String finAccountTransId = (String)createResult.get("finAccountTransId");
	                        finAccountTransIds.add(finAccountTransId);
		  				
		  			}
		  		}
		  		 totalAmount = totalAmount.add(depositAmt);
        	if(UtilValidate.isNotEmpty(finAccountTransIds) && finAccountTransIds.size() > 0 ){
		  		  Map serviceCtx = FastMap.newInstance();
		  		  serviceCtx.put("finAccountTransIds", finAccountTransIds);
		  		  serviceCtx.put("instrumentDate", instrumentDate);
		  		  serviceCtx.put("finAccntTransDate", paymentDate);
		  		  serviceCtx.put("fromDate", paymentDate);
		  		  serviceCtx.put("contraRefNum", paymentRefNum);
		  		  serviceCtx.put("issuingAuthority", finAccountId);
		  		  serviceCtx.put("amount", totalAmount);
		  		  serviceCtx.put("statusId", "FNACTTRNSGRP_CREATED");
		  		  serviceCtx.put("finAccountId", finAccountId);
		  		  serviceCtx.put("finAcntTrnsGrpTypeId", "FIN_ACNT_TRNS_BATCH");
		  		  serviceCtx.put("createdDate", UtilDateTime.nowTimestamp());
		  		  serviceCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
		  		  serviceCtx.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
		  		  serviceCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
		  		  serviceCtx.put("userLogin", userLogin);
	  			  Map resultCtx = dispatcher.runSync("createFinAccountTransGroupAndMember", serviceCtx);
		  		  if(ServiceUtil.isError(resultCtx)){
		    			Debug.logError("Error while creating fin account trans group: " + ServiceUtil.getErrorMessage(resultCtx), module);
		    			request.setAttribute("_ERROR_MESSAGE_", "Error while creating fin account trans group");
			  			TransactionUtil.rollback();
			  			return "error";
		  		  }
			  	  String finAccntTransGroupId = (String)resultCtx.get("finAccntTransGroupId");
		  	  }	
        } catch (Exception ex) {
	  		    return "error";
	     }
			 result = ServiceUtil.returnSuccess("Transaction Completed successfully...!");
			 request.setAttribute("_EVENT_MESSAGE_", "Transaction Completed successfully...!");
		     return "success"; 
	  	 }  

public static Map<String, Object> cancelContraTransactions(DispatchContext ctx, Map<String, ? extends Object> context){
  	Delegator delegator = ctx.getDelegator();
  	GenericValue userLogin = (GenericValue) context.get("userLogin");
  	LocalDispatcher dispatcher = ctx.getDispatcher();
  	String chequeReturns = (String)context.get("chequeBounce");
  	String finAccountTransId = (String)context.get("finAccountTransId");
  	String returnDateStr = (String)context.get("returnDate");
  	Map<String, Object> result =  FastMap.newInstance();
  	Map<String, Object> inMap = FastMap.newInstance();
  	Map<String, Object> createInvoiceResult = FastMap.newInstance();
	inMap.put("userLogin", userLogin);
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
  	  	Timestamp cancelDate = null;
  	  	boolean isNonRouteMrktingChqReturn = Boolean.FALSE;// always excluding if externally not set
		if(UtilValidate.isNotEmpty(returnDateStr)){
	  	  		try {
	  	  		cancelDate = new java.sql.Timestamp(sdf.parse(returnDateStr).getTime());
	  	  		} catch (ParseException e) {
	  	  			Debug.logError(e, "Cannot parse date string: " + returnDateStr, module);
	  	  		} catch (NullPointerException e) {
	  	  			Debug.logError(e, "Cannot parse date string: " + returnDateStr, module);
	  	  		}
	  	  	}
  	  	else{
  	  		cancelDate = UtilDateTime.nowTimestamp();
  	  	}
	try{
		GenericValue finAccountTrans = delegator.findOne("FinAccountTrans", UtilMisc.toMap("finAccountTransId", finAccountTransId), false);
		String finAccntTransStatus=(String)finAccountTrans.get("statusId");
		GenericValue finAccountTrnsAttr = delegator.findOne("FinAccountTransAttribute", UtilMisc.toMap("finAccountTransId", finAccountTransId,"attrName","FATR_CONTRA"), false);
		GenericValue finAccountTrnsAttb=null;
		String finAccntTransAttrbStatus=null;
		if(UtilValidate.isNotEmpty(finAccountTrnsAttr)){
			String finAccntAttrbTransId=(String)finAccountTrnsAttr.get("attrValue");
			finAccountTrnsAttb = delegator.findOne("FinAccountTrans", UtilMisc.toMap("finAccountTransId", finAccntAttrbTransId), false);
			finAccntTransAttrbStatus=(String)finAccountTrnsAttb.get("statusId");
		}
		if((UtilValidate.isNotEmpty(finAccntTransStatus)&&("FINACT_TRNS_CREATED".equals(finAccntTransStatus)))){
			
				Map finAccountTransMap = UtilMisc.toMap("statusId","FINACT_TRNS_CANCELED");
		            finAccountTransMap.put("userLogin", userLogin);
		           	finAccountTransMap.put("finAccountTransId", finAccountTransId);
		           	finAccountTransMap.put("cancelDate",cancelDate);
		           	finAccountTransMap.put("chequeReturns",chequeReturns);
		           	Map finAccountTransMapResult=dispatcher.runSync("setFinAccountTransStatus", finAccountTransMap);
	               if (ServiceUtil.isError(finAccountTransMapResult)){
	     		  		String errMsg =  ServiceUtil.getErrorMessage(finAccountTransMapResult);
	     		  		Debug.logError(errMsg , module);
	     		  	    return ServiceUtil.returnError("setFinAccountTransStatus  having Problem for" + finAccountTransId);    
	     		  	}		               
	              
	           	result = ServiceUtil.returnSuccess("FinAccountTrans Successfully cancelled");
	       		result.put("finAccountTransId",finAccountTransId);				
			
		}else{
    		return ServiceUtil.returnError("you can not  cancel the finAccountTransId which is in approved status");
		}
		
	}catch(Exception e){
		Debug.logError("Unable to cancel the finAccountTransId"+e, module);
		return ServiceUtil.returnError("Unable to cancel the finAccountTransId");
	}		
  	return result;
}

public static String createEmpAdvDisbursement(HttpServletRequest request, HttpServletResponse response) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");
  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  	  Locale locale = UtilHttp.getLocale(request);
  	  Map<String, Object> result = ServiceUtil.returnSuccess();
  	  HttpSession session = request.getSession();
  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  	  BigDecimal totalAmount = BigDecimal.ZERO;
  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  	  if (rowCount < 1) {
  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
		  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
  		  return "error";
  	  }
  	  String finAccountId = "";
  	  String instrumentDateStr = "";
  	  String contraRefNum = "";
  	  String inFavourOf = "";
  	  String custRequestId = "";
  	  String description = "";
  	  String partyId = "";
  	  
  	boolean beganTransaction = false;
  	List finAccountTransIds = FastList.newInstance();
  	Timestamp instrumentDate=UtilDateTime.nowTimestamp();
		
  	try{
  		for (int i = 0; i < rowCount; i++){
	  		  
	  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	  		  
	  		  BigDecimal amount = BigDecimal.ZERO;
	  		  String amountStr = "";
	  		  
	  		  if (paramMap.containsKey("custRequestId" + thisSuffix)) {
	  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
	  		  }
	  		  if (paramMap.containsKey("partyId" + thisSuffix)) {
	  			partyId = (String) paramMap.get("partyId"+thisSuffix);
	  		  }
	  		  if (paramMap.containsKey("amount" + thisSuffix)) {
	  			amountStr = (String) paramMap.get("amount"+thisSuffix);
	  		  }
	  		  if(UtilValidate.isNotEmpty(amountStr)){
				  try {
		  			  amount = new BigDecimal(amountStr);
		  		  } catch (Exception e) {
		  			  Debug.logError(e, "Problems parsing amount string: " + amountStr, module);
		  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + amountStr);
		  			  return "error";
		  		  }
	  		  }
		  	finAccountId = (String) paramMap.get("finAccountId");
		  	instrumentDateStr = (String) paramMap.get("instrumentDate");
		  	contraRefNum = (String) paramMap.get("contraRefNum");
		  	inFavourOf = (String) paramMap.get("inFavourOf");
		  	description = (String) paramMap.get("description");
	  	    
		  	GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
		  	custRequest.set("finstatusId", "DISBURSED");
		  	custRequest.store();
		  	
		  	String custfinAccountTypeId = "";
		  	String custfinAccountId = "";
		  	String custPartyId = "";
		  	String costCenterId = "";
		  	custfinAccountTypeId = custRequest.getString("finAccountTypeId");
		  	custPartyId = custRequest.getString("fromPartyId");
		  	costCenterId = custRequest.getString("costCenterId");
		  	List fincondList = FastList.newInstance();
		  	fincondList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, custPartyId));
		  	fincondList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, custfinAccountTypeId));
    		EntityCondition fincond = EntityCondition.makeCondition(fincondList, EntityOperator.AND);
		  	List<GenericValue> custfinAccounts = delegator.findList("FinAccount", fincond, UtilMisc.toSet("finAccountId"), null, null, false);
		  	if(UtilValidate.isEmpty(custfinAccounts)){
				GenericValue finAccount = delegator.makeValue("FinAccount");
				finAccount.set("ownerPartyId", custPartyId);
				finAccount.set("costCenterId", costCenterId);
				finAccount.set("fromDate", custRequest.getTimestamp("custRequestDate"));
				finAccount.set("currencyUomId", "INR");
				finAccount.set("finAccountTypeId", custfinAccountTypeId);
				finAccount.set("statusId", "FNACT_ACTIVE");
				finAccount.set("organizationPartyId", "Company");
	 			delegator.createSetNextSeqId(finAccount);
	 			if(UtilValidate.isNotEmpty(finAccount)){
	 				custfinAccountId=finAccount.getString("finAccountId");
	 			}	
			}else{
				GenericValue finAcc = EntityUtil.getFirst(custfinAccounts);
				if(UtilValidate.isNotEmpty(finAcc)){
					custfinAccountId=finAcc.getString("finAccountId");
	 			}	
			}
		  
		  	String partyfinAccountId = "";
		  	
		  	
	        if (UtilValidate.isNotEmpty(instrumentDateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				try {
					instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
					instrumentDate = UtilDateTime.getDayStart(instrumentDate);
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
				}
			}
	        List condList = FastList.newInstance();
	        if(UtilValidate.isNotEmpty(partyId)){
	        condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	        condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, custfinAccountTypeId));
    		EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
    		List<GenericValue> finAccounts = delegator.findList("FinAccount", cond, UtilMisc.toSet("finAccountId"), null, null, false);
	        if(UtilValidate.isNotEmpty(finAccounts)){
					GenericValue finAccountIds = EntityUtil.getFirst(finAccounts);
				 partyfinAccountId = (String)finAccountIds.get("finAccountId");
	        }
	        
	        
	        }
	        
					//creating  fin account transactions here
		             Map<String, Object> transCtxMap = FastMap.newInstance();
		             transCtxMap.put("statusId", "FINACT_TRNS_CREATED");
		             transCtxMap.put("entryType", "Contra");
		             transCtxMap.put("transactionDate", instrumentDate);
		             transCtxMap.put("amount", amount);
		             transCtxMap.put("comments", description);
		             transCtxMap.put("contraRefNum", contraRefNum);
		             transCtxMap.put("inFavourOf", inFavourOf);
		             if(UtilValidate.isNotEmpty(custfinAccountId)){
		             transCtxMap.put("contraFinAccountId", custfinAccountId);
		             }
		             if(UtilValidate.isNotEmpty(partyfinAccountId)){
		           	 transCtxMap.put("contraFinAccountId", partyfinAccountId);
		             }
		             transCtxMap.put("finAccountId", finAccountId); 
		           	 transCtxMap.put("finAccountTransTypeId", "WITHDRAWAL");
		           	 transCtxMap.put("partyId", partyId);
		             transCtxMap.put("userLogin", userLogin);
		             Map<String, Object> createResult = dispatcher.runSync("preCreateFinAccountTrans", transCtxMap);
		             if (ServiceUtil.isError(createResult)) {
		            	 return "error";
		             }
		             BigDecimal totAmt = BigDecimal.ZERO;
		             String finAccountTransId = (String)createResult.get("finAccountTransId");
		             
		             if(UtilValidate.isEmpty(custfinAccounts)){
		             GenericValue FinAccountTransDetails = delegator.findOne("FinAccountTrans", UtilMisc.toMap("finAccountTransId", finAccountTransId), false);
		             BigDecimal transAmount = FinAccountTransDetails.getBigDecimal("amount");
		             GenericValue finAcctValue = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", custfinAccountId), false);
		             BigDecimal existingactualBalance = FinAccountTransDetails.getBigDecimal("actualBalance");
		             if(UtilValidate.isNotEmpty(existingactualBalance)){
		             totAmt = existingactualBalance.add(transAmount);
		             finAcctValue.set("actualBalance",totAmt);
		             
		             }else{
		            	 finAcctValue.set("actualBalance",transAmount);
		             }
		             
						finAcctValue.store();
		             }
		             finAccountTransIds.add(finAccountTransId);
				
			  totalAmount = totalAmount.add(amount);
			//end of for loop
  
  		//creating batch fin account transactions here
	  	if(UtilValidate.isNotEmpty(finAccountTransIds) && finAccountTransIds.size() > 0 ){
	  		  Map serviceCtx = FastMap.newInstance();
	  		  serviceCtx.put("finAccountTransIds", finAccountTransIds);
	  		  serviceCtx.put("instrumentDate", instrumentDate);
	  		  serviceCtx.put("finAccntTransDate", instrumentDate);
	  		  serviceCtx.put("fromDate", instrumentDate);
	  		  serviceCtx.put("contraRefNum", contraRefNum);
	  		  serviceCtx.put("inFavor", inFavourOf);
	  		  serviceCtx.put("issuingAuthority", finAccountId);
	  		  serviceCtx.put("amount", totalAmount);
	  		  serviceCtx.put("statusId", "FNACTTRNSGRP_CREATED");
	  		  serviceCtx.put("finAccountId", finAccountId);
	  		  serviceCtx.put("finAcntTrnsGrpTypeId", "FIN_ACNT_TRNS_BATCH");
	  		  serviceCtx.put("createdDate", UtilDateTime.nowTimestamp());
	  		  serviceCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
	  		  serviceCtx.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
	  		  serviceCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
	  		  serviceCtx.put("userLogin", userLogin);
  			  Map resultCtx = dispatcher.runSync("createFinAccountTransGroupAndMember", serviceCtx);
	  		  if(ServiceUtil.isError(resultCtx)){
	    			Debug.logError("Error while creating fin account trans group: " + ServiceUtil.getErrorMessage(resultCtx), module);
	    			request.setAttribute("_ERROR_MESSAGE_", "Error while creating fin account trans group");
		  			TransactionUtil.rollback();
		  			return "error";
	  		  }
		  	  String finAccntTransGroupId = (String)resultCtx.get("finAccntTransGroupId");
	  	}
  		}
  	}catch (GenericEntityException e) {
  		  try {
  			  // only rollback the transaction if we started one...
  			  TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
  		  } catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  		  }
  		  Debug.logError("An entity engine error occurred while fetching data", module);
  	  }
  	  catch (GenericServiceException e) {
  		  try {
  			  // only rollback the transaction if we started one...
  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  		  } catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  		  }
  		  Debug.logError("An entity engine error occurred while calling services", module);
  	  }
  	  finally {
  		  // only commit the transaction if we started one... this will throw an exception if it fails
  		  try {
  			  TransactionUtil.commit(beganTransaction);
  		  } catch (GenericEntityException e) {
  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
  		  }
  	  }
  	request.setAttribute("_EVENT_MESSAGE_", "Successfully made processed group fin account trans entries ");
    request.setAttribute("_EVENT_MESSAGE_", "Disbursement successfully done");
    result = ServiceUtil.returnSuccess("Disbursement successfully done ");
    request.setAttribute("custRequestId",custRequestId);
    result.put("finAccountTransIds", finAccountTransIds);
    return "success"; 
}

public static Map<String, Object> cancelCustRequest(DispatchContext ctx, Map<String, ? extends Object> context){
  	Delegator delegator = ctx.getDelegator();
  	GenericValue userLogin = (GenericValue) context.get("userLogin");
  	LocalDispatcher dispatcher = ctx.getDispatcher();
  	String custRequestId = (String)context.get("custRequestId");
  	Map<String, Object> result =  FastMap.newInstance();
	try{
		GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
		custRequest.set("finstatusId", "CANCELLED");
		custRequest.store();
	}catch(Exception e){
		Debug.logError("Unable to cancel the custRequestId"+e, module);
		return ServiceUtil.returnError("Unable to cancel the custRequestId");
	}	
	result = ServiceUtil.returnSuccess("CustRequest successfully cancelled ");
  	return result;
}

}
