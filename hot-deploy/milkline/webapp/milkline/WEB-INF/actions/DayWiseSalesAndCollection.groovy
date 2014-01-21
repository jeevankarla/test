import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
import org.ofbiz.network.NetworkServices;
import org.ofbiz.network.LmsServices;
import org.ofbiz.product.product.ProductWorker;

userLogin= context.userLogin;
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
dctx = dispatcher.getDispatchContext();

def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());		
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());		
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+fromDate, "");	   
	}
	dayBegin= UtilDateTime.getDayStart(fromDateTime);
	conditionList = [];	
	if(parameters.facilityId){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
	}
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	routeList = delegator.findList("Facility",condition,null,null,null,false);
	

	List<GenericValue> lmsproductList =FastList.newInstance();
	
	lmsproductList=NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	  List<GenericValue> byProductsList =FastList.newInstance();
	  byProductsList=ProductWorker.getProductsByCategory(delegator ,"BYPROD" ,null);
       lmsproductList=  EntityUtil.filterByCondition(lmsproductList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , byProductsList.productId));
		 //Getting ProductCategeries by classification
		 kgCurdProdList = ProductWorker.getProductsByCategory(delegator ,"CURD_KGS" ,null);
		 curd200ProdList = ProductWorker.getProductsByCategory(delegator ,"CURD_200" ,null);
		 curd500ProdList = ProductWorker.getProductsByCategory(delegator ,"CURD_500" ,null);
		 cupCurdProdList = ProductWorker.getProductsByCategory(delegator ,"CURD_100" ,null);
		 flavredProdList = ProductWorker.getProductsByCategory(delegator ,"FLAVERD_MILK" ,null);
		 butterProductList = ProductWorker.getProductsByCategory(delegator ,"BUTTER_MILK" ,null);
		 pannerProdList = ProductWorker.getProductsByCategory(delegator ,"PANNER" ,null);
		 
		 kgCurdProdList = EntityUtil.getFieldListFromEntityList(kgCurdProdList, "productId", true);
		 curd200ProdList = EntityUtil.getFieldListFromEntityList(curd200ProdList, "productId", true);
		 curd500ProdList = EntityUtil.getFieldListFromEntityList(curd500ProdList, "productId", true);
		 cupCurdProdList = EntityUtil.getFieldListFromEntityList(cupCurdProdList, "productId", true);
		 flavredProdList = EntityUtil.getFieldListFromEntityList(flavredProdList, "productId", true);
		 butterProductList = EntityUtil.getFieldListFromEntityList(butterProductList, "productId", true);
		 pannerProdList = EntityUtil.getFieldListFromEntityList(pannerProdList, "productId", true);
  
	   Set lmsProductIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(lmsproductList, "productId", false));
	   
	 zoneGrandTotalMap =[:]
	 zoneGrandTotalMap["lmsQty"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["BUTTER_MILK"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["CURD_200"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["CURD_500"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["CURD_100"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["CURD_KGS"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["PANNER"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["FLAVERD_MILK"] = BigDecimal.ZERO;
	 zoneGrandTotalMap["totSaleAmt"]=BigDecimal.ZERO;
	 zoneGrandTotalMap["totRecpts"]=BigDecimal.ZERO;
	 
	zoneTotalMap =[:];
	allRoutesList =[];
	Map addingZoneTotalMap=FastMap.newInstance();
	for(int i=0; i< routeList.size();i++){
		route = routeList.get(i);
		boothsList=NetworkServices.getBoothList(delegator ,route.facilityId);//getting list of Booths
	routeTotalMap =[:];
	routeTotalMap["ZONE"]=route.parentFacilityId;
	routeTotalMap["route"]=route.facilityId;
	routeTotalMap["lmsQty"] = BigDecimal.ZERO;
	
	routeTotalMap["BUTTER_MILK"] = BigDecimal.ZERO;
	routeTotalMap["CURD_200"] = BigDecimal.ZERO;
	routeTotalMap["CURD_500"] = BigDecimal.ZERO;
	routeTotalMap["CURD_100"] = BigDecimal.ZERO;
	routeTotalMap["CURD_KGS"] = BigDecimal.ZERO;
	routeTotalMap["PANNER"] = BigDecimal.ZERO;
	routeTotalMap["FLAVERD_MILK"] = BigDecimal.ZERO;
	routeTotalMap["totSaleAmt"]=BigDecimal.ZERO;
	routeTotalMap["totRecpts"]=BigDecimal.ZERO;
	
	dayTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:boothsList,fromDate:fromDateTime, thruDate:thruDateTime]);
	productTotalsList =dayTotals.get("productTotals");
	productTotalsList.each{ productTotal ->
		curntProdId=productTotal.getKey();
		curntProdTotal=productTotal.getValue().get("total");
		
		if(lmsProductIdsSet.contains(curntProdId)){
			routeTotalMap["lmsQty"]  += curntProdTotal;
		}else if(butterProductList.contains(curntProdId)){
			   routeTotalMap["BUTTER_MILK"]  += curntProdTotal;
		}else if(kgCurdProdList.contains(curntProdId)){
			   routeTotalMap["CURD_KGS"]  += curntProdTotal;
		}else if(curd200ProdList.contains(curntProdId)){
			routeTotalMap["CURD_200"]  += curntProdTotal;
		}else if(curd500ProdList.contains(curntProdId)){
			routeTotalMap["CURD_500"]  += curntProdTotal;
		}else if(cupCurdProdList.contains(curntProdId)){
			routeTotalMap["CURD_100"]  += curntProdTotal;
		}else if(pannerProdList.contains(curntProdId)){
			routeTotalMap["PANNER"]  += curntProdTotal;
		}else if(flavredProdList.contains(curntProdId)){
			routeTotalMap["FLAVERD_MILK"]  += curntProdTotal;
		}
	}
	Map creditTypeMap =(Map)dayTotals.get("supplyTypeTotals").get("CREDIT");
	saleAmt= dayTotals.get("totalRevenue");
	quantity = dayTotals.get("totalQuantity");
	creditQty = BigDecimal.ZERO;
	if(creditTypeMap){
		saleAmt = saleAmt-(creditTypeMap.get("totalRevenue"));
		creditQty = (creditTypeMap.get("total"));
		quantity =quantity.subtract((creditTypeMap.get("total")));
	}
	routeTotalMap["totSaleAmt"] +=((new BigDecimal(saleAmt)).setScale(2,BigDecimal.ROUND_HALF_UP));
		routePaidDetail = NetworkServices.getBoothPaidPayments( dctx , [fromDate:fromDateTime ,thruDate:thruDateTime , facilityId:route.facilityId]);
		reciepts = BigDecimal.ZERO;		
		if(UtilValidate.isNotEmpty(routePaidDetail)){
			reciepts = routePaidDetail.get("invoicesTotalAmount");
		}
		routeTotalMap["totRecpts"] += ((new BigDecimal(reciepts)).setScale(2,BigDecimal.ROUND_HALF_UP));
		zoneGrandTotalMap["lmsQty"]+=routeTotalMap.get("lmsQty");
		zoneGrandTotalMap["BUTTER_MILK"]+=routeTotalMap.get("BUTTER_MILK");
		zoneGrandTotalMap["CURD_200"]+=routeTotalMap.get("CURD_200");
		zoneGrandTotalMap["CURD_500"]+=routeTotalMap.get("CURD_500");
		zoneGrandTotalMap["CURD_100"]+=routeTotalMap.get("CURD_100");
		zoneGrandTotalMap["CURD_KGS"]+=routeTotalMap.get("CURD_KGS");
		zoneGrandTotalMap["PANNER"]+=routeTotalMap.get("PANNER");
		zoneGrandTotalMap["FLAVERD_MILK"]+=routeTotalMap.get("FLAVERD_MILK");
		zoneGrandTotalMap["totSaleAmt"]+=routeTotalMap.get("totSaleAmt");
		zoneGrandTotalMap["totRecpts"]+=routeTotalMap.get("totRecpts");
		
		allRoutesList.add(routeTotalMap);
		if(zoneTotalMap.get(route.parentFacilityId)){//updating addingzoneTotlamap by zone
		    addingZoneTotalMap["lmsQty"]+=routeTotalMap.get("lmsQty");
			addingZoneTotalMap["BUTTER_MILK"]+=routeTotalMap.get("BUTTER_MILK");
			addingZoneTotalMap["CURD_200"]+=routeTotalMap.get("CURD_200");
			addingZoneTotalMap["CURD_500"]+=routeTotalMap.get("CURD_500");
			addingZoneTotalMap["CURD_100"]+=routeTotalMap.get("CURD_100");
			addingZoneTotalMap["CURD_KGS"]+=routeTotalMap.get("CURD_KGS");
			addingZoneTotalMap["PANNER"]+=routeTotalMap.get("PANNER");
			addingZoneTotalMap["FLAVERD_MILK"]+=routeTotalMap.get("FLAVERD_MILK");
			addingZoneTotalMap["totSaleAmt"]+=routeTotalMap.get("totSaleAmt");
			addingZoneTotalMap["totRecpts"]+=routeTotalMap.get("totRecpts");
			
			tempAddingZoneTotalMap = [:];
			tempAddingZoneTotalMap.putAll(addingZoneTotalMap);
			zoneTotalMap[route.parentFacilityId]=tempAddingZoneTotalMap;
		}else{//first time inserting route totals
		   addingZoneTotalMap["route"]=route.parentFacilityId;
		    addingZoneTotalMap["lmsQty"]=routeTotalMap.get("lmsQty");
			addingZoneTotalMap["BUTTER_MILK"]=routeTotalMap.get("BUTTER_MILK");
			addingZoneTotalMap["CURD_200"]=routeTotalMap.get("CURD_200");
			addingZoneTotalMap["CURD_500"]=routeTotalMap.get("CURD_500");
			addingZoneTotalMap["CURD_100"]=routeTotalMap.get("CURD_100");
			addingZoneTotalMap["CURD_KGS"]=routeTotalMap.get("CURD_KGS");
			addingZoneTotalMap["PANNER"]=routeTotalMap.get("PANNER");
			addingZoneTotalMap["FLAVERD_MILK"]=routeTotalMap.get("FLAVERD_MILK");
			addingZoneTotalMap["totSaleAmt"]=routeTotalMap.get("totSaleAmt");
			addingZoneTotalMap["totRecpts"]=routeTotalMap.get("totRecpts");
			
			tempAddingZoneTotalMap = [:];
			tempAddingZoneTotalMap.putAll(addingZoneTotalMap);
			zoneTotalMap[route.parentFacilityId]=tempAddingZoneTotalMap;
		}
	context.put("userLogin",userLogin);
	context.put("fromDate", fromDateTime);
	context.put("thruDate", thruDateTime);	
}		
	context.put("allRoutesList", allRoutesList);
	context.put("zoneTotalMap", zoneTotalMap);
	context.put("zoneGrandTotalMap", zoneGrandTotalMap);

