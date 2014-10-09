import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementServices;
import net.sf.json.JSONObject;

dctx = dispatcher.getDispatchContext();
String shedId = parameters.shedId;
String customTimePeriodId = parameters.customTimePeriodId;
List missingDataList = FastList.newInstance();

Map shedUnits = FastMap.newInstance();
shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : shedId]);
List unitIds = FastList.newInstance();
unitIds.addAll(shedUnits.get("unitsList"));
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",["customTimePeriodId":customTimePeriodId],false);
	Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	Timestamp thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	if(UtilValidate.isNotEmpty(customTimePeriod)){
			fromDate=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
			thruDate=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
		}
	// here we are getting customTimePeriodIds  between fromDate and thruDate
	List<GenericValue> customTimePeriodList = FastList.newInstance();
	List condList = FastList.newInstance();
	condList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,customTimePeriod.getDate("fromDate")));
	condList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,customTimePeriod.getDate("thruDate")));
	condList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,customTimePeriod.get("periodTypeId")));
	condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
	List periodUnitsList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",EntityCondition.makeCondition(condList,EntityOperator.AND),null,null,null,false);
	List periodUnitIds = FastList.newInstance();
	Set tempUnitIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(periodUnitsList, "facilityId", false)) 
	periodUnitIds.addAll((tempUnitIdsSet.toList()).sort());
	
	List transfersConditionList = FastList.newInstance();
	transfersConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,periodUnitIds),EntityOperator.OR, EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,periodUnitIds)));
	transfersConditionList.add(EntityCondition.makeCondition("sendDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	transfersConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt",EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("isMilkRcpt",EntityOperator.EQUALS,"N")));
	transfersConditionList.add(EntityCondition.makeCondition("sendDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	EntityCondition transfersCondition = EntityCondition.makeCondition(transfersConditionList,EntityOperator.AND);
	
	List milkTransfersList = delegator.findList("MilkTransfer",transfersCondition,null,null,null,false);
	for(String unitId in periodUnitIds){
		GenericValue unitDetails = delegator.findOne("Facility",["facilityId": unitId],false);
		Map periodTotals = ProcurementReports.getPeriodTotals(dctx,UtilMisc.toMap("userLogin", userLogin, "fromDate", fromDate, "thruDate", thruDate,"facilityId",unitId));
			if(UtilValidate.isNotEmpty(periodTotals)){
				// here we are getting Transfers for the unit
				Map missingTransfersMap = FastMap.newInstance();
				List transfersList = FastList.newInstance();	
				transfersList = EntityUtil.filterByCondition(milkTransfersList, EntityCondition.makeCondition("facilityId",unitId));
				if(UtilValidate.isNotEmpty(transfersList)){
					List inProcessList = EntityUtil.filterByCondition(milkTransfersList, EntityCondition.makeCondition("statusId","MXF_INPROCESS"));
						if(UtilValidate.isNotEmpty(inProcessList)){
								missingTransfersMap.put("facilityCode", unitDetails.get("facilityCode"));
								missingTransfersMap.put("facilityName", unitDetails.get("facilityName"));
								missingTransfersMap.put("MissingType", "Milk Transfer");
								missingTransfersMap.put("status", "Not Approved");
								missingDataList.add(missingTransfersMap);
							}
					}else{
						missingTransfersMap.put("facilityCode", unitDetails.get("facilityCode"));
						missingTransfersMap.put("facilityName", unitDetails.get("facilityName"));
						missingTransfersMap.put("MissingType", "Milk Transfer");
						missingTransfersMap.put("status", "Not Entered");
						missingDataList.add(missingTransfersMap);
					
					}
				//here we are trying to get staus of billing 
				   List billingList = FastList.newInstance();
				   List billingConditionList = FastList.newInstance();
				   billingConditionList.add(EntityCondition.makeCondition("billingTypeId","PB_PROC_MRGN"));
				   billingConditionList.add(EntityCondition.makeCondition("statusId","GENERATED"));
				   billingConditionList.add(EntityCondition.makeCondition("facilityId",unitId));
				   billingConditionList.add(EntityCondition.makeCondition("customTimePeriodId",customTimePeriodId));
				   EntityCondition billingCondition = EntityCondition.makeCondition(billingConditionList,EntityOperator.AND); 
				   billingList = delegator.findList("PeriodBilling",billingCondition,null,null,null,false);
				   if(UtilValidate.isEmpty(billingList)){
					   Map missingBillingMap =FastMap.newInstance();
					   missingBillingMap.put("facilityCode", unitDetails.get("facilityCode"));
					   missingBillingMap.put("facilityName", unitDetails.get("facilityName"));
					   missingBillingMap.put("MissingType", "Billing");
					   missingBillingMap.put("status", "Not Generated");
					   missingDataList.add(missingBillingMap);
					   }
			}
	}
}
context.putAt("missingDataList", missingDataList);