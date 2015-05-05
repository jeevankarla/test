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


String hideSearch = "Y";
hideSearch = parameters.hideSearch ;
List listTripStatus = FastList.newInstance();
if(UtilValidate.isNotEmpty(hideSearch) && hideSearch.equalsIgnoreCase("N")){

	shiftDate = parameters.shiftDate;
	
	if(UtilValidate.isEmpty(shiftDate)){
		Debug.logError("shiftDate Cannot Be Empty","");
		context.errorMessage = "shiftDate Cannot Be Empty";
		return;
	}
	sdf = new SimpleDateFormat("yyyy-MM-dd");
	shiftDate = new java.sql.Timestamp(sdf.parse(shiftDate).getTime());
	shiftDateTime = UtilDateTime.toDateString(shiftDate,"yyyy-MM-dd");
	nextDay = UtilDateTime.getNextDayStart(shiftDate);
	nextDateTime = UtilDateTime.toDateString(nextDay,"yyyy-MM-dd");
	sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	shiftDayTimeStart = shiftDateTime + " 05:00:00.000";
	shiftDateStart = new java.sql.Timestamp(sdf1.parse(shiftDayTimeStart).getTime());
	shiftDayTimeEnd = nextDateTime + " 04:59:59.000";
	shiftDateEnd = new java.sql.Timestamp(sdf1.parse(shiftDayTimeEnd).getTime());
	/*shiftDateStart = UtilDateTime.getDayStart(shiftDate);
	shiftDateEnd = UtilDateTime.getDayEnd(shiftDate);
	*/
	ShiftWiseMap = [:];
	ShiftWiseTimeMap = [:];
	shiftTime = null;
	List stausList = FastList.newInstance();
	List conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "MR_VEHICLE_STATUS" ));
	EntityCondition condition = EntityCondition.makeCondition(conditionList);
	stausList = delegator.findList("StatusItem",condition,null,null,null,false);
	
	List<String> statusIdsList = EntityUtil.getFieldListFromEntityList(stausList, "statusId", false);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO , shiftDateStart ));
	conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO , shiftDateEnd ));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , statusIdsList ));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedEndDate", EntityOperator.EQUALS , null ),EntityOperator.OR,EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MR_VEHICLE_OUT" )));
	
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List vehicleStatusList = delegator.findList("VehicleTripStatus",condition,null,UtilMisc.toList("-estimatedStartDate"),null,false);
	
	if(UtilValidate.isNotEmpty(vehicleStatusList)){
		for(vehicleStatus in vehicleStatusList){
			String vehicleId = vehicleStatus.getAt("vehicleId");
			String sequenceNum = vehicleStatus.getAt("sequenceNum");
			String statusId = vehicleStatus.getAt("statusId");
			String userLoginId = vehicleStatus.getAt("lastModifiedByUserLogin");
			Map vehicleTripMap = FastMap.newInstance();
			conditionList.clear();
			
			GenericValue vehicleTrip = delegator.findOne("VehicleTrip", UtilMisc.toMap("vehicleId" ,vehicleId,"sequenceNum",sequenceNum), true);
			if(UtilValidate.isNotEmpty(vehicleTrip) ){
				String partyId = vehicleTrip.get("partyId");
				vehicleTripMap.put("vehicleId",vehicleId);
				vehicleTripMap.put("partyId",partyId);
				vehicleTripMap.put("statusId",statusId);
				vehicleTripMap.put("userLogin",userLoginId);
				listTripStatus.add(vehicleTripMap);
			
			}
		}
	}
		
}
context.putAt("listTripStatus", listTripStatus);

