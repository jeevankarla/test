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

dctx = dispatcher.getDispatchContext();

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
context.timePeriodStart= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
context.timePeriodEnd= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

List stautsList = UtilMisc.toList("GENERATED","APPROVED");
conditionList=[];

if(UtilValidate.isEmpty(parameters.billingTypeId)){
	parameters.billingTypeId = "PAYROLL_BILL";
}

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

Map emplInputMap = FastMap.newInstance();
bankAdvise_deptId=parameters.bankAdvise_deptId;
if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
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
sortBy = UtilMisc.toList("sequenceNum");
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


List conditionList =[];
if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
	parameters.partyIdFrom=parameters.partyIdFrom;
}else{
	parameters.partyIdFrom=parameters.partyId;
}
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,"company"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
if(UtilValidate.isNotEmpty(parameters.finAccountId) && (!"All".equals(parameters.finAccountId))){
	conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS ,parameters.finAccountId));
}
EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
companyBankAccountList= delegator.findList("FinAccount",condition,null,null,null,false);
Map bankWiseEmplDetailsMap=FastMap.newInstance();

if(UtilValidate.isNotEmpty(companyBankAccountList)){
	companyBankAccountList.each{ bankDetails->
		Map BankAdvicePayRollMap=FastMap.newInstance();
		finAccountId= bankDetails.finAccountId;
		List conList=FastList.newInstance();
		conList=UtilMisc.toList(
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"),
			EntityCondition.makeCondition("partyId", EntityOperator.IN ,employementIds),
			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd),
			EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)),
			EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
			EntityCondition cond = EntityCondition.makeCondition(conList, EntityOperator.AND);
			finAccountRoleList=delegator.findList("FinAccountRole",cond, null,null, null, false);
			if(UtilValidate.isNotEmpty(finAccountRoleList)){
			partyIds = EntityUtil.getFieldListFromEntityList(finAccountRoleList, "partyId", true);
			if(UtilValidate.isNotEmpty(partyIds)){
				List finAccConList=FastList.newInstance();
				 finAccConList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN ,partyIds));
				 finAccConList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
				 finAccConList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
				 EntityCondition finAccCond = EntityCondition.makeCondition(finAccConList, EntityOperator.AND);
				List<GenericValue> finAccountDetailsList = delegator.findList("FinAccount", finAccCond, null, null, null, false);
				partiesFinAccList = UtilMisc.sortMaps(finAccountDetailsList, UtilMisc.toList("finAccountCode"));
				
				if(UtilValidate.isNotEmpty(partiesFinAccList)){
					partiesFinAccList.each{ partyFin->
						employee = partyFin.get("ownerPartyId");
						if(UtilValidate.isNotEmpty(periodBillingList)){
							periodBillDetails = EntityUtil.getFirst(periodBillingList);
							periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
							periodBillingId = periodBillDetails.get("periodBillingId");
							payRollHeaderList=[];
							payConList=[];
							payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingIds));
							payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,employee));
							payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
							payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
							if(UtilValidate.isNotEmpty(payRollHeaderList)){
								payrollHeader = payRollHeaderList[0];
								payRollHeaderList.each{ payRollHead->
									payrollHeaderId=payRollHead.get("payrollHeaderId");
									partyId=payRollHead.get("partyIdFrom");
									partyDetails = delegator.findOne("PartyPersonAndEmployeeDetail", [partyId :partyId], false);
									bankAdviceDetailsMap=[:];
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
										}
									}
									netAmount=totEarnings+totDeductions;
									bankAdviceDetailsMap.put("totEarnings",totEarnings);
									bankAdviceDetailsMap.put("totDeductions",totDeductions);
									accNo = partyFin.get("finAccountCode");
									bankAdviceDetailsMap.put("acNo",accNo);
									if(UtilValidate.isNotEmpty(partyDetails.employeeId)){
										bankAdviceDetailsMap.put("emplNo",partyDetails.get("employeeId"));
										partyDetails = delegator.findOne("Person",[ partyId : partyDetails.get("employeeId") ], false);
										String partyName = partyDetails.get("nickname");
										if(UtilValidate.isEmpty(partyName)){
											partyName = PartyHelper.getPartyName(delegator, partyDetails.get("employeeId"), false);
										}
										bankAdviceDetailsMap["empName"]= partyName;
									}else{
										bankAdviceDetailsMap.put("emplNo",partyId);
										partyDetails = delegator.findOne("Person",[ partyId : partyId], false);
										String partyName = partyDetails.get("nickname");
										if(UtilValidate.isEmpty(partyName)){
											partyName = PartyHelper.getPartyName(delegator, partyId, false);
										}
										bankAdviceDetailsMap["empName"]= partyName;
									}
									/*if(UtilValidate.isNotEmpty(partyDetails.firstName)){
										if(UtilValidate.isNotEmpty(partyDetails.lastName)){
											bankAdviceDetailsMap["empName"]=partyDetails.firstName+" "+partyDetails.lastName;
										}else{
											bankAdviceDetailsMap["empName"]=partyDetails.firstName;
										}
									}*/
									bankAdviceDetailsMap.put("netAmt",netAmount);
									if(UtilValidate.isNotEmpty(bankAdviceDetailsMap) && (netAmount !=0)){
										BankAdvicePayRollMap.put(partyId,bankAdviceDetailsMap);
									}
								}
							}
						}
					}
				}
			}
		}
		if(UtilValidate.isNotEmpty(BankAdvicePayRollMap)){
			bankWiseEmplDetailsMap.put(finAccountId,BankAdvicePayRollMap);
		}
	}
}
context.put("bankWiseEmplDetailsMap",bankWiseEmplDetailsMap);

