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

import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();

purposeTypeId=parameters.purposeTypeId;

fromDate=parameters.fromDate;
thruDate=parameters.thruDate;

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
purposeTypeIds=[];
List<GenericValue> milkPurposeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"MILK_PRCH_PURP"), null, ['sequenceId'], null, true);
if(UtilValidate.isNotEmpty(milkPurposeList)){
	purposeTypeIds=EntityUtil.getFieldListFromEntityList(milkPurposeList, "enumId", true);
}
conditionList =[];
if((!"All".equalsIgnoreCase(purposeTypeId)) && UtilValidate.isNotEmpty(purposeTypeId)){
	context.purposeTypeId=purposeTypeId;
   	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS , purposeTypeId));
}else{
	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN , purposeTypeIds));
}
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_APPROVED","MXF_RECD"]));
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , "MD"));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
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
	unions.sort();
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
				if(purposeTypeId.equals("INTERNAL") && UtilValidate.isNotEmpty(purposeTypeId)){
					if(UtilValidate.isEmpty(idrProductsMap) || (UtilValidate.isNotEmpty(idrProductsMap) && UtilValidate.isEmpty(idrProductsMap.get(productId)))){
						idrMap.put("quantity", receivedQuantity);
						idrMap.put("kgFat", receivedKgFat);
						idrMap.put("kgSnf", receivedKgSnf);
						idrMap.put("purposeTypeId", purposeTypeId);
						idrProductsMap.put(productId, idrMap);
					}else{
						 Map idrTempMap = FastMap.newInstance();
						 idrTempMap.putAll(idrProductsMap.get(productId));
						 idrTempMap.putAt("quantity", idrTempMap.get("quantity") + receivedQuantity);
						 idrTempMap.putAt("kgFat", idrTempMap.get("kgFat") + receivedKgFat);
						 idrTempMap.putAt("kgSnf", idrTempMap.get("kgSnf") + receivedKgSnf);
						 idrTempMap.putAt("purposeTypeId", purposeTypeId);
						 idrProductsMap.put(productId, idrTempMap);
						}
				}else if(purposeTypeId.equals("CONVERSION") && UtilValidate.isNotEmpty(purposeTypeId)){
					if(UtilValidate.isEmpty(convProductsMap) || (UtilValidate.isNotEmpty(convProductsMap) && UtilValidate.isEmpty(convProductsMap.get(productId)))){
					    convMap.put("quantity", receivedQuantity);
						convMap.put("kgFat", receivedKgFat);
						convMap.put("kgSnf", receivedKgSnf);
						convMap.put("purposeTypeId", purposeTypeId);
						convProductsMap.put(productId, convMap);
					}else{
						 Map convTempMap = FastMap.newInstance();
						 convTempMap.putAll(convProductsMap.get(productId));
						 convTempMap.putAt("quantity", convTempMap.get("quantity") + receivedQuantity);
						 convTempMap.putAt("kgFat", convTempMap.get("kgFat") + receivedKgFat);
						 convTempMap.putAt("kgSnf", convTempMap.get("kgSnf") + receivedKgSnf);
						 convTempMap.putAt("purposeTypeId", purposeTypeId);
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


