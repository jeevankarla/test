import java.util.*;
import java.lang.*;
import java.sql.*;
import java.util.Calendar;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
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
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);

List quarterPeriodIdsList=[];
List periodConditionList=[];
periodConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_QUARTER"));
periodConditionList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.LESS_THAN_EQUAL_TO, parameters.customTimePeriodId));
periodCondition=EntityCondition.makeCondition(periodConditionList,EntityOperator.AND);
def orderBy = UtilMisc.toList("fromDate");
quarterlyCustomTimePeriodList = delegator.findList("CustomTimePeriod", periodCondition , null, orderBy, null, false );
if(UtilValidate.isNotEmpty(quarterlyCustomTimePeriodList)){
	quarterlyCustomTimePeriodList.each { period ->
		quarterPeriodId = period.get("customTimePeriodId");
		quarterPeriodIdsList.add(quarterPeriodId);
	}
}

monthWiseBillingDetailsMap = [:];
quarterBillingMap = [:];
if(UtilValidate.isNotEmpty(quarterPeriodIdsList)){
	quarterPeriodIdsList.each { quarterId ->
		List quarterConditionList=[];
		quarterConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_QUARTER"));
		quarterConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, quarterId));
		periodCondition=EntityCondition.makeCondition(quarterConditionList,EntityOperator.AND);
		quarterlyPeriodList = delegator.findList("CustomTimePeriod", periodCondition , null, null, null, false );
		if(UtilValidate.isNotEmpty(quarterlyPeriodList)){
			quarterlyPeriodList = EntityUtil.getFirst(quarterlyPeriodList);
			quarterFromDate = quarterlyPeriodList.get("fromDate");
			fromDate=UtilDateTime.toTimestamp(quarterFromDate);
			Timestamp monthStart=UtilDateTime.getMonthStart(fromDate);
			monthEnd = UtilDateTime.getMonthEnd(monthStart, timeZone, locale);
			billingMap = [:];
			for(i=0 ; i<3 ; i++){
				List monthConditionList=[];
				monthConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
				monthConditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
				monthConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("GENERATED","APPROVED")));
				monthConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthStart)));
				monthConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthEnd)));
				monthCondition=EntityCondition.makeCondition(monthConditionList,EntityOperator.AND);
				monthPeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", monthCondition , null, null, null, false );
				if(UtilValidate.isNotEmpty(monthPeriodList)){
					monthPeriodList = EntityUtil.getFirst(monthPeriodList);
					customTimePeriodId = monthPeriodList.get("customTimePeriodId");
					billingId = monthPeriodList.get("periodBillingId");
					billingMap.put(billingId,billingId);
					monthWiseBillingDetailsMap.put(customTimePeriodId,billingId);
				}
				nextMonth = UtilDateTime.addDaysToTimestamp(monthEnd, 1);
				monthStart=UtilDateTime.getMonthStart(nextMonth);
				monthEnd = UtilDateTime.getMonthEnd(monthStart, timeZone, locale);
			}
			quarterBillingMap.put(quarterId,billingMap);
		}
	}
}

List currMonthKeyList = FastList.newInstance();
tempCurrentDate =  UtilDateTime.getMonthStart(fromDateStart);
while(tempCurrentDate<= (UtilDateTime.getMonthEnd(thruDateEnd,timeZone, locale))){
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


acknowlemntMap = [:];
iteration =0;
if(UtilValidate.isNotEmpty(quarterPeriodIdsList)){
	quarterPeriodIdsList.each { periodId ->
		iteration = iteration + 1;
		quarterName = "quarter" + iteration;
		List ConditionList1=[];
		ConditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
		ConditionList1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, periodId));
		Condition1 =EntityCondition.makeCondition(ConditionList1,EntityOperator.AND);
		acknowledmntList = delegator.findList("TDSRemittances", Condition1 , null, null, null, false );
		if(UtilValidate.isNotEmpty(acknowledmntList)){
			acknowledmntList = EntityUtil.getFirst(acknowledmntList);
			acknowlemntMap.put(quarterName,acknowledmntList.get("ReceiptNumber"));
		}
	}
}

employeeIdsList = [];
if(UtilValidate.isNotEmpty(parameters.employeeId)){
	employeeIdsList.add(parameters.employeeId);
}else{
	emplInputMap = [:];
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("orgPartyId", parameters.partyIdFrom);
	emplInputMap.put("fromDate", fromDateStart);
	emplInputMap.put("thruDate", thruDateEnd);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	employments=EmploymentsMap.get("employementList");
	employementList = EntityUtil.orderBy(employments, UtilMisc.toList("partyIdTo"));
	employeeIdsList = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
}

sectionTypesList = [];
form16InputTypes = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "FORM16_INPUT_TYPE") , null, null, null, false);
if(UtilValidate.isNotEmpty(form16InputTypes)){
	sectionTypesList = EntityUtil.getFieldListFromEntityList(form16InputTypes, "enumId", true);
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

finalEmployeeMap = [:];
employeeSectionMap = [:];
employeewiseQuarterlyTaxMap = [:];
monthWiseTaxDepositedMap = [:];
if(UtilValidate.isNotEmpty(employeeIdsList)){
	employeeIdsList.each { employee ->
		totalDeductableAmount = 0;
		rentPaidExcess = 0;
		employeeSalary = 0;
		rentPaid = 0;
		actualHRA = 0;
		leastValue = 0;
		salary40Percent = 0;
		totalEarnings = 0;
		aggregate = 0;
		income= 0;
		totalIncome = 0;
		tax = 0;
		employeeName = "";
		employeeDetailsMap = [:];
		personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employee), false);
		if(UtilValidate.isNotEmpty(personDetails)){
			firstName = personDetails.get("firstName");
			lastName = personDetails.get("lastName");
			employeeName = firstName +" "+ lastName;
		}
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : employee]));
		if(UtilValidate.isNotEmpty(emplPositionAndFulfillments)){
			emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
			if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
				emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
				if(UtilValidate.isNotEmpty(emplPositionType)){
					employeePosition = emplPositionType.getString("description");
					employeeDetailsMap.put("employeePosition",employeePosition);
				}
			}
		}
		companyDetails = delegator.findOne("PartyIdentification", [partyId : parameters.partyIdFrom, partyIdentificationTypeId : "PAN_NUMBER"], false);
		if(UtilValidate.isNotEmpty(companyDetails)){
			panNumberOfCompany = companyDetails.get("idValue");
			employeeDetailsMap.put("panNumberOfCompany",panNumberOfCompany);
		}
		partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : parameters.partyIdFrom, partyIdentificationTypeId : "TAN_NUMBER"], false);
		if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
			tanNumberOfCompany = partyIdentificationDetails.get("idValue");
			employeeDetailsMap.put("tanNumberOfCompany",tanNumberOfCompany);
		}
		partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : employee, partyIdentificationTypeId : "PAN_NUMBER"], false);
		if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
			panNumberOfEmployee = partyIdentificationDetails.get("idValue");
			employeeDetailsMap.put("panNumberOfEmployee",panNumberOfEmployee);
		}
		
		subSequenceYearStart=UtilDateTime.addDaysToTimestamp(thruDateEnd, 1);
		date = UtilDateTime.toDateString(subSequenceYearStart);
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_YEAR"));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(date)));
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(date)));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		customTimePeriodList = delegator.findList("CustomTimePeriod", condition , null, null, null, false );
		if(UtilValidate.isNotEmpty(customTimePeriodList)){
			customTimePeriodList1 = EntityUtil.getFirst(customTimePeriodList);
			customTimePeriodId = customTimePeriodList1.get("customTimePeriodId");
			GenericValue subSequentPeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : customTimePeriodId], false);
			subSequentFromDate=UtilDateTime.toTimestamp(subSequentPeriod.getDate("fromDate"));
			subSequentThruDate=UtilDateTime.toTimestamp(subSequentPeriod.getDate("thruDate"));
			employeeDetailsMap.put("subSequentFromDate",subSequentFromDate);
			employeeDetailsMap.put("subSequentThruDate",subSequentThruDate);
		}else{
			subSequenceYearEnd = UtilDateTime.addDaysToTimestamp(subSequenceYearStart, 365);
			employeeDetailsMap.put("subSequentFromDate",subSequenceYearStart);
			employeeDetailsMap.put("subSequentThruDate",subSequenceYearEnd);
		}
		supplyPayrollTotalEarnings = 0;
		dasupplyPayrollEarnings = 0;
		totalSupplyPT = 0;
		totalSupplyPF = 0;
		if(UtilValidate.isNotEmpty(supplyPayrollIdsMap)){
			Iterator supplyPayrollIdsMapIter = supplyPayrollIdsMap.entrySet().iterator();
			while(supplyPayrollIdsMapIter.hasNext()){
				Map.Entry supplyPayrollIdsMapIterEntry = supplyPayrollIdsMapIter.next();
				payrollTypeId = supplyPayrollIdsMapIterEntry.getKey();
				periodTypeId = supplyPayrollIdsMapIterEntry.getValue();
				InputMap = [:];
				InputMap.put("partyId", employee);
				InputMap.put("fromDate", fromDateStart);
				InputMap.put("thruDate", thruDateEnd);
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
											if(payrollTypeId.equals("SP_DA_ARREARS")){
												daArrearsAmount = suppplyEachPeriodTotalsIterEntry.getValue().get("PAYROL_BEN_DA");
												if(UtilValidate.isNotEmpty(daArrearsAmount)){
													dasupplyPayrollEarnings = dasupplyPayrollEarnings + daArrearsAmount;
												}
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
		GSLISAmount = 0;
		providuntFund = 0;
		totalTaxDeducted = 0;
		vpf = 0;
		licAmount = 0;
		daAmount =0;
		sec80CDedAmount =0;
		totalLICAmt = 0;
		interestHBA = 0;
		customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employee,"fromDate",fromDateStart,"thruDate",thruDateEnd,"userLogin",userLogin)).get("periodTotalsForParty");
		if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
			Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
			while(customTimePeriodIter.hasNext()){
				Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
				if(customTimePeriodEntry.getKey() == "customTimePeriodTotals"){
					if(UtilValidate.isNotEmpty(customTimePeriodEntry.getValue())){
						totalEarnings = customTimePeriodEntry.getValue().get("grossBenefitAmt");
						basic = customTimePeriodEntry.getValue().get("PAYROL_BEN_SALARY");
						professionalTax = customTimePeriodEntry.getValue().get("PAYROL_DD_PR_TAX");
						actualHRA = customTimePeriodEntry.getValue().get("PAYROL_BEN_HRA");
						GSLISAmount = customTimePeriodEntry.getValue().get("PAYROL_DD_GR_SAVG");
						providuntFund = customTimePeriodEntry.getValue().get("PAYROL_DD_EMP_PR");
						vpf = customTimePeriodEntry.getValue().get("PAYROL_DD_VLNT_PR");
						lic = customTimePeriodEntry.getValue().get("PAYROL_DD_LIFE_IN");
						licKmf = customTimePeriodEntry.getValue().get("PAYROL_DD_LIC_KMF");
						daAmount = customTimePeriodEntry.getValue().get("PAYROL_BEN_DA");
						totalTaxDeducted = customTimePeriodEntry.getValue().get("PAYROL_DD_INC_TAX");
						sec80cPFAmt = 0;
						if(UtilValidate.isNotEmpty(lic)){
							licAmount = licAmount - lic;
						}
						if(UtilValidate.isNotEmpty(licKmf)){
							licAmount = licAmount - licKmf;
						}
						
						if(UtilValidate.isNotEmpty(sec80CDedAmount)){
							if(UtilValidate.isNotEmpty(GSLISAmount)){
								sec80CDedAmount = sec80CDedAmount - GSLISAmount;
								employeeDetailsMap.put("sec80cGSLISAmt",GSLISAmount);
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
						if(UtilValidate.isNotEmpty(sec80CDedAmount)){
							employeeDetailsMap.put("sec80cPFAmt",sec80cPFAmt);
						}
						if(totalEarnings != 0){
							totalEarnings = totalEarnings + supplyPayrollTotalEarnings;
							employeeDetailsMap.put("totalEarnings",totalEarnings);
						}
						if(professionalTax != 0){
							employeeDetailsMap.put("professionalTax",professionalTax);
							if(UtilValidate.isNotEmpty(professionalTax)){
								aggregate = professionalTax;
							}
						}
						if(basic != 0){
							employeeSalary = employeeSalary + basic;
							if(UtilValidate.isNotEmpty(dasupplyPayrollEarnings)){
								employeeSalary = employeeSalary + dasupplyPayrollEarnings;
							}
							if(UtilValidate.isNotEmpty(daAmount)){
								employeeSalary = employeeSalary + daAmount;
							}
						}
					}
				}
			}
		}
		List addressConditionList=[];
		addressConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee));
		addressConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		addCondition=EntityCondition.makeCondition(addressConditionList,EntityOperator.AND);
		employeeAddressList = delegator.findList("PartyAndPostalAddress", addCondition , null, null, null, false );
		if(UtilValidate.isNotEmpty(employeeAddressList)){
			employeeAddressList = EntityUtil.getFirst(employeeAddressList);
			address1 = employeeAddressList.get("address1");
			address2 = employeeAddressList.get("address2");
			employeeDetailsMap.put("address1",address1);
			employeeDetailsMap.put("address2",address2);
		}
		sectionMap = [:];
		otherAlwDeductableAmount = 0;
		if(UtilValidate.isNotEmpty(sectionTypesList)){
			sectionTypesList.each { sectionType ->
				sectionDetailsMap = [:];
				List employeeSectionList=[];
				employeeSectionList.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employee));
				employeeSectionList.add(EntityCondition.makeCondition("sectionTypeId", EntityOperator.EQUALS, sectionType));
				employeeSectionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
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
								employeeSectionList1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
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
									//Debug.log("subDeductableAmount================="+subDeductableAmount);
									Debug.log("licAmount================="+licAmount);
									if(subSectionId.equals("LIC_POLICY")){
										if(UtilValidate.isNotEmpty(subDeductableAmount)){
											totalLICAmt = totalLICAmt + subDeductableAmount;
											subDeductableAmount = subDeductableAmount + licAmount;
										}
									}
									subSectionDetailsMap = [:];
									subSectionDetailsMap.put("grossAmount",subGrossAmount);
									subSectionDetailsMap.put("qualifyingAmount",subQualifyingAmount);
									subSectionDetailsMap.put("deductableAmount",subDeductableAmount);
									sectionMap.put(subSectionId,subSectionDetailsMap);
								}
							}
						}
						if(sectionId.equals("RENT_PAID")){
							rentPaid = section.get("deductableAmount");
						}else{
							sectionDetailsMap.put("grossAmount",grossAmount);
							sectionDetailsMap.put("qualifyingAmount",qualifyingAmount);
							if(sectionId.equals("SECTION_80C") || sectionId.equals("SECTION_80CCC") || sectionId.equals("SECTION_80CCD")){
								
							}else{
								if(sectionId.equals("INTEREST_HBA_24B") || sectionId.equals("OTHER_ALW")){
									if(sectionId.equals("OTHER_ALW")){
										otherAlwDeductableAmount = otherAlwDeductableAmount + deductableAmount;
										employeeDetailsMap.put("otherAlwDeductableAmount",otherAlwDeductableAmount);
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
							sectionDetailsMap.put("deductableAmount",deductableAmount);
							sectionMap.put(sectionId,sectionDetailsMap);
						}
					}
				}else{
					grossAmount = 0;
					qualifyingAmount = 0;
					deductableAmount =0;
					sectionDetailsMap.put("grossAmount",grossAmount);
					sectionDetailsMap.put("qualifyingAmount",qualifyingAmount);
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
					sectionDetailsMap.put("deductableAmount",deductableAmount);
					sectionMap.put(sectionType,sectionDetailsMap);
				}
			}
		}
		if(UtilValidate.isNotEmpty(sectionMap)){
			employeeSectionMap.put(employee,sectionMap);
		}
		if(UtilValidate.isNotEmpty(sec80CDedAmount)){
			employeeDetailsMap.put("total9Amount",sec80CDedAmount);
		}
		if(UtilValidate.isNotEmpty(sec80CDedAmount)){
			if(sec80CDedAmount > 150000){
				totalDeductableAmount = totalDeductableAmount + 150000;
			}else{
				totalDeductableAmount = totalDeductableAmount + sec80CDedAmount;
			}
		}
		itertn = 0;
		quarterlyTaxMap = [:];
		if(UtilValidate.isNotEmpty(quarterBillingMap)){
			Iterator quarterBillingMapIter = quarterBillingMap.entrySet().iterator();
			while(quarterBillingMapIter.hasNext()){
				totalAmount = 0;
				Map.Entry BillingMapEntry = quarterBillingMapIter.next();
				billingIdsMap = BillingMapEntry.getValue();
				Iterator billingMapIter = billingIdsMap.entrySet().iterator();
				while(billingMapIter.hasNext()){
					Map.Entry periodBillingMapEntry = billingMapIter.next();
					periodBillingId = periodBillingMapEntry.getKey();
					List payrollHeaderList=[];
					payrollHeaderList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employee));
					payrollHeaderList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
					payrollHeaderList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_INC_TAX"));
					headerCondition=EntityCondition.makeCondition(payrollHeaderList,EntityOperator.AND);
					monthlyTaxList = delegator.findList("PayrollHeaderAndHeaderItem", headerCondition , null, null, null, false );
					if(UtilValidate.isNotEmpty(monthlyTaxList)){
						monthlyTaxList = EntityUtil.getFirst(monthlyTaxList);
						if(UtilValidate.isNotEmpty(monthlyTaxList)){
							amount = monthlyTaxList.get("amount");
							totalAmount = totalAmount - amount;
						}
					}
				}
				itertn = itertn + 1;
				quarterName1 = "quarter" + itertn;
				quarterlyTaxMap.put(quarterName1,totalAmount);
			}
		}
		if(UtilValidate.isNotEmpty(quarterlyTaxMap)){
			employeewiseQuarterlyTaxMap.put(employee,quarterlyTaxMap);
		}
		totalConveyanceTaxableAmount = 0;
		totalConvAmount = 0;
		monthWiseTaxMap = [:];
		conveyAlw =0;
		if(UtilValidate.isNotEmpty(currMonthKeyList)){
			currMonthKeyList.each { currMonth ->
				monthWiseDetailsMap = [:];
				GenericValue customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : currMonth], false);
				fromDateStart=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("fromDate"));
				customTimePeriodDayEnd = UtilDateTime.getMonthEnd(fromDateStart, timeZone, locale);
				monthWiseDetailsMap.put("customTimePeriodDayEnd",customTimePeriodDayEnd);
				TDSRemittancesValues = delegator.findOne("TDSRemittances", ["partyId" :"Company", "customTimePeriodId":currMonth], true);
				if(UtilValidate.isNotEmpty(TDSRemittancesValues)){
					BSRcode = TDSRemittancesValues.get("BSRcode");
					challanNumber = TDSRemittancesValues.get("challanNumber");
					monthWiseDetailsMap.put("BSRcode",BSRcode);
					monthWiseDetailsMap.put("challanNumber",challanNumber);
				}
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
							if(UtilValidate.isNotEmpty(taxAmount)){
								monthWiseDetailsMap.put("taxAmount",taxAmount);
							}
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
				monthWiseTaxMap.put(currMonth,monthWiseDetailsMap);
			}
		}
		/*DAarrearsSupplyPayrollTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employee,"fromDate",fromDateStart,"thruDate",thruDateEnd,"payrollTypesList",UtilMisc.toList("SP_DA_ARREARS"),"userLogin",userLogin)).get("periodTotalsForParty");
		if(UtilValidate.isNotEmpty(DAarrearsSupplyPayrollTotals)){
			Iterator daSupplyPayrollTotalsIter = DAarrearsSupplyPayrollTotals.entrySet().iterator();
			while(daSupplyPayrollTotalsIter.hasNext()){
				Map.Entry daSupplyPayrollTotalsIterEntry = daSupplyPayrollTotalsIter.next();
				if(daSupplyPayrollTotalsIterEntry.getKey() == "customTimePeriodTotals"){
					if(UtilValidate.isNotEmpty(daSupplyPayrollTotalsIterEntry.getValue())){
						dasupplyPayrollEarnings = daSupplyPayrollTotalsIterEntry.getValue().get("grossBenefitAmt");
						Debug.log("employeeSalary========22======="+employeeSalary);
						if(UtilValidate.isNotEmpty(dasupplyPayrollEarnings)){
							employeeSalary = employeeSalary + dasupplyPayrollEarnings;
						}
					}
				}
			}
		}*/
		conveyAlw = totalConvAmount - totalConveyanceTaxableAmount;
		if(UtilValidate.isNotEmpty(monthWiseTaxMap)){
			monthWiseTaxDepositedMap.put(employee,monthWiseTaxMap);
		}
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
			if(salary40Percent < actualHRA){
				if(UtilValidate.isNotEmpty(salary40Percent)){
					leastValue = salary40Percent;
				}
			}else{
				if(UtilValidate.isNotEmpty(actualHRA)){
					leastValue = actualHRA;
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
		if(UtilValidate.isNotEmpty(leastValue)){
			employeeDetailsMap.put("leastValue",leastValue);
		}
		totalExtentAlw = 0;
		
		if(UtilValidate.isNotEmpty(conveyAlw)){
			totalExtentAlw = leastValue + conveyAlw;
		}
		if(UtilValidate.isNotEmpty(otherAlwDeductableAmount)){
			totalExtentAlw = totalExtentAlw + otherAlwDeductableAmount;
		}
		employeeDetailsMap.put("totalExtentAlw",totalExtentAlw);
		balance = totalEarnings - totalExtentAlw;
		if(UtilValidate.isNotEmpty(aggregate)){
			income = balance + aggregate;
		}
		grossTotIncome = 0;
		
		if(UtilValidate.isNotEmpty(interestHBA)){
			if(interestHBA > 200000){
				interestHBA = 200000;
			}
			grossTotIncome = income - interestHBA;
		}
		totalIncome = grossTotIncome - totalDeductableAmount;
		if(totalIncome < 0){
			totalIncome = 0;
		}
		BigDecimal totIncome = new BigDecimal(totalIncome);
		employeeDetails = PayrollService.getEmployeePayrollCondParms(dctx,UtilMisc.toMap("employeeId",employee,"timePeriodStart",fromDateStart,"timePeriodEnd",thruDateEnd,"userLogin",userLogin));
		if(UtilValidate.isNotEmpty(employeeDetails)){
			age = employeeDetails.get("age");
			List employeeTaxSlabList=[];
			employeeTaxSlabList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
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
		}
		taxPayable = 0;
		percentagesDetails = delegator.findOne("TDSRemittances", [partyId : "Company", customTimePeriodId : parameters.customTimePeriodId], false);
		if(UtilValidate.isNotEmpty(percentagesDetails)){
			signatoryName = percentagesDetails.get("name");
			fatherName = percentagesDetails.get("fatherName");
			designation = percentagesDetails.get("designation");
			surchargePercentage = percentagesDetails.get("surchargePercentage");
			surchargeAmount = 0;
			educationalCessAmount = 0;
			if(UtilValidate.isNotEmpty(surchargePercentage)){
				if(totalIncome > 10000000){
					surchargeAmount = (surchargePercentage/100)*tax;
				}
			}
			educationalCessPercentage = percentagesDetails.get("educationalCessPercentage");
			if(UtilValidate.isNotEmpty(educationalCessPercentage)){
				educationalCessAmount = (educationalCessPercentage/100)*tax;
			}
			taxPayable = taxPayable + surchargeAmount;
			taxPayable = taxPayable + tax;
			BigDecimal surchargeAmt = new BigDecimal(surchargeAmount);
			surchargeAmt=surchargeAmt.setScale(0, BigDecimal.ROUND_HALF_UP);
			BigDecimal educationalCessAmt = new BigDecimal(educationalCessAmount);
			educationalCessAmt=educationalCessAmt.setScale(0, BigDecimal.ROUND_HALF_UP);
			BigDecimal taxPayableAmt = new BigDecimal(taxPayable);
			taxPayableAmt=taxPayableAmt.setScale(0, BigDecimal.ROUND_HALF_UP);
			employeeDetailsMap.put("surchargeAmount",surchargeAmt);
			employeeDetailsMap.put("educationalCessAmount",educationalCessAmt);
			employeeDetailsMap.put("taxPayable",taxPayableAmt);
			employeeDetailsMap.put("signatoryName",signatoryName);
			employeeDetailsMap.put("fatherName",fatherName);
			employeeDetailsMap.put("designation",designation);
		}
		if(UtilValidate.isNotEmpty(totalTaxDeducted)){
			BigDecimal totalTaxDeductedatSource = new BigDecimal(totalTaxDeducted);
			totalTaxDeductedatSource = totalTaxDeductedatSource.setScale(0, BigDecimal.ROUND_HALF_UP);
			employeeDetailsMap.put("totalTaxDeductedatSource",totalTaxDeductedatSource);
		}
		employeeDetailsMap.put("totalLICAmt",totalLICAmt);
		employeeDetailsMap.put("employeeName",employeeName);
		employeeDetailsMap.put("balance",balance);
		employeeDetailsMap.put("aggregate",aggregate);
		employeeDetailsMap.put("income",income);
		employeeDetailsMap.put("interestHBA",interestHBA);
		employeeDetailsMap.put("totalDeductableAmount",totalDeductableAmount);
		employeeDetailsMap.put("totalIncome",totalIncome);
		BigDecimal taxOnIncome = new BigDecimal(tax);
		taxOnIncome=taxOnIncome.setScale(0, BigDecimal.ROUND_HALF_UP);
		employeeDetailsMap.put("tax",taxOnIncome);
		if(conveyAlw != 0){
			employeeDetailsMap.put("totalConveyanceTaxableAmount",conveyAlw);
		}
		if(UtilValidate.isNotEmpty(employeeDetailsMap)){
			finalEmployeeMap.put(employee,employeeDetailsMap);
		}
	}
}

context.put("currMonthKeyList",currMonthKeyList);
context.put("acknowlemntMap",acknowlemntMap);
context.put("finalEmployeeMap",finalEmployeeMap);
context.put("employeeSectionMap",employeeSectionMap);
context.put("monthWiseTaxDepositedMap",monthWiseTaxDepositedMap);
context.put("employeewiseQuarterlyTaxMap",employeewiseQuarterlyTaxMap);

