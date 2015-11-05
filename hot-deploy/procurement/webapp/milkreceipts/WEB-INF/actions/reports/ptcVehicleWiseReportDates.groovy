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
import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptsTransporterServices;

dctx = dispatcher.getDispatchContext();

fromDateSql=parameters.fromDate;
thruDateSql=parameters.thruDate;

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDateSql).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDateSql).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

Map inMap = FastMap.newInstance();
inMap.put("userLogin", userLogin);
inMap.put("shiftType", "MILK_SHIFT");
inMap.put("fromDate", dayBegin);
inMap.put("thruDate", dayEnd);
Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );

fromDate=workShifts.fromDate;
thruDate=workShifts.thruDate;

context.fromDate = fromDate;
context.thruDate = dayEnd;
vehicleId=parameters.vehicleId;

containerIds=null;
conditionList =[];
List statusList = UtilMisc.toList("MXF_APPROVED");
statusList.add("MXF_RECD");
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , statusList));
if(UtilValidate.isNotEmpty(vehicleId) && (!"all".equals(vehicleId))){
	context.vehicleId = vehicleId;
	conditionList.add(EntityCondition.makeCondition("containerId", EntityOperator.EQUALS , vehicleId));
}
List purposeTypeList = UtilMisc.toList("INTERNAL");
purposeTypeList.add("COPACKING");
purposeTypeList.add("OUTGOING");
conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN,purposeTypeList));
conditionList.add(EntityCondition.makeCondition("receivedQuantity", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<String> orderBy = UtilMisc.toList("receiveDate");
milkVehicleTransferList = delegator.findList("MilkTransfer", condition, null, orderBy, null, false);

List vehicleIds = EntityUtil.getFieldListFromEntityList(milkVehicleTransferList, "containerId", false);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["GENERATED","APPROVED","APPROVED_PAYMENT"]));
//conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,thruDateSql));
//conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateSql));
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PTC_TRSPT_MRGN"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBilling = delegator.findList("PeriodBillingAndCustomTimePeriod", condition , null, null, null, false );
List periodBillingIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(periodBilling)){
	periodBillingData = EntityUtil.getFirst(periodBilling);
	periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBilling, "periodBillingId", false);
}

List billingVehicleIds = FastList.newInstance();
List ptcBillingCommiMilkTransfer=[];
if(UtilValidate.isNotEmpty(periodBillingIds)){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingIds));
	if(UtilValidate.isNotEmpty(vehicleId) && (!"all".equals(vehicleId))){
		context.vehicleId = vehicleId;
		conditionList.add(EntityCondition.makeCondition("containerId", EntityOperator.EQUALS , vehicleId));
	}
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	ptcBillingCommiMilkTransfer = delegator.findList("PtcBillingCommissionAndMilkTransfer", condition , null, null, null, false );
	if(UtilValidate.isNotEmpty(ptcBillingCommiMilkTransfer)){
		 billingVehicleIds = EntityUtil.getFieldListFromEntityList(ptcBillingCommiMilkTransfer, "containerId", false);
		
	}
}
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

				milkTransferId=eachMilkTransfer.milkTransferId;
				partyId=eachMilkTransfer.partyId;
				dcNo=eachMilkTransfer.dcNo;
				String purposeType = eachMilkTransfer.get("purposeTypeId");
				if(UtilValidate.isNotEmpty(purposeType) && (!purposeType.equalsIgnoreCase("INTERNAL"))){
					partyId=eachMilkTransfer.partyIdTo;
				}
				containerId=eachMilkTransfer.containerId;
				receiveDate=eachMilkTransfer.receiveDate;
				sendQuantity=eachMilkTransfer.quantity;
				receivedQuantity=eachMilkTransfer.receivedQuantity;
				receivedQuantityLtrs=eachMilkTransfer.receivedQuantityLtrs;
				if(UtilValidate.isNotEmpty(milkTransferId)){
					ptcBillingCommission=EntityUtil.filterByCondition(ptcBillingCommiMilkTransfer,EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId));
					
					if(UtilValidate.isNotEmpty(ptcBillingCommission)){
						ptcBillingCommissionData = EntityUtil.getFirst(ptcBillingCommission);
						amount=ptcBillingCommissionData.commissionAmount;
						amount=amount.setScale(2, BigDecimal.ROUND_HALF_UP);
						eachVehicleMap.put("amount",amount);
						totAmount=totAmount+amount;
						
					}
					
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
					
					Map rateMap = FastMap.newInstance();
					rateMap.put("userLogin",userLogin);
					rateMap.put("partyId",partyId);
					rateMap.put("vehicleId",containerId);
					rateMap.put("quantityKgs",receivedQuantity);
					rateMap.put("quantityLtrs",receivedQuantityLtrs);
					rateMap.put("priceDate",receiveDate);
					rateMap.put("returnRateAmt",Boolean.TRUE);
					//rateMap.put("customTimePeriodId",customTimePeriodId);
					Map rateResultMap = MilkReceiptsTransporterServices.calculateTankerMarginRate(dctx, rateMap);

					if(UtilValidate.isNotEmpty(rateResultMap) && UtilValidate.isNotEmpty(rateResultMap.amount)){
						rateAmount = rateResultMap.amount;
						partyDistance = rateResultMap.distance
					 }
					eachVehicleMap.put("rateAmount",rateAmount);
					eachVehicleMap.put("partyDistance",partyDistance);

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


