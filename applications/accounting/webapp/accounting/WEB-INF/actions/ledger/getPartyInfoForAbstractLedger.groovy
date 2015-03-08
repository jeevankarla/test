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
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.common.*;
import org.ofbiz.webapp.control.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;
import java.util.Calendar;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;




fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
partyCode = parameters.partyId;
dctx = dispatcher.getDispatchContext();

//Check for Party is Valid or Not
result = [:];
//finAccount Reconcilation Report will use this logic....
SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");

partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
//Debug.log("=======partyfromDate========>"+partyfromDate+"==partythruDate=="+partythruDate);
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
   Timestamp daystart;
	try {
		daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		 } catch (ParseException e) {
			 Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   parameters.fromDateReport=UtilDateTime.getDayStart(daystart);
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   Timestamp dayend;
   try {
	   dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
   } catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
   parameters.thruDateReport=UtilDateTime.getDayEnd(dayend);
}

reconciledDate=parameters.reconciledDate;
if(UtilValidate.isNotEmpty(reconciledDate)){
Timestamp fromDateTs = null;
if(reconciledDate){
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
	try {
		fromDateTs = new java.sql.Timestamp(sdfo.parse(reconciledDate).getTime());	} catch (ParseException e) {
	}
}
parameters.reconciledDateStart = UtilDateTime.getDayStart(fromDateTs);
parameters.reconciledDateEnd = UtilDateTime.getDayEnd(fromDateTs);
}
Debug.log("==arameters.partyRoleTypeId===PartyListttt="+parameters.partyRoleTypeId);


partyIdsList=[];

dctx = dispatcher.getDispatchContext();
partyIds=[];
if(UtilValidate.isNotEmpty(parameters.partyRoleTypeId)){//to handle IceCream Parties
	roleTypeId =parameters.partyRoleTypeId;
	inputMap = [:];
	inputMap.put("userLogin", userLogin);
	inputMap.put("roleTypeId", roleTypeId);
	if(UtilValidate.isNotEmpty(parameters.partyStatusId)){
			inputMap.put("statusId", parameters.partyStatusId);
	}
	Map partyDetailsMap =(in.vasista.vbiz.byproducts.ByProductNetworkServices.getPartyByRoleType(dctx, inputMap));
	if(UtilValidate.isNotEmpty(partyDetailsMap)){
		partyDetailsList = partyDetailsMap.get("partyDetails");
		partyIdsList=partyDetailsMap.get("partyIds");
		Debug.log("==partyIdsList=="+partyIdsList);
	}
}
context.partyIdsList=partyIdsList;
	
   
Debug.log("==partyIdsList=="+partyIdsList);
return  "success";
