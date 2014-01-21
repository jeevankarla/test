import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

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
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;

dctx = dispatcher.getDispatchContext();
unitRoutes = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId", parameters.unitId));
List routeList = unitRoutes.get("routesList");

 tenantId = delegator.getDelegatorTenantId();
 context.tenantId =  tenantId.toUpperCase();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDate = new java.sql.Timestamp(sdf.parse(parameters.selectFromDate).getTime());
	thruDate = new java.sql.Timestamp(sdf.parse(parameters.selectThruDate).getTime());
	context.put("selectFromDate",fromDate);
	context.put("selectThruDate",thruDate);
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
periodBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
periodEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);

conditionList = [];
if(parameters.checkListType !='All'){
	conditionList.add(EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS , userLogin.userLoginId));
	context.userLoginId = userLogin.userLoginId;
}else{
	context.userLoginId = "All";
}
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodBegin)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, periodEnd)));
conditionList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS , parameters.purchaseTime));
conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN ,routeList));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS , "PURCHASE_ORDER"));
//conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS , "ORDER_CREATED"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderItemsList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,["estimatedDeliveryDate","changeDatetime"],null,false);

tempDate ="";
dayWiseEntryMap=[:];
tempList =[];
int lastEntryValue =0;
for(orderItems in orderItemsList){
	lastEntryValue =lastEntryValue+1;
	if(tempDate == ""){
		tempDate = orderItems.estimatedDeliveryDate;
	}	
	if(tempDate.compareTo(orderItems.estimatedDeliveryDate)!=0){
		dayWiseEntryMap[tempDate] =tempList;
		tempDate = orderItems.estimatedDeliveryDate;
		tempList=[];
	}
	tempList.add(orderItems);		
	dayWiseEntryMap[tempDate]=(tempList);
	if(lastEntryValue == orderItemsList.size()-1){		
		dayWiseEntryMap[tempDate]=(tempList);
	}
}
context.putAt("dayWiseEntryMap", dayWiseEntryMap);
Map GrTotMap = FastMap.newInstance();
Map dayTotalsMap = FastMap.newInstance();
Map tempMap =FastMap.newInstance();
tempMap["QtyKgs"] = BigDecimal.ZERO;
tempMap["kgFat"] = BigDecimal.ZERO;
tempMap["kgSnf"] = BigDecimal.ZERO;
tempMap["SqtyLts"] = BigDecimal.ZERO;
tempMap["SFat"] = BigDecimal.ZERO;
tempMap["CqtyLts"] = BigDecimal.ZERO;
tempMap["PtcRcyKgs"] = BigDecimal.ZERO;

for(procProduct in procurementProductList){
	tempMap["QtyKgs"+procProduct.brandName] = BigDecimal.ZERO;
	tempMap["kgFat"+procProduct.brandName] = BigDecimal.ZERO;
	tempMap["kgSnf"+procProduct.brandName] = BigDecimal.ZERO;
	tempMap["SqtyLts"+procProduct.brandName] = BigDecimal.ZERO;
	tempMap["SFat"+procProduct.brandName] = BigDecimal.ZERO;
	tempMap["CqtyLts"+procProduct.brandName] = BigDecimal.ZERO;
	tempMap["PtcRcyKgs"+procProduct.brandName] = BigDecimal.ZERO;
}

GrTotMap.putAll(tempMap);
for(int i=0;i < orderItemsList.size();i++){
	orderItem = orderItemsList.get(i);
	procDate = orderItem.estimatedDeliveryDate;
	if(UtilValidate.isEmpty(dayTotalsMap[procDate])){
		dayTotalsMap[procDate]=[:];
		dayTotalsMap[procDate].putAll(tempMap);		
	}
	Map tempDayTotMap=FastMap.newInstance();
	tempDayTotMap.putAll(dayTotalsMap[procDate]);
		for(procProduct in procurementProductList){
			if(orderItem.productName == procProduct.productName){
				//Code for Calculating Grand total
				kgFat = ((orderItem.quantity).multiply((orderItem.fat).divide(new BigDecimal(100))));
				kgSnf = ((orderItem.quantity).multiply((orderItem.snf).divide(new BigDecimal(100))));
				
				GrTotMap["QtyKgs"+procProduct.brandName] += orderItem.quantity;
				GrTotMap["QtyKgs"] += orderItem.quantity;
				GrTotMap["kgFat"+procProduct.brandName] += kgFat;
				GrTotMap["kgFat"] += kgFat;
				GrTotMap["kgSnf"+procProduct.brandName] += kgSnf;
				GrTotMap["kgSnf"] += kgSnf;
				
				//Code for Calculating Day total
				tempDayTotMap["QtyKgs"+procProduct.brandName] += orderItem.quantity;;
				tempDayTotMap["QtyKgs"] += orderItem.quantity;				
				tempDayTotMap["kgFat"+procProduct.brandName] += kgFat;
				tempDayTotMap["kgFat"] += kgFat;
				tempDayTotMap["kgSnf"+procProduct.brandName] += kgSnf;
				tempDayTotMap["kgSnf"] += kgSnf;
				
				if(UtilValidate.isNotEmpty(orderItem.sQuantityLtrs)){
					GrTotMap["SqtyLts"+procProduct.brandName] += orderItem.sQuantityLtrs;
					GrTotMap["SqtyLts"] += orderItem.sQuantityLtrs;
					
					tempDayTotMap["SqtyLts"+procProduct.brandName] += orderItem.sQuantityLtrs;
					tempDayTotMap["SqtyLts"] += orderItem.sQuantityLtrs;
				}
				if(UtilValidate.isNotEmpty(orderItem.sFat)){					
					GrTotMap["SFat"+procProduct.brandName] += orderItem.sFat;
					GrTotMap["SFat"] += orderItem.sFat;
					
					tempDayTotMap["SFat"+procProduct.brandName] += orderItem.sFat;
					tempDayTotMap["SFat"] += orderItem.sFat;
				}
				if(UtilValidate.isNotEmpty(orderItem.cQuantityLtrs)){
					GrTotMap["CqtyLts"+procProduct.brandName] += orderItem.cQuantityLtrs;
					GrTotMap["CqtyLts"] += orderItem.cQuantityLtrs;
					
					tempDayTotMap["CqtyLts"+procProduct.brandName] += orderItem.cQuantityLtrs;
					tempDayTotMap["CqtyLts"] += orderItem.cQuantityLtrs;
				}
				if(UtilValidate.isNotEmpty(orderItem.ptcQuantity)){
					GrTotMap["PtcRcyKgs"+procProduct.brandName] += orderItem.ptcQuantity;
					GrTotMap["PtcRcyKgs"] += orderItem.ptcQuantity;
					
					tempDayTotMap["PtcRcyKgs"+procProduct.brandName] += orderItem.ptcQuantity;
					tempDayTotMap["PtcRcyKgs"] += orderItem.ptcQuantity;
				}
			}
		}
	
	dayTotalsMap[procDate].putAll(tempDayTotMap);
}
context.dayTotalsMap =dayTotalsMap;
context.put("GrTotMap",GrTotMap);


