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
import org.ofbiz.entity.*;
import org.ofbiz.base.util.Debug;
import org.ofbiz.common.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;

pmntExprs =
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADVANCES_PAYIN"),
        EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "REFUND_PAYIN"),
    ],EntityOperator.OR);

paymentTypes = delegator.findList("PaymentType", pmntExprs, null, null, null, null);
advPaymentTypeIds = EntityUtil.getFieldListFromEntityList(paymentTypes, "paymentTypeId", true);

findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
invExprs =
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_IN_PROCESS"),
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_WRITEOFF"),
        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"),
        EntityCondition.makeCondition([
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.defaultOrganizationPartyId)
                ],EntityOperator.AND),
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, context.defaultOrganizationPartyId),
                EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parameters.partyId)
                ],EntityOperator.AND)
            ],EntityOperator.OR)
        ],EntityOperator.AND);

invoiceAndApplAndPayment = delegator.find("InvoiceAndApplAndPayment", invExprs, null, null, null, findOpts);

Debug.log("invoiceAndApplAndPayment ==================="+invoiceAndApplAndPayment);

invoicesApplPaymentsList = [];
for(int i=0; i<InvoiceAndApplAndPayment.size(); i++){
	invApplPmnt = InvoiceAndApplAndPayment[i];
	
	invApplPmntMap = [:];
	invApplPmntMap["invoiceId"] = invApplPmnt.get("invoiceId"); 
	invApplPmntMap["invoiceTypeId"] = invApplPmnt.get("invoiceTypeId");
	invApplPmntMap["partyIdFrom"] = invApplPmnt.get("partyIdFrom");
	invApplPmntMap["partyId"] = invApplPmnt.get("partyId");
	invApplPmntMap["invoiceDate"] = invApplPmnt.get("invoiceDate");
	
	paymentType = invApplPmnt.get("pmPaymentTypeId");
	if(advPaymentTypeIds.contains(paymentType)){
		invApplPmntMap["advanceId"] = invApplPmnt.get("paymentId");
	}
	else{
		invApplPmntMap["paymentId"] = invApplPmnt.get("paymentId");
	}
	
	invoicesApplPaymentsList.addAll(invApplPmntMap);
	
}

context.ListInvoicesApplPayments = invoicesApplPaymentsList;



