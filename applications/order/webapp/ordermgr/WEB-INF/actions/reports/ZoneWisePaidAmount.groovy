import java.io.ObjectOutputStream.DebugTraceInfoStack;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;

if(parameters.paymentDate){
paymentDate = parameters.paymentDate;
}
if(context.paymentDate){
paymentDate = context.paymentDate;
}
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
context.put("paymentDate",paymentDate);

zoneWisePaidMap=[:];
zoneValueMap=[:];
zoneValueMap["TOTAL_RECBLE"]=BigDecimal.ZERO;
zoneValueMap["CASH_HO_PAYIN"]=BigDecimal.ZERO;
zoneValueMap["ESEVA_PAYIN"]=BigDecimal.ZERO;
zoneValueMap["APONLINE_PAYIN"]=BigDecimal.ZERO;
zoneValueMap["ZONE_PAYIN"]=BigDecimal.ZERO;
zoneValueMap["ESEVA_CHARGES"]=BigDecimal.ZERO;
zoneValueMap["APONLINE_CHARGES"]=BigDecimal.ZERO;
distributorMap=[:];

List<GenericValue>  distributorFacilites= delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DISTRIBUTOR"), null, UtilMisc.toList("sequenceNum","facilityName"), null, false);

distributorFacilites.each{ distributorFacility ->
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, distributorFacility.facilityId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "DISTRIBUTOR"));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	List<GenericValue>  zoneFacilities= delegator.findList("Facility", condition, null, UtilMisc.toList("sequenceNum","facilityName"), null, false);
	
	distributorMap[distributorFacility.description]=zoneFacilities.facilityId;
	context.put("distributorMap",distributorMap);		
}
zones = NetworkServices.getZones(delegator);
zonesList = zones.zonesList;
zonesList.each { zone ->
	tempZoneValueMap=[:];
	paymentDate = paymentDate;
	boothReceivablePayments = NetworkServices.getBoothReceivablePayments(delegator ,dispatcher ,userLogin,paymentDate, null ,zone ,null ,Boolean.TRUE ,Boolean.FALSE);
	totalReceivable = boothReceivablePayments.get("invoicesTotalAmount");
	zoneValueMap["TOTAL_RECBLE"]=totalReceivable;
	tempZoneValueMap.putAll(zoneValueMap);
	zoneWisePaidMap.put(zone, tempZoneValueMap);
}
context.put("paymentDate", paymentDate);
context.put("userLogin",userLogin);
boolean onlyCurrentDues = Boolean.TRUE;
context.put("onlyCurrentDues",onlyCurrentDues);
boothsPaymentsDetail = NetworkServices.getBoothPaidPayments(dctx , context);
boothpaymentList = boothsPaymentsDetail.get("boothPaymentsList");
boothpaymentList.each { boothpayment ->	
	boothDetails = (NetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",boothpayment.get("facilityId")))).get("boothDetails");
	paymentMethodTypeId = boothpayment.get("paymentMethodTypeId");
	zoneId = boothDetails.getAt("zoneId");
	zoneMap=[:];
	zoneMap.putAll(zoneWisePaidMap[zoneId]);
	if(paymentMethodTypeId == "CASH_HO_PAYIN" || paymentMethodTypeId == "ESEVA_PAYIN" || paymentMethodTypeId == "APONLINE_PAYIN"){
		zoneMap[paymentMethodTypeId] = ((BigDecimal)zoneMap[paymentMethodTypeId]).add(boothpayment.get("amount"));
		if( paymentMethodTypeId == "ESEVA_PAYIN"){
			zoneMap["ESEVA_CHARGES"] = ((BigDecimal)zoneMap["ESEVA_CHARGES"]).add((0.70));			
		}
		 if(paymentMethodTypeId == "APONLINE_PAYIN"){
		zoneMap["APONLINE_CHARGES"] = ((BigDecimal)zoneMap["APONLINE_CHARGES"]).add((2));;
		}
		
		
	}else{
	zoneMap["ZONE_PAYIN"] = ((BigDecimal)zoneMap["ZONE_PAYIN"]).add(boothpayment.get("amount"));	
		
}
	zoneWisePaidMap[zoneId]=zoneMap;

}
context.putAt("zoneWisePaidMap", zoneWisePaidMap);
