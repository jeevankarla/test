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
shipmentId = null;
shipmentIds = [];

if(parameters.routeId){
	facility = delegator.findOne("Facility", [facilityId : parameters.routeId], false);
	facilityId = facility.facilityId;
	routeIdsList.add(facilityId);
}else{
   routeIdsList = NetworkServices.getRoutes(dctx,context).get("routesList");
   routeIdsList.add(parameters.routeId);
}

effectiveDate = null;
effectiveDateStr = parameters.supplyDate;

if (UtilValidate.isNotEmpty(effectiveDateStr)) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		estimatedDeliveryDateTime = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + estimatedDeliveryDateTime, "");
	}
}
dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime);

context.putAt("estimatedDeliveryDateTime", estimatedDeliveryDateTime);
dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);
List shipmentIds  = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);

products = delegator.findList("Product", null, UtilMisc.toSet("productId","quantityIncluded","sequenceNum"),["sequenceNum"],null, false);
prodIncMap = [:];
products.each{eachProd->
	qtyInc = eachProd.quantityIncluded;
	if(qtyInc){
		prodIncMap.put(eachProd.productId, qtyInc);
	}
}
context.putAt("products", products);
routeWiseMap =[:];
if(UtilValidate.isNotEmpty(routeIdsList)){
	routeIdsList.each{ routeId ->
		allProductList =[];
		allProdSeqList =[];
		boothsList = (NetworkServices.getRouteBooths(delegator , routeId));
		if(UtilValidate.isNotEmpty(boothsList)){
			if(UtilValidate.isNotEmpty(shipmentIds)){
				dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDateTime);
				context.putAt("dayBegin", dayBegin);
				dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDateTime);
				Map boothWiseMap =[:];
				
				boothsList.each{ boothId ->
					Map prodWiseDetailMap= FastMap.newInstance();
					dayTotals = NetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, facilityIds:UtilMisc.toList(boothId),fromDate:dayBegin, thruDate:dayEnd]);
					if(UtilValidate.isNotEmpty(dayTotals)){
						productTotals = dayTotals.get("productTotals");		
						if(UtilValidate.isNotEmpty(productTotals)){
							Iterator prodIter = productTotals.entrySet().iterator();
							while (prodIter.hasNext()) {
								Map.Entry entry = prodIter.next();
								product = entry.getKey();
								conditionList=[];
								conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,product));
								condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
								productCategoryList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
								prodCategoryIds= EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
								temp =[:];
								temp["Crates"]=0;
								temp["packets"]=0;
								prodCategoryIds.each{ prodCategory->
									qtyValue=entry.getValue().get("total");
									allProductList.add(entry.getKey());
									if("PACKET_INDENT".equals(prodCategory)){
										qtyIncluded = prodIncMap.get(product);
										temp["Packs"] = qtyValue/qtyIncluded;
										temp.put("Crates",0);
										temp.put("excessCrates",0);
									}
									if("CRATE_INDENT".equals(prodCategory)){
										Crates =(qtyValue/(12)).intValue();
										excess = (qtyValue.intValue()%12);
										temp.put("Crates",Crates);
										temp.put("excessCrates",excess);
										temp.put("Packs",0);
									}
									temp.put("qtyValue",qtyValue);
								}
							    prodWiseDetailMap.put(product, temp);
							}
						}
						boothWiseMap.put(boothId, prodWiseDetailMap);
					}
				}
						allProdSeqList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, allProductList) , null, ["sequenceNum"], null, false);
						allProdIdsList= EntityUtil.getFieldListFromEntityList(allProdSeqList, "productId", true);
						context.put("allProdList",allProdIdsList);

						Map boothDetailsMap=FastMap.newInstance();
						boothDetailsMap.put("boothWiseMap", boothWiseMap);
						if(UtilValidate.isNotEmpty(boothDetailsMap)){
							routeWiseMap.put(routeId, boothDetailsMap);
						}
			}
		}		
	}
}
context.put("routeWiseMap",routeWiseMap);
