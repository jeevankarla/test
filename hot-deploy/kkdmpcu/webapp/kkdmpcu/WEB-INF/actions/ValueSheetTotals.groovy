	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;

	import java.text.SimpleDateFormat;
	import java.util.*;
	import java.lang.*;
	import java.math.BigDecimal;

	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import org.ofbiz.base.util.UtilDateTime;
	import org.ofbiz.network.NetworkServices;
	import org.ofbiz.network.LmsServices;
	
	valueSheetTotalsMap = [:];
	routeIds = [];
	routePrevDue = BigDecimal.ZERO;
	routeDueAmount = BigDecimal.ZERO;
	routeOldDues = BigDecimal.ZERO;
	routePaidAmount = BigDecimal.ZERO;
	cratesIssued = BigDecimal.ZERO;
	totalLitres = BigDecimal.ZERO;
	currentPendingDues = BigDecimal.ZERO;
	todaysPayment = BigDecimal.ZERO;
	oldDues = BigDecimal.ZERO;
	productId = null;
	
	if(parameters.facilityId) {
	   routeId = parameters.facilityId;
	   routeIds.add(routeId);
	}
	else{
		routeIdsList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ROUTE") , ["facilityId"] as Set, null, null, false);
		routeIds = EntityUtil.getFieldListFromEntityList(routeIdsList, "facilityId", false);
	}
	paymentDate = UtilDateTime.nowDateString("yyyy-MM-dd");
	if(parameters.estimatedDeliveryDate){
		paymentDate = parameters.estimatedDeliveryDate;
	}
	TotalSalesList = context.truckSheetReportList;
	
	boothsPaymentsDetail = [:];
	facilityValuesMap = [:];
	tempValuesMap = [:];
	tempValuesMap["facilityId"] = null;
	tempValuesMap["facilityType"] = null;
	tempValuesMap["paidAmount"] = BigDecimal.ZERO;
	tempValuesMap["PREV_DUE"] = BigDecimal.ZERO;
	tempValuesMap["cardSale"] = BigDecimal.ZERO;
	tempValuesMap["CARD_AMOUNT"] = BigDecimal.ZERO;
	tempValuesMap["total"] = BigDecimal.ZERO;
	tempValuesMap["totalAmount"] = BigDecimal.ZERO;
	tempValuesMap["cratesIssued"] = BigDecimal.ZERO;
	tempValuesMap["oldDues"] = BigDecimal.ZERO;
	tempValuesMap["dueAmount"] = BigDecimal.ZERO;
	tempValuesMap["transporterDues"] = BigDecimal.ZERO;
	facilityValuesMap.putAll(tempValuesMap);
	
	Map boothWiseTotalsMap = FastMap.newInstance();
	routeWiseTotalsList = [];
	boothWiseTotalsMap = FastMap.newInstance();
	TotalSalesList.each{ boothWiseSalesMap ->
		Map boothMap = FastMap.newInstance();
		
		if(facilityValuesMap["facilityId"] != null && facilityValuesMap["facilityType"] == "BOOTH"){
			todaysPayment = facilityValuesMap["totalAmount"] - currentPendingDues;
			facilityValuesMap["oldDues"] = facilityValuesMap["paidAmount"] - todaysPayment;
			if(facilityValuesMap["oldDues"] < 1){
				facilityValuesMap["oldDues"] = 0;
			}
			routeOldDues += facilityValuesMap["oldDues"];
			
			facilityId = facilityValuesMap["facilityId"];
			temporaryMap = [:];
			temporaryMap.putAll(facilityValuesMap);
			boothWiseTotalsMap.put(facilityId, temporaryMap);
			facilityValuesMap.clear();
			facilityValuesMap.putAll(tempValuesMap);
			productId = null;
		}
		if(facilityValuesMap["facilityId"] != null && facilityValuesMap["facilityType"] == "ROUTE"){
			
			nonExistingPrevDue = BigDecimal.ZERO;
			nonExistingPaidAmount = BigDecimal.ZERO;
			facilityId = facilityValuesMap["facilityId"];
			temporaryRouteMap = [:];
			tempRouteWiseTotalsMap = [:]
			Map routeMap = FastMap.newInstance();
			
			existingBoothIds = [];
			Iterator boothIter = boothWiseTotalsMap.entrySet().iterator();
			while (boothIter.hasNext()) {
				Map.Entry boothEntry = boothIter.next();
				boothId = null;
				boothId = boothEntry.getKey();
				existingBoothIds.add(boothId);
			}
			HashSet existingBoothIdsHashSet = new HashSet(existingBoothIds);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp paymentDateTimestamp = new java.sql.Timestamp(formatter.parse(paymentDate).getTime());
			
			Map transporterDuesMap = LmsServices.getTransporterDues(dctx, UtilMisc.toMap("userLogin", userLogin, "fromDate", paymentDateTimestamp, "thruDate", paymentDateTimestamp, "invoiceTypeId", "TRANSPORTER_PAYIN", "facilityId", facilityId));
			List transporterDueList = (List) transporterDuesMap.get("transporterDuesList");
			BigDecimal transptDueAmt = BigDecimal.ZERO;
			if (transporterDueList.size() != 0) {
				Map transporterDue = (Map) transporterDueList.get(0);
				transptDueAmt = (BigDecimal) transporterDue.get("amount");
			}
			
			boothsPaymentsDetail = NetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate , facilityId:facilityId]);
			paymentsList = boothsPaymentsDetail.get("boothPaymentsList");
			tempPaymentMap = [:];
			additionalBoothMap = [:];
			paymentsList.each{ paymentItem ->
				  paymentFacilityId = null;
				  tempFacilityId = null;
				  Iterator paymentIter = paymentItem.entrySet().iterator();
				  while (paymentIter.hasNext()) {
					  Map.Entry paymentEntry = paymentIter.next();
					  if(paymentEntry.getKey() == "facilityId") {
						  paymentFacilityId = paymentEntry.getValue();
					  }
					  if(!existingBoothIdsHashSet.contains(paymentFacilityId)) {
						  tempPaymentMap["facilityId"] = paymentFacilityId;
						  tempFacilityId = paymentFacilityId;
						  
						  tempPaymentMap["facilityType"]= "BOOTH";
						  if(paymentEntry.getKey() == "amount") {
							  tempPaymentMap["paidAmount"] = paymentEntry.getValue();
							  nonExistingPaidAmount += paymentEntry.getValue(); 
						  }
						  Map<String, Object> boothPayments = NetworkServices.getBoothPayments(delegator, dctx.getDispatcher(), userLogin,
							  paymentDate, null, tempFacilityId ,null ,Boolean.FALSE);
						  Map boothTotalDues = FastMap.newInstance();
						  List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
						  if (boothPaymentsList.size() > 0) {
							  boothTotalDues = (Map)boothPaymentsList.get(0);
							  tempPaymentMap["PREV_DUE"] = ((BigDecimal)boothTotalDues.getAt("totalDue")).subtract((BigDecimal)boothTotalDues.getAt("grandTotal"));
							  nonExistingPrevDue += ((BigDecimal)boothTotalDues.getAt("totalDue")).subtract((BigDecimal)boothTotalDues.getAt("grandTotal"));
						  }	  	
						  tempPaymentMap["cardSale"]= 0;
						  tempPaymentMap["CARD_AMOUNT"]= 0;
						  tempPaymentMap["total"]= 0;
						  tempPaymentMap["totalAmount"]= 0;
						  tempPaymentMap["cratesIssued"]= 0;
					  }
				  }
				  tempMap = [:];
				  tempMap.putAll(tempPaymentMap);
				  if(tempFacilityId != null){
					  if(additionalBoothMap.get(tempFacilityId) == null){
						  additionalBoothMap.put(tempFacilityId, tempMap);
					  }
					  else {
						  updateMap = [:];
						  updateMap = additionalBoothMap.get(tempFacilityId);
						  paidAmount = updateMap.get("paidAmount");
						  paidAmount = paidAmount + tempPaymentMap.get("paidAmount");
						  updateMap["paidAmount"] = paidAmount;
						  additionalBoothMap.put(tempFacilityId, updateMap);
					  }
				  }
			}
			
			facilityValuesMap["paidAmount"] = routePaidAmount + nonExistingPaidAmount;
			facilityValuesMap["PREV_DUE"] = routePrevDue + nonExistingPrevDue;
			facilityValuesMap["dueAmount"] = routeDueAmount;
			cratesIssued = ((BigDecimal)(totalLitres/12)).setScale(2, rounding);
			facilityValuesMap["cratesIssued"] = cratesIssued;
			facilityValuesMap["oldDues"] = routeOldDues;
			facilityValuesMap["transporterDues"] = transptDueAmt;
			
			
			tempRouteWiseTotalsMap.putAll(facilityValuesMap);
			temporaryRouteMap.put(facilityId, tempRouteWiseTotalsMap);
			
			helperMap = [:];
			helperMap.putAll(boothWiseTotalsMap)
			TreeMap tempMap = new TreeMap();
			tempMap.putAll(helperMap);
			if(UtilValidate.isNotEmpty(additionalBoothMap)) {
			   tempMap.putAll(additionalBoothMap);
			}
			tempMap.putAll(temporaryRouteMap);
			routeMap[facilityValuesMap["facilityId"]] = tempMap;
			routeWiseTotalsList.add(routeMap);
			facilityValuesMap.clear();
			facilityValuesMap.putAll(tempValuesMap);
			routePrevDue = BigDecimal.ZERO;
			routeDueAmount = BigDecimal.ZERO;
			routeOldDues = BigDecimal.ZERO;
			routePaidAmount = BigDecimal.ZERO;
			cratesIssued = BigDecimal.ZERO;
			totalLitres = BigDecimal.ZERO;
			boothWiseTotalsMap.clear();
		}
		
		Iterator treeMapIter = boothWiseSalesMap.entrySet().iterator();
		while (treeMapIter.hasNext()) {
			Map.Entry entry = treeMapIter.next();
			if(boothWiseSalesMap.facilityType == "BOOTH") {
				if(entry.getKey() == "facilityType") {
					facilityValuesMap["facilityType"] = entry.getValue();
				}
				else if(entry.getKey() == "facilityId") {
					facilityValuesMap["facilityId"] = entry.getValue();
					boothsPaymentsDetail = NetworkServices.getBoothReceivablePayments(delegator, dispatcher, userLogin, paymentDate, null, facilityValuesMap.get("facilityId"), null, Boolean.TRUE ,Boolean.TRUE);
					currentPendingDues = boothsPaymentsDetail.get("invoicesTotalDueAmount");
					facilityValuesMap["dueAmount"] = currentPendingDues;
					routeDueAmount = routeDueAmount + facilityValuesMap["dueAmount"];
				}
				else if(entry.getKey() == "paidAmount") {
					facilityValuesMap["paidAmount"] = entry.getValue();
					if (facilityValuesMap["paidAmount"] == null){
						facilityValuesMap["paidAmount"] = 0;
					}
					routePaidAmount = routePaidAmount + facilityValuesMap["paidAmount"];
				}
				else if(entry.getKey() == "PREV_DUE") {
					facilityValuesMap["PREV_DUE"] = entry.getValue();
					routePrevDue = routePrevDue + facilityValuesMap["PREV_DUE"];
				}
				else {
					productId = entry.getKey();
					productSales = entry.getValue();
					tempProdMap = [:];
					facilityValuesMap["cardSale"] += productSales.get("CARD");
					facilityValuesMap["CARD_AMOUNT"] += productSales.get("CARD_AMOUNT");
					facilityValuesMap["total"] += productSales.get("TOTAL");
					facilityValuesMap["totalAmount"] += productSales.get("TOTALAMOUNT");
					tempProdMap["prodCardSale"] = productSales.get("CARD");
					tempProdMap["prodTotalSale"] = productSales.get("TOTAL");
					tempProdMap["prodTotalValue"] = (productSales.get("TOTALAMOUNT")).setScale(2, rounding);
					
					facilityValuesMap[productId] = tempProdMap;
					
				}
			}
			else if(boothWiseSalesMap.facilityType == "ROUTE") {
				
				if(entry.getKey() == "facilityType") {
					facilityValuesMap["facilityType"] = entry.getValue();
				}
				else if(entry.getKey() == "facilityId") {
					facilityValuesMap["facilityId"] = entry.getValue();
				}
				
				else {
					productId = entry.getKey();
					productSales = entry.getValue();
					tempProdMap = [:];
					facilityValuesMap["cardSale"] += productSales.get("CARD");
					facilityValuesMap["CARD_AMOUNT"] += productSales.get("CARD_AMOUNT");
					facilityValuesMap["total"] += productSales.get("TOTAL");
					facilityValuesMap["totalAmount"] += productSales.get("TOTALAMOUNT");
					tempProdMap["prodCardSale"] = productSales.get("CARD");
					tempProdMap["prodTotalSale"] = productSales.get("TOTAL");
					tempProdMap["prodTotalValue"] = productSales.get("TOTALAMOUNT");
					
					facilityValuesMap[productId] = tempProdMap;
					totalLitres += productSales.get("LITRES");
				}
			}
		}
	}
	routeIdsList = [];
	routeWiseList = [];
	routeWiseTotalsList.each{ routeWiseTotals ->
	
		Iterator routeIter = routeWiseTotals.entrySet().iterator();
		while (routeIter.hasNext()) {
			Map.Entry routeEntry = routeIter.next();
			
			for(i=0; i<(routeIds.size()); i++) {
				routeId = routeIds.get(i);
				if(routeEntry.getKey() == routeId){
					routeValues = routeEntry.getValue();
					Map routeWiseMap = FastMap.newInstance();
					routeWiseMap[routeEntry.getKey()]= routeValues;
					tempRouteId = routeEntry.getKey();
					routeIdsList.add(tempRouteId);
					routeWiseList.add(routeWiseMap);
				}
			}
		}
	}
	Debug.logImportant("routeWiseList=" + routeWiseList, "");
	context.put("routeWiseList", routeWiseList);

