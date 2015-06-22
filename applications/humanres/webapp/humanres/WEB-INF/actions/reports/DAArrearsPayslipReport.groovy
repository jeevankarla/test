import org.apache.avalon.framework.parameters.Parameters;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.util.concurrent.TimeUnit;


dctx = dispatcher.getDispatchContext();

periodBillingIdParam = parameters.periodBillingId;
customTimePeriodId = parameters.customTimePeriodId;
context.putAt("customTimePeriodId", customTimePeriodId);

Timestamp fromDateMonthBegin = null;
Timestamp thruDateMonthEnd = null;
customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId] , false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	Timestamp fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	Timestamp thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	
	fromDateMonthBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
	thruDateMonthEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
}


employmentsList = [];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateMonthBegin);
emplInputMap.put("thruDate", thruDateMonthEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}


List retireEmplList = FastList.newInstance();
List retireCondList = FastList.newInstance();
/*retireCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateMonthBegin), EntityOperator.AND,
	EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateMonthEnd)));*/
retireCondList.add(EntityCondition.makeCondition("terminationTypeId" ,EntityOperator.IN ,UtilMisc.toList("RETIRE","DEATH")));
retireCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDateMonthBegin),EntityOperator.AND,
	EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDateMonthEnd),EntityOperator.AND,
																EntityCondition.makeCondition("thruDate",EntityOperator.NOT_EQUAL,null))));
retireCond = EntityCondition.makeCondition(retireCondList,EntityOperator.AND);
List<GenericValue> retireEmplDetails = delegator.findList("Employment", retireCond, null, null, null, false);

if(UtilValidate.isNotEmpty(retireEmplDetails)){
	retireEmplList = EntityUtil.getFieldListFromEntityList(retireEmplDetails, "partyIdTo", true);
}

totalNetMap = [:];
Timestamp startMonth = null;
Timestamp endMonth = null;
GenericValue basicSalPeriod1 = null;
Timestamp oldDADate = null;
geoId = "";

daArrearsData = delegator.findByAnd("EmployeeDAArrears", [periodBillingId: periodBillingIdParam]);
if(UtilValidate.isNotEmpty(daArrearsData)){
	geoId = daArrearsData[0].geoId;
}
List pastPeriodCondList = FastList.newInstance();
pastPeriodCondList.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS ,geoId));
EntityCondition pastPeriodCond = EntityCondition.makeCondition(pastPeriodCondList,EntityOperator.AND);
pastDAPeriodList = delegator.findList("EmployeeDAArrears", pastPeriodCond, null, null, null, false);

if(UtilValidate.isNotEmpty(pastDAPeriodList)){
	pastDAPeriod = EntityUtil.getFirst(pastDAPeriodList);
	oldDADate = (Timestamp) pastDAPeriod.get("DADate");
	String oldPeriodBillingId = pastDAPeriod.get("periodBillingId");
	if(UtilValidate.isNotEmpty(oldPeriodBillingId)){
		List oldPeriodBillCondList = FastList.newInstance();
		oldPeriodBillCondList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS ,oldPeriodBillingId));
		oldPeriodBillCondList.add(EntityCondition.makeCondition("billingTypeId" ,EntityOperator.EQUALS , "SP_DA_ARREARS"));
		oldPeriodBillCondList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS ,"APPROVED"));
		EntityCondition oldPeriodBillCond = EntityCondition.makeCondition(oldPeriodBillCondList,EntityOperator.AND);
		List<GenericValue> oldPeriodBillCTList = delegator.findList("PeriodBillingAndCustomTimePeriod", oldPeriodBillCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(oldPeriodBillCTList)){
			GenericValue oldPeriodBillCTP = EntityUtil.getFirst(oldPeriodBillCTList);
			String oldCTPId = oldPeriodBillCTP.get("customTimePeriodId");
			GenericValue oldCustomTimePeriod;
			if(UtilValidate.isNotEmpty(oldCTPId)){
				oldCustomTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : oldCTPId] , false);
				Timestamp oldFromDateTime = UtilDateTime.toTimestamp(oldCustomTimePeriod.getDate("fromDate"));
				Timestamp oldThruDateTime = UtilDateTime.toTimestamp(oldCustomTimePeriod.getDate("thruDate"));
				
				startMonth = UtilDateTime.getDayStart(oldFromDateTime, timeZone, locale);
				endMonth = UtilDateTime.getDayEnd(oldThruDateTime, timeZone, locale);
				
			  //getting old BasicSalDate period
				List condBasicSalPeriodList1 = FastList.newInstance();
				condBasicSalPeriodList1.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
				condBasicSalPeriodList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayEnd(oldDADate))));
				condBasicSalPeriodList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayStart(oldDADate))));
				EntityCondition basicSalPeriodCond1 = EntityCondition.makeCondition(condBasicSalPeriodList1,EntityOperator.AND);
				List<GenericValue> basicSalPeriodList1 = delegator.findList("CustomTimePeriod", basicSalPeriodCond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(basicSalPeriodList1)){
						basicSalPeriod1 = EntityUtil.getFirst(basicSalPeriodList1);
				}
			}
		}
	}
}

DAArrearMap = [:];
DAArrearLEMap = [:];
employmentsList.each{ employeeId ->
	BigDecimal totalNetAmt = BigDecimal.ZERO;
	periodMap = [:];
	leaveEncashMap = [:];
	customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId] , false);
	if(UtilValidate.isNotEmpty(customTimePeriod)){
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
		Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
		
		periodBillingBasic = delegator.findOne("PeriodBilling",[periodBillingId : periodBillingIdParam] , false);
		basicSalDate = periodBillingBasic.basicSalDate;
		
		List condBasicSalPeriodList = FastList.newInstance();
		condBasicSalPeriodList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"APPROVED"));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayStart(fromDateTime))));
		condBasicSalPeriodList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayEnd(thruDateTime))));
		EntityCondition basicSalPeriodCond = EntityCondition.makeCondition(condBasicSalPeriodList,EntityOperator.AND);
		List basicSalPeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", basicSalPeriodCond, null, null, null, false);
		periodBillingIdList = EntityUtil.getFieldListFromEntityList(basicSalPeriodList, "periodBillingId", true);
		if(UtilValidate.isNotEmpty(periodBillingIdList)){
			
			periodBillingIdList.each{ periodBillingId->
				
				periodBillingEach = delegator.findOne("PeriodBilling",[periodBillingId : periodBillingId] , false);
				ctpId = periodBillingEach.customTimePeriodId;
				
				ctpDA = delegator.findOne("CustomTimePeriod",[customTimePeriodId : ctpId] , false);
				
				Timestamp ctpFromDate=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(ctpDA.getDate("fromDate")));
				Timestamp ctpThruDate=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(ctpDA.getDate("thruDate")));
				
				BigDecimal oldDAAmount = BigDecimal.ZERO;
				BigDecimal rateAmount = BigDecimal.ZERO;
				BigDecimal newDAAmount = BigDecimal.ZERO;
				BigDecimal netDAAmount = BigDecimal.ZERO;
				
				
				/*List payHeadCondList = FastList.newInstance();
				payHeadCondList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId));
				payHeadCondList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_DA"));
				payHeadCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond = EntityCondition.makeCondition(payHeadCondList,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter)){
					GenericValue payrollItems = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter);
					oldDAAmount = (BigDecimal)payrollItems.get("amount");
				}*/
				
				GenericValue basicSalPeriod = null; 
				
				List basicSalPeriodcondList = FastList.newInstance();
				basicSalPeriodcondList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
				basicSalPeriodcondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayEnd(basicSalDate))));
				basicSalPeriodcondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayStart(basicSalDate))));
				basicSalCond = EntityCondition.makeCondition(basicSalPeriodcondList,EntityOperator.AND);
				basicSalPeriodDetails = delegator.findList("CustomTimePeriod", basicSalCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(basicSalPeriodDetails)){
					basicSalPeriod = EntityUtil.getFirst(basicSalPeriodDetails);
				}
				List payHeadCondList = FastList.newInstance();
				payHeadCondList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId));
				payHeadCondList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_SALARY"));
				payHeadCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond1 = EntityCondition.makeCondition(payHeadCondList,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter1 = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter1)){
					GenericValue payrollItems1 = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter1);
					basicAmount = (BigDecimal)payrollItems1.get("amount");
					if(UtilValidate.isNotEmpty(basicAmount)){
						Map activeEmployeeDetails = PayrollService.getEmployeePayrollCondParms(dctx, UtilMisc.toMap("employeeId",employeeId,"timePeriodStart",ctpFromDate,"timePeriodEnd" ,ctpThruDate ,"userLogin",userLogin));
						if(UtilValidate.isNotEmpty(activeEmployeeDetails)){
						String activeGeoId = (String)activeEmployeeDetails.get("geoId");
						daArrearsData = delegator.findByAnd("EmployeeDAArrears", [periodBillingId: periodBillingIdParam]);
						if(UtilValidate.isNotEmpty(daArrearsData)){
						  if(UtilValidate.isNotEmpty(daArrearsData[0].geoId)){
							  geoId = daArrearsData[0].geoId;
							  if(geoId.equals(activeGeoId)){
								  String rateTypeId = null;
								  BigDecimal oldRateDaAmount = BigDecimal.ZERO;
								  if(UtilValidate.isNotEmpty(activeGeoId)){
									  if(activeGeoId.equals("BAGALKOT")){
										  rateTypeId = "DA_BAGALKOT_RATE";
									  }
									  if(activeGeoId.equals("BELL")){
										  rateTypeId = "DA_BELL_RATE";
									  }
									  if(activeGeoId.equals("BGLR")){
										  rateTypeId = "DA_BGLR_RATE";
									  }
									  if(activeGeoId.equals("DRWD")){
										  rateTypeId = "DA_DRWD_RATE";
									  }
									  if(activeGeoId.equals("GULB")){
										  rateTypeId = "DA_GULB_RATE";
									  }
									  if(UtilValidate.isNotEmpty(rateTypeId)){
										  List rateAmountCondList = FastList.newInstance();
										  rateAmountCondList.add(EntityCondition.makeCondition("rateTypeId" ,EntityOperator.EQUALS , rateTypeId));
										  rateAmountCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalPeriod.getDate("fromDate")))));
										  rateAmountCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(basicSalPeriod.getDate("thruDate"))))));
										  EntityCondition rateAmountCond = EntityCondition.makeCondition(rateAmountCondList,EntityOperator.AND);
										  List<GenericValue> rateAmountList = delegator.findList("RateAmount", rateAmountCond,null, UtilMisc.toList("fromDate"), null, false);
										  if(UtilValidate.isNotEmpty(rateAmountList)){
											  GenericValue rateAmountGen = EntityUtil.getFirst(rateAmountList);
											  rateAmount = (BigDecimal) rateAmountGen.get("rateAmount");
											  if(UtilValidate.isNotEmpty(rateAmount)){
												  if(UtilValidate.isNotEmpty(basicAmount)){
													  newDAAmount = (rateAmount*basicAmount);
												  }
												  
												  //if(ctpFromDate.compareTo(startMonth) > 0 && ctpFromDate.compareTo(endMonth) < 0){
													  List oldRateAmountCondList = FastList.newInstance();
													  oldRateAmountCondList.add(EntityCondition.makeCondition("rateTypeId" ,EntityOperator.EQUALS , rateTypeId));
													  oldRateAmountCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin));
													  oldRateAmountCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalPeriod.getDate("fromDate")))));
													  EntityCondition oldRateAmountCond = EntityCondition.makeCondition(oldRateAmountCondList,EntityOperator.AND);
													  
													  List<GenericValue> oldRateAmountList = delegator.findList("RateAmount", oldRateAmountCond,null, UtilMisc.toList("fromDate"), null, false);
													  BigDecimal oldRateAmount = BigDecimal.ZERO;
													  
													  if(UtilValidate.isNotEmpty(oldRateAmountList)){
														  GenericValue rateAmountOldGen = EntityUtil.getFirst(oldRateAmountList);
														  oldRateAmount = (BigDecimal) rateAmountOldGen.get("rateAmount");
													  }
													  if(UtilValidate.isNotEmpty(basicAmount)){
														  oldDAAmount = (oldRateAmount*basicAmount);
													  }
												  //}
													  
												  BigDecimal payAmt = BigDecimal.ZERO;
												  payAmt = oldDAAmount;
												  /*if(ctpFromDate.compareTo(startMonth) > 0 && ctpFromDate.compareTo(endMonth) < 0){
													  payAmt = oldDAAmount;
												  }else{
												   		List payHeadConditionList = FastList.newInstance();
														payHeadConditionList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId));
														payHeadConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_DA"));
														payHeadConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
														EntityCondition payHeadCondition = EntityCondition.makeCondition(payHeadConditionList,EntityOperator.AND);
														List<GenericValue> payrollHeaderAndHeaderItemIter = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCondition, null, null, null, false);
														//Debug.log("payrollHeaderAndHeaderItemIter====="+payrollHeaderAndHeaderItemIter);
														//BigDecimal payAmt=BigDecimal.ZERO;
														if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter)){
														  GenericValue payrollItems = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter);
														  payAmt = (BigDecimal)payrollItems.get("amount");
													  }
												  }*/
												  BigDecimal roundedPayAmt = BigDecimal.ZERO;
												  roundedPayAmt = payAmt.setScale(0,BigDecimal.ROUND_HALF_DOWN);
												  
												  BigDecimal roundedNewDAAmount = BigDecimal.ZERO;
												  roundedNewDAAmount = newDAAmount.setScale(0,BigDecimal.ROUND_HALF_DOWN);
												  
												  if(UtilValidate.isNotEmpty(newDAAmount)){
													  netDAAmount  = roundedNewDAAmount - roundedPayAmt;
													  totalNetAmt = totalNetAmt + netDAAmount;
												  }
												  
												  tempMap = [:];
												  tempMap.put("Basic",basicAmount.setScale(0,BigDecimal.ROUND_HALF_DOWN));
												  tempMap.put("oldDA",payAmt.setScale(0,BigDecimal.ROUND_HALF_DOWN));
												  tempMap.put("newDA",newDAAmount.setScale(0,BigDecimal.ROUND_HALF_DOWN));
												  tempMap.put("netDA",netDAAmount.setScale(0,BigDecimal.ROUND_HALF_DOWN));
												  fromDateStart = basicSalDate;
												  thruDateEnd = UtilDateTime.getDayEnd(basicSalDate);
												  BigDecimal EpfAmount = BigDecimal.ZERO;
												  
												  /*person = delegator.findOne("Person", [partyId : employeeId], false);
												  String age = "";
												  if(UtilValidate.isNotEmpty(person)){
													  if(UtilValidate.isNotEmpty(person.getDate("birthDate"))){
														  ageTime = (UtilDateTime.toSqlDate(monthEnd)).getTime()- (person.getDate("birthDate")).getTime();
														  age = new Long((new BigDecimal((TimeUnit.MILLISECONDS.toDays(ageTime))).divide(new BigDecimal(365),0,BigDecimal.ROUND_DOWN)).toString());
													  }
												  }
												  
												  BigDecimal emplAge = new BigDecimal(age);*/
												  
												  //if((emplAge.compareTo(new BigDecimal(58))) <= 0){
												  if(UtilValidate.isNotEmpty(periodBillingIdParam)){
													  List payHeadCondListEpf = FastList.newInstance();
													  payHeadCondListEpf.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingIdParam));
													  if(UtilValidate.isNotEmpty(retireEmplList)){
														  payHeadCondListEpf.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId), EntityOperator.AND,
															  EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_IN, retireEmplList)));
													  }else{
													  	payHeadCondListEpf.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
													  }
													  payHeadCondListEpf.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_PF"));
													  payHeadCondEpf = EntityCondition.makeCondition(payHeadCondListEpf,EntityOperator.AND);
													  EpfPayHeadList = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCondEpf, null, null, null, false);
													  if(UtilValidate.isNotEmpty(EpfPayHeadList)){
														  EpfHead = EntityUtil.getFirst(EpfPayHeadList);
														  if(UtilValidate.isNotEmpty(EpfHead)){
															  EpfAmount = EpfHead.amount;
															  if(UtilValidate.isEmpty(EpfAmount)){
																  EpfAmount = 0;
															  }
														  }
													  }
													  tempMap.putAt("EpfAmount", -(EpfAmount.setScale(0,BigDecimal.ROUND_HALF_UP)));
												  }
												  //}
												  if(UtilValidate.isNotEmpty(tempMap)){
													  periodMap.putAt(ctpId, tempMap);
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
				}
			}
		}
		//Leave Encashment Here
		List condBasicSalPeriodList1 = FastList.newInstance();
		condBasicSalPeriodList1.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"SP_LEAVE_ENCASH"));
		condBasicSalPeriodList1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"APPROVED"));
		condBasicSalPeriodList1.add(EntityCondition.makeCondition("basicSalDate", EntityOperator.GREATER_THAN_EQUAL_TO ,UtilDateTime.getDayStart(fromDateTime)));
		condBasicSalPeriodList1.add(EntityCondition.makeCondition("basicSalDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.getDayEnd(thruDateTime)));
		EntityCondition basicSalPeriodCond1 = EntityCondition.makeCondition(condBasicSalPeriodList1,EntityOperator.AND);
		List basicSalPeriodList1 = delegator.findList("PeriodBillingAndCustomTimePeriod", basicSalPeriodCond1, null, null, null, false);
		periodBillingIdList1 = EntityUtil.getFieldListFromEntityList(basicSalPeriodList1, "periodBillingId", true);
		if(UtilValidate.isNotEmpty(periodBillingIdList1)){
			
			periodBillingIdList1.each{ periodBillingId1->
				
				periodBillingEach1 = delegator.findOne("PeriodBilling",[periodBillingId : periodBillingId1] , false);
				ctpId1 = periodBillingEach1.customTimePeriodId;
				
				ctpDA1 = delegator.findOne("CustomTimePeriod",[customTimePeriodId : ctpId1] , false);
				Timestamp ctpFromDate1=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(ctpDA1.getDate("fromDate")));
				Timestamp ctpThruDate1=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(ctpDA1.getDate("thruDate")));
				
				GenericValue LEbasicSalPeriod = null;
				List basicSalPeriodcondList1 = FastList.newInstance();
				basicSalPeriodcondList1.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
				basicSalPeriodcondList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayEnd(basicSalDate))));
				basicSalPeriodcondList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.getDayStart(basicSalDate))));
				basicSalCond1 = EntityCondition.makeCondition(basicSalPeriodcondList1,EntityOperator.AND);
				LEbasicSalPeriodDetails = delegator.findList("CustomTimePeriod", basicSalCond1, null, null, null, false);
				if(UtilValidate.isNotEmpty(LEbasicSalPeriodDetails)){
					LEbasicSalPeriod = EntityUtil.getFirst(LEbasicSalPeriodDetails);
				}
				
				BigDecimal LEpayAmt=BigDecimal.ZERO;
				basicLEDate = periodBillingEach1.basicSalDate;
				
				BigDecimal oldDAAmount1 = BigDecimal.ZERO;
				BigDecimal rateAmount1 = BigDecimal.ZERO;
				BigDecimal newDAAmount1 = BigDecimal.ZERO;
				BigDecimal netDAAmount1 = BigDecimal.ZERO;
				
				List payHeadCondList1 = FastList.newInstance();
				payHeadCondList1.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId1));
				payHeadCondList1.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_DA"));
				payHeadCondList1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond2 = EntityCondition.makeCondition(payHeadCondList1,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter2 = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond2, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter2)){
					GenericValue payrollItems2 = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter2);
					oldDAAmount1 = (BigDecimal)payrollItems2.get("amount");
				}
				payHeadCondList1.clear();
				payHeadCondList1.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId1));
				payHeadCondList1.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_SALARY"));
				payHeadCondList1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond3 = EntityCondition.makeCondition(payHeadCondList1,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter3 = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond3, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter3)){
					GenericValue payrollItems3 = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter3);
					basicAmount1 = (BigDecimal)payrollItems3.get("amount");
					if(UtilValidate.isNotEmpty(basicAmount1)){
						Map activeEmployeeDetails = PayrollService.getEmployeePayrollCondParms(dctx, UtilMisc.toMap("employeeId",employeeId,"timePeriodStart",ctpFromDate1,"timePeriodEnd" ,ctpThruDate1 ,"userLogin",userLogin));
						if(UtilValidate.isNotEmpty(activeEmployeeDetails)){
						String activeGeoId1 = (String)activeEmployeeDetails.get("geoId");
						daArrearsData1 = delegator.findByAnd("EmployeeDAArrears", [periodBillingId: periodBillingIdParam]);
						if(UtilValidate.isNotEmpty(daArrearsData1)){
						  if(UtilValidate.isNotEmpty(daArrearsData1[0].geoId)){
							  geoId1 = daArrearsData1[0].geoId;
							  if(geoId1.equals(activeGeoId1)){
								  String rateTypeId1 = null;
								  if(UtilValidate.isNotEmpty(activeGeoId1)){
									  if(activeGeoId1.equals("BAGALKOT")){
										  rateTypeId1 = "DA_BAGALKOT_RATE";
									  }
									  if(activeGeoId1.equals("BELL")){
										  rateTypeId1 = "DA_BELL_RATE";
									  }
									  if(activeGeoId1.equals("BGLR")){
										  rateTypeId1 = "DA_BGLR_RATE";
									  }
									  if(activeGeoId1.equals("DRWD")){
										  rateTypeId1 = "DA_DRWD_RATE";
									  }
									  if(activeGeoId1.equals("GULB")){
										  rateTypeId1 = "DA_GULB_RATE";
									  }
									  if(UtilValidate.isNotEmpty(rateTypeId1)){
										  List rateAmountCondList1 = FastList.newInstance();
										  rateAmountCondList1.add(EntityCondition.makeCondition("rateTypeId" ,EntityOperator.EQUALS , rateTypeId1));
										  rateAmountCondList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(LEbasicSalPeriod.getDate("fromDate")))));
										  rateAmountCondList1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(LEbasicSalPeriod.getDate("thruDate"))))));
										  EntityCondition rateAmountCond1 = EntityCondition.makeCondition(rateAmountCondList1,EntityOperator.AND);
										  List<GenericValue> rateAmountList1 = delegator.findList("RateAmount", rateAmountCond1,null, UtilMisc.toList("fromDate"), null, false);
										  if(UtilValidate.isNotEmpty(rateAmountList1)){
											  GenericValue rateAmountGen1 = EntityUtil.getFirst(rateAmountList1);
											  rateAmount1 = (BigDecimal) rateAmountGen1.get("rateAmount");
											  if(UtilValidate.isNotEmpty(rateAmount1)){
												  newDAAmount1 = (rateAmount1*basicAmount1);
											  }
											  if(oldDADate.compareTo(monthBegin) > 0 && oldDADate.compareTo(monthEnd) < 0){
												  List oldRateAmountCondList1 = FastList.newInstance();
												  oldRateAmountCondList1.add(EntityCondition.makeCondition("rateTypeId" ,EntityOperator.EQUALS , rateTypeId1));
												  oldRateAmountCondList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalPeriod1.getDate("fromDate")))));
												  oldRateAmountCondList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(LEbasicSalPeriod.getDate("fromDate")))));
												  EntityCondition oldRateAmountCond1 = EntityCondition.makeCondition(oldRateAmountCondList1,EntityOperator.AND);
												  List<GenericValue> oldRateAmountList1 = delegator.findList("RateAmount", oldRateAmountCond1,null, UtilMisc.toList("fromDate"), null, false);
												  BigDecimal oldRateAmount1 = BigDecimal.ZERO;
												  if(UtilValidate.isNotEmpty(oldRateAmountList1)){
													  GenericValue rateAmountOldGen1 = EntityUtil.getFirst(oldRateAmountList1);
													  oldRateAmount1 = (BigDecimal) rateAmountOldGen1.get("rateAmount");
												  }
												  if(UtilValidate.isNotEmpty(basicAmount1)){
													  oldDAAmount1 = (oldRateAmount1*basicAmount1);
												  }
											  }
											  /*if(oldDADate.compareTo(monthBegin) > 0 && oldDADate.compareTo(monthEnd) < 0){
													  LEpayAmt = oldDAAmount1;
											  }else{
												  if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter3)){
													  GenericValue LEpayrollItems = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter3);
													  LEpayAmt = (BigDecimal)LEpayrollItems.get("amount");
												  }
											  }*/
											  
											  LEpayAmt = oldDAAmount1;
											  
											  BigDecimal roundedLEpayAmt = BigDecimal.ZERO;
											  roundedLEpayAmt = LEpayAmt.setScale(0,BigDecimal.ROUND_HALF_DOWN);
											  
											  BigDecimal roundedNewDAAmount1 = BigDecimal.ZERO;
											  roundedNewDAAmount1 = newDAAmount1.setScale(0,BigDecimal.ROUND_HALF_DOWN);
											  
											  if(UtilValidate.isNotEmpty(roundedNewDAAmount1 )){
												  netDAAmount1  = roundedNewDAAmount1 - roundedLEpayAmt;
												  totalNetAmt = totalNetAmt + netDAAmount1;
											  }
											  tempMap1 = [:];
											  tempMap1.put("Basic1",basicAmount1.setScale(0,BigDecimal.ROUND_HALF_DOWN));
											  tempMap1.put("oldDA1",LEpayAmt.setScale(0,BigDecimal.ROUND_HALF_DOWN));
											  tempMap1.put("newDA1",newDAAmount1.setScale(0,BigDecimal.ROUND_HALF_DOWN));
											  tempMap1.put("netDA1",netDAAmount1.setScale(0,BigDecimal.ROUND_HALF_DOWN));
											  if(UtilValidate.isNotEmpty(tempMap1)){
												  leaveEncashMap.putAt(basicLEDate, tempMap1);
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
			}
		}
	}
	if(UtilValidate.isNotEmpty(totalNetAmt)){
		totalNetMap.put(employeeId, totalNetAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
	}
	if(UtilValidate.isNotEmpty(periodMap)){
		DAArrearMap.put(employeeId,periodMap);
	}
	if(UtilValidate.isNotEmpty(leaveEncashMap)){
		DAArrearLEMap.put(employeeId,leaveEncashMap);
	}
}

context.totalNetMap=totalNetMap;
context.DAArrearMap=DAArrearMap;
context.DAArrearLEMap = DAArrearLEMap;




