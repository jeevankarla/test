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

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.awt.List;
import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;
import javolution.util.FastList;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
supplyDate = parameters.supplyDate;
subscriptionTypeId =  parameters.subscriptionTypeId;
dispatchshiftid = "32";
if(subscriptionTypeId =="PM"){
	dispatchshiftid ="33";
}
populateData = parameters.populateData;
dailyIndents =[];
dailyindentdetails =[];
Debug.log("supplyDate=========="+supplyDate);
SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy");
if(UtilValidate.isNotEmpty(supplyDate)){
	try {
		supplyDateTime = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + supplyDate, "");
	}
	supplyDateTime = UtilDateTime.getDayStart(supplyDateTime);
	}
	
 if(populateData && populateData =="on"){
		Debug.log("populate Data"+supplyDateTime);
		parameters.supplyDate = UtilDateTime.toDateString(supplyDateTime , "dd MMMMM, yyyy");
		parameters.hideSearch ="N";
		parameters.productSubscriptionTypeIds = ["CASH","CREDIT"]
		parameters.facilityId = "S1350";
		quotaResult = GroovyUtil.runScriptAtLocation("component://byproducts/webapp/byproducts/WEB-INF/actions/facility/quotaListing.groovy", context);
		//lets populate Daily indent and indent details here
		populateDailyIndentDetail(quotaResult);
		
 }
conditionList =[];
conditionList.add(EntityCondition.makeCondition("indentdate", EntityOperator.EQUALS, (UtilDateTime.toDateString(supplyDateTime, "dd-MMM-yyyy"))));
EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

DailyIndentFieldsOrder =[];
DailyIndentFieldsOrder = ["id", "routeid" , "retailerid","indentsourceid","dispatchshiftid","indentdate","createddatetime","createdby","modifieddatetime","modifiedby"];
HashSet fieldsToSelect = new HashSet(DailyIndentFieldsOrder);

dailyIndents = delegator.findList("Dailyindent", condition1,  fieldsToSelect, null, null, false);

/*INSERT INTO DAILYINDENT (ID, ROUTEID, RETAILERID, INDENTSOURCEID, DISPATCHSHIFTID, INDENTDATE, 
	CREATEDDATETIME, CREATEDBY, MODIFIEDDATETIME, MODIFIEDBY) VALUES
(5000000, 144, 3683, 86, 33, '28-Mar-2014', '28-Mar-2014', '6106', '28-MAR-2014', '6106')
*/

sqlString = "";

stringFieldList =[];
numberFieldList =[];
stringFieldList = ["indentdate","createddatetime","createdby","modifieddatetime","modifiedby"];
numberFieldList = ["id", "routeid" , "retailerid","indentsourceid","dispatchshiftid"];

for(i=0; i<dailyIndents.size(); i++){
	dailyIndent = dailyIndents.get(i);
	sqlString = sqlString +"INSERT INTO DAILYINDENT (ID, ROUTEID, RETAILERID, INDENTSOURCEID, DISPATCHSHIFTID, INDENTDATE,	CREATEDDATETIME, CREATEDBY, MODIFIEDDATETIME, MODIFIEDBY) VALUES ("
	for(j=0;j<DailyIndentFieldsOrder.size();j++){
		dailyIndentField = DailyIndentFieldsOrder.get(j);
		if(stringFieldList.contains(dailyIndentField)){
			sqlString = sqlString+"\'"+dailyIndent.getAt(dailyIndentField) +"\'";
		}else{
		  sqlString = sqlString+ dailyIndent.getAt(dailyIndentField);
		}
		if((DailyIndentFieldsOrder.size()-1)!= j){
			sqlString = sqlString+",";
		}
	}
	sqlString = sqlString+");\n\t";
}


// Indent Details
DailyIndentDetailFieldsOrder =[];
DailyIndentDetailFieldsOrder = ["id", "dailyindentid" , "productid","quantity","createddatetime","createdby","modifieddatetime","modifiedby"];
HashSet fieldsToSelect1 = new HashSet(DailyIndentDetailFieldsOrder);

dailyindentdetails = delegator.findList("Dailyindentdetail", EntityCondition.makeCondition("dailyindentid",EntityOperator.IN ,
	 EntityUtil.getFieldListFromEntityList(dailyIndents, "id", true)),  fieldsToSelect1, null, null, false);
 
 
 for(i=0; i<dailyindentdetails.size(); i++){
	 dailyIndentDetail = dailyindentdetails.get(i);
	 sqlString = sqlString +"INSERT INTO DAILYINDENTDETAIL (ID, DAILYINDENTID,PRODUCTID, QUANTITY, CREATEDDATETIME, CREATEDBY, MODIFIEDDATETIME, MODIFIEDBY) VALUES ("
	 for(j=0;j<DailyIndentDetailFieldsOrder.size();j++){
		 dailyIndentField = DailyIndentDetailFieldsOrder.get(j);
		 if(stringFieldList.contains(dailyIndentField)){
			 sqlString = sqlString+"\'"+dailyIndentDetail.getAt(dailyIndentField) +"\'";
		 }else{
		   sqlString = sqlString+ dailyIndentDetail.getAt(dailyIndentField);
		 }
		 if((DailyIndentDetailFieldsOrder.size()-1)!= j){
			 sqlString = sqlString+",";
		 }
	 }
	 sqlString = sqlString+");\n\t";
 }
 
context.sqlString =sqlString;

def populateDailyIndentDetail(quotaResult){
	//Debug.log("quotaResult====="+quotaResult);
	facilityIdisRetailers = delegator.findList("FacilityIdisRetailer", null, null, null, null, false);
	facilityRetailerMap = [:];
	conditionList1 =[];
	conditionList1.add(EntityCondition.makeCondition("indentdate", EntityOperator.EQUALS, (UtilDateTime.toDateString(supplyDateTime, "dd-MMM-yyyy"))));
	EntityCondition condition2 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
	dailyIndents = delegator.findList("Dailyindent", condition2,  null, null, null, false);
	dailyindentdetails = delegator.findList("Dailyindentdetail", EntityCondition.makeCondition("dailyindentid",EntityOperator.IN ,
		EntityUtil.getFieldListFromEntityList(dailyIndents, "id", true)),  null, null, null, false);
	delegator.removeAll(dailyIndents);
	delegator.removeAll(dailyindentdetails);
	
	for(GenericValue facilityIdisRetailer :facilityIdisRetailers){
		facilityRetailerMap.put(facilityIdisRetailer.get("facilityId"),facilityIdisRetailer.get("idisRetailerId"));
	}
	boothsResultMap = quotaResult.get("boothsResultMap");
	productList =  quotaResult.get("productList");
	BoothRouteWiseMap = quotaResult.get("BoothRouteWiseMap");
	Iterator mapIter = BoothRouteWiseMap.entrySet().iterator();
	
	
	while (mapIter.hasNext()) {
		Map.Entry entry = mapIter.next();
		boothId =entry.getKey();
		indentRouteValue = entry.getValue();
		//Debug.log("boothId====="+boothId);
		
		Iterator mapRouteIter = indentRouteValue.entrySet().iterator();
		while (mapRouteIter.hasNext()) {
			Map.Entry entryDtail = mapRouteIter.next();
			routeId =entryDtail.getKey();
			indentValue = entryDtail.getValue();
			GenericValue newDailyIndent = delegator.makeValue("Dailyindent");
			newDailyIndent.set("indentsourceid","33");
			newDailyIndent.set("indentdate", UtilDateTime.toDateString(supplyDateTime , "dd-MMM-yyyy"));
			newDailyIndent.set("createddatetime", UtilDateTime.toDateString(supplyDateTime , "dd-MMM-yyyy"));
			newDailyIndent.set("modifieddatetime", UtilDateTime.toDateString(supplyDateTime , "dd-MMM-yyyy"));
			newDailyIndent.set("createdby","6106");
			newDailyIndent.set("modifiedby","6106");
			newDailyIndent.set("dispatchshiftid",dispatchshiftid);
			newDailyIndent.set("routeid",(facilityRetailerMap[routeId]).toString());
			newDailyIndent.set("retailerid",(facilityRetailerMap[boothId]).toString());
			//dailyindentSeq++;
			
			try{
				//newDailyIndent.create();
				delegator.createSetNextSeqId(newDailyIndent);
			}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, "", "");
			}
			
			//newDailyIndent.store();
			for(GenericValue product :productList){
				if(UtilValidate.isEmpty(indentValue.get(product.get("productId")))){
					continue;
				}
				GenericValue newDailyIndentDetail = delegator.makeValue("Dailyindentdetail");
				newDailyIndentDetail.set("dailyindentid", newDailyIndent.get("id"));
				newDailyIndentDetail.set("productid", product.get("productId"));
				newDailyIndentDetail.set("quantity", (indentValue.get(product.get("productId"))).toString());
				newDailyIndentDetail.set("createddatetime", UtilDateTime.toDateString(supplyDateTime , "dd-MMM-yyyy"));
				newDailyIndentDetail.set("modifieddatetime", UtilDateTime.toDateString(supplyDateTime , "dd-MMM-yyyy"));
				newDailyIndentDetail.set("createdby","6106");
				newDailyIndentDetail.set("modifiedby","6106");
				delegator.createSetNextSeqId(newDailyIndentDetail);
				//dailyindentdetails.add(newDailyIndentDetail);
				
			}
	
		}
		
	}	//end of while
		
}//end function
