import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;


dctx = dispatcher.getDispatchContext();
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

thruDateEnd = UtilDateTime.getDayEnd(thruDate);

allCustomTimePeriodIds = [];
List condList =[];
condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(thruDateEnd)));
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
List<GenericValue> allCustomTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, UtilMisc.toList("fromDate"), null, false);
if(UtilValidate.isNotEmpty(allCustomTimePeriodList)){
	allCustomTimePeriodIds = EntityUtil.getFieldListFromEntityList(allCustomTimePeriodList,"customTimePeriodId", true);
}


bonusEmplList = [];

List condList1 =[];
condList1.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_BONUS"));
condList1.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"SP_BONUS"));
condList1.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));  
condList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
condList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(thruDateEnd)));
EntityCondition cond1 = EntityCondition.makeCondition(condList1,EntityOperator.AND);
List<GenericValue> bonusBillingIdsDetailList = delegator.findList("PeriodBillingAndCustomTimePeriod", cond1, null, null, null, false);
if(UtilValidate.isNotEmpty(bonusBillingIdsDetailList)){
	GenericValue bonusBillingIdsList = EntityUtil.getFirst(bonusBillingIdsDetailList);
	if(UtilValidate.isNotEmpty(bonusBillingIdsList)){
		bonusBillingId = bonusBillingIdsList.get("periodBillingId");
		List condList2 =[];
		condList2.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,bonusBillingId));
		EntityCondition cond2 = EntityCondition.makeCondition(condList2,EntityOperator.AND);
		List<GenericValue> bonusEmplDetailList = delegator.findList("PayrollHeader", cond2, null, null, null, false);
		if(UtilValidate.isNotEmpty(bonusEmplDetailList)){
			bonusEmplList = EntityUtil.getFieldListFromEntityList(bonusEmplDetailList,"partyIdFrom", true);
		}
	}
}

bonusEmplIdsList = [];
if(UtilValidate.isNotEmpty(parameters.employeeId)){
	bonusEmplIdsList.add(parameters.employeeId);
}else{
	bonusEmplIdsList.addAll(bonusEmplList);
}


employeeWiseMap = [:];
if(UtilValidate.isNotEmpty(bonusEmplIdsList)){
	bonusEmplIdsList.each{ employeeId->
		monthWiseMap = [:];
		finAccountCode = "";
		List finAccConList=FastList.newInstance();
		finAccConList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,employeeId));
		finAccConList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
		finAccConList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
		EntityCondition finAccCond = EntityCondition.makeCondition(finAccConList, EntityOperator.AND);
		List<GenericValue> finAccountDetailsList = delegator.findList("FinAccount", finAccCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(finAccountDetailsList)){
			GenericValue finAccountList = EntityUtil.getFirst(finAccountDetailsList);
			finAccountCode = finAccountList.get("finAccountCode");
		}
		if(UtilValidate.isNotEmpty(allCustomTimePeriodIds)){
			allCustomTimePeriodIds.each{ customTimePeriodKey ->
				monthlyDetailsMap = [:];
				totalValue = 0;
				basic = 0;
				dearnessAllowance =0;
				specPay = 0;
				fixedPay = 0;
				noOfPayableDays = 0;
				noOfArrearDays = 0;
				lossOfPayDays = 0;
				bonus = 0;
				customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodKey] , false);
				if(UtilValidate.isNotEmpty(customTimePeriod)){
					Date monthDate = (Date)customTimePeriod.get("fromDate");
					monthDateStart = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(monthDate), timeZone, locale);
					monthDateEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(monthDate), timeZone, locale);
					customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"userLogin",userLogin)).get("periodTotalsForParty");
					if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
						Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
						while(customTimePeriodIter.hasNext()){
							Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
							if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
								Map periodTotalsMap = [:];
								periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
								basic = periodTotals.get("PAYROL_BEN_SALARY");
								if(UtilValidate.isEmpty(basic)){
									basic = 0;
								}
								dearnessAllowance = periodTotals.get("PAYROL_BEN_DA");
								if(UtilValidate.isEmpty(dearnessAllowance)){
									dearnessAllowance = 0;
								}
								specPay = periodTotals.get("PAYROL_BEN_SPELPAY");
								if(UtilValidate.isEmpty(specPay)){
									specPay = 0;
								}
								fixedPay = periodTotals.get("PAYROL_BEN_FXEDPAY");
								if(UtilValidate.isEmpty(fixedPay)){
									fixedPay = 0;
								}
								payrollAttendance=delegator.findOne("PayrollAttendance",[partyId:employeeId,customTimePeriodId:customTimePeriodKey],false);
								if(UtilValidate.isNotEmpty(payrollAttendance)){
									noOfPayableDays=payrollAttendance.get("noOfPayableDays");
									if(UtilValidate.isEmpty(noOfPayableDays)){
										noOfPayableDays = 0;
									}
									noOfArrearDays=payrollAttendance.get("noOfArrearDays");
									if(UtilValidate.isEmpty(noOfArrearDays)){
										noOfArrearDays = 0;
									}
									lossOfPayDays = payrollAttendance.get("lossOfPayDays");
									if(UtilValidate.isEmpty(lossOfPayDays)){
										lossOfPayDays = 0;
									}
								}
							}
						}
					}
					bonusPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_BONUS","billingTypeId","SP_BONUS","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
					if(UtilValidate.isNotEmpty(bonusPeriodTotals)){
						Iterator bonusPeriodTotalsIter = bonusPeriodTotals.entrySet().iterator();
						while(bonusPeriodTotalsIter.hasNext()){
							Map.Entry bonusPeriodEntry = bonusPeriodTotalsIter.next();
							if(bonusPeriodEntry.getKey() != "customTimePeriodTotals"){
								bonusTotals = bonusPeriodEntry.getValue().get("periodTotals");
								bonus = bonusTotals.get("PAYROL_BEN_BONUS_EX");
								if(UtilValidate.isEmpty(bonus)){
									bonus = 0;
								}
							}
						}
					}
				}
				
				totalValue = totalValue + basic;
				totalValue = totalValue + dearnessAllowance;
				totalValue = totalValue + specPay;
				totalValue = totalValue + fixedPay;
				
				monthlyDetailsMap.put("basic", basic);
				monthlyDetailsMap.put("dearnessAllowance", dearnessAllowance);
				monthlyDetailsMap.put("specPay", specPay);
				monthlyDetailsMap.put("fixedPay", fixedPay);
				monthlyDetailsMap.put("noOfPayableDays", noOfPayableDays);
				monthlyDetailsMap.put("noOfArrearDays", noOfArrearDays);
				monthlyDetailsMap.put("lossOfPayDays", lossOfPayDays);
				monthlyDetailsMap.put("totalValue", totalValue);
				monthlyDetailsMap.put("bonus", bonus);
				monthlyDetailsMap.put("finAccountCode", finAccountCode);
				monthWiseMap.put(customTimePeriodKey, monthlyDetailsMap);
			}
		}
		if(UtilValidate.isNotEmpty(monthWiseMap)){
			employeeWiseMap.put(employeeId, monthWiseMap);
		}
	}
}

context.put("employeeWiseMap", employeeWiseMap);


