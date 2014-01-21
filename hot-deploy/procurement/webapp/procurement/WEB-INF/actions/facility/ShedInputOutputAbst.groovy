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
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList =[];

unitTotalsMap =[:];
conditionList.clear();
if(parameters.shedId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parameters.shedId)));
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);
unitIds =EntityUtil.getFieldListFromEntityList(unitsList, "facilityId", true);

//getting IUT Totals
conditionList.clear();
periodStart =UtilDateTime.getDayStart(fromDate);
periodEnd =UtilDateTime.getDayEnd(thruDate);
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodStart)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, periodEnd)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.NOT_IN, unitIds)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityIdTo", EntityOperator.IN ,unitIds)));
conditionValue = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkTransfers = delegator.findList("MilkTransfer", conditionValue, null, null, null, false);

IutTotalsMap =[:];
totIutQty=0;
totIutKgFat=0;
totIutKgSnf=0;
if(UtilValidate.isNotEmpty(milkTransfers)){
	milkTransfers.each{ milkTrns ->			
		totIutQty += milkTrns.receivedQuantity;
		totIutKgFat += ProcurementNetworkServices.calculateKgFatOrKgSnf(milkTrns.receivedQuantity,milkTrns.receivedFat);
		totIutKgSnf += ProcurementNetworkServices.calculateKgFatOrKgSnf(milkTrns.receivedQuantity,milkTrns.receivedSnf);		
	}	
}
IutTotalsMap["iutQty"]=totIutQty;
IutTotalsMap["iutKgFat"]=totIutKgFat;
IutTotalsMap["iutKgSnf"]=totIutKgSnf;
context.IutTotalsMap=IutTotalsMap;
//shedUnites = ProcurementNetworkServices.getShedAgents(dctx,UtilMisc.toMap("shedId", parameters.shedId));
totQty =0;
totKgFat=0;
totKgSnf=0;
totQtyLtrs=0;
tmObQty =0;
tmObKgFat=0;
tmObKgSnf=0;
tmObQtyLtrs=0;
shedWiseTotMap =[:];
openingBalMap =[:];
TMPreparationOB =[:];
unitsList.each{ unit ->
	openingBal = ProcurementReports.getOpeningBalance(dctx,[facilityId:unit.facilityId,customTimePeriodId:parameters.customTimePeriodId,userLogin:userLogin,periodTypeId:"PROC_BILL_MONTH"]);
	if(UtilValidate.isNotEmpty(openingBal)){
		totQty = totQty+openingBal.get("openingBalance").get("qtyKgs");
		totKgFat = totKgFat+openingBal.get("openingBalance").get("kgFat");
		totKgSnf = totKgSnf+openingBal.get("openingBalance").get("kgSnf");
		totQtyLtrs = totQtyLtrs+openingBal.get("openingBalance").get("qtyLtrs");
	}
	
//Here getting Tm preparation opening balance
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,parameters.customTimePeriodId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,unit.facilityId)));
	cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	currentPeriodOutputEntry = delegator.findList("ProcFacilityOutput",cond,null,null,null,false);
	if(UtilValidate.isNotEmpty(currentPeriodOutputEntry)){
		currentPeriodOutputEntry.each{ outputEntry ->			
				if("TM_PREPARATION_OB".equals(outputEntry.outputTypeId)){
						tmObQty += outputEntry.get("qty");
						tmObKgFat += outputEntry.get("kgFat");
						tmObKgSnf += outputEntry.get("kgSnf");
						tmObQtyLtrs += outputEntry.get("quantityLtrs");						
				}
		}
	}
	unitTotals = ProcurementReports.getPeriodTransferTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: unit.facilityId]);
	if(UtilValidate.isNotEmpty(unitTotals)){
		unitTotalsMap[unit.facilityId] =[:];
		unitTotalsMap[unit.facilityId].putAll(unitTotals);
	}	
}
openingBalMap["qty"] = totQty;
openingBalMap["kgFat"] = totKgFat;
openingBalMap["kgSnf"] = totKgSnf;
openingBalMap["quantityLtrs"] = totQtyLtrs;

shedWiseTotMap["OpeningBalace"]=[:];
shedWiseTotMap["OpeningBalace"].putAll(openingBalMap);

TMPreparationOB["qty"] = tmObQty;
TMPreparationOB["kgFat"] = tmObKgFat;
TMPreparationOB["kgSnf"] = tmObKgSnf;
TMPreparationOB["quantityLtrs"] = tmObQtyLtrs;

shedWiseTotMap["TMPreparation"]=[:];
shedWiseTotMap["TMPreparation"].putAll(TMPreparationOB);
context.putAt("shedWiseTotMap", shedWiseTotMap);
shedTotals = ProcurementReports.getPeriodTransferTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: parameters.shedId]);
context.putAt("shedTotals", shedTotals);
context.put("unitTotalsMap", unitTotalsMap);

