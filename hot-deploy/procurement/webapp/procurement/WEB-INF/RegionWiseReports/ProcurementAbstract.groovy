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

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
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

finalshedMap =[:];
finaltotalMap=[:];
regionMap=[:];
regiontotalMap=[:];
Map regionWiseMap = FastMap.newInstance();
procurementProducts= ProcurementReports.getRegionWisePeriodTotals(dctx, [fromDate: fromDate , thruDate: thruDate,facilityGroupId:parameters.facilityGroupId]).get("periodTotalsMap");
if(UtilValidate.isNotEmpty(procurementProducts)){
	Iterator annualPeriodIter = procurementProducts.entrySet().iterator();
	while(annualPeriodIter.hasNext()){
		Map.Entry entry = annualPeriodIter.next();
		key=entry.getKey();
		if(!key.equals("TOT"))
		{	
			List shedList=FastList.newInstance();
			Map RegionValuesMap = (Map)entry.getValue();
			if(UtilValidate.isNotEmpty(RegionValuesMap)){
				Iterator regionMapIter = RegionValuesMap.entrySet().iterator();
				while(regionMapIter.hasNext()){
					Map.Entry shedEntry = regionMapIter.next();
					if(!"TOT".equals(shedEntry.getKey())){
						shedName=shedEntry.getKey();
						shedList.add(shedName);
						Map shedValuesMap = (Map)shedEntry.getValue().get("TOT");
						finalshedMap.put(shedName, shedValuesMap);
					}
					regionMap.put(key,shedList);
					if("TOT".equals(shedEntry.getKey())){
						regionWiseMap = (Map)shedEntry.getValue();
					}
				}
			}
			regiontotalMap.put(key,regionWiseMap);
			regionMap.put(key,shedList);
		}
		if(key=="TOT"){
			Map grandtotValues = (Map) entry.getValue();
			finaltotalMap.putAt("grandtotValues", grandtotValues);
		}
	}
}

context.put("finaltotalMap", finaltotalMap);
context.put("regiontotalMap", regiontotalMap);
context.put("regionMap", regionMap);
context.put("finalshedMap", finalshedMap);
