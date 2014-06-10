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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.Delegator;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;
import org.ofbiz.accounting.util.*;

  asOnDate = parameters.asOnDate;

  dctx = dispatcher.getDispatchContext();
 glAccountAndHistories =[];
 condList = [];
 condList.add(EntityCondition.makeCondition("organizationPartyId" , EntityOperator.EQUALS,parameters.organizationPartyId));
 condList.add(EntityCondition.makeCondition("customTimePeriodId" , EntityOperator.EQUALS,parameters.customTimePeriodId));
  List tempGlAccountAndHistories = delegator.findList("GlAccountAndHistoryTotals", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
	 Map lastClosedGlBalances = UtilAccounting.getLastClosedGlBalance(dctx, UtilMisc.toMap("organizationPartyId", parameters.organizationPartyId,"customTimePeriodId",parameters.customTimePeriodId));
	 lastClosedGlBalanceList =[];
	 lastClosedGlBalanceList = lastClosedGlBalances.get("openingGlHistory");
	 Debug.log(" before lastClosedGlBalanceList==========="+lastClosedGlBalanceList);
	 tempGlAccountAndHistories.each { tempGlAccountAndHistory ->
		 tempGlAccountAndHistoryMap =[:];
		 tempGlAccountAndHistoryMap.putAll(tempGlAccountAndHistory);
		 lastClosedGlBalance = EntityUtil.getFirst(EntityUtil.filterByAnd(lastClosedGlBalanceList, UtilMisc.toMap("glAccountId",tempGlAccountAndHistory.get("glAccountId"))))
		 tempGlAccountAndHistoryMap.putAt("openingC",0);
		 tempGlAccountAndHistoryMap.putAt("openingD",0);
		 if(UtilValidate.isNotEmpty(lastClosedGlBalance)){
			
			if((lastClosedGlBalance.get("totalEndingBalance") <0 )){
				tempGlAccountAndHistoryMap.putAt("openingC",((lastClosedGlBalance.getBigDecimal("totalEndingBalance").negate())));
			}else{
			   tempGlAccountAndHistoryMap.putAt("openingD", lastClosedGlBalance.getBigDecimal("totalEndingBalance"));
			}
			lastClosedGlBalanceList.remove(lastClosedGlBalance);
		}
		tempGlAccountAndHistoryMap.putAt("totalEndingBalance", ((tempGlAccountAndHistoryMap.get("totalPostedDebits")+tempGlAccountAndHistoryMap.get("openingD"))-(tempGlAccountAndHistoryMap.get("totalPostedCredits")+tempGlAccountAndHistoryMap.get("openingC"))));
		glAccountAndHistories.add(tempGlAccountAndHistoryMap);
	 }
	 Debug.log("lastClosedGlBalanceList==========="+lastClosedGlBalanceList);
	 if(UtilValidate.isNotEmpty(lastClosedGlBalanceList)){
		 lastClosedGlBalanceList.each{ tempGlAccountAndHistory ->
			 tempGlAccountAndHistoryMap =[:];
			 tempGlAccountAndHistoryMap.putAll(tempGlAccountAndHistory);
			 tempGlAccountAndHistoryMap.putAt("openingC",0);
			 tempGlAccountAndHistoryMap.putAt("openingD",0);
			 if((tempGlAccountAndHistory.get("totalEndingBalance") < 0 )){
				 tempGlAccountAndHistoryMap.putAt("openingC",((tempGlAccountAndHistory.getBigDecimal("totalEndingBalance").negate())));
			 }else{
				tempGlAccountAndHistoryMap.putAt("openingD", tempGlAccountAndHistory.getBigDecimal("totalEndingBalance"));
			 }
			 tempGlAccountAndHistoryMap.putAt("totalPostedDebits",0 );
			 tempGlAccountAndHistoryMap.putAt("totalPostedCredits",0 );
			 tempGlAccountAndHistoryMap.putAt("totalEndingBalance", ((tempGlAccountAndHistoryMap.get("totalPostedDebits")+tempGlAccountAndHistoryMap.get("openingD"))-(tempGlAccountAndHistoryMap.get("totalPostedCredits")+tempGlAccountAndHistoryMap.get("openingC"))));
			 glAccountAndHistories.add(tempGlAccountAndHistoryMap);
		 }
	 }
  context.glAccountAndHistories = glAccountAndHistories;


