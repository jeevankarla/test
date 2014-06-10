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

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;

JSONArray jsonTrialBalanceData = new JSONArray();



JSONObject parentGlJson = new JSONObject();
//REVENUE
trillDownTrialBalanceMap=[:];
parentGlNameMap=[:];

if (glAccountAndHistories) {
   i = 0;
   List trailBalParentGlAccount = delegator.findList("GlAccountClass", EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, glAccountAndHistories.glAccountClassId), null, null, null, false);
   trailBalParentGlAccount.each{ parentGl ->
	   parentGlJson.put(parentGl.glAccountClassId , parentGl.description);
	   parentGlNameMap[parentGl.glAccountClassId]=parentGl.description;
   }
   glAccountAndHistories.each { accValue ->
	   JSONObject obj = new JSONObject();
	   id = "id_" + i++;
	   obj.put("id", id);
	   obj.putAll(accValue);
	   //obj.put("totalEndingBalance", (accValue.totalPostedDebits- accValue.totalPostedCredits));
	   obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
	   jsonTrialBalanceData.add(obj);
	   glAccountClassId=accValue.glAccountClassId;
	   if(UtilValidate.isEmpty(trillDownTrialBalanceMap.get(glAccountClassId))){
		   innerGlmap=[:];
		   innerGlmap["debit"]=accValue.totalPostedDebits;
		   innerGlmap["credit"]=accValue.totalPostedCredits;
		   innerGlmap["openingD"]=accValue.openingD;
		   innerGlmap["openingC"]=accValue.openingC;
		   innerGlmap["endingBal"]=((accValue.totalPostedDebits+accValue.openingD)- (accValue.totalPostedCredits+accValue.openingC));
		   List<GenericValue> accValueList=FastList.newInstance();
		   accValueList.add(accValue);
		   innerGlmap["chaildGlList"]=accValueList;
		   trillDownTrialBalanceMap[glAccountClassId]=innerGlmap;
	   }else if(UtilValidate.isNotEmpty(trillDownTrialBalanceMap.get(glAccountClassId))){
	         updateInnerGlMap=(Map)trillDownTrialBalanceMap.get(glAccountClassId);
			 updateInnerGlMap["debit"]+=accValue.totalPostedDebits;
			 updateInnerGlMap["credit"]+=accValue.totalPostedCredits;
			 updateInnerGlMap["endingBal"]+=((accValue.totalPostedDebits+accValue.openingD)- (accValue.totalPostedCredits+accValue.openingC));
			 List<GenericValue> accValueUpdateList=FastList.newInstance();
			accValueUpdateList= updateInnerGlMap["chaildGlList"];
			accValueUpdateList.add(accValue);
			 updateInnerGlMap["chaildGlList"]=accValueUpdateList;
			 trillDownTrialBalanceMap[glAccountClassId]=updateInnerGlMap;
	   }
   }

}


context.put("jsonTrialBalanceData", jsonTrialBalanceData.toString());
context.put("parentGlJson", parentGlJson.toString());
//using for pdf report
//Debug.log("trillDownTrialBalanceMap============="+trillDownTrialBalanceMap);
context.put("trillDownTrialBalanceMap", trillDownTrialBalanceMap);
context.put("parentGlNameMap", parentGlNameMap);
