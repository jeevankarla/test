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
	
	if(UtilValidate.isNotEmpty(parameters.ShedId)){
		parameters.partyIdFrom=parameters.ShedId;
	}else{
		context.errorMessage = "Shed is Not Selected";
		return ;
	}
	timePeriodStart = null;
	timePeriodEnd = null;
	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
	if(UtilValidate.isNotEmpty(customTimePeriod)){
		timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		context.timePeriodStart=timePeriodStart;
		context.timePeriodEnd=timePeriodEnd;
		context.ShedId=parameters.partyIdFrom;
	}
	//getting benefits
	sortBy = UtilMisc.toList("sequenceNum");
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
	Map unitIdMap=FastMap.newInstance();
	
	Map emplInputMap = FastMap.newInstance();
	emplInputMap.put("orgPartyId", parameters.partyIdFrom);
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("fromDate", timePeriodStart);
	emplInputMap.put("thruDate", timePeriodEnd);
	
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
	employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
	employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
	
	unitEmplys = [];
	employmentConditionList=[];
	employmentConditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
	employmentConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , parameters.partyIdFrom));
	employCondition = EntityCondition.makeCondition(employmentConditionList,EntityOperator.AND);
	def unitOrderBy = UtilMisc.toList("locationGeoId");
	employeeUnitList = delegator.findList("Employment", employCondition, null, unitOrderBy, null, false);
	shedEmployeesList = EntityUtil.getFieldListFromEntityList(employeeUnitList, "partyIdTo", true);
	finalMap = [:];
	costCodeSummaryMap = [:];
	partyRelationconditionList=[];
	partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "DEPARTMENT"));
	partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
	partyRelationconditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN ,shedEmployeesList));
	partyCondition = EntityCondition.makeCondition(partyRelationconditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("comments");
	partyRelationList = delegator.findList("PartyRelationship", partyCondition, null, orderBy, null, false);
	costList = EntityUtil.getFieldListFromEntityList(partyRelationList, "comments", true);
	for(i=0; i<costList.size(); i++){
		costCode=costList.get(i);	
		partyLists = EntityUtil.filterByCondition(partyRelationList, EntityCondition.makeCondition("comments", EntityOperator.EQUALS, costCode));
		
		empList = EntityUtil.filterByCondition(employeeUnitList, EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(partyLists, "partyIdTo", true)));
		unitCode=empList.get(0).get("locationGeoId");
		if(UtilValidate.isEmpty(partyLists)){
			continue;
		}
		deptId=partyLists.get(0).get("partyIdFrom");
		totEarnings=0.0;
		totDeductions=0.0;
		netAmount=0.0;
		rndNetAmt=0.0;
		amount=0.0;
		
		partyLists.each{ partyList->
			employeeId=partyList.get("partyIdTo");
			List stautsList = UtilMisc.toList("GENERATED","APPROVED");
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , "PAYROLL_BILL"));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,stautsList));
			conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(periodBillingList)){
				periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
				periodBillingIds.each{ periodBillingId->
					payRollHeaderList=[];
					payConList=[];
					payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,periodBillingId));
					payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,employeeId));
					payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
					payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
					if(UtilValidate.isNotEmpty(payRollHeaderList)){
						payRollHeaderList.each{ payRollHead->
							payrollHeaderId=payRollHead.get("payrollHeaderId");
							itemConList=[];
							itemConList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS ,payrollHeaderId));
							itemCond = EntityCondition.makeCondition(itemConList,EntityOperator.AND);
							payRollHeaderItemsList = delegator.findList("PayrollHeaderItem", itemCond, null, ["payrollItemSeqId"], null, false);
							if(UtilValidate.isNotEmpty(payRollHeaderItemsList)){
								payRollHeaderItemsList.each{ payRollHeaderItem->
									payrollHeaderItemTypeId=payRollHeaderItem.get("payrollHeaderItemTypeId");
									amount=payRollHeaderItem.get("amount");
									if(benefitTypeIds.contains(payrollHeaderItemTypeId)){
										totEarnings=totEarnings+amount;
									}
									if(dedTypeIds.contains(payrollHeaderItemTypeId)){
										totDeductions=totDeductions+amount;
									}
								}
							}
						}
					}
				}
			}
		}
		netAmount=totEarnings+totDeductions;
		payRollMap = [:];
		payRollMap["totEarnings"] = totEarnings;
		payRollMap["totDeductions"] = totDeductions;
		payRollMap["netAmount"] = netAmount;
		payRollMap["rndNetAmt"] = netAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
		payRollMap["deptId"] = deptId;
		payRollMap["unitCode"] = unitCode;
		if(UtilValidate.isNotEmpty(payRollMap)){
			finalMap.put(costCode,payRollMap);
		}
	}
	context.finalMap=finalMap;
