import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
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
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;
dctx = dispatcher.getDispatchContext();

periodList = [];
customTimePeriodId=parameters.customTimePeriodId;

if (UtilValidate.isEmpty(customTimePeriodId)) {
	Debug.logError("customTimePeriodId cannot be empty");
	context.errorMessage = "customTimePeriodId cannot be empty";
	return;
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;
context.timePeriodEnd= timePeriodEnd;

deductionTypeList = delegator.findList("DeductionType", null, null, ["sequenceNum"], null, false);
dedTypeIds = EntityUtil.getFieldListFromEntityList(deductionTypeList, "deductionTypeId", true);
if(dedTypeIds.contains(parameters.dedTypeId)){
	dedTypeIds=UtilMisc.toList(parameters.dedTypeId);
}else{
	dedTypeIds=dedTypeIds;
}
context.dedTypeIds=dedTypeIds;

emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
employmentsList = [];
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
deductionList = [];
Map allDeductionMap=FastMap.newInstance();
periodBillingIdMap=[:];
List periodbillingConditionList=[];
periodbillingConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
periodbillingConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
periodbillingConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , parameters.partyId));
periodbillingCondition = EntityCondition.makeCondition(periodbillingConditionList,EntityOperator.AND);
BillingList = delegator.findList("PeriodBillingAndCustomTimePeriod", periodbillingCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(BillingList)){
	BillingId = BillingList.periodBillingId;
	dedTypeIds.each{ dedTypeId->
		if(UtilValidate.isNotEmpty(employmentsList)){
			periodTotalsMap=[:];
			employmentsList.each{ employeeId ->
				payrollHeaderList = [];
			detailsMap=[:];
			Map polacyDetailsMap = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(BillingId)){
					List headerConditionList=[];
					headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, BillingId));
					headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
					headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
					headerIdsList = delegator.findList("PayrollHeader", headerCondition, null, null, null, false);
					if(UtilValidate.isNotEmpty(headerIdsList)){
						customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",timePeriodStart,"thruDate",timePeriodEnd,"userLogin",userLogin)).get("periodTotalsForParty");
							if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
								Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
								while(customTimePeriodIter.hasNext()){
									Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
									if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
										periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
										Wages =0;
										basic = 0;
										dearnessAllowance =0;
										irAllowance =0;
										personalPay = 0;
										specialPay = 0;
										basic = periodTotals.get("PAYROL_BEN_SALARY");
										if(UtilValidate.isEmpty(basic)){
											basic = 0;
										}
										dearnessAllowance = periodTotals.get("PAYROL_BEN_DA");
										if(UtilValidate.isEmpty(dearnessAllowance)){
											dearnessAllowance = 0;
										}
										irAllowance = periodTotals.get("PAYROL_BEN_IR");
										if(UtilValidate.isEmpty(irAllowance)){
											irAllowance = 0;
										}
										personalPay = periodTotals.get("PAYROL_BEN_PNLPAY");
										if(UtilValidate.isEmpty(personalPay)){
											personalPay = 0;
										}
										specialPay = periodTotals.get("PAYROL_BEN_SPLPAY");
										if(UtilValidate.isEmpty(specialPay)){
											specialPay = 0;
										}
										Wages = basic+dearnessAllowance+irAllowance+personalPay+specialPay;
										detailsMap.put("Wages",Wages);
										employeeContribtn=0;
										employerContribtn=0;
										employerVolPf=0;
										pensionAmount = 0;
										headerIdsList.each{ headerId ->
											headerId = headerId.payrollHeaderId;
											employeCont=0;
											employerCont=0;
											empVolPf=0;
											pension=0;
											List employeeConditionList=[];
											employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
											employeeConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_EPF"));
											employeeCondition = EntityCondition.makeCondition(employeeConditionList,EntityOperator.AND);
											employeeList = delegator.findList("PayrollHeaderItem", employeeCondition, null, null, null, false);
											if(UtilValidate.isNotEmpty(employeeList)){
												employeeList.each{ empList ->
													employeCont = empList.amount;
													employeCont = employeCont.abs();
													employeeContribtn = employeeContribtn+employeCont;
												}
												detailsMap.put("employeeContribtn",employeeContribtn);
											}
											List volPfConditionList=[];
											volPfConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
											volPfConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_DD_VOLPF"));
											volPfCondition = EntityCondition.makeCondition(volPfConditionList,EntityOperator.AND);
											employerVolPfList = delegator.findList("PayrollHeaderItem", volPfCondition, null, null, null, false);
											if(UtilValidate.isNotEmpty(employerVolPfList)){
												employerVolPfList.each{ empVolPf ->
													empVolPf = empVolPf.amount;
													empVolPf = empVolPf.abs();
													employerVolPf = employerVolPf+empVolPf;
												}
												detailsMap.put("employerVolPf",employerVolPf);
											}
											List emplyrConditionList=[];
											emplyrConditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
											emplyrConditionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_PFEMPLYR"));
											emplyrCondition = EntityCondition.makeCondition(emplyrConditionList,EntityOperator.AND);
											employerList = delegator.findList("PayrollHeaderItemEc", emplyrCondition, null, null, null, false);
											if(UtilValidate.isNotEmpty(employerList)){
												employerList.each{ emplyrList ->
													employerCont = emplyrList.amount;
													employerContribtn = employerContribtn+employerCont;
												}
											}
											detailsMap.put("employerContribtn",employerContribtn);
											List pensionList=[];
											pensionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
											pensionList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, "PAYROL_BEN_PENS"));
											pensionCondition = EntityCondition.makeCondition(pensionList,EntityOperator.AND);
											pensionList = delegator.findList("PayrollHeaderItemEc", pensionCondition, null, null, null, false);
											if(UtilValidate.isNotEmpty(pensionList)){
												pensionList.each{ penList ->
													pension = penList.amount;
													pensionAmount = pensionAmount+pension;
												}
											}
											detailsMap.put("pensionAmount",pensionAmount);
										}
									conveyanceAmt= periodTotals.get("PAYROL_BEN_CCA");
									if(UtilValidate.isEmpty(conveyanceAmt)){
										conveyanceAmt = 0;
									}
									grossBenefitAmt = 0;
									grossAmt = (periodTotals.get("grossBenefitAmt"));
									if(UtilValidate.isNotEmpty(grossAmt)){
										gross = (periodTotals.get("grossBenefitAmt")-conveyanceAmt);
									}else{
										gross = 0;
									}
									detailsMap.put("gross",gross);
								}
							}
						}
						accountNo = 0;
						deductionAmt=0;
						balance = 0;
						gisNo = 0;
						requiredAmount = 0;
						headerIdsList.each{ headerId ->
							headerId = headerId.payrollHeaderId;
							deductionAmount = 0;
							closingBalance = 0;
							polNo = 0;
							premium = 0;
							List deductionsList=[];
							deductionsList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, headerId));
							deductionsList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, employeeId));
							deductionsList.add(EntityCondition.makeCondition("payrollHeaderItemTypeId", EntityOperator.EQUALS, dedTypeId));
							deductionsCondition = EntityCondition.makeCondition(deductionsList,EntityOperator.AND);
							payrollHeaderList = delegator.findList("PayrollHeaderAndHeaderItem", deductionsCondition, null, null, null, false);
							if(UtilValidate.isNotEmpty(payrollHeaderList)){
								payrollHeaderList.each{ payrollList ->
									deductionAmount = payrollList.amount;
									deductionAmount = deductionAmount.abs();
									deductionAmt = deductionAmt+deductionAmount;
								}
								deductionList.add(deductionAmt);
								detailsMap.put("deductionAmt",deductionAmt);
							}
						}
						List loanRecoveryList=[];
						loanRecoveryList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
						loanRecoveryList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, dedTypeId));
						loanRecoveryCondition = EntityCondition.makeCondition(loanRecoveryList,EntityOperator.AND);
						loansAndRecoveryList = delegator.findList("LoanAndRecoveryAndType", loanRecoveryCondition, null, null, null, false);
						if(UtilValidate.isNotEmpty(loansAndRecoveryList)){
							loansAndRecoveryList.each{ loanAndRecovery ->
								if(UtilValidate.isNotEmpty(loanAndRecovery)){
									accountNumber = loanAndRecovery.loanFinAccountId;
									closingBalance = loanAndRecovery.closingBalance;
									balance = balance+closingBalance;
								}
								if(UtilValidate.isNotEmpty(accountNumber)){
									accountNo = accountNumber.trim();
								}
							}
						}
						detailsMap.put("accountNo",accountNo);
						detailsMap.put("balance",balance);
						List partyInsuranceConditionList=[];
						partyInsuranceConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
						partyInsuranceConditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, dedTypeId));
						partyInsuranceCondition = EntityCondition.makeCondition(partyInsuranceConditionList,EntityOperator.AND);
						PartyInsuranceList = delegator.findList("PartyInsurance", partyInsuranceCondition, null, null, null, false);
						if(UtilValidate.isNotEmpty(PartyInsuranceList)){
							PartyInsuranceList.each{ PartyInsurance ->
								if(UtilValidate.isNotEmpty(PartyInsurance)){
									polNo = PartyInsurance.insuranceNumber;
									premium = PartyInsurance.premiumAmount;
								}
								polacyDetailsMap.put(polNo, premium);
								detailsMap.put("polDetails",polacyDetailsMap);
							}
						}
					}
				}
				if(UtilValidate.isNotEmpty(detailsMap) && UtilValidate.isNotEmpty(detailsMap.get("deductionAmt")) && detailsMap.get("deductionAmt") !=0){
					periodTotalsMap.put(employeeId,detailsMap);
				}
				deductionList = (new HashSet(deductionList)).toList();
			}
		}
		if(UtilValidate.isNotEmpty(periodTotalsMap)){
			allDeductionMap.put(dedTypeId,periodTotalsMap);
		}
	}
	context.put("allDeductionMap",allDeductionMap);
}else{
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employmentsList));
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , parameters.partyId));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("partyIdTo","cost");
	List<GenericValue> partyDeductionList = delegator.findList("PartyDeduction", condition, null, orderBy, null, false);
	periodTotalsMap=[:];
	if(UtilValidate.isNotEmpty(partyDeductionList)){
		partyDeductionList.each{ partyDed->
			deductionEmployeeId= partyDed.partyIdTo;
			headerItemTypeId=partyDed.deductionTypeId;
			dedTypeIds.each{ dedTypeId->
				if(dedTypeId==headerItemTypeId){
					if(UtilValidate.isNotEmpty(employmentsList)){
						employmentsList.each{ employeeId ->
							detailsMap=[:];
							Map polacyDetailsMap = FastMap.newInstance();
							if(deductionEmployeeId==employeeId){
								deductionAmount = 0;
								accountNo = 0;
								deductionAmt=0;
								gross = 0;
								balance = 0;
								closingBalance = 0;
								polNo = 0;
								premium = 0;
								customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employeeId,"fromDate",timePeriodStart,"thruDate",timePeriodEnd,"userLogin",userLogin)).get("periodTotalsForParty");
								if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
									Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
									while(customTimePeriodIter.hasNext()){
										Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
										if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
											periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
											Wages =0;
											basic = 0;
											dearnessAllowance =0;
											irAllowance =0;
											personalPay = 0;
											specialPay = 0;
											basic = periodTotals.get("PAYROL_BEN_SALARY");
											if(UtilValidate.isEmpty(basic)){
												basic = 0;
											}
											dearnessAllowance = periodTotals.get("PAYROL_BEN_DA");
											if(UtilValidate.isEmpty(dearnessAllowance)){
												dearnessAllowance = 0;
											}
											irAllowance = periodTotals.get("PAYROL_BEN_IR");
											if(UtilValidate.isEmpty(irAllowance)){
												irAllowance = 0;
											}
											personalPay = periodTotals.get("PAYROL_BEN_PNLPAY");
											if(UtilValidate.isEmpty(personalPay)){
												personalPay = 0;
											}
											specialPay = periodTotals.get("PAYROL_BEN_SPLPAY");
											if(UtilValidate.isEmpty(specialPay)){
												specialPay = 0;
											}
											Wages = basic+dearnessAllowance+irAllowance+personalPay+specialPay;
											detailsMap.put("Wages",Wages);
											deductionAmt= partyDed.cost;
											if(UtilValidate.isEmpty(deductionAmt)){
												deductionAmt = 0;
											}else{
												detailsMap.put("deductionAmt", deductionAmt);
											}
											grossBenefitAmt = 0;
											gross = periodTotals.get("grossBenefitAmt");
											if(UtilValidate.isEmpty(gross)){
												gross = 0;
											}
											detailsMap.put("gross",gross);
											List loanRecoveryList=[];
											loanRecoveryList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
											loanRecoveryList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, dedTypeId));
											loanRecoveryCondition = EntityCondition.makeCondition(loanRecoveryList,EntityOperator.AND);
											loansAndRecoveryList = delegator.findList("LoanAndRecoveryAndType", loanRecoveryCondition, null, null, null, false);
											loansAndRecoveryList.each{ loanAndRecovery ->
												if(UtilValidate.isNotEmpty(loanAndRecovery)){
													accountNumber = loanAndRecovery.loanFinAccountId;
													closingBalance = loanAndRecovery.closingBalance;
													balance = balance+closingBalance;
												}
												if(UtilValidate.isNotEmpty(accountNumber)){
													accountNo = accountNumber.trim();
												}
											}
											detailsMap.put("accountNo",accountNo);
											detailsMap.put("balance",balance);
											List partyInsuranceConditionList=[];
											partyInsuranceConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
											partyInsuranceConditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, dedTypeId));
											partyInsuranceCondition = EntityCondition.makeCondition(partyInsuranceConditionList,EntityOperator.AND);
											PartyInsuranceList = delegator.findList("PartyInsurance", partyInsuranceCondition, null, null, null, false);
											if(UtilValidate.isNotEmpty(PartyInsuranceList)){
												PartyInsuranceList.each{ PartyInsurance ->
													if(UtilValidate.isNotEmpty(PartyInsurance)){
														polNo = PartyInsurance.insuranceNumber;
														premium = PartyInsurance.premiumAmount;
													}
													polacyDetailsMap.put(polNo, premium);
													detailsMap.put("polDetails",polacyDetailsMap);
												}
											}
											gisNoDetails = [];
											gisNoDetails = delegator.findOne("EmployeeDetail",[partyId : employeeId ], false);
											if(UtilValidate.isNotEmpty(gisNoDetails)){
												gisNum = gisNoDetails.presentEpf;
											}
											if(UtilValidate.isNotEmpty(gisNum)){
												gisNo = gisNum.trim()
											}
											detailsMap.put("gisNo",gisNo);
										}
									}
								}
							}
							if(UtilValidate.isNotEmpty(detailsMap)){
								periodTotalsMap.put(employeeId,detailsMap);
							}
						}
					}
					if(UtilValidate.isNotEmpty(periodTotalsMap)){
						allDeductionMap.put(dedTypeId,periodTotalsMap);
					}
				}
				
			}
		}
	}
}
context.put("allDeductionMap",allDeductionMap);
Comparator mycomparator = Collections.sort(deductionList);
GisdetailsMap = [:];
gisDetailsList = allDeductionMap.get("PAYROL_DD_GIS");
employeeDetailsMap = [:];
deductionList.each{ dedList ->
	sum = 0;
	employeeList = [];
	gisDetailsList.each{ gisDetList ->
		tempMap = [:];
		employeeId = gisDetList.getKey();
		gisAmount = gisDetList.getValue().get("deductionAmt");
		emplMap = [:];
		if(dedList==gisAmount){
			sum = sum + gisAmount;
			temMap = [:];
			temMap.put("employeeId", employeeId);
			temMap.put("gisAmount", gisAmount);
			emplMap.putAll(temMap);
			employeeList.add(emplMap);
		}
	}
	employeeDetailsMap.put(dedList,employeeList);	
}
GisdetailsMap.put("PAYROL_DD_GIS",employeeDetailsMap);

context.put("GisdetailsMap",GisdetailsMap);
