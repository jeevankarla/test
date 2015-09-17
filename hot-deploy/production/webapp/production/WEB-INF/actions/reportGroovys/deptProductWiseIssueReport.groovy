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
context.thruDeptId = thruDeptId;
context.productId = productId;


int totalDays =UtilDateTime.getIntervalInDays(dayBegin, dayEnd);
totalDays=totalDays+1;
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
departmentMilkIssues = ProductionServices.getDepartmentMilkIssues(dctx, [fromDate: fromDateStart,thruDate: thruDateEnd, ownerPartyId:fromDeptId, productId:productId, thruDeptId:thruDeptId, userLogin: userLogin,]);
milkIssuesMap=departmentMilkIssues.get("milkIssuesMap");
milkIssuesTotalsMap=departmentMilkIssues.get("milkIssuesTotalsMap");
dayWiseMap.put(fromDateStart,milkIssuesTotalsMap);

}
context.dayWiseMap=dayWiseMap;
