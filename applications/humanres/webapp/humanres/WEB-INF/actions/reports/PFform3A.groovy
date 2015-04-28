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

employeeMap = [:];
monthNameMap = [:];
currMonthNameMap =[:];
employeeWiseMap = [:];
if(UtilValidate.isNotEmpty(employmentsList)){
	employmentsList.each{ employee ->
		employeeDetailsMap = [:];
		employeeDetails = delegator.findOne("EmployeeDetail", [partyId : employee], false);
		if(UtilValidate.isNotEmpty(employeeDetails)){
			pfAccNo = employeeDetails.get("presentEpf");
			employeeDetailsMap.put("pfAccNo",pfAccNo);
		}
		personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employee), false);
		if(UtilValidate.isNotEmpty(personDetails)){
			firstName = personDetails.get("firstName");
			lastName = personDetails.get("lastName");
			fatherName = personDetails.get("fatherName");
			employeeName = firstName +" "+ lastName;
			employeeDetailsMap.put("employeeName",employeeName);
			employeeDetailsMap.put("fatherName",fatherName);
		}
		
		periodWiseMap = [:];
		if(UtilValidate.isNotEmpty(timePeriodIdsList)){
			timePeriodIdsList.each{ periodId ->
				timePeriodId = "";
				detailsMap = [:];
				workerShare = 0;
				monthTotWages = 0;
				employerEpf = 0;
				employerFpf = 0;
				employeeEpf = 0;
				employeeVpf = 0;
				GenericValue customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : periodId], false);
				if(UtilValidate.isNotEmpty(customTimePeriodDetails)){
					monthFromDate=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("fromDate"));
					monthThruDate=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("thruDate"));
					prevMonth = UtilDateTime.addDaysToTimestamp(monthFromDate, -1);
					prevMonthStart=UtilDateTime.getMonthStart(prevMonth);
					prevMonthEnd = UtilDateTime.getMonthEnd(prevMonthStart, timeZone, locale);
					
					List monthsConList=[];
					monthsConList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
					monthsConList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(prevMonthStart)));
					monthsConList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(prevMonthEnd)));
					monthCond=EntityCondition.makeCondition(monthsConList,EntityOperator.AND);
					monthsList = delegator.findList("CustomTimePeriod", monthCond , null, null, null, false );
					if(UtilValidate.isNotEmpty(monthsList)){
						monthsList = EntityUtil.getFirst(monthsList);
						timePeriodId = monthsList.get("customTimePeriodId");
					}
					monthNameMap.put(timePeriodId, prevMonthStart);
					currMonthNameMap.put(timePeriodId, monthFromDate);
					
					Timestamp monthBegin = UtilDateTime.getMonthStart(prevMonthStart);
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
								houseRentAllowance =0;
								cityComp = 0;
								HeatAllowance=0;
								CashAllowance = 0;
								coldAllowance = 0;
								convey = 0;
								ShiftAllowance = 0;
								CanteenAllowance = 0;
								attendanceBonus =0;
								FieldAllowance = 0;
								SpecialPay = 0;
								GeneralHolidayWages = 0;
								SecondSaturdayWages = 0;
							
								personalPay =0;
								secndSatDay =0;
								shift =0;
								washing =0;
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
								houseRentAllowance = periodTotals.get("PAYROL_BEN_HRA");
								if(UtilValidate.isEmpty(houseRentAllowance)){
									houseRentAllowance = 0;
								}
								cityComp = periodTotals.get("PAYROL_BEN_CITYCOMP");
								if(UtilValidate.isEmpty(cityComp)){
									cityComp = 0;
								}
								HeatAllowance = periodTotals.get("PAYROL_BEN_HEATALLOW");
								if(UtilValidate.isEmpty(HeatAllowance)){
									HeatAllowance = 0;
								}
								CashAllowance = periodTotals.get("PAYROL_BEN_CASH");
								if(UtilValidate.isEmpty(CashAllowance)){
									CashAllowance = 0;
								}
								coldAllowance = periodTotals.get("PAYROL_BEN_COLDALLOW");
								if(UtilValidate.isEmpty(coldAllowance)){
									coldAllowance = 0;
								}
								convey = periodTotals.get("PAYROL_BEN_CONVEY");
								if(UtilValidate.isEmpty(convey)){
									convey = 0;
								}
								ShiftAllowance = periodTotals.get("PAYROL_BEN_SHIFT");
								if(UtilValidate.isEmpty(ShiftAllowance)){
									ShiftAllowance = 0;
								}
								CanteenAllowance = periodTotals.get("PAYROL_BEN_CANTN");
								if(UtilValidate.isEmpty(CanteenAllowance)){
									CanteenAllowance = 0;
								}
								AttendanceBonus = periodTotals.get("PAYROL_BEN_ATNDBON");
								if(UtilValidate.isEmpty(AttendanceBonus)){
									AttendanceBonus = 0;
								}
								FieldAllowance = periodTotals.get("PAYROL_BEN_FIELD");
								if(UtilValidate.isEmpty(FieldAllowance)){
									FieldAllowance = 0;
								}
								SpecialPay = periodTotals.get("PAYROL_BEN_SPELPAY");
								if(UtilValidate.isEmpty(SpecialPay)){
									SpecialPay = 0;
								}
								GeneralHolidayWages = periodTotals.get("PAYROL_BEN_GEN_HOL_W");
								if(UtilValidate.isEmpty(GeneralHolidayWages)){
									GeneralHolidayWages = 0;
								}
								SecondSaturdayWages = periodTotals.get("PAYROL_BEN_SECSATDAY");
								if(UtilValidate.isEmpty(SecondSaturdayWages)){
									SecondSaturdayWages = 0;
								}
								EmployeeStateInsurance = periodTotals.get("PAYROL_DD_ESI");
								if(UtilValidate.isEmpty(EmployeeStateInsurance)){
									MedicalAllowance = periodTotals.get("PAYROL_BEN_MED_ALLOW");
									if(UtilValidate.isEmpty(MedicalAllowance)){
										MedicalAllowance = 0;
									}
								}
								others = basic+dearnessAllowance+houseRentAllowance+cityComp+HeatAllowance+CashAllowance+coldAllowance+convey+ShiftAllowance;
								Wages = others+CanteenAllowance+attendanceBonus+FieldAllowance+SpecialPay+GeneralHolidayWages+SecondSaturdayWages+MedicalAllowance;
								monthTotWages = monthTotWages + Wages;
								
								List stautsList = UtilMisc.toList("GENERATED","APPROVED");
								periodBillingIdList = [];
								conditionList=[];
								conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , "PAYROLL_BILL"));
								conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,stautsList));
								conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,timePeriodId));
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
											
											employeePFList = delegator.findList("PayrollHeaderItem", EntityCondition.makeCondition(["payrollHeaderId" : headerId]), null, null, null, false);
											if(UtilValidate.isNotEmpty(employeePFList)){
												employeePFList.each{ employeepf ->
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
								workerShare = employeeEpf + employeeVpf;
							}
						}
					}
				}
				detailsMap.put("monthTotWages", monthTotWages);
				detailsMap.put("employerEpf", employerEpf);
				detailsMap.put("employerFpf", employerFpf);
				detailsMap.put("workerShare", workerShare);
				periodWiseMap.put(timePeriodId, detailsMap);
			}
		}
		employeeMap.put(employee,employeeDetailsMap);
		employeeWiseMap.put(employee,periodWiseMap);
	}
}

context.put("employeeWiseMap", employeeWiseMap);
context.put("currMonthNameMap", currMonthNameMap);
context.put("monthNameMap", monthNameMap);
context.put("employeeMap", employeeMap);


