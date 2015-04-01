import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
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
bonusBillingId = "";
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

emplBonusMap = [:];
employeeWiseMap = [:];
totEmployeePFBonusMap = [:];
if(UtilValidate.isNotEmpty(bonusEmplIdsList)){
	bonusEmplIdsList.each{ employeeId->
		totalBonus = 0;
		totBonusAmount = 0;
		monthWiseMap = [:];
		finAccountCode = "";
		employeePFBonusMap = [:];
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
				ptAmount = 0;
				noOfPayableDays = 0;
				noOfArrearDays = 0;
				lossOfPayDays = 0;
				noOfCalenderDays = 0;
				bonus = 0;
				monthlyBonusOfPayableDays = 0;
				minimumBonus = 0;
				newDAAmount = 0;
				totalEarnings = 0;
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
								ptAmount = periodTotals.get("PAYROL_DD_PR_TAX");
								if (UtilValidate.isEmpty(ptAmount)) {
									ptAmount = 0;
								}
								if (UtilValidate.isNotEmpty(ptAmount) && (ptAmount != 0)) {
									ptAmount = ptAmount*(-1);
								}
								customMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:monthDateStart,timePeriodEnd:monthDateEnd,timePeriodId:customTimePeriodKey,locale:locale]);
								lastClosePeriod=customMap.get("lastCloseAttedancePeriod");
								attenCustomTimePeriodId=lastClosePeriod.get("customTimePeriodId");
								if(UtilValidate.isNotEmpty(attenCustomTimePeriodId)){
									attenCustomTimePeriodId = attenCustomTimePeriodId;
								}else{
									attenCustomTimePeriodId = null;
								}
								payrollAttendance=delegator.findOne("PayrollAttendance",[partyId:employeeId,customTimePeriodId:attenCustomTimePeriodId],false);
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
									noOfCalenderDays=payrollAttendance.get("noOfCalenderDays");
									if(UtilValidate.isEmpty(noOfCalenderDays)){
										noOfCalenderDays = 0;
									}
								}
							}
							if(customTimePeriodEntry.getKey() == "customTimePeriodTotals"){
								if(UtilValidate.isNotEmpty(customTimePeriodEntry.getValue())){
									totalEarnings = customTimePeriodEntry.getValue().get("grossBenefitAmt");
								}
							}
						}
					}
					String rateTypeId = null;
					Map activeEmployeeDetails = PayrollService.getEmployeePayrollCondParms(dctx, UtilMisc.toMap("employeeId",employeeId,"timePeriodStart",monthDateStart,"timePeriodEnd" ,monthDateEnd ,"userLogin",userLogin));
					if(UtilValidate.isNotEmpty(activeEmployeeDetails)){
						String activeGeoId = (String)activeEmployeeDetails.get("geoId");
						if(UtilValidate.isNotEmpty(activeGeoId)){
							if(activeGeoId.equals("BAGALKOT")){
								rateTypeId = "DA_BAGALKOT_RATE";
							}
							if(activeGeoId.equals("BELL")){
								rateTypeId = "DA_BELL_RATE";
							}
							if(activeGeoId.equals("BGLR")){
								rateTypeId = "DA_BGLR_RATE";
							}
							if(activeGeoId.equals("DRWD")){
								rateTypeId = "DA_DRWD_RATE";
							}
							if(activeGeoId.equals("GULB")){
								rateTypeId = "DA_GULB_RATE";
							}
						}
					}
					if(UtilValidate.isNotEmpty(rateTypeId)){
						List rateAmountCondList = FastList.newInstance();
						rateAmountCondList.add(EntityCondition.makeCondition("rateTypeId" ,EntityOperator.EQUALS , rateTypeId));
						rateAmountCondList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, monthDateStart));
						rateAmountCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthDateEnd)));
						EntityCondition rateAmountCond = EntityCondition.makeCondition(rateAmountCondList,EntityOperator.AND);
						List<GenericValue> rateAmountList = delegator.findList("RateAmount", rateAmountCond,null, UtilMisc.toList("-fromDate"), null, false);
						if(UtilValidate.isNotEmpty(rateAmountList)){
							GenericValue rateAmountGen = EntityUtil.getFirst(rateAmountList);
							rateAmount = (BigDecimal) rateAmountGen.get("rateAmount");
							if(UtilValidate.isNotEmpty(rateAmount)){
								newDAAmount = (rateAmount*basic);
							  }
						  }
					 }
					/*bonusPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_BONUS","billingTypeId","SP_BONUS","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
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
					}*/
				}
				totalValue = totalValue + basic;
				totalValue = totalValue + newDAAmount;
				totalValue = totalValue + specPay;
				bonusValue = totalValue;
				totalValue = totalValue + fixedPay;
				percentageOfTotal = (0.1)*(bonusValue);
				
				Timestamp employmentThruDate = null;
				netPayableDays = 0;
				List employmentList = FastList.newInstance();
				employmentList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
				employmentList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthDateStart)));
				EntityCondition empCondition = EntityCondition.makeCondition(employmentList, EntityOperator.AND);
				List<GenericValue> employments = delegator.findList("Employment", empCondition, null, UtilMisc.toList("-fromDate"), null, false);
				GenericValue employment = EntityUtil.getFirst(employments);
				if(UtilValidate.isNotEmpty(employment)){
					employmentThruDate = employment.getTimestamp("thruDate");
					if(UtilValidate.isNotEmpty(employmentThruDate)){
						Timestamp thruDateMonthStart = UtilDateTime.getMonthStart(employmentThruDate);
						if(thruDateMonthStart.compareTo(monthDateStart)== 0){
							double intervalDays = UtilDateTime.getIntervalInDays(thruDateMonthStart, employmentThruDate)+1;
							BigDecimal intDays = new BigDecimal(intervalDays);
							if(UtilValidate.isNotEmpty(intDays) && ((intDays).compareTo(BigDecimal.ZERO) != 0)){
								netPayableDays = intDays;
							}
						}
					}
				}
				finalPayableDays = noOfPayableDays;
				if(UtilValidate.isNotEmpty(netPayableDays) && ((netPayableDays).compareTo(BigDecimal.ZERO) != 0)){
					if((netPayableDays).compareTo(noOfPayableDays) < 0){
						finalPayableDays = netPayableDays;
					}
				}else{
					finalPayableDays = noOfPayableDays;
				}
				
				monthlyBonus = 22000/12;
				
				if(noOfCalenderDays != 0){
					monthlyBonusOfPayableDays = monthlyBonus*(finalPayableDays/noOfCalenderDays);
				}else{
					monthlyBonusOfPayableDays = 0;
				}
				if(percentageOfTotal < monthlyBonusOfPayableDays){
					minimumBonus = percentageOfTotal;
				}else{
					minimumBonus = monthlyBonusOfPayableDays;
				}
				totalBonus = totalBonus + minimumBonus;
				minimumBonusVal = new BigDecimal(minimumBonus);
				minimumBonusVal = minimumBonusVal.setScale(0, BigDecimal.ROUND_HALF_UP);
				
				if(UtilValidate.isNotEmpty(ptAmount) && (ptAmount!=0)){
					netGrossSalary = 0;
					netPfAmount = 0;
					totalNetPfAmount = 0;
					if(UtilValidate.isNotEmpty(totalEarnings)){
						netGrossSalary = totalEarnings + minimumBonusVal;
						if(UtilValidate.isNotEmpty(netGrossSalary)){
							if ((netGrossSalary < 9999) || (netGrossSalary > 0)) {
								netPfAmount = 150;
							}
							if(netGrossSalary >= 10000){
								netPfAmount = 200;
							}
						}
						if(UtilValidate.isNotEmpty(netPfAmount)){
							if ((netPfAmount.equals(ptAmount))) {
								totalNetPfAmount = 0;
							}else{
								totalNetPfAmount = netPfAmount-ptAmount;
							}
						}
					}
					if(UtilValidate.isNotEmpty(totalNetPfAmount)){
						monthlyDetailsMap.put("totalNetPfAmount", totalNetPfAmount);
					}
				}
				
				
				monthlyDetailsMap.put("basic", basic);
				monthlyDetailsMap.put("dearnessAllowance", newDAAmount);
				monthlyDetailsMap.put("specPay", specPay);
				monthlyDetailsMap.put("fixedPay", fixedPay);
				monthlyDetailsMap.put("noOfPayableDays", finalPayableDays);
				monthlyDetailsMap.put("noOfArrearDays", noOfArrearDays);
				monthlyDetailsMap.put("lossOfPayDays", lossOfPayDays);
				monthlyDetailsMap.put("totalValue", totalValue);
				monthlyDetailsMap.put("bonus", minimumBonusVal);
				monthlyDetailsMap.put("finAccountCode", finAccountCode);
				monthWiseMap.put(customTimePeriodKey, monthlyDetailsMap);
			}
		}
		List PayrollHeaderBonusCondList=FastList.newInstance();
		PayrollHeaderBonusCondList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,bonusBillingId));
		PayrollHeaderBonusCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,employeeId));
		PayrollHeaderBonusCondList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS ,"PAYROL_BEN_BONUS_EX"));
		EntityCondition PayrollHeaderBonusCond = EntityCondition.makeCondition(PayrollHeaderBonusCondList, EntityOperator.AND);
		List<GenericValue> PayrollHeaderBonusList = delegator.findList("PayrollHeaderAndHeaderItem", PayrollHeaderBonusCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(PayrollHeaderBonusList)){
			GenericValue PayrollHeaderBonusGen = EntityUtil.getFirst(PayrollHeaderBonusList);
			totBonusAmount = PayrollHeaderBonusGen.get("amount");
		}
		if(UtilValidate.isNotEmpty(monthWiseMap)){
			employeeWiseMap.put(employeeId, monthWiseMap);
		}
		if(totalBonus > 22000){
			totalBonus = 22000;
		}else{
			totalBonus = totalBonus;
		}
		totalBonusVal = new BigDecimal(totalBonus);
		totalBonusVal = totalBonusVal.setScale(0, BigDecimal.ROUND_HALF_UP);
		emplBonusMap.put(employeeId, totBonusAmount);
	}
}

context.put("employeeWiseMap", employeeWiseMap);
context.put("emplBonusMap", emplBonusMap);