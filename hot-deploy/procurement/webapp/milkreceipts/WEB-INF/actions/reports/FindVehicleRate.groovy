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
BigDecimal rateAmount = BigDecimal.ZERO;
if(UtilValidate.isNotEmpty(parameters.rateAmount)){
	rateAmount = new BigDecimal(parameters.rateAmount);
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
if(UtilValidate.isNotEmpty(parameters.rateAmount)){
	conditionList.add(EntityCondition.makeCondition("rateAmount",EntityOperator.EQUALS,rateAmount));
}

EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List vehicleRateList = delegator.findList("VehicleRate",condition,null,null,null,false);
List vehicleIds = EntityUtil.getFieldListFromEntityList(vehicleRateList, "vehicleId",  true);
List finalList = FastList.newInstance();
ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("vehicleId",EntityOperator.IN,vehicleIds),
	                                 EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE")],EntityOperator.AND);
List vehicleRoleList = delegator.findList("VehicleRole",ecl,null,null,null,false);
vehicleRateList.each{vehicleRate->
	tempMap=[:];
	List vehiclePartyList = EntityUtil.filterByCondition(vehicleRoleList, EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleRate.vehicleId));
	if(vehiclePartyList){
		vehicleParty = EntityUtil.getFirst(vehiclePartyList);
	}	
	tempMap.vehicleId = vehicleRate.vehicleId;
	tempMap.fromDate = vehicleRate.fromDate;
	tempMap.thruDate = vehicleRate.thruDate;
	tempMap.rateAmount = vehicleRate.rateAmount;
	tempMap.partyId = vehicleParty.partyId;
	if(UtilValidate.isNotEmpty(parameters.partyId) && vehicleParty.partyId == parameters.partyId){
		finalList.add(tempMap);
	}else if(UtilValidate.isEmpty(parameters.partyId)){
	  finalList.add(tempMap);
	}
}
context.vehicleRateList=finalList;
