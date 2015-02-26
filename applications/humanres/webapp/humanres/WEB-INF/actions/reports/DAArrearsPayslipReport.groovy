import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import javolution.util.FastMap;


dctx = dispatcher.getDispatchContext();

periodBillingIdParam = parameters.periodBillingId;
customTimePeriodId = parameters.customTimePeriodId;



employmentsList = [];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-365)));
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
DAArrearMap = [:];
employmentsList.each{ employeeId ->
	
	customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId] , false);
	if(UtilValidate.isNotEmpty(customTimePeriod)){
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
		periodBillingBasic = delegator.findOne("PeriodBilling",[periodBillingId : periodBillingIdParam] , false);
		basicSalDate = periodBillingBasic.basicSalDate;
		
		List condBasicSalPeriodList = FastList.newInstance();
		condBasicSalPeriodList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayStart(fromDateTime))));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayEnd(thruDateTime))));
		EntityCondition basicSalPeriodCond = EntityCondition.makeCondition(condBasicSalPeriodList,EntityOperator.AND);
		List basicSalPeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", basicSalPeriodCond, null, null, null, false);
		periodBillingIdList = EntityUtil.getFieldListFromEntityList(basicSalPeriodList, "periodBillingId", true);
		periodMap = [:];
		if(UtilValidate.isNotEmpty(periodBillingIdList)){
			
			periodBillingIdList.each{ periodBillingId->
				
				periodBillingEach = delegator.findOne("PeriodBilling",[periodBillingId : periodBillingId] , false);
				ctpBillingId = periodBillingEach.customTimePeriodId;
				
				BigDecimal oldDAAmount = BigDecimal.ZERO;
				BigDecimal rateAmount = BigDecimal.ZERO;
				BigDecimal newDAAmount = BigDecimal.ZERO;
				BigDecimal netDAAmount = BigDecimal.ZERO;
				
				
				
				
				List payHeadCondList = FastList.newInstance();
				payHeadCondList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId));
				payHeadCondList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_DA"));
				payHeadCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond = EntityCondition.makeCondition(payHeadCondList,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter)){
					GenericValue payrollItems = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter);
					oldDAAmount = (BigDecimal)payrollItems.get("amount");
				}
				payHeadCondList.clear();
				payHeadCondList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId));
				payHeadCondList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_SALARY"));
				payHeadCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond1 = EntityCondition.makeCondition(payHeadCondList,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter1 = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter1)){
					GenericValue payrollItems1 = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter1);
					basicAmount = (BigDecimal)payrollItems1.get("amount");
					if(UtilValidate.isNotEmpty(basicAmount)){
						List rateAmountCondList = FastList.newInstance();
						rateAmountCondList.add(EntityCondition.makeCondition("rateTypeId" ,EntityOperator.EQUALS , "DA_BGLR_RATE"));
						rateAmountCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalDate))));
						rateAmountCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalDate)))));
						EntityCondition rateAmountCond = EntityCondition.makeCondition(rateAmountCondList,EntityOperator.AND);
						List<GenericValue> rateAmountList = delegator.findList("RateAmount", rateAmountCond,null, UtilMisc.toList("fromDate"), null, false);
						//Debug.log("payrollHeaderAndHeaderItemIter====="+payrollHeaderAndHeaderItemIter);
						if(UtilValidate.isNotEmpty(rateAmountList)){
							GenericValue rateAmountGen = EntityUtil.getFirst(rateAmountList);
							rateAmount = (BigDecimal) rateAmountGen.get("rateAmount");
						}
						newDAAmount = (0.35*basicAmount);
						if(UtilValidate.isNotEmpty(newDAAmount)){
							netDAAmount  = newDAAmount - oldDAAmount;
						}
						tempMap = [:];
						tempMap.put("Basic",basicAmount);
						tempMap.put("oldDA",oldDAAmount);
						tempMap.put("newDA",newDAAmount);
						tempMap.put("netDA",netDAAmount);
						if(UtilValidate.isNotEmpty(tempMap)){
							periodMap.putAt(ctpBillingId, tempMap);
						}
					}
				}
			}
		}
	}
	if(UtilValidate.isNotEmpty(periodMap)){
		DAArrearMap.put(employeeId,periodMap);
	}
}
Debug.log("DAArrearMap================================="+DAArrearMap);

context.DAArrearMap=DAArrearMap;

		
	
