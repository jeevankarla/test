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

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import javolution.util.FastList;

exprBldr =  new EntityConditionBuilder();
invoice = context.invoice;
if (!invoice) return;
glAccountOrganizationAndClassList = null;
if ("SALES_INVOICE".equals(invoice.invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "INVOICE_ADJ")
        EQUALS(parentTypeId: "INVOICE_ADJ")
        EQUALS(invoiceItemTypeId: "INVOICE_ITM_ADJ")
        EQUALS(parentTypeId: "INVOICE_ITM_ADJ")
        EQUALS(invoiceItemTypeId: "INV_PROD_ITEM")
        EQUALS(parentTypeId: "INV_PROD_ITEM")
    }
	map = null;
	if(invoice.partyIdFrom == "Company"){
		map = delegator.findList("InvoiceItemTypeMap", EntityCondition.makeCondition(EntityCondition.makeCondition([invoiceTypeId : invoice.invoiceTypeId]),EntityOperator.AND,EntityCondition.makeCondition(EntityCondition.makeCondition([organizationPartyId : invoice.partyIdFrom]),EntityOperator.OR,EntityCondition.makeCondition([organizationPartyId : null ]))), null, null, null, false);
	}else{
		map = delegator.findByAndCache("InvoiceItemTypeMap", [invoiceTypeId : invoice.invoiceTypeId , organizationPartyId : invoice.partyIdFrom ]);
	}
	
    
    invoiceItemTypeMaps = EntityUtil.getRelated("InvoiceItemType", map);
    invoiceItemTypes = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
    invoiceItemTypes.addAll(invoiceItemTypeMaps);
    glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyIdFrom]);
    
} else if ("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "PINVOICE_ADJ")
        EQUALS(parentTypeId: "PINVOICE_ADJ")
        EQUALS(invoiceItemTypeId: "PINVOICE_ITM_ADJ")
        EQUALS(parentTypeId: "PINVOICE_ITM_ADJ")
        EQUALS(invoiceItemTypeId: "PINV_PROD_ITEM")
        EQUALS(parentTypeId: "PINV_PROD_ITEM")
    }
    invoiceItemTypes = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
    glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyId]);
} else if ("PAYROL_INVOICE".equals(invoice.invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "PAYROL_EARN_HOURS")
        EQUALS(parentTypeId: "PAYROL_EARN_HOURS")
        EQUALS(invoiceItemTypeId: "PAYROL_DD_FROM_GROSS")
        EQUALS(parentTypeId: "PAYROL_DD_FROM_GROSS")
        EQUALS(invoiceItemTypeId: "PAYROL_TAXES")
        EQUALS(parentTypeId: "PAYROL_TAXES")
    }
    invoiceItemTypes = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
    glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyId]);
} else if ("COMMISSION_INVOICE".equals(invoice.invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "COMM_INV_ITEM")
        EQUALS(parentTypeId: "COMM_INV_ITEM")
        EQUALS(invoiceItemTypeId: "COMM_INV_ADJ")
        EQUALS(parentTypeId: "COMM_INV_ADJ")
    }
    invoiceItemTypes = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
    glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyId]);
} else {	
	map = null;
	GenericValue invoiceType = delegator.findOne("InvoiceType", [invoiceTypeId : invoice.invoiceTypeId] , false);
	if(invoiceType.parentTypeId == "SALES_INVOICE"){
		if(invoice.partyIdFrom == "Company"){
			map = delegator.findList("InvoiceItemTypeMap", EntityCondition.makeCondition(EntityCondition.makeCondition([invoiceTypeId : invoice.invoiceTypeId]),EntityOperator.AND,EntityCondition.makeCondition(EntityCondition.makeCondition([organizationPartyId : invoice.partyIdFrom]),EntityOperator.OR,EntityCondition.makeCondition([organizationPartyId : null ]))), null, null, null, false);
		}else{
			map = delegator.findByAndCache("InvoiceItemTypeMap", [invoiceTypeId : invoice.invoiceTypeId , organizationPartyId : invoice.partyIdFrom ]);
		}
		
	}
	if(invoiceType.parentTypeId == "PURCHASE_INVOICE"){
		if(invoice.partyId == "Company"){
			map = delegator.findList("InvoiceItemTypeMap", EntityCondition.makeCondition(EntityCondition.makeCondition([invoiceTypeId : invoice.invoiceTypeId]),EntityOperator.AND,EntityCondition.makeCondition(EntityCondition.makeCondition([organizationPartyId : invoice.partyId]),EntityOperator.OR,EntityCondition.makeCondition([organizationPartyId : null ]))), null, null, null, false);
		}else{
			map = delegator.findByAndCache("InvoiceItemTypeMap", [invoiceTypeId : invoice.invoiceTypeId , organizationPartyId : invoice.partyId ]);
		}
	}
	
	/*
	taxTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.NOT_EQUAL, null), UtilMisc.toSet("invoiceItemTypeId"), null, null, false);
	taxTypeIds = EntityUtil.getFieldListFromEntityList(taxTypes, "invoiceItemTypeId", true);
	map.each{ eachType ->
		if(taxTypeIds && taxTypeIds.contains(eachType.invoiceItemTypeId)){
			map.remove(eachType);
		}
	}
	Debug.log("taxTypeIds#######################"+taxTypeIds);
	Debug.log("#######################"+map);
	*/
	
	invoiceItemTypes = EntityUtil.getRelated("InvoiceItemType", map);
}
context.invoiceItemTypes = invoiceItemTypes;

context.glAccountOrganizationAndClassList = glAccountOrganizationAndClassList;
