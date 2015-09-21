import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.production.ProductionServices;

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

fromDeptId=parameters.fromDeptId;
productId=parameters.productId;
thruDeptId=parameters.thruDeptId;

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
context.fromDeptId = fromDeptId;
context.productId = productId;

partyName =  PartyHelper.getPartyName(delegator, thruDeptId, false);
if(UtilValidate.isNotEmpty(partyName)){
	context.thruDeptId = partyName;
}

int totalDays =UtilDateTime.getIntervalInDays(dayBegin, dayEnd);
totalDays=totalDays+1;

List<String> productIds=[];
if(UtilValidate.isEmpty(productId)){
	List<GenericValue> facility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, fromDeptId), null, null, null, false);
	List facilityIds=[];
	if(UtilValidate.isNotEmpty(facility)){
		facilityIds = EntityUtil.getFieldListFromEntityList(facility, "facilityId", true);
	}
	List<GenericValue> productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null, null, null, false);
	if(UtilValidate.isNotEmpty(productFacility)){
		productIds = EntityUtil.getFieldListFromEntityList(productFacility, "productId", false);
		
	}
}else{
	productIds.add(productId);
}
Set<String> hsProdIds = new HashSet();
hsProdIds.addAll(productIds);
Map allProductsMap =FastMap.newInstance();

if(UtilValidate.isNotEmpty(hsProdIds)){
	hsProdIds.each {eachProdId->
		Map dayWiseMap =FastMap.newInstance();
		for(int i=0; i <totalDays; i++){
			dayBeginStart = UtilDateTime.getDayStart(fromDate, i);
			
			Map inMapStr = FastMap.newInstance();
			inMapStr.put("userLogin", userLogin);
			inMapStr.put("shiftType", "MILK_SHIFT");
			inMapStr.put("fromDate", dayBeginStart);
			Map dayWorkShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMapStr );
			
			fromDateStart=dayWorkShifts.fromDate;
			thruDateEnd=dayWorkShifts.thruDate;
			
			// ISSUE DETAILS==============================>
			departmentMilkIssues = ProductionServices.getDepartmentMilkIssues(dctx, [fromDate: fromDateStart,thruDate: thruDateEnd, ownerPartyId:fromDeptId, productId:eachProdId, thruDeptId:thruDeptId, userLogin: userLogin,]);
			milkIssuesMap=departmentMilkIssues.get("milkIssuesMap");
			milkIssuesTotalsMap=departmentMilkIssues.get("milkIssuesTotalsMap");
			if(milkIssuesTotalsMap.get("totIssuedQty") >0 && UtilValidate.isNotEmpty(milkIssuesTotalsMap.get("totIssuedQty"))){
				dayWiseMap.put(fromDateStart,milkIssuesTotalsMap);
			}
		}
		if(UtilValidate.isNotEmpty(dayWiseMap)){
			allProductsMap.put(eachProdId, dayWiseMap);
		}
	}
}
context.allProductsMap=allProductsMap;
