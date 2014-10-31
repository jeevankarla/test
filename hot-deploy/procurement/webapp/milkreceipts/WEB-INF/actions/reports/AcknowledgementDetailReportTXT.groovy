import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;


result = ServiceUtil.returnSuccess();
if(UtilValidate.isEmpty(parameters.shedId)){
	Debug.logError("Shed Cannot Be Empty","");
	/*context.errorMessage = "No Shed Has Been Selected.......!";
	return;*/
}
if(UtilValidate.isEmpty(parameters.unitId)){
	Debug.logError("Unit Cannot Be Empty","");
	context.errorMessage = "No Unit Has Been Selected.......!";
	return;
}
dctx = dispatcher.getDispatchContext();
context.putAt("dctx", dctx);

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


conditionList =[];
milkDetailslist =[];
unitId = parameters.unitId;
productId = parameters.productId;
result.put("productId", productId);
result.put("unitId", unitId);
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , unitId));
conditionList.add(EntityCondition.makeCondition("receivedProductId", EntityOperator.EQUALS ,productId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart), 
				EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd)],EntityOperator.AND));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,null,null,false);
milkDetailslist = UtilMisc.sortMaps(milkDetailslist, UtilMisc.toList("receiveDate"));
context.put("milkDetailslist",milkDetailslist);

Map inputMap = UtilMisc.toMap("userLogin", userLogin);
inputMap.put("facilityId", unitId);
inputMap.put("rateCurrencyUomId", "INR");
inputMap.put("rateTypeId", "MLKRECPT_OPCOST");
facilityRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputMap);
opCost =  facilityRateAmount.get("rateAmount");
context.putAt("opCost", opCost);
result.put("opCost", opCost);
inputMap.remove("rateCurrencyUomId");
inputMap.remove("rateTypeId");
inputMap.put("productId",productId );
inputMap.put("fatPercent",BigDecimal.ZERO );
inputMap.put("snfPercent",BigDecimal.ZERO);
inputMap.put("priceDate",fromDateStart);


Map procurementPriceDetails = PriceServices.getProcurementProductPrice(dctx, inputMap);

String reportTypeStr = "ACKNOWLEDGEMENT";
String uomTypeStr = "KG.FAT";
 if(UtilValidate.isNotEmpty(procurementPriceDetails)){
	 		String uomId = (String)procurementPriceDetails.get("uomId");
			String billQuantity = (String)procurementPriceDetails.get("billQuantity");
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
	 }
 context.put("reportTypeStr",reportTypeStr);
 context.put("uomTypeStr",uomTypeStr);
 
 result.put("reportTypeStr",reportTypeStr);
 result.put("uomTypeStr",uomTypeStr);
 


 List finalBillingList = FastList.newInstance();
 for(milkdetails in milkDetailslist){
	 	Map milkDetMap = FastMap.newInstance();
		milkDetMap.putAll(milkdetails);
		inputMap.put("fatPercent",(BigDecimal)milkdetails.get("receivedFat") );
			inputMap.put("snfPercent",(BigDecimal)milkdetails.get("receivedSnf"));
			inputMap.put("priceDate",milkdetails.get("receiveDate"));
		
		if((reportTypeStr.trim()).equalsIgnoreCase("DISPATCH")){
			inputMap.put("fatPercent",(BigDecimal)milkdetails.get("fat") );
			inputMap.put("snfPercent",(BigDecimal)milkdetails.get("snf"));
			}
		Map priceResult = FastMap.newInstance();
		priceResult = PriceServices.getProcurementProductPrice(dctx, inputMap);
		if(UtilValidate.isNotEmpty(priceResult)){
			BigDecimal defaultRate = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(priceResult.get("defaultRate"))){
				defaultRate = (BigDecimal)priceResult.get("defaultRate");
				}
			String uomId = (String)priceResult.get("uomId");
			if(UtilValidate.isNotEmpty(uomId) && ("VLIQ_L".equalsIgnoreCase(uomId))){
				uomTypeStr ="LTR.  ";
				context.putAt("uomTypeStr", uomTypeStr);
				result.putAt("uomTypeStr", uomTypeStr);
				}
			reportTypeStr = "ACKNOWLEDGEMENT";
			String billQuantity = (String)priceResult.get("billQuantity");
			if(UtilValidate.isNotEmpty(billQuantity) && ("DISP_QTY".equalsIgnoreCase(billQuantity))){
				reportTypeStr = "DISPATCH       ";
				}
			
			context.putAt("reportTypeStr", reportTypeStr);
			BigDecimal price = priceResult.get("price");
			BigDecimal fatPremiumRate = BigDecimal.ZERO;
			fatPremiumRate = (BigDecimal)priceResult.get("fatPremium");
			
			BigDecimal snfPremiumRate = BigDecimal.ZERO;
			snfPremiumRate = (BigDecimal)priceResult.get("snfPremium");
			
			String useTotalSolids = "N";
			if(UtilValidate.isNotEmpty(priceResult.get("useTotalSolids"))){
				useTotalSolids = priceResult.get("useTotalSolids");
				}
			
			
			BigDecimal milkAmount = BigDecimal.ZERO;
			BigDecimal fatPremium = BigDecimal.ZERO;
			BigDecimal snfPremium = BigDecimal.ZERO;
			
			BigDecimal billQty = BigDecimal.ZERO;
			billQty = (BigDecimal)milkdetails.get("receivedKgFat");
			if("Y".equalsIgnoreCase(useTotalSolids)){
				billQty.add((BigDecimal)milkdetails.get("receivedKgSnf"));
				}
			BigDecimal premQty =  BigDecimal.ZERO;
			premQty = (BigDecimal)milkdetails.get("receivedQuantity");
			if((uomTypeStr.trim()).equalsIgnoreCase("KG.")){
				billQty = (BigDecimal)milkdetails.get("receivedQuantity");
				}
			if((uomTypeStr.trim()).equalsIgnoreCase("LTR.")){
				billQty = (BigDecimal)milkdetails.get("receivedQuantityLtrs");
				}
			
			if((reportTypeStr.trim()).equalsIgnoreCase("DISPATCH")){
				billQty = (BigDecimal)milkdetails.get("sendKgFat");
				if("Y".equalsIgnoreCase(useTotalSolids)){
					billQty.add((BigDecimal)milkdetails.get("sendKgSnf"));
					}
				premQty = (BigDecimal)milkdetails.get("quantity");
				if((uomTypeStr.trim()).equalsIgnoreCase("KG.")){
					billQty = (BigDecimal)milkdetails.get("quantity");
						}
				if((uomTypeStr.trim()).equalsIgnoreCase("LTR.")){
					billQty = (BigDecimal)milkdetails.get("quantityLtrs");
						}
				
				}
			
			milkAmount = (billQty.multiply(defaultRate)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
			fatPremium = (premQty.multiply(fatPremiumRate)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
			snfPremium = (premQty.multiply(snfPremiumRate)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
			milkDetMap.put("defaultRate",priceResult.get("defaultRate"));
			milkDetMap.put("milkAmount",milkAmount);
			milkDetMap.put("fatPremium",fatPremium);
			milkDetMap.put("snfPremium",snfPremium);
			
			
			finalBillingList.add(milkDetMap);
			} 
		 
	 }
 context.putAt("finalBillingList", finalBillingList);
 result.putAt("finalBillingList", finalBillingList);
 return result;
 
 

