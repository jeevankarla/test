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
if (parameters.customTimePeriodId == null) {
	return;
}

dctx = dispatcher.getDispatchContext();
context.put("type",parameters.type);
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;

context.timePeriodEnd= timePeriodEnd;

if(UtilValidate.isEmpty(parameters.benefitTypeId)){
	if(UtilValidate.isEmpty(parameters.dedTypeId)){
		Debug.logError("Choose one of Benefit or deduction Type ids..","");
		context.errorMessage = "Choose one of Benefit or deduction Type ids..";
	}
}

Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);

if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
//getting benefits
benefitTypeList = delegator.findList("BenefitType", EntityCondition.makeCondition("benefitTypeId", EntityOperator.NOT_EQUAL ,"PAYROL_BEN_SALARY"), null, ["sequenceNum"], null, false);
benefitDescMap=[:];
if(UtilValidate.isNotEmpty(benefitTypeList)){
	benefitTypeList.each{ benefit->
		benefitName =  benefit.get("benefitName");
		benefitType = benefit.get("benefitTypeId");
		benefitDescMap.put(benefitType,benefitName);
	}
}
benefitTypeIds = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);

if(benefitTypeIds.contains(parameters.benefitTypeId)){
	benefitTypeIds=UtilMisc.toList(parameters.benefitTypeId);
}else{
	benefitTypeIds=benefitTypeIds;
}
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
if(dedTypeIds.contains(parameters.dedTypeId)){
	dedTypeIds=UtilMisc.toList(parameters.dedTypeId);
}else{
	dedTypeIds=dedTypeIds;
}
context.dedTypeIds=dedTypeIds;
context.dedDescMap=dedDescMap;

Map headerDetailsMap=FastMap.newInstance();
List benfitItemIdsList=FastList.newInstance();
Map benefitTypeFinalMap=FastMap.newInstance();
Map benefitWiseMap=FastMap.newInstance();
Map hederFinalBenfMap=FastMap.newInstance();
Map totalBenefitsMap=FastMap.newInstance();
Map deductionTypeValueMap=FastMap.newInstance();
List dedItemIdsList=FastList.newInstance();
Map deductionWiseMap=FastMap.newInstance();
Map hederFinalDedMap=FastMap.newInstance();
Map totalDeductionsMap=FastMap.newInstance();

JSONArray headBenefitItemsJSON = new JSONArray();
JSONArray headItemsJSON = new JSONArray();
payrollConditionList=[];
payrollConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
payrollConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
payrollCondition = EntityCondition.makeCondition(payrollConditionList,EntityOperator.AND);
payrollDetailsList = delegator.findList("PeriodBillingAndCustomTimePeriod", payrollCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(payrollDetailsList)){
	payrollDetailsList.each { payrollDetails->
		billingId=payrollDetails.get("periodBillingId");
		generatedConditionList=[];
		generatedConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,billingId));
		generatedConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN , employementIds));
		typecondition = EntityCondition.makeCondition(generatedConditionList,EntityOperator.AND);
		def orderBy = UtilMisc.toList("amount","partyIdFrom");
		partyList = delegator.findList("PayrollHeaderAndHeaderItem", typecondition, null, orderBy, null, false);
		if(UtilValidate.isNotEmpty(partyList)){
			partyList.each{ employee->
				employeeId= employee.partyIdFrom;
				amount= employee.amount;
				if(UtilValidate.isNotEmpty(amount)){
					amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
				}
				headerItemTypeId=employee.payrollHeaderItemTypeId;
				if("benefits".equals(parameters.type)){
					if(UtilValidate.isNotEmpty(amount)){
						if(parameters.benefitTypeId==headerItemTypeId){
							if(UtilValidate.isEmpty(benefitWiseMap.get(employeeId))){
								Map tempBenf=FastMap.newInstance();
								tempBenf.put(headerItemTypeId, amount);
								if(UtilValidate.isNotEmpty(tempBenf)){
									benefitWiseMap.put(employeeId,tempBenf);
								}
							}else{
								Map tempMap=FastMap.newInstance();
								tempMap.putAll(benefitWiseMap.get(employeeId));
								tempMap.put(headerItemTypeId,amount);
								benefitWiseMap.put(employeeId,tempMap);
							}
						}
						else{
							if(UtilValidate.isEmpty(hederFinalBenfMap.get(employeeId))){
								Map headBenf=FastMap.newInstance();
								headBenf.put(headerItemTypeId, amount);
								if(UtilValidate.isNotEmpty(headBenf)){
									hederFinalBenfMap.put(employeeId,headBenf);
								}
							}else{
									Map headBenfMap=FastMap.newInstance();
									headBenfMap.putAll(hederFinalBenfMap.get(employeeId));
									headBenfMap.put(headerItemTypeId,amount);
									hederFinalBenfMap.put(employeeId,headBenfMap);
							}
						}
						benfitItemIdsList.addAll(headerItemTypeId);
						if(UtilValidate.isEmpty(totalBenefitsMap[headerItemTypeId])){
							totalBenefitsMap[headerItemTypeId]=amount;
						}else{
							totalBenefitsMap[headerItemTypeId]+=amount;
						}
					}
					if(UtilValidate.isNotEmpty(amount)){
						amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
					}else{
						amount=" ";
					}
					if(UtilValidate.isNotEmpty(amount)){
						if(UtilValidate.isEmpty(benefitTypeFinalMap.get(employeeId))){
							Map tempBenefitMap=FastMap.newInstance();
							tempBenefitMap.put(headerItemTypeId,amount);
							benefitTypeFinalMap.put(employeeId,tempBenefitMap);
						}else{
							Map tempBenfMap=FastMap.newInstance();
								tempBenfMap.putAll(benefitTypeFinalMap.get(employeeId));
								tempBenfMap.put(headerItemTypeId,amount);
								benefitTypeFinalMap.put(employeeId,tempBenfMap);
						}
					}
				}
				else{
					if(UtilValidate.isNotEmpty(amount)){
						amount=amount*(-1);
								//this is for Benefits/Deductions Report
						if(parameters.dedTypeId==headerItemTypeId){
							if(UtilValidate.isEmpty(deductionWiseMap.get(employeeId))){
								Map tempDed=FastMap.newInstance();
								tempDed.put(headerItemTypeId, amount);
								if(UtilValidate.isNotEmpty(tempDed)){
									deductionWiseMap.put(employeeId,tempDed);
								}
							}else{
									Map tempMap=FastMap.newInstance();
									tempMap.putAll(deductionWiseMap.get(employeeId));
									tempMap.put(headerItemTypeId,amount);
									deductionWiseMap.put(employeeId,tempMap);
							}
						}else{
							if(UtilValidate.isEmpty(hederFinalDedMap.get(employeeId))){
								Map headDed=FastMap.newInstance();
								headDed.put(headerItemTypeId, amount);
								if(UtilValidate.isNotEmpty(headDed)){
									hederFinalDedMap.put(employeeId,headDed);
								}
							}else{
									Map headMap=FastMap.newInstance();
									headMap.putAll(hederFinalDedMap.get(employeeId));
									headMap.put(headerItemTypeId,amount);
									hederFinalDedMap.put(employeeId,headMap);
							}
						}
						dedItemIdsList.add(headerItemTypeId);
						if(UtilValidate.isEmpty(totalDeductionsMap[headerItemTypeId])){
							totalDeductionsMap[headerItemTypeId]=amount;
						}else{
							totalDeductionsMap[headerItemTypeId]+=amount;
						}
						
					}
					if(UtilValidate.isNotEmpty(amount)){
						amount=amount*(-1);
						amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
					}else{
						amount=" ";
					}
					if(UtilValidate.isNotEmpty(amount)){
						amount=amount*(-1);
						if(UtilValidate.isEmpty(deductionTypeValueMap.get(employeeId))){
							Map tempDedMap=FastMap.newInstance();
							tempDedMap.put(headerItemTypeId,amount);
							deductionTypeValueMap.put(employeeId,tempDedMap);
						}else{
							Map tempDedWiseMap=FastMap.newInstance();
								tempDedWiseMap.putAll(deductionTypeValueMap.get(employeeId));
								tempDedWiseMap.put(headerItemTypeId,amount);
								deductionTypeValueMap.put(employeeId,tempDedWiseMap);
						}
					}
				}
			}
		}
		Set benefitIds = new HashSet(benfitItemIdsList);
		List benfitIdsList =  benefitIds.toList();
		benfitItemIdsList=benfitIdsList;
		//Debug.logError("benefitTypeFinalMap="+benefitTypeFinalMap,"");
		if(UtilValidate.isNotEmpty(benefitTypeFinalMap)){
			Iterator BenfIter = benefitTypeFinalMap.entrySet().iterator();
			while(BenfIter.hasNext()){
				Map.Entry entry = BenfIter.next();
				emplyId= entry.getKey();
				JSONObject newObj = new JSONObject();
				partyName=PartyHelper.getPartyName(delegator, emplyId, false);
				departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
				deptName="";
				if(departmentDetails){
					deptPartyId=departmentDetails[0].partyIdFrom;
					deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
				}
				newObj.put("id",emplyId+"["+partyName+"]");
				newObj.put("partyId",emplyId);
				newObj.put("periodId",parameters.customTimePeriodId);
				if(UtilValidate.isNotEmpty(deptName)){
					newObj.put("deptName",deptName);
				}
				if(UtilValidate.isNotEmpty(entry.getValue())){
					Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
					while(headerItemIter.hasNext()){
						Map.Entry itemEntry = headerItemIter.next();
						benefitAmt=((itemEntry.getValue()));
						newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
					}
				}
				headBenefitItemsJSON.add(newObj);
			}
		}
		Set deductionIds = new HashSet(dedItemIdsList);
		List dedIdsList =  deductionIds.toList();
		dedItemIdsList=dedIdsList;
		//this is for report purpose
		if(UtilValidate.isNotEmpty(parameters.benefitTypeId)){
			benfitItemIdsList=UtilMisc.toList(parameters.benefitTypeId);
		}else{
			benfitItemIdsList=benfitItemIdsList;
		}
		if(UtilValidate.isNotEmpty(parameters.dedTypeId)){
			dedItemIdsList=UtilMisc.toList(parameters.dedTypeId);
		}else{
			dedItemIdsList=dedItemIdsList;
		}
		List quarterDedList = UtilMisc.toList("PAYROL_DD_ELECT","PAYROL_DD_WATR");
		if(UtilValidate.isNotEmpty(deductionTypeValueMap)){
			Iterator dedIter = deductionTypeValueMap.entrySet().iterator();
			while(dedIter.hasNext()){
				Map.Entry entry = dedIter.next();
				emplyId= entry.getKey();
				JSONObject newObj = new JSONObject();
				partyName=PartyHelper.getPartyName(delegator, emplyId, false);
				newObj.put("id",emplyId+"["+partyName+"]");
				partyName=PartyHelper.getPartyName(delegator, emplyId, false);
				departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
				empDetails = delegator.findOne("EmployeeDetail", [partyId : emplyId],true);
				if(UtilValidate.isNotEmpty(empDetails) && UtilValidate.isEmpty(empDetails.quarterType) && UtilValidate.isNotEmpty(parameters.dedTypeId) && (quarterDedList.contains(parameters.dedTypeId))){
					continue;
				}
				deptName="";
				if(departmentDetails){
					deptPartyId=departmentDetails[0].partyIdFrom;
					deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
				}
				if(UtilValidate.isNotEmpty(deptName)){
					newObj.put("deptName",deptName);
				}
				newObj.put("periodId",parameters.customTimePeriodId);
				newObj.put("partyId",emplyId);
				if(UtilValidate.isNotEmpty(entry.getValue())){
					Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
					while(headerItemIter.hasNext()){
						Map.Entry itemEntry = headerItemIter.next();
						newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
					}
				}
				headItemsJSON.add(newObj);
			}
		}
	}
}
else{
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , employementIds));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("cost","partyIdTo");
	List<GenericValue> partyBenefitList = delegator.findList("PartyBenefit", condition, null, orderBy, null, false);
	List<GenericValue> partyDeductionList = delegator.findList("PartyDeduction", condition, null, orderBy, null, false);
	if(UtilValidate.isNotEmpty(partyBenefitList)){
		partyBenefitList.each{ partyBenefit->
			employeeId= partyBenefit.partyIdTo;
			amount= partyBenefit.cost;
			if(UtilValidate.isNotEmpty(amount)){
				amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
			}
			headerItemTypeId=partyBenefit.benefitTypeId;
			if(UtilValidate.isNotEmpty(amount)){
				//this is for Benefits/Deductions Report
				if(parameters.benefitTypeId==headerItemTypeId){
					if(UtilValidate.isEmpty(benefitWiseMap.get(employeeId))){
						Map tempBenf=FastMap.newInstance();
						tempBenf.put(headerItemTypeId, amount);
						if(UtilValidate.isNotEmpty(tempBenf)){
							benefitWiseMap.put(employeeId,tempBenf);
						}
					}else{
							Map tempMap=FastMap.newInstance();
							tempMap.putAll(benefitWiseMap.get(employeeId));
							tempMap.put(headerItemTypeId,amount);
							benefitWiseMap.put(employeeId,tempMap);
					}
				}else{
					if(UtilValidate.isEmpty(hederFinalBenfMap.get(employeeId))){
						Map headBenf=FastMap.newInstance();
						headBenf.put(headerItemTypeId, amount);
						if(UtilValidate.isNotEmpty(headBenf)){
							hederFinalBenfMap.put(employeeId,headBenf);
						}
					}else{
							Map headBenfMap=FastMap.newInstance();
							headBenfMap.putAll(hederFinalBenfMap.get(employeeId));
							headBenfMap.put(headerItemTypeId,amount);
							hederFinalBenfMap.put(employeeId,headBenfMap);
					}
				}
				benfitItemIdsList.addAll(headerItemTypeId);
				if(UtilValidate.isEmpty(totalBenefitsMap[headerItemTypeId])){
					totalBenefitsMap[headerItemTypeId]=amount;
				}else{
					totalBenefitsMap[headerItemTypeId]+=amount;
				}
			}
			if(UtilValidate.isNotEmpty(amount)){
				amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
			}else{
				amount=" ";
			}
			if(UtilValidate.isNotEmpty(amount)){
				if(UtilValidate.isEmpty(benefitTypeFinalMap.get(employeeId))){
					Map tempBenefitMap=FastMap.newInstance();
					tempBenefitMap.put(headerItemTypeId,amount);
					benefitTypeFinalMap.put(employeeId,tempBenefitMap);
				}else{
					Map tempBenfMap=FastMap.newInstance();
						tempBenfMap.putAll(benefitTypeFinalMap.get(employeeId));
						tempBenfMap.put(headerItemTypeId,amount);
						benefitTypeFinalMap.put(employeeId,tempBenfMap);
				}
			}
		}
	}
	Set benefitIds = new HashSet(benfitItemIdsList);
	List benfitIdsList =  benefitIds.toList();
	benfitItemIdsList=benfitIdsList;
	//Debug.logError("benefitTypeFinalMap="+benefitTypeFinalMap,"");
	if(UtilValidate.isNotEmpty(benefitTypeFinalMap)){
		Iterator BenfIter = benefitTypeFinalMap.entrySet().iterator();
		while(BenfIter.hasNext()){
			Map.Entry entry = BenfIter.next();
			emplyId= entry.getKey();
			JSONObject newObj = new JSONObject();
			partyName=PartyHelper.getPartyName(delegator, emplyId, false);
			departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
			deptName="";
			if(departmentDetails){
				deptPartyId=departmentDetails[0].partyIdFrom;
				deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
			}
			newObj.put("id",emplyId+"["+partyName+"]");
			newObj.put("partyId",emplyId);
			newObj.put("periodId",parameters.customTimePeriodId);
			if(UtilValidate.isNotEmpty(deptName)){
				newObj.put("deptName",deptName);
			}
			if(UtilValidate.isNotEmpty(entry.getValue())){
				Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
				while(headerItemIter.hasNext()){
					Map.Entry itemEntry = headerItemIter.next();
					benefitAmt=((itemEntry.getValue()));
					newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
				}
			}
			headBenefitItemsJSON.add(newObj);
		}
	}
	if(UtilValidate.isNotEmpty(partyDeductionList)){
		partyDeductionList.each{ partyDed->
			employeeId= partyDed.partyIdTo;
			amount= partyDed.cost;
			if(UtilValidate.isNotEmpty(amount)){
				amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
			}
			headerItemTypeId=partyDed.deductionTypeId;
			if(UtilValidate.isNotEmpty(amount)){
				//this is for Benefits/Deductions Report
				if(parameters.dedTypeId==headerItemTypeId){
					if(UtilValidate.isEmpty(deductionWiseMap.get(employeeId))){
						Map tempDed=FastMap.newInstance();
						tempDed.put(headerItemTypeId, amount);
						if(UtilValidate.isNotEmpty(tempDed)){
							deductionWiseMap.put(employeeId,tempDed);
						}
					}else{
							Map tempMap=FastMap.newInstance();
							tempMap.putAll(deductionWiseMap.get(employeeId));
							tempMap.put(headerItemTypeId,amount);
							deductionWiseMap.put(employeeId,tempMap);
					}
				}else{
					if(UtilValidate.isEmpty(hederFinalDedMap.get(employeeId))){
						Map headDed=FastMap.newInstance();
						headDed.put(headerItemTypeId, amount);
						if(UtilValidate.isNotEmpty(headDed)){
							hederFinalDedMap.put(employeeId,headDed);
						}
					}else{
							Map headMap=FastMap.newInstance();
							headMap.putAll(hederFinalDedMap.get(employeeId));
							headMap.put(headerItemTypeId,amount);
							hederFinalDedMap.put(employeeId,headMap);
					}
				}
				dedItemIdsList.add(headerItemTypeId);
				if(UtilValidate.isEmpty(totalDeductionsMap[headerItemTypeId])){
					totalDeductionsMap[headerItemTypeId]=amount;
				}else{
					totalDeductionsMap[headerItemTypeId]+=amount;
				}
				
			}
			if(UtilValidate.isNotEmpty(amount)){
				amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
			}else{
				amount=" ";
			}
			if(UtilValidate.isNotEmpty(amount)){
				if(UtilValidate.isEmpty(deductionTypeValueMap.get(employeeId))){
					Map tempDedMap=FastMap.newInstance();
					tempDedMap.put(headerItemTypeId,amount);
					deductionTypeValueMap.put(employeeId,tempDedMap);
				}else{
					Map tempDedWiseMap=FastMap.newInstance();
						tempDedWiseMap.putAll(deductionTypeValueMap.get(employeeId));
						tempDedWiseMap.put(headerItemTypeId,amount);
						deductionTypeValueMap.put(employeeId,tempDedWiseMap);
				}
			}
		}
	}
	Set deductionIds = new HashSet(dedItemIdsList);
	List dedIdsList =  deductionIds.toList();
	dedItemIdsList=dedIdsList;
	//this is for report purpose
	if(UtilValidate.isNotEmpty(parameters.benefitTypeId)){
		benfitItemIdsList=UtilMisc.toList(parameters.benefitTypeId);
	}else{
		benfitItemIdsList=benfitItemIdsList;
	}
	if(UtilValidate.isNotEmpty(parameters.dedTypeId)){
		dedItemIdsList=UtilMisc.toList(parameters.dedTypeId);
	}else{
		dedItemIdsList=dedItemIdsList;
	}
	List quarterDedList = UtilMisc.toList("PAYROL_DD_ELECT","PAYROL_DD_WATR");
	if(UtilValidate.isNotEmpty(deductionTypeValueMap)){
		Iterator dedIter = deductionTypeValueMap.entrySet().iterator();
		while(dedIter.hasNext()){
			Map.Entry entry = dedIter.next();
			emplyId= entry.getKey();
			JSONObject newObj = new JSONObject();
			partyName=PartyHelper.getPartyName(delegator, emplyId, false);
			newObj.put("id",emplyId+"["+partyName+"]");
			partyName=PartyHelper.getPartyName(delegator, emplyId, false);
			departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
			empDetails = delegator.findOne("EmployeeDetail", [partyId : emplyId],true);
			if(UtilValidate.isNotEmpty(empDetails) && UtilValidate.isEmpty(empDetails.quarterType) && UtilValidate.isNotEmpty(parameters.dedTypeId) && (quarterDedList.contains(parameters.dedTypeId))){
				continue;
			}
			deptName="";
			if(departmentDetails){
				deptPartyId=departmentDetails[0].partyIdFrom;
				deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
			}
			if(UtilValidate.isNotEmpty(deptName)){
				newObj.put("deptName",deptName);
			}
			newObj.put("periodId",parameters.customTimePeriodId);
			newObj.put("partyId",emplyId);
			if(UtilValidate.isNotEmpty(entry.getValue())){
				Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
				while(headerItemIter.hasNext()){
					Map.Entry itemEntry = headerItemIter.next();
					newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
				}
			}
			headItemsJSON.add(newObj);
		}
	}
}
if("benefits".equals(parameters.type)){
	context.headItemsJson=headBenefitItemsJSON;
	if(UtilValidate.isNotEmpty(parameters.benefitTypeId)){
		if(UtilValidate.isNotEmpty(benefitWiseMap)){
			context.headerDetailsMap=benefitWiseMap;
		}
	}else{
		if(UtilValidate.isNotEmpty(hederFinalBenfMap)){
			context.headerDetailsMap=hederFinalBenfMap;
		}
	}
	context.headerItemIdsList=benfitItemIdsList;
	context.totalBenefitsMap=totalBenefitsMap;
}else{
	context.headItemsJson=headItemsJSON;
	if(UtilValidate.isNotEmpty(parameters.dedTypeId)){
		if(UtilValidate.isNotEmpty(deductionWiseMap)){
			context.headerDetailsMap=deductionWiseMap;
		}
	}else{
		if(UtilValidate.isNotEmpty(deductionWiseMap)){
			context.headerDetailsMap=hederFinalDedMap;
		}
	}
	context.headerItemIdsList=dedItemIdsList;
	context.totalDeductionsMap=totalDeductionsMap;
}
//Debug.logError("context.headItemsJson="+context.headItemsJson,"");


