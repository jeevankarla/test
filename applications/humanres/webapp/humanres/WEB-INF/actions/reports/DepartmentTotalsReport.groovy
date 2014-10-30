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
//context.put("customTimePeriod",customTimePeriod);
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
	emplInputMap.put("orgPartyId", parameters.partyIdFrom);
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("fromDate", timePeriodStart);
	emplInputMap.put("thruDate", timePeriodEnd);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
	employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
	employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
	locationGeoIds = EntityUtil.getFieldListFromEntityList(employementList, "locationGeoId", true);
	
	
Map unitIdMap=FastMap.newInstance();
Map finalMap=FastMap.newInstance();
employementList.each{ employement->
	unitIdMap.put(employement.partyIdTo,employement.locationGeoId);
}
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
		locationGeoIds.each{ locationGeoId->
			totEarnings=0.0;
			totDeductions=0.0;
			netAmount=0.0;
			rndNetAmt=0.0;
			amount=0.0;
			loanAmount=0.0;
			Map payRollSummaryMap=FastMap.newInstance();
			payRollSummaryMap["totEarnings"]=totEarnings;
			payRollSummaryMap["totDeductions"]=totDeductions;
			payRollSummaryMap["netAmount"]=netAmount;
			payRollSummaryMap["rndNetAmt"]=rndNetAmt;
			payRollSummaryMap["loanAmount"]=loanAmount;
			payRollHeaderList.each{ payRollHead->
				payrollHeaderId=payRollHead.get("payrollHeaderId");
				partyId=payRollHead.get("partyIdFrom");
				if(locationGeoId==unitIdMap.get(partyId)){
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
							//payroll Summary Map
							if(UtilValidate.isEmpty(payRollSummaryMap.get(payrollHeaderItemTypeId))){
								payRollSummaryMap[payrollHeaderItemTypeId]=amount;
							}else{
								payRollSummaryMap[payrollHeaderItemTypeId]+=amount;
							}
						}
						
					}
					netAmount=totEarnings+totDeductions;
					payRollSummaryMap["totEarnings"]=totEarnings;
					payRollSummaryMap["totDeductions"]=totDeductions;
					payRollSummaryMap["netAmount"]=netAmount;
					payRollSummaryMap["rndNetAmt"]=(netAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
					payRollSummaryMap["loanAmount"]=loanAmount;
				}
			}
			finalMap.put(locationGeoId,payRollSummaryMap);
		}
	}
}
context.finalMap=finalMap;
