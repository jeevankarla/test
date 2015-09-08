import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


List<GenericValue> facilityDepartments = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "PLANT"), null,null,null,false);
if(UtilValidate.isNotEmpty(facilityDepartments)){
	 context.facilityDepartments=facilityDepartments;
 }

allShiftsList = delegator.findList("WorkShiftTypePeriodAndMap",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILK_SHIFT"),null,UtilMisc.toList("shiftTypeId"),null,false);
if(UtilValidate.isNotEmpty(allShiftsList)){
	context.allShiftsList=allShiftsList;
}
