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
	timePeriod=lastCloseAttedancePeriod.get("periodName");
	context.timePeriod=timePeriod;
}
List stautsList = UtilMisc.toList("GENERATED","APPROVED");
conditionList=[];
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,stautsList));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
//getting benefits
benefitTypeList = delegator.findList("BenefitType", null, null, ["sequenceNum"], null, false);
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

deductionTypeList = delegator.findList("DeductionType", null, null, ["sequenceNum"], null, false);
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
Map payRollMap=FastMap.newInstance();
Map payRollSummaryMap=FastMap.newInstance();
Map payRollEmployeeMap=FastMap.newInstance();
Map BankAdvicePayRollMap=FastMap.newInstance();
Map InstallmentFinalMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(periodBillingList)){
	periodBillDetails = EntityUtil.getFirst(periodBillingList);
	periodBillingId = periodBillDetails.get("periodBillingId");
	payRollHeaderList=[];
	payConList=[];
	payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,periodBillingId));
	if(UtilValidate.isNotEmpty(parameters.employeeId))
		payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,parameters.employeeId));
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
							if(UtilValidate.isNotEmpty(loanRecvryDetails.get("principalInstNum"))){
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
			if(UtilValidate.isNotEmpty(partyDetails.get("employeeBankAccNo"))){
				bankAdviceDetailsMap.put("acNo",partyDetails.get("employeeBankAccNo"));
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
		
	   String text = "Your net salary of Rs."+amountMap.getAt("netAmt")+" for "+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate") ,'MMMM yyyy')+" has been generated by Milkosoft. Automated message sent from Admin Dept, MD.";
	   Debug.log("Sms text: " + text);
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
