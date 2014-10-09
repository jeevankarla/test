import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
dctx = dispatcher.getDispatchContext();
	
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}

fromDateStart = null;
thruDateEnd = null;
if(UtilValidate.isNotEmpty(parameters.fromDate) && UtilValidate.isNotEmpty(parameters.thruDate)){
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		if (parameters.fromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
		}
		if (parameters.thruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
		}
	
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	fromDateStart = UtilDateTime.getDayStart(fromDate);
	thruDateEnd = UtilDateTime.getDayEnd(thruDate);
}

if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	
	fromDateStart = UtilDateTime.getDayStart(fromDate);
	thruDateEnd = UtilDateTime.getDayEnd(thruDate);
}
context.put("fromDate", fromDateStart);
context.put("thruDate", thruDateEnd);

if(UtilValidate.isEmpty(fromDateStart) || UtilValidate.isEmpty(thruDateEnd)){
	Debug.logError("FromDate or ThruDate Cannot Be Empty","");
	context.errorMessage = "FromDate or ThruDate Cannot Be Empty.......!";
	return;
}
unitsList = [];
shedId = parameters.shedId;


if(UtilValidate.isEmpty(parameters.unitId)){
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: shedId]);
	unitsList.addAll(shedUnitDetails.get("unitsList"));
}  
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId) && UtilValidate.isEmpty(parameters.unitId)){
	unitsList.clear();
	unitsListDetails = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx ,[userLogin: userLogin,customTimePeriodId : parameters.customTimePeriodId,shedId: shedId]);
	unitsList = unitsListDetails.unitsList;
}
if(UtilValidate.isNotEmpty(parameters.unitId)){
	unitsList.clear();
	unitsList.add(parameters.unitId);
}
 unitMap = [:];
 unitWiseProductTotalMap = [:];
 unitsList.each{ unit->
	productTotMap = [:];
	conditionList =[];
	milkDetailslist =[];
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , unit));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart), 
	EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd)],EntityOperator.AND));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,UtilMisc.toList("receiveDate","ackTime","vehicleId","-cellType"),null,false);
	if(UtilValidate.isNotEmpty(milkDetailslist)){
		unitMap.put(unit, milkDetailslist);
	}
	milkDetailslist.each{ milkDetail->
		productId=milkDetail.get("receivedProductId");
		if(UtilValidate.isEmpty(productTotMap.get(productId))){
			Map finalProductMap = FastMap.newInstance();
			finalProductMap["receivedQuantityLtrs"] = milkDetail.receivedQuantityLtrs;
			finalProductMap["receivedQuantity"] = milkDetail.receivedQuantity;
			finalProductMap["receivedKgFat"] = milkDetail.receivedKgFat;
			finalProductMap["receivedKgSnf"] = milkDetail.receivedKgSnf;
			productTotMap.put(productId, finalProductMap);
		}else{
			Map tempMap=FastMap.newInstance();
			tempMap.putAll(productTotMap.get(productId));
			tempMap.put("receivedQuantityLtrs", tempMap.get("receivedQuantityLtrs")+ milkDetail.receivedQuantityLtrs);
			tempMap.put("receivedQuantity", tempMap.get("receivedQuantity")+ milkDetail.receivedQuantity);
			tempMap.put("receivedKgFat", tempMap.get("receivedKgFat")+ milkDetail.receivedKgFat);
			tempMap.put("receivedKgSnf", tempMap.get("receivedKgSnf")+ milkDetail.receivedKgSnf);
			if(UtilValidate.isNotEmpty(tempMap)){
				productTotMap.put(productId, tempMap);
			}
		}
	}
	if(UtilValidate.isNotEmpty(productTotMap)){
		unitWiseProductTotalMap.put(unit, productTotMap);
	}
 }
context.put("unitWiseProductTotalMap",unitWiseProductTotalMap);
context.put("unitMap",unitMap);
