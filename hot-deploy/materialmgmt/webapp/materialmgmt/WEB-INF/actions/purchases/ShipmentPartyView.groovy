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
import org.ofbiz.party.party.PartyHelper;

List finalList=[];
List conditionList=[];

//if(UtilValidate.isNotEmpty(parameters.noConditionFind) && parameters.noConditionFind=="Y"){
	/*if(UtilValidate.isNotEmpty(parameters.shipmentId)){
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,parameters.shipmentId));
	}
	
	if(UtilValidate.isNotEmpty(parameters.primaryOrderId)){
		conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,parameters.primaryOrderId));
	}
	if(UtilValidate.isNotEmpty(parameters.vehicleId)){
		conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,parameters.vehicleId));
	}
	if(UtilValidate.isNotEmpty(parameters.statusId)){
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,parameters.statusId));
	}
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS,"MATERIAL_SHIPMENT"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	shipmentList=delegator.findList("Shipment",condition,null,UtilMisc.toList("-estimatedShipDate"),null,false);*/
	shipmentList=result.listIt;
	partyName = "";
	shipmentList.each{shipment->
		tempMap=[:];
		tempMap.put("shipmentId",shipment.shipmentId);
		tempMap.put("estimatedShipDate",shipment.estimatedShipDate);
		tempMap.put("vehicleId",shipment.vehicleId);
		tempMap.put("partyIdTo",shipment.partyIdTo);
		
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,shipment.partyIdTo));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		facilityList=delegator.findList("Facility",condition,null,null,null,false);
		
		depoPartyId = "";
		if(UtilValidate.isNotEmpty(facilityList)){
			depoPartyId = facilityList[0].get("facilityId");
			depoPartyName=PartyHelper.getPartyName(delegator, depoPartyId, false);
			tempMap.put("depoPartyName",depoPartyName);
		}else{
		tempMap.put("depoPartyName","");
		}
		if(UtilValidate.isNotEmpty(shipment.partyIdTo)){
		 partyName=PartyHelper.getPartyName(delegator, shipment.partyIdTo, false);
		    tempMap.put("partyName",partyName);
		}
		else{
			tempMap.put("partyName","");
		}
		tempMap.put("statusId",shipment.statusId);
		exprCondList=[];
		exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId));
		exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
		OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
		
		if(UtilValidate.isNotEmpty(OrderAss)){
			salesOrder = OrderAss.get("toOrderId");
			tempMap.put("salesOrder",salesOrder);
		}else{
			tempMap.put("salesOrder","");
		}
		
		tempMap.put("primaryOrderId",shipment.primaryOrderId);
		if(shipment.partyIdFrom){
			tempMap.putAt("partyId", shipment.partyIdFrom);
			
			if(UtilValidate.isNotEmpty(shipment.primaryOrderId)){
				ecl = EntityCondition.makeCondition([
					EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER")],
				EntityOperator.AND);
				billToOrderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				billToOrderRole=EntityUtil.getFirst(billToOrderRoles);
				tempMap.put("billToPartyId",billToOrderRole.partyId);
			}
			
		}else{
			if(UtilValidate.isNotEmpty(shipment.primaryOrderId)){
				ecl = EntityCondition.makeCondition([
									   EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId),
									   EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_FROM_VENDOR")],
								   EntityOperator.AND);
				orderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				orderRole=EntityUtil.getFirst(orderRoles);
				tempMap.put("partyId",orderRole.partyId);
				
				ecl = EntityCondition.makeCondition([
					EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER")],
				EntityOperator.AND);
				billToOrderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				billToOrderRole=EntityUtil.getFirst(billToOrderRoles);
				tempMap.put("billToPartyId",billToOrderRole.partyId);
				
			}else{
				tempMap.putAt("partyId", null);
			}
		}
		if(shipment.partyIdTo){
			tempMap.putAt("weaver", shipment.partyIdTo);
		}else{
			if(UtilValidate.isNotEmpty(shipment.primaryOrderId)){
				ecl = EntityCondition.makeCondition([
									   EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, shipment.primaryOrderId),
									   EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER")],
								   EntityOperator.AND);
				orderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				orderRole=EntityUtil.getFirst(orderRoles);
				tempMap.put("weaver",orderRole.partyId);
			}else{
				tempMap.putAt("weaver", null);
			}
		}
		
		
		
		
		
		finalList.add(tempMap);
	}
	//}
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
				tempMap.put("partyIdTo",shipment.partyIdTo);
				
				conditionList = [];
				conditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,shipment.partyIdTo));
				condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				facilityList=delegator.findList("Facility",condition,null,null,null,false);
				
				depoPartyId = "";
				if(UtilValidate.isNotEmpty(facilityList)){
					depoPartyId = facilityList[0].get("facilityId");
					depoPartyName=PartyHelper.getPartyName(delegator, depoPartyId, false);
					tempMap.put("depoPartyName",depoPartyName);
				}else{
				tempMap.put("depoPartyName","");
				}
				if(UtilValidate.isNotEmpty(shipment.partyIdTo)){
					partyName=PartyHelper.getPartyName(delegator, shipment.partyIdTo, false);
					   tempMap.put("partyName",partyName);
				   }
				   else{
					   tempMap.put("partyName","");
				   }
				newFinalList.add(tempMap);
			}
		}
		context.listIt=newFinalList;
	}else{
		context.listIt=finalList;
	}
	