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

import org.eclipse.emf.ecore.impl.EPackageRegistryImpl.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import  org.ofbiz.network.NetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;

cardSaleReportList = [];
lastChangeSubProdMap = [:];
productSeqMap = [:];
productNamMap = [:];
productMap = [:];
exprList = [];

productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
for (int i=0; i < productList.size(); i++) {
	product = productList.get(i);
	productSeqMap[product.productId]= "prod_"+i;
	productNamMap["prodName_"+i]= product.productName;
}
context.productNamMap = productNamMap;

customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : timePeriod], false);
fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));

exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CARD"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);   
cardSubProdList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["parentFacilityId", "facilityId"], null, false);

tempBoothId = "";
for (int i=0; i < cardSubProdList.size(); i++) {
	cardSubProdItem = cardSubProdList.get(i);
	if (tempBoothId == "") {
		tempBoothId = cardSubProdItem.facilityId;
		lastChangeSubProdMap["boothId"] = cardSubProdItem.facilityId;
		lastChangeSubProdMap["productSubscriptionTypeId"] = cardSubProdItem.productSubscriptionTypeId;
		lastChangeSubProdMap["routeId"] = cardSubProdItem.parentFacilityId;
		lastChangeSubProdMap["customTimePeriodId"] = cardSubProdItem.customTimePeriodId;
		lastChangeSubProdMap["paymentTypeId"] = cardSubProdItem.paymentTypeId;
	}
	if (tempBoothId != cardSubProdItem.facilityId)  {
		tempBoothId = cardSubProdItem.facilityId;
		cardSaleReportList.add(lastChangeSubProdMap);
		lastChangeSubProdMap = [:];
		productList.each{ product ->
			lastChangeSubProdMap[productSeqMap.get(product.productId)] = '';
		}
		lastChangeSubProdMap["boothId"] = cardSubProdItem.facilityId;
		lastChangeSubProdMap["productSubscriptionTypeId"] = cardSubProdItem.productSubscriptionTypeId;
		lastChangeSubProdMap["routeId"] = cardSubProdItem.parentFacilityId;
		lastChangeSubProdMap["customTimePeriodId"] = cardSubProdItem.customTimePeriodId;
		lastChangeSubProdMap["paymentTypeId"] = cardSubProdItem.paymentTypeId;
	}
	lastChangeSubProdMap[productSeqMap.get(cardSubProdItem.productId)] = (cardSubProdItem.quantity);
}
cardSaleReportList.add(lastChangeSubProdMap);
context.cardSaleReportList = cardSaleReportList;
//Debug.logInfo("checkListReportList=========================>"+cardSaleReportList,"");
