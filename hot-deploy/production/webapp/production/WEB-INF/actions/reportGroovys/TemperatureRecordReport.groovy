import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import java.math.BigDecimal;

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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

if(UtilValidate.isNotEmpty(parameters.temperatureDate)){
	context.reportDate = parameters.temperatureDate;
}
if(UtilValidate.isNotEmpty(parameters.facilityId)){
	facilityId = parameters.facilityId;
	context.facilityId=facilityId;
}

SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
List conditionList = FastList.newInstance()

Map finalMap = FastMap.newInstance();
List facilityList = FastList.newInstance();
List shiftTypeIds = FastList.newInstance();
List workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap",EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"PROD_SHIFT"),null,null,null,false);
shiftTypeIds = EntityUtil.getFieldListFromEntityList(workShiftTypePeriodAndMap, "shiftTypeId", true);
context.shiftTypeIds=shiftTypeIds;
if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
	workShiftTypePeriodAndMap.each{workShift->
		shiftTypeId = workShift.shiftTypeId;
		recordDateStart = parameters.temperatureDate+" "+workShift.startTime;
		recordDateTimeStart = null;
		if(UtilValidate.isNotEmpty(recordDateStart)){
			try {
				recordDateTimeStart =new java.sql.Timestamp(format.parse(recordDateStart).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + e, "");
			}
		}
		recordDateEnd = parameters.temperatureDate+" "+workShift.endTime;
		recordDateTimeEnd = null;
		if(UtilValidate.isNotEmpty(recordDateEnd)){
			try {
				recordDateTimeEnd =new java.sql.Timestamp(format.parse(recordDateEnd).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + e, "");
			}
		}
		List siloIds=FastList.newInstance();
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("recordDateTime",EntityOperator.BETWEEN,UtilMisc.toList(recordDateTimeStart,recordDateTimeEnd)));
		if(shiftTypeId.indexOf("NIGHT")!=-1){
			dayEnd = UtilDateTime.getDayEnd(recordDateTimeStart);
			nextDayStart = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(dayEnd), 1));
			nextDay = UtilDateTime.toDateString(nextDayStart,"MMMM dd, yyyy")+" "+workShift.endTime;
			nextDayShiftEnd = null;
			if(UtilValidate.isNotEmpty(nextDay)){
				try {
					nextDayShiftEnd =new java.sql.Timestamp(format.parse(nextDay).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + e, "");
				}
			}
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("recordDateTime",EntityOperator.BETWEEN,UtilMisc.toList(recordDateTimeStart,dayEnd)), EntityOperator.OR,
															EntityCondition.makeCondition("recordDateTime",EntityOperator.BETWEEN,UtilMisc.toList(nextDayStart,nextDayShiftEnd))));
			
		}
		if(UtilValidate.isNotEmpty(facilityId)){
			condList =[];
			condList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS , facilityId));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			siloList = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, false);
			siloIds = EntityUtil.getFieldListFromEntityList(siloList, "facilityId", false);
			if(UtilValidate.isNotEmpty(siloIds)){
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, siloIds));
			}
				
		}
		EntityCondition condition =  EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List facilityTemperature = delegator.findList("FacilityTemperature",condition,UtilMisc.toSet("facilityId","recordDateTime","temperature","comments"),UtilMisc.toList("recordDateTime"),null,false);
		facilityTemperature.each{facility->
			if(!facilityList.contains(facility.facilityId)){
				facilityList.add(facility.facilityId);
			}
		}
		if(UtilValidate.isNotEmpty(facilityTemperature)){
		finalMap[shiftTypeId]=facilityTemperature;
		}
	}
}
context.facilityList=facilityList;
context.finalMap=finalMap;







