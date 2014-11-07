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
finAccountReconciliationList=[];

//finAccountTransList.each{finAccountTrans->
finAccountTransList.eachWithIndex {finAccountTrans, idx ->
	tempFinAccountTransMap=[:];
	tempFinAccountTransMap["sNo"]=idx+1;
	tempFinAccountTransMap["finAccountTransId"]=finAccountTrans.finAccountTransId;
    tempFinAccountTransMap["finAccountTransTypeId"]=finAccountTrans.finAccountTransTypeId;
    tempFinAccountTransMap["partyId"]=finAccountTrans.partyId;
    tempFinAccountTransMap["paymentPartyId"]="";
    tempFinAccountTransMap["paymentPartyName"]="";
    tempFinAccountTransMap["transactionDate"]=UtilDateTime.toDateString(finAccountTrans.transactionDate,"dd-MM-yy hh:mm:ss");
    tempFinAccountTransMap["instrumentNo"]="";
   tempFinAccountTransMap["amount"]=finAccountTrans.amount;
   tempFinAccountTransMap["paymentId"]=finAccountTrans.paymentId;
   tempFinAccountTransMap["paymentType"]="";
   tempFinAccountTransMap["paymentMethodTypeId"]="";
   tempFinAccountTransMap["statusId"]=finAccountTrans.statusId;
   tempFinAccountTransMap["comments"]=finAccountTrans.comments;
   
      if(UtilValidate.isNotEmpty(finAccountTrans.paymentId)){
       payment = delegator.findOne("Payment", [paymentId : finAccountTrans.paymentId], true);
	   if(UtilValidate.isNotEmpty(payment.paymentTypeId)){
	   paymentType=delegator.findOne("PaymentType", [paymentTypeId : payment.paymentTypeId], true);
	     if(UtilValidate.isNotEmpty(paymentType)){
          tempFinAccountTransMap["paymentType"]=paymentType.description;
	     }
	   }
       if(UtilValidate.isNotEmpty(finAccountTrans.paymentId) && UtilValidate.isNotEmpty(finAccountTrans.finAccountTransTypeId)){
		   if( finAccountTrans.finAccountTransTypeId=="WITHDRAWAL"){
		    tempFinAccountTransMap["paymentPartyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, payment.partyIdTo, false);
		    tempFinAccountTransMap["paymentPartyId"] = payment.partyIdTo;
		   }else if(finAccountTrans.finAccountTransTypeId=="DEPOSIT"){
		    tempFinAccountTransMap["paymentPartyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, payment.partyIdFrom, false);
		    tempFinAccountTransMap["paymentPartyId"] = payment.partyIdFrom;
		   }
        }
         tempFinAccountTransMap["instrumentNo"]=payment.paymentRefNum;
         paymentMethodType = delegator.findOne("PaymentMethodType", ["paymentMethodTypeId" : payment.paymentMethodTypeId], true);
          if(UtilValidate.isNotEmpty(paymentMethodType)){
            tempFinAccountTransMap["paymentMethodTypeId"]=paymentMethodType.description;
          }
	   }else if(UtilValidate.isNotEmpty(finAccountTrans.reasonEnumId) &&(finAccountTrans.reasonEnumId=="FATR_CONTRA")){
	          contraFinTransEntry = delegator.findOne("FinAccountTransAttribute", ["finAccountTransId" : finAccountTrans.finAccountTransId,"attrName" : "FATR_CONTRA"], true);
	          if(UtilValidate.isNotEmpty(contraFinTransEntry)){
                 contraFinAccountTrans = delegator.findOne("FinAccountTrans", ["finAccountTransId" : contraFinTransEntry.finAccountTransId], false);
                 if(UtilValidate.isNotEmpty(contraFinTransEntry)){
                   contraFinAccount= delegator.findOne("FinAccount", ["finAccountId" :contraFinAccountTrans.finAccountId], false);
                   tempFinAccountTransMap["paymentPartyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, contraFinAccount.ownerPartyId, false);
		           tempFinAccountTransMap["paymentPartyId"] = contraFinAccount.ownerPartyId;
                 }
	          }
	        tempFinAccountTransMap["instrumentNo"]=finAccountTrans.contraRefNum;
    }
   finAccountReconciliationList.addAll(tempFinAccountTransMap);
}
context.finAccountReconciliationList=finAccountReconciliationList;
//Debug.log("==finAccountReconciliationList=="+finAccountReconciliationList);





