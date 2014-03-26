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
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.Delegator;
import org.ofbiz.party.party.PartyHelper;

JSONArray assetAccountBalanceData = new JSONArray();
JSONArray liabilityAccountBalanceData =new JSONArray();
JSONArray equityAccountBalanceData =new JSONArray();
JSONArray balanceTotalData = new JSONArray();

JSONObject parentGlJson = new JSONObject();
//Asset
if (assetAccountBalanceList) {
   i = 0;
   List glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, assetAccountBalanceList.glAccountId), null, null, null, false);
   assetAccountBalanceList.each { accValue ->
	   JSONObject obj = new JSONObject();
	   id = "id_" + i++;
	   obj.put("id", id);
	   obj.putAll(accValue);
	   parentGl = EntityUtil.getFirst(EntityUtil.filterByCondition(glAccountList, EntityCondition.makeCondition(UtilMisc.toMap("glAccountId",accValue.glAccountId))));
	   obj.put("parentGlAccountId", "");
	   if(parentGl){
		   obj.put("parentGlAccountId", parentGl.glAccountId);
	   }
	   if(accValue.glAccountId){
		   parentGlJson.put(accValue.glAccountId , accValue.accountName);
	   }
	   
	   obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
	   assetAccountBalanceData.add(obj);
   }
  
  List parentGlAccount = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountList.parentGlAccountId), null, null, null, false);
   parentGlAccount.each{ parentGl ->
	   parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
   }
}

context.put("assetAccountBalanceData", assetAccountBalanceData.toString());

//liability

if (liabilityAccountBalanceList) {
	i = 0;
	List glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, liabilityAccountBalanceList.glAccountId), null, null, null, false);
	liabilityAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		parentGl = EntityUtil.getFirst(EntityUtil.filterByCondition(glAccountList, EntityCondition.makeCondition(UtilMisc.toMap("glAccountId",accValue.glAccountId))));
		obj.put("parentGlAccountId", "");
		if(parentGl){
			obj.put("parentGlAccountId", parentGl.glAccountId);
		}
		if(accValue.glAccountId){
			parentGlJson.put(accValue.glAccountId , accValue.accountName);
		}
		
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		liabilityAccountBalanceData.add(obj);
	}
   
   List parentGlAccount = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountList.parentGlAccountId), null, null, null, false);
	parentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
 }
context.put("liabilityAccountBalanceData", liabilityAccountBalanceData.toString());

//equity

if (equityAccountBalanceList) {
	i = 0;
	List glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, equityAccountBalanceList.glAccountId), null, null, null, false);
	equityAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		parentGl = EntityUtil.getFirst(EntityUtil.filterByCondition(glAccountList, EntityCondition.makeCondition(UtilMisc.toMap("glAccountId",accValue.glAccountId))));
		obj.put("parentGlAccountId", "");
		if(parentGl){
			obj.put("parentGlAccountId", parentGl.glAccountId);
		}
		if(accValue.glAccountId){
			parentGlJson.put(accValue.glAccountId , accValue.accountName);
		}
		
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		equityAccountBalanceData.add(obj);
	}
   
   List parentGlAccount = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountList.parentGlAccountId), null, null, null, false);
	parentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
 }
context.put("equityAccountBalanceData", equityAccountBalanceData.toString());
//balance Totals

if (balanceTotalList) {
	i = 0;
	List glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, balanceTotalList.glAccountId), null, null, null, false);
	balanceTotalList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		parentGl = EntityUtil.getFirst(EntityUtil.filterByCondition(glAccountList, EntityCondition.makeCondition(UtilMisc.toMap("glAccountId",accValue.glAccountId))));
		obj.put("parentGlAccountId", "");
		if(parentGl){
			obj.put("parentGlAccountId", parentGl.glAccountId);
		}
		if(accValue.glAccountId){
			parentGlJson.put(accValue.glAccountId , accValue.accountName);
		}
		
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		balanceTotalData.add(obj);
	}
   
   List parentGlAccount = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountId", EntityOperator.IN, glAccountList.parentGlAccountId), null, null, null, false);
	parentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
 }
context.put("balanceTotalData", balanceTotalData.toString());

context.put("parentGlJson", parentGlJson.toString());
Debug.log("parentGlJson========"+parentGlJson);