import java.sql.*
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


List shiftSiloDetailsList  = FastList.newInstance();
List silosList = FastList.newInstance();

List condList = FastList.newInstance();
Timestamp effectiveDate = UtilDateTime.nowTimestamp();
dctx = dispatcher.getDispatchContext();

condList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"SILO"));
condList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.EQUALS,"POWDER"));
EntityCondition condition = EntityCondition.makeCondition(condList);
silosList = delegator.findList("Facility",condition,null,null,null,false);

if(UtilValidate.isEmpty(silosList)){
	context.errorMessage = "Powder Silos are not configured";
	return;
}
Map inMap = FastMap.newInstance();
inMap.put("userLogin", userLogin);
inMap.put("effectiveDate", effectiveDate);
for(silo in silosList){
	String facilityId = silo.get("facilityId");
	inMap.put("facilityId", facilityId);
	Map shiftSiloDetailsMap = FastMap.newInstance();
	
	Map siloOpeningBalance = dispatcher.runSync("getSiloInventoryOpeningBalance",inMap);
	
	if(UtilValidate.isNotEmpty(siloOpeningBalance) && UtilValidate.isNotEmpty(siloOpeningBalance.get("openingBalance"))){
		shiftSiloDetailsMap.putAll(siloOpeningBalance.get("openingBalance"))
		shiftSiloDetailsMap.put("siloId",facilityId);
		shiftSiloDetailsList.add(shiftSiloDetailsMap);
	}
	
}
String shift1TimeStart = " 05:00";
String shift2TimeStart = " 13:00";
String shift3TimeStart = " 21:00";
SimpleDateFormat   sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
String todayDate = UtilDateTime.toDateString(effectiveDate,"yyyy-MM-dd");

String shift1DateStr = todayDate.concat(shift1TimeStart);
String shift2DateStr = todayDate.concat(shift2TimeStart);
String shift3DateStr = todayDate.concat(shift3TimeStart);

Timestamp shift1Time =  new java.sql.Timestamp(sdf.parse(shift1DateStr).getTime());
Timestamp shift2Time =  new java.sql.Timestamp(sdf.parse(shift2DateStr).getTime());
Timestamp shift3Time =  new java.sql.Timestamp(sdf.parse(shift3DateStr).getTime());

String currentShift = "";
if(effectiveDate>=shift1Time &&  effectiveDate<shift2Time){
	currentShift = "Shift : 1";
}else if(effectiveDate>=shift2Time &&  effectiveDate<shift3Time){
	currentShift = "Shift : 2";
}else{
	currentShift = "Shift :3";
	if(effectiveDate<shift1Time){
		String shiftDateStr = UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(effectiveDate,-1),"dd-MMM-yyyy");
		currentShift = "Shift :3 of "+shiftDateStr;
	}
}
context.putAt("shift", currentShift);
context.putAt("todayDate",UtilDateTime.toDateString(effectiveDate,"dd-MMM-yyyy"));
context.putAt("currentTime", UtilDateTime.toDateString(effectiveDate,"HH:mm"));
context.putAt("shiftSiloDetailsList", shiftSiloDetailsList);