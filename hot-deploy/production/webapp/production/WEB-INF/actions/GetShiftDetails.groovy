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



List condList = FastList.newInstance();
Timestamp effectiveDate = UtilDateTime.nowTimestamp();
dctx = dispatcher.getDispatchContext();


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
	currentShift = "1";
}else if(effectiveDate>=shift2Time &&  effectiveDate<shift3Time){
	currentShift = "2";
}else{
	currentShift = "3";
	if(effectiveDate<shift1Time){
		String shiftDateStr = UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(effectiveDate,-1),"dd-MMM-yyyy");
		currentShift = "3 of "+shiftDateStr;
	}
}
context.putAt("shift", currentShift);
context.putAt("todayDate",UtilDateTime.toDateString(effectiveDate,"dd-MMM-yyyy"));
context.putAt("currentTime", UtilDateTime.toDateString(effectiveDate,"HH:mm"));
