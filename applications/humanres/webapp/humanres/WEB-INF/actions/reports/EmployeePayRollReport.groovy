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

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
context.timePeriodStart= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.timePeriodEnd= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

resultMap = PayrollService.getPayrollAttedancePeriod(dctx, [timePeriodStart:timePeriodStart, timePeriodEnd: timePeriodEnd, timePeriodId: parameters.customTimePeriodId, userLogin : userLogin]);
if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){	
	lastCloseAttedancePeriod=resultMap.get("lastCloseAttedancePeriod");
	timePeriod=lastCloseAttedancePeriod.get("customTimePeriodId");
	context.timePeriod=timePeriod;
}
List stautsList = UtilMisc.toList("GENERATED","APPROVED");
conditionList=[];

if(UtilValidate.isNotEmpty(parameters.billingTypeId)){
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , parameters.billingTypeId));
}else{
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
}
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
Map payRollMap=FastMap.newInstance();
Map payRollSummaryMap=FastMap.newInstance();
Map payRollEmployeeMap=FastMap.newInstance();
Map BankAdvicePayRollMap=FastMap.newInstance();
Map InstallmentFinalMap=FastMap.newInstance();
Map EmplSalaryDetailsMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(periodBillingList)){
	periodBillDetails = EntityUtil.getFirst(periodBillingList);
	periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	periodBillingId = periodBillDetails.get("periodBillingId");
	payRollHeaderList=[];
	payConList=[];
	payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingIds));
	if(UtilValidate.isNotEmpty(parameters.employeeId))
		payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,parameters.employeeId));
		if(UtilValidate.isNotEmpty(deptId))
			payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
		if(UtilValidate.isNotEmpty(bankAdvise_deptId))
			payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
	payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
	payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
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
			totEarnings=0;
			totDeductions=0;
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
				}
			}
			netAmount=totEarnings+totDeductions;
			accountDetails = delegator.findList("FinAccount", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , partyId), null, null, null, false);
			if(UtilValidate.isNotEmpty(accountDetails)){
				accDetails = EntityUtil.getFirst(accountDetails);
				accNo=0;
				if(UtilValidate.isNotEmpty(accDetails))	{
					accNo= accDetails.get("finAccountCode");					
				}		
				bankAdviceDetailsMap.put("acNo",accNo);
				
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
		}
	}
}


if(UtilValidate.isNotEmpty(BankAdvicePayRollMap) && UtilValidate.isNotEmpty(parameters.sendSms) && (parameters.sendSms).equals("Y")){
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
		
	   String text = "Your remuneration of Rs "+amountMap.getAt("netAmt").setScale(2,BigDecimal.ROUND_HALF_UP)+" for "+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate") ,'MMMM yyyy')+" has been approved for bank payment. Automated message sent from Milkosoft, Mother Dairy.";
	   //Debug.log("Sms text: " + text);
	   Map<String, Object> sendSmsParams = FastMap.newInstance();
	  if(UtilValidate.isNotEmpty(contactNumberTo)){
			sendSmsParams.put("contactNumberTo", contactNumberTo);
			sendSmsParams.put("text",text);
			dispatcher.runAsync("sendSms", sendSmsParams,false);
		}
	}
	
}

parameters.partyId=orgPartyId;
context.put("InstallmentFinalMap",InstallmentFinalMap);
context.put("BankAdvicePayRollMap",BankAdvicePayRollMap);
context.put("payRollSummaryMap",payRollSummaryMap);
context.put("payRollMap",payRollMap);
context.put("payRollEmployeeMap",payRollEmployeeMap);
context.put("EmplSalaryDetailsMap",EmplSalaryDetailsMap);
