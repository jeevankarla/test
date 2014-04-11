import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.byproducts.ByProductServices;

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

productNames = [:];
paymentMethodTypeList = [];
paymentMethodTypeMap = [:];
allProductsList = ByProductNetworkServices.getAllProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("salesDate",estimatedDeliveryDateTime));

allProductsList.each{ eachProd ->
	productNames.put(eachProd.productId, eachProd.brandName);
}
context.productNames = productNames;
paymentMethodTypeList = delegator.findList("PaymentMethodType", null,UtilMisc.toSet("paymentMethodTypeId","description"),null, null, false);
paymentMethodTypeList.each{ paymentMethodType ->
	paymentMethodTypeMap.put(paymentMethodType.paymentMethodTypeId ,paymentMethodType.description );
}
lmsProductsList=ProductWorker.getProductsByCategory(delegator ,"LMS" ,null);
byProductsList=  EntityUtil.filterByCondition(allProductsList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , lmsProductsList.productId));

lmsProductsIdsList=EntityUtil.getFieldListFromEntityList(lmsProductsList, "productId", false);
byProductsIdsList=EntityUtil.getFieldListFromEntityList(byProductsList, "productId", false);

crateProductsList=ProductWorker.getProductsByCategory(delegator ,"CRATE" ,null);
canProductsList=ProductWorker.getProductsByCategory(delegator ,"CAN" ,null);

crateProductsIdsList=EntityUtil.getFieldListFromEntityList(crateProductsList, "productId", false);
canProductsIdsList=EntityUtil.getFieldListFromEntityList(canProductsList, "productId", false);
shipments = [];
String vehicleId="";
if(parameters.shipmentId){
		shipmentId = parameters.shipmentId;
		shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", parameters.shipmentId), false);
		vehicleId=shipment.vehicleId;
		shipments.add(shipment.shipmentId);
}
	

result =ByProductNetworkServices.getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin",userLogin, "saleDate", estimatedDeliveryDateTime));
piecesPerCrate = result.get("piecesPerCrate");
piecesPerCan = result.get("piecesPerCan");

routeWiseMap =[:];
conditionList = [];
routeWiseTotalCrates = [:];
routeShipmentMap = [:];
routeTotals=[:];
		 Set lmsProductList =new HashSet();
		 Set byProdList =new HashSet();
		lmsProdSeqList=[];
		byProdSeqList=[];
		conditionList.clear();
		orderHeader = delegator.findList("OrderHeader", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, parameters.shipmentId), UtilMisc.toSet("originFacilityId"), null, null, false);
		boothsList = EntityUtil.getFieldListFromEntityList(orderHeader, "originFacilityId", true);
		
		if(UtilValidate.isNotEmpty(boothsList)){
			//ownerParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
			contractorName = "";
			if(UtilValidate.isNotEmpty(shipments)){
				dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime);
				context.putAt("dayBegin", dayBegin);
				dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);
				Map boothWiseMap =[:];
				boothsList.each{ boothId ->
					dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:UtilMisc.toList(parameters.shipmentId), facilityIds:UtilMisc.toList(boothId),fromDate:dayBegin, thruDate:dayEnd]);
					routeTotals=dayTotals;
					if(UtilValidate.isNotEmpty(dayTotals)){
						productTotals = dayTotals.get("productTotals");
						Map boothWiseProd= FastMap.newInstance();
						if(UtilValidate.isNotEmpty(productTotals)){
							//Calulating Crates
							totalQuantity =0;
							Iterator prodIter = productTotals.entrySet().iterator();
							cratesTotalSub = 0;
							noPacketsexc = 0;
							canTotalSub = 0;
							crateDivisior=12;
							packetTotal=0;
							subsidyTotal=0;
							productsCrateMap=[:];
							subsidyTotal = 0;
							amount=dayTotals.get("totalRevenue");
							while (prodIter.hasNext()) {
								Map.Entry entry = prodIter.next();
								 String itrProductId=entry.getKey();
								//if(lmsProductsIdsList.contains(itrProductId)){
									//crateDivisior=piecesPerCrate.getAt(itrProductId);
									qty=productTotals.get(entry.getKey()).get("packetQuantity");
									supplyTypeTotals=productTotals.get(entry.getKey()).get("supplyTypeTotals");
									if(supplyTypeTotals && supplyTypeTotals.get("EMP_SUBSIDY")){
										packetTotal=supplyTypeTotals.get("EMP_SUBSIDY").get("packetQuantity");
										subsidyRevenue = supplyTypeTotals.get("EMP_SUBSIDY").get("totalRevenue");
										subsidyTotal=subsidyTotal+packetTotal;
										qty = qty - packetTotal;
										tempMap = productTotals.get(entry.getKey())
										tempMap.packetQuantity = qty;
										productTotals.put(entry.getKey(), tempMap);
										empSubsidyAmt = subsidyPrice*packetTotal;
										amount = amount - subsidyRevenue + empSubsidyAmt;
									
									}
									totalQuantity=totalQuantity+qty;
									if(crateProductsIdsList.contains(itrProductId)){
										if(piecesPerCrate && piecesPerCrate.get(itrProductId)){
											int crateDivisior=(piecesPerCrate.get(itrProductId)).intValue();
										   tempCrates = (qty/(crateDivisior)).intValue();
										   tempExcess=((qty.intValue())%(crateDivisior.intValue()));
										   cratesTotalSub = cratesTotalSub+tempCrates;
										   noPacketsexc = noPacketsexc+tempExcess;
										   cratesMap =[:];
										   cratesMap["crates"]=tempCrates;
										   cratesMap["loosePkts"]=tempExcess;
										   productsCrateMap[itrProductId]=cratesMap;
									   }
										
									}
									if(canProductsIdsList.contains(itrProductId)){
										if(piecesPerCan && piecesPerCan.get(itrProductId)){
											int canDivisior=(piecesPerCan.get(itrProductId)).intValue();
										   tempCan = (qty/(canDivisior)).intValue();
										   tempCanExcess=((qty.intValue())%(canDivisior.intValue()));
										   canTotalSub = canTotalSub+tempCan;
									   }
										
									}
							}
							
							
							boothWiseProd.put("prodDetails", productTotals);
							boothWiseProd.put("productsCrateMap", productsCrateMap);
							boothWiseProd.put("amount", amount);
							boothWiseProd.put("vatAmount", dayTotals.get("totalVatRevenue"));
							boothWiseProd.put("crates", cratesTotalSub.intValue());
							boothWiseProd.put("excess", noPacketsexc);
							boothWiseProd.put("cans", canTotalSub.intValue());
							boothWiseProd.put("subsidy", subsidyTotal.intValue());
							boothWiseProd.put("paymentMode","CSH");
							boothWiseMap.put(boothId, boothWiseProd);
						}
					
					}
					
				}
				routeAmount=0;
				
				productWiseTotalCratesMap =[:];
				routeProdTotals=[:];
				routeTotQty=0;
				rtCrates = 0;
				rtExcessPkts = 0;
				rtCans = 0;
				
				rtEmpCrate = 0;
				rtEmpExcess = 0;
				addTotalSub = 0;
				rtLooseCans = 0;
				if(UtilValidate.isNotEmpty(routeTotals)){
					routeProdTotals = routeTotals.get("productTotals");
					routeAmount= routeTotals.get("totalRevenue");
					if(UtilValidate.isNotEmpty(routeProdTotals)){
						Iterator mapIter = routeProdTotals.entrySet().iterator();
						while (mapIter.hasNext()) {
							Map.Entry entry = mapIter.next();
							productId=entry.getKey();
							subsidyRtTotal = 0;
							
							//populating grand total crates and excess packets
							cratesDetailMap =[:];
							cratesDetailMap["prodCrates"]=0;
							cratesDetailMap["packetsExces"]=0;
							
							if(lmsProductsIdsList.contains(productId)){
								lmsProductList.add(productId);
							}else{
								byProdList.add(productId);
							}
							rtQty =entry.getValue().get("packetQuantity");
							//supplyTypeTotals = [];
							
							supplyTypeTotals=entry.getValue().get("supplyTypeTotals");
							if(supplyTypeTotals && supplyTypeTotals.get("EMP_SUBSIDY")){
								packetTotal=supplyTypeTotals.get("EMP_SUBSIDY").get("packetQuantity");
								subsidyRev = supplyTypeTotals.get("EMP_SUBSIDY").get("totalRevenue");
								subsidyRtTotal = subsidyRtTotal+packetTotal;
								addTotalSub = addTotalSub+packetTotal;
								totalSubsidy = subsidyPrice*packetTotal;
								routeAmount = routeAmount - subsidyRev + totalSubsidy;
								rtQty = rtQty - packetTotal;
								tempMap = routeProdTotals.get(productId)
								tempMap.packetQuantity = rtQty;
								routeProdTotals.put(productId, tempMap);
								
							}
							if(subsidyRtTotal>0){
								if(crateProductsIdsList.contains(productId)){
									if(piecesPerCrate && piecesPerCrate.get(productId)){
										int crateDiv=(piecesPerCrate.get(productId)).intValue();
										tempRtEmpCrates = (subsidyRtTotal/(crateDiv)).intValue();
										tempRtEmpExcess=((subsidyRtTotal.intValue())%(crateDiv.intValue()));
										/*if(tempRtEmpExcess>0){
											tempRtEmpExcess = 1;
										}*/
										rtEmpCrate = rtEmpCrate+tempRtEmpCrates;
										rtEmpExcess = rtEmpExcess+tempRtEmpExcess;
									}//+tempRtEmpExcess
									
								}
							}
							routeTotQty=routeTotQty+rtQty;
							if(crateProductsIdsList.contains(productId)){
								if(piecesPerCrate && piecesPerCrate.get(productId)){
									int crateDivisior=(piecesPerCrate.get(productId)).intValue();
									tempRtCrates = (rtQty/(crateDivisior)).intValue();
									tempRtExcess=((rtQty.intValue())%(crateDivisior.intValue()));
									rtCrates = rtCrates+tempRtCrates;
									rtExcessPkts = rtExcessPkts+tempRtExcess;
									cratesDetailMap.put("prodCrates", tempRtCrates);
									cratesDetailMap.put("packetsExces", tempRtExcess);
								}
								
							}
							if(canProductsIdsList.contains(productId)){
								if(piecesPerCan && piecesPerCan.get(productId)){
									int canDivisior=(piecesPerCan.get(productId)).intValue();
								   tempRtCan = (rtQty/(canDivisior)).intValue();
								   tempLooseRtCan=((rtQty.intValue())%(canDivisior));
								   if(tempLooseRtCan>0){
								   rtLooseCans=rtLooseCans+1;
								   }
								   rtCans = rtCans+tempRtCan;
							   }
								
							}
							/*tempRtCrates = (rtQty/12).intValue();
							tempRtExcess=(rtQty.intValue()%(12));
							if(piecesPerCrate && piecesPerCrate.get(productId)){
								tempRtCrates = (rtQty/(piecesPerCrate.get(productId))).intValue();
								tempRtExcess=(rtQty.intValue()%(piecesPerCrate.get(productId).intValue()));
							}*/
							//rtCrates = rtCrates+tempRtCrates;
							productWiseTotalCratesMap.put(productId, cratesDetailMap);
						}
							
							
						}
					}
					lmsProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, lmsProductList) , null, ["sequenceNum"], null, false);
					lmsProdIdsList= EntityUtil.getFieldListFromEntityList(lmsProdSeqList, "productId", true);
					byProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, byProdList) , null, ["sequenceNum"], null, false);
					byProdIdsList= EntityUtil.getFieldListFromEntityList(byProdSeqList, "productId", true);
					
					 //getting vehicle role for vehcileId
				
					
					tempRtEmpExcess = 0
					if(rtEmpExcess>0){
						tempRtEmpExcess = 1;
					}
					customRtCrates = rtCrates+tempRtEmpExcess+rtEmpCrate;
					Map boothDetailsMap=FastMap.newInstance();
					boothDetailsMap.put("boothWiseMap", boothWiseMap);
					boothDetailsMap.put("lmsProdList", lmsProdIdsList);
					boothDetailsMap.put("byProdList", byProdIdsList);
					boothDetailsMap.put("routeWiseTotals", routeProdTotals);
					boothDetailsMap.put("routeWiseCrates", productWiseTotalCratesMap);
					boothDetailsMap.put("routeTotalSubsidy", addTotalSub);
					boothDetailsMap.put("routeEmpCrates", rtEmpCrate);
					boothDetailsMap.put("routeEmpPcks", rtEmpExcess);
					boothDetailsMap.put("routeTotalCrate", customRtCrates);
					boothDetailsMap.put("routeAmount", routeAmount);
					boothDetailsMap.put("routeVatAmount", routeTotals.get("totalVatRevenue"));
					boothDetailsMap.put("contractorName", contractorName);
					boothDetailsMap.put("vehicleId", vehicleId);
					boothDetailsMap.put("rtCrates", rtCrates);
					boothDetailsMap.put("rtExcessPkts", rtExcessPkts);
					boothDetailsMap.put("rtCans", rtCans);
					boothDetailsMap.put("rtLooseCans", rtLooseCans);
					if(UtilValidate.isNotEmpty(boothWiseMap)){
						routeWiseMap.put("routeId", boothDetailsMap);
					}
					routeWiseTotalCrates.put("routeId", productWiseTotalCratesMap);
				}
			}
context.routeShipmentMap = routeShipmentMap;
context.put("routeWiseMap",routeWiseMap);
context.putAt("routeWiseTotalCrates", routeWiseTotalCrates);
