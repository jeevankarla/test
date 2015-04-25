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

shiftType = parameters.shiftType;
shiftDate = parameters.shiftDate;

if(UtilValidate.isEmpty(shiftDate)){
	Debug.logError("shiftDate Cannot Be Empty","");
	context.errorMessage = "shiftDate Cannot Be Empty";
	return;
}
sdf = new SimpleDateFormat("MMMM dd, yyyy");
shiftDate = new java.sql.Timestamp(sdf.parse(shiftDate).getTime());
shiftDateTime = UtilDateTime.toDateString(shiftDate,"yyyy-MM-dd");
nextDay = UtilDateTime.getNextDayStart(shiftDate);
nextDateTime = UtilDateTime.toDateString(nextDay,"yyyy-MM-dd");
sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
shiftDayTimeStart = shiftDateTime + " 05:00:00.000";
dayStart = new java.sql.Timestamp(sdf1.parse(shiftDayTimeStart).getTime());
shiftDayTimeEnd = nextDateTime + " 04:59:59.000";
dayEnd = new java.sql.Timestamp(sdf1.parse(shiftDayTimeEnd).getTime());
milkTransferDetailsList = [];
ShiftWiseMap = [:];
shiftTime = null;
conditionList =[];
conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "MD"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkTransferDetails = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,null,null,false);
sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
def setShiftDateTime(int i){
	try {
		if (shiftDateTime) {
		  if(i == 1){
			  shiftTimeStart = shiftDateTime + " 05:00:00.0";
			  shiftTimeEnd = shiftDateTime + " 12:59:59.0";
			  shiftTime = "0500 TO 1300";
		  }
		  else if(i==2){
			  shiftTimeStart = shiftDateTime + " 13:00:00.0";
			  shiftTimeEnd = shiftDateTime + " 20:59:59.0";
			  shiftTime = "1300 TO 2100";
		  }
		  else if(i==3){
			  shiftTimeStart = shiftDateTime + " 21:00:00.0";
			  shiftTimeEnd = nextDateTime + " 04:59:59.0";
			  shiftTime = "2100 TO 0500";
		  }
		  shiftDateTimeStart = new java.sql.Timestamp(sdf.parse(shiftTimeStart).getTime());
		  shiftDateTimeEnd = new java.sql.Timestamp(sdf1.parse(shiftTimeEnd).getTime());
		}
	  }catch (ParseException e) {
		  Debug.logError(e, "Cannot parse date string: " + e, "");
		  context.errorMessage = "Cannot parse date string: " + e;
	  }
}
def getShiftWiseRecords(int shiftType,Timestamp shiftDateTimeStart,Timestamp shiftDateTimeEnd){
	  conditionList.clear();
	  conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO, shiftDateTimeStart));
	  conditionList.add(EntityCondition.makeCondition("sendDate",EntityOperator.LESS_THAN_EQUAL_TO, shiftDateTimeEnd));
	  condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	  milkTransferDetailsList = EntityUtil.filterByCondition(milkTransferDetails, condition);
	  conditionList.clear();
	if(UtilValidate.isNotEmpty(milkTransferDetailsList)){
		ShiftWiseMap.put(shiftType, milkTransferDetailsList);
	}
}

if(shiftType.equalsIgnoreCase("all")){
	for(int i=1;i<=3;i++){
		setShiftDateTime(i);
		getShiftWiseRecords(i,shiftDateTimeStart,shiftDateTimeEnd);
	}
}else{
	int i = Integer.parseInt(shiftType);
	setShiftDateTime(i);
	getShiftWiseRecords(i,shiftDateTimeStart,shiftDateTimeEnd);
}
context.putAt("ShiftWiseMap", ShiftWiseMap);
context.putAt("shiftDate", shiftDate);
