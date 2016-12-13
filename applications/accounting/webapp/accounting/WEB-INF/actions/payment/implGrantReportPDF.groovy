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
		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
		}
		cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		finAccountDetails = delegator.findList("FinAccountTrans", cond, null, null, null, false);
	 
		DetailssList = [];
		
	
		for(int a=0; a<finAccountDetails.size();a++){
			finAccountDetailsList=[];
			
			finAccountDetailsList = finAccountDetails[a];
			InvoiceDetails = [];
			invoiceDate = "";
			finAccountTransId = "";
			invoiceId = "";
			narration = "";
			fininvoiceId = finAccountDetailsList.get("invoiceId");
			if(fininvoiceId){
				Debug.log("fininvoiceId==="+fininvoiceId);
			
				conditionList = [];
				if(fininvoiceId){
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, fininvoiceId));
				}
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "INVOICE_PAID"),EntityOperator.OR,EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"INVOICE_READY")));
				cond1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				InvoiceDetails = delegator.findList("Invoice", cond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(InvoiceDetails)){
			invoiceDate = EntityUtil.getFirst(InvoiceDetails).invoiceDate;
			narration = EntityUtil.getFirst(InvoiceDetails).description;
		//	Debug.log("invoiceDate==="+invoiceDate);
				
			
				conditionList1 = [];
				if(fininvoiceId){
				conditionList1.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, fininvoiceId));
				}
				invoiceItemTypeId = "";
				cond2 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
				InvoiceDetails1 = delegator.findList("InvoiceItem", cond2, null, null, null, false);
				if(UtilValidate.isNotEmpty(InvoiceDetails1)){
			amount = EntityUtil.getFirst(InvoiceDetails1).amount;
			//description1 = EntityUtil.getFirst(InvoiceDetails1).description;
			invoiceItemTypeId =  EntityUtil.getFirst(InvoiceDetails1).invoiceItemTypeId;
				}
				
			
			condList = [];
			if(UtilValidate.isNotEmpty(invoiceItemTypeId)){
			condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invoiceItemTypeId));
			
			}
			cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			InvoiceItemTypeDetails = delegator.findList("InvoiceItemType", cond, null, null, null, false);
		 
			if(UtilValidate.isNotEmpty(InvoiceItemTypeDetails)){
			description = EntityUtil.getFirst(InvoiceItemTypeDetails).description;
			
			}
			finAccMap=[:];
			
			finAccMap["invoiceDate"]=invoiceDate;
			finAccMap["narration"]=narration;
			finAccMap["invoiceId"]=fininvoiceId;
			finAccMap["amount"]=amount;
			finAccMap["description"]=description;
			DetailssList.add(finAccMap);
			finAccountTransMap.put(finAccountId,DetailssList);
			Debug.log(finAccountId+"finAccountId==="+DetailssList);
				}
			}
			
	}
		
		context.finAccountTransMap=finAccountTransMap;
		context.finAccountId=finAccountId;
	
	