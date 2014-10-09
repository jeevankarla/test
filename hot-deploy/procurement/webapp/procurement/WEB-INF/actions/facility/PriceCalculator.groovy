import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.JdbcValueHandler.BigDecimalJdbcValueHandler;

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
import java.sql.Timestamp;
import java.math.RoundingMode;

import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
 
rateList = [];
fatPercentStr = parameters.fatPercent;
snfPercentStr = parameters.snfPercent;
milkKgsStr = parameters.milkKgs ;
BigDecimal fatPercent = null;
BigDecimal snfPercent = null;
BigDecimal lrValue = null;
BigDecimal milkKgs = BigDecimal.ONE;
dctx = dispatcher.getDispatchContext();
tenantId = delegator.getDelegatorTenantId();
context.tenantId =  tenantId.toUpperCase();
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.putAt("procurementProductList", procurementProductList);
if(UtilValidate.isNotEmpty(fatPercentStr)&&UtilValidate.isNotEmpty(snfPercentStr)){
	fatPercent = new BigDecimal(fatPercentStr).setScale(2,BigDecimal.ROUND_HALF_UP);
	snfPercent =new BigDecimal(snfPercentStr).setScale(2,BigDecimal.ROUND_HALF_UP);
 	milkKgs = new BigDecimal(milkKgsStr).setScale(2,BigDecimal.ROUND_HALF_UP);
	lrValue = ProcurementNetworkServices.convertFatSnfToLR(fatPercent,snfPercent);
}
productId  = parameters.productId;

facilityId = parameters.regionId;
categoryTypeEnum = null;
if(UtilValidate.isNotEmpty(facilityId)){
	GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId) ,true);
	if(UtilValidate.isNotEmpty(facility)){
		categoryTypeEnum = facility.getString("categoryTypeEnum");
	}
}
def sdf = new SimpleDateFormat("yyyy-MM-dd");
Timestamp priceDate = UtilDateTime.nowTimestamp();
if(UtilValidate.isNotEmpty(parameters.get("priceDate"))){
	 parseDate = parameters.get("priceDate");
	try {
		priceDate = new java.sql.Timestamp(sdf.parse(parseDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+priceDate, "");
   
	}
}

rateMap = [:];
rateList.clear();
if(UtilValidate.isNotEmpty(fatPercent)&&UtilValidate.isNotEmpty(snfPercent)&&UtilValidate.isNotEmpty(facilityId)){
	inMap = [:];
	inMap.put("userLogin",context.userLogin);
	inMap.put("productId", productId);
	inMap.put("facilityId", facilityId);
	inMap.put("fatPercent", fatPercent);
	inMap.put("snfPercent", snfPercent);
	inMap.put("priceDate", priceDate);
	
	inMap.put("supplyTypeEnumId",parameters.supplyTypeEnumId);
	inMap.put("categoryTypeEnum",categoryTypeEnum);
	rateMap = dispatcher.runSync("calculateProcurementProductPrice",inMap);
	if (ServiceUtil.isError(rateMap)) {
		context.errorMessage = "No valid price chart found";
		context.put("rateMap", rateMap);
		return ;
	}
	BigDecimal lts = ProcurementNetworkServices.convertKGToLitre(milkKgs);
	kgFat = (milkKgs*fatPercent/100).setScale(2,BigDecimal.ROUND_HALF_UP);
	kgSnf = new BigDecimal(milkKgs*snfPercent/100).setScale(2,BigDecimal.ROUND_HALF_UP);

	rateMap.putAt("price",rateMap.get("price"));
	rateMap.putAt("kgFat",kgFat);
	rateMap.putAt("kgSnf",kgSnf);
	rateMap.putAt("productId", productId);
	rateMap.putAt("fatPercent", fatPercent);
	rateMap.putAt("snfPercent", snfPercent);
	rateMap.putAt("kgs",milkKgs);
	rateMap.putAt("lts",lts);
	rateMap.putAt("lrValue", lrValue);
	rateMap.putAt("facilityId", facilityId);
	uomId = rateMap.uomId;
	rateMap.putAt("gross",((rateMap.get("price"))*milkKgs)-(rateMap.get("premium")*milkKgs));
	rateMap.putAt("net",((rateMap.get("price"))*milkKgs));
	if(UtilValidate.isNotEmpty(uomId)&&(uomId.equalsIgnoreCase("VLIQ_L"))){
		rateMap.putAt("gross",((rateMap.get("price"))*lts)-(rateMap.get("premium")*milkKgs));
		rateMap.putAt("net",((rateMap.get("price"))*lts));
	}
	rateMap.putAt("tipDed", BigDecimal.ZERO);
	if(UtilValidate.isEmpty(kgFat)){
			rateMap.putAt("solids",BigDecimal.ZERO);
		}else if(UtilValidate.isEmpty(kgSnf)){
			rateMap.putAt("solids",BigDecimal.ZERO);
		}else{
			rateMap.putAt("solids",kgFat+kgSnf );
		}
	
	}
	if(UtilValidate.isNotEmpty(rateMap.get("price")) && rateMap.get("price")!=0){
		inputRateAmt=[:];
		inputRateAmt.put("userLogin", userLogin);
		inputRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
		inputRateAmt.put("rateCurrencyUomId", "INR");
		inputRateAmt.put("productId", productId);
		inputRateAmt.put("facilityId",facilityId);
		rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
		rateMap.putAt("tipDed", (rateAmount.rateAmount)*rateMap.get("kgFat"));
			
	}
	context.put("rateMap",rateMap);
	rateList.add(rateMap);
	context.put("rateList", rateList);
