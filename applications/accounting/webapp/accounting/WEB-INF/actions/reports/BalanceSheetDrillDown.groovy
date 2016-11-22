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
	assetAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		assetAccountBalanceData.add(obj);
	}
	
	ParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
	
}

context.put("assetAccountBalanceData", assetAccountBalanceData.toString());

//liability

if (liabilityAccountBalanceList) {
	i = 0;
	liabilityAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		liabilityAccountBalanceData.add(obj);
	}
	
	ParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
	
}

context.put("liabilityAccountBalanceData", liabilityAccountBalanceData.toString());

//equity

if (equityAccountBalanceList) {
	i = 0;
	equityAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		equityAccountBalanceData.add(obj);
	}
	
	ParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
}

	
context.put("equityAccountBalanceData", equityAccountBalanceData.toString());
//balance Totals

if (balanceTotalList) {
	i = 0;
	balanceTotalList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		balanceTotalData.add(obj);
	}
	
	ParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
}

	
context.put("balanceTotalData", balanceTotalData.toString());

context.put("parentGlJson", parentGlJson.toString());
