import java.util.*;
import java.lang.*;
import java.sql.*;
import java.util.Calendar;
import java.sql.Timestamp;

import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();
Timestamp fromDateStart = null;
Timestamp thruDateEnd = null;
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	context.put("fromDate",fromDateStart);
	context.put("thruDate",thruDateEnd);
}

Timestamp yearBegin = UtilDateTime.getMonthStart(fromDateStart);
Timestamp yearEnd = UtilDateTime.getMonthEnd(thruDateEnd, timeZone, locale);

timePeriodIdsList = [];
List hrTimePeriodIdCondList=[];
hrTimePeriodIdCondList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
hrTimePeriodIdCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(yearBegin)));
hrTimePeriodIdCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(yearEnd)));
hrTimePeriodCond=EntityCondition.makeCondition(hrTimePeriodIdCondList,EntityOperator.AND);
hrTimePeriodIdsList = delegator.findList("CustomTimePeriod", hrTimePeriodCond , null, ["fromDate"], null, false );
if(UtilValidate.isNotEmpty(hrTimePeriodIdsList)){
	timePeriodIdsList = EntityUtil.getFieldListFromEntityList(hrTimePeriodIdsList, "customTimePeriodId", true);
}

employmentsList = [];

if(UtilValidate.isNotEmpty(parameters.employeeId)){
	employmentsList.add(parameters.employeeId);
}else{
	emplInputMap = [:];
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("orgPartyId", "Company");
	emplInputMap.put("fromDate", yearBegin);
	emplInputMap.put("thruDate", yearEnd);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	employments=EmploymentsMap.get("employementList");
	if(UtilValidate.isNotEmpty(employments)){
		employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
	}
}

totWages = 0;
totEmployeeEpf = 0;
totEmployeeVpf = 0;
totEmployerEpf = 0;
totEmployerFpf = 0;
employeeTotal = 0;
employerTotal = 0;

//employmentsList = UtilMisc.toList("6220","6013","6672","6031","6648","6632");
employeeMap = [:];
employeeWiseMap = [:];
grandTotMap = [:];
finalTotalsMap = [:];
if(UtilValidate.isNotEmpty(employmentsList)){
	employmentsList.each{ employee ->
		employeeDetailsMap = [:];
		pfAccNo = "";
		employeeName = "";
		monthTotWages = 0;
		employerEpf = 0;
		employerFpf = 0;
		employeeEpf = 0;
		employeeVpf = 0;
		employeeDetails = delegator.findOne("EmployeeDetail", [partyId : employee], false);
		if(UtilValidate.isNotEmpty(employeeDetails)){
			pfAccNo = employeeDetails.get("presentEpf");
			if(UtilValidate.isNotEmpty(pfAccNo)){
				if ((pfAccNo).contains("/")) {
					accArr = pfAccNo.split("/");
					listSize = accArr.size();
					if(listSize == 2){
						pfAccNo = accArr[1];
					}
					if(listSize == 3){
						pfAccNo = accArr[2];
					}
					if(listSize == 4){
						pfAccNo = accArr[3];
					}
				}
			}
		}
		personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employee), false);
		if(UtilValidate.isNotEmpty(personDetails)){
			firstName = personDetails.get("firstName");
			lastName = personDetails.get("lastName");
			fatherName = personDetails.get("fatherName");
			employeeName = firstName +" "+ lastName;
		}
		if(UtilValidate.isNotEmpty(timePeriodIdsList)){
			timePeriodIdsList.each{ periodId ->
				GenericValue customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : periodId], false);
				if(UtilValidate.isNotEmpty(customTimePeriodDetails)){
					monthFromDate=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("fromDate"));
					monthThruDate=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("thruDate"));
					
					Timestamp monthBegin = UtilDateTime.getMonthStart(monthFromDate);
					Timestamp monthEnd = UtilDateTime.getMonthEnd(monthBegin, timeZone, locale);
					customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employee,"fromDate",monthBegin,"thruDate",monthEnd,"userLogin",userLogin)).get("periodTotalsForParty");
					periodDetailsMap=[:];
					if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
						emplyrCont = 0;
						Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
						while(customTimePeriodIter.hasNext()){
							Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
							if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
							
								Map periodTotalsMap = [:];
								periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
								basic = 0;
								dearnessAllowance =0;
								SpecialPay = 0;
								
								others = 0;
								totalBenefits = 0;
								
								basic = periodTotals.get("PAYROL_BEN_SALARY");
								if(UtilValidate.isEmpty(basic)){
									basic = 0;
								}
								detailsMap=[:];
								dearnessAllowance = periodTotals.get("PAYROL_BEN_DA");
								if(UtilValidate.isEmpty(dearnessAllowance)){
									dearnessAllowance = 0;
								}
								SpecialPay = periodTotals.get("PAYROL_BEN_SPELPAY");
								if(UtilValidate.isEmpty(SpecialPay)){
									SpecialPay = 0;
								}
								Wages = basic+dearnessAllowance+SpecialPay;
								monthTotWages = monthTotWages + Wages;
								List stautsList = UtilMisc.toList("GENERATED","APPROVED");
								periodBillingIdList = [];
								conditionList=[];
								conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , "PAYROLL_BILL"));
								conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,stautsList));
								conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,periodId));
								condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
								periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
								if(UtilValidate.isNotEmpty(periodBillingList)){
									periodBillingList = EntityUtil.getFirst(periodBillingList);
									periodBillingId = periodBillingList.get("periodBillingId");
									if(UtilValidate.isNotEmpty(periodBillingId)){
										periodBillingIdList.add(periodBillingId);
									}
								}
								daPeriodBillingIdList = [];
								List condList = FastList.newInstance();
								condList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"SP_DA_ARREARS"));
								condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
								condList.add(EntityCondition.makeCondition("basicSalDate", EntityOperator.GREATER_THAN_EQUAL_TO,monthBegin));
								condList.add(EntityCondition.makeCondition("basicSalDate", EntityOperator.LESS_THAN_EQUAL_TO, monthEnd));
								EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
								List<GenericValue> PeriodBillingAndCustomTimePeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", cond, null, UtilMisc.toList("fromDate"), null, false);
								if(UtilValidate.isNotEmpty(PeriodBillingAndCustomTimePeriodList)){
									daPeriodBillingIdList = EntityUtil.getFieldListFromEntityList(PeriodBillingAndCustomTimePeriodList, "periodBillingId", true);
								}
								if(UtilValidate.isNotEmpty(daPeriodBillingIdList)){
									daPeriodBillingIdList.each{ daPeriodBilling ->
										periodBillingIdList.add(daPeriodBilling);
									}
								}
								if(UtilValidate.isNotEmpty(periodBillingIdList)){
									periodBillingIdList.each{ billingId ->
										List headerConditionList=[];
										headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, billingId));
										headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employee));
										headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
										headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
										if(UtilValidate.isNotEmpty(headerIdsList)){
											headerIdList = EntityUtil.getFirst(headerIdsList);
											headerId = headerIdList.payrollHeaderId;
											employerPFList = delegator.findList("PayrollHeaderItemEc", EntityCondition.makeCondition(["payrollHeaderId" : headerId]), null, null, null, false);
							
											if(UtilValidate.isNotEmpty(employerPFList)){
												employerPFList.each{ employer ->
													if(employer.get("payrollHeaderItemTypeId")=="PAYROL_BEN_PFEMPLYR"){
														employerEpf = employerEpf + employer.get("amount");
													}
													if(employer.get("payrollHeaderItemTypeId")=="PAYROL_BEN_PENSION"){
														employerFpf=employerFpf + employer.get("amount");
													}
												}
											}
											employeeePFList = delegator.findList("PayrollHeaderItem", EntityCondition.makeCondition(["payrollHeaderId" : headerId]), null, null, null, false);
											if(UtilValidate.isNotEmpty(employeeePFList)){
												employeeePFList.each{ employeepf ->
													if(employeepf.get("payrollHeaderItemTypeId")=="PAYROL_DD_EMP_PR"){
														employeeEpf = employeeEpf - employeepf.get("amount");
													}
													if(employeepf.get("payrollHeaderItemTypeId")=="PAYROL_DD_VLNT_PR"){
														employeeVpf=employeeVpf - employeepf.get("amount");
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		employeeTot = employeeEpf + employeeVpf;
		employerTot = employerEpf + employerFpf;
		if(monthTotWages != 0){
			employeeDetailsMap.put("monthTotWages", monthTotWages);
			totWages = totWages  + monthTotWages;
		}
		if(employeeEpf != 0){
			employeeDetailsMap.put("employeeEpf", employeeEpf);
			totEmployeeEpf = totEmployeeEpf  + employeeEpf;
		}
		if(employeeVpf != 0){
			employeeDetailsMap.put("employeeVpf", employeeVpf);
			totEmployeeVpf = totEmployeeVpf  + employeeVpf;
		}
		if(employerEpf != 0){
			employeeDetailsMap.put("employerEpf", employerEpf);
			totEmployerEpf = totEmployerEpf  + employerEpf;
		}
		if(employerFpf != 0){
			employeeDetailsMap.put("employerFpf", employerFpf);
			totEmployerFpf = totEmployerFpf  + employerFpf;
		}
		if(employeeTot != 0){
			employeeDetailsMap.put("employeeTot", employeeTot);
			employeeTotal = employeeTotal + employeeTot;
		}
		if(employerTot != 0){
			employeeDetailsMap.put("employerTot", employerTot);
			employerTotal = employerTotal + employerTot;
		}
		if(UtilValidate.isNotEmpty(employeeDetailsMap)){
			employeeDetailsMap.put("pfAccNo",pfAccNo);
			employeeDetailsMap.put("employeeName",employeeName);
			employeeWiseMap.put(employee,employeeDetailsMap);
		}
	}
}


grandTotMap.put("totWages", totWages);
grandTotMap.put("totEmployeeEpf", totEmployeeEpf);
grandTotMap.put("totEmployeeVpf", totEmployeeVpf);
grandTotMap.put("totEmployerEpf", totEmployerEpf);
grandTotMap.put("totEmployerFpf", totEmployerFpf);
grandTotMap.put("employeeTotal", employeeTotal);
grandTotMap.put("employerTotal", employerTotal);

finalTotalsMap.put("grandTotMap", grandTotMap);

context.put("employeeWiseMap", employeeWiseMap);
context.put("finalTotalsMap", finalTotalsMap);
