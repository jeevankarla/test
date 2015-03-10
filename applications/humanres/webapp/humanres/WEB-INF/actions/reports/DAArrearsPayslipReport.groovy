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


dctx = dispatcher.getDispatchContext();

periodBillingIdParam = parameters.periodBillingId;
customTimePeriodId = parameters.customTimePeriodId;
context.putAt("customTimePeriodId", customTimePeriodId);


employmentsList = [];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-60)));
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
DAArrearMap = [:];
DAArrearLEMap = [:];
employmentsList.each{ employeeId ->
	periodMap = [:];
	leaveEncashMap = [:];
	customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId] , false);
	if(UtilValidate.isNotEmpty(customTimePeriod)){
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
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
				
				
				List payHeadCondList = FastList.newInstance();
				payHeadCondList.add(EntityCondition.makeCondition("periodBillingId" ,EntityOperator.EQUALS , periodBillingId));
				payHeadCondList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_DA"));
				payHeadCondList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
				EntityCondition payHeadCond = EntityCondition.makeCondition(payHeadCondList,EntityOperator.AND);
				List<GenericValue> payrollHeaderAndHeaderItemIter = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollHeaderAndHeaderItemIter)){
					GenericValue payrollItems = EntityUtil.getFirst(payrollHeaderAndHeaderItemIter);
					oldDAAmount = (BigDecimal)payrollItems.get("amount");
				}
				payHeadCondList.clear();
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
						  if(UtilValidate.isNotEmpty(daArrearsData[0].geoId)){
							  geoId = daArrearsData[0].geoId;
							  if(geoId.equals(activeGeoId)){
								  String rateTypeId = null;
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
										  rateAmountCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalDate))));
										  rateAmountCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalDate)))));
										  EntityCondition rateAmountCond = EntityCondition.makeCondition(rateAmountCondList,EntityOperator.AND);
										  List<GenericValue> rateAmountList = delegator.findList("RateAmount", rateAmountCond,null, UtilMisc.toList("fromDate"), null, false);
										  if(UtilValidate.isNotEmpty(rateAmountList)){
											  GenericValue rateAmountGen = EntityUtil.getFirst(rateAmountList);
											  rateAmount = (BigDecimal) rateAmountGen.get("rateAmount");
											  if(UtilValidate.isNotEmpty(rateAmount)){
												  newDAAmount = (rateAmount*basicAmount);
												  if(UtilValidate.isNotEmpty(newDAAmount)){
													  netDAAmount  = newDAAmount - oldDAAmount;
												  }
												  tempMap = [:];
												  tempMap.put("Basic",basicAmount.setScale(0,BigDecimal.ROUND_HALF_UP));
												  tempMap.put("oldDA",oldDAAmount.setScale(0,BigDecimal.ROUND_HALF_UP));
												  tempMap.put("newDA",newDAAmount.setScale(0,BigDecimal.ROUND_HALF_UP));
												  tempMap.put("netDA",netDAAmount.setScale(0,BigDecimal.ROUND_HALF_UP));
												  fromDateStart = basicSalDate;
												  thruDateEnd = UtilDateTime.getDayEnd(basicSalDate);
												  BigDecimal EpfAmount = BigDecimal.ZERO;
												  if(UtilValidate.isNotEmpty(periodBillingIdParam)){
													  List payHeadCondListEpf = FastList.newInstance();
													  payHeadCondListEpf.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingIdParam));
													  payHeadCondListEpf.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
													  payHeadCondListEpf.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_PF"));
													  EntityCondition payHeadCondEpf = EntityCondition.makeCondition(payHeadCondListEpf,EntityOperator.AND);
													  List<GenericValue> EpfPayHeadList = delegator.findList("PayrollHeaderAndHeaderItem", payHeadCondEpf, null, null, null, false);
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
										  rateAmountCondList1.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalDate))));
										  rateAmountCondList1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(basicSalDate)))));
										  EntityCondition rateAmountCond1 = EntityCondition.makeCondition(rateAmountCondList1,EntityOperator.AND);
										  List<GenericValue> rateAmountList1 = delegator.findList("RateAmount", rateAmountCond1,null, UtilMisc.toList("fromDate"), null, false);
										  if(UtilValidate.isNotEmpty(rateAmountList1)){
											  GenericValue rateAmountGen1 = EntityUtil.getFirst(rateAmountList1);
											  rateAmount1 = (BigDecimal) rateAmountGen1.get("rateAmount");
											  
											  if(UtilValidate.isNotEmpty(rateAmount1)){
												  newDAAmount1 = (rateAmount1*basicAmount1);
												  if(UtilValidate.isNotEmpty(newDAAmount1 )){
													  netDAAmount1  = newDAAmount1 - oldDAAmount1;
												  }
												  tempMap1 = [:];
												  tempMap1.put("Basic1",basicAmount1.setScale(0,BigDecimal.ROUND_HALF_UP));
												  tempMap1.put("oldDA1",oldDAAmount1.setScale(0,BigDecimal.ROUND_HALF_UP));
												  tempMap1.put("newDA1",newDAAmount1.setScale(0,BigDecimal.ROUND_HALF_UP));
												  tempMap1.put("netDA1",netDAAmount1.setScale(0,BigDecimal.ROUND_HALF_UP));
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
	if(UtilValidate.isNotEmpty(periodMap)){
		DAArrearMap.put(employeeId,periodMap);
	}
	if(UtilValidate.isNotEmpty(leaveEncashMap)){
		DAArrearLEMap.put(employeeId,leaveEncashMap);
	}
}
context.DAArrearMap=DAArrearMap;
context.DAArrearLEMap = DAArrearLEMap;








