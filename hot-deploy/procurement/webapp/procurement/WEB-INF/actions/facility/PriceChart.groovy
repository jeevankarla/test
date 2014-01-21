import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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
import java.sql.Timestamp;
import java.math.RoundingMode;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

import in.vasista.vbiz.procurement.PriceServices;

facilityId = parameters.getAt("facilityId");
def sdf = new SimpleDateFormat("yyyy-MM-dd");
Timestamp priceDate = UtilDateTime.nowTimestamp();
if(parameters.get("priceDate")!=null){
	 String parseDate = parameters.get("priceDate");
	try {
		priceDate = new java.sql.Timestamp(sdf.parse(parseDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+priceDate, "");
   
	}
}
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

 procPriceChartList =[];
 priceChartLists = [];
 procPriceChartId = null;
 if(!facilityId){
	 facilityId = "_NA_";
	  }
 //facility = delegator.findOne("Facility",[facilityId:facilityId],false);
 //categoryTypeEnum = facility.get("categoryTypeEnum");
 categoryTypeEnum = parameters.categoryTypeEnum;
 context.put("categoryTypeEnum",categoryTypeEnum);
 dctx = dispatcher.getDispatchContext();
 inMap = [:];
 inMap.put("userLogin",context.userLogin);
 inMap.put("facilityId",facilityId);
 inMap.put("priceDate",priceDate);
 inMap.put("supplyTypeEnumId",parameters.supplyTypeEnumId);
 inMap.put("categoryTypeEnum",categoryTypeEnum);
 GenericValue priceChartLists = PriceServices.fetchPriceChart(dctx,inMap);
 procurementPrices = [];
 if (priceChartLists == null) {
		context.errorMessage = "No valid price chart found";
		return ;
	}else{
			procurementPriceLists = delegator.findList("ProcurementPrice",EntityCondition.makeCondition("procPriceChartId",EntityOperator.EQUALS,priceChartLists.get("procPriceChartId")),null,null,null,false);
			chartId = procurementPriceLists[0].get("procPriceChartId");
			for(procurementPriceList in procurementPriceLists){
				priceChartMap = [:] ;
				priceChartMap.put("facilityId",facilityId);
				priceChartMap.put("price",procurementPriceList.get("price"));
				priceChartMap.put("procurementPriceTypeId",procurementPriceList.get("procurementPriceTypeId"));
				priceChartMap.put("productId",procurementPriceList.get("productId"));
				priceChartMap.put("fatPercent",procurementPriceList.get("fatPercent"));
				priceChartMap.put("snfPercent",procurementPriceList.get("snfPercent"));
				procurementPrices.add(priceChartMap);
				}
	}
	chartDetails = delegator.findOne("ProcurementPriceChart",[procPriceChartId:chartId],false);
	regionId = chartDetails.get("regionId");
	context.put("fromDate", chartDetails.fromDate);
	context.putAt("thruDate", chartDetails.thruDate);
	regionType = null;
	if("_NA_".equals(regionId)){
		regionType = "DEFAULT";
		}else{
		regionFacility = delegator.findOne("Facility",[facilityId:regionId],false);
		regionType = regionFacility.get("facilityTypeId");
		}
	context.putAt("regionType",regionType); 
	context.putAt("chartId", chartId);
	context.putAt("procPriceChartList", procurementPrices);
  