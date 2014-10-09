import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;


import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementServices;

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
Timestamp fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
Timestamp thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate); 
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
context.putAt("noOfDays", (UtilDateTime.getIntervalInDays(fromDate, thruDate))+1);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.shedId;
facility = delegator.findOne("Facility",[facilityId:facilityId],false);
milkBillList = [];
opCost = 0;
//shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : facilityId]);
shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);

List unitIds = FastList.newInstance();
unitIds.addAll(shedUnits.unitsList);

if(!UtilValidate.isEmpty(facility)){
	context.put("facility",facility);
	productsList = [];
	productsList = ProcurementNetworkServices.getProcurementProducts(dctx,UtilMisc.toMap());
	productRatesList = [];
	//test to be done
	productsBrandMap = [:];
	for(product in productsList){
		productMap = [:];
		productMap.put("productName",product.brandName);
		rateMap = [:];
	    rateMap = PriceServices.getProcurementProductPrice(dctx,[userLogin:userLogin,facilityId:facilityId,priceDate:fromDate, productId:product.productId,fatPercent:BigDecimal.ZERO,snfPercent:BigDecimal.ZERO]);
		productMap.put("defaultRate",rateMap.defaultRate);
		if((rateMap.useTotalSolids)=="N"){
			productMap.put("using","KGFAT");
		}else{
			productMap.put("using","TOTAL SOLIDS");
		}
		Map<String, Object> getRateInMap = FastMap.newInstance();
		getRateInMap.put("userLogin", userLogin);
		getRateInMap.put("rateCurrencyUomId", "INR");
		getRateInMap.put("facilityId", facilityId);
		getRateInMap.put("productId", product.productId);
		getRateInMap.put("fromDate",fromDate);
		getRateInMap.put("rateTypeId", "PROC_TIP_AMOUNT");
		Map<String, Object> tipAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", getRateInMap);
		BigDecimal tipRate = BigDecimal.ZERO;
		if(ServiceUtil.isSuccess(tipAmtMap)){
			tipRate = (BigDecimal)tipAmtMap.get("rateAmount");
		}
		productMap.put("defaultRate",(productMap.get("defaultRate"))+tipRate);
		productRatesList.add(productMap);
		productsBrandMap[product.brandName]=product.productName;
	}
	context.putAt("productsBrandMap",productsBrandMap);
	context.putAt("productRatesList",productRatesList);
	/*facilityPeriodTotals = [:];
	facilityPeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:facilityId,userLogin:userLogin]);*/
	totalsMap =[:];
	shortages =[:];
	amounts	= [:];
	/*if(UtilValidate.isNotEmpty(facilityPeriodTotals.get(facilityId))&& UtilValidate.isNotEmpty(facilityPeriodTotals.get(facilityId).get("dayTotals"))){
		totalsMap = facilityPeriodTotals.get(facilityId).get("dayTotals").get("TOT").get("TOT");
	}*/
	shedWiseTotalAmt=[:];
	if(UtilValidate.isNotEmpty(context.getAt("shedWiseTotalsMap"))){
		shedWiseTotalAmt=context.getAt("shedWiseTotalsMap");
		
	}
	//getting shed total Ltrs
	shedTotLtrsMap=[:];
	
	shedTotLtrsMap=[:];
	for(key in productsBrandMap.keySet()){
		productName = productsBrandMap.get(key);		
		shedTotLtrsMap[productName]=0;
	}	
	unitIds.each{ unit->
		//unitDetailTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:unit,userLogin:userLogin]);
		
		unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: unit]);
		if(UtilValidate.isNotEmpty(unitBillAbstract)){
			unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
			unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
			if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
				productsList.each{ product->
					if(UtilValidate.isNotEmpty(unitGrndValuesTot.getAt(product.productId))){
						productAbs = unitGrndValuesTot.getAt(product.productId);
						sQtyLtrs = (ProcurementNetworkServices.convertKGToLitre(productAbs.get("sQtyKgs")));
						qtyLtrs= productAbs.get("qtyLtrs");
						shedTotLtrsMap[product.productName] +=qtyLtrs
					}
				}
			}
		}
		
	}
	
	context.putAt("shedTotLtrsMap", shedTotLtrsMap);
	//unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: facilityId]);
	feedAmt =0;
	cessOnSaleAmt =0;
	if(UtilValidate.isNotEmpty(shedWiseTotalAmt)){
			if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("MILKPROC_FEEDDED"))){
				feedAmt=shedWiseTotalAmt.get("MILKPROC_FEEDDED");
			}
			if(facilityId.equals("WGD")){
				if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("MILKPROC_MSPARES"))){
					cessOnSaleAmt=shedWiseTotalAmt.get("MILKPROC_MSPARES");
				}
			  }else{
			if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("MILKPROC_CESSONSALE"))){
				cessOnSaleAmt=shedWiseTotalAmt.get("MILKPROC_CESSONSALE");
			}
			if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("MILKPROC_COLSALE"))){
				cessOnSaleAmt +=shedWiseTotalAmt.get("MILKPROC_COLSALE");
			}
		}
	}
	/*if(UtilValidate.isNotEmpty(unitAdjustments)){
		adjustments = unitAdjustments.adjustmentsTypeMap;
		if(UtilValidate.isNotEmpty(adjustments)){
			deductions = adjustments.get("MILKPROC_DEDUCTIONS");
			if(UtilValidate.isNotEmpty(deductions.get("MILKPROC_FEEDDED"))){
				feedAmt = feedAmt+ (deductions.get("MILKPROC_FEEDDED")).setScale(0,BigDecimal.ROUND_HALF_UP);
				}
			if(UtilValidate.isNotEmpty(deductions.get("MILKPROC_CESSONSALE"))){
				cessOnSaleAmt = cessOnSaleAmt+ (deductions.get("MILKPROC_CESSONSALE")).setScale(0,BigDecimal.ROUND_HALF_UP);
				}
			if(UtilValidate.isNotEmpty(deductions.get("MILKPROC_COLSALE"))){
				cessOnSaleAmt = cessOnSaleAmt+ (deductions.get("MILKPROC_COLSALE")).setScale(0,BigDecimal.ROUND_HALF_UP);
				}
		}
	}*/
	// here we are getting ddAccountDetails to know ddAccount having FeedRecovery or Not
	ddAmountDetails=delegator.findOne("FacilityAttribute",[facilityId : parameters.shedId, attrName: "DDACCOUNTAMOUNT"], false);
	if(((UtilValidate.isNotEmpty(ddAmountDetails))&&(("Y".equals(ddAmountDetails.get("attrValue")))))){
		feedAmt=0;
	}
	context.put("feedAmt",feedAmt);
	context.put("cessOnSaleAmt",cessOnSaleAmt);
	context.put("difAmt",0);
	totAmountsMap = context.get("totAmountsMap");
	if(UtilValidate.isNotEmpty(shedWiseTotalAmt)){
	for(key in productsBrandMap.keySet()){
		productName = productsBrandMap.get(key);
		amt = 0;
		amt = shedWiseTotalAmt.get(key+"Amount");
		totAmountsMap.put(key,amt.setScale(0,BigDecimal.ROUND_HALF_UP));
	}
	BigDecimal shedOpcost = BigDecimal.ZERO;
	BigDecimal shedCommAmt = BigDecimal.ZERO;
	BigDecimal shedTipAmt = BigDecimal.ZERO;
	BigDecimal shedCartage = BigDecimal.ZERO;
	
	if(UtilValidate.isNotEmpty(shedWiseTotalAmt)){
		if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("opCost"))){
			shedOpcost = shedWiseTotalAmt.get("opCost");
		}
		if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("commissionAmount"))){
			shedCommAmt = shedWiseTotalAmt.get("commissionAmount");
		}
		if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("cartage"))){
			shedCartage = shedWiseTotalAmt.get("cartage");
		}
		if(UtilValidate.isNotEmpty(shedWiseTotalAmt.get("tipAmount"))){
			shedTipAmt = shedWiseTotalAmt.get("tipAmount");
		}
	}
	
	totAmountsMap.putAt("opCost", shedOpcost.setScale(0, BigDecimal.ROUND_HALF_UP));
	totAmountsMap.putAt("commAmt", shedCommAmt.setScale(0, BigDecimal.ROUND_HALF_UP));
	totAmountsMap.putAt("cartage", shedCartage.setScale(0, BigDecimal.ROUND_HALF_UP));
	totAmountsMap.putAt("tipAmt", shedTipAmt.setScale(0, BigDecimal.ROUND_HALF_UP));
	/*sprice = 0;
	sprice = totalsMap.get("TOT").get("sPrice");
	totAmountsMap.put("sprice",sprice.setScale(0,BigDecimal.ROUND_HALF_UP));*/
	context.putAt("totAmountsMap", totAmountsMap);
	//context.putAt("totalsMap", totalsMap);
	//for calculating variations in fat and snf
	centersFatSnfList = [];
	childFacilities = [];
	totProcKgFat = 0;
	totProcKgSnf = 0;
	totRecvKgFat = 0;
	totRecvKgSnf = 0;
	totProcQtyKgs = 0;
	totRecvQtyKgs = 0;
	List tempFacilityIdToList = FastList.newInstance();
	List childConditionList = FastList.newInstance();
	List transferFacililityCondList = FastList.newInstance();
	transferFacililityCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds),EntityOperator.OR, EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,unitIds)));
	transferFacililityCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	transferFacililityCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	transferFacililityCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
	EntityCondition transferFacilityCondition = EntityCondition.makeCondition(transferFacililityCondList,EntityOperator.AND);

	List transferFacilitiesList = delegator.findList("MilkTransfer",transferFacilityCondition,null,null,null,false);
	
	childConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
	childConditionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
	EntityCondition childFacilityCondition = EntityCondition.makeCondition(childConditionList,EntityOperator.AND);
	
	List childFacilityTransfersList = EntityUtil.filterByCondition(transferFacilitiesList,childFacilityCondition);
	
	Set childFacilitiesSet= null;
	childFacilitiesList = EntityUtil.getFieldListFromEntityList(childFacilityTransfersList,"facilityId", true);
	childFacilityIds = new HashSet(childFacilitiesList);
	for(childFacilityId in childFacilityIds){
		unitIds.remove(childFacilityId);
		}
	if(UtilValidate.isNotEmpty(childFacilityIds)){
		BigDecimal grProcTotKgs = BigDecimal.ZERO;
		BigDecimal grProcTotKgFat = BigDecimal.ZERO;
		BigDecimal grProcTotKgSnf = BigDecimal.ZERO;
		BigDecimal grRecvTotKgs = BigDecimal.ZERO;
		BigDecimal grRecvTotKgFat = BigDecimal.ZERO;
		grRecvTotKgSnf = BigDecimal.ZERO;
		for(childFacilityId in childFacilityIds){
			childFacility = delegator.findOne("Facility",["facilityId":childFacilityId],false);
			centersFatSnfMap = [:];
			childFacilityId = childFacility.get("facilityId");
			String childFacilityCode = childFacility.get("facilityCode");
			int childFacCode = childFacilityCode.toInteger();
			childFacilityName = childFacility.get("facilityName");
			Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", childFacilityId));
			List facilityIds = FastList.newInstance();
			if(UtilValidate.isNotEmpty(facilityAgents)){
				facilityIds= (List) facilityAgents.get("facilityIds");
			}
			// we have to consider the Procurement  of other units which sends milk to this unit, to get Procuerment under This Unit
			List facilityTransferList = FastList.newInstance();
			List facilityRecievedConditionList = FastList.newInstance();
			facilityRecievedConditionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,childFacilityId));
			facilityRecievedConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
			EntityCondition facilityRecievedCondition = EntityCondition.makeCondition(facilityRecievedConditionList,EntityOperator.AND);
			
			facilityTransferList = EntityUtil.filterByCondition(transferFacilitiesList,facilityRecievedCondition);
			
			// here we are considering internal unit transfers
			List facTranIds = FastList.newInstance();
			facTranIds.addAll((List)EntityUtil.getFieldListFromEntityList(facilityTransferList,"facilityId", true));
			
			for(facTranId in facTranIds){
				unitIds.remove(facTranId);
				}
			
			List facilityRecdConditionList = FastList.newInstance();
			facilityRecdConditionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,facTranIds));
			facilityRecdConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
			EntityCondition facilityRecdCondition = EntityCondition.makeCondition(facilityRecdConditionList,EntityOperator.AND);
			List facilityTranList = EntityUtil.filterByCondition(transferFacilitiesList,facilityRecdCondition);
			
			facilityTransferList.addAll(facilityTranList);
			for(transfer in facilityTransferList){
				String tempFacilityId = null;
				tempFacilityId = transfer.get("facilityId");
				Map tempFacilityAgents = FastMap.newInstance();
				unitIds.remove(tempFacilityId);
				tempFacilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", tempFacilityId));
				if(UtilValidate.isNotEmpty(tempFacilityAgents)){
					List tempFacilityAgentIds = FastList.newInstance();
					tempFacilityAgentIds =(List) tempFacilityAgents.get("facilityIds");
					facilityIds.addAll(tempFacilityAgentIds);
					}
			}
			procConList =  [];
			procConList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN,facilityIds));
			procConList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));    		
        	procConList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));    		
        	procConList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));    		
			procConList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
			procConList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
			EntityCondition procEntityCondition = EntityCondition.makeCondition(procConList,EntityOperator.AND);
			List procuredList = FastList.newInstance();
			procuredList = delegator.findList("OrderHeaderItemProductAndFacility",procEntityCondition,null,null,null,false);
			
			BigDecimal procTotKgFat = BigDecimal.ZERO;
			BigDecimal procTotKgSnf = BigDecimal.ZERO;
			BigDecimal procTotKgs = BigDecimal.ZERO;
			BigDecimal recvTotKgFat = BigDecimal.ZERO;
			BigDecimal recvTotKgSnf = BigDecimal.ZERO;
			BigDecimal recvTotKgs = BigDecimal.ZERO;
			for(procurement in procuredList){
				BigDecimal sQtyKgs = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(procurement.get("sQuantityLtrs"))){
					sQtyKgs = ((BigDecimal)procurement.get("sQuantityLtrs")).multiply(new BigDecimal(1.03));
					}
				BigDecimal qtyKgs = ((BigDecimal)procurement.get("quantity"));
				BigDecimal fat = procurement.fat;
				if(fat.compareTo(BigDecimal.ZERO)==0){
						fat=procurement.sFat;
					}
				BigDecimal snf = procurement.snf;
				BigDecimal kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,fat);
				BigDecimal kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,snf);
				procTotKgs += qtyKgs; 
				procTotKgFat += kgFat;
				procTotKgSnf += kgSnf;
			}
			grProcTotKgs += procTotKgs;
			grProcTotKgFat += procTotKgFat;
			grProcTotKgSnf += procTotKgSnf;
			
			if(procTotKgs!=0){
				centersFatSnfMap.put("facilityCode",childFacCode);
				centersFatSnfMap.put("facilityName",childFacilityName);
				BigDecimal tempFat = BigDecimal.ZERO;
				tempFat = ProcurementNetworkServices.calculateFatOrSnf(procTotKgFat, procTotKgs);
				centersFatSnfMap["procFat"] =  tempFat.setScale(1,BigDecimal.ROUND_HALF_UP);
				BigDecimal tempSnf = BigDecimal.ZERO;
				tempSnf = ProcurementNetworkServices.calculateFatOrSnf(procTotKgSnf, procTotKgs);
				centersFatSnfMap["procSnf"] = tempSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			//here we are calculating recieved fat ,snf
			//getting records of transfered Milk
			List mpfRecievedConditionList = FastList.newInstance();
			mpfRecievedConditionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
			mpfRecievedConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,childFacilityId));
			mpfRecievedCondition = EntityCondition.makeCondition(mpfRecievedConditionList,EntityOperator.AND);
			List mpfRecievedList = EntityUtil.filterByCondition(transferFacilitiesList,mpfRecievedCondition);
			if(UtilValidate.isEmpty(mpfRecievedList)){
				centersFatSnfMap["recvFat"] = BigDecimal.ZERO;
				centersFatSnfMap["recvSnf"] = BigDecimal.ZERO;
			}else{
				for(mpfRecieved in mpfRecievedList){
					tempRecvKgs = mpfRecieved.receivedQuantity;
					tempRecvKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(tempRecvKgs,mpfRecieved.receivedFat);
					tempRecvKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(tempRecvKgs,mpfRecieved.receivedSnf);
					recvTotKgs += tempRecvKgs;
					recvTotKgFat += tempRecvKgFat;
					recvTotKgSnf += tempRecvKgSnf;
				}
				grRecvTotKgs += recvTotKgs;
				grRecvTotKgFat += recvTotKgFat;
				grRecvTotKgSnf += recvTotKgSnf;
				BigDecimal tempFat = BigDecimal.ZERO;
				tempFat = ProcurementNetworkServices.calculateFatOrSnf(recvTotKgFat, recvTotKgs);
				centersFatSnfMap["recvFat"] = tempFat.setScale(1,BigDecimal.ROUND_HALF_UP);
				BigDecimal tempSnf = BigDecimal.ZERO;
				tempSnf = ProcurementNetworkServices.calculateFatOrSnf(recvTotKgSnf, recvTotKgs);
				centersFatSnfMap["recvSnf"] = tempSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			centersFatSnfMap["varFat"] = BigDecimal.ZERO;
			centersFatSnfMap["varSnf"] = BigDecimal.ZERO;
			if((UtilValidate.isNotEmpty(centersFatSnfMap.get("procFat")))&&(UtilValidate.isNotEmpty(centersFatSnfMap.get("recvFat")))){
					centersFatSnfMap["varFat"] = centersFatSnfMap.get("recvFat")-centersFatSnfMap.get("procFat");
				}
			if((UtilValidate.isNotEmpty(centersFatSnfMap.get("procSnf")))&&(UtilValidate.isNotEmpty(centersFatSnfMap.get("recvSnf")))){
				centersFatSnfMap["varSnf"] = centersFatSnfMap.get("recvSnf")-centersFatSnfMap.get("procSnf");
			}
			if(UtilValidate.isNotEmpty(centersFatSnfMap.facilityName)){
				centersFatSnfList.add(centersFatSnfMap);
			}
		}
		centersFatSnfList=UtilMisc.sortMaps(centersFatSnfList, UtilMisc.toList("facilityCode"));
		tempfatSnfMap = [:];
		tempfatSnfMap.put("facilityCode",-1);
		tempfatSnfMap.put("facilityName",facility.get("facilityName"));
		tempfatSnfMap.put("procFat", BigDecimal.ZERO);
		tempfatSnfMap.put("procSnf", BigDecimal.ZERO);
		tempfatSnfMap.put("recvFat", BigDecimal.ZERO);
		tempfatSnfMap.put("recvSnf", BigDecimal.ZERO);
		
		if(grProcTotKgs!=0){
			BigDecimal tempFat = BigDecimal.ZERO;
			tempFat = ProcurementNetworkServices.calculateFatOrSnf(grProcTotKgFat, grProcTotKgs);
			tempfatSnfMap.put("procFat", tempFat.setScale(1,BigDecimal.ROUND_HALF_UP));
			BigDecimal tempSnf = BigDecimal.ZERO;
			tempSnf = ProcurementNetworkServices.calculateFatOrSnf(grProcTotKgSnf, grProcTotKgs);
			tempfatSnfMap["procSnf"] = tempSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		if(grRecvTotKgs!=0){
			BigDecimal tempFat = BigDecimal.ZERO;
			tempFat = ProcurementNetworkServices.calculateFatOrSnf(grRecvTotKgFat, grRecvTotKgs);
			tempfatSnfMap.put("recvFat", tempFat.setScale(1,BigDecimal.ROUND_HALF_UP));
			BigDecimal tempSnf = BigDecimal.ZERO;
			tempSnf = ProcurementNetworkServices.calculateFatOrSnf(grRecvTotKgSnf, grRecvTotKgs);
			tempfatSnfMap["recvSnf"] = tempSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		tempfatSnfMap["varFat"] = BigDecimal.ZERO;
		tempfatSnfMap["varSnf"] = BigDecimal.ZERO;
		if((UtilValidate.isNotEmpty(tempfatSnfMap.get("procFat")))&&(UtilValidate.isNotEmpty(tempfatSnfMap.get("recvFat")))){
				tempfatSnfMap["varFat"] = tempfatSnfMap.get("recvFat")-tempfatSnfMap.get("procFat");
			}
		if((UtilValidate.isNotEmpty(tempfatSnfMap.get("procSnf")))&&(UtilValidate.isNotEmpty(tempfatSnfMap.get("recvSnf")))){
			tempfatSnfMap["varSnf"] = tempfatSnfMap.get("recvSnf")-tempfatSnfMap.get("procSnf");
		}
		centersFatSnfList.add(tempfatSnfMap);
		context.put("centersFatSnfList",centersFatSnfList);
	 }
	}
}
