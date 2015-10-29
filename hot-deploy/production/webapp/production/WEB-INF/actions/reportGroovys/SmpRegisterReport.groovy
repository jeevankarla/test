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

import in.vasista.vbiz.production.ProductionServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;

fromDate=parameters.fromDate;
thruDate=parameters.thruDate;

shiftId=parameters.shiftId;
context.shiftId = shiftId;

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
//inMap.put("shiftTypeId", shiftId);
Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );

fromDate=workShifts.fromDate;
thruDate=workShifts.thruDate;
context.fromDate = fromDate;
context.thruDate = dayEnd

List allSilosList = FastList.newInstance();
conditionList =[];
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_EQUAL, null));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
allSiloInveAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);
Map smpRegsterMap= FastMap.newInstance();
BigDecimal smpTotQty=BigDecimal.ZERO;

facilityCondList =[];
facilityCondList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "INT7" ));
facilityCondList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SILO"));
EntityCondition facilityCond = EntityCondition.makeCondition(facilityCondList,EntityOperator.AND);
allSilosList = delegator.findList("Facility", facilityCond , null, UtilMisc.toList("sequenceNum"), null, false );
if(UtilValidate.isNotEmpty(allSilosList)){
	allSiloIds=EntityUtil.getFieldListFromEntityList(allSilosList, "facilityId", true);
	if(UtilValidate.isNotEmpty(allSiloInveAndDetailList)){
		workEffortIds = new HashSet(EntityUtil.getFieldListFromEntityList(allSiloInveAndDetailList, "workEffortId", true));
		workEffortIds.each{eachWorkEffortId->
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "91"));
			conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN,BigDecimal.ZERO ));
			conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachWorkEffortId ));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			eachSmpIssuedInvs = EntityUtil.filterByCondition(allSiloInveAndDetailList, condition);
			if(UtilValidate.isNotEmpty(eachSmpIssuedInvs)){
				BigDecimal smpQty=BigDecimal.ZERO;
				eachSmpIssuedInvs.each{eachSmpQty->
					smpQty=smpQty+eachSmpQty.quantityOnHandDiff;
				}
				smpQty=smpQty.negate();
				smpTotQty=smpTotQty+smpQty;
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, allSiloIds));
				conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN,BigDecimal.ZERO ));
				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachWorkEffortId ));
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				smpAndRmSilosData = EntityUtil.filterByCondition(allSiloInveAndDetailList, condition);
				facilityId = ((EntityUtil.getFirst(smpAndRmSilosData)).facilityId);
				if(UtilValidate.isEmpty(smpRegsterMap) || (UtilValidate.isNotEmpty(smpRegsterMap) && UtilValidate.isEmpty(smpRegsterMap.get(facilityId)))){
					smpRegsterMap.putAt(facilityId, smpQty);
				}else{
					smpRegsterMap.putAt(facilityId, smpQty+(smpRegsterMap.get(facilityId)));
				}
					
				
			}
			
		}
	}
}	

context.smpRegsterMap=smpRegsterMap;
context.smpTotQty=smpTotQty;