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

import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if (parameters.thruDate) {
		   thruDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
   }
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();

milkProductList =[];
milkProductList = ProcurementNetworkServices.getMilkReceiptProducts(dctx, UtilMisc.toMap());
context.put("milkProductList", milkProductList);

finalUnitMap =[:];
finalTotalMap=[:];
finalGrandTotalMap=[:];
Map initMap = FastMap.newInstance();
initMap.put("recdQtyLtrs", BigDecimal.ZERO);
initMap.put("recdQtyKgs", BigDecimal.ZERO);
initMap.put("recdKgFat", BigDecimal.ZERO);
initMap.put("recdKgSnf", BigDecimal.ZERO);
initMap.put("receivedFat", BigDecimal.ZERO);
initMap.put("receivedSnf", BigDecimal.ZERO);
	Map unitProductTotalMap = FastMap.newInstance();
	Map privateProductTotalMap = FastMap.newInstance();
for(product in milkProductList){
	tempInitMap = [:];
	tempInitMap.putAll(initMap);
	unitProductTotalMap.put(product.get("productId"), tempInitMap);
}
Map facilityWiseMap = FastMap.newInstance();
	shedConditionList =[];
	shedConditionList.add(EntityCondition.makeCondition("mccTypeId", EntityOperator.IN , UtilMisc.toList("FEDERATION")));
	shedConditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "SHED"));
	EntityCondition shedCondition = EntityCondition.makeCondition(shedConditionList,EntityOperator.AND);
	federationDetailsList = delegator.findList("Facility",shedCondition,null,null,null,false);

List shedsList = FastList.newInstance();
	shedsList.addAll(EntityUtil.getFieldListFromEntityList(federationDetailsList,"facilityId",true))
	unitConditionList = [];
	unitConditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN , shedsList));
	unitConditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "UNIT"));
	unitConditionList.add(EntityCondition.makeCondition("mccCode", EntityOperator.NOT_EQUAL , null));
	EntityCondition unitCondition = EntityCondition.makeCondition(unitConditionList,EntityOperator.AND);
	 
List unitsList = delegator.findList("Facility",unitCondition,null,null,null,false);
	List unitIdDetailList = FastList.newInstance();
	shedUnitList = UtilMisc.sortMaps(unitsList, UtilMisc.toList("mccCode"));
	for(unit in shedUnitList){
		Map tempDetailMap = FastMap.newInstance();
		tempDetailMap.put("facilityId", unit.get("facilityId"));
		tempDetailMap.put("facilityName", unit.get("facilityName"));
		tempDetailMap.put("mccTypeId", unit.get("mccTypeId"));
		if(UtilValidate.isNotEmpty(unit.get("mccCode"))){
			tempDetailMap.put("mccCode",Integer.parseInt(unit.get("mccCode")));
		}
		unitIdDetailList.add(tempDetailMap);
	}
productAvgMap = [:];
productWiseDaysMap = [:];
unitMccList = UtilMisc.sortMaps(unitIdDetailList, UtilMisc.toList("mccCode"));
if(UtilValidate.isNotEmpty(unitMccList)){
	unitMccList.each{unitId->
		if(UtilValidate.isEmpty(facilityWiseMap.get(unitId))){
			mccTypeId=unitId.get("mccTypeId");
			if(UtilValidate.isNotEmpty(mccTypeId)){
				periodTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:unitId.facilityId]);
				if(UtilValidate.isNotEmpty(periodTotals)){
					Map unitTotals = FastMap.newInstance();
					unitTotals = periodTotals.get(unitId.facilityId);
					if(UtilValidate.isNotEmpty(unitTotals)){
						dayTotals=unitTotals.get("dayTotals");
						//POPULATING NO: OF DAYS
						productInitDaysMap =[:];
						days =dayTotals;
						Iterator daysIter = days.entrySet().iterator();
						while(daysIter.hasNext()){
							Map.Entry daysEntry = daysIter.next();
							if(!"TOT".equals(daysEntry.getKey())){
								productDetails = daysEntry.getValue();
								Iterator productIter = productDetails.entrySet().iterator();
								while(productIter.hasNext()){
									Map.Entry productEntry = productIter.next();
									if(UtilValidate.isNotEmpty(productEntry.getKey())){
										recdQtyLtrs = productEntry.getValue().get("recdQtyLtrs");
										if(recdQtyLtrs!=0){
											BigDecimal avgQtyLtrs = BigDecimal.ZERO;
											if(UtilValidate.isEmpty(productInitDaysMap.get(productEntry.getKey()))){
												Map initDaysMap = FastMap.newInstance();
												initDaysMap.put("noofdays", 1);
												initDaysMap.put("recdQtyLtrs", recdQtyLtrs);
												avgQtyLtrs = recdQtyLtrs;
												initDaysMap.put("avgQtyLtrs", avgQtyLtrs);
												productInitDaysMap.put(productEntry.getKey(), initDaysMap);
											}else{
												Map tempInitDaysMap=FastMap.newInstance();
												tempInitDaysMap.putAll(productInitDaysMap.get(productEntry.getKey()));
												tempInitDaysMap.put("recdQtyLtrs", tempInitDaysMap.get("recdQtyLtrs")+recdQtyLtrs);
												tempInitDaysMap.put("noofdays", tempInitDaysMap.get("noofdays")+1);
												BigDecimal tempQtyLtrs = tempInitDaysMap.get("recdQtyLtrs");
												BigDecimal noOfDays = tempInitDaysMap.get("noofdays")
												avgQtyTotLtrs = tempQtyLtrs.divide(noOfDays, 0,BigDecimal.ROUND_HALF_UP);
												tempInitDaysMap.put("avgQtyLtrs", avgQtyTotLtrs);
												productInitDaysMap.put(productEntry.getKey(), tempInitDaysMap);
											}
										}
									}
								}
							}
						}
						//GETTING PRODUCT WISE TOTALS
						if(UtilValidate.isNotEmpty(productInitDaysMap)){
							productWiseDaysMap.put(unitId.facilityId, productInitDaysMap);
						}
						if(UtilValidate.isNotEmpty(dayTotals)){
							Map annualValuesMap = FastMap.newInstance();
							annualValuesMap.putAll(dayTotals.get("TOT"));
							for(key in annualValuesMap.keySet()){
								if(key!="TOT" && UtilValidate.isNotEmpty(unitProductTotalMap.get(key))){
									Map tempDetailMap = FastMap.newInstance();
									tempDetailMap.putAll(unitProductTotalMap.get(key));
									Map tempProdMap = FastMap.newInstance();
									tempProdMap.putAll(annualValuesMap.get(key));
									for(qtyKey in tempDetailMap.keySet()){
										tempDetailMap.put(qtyKey, tempDetailMap.get(qtyKey)+tempProdMap.get(qtyKey));
									}
									unitProductTotalMap.put(key, tempDetailMap);
								}
							}
							finalUnitMap.put(unitId.facilityId, annualValuesMap);
						 }
					 }
				 }
			 }
		 }
	 }
 }
context.put("unitProductTotalMap", unitProductTotalMap);
context.put("finalUnitMap", finalUnitMap);
context.put("productWiseDaysMap", productWiseDaysMap);

Iterator productQtyIter = productWiseDaysMap.entrySet().iterator();
while (productQtyIter.hasNext()) {
	Map.Entry entry = productQtyIter.next();
	if(UtilValidate.isNotEmpty(entry.getKey())){
		productQtyMap =  entry.getValue();
		Iterator productWiseQtyIter = productQtyMap.entrySet().iterator();
		while (productWiseQtyIter.hasNext()) {
			Map.Entry productEntry = productWiseQtyIter.next();
			if(UtilValidate.isNotEmpty(productEntry.getKey())){
				if(UtilValidate.isNotEmpty(productEntry.getValue().get("recdQtyLtrs"))){
					avgQtyLtrs = (productEntry.getValue().get("avgQtyLtrs"));
					if(avgQtyLtrs!=0){
						if(UtilValidate.isEmpty(productAvgMap.get(productEntry.getKey()))){
							Map productGrandMap=FastMap.newInstance();
							productGrandMap.put("avgQtyLtrs",0);
							productGrandMap.put("avgQtyLtrs",avgQtyLtrs);
							productAvgMap.put(productEntry.getKey(), productGrandMap);
						}
						else{
							Map tempProdGrandMap =FastMap.newInstance();
							tempProdGrandMap.putAll(productAvgMap.get(productEntry.getKey()));
							tempProdGrandMap.put("avgQtyLtrs", tempProdGrandMap.get("avgQtyLtrs")+avgQtyLtrs);
							productAvgMap.put(productEntry.getKey(),tempProdGrandMap);
						}
					}
				}
			}
		}
	}
}
context.put("productAvgMap", productAvgMap);

for(product in milkProductList){
	tempInitMap = [:];
	tempInitMap.putAll(initMap);
	privateProductTotalMap.put(product.get("productId"), tempInitMap);
}
finalPrivateUnitMap =[:];
conditionList =[];
conditionList.add(EntityCondition.makeCondition("mccTypeId", EntityOperator.IN , UtilMisc.toList("UNION","OTHERS")));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "UNIT"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unionAndOthersDetailsList = delegator.findList("Facility",condition,null,null,null,false);

List privateIdList = FastList.newInstance();
unionAndOthersDetailsList = UtilMisc.sortMaps(unionAndOthersDetailsList, UtilMisc.toList("mccCode"));
for(privateId in unionAndOthersDetailsList){
	Map tempPrivateMap = FastMap.newInstance();
	tempPrivateMap.put("facilityId", privateId.get("facilityId"));
	tempPrivateMap.put("facilityName", privateId.get("facilityName"));
	tempPrivateMap.put("mccCode",Integer.parseInt(privateId.get("mccCode")));
	privateIdList.add(tempPrivateMap);
}
productPrivateAvgMap = [:];
privateProductWiseDaysMap = [:];
privateIdList = UtilMisc.sortMaps(privateIdList, UtilMisc.toList("mccCode"));
if(UtilValidate.isNotEmpty(privateIdList)){
privateIdList.each{privateUnit->
	privateTotal = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate ,facilityId: privateUnit.facilityId]);
	if(UtilValidate.isNotEmpty(privateTotal)){
		Map unitPrivateTotals = FastMap.newInstance();
		unitPrivateTotals = privateTotal.get(privateUnit.facilityId);
			if(UtilValidate.isNotEmpty(unitPrivateTotals)){
				dayPrivateTotals=unitPrivateTotals.get("dayTotals");
				//POPULATING NO: OF DAYS
				privateProdDaysMap = [:];
				privateDays =dayPrivateTotals;
				Iterator daysIter = privateDays.entrySet().iterator();
				while(daysIter.hasNext()){
					Map.Entry daysEntry = daysIter.next();
					if(!"TOT".equals(daysEntry.getKey())){
						privateTotDays = daysEntry.getValue();
						Iterator productPrivateIter = privateTotDays.entrySet().iterator();
						while(productPrivateIter.hasNext()){
							Map.Entry privateProdEntry = productPrivateIter.next();
							if(UtilValidate.isNotEmpty(privateProdEntry.getKey())){
								recdQtyLtrs = privateProdEntry.getValue().get("recdQtyLtrs");
								if(recdQtyLtrs!=0){
									BigDecimal avgQtyLtrs = BigDecimal.ZERO;
									if(UtilValidate.isEmpty(privateProdDaysMap.get(privateProdEntry.getKey()))){
										Map initPrivDaysMap = FastMap.newInstance();
										initPrivDaysMap.put("noofdays", 1);
										initPrivDaysMap.put("recdQtyLtrs", recdQtyLtrs);
										avgQtyLtrs = recdQtyLtrs;
										initPrivDaysMap.put("avgQtyLtrs", avgQtyLtrs);
										privateProdDaysMap.put(privateProdEntry.getKey(), initPrivDaysMap);
									}else{
										Map tempPrivDaysMap=FastMap.newInstance();
										tempPrivDaysMap.putAll(privateProdDaysMap.get(privateProdEntry.getKey()));
										tempPrivDaysMap.put("recdQtyLtrs", tempPrivDaysMap.get("recdQtyLtrs")+recdQtyLtrs);
										tempPrivDaysMap.put("noofdays", tempPrivDaysMap.get("noofdays")+1);
										BigDecimal tempQtyLtrs = tempPrivDaysMap.get("recdQtyLtrs");
										BigDecimal noOfDays = tempPrivDaysMap.get("noofdays")
										avgQtyTotLtrs = tempQtyLtrs.divide(noOfDays, 0,BigDecimal.ROUND_HALF_UP);
										tempPrivDaysMap.put("avgQtyLtrs", avgQtyTotLtrs);
										privateProdDaysMap.put(privateProdEntry.getKey(), tempPrivDaysMap);
									}
								}	
							}
						}
					}	
				}
				if(UtilValidate.isNotEmpty(privateProdDaysMap)){
					privateProductWiseDaysMap.put(privateUnit.facilityId, privateProdDaysMap);
				}
				//GETTING PRODUCT WISE PRIVATE TOTALS
				if(UtilValidate.isNotEmpty(dayPrivateTotals)){
					Map annualPrivateValuesMap = FastMap.newInstance();
					annualPrivateValuesMap = dayPrivateTotals.get("TOT");
					for(key in annualPrivateValuesMap.keySet()){
						if(UtilValidate.isNotEmpty(privateProductTotalMap.get(key))){
						   Map tempPrivateMap = FastMap.newInstance();
						   tempPrivateMap = privateProductTotalMap.get(key);
						   Map tempPrivateProdMap = FastMap.newInstance();
						   tempPrivateProdMap = annualPrivateValuesMap.get(key);
						   for(qtyKey in tempPrivateMap.keySet()){
							   tempPrivateMap.put(qtyKey, tempPrivateMap.get(qtyKey)+tempPrivateProdMap.get(qtyKey));
						   }
						   privateProductTotalMap.put(key, tempPrivateMap);
						}
					}
					finalPrivateUnitMap.put(privateUnit.facilityId, annualPrivateValuesMap);
				}
			}
		}
	}
}
context.put("finalPrivateUnitMap", finalPrivateUnitMap);
context.put("privateProductTotalMap", privateProductTotalMap);
context.put("privateProductWiseDaysMap", privateProductWiseDaysMap);

Iterator productPrivateQtyIter = privateProductWiseDaysMap.entrySet().iterator();
while (productPrivateQtyIter.hasNext()) {
	Map.Entry entry = productPrivateQtyIter.next();
	if(UtilValidate.isNotEmpty(entry.getKey())){
		prodPrivateQtyMap = entry.getValue();
		Iterator prodPrivateQtyIter = prodPrivateQtyMap.entrySet().iterator();
		while (prodPrivateQtyIter.hasNext()) {
			Map.Entry productPrivateEntry = prodPrivateQtyIter.next();
			if(UtilValidate.isNotEmpty(productPrivateEntry)){
				avgQtyLtrs = productPrivateEntry.getValue().get("avgQtyLtrs");
				if(avgQtyLtrs !=0){
					if(UtilValidate.isEmpty(productPrivateAvgMap.get(productPrivateEntry.getKey()))){
						Map productPrivateGrandMap=FastMap.newInstance();
						productPrivateGrandMap.put("avgQtyLtrs",0);
						productPrivateGrandMap.put("avgQtyLtrs",avgQtyLtrs);
						productPrivateAvgMap.put(productPrivateEntry.getKey(), productPrivateGrandMap);
					}
					else
					{
						Map tempProdPrivateGrandMap =FastMap.newInstance();
						tempProdPrivateGrandMap.putAll(productPrivateAvgMap.get(productPrivateEntry.getKey()));
						tempProdPrivateGrandMap.put("avgQtyLtrs", tempProdPrivateGrandMap.get("avgQtyLtrs")+avgQtyLtrs);
						productPrivateAvgMap.put(productPrivateEntry.getKey(),tempProdPrivateGrandMap);
					}
				}
			}
		}
	}
}
context.put("productPrivateAvgMap", productPrivateAvgMap);

for(product in milkProductList){
	tempInitMap = [:];
	tempInitMap.putAll(initMap);
	finalTotalMap.put(product.get("productId"), tempInitMap);
}
for(String productKey in finalTotalMap.keySet()){
   Map tempFinalQtyMap = FastMap.newInstance();
   tempFinalQtyMap.putAll(finalTotalMap.get(productKey));
   Map tempFedQtyMap = FastMap.newInstance();
   tempFedQtyMap.putAll(unitProductTotalMap.get(productKey));
   Map tempPriQtyMap = FastMap.newInstance();
   tempPriQtyMap.putAll(privateProductTotalMap.get(productKey));
   for(String qtyKey in tempFinalQtyMap.keySet()){
	   tempFinalQtyMap.put(qtyKey, tempFedQtyMap.get(qtyKey)+tempPriQtyMap.get(qtyKey));
	}
   finalTotalMap.put(productKey, tempFinalQtyMap);
}
context.put("finalTotalMap", finalTotalMap);

grandTotalAvgMap = [:];
for(product in milkProductList){
	tempAvgMap=[:];
    tempAvgMap.put("avgQtyLtrs", BigDecimal.ZERO);
    finalGrandTotalMap.put(product.get("productId"), tempAvgMap);
}
for(String productKey in finalGrandTotalMap.keySet()){
	Map tempAvgQtyMap = FastMap.newInstance();
	tempAvgQtyMap.putAll(finalGrandTotalMap.get(productKey));
	Map temUnitAvgMap = FastMap.newInstance();
	if (UtilValidate.isNotEmpty(productAvgMap.get(productKey))){
		temUnitAvgMap.putAll(productAvgMap.get(productKey));
	}
	Map temPrivateAvgMap = FastMap.newInstance();
	if (UtilValidate.isNotEmpty(productPrivateAvgMap.get(productKey))){
		temPrivateAvgMap.putAll(productPrivateAvgMap.get(productKey));
	}
	for(String avgKey in tempAvgQtyMap.keySet()){
		unitProdAvg=0;
		unitPrivateAvg=0;
		  if((temUnitAvgMap.get(avgKey)) !=0){
			  unitProdAvg = temUnitAvgMap.get(avgKey);
		  }
		  if(UtilValidate.isEmpty(unitProdAvg)){
			  unitProdAvg = 0;
		  }
		  if((temPrivateAvgMap.get(avgKey)) !=0){
			  unitPrivateAvg = temPrivateAvgMap.get(avgKey);
		  }
		  if(UtilValidate.isEmpty(unitPrivateAvg)){
			  unitPrivateAvg = 0;
		  }
		  tempAvgQtyMap.put(avgKey, unitProdAvg+unitPrivateAvg);
	}
	grandTotalAvgMap.put(productKey, tempAvgQtyMap);
}
context.put("grandTotalAvgMap", grandTotalAvgMap);
   