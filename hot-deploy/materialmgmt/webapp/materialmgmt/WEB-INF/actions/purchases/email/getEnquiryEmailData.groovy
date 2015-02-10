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
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;

import org.ofbiz.entity.util.EntityUtil;
uomList=[];
condList=[];
supplierPartyId=parameters.supplierPartyId;
custRequestId=parameters.custRequestId;
sendFromEmail="";
sendDeptEmail="";
	partyEmail= dispatcher.runSync("getPartyEmail", [partyId: supplierPartyId, userLogin: userLogin]);
	emailAddress="";
	if (partyEmail != null && partyEmail.emailAddress != null) {
			emailAddress = partyEmail.emailAddress;
	}
	Debug.log("====emailAddress=="+emailAddress+"==partyEmail=="+partyEmail+"=supplierPartyId="+supplierPartyId);
	JSONObject emailData = new JSONObject();
	emailData.put("sendToEmail",emailAddress);
	emailData.put("sendFromEmail",sendFromEmail);
	emailData.put("sendCcEmail",sendDeptEmail);
	request.setAttribute("emailData", emailData);
	return "success";



	



