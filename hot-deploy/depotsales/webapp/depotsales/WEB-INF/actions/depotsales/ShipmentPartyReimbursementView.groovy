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
import groovy.json.*

//get SchemeTimePeriod list start
timePeriodId=parameters.timePeriodId;
condList = [];
condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "DEPOT_REIMB_YEAR"));
condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate( UtilDateTime.nowTimestamp())));
condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN,UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp()))));
depotReimbYearList = delegator.findList("SchemeTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
depotReimbYear = EntityUtil.getFirst(depotReimbYearList);
if(depotReimbYear) {
	condList.clear();
	condList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS,depotReimbYear.get("schemeTimePeriodId")));
	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp())));
//		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
//		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp()))));
	List<String> orderBy = UtilMisc.toList("periodNum");
	 depotReimbPeriodList = delegator.findList("SchemeTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	List reimbPeriodList = [];
	for (eachList in depotReimbPeriodList) {
		
		depotReimbPeriodMap = [:];
		timePeriodDate=new SimpleDateFormat("MMM").format(eachList.get("fromDate"))+", "+new SimpleDateFormat("yyyy").format(eachList.get("fromDate"));
		
		if(UtilValidate.isNotEmpty(eachList.get("thruDate"))){
			timePeriodDate=timePeriodDate+" - "+new SimpleDateFormat("MMM").format(eachList.get("thruDate"))+", "+new SimpleDateFormat("yyyy").format(eachList.get("thruDate"));
		}
		depotReimbPeriodMap.put("schemeTimePeriodId",eachList.get("schemeTimePeriodId"));
		depotReimbPeriodMap.put("value",timePeriodDate);
		reimbPeriodList.addAll(depotReimbPeriodMap);
		if(UtilValidate.isEmpty(timePeriodId) || timePeriodId.equals(eachList.get("schemeTimePeriodId"))){
			timePeriodId=eachList.get("schemeTimePeriodId");
			timePeriodFromDate=eachList.get("fromDate");
			timePeriodThruDate=eachList.get("thruDate");
		}
	}
	context.reimbPeriodList = reimbPeriodList;

	}
//get SchemeTimePeriod list end

List finalList=[];
List conditionList=[];
    reimbursementEligibilityPercentage=2;
	shipmentList=result.listIt;
	partyName = "";
	JSONObject shipmentReimbursementJson = new JSONObject();
	shipmentList.each{shipment->
		tempMap=[:];
		if(shipment){Debug.log("&&&&&&&&&&&&&&&&&&---#######-"+shipment);
		tempMap.put("shipmentId",shipment.shipmentId);
		tempMap.put("vehicleId",shipment.vehicleId);
		tempMap.put("partyIdTo",shipment.partyIdTo);
		tempMap.put("claimAmount",shipment.claimAmount);
		tempMap.put("claimStatus",shipment.claimStatus);
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
				if(billToOrderRole && billToOrderRole.partyId){
				tempMap.put("billToPartyId",billToOrderRole.partyId);
				}
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
		
		//get Invoice details
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipment.shipmentId));
		conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"PURCHASE_INVOICE"));
//		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_PAID"));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		invoiceAndItemList=delegator.findList("InvoiceAndItem",condition,null,null,null,false);
		invoiceAmount=0;
		invoiceEligablityAmount=0;
		if(UtilValidate.isNotEmpty(invoiceAndItemList)){
			 invoiceAndItemList.each{list->
				 quantity=list.quantity;
				 amount=list.amount;
				 invoiceAmount=invoiceAmount+(quantity.multiply(amount));
			}
			
		}
		reimbursementEligibilityAmount=(invoiceAmount.multiply(reimbursementEligibilityPercentage)).div(100);
		tempMap.put("invoiceAmount",invoiceAmount);
		tempMap.put("reimbursementEligibilityAmount",reimbursementEligibilityAmount);
		tempMap.put("reimbursementEligibilityPercentage",reimbursementEligibilityPercentage);
// prepare edit reimbursement array
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipment.shipmentId));
//		conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"PURCHASE_INVOICE"));
//		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_PAID"));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		shipmentReimbursementList=delegator.findList("ShipmentReimbursement",condition,null,null,null,false);
//prepare json object
		tempMap.put("receiptAction","AddReceipt");
		if(UtilValidate.isNotEmpty(shipmentReimbursementList)){
			JSONArray shipmentReimbursementJsonArray = new JSONArray();
			shipmentReimbursementList.each{eachItem->
				JSONObject newObj = new JSONObject();
				newObj.put("claimId",eachItem.claimId);
				newObj.put("receiptNo",eachItem.receiptNo);
				newObj.put("receiptAmount",eachItem.receiptAmount);
				newObj.put("receiptDate", UtilDateTime.toDateString(eachItem.receiptDate, "dd/MM/yyyy"));
				newObj.put("description",eachItem.description);
				shipmentReimbursementJsonArray.add(newObj);
			}
			shipmentReimbursementJson.put(shipment.shipmentId,shipmentReimbursementJsonArray);
			tempMap.put("receiptAction","EditReceipt");
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
				tempMap.put("partyIdTo",list.partyIdTo);
				tempMap.put("invoiceAmount",list.invoiceAmount);
				tempMap.put("reimbursementEligibilityAmount",list.reimbursementEligibilityAmount);
				tempMap.put("reimbursementEligibilityPercentage",list.reimbursementEligibilityPercentage);
				tempMap.put("claimAmount",list.claimAmount);
				tempMap.put("claimStatus",list.claimStatus);
				tempMap.put("receiptAction",list.receiptAction);
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
		context.shipmentReimbursementList=shipmentReimbursementJson.toString();
	}


	