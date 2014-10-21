import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;


dctx = dispatcher.getDispatchContext();

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;


def sdf = new SimpleDateFormat("MMMM dd,yyyy");
try {
	if (fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	}
	if (thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}


periodList=[];
PeriodIdsList=[];
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
totalDays=UtilDateTime.getIntervalInDays(fromDate,thruDate);
totalDays=totalDays+1;
if(totalDays>183){
	Debug.logError("We can't select more than 6 months..","");
	context.errorMessage = "We can't select more than 6 months..";
}
List conList = [];
conList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
conList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDate)));
conList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "HR_MONTH"));
List customTimePeriodList = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(conList,EntityOperator.AND),null,null,null,false);

if(UtilValidate.isNotEmpty(customTimePeriodList)){
	PeriodIdsList = EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", true);
}

context.put("PeriodIdsList",PeriodIdsList);
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyIdTo"));
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
periodBillingIdMap=[:];
PeriodIdsList.each{ Period ->
	List periodbillingConditionList=[];
	periodbillingConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, Period));
	periodbillingConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
	periodbillingCondition = EntityCondition.makeCondition(periodbillingConditionList,EntityOperator.AND);
	BillingList = delegator.findList("PeriodBilling", periodbillingCondition, null, null, null, false);
	if(UtilValidate.isNotEmpty(BillingList)){
		BillingList = EntityUtil.getFirst(BillingList);
		BillingId = BillingList.periodBillingId;
		periodBillingIdMap.put(Period,BillingId);
	}
}

EmplWiseDetailsMap=[:];
EmplNameMap=[:];
totalWages=0;
if(UtilValidate.isNotEmpty(employmentsList)){
	employmentsList.each{ employeeId ->
		customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",fromDate,"thruDate",thruDate,"userLogin",userLogin)).get("periodTotalsForParty");
		totpayableDays=0;
		totWages=0;
		employeeContributn=0;
		employerContributn=0;
		Map periodTotalsMap = [:];
		if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
			Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
			while(customTimePeriodIter.hasNext()){
				Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
				if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
					PeriodIdsList.each{ Period ->
						if(Period.equals(customTimePeriodEntry.getKey())){
							Periodid=Period;
						}
					}
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
					EmployeeStateInsurance = periodTotals.get("PAYROL_DD_ESI_DED");
					if(UtilValidate.isNotEmpty(EmployeeStateInsurance)){
						basic = periodTotals.get("PAYROL_BEN_SALARY");
						if(UtilValidate.isEmpty(basic)){
							basic = 0;
						}
						if(basic>=15000){
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
							others = basic+dearnessAllowance+houseRentAllowance+cityComp+HeatAllowance+CashAllowance+coldAllowance+convey+ShiftAllowance;
							Wages = others+CanteenAllowance+attendanceBonus+FieldAllowance+SpecialPay+GeneralHolidayWages+SecondSaturdayWages;
							
							if(UtilValidate.isNotEmpty(periodBillingIdMap)){
								Iterator periodBillingIdMapIter = periodBillingIdMap.entrySet().iterator();
								while(periodBillingIdMapIter.hasNext()){
									Map.Entry periodBillingIdMapEntry = periodBillingIdMapIter.next();
									if((periodBillingIdMapEntry.getKey()).equals(Periodid)){
										periodBillingId=periodBillingIdMapEntry.getValue();
									}
								}
							}
							List headerConditionList=[];
							headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
							headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
							headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
							headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
							if(UtilValidate.isNotEmpty(headerIdsList)){
								headerIdsList = EntityUtil.getFirst(headerIdsList);
								headerId = headerIdsList.payrollHeaderId;
							}
							
							emplyeContrbtn=0;
							List employeeConditionList=[];
							employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
							employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_ESI_DED"));
							employeeCondition = EntityCondition.makeCondition(employeeConditionList,EntityOperator.AND);
							employeeList = delegator.findList("PayrollHeaderItem", employeeCondition, null, null, null, false);
							if(UtilValidate.isNotEmpty(employeeList)){
								employeeList = EntityUtil.getFirst(employeeList);
								emplyeContrbtn = employeeList.amount;
							}
							
							List conditionList=[];
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
							conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.NOT_EQUAL, BigDecimal.ZERO)));
							conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, Periodid));
							condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
							attendanceDetails = delegator.findList("PayrollAttendance", condition , null, null, null, false);
							if(UtilValidate.isNotEmpty(attendanceDetails)){
								attendanceDetails.each{ emplAttendance ->
									payableDays = emplAttendance.get("noOfPayableDays");
									if(payableDays!=0){
										totpayableDays=totpayableDays+payableDays;
									}
								}
							}
							if(Wages!=0){
								totWages=totWages+Wages;
								totalWages=totalWages+Wages;
							}
							if(emplyeContrbtn!=0){
								employeeContributn=employeeContributn+emplyeContrbtn;
							}
							detailsMap.put("Wages",Wages);
							periodTotalsMap.put(Periodid,detailsMap);
							periodList.add(Periodid);
						}
					}
				}
			}
		}
		String partyName = PartyHelper.getPartyName(delegator, employeeId, false);
		EmplNameMap.put(employeeId,partyName);
		periodTotalsMap.put("totpayableDays",totpayableDays);
		if(totWages!=0){
			periodTotalsMap.put("totWages",totWages);
			periodTotalsMap.put("employeeContributn",employeeContributn);
			if(UtilValidate.isNotEmpty(periodTotalsMap)){
				EmplWiseDetailsMap.put(employeeId,periodTotalsMap);
			}
		}
	}
}
if(totalWages!=0){
	EmplWiseDetailsMap.put("totalWages",totalWages);
}

periodList = (new HashSet(periodList)).toList();
context.put("periodList",periodList);

periodList = (new HashSet(periodList)).toList();
context.put("periodList",periodList);

periodMap=[:];
periodList.each{ Period ->
	customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", Period), false);
	if(UtilValidate.isNotEmpty(customTimePeriod)){
		monthStart=customTimePeriod.get("fromDate");
		monthEnd=customTimePeriod.get("thruDate");
		monthStart = UtilDateTime.toDateString(monthStart);
		def sdf1 = new SimpleDateFormat("MM/dd/yyyy");
		try {
			if (monthStart) {
				dateTimestamp = new java.sql.Timestamp(sdf1.parse(monthStart).getTime());
				dateStr = UtilDateTime.toDateString(dateTimestamp,"MMMM,yyyy");
			}
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + e, "");
			context.errorMessage = "Cannot parse date string: " + e;
			return;
		}
	}
	periodMap.put(Period,dateStr);
}

totemployeeContribtn=0;
totemployerContribtn=0;
totalContribution=0;
periodTotMap=[:];
contributionMap=[:];
periodList.each{ Period ->
	totWages=0;
	employeeContribtn=0;
	employerContribtn=0;
	TotContribtn=0;
	totValueMap=[:];
	if(UtilValidate.isNotEmpty(employmentsList)){
		employmentsList.each{ employeeId ->
			if(UtilValidate.isNotEmpty(EmplWiseDetailsMap)){
				Iterator EmplIter = EmplWiseDetailsMap.entrySet().iterator();
				while(EmplIter.hasNext()){
					Map.Entry EmplEntry = EmplIter.next();
					if((EmplEntry.getKey()).equals(employeeId)){
						emplValues=EmplEntry.getValue();
						if(UtilValidate.isNotEmpty(emplValues)){
							Iterator EmplPeriodIter = emplValues.entrySet().iterator();
							while(EmplPeriodIter.hasNext()){
								Map.Entry EmplPeriodEntry = EmplPeriodIter.next();
								if((EmplPeriodEntry.getKey()).equals(Period)){
									if(!(EmplPeriodEntry.getValue().get("Wages")).equals(null)){
										totWages=totWages+EmplPeriodEntry.getValue().get("Wages");
									}
									if(UtilValidate.isNotEmpty(periodBillingIdMap)){
										Iterator periodBillingIdMapIter = periodBillingIdMap.entrySet().iterator();
										while(periodBillingIdMapIter.hasNext()){
											Map.Entry periodBillingIdMapEntry = periodBillingIdMapIter.next();
											if((periodBillingIdMapEntry.getKey()).equals(Period)){
												BillingId=periodBillingIdMapEntry.getValue();
											}
										}
									}
									List PayrollHeaderConditionList=[];
									PayrollHeaderConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, BillingId));
									PayrollHeaderConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
									PayrollHeaderCondition = EntityCondition.makeCondition(PayrollHeaderConditionList,EntityOperator.AND);
									headerIdList = delegator.findList("PayrollHeader", PayrollHeaderCondition, null, null, null, false);
									if(UtilValidate.isNotEmpty(headerIdList)){
										headerIdList = EntityUtil.getFirst(headerIdList);
										PayrollHeaderId = headerIdList.payrollHeaderId;
									}
									
									emplyeContrbtn=0;
									emplyrContrbtn=0;
									List emplyeConditionList=[];
									emplyeConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, PayrollHeaderId));
									emplyeConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_ESI_DED"));
									emplyeCondition = EntityCondition.makeCondition(emplyeConditionList,EntityOperator.AND);
									emplyeList = delegator.findList("PayrollHeaderItem", emplyeCondition, null, null, null, false);
									if(UtilValidate.isNotEmpty(emplyeList)){
										emplyeList = EntityUtil.getFirst(emplyeList);
										emplyeCont = emplyeList.amount;
										employeeContribtn=employeeContribtn+emplyeCont;
										totalContribution=totalContribution+emplyeCont;
									}
									List emplyrConditionList=[];
									emplyrConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, PayrollHeaderId));
									emplyrConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_ESI_DED"));
									emplyrCondition = EntityCondition.makeCondition(emplyrConditionList,EntityOperator.AND);
									emplyrList = delegator.findList("PayrollHeaderItemEc", emplyrCondition, null, null, null, false);
									if(UtilValidate.isNotEmpty(emplyrList)){
										emplyrList = EntityUtil.getFirst(emplyrList);
										employerCont = emplyrList.amount;
										employerContribtn=employerContribtn+employerCont;
										totalContribution=totalContribution+employerCont;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	if(UtilValidate.isNotEmpty(periodMap)){
		Iterator periodMapIter = periodMap.entrySet().iterator();
		while(periodMapIter.hasNext()){
			Map.Entry periodMapEntry = periodMapIter.next();
			if((periodMapEntry.getKey()).equals(Period)){
				periodId=periodMapEntry.getValue();
			}
		}
	}
	TotContribtn=employeeContribtn+employerContribtn;
	totemployeeContribtn=totemployeeContribtn+employeeContribtn;
	totemployerContribtn=totemployerContribtn+employerContribtn;
	if(UtilValidate.isNotEmpty(TotContribtn)){
		periodTotMap.put(periodId,TotContribtn);
	}
}

contributionMap.put("totemployeeContribtn",totemployeeContribtn);
contributionMap.put("totemployerContribtn",totemployerContribtn);
contributionMap.put("totalContribution",totalContribution);
periodTotMap.put("Contribution",contributionMap);
context.put("periodTotMap",periodTotMap);
context.put("EmplNameMap",EmplNameMap);
context.put("EmplWiseDetailsMap",EmplWiseDetailsMap);

