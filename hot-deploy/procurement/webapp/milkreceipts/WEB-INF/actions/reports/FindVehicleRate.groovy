
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityListIterator;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.jar.Manifest.FastInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
dctx = dispatcher.getDispatchContext();
fromDate=null;
thruDate=null;
SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
if(parameters.fromDate){
	try {
		fromDate= new java.sql.Timestamp(formatter.parse(parameters.fromDate).getTime());
	} catch (ParseException e) {
	}
}
if(parameters.thruDate){
	try {
		fromDate= new java.sql.Timestamp(formatter.parse(parameters.thruDate).getTime());
	} catch (ParseException e) {
	}
}

List conditionList = FastList.newInstance();

if(UtilValidate.isNotEmpty(parameters.tankerNo)){
	conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,parameters.tankerNo));
}
if(UtilValidate.isNotEmpty(parameters.fromDate)){
	conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,fromDate));
}
if(UtilValidate.isNotEmpty(parameters.thruDate)){
	conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,thruDate));
}
if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,parameters.partyId));
}
conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

List finalList = FastList.newInstance();

List vehicleRoleList = delegator.findList("VehicleRole",condition,null,null,null,false);
List vehicleIds = EntityUtil.getFieldListFromEntityList(vehicleRoleList, "vehicleId", true);
List vehicles = delegator.findList("Vehicle",EntityCondition.makeCondition("vehicleId",EntityOperator.IN,vehicleIds),UtilMisc.toSet("vehicleId","vehicleCapacity"),null,null,false);
vehicleRoleList.each{vehicleRole->
	tempMap=[:];
	List vehicleList = EntityUtil.filterByCondition(vehicles, EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleRole.vehicleId));
	vehicle = EntityUtil.getFirst(vehicleList);
	tempMap.vehicleId = vehicleRole.vehicleId;
	tempMap.partyId = vehicleRole.partyId;
	tempMap.fromDate = vehicleRole.fromDate;
	tempMap.thruDate = vehicleRole.thruDate;
	BigDecimal vehicleCapacity = BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(vehicle.vehicleCapacity)){
		vehicleCapacity = new BigDecimal(vehicle.vehicleCapacity);
	}
	tempMap.vehicleCapacity = vehicleCapacity; 
	if(UtilValidate.isNotEmpty(parameters.vehicleCapacity) && (vehicleCapacity == new BigDecimal(parameters.vehicleCapacity))){
		finalList.add(tempMap);
	}else if(UtilValidate.isEmpty(parameters.vehicleCapacity)){
		finalList.add(tempMap);
	}
}

context.vehicleRateList=finalList;
