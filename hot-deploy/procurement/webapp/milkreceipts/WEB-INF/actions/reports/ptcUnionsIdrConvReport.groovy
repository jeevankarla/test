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

dctx = dispatcher.getDispatchContext();
purposeTypeId=parameters.purposeTypeId;
customTimePeriodId=parameters.customTimePeriodId;

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
nextDateTime = UtilDateTime.getNextDayStart(thruDateTime);
thruDate = UtilDateTime.toDateString(nextDateTime,"yyyy-MM-dd");
fromDate = UtilDateTime.toDateString(fromDateTime,"yyyy-MM-dd");
sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
shiftDayTimeStart = fromDate + " 05:00:00.000";
shiftDayTimeEnd = thruDate + " 04:59:59.000";

dayBegin = new java.sql.Timestamp(sdf.parse(shiftDayTimeStart).getTime());
dayEnd = new java.sql.Timestamp(sdf.parse(shiftDayTimeEnd).getTime());


context.fromDate = dayBegin;
context.thruDate = thruDateTime;

periodBillingId=null;
List periodBillingList = FastList.newInstance();
periodBillingList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["GENERATED","APPROVED","APPROVED_PAYMENT"]));
periodBillingList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
periodBillingList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PTC_TRSPT_MRGN"));
periodBillingCond=EntityCondition.makeCondition(periodBillingList,EntityOperator.AND);
periodBilling = delegator.findList("PeriodBilling", periodBillingCond , null, null, null, false );
if(UtilValidate.isNotEmpty(periodBilling)){
	periodBillingData = EntityUtil.getFirst(periodBilling);
	periodBillingId = periodBillingData.periodBillingId;
}
List milkTransferIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(periodBillingId)){
	periodBillingList.clear();
	periodBillingList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
	periodBillingCond=EntityCondition.makeCondition(periodBillingList,EntityOperator.AND);
	ptcBillingCommiMilkTransfer = delegator.findList("PtcBillingCommissionAndMilkTransfer", periodBillingCond , null, null, null, false );
	if(UtilValidate.isNotEmpty(ptcBillingCommiMilkTransfer)){
		 milkTransferIds = EntityUtil.getFieldListFromEntityList(ptcBillingCommiMilkTransfer, "milkTransferId", false);
	}
}
	
conditionList =[];
if((!"All".equalsIgnoreCase(purposeTypeId)) && UtilValidate.isNotEmpty(purposeTypeId)){
	context.purposeTypeId=purposeTypeId;
   	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS , purposeTypeId));
}
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_APPROVED","MXF_RECD"]));
conditionList.add(EntityCondition.makeCondition("milkTransferId", EntityOperator.IN , milkTransferIds));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
MilkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);

unions=null;
if(UtilValidate.isNotEmpty(MilkTransferList)){
	unions=EntityUtil.getFieldListFromEntityList(MilkTransferList, "partyId", true);
}
unionsMilkMap=[:];
totUnionsMilkMap=[:];
BigDecimal totQuantity=BigDecimal.ZERO;
BigDecimal totKgFat=BigDecimal.ZERO;
BigDecimal totKgSnf=BigDecimal.ZERO;

if(UtilValidate.isNotEmpty(unions)){
	unions.each {union->
		idrConvDetailsMap=[:];
		
		idrProductsMap=[:];
		convProductsMap=[:];
		unionList=EntityUtil.filterByCondition(MilkTransferList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, union));
		
		if(UtilValidate.isNotEmpty(unionList)){
			unionList.each {unionDataEachTime->
				
				BigDecimal receivedQuantity=BigDecimal.ZERO;
				String productId='';
				String purposeTypeId='';
				
				productId=unionDataEachTime.productId;
				purposeTypeId=unionDataEachTime.purposeTypeId;
				receivedQuantity=unionDataEachTime.receivedQuantity;
				receivedKgFat=unionDataEachTime.receivedKgFat;
				receivedKgSnf=unionDataEachTime.receivedKgSnf;
				if(UtilValidate.isNotEmpty(receivedQuantity)){
					totQuantity=totQuantity+receivedQuantity;
				}else
			    receivedQuantity=0;
				if(UtilValidate.isNotEmpty(receivedKgFat)){
					totKgFat=totKgFat+receivedKgFat;
				}else
     			receivedKgFat=0;
				if(UtilValidate.isNotEmpty(receivedKgSnf)){
					totKgSnf=totKgSnf+receivedKgSnf;
				}else
			    receivedKgSnf=0;
			
				idrMap=[:];
				convMap=[:];
				if(("INTERNAL".equalsIgnoreCase(purposeTypeId)) && UtilValidate.isNotEmpty(purposeTypeId)){
					if(UtilValidate.isEmpty(idrProductsMap) || (UtilValidate.isNotEmpty(idrProductsMap) && UtilValidate.isEmpty(idrProductsMap.get(productId)))){
						idrMap.put("quantity", receivedQuantity);
						idrMap.put("kgFat", receivedKgFat);
						idrMap.put("kgSnf", receivedKgSnf);
						idrProductsMap.put(productId, idrMap);
					}else{
						 Map idrTempMap = FastMap.newInstance();
						 idrTempMap.putAll(idrProductsMap.get(productId));
						 idrTempMap.putAt("quantity", idrTempMap.get("quantity") + receivedQuantity);
						 idrTempMap.putAt("kgFat", idrTempMap.get("kgFat") + receivedKgFat);
						 idrTempMap.putAt("kgSnf", idrTempMap.get("kgSnf") + receivedKgSnf);
						 idrProductsMap.put(productId, idrTempMap);
						}
				}else if(("CONVERSION".equalsIgnoreCase(purposeTypeId)) && UtilValidate.isNotEmpty(purposeTypeId)){
					if(UtilValidate.isEmpty(convProductsMap) || (UtilValidate.isNotEmpty(convProductsMap) && UtilValidate.isEmpty(convProductsMap.get(productId)))){
					    convMap.put("quantity", receivedQuantity);
						convMap.put("kgFat", receivedKgFat);
						convMap.put("kgSnf", receivedKgSnf);
						convProductsMap.put(productId, convMap);
					}else{
						 Map convTempMap = FastMap.newInstance();
						 convTempMap.putAll(idrProductsMap.get(productId));
						 convTempMap.putAt("quantity", convTempMap.get("quantity") + receivedQuantity);
						 convTempMap.putAt("kgFat", convTempMap.get("kgFat") + receivedKgFat);
						 convTempMap.putAt("kgSnf", convTempMap.get("kgSnf") + receivedKgSnf);
						 convProductsMap.put(productId, convTempMap);
						}
				}
			}
		}
		idrConvDetailsMap.put("idrProductsMap", idrProductsMap);
		idrConvDetailsMap.put("convProductsMap", convProductsMap);
		
		partyName =  PartyHelper.getPartyName(delegator, union, false);
		if(UtilValidate.isEmpty(partyName)){
		    unionsMilkMap.put(union, idrConvDetailsMap);
		}else{
	       unionsMilkMap.put(partyName, idrConvDetailsMap);
		}
		
		
		
	}
}
totUnionsMilkMap.put("totQuantity", totQuantity);
totUnionsMilkMap.put("totKgFat", totKgFat);
totUnionsMilkMap.put("totKgSnf", totKgSnf);

context.unionsMilkMap=unionsMilkMap;			
context.totUnionsMilkMap=totUnionsMilkMap;










