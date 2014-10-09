
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
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
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.util.Map;


import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import com.sun.org.apache.bcel.internal.generic.RETURN;


import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.PriceServices;

dctx = dispatcher.getDispatchContext();
Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
Timestamp thruDate=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());


def sdf = new SimpleDateFormat("yyyy-MM-dd");
TimeZone timeZone = null;
locale = Locale.getDefault();
timeZone = TimeZone.getDefault();

String customTimeperiodId = parameters.customTimePeriodId;
if(UtilValidate.isEmpty(customTimeperiodId)){
	return;
	}else{
	customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId:customTimeperiodId],false);
	fromDate= UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
	thruDate=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	}

context.put("fromDateStr",  (String)UtilDateTime.toDateString(fromDate,"dd-MM-yyyy"));
context.put("thruDateStr",  (String)UtilDateTime.toDateString(thruDate,"dd-MM-yyyy"));
/*if(UtilValidate.isNotEmpty(parameters.fromDate)){
	fromDate= UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
		if(UtilValidate.isEmpty(parameters.thruDate)){
			parameters.thruDate= parameters.fromDate;
		}
	}else{
		return;
	}

if(UtilValidate.isNotEmpty(parameters.thruDate)){
	thruDate= UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}*/

List shedsList = FastList.newInstance();
if(UtilValidate.isEmpty(parameters.facilityId)){
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"SHED"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList);
		tempShedDetailsList = delegator.findList("Facility",condition,null,null,null,false);
		shedsList = EntityUtil.getFieldListFromEntityList(tempShedDetailsList,"facilityId",false);
			
	}else{
		shedsList.add(parameters.facilityId);
	}

	
int noOfDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate))+1
context.putAt("noOfDays", noOfDays);
Timestamp tempFromDate = fromDate;

List dayKeysList = FastList.newInstance();
for(int i=1; i<=noOfDays;i++ ){
	String dateKey = (String)UtilDateTime.toDateString(tempFromDate,"dd-MM");
	dayKeysList.add(dateKey);
	tempFromDate = UtilDateTime.addDaysToTimestamp(tempFromDate, 1);
	}
context.putAt("dayKeysList", dayKeysList);

Map shedWiseProcMap = FastMap.newInstance();
for(facilityId in shedsList){
	tempFromDate = fromDate;
	Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId));
	List facilityIds = FastList.newInstance();
	if(UtilValidate.isNotEmpty(facilityAgents)){
		facilityIds= (List) facilityAgents.get("facilityIds");
	}
	List conditionList= FastList.newInstance();
	//let filter out items which has value null fat,snf,unitPrice
	conditionList.add(EntityCondition.makeCondition("fat", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("snf", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("unitPrice", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<GenericValue> orderItems = null;
	orderItems = delegator.findList("OrderHeaderItemProductAndFacility", condition, null, null, null, false);
	Map dateMap = FastMap.newInstance();
	
	for(int i=1; i<=noOfDays;i++ ){
		String dateKey = (String)UtilDateTime.toDateString(tempFromDate,"dd-MM");
		BigDecimal quantity = BigDecimal.ZERO;
		int entries = 0;
		if(UtilValidate.isNotEmpty(orderItems)){
			List tempConditionList= UtilMisc.toList(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,tempFromDate));
	           	 tempConditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.getDayEnd(tempFromDate)));
			EntityCondition tempCondition = EntityCondition.makeCondition(tempConditionList,EntityOperator.AND);
			List dayOrderItems = EntityUtil.filterByCondition(orderItems,tempCondition);
			entries = dayOrderItems.size();
			for(orderItem in dayOrderItems){
					quantity = quantity.add(orderItem.quantityKgs);
				}
		}
		Map quantityMap = FastMap.newInstance();
		quantityMap.put("entries",entries);
		quantityMap.put("quantity",quantity);
		dateMap.put(dateKey, quantityMap);
		tempFromDate = UtilDateTime.addDaysToTimestamp(tempFromDate, 1);
		}
	shedWiseProcMap.put(facilityId,dateMap);
}
context.putAt("shedWiseProcMap",shedWiseProcMap);
Debug.log("shedWiseProcMap=========="+shedWiseProcMap);