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

import java.util.ArrayList;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.GenericServiceException;
import java.text.ParseException;


	finAccountDetails = [];
	finAccountTransMap = [:];
	finAccountId = parameters.finAccountId;
	
	FinAccountLis = delegator.findOne("FinAccount", [finAccountId : finAccountId], false);
	finAccountName = null;
	if(UtilValidate.isNotEmpty(FinAccountLis)){
		finAccountName = FinAccountLis.finAccountName;
	}
	
	context.finAccountName=finAccountName;
	
	
	tempMap = [:];
	
		conditionList = [];
		if(UtilValidate.isNotEmpty(finAccountId)){
		conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS, finAccountId));
		}
		cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		InvoiceeeDetails = delegator.findList("Invoice", cond, null, null, null, false);
	   
		
		DetailssList = [];
		
		Map invoiceMap = FastMap.newInstance();
		for(int a=0; a<InvoiceeeDetails.size();a++){
			InvoiceeeDetailsList=[];
			
			InvoiceeeDetailsList = InvoiceeeDetails[a];
			InvoiceDetails = [];
			invoiceDate = "";
			finAccountTransId = "";
			invoiceId = "";
			narration = "";
			fininvoiceId = InvoiceeeDetailsList.get("invoiceId");
			if(fininvoiceId){
				conditionList = [];
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, fininvoiceId));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "INVOICE_PAID"),EntityOperator.OR,EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"INVOICE_READY")));
				cond1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				InvoiceDetails = delegator.findList("InvoiceAndItem", cond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(InvoiceDetails)){
					Map tempMap =FastMap.newInstance();
					invoiceDate = EntityUtil.getFirst(InvoiceDetails).invoiceDate;
					narration = EntityUtil.getFirst(InvoiceDetails).description;
					tempMap.put("invoiceDate", invoiceDate);
					tempMap.put("narration", narration);
					List invItmList = FastList.newInstance();
					InvoiceDetails.each {invItem->
						Map itemMap =FastMap.newInstance();
						itemMap.put("amount", invItem.amount);
						String invoiceItemTypeId = invItem.invoiceItemTypeId;
						invoiceItemType = delegator.findOne("InvoiceItemType",["invoiceItemTypeId":invoiceItemTypeId,],false);
						if(invoiceItemType){
							itemMap.put("description",invoiceItemType.get("description"));
						}
						invItmList.add(itemMap);
					}
					tempMap.put("invItmList", invItmList);
					invoiceMap.put(fininvoiceId, tempMap);
				}
			}
		}
		context.invoiceMap=invoiceMap;
		context.finAccountId=finAccountId;
		
	
	
	