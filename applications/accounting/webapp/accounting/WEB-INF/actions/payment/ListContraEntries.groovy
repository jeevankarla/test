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

reportTypeFlag = parameters.reportTypeFlag;
interUnitFlag = parameters.interUnitFlag;
finAccountTransList = [];
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "depositCheque" && reportTypeFlag == "headOfficeTras"){
	//finAccountTransList = [];
	finAccountId = parameters.finAccountId;
	if(UtilValidate.isNotEmpty(finAccountId)){
		finAccountTransList = delegator.findList("FinAccountTrans", EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId), null, null, null, false);
		context.put("finAccountTransList",finAccountTransList);
	}
}else{
	//finAccountTransList = [];
	finAccountId = parameters.finAccountId;
	amountStr = parameters.amount;
	contraRefNum = parameters.contraRefNum;
	transactionDate = parameters.transactionDate;
	finAccountTransTypeId = parameters.finAccountTransTypeId;
	transDate = null;
	transDateStart = null;
	transDateEnd = null;
	if(UtilValidate.isNotEmpty(transactionDate)){
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			transDate = new java.sql.Timestamp(sdf.parse(transactionDate+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + transactionDate, "");
		}
		transDateStart = UtilDateTime.getDayStart(transDate);
		transDateEnd = UtilDateTime.getDayEnd(transDate);
	}
	finAccountIdsList = [];
	if(UtilValidate.isNotEmpty(interUnitFlag) && interUnitFlag == "interUnitFlag"){
		condList=[];
		condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "INTERUNIT_ACCOUNT"));
		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, ["BANK_ACCOUNT","CASH"]),EntityOperator.AND,
			EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company")));
		
	conditions=EntityCondition.makeCondition(condList,EntityOperator.OR);
		finAccountList = delegator.findList("FinAccount",conditions , null, null, null, false);
		if(UtilValidate.isNotEmpty(finAccountList)){
			finAccountIdsList = EntityUtil.getFieldListFromEntityList(finAccountList, "finAccountId", true);
		}
	}
	List conditionList=[];
	if(UtilValidate.isNotEmpty(finAccountId)){
		conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
	}else{
		if(UtilValidate.isNotEmpty(interUnitFlag) && interUnitFlag == "interUnitFlag"){
			conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.IN, finAccountIdsList));
		}
	}
	if(UtilValidate.isNotEmpty(amountStr)){
		BigDecimal amount = new BigDecimal(amountStr);
		conditionList.add(EntityCondition.makeCondition("amount", EntityOperator.EQUALS, amount));
	}
	if(UtilValidate.isNotEmpty(contraRefNum)){
		conditionList.add(EntityCondition.makeCondition("contraRefNum", EntityOperator.EQUALS, contraRefNum));
	}
	if(UtilValidate.isNotEmpty(transactionDate)){
		conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, transDateStart));
		conditionList.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, transDateEnd));
	}
	if(UtilValidate.isNotEmpty(finAccountTransTypeId) ){
		if(!("ALL".equals(finAccountTransTypeId))){
		    conditionList.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, finAccountTransTypeId));
		}
	}else{
		conditionList.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "WITHDRAWAL"));
	}
	//conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.NOT_EQUAL, "FIN_ACCNT1"));
	conditionList.add(EntityCondition.makeCondition("reasonEnumId", EntityOperator.EQUALS, "FATR_CONTRA"));
	if(UtilValidate.isNotEmpty(parameters.finAccountTransId)){
		conditionList.add(EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, parameters.finAccountTransId));
		}
		if(UtilValidate.isNotEmpty(parameters.statusId)){
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameters.statusId));
	}
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("-transactionDate");
	finAccountTransList = delegator.findList("FinAccountTrans", condition , null, orderBy, null, false );
	if(UtilValidate.isNotEmpty(finAccountTransList)){
		context.put("finAccountTransList",finAccountTransList);
	}
}

finAccountsTranList = [];
if(parameters.noConditionFind == "N"){
	finAccountsTranList = finAccountTransList;
}
context.finAccountsTranList = finAccountsTranList;




