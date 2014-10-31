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
	import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
	import in.vasista.vbiz.procurement.ProcurementReports;
	import in.vasista.vbiz.procurement.ProcurementNetworkServices;
	import in.vasista.vbiz.procurement.ProcurementServices;
	import in.vasista.vbiz.procurement.PriceServices;
	
	fromDate = parameters.fromDate;
	thruDate = parameters.thruDate;
	if(UtilValidate.isEmpty(fromDate)){
		Debug.logError("fromDate Cannot Be Empty","");
		context.errorMessage = "FromDate Cannot Be Empty.......!";
		return;
	}
	if(UtilValidate.isEmpty(thruDate)){
		Debug.logError("thruDate Cannot Be Empty","");
		context.errorMessage = "ThruDate Cannot Be Empty.......!";
		return;
	}
	def sdf1 = new SimpleDateFormat("yyyy/MM/dd");
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		if (fromDate) {
			fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
		}
		if (thruDate) {
			thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	context.putAt("fromDate", fromDateStart);
	context.putAt("thruDate", thruDateEnd);
	
	prevFromDate = UtilDateTime.previousYearDateString(fromDateStart.toString());
	prevThruDate = UtilDateTime.previousYearDateString(thruDateEnd.toString());
	def sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	try {
		if (prevFromDate) {
			prevDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevFromDate).getTime()));
		}
		if (prevThruDate) {
			prevDateEnd = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevThruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	totalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
	
	if(totalDays > 31){
		Debug.logError("You Cannot Choose More Than 31 Days.","");
		context.errorMessage = "You Cannot Choose More Than 31 Days";
		return;
	}
	context.put("totalDays",totalDays);
	
	List dateKeysList = FastList.newInstance();
	Map dayKeysMap = FastMap.newInstance();
	totalDays=totalDays+1;
	for(int i=0; i <totalDays; i++){
		currentDayTimeStart = UtilDateTime.getDayStart(fromDateStart, i);
		currentDayTimeEnd = UtilDateTime.getDayEnd(currentDayTimeStart);
		date = UtilDateTime.toDateString(currentDayTimeStart,"dd/MM");
		dateKeysList.add(date);
		dayKeysMap.put(UtilDateTime.toDateString(currentDayTimeStart,"yyyy/MM/dd"), date);
	}
	context.putAt("dateKeysList", dateKeysList);
	context.putAt("dayKeysMap", dayKeysMap);
	
	List mccTypeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED" )  , null, null, null, false );
	mccTypeIdsList = EntityUtil.getFieldListFromEntityList(mccTypeList, "mccTypeId", true);
	mccTypeIdsList.add("dayTotals");
	context.put("mccTypes",mccTypeIdsList);
	
	dctx = dispatcher.getDispatchContext();
	
	grandTotMap =[:];
	prevGrandTotal = 0;
	currentShedFinalMap =[:];
	mpfMilkReceiptsMap =[:];
	mpfMilkReceiptsMap = MilkReceiptReports.getAllMilkReceipts(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd]);
	Map prevYearMapData = FastMap.newInstance();
	if(UtilValidate.isNotEmpty(mpfMilkReceiptsMap)){
		Map milkReceipts = mpfMilkReceiptsMap.get("milkReceiptsMap");
		if(UtilValidate.isNotEmpty(milkReceipts)){
			Iterator mccIter = milkReceipts.entrySet().iterator();
			while(mccIter.hasNext()){
			Map.Entry mccEntry = mccIter.next();
				if(!"dayTotals".equals(mccEntry.getKey())){
					Map mccTypeTotals = FastMap.newInstance();
					prevMccTypeTotals = 0;
					value = mccEntry.getValue();
					Iterator shedIter = value.entrySet().iterator();
					while(shedIter.hasNext()){
						Map.Entry shedEntry = shedIter.next();
						Map shedWiseTotals = FastMap.newInstance();
						prevShedTotals = 0;
						if(!"dayTotals".equals(shedEntry.getKey())){
							unitValue =shedEntry.getValue();
							Iterator unitIter = unitValue.entrySet().iterator();
							while(unitIter.hasNext()){
								Map.Entry unitEntry = unitIter.next();
								currentQtyDateMap =[:];
								if(!"dayTotals".equals(unitEntry.getKey())){
									unitVal =unitEntry.getValue();
									if(UtilValidate.isNotEmpty(unitVal)){
										unitDayTotals = unitVal.get("dayTotals");
										if(UtilValidate.isNotEmpty(unitDayTotals)){
											for(dateKey in unitDayTotals.keySet()){
												if(dateKey != "TOT"){
													dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
													reqDateFormate = UtilDateTime.toDateString(dateFormate,"dd/MM");
													recQtyLtrs = unitDayTotals.get(dateKey).get("TOT").get("recdQtyLtrs");
													//roundedValue = (recQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
													if(UtilValidate.isEmpty(currentQtyDateMap.get(reqDateFormate))){
														currentQtyDateMap.put(reqDateFormate, recQtyLtrs);
													}else{
														currentQtyDateMap.put(reqDateFormate, currentQtyDateMap.get(reqDateFormate)+recQtyLtrs);
													}
													// populating shedWise Totals
													if(UtilValidate.isEmpty(shedWiseTotals) || (UtilValidate.isNotEmpty(shedWiseTotals) && UtilValidate.isEmpty(shedWiseTotals.get(reqDateFormate)))){
														shedWiseTotals.put(reqDateFormate, recQtyLtrs);
													}else{
														shedWiseTotals.put(reqDateFormate, shedWiseTotals.get(reqDateFormate)+recQtyLtrs);
													 }
													// populating mccTypeTotals
													if(UtilValidate.isEmpty(mccTypeTotals) ||( UtilValidate.isNotEmpty(mccTypeTotals) && UtilValidate.isEmpty(mccTypeTotals.get(reqDateFormate)))){
														mccTypeTotals.put(reqDateFormate, recQtyLtrs);
													}else{
														mccTypeTotals.put(reqDateFormate, mccTypeTotals.get(reqDateFormate)+recQtyLtrs);
													 }
													// populating mccTypeTotals
													if(UtilValidate.isEmpty(grandTotMap) ||( UtilValidate.isNotEmpty(grandTotMap) && UtilValidate.isEmpty(grandTotMap.get(reqDateFormate)))){
														grandTotMap.put(reqDateFormate, recQtyLtrs);
													}else{
														grandTotMap.put(reqDateFormate,grandTotMap.get(reqDateFormate)+recQtyLtrs);
													 }
												}else{
											// populating Rounding Values 
													if(UtilValidate.isNotEmpty(currentQtyDateMap)){
														for(dateKeyString in currentQtyDateMap.keySet()){
															BigDecimal qty = currentQtyDateMap.get(dateKeyString);
															BigDecimal roundedValue = qty.divide(new BigDecimal(100000),2,BigDecimal.ROUND_HALF_UP);
															currentQtyDateMap.put(dateKeyString,roundedValue);
														}
													}
												}	
											}
										}
										if(UtilValidate.isNotEmpty(currentQtyDateMap)){
											currentShedFinalMap.put(unitEntry.getKey(), currentQtyDateMap);
										}
									}
									// populating PreviousYearMilkReceipts 
									prevGrandTotMap =[:];
									previousShedFinalMap =[:];
									milkReceiptsPrevTotalMap =[:];
									milkReceiptsPrevTotalMap = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: prevDateStart , thruDate: prevDateEnd , facilityId: unitEntry.getKey()]);
									prevUnitTotals = 0;
									if(UtilValidate.isNotEmpty(milkReceiptsPrevTotalMap)){
										Map prevYearReceipts = FastMap.newInstance();
										prevYearReceipts.putAll( milkReceiptsPrevTotalMap.get(unitEntry.getKey()));
										if(UtilValidate.isNotEmpty(prevYearReceipts)){
											Map prevUnitDayTotals = FastMap.newInstance();
											prevUnitDayTotals.putAll((prevYearReceipts.get("dayTotals").get("TOT").get("TOT")));
											prevUnitTotals = prevUnitDayTotals.get("recdQtyLtrs");
											roundedValue = (prevUnitTotals/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											prevShedTotals = prevShedTotals+roundedValue;
											prevMccTypeTotals = prevMccTypeTotals+roundedValue;
											prevGrandTotal = prevGrandTotal+roundedValue;
											prevUnitTotals = roundedValue;
										}
									}
									prevYearMapData.put(unitEntry.getKey(), prevUnitTotals);
								}
							}
							//populating shedWise Totals Rounding
							if(UtilValidate.isNotEmpty(shedWiseTotals)){
							   for(dateKeyStr in shedWiseTotals.keySet()){
								   BigDecimal shedQty = shedWiseTotals.get(dateKeyStr);
								   BigDecimal shedRoundedValue = shedQty.divide(new BigDecimal(100000),2,BigDecimal.ROUND_HALF_UP);
								   shedWiseTotals.put(dateKeyStr,shedRoundedValue);
							   }
							}
							currentShedFinalMap.put(shedEntry.getKey(), shedWiseTotals);
							prevYearMapData.put(shedEntry.getKey(), prevShedTotals);
						}
					}
					currentShedFinalMap.put(mccEntry.getKey(), mccTypeTotals);
					prevYearMapData.put(mccEntry.getKey(), prevMccTypeTotals);
					//populating mccTypes
					if(UtilValidate.isNotEmpty(mccTypeTotals)){
						for(dateKeyStr in mccTypeTotals.keySet()){
							BigDecimal mccQty = mccTypeTotals.get(dateKeyStr);
							BigDecimal mccRoundedValue = mccQty.divide(new BigDecimal(100000),2,BigDecimal.ROUND_HALF_UP);
							mccTypeTotals.put(dateKeyStr,mccRoundedValue);
						}
					 }
				}else{
				if(UtilValidate.isNotEmpty(grandTotMap)){
					for(dateKeyStr in grandTotMap.keySet()){
						BigDecimal grandQty = grandTotMap.get(dateKeyStr);
						BigDecimal grandRoundedValue = grandQty.divide(new BigDecimal(100000),2,BigDecimal.ROUND_HALF_UP);
						grandTotMap.put(dateKeyStr,grandRoundedValue);
					}
				}
				currentShedFinalMap.put(mccEntry.getKey(), grandTotMap);
				prevYearMapData.put(mccEntry.getKey(), prevGrandTotal);
				}
			}
		}
	}
	context.put("currentShedFinalMap",currentShedFinalMap);
	context.put("prevYearMapData",prevYearMapData);
