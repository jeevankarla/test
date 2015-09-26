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
 import org.ofbiz.entity.Delegator;
 import org.ofbiz.entity.GenericValue;
 import org.ofbiz.entity.util.EntityUtil;
 
 import java.util.*;
 import java.awt.image.renderable.ContextualRenderedImageFactory;
 import java.lang.*;
 
 import org.ofbiz.entity.*;
 import org.ofbiz.entity.condition.*;
 import org.ofbiz.base.util.UtilMisc;
 import org.ofbiz.entity.condition.EntityCondition;
 import org.ofbiz.entity.condition.EntityOperator;
 
 import java.sql.*;
 import java.util.Calendar;
 
 import javolution.util.FastList;
 import javolution.util.FastMap;
 
 import java.sql.Timestamp;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 
 import org.ofbiz.base.util.UtilNumber;
 
 import java.math.RoundingMode;
 import java.util.Map;
 
 import org.ofbiz.entity.util.EntityFindOptions;
 import org.ofbiz.service.ServiceUtil;
 import org.ofbiz.base.util.UtilDateTime;
 
 import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
 import in.vasista.vbiz.procurement.ProcurementReports;
 import in.vasista.vbiz.procurement.ProcurementNetworkServices;
 import in.vasista.vbiz.procurement.ProcurementServices;
 import in.vasista.vbiz.procurement.PriceServices;
 
 import java.util.List;
 import org.ofbiz.entity.GenericValue;
 import org.ofbiz.base.util.UtilDateTime;
 
 import org.ofbiz.entity.condition.*;
 import org.ofbiz.base.util.*;
 
 import javolution.util.FastList;
 
 import org.ofbiz.base.util.Debug;
 import org.ofbiz.base.util.UtilMisc;
 import org.ofbiz.entity.GenericValue;
 import org.ofbiz.entity.condition.EntityCondition;
 import org.ofbiz.entity.condition.EntityOperator;
 import org.ofbiz.service.ServiceUtil;
 import org.ofbiz.entity.util.EntityUtil;
 import net.sf.json.JSONObject;
 import net.sf.json.JSONArray;
 import in.vasista.vbiz.procurement.ProcurementNetworkServices;
 String flag = flag;
 
 // shifts for Milk Receipts
 allShiftsList = delegator.findList("WorkShiftTypePeriodAndMap",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILK_SHIFT"),null,UtilMisc.toList("shiftTypeId"),null,false);
 if(UtilValidate.isNotEmpty(allShiftsList)){
	 context.allShiftsList=allShiftsList;
 }
 
 shiftType= null;
 shiftDate = null;
 shiftDateTime=null;
 shiftDateTimeStart=null;
 shiftDateTimeEnd=null;
 
 String thruDate = null;
 String hideSearch = parameters.hideSearch;
 nonMilkDetailslist=[];
 weighmentDetailsList=[];
 if(UtilValidate.isNotEmpty(parameters.shiftId)){
	 shiftType = parameters.shiftId;
	 context.shiftId=shiftType;
 }
 hideSearch = "N";
 if(UtilValidate.isNotEmpty(hideSearch) && (hideSearch.equalsIgnoreCase("N"))){
	 List conditionList=FastList.newInstance();
	 if(UtilValidate.isNotEmpty(parameters.partyId) && UtilValidate.isNotEmpty(flag) && flag == "Incoming"){
		 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
		 context.partyId=parameters.partyId;
	 }
	 if(UtilValidate.isNotEmpty(parameters.partyId) && UtilValidate.isNotEmpty(flag) && flag == "OutGoing"){
		 conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , parameters.partyId));
		 context.partyId=parameters.partyId;
	 }
	 if(UtilValidate.isNotEmpty(parameters.tankerName)){
		 conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , parameters.tankerName));
		 context.vehicleId=parameters.tankerName;
	 }
	 if(UtilValidate.isNotEmpty(parameters.weighmentId)){
		 conditionList.add(EntityCondition.makeCondition("weighmentId", EntityOperator.EQUALS , parameters.weighmentId));
		 context.weighmentId=parameters.weighmentId;
	 }
	 if(UtilValidate.isNotEmpty(parameters.productId)){
		 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , parameters.productId));
		 context.productId=parameters.productId;
	 }
	 if(UtilValidate.isNotEmpty(flag) && flag=="Incoming"){
	 conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
	 }
	 if(UtilValidate.isNotEmpty(flag) && flag=="OutGoing"){
		 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "MD"));
	 }
	 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	 vehicleTripStatusViewList = delegator.findList("WeighmentDetailsAndItemAndVehicleTripStatus",condition,null,UtilMisc.toList("-estimatedStartDate"),null,false);
	 
	 if(UtilValidate.isNotEmpty(flag) && flag=="Incoming"){
		 weighmentDetailsList = EntityUtil.filterByCondition(vehicleTripStatusViewList, EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedEndDate", EntityOperator.EQUALS , null ),EntityOperator.OR,EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "WMNT_VCL_OUT" )));
	 }
	 if(UtilValidate.isNotEmpty(flag) && flag=="OutGoing"){
		 weighmentDetailsList = EntityUtil.filterByCondition(vehicleTripStatusViewList, EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedEndDate", EntityOperator.EQUALS , null ),EntityOperator.OR,EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "WMNT_ISSUE_VCL_OUT" )));
	 }
 }
 sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 String toDate=null;
 shiftDate = parameters.fromDate;
 if(UtilValidate.isEmpty(shiftDate)){
	  shiftDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	  shiftDateTime = UtilDateTime.toDateString(shiftDate, "yyyy-MM-dd");
 }else{
	  context.shiftDate=shiftDate;
	 sdf = new SimpleDateFormat("dd-MM-yyyy");
	 shiftDate = new java.sql.Timestamp(sdf.parse(shiftDate).getTime());
	 shiftDateTime = UtilDateTime.toDateString(shiftDate,"yyyy-MM-dd");
 }
 
   nextDay = UtilDateTime.getNextDayStart(shiftDate);
   nextDateTime = UtilDateTime.toDateString(nextDay,"yyyy-MM-dd");
 
 
 def getShiftWiseRecords(Timestamp shiftDateTimeStart,Timestamp shiftDateTimeEnd){
	   List condList=FastList.newInstance();
	   if(UtilValidate.isNotEmpty(flag) && flag=="Incoming"){
		   condList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, shiftDateTimeStart));
		   condList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO, shiftDateTimeEnd));
	   }
	   if(UtilValidate.isNotEmpty(flag) && flag=="OutGoing"){
		   condList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, shiftDateTimeStart));
		   condList.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.LESS_THAN_EQUAL_TO, shiftDateTimeEnd));
	   }
	   cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	   shiftList = EntityUtil.filterByCondition(weighmentDetailsList, cond);
	   shiftList.each{eachShift->
		   shiftsMap=[:];
		   shiftsMap.put("weighmentId",eachShift.weighmentId);
		   shiftsMap.putAt("itemSequenceNo", eachShift.itemSequenceNo);
		   shiftsMap.put("partyId",eachShift.partyId);
		   shiftsMap.put("partyIdTo",eachShift.partyIdTo);
		   shiftsMap.put("productId",eachShift.productId);
		   shiftsMap.put("dispatchWeight",eachShift.dispatchWeight);
		   shiftsMap.put("grossWeight",eachShift.grossWeight);
		   shiftsMap.put("tareWeight",eachShift.tareWeight);
		   shiftsMap.put("quantity",eachShift.quantity);
		   shiftsMap.put("vehicleId",eachShift.vehicleId);
		   shiftsMap.put("statusId",eachShift.statusId);
		   shiftsMap.put("sequenceNum",eachShift.sequenceNum);
		   shiftsMap.put("dcNo", eachShift.dcNo);
		   
		   List eachCondList = FastList.newInstance();
		   eachCondList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , eachShift.vehicleId ));
		   eachCondList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS , eachShift.sequenceNum ));
		   if(UtilValidate.isNotEmpty(flag) && flag=="Incoming"){
			   eachCondList.add(EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "WMNT_VCL_IN" ));
		   }
		   if(UtilValidate.isNotEmpty(flag) && flag=="OutGoing"){
			  eachCondList.add(EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "WMNT_ISSUE_VCL_INIT" ));
		   }
		   
		   EntityCondition eachCond = EntityCondition.makeCondition(eachCondList,EntityOperator.AND);
		   vehicleEntryProductList = EntityUtil.filterByCondition(vehicleTripStatusViewList, eachCond);
		   if(vehicleEntryProductList){
			   vehicleEntryProduct = EntityUtil.getFirst(vehicleEntryProductList);
			   estimatedStartDate = vehicleEntryProduct.estimatedStartDate;
			   String vehicleEntryDate=null;
			   String receiveDate=null;
			   if(UtilValidate.isNotEmpty(estimatedStartDate)){
				   vehicleEntryDate = UtilDateTime.toDateString(estimatedStartDate,"dd-MM-yyyy HH:mm");
			   }
			   if(UtilValidate.isNotEmpty(vehicleEntryProduct.receiveDate)){
				   receiveDate = UtilDateTime.toDateString(vehicleEntryProduct.receiveDate,"dd-MM-yyyy HH:mm");
			   }
			   shiftsMap.put("vehicleEntryDate",vehicleEntryDate);
			   shiftsMap.put("receiveDate",receiveDate);
		   }
		   nonMilkDetailslist.add(shiftsMap);
	   }
 }
 
 if(UtilValidate.isNotEmpty(hideSearch) && (hideSearch.equalsIgnoreCase("N"))){
	 if(UtilValidate.isEmpty(shiftType)){
		 allShiftsList.each{eachShift->
			 eachShiftType = EntityUtil.filterByCondition(allShiftsList, EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, eachShift.shiftTypeId));
			 if(UtilValidate.isNotEmpty(eachShiftType)){
				 eachShiftType = EntityUtil.getFirst(eachShiftType);
				 startTime=eachShiftType.startTime;
				 endTime=eachShiftType.endTime;
				 if(startTime>endTime){
					 shiftTimeStart = shiftDateTime +" "+startTime;
					 shiftTimeEnd = nextDateTime +" "+endTime
				 }else{
				 shiftTimeStart = shiftDateTime +" "+startTime;
				 shiftTimeEnd = shiftDateTime +" "+endTime;
				 }
				 shiftDateTimeStart= new java.sql.Timestamp(sdf1.parse(shiftTimeStart).getTime());
				 shiftDateTimeEnd = new java.sql.Timestamp(sdf1.parse(shiftTimeEnd).getTime());
			 
				 getShiftWiseRecords(shiftDateTimeStart,shiftDateTimeEnd);
			 }
		 }
		 
	 }else{
		 eachShiftType = EntityUtil.filterByCondition(allShiftsList, EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, shiftType));
		 if(UtilValidate.isNotEmpty(eachShiftType)){
			 eachShiftType = EntityUtil.getFirst(eachShiftType);
			 startTime=eachShiftType.startTime;
			 endTime=eachShiftType.endTime;
			 if(startTime>endTime){
				 shiftTimeStart = shiftDateTime +" "+startTime;
				 shiftTimeEnd = nextDateTime +" "+endTime
			 }else{
				 shiftTimeStart = shiftDateTime +" "+startTime;
				 shiftTimeEnd = shiftDateTime +" "+endTime;
			 }
			 shiftDateTimeStart= new java.sql.Timestamp(sdf1.parse(shiftTimeStart).getTime());
			 shiftDateTimeEnd = new java.sql.Timestamp(sdf1.parse(shiftTimeEnd).getTime());
			 
			 getShiftWiseRecords(shiftDateTimeStart,shiftDateTimeEnd);
		 
		 }
	 }
 }
 context.nonMilkDetailslist=nonMilkDetailslist;
 
 
 
 