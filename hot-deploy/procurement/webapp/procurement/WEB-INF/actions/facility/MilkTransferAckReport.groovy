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
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
  	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

fromDateStart = UtilDateTime.getDayStart(fromDate);
thruDateEnd = UtilDateTime.getDayEnd(thruDate);
context.put("fromDateStart",fromDateStart);
context.put("thruDateEnd",thruDateEnd);

String facilityId = parameters.facilityId;
String facilityIdTo = parameters.facilityIdTo;
String customTimePeriodId = parameters.customTimePeriodId;

List milkList = [];
milkList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
milkList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityIdTo));
milkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart));
milkList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
milkList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MXF_RECD"));
milkTransfersCondition = EntityCondition.makeCondition(milkList,EntityOperator.AND);
milkTransfersList = delegator.findList("MilkTransfer",milkTransfersCondition,null,null,null,false);

int totalDays =UtilDateTime.getIntervalInDays(fromDateStart, thruDateEnd);
totalDays=totalDays+1;
List dayWiseList =FastList.newInstance();

totalsMap = [:];
totalsMap["date"]="TOTAL";
totalsMap["kgFat"] = 0;
totalsMap["kgSnf"] = 0;
totalsMap["qtyKgs"] = 0;
totalsMap["qtyLtrs"] = 0;
totalsMap["fat"] = 0;
totalsMap["snf"] = 0;
	   	   
for(int i=0; i <totalDays; i++){
   List currentMilkTransferList = FastList.newInstance();
   currentDayTimeStart = UtilDateTime.getDayStart(fromDateStart, i);
   currentDayTimeEnd = UtilDateTime.getDayEnd(currentDayTimeStart);
   currentMilkTransferList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, currentDayTimeStart));
   currentMilkTransferList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, currentDayTimeEnd));
   currentMilkTransferCondition = EntityCondition.makeCondition(currentMilkTransferList,EntityOperator.AND);
   currentMilkTransfersDateList = EntityUtil.filterByCondition(milkTransfersList,currentMilkTransferCondition);

   Map tranKgFatSnfMap = FastMap.newInstance();
   tranKgFatSnfMap.put("kgFat",BigDecimal.ZERO);
   tranKgFatSnfMap.put("kgSnf",BigDecimal.ZERO);
   tranKgFatSnfMap.put("qtyKgs",BigDecimal.ZERO);
   tranKgFatSnfMap.put("qtyLtrs",BigDecimal.ZERO);
   tranKgFatSnfMap.put("snf",BigDecimal.ZERO);
   tranKgFatSnfMap.put("fat",BigDecimal.ZERO);
    for(transfer in currentMilkTransfersDateList){
	   if(UtilValidate.isNotEmpty(transfer.receivedQuantity)){
		   tranKgFatSnfMap.put("qtyKgs",tranKgFatSnfMap.get("qtyKgs")+(transfer.receivedQuantity));
	   }
	   if(UtilValidate.isNotEmpty(transfer.receivedKgFat)){
		   tranKgFatSnfMap.put("kgFat",tranKgFatSnfMap.get("kgFat")+( transfer.receivedKgFat));
		   }else{
		   BigDecimal kgFat = BigDecimal.ZERO;
		   kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.qtyKgs, transfer.receivedFat);
		   tranKgFatSnfMap.put("kgFat",tranKgFatSnfMap.get("kgFat")+kgFat);
		   }
	   if(UtilValidate.isNotEmpty(transfer.receivedKgSnf)){
		   tranKgFatSnfMap.put("kgSnf",tranKgFatSnfMap.get("kgSnf")+( transfer.receivedKgSnf));
		   }else{
		   BigDecimal kgSnf = BigDecimal.ZERO;
		   kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.qtyKgs, transfer.receivedSnf);
		   tranKgFatSnfMap.put("kgSnf",tranKgFatSnfMap.get("kgSnf")+kgSnf);
		   }
		   tranKgFatSnfMap.put("fat", transfer.receivedFat);
		   tranKgFatSnfMap.put("snf", transfer.receivedSnf);
		   tranKgFatSnfMap.put("vehicleNo", transfer.containerId);
		   tranKgFatSnfMap["date"]=UtilDateTime.toDateString(currentDayTimeStart,"dd-MM-yyyy");
		   qtyLtrs = 0;
	   if(UtilValidate.isNotEmpty(transfer.receivedQuantityLtrs)){
		   tranKgFatSnfMap.put("qtyLtrs",tranKgFatSnfMap.get("qtyLtrs")+(transfer.receivedQuantityLtrs));
	   }else{
			 tranKgFatSnfMap.put("qtyLtrs",tranKgFatSnfMap.get("qtyLtrs")+ProcurementNetworkServices.convertKGToLitreSetScale(transfer.receivedQuantity, true));
	   }
		   qtyLtrs=tranKgFatSnfMap.get("qtyLtrs");
		   if(qtyLtrs>0){
			   Map dayWiseTotals = FastMap.newInstance();
			   dayWiseTotals.putAll(tranKgFatSnfMap);
			   dayWiseTotals["currentdate"]= currentDayTimeStart;
			   dayWiseList.add(dayWiseTotals);
		   }
     }
			for(key in totalsMap.keySet()){
			if(!"date".equalsIgnoreCase(key)){
				totalsMap[key] = totalsMap.get(key)+tranKgFatSnfMap.get(key);
				}
			}
}
dayWiseList.add(totalsMap);
context.put("dayWiseList",dayWiseList);


List outputDayWiseList =FastList.newInstance();

List outputEntryList = [];
outputEntryList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
outputEntryList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
outputEntryList.add(EntityCondition.makeCondition("outputTypeId", EntityOperator.EQUALS, "CLOSING_BALANCE"));
outputEntryCondition = EntityCondition.makeCondition(outputEntryList,EntityOperator.AND);
outputEntryConditionList = delegator.findList("ProcFacilityOutput",outputEntryCondition,null,null,null,false);

outputTotalsMap = [:];
outputTotalsMap["outputDate"]="OPTOTAL";
outputTotalsMap["kgFat"] = 0;
outputTotalsMap["kgSnf"] = 0;
outputTotalsMap["qty"] = 0;
outputTotalsMap["quantityLtrs"] = 0;


Map outputTranKgFatSnfMap = FastMap.newInstance();
outputTranKgFatSnfMap.put("kgFat",BigDecimal.ZERO);
outputTranKgFatSnfMap.put("kgSnf",BigDecimal.ZERO);
outputTranKgFatSnfMap.put("qty",BigDecimal.ZERO);
outputTranKgFatSnfMap.put("quantityLtrs",BigDecimal.ZERO);
for(outputTransfer in outputEntryConditionList){
	if(UtilValidate.isNotEmpty(outputTransfer.qty)){
		outputTranKgFatSnfMap.put("qty",outputTranKgFatSnfMap.get("qty")+(outputTransfer.qty));
	}
	if(UtilValidate.isNotEmpty(outputTransfer.kgFat)){
		outputTranKgFatSnfMap.put("kgFat",outputTranKgFatSnfMap.get("kgFat")+( outputTransfer.kgFat));
	}
	if(UtilValidate.isNotEmpty(outputTransfer.kgSnf)){
		outputTranKgFatSnfMap.put("kgSnf",outputTranKgFatSnfMap.get("kgSnf")+( outputTransfer.kgSnf));
	}
	if(UtilValidate.isNotEmpty(outputTransfer.kgFat)){
		outputTranKgFatSnfMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(outputTranKgFatSnfMap.get("kgFat"),outputTranKgFatSnfMap.get("qty")));
		outputTranKgFatSnfMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(outputTranKgFatSnfMap.get("kgSnf"),outputTranKgFatSnfMap.get("qty")));
	}
	quantityLtrs = 0;
	if(UtilValidate.isNotEmpty(outputTransfer.quantityLtrs)){
		outputTranKgFatSnfMap.put("quantityLtrs",outputTranKgFatSnfMap.get("quantityLtrs")+(outputTransfer.quantityLtrs));
	}else{
		outputTranKgFatSnfMap.put("quantityLtrs",outputTranKgFatSnfMap.get("quantityLtrs")+ProcurementNetworkServices.convertKGToLitreSetScale(outputTransfer.qty, true));
		 }
	quantityLtrs=outputTranKgFatSnfMap.get("quantityLtrs");
	if(quantityLtrs>0){
		Map outputDayWiseTotals = FastMap.newInstance();
		outputDayWiseTotals.putAll(outputTranKgFatSnfMap);
		outputDayWiseList.add(outputDayWiseTotals);
	}
		
}	
		for(key in outputTotalsMap.keySet()){
			if(!"outputDate".equalsIgnoreCase(key)){
			outputTotalsMap[key] = outputTotalsMap.get(key)+outputTranKgFatSnfMap.get(key);
			}
		}
		outputTotalsMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(outputTotalsMap.get("kgFat"),outputTotalsMap.get("qty")));
		outputTotalsMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(outputTotalsMap.get("kgSnf"),outputTotalsMap.get("qty")));
outputDayWiseList.add(outputTotalsMap);
context.put("outputDayWiseList",outputDayWiseList);

totQty =0;
totKgFat=0;
totKgSnf=0;
totQtyLtrs=0;
totFat = 0;
totSnf = 0;
openingBal = ProcurementReports.getOpeningBalance(dctx,[facilityId:facilityId,customTimePeriodId:customTimePeriodId,periodTypeId:"PROC_BILL_MONTH"]);
if(UtilValidate.isNotEmpty(openingBal)){
	totQty = totQty+openingBal.get("openingBalance").get("qtyKgs");
	totKgFat = totKgFat+openingBal.get("openingBalance").get("kgFat");
	totKgSnf = totKgSnf+openingBal.get("openingBalance").get("kgSnf");
	totQtyLtrs = totQtyLtrs+openingBal.get("openingBalance").get("qtyLtrs");
	totFat = totFat+openingBal.get("openingBalance").get("fat");
	totSnf = totSnf+openingBal.get("openingBalance").get("snf");
}
context.put("totQty",totQty);
context.put("totKgFat",totKgFat);
context.put("totKgSnf",totKgSnf);
context.put("totQtyLtrs",totQtyLtrs);
context.put("totFat",totFat);
context.put("totSnf",totSnf);


unitPeriodTransferTotals = dispatcher.runSync("getPeriodTransferTotals" , [fromDate: fromDateStart , thruDate: thruDateEnd , facilityId: facilityId,userLogin:userLogin]);
if(UtilValidate.isNotEmpty(unitPeriodTransferTotals)){
	shortages = unitPeriodTransferTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("shortages");
	procPeriodTotals = unitPeriodTransferTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("procurementPeriodTotals").get("dayTotals").get("TOT");
	grossAmount = unitPeriodTransferTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("amounts").get("grossAmt");
}
context.put("grossAmount",grossAmount);
shortageMap = [:];
if(UtilValidate.isNotEmpty(shortages)){
	shortageMap.put("shortageQtyLtrs", shortages.qtyLtrs);
	shortageMap.put("shortageQtyKgs", shortages.qtyKgs);
	shortageMap.put("shortageKgFatAmt", shortages.kgFatAmt);
	shortageMap.put("shortageKgSnfAmt", shortages.kgSnfAmt);
}
context.put("shortageMap",shortageMap);
procMilkBillMap = [:];
if(UtilValidate.isNotEmpty(procPeriodTotals)){
	procMilkBillMap.put("milkBillQtyLtrs", procPeriodTotals.qtyLtrs);
	procMilkBillMap.put("milkBillQtyKgs", procPeriodTotals.qtyKgs);
	procMilkBillMap.put("milkBillKgFat", procPeriodTotals.kgFat);
	procMilkBillMap.put("milkBillKgSnf", procPeriodTotals.kgSnf);
}
context.put("procMilkBillMap",procMilkBillMap);
fatSnfMap = [:];
if(UtilValidate.isNotEmpty(grossAmount) && UtilValidate.isNotEmpty(procPeriodTotals)){
	fat55 = ((grossAmount*55)/100);
	kgFatRate = (fat55/(procPeriodTotals.kgFat));
	snf45 = ((grossAmount*45)/100);
	kgSnfRate = (fat55/(procPeriodTotals.kgSnf));
	fatSnfMap.put("fat55", fat55);
	fatSnfMap.put("kgFatRate", kgFatRate);
	fatSnfMap.put("snf45", snf45);
	fatSnfMap.put("kgSnfRate", kgSnfRate);
}
context.put("fatSnfMap",fatSnfMap);
