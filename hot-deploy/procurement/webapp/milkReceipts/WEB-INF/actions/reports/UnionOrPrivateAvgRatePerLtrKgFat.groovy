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
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.PriceServices;
Timestamp fromDate;
Timestamp thruDate;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if(parameters.thruDate){
		   thruDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
totalDays=UtilDateTime.getIntervalInDays(fromDate,thruDate);
context.put("totalDays", totalDays+1);

dctx = dispatcher.getDispatchContext();
context.putAt("dctx", dctx);

List productWiseList = FastList.newInstance();
productWiseList.addAll(EntityUtil.getFieldListFromEntityList(procurementProductList, "productId", true));
context.putAt("productWiseList", productWiseList);

privateDairiesList = [];
if(UtilValidate.isNotEmpty(context.getAt("privateDairiesMap"))){
	privateDairiesList = context.get("privateDairiesMap");
}
shedList = [];
unionPrivateDetailsMap = [:];
Map shedUnitsMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(privateDairiesList)){
	privateDairiesList.each{ privateFacilityList->
		if(UtilValidate.isNotEmpty(privateFacilityList)){
			unitFacilityId = privateFacilityList.getKey();
			facility = delegator.findOne("Facility",[facilityId : unitFacilityId], false);
			shedId = facility.get("parentFacilityId");
			List shedUnitsList = FastList.newInstance();
			shedUnitsList.add(unitFacilityId);
			if(UtilValidate.isEmpty(shedUnitsMap) || (UtilValidate.isNotEmpty(shedUnitsMap) && UtilValidate.isEmpty(shedUnitsMap.get(shedId)))){
				shedUnitsMap.put(shedId, shedUnitsList);
			}else{
				List tempUnitsList = FastList.newInstance();
				tempUnitsList.addAll(shedUnitsMap.get(shedId));
				if(!tempUnitsList.contains(unitFacilityId)){
					tempUnitsList.add(unitFacilityId);
					shedUnitsMap.put(shedId, tempUnitsList);
				}
			}
			shedList.add(shedId);
		}
	}
}
shedList = (new HashSet(shedList)).toList();
context.put("shedList",shedList);
if(UtilValidate.isNotEmpty(shedList)){
	shedList.each{ shed->
		List tempShedList = FastList.newInstance();
		tempShedList = shedUnitsMap.get(shed);
		if(UtilValidate.isNotEmpty(privateDairiesList)){
			productUnitMap = [:];
			privateDairiesList.each{ privateFacilityList->
				if(UtilValidate.isNotEmpty(privateFacilityList)){
					unitFacilityId = privateFacilityList.getKey();
					if(tempShedList.contains(unitFacilityId)){
						productDetails = privateFacilityList.getValue();
						productDetails.each{prodDetail ->
							productId = prodDetail.getKey();
							productValue = prodDetail.getValue();
							if(productUnitMap.get(productId)){
								productList = productUnitMap.get(productId);
								tempProductMap = [:];
								tempProductMap.put(unitFacilityId, productValue);
								productList.add(tempProductMap);
								productUnitMap.put(productId, productList);
							}
							else{
								tempProductList = [];
								tempProductMap = [:];
								tempProductMap.put(unitFacilityId, productValue);
								tempProductList.add(tempProductMap);
								productUnitMap.put(productId, tempProductList);
							}
						}
					}
				}
			}
			if(UtilValidate.isNotEmpty(productUnitMap)){
				unionPrivateDetailsMap.put(shed, productUnitMap);
			}
		}
	}
}
context.putAt("unionPrivateDetailsMap", unionPrivateDetailsMap);
