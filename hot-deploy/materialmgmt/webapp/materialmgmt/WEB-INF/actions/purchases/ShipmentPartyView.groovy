import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
List finalList=[];
List conditionList=[];
if(UtilValidate.isNotEmpty(parameters.noConditionFind) && parameters.noConditionFind=="Y"){
	if(UtilValidate.isNotEmpty(parameters.shipmentId)){
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,parameters.shipmentId));
	}
	
	if(UtilValidate.isNotEmpty(parameters.primaryOrderId)){
		conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,parameters.primaryOrderId));
	}
	if(UtilValidate.isNotEmpty(parameters.vehicleId)){
		conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,parameters.vehicleId));
	}
	/*if(UtilValidate.isNotEmpty(parameters.vehicleId)){
		conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,parameters.vehicleId));
	}*/
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS,"MATERIAL_SHIPMENT"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	shipmentList=delegator.findList("Shipment",condition,null,UtilMisc.toList("-estimatedShipDate"),null,false);
	shipmentList.each{shipment->
		tempMap=[:];
		tempMap.put("shipmentId",shipment.shipmentId);
		tempMap.put("estimatedShipDate",shipment.estimatedShipDate);
		tempMap.put("vehicleId",shipment.vehicleId);
		tempMap.put("statusId",shipment.statusId);
		tempMap.put("primaryOrderId",shipment.primaryOrderId);
		if(shipment.partyIdFrom){
			tempMap.putAt("partyId", shipment.partyIdFrom);
		}else{
			if(UtilValidate.isNotEmpty(shipment.primaryOrderId)){
				ecl = EntityCondition.makeCondition([
									   EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId),
									   EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_FROM_VENDOR")],
								   EntityOperator.AND);
				orderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				orderRole=EntityUtil.getFirst(orderRoles);
				tempMap.put("partyId",orderRole.partyId);
			}else{
				tempMap.putAt("partyId", null);
			}
		}
		
		finalList.add(tempMap);
	}
	}
	newFinalList=[];
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		finalList.each{list->
			partyId=list.partyId
			if(partyId == parameters.partyId){
				tempMap=[:];
				tempMap.put("shipmentId",list.shipmentId);
				tempMap.put("estimatedShipDate",list.estimatedShipDate);
				tempMap.put("vehicleId",list.vehicleId);
				tempMap.put("statusId",list.statusId);
				tempMap.put("primaryOrderId",list.primaryOrderId);
				tempMap.put("partyId", partyId);
				newFinalList.add(tempMap);
			}
		}
		context.listIt=newFinalList;
	}else{
		context.listIt=finalList;
	}