import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import org.ofbiz.network.NetworkServices;


dctx = dispatcher.getDispatchContext();
routeIdsList =[];
shipmentIds = [];
Timestamp estimatedDeliveryDateTime = null;
if(parameters.estimatedShipDate){
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	try {
		estimatedDeliveryDateTime = new java.sql.Timestamp(formatter.parse(parameters.estimatedShipDate).getTime());
		
	} catch (ParseException e) {
	}
}
context.put("estimatedDeliveryDate", estimatedDeliveryDateTime);
if(parameters.shipmentId){
	if(parameters.shipmentId == "allRoutes"){
		shipments = delegator.findByAnd("Shipment", [estimatedShipDate : estimatedDeliveryDateTime , shipmentTypeId : parameters.shipmentTypeId ],["routeId"]);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false));
		routeIdsList.addAll(EntityUtil.getFieldListFromEntityList(shipments, "routeId", false))
	}else{
		shipmentId = parameters.shipmentId;
		shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
		shipmentIds.add(shipmentId);
		routeIdsList.add(shipment.routeId);
	}
	
}

routeWiseMap =[:];
routeWiseTotalCrates = [:];
if(UtilValidate.isNotEmpty(routeIdsList)){
	routeIdsList.each{ routeId ->
		lmsProductList =[];
		byProdList =[];
		lmsProdSeqList=[];
		byProdSeqList=[];
		boothsList = (NetworkServices.getRouteBooths(delegator , routeId));
		if(UtilValidate.isNotEmpty(boothsList)){
			ownerParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
			partyId = ownerParty.ownerPartyId;
			condList = [];
			condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
			condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"));
			cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			contractors = delegator.findList("Facility", cond, UtilMisc.toSet("facilityName"), null, null, false);
			contractor = EntityUtil.getFirst(contractors);
			contractorName = ""
			if(contractor){
				contractorName = contractor.getString("facilityName");
			} 
			if(UtilValidate.isNotEmpty(shipmentIds)){
				dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime);
				context.putAt("dayBegin", dayBegin);
				dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);
				Map boothWiseMap =[:];
				boothsList.each{ boothId ->
					dayTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, facilityIds:UtilMisc.toList(boothId),fromDate:dayBegin, thruDate:dayEnd]);
					if(UtilValidate.isNotEmpty(dayTotals)){
						productTotals = dayTotals.get("productTotals");		
						Map boothWiseProd= FastMap.newInstance();						
						if(UtilValidate.isNotEmpty(productTotals)){
							//Calulating Crates
							totalQuantity =0;
							Iterator prodIter = productTotals.entrySet().iterator();
							while (prodIter.hasNext()) {
								Map.Entry entry = prodIter.next();
								conList=[];
								conList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,entry.getKey()));
								condition = EntityCondition.makeCondition(conList,EntityOperator.AND);
								prodCategoryList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
								prodCatIds= EntityUtil.getFieldListFromEntityList(prodCategoryList, "productCategoryId", true);
								prodCatIds.each{ prodCategory->
									if("LMS".equals(prodCategory)){
										qty=productTotals.get(entry.getKey()).get("total");										
										totalQuantity=totalQuantity+qty;
									}									
								}
							}
							cratesTotalSub =(totalQuantity/(12));
							noPacketsexc = (totalQuantity.intValue()%12);
							amount=dayTotals.get("totalRevenue");
							boothWiseProd.put("prodDetails", productTotals);
							boothWiseProd.put("amount", amount);
							boothWiseProd.put("crates", cratesTotalSub.intValue());
							boothWiseProd.put("excess", noPacketsexc);
							boothWiseMap.put(boothId, boothWiseProd);							
						}
					}
					
				}
				routeTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, facilityIds:boothsList,fromDate:dayBegin, thruDate:dayEnd]);
				routeAmount=0;
				
				productWiseTotalCratesMap =[:];
				routeTotQty=0;
				if(UtilValidate.isNotEmpty(routeTotals)){
					routeProdTotals = routeTotals.get("productTotals");
					routeAmount= routeTotals.get("totalRevenue");
					if(UtilValidate.isNotEmpty(routeProdTotals)){
						Iterator mapIter = routeProdTotals.entrySet().iterator();	
						while (mapIter.hasNext()) {
							Map.Entry entry = mapIter.next();
							conditionList=[];
							conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,entry.getKey()));
							condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
							productCategoryList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
							prodCategoryIds= EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
							
							//populating grand total crates and excess packets
							cratesDetailMap =[:];
							cratesDetailMap["prodCrates"]=0;
							cratesDetailMap["packetsExces"]=0;
							
							prodCategoryIds.each{ prodCategory->
								if("LMS".equals(prodCategory)){
									lmsProductList.add(entry.getKey());
									rtQty =entry.getValue().get("total");
									routeTotQty=routeTotQty+rtQty;
								}
								if("BYPROD".equals(prodCategory)){
									byProdList.add(entry.getKey());
								}
								if("CRATE_INDENT".equals(prodCategory)){
									qtyValue=entry.getValue().get("total");
									prodCrates =(qtyValue/(12)).intValue();
									packetsExces = (qtyValue.intValue()%12);
									cratesDetailMap.put("prodCrates", prodCrates);
									cratesDetailMap.put("packetsExces", packetsExces);
								}
							}					
							productWiseTotalCratesMap.put(entry.getKey(), cratesDetailMap);
						}						
					}			
					
					lmsProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, lmsProductList) , null, ["sequenceNum"], null, false);
					lmsProdIdsList= EntityUtil.getFieldListFromEntityList(lmsProdSeqList, "productId", true);
					byProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, byProdList) , null, ["sequenceNum"], null, false);
					byProdIdsList= EntityUtil.getFieldListFromEntityList(byProdSeqList, "productId", true);
					//route wise crates
					rtCrates = (routeTotQty/(12)).intValue();
					rtExcessPkts=(routeTotQty.intValue()%12);
					Map boothDetailsMap=FastMap.newInstance();
					boothDetailsMap.put("boothWiseMap", boothWiseMap);
					boothDetailsMap.put("lmsProdList", lmsProdIdsList);
					boothDetailsMap.put("byProdList", byProdIdsList);
					boothDetailsMap.put("routeWiseTotals", routeProdTotals);
					boothDetailsMap.put("routeWiseCrates", productWiseTotalCratesMap);
					boothDetailsMap.put("routeAmount", routeAmount);
					boothDetailsMap.put("contractorName", contractorName);
					boothDetailsMap.put("rtCrates", rtCrates);
					boothDetailsMap.put("rtExcessPkts", rtExcessPkts);
					if(UtilValidate.isNotEmpty(boothWiseMap)){
						routeWiseMap.put(routeId, boothDetailsMap);
					}
					routeWiseTotalCrates.put(routeId, productWiseTotalCratesMap);
				}				
			}
		}		
	}
}
context.put("routeWiseMap",routeWiseMap);
context.putAt("routeWiseTotalCrates", routeWiseTotalCrates);
