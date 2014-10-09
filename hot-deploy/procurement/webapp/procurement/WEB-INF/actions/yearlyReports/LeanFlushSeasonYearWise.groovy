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
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;


dctx = dispatcher.getDispatchContext();

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
facilityId = parameters.shedId ;

if(UtilValidate.isEmpty(fromDate)){
	Debug.logError("fromDate Cannot Be Empty","");
	context.errorMessage = "FromDate Cannot Be Empty.......!";
	return;
}
if(UtilValidate.isEmpty(thruDate)){
	Debug.logError("thruDate Cannot Be Empty","");
	context.errorMessage = "ThruDate Cannot Be Empty.......!";
	return;
}
if(UtilValidate.isEmpty(facilityId)){
	Debug.logError("No Shed Has Been Selected......","");
	context.errorMessage = "No Shed Has Been Selected......!";
	return;
	}
context.putAt("fromDate",fromDate);
context.putAt("thruDate", thruDate);

List leanMonthList=FastList.newInstance();
leanMonthList.add("APR");
leanMonthList.add("MAY");
leanMonthList.add("JUN");
leanMonthList.add("JUL");
leanMonthList.add("AUG");
leanMonthList.add("SEP");
context.putAt("leanMonthList",leanMonthList);
List flushMonthList=FastList.newInstance();
flushMonthList.add("OCT");
flushMonthList.add("NOV");
flushMonthList.add("DEC");
flushMonthList.add("JAN");
flushMonthList.add("FEB");
flushMonthList.add("MAR");
context.putAt("flushMonthList",flushMonthList);

int startDate=Integer.parseInt(fromDate);
int endDate=Integer.parseInt(thruDate);
endDate=endDate-1;

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

String mnthName;
finalMap=[:];

for(startDate;startDate<=endDate;startDate++){
	tempfinalMap=[:];
	context.put("startDate",startDate );
	String stDate=startDate+"-04-01";
	String edDate=(startDate+1)+"-03-31";
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	try{
		if(startDate){
			fromStartDate=UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(stDate).getTime()))}
		if(endDate){
			fromEndDate=UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(edDate).getTime()))}
	   }
	
	catch(ParseException e){
		Debug.logError(e,"Cannot Parse Date String:"+e,"");
		context.errorMessage="Cannot Parse Date String:"+e;
		return;
	}
	
	annualPeriodTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromStartDate , thruDate: fromEndDate,userLogin: userLogin,facilityId: facilityId ]);
	if(UtilValidate.isNotEmpty(annualPeriodTotals)){
		facilityTotals=annualPeriodTotals.get(facilityId);
		if(UtilValidate.isNotEmpty(facilityTotals)){
			Iterator annualPeriodIter=facilityTotals.entrySet().iterator();
			while(annualPeriodIter.hasNext()){
				Map.Entry entry=annualPeriodIter.next();
				if(!"TOT".equals(entry.getKey())){
					Map annualValuesMap=(Map) entry.getValue();	
					if(UtilValidate.isNotEmpty(annualValuesMap)){
						Iterator monthMapIter=annualValuesMap.entrySet().iterator();
						while(monthMapIter.hasNext()){
							Map.Entry monthEntry=monthMapIter.next();
							if(!"TOT".equals(monthEntry.getKey())){
								Map monthValuesMap=(Map) monthEntry.getValue();
								mnthName=monthEntry.getKey().substring(0,3);
								mnthName=mnthName.toUpperCase();
								mnthValue=monthValuesMap.get("TOT").get("TOT").get("qtyLtrs");
								roundMnthValue=(mnthValue/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
								tempfinalMap.put(mnthName,roundMnthValue);	
							}
						}	
					}
				}
			}	
		}
	}
	finalMap.put(startDate,tempfinalMap);
}
context.putAt("finalMap", finalMap);


