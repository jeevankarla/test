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
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

dctx=dispatcher.getDispatchContext();
effectiveDateStr = parameters.effectiveDate;
Debug.log("effectiveDateStr ######################################"+effectiveDateStr);
effectiveDate = null;
if (UtilValidate.isNotEmpty(effectiveDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, "");
	}
}
else{
	effectiveDate = UtilDateTime.nowTimestamp();
}
dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(effectiveDate);
context.displayDate = UtilDateTime.toDateString(dayBegin, "dd MMMMM, yyyy");
conditionList=[];
resultCtx = ByProductNetworkServices.getAllBooths(delegator,null);
boothsList= resultCtx.get("boothsList");
boothsDetailsList = resultCtx.get("boothsDetailsList");
boothTotals=[:];
returnBoothTotals=[:];
boothTotalsWithReturn=[:];
periodBoothTotals=[:];
FDRDetail = ByProductNetworkServices.getFacilityFixedDeposit( dctx , [userLogin: userLogin, effectiveDate: dayBegin]).get("FacilityFDRDetail");
duesFDRList = [];
//boothsList.clear();
//boothsList.add("9598");
boothsList.each{ eachBoothId ->
	//facility
	facilityFDRMap = [:];
	facilityFDRMap.putAt("facilityId", eachBoothId);
	facilityDet = EntityUtil.filterByCondition(boothsDetailsList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachBoothId));
	facilityFDRMap.putAt("facilityName", (EntityUtil.getFirst(facilityDet)).facilityName);
	openingBalance =(ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: dayBegin , facilityId:eachBoothId])).get("openingBalance");
	facilityFDRMap.putAt("openingBalance", openingBalance);
	boothFDRDet = FDRDetail.get(eachBoothId);
	fdrAmt = 0;
	fdrNums = "";
	if(boothFDRDet){
		fdrAmt = boothFDRDet.get("totalAmount");
		fdrDetails = boothFDRDet.get("FDRDetail");
		fdrDetails.each{eachDetail->
			fdrNums = fdrNums+eachDetail.fdrNumber+",";
		}
	}
	facilityFDRMap.putAt("fdrNumber", fdrNums);
	facilityFDRMap.putAt("fdrAmount", fdrAmt);
	facilityFDRMap.putAt("diffAmount", (openingBalance-fdrAmt));
	duesFDRList.add(facilityFDRMap);
}
context.duesFDRList = duesFDRList;