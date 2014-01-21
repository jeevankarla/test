	import java.util.HashSet;
	import java.util.Iterator;
	import java.util.List;
	
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.GenericValue;
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
	import org.ofbiz.base.util.UtilNumber;
	import org.ofbiz.network.NetworkServices;

	import java.math.BigDecimal;
	import java.math.RoundingMode;
	import java.util.Map;
	import java.util.Map.Entry;

	import org.ofbiz.entity.util.EntityFindOptions;
	import org.ofbiz.service.ServiceUtil;
	
	result = ServiceUtil.returnError(null);
	rounding = RoundingMode.HALF_UP;
	
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	suppDate = null;
	
	context.put("fromDateTime",fromDateTime);
	context.put("thruDateTime",thruDateTime);
	dctx = dispatcher.getDispatchContext();
	
	totalDays=(UtilDateTime.getIntervalInDays(monthBegin,monthEnd))+1;
	context.put("totalDays", totalDays);
	
	periodBillingId = null;
	if(parameters.periodBillingId){
		periodBillingId = parameters.periodBillingId;
	}else{
		context.errorMessage = "No PeriodBillingId Found";
		return;
	}
	
	routesList=[];
	boothsList=[];
	masterList=[];
	BigDecimal totalQty = BigDecimal.ZERO;
	BigDecimal checkTotalQty = BigDecimal.ZERO;
	BigDecimal totalRev = BigDecimal.ZERO;
	totalCommission = BigDecimal.ZERO;
	totalDues = BigDecimal.ZERO;
	recoveryAmt = BigDecimal.ZERO;
	
	productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	productIdList = EntityUtil.getFieldListFromEntityList(productList, "productId", false);
	HashSet productIdListSet = new HashSet(productIdList);
	
	recoveryConditionList = [];
	recoveryConditionList.add(EntityCondition.makeCondition("commissionDate",EntityOperator.EQUALS, thruDateTime));
	recoveryConditionList.add(EntityCondition.makeCondition("periodBillingId",EntityOperator.EQUALS, periodBillingId));
	recoveryCondition = EntityCondition.makeCondition(recoveryConditionList,EntityOperator.AND);
	recoveryList = delegator.findList("FacilityAndCommission",recoveryCondition,["facilityId", "recovery", "dues"] as Set, null, null, false);
	
	boothRecoveryMap = [:];
	recoveryList.each{recoveryItem ->
		boothRecoveryMap[recoveryItem.facilityId] = recoveryItem.recovery;
	}
	
	List sachetsProdIds = [];
	
	initializingMap = [:]; 
	productList.each{product ->
		initializingMap[product.productId] = (BigDecimal.ZERO).setScale(1, rounding);
		
		conditionList = UtilMisc.toList(EntityCondition.makeCondition("acctgFormulaId", EntityOperator.EQUALS, "LMS_VOL_INC_"+product.productId));
		EntityCondition acctngFormulaCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		List<GenericValue> acctgFormulaIds = delegator.findList("AcctgFormula",acctngFormulaCond,UtilMisc.toSet("acctgFormulaId"),null,null,false);
		conditionList.clear();
		
		if(UtilValidate.isEmpty(acctgFormulaIds)){
			sachetsProdIds.add(product.productId);
		}
	}
	
	HashSet sachetIdsSet = new HashSet(sachetsProdIds);
	
	initializingMap["TOTAL"] = (BigDecimal.ZERO).setScale(1, rounding);
	initializingMap["totalRev"] = (BigDecimal.ZERO).setScale(1, rounding);
	initializingMap["AVG"] = (BigDecimal.ZERO).setScale(1, rounding);
	initializingMap["MAINTENANCE"] = (BigDecimal.ZERO).setScale(1, rounding);
	initializingMap["DUES"] = (BigDecimal.ZERO).setScale(1, rounding);
	initializingMap["NET"] = (BigDecimal.ZERO).setScale(1, rounding);
	initializingMap["CHECK_TOTAL"] = (BigDecimal.ZERO).setScale(1, rounding);
	
	dayInitializingMap = [:];
	Timestamp iniSupplyDate = monthBegin;
	for(int i=1 ; i <= totalDays; i++){
		suppDate = new SimpleDateFormat("dd/MM/yyyy").format(iniSupplyDate);
		dayInitializingMap[suppDate] = initializingMap;
		iniSupplyDate = UtilDateTime.getNextDayStart(iniSupplyDate);
	}
	dayInitializingMap["boothRecovery"] = (BigDecimal.ZERO).setScale(1, rounding);
	dayInitializingMap["boothTotals"] = initializingMap;
	
	//fields = new HashSet(["RT01"]);
	routesList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ROUTE"),["facilityId"] as Set, null, null, false);
	
	Map boothMargins= [:];
	Map routeMargins= [:];
	Map tempDayIniMap= [:];
	Map tempIniMap= [:];
	Map boothRoutes = [:];
	List allBoothsList = [];
	tempDayIniMap.putAll(dayInitializingMap);
	tempIniMap.putAll(initializingMap);
	
	routesList.each{route ->
		boothsList = (Collection)NetworkServices.getRouteBooths(delegator, route.facilityId);
		boothsList.each{booth ->
			
			if(!UtilValidate.isEmpty(boothRecoveryMap.get(booth))){
				tempDayIniMap["boothRecovery"] = (BigDecimal) boothRecoveryMap.get(booth);
			}
			boothMargins[booth] = [:];
			boothMargins[booth].putAll(tempDayIniMap);
			
			tempDayIniMap.putAll(dayInitializingMap);
			
			boothRoutes[booth] = route.facilityId;
			allBoothsList.addAll(booth);
		}
		boothMargins["routeTotals"] = [:];
		boothMargins["routeTotals"].putAll(tempIniMap);
		
		routeMargins[route.facilityId] = [:];
		routeMargins[route.facilityId].putAll(boothMargins);
		boothMargins.clear();
	}
	routeMargins["grandTotals"] = [:];
	routeMargins["grandTotals"].putAll(tempIniMap);
	
	Timestamp supplyDate = monthBegin;
	for(int i=1 ; i <= totalDays; i++){
		dayTotals =  NetworkServices.getDayTotals(dispatcher.getDispatchContext(), supplyDate, null, false, false, allBoothsList);
		Map boothTotals = [:];
		boothTotals.putAll(dayTotals.get("boothTotals"));
		if(boothTotals == null){
			continue;
		}
		Iterator treeMapBoothIter = boothTotals.entrySet().iterator();
		while (treeMapBoothIter.hasNext()) {
			Map.Entry boothEntry = (Entry) treeMapBoothIter.next();
			
			context.put("facilityId", boothEntry.getKey());
			routeId = boothRoutes.get(boothEntry.getKey());
			
			Map boothMarginsMap = [:];
			boothMarginsMap.putAll(routeMargins.get(routeId));
			Map grandTotalsMap = [:];
			grandTotalsMap.putAll(routeMargins.get("grandTotals"));
			
			Map dayWiseMargins = [:]; 
			dayWiseMargins.putAll(boothMarginsMap.get(boothEntry.getKey()));
			Map routeTotalsMap = [:];
			routeTotalsMap.putAll(boothMarginsMap.get("routeTotals"));
			
			suppDate = new SimpleDateFormat("dd/MM/yyyy").format(supplyDate);
			Map dayWiseDetails = [:];
			dayWiseDetails.putAll(dayWiseMargins.get(suppDate));
			Map dayWiseTotals = [:];
			dayWiseTotals.putAll(dayWiseMargins.get("boothTotals"));
			
			Map productTotals = (Map) ((boothEntry.getValue()).get("productTotals"));
			Iterator productTotalsIter = productTotals.entrySet().iterator();
			while (productTotalsIter.hasNext()) {
				Map.Entry productEntry = productTotalsIter.next();
				
				if(productEntry.getKey() in productIdListSet){
					
					if(productEntry.getKey() in sachetIdsSet){
						totalQty += (productEntry.getValue()).get("total");
					}
					checkTotalQty += (productEntry.getValue()).get("total");
					
					totalRev += (productEntry.getValue()).get("totalRevenue");
					dayWiseDetails[(productEntry.getKey())] = (((productEntry.getValue()).get("total")).toBigDecimal()).setScale(1, rounding);
					
					dayWiseTotals[(productEntry.getKey())] += (((productEntry.getValue()).get("total")).toBigDecimal()).setScale(1, rounding);
					routeTotalsMap[(productEntry.getKey())] += (((productEntry.getValue()).get("total")).toBigDecimal()).setScale(1, rounding);
					grandTotalsMap[(productEntry.getKey())] += (((productEntry.getValue()).get("total")).toBigDecimal()).setScale(1, rounding);
				
				}
			}
			
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , (boothEntry.getKey())));
			conditionList.add(EntityCondition.makeCondition("commissionDate",EntityOperator.EQUALS, supplyDate));
			conditionList.add(EntityCondition.makeCondition("periodBillingId",EntityOperator.EQUALS, periodBillingId));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
			netAmt = BigDecimal.ZERO;
			commissionsList = delegator.findList("FacilityAndCommission",condition,["commissionDate", "totalAmount", "dues", "recovery"] as Set, null, null, false);
			
			if(commissionsList.size() > 0){
				commissions = commissionsList[0];
				totalCommission = commissions.get("totalAmount");
				totalDues = BigDecimal.ZERO;
				recoveryAmt = BigDecimal.ZERO;
			}
			
			dayWiseDetails["TOTAL"] = totalQty.setScale(1, rounding);
			dayWiseDetails["totalRev"] = totalRev.setScale(2, rounding);
			dayWiseDetails["MAINTENANCE"] = (totalCommission.toBigDecimal()).setScale(2, rounding);
			dayWiseDetails["DUES"] = (totalDues.toBigDecimal()).setScale(2, rounding);
			dayWiseDetails["NET"] = ((netAmt).toBigDecimal()).setScale(2, rounding);
			dayWiseDetails["CHECK_TOTAL"] = checkTotalQty.setScale(1, rounding);
			
			dayWiseTotals["TOTAL"] += totalQty.setScale(1, rounding);
			dayWiseTotals["totalRev"] += totalRev.setScale(2, rounding);
			dayWiseTotals["AVG"] += ((totalQty/totalDays).toBigDecimal()).setScale(9, rounding);
			dayWiseTotals["MAINTENANCE"] += (totalCommission.toBigDecimal()).setScale(2, rounding);
			dayWiseTotals["DUES"] += (totalDues.toBigDecimal()).setScale(2, rounding);
			dayWiseTotals["NET"] = (netAmt).setScale(2, rounding);
			dayWiseTotals["CHECK_TOTAL"] += checkTotalQty.setScale(1, rounding);
			
			routeTotalsMap["TOTAL"] += totalQty.setScale(1, rounding);
			routeTotalsMap["totalRev"] += totalRev.setScale(2, rounding);
			routeTotalsMap["AVG"] += ((totalQty/totalDays).toBigDecimal()).setScale(5, rounding);
			routeTotalsMap["MAINTENANCE"] += (totalCommission.toBigDecimal()).setScale(2, rounding);
			routeTotalsMap["DUES"] += (totalDues.toBigDecimal()).setScale(2, rounding);
			routeTotalsMap["NET"] += ((netAmt).toBigDecimal()).setScale(2, rounding);
			routeTotalsMap["CHECK_TOTAL"] += checkTotalQty.setScale(1, rounding);
			
			grandTotalsMap["TOTAL"] += totalQty.setScale(1, rounding);
			grandTotalsMap["totalRev"] += totalRev.setScale(2, rounding);
			grandTotalsMap["AVG"] += ((totalQty/totalDays).toBigDecimal()).setScale(5, rounding);
			grandTotalsMap["MAINTENANCE"] += (totalCommission.toBigDecimal()).setScale(2, rounding);
			grandTotalsMap["DUES"] += (totalDues.toBigDecimal()).setScale(2, rounding);
			grandTotalsMap["NET"] += ((netAmt).toBigDecimal()).setScale(2, rounding);
			grandTotalsMap["CHECK_TOTAL"] += checkTotalQty.setScale(1, rounding);
			
			
			Map tempDayWiseMap = [:];
			tempDayWiseMap.putAll(dayWiseDetails);
			dayWiseMargins.put(suppDate, tempDayWiseMap);
			
			Map tempDayWiseTotMap = [:];
			tempDayWiseTotMap.putAll(dayWiseTotals);
			dayWiseMargins.put("boothTotals", tempDayWiseTotMap);
			
			Map tempboothMargins = [:];
			tempboothMargins.putAll(dayWiseMargins);
			boothMarginsMap.put((boothEntry.getKey()), tempboothMargins);
			
			Map tempRouteTotMap = [:];
			tempRouteTotMap.putAll(routeTotalsMap);
			boothMarginsMap.put("routeTotals", tempRouteTotMap);
			
			Map tempRouteMargins = [:];
			tempRouteMargins.putAll(boothMarginsMap);
			routeMargins.put(routeId, tempRouteMargins);
			
			Map tempgrandTotMap = [:];
			tempgrandTotMap.putAll(grandTotalsMap);
			routeMargins.put("grandTotals", tempgrandTotMap);
			
			netDues = 0;
			totalQty = BigDecimal.ZERO;
			checkTotalQty = BigDecimal.ZERO;
			totalRev = BigDecimal.ZERO;
			totalCommission = BigDecimal.ZERO;
			totalDues = BigDecimal.ZERO;
		}
		supplyDate = UtilDateTime.getNextDayStart(supplyDate);
	}
	
	netAmt = BigDecimal.ZERO;
	routeNetAmt = BigDecimal.ZERO;
	routeTotalDues = BigDecimal.ZERO;
	grandTotalDues = BigDecimal.ZERO;
	grandNetAmt = BigDecimal.ZERO;
	Iterator checkRouteMarginsIter = routeMargins.entrySet().iterator();
	while (checkRouteMarginsIter.hasNext()) {
		Map.Entry RouteEntry = checkRouteMarginsIter.next();
		
		Map tempRouteBooths = [:];
		tempRouteBooths = RouteEntry.getValue();
		Map CheckRouteTotalsMap = [:];
		if(RouteEntry.getKey() == "grandTotals"){
			checkGrandTotalsMap = [:];
			
			tempRouteBooths.put("DUES", grandTotalDues);
			tempRouteBooths.put("NET", grandNetAmt);
			
			continue;
		}
		CheckRouteTotalsMap.putAll(tempRouteBooths.get("routeTotals"));
		if((CheckRouteTotalsMap.get("CHECK_TOTAL")) == BigDecimal.ZERO){
			checkRouteMarginsIter.remove();
		}
		else{
			Iterator checkBoothMarginsIter = tempRouteBooths.entrySet().iterator();
			while (checkBoothMarginsIter.hasNext()) {
				Map.Entry BoothEntry = checkBoothMarginsIter.next();
				Map tempBoothMap = [:];
				tempBoothMap = BoothEntry.getValue();
				
				Map CheckBoothTotalsMap = [:];
				if(BoothEntry.getKey() == "routeTotals"){
					continue;
				}
				CheckBoothTotalsMap.putAll(tempBoothMap.get("boothTotals"));
				recoveryList.each{recoveryItem ->
					if((BoothEntry.getKey()) == recoveryItem.facilityId){
						if(UtilValidate.isNotEmpty(recoveryItem.dues)){
							totalDues = recoveryItem.dues;
						}
						if(UtilValidate.isNotEmpty(recoveryItem.recovery)){
							recoveryAmt = recoveryItem.recovery;
						}
						totalCommission = CheckBoothTotalsMap.get("MAINTENANCE");
						netAmt = totalCommission - totalDues - recoveryAmt;
						routeTotalDues = routeTotalDues + totalDues;
						grandTotalDues = grandTotalDues + totalDues;
						if(netAmt < 0 ){
							netAmt = 0
						}
						routeNetAmt = routeNetAmt + netAmt;
						grandNetAmt = grandNetAmt + netAmt;
						CheckBoothTotalsMap.put("DUES", totalDues);
						CheckBoothTotalsMap.put("NET", netAmt);
						tempBoothMap["boothTotals"] = CheckBoothTotalsMap;
						netAmt = BigDecimal.ZERO;
					}
				}
				if(((CheckBoothTotalsMap.get("CHECK_TOTAL")) == BigDecimal.ZERO) && totalDues == BigDecimal.ZERO && recoveryAmt == BigDecimal.ZERO){
					checkBoothMarginsIter.remove();
				}
				totalDues = BigDecimal.ZERO;
				recoveryAmt = BigDecimal.ZERO;
			}
		}
		CheckRouteTotalsMap.put("DUES", routeTotalDues);
		CheckRouteTotalsMap.put("NET", routeNetAmt);
		tempRouteBooths["routeTotals"] = CheckRouteTotalsMap;
		routeTotalDues = BigDecimal.ZERO;
		routeNetAmt = BigDecimal.ZERO;
	}
	
	masterList.add(routeMargins);
	context.put("masterList", masterList);
	
