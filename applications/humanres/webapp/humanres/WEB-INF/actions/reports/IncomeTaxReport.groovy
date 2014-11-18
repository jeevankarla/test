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
dayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

Map emplInputMap = FastMap.newInstance();
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", dayStart);
emplInputMap.put("thruDate", dayEnd);
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

periodHeaderListMap = [:];

if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	periodIdsList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId), null, null, null, false);
	if(UtilValidate.isNotEmpty(periodIdsList)){
		periodIdsList.each { period ->
			periodId = period.get("customTimePeriodId");
			fromDate = period.get("fromDate");
			thruDate = period.get("thruDate");
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS , fromDate));
			conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS , thruDate));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			CustomTimePeriodList = delegator.findList("CustomTimePeriod", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(CustomTimePeriodList)){
				CustomTimePeriod = EntityUtil.getFirst(CustomTimePeriodList);
				customTimePeriodId = CustomTimePeriod.get("customTimePeriodId");
				List statusList = UtilMisc.toList("GENERATED","APPROVED");
				billingConditionList=[];
				billingConditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , "PAYROLL_BILL"));
				billingConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,statusList));
				billingConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
				billingCondition = EntityCondition.makeCondition(billingConditionList,EntityOperator.AND);
				periodBillingList = delegator.findList("PeriodBilling", billingCondition, null, null, null, false);
				if(UtilValidate.isNotEmpty(periodBillingList)){
					periodBillDetails = EntityUtil.getFirst(periodBillingList);
					periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
					periodBillingId = periodBillDetails.get("periodBillingId");
					periodHeaderListMap.put(periodId,periodBillingIds);
				}
				if(UtilValidate.isEmpty(periodBillingList)){
					periodHeaderListMap.put(periodId,"");
				}
			}
		}
	}
}

Map unPayRollEmployeeMap=FastMap.newInstance();
Map payRollEmployeeMap=FastMap.newInstance();

if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	if(UtilValidate.isNotEmpty(employementList)){
		employementList.each { employee ->
			partyId = employee.get("partyIdTo");
			empName = employee.get("firstName");
			unitDetails = delegator.findList("Employment", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyId), null, null, null, false);
			if(UtilValidate.isNotEmpty(unitDetails)){
				unitDetails = EntityUtil.getFirst(unitDetails);
				if(UtilValidate.isNotEmpty(unitDetails))	{
					locationGeoId=unitDetails.get("locationGeoId");
				}
			}
			periodWiseEmployeeMap = [:];
			periodWiseMap = [:];
			if(UtilValidate.isNotEmpty(periodHeaderListMap)){
				Iterator periodHeaderIter = periodHeaderListMap.entrySet().iterator();
				while(periodHeaderIter.hasNext()){
					Map.Entry periodHeaderEntry = periodHeaderIter.next();
					periodBillingIds = periodHeaderEntry.getValue();
					if(UtilValidate.isNotEmpty(periodBillingIds)){
						payRollHeaderList=[];
						payConList=[];
						payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingIds));
						payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,partyId));
						payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
						payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
						if(UtilValidate.isNotEmpty(payRollHeaderList)){
							payRollHeaderList.each{ payRollHead->
								payrollHeaderId=payRollHead.get("payrollHeaderId");
								partyId=payRollHead.get("partyIdFrom");
								partyDetails = delegator.findOne("PartyPersonAndEmployeeDetail", [partyId :partyId], false);
								payRollItemsMap=[:];
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
										if(UtilValidate.isEmpty(payRollItemsMap.get(payrollHeaderItemTypeId))){
											payRollItemsMap[payrollHeaderItemTypeId]=amount;
										}else{
											payRollItemsMap[payrollHeaderItemTypeId]+=amount;
										}
										
										periodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId :periodHeaderEntry.getKey()], false);
										periodName = periodDetails.get("periodName");
										if(UtilValidate.isNotEmpty(payRollItemsMap) || tempAmount !=0){
											periodWiseMap.put(periodName,payRollItemsMap);
										}
										if(UtilValidate.isNotEmpty(payRollItemsMap)){
											periodWiseEmployeeMap.put(periodName,payRollItemsMap);
										}
									}
								}
							}
						}else{
							periodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId :periodHeaderEntry.getKey()], false);
							periodName = periodDetails.get("periodName");
							periodWiseEmployeeMap.put(periodName,"");
						}
					}else{
						periodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId :periodHeaderEntry.getKey()], false);
						periodName = periodDetails.get("periodName");
						periodWiseEmployeeMap.put(periodName,"");
					}
				}
			}
			if(UtilValidate.isNotEmpty(periodWiseEmployeeMap)){
				periodWiseEmployeeMap.put("empName",empName);
				periodWiseEmployeeMap.put("unitId",locationGeoId);
				payRollEmployeeMap.put(partyId,periodWiseEmployeeMap);
			}
		}
	}
}


if(UtilValidate.isNotEmpty(payRollEmployeeMap)){
	Iterator payRollEmployeeIter = payRollEmployeeMap.entrySet().iterator();
	while(payRollEmployeeIter.hasNext()){
		highBasic = 0;
		Map unPayRollMap=FastMap.newInstance();
		Map.Entry payRollEmployeeEntry = payRollEmployeeIter.next();
		payRollEmployeeValue =payRollEmployeeEntry.getValue();
		if(UtilValidate.isNotEmpty(payRollEmployeeValue)){
			Iterator payRollPeriodIter = payRollEmployeeValue.entrySet().iterator();
			while(payRollPeriodIter.hasNext()){
				Map.Entry payRollPeriodEntry = payRollPeriodIter.next();
				payRollPeriodValue = payRollPeriodEntry.getValue();
				if(UtilValidate.isNotEmpty(payRollPeriodValue)){
					if(!(payRollPeriodEntry.getKey()).equals("empName")){
						if(!(payRollPeriodEntry.getKey()).equals("unitId")){
							basic = payRollPeriodValue.get("PAYROL_BEN_SALARY");
							if(UtilValidate.isNotEmpty(basic)){
								if(basic != 0){
									if(highBasic < basic){
										highBasic = basic;
										highBasicValue = payRollPeriodValue;
										unPayRollEmployeeMap.put(payRollEmployeeEntry.getKey(),highBasicValue);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}


context.put("unPayRollEmployeeMap",unPayRollEmployeeMap);
context.put("payRollEmployeeMap",payRollEmployeeMap);

