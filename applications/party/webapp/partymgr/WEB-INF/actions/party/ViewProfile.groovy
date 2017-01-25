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

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

partyId = parameters.partyId ?: parameters.party_id;
userLoginId = parameters.userlogin_id ?: parameters.userLoginId;

if (!partyId && userLoginId) {
    thisUserLogin = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
    if (thisUserLogin) {
        partyId = thisUserLogin.partyId;
		
        parameters.partyId = partyId;
		
    }
}

employments=delegator.findByAnd("Employment",[partyIdTo:partyId]);
if(employments){
	context.partyIdFrom=employments.get(0).partyIdFrom;
	context.roleTypeIdFrom=employments.get(0).roleTypeIdFrom;
	context.roleTypeIdTo=employments.get(0).roleTypeIdTo;
	context.fromDate=employments.get(0).fromDate;
}

context.showOld = "true".equals(parameters.SHOW_OLD);
context.partyId = partyId;
context.party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
context.nowStr = UtilDateTime.nowTimestamp().toString();
partyIdentificationList = [];

partyIdentifications=delegator.findByAnd("PartyIdentification",[partyId:partyId]);
if(UtilValidate.isNotEmpty(partyIdentifications)){
	for(int i=0;i<partyIdentifications.size();i++){
		tempMap = [:];
		partyIdentification = partyIdentifications.get(i);
		tempMap.put("partyIdentificationTypeId", partyIdentification.partyIdentificationTypeId);
		tempMap.put("partyId", partyIdentification.partyId);
		tempMap.put("idValue", partyIdentification.idValue);
		tempMap.put("issueDate", UtilDateTime.toDateString(partyIdentification.issueDate, "dd/MM/yyyy"));
		tempMap.put("expiryDate", partyIdentification.expiryDate); 
		partyIdMap = [:];
		partyIdMap.putAll(tempMap);
		if(UtilValidate.isNotEmpty(partyIdMap)){
			partyIdentificationList.addAll(partyIdMap);
		}
	}
}

context.partyIdentificationList = partyIdentificationList;
