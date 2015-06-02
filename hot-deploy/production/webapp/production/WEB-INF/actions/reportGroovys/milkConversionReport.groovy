import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

fromDate=parameters.convFromDate;
thruDate=parameters.convThruDate;

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


BigDecimal totReceivedQty=BigDecimal.ZERO;
BigDecimal totReceivedRawQty=BigDecimal.ZERO;
BigDecimal totReceivedSkimQty=BigDecimal.ZERO;
BigDecimal receivedUnionQty=BigDecimal.ZERO;

milkConversionMap=[:];
receivedtotalsMap=[:];

facilityList = delegator.findList("Facility",EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN , ["RAWMILK","SKIMMILK"])  , null, null, null, false );
facilityRawmilkList=EntityUtil.filterByCondition(facilityList,EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "RAWMILK"));
rawfacilityIds=EntityUtil.getFieldListFromEntityList(facilityRawmilkList, "facilityId", true);

facilitySkimmilkList=EntityUtil.filterByCondition(facilityList,EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "SKIMMILK"));
skimfacilityIds=EntityUtil.getFieldListFromEntityList(facilitySkimmilkList, "facilityId", true);

conditionList =[];
conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS,"CONVERSION"));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
MilkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);
unions=null;
if(UtilValidate.isNotEmpty(MilkTransferList)){
unions=EntityUtil.getFieldListFromEntityList(MilkTransferList, "partyId", true);
}
totalTankers=0;
if(UtilValidate.isNotEmpty(unions)){
	unions.each {union->
		unionList=[];
		unionList=EntityUtil.filterByCondition(MilkTransferList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, union));
		if(UtilValidate.isNotEmpty(unionList)){
            receivedMilkMap	=[:];	
			unionTankers=0;
	        BigDecimal receivedSkimQty=BigDecimal.ZERO;
		    BigDecimal receivedRawQty=BigDecimal.ZERO;
			unionList.each {unionData->
				BigDecimal rQty=BigDecimal.ZERO;
				BigDecimal sQty=BigDecimal.ZERO;
				
				facilityId=unionData.siloId;
				if(UtilValidate.isNotEmpty(facilityId) && rawfacilityIds.contains(facilityId)){
					rQty=unionData.receivedQuantity;
					receivedRawQty=receivedRawQty+rQty
				}
				if(UtilValidate.isNotEmpty(facilityId) && skimfacilityIds.contains(facilityId)){
					sQty=unionData.receivedQuantity;
					receivedSkimQty=receivedSkimQty+sQty
		     	}
				unionTankers=unionTankers+1;
			}
			totReceivedRawQty=totReceivedRawQty+receivedRawQty;
			totReceivedSkimQty=totReceivedSkimQty+receivedSkimQty;
			receivedUnionQty=receivedSkimQty+receivedRawQty;
			
			receivedMilkMap.put("receivedRawQty", receivedRawQty);
			receivedMilkMap.put("receivedSkimQty", receivedSkimQty);
			receivedMilkMap.put("unionTankers", unionTankers);
			receivedMilkMap.put("receivedUnionQty", receivedUnionQty);
			
			totalTankers=totalTankers+unionTankers;;
		}
		milkConversionMap.put(union, receivedMilkMap);
	}
	totReceivedQty=totReceivedRawQty+totReceivedSkimQty;
	receivedtotalsMap.put("totReceivedQty", totReceivedQty)
	receivedtotalsMap.put("totalTankers", totalTankers)
	receivedtotalsMap.put("totReceivedRawQty", totReceivedRawQty)
	receivedtotalsMap.put("totReceivedSkimQty", totReceivedSkimQty)
	
}
context.milkConversionMap=milkConversionMap;
context.receivedtotalsMap=receivedtotalsMap;
//Debug.log("milkConversionMap==========11========="+milkConversionMap);

