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
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

fromDate = null;
thruDate = null;
def sdf1 = new SimpleDateFormat("MMMM dd, yyyy");
Timestamp billingDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf1.parse("JUL 20, 2014").getTime()));
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

dctx = dispatcher.getDispatchContext();
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
custConList=[];
custConList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
custConList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDate)));
custConList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "PROC_BILL_MONTH"));
List customTimePeriodsList = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(custConList,EntityOperator.AND),null,null,null,false);
customTimePeriodIds=EntityUtil.getFieldListFromEntityList(customTimePeriodsList, "customTimePeriodId", false);
shedList = ProcurementNetworkServices.getSheds(delegator);
ShedWiseUserChargesMap=[:]
if(UtilValidate.isNotEmpty(shedList)){
	shedList.each{ sheds->
		shedLtrs=0;
		if(UtilValidate.isNotEmpty(customTimePeriodIds)){
			customTimePeriodIds.each{ timePeriodId->
				shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : sheds.getAt("facilityId"),customTimePeriodId : timePeriodId]);
				unitsListSorted = shedUnits.customTimePeriodUnitsDetailList;
				if(UtilValidate.isNotEmpty(unitsListSorted)){
					unitsListSorted.each{ unit ->
						unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: timePeriodId , unitId: unit.facilityId]);
						if(UtilValidate.isNotEmpty(unitBillAbstract)){
							unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
							unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
							if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
								totProductMap = unitGrndValuesTot.get("TOT");
								shedLtrs=shedLtrs+totProductMap.get("qtyLtrs");
							}
						}
					}
				}
				
			}
		}
		if(shedLtrs !=0){
			BigDecimal amount = shedLtrs.multiply(new BigDecimal(0.025));
			amount=amount.setScale(0, BigDecimal.ROUND_HALF_UP)
			BigDecimal serviceTax = amount.multiply(new BigDecimal(12.36)).divide(new BigDecimal(100));
			serviceTax=serviceTax.setScale(0, BigDecimal.ROUND_HALF_UP)
			BigDecimal userCharges=BigDecimal.ZERO;
			userCharges = userCharges.add(amount.add(serviceTax));
			userCharges=userCharges.setScale(0, BigDecimal.ROUND_HALF_UP);
			Map chargesDetailsMap=FastMap.newInstance();
			if(thruDate <= billingDate){
				userCharges=0;
				amount=0;
				serviceTax=0;
			}
			chargesDetailsMap.put("shedLtrs",shedLtrs.setScale(1, BigDecimal.ROUND_HALF_UP));
			chargesDetailsMap.put("amount",amount);
			chargesDetailsMap.put("serviceTax",serviceTax);
			chargesDetailsMap.put("userCharges",userCharges);
			ShedWiseUserChargesMap.put(sheds.getAt("facilityId"),chargesDetailsMap);
		}
	}
}
context.putAt("ShedWiseUserChargesMap", ShedWiseUserChargesMap);
