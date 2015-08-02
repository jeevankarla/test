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

import in.vasista.vbiz.procurement.ProcurementNetworkServices;

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
milkDetailslist=[];
milkTransferDetailsList=[];
if(UtilValidate.isNotEmpty(parameters.shiftId)){
	shiftType = parameters.shiftId;
	context.shiftId=shiftType;
}
hideSearch = "N";
if(UtilValidate.isNotEmpty(hideSearch) && (hideSearch.equalsIgnoreCase("N"))){
	List conditionList=FastList.newInstance();
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
		context.partyId=parameters.partyId;
	}
	if(UtilValidate.isNotEmpty(parameters.siloId)){
		conditionList.add(EntityCondition.makeCondition("siloId", EntityOperator.EQUALS , parameters.siloId));
		context.siloId=parameters.siloId;
	}
	if(UtilValidate.isNotEmpty(parameters.vehicleId)){
		conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , parameters.vehicleId));
		context.vehicleId=parameters.vehicleId;
	}
	if(UtilValidate.isNotEmpty(parameters.milkTransferId)){
		conditionList.add(EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS , parameters.milkTransferId));
		context.milkTransferId=parameters.milkTransferId;
	}
	if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="APPROVE_RECEIPTS"){
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	}
	if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="FINALIZATION"){
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_APPROVED"));
	}
	if(UtilValidate.isNotEmpty(parameters.productId)){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , parameters.productId));
		context.productId=parameters.productId;
	}
	if(UtilValidate.isNotEmpty(parameters.createdByUserLogin)){
		conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS ,parameters.createdByUserLogin));
		context.createdByUserLogin=parameters.createdByUserLogin;
	}
	//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS , "Y"),EntityOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS ,"N")));
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	vehicleTripStatusViewList = delegator.findList("MilkTransferAndItemVehicleTripStatus",condition,null,UtilMisc.toList("-estimatedStartDate"),null,false);
	
    milkTransferDetailsList = EntityUtil.filterByCondition(vehicleTripStatusViewList, EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedEndDate", EntityOperator.EQUALS , null ),EntityOperator.OR,EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "MR_VEHICLE_OUT" )));
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
	  condList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, shiftDateTimeStart));
	  condList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO, shiftDateTimeEnd));
	  cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	  shiftList = EntityUtil.filterByCondition(milkTransferDetailsList, cond);
	  shiftList.each{eachShift->
		  shiftsMap=[:];
		  shiftsMap.put("milkTransferId",eachShift.milkTransferId);
		  shiftsMap.put("partyId",eachShift.partyId);
		  shiftsMap.put("partyIdTo",eachShift.partyIdTo);
		  shiftsMap.put("productId",eachShift.productId);
		  shiftsMap.put("siloId",eachShift.siloId);
		  shiftsMap.put("receivedQuantity",eachShift.receivedQuantity);
		  shiftsMap.put("receivedQuantityLtrs",eachShift.receivedQuantityLtrs);
		  shiftsMap.put("receivedFat",eachShift.receivedFat);
		  shiftsMap.put("receivedSnf",eachShift.receivedSnf);
		  shiftsMap.put("receivedKgFat",eachShift.receivedKgFat);
		  shiftsMap.put("receivedKgSnf",eachShift.receivedKgSnf);
		  shiftsMap.put("containerId",eachShift.vehicleId);
		  shiftsMap.put("statusId",eachShift.statusId);
		  shiftsMap.put("purposeTypeId",eachShift.purposeTypeId);
		  shiftsMap.put("sequenceNum",eachShift.sequenceNum);
		  
		  List eachCondList = FastList.newInstance();
		  eachCondList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , eachShift.vehicleId ));
		  eachCondList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS , eachShift.sequenceNum ));
		  eachCondList.add(EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "MR_VEHICLE_IN" ));
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
		  
		  List contrcatCondList = FastList.newInstance();
		  contrcatCondList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , eachShift.vehicleId ));
		  contrcatCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "PTC_VEHICLE"));
		  contrcatCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, shiftDate));
		  contrcatCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, shiftDate)));
		  EntityCondition contrcatCond = EntityCondition.makeCondition(contrcatCondList,EntityOperator.AND);
		  vehicleRole = delegator.findList("VehicleRole",contrcatCond, null, null, null, true);
		  if(UtilValidate.isNotEmpty(vehicleRole)){
			  vehicleRole = EntityUtil.getFirst(vehicleRole);
			  if(UtilValidate.isNotEmpty(vehicleRole.partyId)){
				  shiftsMap.put("contractorId",vehicleRole.partyId);
			  }
		  }
		  milkDetailslist.add(shiftsMap);
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
context.milkDetailslist=milkDetailslist;
