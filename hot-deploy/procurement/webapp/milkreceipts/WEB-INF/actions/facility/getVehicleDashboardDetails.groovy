import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;

import org.ofbiz.entity.util.EntityUtil;
dctx = dispatcher.getDispatchContext();
//resultReturn = ServiceUtil.returnSuccess();
vehicleId=parameters.vehicleId;
sequenceNum=parameters.sequenceNum;


JSONArray vehicleStatusJSONList = new JSONArray();
JSONObject productObj = new JSONObject();


conditionList=[];
conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
conditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,sequenceNum));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> vehicleTripStatus = delegator.findList("MilkTransferAndItemVehicleTripStatus", condition, null, UtilMisc.toList("estimatedStartDate"), null, false);
if(UtilValidate.isNotEmpty(vehicleTripStatus)){
	vehicleProductDetails=EntityUtil.getFirst(vehicleTripStatus);
	productObj.put("productId",vehicleProductDetails.productId);
	productObj.put("partyId",vehicleProductDetails.partyId);
    vehicleTripStatus.each{eachVehicleStatus->
	   JSONObject newVehicleObj = new JSONObject();
	   String statusId =eachVehicleStatus.statusId;
	   String statusEntryDate =eachVehicleStatus.estimatedStartDate;
	   statusItem = delegator.findOne("StatusItem",["statusId":statusId],false);
	   if(statusItem){
		   currentStatusId=statusItem.get("description");
	   newVehicleObj.put("statusId", currentStatusId);
	   }
	   newVehicleObj.put("statusEntryDate", statusEntryDate);
	   if(UtilValidate.isNotEmpty(eachVehicleStatus.siloId)){
		   productObj.put("siloId", eachVehicleStatus.siloId);
	   }
	   if(UtilValidate.isNotEmpty(eachVehicleStatus.tareWeight)){
		   productObj.put("netWeight", grossWeight-tareWeight);
	   }
	   vehicleStatusJSONList.add(newVehicleObj);
	}
}
request.setAttribute("vehicleStatusJSONList", vehicleStatusJSONList);
request.setAttribute("productObj", productObj);

