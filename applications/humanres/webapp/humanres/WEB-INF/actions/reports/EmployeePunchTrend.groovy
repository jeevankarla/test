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
import org.ofbiz.entity.condition.*;
import java.util.*;
import java.lang.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityFindOptions;
import net.sf.json.JSONArray;
import java.text.SimpleDateFormat;
import java.text.ParseException;
//partyId = parameters.get("partyId");
//delegator=request.getAttribute("delegator");
employeeId = parameters.get("employeeId");
//System.out.println("\n\n\n==============================party id==================================="+partyId);
if(parameters.get("employeeId") != null){
	GenericValue party = delegator.findByPrimaryKey("EmployeeDetail", UtilMisc.toMap("partyId", employeeId));
	partyId = party.get("partyId");

}
if(partyId==null)
	partyId = userLogin.getString("partyId");

Map serviceCtx = UtilMisc.toMap("userLogin", userLogin);
serviceCtx.put("partyId", "Company");
serviceCtx.put("employeeId", partyId);
Map result = dispatcher.runSync("emplMonthlyPunchReport", serviceCtx);
punchDataList = result.get("punchDataList");
columnTitleMap = result.get("columnTitleMap");
JSONArray listPNOJSON= new JSONArray();
def sdf = new SimpleDateFormat("dd/MM/yy");

result = dispatcher.runSync("emplMonthlyLeaveReport", serviceCtx);
leaveDataList = result.get("leaveDataList");

if(punchDataList){
	partyPunchData = [:];
	for(int j=0; j<punchDataList.size();j++){
		punchData = punchDataList.get(j);
		if((punchData.get("partyId")).equals(partyId)){
			partyPunchData= punchData;
		}
		
	}
	for(Map.Entry entry : partyPunchData.entrySet()){
		
		dayNo = entry.getKey();
		totalHours = entry.getValue();
		keyname = dayNo+"Title";
		if(columnTitleMap.get(keyname)){
			JSONArray dayInoutList= new JSONArray();
			dateString = columnTitleMap.get(keyname);
			totalHours = (totalHours.replace("Hrs","")).replace(":", ".");
			try {
				dayInoutList.add(((sdf.parse(dateString)).getTime()));
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + e, "");
				context.errorMessage = "Cannot parse date string: " + e;
				return;
			}
			dayInoutList.add(totalHours);
			listPNOJSON.add(dayInoutList);
		}
		
	}
	
}
context.listPNOJSON=listPNOJSON;
JSONArray listLeavesJSON= new JSONArray();
JSONArray labelsJSON = new JSONArray();
int i = 1;
if(leaveDataList){
	partyLeaveData = [:];
	for(int j=0; j<leaveDataList.size();j++){
		leaveData = leaveDataList.get(j);
		if((leaveData.get("partyId")).equals(partyId)){
			partyLeaveData = leaveData;
		}
		
	}
	partyLeaveDetails = [:];
	if(partyLeaveData){
		partyLeaveDetails = partyLeaveData.get("emplLeaveDetail");
	}
	
	for(Map.Entry entry : partyLeaveDetails.entrySet()){
		
		month = entry.getKey();
		totalDays = entry.getValue();
		JSONArray monthLeaveList= new JSONArray();
		Debug.log("month============"+month);
		Debug.log("totalDays============"+totalDays);
		//monthLeaveList.add(new SimpleDateFormat("MMMM").format(month));
		monthLeaveList.add(i);
		monthLeaveList.add(totalDays);
		JSONArray labelsList= new JSONArray();
		labelsList.add(i);
		labelsList.add(new SimpleDateFormat("MMMM-yyyy").format(month));
		labelsJSON.add(labelsList);
		listLeavesJSON.add(monthLeaveList);
		++i;
	}
	
}
context.listLeavesJSON=listLeavesJSON;
context.labelsJSON=labelsJSON;
//context.put("data",data);
context.put("employeeId",employeeId);

