import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
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
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;

selectDate = UtilDateTime.nowTimestamp();
context.selectDate=selectDate;
dctx = dispatcher.getDispatchContext();
context.dctx=dctx;
allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}

facilityList = [];
if(parameters.unitId){
	facilityList = (ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", parameters.unitId))).get("facilityIds");
}else if(parameters.shedId){
	facilityList = (ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", parameters.shedId))).get("facilityIds");
}

dayBegin = UtilDateTime.getDayStart(selectDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(selectDate, timeZone, locale);
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);

conditionList = [];
if(!allChanges){
conditionList.add(EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS , userLogin.userLoginId));
}
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS , "PURCHASE_ORDER"));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS , "ORDER_CREATED"));
//conditionList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
if(facilityList){
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityList));
}
conditionList.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
   ], EntityOperator.OR));
conditionList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderItemsList = delegator.findList("OrderHeaderItemProductAndFacility", condition, null, ["changeDatetime"], null, false);

totMap = [:];
totMap["totQtyKgsTot"] = BigDecimal.ZERO;
totMap["totFatTot"] = BigDecimal.ZERO;
totMap["totSngTot"] = BigDecimal.ZERO;
totMap["totSqtyLtsTot"] = BigDecimal.ZERO;
totMap["totSqtyFatTot"] = BigDecimal.ZERO;
totMap["totCqtyLtsTot"] = BigDecimal.ZERO;
totMap["totPtcRcyKgsTot"] = BigDecimal.ZERO;

for(procProduct in procurementProductList){
	totMap["totQtyKgs"+procProduct.brandName] = BigDecimal.ZERO;
	totMap["totFat"+procProduct.brandName] = BigDecimal.ZERO;
	totMap["totSng"+procProduct.brandName] = BigDecimal.ZERO;
	totMap["totSqtyLts"+procProduct.brandName] = BigDecimal.ZERO;
	totMap["totSqtyFat"+procProduct.brandName] = BigDecimal.ZERO;
	totMap["totCqtyLts"+procProduct.brandName] = BigDecimal.ZERO;
	totMap["totPtcRcyKgs"+procProduct.brandName] = BigDecimal.ZERO;
}


for(int i=0;i < orderItemsList.size();i++){
	orderItem = orderItemsList.get(i);
	if(UtilValidate.isNotEmpty(orderItem)){
		for(procProduct in procurementProductList){
			if(orderItem.productName == procProduct.productName){
				totMap["totQtyKgs"+procProduct.brandName] += orderItem.quantity;
				totMap["totQtyKgsTot"] += orderItem.quantity;
				if(orderItem.fat){
					totMap["totFat"+procProduct.brandName] += orderItem.fat;
					totMap["totFatTot"] += orderItem.fat;
				}
				if(orderItem.snf){
					totMap["totSng"+procProduct.brandName] += orderItem.snf;
					totMap["totSngTot"] += orderItem.snf;
				}
				
			
				if(UtilValidate.isNotEmpty(orderItem.sQuantityLtrs)){
					totMap["totSqtyLts"+procProduct.brandName] += orderItem.sQuantityLtrs;
					totMap["totSqtyLtsTot"] += orderItem.sQuantityLtrs;
				}
				if(UtilValidate.isNotEmpty(orderItem.sFat)){
					totMap["totSqtyFat"+procProduct.brandName] += orderItem.sFat;
					totMap["totSqtyFatTot"] += orderItem.sFat;
				}
				if(UtilValidate.isNotEmpty(orderItem.cQuantityLtrs)){
					totMap["totCqtyLts"+procProduct.brandName] += orderItem.cQuantityLtrs;
					totMap["totCqtyLtsTot"] += orderItem.cQuantityLtrs;
				}
				if(UtilValidate.isNotEmpty(orderItem.ptcQuantity)){
					totMap["totPtcRcyKgs"+procProduct.brandName] += orderItem.ptcQuantity;
					totMap["totPtcRcyKgsTot"] += orderItem.ptcQuantity;
				}
			}
		}
	}
}
context.put("orderItemsList",orderItemsList);
context.put("totMap",totMap);


