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
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.PriceServices;
Timestamp fromDate;
Timestamp thruDate; 
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if(parameters.thruDate){
		   thruDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.putAt("dctx", dctx);
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("procurementProductList", procurementProductList);

List productWiseList = FastList.newInstance();
productWiseList.addAll(EntityUtil.getFieldListFromEntityList(procurementProductList, "productId", true));
Map initMap = FastMap.newInstance();
initMap.put("recdQtyLtrs", BigDecimal.ZERO);
initMap.put("recdQtyKgs", BigDecimal.ZERO);
initMap.put("recdKgFat", BigDecimal.ZERO);
initMap.put("recdKgSnf", BigDecimal.ZERO);
initMap.put("recdFat", BigDecimal.ZERO);
initMap.put("recdSnf", BigDecimal.ZERO);
initMap.put("sendQtyLtrs", BigDecimal.ZERO);
initMap.put("sendQtyKgs", BigDecimal.ZERO);
initMap.put("sendKgFat", BigDecimal.ZERO);
initMap.put("sendKgSnf", BigDecimal.ZERO);
initMap.put("totSolids", BigDecimal.ZERO);
initMap.put("opCost", BigDecimal.ZERO);

Map initProductMap = FastMap.newInstance();
for(productId in productWiseList){
		tempInitMap = [:];
		tempInitMap.putAll(initMap);
		initProductMap.put(productId, tempInitMap);
	}
conditionList =[];
conditionList.add(EntityCondition.makeCondition("mccTypeId", EntityOperator.IN , UtilMisc.toList("UNION","OTHERS")));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "SHED"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List shedsList = delegator.findList("Facility",condition,null,null,null,false);

shedIds= EntityUtil.getFieldListFromEntityList(shedsList, "facilityId", true);

List unionOthersConditionList = FastList.newInstance();
unionOthersConditionList.add(EntityCondition.makeCondition("parentFacilityId",EntityOperator.IN,shedIds));
unionOthersConditionList.add(EntityCondition.makeCondition("mccCode",EntityOperator.NOT_EQUAL,null));
EntityCondition unionOthersCondition = EntityCondition.makeCondition(unionOthersConditionList,EntityOperator.AND);
unionAndOthersDetailsList = delegator.findList("Facility",unionOthersCondition,null,null,null,false);

facilityIds= EntityUtil.getFieldListFromEntityList(unionAndOthersDetailsList, "facilityId", true);

Map privateDairiesMap = FastMap.newInstance();
Map productWiseTotalsMap = FastMap.newInstance();
//productWiseTotalsMap.putAll(initProductMap);
facilityIds.each{ facilityId ->
	
	conList =[];
	milkDetailslist =[];	
	conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));	
	conList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
	conList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
					EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)],EntityOperator.AND));
	EntityCondition cond = EntityCondition.makeCondition(conList,EntityOperator.AND);
	milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",cond,null,null,null,false);
	milkDetailslist = UtilMisc.sortMaps(milkDetailslist, UtilMisc.toList("receiveDate"));	
	Map productWisePrivateDairiesMap= FastMap.newInstance();
	if(UtilValidate.isNotEmpty(milkDetailslist)){
		milkDetailslist.each{ milkDetails->
			String productId = milkDetails.get("receivedProductId");
			qtyKgs=milkDetails.get("quantity");
			qtyLtrs=milkDetails.get("quantityLtrs")
			kgFat=milkDetails.get("sendKgFat");
			kgSnf=milkDetails.get("sendKgSnf");
			sendFat=milkDetails.get("fat");
			sendSnf=milkDetails.get("snf");
			recdKgs=milkDetails.get("receivedQuantity");
			recdLtrs=milkDetails.get("receivedQuantityLtrs");
			recKgFat=milkDetails.get("receivedKgFat");
			recKgSnf=milkDetails.get("receivedKgSnf");
			recdFat=milkDetails.get("receivedFat");
			recdSnf=milkDetails.get("receivedSnf");
			if(UtilValidate.isEmpty(productWiseTotalsMap[productId])){
				productWiseTotalsMap[productId]=recdLtrs;
			}else{
				productWiseTotalsMap[productId]+=recdLtrs;
			}
			
			
			Map priceCtx=UtilMisc.toMap("userLogin", userLogin);
			priceCtx.put("facilityId", facilityId);
			priceCtx.put("fatPercent", recdFat);
			priceCtx.put("snfPercent", recdSnf);
			priceCtx.put("productId",productId);
			priceCtx.put("priceDate",milkDetails.get("receiveDate"));
			Map priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
			BigDecimal milkValue = BigDecimal.ZERO;
			BigDecimal milkAmount = BigDecimal.ZERO;
			BigDecimal fatPremium = BigDecimal.ZERO;
			BigDecimal snfPremium = BigDecimal.ZERO;
			Map tempQtyMap=FastMap.newInstance();
			tempQtyMap.put("sendQtyLtrs", qtyLtrs);
			tempQtyMap.put("sendQtyKgs", qtyKgs);
			tempQtyMap.put("sendKgFat", kgFat);
			tempQtyMap.put("sendKgSnf", kgSnf);
			tempQtyMap.put("recdQtyLtrs", recdLtrs);
			tempQtyMap.put("recdQtyKgs", recdKgs);
			tempQtyMap.put("recdKgFat", recKgFat);
			tempQtyMap.put("recdKgSnf", recKgSnf);
			tempQtyMap.put("totSolids", (recKgFat+recKgSnf));
			tempQtyMap.put("milkValue", milkValue);
			
			String uomTypeStr = "KG.FAT";
			String reportTypeStr = "ACKNOWLEDGEMENT";
			String billQuantity = "ACK"
			if(UtilValidate.isNotEmpty(priceResult)){
				billQuantity = (String)priceResult.get("billQuantity");
				
			}	
			if(UtilValidate.isNotEmpty(billQuantity) && "DISP_QTY".equalsIgnoreCase(billQuantity) ){
					priceCtx.put("fatPercent", sendFat);
					priceCtx.put("snfPercent", sendSnf);
					priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
				}	
			if(UtilValidate.isNotEmpty(priceResult)){
				billQuantity = (String)priceResult.get("billQuantity");
				String uomId = (String)priceResult.get("uomId");
				BigDecimal defaultRate = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(priceResult.get("defaultRate"))){
					defaultRate = (BigDecimal)priceResult.get("defaultRate");
				}
				BigDecimal fatPremiumRate = BigDecimal.ZERO;
				fatPremiumRate = (BigDecimal)priceResult.get("fatPremium");
				
				BigDecimal snfPremiumRate = BigDecimal.ZERO;
				snfPremiumRate = (BigDecimal)priceResult.get("snfPremium");
				
				String useTotalSolids = "N";
				if(UtilValidate.isNotEmpty(uomId)){
					if("VLIQ_KG".equalsIgnoreCase(uomId)){
							uomTypeStr ="KG.   ";
						}
					if("VLIQ_L".equalsIgnoreCase(uomId)){
						uomTypeStr ="LTR.  ";
					}
				}
				if(UtilValidate.isNotEmpty(billQuantity)){
					if("DISP_QTY".equalsIgnoreCase(billQuantity)){
							reportTypeStr = "DISPATCH       ";
						}
				}
				
				if(UtilValidate.isNotEmpty(priceResult.get("useTotalSolids"))){
					useTotalSolids = priceResult.get("useTotalSolids");
					}
				
				BigDecimal billQty = BigDecimal.ZERO;
				billQty = (BigDecimal)milkDetails.get("receivedKgFat");
				if("Y".equalsIgnoreCase(useTotalSolids)){
					billQty.add((BigDecimal)milkDetails.get("receivedKgSnf"));
					}
				BigDecimal premQty =  BigDecimal.ZERO;
				premQty = (BigDecimal)milkDetails.get("receivedQuantity");
				
				if((uomTypeStr.trim()).equalsIgnoreCase("KG.")){
					billQty = (BigDecimal)milkDetails.get("receivedQuantity");
					}
				if((uomTypeStr.trim()).equalsIgnoreCase("LTR.")){
					billQty = (BigDecimal)milkDetails.get("receivedQuantityLtrs");
					}
				
				if((reportTypeStr.trim()).equalsIgnoreCase("DISPATCH")){
					billQty = (BigDecimal)milkDetails.get("sendKgFat");
					if("Y".equalsIgnoreCase(useTotalSolids)){
						billQty.add((BigDecimal)milkDetails.get("sendKgSnf"));
						}
					premQty = (BigDecimal)milkDetails.get("quantity");
					if((uomTypeStr.trim()).equalsIgnoreCase("KG.")){
						billQty = (BigDecimal)milkDetails.get("quantity");
							}
					if((uomTypeStr.trim()).equalsIgnoreCase("LTR.")){
						billQty = (BigDecimal)milkDetails.get("quantityLtrs");
						}
					
					}
				milkAmount = (billQty.multiply(defaultRate)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
				fatPremium = (premQty.multiply(fatPremiumRate)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
				snfPremium = (premQty.multiply(snfPremiumRate)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
			} 
			
			Map inputMap = UtilMisc.toMap("userLogin", userLogin);
			inputMap.put("facilityId", facilityId);
			inputMap.put("rateCurrencyUomId", "INR");
			inputMap.put("rateTypeId", "MLKRECPT_OPCOST");
			facilityRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputMap);
			opCost =  facilityRateAmount.get("rateAmount");
			opCostValue=0;
			if(UtilValidate.isNotEmpty(opCost)){
				opCostValue=opCost*recdLtrs;
			}
			tempQtyMap.put("opCost", opCostValue);
			milkValue = milkAmount+fatPremium+snfPremium;
			tempQtyMap.put("milkValue",milkValue);
			if(UtilValidate.isEmpty(productWisePrivateDairiesMap) || (UtilValidate.isNotEmpty(productWisePrivateDairiesMap) && UtilValidate.isEmpty(productWisePrivateDairiesMap.get(productId)))){
				productWisePrivateDairiesMap.put(productId,tempQtyMap);
			}else{
				Map tempProdQtyMap = FastMap.newInstance();
				tempProdQtyMap.putAll(productWisePrivateDairiesMap.get(productId));
				tempProdQtyMap.put("sendQtyLtrs", qtyLtrs+tempProdQtyMap.get("sendQtyLtrs"));
				tempProdQtyMap.put("sendQtyKgs", qtyKgs+tempProdQtyMap.get("sendQtyKgs"));
				tempProdQtyMap.put("sendKgFat", kgFat+tempProdQtyMap.get("sendKgFat"));
				tempProdQtyMap.put("sendKgSnf", kgSnf+tempProdQtyMap.get("sendKgSnf"));
				tempProdQtyMap.put("recdQtyLtrs", recdLtrs+tempProdQtyMap.get("recdQtyLtrs"));
				tempProdQtyMap.put("recdQtyKgs", recdKgs+tempProdQtyMap.get("recdQtyKgs"));
				tempProdQtyMap.put("recdKgFat", recKgFat+tempProdQtyMap.get("recdKgFat"));
				tempProdQtyMap.put("recdKgSnf", recKgSnf+tempProdQtyMap.get("recdKgSnf"));
				tempProdQtyMap.put("totSolids", (recKgFat+recKgSnf)+tempProdQtyMap.get("totSolids"));
				tempProdQtyMap.put("milkValue", milkValue+tempProdQtyMap.get("milkValue"));
				tempProdQtyMap.put("opCost", opCostValue+tempProdQtyMap.get("opCost"));
				BigDecimal avgFat = (ProcurementNetworkServices.calculateFatOrSnf(tempProdQtyMap.get("recdKgFat"), tempProdQtyMap.get("recdQtyKgs"))).setScale(1, BigDecimal.ROUND_HALF_UP);
				BigDecimal avgSnf = (ProcurementNetworkServices.calculateFatOrSnf(tempProdQtyMap.get("recdKgSnf"), tempProdQtyMap.get("recdQtyKgs"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				tempProdQtyMap.put("recdFat",avgFat);
				tempProdQtyMap.put("recdSnf", avgSnf);
				productWisePrivateDairiesMap.put(productId,tempProdQtyMap);
			}	
		}
			
		}//End  of the List 
		tempFinalMap = [:];
		tempFinalMap.putAll(productWisePrivateDairiesMap);
		privateDairiesMap.put(facilityId, tempFinalMap);
	}//end of MilkReceiptsList
context.putAt("privateDairiesMap", privateDairiesMap);
context.putAt("productWiseTotalsMap", productWiseTotalsMap);
