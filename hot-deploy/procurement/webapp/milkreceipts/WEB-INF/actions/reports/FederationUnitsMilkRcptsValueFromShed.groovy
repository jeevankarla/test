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
import org.ofbiz.network.NetworkServices;
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


Timestamp fromDate;
Timestamp thruDate;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if(parameters.thruDate){
		   thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
preMonthStart=UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(fromDate, -1));
preMonthEnd=UtilDateTime.getMonthEnd(preMonthStart, timeZone, locale);
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();

Map shedWiseTotalsMap = FastMap.newInstance();
List shedConList= FastList.newInstance();
shedConList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"SHED")));
shedConList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("mccTypeId", EntityOperator.EQUALS ,"FEDERATION")));
shedCon = EntityCondition.makeCondition(shedConList,EntityOperator.AND);
shedList = delegator.findList("Facility",shedCon,null,null,null,false);

//shedList = ProcurementNetworkServices.getSheds(delegator);
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);
if(UtilValidate.isNotEmpty(shedList)){
	shedList.each{ shed->
		Map shedTotalMap=FastMap.newInstance();
		Map finalUnitMap = FastMap.newInstance();
		totQtyLtrs=0;
		totQtyKgs=0;
		totKgFat=0;
		totKgSnf=0;
		qtyKgs=0;
		shedIutRcptMlkValue=0;
		shedIutSentValue=0;
		shedGrossAmt=0;
		List unitsList = FastList.newInstance();
		shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: shed.facilityId]);
	 	unitsList = shedUnitDetails.get("unitsList");
		List milkList = [];
		milkList = UtilMisc.toList(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitsList),EntityOperator.OR, EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,unitsList)));
		milkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		milkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		milkList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
		milkList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MXF_RECD"));
		milkTransfersCondition = EntityCondition.makeCondition(milkList,EntityOperator.AND);
		milkTransfersList = delegator.findList("MilkTransfer",milkTransfersCondition,null,null,null,false);
		
		List havingTransfersList = FastList.newInstance();
		
		Map facilityTransferMap=FastMap.newInstance();
		List unitSendFacilities = FastList.newInstance();
		Set unitSendFacilitiesSet= new HashSet();
		if(UtilValidate.isNotEmpty(milkTransfersList)){
			havingTransfersList =(new HashSet( EntityUtil.getFieldListFromEntityList(milkTransfersList, "facilityId", false))).toList();
			List mpfSendList = FastList.newInstance();
			EntityCondition milkCondition  = EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT");
			mpfSendList = EntityUtil.filterByCondition(milkTransfersList,milkCondition );
			unitSendFacilitiesSet = new HashSet((List)EntityUtil.getFieldListFromEntityList(mpfSendList,"facilityId", true));
		}
		
		if(UtilValidate.isNotEmpty(unitSendFacilitiesSet)){
			unitSendFacilities.addAll( unitSendFacilitiesSet.toList());
		}
		if(UtilValidate.isEmpty(unitSendFacilities)){
			HashSet unitSendFacIdsSet = (new HashSet( EntityUtil.getFieldListFromEntityList(milkTransfersList, "facilityIdTo", false)));
			
			List tempList = FastList.newInstance();
			tempList.addAll(unitSendFacIdsSet.toList());
			for(facId in tempList){
					if(!unitsList.contains(facId)){
						unitSendFacIdsSet.remove(facId);
						}
				}	
			if(UtilValidate.isEmpty(unitSendFacIdsSet)){
				unitSendFacIdsSet = (new HashSet( EntityUtil.getFieldListFromEntityList(milkTransfersList, "facilityId", false)));
				}
			unitSendFacilities.addAll(unitSendFacIdsSet.toList());
		}
		List donthaveTransfersList = FastList.newInstance();
		donthaveTransfersList.addAll(unitsList);
		
		donthaveTransfersList.removeAll(havingTransfersList);
		
		unitSendFacilities.addAll(donthaveTransfersList);
		
		List milkSentFacilitiesList = FastList.newInstance();
		
		unitSendFacilities = unitSendFacilities.sort();
		unitSendFacilities.each{ unit->
			unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: unit]);
			Map unitTotalMap = FastMap.newInstance();
			unitTotalMap["qtyLtrs"] = 0;
			unitTotalMap["qtyKgs"] = 0;
			unitTotalMap["kgFat"] = 0;
			unitTotalMap["kgSnf"] = 0;
			unitTotalMap["fat"] = 0;
			unitTotalMap["snf"] = 0;
			unitTotalMap["rate"] = 0;
			unitTotalMap["mlkAmount"] = 0;
			unitTotalMap["opCost"] = 0;
			unitTotalMap["totOpCost"] = 0;
			unitTotalMap["totAmt"] = 0;
			if(UtilValidate.isNotEmpty(unitTotals)){
				facilityTotals = unitTotals.get(unit);
				if(UtilValidate.isNotEmpty(facilityTotals)){
					dayTotals = facilityTotals.get("dayTotals");
					if(UtilValidate.isNotEmpty(dayTotals)){
						Iterator dayTotIter = dayTotals.entrySet().iterator();
						while(dayTotIter.hasNext()){
							Map.Entry entry = dayTotIter.next();
							if("TOT".equals(entry.getKey())){
								if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdQtyLtrs"))){
									unitTotalMap.put("qtyLtrs", (entry.getValue().get("TOT").get("recdQtyLtrs")));
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdQtyKgs"))){
										unitTotalMap.put("qtyKgs", entry.getValue().get("TOT").get("recdQtyKgs"));
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdKgFat"))){
										unitTotalMap.put("kgFat", entry.getValue().get("TOT").get("recdKgFat"));
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdKgSnf"))){
										unitTotalMap.put("kgSnf", entry.getValue().get("TOT").get("recdKgSnf"));
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdQtyLtrs"))){
										totQtyLtrs = totQtyLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdQtyKgs"))){
										totQtyKgs = totQtyKgs+entry.getValue().get("TOT").get("recdQtyKgs");
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdKgFat"))){
										totKgFat = totKgFat+entry.getValue().get("TOT").get("recdKgFat");
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdKgSnf"))){
										totKgSnf = totKgSnf+entry.getValue().get("TOT").get("recdKgSnf");
									}
									if(UtilValidate.isNotEmpty(entry.getValue().get("TOT").get("recdQtyKgs"))){
										qtyKgs=entry.getValue().get("TOT").get("recdQtyKgs");
									}
									unitTotalMap.put("fat", entry.getValue().get("TOT").get("receivedFat"));
									unitTotalMap.put("snf", entry.getValue().get("TOT").get("receivedSnf"));
									Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
									inputRateAmt.put("rateTypeId", "MLKRECPT_OPCOST");
									inputRateAmt.put("rateCurrencyUomId", "INR");
									inputRateAmt.put("facilityId", unit);
									rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
									opCostRate= rateAmount.rateAmount;
									unitTotalMap["opCost"] = opCostRate;
									totalOpCost=(opCostRate*entry.getValue().get("TOT").get("recdQtyLtrs"));
									unitTotalMap["totOpCost"] = totalOpCost;
								}
							}
						}
					}
				}
			}
			Map unitGrndTot=FastMap.newInstance();
			unitGrndTot["mixProcQty"] =0;
			unitGrndTot["mixKgFat"] =0;
			unitGrndTot["mixKgSnf"] =0;
			unitGrndTot["mixedMlkAmount"] =0;
			unitGrndTot["tipAmount"] =0;
			unitGrndTot["addnAmt"] =0;
			unitGrndTot["grossAmt"] =0;
			totProcQtyKgs=0;
			totProcGross=0;
			trnsfRate=0;
			
			if(unitsList.contains(unit)){
				trnsferUnitProcurement = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: unit]);
				if(UtilValidate.isNotEmpty(trnsferUnitProcurement)){
					 facilityTotals = trnsferUnitProcurement.get(unit);
					 if(UtilValidate.isNotEmpty(facilityTotals)){
						 Iterator mccIter = facilityTotals.entrySet().iterator();
						 while(mccIter.hasNext()){
							 Map.Entry mccEntry = mccIter.next();
							 transferValues=mccEntry.getValue();
							 if(UtilValidate.isNotEmpty(transferValues)){
								 Iterator transIter = transferValues.entrySet().iterator();
								 while(transIter.hasNext()){
									 Map.Entry transEntry = transIter.next();
									 if("TOT".equals(transEntry.getKey())){
										 transprocValues=transEntry.getValue();
										 for(GenericValue procurementProduct : procurementProductList){
											 String productId = procurementProduct.getString("productId");
											 String productBrandName = procurementProduct.getString("brandName");
											 productValues = transprocValues.getAt(productId);
											 if(UtilValidate.isNotEmpty(productValues)){
												 if(UtilValidate.isNotEmpty(productValues.get("qtyKgs"))){
													 if(UtilValidate.isEmpty(unitGrndTot[productBrandName+"QtyKgs"])){
														 unitGrndTot[productBrandName+"QtyKgs"]=0;
														 unitGrndTot[productBrandName+"QtyKgs"] = productValues.get("qtyKgs");
													 }
													 else{
														 unitGrndTot[productBrandName+"QtyKgs"] += productValues.get("qtyKgs");
													 }
												 }
												 if(UtilValidate.isNotEmpty(productValues.get("kgFat"))){
													 if(UtilValidate.isEmpty(unitGrndTot[productBrandName+"KgFat"])){
														 unitGrndTot[productBrandName+"KgFat"]=0;
														 unitGrndTot[productBrandName+"KgFat"] = productValues.get("kgFat");
													 }
													 else{
														 unitGrndTot[productBrandName+"KgFat"] += productValues.get("kgFat");
													 }
												 }
												 if(UtilValidate.isNotEmpty(productValues.get("kgSnf"))){
													 if(UtilValidate.isEmpty(unitGrndTot[productBrandName+"KgSnf"])){
														 unitGrndTot[productBrandName+"KgSnf"]=0;
														 unitGrndTot[productBrandName+"KgSnf"] = productValues.get("kgSnf");
													 }
													 else{
														 unitGrndTot[productBrandName+"KgSnf"] += productValues.get("kgSnf");
													 }
												 }
												 if(UtilValidate.isNotEmpty(productValues.get("price"))){
													 if(UtilValidate.isEmpty(unitGrndTot[productBrandName+"Price"])){
														 unitGrndTot[productBrandName+"Price"]=0;
														 unitGrndTot[productBrandName+"Price"] = productValues.get("price");
													 }
													 else{
														 unitGrndTot[productBrandName+"Price"] += productValues.get("price");
													 }
												 }
											 }
										 }
										 totProductMap = transprocValues.get("TOT");
										 if(UtilValidate.isNotEmpty(totProductMap)){
											 if(UtilValidate.isNotEmpty(totProductMap)){
												if(UtilValidate.isNotEmpty(totProductMap.get("grossAmt"))){
													 totProcGross=totProcGross+totProductMap.get("grossAmt");
												 }
												 if(UtilValidate.isEmpty(unitGrndTot["mixkgFat"])){
													 unitGrndTot["mixKgFat"] =0;
													 if(UtilValidate.isNotEmpty(totProductMap.get("kgFat"))){
														 unitGrndTot["mixKgFat"] = totProductMap.get("kgFat");
													 }
												 }else{
													 if(UtilValidate.isNotEmpty(totProductMap.get("kgFat"))){
														 unitGrndTot["mixKgFat"] += totProductMap.get("kgFat");
													 }
												 }
												 if(UtilValidate.isEmpty(unitGrndTot["mixKgSnf"])){
													 unitGrndTot["mixKgSnf"] =0;
													 if(UtilValidate.isNotEmpty(totProductMap.get("kgSnf"))){
														 unitGrndTot["mixKgSnf"] = totProductMap.get("kgSnf");
													 }
												 }else{
													 if(UtilValidate.isNotEmpty(totProductMap.get("kgSnf"))){
														 unitGrndTot["mixKgSnf"] += totProductMap.get("kgSnf");
													 }
												 }
												 if(UtilValidate.isEmpty(unitGrndTot["tipAmount"])){
													 unitGrndTot["tipAmount"] =0;
													 if(UtilValidate.isNotEmpty(totProductMap.get("tipAmt"))){
														 unitGrndTot["tipAmount"] = totProductMap.get("tipAmt");
													 }
												 }else{
													 if(UtilValidate.isNotEmpty(totProductMap.get("tipAmt"))){
														 unitGrndTot["tipAmount"] += totProductMap.get("tipAmt");
													 }
												 }
												 if(UtilValidate.isEmpty(unitGrndTot["addnAmt"])){
													 unitGrndTot["addnAmt"] =0;
													 if(UtilValidate.isNotEmpty(totProductMap.get("opCost"))|| UtilValidate.isNotEmpty(totProductMap.get("commissionAmount"))){
														 unitGrndTot["addnAmt"] = totProductMap.get("grsAddn")+totProductMap.get("commissionAmount")+totProductMap.get("opCost");
													 }
												 }else{
													if(UtilValidate.isNotEmpty(totProductMap.get("opCost"))|| UtilValidate.isNotEmpty(totProductMap.get("commissionAmount"))){
														unitGrndTot["addnAmt"] += totProductMap.get("grsAddn")+totProductMap.get("commissionAmount")+totProductMap.get("opCost");
													}
												 }
											 }
										 }
									 }
								 }
							 }
						 }
					 }
				 }
			}
			List transmilkList = FastList.newInstance();
			transmilkList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, unit));
			transmilkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			transmilkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			transmilkList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MXF_RECD"));
			
			List tempUnitList = FastList.newInstance();
			//tempUnitList.addAll(unitsList);
			tempUnitList.addAll(unitSendFacilities);
			tempUnitList.addAll(milkSentFacilitiesList);
			transmilkList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_IN,tempUnitList));
			milkTransCondition = EntityCondition.makeCondition(transmilkList,EntityOperator.AND);
			List milkTransList = FastList.newInstance();
			milkTransList = EntityUtil.filterByCondition(milkTransfersList,milkTransCondition );
			
			Map facilityInMap=FastMap.newInstance();
			Map trnsferDetailsMap=FastMap.newInstance();
			trnsferDetailsMap["QtyKgs"]=0;
			trnsferDetailsMap["kgFat"]=0;
			trnsferDetailsMap["kgSnf"]=0;
			Map productWiseMap=FastMap.newInstance();
			if(UtilValidate.isNotEmpty(milkTransList)){
				List tranFacilityIds = (new HashSet( EntityUtil.getFieldListFromEntityList(milkTransList, "facilityId", false))).toList();
				if(UtilValidate.isEmpty(milkSentFacilitiesList)){
					milkSentFacilitiesList.addAll(tranFacilityIds);
					}else{
						for(tranFac in tranFacilityIds){
								if(!milkSentFacilitiesList.contains(tranFac)){
									milkSentFacilitiesList.add(tranFac);
									}
							}
					}
				
				
				
				milkTransList.each{ milkTrans->
					facilityId= milkTrans.get("facilityId");
					quantity= milkTrans.get("receivedQuantity");
					kgFat= milkTrans.get("receivedKgFat");
					kgSnf= milkTrans.get("receivedKgSnf");
					productId= milkTrans.get("productId");
					if(UtilValidate.isEmpty(facilityInMap[facilityId])){
						facilityInMap[facilityId]=0;
						if(UtilValidate.isNotEmpty(quantity)){
							facilityInMap[facilityId]=quantity;
						}
					}else{
						if(UtilValidate.isNotEmpty(quantity)){
							facilityInMap[facilityId]=facilityInMap.get(facilityId)+quantity;
						}
					}
				}
			}
			if(UtilValidate.isNotEmpty(facilityInMap)){
				for(key in facilityInMap.keySet()){
					Map facilityDetails = ProcurementNetworkServices.getShedDetailsForFacility(dctx,UtilMisc.toMap("userLogin",userLogin,"facilityId",key));
					String mccTypeId = "FEDERATION";
					if(UtilValidate.isNotEmpty(facilityDetails.get("facility"))){
							mccTypeId = (facilityDetails.get("facility")).get("mccTypeId");
						}
					
					trnsfRate=0;
					inTransferProcurement = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: key]);
					if(UtilValidate.isNotEmpty(inTransferProcurement) && (unitsList.contains(key) || (UtilValidate.isNotEmpty(mccTypeId)&& (!mccTypeId.equalsIgnoreCase("FEDERATION"))))){
						facilityInTotals = inTransferProcurement.get(key);
						if(UtilValidate.isNotEmpty(facilityInTotals)){
							Iterator mccInIter = facilityInTotals.entrySet().iterator();
							while(mccInIter.hasNext()){
								Map.Entry mccInEntry = mccInIter.next();
								transferInValues=mccInEntry.getValue();
								if(UtilValidate.isNotEmpty(transferInValues)){
									Iterator transferInIter = transferInValues.entrySet().iterator();
									while(transferInIter.hasNext()){
										Map.Entry transferInEntry = transferInIter.next();
										if("TOT".equals(transferInEntry.getKey())){
											procInValues=transferInEntry.getValue();
											for(GenericValue procurementProduct : procurementProductList){
												String productId = procurementProduct.getString("productId");
												String productInBrandName = procurementProduct.getString("brandName");
												inTransproductValues = procInValues.getAt(productId);
												if(UtilValidate.isNotEmpty(inTransproductValues)){
													if(UtilValidate.isNotEmpty(inTransproductValues.get("qtyKgs"))){
														if(UtilValidate.isEmpty(unitGrndTot[productInBrandName+"QtyKgs"])){
															unitGrndTot[productInBrandName+"QtyKgs"]=0;
															unitGrndTot[productInBrandName+"QtyKgs"] = inTransproductValues.get("qtyKgs");
														}
														else{
															unitGrndTot[productInBrandName+"QtyKgs"] += inTransproductValues.get("qtyKgs");
														}
													}
													if(UtilValidate.isNotEmpty(inTransproductValues.get("kgFat"))){
														if(UtilValidate.isEmpty(unitGrndTot[productInBrandName+"KgFat"])){
															unitGrndTot[productInBrandName+"KgFat"]=0;
															unitGrndTot[productInBrandName+"KgFat"] = inTransproductValues.get("kgFat");
														}
														else{
															unitGrndTot[productInBrandName+"KgFat"] += inTransproductValues.get("kgFat");
														}
													}
													if(UtilValidate.isNotEmpty(inTransproductValues.get("kgSnf"))){
														if(UtilValidate.isEmpty(unitGrndTot[productInBrandName+"KgSnf"])){
															unitGrndTot[productInBrandName+"KgSnf"]=0;
															unitGrndTot[productInBrandName+"KgSnf"] = inTransproductValues.get("kgSnf");
														}
														else{
															unitGrndTot[productInBrandName+"KgSnf"] += inTransproductValues.get("kgSnf");
														}
													}
													
													
													if(UtilValidate.isNotEmpty(inTransproductValues.get("price"))){
														if(UtilValidate.isEmpty(unitGrndTot[productInBrandName+"Price"])){
															unitGrndTot[productInBrandName+"Price"]=0;
															unitGrndTot[productInBrandName+"Price"] = inTransproductValues.get("price");
														}
														else{
															unitGrndTot[productInBrandName+"Price"] += inTransproductValues.get("price");
														}
													}
												}
											}
											inTranstotMap=procInValues.get("TOT");
											if(UtilValidate.isNotEmpty(inTranstotMap)){
												if(UtilValidate.isEmpty(unitGrndTot["tipAmount"])){
													unitGrndTot["tipAmount"] =0;
													if(UtilValidate.isNotEmpty(inTranstotMap.get("tipAmt"))){
														unitGrndTot["tipAmount"] = inTranstotMap.get("tipAmt");
													}
												}else{
													if(UtilValidate.isNotEmpty(inTranstotMap.get("tipAmt"))){
														unitGrndTot["tipAmount"] += inTranstotMap.get("tipAmt");
													}
												}
												if(UtilValidate.isEmpty(unitGrndTot["addnAmt"])){
													unitGrndTot["addnAmt"] =0;
													if(UtilValidate.isNotEmpty(inTranstotMap.get("opCost"))|| UtilValidate.isNotEmpty(inTranstotMap.get("commissionAmount"))){
														unitGrndTot["addnAmt"] = inTranstotMap.get("grsAddn")+inTranstotMap.get("commissionAmount")+inTranstotMap.get("opCost");
													}
												}else{
													if(UtilValidate.isNotEmpty(inTranstotMap.get("opCost"))|| UtilValidate.isNotEmpty(inTranstotMap.get("commissionAmount"))){
														unitGrndTot["addnAmt"] += inTranstotMap.get("grsAddn")+inTranstotMap.get("commissionAmount")+inTranstotMap.get("opCost");
													}
												}
											}
										}
									}
								}
							}
						}
					}
					trnsfGross=(trnsfRate*facilityInMap.get(key)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
					if(UtilValidate.isNotEmpty(trnsfGross)){
						shedIutRcptMlkValue=shedIutRcptMlkValue+trnsfGross;
						totProcGross=totProcGross+trnsfGross;
					}
					totProcGross=totProcGross+trnsfGross;
					totProcQtyKgs=totProcQtyKgs+facilityInMap.get(key);
				}
			}
								
			if(UtilValidate.isNotEmpty(facilityInMap)){
				for(key in facilityInMap.keySet()){
					List innermilkList = FastList.newInstance();
					innermilkList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, key));
					innermilkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
					innermilkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
					innermilkList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MXF_RECD"));
					List tempList = FastList.newInstance();
					//tempList.addAll(unitsList);
					tempList.addAll(unitSendFacilities);
					tempList.addAll(milkSentFacilitiesList);
					innermilkList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_IN,tempList));
					
					
					
					/*if(UtilValidate.isNotEmpty(milkSentFacilitiesList)){
						transmilkList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_IN,milkSentFacilitiesList.addAll(unitSendFacilities)));
					}else{
						innermilkList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_IN,unitSendFacilities));
					}*/
					innerMilkTransfersCondition = EntityCondition.makeCondition(innermilkList,EntityOperator.AND);
					List innerMilkTransfersList = FastList.newInstance();
					innerMilkTransfersList = EntityUtil.filterByCondition(milkTransfersList,innerMilkTransfersCondition );
					//innerMilkTransfersList = delegator.findList("MilkTransfer",innerMilkTransfersCondition,null,null,null,false);
					Map facilityInnerMap=FastMap.newInstance();
					if(UtilValidate.isNotEmpty(innerMilkTransfersList)){
						List tranFacilityIds = (new HashSet( EntityUtil.getFieldListFromEntityList(innerMilkTransfersList, "facilityId", false))).toList();
						if(UtilValidate.isEmpty(milkSentFacilitiesList)){
							milkSentFacilitiesList.addAll(tranFacilityIds);
							}else{
								for(tranFac in tranFacilityIds){
										if(!milkSentFacilitiesList.contains(tranFac)){
											milkSentFacilitiesList.add(tranFac);
											}
									}
							}
						
						innerMilkTransfersList.each{ innerMilkTransfer->
							facilityId= innerMilkTransfer.get("facilityId");
							quantity= innerMilkTransfer.get("receivedQuantity");
							kgFat= innerMilkTransfer.get("receivedKgFat");
							kgSnf= innerMilkTransfer.get("receivedKgSnf");
							productId= innerMilkTransfer.get("productId");
							if(UtilValidate.isEmpty(facilityInnerMap[facilityId])){
								facilityInnerMap[facilityId]=0;
								if(UtilValidate.isNotEmpty(quantity)){
									facilityInnerMap[facilityId]=quantity;
								}
							}else{
								if(UtilValidate.isNotEmpty(quantity)){
									facilityInnerMap[facilityId]=facilityInnerMap.get(facilityId)+quantity;
								}
							}
						}
					}
					if(UtilValidate.isNotEmpty(facilityInnerMap)){
						for(innerUnit in facilityInnerMap.keySet()){
							innerTransferProcurement = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: innerUnit]);
							String mccTypeId = "FEDERATION";
							Map facilityDetails = ProcurementNetworkServices.getShedDetailsForFacility(dctx,UtilMisc.toMap("userLogin",userLogin,"facilityId",innerUnit));
							if(UtilValidate.isNotEmpty(facilityDetails.get("facility"))){
									mccTypeId = (facilityDetails.get("facility")).get("mccTypeId");
								}
							if(UtilValidate.isNotEmpty(innerTransferProcurement) && (unitsList.contains(innerUnit) || (UtilValidate.isNotEmpty(mccTypeId)&& (!mccTypeId.equalsIgnoreCase("FEDERATION"))))){
								facilityinnerTotals = innerTransferProcurement.get(innerUnit);
								if(UtilValidate.isNotEmpty(facilityinnerTotals)){
									Iterator mccinnerIter = facilityinnerTotals.entrySet().iterator();
									while(mccinnerIter.hasNext()){
										Map.Entry mccinnerEntry = mccinnerIter.next();
										transferinnerValues=mccinnerEntry.getValue();
										if(UtilValidate.isNotEmpty(transferinnerValues)){
											Iterator transferinnerIter = transferinnerValues.entrySet().iterator();
											while(transferinnerIter.hasNext()){
												Map.Entry transferinnerEntry = transferinnerIter.next();
												if("TOT".equals(transferinnerEntry.getKey())){
													procinnerValues=transferinnerEntry.getValue();
													for(GenericValue procurementProduct : procurementProductList){
														String productId = procurementProduct.getString("productId");
														String productinnerBrandName = procurementProduct.getString("brandName");
														innerTransproductValues = procinnerValues.getAt(productId);
														if(UtilValidate.isNotEmpty(innerTransproductValues)){
															if(UtilValidate.isNotEmpty(innerTransproductValues.get("qtyKgs"))){
																if(UtilValidate.isEmpty(unitGrndTot[productinnerBrandName+"QtyKgs"])){
																	unitGrndTot[productinnerBrandName+"QtyKgs"]=0;
																	unitGrndTot[productinnerBrandName+"QtyKgs"] = innerTransproductValues.get("qtyKgs");
																}
																else{
																	unitGrndTot[productinnerBrandName+"QtyKgs"] += innerTransproductValues.get("qtyKgs");
																}
															}
															if(UtilValidate.isNotEmpty(innerTransproductValues.get("kgFat"))){
																if(UtilValidate.isEmpty(unitGrndTot[productinnerBrandName+"KgFat"])){
																	unitGrndTot[productinnerBrandName+"KgFat"]=0;
																	unitGrndTot[productinnerBrandName+"KgFat"] = innerTransproductValues.get("kgFat");
																}
																else{
																	unitGrndTot[productinnerBrandName+"KgFat"] += innerTransproductValues.get("kgFat");
																}
															}
															if(UtilValidate.isNotEmpty(innerTransproductValues.get("kgSnf"))){
																if(UtilValidate.isEmpty(unitGrndTot[productinnerBrandName+"KgSnf"])){
																	unitGrndTot[productinnerBrandName+"KgSnf"]=0;
																	unitGrndTot[productinnerBrandName+"KgSnf"] = innerTransproductValues.get("kgSnf");
																}
																else{
																	unitGrndTot[productinnerBrandName+"KgSnf"] += innerTransproductValues.get("kgSnf");
																}
															}
															
															
															if(UtilValidate.isNotEmpty(innerTransproductValues.get("price"))){
																if(UtilValidate.isEmpty(unitGrndTot[productinnerBrandName+"Price"])){
																	unitGrndTot[productinnerBrandName+"Price"]=0;
																	unitGrndTot[productinnerBrandName+"Price"] = innerTransproductValues.get("price");
																}
																else{
																	unitGrndTot[productinnerBrandName+"Price"] += innerTransproductValues.get("price");
																}
															}
														}
													}
													innerTranstotMap=procinnerValues.get("TOT");
													if(UtilValidate.isNotEmpty(innerTranstotMap)){
														if(UtilValidate.isEmpty(unitGrndTot["tipAmount"])){
															unitGrndTot["tipAmount"] =0;
															if(UtilValidate.isNotEmpty(innerTranstotMap.get("tipAmt"))){
																unitGrndTot["tipAmount"] = innerTranstotMap.get("tipAmt");
															}
														}else{
															if(UtilValidate.isNotEmpty(innerTranstotMap.get("tipAmt"))){
																unitGrndTot["tipAmount"] += innerTranstotMap.get("tipAmt");
															}
														}
														if(UtilValidate.isEmpty(unitGrndTot["addnAmt"])){
															unitGrndTot["addnAmt"] =0;
															if(UtilValidate.isNotEmpty(innerTranstotMap.get("opCost"))|| UtilValidate.isNotEmpty(innerTranstotMap.get("commissionAmount"))){
																unitGrndTot["addnAmt"] = innerTranstotMap.get("grsAddn")+innerTranstotMap.get("commissionAmount")+innerTranstotMap.get("opCost");
															}
														}else{
															if(UtilValidate.isNotEmpty(innerTranstotMap.get("opCost"))|| UtilValidate.isNotEmpty(innerTranstotMap.get("commissionAmount"))){
																unitGrndTot["addnAmt"] += innerTranstotMap.get("grsAddn")+innerTranstotMap.get("commissionAmount")+innerTranstotMap.get("opCost");
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			shedGrossAmt=shedGrossAmt+totProcGross;
			rate=0;
			totKgs=0;
			totUnitKgFat =0;
			totUnitKgSnf =0;
			totPrice=0;
			for(GenericValue procurementProduct : procurementProductList){
				String productId = procurementProduct.getString("productId");
				String productBrandName = procurementProduct.getString("brandName");
				if(UtilValidate.isNotEmpty(unitGrndTot[productBrandName+"QtyKgs"])){
					totKgs=totKgs+unitGrndTot[productBrandName+"QtyKgs"];
				}
				if(UtilValidate.isNotEmpty(unitGrndTot[productBrandName+"Price"])){
					totPrice=totPrice+unitGrndTot[productBrandName+"Price"];
				}
				if(UtilValidate.isNotEmpty(unitGrndTot[productBrandName+"KgFat"])){
					BigDecimal unitKgFatVal = BigDecimal.ZERO;
					unitKgFatVal = unitGrndTot[productBrandName+"KgFat"];
					unitKgFatVal = unitKgFatVal.setScale(1,BigDecimal.ROUND_HALF_UP);
					unitGrndTot.put(productBrandName+"KgFat",unitKgFatVal);
					totUnitKgFat = totUnitKgFat+unitKgFatVal;
				}
				if(UtilValidate.isNotEmpty(unitGrndTot[productBrandName+"KgSnf"])){
					BigDecimal unitKgSnfVal = BigDecimal.ZERO;
					unitKgSnfVal = unitGrndTot[productBrandName+"KgSnf"];
					unitKgSnfVal = unitKgSnfVal.setScale(2,BigDecimal.ROUND_HALF_UP);
					unitGrndTot.put(productBrandName+"KgSnf",unitKgSnfVal);
					totUnitKgSnf = totUnitKgSnf+unitGrndTot[productBrandName+"KgSnf"];
				}
			}
			unitGrndTot["grossAmt"]=totPrice+unitGrndTot["tipAmount"]+unitGrndTot["addnAmt"];
			if(totKgs != 0){
				rate=(unitGrndTot["grossAmt"]/totKgs).setScale(2,BigDecimal.ROUND_HALF_EVEN);
				mlkAmt=(qtyKgs*rate);
				unitTotalMap["mlkAmount"] = mlkAmt;
				if(UtilValidate.isNotEmpty(mlkAmt)){
					unitTotalMap["totAmt"] = (totalOpCost+mlkAmt);
				}
			}
			unitGrndTot["mixProcQty"] = totKgs;
			unitGrndTot["mixProcKgFat"] = totUnitKgFat;
			unitGrndTot["mixProcKgSnf"] = totUnitKgSnf;
			unitGrndTot["mixedMlkAmount"] = totPrice;
			unitTotalMap["rate"] = rate;
			unitGrndTot["rate"]=rate;
			unitTotalMap["procurement"] = unitGrndTot;
			finalUnitMap.put(unit, unitTotalMap);
		 }
		
		
		boolean addfinalTot = false;
		if(UtilValidate.isNotEmpty(finalUnitMap)){
			for(unit in finalUnitMap.keySet()){
				mixprocQty = 0;
				Map unitWiseTotalsProc = FastMap.newInstance();
				unitWiseTotalsProc.putAll(finalUnitMap.get(unit).get("procurement"));
				mixprocQty = unitWiseTotalsProc.get("mixProcQty");
				if(mixprocQty!=0){
					addfinalTot = true;
					break;
				}
			}
		}
		shedTotalMap.put("qtyLtrs", totQtyLtrs);
		shedTotalMap.put("qtyKgs", totQtyKgs);
		shedTotalMap.put("kgFat", totKgFat);
		shedTotalMap.put("kgSnf", totKgSnf);
		if(totQtyKgs !=0 || addfinalTot){
			if(totQtyKgs!=0){
				shedConList.clear();
				shedConList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
				shedConList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDate)));
				shedConList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "PROC_BILL_MONTH"));
				shedConList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, shed.facilityId));
				List shedCustomTimePeriodsList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",EntityCondition.makeCondition(shedConList,EntityOperator.AND),null,null,null,false);
				shedCustomTimePeriodIds=EntityUtil.getFieldListFromEntityList(shedCustomTimePeriodsList, "customTimePeriodId", false);
				totalSaleAmt=0;
				shedCashRemittance=0;
				shedMaintAmount=0;
				shedCustomTimePeriodIds.each{ shedTimePeriodId->
					List incConList=FastList.newInstance();
					incConList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS, shedTimePeriodId));
					incConList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, shed.facilityId));
					List facilityIncomeAndExpList = delegator.findList("FacilityIncomeExpenditure",EntityCondition.makeCondition(incConList,EntityOperator.AND),null,null,null,false);
				   if(UtilValidate.isNotEmpty(facilityIncomeAndExpList)){
						 facilityIncomeAndExpList.each{ facilityIncExp->
							saleAmt= facilityIncExp.getAt("amount");
							cashRemittance=facilityIncExp.getAt("cashRemittance");
							if(UtilValidate.isNotEmpty(saleAmt)){
								totalSaleAmt=totalSaleAmt+saleAmt;
								if(UtilValidate.isNotEmpty(cashRemittance)){
									shedCashRemittance=shedCashRemittance+cashRemittance;
								}
							}
						 }
				   }
				   List conditionList = FastList.newInstance();
				   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PROC_MRGN")));
				   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shed.facilityId)));
				   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, shedTimePeriodId)));
				   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED")));
				   conditionPeriodBill = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				   periodBillingList = delegator.findList("PeriodBilling",conditionPeriodBill,null,null,null,false);
				   List periodBillingIds = periodBillingList.periodBillingId;
				   List conShedList=FastList.newInstance();
				   conShedList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingIds)));
				   conShedList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shed.facilityId)));
				   shedCond = EntityCondition.makeCondition(conShedList,EntityOperator.AND);
				   List procurementAbstract=FastList.newInstance();
					procurementAbstract = delegator.findList("ProcurementAbstract",shedCond,null,null,null,false);
					if(UtilValidate.isNotEmpty(procurementAbstract)){
						procurementAbstract.each{ procAbstract->
							if(UtilValidate.isNotEmpty(procAbstract.get("grossAmt"))){
								shedMaintAmount=shedMaintAmount+procAbstract.get("grossAmt");
							}
						}
					}
			   }
				shedOutStandDues=0;
				List prevConList = FastList.newInstance();
					prevConList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(preMonthStart)));
				   prevConList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(preMonthEnd)));
				   prevConList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "PROC_BILL_MONTH"));
				   prevConList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, shed.facilityId));
				   List shedPreviousCustomTimePeriodsList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",EntityCondition.makeCondition(prevConList,EntityOperator.AND),null,null,null,false);
				   if(UtilValidate.isNotEmpty(shedPreviousCustomTimePeriodsList)){
					   shedPreviousCustomTimePeriodIds=EntityUtil.getFieldListFromEntityList(shedPreviousCustomTimePeriodsList, "customTimePeriodId", false);
					   shedPreviousCustomTimePeriodIds.each{ shedPreviousTimePeriodId->
						   List prevIncConList=FastList.newInstance();
						   prevIncConList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS, shedPreviousTimePeriodId));
						   prevIncConList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, shed.facilityId));
						   List facilityPreviousIncomeAndExpList = delegator.findList("FacilityIncomeExpenditure",EntityCondition.makeCondition(prevIncConList,EntityOperator.AND),null,null,null,false);
						  if(UtilValidate.isNotEmpty(facilityPreviousIncomeAndExpList)){
								facilityPreviousIncomeAndExpList.each{ facilityPreviousIncExp->
								   prevSaleAmt=0;
								   if(UtilValidate.isNotEmpty(facilityPreviousIncExp.getAt("amount"))){
									   prevSaleAmt= facilityPreviousIncExp.getAt("amount");
								   }
								   prevCashRemittance=0;
								   if(UtilValidate.isNotEmpty(facilityPreviousIncExp.getAt("cashRemittance"))){
									   prevCashRemittance=facilityPreviousIncExp.getAt("cashRemittance");
								   }
								   outStandAmt=prevSaleAmt-prevCashRemittance;
								   if(UtilValidate.isNotEmpty(outStandAmt)){
									   shedOutStandDues=shedOutStandDues+outStandAmt;
								   }
								}
						  }
					   }
				   }
				totalSaleAmt=totalSaleAmt+shedOutStandDues;
				shedIutRcptMlkValue=shedIutRcptMlkValue+shedMaintAmount;
				totalSaleAmt=(totalSaleAmt/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN);
				shedCashRemittance=(shedCashRemittance/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN);
				shedTotalMap.put("milkProductsSaleValue", totalSaleAmt);
				shedTotalMap.put("cashRemittance", shedCashRemittance);
				shedTotalMap.put("outStandDues", (totalSaleAmt-shedCashRemittance));
				//getting shed salaries, store materials, milk products value
				salaryAmt=0;
				storeMaterialAmt=0;
				opCostReleased=0;
				milkProdValue=0;
				List conList = FastList.newInstance();
				conList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
				conList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDate)));
				conList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "HR_MONTH"));
				List customTimePeriodList = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(conList,EntityOperator.AND),null,null,null,false);
				if(UtilValidate.isNotEmpty(customTimePeriodList)){
					expCustomTimePeriodId =EntityUtil.getFirst(customTimePeriodList).get("customTimePeriodId");
					List expList=FastList.newInstance();
					expList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS, expCustomTimePeriodId));
					expList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, shed.facilityId));
					List facilityExpList = delegator.findList("FacilityIncomeExpenditure",EntityCondition.makeCondition(expList,EntityOperator.AND),null,null,null,false);
					if(UtilValidate.isNotEmpty(facilityExpList)){
						facilityExpList.each{ expendature->
							amountTypeId=expendature.getAt("amountTypeId");
							if("SALARY".equals(amountTypeId)){
								salaryAmt=expendature.getAt("amount");
							}
							if("STORE_MATERIAL".equals(amountTypeId)){
								storeMaterialAmt=expendature.getAt("amount");
							}
							if("OPCOST_RELSD".equals(amountTypeId)){
								opCostReleased=expendature.getAt("amount");
							}
							if("MLK_PROD_VAL".equals(amountTypeId)){
								if(UtilValidate.isNotEmpty(expendature.getAt("amount"))){
									milkProdValue=expendature.getAt("amount");
								}
							}
						}
					}
				}
				shedTotalMap.put("salaryAmt", (salaryAmt/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN));
				shedTotalMap.put("storeMaterialAmt", (storeMaterialAmt/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN));
				shedTotalMap.put("opCostReleased", (opCostReleased/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN));
				shedTotalMap.put("milkProdValue", (milkProdValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN));
				shedTotalMap.put("shedIutSentValue", (shedIutSentValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN));
				shedTotalMap.put("shedMlkRecptValue", (shedIutRcptMlkValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN));
			}
			if(UtilValidate.isNotEmpty(finalUnitMap)){
				finalUnitMap.put("TOTAL", shedTotalMap);
				shedWiseTotalsMap.put(shed.facilityId, finalUnitMap);
			}
		}
	 }
 }
		 
context.putAt("shedWiseTotalsMap", shedWiseTotalsMap);
		 
		 
Map grandTotMap = FastMap.newInstance();
//This code is for getting procurement for all sheds.
Map sheWiseProcurementMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(shedList)){
	shedList.each{ shedDetails->
		shedId=shedDetails.facilityId;
		shedTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: shedId]);
		if(UtilValidate.isNotEmpty(shedTotals)){
			shedWiseTotals = shedTotals.get(shedId);
			if(UtilValidate.isNotEmpty(shedWiseTotals)){
				Map shedTot = FastMap.newInstance();
				shedTot.putAll(shedWiseTotals.get("TOT"));				
				sheWiseProcurementMap.put(shedId,shedTot);
				if(UtilValidate.isEmpty(grandTotMap)){
					grandTotMap.putAll(shedTot);
				}else{
					for(productKey in grandTotMap.keySet()){
						Map tempProductWiseMap = FastMap.newInstance();
						tempProductWiseMap.putAll(grandTotMap.get(productKey));
						
						Map tempShedProdMap = FastMap.newInstance();
						tempShedProdMap.putAll(shedTot.get(productKey));						
						for(qtyKey in tempProductWiseMap.keySet()){
							tempProductWiseMap.put(qtyKey, tempProductWiseMap.get(qtyKey)+tempShedProdMap.get(qtyKey));
						}
						grandTotMap.put(productKey, tempProductWiseMap);
					}
				}
			}
		}
	}	
}
if(UtilValidate.isNotEmpty(sheWiseProcurementMap)){
	sheWiseProcurementMap.put("TOT",grandTotMap);
}
context.putAt("sheWiseProcurementMap", sheWiseProcurementMap);






