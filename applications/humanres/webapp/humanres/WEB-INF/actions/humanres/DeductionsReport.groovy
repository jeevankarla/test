import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
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
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;
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

deductionTypeList = delegator.findList("DeductionType", null, null, ["sequenceNum"], null, false);
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
if(dedTypeIds.contains(parameters.dedTypeId)){
	dedTypeIds=UtilMisc.toList(parameters.dedTypeId);
}else{
	dedTypeIds=dedTypeIds;
}
context.dedTypeIds=dedTypeIds;

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
periodbillingConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
periodbillingCondition = EntityCondition.makeCondition(periodbillingConditionList,EntityOperator.AND);
BillingList = delegator.findList("PeriodBillingAndCustomTimePeriod", periodbillingCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(BillingList)){
	BillingId = BillingList.periodBillingId;
}

Map allDeductionMap=FastMap.newInstance();
dedTypeIds.each{ dedTypeId->
	if(UtilValidate.isNotEmpty(employmentsList)){
		periodTotalsMap=[:];
		employmentsList.each{ employeeId ->
		detailsMap=[:];
		Map polacyDetailsMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(BillingId)){
				List headerConditionList=[];
				headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, BillingId));
				headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
				headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
				if(UtilValidate.isNotEmpty(headerIdsList)){
					if(dedTypeId.equals("PAYROL_DD_EPF")){
						customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",timePeriodStart,"thruDate",timePeriodEnd,"userLogin",userLogin)).get("periodTotalsForParty");
						if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
							Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
							while(customTimePeriodIter.hasNext()){
								Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
								if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
									periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
									Wages =0;
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
									detailsMap.put("Wages",Wages);
									employeeContribtn=0;
									employerContribtn=0;
									pensionAmount = 0;
									headerIdsList.each{ headerId ->
										headerId = headerId.payrollHeaderId;
										employeCont=0;
										employerCont=0;
										pension=0;
										List employeeConditionList=[];
										employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
										employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_EPF"));
										employeeCondition = EntityCondition.makeCondition(employeeConditionList,EntityOperator.AND);
										employeeList = delegator.findList("PayrollHeaderItem", employeeCondition, null, null, null, false);
										if(UtilValidate.isNotEmpty(employeeList)){
											employeeList.each{ empList ->
												employeCont = empList.amount;
												employeCont = employeCont.abs();
												employeeContribtn = employeeContribtn+employeCont;
											}
											detailsMap.put("employeeContribtn",employeeContribtn);
										}
										List emplyrConditionList=[];
										emplyrConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
										emplyrConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_PFEMPLYR"));
										emplyrCondition = EntityCondition.makeCondition(emplyrConditionList,EntityOperator.AND);
										employerList = delegator.findList("PayrollHeaderItemEc", emplyrCondition, null, null, null, false);
										if(UtilValidate.isNotEmpty(employerList)){
											employerList.each{ emplyrList ->
												employerCont = emplyrList.amount;
												employerContribtn = employerContribtn+employerCont;
											}
										}
										detailsMap.put("employerContribtn",employerContribtn);
										List pensionList=[];
										pensionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
										pensionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_PENS"));
										pensionCondition = EntityCondition.makeCondition(pensionList,EntityOperator.AND);
										pensionList = delegator.findList("PayrollHeaderItemEc", pensionCondition, null, null, null, false);
										if(UtilValidate.isNotEmpty(pensionList)){
											pensionList.each{ penList ->
												pension = penList.amount;
												pensionAmount = pensionAmount+pension;
											}
										}
										detailsMap.put("pensionAmount",pensionAmount);
									}
								}
							}
						}
					}
					accountNo = 0;
					deductionAmt=0;
					balance = 0;
					gisNo = 0;
					headerIdsList.each{ headerId ->
						headerId = headerId.payrollHeaderId;
						deductionAmount = 0;
						closingBalance = 0;
						polNo = 0;
						premium = 0;
						List deductionsList=[];
						deductionsList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
						deductionsList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
						deductionsList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, dedTypeId));
						deductionsCondition = EntityCondition.makeCondition(deductionsList,EntityOperator.AND);
						def orderBy = UtilMisc.toList("amount","partyIdFrom");
						payrollHeaderList = delegator.findList("PayrollHeaderAndHeaderItem", deductionsCondition, null, null, null, false);
						if(UtilValidate.isNotEmpty(payrollHeaderList)){
							payrollHeaderList.each{ payrollList ->
								deductionAmount = payrollList.amount;
								deductionAmount = deductionAmount.abs();
								deductionAmt = deductionAmt+deductionAmount;
							}
						}
						detailsMap.put("deductionAmt",deductionAmt);
					}
					List loanRecoveryList=[];
					loanRecoveryList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
					loanRecoveryList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, dedTypeId));
					loanRecoveryCondition = EntityCondition.makeCondition(loanRecoveryList,EntityOperator.AND);
					loansAndRecoveryList = delegator.findList("LoanAndRecoveryAndType", loanRecoveryCondition, null, null, null, false);
					if(UtilValidate.isNotEmpty(loansAndRecoveryList)){
						loansAndRecoveryList.each{ loanAndRecovery ->
							if(UtilValidate.isNotEmpty(loanAndRecovery)){
								accountNo = loanAndRecovery.loanId;
								closingBalance = loanAndRecovery.closingBalance;
								balance = balance+closingBalance;
							}
						}
					}
					detailsMap.put("accountNo",accountNo);
					detailsMap.put("balance",balance);
					List partyInsuranceConditionList=[];
					partyInsuranceConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
					partyInsuranceConditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, dedTypeId));
					partyInsuranceCondition = EntityCondition.makeCondition(partyInsuranceConditionList,EntityOperator.AND);
					PartyInsuranceList = delegator.findList("PartyInsurance", partyInsuranceCondition, null, null, null, false);
					if(UtilValidate.isNotEmpty(PartyInsuranceList)){
						PartyInsuranceList.each{ PartyInsurance ->
							if(UtilValidate.isNotEmpty(PartyInsurance)){
								polNo = PartyInsurance.insuranceNumber;
								premium = PartyInsurance.premiumAmount;
							}
							polacyDetailsMap.put(polNo, premium);
							detailsMap.put("polDetails",polacyDetailsMap);
						}
					}
					gisNoDetails = [];
					gisNoDetails = delegator.findOne("EmployeeDetail",[partyId : employeeId ], false);
					if(UtilValidate.isNotEmpty(gisNoDetails)){
						gisNum = gisNoDetails.presentEpf;
					}
					if(UtilValidate.isNotEmpty(gisNum)){
						gisNo = gisNum.trim()
					}
					detailsMap.put("gisNo",gisNo);
				}
			}
			if(UtilValidate.isNotEmpty(detailsMap)){
				periodTotalsMap.put(employeeId,detailsMap);
			}
		}
	}
	if(UtilValidate.isNotEmpty(periodTotalsMap)){
		allDeductionMap.put(dedTypeId,periodTotalsMap);
	}
}
context.put("allDeductionMap",allDeductionMap);
