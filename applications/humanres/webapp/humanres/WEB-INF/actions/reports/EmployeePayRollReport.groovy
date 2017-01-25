import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
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
import in.vasista.vbiz.humanres.HumanresService;

if (parameters.customTimePeriodId == null) {
	return;	
}
dctx = dispatcher.getDispatchContext();

orgPartyId = null;


if(UtilValidate.isNotEmpty(parameters.OrganizationId)){
	//parameters.partyIdFrom=parameters.OrganizationId;
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
//context.put("customTimePeriod",customTimePeriod);
context.timePeriodStart= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.timePeriodEnd= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

resultMap = PayrollService.getPayrollAttedancePeriod(dctx, [timePeriodStart:timePeriodStart, timePeriodEnd: timePeriodEnd, timePeriodId: parameters.customTimePeriodId, userLogin : userLogin]);
if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){	
	lastCloseAttedancePeriod=resultMap.get("lastCloseAttedancePeriod");
	timePeriod=lastCloseAttedancePeriod.get("customTimePeriodId");
	if(UtilValidate.isNotEmpty(parameters.billingTypeId) && ("SP_LEAVE_ENCASH".equals(parameters.billingTypeId))){
		timePeriod=parameters.customTimePeriodId;
	}
	context.timePeriod=timePeriod;
}
List stautsList = UtilMisc.toList("GENERATED","APPROVED");
conditionList=[];
if(UtilValidate.isEmpty(parameters.billingTypeId)){
	//conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , parameters.billingTypeId));
	parameters.billingTypeId = "PAYROLL_BILL";
}/*else{
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
}*/

conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , parameters.billingTypeId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,stautsList));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
if(UtilValidate.isNotEmpty(parameters.periodBillingId)){
	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,parameters.periodBillingId));
}
if(UtilValidate.isNotEmpty(parameters.partyIdFrom) && (!"Company".equals(parameters.partyIdFrom))){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyIdFrom));
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
//getting benefits
sortBy = UtilMisc.toList("sequenceNum");
//description
if(UtilValidate.isNotEmpty(context.reportFlag) && (context.reportFlag).equals("summary")){
	sortBy = UtilMisc.toList("description");
}
benefitTypeList = delegator.findList("BenefitType", null, null, sortBy, null, false);
benefitDescMap=[:];
if(UtilValidate.isNotEmpty(benefitTypeList)){
	benefitTypeList.each{ benefit->
		benefitName =  benefit.get("benefitName");
		benefitType = benefit.get("benefitTypeId");
		benefitDescMap.put(benefitType,benefitName);
	}
}
benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
context.benefitTypeIds=benefitTypeIds;
context.benefitDescMap=benefitDescMap;
//getting deductions

deductionTypeList = delegator.findList("DeductionType", null, null, sortBy, null, false);
dedDescMap=[:];
if(UtilValidate.isNotEmpty(deductionTypeList)){
	deductionTypeList.each{ deduction->
		dedName =  deduction.get("deductionName");
		dedType = deduction.get("deductionTypeId");
		dedDescMap.put(dedType,dedName);
	}
}
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
context.dedTypeIds=dedTypeIds;
context.dedDescMap=dedDescMap;
Map emplInputMap = FastMap.newInstance();
deptId=parameters.deptId;
bankAdvise_deptId=parameters.bankAdvise_deptId;
if(UtilValidate.isNotEmpty(deptId)){
	context.deptId=deptId;
	emplInputMap.put("orgPartyId", deptId);
}else if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
	emplInputMap.put("orgPartyId", parameters.partyIdFrom);
}else if(UtilValidate.isNotEmpty(bankAdvise_deptId)){
	emplInputMap.put("orgPartyId", bankAdvise_deptId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}

emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);


OrganizationId = parameters.OrganizationId;
if(OrganizationId != "Company"){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
  				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
  						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS  ,"INTERNAL_ORGANIZATIO"));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS  ,"EMPLOYEE"));
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS  ,OrganizationId));
	employementAndPerson = delegator.findList("EmploymentAndPerson", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(employementAndPerson)){
		employementIds = EntityUtil.getFieldListFromEntityList(employementAndPerson, "partyIdTo", true);
	}
}

//Debug.log("employementIds================"+employementIds);




periodBillIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
conList = [];
conList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillIds));
payrollDeductionCond = EntityCondition.makeCondition(conList,EntityOperator.AND);
payrollHeaderDeductionList = delegator.findList("PayrollHeaderAndPayrollRetention", payrollDeductionCond, null, null, null, false);
deductPayrollHeaderIdList = [];
if(payrollHeaderDeductionList){
	deductPayrollHeaderIdList = EntityUtil.getFieldListFromEntityList(payrollHeaderDeductionList, "payrollHeaderId", true);
}

Map loanBalancesMap=FastMap.newInstance();
Map payRateMap=FastMap.newInstance();
Map unitIdMap=FastMap.newInstance();
Map payRollMap=FastMap.newInstance();
Map payRollSummaryMap=FastMap.newInstance();
Map payRollEmployeeMap=FastMap.newInstance();
Map BankAdvicePayRollMap=FastMap.newInstance();
Map InstallmentFinalMap=FastMap.newInstance();
Map EmplSalaryDetailsMap=FastMap.newInstance();
Map emplAttendanceDetailsMap = FastMap.newInstance();
Timestamp basicSalDate=null;
if(UtilValidate.isNotEmpty(periodBillingList)){
	periodBillDetails = EntityUtil.getFirst(periodBillingList);
	periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	periodBillingId = periodBillDetails.get("periodBillingId");
	basicSalDate = periodBillDetails.get("basicSalDate");
	payRollHeaderList=[];
	payConList=[];
	payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingIds));
	if(UtilValidate.isNotEmpty(parameters.employeeId)){
		payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,parameters.employeeId));
	}
	if(UtilValidate.isEmpty(parameters.employeeId)){
		payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
		
		Debug.log("employementIds==============================")
		
	}
	/*if(UtilValidate.isNotEmpty(deptId)){
		payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
	}*/
	if(UtilValidate.isNotEmpty(bankAdvise_deptId)){
		payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
	}
	if(UtilValidate.isNotEmpty(deductPayrollHeaderIdList)){
		payConList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.NOT_IN ,deductPayrollHeaderIdList));
	}
	payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
	payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
	payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
	
	
	//Debug.log("payRollHeaderList=========================="+payRollHeaderList);
	
	if(UtilValidate.isNotEmpty(payRollHeaderList)){
		payrollHeader = payRollHeaderList[0];
		if(UtilValidate.isNotEmpty(payrollHeader)){
			orgPartyId = payrollHeader.partyId;
		}
		payRollHeaderList.each{ payRollHead->
			payrollHeaderId=payRollHead.get("payrollHeaderId");
			partyId=payRollHead.get("partyIdFrom");
			partyDetails = delegator.findOne("PartyPersonAndEmployeeDetail", [partyId :partyId], false);
			bankAdviceDetailsMap=[:];
			payRollItemsMap=[:];
			InstallmentNoMap=[:];
			itemConList=[];
			itemConList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS ,payrollHeaderId));
			itemCond = EntityCondition.makeCondition(itemConList,EntityOperator.AND);
			payRollHeaderItemsList = delegator.findList("PayrollHeaderItem", itemCond, null, ["payrollItemSeqId"], null, false);
			totEarnings=0.0;
			totDeductions=0.0;
			if(UtilValidate.isNotEmpty(payRollHeaderItemsList)){
				tempAmount =0;
				payRollHeaderItemsList.each{ payRollHeaderItem->
					payrollItemSeqId=payRollHeaderItem.get("payrollItemSeqId");
					payrollHeaderItemTypeId=payRollHeaderItem.get("payrollHeaderItemTypeId");
					//getting installment No
					if(dedTypeIds.contains(payrollHeaderItemTypeId)){
						loanRecList=[];
						loanRecList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS ,payrollHeaderId));
						loanRecList.add(EntityCondition.makeCondition("payrollItemSeqId", EntityOperator.EQUALS ,payrollItemSeqId));
						loanCond = EntityCondition.makeCondition(loanRecList,EntityOperator.AND);
						loanRecvryList = delegator.findList("LoanRecovery", loanCond, null, null, null, false);
						if(UtilValidate.isNotEmpty(loanRecvryList)){							
							loanRecvryDetails = EntityUtil.getFirst(loanRecvryList);
							instNum=0;
							if(UtilValidate.isNotEmpty(loanRecvryDetails.get("principalInstNum"))){
								instNum = loanRecvryDetails.get("principalInstNum");
							}
							if(UtilValidate.isNotEmpty(loanRecvryDetails.get("interestInstNum"))){
								instNum = loanRecvryDetails.get("interestInstNum");
							}
								InstallmentNoMap.put(payrollHeaderItemTypeId,instNum);
						}
					}
					amount=payRollHeaderItem.get("amount");
					if(amount >0){
						tempAmount +=amount;
					}
					if(benefitTypeIds.contains(payrollHeaderItemTypeId)){
						totEarnings=totEarnings+amount;
					}
					if(dedTypeIds.contains(payrollHeaderItemTypeId)){
						totDeductions=totDeductions+amount;
					}
					if(UtilValidate.isEmpty(payRollItemsMap.get(payrollHeaderItemTypeId))){
						payRollItemsMap[payrollHeaderItemTypeId]=amount;
					}else{
						payRollItemsMap[payrollHeaderItemTypeId]+=amount;
					}
					//payroll Summary Map
					
					if(UtilValidate.isEmpty(payRollSummaryMap.get(payrollHeaderItemTypeId))){
						payRollSummaryMap[payrollHeaderItemTypeId]=amount;
					}else{
						payRollSummaryMap[payrollHeaderItemTypeId]+=amount;
					}
					
				}
				if(UtilValidate.isNotEmpty(InstallmentNoMap)){
					InstallmentFinalMap.put(payrollHeaderId,InstallmentNoMap)
				}
				if(UtilValidate.isNotEmpty(payRollItemsMap) || tempAmount !=0){
					payRollMap.put(payrollHeaderId,payRollItemsMap);
					payRollEmployeeMap.put(partyId,payRollItemsMap);
					
					loanDetails = delegator.findList("Loan", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , partyId), null, null, null, false);
					if(UtilValidate.isNotEmpty(loanDetails)){
						loanDetails.each{ loan->
							loanTypeMap = [:];
							loanBalance = 0;
							loanId = loan.get("loanId");
							loanTypeId = loan.get("loanTypeId");
							loanRecoveryList = delegator.findList("LoanRecovery",EntityCondition.makeCondition("loanId", EntityOperator.EQUALS , loanId)  , null, null, null, false );
							if(UtilValidate.isNotEmpty(loanRecoveryList)){
								loanRecoveryDetails = EntityUtil.getFirst(loanRecoveryList);
								loanBalance = loanRecoveryDetails.get("closingBalance");
							}
							loanTypeMap.put(loanTypeId,loanBalance);
							loanBalancesMap.put(partyId,loanTypeMap);
						}
					}
					
					
					unitDetails = delegator.findList("Employment", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyId), null, null, null, false);
					if(UtilValidate.isNotEmpty(unitDetails)){
						unitDetails = EntityUtil.getFirst(unitDetails);
						if(UtilValidate.isNotEmpty(unitDetails))	{
							locationGeoId=unitDetails.get("locationGeoId");
						}
						unitIdMap.put(partyId,locationGeoId);
					}
					payRateList=[];
					payRateList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
					payRateList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,parameters.OrganizationId));
					payRateList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
					payRateList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodStart));
					//payRateList.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN, UtilMisc.toList(timePeriodStart,timePeriodEnd)));
					payRateCond = EntityCondition.makeCondition(payRateList,EntityOperator.AND);
					payRateList = delegator.findList("PayHistory", payRateCond, null, null, null, false);
					if(UtilValidate.isNotEmpty(payRateList)){
						payRateList.each { pay ->
							salaryStepSeqId = pay.get("salaryStepSeqId");
							payGradeId = pay.get("payGradeId");
							payRateSalary = delegator.findOne("SalaryStep", [salaryStepSeqId : salaryStepSeqId, payGradeId : payGradeId], false);
							if(UtilValidate.isNotEmpty(payRateSalary)){
								salary = payRateSalary.get("amount");
								payRateMap.put(partyId, salary);
							}
						}
					}else{
						salary = null;
						payRateMap.put(partyId, salary);
					}
				}
			}
			netAmount=totEarnings+totDeductions;
			bankAdviceDetailsMap.put("totEarnings",totEarnings);
			bankAdviceDetailsMap.put("totDeductions",totDeductions);
			List finAccConList=FastList.newInstance();
			finAccConList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,partyId));
			finAccConList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
			finAccConList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
			EntityCondition finAccCond = EntityCondition.makeCondition(finAccConList, EntityOperator.AND);
			accountDetails = delegator.findList("FinAccount", finAccCond, null, null, null, false);
			if(UtilValidate.isNotEmpty(accountDetails)){
				accDetails = EntityUtil.getFirst(accountDetails);
				accNo=0;
				ifscCode="";
				finAccountBranch="";
				finAccountName="";
				if(UtilValidate.isNotEmpty(accDetails))	{
					accNo = accDetails.get("finAccountCode");	
					ifscCode = accDetails.get("ifscCode");
					finAccountBranch = accDetails.get("finAccountBranch");
					finAccountName = accDetails.get("finAccountName");
				}		
				bankAdviceDetailsMap.put("acNo",accNo);
				bankAdviceDetailsMap.put("ifscCode",ifscCode);
				bankAdviceDetailsMap.put("finAccountBranch",finAccountBranch);
				bankAdviceDetailsMap.put("finAccountName",finAccountName);
			}
			if(UtilValidate.isNotEmpty(partyDetails.employeeId)){
				bankAdviceDetailsMap.put("emplNo",partyDetails.get("employeeId"));
			}else{
				bankAdviceDetailsMap.put("emplNo",partyId);
			}
			if(UtilValidate.isNotEmpty(partyDetails.firstName)){
				if(UtilValidate.isNotEmpty(partyDetails.lastName)){
					bankAdviceDetailsMap["empName"]=partyDetails.firstName+" "+partyDetails.lastName;
				}else{
					bankAdviceDetailsMap["empName"]=partyDetails.firstName;
				}
			}
			bankAdviceDetailsMap.put("netAmt",netAmount);
			if(UtilValidate.isNotEmpty(bankAdviceDetailsMap) && (netAmount !=0)){
				BankAdvicePayRollMap.put(partyId,bankAdviceDetailsMap);
			}	
			panDetails = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId",bankAdviceDetailsMap.get("emplNo"),"partyIdentificationTypeId","PAN_NUMBER"), false);
			
			panNumber = "";
			if(UtilValidate.isNotEmpty(panDetails)){
				panNumber = panDetails.get("idValue");
			}
			bankAdviceDetailsMap["panNumber"]=panNumber;
			//getting actual Basic, DA, HRA for employee
			/*basicSalAndGradeMap=PayrollService.fetchBasicSalaryAndGrade(dctx,[employeeId:partyId,timePeriodStart:timePeriodStart, timePeriodEnd: timePeriodEnd, userLogin : userLogin, proportionalFlag:"Y"]);
			Map salaryDetailsMap=FastMap.newInstance();
			basicAmount =0;
			if(UtilValidate.isNotEmpty(basicSalAndGradeMap)){
				basicAmount = basicSalAndGradeMap.get("amount");
			}
			payHeadDAAmount = dispatcher.runSync("getPayHeadAmount", [employeeId: partyId, customTimePeriodId: parameters.customTimePeriodId,payHeadTypeId: "PAYROL_BEN_DA", proportionalFlag:"Y", userLogin: userLogin]);
			daAmt=0;
			if(UtilValidate.isNotEmpty(payHeadDAAmount)){
				daAmt = payHeadDAAmount.get("amount");
			}
			payHeadHRAAmount = dispatcher.runSync("getPayHeadAmount", [employeeId: partyId, customTimePeriodId: parameters.customTimePeriodId,payHeadTypeId: "PAYROL_BEN_HRA", proportionalFlag:"Y", userLogin: userLogin]);
			hraAmt=0;
			if(UtilValidate.isNotEmpty(payHeadHRAAmount)){
				hraAmt = payHeadHRAAmount.get("amount");
			}
			salaryDetailsMap.put("basic",basicAmount);
			salaryDetailsMap.put("daAmt",daAmt);
			salaryDetailsMap.put("hraAmt",hraAmt);
			if(UtilValidate.isNotEmpty(salaryDetailsMap)){
				EmplSalaryDetailsMap.put(partyId,salaryDetailsMap);
			}*/
			
			//attendance here
			if(UtilValidate.isNotEmpty(partyId)){
				 emplAttendanceDetails = delegator.findOne("PayrollAttendance", [partyId : partyId , customTimePeriodId : timePeriod], false);
				 if(UtilValidate.isNotEmpty(emplAttendanceDetails)){
					 emplAttndDetailsMap = [:];
					 lateMinNew = 0;
					  lateMinStr = "";
					  noOfCalenderDays = emplAttendanceDetails.get("noOfCalenderDays");
					  lateMin = emplAttendanceDetails.get("lateMin");
					  lossOfPayDays = emplAttendanceDetails.get("lossOfPayDays");
					  noOfArrearDays = emplAttendanceDetails.get("noOfArrearDays");
					  noOfPayableDays = emplAttendanceDetails.get("noOfPayableDays");
					  
					  
					  
					  
					  if(UtilValidate.isNotEmpty(lateMin)){
						  lateMinNew = lateMin.multiply(480).setScale(2,BigDecimal.ROUND_HALF_UP);
					  }
					  if(UtilValidate.isNotEmpty(lateMinNew) && lateMinNew <= 59){
						  lateMinStr = lateMinNew + " min ";
					  }else{
					  	 String lateMinHr = (lateMinNew/60).setScale(2,BigDecimal.ROUND_HALF_UP);
						 List<String> timeSplit = StringUtil.split(lateMinHr, ".");
						 if(UtilValidate.isNotEmpty(timeSplit)){
							  int hours = Integer.parseInt(timeSplit.get(0));
							  int minutes = Integer.parseInt(timeSplit.get(1));
							  int finalMin = minutes.multiply(0.6).setScale(0,BigDecimal.ROUND_HALF_UP);
							  lateMinStr = hours + " hrs " + finalMin + " min ";
						 }
					  }	
					  emplAttndDetailsMap.putAt("noOfCalenderDays", noOfCalenderDays);
					  emplAttndDetailsMap.putAt("lateMin", lateMin);
					  emplAttndDetailsMap.putAt("lateMinStr", lateMinStr);
					  emplAttndDetailsMap.putAt("lossOfPayDays", lossOfPayDays);
					  emplAttndDetailsMap.putAt("noOfArrearDays", noOfArrearDays);
					  emplAttndDetailsMap.putAt("noOfPayableDays", noOfPayableDays);
					  if(UtilValidate.isNotEmpty(emplAttndDetailsMap)){
						  emplAttendanceDetailsMap.putAt(partyId, emplAttndDetailsMap);
					  }
				 }
			}
		}
	}
}
context.put("emplAttendanceDetailsMap",emplAttendanceDetailsMap);
context.put("BankAdvicePayRollMap",BankAdvicePayRollMap);

if(UtilValidate.isNotEmpty(BankAdvicePayRollMap) && UtilValidate.isNotEmpty(parameters.sendSms) && (parameters.sendSms).equals("Y")){
	payrollType = delegator.findOne("PayrollType", [ payrollTypeId : parameters.billingTypeId], false);
	if (UtilValidate.isNotEmpty(payrollType)  && UtilValidate.isNotEmpty(payrollType.getString("smsServicePath"))) {
		smsResult = GroovyUtil.runScriptAtLocation(payrollType.getString("smsServicePath"), context);
	}else{
		for(Map.Entry entry : BankAdvicePayRollMap.entrySet()){
			partyId = entry.getKey();
			amountMap = entry.getValue();
			Map<String, Object> getTelParams = FastMap.newInstance();
			contactNumberTo = null;
			getTelParams.put("partyId", partyId);
			if(UtilValidate.isNotEmpty(partyId)){
				getTelParams.put("partyId", partyId);
			}
			getTelParams.put("userLogin", userLogin);
			serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
			if (ServiceUtil.isError(serviceResult)) {
				 Debug.logError(ServiceUtil.getErrorMessage(serviceResult),"");
			}
			if(UtilValidate.isNotEmpty(serviceResult.get("contactNumber"))){
				contactNumberTo = (String) serviceResult.get("contactNumber");
				if(!UtilValidate.isEmpty(serviceResult.get("countryCode"))){
					contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");
				}
			}
			billingDesc = null;
			if(UtilValidate.isNotEmpty(parameters.billingTypeId)){
				GenericValue enumeration = delegator.findOne("Enumeration", [enumId : parameters.billingTypeId], false);
				if(UtilValidate.isNotEmpty(enumeration.description)){
					billingDesc = enumeration.description;
				}
			}
			String text = null;
		    if(UtilValidate.isNotEmpty(parameters.billingTypeId) && (parameters.billingTypeId.equals("PAYROLL_BILL"))){
				text = "Your remuneration of Rs "+amountMap.getAt("netAmt").setScale(2,BigDecimal.ROUND_HALF_UP)+" for "+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate") ,'MMMM yyyy')+" has been approved for bank payment. Automated message sent from Milkosoft, Mother Dairy.";
		    }else{
			    text = "Your " +billingDesc+" remuneration of Rs "+amountMap.getAt("netAmt").setScale(2,BigDecimal.ROUND_HALF_UP)+" from "+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate") ,'MMMM yyyy')+ " to " +UtilDateTime.toDateString(customTimePeriod.getDate("thruDate"),'MMMM yyyy')+ " has been approved for bank payment. Automated message sent from Milkosoft, Mother Dairy.";
		    }
		   
		   Debug.log("Sms text: " + text);
		   Map<String, Object> sendSmsParams = FastMap.newInstance();
		  if(UtilValidate.isNotEmpty(contactNumberTo)){
				sendSmsParams.put("contactNumberTo", contactNumberTo);
				sendSmsParams.put("text",text);
				dispatcher.runAsync("sendSms", sendSmsParams,false);
			}
		}
	}
}

parameters.partyId=orgPartyId;
context.put("unitIdMap",unitIdMap);
context.put("payRateMap",payRateMap);
context.put("loanBalancesMap",loanBalancesMap);
context.put("InstallmentFinalMap",InstallmentFinalMap);
context.put("payRollSummaryMap",payRollSummaryMap);
context.put("payRollMap",payRollMap);
context.put("payRollEmployeeMap",payRollEmployeeMap);
context.put("EmplSalaryDetailsMap",EmplSalaryDetailsMap);
if(UtilValidate.isNotEmpty(basicSalDate)){
	context.putAt("basicSalDate", basicSalDate);
}
if(UtilValidate.isEmpty(orgPartyId)){
	orgPartyId = "Company";
}

emplWiseTypeIdsMap = [:];
if(UtilValidate.isNotEmpty(payRollEmployeeMap)){
	for(Map.Entry entry : payRollEmployeeMap.entrySet()){
		dedList = [];
		benList = [];
		typeIdsSizeMap = [:];
		partyId = entry.getKey();
		amountMap = entry.getValue();
		for(Map.Entry typeEntry : amountMap.entrySet()){
			typeId = typeEntry.getKey();
			for(dedTypeid in dedTypeIds){
				if(dedTypeid.equals(typeId)){
					dedList.add(dedTypeid);
				}
			}
			for(benefitTypeId in benefitTypeIds){
				if(benefitTypeId.equals(typeId)){
					benList.add(benefitTypeId);
				}
			}
		}
		typeIdsSizeMap.put("dedTypeIds",dedList.size());
		typeIdsSizeMap.put("benTypeIds",benList.size());
		emplWiseTypeIdsMap.put(partyId,typeIdsSizeMap);
	}
}

context.put("emplWiseTypeIdsMap",emplWiseTypeIdsMap);

// Fetch Organisation logo url
logoImageUrl = null;

partyGroup = delegator.findByPrimaryKey("PartyGroup", [partyId : orgPartyId]);
if (partyGroup?.logoImageUrl) {
   logoImageUrl = partyGroup.logoImageUrl;
}

//Debug.log("payRollEmployeeMap==========="+payRollEmployeeMap);
context.logoImageUrl = logoImageUrl;
