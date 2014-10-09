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
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
facilityId = parameters.shedId;
facility = delegator.findOne("Facility",[facilityId:facilityId],false);
context.putAt("facility", facility);

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
List productsList = FastList.newInstance();
productsList = ProcurementNetworkServices.getProcurementProducts(dctx,UtilMisc.toMap());
context.put("productsList",productsList);
shedPeriodTotals = ProcurementReports.getPeriodTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: parameters.shedId]);
shedTotals = shedPeriodTotals.get(parameters.shedId).get("dayTotals").get("TOT").get("TOT");
Map tempMap = FastMap.newInstance();
Map totalsMap = FastMap.newInstance();

tempMap.put("QTY IN KGS",BigDecimal.ZERO);
tempMap.put("QTY KGFAT",BigDecimal.ZERO);
tempMap.put("QTY KGSNF",BigDecimal.ZERO);
tempMap.put("QTY KG SOLIDS",BigDecimal.ZERO);
tempMap.put("BULK FAT(%)",BigDecimal.ZERO);
tempMap.put("BULK SNF(%)",BigDecimal.ZERO);
tempMap.put("RATE",BigDecimal.ZERO);
tempMap.put("MILK VALUE",BigDecimal.ZERO);
tempMap.put("SNF DED RATE",BigDecimal.ZERO);
tempMap.put("SNF DED VALUE",BigDecimal.ZERO);
tempMap.put("NET MILK VALUE",BigDecimal.ZERO);
tempMap.put("AS PER BILL",BigDecimal.ZERO);
tempMap.put("DIFFERENCE BILL",BigDecimal.ZERO);
context.putAt("keyMap",tempMap);
Map totMap = FastMap.newInstance();
Map requisitionTotMap = FastMap.newInstance();
totalsMap.putAll(tempMap);
//For Requisition Map 
Map reqTempMap =FastMap.newInstance();
reqTempMap.putAll(tempMap);
reqTempMap.put("QTY IN LTS", BigDecimal.ZERO);
reqTempMap.put("sQtyKgs", BigDecimal.ZERO);
reqTempMap.put("sFat", BigDecimal.ZERO);
reqTempMap.put("sQtyKgFat", BigDecimal.ZERO);
reqTempMap.put("sQtyLtrs", BigDecimal.ZERO);
reqTempMap.put("cQtyLtrs", BigDecimal.ZERO);
reqTempMap.put("sourPtcQtyKgs", BigDecimal.ZERO);
reqTempMap.put("sourPtcQtyLtrs", BigDecimal.ZERO);
reqTempMap.put("curdPtcQtyKgs", BigDecimal.ZERO);
reqTempMap.put("curdPtcQtyLtrs", BigDecimal.ZERO);

for(product in productsList){
	String name = product.brandName; 
	totMap.put(product.brandName,tempMap );	
	requisitionTotMap.put(product.brandName,reqTempMap);	
}
totalsMap.put("QTY IN KGS",((shedTotals.get("TOT")).get("qtyKgs"))+((shedTotals.get("TOT")).get("sQtyKgs")));
totalsMap.put("QTY KGFAT",((shedTotals.get("TOT")).get("kgFat")));
totalsMap.put("QTY KGSNF",(shedTotals.get("TOT")).get("kgSnf"));
totalsMap.put("QTY KG SOLIDS",(shedTotals.get("TOT")).get("kgSnf")+(shedTotals.get("TOT")).get("kgFat"));
totalsMap.put("BULK FAT(%)",((shedTotals.get("TOT")).get("fat")).setScale(1,BigDecimal.ROUND_HALF_UP));
totalsMap.put("BULK SNF(%)",(shedTotals.get("TOT")).get("snf").setScale(2,BigDecimal.ROUND_HALF_UP));
totalsMap.put("RATE",BigDecimal.ZERO);
totalsMap.put("SNF DED RATE",BigDecimal.ZERO);

for(product in productsList){
	Map tempProductMap = FastMap.newInstance();
	Map reqProductMap = FastMap.newInstance();
	
	String productName = product.productName;
	String brandName = product.brandName;
	BigDecimal fat = ((shedTotals.get(productName)).get("fat"));
	BigDecimal snf = (shedTotals.get(productName)).get("snf");
	BigDecimal kgFat = (shedTotals.get(productName)).get("kgFat");
	BigDecimal kgSnf = (shedTotals.get(productName)).get("kgSnf");
	BigDecimal sQtyLtrs=(shedTotals.get(productName)).get("sQtyLtrs");
	BigDecimal sQtykgs=(shedTotals.get(productName)).get("sQtyKgs");
	BigDecimal cQtyLtrs=(shedTotals.get(productName)).get("cQtyLtrs");
	BigDecimal ptcQtyKgs=(shedTotals.get(productName)).get("ptcQtyKgs");
	BigDecimal ptcQtyLtrs=(shedTotals.get(productName)).get("ptcQtyLtrs");	
	BigDecimal qtyLtrs = (shedTotals.get(productName)).get("qtyLtrs");
	BigDecimal qtyKgs = ((shedTotals.get(productName)).get("qtyKgs"))+((shedTotals.get(productName)).get("sQtyKgs"));
	BigDecimal billValue = ((shedTotals.get(productName)).get("price"))+((shedTotals.get(productName)).get("sPrice"));
	billValue = billValue.setScale(0, BigDecimal.ROUND_HALF_UP);
	
	fat = fat.setScale(1,BigDecimal.ROUND_HALF_UP);
	snf = snf.setScale(2,BigDecimal.ROUND_HALF_UP);
	rateMap = PriceServices.getProcurementProductPrice(dctx,[userLogin:userLogin,priceDate:thruDate,facilityId:parameters.shedId,productId:product.productId,fatPercent:fat,snfPercent:snf]);
	BigDecimal defaultRate = rateMap.defaultRate;
	String useTotalSolids = "";
	if(UtilValidate.isNotEmpty((rateMap.useTotalSolids))){
			useTotalSolids = rateMap.useTotalSolids;
		}

	Map priceChartDetails = rateMap.priceChartMap;
	String priceChartId = null;
	if(UtilValidate.isNotEmpty(priceChartDetails)){
			priceChartId = priceChartDetails.procPriceChartId;
	}
	
	BigDecimal milkValue = BigDecimal.ZERO;
	BigDecimal tototalSolids = kgFat+kgSnf;
	if(UtilValidate.isNotEmpty(useTotalSolids) &&("Y".equals(useTotalSolids.toUpperCase()))){
		BigDecimal maxSnfValue = 8.5;
		/*if(UtilValidate.isNotEmpty(priceChartId)){
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("procPriceChartId",EntityOperator.EQUALS,priceChartId));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,product.productId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List procurementPrices =delegator.findList("ProcurementPrice",condition,["snfPercent"]as Set,null,null,false);
			List<BigDecimal> snfList = FastList.newInstance();
			for(procPrice in procurementPrices){
					snfList.add(procPrice.snfPercent);
				}
			maxSnfValue = Collections.max(snfList);
		}*/ 
		BigDecimal fatPercent = fat+maxSnfValue;
		milkValue =(fatPercent*defaultRate*qtyKgs)/100;
	
	}else{
		milkValue = defaultRate*kgFat;
	}
	milkValue = milkValue.setScale(2, BigDecimal.ROUND_HALF_UP);
	BigDecimal snfDedRate = rateMap.premium;
	BigDecimal snfDedValue = snfDedRate*qtyKgs;
	tempProductMap.put("QTY IN KGS",qtyKgs);
	tempProductMap.put("QTY KGFAT",kgFat);
	tempProductMap.put("QTY KGSNF",kgSnf);
	tempProductMap.put("QTY KG SOLIDS",kgFat+kgSnf);
	tempProductMap.put("BULK FAT(%)",fat);
	tempProductMap.put("BULK SNF(%)",snf);
	tempProductMap.put("RATE",defaultRate);
	tempProductMap.put("MILK VALUE",milkValue);
	tempProductMap.put("SNF DED RATE",snfDedRate);
	tempProductMap.put("SNF DED VALUE",snfDedValue);
	tempProductMap.put("NET MILK VALUE",milkValue+snfDedValue);
	tempProductMap.put("AS PER BILL",billValue);
	tempProductMap.put("DIFFERENCE BILL",(milkValue+snfDedValue-billValue)*-1);
	
	//for requisition map
	reqProductMap.putAll(tempProductMap);	
	reqProductMap.put("QTY IN LTS", qtyLtrs);
	tempProductMap.put("QTY IN KGS",(shedTotals.get(productName)).get("qtyKgs"));
	reqProductMap.put("sQtyKgs", sQtykgs);
	reqProductMap.put("sFat", (shedTotals.get(productName)).get("sFat"));
	reqProductMap.put("sQtyKgFat", (shedTotals.get(productName)).get("sKgFat"));
	reqProductMap.put("sQtyLtrs", sQtyLtrs);
	reqProductMap.put("cQtyLtrs", cQtyLtrs);
	reqProductMap.put("cQtykgs",ProcurementNetworkServices.convertLitresToKG(new BigDecimal(cQtyLtrs)));
	reqProductMap.put("sourPtcQtyKgs", BigDecimal.ZERO);
	reqProductMap.put("sourPtcQtyLtrs",  BigDecimal.ZERO);
	reqProductMap.put("curdPtcQtyKgs",BigDecimal.ZERO);
	reqProductMap.put("curdPtcQtyLtrs", BigDecimal.ZERO);
	if(UtilValidate.isNotEmpty((shedTotals.get(productName)).get("ptcMilkType"))){
		ptcMlkType=(shedTotals.get(productName)).get("ptcMilkType");
		if(ptcMlkType.equals("S")){
			reqProductMap.put("sourPtcQtyKgs", ptcQtyKgs);
			reqProductMap.put("sourPtcQtyLtrs", ptcQtyLtrs);
		}else if(ptcMlkType.equals("C")){
			reqProductMap.put("curdPtcQtyKgs",ptcQtyKgs);
			reqProductMap.put("curdPtcQtyLtrs", ptcQtyLtrs);			
		}
	}
	totalsMap.put("MILK VALUE",totalsMap.get("MILK VALUE")+milkValue);
	totalsMap.put("SNF DED VALUE",totalsMap.get("SNF DED VALUE")+snfDedValue);
	totalsMap.put("NET MILK VALUE",totalsMap.get("NET MILK VALUE")+milkValue+snfDedValue);
	totalsMap.put("AS PER BILL",totalsMap.get("AS PER BILL")+billValue);
	totalsMap.put("DIFFERENCE BILL",totalsMap.get("DIFFERENCE BILL")+(milkValue+snfDedValue-billValue)*-1);
	
	totMap.put(product.brandName,tempProductMap);
	requisitionTotMap.put(product.brandName, reqProductMap);
 }
totMap.put("TOT", totalsMap);
context.putAt("totMap", totMap);
context.putAt("requisitionTotMap", requisitionTotMap);





