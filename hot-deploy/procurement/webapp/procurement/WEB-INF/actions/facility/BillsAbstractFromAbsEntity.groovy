import java.security.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;

import java.lang.ref.ReferenceQueue.Null;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.*;
import java.sql.Date;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.procurement.PriceServices;
import org.ofbiz.service.ServiceUtil;

result = ServiceUtil.returnSuccess();
//commAmtGtot = 0;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
		  parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
	}
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);
def populateUnitBillAbstract(unitId , customTimePeriodId){
		context.put("fromDateTime",fromDateTime);
		context.put("thruDateTime",thruDateTime);
		
		//context.put("facilityId",unitId);
	
		facilityDetail = delegator.findOne("Facility", [facilityId : unitId], false);
		if(UtilValidate.isNotEmpty(facilityDetail)){
			unitName = facilityDetail.facilityName;
			unitCode = facilityDetail.facilityCode;
			context.put("unitName",unitName);
			context.put("unitCode",unitCode);
			context.put("shedId",facilityDetail.parentFacilityId);
		}else{
			return;
		}
		
		adjustmentDedTypes = [:];
		orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
		initAdjMap =[:];
		for(int i=0;i<orderAdjItemsList.size();i++){
			orderAdjItem = orderAdjItemsList.get(i);
			adjustmentDedTypes[i] = orderAdjItem;
			initAdjMap[orderAdjItem.orderAdjustmentTypeId] = 0;
		}
		testAdjMap = [:];
		for(int j=0;j < 12;j++){
			if(UtilValidate.isNotEmpty(adjustmentDedTypes.get(j))){
				testAdjMap[j] = (adjustmentDedTypes.get(j).orderAdjustmentTypeId);
			}else{
				testAdjMap[j] = 0.00;
			}
		}
		context.put("testAdjMap",testAdjMap);
		context.put("adjustmentDedTypes",adjustmentDedTypes);
		orderAdjItemsList.addAll(delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_ADDITIONS"),null,null,null,false));
		
		
		Map productDetailsMap = FastMap.newInstance();
		procurementProductList.each { procProd ->
			productDetailsMap.put(procProd.get("productName"), procProd.getAt("productId"));
		}
		
		unitMilkBillAbstMap = [:];
		routeMilkBillAbstMap = [:];
		unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: customTimePeriodId , unitId: unitId]);
		if(ServiceUtil.isError(unitBillAbstract)){
			Debug.logError("error while geting Bill's Abstract unitId: :"+unitId, "");
			return error;
		}
		unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
		unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
		//total
		unitGrndTot = [:];
		unitGrndTot["centerName"] = "TOTAL";
		opCost =0;
		if (UtilValidate.isNotEmpty(unitGrndValuesTot)) {
			for(GenericValue procurementProduct : procurementProductList){
				   String productId = procurementProduct.getString("productId");
				   String productBrandName = procurementProduct.getString("brandName");
				   productAbs = unitGrndValuesTot.getAt(productId);
				   unitGrndTot[productBrandName+"QtyKgs"] = productAbs.get("qtyKgs");
				   unitGrndTot[productBrandName+"QtyLtrs"] = productAbs.get("qtyLtrs");
				   unitGrndTot[productBrandName+"KgFat"] = productAbs.get("kgFat");
				   unitGrndTot[productBrandName+"KgSnf"] = productAbs.get("kgSnf");
				   unitGrndTot[productBrandName+"Price"] = productAbs.get("price");
				   unitGrndTot[productBrandName+"tipAmount"] = productAbs.get("tipAmt");
				
			}
			
			totProductMap = unitGrndValuesTot.get("TOT");
			unitGrndTot["opCost"] = 0;
			unitGrndTot["netRndAmount"]=0;
			if(UtilValidate.isNotEmpty(totProductMap.getAt("opCost"))){
				unitGrndTot["opCost"] = totProductMap.getAt("opCost");
			}
			unitGrndTot["opCostRnd"] = Math.round(unitGrndTot["opCost"]);
			
			unitGrndTot["totQtyKgs"] = totProductMap.get("qtyKgs");
			unitGrndTot["totQtyLtrs"] = totProductMap.get("qtyLtrs");
			unitGrndTot["totKgFat"] = totProductMap.get("kgFat");
			unitGrndTot["totKgSnf"] = totProductMap.get("kgSnf");
			
		}
		
		unitGrndTot["commAmt"] = 0;
		unitGrndTot["cartage"] = 0;
		orderAdjItemsList.each { orderAdj ->
			unitGrndTot[orderAdj.orderAdjustmentTypeId] = 0;
		}
		unitGrndTot["DednsTot"] = 0;
		unitGrndTot["AddsTot"] = 0;
		unitGrndTot["netAmount"] = 0;
		unitGrndTot["grossAmount"] = 0;
		unitGrndTot["TOTtipAmount"] = 0;
		unitGrndTot["solids"] = 0;
		unitRoutesList = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",unitId));
		routesDetailsList = unitRoutesList.get("routesDetailList");
		unitGrndTot["netCenterRndUnitAmount"] = 0;
		for(route in routesDetailsList){
			
			routeCentersList = ProcurementNetworkServices.getRouteAgents(dctx,UtilMisc.toMap("routeId",route.facilityId ));
			centerDetailsList = routeCentersList.get("agentDetailsList");
			routeWiseMilkBillsAbstmap = [:];
			routeWiseMilkBillsAbstmap["commAmt"] = 0;
			routeWiseMilkBillsAbstmap["cartage"] = 0;
			orderAdjItemsList.each { orderAdj ->
				routeWiseMilkBillsAbstmap[orderAdj.orderAdjustmentTypeId] = 0;
			}
			routeWiseMilkBillsAbstmap["TOTtipAmount"] = 0;
			routeWiseMilkBillsAbstmap["cartage"] = 0;
			routeWiseMilkBillsAbstmap["commAmt"] = 0;
			routeWiseMilkBillsAbstmap["grossAmount"] = 0;
			routeWiseMilkBillsAbstmap["DednsTot"] = 0;
			routeWiseMilkBillsAbstmap["AddsTot"] = 0;
			routeWiseMilkBillsAbstmap["netAmount"] = 0;
			routeWiseMilkBillsAbstmap["netRndAmount"]=0;
			routeWiseMilkBillsAbstmap["solids"] = 0;
			routeWiseMilkBillsAbstmap["totQtyKgs"] = 0;
			routeWiseMilkBillsAbstmap["totQtyLtrs"] = 0;
			routeWiseMilkBillsAbstmap["totKgFat"] = 0;
			routeWiseMilkBillsAbstmap["totKgSnf"] = 0;
			
			for(procProd in procurementProductList){
				routeWiseMilkBillsAbstmap[procProd.brandName+"QtyKgs"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"QtyLtrs"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"KgFat"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"KgSnf"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"Price"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"tipAmount"] = 0;
			}
			
			routeWiseMilkBillsAbstmap["centerName"] = "TOTAL";
			routeWiseMilkBillsAbstmap["centerCode"] = route.facilityCode;
			routeWiseMilkBillsAbstmap["centerOwnerName"] = (PartyHelper.getPartyName(delegator, route.ownerPartyId, true)).replace(',', '');
			
			centerDetailsList.each{ center ->
				unitCentersMilkBillAbstMap = [:];
				//centerAbsMap = 
				totPriceForCenter = 0;
				unitCentersMilkBillAbstMap["cartage"] = 0;
				unitCentersMilkBillAbstMap["commAmt"] = 0;
				unitCentersMilkBillAbstMap["grossAmount"] = 0;
				unitCentersMilkBillAbstMap["DednsTot"] = 0;
				unitCentersMilkBillAbstMap["AddsTot"] = 0;
				unitCentersMilkBillAbstMap["netAmount"] = 0;
				unitCentersMilkBillAbstMap["solids"] = 0;
				unitCentersMilkBillAbstMap["centerId"] = center.facilityId;
				unitCentersMilkBillAbstMap["RNO"]   = route.facilityCode;
				unitCentersMilkBillAbstMap["cQtyLtrs"] = 0;
				unitCentersMilkBillAbstMap["ptcCurd"] = 0;
				unitCentersMilkBillAbstMap["sQtyKgs"] = 0;
				unitCentersMilkBillAbstMap["amQtyLtrs"] = 0;
				unitCentersMilkBillAbstMap["pmQtyLtrs"] = 0;
				unitCentersMilkBillAbstMap.put("BANO",  0);
				unitCentersMilkBillAbstMap.put("GBCODE", 0);
				unitCentersMilkBillAbstMap.put("BCODE",  0);
				
				
				for(procProd in procurementProductList){
					unitCentersMilkBillAbstMap[procProd.brandName+"cQtyLtrs"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"ptcCurd"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"sQtyKgs"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"amQtyLtrs"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"pmQtyLtrs"] = 0;
					
					unitCentersMilkBillAbstMap[procProd.brandName+"tipAmount"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"cartage"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"commAmt"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"commAmt"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"grossAmount"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"netAmount"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"solids"] = 0;
					
				}
			
					if(UtilValidate.isNotEmpty(unitAbsTotals)){
					centerMap = unitAbsTotals.getAt(center.facilityId);
					unitCentersMilkBillAbstMap["centerName"] = center.facilityName;
					unitCentersMilkBillAbstMap["centerCode"] = center.facilityCode;
					unitCentersMilkBillAbstMap["centerOwnerName"] = (PartyHelper.getPartyName(delegator, center.ownerPartyId, true)).replace(',', '');
					
					if (UtilValidate.isNotEmpty(centerMap)) {
			
						totProductMap = centerMap.get("TOT");
						unitCentersMilkBillAbstMap["totQtyKgs"] = totProductMap.get("qtyKgs");
						unitCentersMilkBillAbstMap["totQtyLtrs"] = totProductMap.get("qtyLtrs");
						unitCentersMilkBillAbstMap["totKgFat"] = totProductMap.get("kgFat");
						unitCentersMilkBillAbstMap["totKgSnf"] = totProductMap.get("kgSnf");
						
						routeWiseMilkBillsAbstmap["totQtyKgs"] += totProductMap.get("qtyKgs");
						routeWiseMilkBillsAbstmap["totQtyLtrs"] += totProductMap.get("qtyLtrs");
						routeWiseMilkBillsAbstmap["totKgFat"] += totProductMap.get("kgFat");
						routeWiseMilkBillsAbstmap["totKgSnf"] += totProductMap.get("kgSnf");
						
						totPriceForCenter = totProductMap.get("price");
						tipAmount = 0;
						procurementProductList.each { procProd ->
							productMap = centerMap.get(procProd.productId);
							procBrandName = procProd.brandName;
							
							if(UtilValidate.isNotEmpty(productMap)){
								
								unitCentersMilkBillAbstMap[procBrandName+"QtyKgs"] = productMap.get("qtyKgs");
								unitCentersMilkBillAbstMap[procBrandName+"QtyLtrs"] = productMap.get("qtyLtrs");
								unitCentersMilkBillAbstMap[procBrandName+"KgFat"] = productMap.get("kgFat");
								unitCentersMilkBillAbstMap[procBrandName+"KgSnf"] = productMap.get("kgSnf");
								unitCentersMilkBillAbstMap[procBrandName+"Price"] = productMap.get("price");
								
								unitCentersMilkBillAbstMap[procBrandName+"cQtyLtrs"] = productMap.get("cQtyLtrs");
								unitCentersMilkBillAbstMap[procBrandName+"ptcCurd"] = productMap.get("ptcCurd");
								unitCentersMilkBillAbstMap[procBrandName+"sQtyKgs"] = productMap.get("sQtyKgs");
								unitCentersMilkBillAbstMap[procBrandName+"amQtyLtrs"] = productMap.get("amQtyLtrs");
								unitCentersMilkBillAbstMap[procBrandName+"pmQtyLtrs"] = productMap.get("pmQtyLtrs");
								
								
								
								unitCentersMilkBillAbstMap[procBrandName+"commAmt"] = productMap.get("commissionAmount");
								unitCentersMilkBillAbstMap[procBrandName+"cartage"] = productMap.get("cartage");
								unitCentersMilkBillAbstMap[procBrandName+"grossAmount"] = productMap.getAt("grossAmt");
								unitCentersMilkBillAbstMap[procBrandName+"netAmount"] = productMap.getAt("netAmt");
								unitCentersMilkBillAbstMap[procBrandName+"solids"] = productMap.getAt("solids");
								
								
								
								routeWiseMilkBillsAbstmap[procBrandName+"QtyKgs"] += productMap.get("qtyKgs");
								routeWiseMilkBillsAbstmap[procBrandName+"QtyLtrs"] += productMap.get("qtyLtrs");
								routeWiseMilkBillsAbstmap[procBrandName+"KgFat"] += productMap.get("kgFat");
								routeWiseMilkBillsAbstmap[procBrandName+"KgSnf"] += productMap.get("kgSnf");
								routeWiseMilkBillsAbstmap[procBrandName+"Price"] += productMap.get("price");
								
								
								
								tempTipAmt = productMap.get("tipAmt")
								tipAmount += tempTipAmt;
								
								unitCentersMilkBillAbstMap[procBrandName+"tipAmount"] = tempTipAmt;
								
								//unitGrndTot[procBrandName+"tipAmount"] +=  tempTipAmt;
								routeWiseMilkBillsAbstmap[procBrandName+"tipAmount"] +=  tempTipAmt;
							}
						
						}
						unitCentersMilkBillAbstMap["TOTtipAmount"] = totProductMap.getAt("tipAmt");
						//unitGrndTot["TOTtipAmount"] +=  unitCentersMilkBillAbstMap.get("TOTtipAmount");
						routeWiseMilkBillsAbstmap["TOTtipAmount"] +=  totProductMap.getAt("tipAmt");
						
						
						routeWiseMilkBillsAbstmap["commAmt"] += totProductMap.get("commissionAmount");
						routeWiseMilkBillsAbstmap["cartage"] += totProductMap.get("cartage");
						routeWiseMilkBillsAbstmap["grossAmount"] += totProductMap.getAt("grossAmt");
						routeWiseMilkBillsAbstmap["netAmount"] += totProductMap.getAt("netAmt");
						routeWiseMilkBillsAbstmap["netRndAmount"] += Math.round(totProductMap.get("netAmt"));
						routeWiseMilkBillsAbstmap["solids"] += totProductMap.getAt("solids");
						orderAdjItemsList.each{ adjValue ->
							adjTypeId = adjValue.orderAdjustmentTypeId;
							if(UtilValidate.isNotEmpty(totProductMap.get(adjTypeId))){
								if(UtilValidate.isEmpty(routeWiseMilkBillsAbstmap.get(adjTypeId))){
									routeWiseMilkBillsAbstmap[adjTypeId] = totProductMap.get(adjTypeId);
								} else {
									routeWiseMilkBillsAbstmap[adjTypeId] += totProductMap.get(adjTypeId);
								}
							}
						}
						routeWiseMilkBillsAbstmap["DednsTot"] += totProductMap.getAt("grsDed");
						routeWiseMilkBillsAbstmap["AddsTot"] += totProductMap.getAt("grsAddn");
						
						unitGrndTot["commAmt"] += totProductMap.get("commissionAmount");
						unitGrndTot["cartage"] += totProductMap.get("cartage");
						orderAdjItemsList.each{ adjValue ->
							adjTypeId = adjValue.orderAdjustmentTypeId;
							if(UtilValidate.isNotEmpty(totProductMap.get(adjTypeId))){
								if(UtilValidate.isEmpty(unitGrndTot.get(adjTypeId))){
									unitGrndTot[adjTypeId] = totProductMap.get(adjTypeId);
								} else {
									unitGrndTot[adjTypeId] += totProductMap.get(adjTypeId);
								}
							}
						}
						unitGrndTot["DednsTot"] += totProductMap.get("grsDed");
						unitGrndTot["AddsTot"] += totProductMap.get("grsAddn");
						unitGrndTot["grossAmount"] += totProductMap.get("grossAmt");
						unitGrndTot["netAmount"]+= totProductMap.get("netAmt");
						unitGrndTot["TOTtipAmount"] += totProductMap.get("tipAmt");
						unitGrndTot["solids"] += totProductMap.get("solids");
						
						unitGrndTot["netRndAmount"] += Math.round(totProductMap.getAt("netAmt"));
					}
					if("300".equalsIgnoreCase((center.facilityCode))){
						unitCentersMilkBillAbstMap["commAmt"] = totProductMap.getAt("opCost");
						unitCentersMilkBillAbstMap["opCost"] = totProductMap.getAt("opCost");
						unitGrndTot["commAmt"] = unitGrndTot.get("commAmt") + totProductMap.getAt("opCost"); 
					}
					
					
					unitCentersMilkBillAbstMap["cQtyLtrs"] = totProductMap.getAt("cQtyLtrs");
					unitCentersMilkBillAbstMap["ptcCurd"] = totProductMap.getAt("ptcCurd");
					unitCentersMilkBillAbstMap["sQtyKgs"] = totProductMap.getAt("sQtyKgs");
					unitCentersMilkBillAbstMap["amQtyLtrs"] = totProductMap.getAt("amQtyLtrs");
					unitCentersMilkBillAbstMap["pmQtyLtrs"] = totProductMap.getAt("pmQtyLtrs");
					//Bank Details
					if(UtilValidate.isNotEmpty( totProductMap.getAt("finAccountCode"))&&(totProductMap.getAt("finAccountCode")!=0)){
						if(UtilValidate.isNotEmpty( totProductMap.getAt("finAccountCode"))){
							unitCentersMilkBillAbstMap.put("BANO",  totProductMap.getAt("finAccountCode"));
							}
						if(UtilValidate.isNotEmpty( totProductMap.getAt("gbCode"))){
							unitCentersMilkBillAbstMap.put("GBCODE", totProductMap.getAt("gbCode"));
							}
						if(UtilValidate.isNotEmpty( totProductMap.getAt("bCode"))){
							unitCentersMilkBillAbstMap.put("BCODE",  totProductMap.getAt("bCode"));
							}
					}
					//comm And cartage
					billingComm = totProductMap.getAt("commissionAmount");
					billingCartage = totProductMap.getAt("cartage");
					unitCentersMilkBillAbstMap["commAmt"] = unitCentersMilkBillAbstMap.get("commAmt")+billingComm;
					unitCentersMilkBillAbstMap["cartage"] = billingCartage;
					unitCentersMilkBillAbstMap["grossAmount"] += totProductMap.getAt("grossAmt");
					unitCentersMilkBillAbstMap["netAmount"] += totProductMap.getAt("netAmt");
					unitCentersMilkBillAbstMap["solids"] += totProductMap.getAt("solids");
					
					
					
					context.put("unitGrndTot",unitGrndTot);//for bankAbstract
						
					//Adjustments
					orderAdjItemsList.each{ adjValue ->
						
						adjTypeId = adjValue.orderAdjustmentTypeId;
						//Debug.log("adjValue========="+adjTypeId);
						if(UtilValidate.isNotEmpty(totProductMap.get(adjTypeId))){
							unitCentersMilkBillAbstMap[adjTypeId] = totProductMap.get(adjTypeId);
						
						}
						
					}
					unitCentersMilkBillAbstMap["DednsTot"] += totProductMap.getAt("grsDed");
					
					unitCentersMilkBillAbstMap["AddsTot"] += totProductMap.getAt("grsAddn");
					
					//Rnd Net
					unitCentersMilkBillAbstMap["netRndAmount"] = Math.round(unitCentersMilkBillAbstMap.get("netAmount"));
					
					if (!(UtilValidate.isEmpty(unitCentersMilkBillAbstMap.get("totQtyKgs")))) {
						unitMilkBillAbstMap[center.facilityCode] = unitCentersMilkBillAbstMap;
						routeMilkBillAbstMap[center.facilityId] = unitCentersMilkBillAbstMap;
					}
				}
			}
			
			if (!(UtilValidate.isEmpty(routeWiseMilkBillsAbstmap.get("totQtyKgs")))) {
				routeMilkBillAbstMap[route.facilityId] = routeWiseMilkBillsAbstmap;
			}
			
		}//route
		
		//for center order in unitWiseBillsABstract
		unitMilkBillAbstTempMap = [:];
		SortedSet keys = new TreeSet(unitMilkBillAbstMap.keySet());
		for(key in keys) {
		   value = unitMilkBillAbstMap.get(key);
		   unitMilkBillAbstTempMap[key] = value;
		}		
		unitMilkBillAbstTempMap["Total"] = unitGrndTot;
		routeMilkBillAbstMap["Total"] = unitGrndTot;
		context.put("unitMilkBillAbstMap",unitMilkBillAbstTempMap);//For Center Wise And Unit Wise
		//Debug.log("unitMilkBillAbstMap================="+unitMilkBillAbstMap);
		context.put("routeMilkBillAbstMap",routeMilkBillAbstMap);//for Route Wise Bills Abstract
		
		Map unitAbsCsvMap = FastMap.newInstance();
		unitAbsCsvMap.put("abstract",unitMilkBillAbstMap);
		unitAbsCsvMap.put("opcost",unitGrndTot["opCost"]);
		unitAbsCsvMap.put("netRndAmount",unitGrndTot["netRndAmount"]);
		unitAbsCsvMap.put("netRndAmountWithOp",unitGrndTot["netRndAmount"]);
		
		//This Total is for Sms purpose
		if(UtilValidate.isNotEmpty(unitMilkBillAbstTempMap) &&UtilValidate.isNotEmpty(unitMilkBillAbstTempMap.get("Total")) ){
			unitAbsCsvMap.put("Total",unitMilkBillAbstTempMap.get("Total"));
		}
		context.put("unitTotals",unitAbsCsvMap);
		return unitAbsCsvMap;
}// function

customTimePeriodId = parameters.customTimePeriodId;
customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);

if(UtilValidate.isNotEmpty(parameters.unitId) && UtilValidate.isEmpty(parameters.reportTypeFlag)){
	populateUnitBillAbstract(parameters.unitId , parameters.customTimePeriodId);
}else{
		MPABSFoxproCsv =[];
		purchaseTimeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SUPPLY_TYPE"), null, null, null, true);
		context.put("purchaseTimeList",purchaseTimeList);
		Map netPayableMap = FastMap.newInstance();
		Map comparingAbstractEntityMap=FastMap.newInstance();
		Map summeryTotalsMap = FastMap.newInstance();
		if(parameters.shedId){
			shedDetails = delegator.findOne("Facility", [facilityId : parameters.shedId], false);
			List unitsDetailList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(parameters.unitId)){
					unitFacilityDetails = delegator.findOne("Facility", [facilityId : parameters.unitId], false);
					unitsDetailList.add(unitFacilityDetails);
				}else{
					//unitsDetailList.addAll((List)(ProcurementNetworkServices.getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",parameters.shedId))).get("unitsDetailList"));
				unitsDetailList.addAll((List)(ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId])).get("customTimePeriodUnitsDetailList"));
				}
			facilityFinaccountMap =[:];
			for( GenericValue unitDetail : unitsDetailList){
				unitId =  unitDetail.facilityId;
				unitCode =  unitDetail.facilityCode;
				unitBillAbsCsv = populateUnitBillAbstract(unitId , customTimePeriodId);
				if(UtilValidate.isNotEmpty(unitBillAbsCsv)){
					comparingAbstractEntityMap.put(unitId, unitBillAbsCsv);
					if(UtilValidate.isNotEmpty(unitBillAbsCsv.getAt("Total"))){
						summeryTotalsMap.put(unitId, unitBillAbsCsv.getAt("Total"));
					}
				}
				// for unitMilkBill NetPayable
				netPayableMap.put(unitId,unitBillAbsCsv.get("netRndAmount"));
				
				tempBillValueCsv = unitBillAbsCsv.get("abstract");
				for(Map.Entry centerEntry : tempBillValueCsv.entrySet()){
					
					centerCode = centerEntry.getKey();
					centerValue = 	centerEntry.getValue();
					int countFlag =0;
					
					countFlagOpCost =false;
					for(int i=0;i<procurementProductList.size();i++) {
						 procProd = procurementProductList.get(i);
						if(centerValue && centerValue.get("netAmount")){
							
							Map tempCenterMap = FastMap.newInstance();
							productBrandName = procProd.brandName;
							if((centerCode.equals("300")) && countFlagOpCost){
								countFlagOpCost= true;
								continue;
							}
							if(centerCode.equals("300") && (!countFlagOpCost)){
								countFlagOpCost= true;
								productBrandName ="";
							}
							
							tempCenterMap.putAll(initAdjMap);
							tempCenterMap.put("GRSDED", 0);
							tempCenterMap.putAll(centerValue);
							tempCenterMap.put("MCCTYP","1");
							if(Integer.parseInt(centerCode) >= 300){
								tempCenterMap.put("MCCTYP","2");
							}
							tempCenterMap.put("DIST", "MBN");
							if(UtilValidate.isNotEmpty(unitDetail.district)){
									tempCenterMap.put("DIST",unitDetail.district);
								}
							tempCenterMap.put("UCODE", unitCode);
							tempCenterMap.put("CCODE", centerCode);
							tempCenterMap.put("centerId", centerValue.centerId);
							tempCenterMap.put("BDATE", fromDateTime);
							tempCenterMap.put("LDATE", thruDateTime);
							tempCenterMap.put("TYPMLK", productBrandName);
							tempCenterMap.put("MLKLTS", centerValue.get(productBrandName+"QtyLtrs"));
							tempCenterMap.put("MLKKGS",  centerValue.get(productBrandName+"QtyKgs"));
							tempCenterMap.put("MLKAMT",  centerValue.get(productBrandName+"Price"));
							tempCenterMap.put("COMSN",  centerValue.get(productBrandName+"commAmt"));
							tempCenterMap.put("CART", centerValue.get(productBrandName+"cartage"));
							tempCenterMap.put("GRSAMT",  centerValue.get(productBrandName+"grossAmount"));
							
							tempCenterMap.put("NETAMT", centerValue.get(productBrandName+"netAmount"));
							tempCenterMap.put("KGFAT", centerValue.get(productBrandName+"KgFat"));
							tempCenterMap.put("KGSNF", centerValue.get(productBrandName+"KgSnf"));
							tempCenterMap.put("SOLIDS",centerValue.get(productBrandName+"solids"));
							tempCenterMap.put("TIP",  centerValue.get(productBrandName+"tipAmount"));
							tempCenterMap.put("MISCADD",  0);
							if(UtilValidate.isNotEmpty(tempCenterMap.get("MLKLTS"))&& tempCenterMap.get("MLKLTS")>0 ){
								countFlag +=1;
								if(countFlag ==1){
									tempCenterMap.put("MISCADD", centerValue.get("AddsTot"));
									tempCenterMap.put("GRSDED", centerValue.get("DednsTot"));
								}else{
									tempCenterMap.putAll(initAdjMap);
								}
							}else{
								tempCenterMap.putAll(initAdjMap);
							}
							
							if(centerCode.equals("300")){
								//Debug.log("centerCode=========="+centerCode);
								tempCenterMap.put("COMSN",  centerValue.get(productBrandName+"opCost"));
								tempCenterMap.put("MLKLTS", 0);
								tempCenterMap.put("MLKKGS",  0);
								tempCenterMap.put("MLKAMT",  0);
								tempCenterMap.put("KGFAT", 0);
								tempCenterMap.put("KGSNF",  0);
								tempCenterMap.put("SOLIDS",  0);
								tempCenterMap.put("TIP",  0);
							}
							
							
							tempCenterMap.put("CURDLTS", centerValue.get(productBrandName+"cQtyLtrs"));
							tempCenterMap.put("PTCCURD", centerValue.get(productBrandName+"ptcCurd") );
							tempCenterMap.put("SOURKGS",centerValue.get(productBrandName+"sQtyKgs"));
							tempCenterMap.put("MORLTS", centerValue.get(productBrandName+"amQtyLtrs"));
							tempCenterMap.put("EVELTS",centerValue.get(productBrandName+"pmQtyLtrs"));
							if((UtilValidate.isNotEmpty(tempCenterMap.get("MLKLTS"))&& tempCenterMap.get("MLKLTS")>0)|| tempCenterMap.get("NETAMT")!=0){
								MPABSFoxproCsv.add(tempCenterMap);
							}
						} // end of center value
					
					}
				}
			
			}
		}
	context.putAt("comparingAbstractEntityMap", comparingAbstractEntityMap);
	context.putAt("summeryTotalsMap", summeryTotalsMap);
	result.comparingAbstractEntityMap = comparingAbstractEntityMap;
  context.put("netPayableMap",netPayableMap);	// this is for Milkbill netPayable
  context.put("MPABSFoxproCsv", MPABSFoxproCsv);
}
centerWiseMap=[:];
if(UtilValidate.isNotEmpty(context.getAt("MPABSFoxproCsv"))){
	mpAbsFoxPro=context.getAt("MPABSFoxproCsv");
	MPABSFoxproCsv.each{ centerWiseValues->
		amount=0;
		if(UtilValidate.isNotEmpty(centerWiseValues.get("NETAMT"))){
			amount=centerWiseValues.get("NETAMT");
		}
		centerId=centerWiseValues.get("centerId");
		if(UtilValidate.isNotEmpty(centerWiseMap[centerId])){
			centerWiseMap[centerId]+=amount;
		}else{
			centerWiseMap[centerId]=amount;
		}
	}
}
if(UtilValidate.isNotEmpty(centerWiseMap)){
	for(String key in centerWiseMap.keySet()){
		BigDecimal amount = (BigDecimal) centerWiseMap.get(key);
		amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
		centerWiseMap.put(key,amount);
	}
}
context.putAt("billsCenterWiseMap", centerWiseMap);
return result;