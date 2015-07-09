import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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

import java.math.RoundingMode;
import java.util.Map;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

import org.ofbiz.party.party.PartyHelper;

fromDate=parameters.fromDate;
thruDate=parameters.thruDate;

vehicleId=parameters.vehicleId;
//vehicleId="KA01B6651";

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

context.fromDate = dayBegin;
context.thruDate = dayEnd;
containerIds=null;
conditionList =[];
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_RECD","MXF_APPROVED"]));
if(UtilValidate.isNotEmpty(vehicleId) && (!"all".equals(vehicleId))){
	context.vehicleId = vehicleId;
    conditionList.add(EntityCondition.makeCondition("containerId", EntityOperator.EQUALS , vehicleId));
}
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<String> orderBy = UtilMisc.toList("-receiveDate");
milkVehicleTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null, orderBy, null, false);
List vehicleIds = EntityUtil.getFieldListFromEntityList(milkVehicleTransferList, "containerId", false);
ptcMap=[:];
if(UtilValidate.isNotEmpty(vehicleIds)){
	vehicleIds.each{eachvehicleId->
		milkTransferList=EntityUtil.filterByCondition(milkVehicleTransferList,EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,eachvehicleId));
		Map allDetailsMap=[:];
		Map vehicleDataMap=[:];
		Map abstractPartyMap=[:];
		Map totPartiesMap=[:];
		siNo=1;
		BigDecimal totSendQty= BigDecimal.ZERO;
		BigDecimal totReceivedQty= BigDecimal.ZERO;
		BigDecimal totAmount= BigDecimal.ZERO;
		vehicleDetails = delegator.findOne("Vehicle",["vehicleId":eachvehicleId],false);
		//milkTransferList = UtilMisc.sortMaps(milkTransferList, UtilMisc.toList("createdStamp"));
		if(UtilValidate.isNotEmpty(milkTransferList)){
			milkTransferList.each{eachMilkTransfer->
				Map eachVehicleMap=[:];
				BigDecimal sendQuantity= BigDecimal.ZERO;
				BigDecimal receivedQuantity= BigDecimal.ZERO;
				BigDecimal diffQty= BigDecimal.ZERO;
				BigDecimal rateAmount= BigDecimal.ZERO;
				BigDecimal amount= BigDecimal.ZERO;
				BigDecimal partyDistance= BigDecimal.ZERO;
				
				partyId=eachMilkTransfer.partyId;
				dcNo=eachMilkTransfer.dcNo;
				containerId=eachMilkTransfer.containerId;
				receiveDate=eachMilkTransfer.receiveDate;
				sendQuantity=eachMilkTransfer.quantity;
				receivedQuantity=eachMilkTransfer.receivedQuantity;
		
				if(UtilValidate.isNotEmpty(sendQuantity) && UtilValidate.isNotEmpty(receivedQuantity)){
					diffQty=receivedQuantity-sendQuantity;
					totSendQty=totSendQty+sendQuantity;
					totReceivedQty=totReceivedQty+receivedQuantity;
				}
				eachVehicleMap.put("partyId",partyId);
				eachVehicleMap.put("dcNo",dcNo);
				eachVehicleMap.put("containerId",containerId);
				eachVehicleMap.put("receiveDate",receiveDate);
				eachVehicleMap.put("sendQuantity",sendQuantity);
				eachVehicleMap.put("receivedQuantity",receivedQuantity);
				eachVehicleMap.put("diffQty",diffQty);
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, containerId));
				conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, "TANKER_RATE"));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, receiveDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, receiveDate)));
				condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				vehicleRate = delegator.findList("VehicleRate", condition , null, null, null, false );
				if(UtilValidate.isNotEmpty(vehicleRate)){
					vehicleRate = EntityUtil.getFirst(vehicleRate);
					rateAmount = vehicleRate.rateAmount;
					eachVehicleMap.put("rateAmount",rateAmount);
				}
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, "DISTANCE_FROM_MD"));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, receiveDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, receiveDate)));
				condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				partyRate = delegator.findList("PartyRate", condition , null, null, null, false );
				if(UtilValidate.isNotEmpty(partyRate)){
					partyRate = EntityUtil.getFirst(partyRate);
					partyDistance = partyRate.rateAmount;
					eachVehicleMap.put("partyDistance",partyDistance);
				}
				if(vehicleDetails){
					vehicleCapacity=vehicleDetails.get("vehicleCapacity");
					if(UtilValidate.isNotEmpty(rateAmount) && UtilValidate.isNotEmpty(partyDistance) && UtilValidate.isNotEmpty(receivedQuantity) && vehicleCapacity>0){
					    //amount = ((partyDistance.multiply(rateAmount)).multiply((receivedQuantity.divide(vehicleCapacity,2,BigDecimal.ROUND_HALF_UP)))).setScale(2,BigDecimal.ROUND_HALF_UP);
						amount = ((partyDistance*rateAmount)*receivedQuantity)/(vehicleCapacity);
						amount=amount.setScale(2, BigDecimal.ROUND_HALF_UP);
						
						eachVehicleMap.put("amount",amount);
						totAmount=totAmount+amount;
					}
				}
				vehicleDataMap.put(siNo, eachVehicleMap);
				siNo=siNo+1;

				eachPartyMap=[:];
			   if(UtilValidate.isEmpty(abstractPartyMap) || (UtilValidate.isNotEmpty(abstractPartyMap) && UtilValidate.isEmpty(abstractPartyMap.get(partyId)))){
				   trips=1;
				   eachPartyMap.put("trips",trips);
				   eachPartyMap.put("tQty",receivedQuantity);
				   eachPartyMap.put("tAmt",amount);
				   abstractPartyMap.put(partyId, eachPartyMap);
			   }else{
				   Map tempPartyMap = FastMap.newInstance();
				   tempPartyMap.putAll(abstractPartyMap.get(partyId));
				   tempPartyMap.putAt("trips", tempPartyMap.get("trips") + 1);
				   if(UtilValidate.isNotEmpty(receivedQuantity)) {
					   tempPartyMap.putAt("tQty", tempPartyMap.get("tQty") + receivedQuantity);
				   }else
			  	   tempPartyMap.putAt("tQty", tempPartyMap.get("tQty") + 0);
				   tempPartyMap.putAt("tAmt", tempPartyMap.get("tAmt") + amount);
				   abstractPartyMap.put(partyId, tempPartyMap);
				   }
			}
			
			totPartiesMap.put("totSendQty",totSendQty);
			totPartiesMap.put("totReceivedQty",totReceivedQty);
			totPartiesMap.put("totAmount",totAmount);
		  }
		allDetailsMap.put("vehicleDataMap", vehicleDataMap);
		allDetailsMap.put("abstractPartyMap", abstractPartyMap);
		allDetailsMap.put("totPartiesMap", totPartiesMap);
		ptcMap.put(eachvehicleId, allDetailsMap);
	}
}
context.ptcMap=ptcMap;
//Debug.log("ptcMap======================"+ptcMap);


