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
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	context.timePeriodStart=timePeriodStart;
	context.timePeriodEnd=timePeriodEnd;
	context.ShedId=parameters.partyIdFrom;
}
resultMap = PayrollService.getPayrollAttedancePeriod(dctx, [timePeriodStart:timePeriodStart, timePeriodEnd: timePeriodEnd, timePeriodId: parameters.customTimePeriodId, userLogin : userLogin]);
if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){
	lastCloseAttedancePeriod=resultMap.get("lastCloseAttedancePeriod");
	timePeriod=lastCloseAttedancePeriod.get("customTimePeriodId");
	context.timePeriod=timePeriod;
}

//getting benefits
sortBy = UtilMisc.toList("sequenceNum");
//description
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

//getting payHeadTypeIds from LoanType
loanConList=[];
loanConList.add(EntityCondition.makeCondition("isExternal", EntityOperator.EQUALS,"Y"));
loanCondition=EntityCondition.makeCondition(loanConList,EntityOperator.AND);
loanTypeList=delegator.findList("LoanType",loanCondition,null,null,null,false);
loanTypes = EntityUtil.getFieldListFromEntityList(loanTypeList, "payHeadTypeId", true);

List stautsList = UtilMisc.toList("GENERATED","APPROVED");
conditionList=[];
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , "PAYROLL_BILL"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN  ,stautsList));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));

if(UtilValidate.isNotEmpty(parameters.partyIdFrom) && (!"Company".equals(parameters.partyIdFrom))){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyIdFrom));
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);

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
	emplInputMap.put("orgPartyId", parameters.partyIdFrom);
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("fromDate", timePeriodStart);
	emplInputMap.put("thruDate", timePeriodEnd);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
	employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
	employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
	
Map unitIdMap=FastMap.newInstance();
	partyRelationconditionList=[];
	partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "DEPARTMENT"));
	partyRelationconditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN ,employementIds));
	partyCondition = EntityCondition.makeCondition(partyRelationconditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("comments");
	partyRelationList = delegator.findList("PartyRelationship", partyCondition, null, orderBy, null, false);
	if(UtilValidate.isNotEmpty(partyRelationList)){
		partyRelationList.each{ partyRelation->
			costCode=partyRelation.get("comments");
			partyIdsList = [];
			employementId = 0;
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("comments", EntityOperator.EQUALS , costCode));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			partyLists = delegator.findList("PartyRelationship", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(partyLists)){
				partyLists.each{ partyList->
					employementId = partyList.get("partyIdTo");
					partyIdsList.addAll(employementId);
				}
			}
			if(UtilValidate.isNotEmpty(partyIdsList)){
				unitIdMap.put(costCode,partyIdsList);
			}
		}
	}
Map payRollSummaryMap=FastMap.newInstance();
Map finalMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(periodBillingList)){
	periodBillDetails = EntityUtil.getFirst(periodBillingList);
	periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	periodBillingId = periodBillDetails.get("periodBillingId");
	payRollHeaderList=[];
	payConList=[];
	payConList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingIds));
	payConList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,employementIds));
	payCond = EntityCondition.makeCondition(payConList,EntityOperator.AND);
	payRollHeaderList = delegator.findList("PayrollHeader", payCond, null, null, null, false);
	
	if(UtilValidate.isNotEmpty(payRollHeaderList)){
		if(UtilValidate.isNotEmpty(unitIdMap)){
			Iterator unitIter = unitIdMap.entrySet().iterator();
			while(unitIter.hasNext()){
				Map.Entry mccEntry = unitIter.next();
				Map costCodeMap=FastMap.newInstance();
				locationGeoId = mccEntry.getKey();
				totEarnings=0.0;
				totDeductions=0.0;
				netAmount=0.0;
				rndNetAmt=0.0;
				amount=0.0;
				loanAmount=0.0;
				reqPartyIds = mccEntry.getValue();
				reqPartyIds.each{ reqPartyId->
					payRollHeaderList.each{ payRollHead->
						payrollHeaderId=payRollHead.get("payrollHeaderId");
						partyId=payRollHead.get("partyIdFrom");
							if(reqPartyId==partyId){
								itemConList=[];
								itemConList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS ,payrollHeaderId));
								itemCond = EntityCondition.makeCondition(itemConList,EntityOperator.AND);
								payRollHeaderItemsList = delegator.findList("PayrollHeaderItem", itemCond, null, ["payrollItemSeqId"], null, false);
								if(UtilValidate.isNotEmpty(payRollHeaderItemsList)){
									payRollHeaderItemsList.each{ payRollHeaderItem->
										payrollItemSeqId=payRollHeaderItem.get("payrollItemSeqId");
										payrollHeaderItemTypeId=payRollHeaderItem.get("payrollHeaderItemTypeId");
										amount=payRollHeaderItem.get("amount");
										if(benefitTypeIds.contains(payrollHeaderItemTypeId)){
											totEarnings=totEarnings+amount;
										}
										if(dedTypeIds.contains(payrollHeaderItemTypeId)){
											totDeductions=totDeductions+amount;
										}
										if(loanTypes.contains(payrollHeaderItemTypeId)){
											loanAmount=loanAmount+amount;
										}
										deductionTypeMap = [:];
										if(UtilValidate.isEmpty(payRollSummaryMap[locationGeoId])){
											deductionTypeMap[payrollHeaderItemTypeId] = amount;
										}
										else{
											deductionTypeMap = payRollSummaryMap[locationGeoId];
											if(UtilValidate.isEmpty(deductionTypeMap[payrollHeaderItemTypeId])){
												deductionTypeMap[payrollHeaderItemTypeId] = amount;
											}
											else{
												deductionTypeMap[payrollHeaderItemTypeId] += amount;
											}
										}
										payRollSummaryMap[locationGeoId] = deductionTypeMap;
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
					payRollMap["loanAmount"] = loanAmount;
					tempPRMap = [:]
					tempPRMap.putAll(payRollMap);
					finalMap.put(locationGeoId, tempPRMap)
				}
			}
		}
	}
context.payRollSummaryMap=payRollSummaryMap;
context.finalMap=finalMap;
