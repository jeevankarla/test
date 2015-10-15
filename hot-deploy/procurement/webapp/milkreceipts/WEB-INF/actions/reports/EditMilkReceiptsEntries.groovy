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
 import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;
 
 dctx = dispatcher.getDispatchContext();
 
 
 //List milkDetailslist = FastList.newInstance();
 //List milkTransferDetailsList = FastList.newInstance();
 
 
 fromDate = null;
 thruDate = null;

fromDateTime = null;
thruDateTime = null;
if(UtilValidate.isNotEmpty(parameters.fromDate)){
	context.entryDate = parameters.fromDate;
	def sdf = new SimpleDateFormat("dd-MM-yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime());
		thruDateTime = new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");
	}
	
	dayBegin = UtilDateTime.getDayStart(fromDateTime);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime);
	
	Map inMap = FastMap.newInstance();
	inMap.put("userLogin", userLogin);
	inMap.put("shiftType", "MILK_SHIFT");
	inMap.put("fromDate", dayBegin);
	inMap.put("thruDate", dayEnd);
	Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );
	
	fromDate=workShifts.fromDate;
	thruDate=workShifts.thruDate;
	
	context.fromDate = fromDate;
	context.thruDate = dayEnd;
}
 List conditionList=FastList.newInstance();
 if(UtilValidate.isNotEmpty(parameters.fromDate)){
	 conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	 conditionList.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
 }
 if(UtilValidate.isNotEmpty(parameters.milkTransferId)){
	 conditionList.add(EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS , parameters.milkTransferId));
	 context.milkTransferId=parameters.milkTransferId;
 } 
 if(UtilValidate.isNotEmpty(parameters.partyId)){
	 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
	 context.partyId=parameters.partyId;
 }
 if(UtilValidate.isNotEmpty(parameters.vehicleId)){
	 conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , parameters.vehicleId));
	 context.vehicleId=parameters.vehicleId;
 }
//if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="EDITMILKRECEIPTS"){
	 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_INPROCESS","MXF_RECD"]));
//}
 conditionList.add(EntityCondition.makeCondition("vehicleTripStatusId", EntityOperator.EQUALS , "MR_VEHICLE_IN"));
	 
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
 milkDetailslist = delegator.findList("MilkTransferAndItemVehicleTripStatus",condition,null,UtilMisc.toList("-estimatedStartDate"),null,false);
 
 context.milkDetailslist=milkDetailslist;
 if(UtilValidate.isNotEmpty(parameters.milkTransferId)){
	 GenericValue milkTransferEditDetails = EntityUtil.getFirst(milkDetailslist);
	 receiveDate = milkTransferEditDetails.getTimestamp("receiveDate");
	 statusDate=null;
	 if(receiveDate){
		 statusDate = UtilDateTime.toDateString(receiveDate,"dd-MM-yyyy HH:mm");
		  }
	 milkTransferEditDetails.set("receiveDate", statusDate);
	
	 dispatchDate = milkTransferEditDetails.getTimestamp("sendDate");
	 sendDate=null;
	 if(dispatchDate){
		 sendDate = UtilDateTime.toDateString(dispatchDate,"dd-MM-yyyy HH:mm");
		  }
	 milkTransferEditDetails.set("sendDate", sendDate);

	 context.milkTransferEditDetails=milkTransferEditDetails;
	 
 }
