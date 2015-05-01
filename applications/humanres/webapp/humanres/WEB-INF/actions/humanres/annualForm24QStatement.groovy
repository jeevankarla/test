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

finalList =[];
if(reportType=="form24Q"){

	dctx = dispatcher.getDispatchContext();
	
	fromMonth=parameters.fromMonth;
	if(UtilValidate.isEmpty(fromMonth)){
		Debug.logError("Month Cannot Be Empty","");
		context.errorMessage = "Month Cannot Be Empty";
		return;
	}
	thruMonth=parameters.thruMonth;
	if(UtilValidate.isEmpty(thruMonth)){
		Debug.logError("Month Cannot Be Empty","");
		context.errorMessage = "Month Cannot Be Empty";
		return;
	}
	
	def sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
	try {
		fromMonthTime = new java.sql.Timestamp(sdf.parse("01-"+ fromMonth +" 00:00:00").getTime());
		thruMonthTime = new java.sql.Timestamp(sdf.parse("01-"+ thruMonth +" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: ", "");
	}
	
	locale = Locale.getDefault();
	timeZone = TimeZone.getDefault();
	
	Timestamp monthBegin = UtilDateTime.getMonthStart(fromMonthTime);
	Timestamp monthEnd = UtilDateTime.getMonthEnd(thruMonthTime, timeZone, locale);
	
	currCustomTimePeriodId = "";
	List timePeriodCondList=[];
	timePeriodCondList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_YEAR"));
	timePeriodCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthBegin)));
	timePeriodCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthEnd)));
	timePeriodCond=EntityCondition.makeCondition(timePeriodCondList,EntityOperator.AND);
	timePeriodList = delegator.findList("CustomTimePeriod", timePeriodCond , null, null, null, false );
	if(UtilValidate.isNotEmpty(timePeriodList)){
		timePeriodList = EntityUtil.getFirst(timePeriodList);
		currCustomTimePeriodId = timePeriodList.get("customTimePeriodId");
	}
	
	payrollTypeIdsList = [:];
	payrollTypesList = delegator.findList("PayrollType", null , null, null, null, false );
	if(UtilValidate.isNotEmpty(payrollTypesList)){
		payrollTypeIdsList = EntityUtil.getFieldListFromEntityList(payrollTypesList, "payrollTypeId", true);
		periodTypeIdsList = EntityUtil.getFieldListFromEntityList(payrollTypesList, "payrollTypeId", true);
	}
	
	supplyPayrollIdsMap = [:];
	if(UtilValidate.isNotEmpty(payrollTypeIdsList)){
		payrollTypeIdsList.each { payrollTypeId ->
			supplyPayrollTypeIdDetails = delegator.findOne("PayrollType", UtilMisc.toMap("payrollTypeId",payrollTypeId), false);
			if(UtilValidate.isNotEmpty(supplyPayrollTypeIdDetails)){
				periodTypeId = supplyPayrollTypeIdDetails.get("periodTypeId");
				supplyPayrollIdsMap.put(payrollTypeId, periodTypeId);
			}
		}
	}
	monthWiseBillingDetailsMap = [:];
	
	List monthConditionList=[];
	monthConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
	monthConditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
	monthConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("GENERATED","APPROVED")));
	monthConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthBegin)));
	monthConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthEnd)));
	monthCondition=EntityCondition.makeCondition(monthConditionList,EntityOperator.AND);
	monthPeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", monthCondition , null, ['-fromDate'], null, false );
	if(UtilValidate.isNotEmpty(monthPeriodList)){
		monthPeriodList.each { eachMonth ->
			customTimePeriodId = eachMonth.get("customTimePeriodId");
			billingId = eachMonth.get("periodBillingId");
			monthWiseBillingDetailsMap.put(customTimePeriodId,billingId);
		}
	}
	
	
	List currMonthKeyList = FastList.newInstance();
	tempCurrentDate =  UtilDateTime.getMonthStart(monthBegin);
	while(tempCurrentDate<= (UtilDateTime.getMonthEnd(monthEnd,timeZone, locale))){
		Timestamp dateMonthStart=UtilDateTime.getMonthStart(tempCurrentDate);
		dateMonthEnd = UtilDateTime.getMonthEnd(dateMonthStart, timeZone, locale);
		List monthsConList=[];
		monthsConList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
		monthsConList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(dateMonthStart)));
		monthsConList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(dateMonthEnd)));
		monthCond=EntityCondition.makeCondition(monthsConList,EntityOperator.AND);
		monthsList = delegator.findList("CustomTimePeriod", monthCond , null, null, null, false );
		if(UtilValidate.isNotEmpty(monthsList)){
			monthsList = EntityUtil.getFirst(monthsList);
			timePeriodId = monthsList.get("customTimePeriodId");
			currMonthKeyList.add(timePeriodId);
				
		}
		tempCurrentDate = UtilDateTime.getMonthEnd(tempCurrentDate,timeZone, locale);
		tempCurrentDate=UtilDateTime.addDaysToTimestamp(tempCurrentDate, 1);
		noofDays = currMonthKeyList.size()
	}
	context.putAt("currMonthKeyList", currMonthKeyList);
	
	sectionTypesList = [];
	form16InputTypes = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "FORM16_INPUT_TYPE") , null, null, null, false);
	if(UtilValidate.isNotEmpty(form16InputTypes)){
		sectionTypesList = EntityUtil.getFieldListFromEntityList(form16InputTypes, "enumId", true);
	}
	
	employeeIdsList = [];
	List emplConditionList=[];
	emplConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
	emplConditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
	emplCondition =EntityCondition.makeCondition(emplConditionList,EntityOperator.AND);
	def orderBy1 = UtilMisc.toList("partyIdTo");
	employementList = delegator.findList("Employment", emplCondition , null, orderBy1, null, false );
	if(UtilValidate.isNotEmpty(employementList)){
		employeeIdsList = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
	}
	sNo = 1;
	//employeeIdsList = UtilMisc.toList("6220");
	if(UtilValidate.isNotEmpty(employeeIdsList)){
		employeeIdsList.each { employee ->
			employeeDetailsMap = [:];
			finalEmplMap=[:];
			vpf = 0;
			tax = 0;
			taxPay = 0;
			rentPaid = 0;
			daAmount = 0;
			actualHRA = 0;
			leastValue = 0;
			interestHBA = 0;
			GSLISAmount = 0;
			entertainAlw = 0;
			totalEarnings = 0;
			providuntFund = 0;
			rentPaidExcess = 0;
			employeeSalary = 0;
			grossTotIncome = 0;
			sec80CDedAmount =0;
			totalDedAmount = 0;
			salary40Percent = 0;
			totalConvAmount = 0;
			professionalTax = 0;
			incomeChargable = 0;
			totalTaxDeducted = 0;
			totSupplyLeaveEnc = 0;
			totalDeductableAmount = 0;
			dasupplyPayrollEarnings = 0;
			supplyPayrollTotalEarnings = 0;
			totalConveyanceTaxableAmount = 0;
			
			employeeName = "";
			gender = "";
			panNumberOfEmployee = "";
			employmentStartDate = "";
			employmentEndDate = "";
			personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employee), false);
			String age = "";
			if(UtilValidate.isNotEmpty(personDetails)){
				firstName = personDetails.get("firstName");
				lastName = personDetails.get("lastName");
				employeeName = firstName +" "+ lastName;
				genderVal = personDetails.get("gender");
				if(UtilValidate.isNotEmpty(personDetails.getDate("birthDate"))){
					ageTime = (UtilDateTime.toSqlDate(monthEnd)).getTime()- (personDetails.getDate("birthDate")).getTime();
					age = new Long((new BigDecimal((TimeUnit.MILLISECONDS.toDays(ageTime))).divide(new BigDecimal(365),0,BigDecimal.ROUND_DOWN)).toString());
					if(age >= "60"){
						gender = "S";
					}else{
						if(genderVal.equals("M")){
							gender = "G";
						}else{
							gender = "W";
						}
					}
				}
			}
			partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : employee, partyIdentificationTypeId : "PAN_NUMBER"], false);
			if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
				panNumberOfEmployee = partyIdentificationDetails.get("idValue");
			}
			List emplCondList=[];
			emplCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
			emplCondList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
			emplCondList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,employee));
			emplCond =EntityCondition.makeCondition(emplCondList,EntityOperator.AND);
			employementDetailsList = delegator.findList("Employment", emplCond , null, null, null, false );
			if(UtilValidate.isNotEmpty(employementDetailsList)){
				employementDetailsList = EntityUtil.getFirst(employementDetailsList);
				employmentStartDate= employementDetailsList.get("fromDate");
				employmentEndDate= employementDetailsList.get("thruDate");
				employmentStartDate = UtilDateTime.toDateString(employmentStartDate ,"dd/MM/yyyy");
				employmentEndDate = UtilDateTime.toDateString(employmentEndDate ,"dd/MM/yyyy");
				actualStartDate = UtilDateTime.toDateString(monthBegin ,"dd/MM/yyyy");
				actualEndDate = UtilDateTime.toDateString(monthBegin ,"dd/MM/yyyy");
				
				Timestamp employmentStartDateTime = null;
				Timestamp actualStartDateTime = null;
				Timestamp employmentEndDateTime = null;
				Timestamp actualEndDateTime = null;
				
				def sdf1 = new SimpleDateFormat("dd/MM/yyyy");
				try {
					if (employmentStartDate) {
						employmentStartDateTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(employmentStartDate).getTime()));
					}
					if (actualStartDate) {
						actualStartDateTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(actualStartDate).getTime()));
					}
					if (employmentEndDate) {
						employmentEndDateTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(employmentEndDate).getTime()));
					}
					if (actualEndDate) {
						actualEndDateTime = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(actualEndDate).getTime()));
					}
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: ", "");
				}
				
				if(employmentStartDateTime.compareTo(actualStartDateTime) < 0){	
					employmentStartDate = actualStartDate;
				}else{
					employmentStartDate = employmentStartDate;
				}
				if(UtilValidate.isNotEmpty(employmentEndDateTime)){
					if(employmentEndDateTime.compareTo(actualEndDateTime) < 0){
						employmentEndDate = actualStartDate;
					}else{
						employmentEndDate = employmentEndDate;
					}
				}
			}
			otherAlwDeductableAmount = 0;
			licAmount = 0;
			totalLICAmt = 0;
			totalSupplyPT = 0;
			totalSupplyPF = 0;
			totalSupplyHRA = 0;
			totalSupplyBASIC = 0;
			
			if(UtilValidate.isNotEmpty(supplyPayrollIdsMap)){
				Iterator supplyPayrollIdsMapIter = supplyPayrollIdsMap.entrySet().iterator();
				while(supplyPayrollIdsMapIter.hasNext()){
					Map.Entry supplyPayrollIdsMapIterEntry = supplyPayrollIdsMapIter.next();
					payrollTypeId = supplyPayrollIdsMapIterEntry.getKey();
					periodTypeId = supplyPayrollIdsMapIterEntry.getValue();
					InputMap = [:];
					InputMap.put("partyId", employee);
					InputMap.put("fromDate", monthBegin);
					InputMap.put("thruDate", monthEnd);
					InputMap.put("periodTypeId", periodTypeId);
					InputMap.put("billingTypeId", payrollTypeId);
					InputMap.put("userLogin", userLogin);
					totalgrossBenefitAmt = 0;
					supplyPayrollTotals = PayrollService.getSupplementaryPayrollTotalsForPeriod(dctx,InputMap).get("supplyPeriodTotalsForParty");
					if(UtilValidate.isNotEmpty(supplyPayrollTotals)){
						Iterator supplyPayrollTotalsIter = supplyPayrollTotals.entrySet().iterator();
						while(supplyPayrollTotalsIter.hasNext()){
							Map.Entry supplyPayrollTotalsIterEntry = supplyPayrollTotalsIter.next();
							if(supplyPayrollTotalsIterEntry.getKey() != "customTimePeriodTotals"){
								Map suppplyEachPeriodTotals = supplyPayrollTotalsIterEntry.getValue();
								if(UtilValidate.isNotEmpty(suppplyEachPeriodTotals)){
									Iterator suppplyEachPeriodTotalsIter = suppplyEachPeriodTotals.entrySet().iterator();
									while(suppplyEachPeriodTotalsIter.hasNext()){
										Map.Entry suppplyEachPeriodTotalsIterEntry = suppplyEachPeriodTotalsIter.next();
										if(UtilValidate.isNotEmpty(suppplyEachPeriodTotalsIterEntry.getValue())){
											if(!(suppplyEachPeriodTotalsIterEntry.getKey()).equals("periodTotals")){
												grossBenefitAmt = suppplyEachPeriodTotalsIterEntry.getValue().get("grossBenefitAmt");
												if(UtilValidate.isNotEmpty(grossBenefitAmt)){
													totalgrossBenefitAmt = totalgrossBenefitAmt + grossBenefitAmt;
												}
												daArrearsAmount = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_BEN_DA");
												if(UtilValidate.isNotEmpty(daArrearsAmount)){
													dasupplyPayrollEarnings = dasupplyPayrollEarnings + daArrearsAmount;
												}
												supplyHRA = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_BEN_HRA");
												supplyBASIC = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_BEN_SALARY");
												supplyLeaveEnc = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_BEN_LEAVENCAS");
												if(UtilValidate.isNotEmpty(supplyLeaveEnc)){
													totSupplyLeaveEnc = totSupplyLeaveEnc + supplyLeaveEnc;
												}
												if(UtilValidate.isNotEmpty(supplyHRA)){
													totalSupplyHRA = totalSupplyHRA + supplyHRA;
												}
												if(UtilValidate.isNotEmpty(supplyBASIC)){
													totalSupplyBASIC = totalSupplyBASIC + supplyBASIC;
												}
												supplyprofessionalTax = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_DD_EMP_PR");
												if(UtilValidate.isNotEmpty(supplyprofessionalTax)){
													totalSupplyPT = totalSupplyPT + supplyprofessionalTax;
												}
												supplyProviduntFund = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_DD_PF");
												if(UtilValidate.isNotEmpty(supplyProviduntFund)){
													totalSupplyPF = totalSupplyPF + supplyProviduntFund;
												}
											}
										}
									}
								}
							}
						}
					}
					supplyPayrollTotalEarnings = supplyPayrollTotalEarnings + totalgrossBenefitAmt;
				}
			}

			customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employee,"fromDate",monthBegin,"thruDate",monthEnd,"userLogin",userLogin)).get("periodTotalsForParty");
			if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
				Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
				while(customTimePeriodIter.hasNext()){
					Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
					if(customTimePeriodEntry.getKey() == "customTimePeriodTotals"){
						if(UtilValidate.isNotEmpty(customTimePeriodEntry.getValue())){
							totalEarnings = customTimePeriodEntry.getValue().get("grossBenefitAmt");
							basic = customTimePeriodEntry.getValue().get("PAYROL_BEN_SALARY");
							professionalTax = customTimePeriodEntry.getValue().get("PAYROL_DD_PR_TAX");
							daAmount = customTimePeriodEntry.getValue().get("PAYROL_BEN_DA");
							actualHRA = customTimePeriodEntry.getValue().get("PAYROL_BEN_HRA");
							GSLISAmount = customTimePeriodEntry.getValue().get("PAYROL_DD_GR_SAVG");
							providuntFund = customTimePeriodEntry.getValue().get("PAYROL_DD_EMP_PR");
							vpf = customTimePeriodEntry.getValue().get("PAYROL_DD_VLNT_PR");
							lic = customTimePeriodEntry.getValue().get("PAYROL_DD_LIFE_IN");
							licKmf = customTimePeriodEntry.getValue().get("PAYROL_DD_LIC_KMF");
							daAmount = customTimePeriodEntry.getValue().get("PAYROL_BEN_DA");
							totalTaxDeducted = customTimePeriodEntry.getValue().get("PAYROL_DD_INC_TAX");
							
							if(UtilValidate.isNotEmpty(actualHRA)){
								totalSupplyHRA = totalSupplyHRA + actualHRA;
							}
							if(UtilValidate.isNotEmpty(basic)){
								totalSupplyBASIC = totalSupplyBASIC + basic;
							}
							if(totalEarnings != 0){
								totalEarnings = totalEarnings + supplyPayrollTotalEarnings;
								if(totalEarnings != 0){
									employeeDetailsMap.put("totalEarnings",totalEarnings);
								}
							}
							if(UtilValidate.isNotEmpty(professionalTax)){
								professionalTax = professionalTax*(-1);
								if(professionalTax != 0){
									employeeDetailsMap.put("professionalTax",professionalTax);
								}
							}
							if(UtilValidate.isNotEmpty(lic)){
								licAmount = licAmount - lic;
							}
							if(UtilValidate.isNotEmpty(licKmf)){
								licAmount = licAmount - licKmf;
							}
							sec80cPFAmt = 0;
							if(UtilValidate.isNotEmpty(sec80CDedAmount)){
								if(UtilValidate.isNotEmpty(GSLISAmount)){
									sec80CDedAmount = sec80CDedAmount - GSLISAmount;
								}
								
								if(UtilValidate.isNotEmpty(providuntFund)){
									sec80cPFAmt = sec80cPFAmt + providuntFund;
									sec80CDedAmount = sec80CDedAmount - providuntFund;
								}
								if(UtilValidate.isNotEmpty(vpf)){
									sec80cPFAmt = sec80cPFAmt + vpf;
									sec80CDedAmount = sec80CDedAmount - vpf;
								}
								if(UtilValidate.isNotEmpty(totalSupplyPF)){
									sec80cPFAmt = sec80cPFAmt + totalSupplyPF;
									sec80CDedAmount = sec80CDedAmount - totalSupplyPF;
								}
								if(UtilValidate.isNotEmpty(totalSupplyPT)){
									sec80cPFAmt = sec80cPFAmt + totalSupplyPT;
									sec80CDedAmount = sec80CDedAmount - totalSupplyPT;
								}
								if(licAmount != 0){
									totalLICAmt = totalLICAmt + licAmount;
									sec80CDedAmount = sec80CDedAmount + licAmount;
								}
							}
							if(totalSupplyBASIC != 0){
								employeeSalary = employeeSalary + totalSupplyBASIC;
								if(UtilValidate.isNotEmpty(dasupplyPayrollEarnings)){
									employeeSalary = employeeSalary + dasupplyPayrollEarnings;
								}
								if(UtilValidate.isNotEmpty(daAmount)){
									employeeSalary = employeeSalary + daAmount;
								}
								if(UtilValidate.isNotEmpty(totSupplyLeaveEnc)){
									employeeSalary = employeeSalary + totSupplyLeaveEnc;
								}
							}
						}
						
					}
				}
			}
			
			if(UtilValidate.isNotEmpty(sectionTypesList)){
				sectionTypesList.each { sectionType ->
					sectionDetailsMap = [:];
					List employeeSectionList=[];
					employeeSectionList.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employee));
					employeeSectionList.add(EntityCondition.makeCondition("sectionTypeId", EntityOperator.EQUALS, sectionType));
					employeeSectionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, currCustomTimePeriodId));
					sectionCondition=EntityCondition.makeCondition(employeeSectionList,EntityOperator.AND);
					sectionList = delegator.findList("EmployeeForm16Detail", sectionCondition , null, null, null, false );
					if(UtilValidate.isNotEmpty(sectionList)){
						sectionList.each { section ->
							grossAmount = 0;
							qualifyingAmount = 0;
							deductableAmount =0;
							sectionId = section.get("sectionTypeId");
							grossAmount = section.get("grossAmount");
							qualifyingAmount = section.get("qualifyingAmount");
							deductableAmount = section.get("deductableAmount");
							if(UtilValidate.isEmpty(grossAmount)){
								grossAmount = 0;
							}
							if(UtilValidate.isEmpty(qualifyingAmount)){
								qualifyingAmount = 0;
							}
							if(UtilValidate.isEmpty(deductableAmount)){
								deductableAmount = 0;
							}
							
							if(sectionId.equals("SECTION_80C")){
								if(UtilValidate.isNotEmpty(sec80CDedAmount)){
									if(UtilValidate.isNotEmpty(deductableAmount)){
										sec80CDedAmount = sec80CDedAmount + deductableAmount;
									}
								}
							}
							if(sectionId.equals("SECTION_80CCC")){
								if(UtilValidate.isNotEmpty(sec80CDedAmount)){
									if(UtilValidate.isNotEmpty(deductableAmount)){
										sec80CDedAmount = sec80CDedAmount + deductableAmount;
									}
								}
							}
							if(sectionId.equals("SECTION_80CCD")){
								if(UtilValidate.isNotEmpty(sec80CDedAmount)){
									if(UtilValidate.isNotEmpty(deductableAmount)){
										sec80CDedAmount = sec80CDedAmount + deductableAmount;
									}
								}
							}
							
							subSectionDetailsList = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, sectionId) , null, null, null, false);
							if(UtilValidate.isNotEmpty(subSectionDetailsList)){
								subSectionDetailsList.each { subSection ->
									subSectionId = subSection.get("enumId");
									List employeeSectionList1=[];
									employeeSectionList1.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employee));
									employeeSectionList1.add(EntityCondition.makeCondition("sectionTypeId", EntityOperator.EQUALS, subSectionId));
									employeeSectionList1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, currCustomTimePeriodId));
									sectionCondition1=EntityCondition.makeCondition(employeeSectionList1,EntityOperator.AND);
									subSectionAmountList = delegator.findList("EmployeeForm16Detail", sectionCondition1 , null, null, null, false );
									if(UtilValidate.isNotEmpty(subSectionAmountList)){
										subSectionAmountList = EntityUtil.getFirst(subSectionAmountList);
										subGrossAmount = subSectionAmountList.get("grossAmount");
										subQualifyingAmount = subSectionAmountList.get("qualifyingAmount");
										subDeductableAmount = subSectionAmountList.get("deductableAmount");
										if(UtilValidate.isNotEmpty(subGrossAmount)){
											grossAmount = grossAmount + subGrossAmount;
										}
										if(UtilValidate.isNotEmpty(subQualifyingAmount)){
											qualifyingAmount = qualifyingAmount + subQualifyingAmount;
										}
										if(UtilValidate.isNotEmpty(subDeductableAmount)){
											sec80CDedAmount = sec80CDedAmount + subDeductableAmount;
										}
										if(subSectionId.equals("LIC_POLICY")){
											if(UtilValidate.isNotEmpty(subDeductableAmount)){
												totalLICAmt = totalLICAmt + subDeductableAmount;
												subDeductableAmount = subDeductableAmount + licAmount;
											}
										}
									}
								}
							}
							if(sectionId.equals("RENT_PAID")){
								rentPaid = section.get("deductableAmount");
							}else{
								if(sectionId.equals("SECTION_80C") || sectionId.equals("SECTION_80CCC") || sectionId.equals("SECTION_80CCD")){
									
								}else{
									if(sectionId.equals("INTEREST_HBA_24B") || sectionId.equals("OTHER_ALW")){
										if(sectionId.equals("OTHER_ALW")){
											otherAlwDeductableAmount = otherAlwDeductableAmount + deductableAmount;
										}
										if(sectionId.equals("INTEREST_HBA_24B")){
											if(UtilValidate.isNotEmpty(deductableAmount)){
												interestHBA = deductableAmount;
											}
										}
									}else{
										if(UtilValidate.isNotEmpty(deductableAmount)){
											totalDeductableAmount = totalDeductableAmount + deductableAmount;
										}
									}
								}
							}
						}
					}else{
						grossAmount = 0;
						qualifyingAmount = 0;
						deductableAmount =0;
						if(sectionType.equals("SECTION_80C") || sectionType.equals("SECTION_80CCC") || sectionType.equals("SECTION_80CCD")){
						
						}else{
							if(sectionType.equals("INTEREST_HBA_24B")){
								if(UtilValidate.isNotEmpty(deductableAmount)){
									interestHBA = deductableAmount;
								}
							}else{
								if(UtilValidate.isNotEmpty(deductableAmount)){
									totalDeductableAmount = totalDeductableAmount + deductableAmount;
								}
							}
						}
					}
				}
			}
			
			if(UtilValidate.isNotEmpty(sec80CDedAmount)){
				if(sec80CDedAmount > 150000){
					sec80CDedAmount = 150000;
				}
				if(sec80CDedAmount != 0){
					employeeDetailsMap.put("total9Aamount",sec80CDedAmount);
				}
			}
			if(UtilValidate.isNotEmpty(totalDeductableAmount)){
				if(totalDeductableAmount != 0){
					employeeDetailsMap.put("total9Bamount",totalDeductableAmount);
				}
				totalDedAmount = totalDeductableAmount + sec80CDedAmount;
				if(totalDedAmount != 0){
					employeeDetailsMap.put("totalDedAmount",totalDedAmount);
				}
			}
			
			if(UtilValidate.isNotEmpty(currMonthKeyList)){
				currMonthKeyList.each { currMonth ->
					periodBilId = "";
					if(UtilValidate.isNotEmpty(monthWiseBillingDetailsMap)){
						periodBilId = monthWiseBillingDetailsMap.get(currMonth);
					}
					List payrollHeaderItemList=[];
					payrollHeaderItemList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employee));
					payrollHeaderItemList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBilId));
					payrollHeaderItemList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.IN,UtilMisc.toList("PAYROL_DD_INC_TAX","PAYROL_BEN_CONVEY")));
					headerItemCondition=EntityCondition.makeCondition(payrollHeaderItemList,EntityOperator.AND);
					monthlyIncomeTaxList = delegator.findList("PayrollHeaderAndHeaderItem", headerItemCondition , null, null, null, false );
					if(UtilValidate.isNotEmpty(monthlyIncomeTaxList)){
						monthlyIncomeTaxList.each { eachMonth ->
							if((eachMonth.get("payrollHeaderItemTypeId")).equals("PAYROL_DD_INC_TAX")){
								taxAmount = eachMonth.get("amount");
							}else{
								conveyanceAlw = eachMonth.get("amount");
								if(UtilValidate.isNotEmpty(conveyanceAlw)){
									totalConvAmount = totalConvAmount + conveyanceAlw;
									if(conveyanceAlw > 800){
										taxableAmount = conveyanceAlw - 800;
										totalConveyanceTaxableAmount = totalConveyanceTaxableAmount + taxableAmount;
									}
								}
							}
						}
					}
				}
			}
			
			conveyAlw = totalConvAmount - totalConveyanceTaxableAmount;
			totalExtentAlw = 0;
			if(UtilValidate.isNotEmpty(rentPaid)){
				if(employeeSalary != 0){
					salary10Percent = employeeSalary * (0.1);
					salary40Percent = employeeSalary * (0.4);
					if(rentPaid > salary10Percent){
						if(UtilValidate.isNotEmpty(salary10Percent)){
							rentPaidExcess = rentPaid - salary10Percent;
						}
					}
				}
				if(salary40Percent < totalSupplyHRA){
					if(UtilValidate.isNotEmpty(salary40Percent)){
						leastValue = salary40Percent;
					}
				}else{
					if(UtilValidate.isNotEmpty(totalSupplyHRA)){
						leastValue = totalSupplyHRA;
					}
				}
				if(leastValue < rentPaidExcess){
					leastValue = leastValue;
				}else{
					if(UtilValidate.isNotEmpty(rentPaidExcess)){
						leastValue = rentPaidExcess;
					}
				}
			}else{
				leastValue = 0;
			}
			if(UtilValidate.isNotEmpty(conveyAlw)){
				totalExtentAlw = leastValue + conveyAlw;
			}
			if(UtilValidate.isNotEmpty(otherAlwDeductableAmount)){
				totalExtentAlw = totalExtentAlw + otherAlwDeductableAmount;
			}
			balance = totalEarnings - totalExtentAlw;
			if(UtilValidate.isNotEmpty(professionalTax)){
				incomeChargable = balance - professionalTax;
			}else{
				incomeChargable = balance;
			}
			
			BigDecimal incChargable = new BigDecimal(incomeChargable);
			incChargable=incChargable.setScale(0, BigDecimal.ROUND_HALF_UP);
			
			if(UtilValidate.isNotEmpty(interestHBA)){
				if(interestHBA > 200000){
					interestHBA = 200000;
				}
				if(interestHBA != 0){
					employeeDetailsMap.put("interestHBA",interestHBA);
				}
				grossTotIncome = incomeChargable - interestHBA;
			}
			
			BigDecimal grossTotInc = new BigDecimal(grossTotIncome);
			grossTotInc=grossTotInc.setScale(0, BigDecimal.ROUND_HALF_UP);
			
			
			totalIncome = grossTotIncome - totalDedAmount;
			if(totalIncome < 0){
				totalIncome = 0;
			}
			
			BigDecimal totInc = new BigDecimal(totalIncome);
			totInc=totInc.setScale(0, BigDecimal.ROUND_HALF_UP);
			
			
			BigDecimal totIncome = new BigDecimal(totalIncome);
			
			currDayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			List employeeTaxSlabList=[];
			employeeTaxSlabList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, currCustomTimePeriodId));
			employeeTaxSlabList.add(EntityCondition.makeCondition("age", EntityOperator.EQUALS, "60"));
			if(age < "60"){
				employeeTaxSlabList.add(EntityCondition.makeCondition("operatorEnumId", EntityOperator.EQUALS, "PRC_LT"));
			}else{
				employeeTaxSlabList.add(EntityCondition.makeCondition("operatorEnumId", EntityOperator.EQUALS, "PRC_GTE"));
			}
			employeeTaxSlabList.add(EntityCondition.makeCondition("totalIncomeFrom", EntityOperator.LESS_THAN_EQUAL_TO, totIncome));
			employeeTaxSlabList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("totalIncomeTo", EntityOperator.GREATER_THAN_EQUAL_TO, totIncome), EntityOperator.OR,
				EntityCondition.makeCondition("totalIncomeTo", EntityOperator.EQUALS, null)));
			taxSlabCondition=EntityCondition.makeCondition(employeeTaxSlabList,EntityOperator.AND);
			employeeTaxList = delegator.findList("TaxSlabs", taxSlabCondition , null, null, null, false );
			if(UtilValidate.isNotEmpty(employeeTaxList)){
				employeeTaxList = EntityUtil.getFirst(employeeTaxList);
				if(UtilValidate.isNotEmpty(employeeTaxList)){
					taxPercentage = employeeTaxList.get("taxPercentage");
					refundAmount = employeeTaxList.get("refundAmount");
					if(taxPercentage != 0){
						totalIncomeFrom = employeeTaxList.get("totalIncomeFrom");
						if(UtilValidate.isNotEmpty(totalIncomeFrom)){
							excess = totalIncome - totalIncomeFrom;
							tax = (taxPercentage/100)*excess;
						}
						if(refundAmount != 0){
							tax = tax + refundAmount;
						}
					}
				}
			}
			
			BigDecimal taxOnIncome = new BigDecimal(tax);
			taxOnIncome=taxOnIncome.setScale(0, BigDecimal.ROUND_HALF_UP);
			if(taxOnIncome != 0){
				employeeDetailsMap.put("tax",taxOnIncome);
			}
			
			rebate = 0;
			taxAfterRebate = 0;
			educationalCessAmount = 0;
			BigDecimal taxAfterRebateValue = BigDecimal.ZERO;
			if(totalIncome < 500000){
				rebate = 2000;
			}else{
				rebate = 0;
			}
			if(UtilValidate.isNotEmpty(taxOnIncome)){
				if(rebate > taxOnIncome){
					rebate = taxOnIncome;
			   }
				taxAfterRebate = taxOnIncome - rebate;
				taxAfterRebateValue = new BigDecimal(taxAfterRebate);
				taxAfterRebateValue = taxAfterRebateValue.setScale(0, BigDecimal.ROUND_HALF_UP);
				if(rebate != 0){
					employeeDetailsMap.put("rebate",rebate);
				}
				if(taxAfterRebateValue != 0){
					employeeDetailsMap.put("taxAfterRebate",taxAfterRebateValue);
				}
			}
			if(UtilValidate.isNotEmpty(taxAfterRebateValue)){
				educationalCessAmount = taxAfterRebateValue * (0.03);
				BigDecimal educationalCessAmt = new BigDecimal(educationalCessAmount);
				educationalCessAmt = educationalCessAmt.setScale(0, BigDecimal.ROUND_HALF_UP);
				if(educationalCessAmt != 0){
					employeeDetailsMap.put("educationalCessAmount",educationalCessAmt);
				}
				taxPay = taxAfterRebateValue + educationalCessAmt;
			}
			
			if(UtilValidate.isNotEmpty(totalTaxDeducted)){
				BigDecimal totalTaxDeductedatSource = new BigDecimal(totalTaxDeducted);
				totalTaxDeductedatSource = totalTaxDeductedatSource.setScale(0, BigDecimal.ROUND_HALF_UP);
				totalTaxDeductedatSource = totalTaxDeductedatSource * (-1);
				if(totalTaxDeductedatSource != 0){
					employeeDetailsMap.put("totalTaxDeductedatSource",totalTaxDeductedatSource);
				}
				taxDiff = taxPay-totalTaxDeductedatSource;
				if(taxDiff != 0){
					employeeDetailsMap.put("taxDiff",taxDiff);
				}
			}
			
			if(taxPay != 0){
				employeeDetailsMap.put("taxPay",taxPay);
			}
			if(entertainAlw != 0){
				employeeDetailsMap.put("entertainAlw",entertainAlw);
			}
			if(balance != 0){
				employeeDetailsMap.put("balance",balance);
			}
			if(incChargable != 0){
				employeeDetailsMap.put("incomeChargable",incChargable);
			}
			if(totInc != 0){
				employeeDetailsMap.put("totalIncome",totInc);
				employeeDetailsMap.put("taxableInc",totInc);
			}
			if(grossTotInc != 0){
				employeeDetailsMap.put("grossTotIncome",grossTotInc);
			}
			
			if(UtilValidate.isNotEmpty(employeeDetailsMap)){
				employeeDetailsMap.put("gender",gender);
				employeeDetailsMap.put("employeeName",employeeName);
				employeeDetailsMap.put("PANnumber",panNumberOfEmployee);
				employeeDetailsMap.put("employmentStartDate",employmentStartDate);
				employeeDetailsMap.put("employmentEndDate",employmentEndDate);
				employeeDetailsMap.put("ReportedTaxableAmount","");
				employeeDetailsMap.put("80CCGAmount","");
				employeeDetailsMap.put("Surcharge","");
				employeeDetailsMap.put("incomeTaxRelief","");
				employeeDetailsMap.put("ReportedAmountOfTax","");
				employeeDetailsMap.put("TaxDeductedatHigherRate","");
				employeeDetailsMap.put("sNo",sNo);
				employeeDetailsMap.put("employee",employee);
				employeeDetailsMap.put("panRefNo","");
				finalEmplMap.putAll(employeeDetailsMap);
				finalList.add(finalEmplMap);
				sNo = sNo +1;
			}
		}
	}
	context.finalList=finalList;
}


