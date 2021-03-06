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
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();
customTimePeriodId = parameters.customTimePeriodId;
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);

fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
printDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy");
context.printDate = printDate;
Timestamp monthStart=UtilDateTime.getMonthStart(fromDate,TimeZone.getDefault(),Locale.getDefault());
context.put("cMonthStart",monthStart);
Timestamp monthEnd=UtilDateTime.getMonthEnd(thruDate, timeZone, locale);
context.put("cMonthEnd",monthEnd);
Timestamp pMonthStart=UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(monthStart, -1),TimeZone.getDefault(),Locale.getDefault());
context.put("pMonthStart",pMonthStart);
Timestamp pMonthEnd=UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(pMonthStart), timeZone, locale);
reportTypeFlag=parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && ("RetailerSalesComparison".equals(reportTypeFlag) || "PCMReport".equals(reportTypeFlag)||"PCMRetailersWorkingNotes".equals(reportTypeFlag))){
exprList=[];
exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "Milk"));
exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
  EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, fromDate)));
  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
	prodIdsList=EntityUtil.getFieldListFromEntityList(prodList, "productId", false);

shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,monthStart,monthEnd,null);
curntMonthDays= UtilDateTime.getIntervalInDays(monthStart,monthEnd)+1;
//Debug.log("curntMonthDays===="+curntMonthDays);
categoryTotalMap = [:];
categorysList = [];
facilityCurntSaleMap=[:];

if(UtilValidate.isNotEmpty(shipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, fromDate:monthStart, thruDate:monthEnd,includeReturnOrders:true,isByParty:true]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		boothTotalsMap=dayTotals.get("boothTotals");
		if(UtilValidate.isNotEmpty(boothTotalsMap)){
			boothTotalsMap.each { booth->
				facilityId=booth.getKey();
				categoryType=booth.getValue().get("categoryTypeEnum");
				boothInnerMap=[:];
				milkAverageTotal = 0;
				milkSaleTotal = 0;
					prodTotals = booth.getValue().get("productTotals");
					if(UtilValidate.isNotEmpty(prodTotals)){
						prodTotals.each{ productValue ->
							if(UtilValidate.isNotEmpty(productValue)){
								currentProduct = productValue.getKey();
								if(prodIdsList.contains(currentProduct)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}
								/*product = delegator.findOne("Product", [productId : currentProduct], false);
								if("Milk".equals(product.primaryProductCategoryId)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}*/
							}
						}
						
						}
					boothInnerMap["facilityId"]=facilityId;
					boothInnerMap["categoryType"]=categoryType;
					boothInnerMap["milkSaleTotal"]=milkSaleTotal;
					boothInnerMap["milkAvgTotal"]=(milkSaleTotal/curntMonthDays);
					facilityCurntSaleMap[facilityId]=boothInnerMap;
					
					if(categoryTotalMap.containsKey(categoryType)){
						tempCatList = categoryTotalMap.get(categoryType);
						tempCatList.addAll(boothInnerMap);
						categoryTotalMap.putAt(categoryType, tempCatList);
					}else{
						tempList = [];
						tempList.add(boothInnerMap);
						categoryTotalMap.putAt(categoryType, tempList);
						categorysList.add(categoryType);
					}
					
					}
		}
	}
  }


//Debug.log("facilityCurntSaleMap===="+facilityCurntSaleMap);
//Debug.log("categorysList===="+categorysList);
//Debug.log("pMonthStart===="+pMonthStart+"===pMonthEnd=="+pMonthEnd);
prvShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,pMonthStart,pMonthEnd,null);
prevMonthDays= UtilDateTime.getIntervalInDays(pMonthStart,pMonthEnd)+1;
//Debug.log("prevMonthDays===="+prevMonthDays);
prevCategoryTotalMap = [:];
prevCategorysList = [];
facilityPrevSaleMap=[:];

if(UtilValidate.isNotEmpty(reportTypeFlag) && !"PCMRetailersWorkingNotes".equals(reportTypeFlag)){
	
	if(UtilValidate.isNotEmpty(prvShipmentIds)){
		dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:prvShipmentIds, fromDate:pMonthStart, thruDate:pMonthEnd,includeReturnOrders:true,isByParty:true]);
		if(UtilValidate.isNotEmpty(dayTotals)){
			boothTotalsMap=dayTotals.get("boothTotals");
			if(UtilValidate.isNotEmpty(boothTotalsMap)){
				boothTotalsMap.each { booth->
					facilityId=booth.getKey();
					categoryType=booth.getValue().get("categoryTypeEnum");
					boothInnerMap=[:];
					milkAverageTotal = 0;
					milkSaleTotal = 0;
						prodTotals = booth.getValue().get("productTotals");
						if(UtilValidate.isNotEmpty(prodTotals)){
								prodTotals.each{ productValue ->
									if(UtilValidate.isNotEmpty(productValue)){
										currentProduct = productValue.getKey();
										if(prodIdsList.contains(currentProduct)){
											milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
										}
										/*product = delegator.findOne("Product", [productId : currentProduct], false);
										if("Milk".equals(product.primaryProductCategoryId)){
											milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
										}*/
									}
								}
							}
						boothInnerMap["facilityId"]=facilityId;
						boothInnerMap["categoryType"]=categoryType;
						boothInnerMap["milkSaleTotal"]=milkSaleTotal;
						boothInnerMap["milkAvgTotal"]=(milkSaleTotal/prevMonthDays);
						facilityPrevSaleMap[facilityId]=boothInnerMap;
						
						if(prevCategoryTotalMap.containsKey(categoryType)){
							tempCatList = prevCategoryTotalMap.get(categoryType);
							tempCatList.addAll(boothInnerMap);
							prevCategoryTotalMap.putAt(categoryType, tempCatList);
						}else{
							tempList = [];
							tempList.add(boothInnerMap);
							prevCategoryTotalMap.putAt(categoryType, tempList);
							prevCategorysList.add(categoryType);
						}
						}
			}
	  }
	}
}
//Debug.log("prevCategoryTotalMap===="+prevCategoryTotalMap);
//Debug.log("categoryTotalMap===="+categoryTotalMap);
context.categorysList=categorysList;
context.categoryTotalMap=categoryTotalMap;

context.facilityCurntSaleMap=facilityCurntSaleMap;
context.facilityPrevSaleMap=facilityPrevSaleMap;
//Debug.log("prevCategorysList===="+prevCategorysList);
}
if(UtilValidate.isNotEmpty(reportTypeFlag) && ("PCMReport".equals(reportTypeFlag)||"PCMRetailersWorkingNotes".equals(reportTypeFlag))){
//current  month abstract map
   if(parameters.customTimePeriodId){
	   curntCatAbsMap=[:];
	   categorysList.each{categoryType->
		if(!("PRLR_RTLR".equals(categoryType))){
		curFacList=[];
		curFacList=categoryTotalMap.get(categoryType);
		prvFacList=facilityPrevSaleMap.get(categoryType);
		pcmAbstractMap=[:];
		pcmAbstractMap["catSize"]=0;
		pcmAbstractMap["milkAvgTot"]=0;
		pcmAbstractMap["sameQty"]=0;
		pcmAbstractMap["descQty"]=0;
		pcmAbstractMap["incQty"]=0;
		pcmAbstractMap["less100"]=0;
		pcmAbstractMap["bwt101To250"]=0;
		pcmAbstractMap["bwt251To500"]=0;
		pcmAbstractMap["bwt501To750"]=0;
		pcmAbstractMap["bwt751To1000"]=0;
		pcmAbstractMap["above1000"]=0;
		curRetailerCount=0;
		prvRetailerCount=0;
		if(UtilValidate.isNotEmpty(curFacList)){
		  curRetailerCount=curFacList.size();
		}
		if(UtilValidate.isNotEmpty(prvFacList)){
		  prvRetailerCount=prvFacList.size();
		}
		
		curFacList.each{ curFacilityMap->
			boothId=curFacilityMap.get("facilityId");
			milkAvgTotal=curFacilityMap.get("milkAvgTotal");
			milkRtlAvgTotal=curFacilityMap.get("milkAvgTotal")/curRetailerCount;
			preFacilityMap=facilityPrevSaleMap.get(boothId);
			preAvgTot=0;
			diff=0;
			if(UtilValidate.isNotEmpty(preFacilityMap)){
				preAvgTot=preFacilityMap.get("milkAvgTotal");
				if(preFacilityMap.get("milkAvgTotal")>0 && prvRetailerCount>0){
				 preRtlAvgTot=preFacilityMap.get("milkAvgTotal")/prvRetailerCount;
				}else{
				 preRtlAvgTot=preFacilityMap.get("milkAvgTotal");
				}
			}
			diff=milkAvgTotal-preAvgTot;
			if(diff==0){
				pcmAbstractMap["sameQty"]+=1;
			}else if(diff<0){
				pcmAbstractMap["descQty"]+=1;
			}else if(diff>0){
				pcmAbstractMap["incQty"]+=1;
			}
			//updating count
			pcmAbstractMap["catSize"]+=1;
			pcmAbstractMap["milkAvgTot"]+=milkRtlAvgTotal;
			if(milkAvgTotal<=100){
				pcmAbstractMap["less100"]+=1;
			}else if(milkAvgTotal>100&&milkAvgTotal<=250){
				pcmAbstractMap["bwt101To250"]+=1;
			}else if(milkAvgTotal>250&&milkAvgTotal<=500){
				pcmAbstractMap["bwt251To500"]+=1;
			}else if(milkAvgTotal>500&&milkAvgTotal<=750){
				pcmAbstractMap["bwt501To750"]+=1;
			}else if(milkAvgTotal>750&&milkAvgTotal<=1000){
				pcmAbstractMap["bwt751To1000"]+=1;
			}else if(milkAvgTotal>1000){
				pcmAbstractMap["above1000"]+=1;
			}
		}
		curntCatAbsMap[categoryType]=pcmAbstractMap;
	}else{
	curFacList=[];
	curFacList=categoryTotalMap.get(categoryType);
	prvFacList=facilityPrevSaleMap.get(categoryType);
	curRetailerCount=0;
	prvRetailerCount=0;
	if(UtilValidate.isNotEmpty(curFacList)){
	  curRetailerCount=curFacList.size();
	}
	if(UtilValidate.isNotEmpty(prvFacList)){
	  prvRetailerCount=prvFacList.size();
	}
	curFacList.each{ curFacilityMap->
		boothId=curFacilityMap.get("facilityId");
		milkAvgTotal=curFacilityMap.get("milkAvgTotal");
		milkRtlAvgTotal=curFacilityMap.get("milkAvgTotal")/curRetailerCount;
		preFacilityMap=facilityPrevSaleMap.get(boothId);
		preAvgTot=0;
		diff=0;
		if(UtilValidate.isNotEmpty(preFacilityMap)){
			preAvgTot=preFacilityMap.get("milkAvgTotal");
			if(preFacilityMap.get("milkAvgTotal")>0 && prvRetailerCount>0){
			 preRtlAvgTot=preFacilityMap.get("milkAvgTotal")/prvRetailerCount;
			}else{
			 preRtlAvgTot=preFacilityMap.get("milkAvgTotal");
			}
		}
		diff=milkAvgTotal-preAvgTot;
		if(diff==0){
			curntCatAbsMap.get("SHP_RTLR").sameQty +=1;
		}else if(diff<0){
			curntCatAbsMap.get("SHP_RTLR").descQty +=1;
		}else if(diff>0){
			curntCatAbsMap.get("SHP_RTLR").incQty +=1;
		}
		//updating count
	curntCatAbsMap.get("SHP_RTLR").catSize +=1;
		curntCatAbsMap.get("SHP_RTLR").milkAvgTot +=milkRtlAvgTotal;
		if(milkAvgTotal<=100){
			curntCatAbsMap.get("SHP_RTLR").less100 +=1;
		}else if(milkAvgTotal>100&&milkAvgTotal<=250){
			curntCatAbsMap.get("SHP_RTLR").bwt101To250 +=1;
		}else if(milkAvgTotal>250&&milkAvgTotal<=500){
			curntCatAbsMap.get("SHP_RTLR").bwt251To500 +=1;
		}else if(milkAvgTotal>500&&milkAvgTotal<=750){
			curntCatAbsMap.get("SHP_RTLR").bwt501To750 +=1;
		}else if(milkAvgTotal>750&&milkAvgTotal<=1000){
			curntCatAbsMap.get("SHP_RTLR").bwt751To1000 +=1;
		}else if(milkAvgTotal>1000){
			curntCatAbsMap.get("SHP_RTLR").above1000 +=1;
		}
	 }
	}
	   }
   }
if(UtilValidate.isNotEmpty(reportTypeFlag) && "PCMReport".equals(reportTypeFlag)){
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthStart));
	conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId",  EntityOperator.EQUALS,"BOOTH"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<GenericValue> facilityList = delegator.findList("Facility", condition, null, null, null, false);
	curNewFacilityMap = [:];
	curCount=0;
	if(UtilValidate.isNotEmpty(facilityList)){
		for(i=0;i<facilityList.size();i++){
			categoryType=facilityList.get(i).get("categoryTypeEnum");
			facilityId=facilityList.get(i).get("facilityId");
			if(UtilValidate.isNotEmpty(categoryType)){
					if(("PRLR_RTLR".equals(categoryType))){
						categoryType="SHP_RTLR";
					}					
				if(UtilValidate.isEmpty(curNewFacilityMap[categoryType])){
						curNewFacilityMap.put(categoryType,curCount+1);
				 }else{
					 curCount=curNewFacilityMap.get(categoryType)+1;
					 curNewFacilityMap[categoryType] = curCount;
				 }
			   }
			curCount=0;
		}
	}
   //Debug.log("curNewFacilityMap==="+curNewFacilityMap);
	context.curNewFacilityMap=curNewFacilityMap;
}
	context.curntCatAbsMap=curntCatAbsMap;
	
	if(UtilValidate.isNotEmpty(reportTypeFlag) && "PCMReport".equals(reportTypeFlag)){
	//prevoius abstract map
		prvCatAbsMap=[:];
		categorysList.each{categoryType->
			if(!("PRLR_RTLR".equals(categoryType))){				
			prvFacList=[];
			prvFacList=prevCategoryTotalMap.get(categoryType);
			prvPcmAbstractMap=[:];
			prvPcmAbstractMap["catSize"]=0;
			prvPcmAbstractMap["milkAvgTot"]=0;
			prvPcmAbstractMap["sameQty"]=0;
			prvPcmAbstractMap["incQty"]=0;
			prvPcmAbstractMap["descQty"]=0;
			prvPcmAbstractMap["less100"]=0;
			prvPcmAbstractMap["bwt101To250"]=0;
			prvPcmAbstractMap["bwt251To500"]=0;
			prvPcmAbstractMap["bwt501To750"]=0;
			prvPcmAbstractMap["bwt751To1000"]=0;
			prvPcmAbstractMap["above1000"]=0;
			prvRetailerCount=0;
			if(UtilValidate.isNotEmpty(prvFacList)){
			 prvRetailerCount=prvFacList.size();
			}
			prvFacList.each{ prvFacilityMap->
				boothId=prvFacilityMap.get("facilityId");
				prvMilkAvgTotal=prvFacilityMap.get("milkAvgTotal");
				 if(prvFacilityMap.get("milkSaleTotal")>0 && prvRetailerCount>0){
				   prvMilkRtlAvgTotal=prvFacilityMap.get("milkAvgTotal")/prvRetailerCount;
				 }else{
				   prvMilkRtlAvgTotal=prvFacilityMap.get("milkAvgTotal");
				 }
				//updating count
				prvPcmAbstractMap["catSize"]+=1;
				prvPcmAbstractMap["milkAvgTot"]+=prvMilkRtlAvgTotal;
				if(prvMilkAvgTotal<=100){
					prvPcmAbstractMap["less100"]+=1;
				}else if(prvMilkAvgTotal>100&&prvMilkAvgTotal<=250){
					prvPcmAbstractMap["bwt101To250"]+=1;
				}else if(prvMilkAvgTotal>250&&prvMilkAvgTotal<=500){
					prvPcmAbstractMap["bwt251To500"]+=1;
				}else if(prvMilkAvgTotal>500&&prvMilkAvgTotal<=750){
					prvPcmAbstractMap["bwt501To750"]+=1;
				}else if(prvMilkAvgTotal>750&&prvMilkAvgTotal<=1000){
					prvPcmAbstractMap["bwt751To1000"]+=1;
				}else if(prvMilkAvgTotal>1000){
					prvPcmAbstractMap["above1000"]+=1;
				}
			}
			prvCatAbsMap[categoryType]=prvPcmAbstractMap;
			}else{
			prvFacList=[];
			prvFacList=prevCategoryTotalMap.get(categoryType);
			prvRetailerCount=0;
			if(UtilValidate.isNotEmpty(prvFacList)){
			 prvRetailerCount=prvFacList.size();
			}
			prvFacList.each{ prvFacilityMap->
				boothId=prvFacilityMap.get("facilityId");
				prvMilkAvgTotal=prvFacilityMap.get("milkAvgTotal");
				 if(prvFacilityMap.get("milkSaleTotal")>0 && prvRetailerCount>0){
				   prvMilkRtlAvgTotal=prvFacilityMap.get("milkAvgTotal")/prvRetailerCount;
				 }else{
				   prvMilkRtlAvgTotal=prvFacilityMap.get("milkAvgTotal");
				 }
				//updating count
				
				 prvCatAbsMap.get("SHP_RTLR").catSize +=1;
				 prvCatAbsMap.get("SHP_RTLR").milkAvgTot += prvMilkRtlAvgTotal;
				if(prvMilkAvgTotal<=100){
					 prvCatAbsMap.get("SHP_RTLR").less100 +=1;
				}else if(prvMilkAvgTotal>100&&prvMilkAvgTotal<=250){
					 prvCatAbsMap.get("SHP_RTLR").bwt101To250 +=1;
				}else if(prvMilkAvgTotal>250&&prvMilkAvgTotal<=500){
					 prvCatAbsMap.get("SHP_RTLR").bwt251To500 +=1;
				}else if(prvMilkAvgTotal>500&&prvMilkAvgTotal<=750){
					 prvCatAbsMap.get("SHP_RTLR").bwt501To750 +=1;
				}else if(prvMilkAvgTotal>750&&prvMilkAvgTotal<=1000){
					 prvCatAbsMap.get("SHP_RTLR").bwt751To1000 +=1;
				}else if(prvMilkAvgTotal>1000){
					 prvCatAbsMap.get("SHP_RTLR").above1000 +=1;
				}
			}
			
			}
		}
		//Debug.log("prvCatAbsMap====="+prvCatAbsMap);
	if(UtilValidate.isNotEmpty(reportTypeFlag) && "PCMReport".equals(reportTypeFlag)){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.GREATER_THAN_EQUAL_TO ,pMonthStart));
		conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO ,pMonthEnd));
		conditionList.add(EntityCondition.makeCondition("facilityTypeId",  EntityOperator.EQUALS,"BOOTH"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<GenericValue> prvfacilityList = delegator.findList("Facility", condition, null, null, null, false);
		prvNewFacilityMap = [:];
		prvCount=0;
		if(UtilValidate.isNotEmpty(prvfacilityList)){
			for(i=0;i<prvfacilityList.size();i++){
				categoryType=prvfacilityList.get(i).get("categoryTypeEnum");
				facilityId=prvfacilityList.get(i).get("facilityId");
				if(UtilValidate.isNotEmpty(categoryType)){
					if(("PRLR_RTLR".equals(categoryType))){
						categoryType="SHP_RTLR";
					}
					if(UtilValidate.isEmpty(prvNewFacilityMap[categoryType])){
							prvNewFacilityMap.put(categoryType,prvCount+1);
					 }else{
						 prvCount=prvNewFacilityMap.get(categoryType)+1;
						 prvNewFacilityMap[categoryType] = prvCount;
					 }
				   }
				prvCount=0;
			}
		}

	//Debug.log("prvNewFacilityMap==="+prvNewFacilityMap);
	context.prvNewFacilityMap=prvNewFacilityMap;
	}
	context.prvCatAbsMap=prvCatAbsMap;
	
 }
}