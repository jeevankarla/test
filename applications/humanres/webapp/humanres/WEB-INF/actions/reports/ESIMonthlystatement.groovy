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

periodList = [];
customTimePeriodId=parameters.customTimePeriodId;
if (UtilValidate.isEmpty(customTimePeriodId)) {
	Debug.logError("customTimePeriodId cannot be empty");
	context.errorMessage = "customTimePeriodId cannot be empty";
	return;
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;
context.timePeriodEnd= timePeriodEnd;

emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}

periodBillingIdMap=[:];
List periodbillingConditionList=[];
periodbillingConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
periodbillingConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
periodbillingCondition = EntityCondition.makeCondition(periodbillingConditionList,EntityOperator.AND);
BillingList = delegator.findList("PeriodBilling", periodbillingCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(BillingList)){
	BillingList = EntityUtil.getFirst(BillingList);
	BillingId = BillingList.periodBillingId;
}

EmplWiseDetailsMap=[:];
EmplNameMap=[:];
if(UtilValidate.isNotEmpty(employmentsList)){
	employmentsList.each{ employeeId ->
		customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",timePeriodStart,"thruDate",timePeriodEnd,"userLogin",userLogin)).get("periodTotalsForParty");
		periodDetailsMap=[:];
		if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
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
							EmployeeStateInsurance = periodTotals.get("PAYROL_DD_ESI");
							if(UtilValidate.isEmpty(EmployeeStateInsurance)){
								MedicalAllowance = periodTotals.get("PAYROL_BEN_MED_ALLOW");
								if(UtilValidate.isEmpty(MedicalAllowance)){
									MedicalAllowance = 0;
								}
							}
							others = basic+dearnessAllowance+houseRentAllowance+cityComp+HeatAllowance+CashAllowance+coldAllowance+convey+ShiftAllowance;
							Wages = others+CanteenAllowance+attendanceBonus+FieldAllowance+SpecialPay+GeneralHolidayWages+SecondSaturdayWages+MedicalAllowance;
							
							List headerConditionList=[];
							headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, BillingId));
							headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
							headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
							headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
							headerId = headerIdsList.payrollHeaderId;
							
							Contribution=0;
							List employeeConditionList=[];
							employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
							employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_ESI_DED"));
							employeeCondition = EntityCondition.makeCondition(employeeConditionList,EntityOperator.AND);
							employeeList = delegator.findList("PayrollHeaderItem", employeeCondition, null, null, null, false);
							if(UtilValidate.isNotEmpty(employeeList)){
								employeeList.each{empl ->
									Contribution = empl.amount;
								}
							}
							Contribution=Contribution;
							List conditionList=[];
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
							conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.NOT_EQUAL, BigDecimal.ZERO)));
							conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
							condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
							attendanceDetails = delegator.findList("PayrollAttendance", condition , null, null, null, false);
							if(UtilValidate.isNotEmpty(attendanceDetails)){
								attendanceDetails.each{ emplAttendance ->
									payableDays = emplAttendance.get("noOfPayableDays");
									detailsMap.put("payableDays",payableDays);
								}
							}
							detailsMap.put("Wages",Wages);
							detailsMap.put("Contribution",Contribution);
							detailsMap.put("employeeId",employeeId);
							if(UtilValidate.isNotEmpty(detailsMap)){
								periodDetailsMap.put(customTimePeriodId,detailsMap);
							}
							if(UtilValidate.isNotEmpty(periodDetailsMap)){
								periodList.addAll(customTimePeriodId);
							}
						}
					}
				}
			}
		}
		String partyName = PartyHelper.getPartyName(delegator, employeeId, false);
		EmplNameMap.put(employeeId,partyName);
		if(UtilValidate.isNotEmpty(periodDetailsMap)){
			EmplWiseDetailsMap.put(employeeId,periodDetailsMap);
		}
	}
}


periodList = (new HashSet(periodList)).toList();
context.put("periodList",periodList);

context.put("EmplNameMap",EmplNameMap);
context.put("EmplWiseDetailsMap",EmplWiseDetailsMap);
