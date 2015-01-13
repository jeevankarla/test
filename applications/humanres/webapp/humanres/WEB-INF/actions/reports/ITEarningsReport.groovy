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
employeeId = parameters.employeeId;

context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
context.putAt("employeeId", employeeId);

reportTypeFlag = parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="ITAXStatement"){
	customTimePeriodId=parameters.customTimePeriodId;
	List condList =[];
	condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
	condList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, null, null, false);
	if(UtilValidate.isNotEmpty(customTimePeriodList)){
		GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
		Date fromDate = (Date)customTimePeriod.get("fromDate");
		fromDateStart=UtilDateTime.toTimestamp(fromDate);
		Date thruDate = (Date)customTimePeriod.get("thruDate");
		thruDateEnd=UtilDateTime.toTimestamp(thruDate);
	}
	context.orderDate=UtilDateTime.nowTimestamp();
}

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


employmentsList = [];
if(UtilValidate.isEmpty(employeeId)){
	emplInputMap = [:];
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("orgPartyId", "Company");
	emplInputMap.put("fromDate", fromDate);
	emplInputMap.put("thruDate", thruDate);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	employments=EmploymentsMap.get("employementList");
	if(UtilValidate.isNotEmpty(employments)){
		employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
	}
}else{
	employmentsList.add(employeeId);
}

partyDeductionFinalMap = [:];
partyBenefitFinalMap = [:];

employmentsList.each{ employeeId->
	customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",fromDate,"thruDate",thruDate,"userLogin",userLogin)).get("periodTotalsForParty");
	partyBenefitsMap = [:];
	partyDeductionsMap = [:];
	if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
		Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
		while(customTimePeriodIter.hasNext()){
			Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
			if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
				Map periodTotalsMap = [:];
				periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
				if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "ITEarningsReport"){
					basic = 0;
					attendanceBonus =0;
					cityComp =0;
					convey =0;
					coldAllowance =0;
					dearnessAllowance =0;
					holidayAllowance =0;
					houseRentAllowance =0;
					personalPay =0;
					secndSatDay =0;
					shift =0;
					washing =0;
					bonus = 0;
					DADAAmount = 0;
					others = 0;
					totalBenefits = 0;
					
					LESalary = 0;
					LEDAAmount = 0;
					LEHRAAmount = 0;
					LECCAmount = 0;
					LESpecPay = 0;
					
					TESalary = 0;
					TEDAAmount = 0;
					TEHRAAmount = 0;
					TECCAmount = 0;
					TESpecPay = 0;
					
					SBESalary = 0;
					SBEDAAmount = 0;
					SBEHRAAmount = 0;
					SBECCAmount = 0;
					SBESpecPay = 0;
					
					basic = periodTotals.get("PAYROL_BEN_SALARY");
					if(UtilValidate.isEmpty(basic)){
						basic = 0;
					}
					attendanceBonus = periodTotals.get("PAYROL_BEN_ATNDBON");
					if(UtilValidate.isEmpty(attendanceBonus)){
						attendanceBonus = 0;
					}
					cityComp = periodTotals.get("PAYROL_BEN_CITYCOMP");
					if(UtilValidate.isEmpty(cityComp)){
						cityComp = 0;
					}
					convey = periodTotals.get("PAYROL_BEN_CONVEY");
					if(UtilValidate.isEmpty(convey)){
						convey = 0;
					}
					coldAllowance = periodTotals.get("PAYROL_BEN_COLDALLOW");
					if(UtilValidate.isEmpty(coldAllowance)){
						coldAllowance = 0;
					}
					dearnessAllowance = periodTotals.get("PAYROL_BEN_DA");
					if(UtilValidate.isEmpty(dearnessAllowance)){	
						dearnessAllowance = 0;
					}
					holidayAllowance = periodTotals.get("PAYROL_BEN_GEN_HOL_W");
					if(UtilValidate.isEmpty(holidayAllowance)){
						holidayAllowance = 0;
					}
					houseRentAllowance = periodTotals.get("PAYROL_BEN_HRA");
					if(UtilValidate.isEmpty(houseRentAllowance)){
						houseRentAllowance = 0;
					}
					personalPay = periodTotals.get("PAYROL_BEN_PERS_PAY");
					if(UtilValidate.isEmpty(personalPay)){
						personalPay = 0;
					}
					secndSatDay = periodTotals.get("PAYROL_BEN_SECSATDAY");
					if(UtilValidate.isEmpty(secndSatDay)){
						secndSatDay = 0;
					}
					shift = periodTotals.get("PAYROL_BEN_SHIFT");
					if(UtilValidate.isEmpty(shift)){
						shift = 0;
					}
					washing = periodTotals.get("PAYROL_BEN_WASHG");
					if(UtilValidate.isEmpty(washing)){
						washing = 0;
					}
					
					customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodEntry.getKey()] , false);
					if(UtilValidate.isNotEmpty(customTimePeriod)){
						Date monthDate = (Date)customTimePeriod.get("fromDate");
						monthDateStart = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(monthDate), timeZone, locale);
						monthDateEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(monthDate), timeZone, locale);
						//for bonus
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
						//DA Arrears
						DAArrearsPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_SDA","billingTypeId","SP_DA_ARREARS","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
						if(UtilValidate.isNotEmpty(DAArrearsPeriodTotals)){
							Iterator DAArrearsPeriodTotalsIter = DAArrearsPeriodTotals.entrySet().iterator();
							while(DAArrearsPeriodTotalsIter.hasNext()){
								Map.Entry DAArrearsEntry = DAArrearsPeriodTotalsIter.next();
								if(DAArrearsEntry.getKey() != "customTimePeriodTotals"){
									DAArrearsTotals = DAArrearsEntry.getValue().get("periodTotals");
									DADAAmount = DAArrearsTotals.get("PAYROL_BEN_DA");
									if(UtilValidate.isEmpty(DADAAmount)){
										DADAAmount = 0;
									}
								}
							}
						}
						// Leave Encashment Here
						leaveEncashPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_LEAVEENCASH","billingTypeId","SP_LEAVE_ENCASH","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
						if(UtilValidate.isNotEmpty(leaveEncashPeriodTotals)){
							Iterator leaveEncashTotalsIter = leaveEncashPeriodTotals.entrySet().iterator();
							while(leaveEncashTotalsIter.hasNext()){
								Map.Entry leaveEncashEntry = leaveEncashTotalsIter.next();
								if(leaveEncashEntry.getKey() != "customTimePeriodTotals"){
									leaveEncashTotals = leaveEncashEntry.getValue().get("periodTotals");
									LESalary = leaveEncashTotals.get("PAYROL_BEN_SALARY");
									if(UtilValidate.isEmpty(LESalary)){
										LESalary = 0;
									}
									LEHRAAmount = leaveEncashTotals.get("PAYROL_BEN_HRA");
									if(UtilValidate.isEmpty(LEHRAAmount)){
										LEHRAAmount = 0;
									}
									LEDAAmount = leaveEncashTotals.get("PAYROL_BEN_DA");
									if(UtilValidate.isEmpty(LEDAAmount)){
										LEDAAmount = 0;
									}
									LECCAmount = leaveEncashTotals.get("PAYROL_BEN_CITYCOMP");
									if(UtilValidate.isEmpty(LECCAmount)){
										LECCAmount = 0;
									}
									LESpecPay = leaveEncashTotals.get("PAYROL_BEN_SPELPAY");
									if(UtilValidate.isEmpty(LESpecPay)){
										LESpecPay = 0;
									}
								}
							}
						}
					
						transferEntryPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_TBE","billingTypeId","SP_TE","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
						if(UtilValidate.isNotEmpty(transferEntryPeriodTotals)){
							Iterator transferEntryTotalsIter = transferEntryPeriodTotals.entrySet().iterator();
							while(transferEntryTotalsIter.hasNext()){
								Map.Entry transferEntry = transferEntryTotalsIter.next();
								if(transferEntry.getKey() != "customTimePeriodTotals"){
									transferEntryTotals = transferEntry.getValue().get("periodTotals");
									TESalary = transferEntryTotals.get("PAYROL_BEN_SALARY");
									if(UtilValidate.isEmpty(TESalary)){
										TESalary = 0;
									}
									TEHRAAmount = transferEntryTotals.get("PAYROL_BEN_HRA");
									if(UtilValidate.isEmpty(TEHRAAmount)){
										TEHRAAmount = 0;
									}
									TEDAAmount = transferEntryTotals.get("PAYROL_BEN_DA");
									if(UtilValidate.isEmpty(TEDAAmount)){
										TEDAAmount = 0;
									}
									TECCAmount = transferEntryTotals.get("PAYROL_BEN_CITYCOMP");
									if(UtilValidate.isEmpty(TECCAmount)){
										TECCAmount = 0;
									}
									TESpecPay = transferEntryTotals.get("PAYROL_BEN_SPELPAY");
									if(UtilValidate.isEmpty(TESpecPay)){
										TESpecPay = 0;
									}
								}
							}
						}
						
						SBEPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_SBE","billingTypeId","SP_BE","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
						if(UtilValidate.isNotEmpty(SBEPeriodTotals)){
							Iterator SBEPeriodTotalsIter = SBEPeriodTotals.entrySet().iterator();
							while(SBEPeriodTotalsIter.hasNext()){
								Map.Entry SBEEntry = SBEPeriodTotalsIter.next();
								if(SBEEntry.getKey() != "customTimePeriodTotals"){
									SBETotals = SBEEntry.getValue().get("periodTotals");
									SBESalary = SBETotals.get("PAYROL_BEN_SALARY");
									if(UtilValidate.isEmpty(SBESalary)){
										SBESalary = 0;
									}
									SBEHRAAmount = SBETotals.get("PAYROL_BEN_HRA");
									if(UtilValidate.isEmpty(SBEHRAAmount)){
										SBEHRAAmount = 0;
									}
									SBEDAAmount = SBETotals.get("PAYROL_BEN_DA");
									if(UtilValidate.isEmpty(SBEDAAmount)){
										SBEDAAmount = 0;
									}
									SBECCAmount = SBETotals.get("PAYROL_BEN_CITYCOMP");
									if(UtilValidate.isEmpty(SBECCAmount)){
										SBECCAmount = 0;
									}
									SBESpecPay = SBETotals.get("PAYROL_BEN_SPELPAY");
									if(UtilValidate.isEmpty(SBESpecPay)){
										SBESpecPay = 0;
									}
								}
							}
						}
					}
				
					others = attendanceBonus+coldAllowance+holidayAllowance+personalPay+secndSatDay+shift;
					totalBenefits = others+basic+cityComp+convey+dearnessAllowance+houseRentAllowance+bonus+DADAAmount;
					
					tempMap = [:];
					tempMap["basic"] = basic;
					tempMap["dearnessAllowance"] = dearnessAllowance;
					tempMap["houseRentAllowance"] = houseRentAllowance;
					tempMap["convey"] = convey;
					tempMap["cityComp"] = cityComp;
					tempMap["others"] = others;
					tempMap["bonus"] = bonus;
					tempMap["DADAAmount"] = DADAAmount;
					tempMap["totalBenefits"] = totalBenefits;
					
					tempMap["LESalary"] = LESalary;
					tempMap["LEDAAmount"] = LEDAAmount;
					tempMap["LEHRAAmount"] = LEHRAAmount;
					tempMap["LECCAmount"] = LECCAmount;
					tempMap["LESpecPay"] = LESpecPay;
					
					tempMap["TESalary"] = TESalary;
					tempMap["TEDAAmount"] = TEDAAmount;
					tempMap["TEHRAAmount"] = TEHRAAmount;
					tempMap["TECCAmount"] = TECCAmount;
					tempMap["TESpecPay"] = TESpecPay;
					
					
					tempMap["SBESalary"] = SBESalary;
					tempMap["SBEDAAmount"] = SBEDAAmount;
					tempMap["SBEHRAAmount"] = SBEHRAAmount;
					tempMap["SBECCAmount"] = SBECCAmount;
					tempMap["SBESpecPay"] = SBESpecPay;
					
					benefitsMap = [:];
					if(UtilValidate.isNotEmpty(tempMap)){
						benefitsMap.putAll(tempMap);
					}
					if(UtilValidate.isNotEmpty(benefitsMap)){
						partyBenefitsMap.put(customTimePeriodEntry.getKey(),benefitsMap);
					}
				}
				
				if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "ITDeductionsReport"){
					
					epf = 0;
					vpf =0;
					gsls =0;
					licp =0;
					frf =0;
					nsc =0;
					ppf =0;
					gsas =0;
					hba =0;
					canf =0;
					hdfc =0;
					hbac =0;
					incomeTax = 0;
					prfTax = 0;
					
					totalFRFNSC = 0;
					totalPPFGSAS = 0;
					totalExterLoan = 0;
					
					totalDeductions = 0;
					
					TEEmpProFund = 0;
					TEIncTax = 0;
					
					SBEEmpProFund = 0;
					SBEIncTax = 0;
					SBEPrTax = 0;
					SBEWFTrust = 0;
					SBEInsurance = 0;
					SBEGrSav = 0;
					SBEMisDed = 0;
					
					othersDed = 0;
					
					epf = periodTotals.get("PAYROL_DD_EMP_PR");
					if(UtilValidate.isEmpty(epf)){
						epf = 0;
					}
					vpf = periodTotals.get("PAYROL_DD_VLNT_PR");
					if(UtilValidate.isEmpty(vpf)){
						vpf = 0;
					}
					gsls = periodTotals.get("PAYROL_DD_GR_SAVG");
					if(UtilValidate.isEmpty(gsls)){
						gsls = 0;
					}
					licOfPGS = periodTotals.get("PAYROL_DD_LIC_PGS");
					licKmf = periodTotals.get("PAYROL_DD_LIC_KMF");
					licDharwadMilk  = periodTotals.get("PAYROL_DD_LIC_DWD");
					rdAmount = periodTotals.get("PAYROL_DD_REC_DEP");
					lifeInsurance = periodTotals.get("PAYROL_DD_LIFE_IN");
					if(UtilValidate.isEmpty(licp) || (licp == 0)){
						if(UtilValidate.isNotEmpty(licOfPGS)){
							licp = licp + licOfPGS;
						}
						if(UtilValidate.isNotEmpty(licKmf)){
							licp = licp + licKmf;
						}
						if(UtilValidate.isNotEmpty(licDharwadMilk)){
							licp = licp + licDharwadMilk;
						}
						if(UtilValidate.isNotEmpty(rdAmount)){
							licp = licp + rdAmount;
						}
						if(UtilValidate.isNotEmpty(lifeInsurance)){
							licp = licp + lifeInsurance;
						}
					}
					frf = periodTotals.get("PAYROL_DD_FD_RELF");
					if(UtilValidate.isEmpty(frf)){
						frf = 0;
					}
					nsc = periodTotals.get("PAYROL_DD_NATINAL");
					if(UtilValidate.isEmpty(nsc)){
						nsc = 0;
					}
					ppf = periodTotals.get("PAYROL_DD_PERSNL_PR");
					if(UtilValidate.isEmpty(ppf)){
						ppf = 0;
					}
					gsas = periodTotals.get("PAYROL_DD_GR_SUP");
					if(UtilValidate.isEmpty(gsas)){
						gsas = 0;
					}
					hba = periodTotals.get("PAYROL_DD_HUSE_ADV");
					if(UtilValidate.isEmpty(hba)){
						hba = 0;
					}
					canf = periodTotals.get("PAYROL_DD_CANFN_HO");
					if(UtilValidate.isEmpty(canf)){
						canf = 0;
					}
					hdfc = periodTotals.get("PAYROL_BEN_SHIFT");
					if(UtilValidate.isEmpty(hdfc)){
						hdfc = 0;
					}
					hbac = periodTotals.get("PAYROL_DD_HUSE_B");
					if(UtilValidate.isEmpty(hbac)){
						hbac = 0;
					}
					incomeTax = periodTotals.get("PAYROL_DD_INC_TAX");
					if(UtilValidate.isEmpty(incomeTax)){
						incomeTax = 0;
					}
					prfTax = periodTotals.get("PAYROL_DD_PR_TAX");
					if(UtilValidate.isEmpty(prfTax)){
						prfTax = 0;
					}
					
					customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodEntry.getKey()] , false);
					if(UtilValidate.isNotEmpty(customTimePeriod)){
						Date monthDate = (Date)customTimePeriod.get("fromDate");
						monthDateStart = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(monthDate), timeZone, locale);
						monthDateEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(monthDate), timeZone, locale);
						//for SBE and TE Deductions
						transferEntryPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_TBE","billingTypeId","SP_TE","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
						if(UtilValidate.isNotEmpty(transferEntryPeriodTotals)){
							Iterator transferEntryTotalsIter = transferEntryPeriodTotals.entrySet().iterator();
							while(transferEntryTotalsIter.hasNext()){
								Map.Entry transferEntry = transferEntryTotalsIter.next();
								if(transferEntry.getKey() != "customTimePeriodTotals"){
									transferEntryTotals = transferEntry.getValue().get("periodTotals");
									TEEmpProFund = transferEntryTotals.get("PAYROL_DD_EMP_PR");
									if(UtilValidate.isEmpty(TEEmpProFund)){
										TEEmpProFund = 0;
									}
									TEIncTax = transferEntryTotals.get("PAYROL_DD_INC_TAX");
									if(UtilValidate.isEmpty(TEIncTax)){
										TEIncTax = 0;
									}
								}
							}
						}
						
						SBEPeriodTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",monthDateStart,"thruDate",monthDateEnd,"periodTypeId","HR_SBE","billingTypeId","SP_BE","userLogin",userLogin)).get("supplyPeriodTotalsForParty");
						if(UtilValidate.isNotEmpty(SBEPeriodTotals)){
							Iterator SBEPeriodTotalsIter = SBEPeriodTotals.entrySet().iterator();
							while(SBEPeriodTotalsIter.hasNext()){
								Map.Entry SBEEntry = SBEPeriodTotalsIter.next();
								if(SBEEntry.getKey() != "customTimePeriodTotals"){
									SBETotals = SBEEntry.getValue().get("periodTotals");
									SBEEmpProFund = transferEntryTotals.get("PAYROL_DD_EMP_PR");
									if(UtilValidate.isEmpty(SBEEmpProFund)){
										SBEEmpProFund = 0;
									}
									SBEIncTax = transferEntryTotals.get("PAYROL_DD_INC_TAX");
									if(UtilValidate.isEmpty(SBEIncTax)){
										SBEIncTax = 0;
									}
									SBEPrTax = transferEntryTotals.get("PAYROL_DD_PR_TAX");
									if(UtilValidate.isEmpty(SBEPrTax)){
										SBEPrTax = 0;
									}
									SBEWFTrust = transferEntryTotals.get("PAYROL_DD_WF_TRST");
									if(UtilValidate.isEmpty(SBEWFTrust)){
										SBEWFTrust = 0;
									}
									SBEInsurance = transferEntryTotals.get("PAYROL_DD_LIFE_IN");
									if(UtilValidate.isEmpty(SBEInsurance)){
										SBEInsurance = 0;
									}
									SBEGrSav = transferEntryTotals.get("PAYROL_DD_GR_SAVG");
									if(UtilValidate.isEmpty(SBEGrSav)){
										SBEGrSav = 0;
									}
									SBEMisDed = transferEntryTotals.get("PAYROL_DD_MISC_DED");
									if(UtilValidate.isEmpty(SBEMisDed)){
										SBEMisDed = 0;
									}
								}
							}
						}
					}
					
					
					othersDed = SBEWFTrust+SBEMisDed;
					
					totalFRFNSC = frf+nsc;
					totalPPFGSAS = ppf+gsas;
					totalExterLoan = hba+canf+hbac;
					totalDeductions = totalFRFNSC+totalPPFGSAS+totalExterLoan+epf+vpf+gsls+licp+othersDed;
					
					tempMap = [:];
					tempMap["epf"] = -(epf);
					tempMap["vpf"] = -(vpf);
					tempMap["gsls"] = -(gsls);
					tempMap["licp"] = -(licp);
					tempMap["incomeTax"] = -(incomeTax);
					tempMap["prfTax"] = -(prfTax);
					tempMap["totalFRFNSC"] = -(totalFRFNSC);
					tempMap["totalPPFGSAS"] = -(totalPPFGSAS);
					tempMap["totalExterLoan"] = -(totalExterLoan);
					tempMap["totalDeductions"] = -(totalDeductions);
					
					tempMap["TEEmpProFund"] = TEEmpProFund;
					tempMap["TEIncTax"] = TEIncTax;
					
					tempMap["SBEEmpProFund"] = SBEEmpProFund;
					tempMap["SBEIncTax"] = SBEIncTax;
					tempMap["SBEPrTax"] = SBEPrTax;
					tempMap["SBEGrSav"] = SBEGrSav;
					tempMap["SBEInsurance"] = SBEInsurance;
					tempMap["othersDed"] = othersDed;
					
					
					deductionsMap = [:];
					if(UtilValidate.isNotEmpty(tempMap)){
						deductionsMap.putAll(tempMap);
					}
					if(UtilValidate.isNotEmpty(deductionsMap)){
						partyDeductionsMap.put(customTimePeriodEntry.getKey(),deductionsMap);
					}
				}
			}
		}
	}
	if(UtilValidate.isNotEmpty(partyBenefitsMap)){
		partyBenefitFinalMap.put(employeeId,partyBenefitsMap);
	}
	if(UtilValidate.isNotEmpty(partyDeductionsMap)){
		partyDeductionFinalMap.put(employeeId,partyDeductionsMap);
	}
}
context.put("partyBenefitFinalMap",partyBenefitFinalMap);
context.put("partyDeductionFinalMap",partyDeductionFinalMap);

//IncomeTax Statement
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="ITAXStatement"){
context.fromDate=fromDateStart;
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
ITAXFinalList=[];
grandTotal=0;
employementList.each{employment->
	String lastName="";
	if(employment.lastName!=null){
		lastName=employment.lastName;
	}
	name=employment.firstName+" "+lastName;
	customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employment.partyId,"fromDate",fromDateStart,"thruDate",thruDateEnd,"userLogin",userLogin)).get("periodTotalsForParty");
	if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
		Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
		while(customTimePeriodIter.hasNext()){
			tempMap=[:];
			Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
			if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
				periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
				tempMap.put("name",name);
				tempMap.put("partyId",employment.partyId);
				panId=employment.panId;
				if(UtilValidate.isEmpty(panId)){
					panId="";
				}
				tempMap.put("panId",panId);
				incomeTax = periodTotals.get("PAYROL_DD_INC_TAX");
				if(UtilValidate.isEmpty(incomeTax)){
					incomeTax = 0;
				}
				grandTotal=grandTotal-(incomeTax);
				tempMap.put("incomeTax",-(incomeTax));
				ITAXFinalList.add(tempMap);
			}	
		}		
	}			
				
}
context.grandTotal=grandTotal;
context.ITAXFinalList=ITAXFinalList;
}



