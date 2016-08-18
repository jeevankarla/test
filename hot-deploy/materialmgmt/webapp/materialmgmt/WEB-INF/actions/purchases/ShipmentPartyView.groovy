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
	shipmentList = [];
	if(UtilValidate.isNotEmpty(result.listIt)){
	    shipmentList=result.listIt.getCompleteList();
	}
	if(UtilValidate.isNotEmpty(parameters.orderNo)){
		draftOrderNo = parameters.orderNo;
		draftOrderIdDetails = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderNo", EntityOperator.EQUALS , draftOrderNo)  , UtilMisc.toSet("orderId"), null, null, false );
		if(UtilValidate.isNotEmpty(draftOrderIdDetails)){
			draftOrderIdDetails = EntityUtil.getFirst(draftOrderIdDetails);
			primaryOrderId = draftOrderIdDetails.orderId;
			shipmentList = EntityUtil.filterByCondition(shipmentList, EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, primaryOrderId));
		}
	}
	if(UtilValidate.isNotEmpty(parameters.refrenceNo)){
		poReferNumDetails = delegator.findList("OrderAttribute",EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS , parameters.refrenceNo)  , UtilMisc.toSet("orderId"), null, null, false );
		poReferNumDetails = EntityUtil.getFirst(poReferNumDetails);
		orderId = poReferNumDetails.orderId;
		shipmentList = EntityUtil.filterByCondition(shipmentList, EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId));
	}
	
	if(UtilValidate.isNotEmpty(parameters.facilityId)){
		orderIdList = [];
		orderIdList = EntityUtil.getFieldListFromEntityList(shipmentList,"primaryOrderId", true);
		if(orderIdList){
			orderCondList = [];
			orderCondList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIdList));
			orderCondList.add(EntityCondition.makeCondition("originFacilityId",EntityOperator.EQUALS,parameters.facilityId));
			orderCond=EntityCondition.makeCondition(orderCondList,EntityOperator.AND);
			orderHeaderList = delegator.findList("OrderHeader", orderCond, UtilMisc.toSet("orderId"), null, null, false );
				filteredOrderIdList = EntityUtil.getFieldListFromEntityList(orderHeaderList,"orderId", true);
				shipmentList = EntityUtil.filterByCondition(shipmentList, EntityCondition.makeCondition("primaryOrderId", EntityOperator.IN, filteredOrderIdList));
		}
		
	}
	
	partyName = "";
	shipmentList.each{shipment->
		tempMap=[:];
		depoPartyId = "";
		depoPartyName = "";
		if(shipment){
		tempMap.put("shipmentId",shipment.shipmentId);
		tempMap.put("estimatedShipDate",shipment.estimatedShipDate);
		//tempMap.put("vehicleId",shipment.vehicleId);
		tempMap.put("partyIdTo",shipment.partyIdTo);
		String primaryOrderId=null;
		if(UtilValidate.isNotEmpty(shipment.primaryOrderId)){
			primaryOrderId=shipment.primaryOrderId;
		}
		tempMap.put("statusId",shipment.statusId);
		orderHeader = delegator.findOne("OrderHeader", [orderId : primaryOrderId], false);
		if(UtilValidate.isNotEmpty(orderHeader)){
		depoPartyId = orderHeader.originFacilityId;
		   tempMap.put("orderDate",orderHeader.orderDate);
		}
		tempMap.put("depoPartyId",depoPartyId);
		facility = delegator.findOne("Facility", [facilityId : depoPartyId], false);
		if(UtilValidate.isNotEmpty(facility)){
			depoPartyName=facility.description;
		}
		tempMap.put("depoPartyName",depoPartyName);
		if(UtilValidate.isNotEmpty(shipment.partyIdTo)){
		 partyName=PartyHelper.getPartyName(delegator, shipment.partyIdTo, false);
		    tempMap.put("partyName",partyName);
		}
		else{
			tempMap.put("partyName","");
		}
		exprCondList=[];
		exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
		exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
		OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
		
		if(UtilValidate.isNotEmpty(OrderAss)){
			salesOrder = OrderAss.get("toOrderId");
			tempMap.put("salesOrder",salesOrder);
		}else{
			tempMap.put("salesOrder","");
		}
		orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , primaryOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
		if(UtilValidate.isNotEmpty(orderHeaderSequences)){
			orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
			salesOrder = orderSeqDetails.orderNo;
			tempMap.put("salesOrder",salesOrder);
		}else{
		    tempMap.put("salesOrder",primaryOrderId);
		}
		tempMap.put("primaryOrderId",primaryOrderId);
		poRefNum = "";
		orderAttributes = delegator.findOne("OrderAttribute", [orderId : primaryOrderId,attrName:"REF_NUMBER"], false);
		if(UtilValidate.isNotEmpty(orderAttributes)){
			poRefNum=orderAttributes.attrValue;
		}
		tempMap.putAt("poRefNum", poRefNum);
		if(shipment.partyIdFrom){
			tempMap.putAt("partyId", shipment.partyIdFrom);
			
			if(UtilValidate.isNotEmpty(primaryOrderId)){
				ecl = EntityCondition.makeCondition([
					EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER")],
				EntityOperator.AND);
				billToOrderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				billToOrderRole=EntityUtil.getFirst(billToOrderRoles);
				if(billToOrderRole && billToOrderRole.partyId){
				tempMap.put("billToPartyId",billToOrderRole.partyId);
				}
			}
			
		}else{
			if(UtilValidate.isNotEmpty(primaryOrderId)){
				ecl = EntityCondition.makeCondition([
									   EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId),
									   EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_FROM_VENDOR")],
								   EntityOperator.AND);
				orderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				orderRole=EntityUtil.getFirst(orderRoles);
				tempMap.put("partyId",orderRole.partyId);
				
				ecl = EntityCondition.makeCondition([
					EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId),
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
			if(UtilValidate.isNotEmpty(primaryOrderId)){
				ecl = EntityCondition.makeCondition([
									   EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId),
									   EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER")],
								   EntityOperator.AND);
				orderRoles=delegator.findList("OrderRole",ecl,null,null,null,false);
				orderRole=EntityUtil.getFirst(orderRoles);
				tempMap.put("weaver",orderRole.partyId);
			}else{
				tempMap.putAt("weaver", null);
			}
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
				tempMap.put("orderDate",list.orderDate);
				tempMap.put("partyId", partyId);
				tempMap.put("partyIdTo",list.partyIdTo);
				
				conditionList = [];
				conditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,list.partyIdTo));
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
				if(UtilValidate.isNotEmpty(list.partyIdTo)){
					partyName=PartyHelper.getPartyName(delegator, list.partyIdTo, false);
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
	