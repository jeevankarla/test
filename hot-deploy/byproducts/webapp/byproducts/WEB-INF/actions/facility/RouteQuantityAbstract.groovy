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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;

dctx = dispatcher.getDispatchContext();
routeIdsList = ByProductNetworkServices.getRoutes(dctx,context).get("routesList");

effectiveDate = null;
effectiveDateStr = parameters.supplyDate;

if (UtilValidate.isNotEmpty(effectiveDateStr)) {
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
}
dayBegin = UtilDateTime.getDayStart(effectiveDate);

productNames = [:];
allProductsList = ByProductNetworkServices.getAllProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("salesDate",effectiveDate));

allProductsList.each{ eachProd ->
	productNames.put(eachProd.productId, eachProd.brandName);
}
context.productNames = productNames;

lmsProductList=[];
byProdList=[];
lmsProdSeqList=[];
byProdSeqList=[];

lmsProductsList=ProductWorker.getProductsByCategory(delegator ,"LMS" ,null);
byProductsList=  EntityUtil.filterByCondition(allProductsList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , lmsProductsList.productId));

lmsProductsIdsList=EntityUtil.getFieldListFromEntityList(lmsProductsList, "productId", false);
byProductsIdsList=EntityUtil.getFieldListFromEntityList(byProductsList, "productId", false);


context.putAt("dayBegin", dayBegin);
dayEnd = UtilDateTime.getDayEnd(effectiveDate);
List shipmentIds  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
amShipmentIds = ByProductNetworkServices.getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"AM");
pmShipmentIds = ByProductNetworkServices.getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"PM");

amProductTotals=[:];
pmProductTotals=[:];
if(UtilValidate.isNotEmpty(amShipmentIds)){
	amDayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:amShipmentIds, fromDate:dayBegin, thruDate:dayEnd]);
	if(UtilValidate.isNotEmpty(amDayTotals)){
		amProductTotals=amDayTotals.get("productTotals");
	}
}
if(UtilValidate.isNotEmpty(pmShipmentIds)){
	pmDayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:pmShipmentIds, fromDate:dayBegin, thruDate:dayEnd]);
	if(UtilValidate.isNotEmpty(pmDayTotals)){
		pmProductTotals=pmDayTotals.get("productTotals");
	}
}

totLmsQty=0;

if(UtilValidate.isNotEmpty(shipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, fromDate:dayBegin, thruDate:dayEnd]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		prodTotals = dayTotals.get("productTotals");
		if(UtilValidate.isNotEmpty(prodTotals)){
			context.putAt("grandProdTotals", prodTotals);
			Iterator mapIter = prodTotals.entrySet().iterator();		
			while (mapIter.hasNext()) {
				Map.Entry entry = mapIter.next();
				 productId=entry.getKey();
				if(lmsProductsIdsList.contains(productId)){
					lmsProductList.add(productId);
					rtQty =entry.getValue().get("total");
					totLmsQty=totLmsQty+rtQty;
				}else{
				byProdList.add(entry.getKey());
				}
				
				/*conditionList=[];
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,entry.getKey()));
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				productCategoryList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
				prodCategoryIds= EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
				prodCategoryIds.each{ prodCategory->
					if("LMS".equals(prodCategory)){
						lmsProductList.add(entry.getKey());
						rtQty =entry.getValue().get("total");
						totLmsQty=totLmsQty+rtQty;
					}
					if("BYPROD".equals(prodCategory)){
						byProdList.add(entry.getKey());
					}					
				}*/
				
			}
		}
	}
}

lmsProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, lmsProductList) , null, ["sequenceNum"], null, false);
lmsProdIdsList= EntityUtil.getFieldListFromEntityList(lmsProdSeqList, "productId", true);
byProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, byProdList) , null, ["sequenceNum"], null, false);
byProdIdsList= EntityUtil.getFieldListFromEntityList(byProdSeqList, "productId", true);

context.put("lmsProductList", lmsProdIdsList);
context.put("byProdList", byProdIdsList);

routeWiseMap =[:];

amRouteWiseMap=[:];
pmRouteWiseMap=[:];
amTotalAmount=0;
amTotalQty=0;
amTotalReceipts=0;

pmTotalAmount=0;
pmTotalQty=0;
pmTotalReceipts=0;
if(UtilValidate.isNotEmpty(routeIdsList)){
	routeIdsList.each{ routeId ->
		/*lmsProductList =[];
		byProdList =[];*/
		boothsList = (ByProductNetworkServices.getRouteBooths(delegator , routeId));
		conList=[];
		conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,routeId));
		cond = EntityCondition.makeCondition(conList,EntityOperator.AND);
		facilityGroupMemberList = delegator.findList("FacilityGroupMember",cond,null,null,null,false);
		totAmLmsQty=0;
		totPmLmsQty=0;
		facilityGroupMemberList.each{ facilityGroup->
			if("AM_RT_GROUP".equals(facilityGroup.facilityGroupId)){
				if(UtilValidate.isNotEmpty(boothsList)){
					if(UtilValidate.isNotEmpty(amShipmentIds)){
						amRouteTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:amShipmentIds, facilityIds:boothsList,fromDate:dayBegin, thruDate:dayEnd]);
						routeAmount=0;
						routeTotQty=0;
						if(UtilValidate.isNotEmpty(amRouteTotals)){
							routeProdTotals = amRouteTotals.get("productTotals");
							routeAmount= amRouteTotals.get("totalRevenue");
							routePaidDetails = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate: effectiveDateStr , facilityId:routeId]);
							reciepts = 0;
							if(UtilValidate.isNotEmpty(routePaidDetails)){
								reciepts = routePaidDetails.get("invoicesTotalAmount");
							}
							//populating route wise am lms product totals
							
							Iterator mapRouteIter = routeProdTotals.entrySet().iterator();
							while (mapRouteIter.hasNext()) {
								Map.Entry entry = mapRouteIter.next();
								conditionList=[];
								conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,entry.getKey()));
								condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
								productCategoryList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
								prodCategoryIds= EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
								prodCategoryIds.each{ prodCategory->
									if("LMS".equals(prodCategory)){
										rtQty =entry.getValue().get("total");
										totAmLmsQty=totAmLmsQty+rtQty;
									}									
								}
							}
							
							amTotalAmount=amTotalAmount+routeAmount;
							amTotalQty=amTotalQty+totAmLmsQty;
							amTotalReceipts=amTotalReceipts+reciepts;
							if(routeAmount !=0){
								Map routeDetailsMap=FastMap.newInstance();
								routeDetailsMap.put("routeWiseTotals", routeProdTotals);
								routeDetailsMap.put("routeTotQty", totAmLmsQty);
								routeDetailsMap.put("routeAmount", routeAmount);
								routeDetailsMap.put("reciepts", reciepts);
								if(UtilValidate.isNotEmpty(routeDetailsMap)){
									amRouteWiseMap.put(routeId, routeDetailsMap);									
								}
							}
						}
					}
				}
				
			}
			if("PM_RT_GROUP".equals(facilityGroup.facilityGroupId)){
				if(UtilValidate.isNotEmpty(boothsList)){
					if(UtilValidate.isNotEmpty(pmShipmentIds)){
						pmRouteTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:pmShipmentIds, facilityIds:boothsList,fromDate:dayBegin, thruDate:dayEnd]);
						routeAmount=0;
						routeTotQty=0;
						if(UtilValidate.isNotEmpty(pmRouteTotals)){
							routeProdTotals = pmRouteTotals.get("productTotals");
							routeAmount= pmRouteTotals.get("totalRevenue");
							routeTotQty= pmRouteTotals.get("totalQuantity");
							routePaidDetails = ByProductNetworkServices.getBoothPaidPayments( dctx , [paymentDate: effectiveDateStr , facilityId:routeId]);
							reciepts = 0;
							if(UtilValidate.isNotEmpty(routePaidDetails)){
								reciepts = routePaidDetails.get("invoicesTotalAmount");
							}
							
							//populating route wise pm lms product totals
							
							Iterator mapRouteIter = routeProdTotals.entrySet().iterator();
							while (mapRouteIter.hasNext()) {
								Map.Entry entry = mapRouteIter.next();
								conditionList=[];
								conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,entry.getKey()));
								condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
								productCategoryList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
								prodCategoryIds= EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
								prodCategoryIds.each{ prodCategory->
									if("LMS".equals(prodCategory)){
										rtQty =entry.getValue().get("total");
										totPmLmsQty=totPmLmsQty+rtQty;
									}
								}
							}
							
							pmTotalAmount=pmTotalAmount+routeAmount;
							pmTotalQty=pmTotalQty+totPmLmsQty;
							pmTotalReceipts=pmTotalReceipts+reciepts;
							if(routeAmount !=0){
								Map routeDetailsMap=FastMap.newInstance();
								routeDetailsMap.put("routeWiseTotals", routeProdTotals);
								routeDetailsMap.put("routeTotQty", totPmLmsQty);
								routeDetailsMap.put("routeAmount", routeAmount);
								routeDetailsMap.put("reciepts", reciepts);
								if(UtilValidate.isNotEmpty(routeDetailsMap)){									
									pmRouteWiseMap.put(routeId, routeDetailsMap);			
								}
							}
						}
					}
				}
				
			}
		}		
	}
}
SortedMap finalRouteWiseMap = new TreeMap();
if(UtilValidate.isNotEmpty(amProductTotals)){
	tempMap=[:];
	tempMap.put("routeWiseTotals", amProductTotals);
	tempMap.put("routeTotQty", amTotalQty);
	tempMap.put("routeAmount", amTotalAmount);
	tempMap.put("reciepts", amTotalReceipts);
	amRouteWiseMap.put("amGrandTotals", tempMap);
}
if(UtilValidate.isNotEmpty(pmProductTotals)){
	tempPmMap=[:];
	tempPmMap.put("routeWiseTotals", pmProductTotals);
	tempPmMap.put("routeTotQty", pmTotalQty);
	tempPmMap.put("routeAmount", pmTotalAmount);
	tempPmMap.put("reciepts", pmTotalReceipts);
	pmRouteWiseMap.put("pmGrandTotals", tempPmMap);
}
context.putAt("amRouteWiseMap", amRouteWiseMap);
context.putAt("pmRouteWiseMap", pmRouteWiseMap);
finalRouteWiseMap.putAll(amRouteWiseMap);
finalRouteWiseMap.putAll(pmRouteWiseMap);
context.put("routeWiseMap",finalRouteWiseMap);

