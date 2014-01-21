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

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.util.EntityFindOptions;

import javolution.util.FastList;

exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();

if (invoiceTypeId) {
    expr = exprBldr.AND() {
        EQUALS(parentTypeId: invoiceTypeId)    
        LESS_THAN(dueDate: UtilDateTime.nowTimestamp())
    }
 
    invoiceStatusesCondition = exprBldr.IN(statusId: ["INVOICE_APPROVED", "INVOICE_READY"])

    expr = exprBldr.AND([expr, invoiceStatusesCondition]);
    context.PastDueInvoicestotalAmount = 0;
    PastDueInvoices = delegator.findList("InvoiceAndType", expr, null, ["dueDate DESC"], null, false);
    if (PastDueInvoices) {
        invoiceIds = PastDueInvoices.invoiceId;
        totalAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: invoiceIds, organizationPartyId: organizationPartyId, userLogin: userLogin]);
        if (totalAmount) {
            context.PastDueInvoicestotalAmount = totalAmount.invoiceRunningTotal;
        }
    }
    context.PastDueInvoices = PastDueInvoices;
        
    invoicesCond = exprBldr.AND(invoiceStatusesCondition) {
        EQUALS(parentTypeId: invoiceTypeId)    
        GREATER_THAN_EQUAL_TO(dueDate: UtilDateTime.nowTimestamp())
    }
    context.InvoicesDueSoonTotalAmount=0;
    InvoicesDueSoon = delegator.findList("InvoiceAndType", invoicesCond, null, ["dueDate ASC"], null, false);
    if (InvoicesDueSoon) {
        invoiceIds = InvoicesDueSoon.invoiceId;
        totalAmount = dispatcher.runSync("getInvoiceRunningTotal", [invoiceIds: invoiceIds, organizationPartyId: organizationPartyId, userLogin: userLogin]);
        if (totalAmount) {
            context.InvoicesDueSoonTotalAmount = totalAmount.invoiceRunningTotal;
        }
    }
    context.InvoicesDueSoon = InvoicesDueSoon;    
}
