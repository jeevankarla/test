import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilDateTime;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementServices;

if(parameters.ecsBankName){
	bankName = parameters.ecsBankName;
}else{
bankName = parameters.bankName;
}
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
		parameters["customTimePeriodId"]= parameters.getAt("shedCustomTimePeriodId");
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
}
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);


ecsMonth = UtilDateTime.toDateString(fromDate, "MMMyyyy");
context.put("ecsMonth",ecsMonth.toUpperCase());

// getting timePeriod related units
unitsList=[];
if(UtilValidate.isNotEmpty(parameters.shedId)){
	/*shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: parameters.shedId]);
	unitsList = shedUnitDetails.get("unitsDetailList");*/
	shedUnitsDetails = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);
	unitsList = shedUnitsDetails.customTimePeriodUnitsDetailList;
}else{
 return;
}
unitIdsList=[];
String facilityTypeStr = "SHED";
if(UtilValidate.isNotEmpty(parameters.facilityTypeStr)){
		facilityTypeStr = parameters.get("facilityTypeStr");
	}

if(("SHED".equalsIgnoreCase(facilityTypeStr))&&UtilValidate.isNotEmpty(unitsList)){
	unitsList.each{ unit->
		unitId = unit.facilityId;
		conList=[];
		conList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,parameters.shedCustomTimePeriodId));
		conList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"PROC_BILL_MONTH"));
		conList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, unitId));
		 condition = EntityCondition.makeCondition(conList,EntityOperator.AND);
		unitCustomTimePeriodList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",condition,null, null,null, false);
		if(UtilValidate.isNotEmpty(unitCustomTimePeriodList)){
			unitIdsList.add(unitCustomTimePeriodList.get(0).facilityId);
		}
	}
}

facilityAttr = "";
shrtRecoveryUnits = delegator.findOne("FacilityAttribute",[facilityId : parameters.shedId, attrName: "SHRTRECRYUNITS"], false);
if(UtilValidate.isNotEmpty(shrtRecoveryUnits)){
	facilityAttr = shrtRecoveryUnits.get("attrValue");
}
context.putAt("facilityAttr", facilityAttr);

if((UtilValidate.isNotEmpty(facilityAttr))&&(("Y".equals(facilityAttr)))){
	unitIdsList = unitIdsList;
}else if(("SHED".equalsIgnoreCase(facilityTypeStr))&&UtilValidate.isNotEmpty(shortageUnitsList)){
	unitIdsList.removeAll(shortageUnitsList);
}

if("UNIT".equalsIgnoreCase(facilityTypeStr) ){
		String unitid = parameters.unitId;
		unitIdsList.clear();
		unitIdsList.add(unitid);
		unitDetails = delegator.findOne("Facility",["facilityId":unitid],false);
		context.putAt("unitDetails",unitDetails);
		
		unitAccountDetails = delegator.findList("FacilityPersonAndFinAccount",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, unitid)  , null, null, null, false );
		unitAccountMap =[:];
		unitAccountMap["ifscCode"]=0;
		if(UtilValidate.isNotEmpty(unitAccountDetails)){
			unitAccountDetails.each{ accountDetails ->
				unitAccountMap.put("finAccountBranch", accountDetails.finAccountBranch);
				unitAccountMap.put("finAccountName", accountDetails.finAccountName);
				unitAccountMap.put("finAccountCode", accountDetails.finAccountCode);
				unitAccountMap.put("thruDate", accountDetails.thruDate);
				if(accountDetails.ifscCode !=null){
					unitAccountMap.put("ifscCode", accountDetails.ifscCode);
				}
			}
		}
		context.put("unitAccountMap", unitAccountMap);
	}
List finalFinAccountList=FastList.newInstance();
if(UtilValidate.isNotEmpty(unitIdsList)){
	unitIdsList.each{ unitId->
		facility=delegator.findOne("Facility",[facilityId : unitId], false);
	if(UtilValidate.isNotEmpty(facility)){
		if("UNIT".equals(facility.facilityTypeId)){
			finAccountList = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",unitId,"statusFlag","ACTIVE"))).get("finAccountList");
			finalFinAccountList.addAll(finAccountList);
			 finAccFacilityIds=[];
			 unitFinAccMap=[:];
			//If finAccount is not configured for center,then we are taking unit finAccount as center finAccount
			 finAccountList.each{ finAcc->
					if("CENTER".equals(finAcc.facilityType)){
							finAccFacilityIds.add(finAcc.facilityId);
					}
					if("UNIT".equals(finAcc.facilityType)){
						unitFinAccMap.putAll(finAcc);
					}	
			}
			unitCentersList = ProcurementNetworkServices.getUnitAgents(dctx,UtilMisc.toMap("unitId",unitId ));
			centersList = unitCentersList.get("agentsList");
			List unitFacilityIds=EntityUtil.getFieldListFromEntityList(centersList, "facilityId", false);			
			unitFacilityIds.removeAll(finAccFacilityIds);
			unitFacilityIds.each{ facilityId->
				 finAccount = [:];
				centerDetails= delegator.findOne("Facility",["facilityId":facilityId],false);
				finAccount.putAll(unitFinAccMap);
				finAccount.put("facilityId", facilityId);
				finAccount.put("facilityType", centerDetails.get("facilityTypeId"));
				finAccount.put("facilityCode", centerDetails.get("facilityCode"));
				finalFinAccountList.add(finAccount);
			}
			
			
		}
	  }
	}
}
/*Debug.log("finalFinAccountList==="+finalFinAccountList);*/
/*periodTotals = ProcurementReports.getPeriodTotals(dctx , [includeCenterTotals:Boolean.TRUE,fromDate: fromDate , thruDate: thruDate , facilityId: parameters.shedId]);
centerWiseTotals=periodTotals.get("centerWiseTotals");*/
Map bankBranchCenterWiseMap=FastMap.newInstance();
Map bankBranchIfscWiseMap=FastMap.newInstance();
finalFinAccountList = UtilMisc.sortMaps(finalFinAccountList, UtilMisc.toList("finAccountCode","gbCode","-facilityCode"));
Map bankWiseMap=FastMap.newInstance();
Map bankBranchWiseMap=FastMap.newInstance();
Map bankBranchUnitCenterWiseMap=FastMap.newInstance();
Map transferBankMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(finalFinAccountList)){
	finalFinAccountList.each{ finAccount->
	if((UtilValidate.isNotEmpty(finAccount.thruDate) && (fromDate<UtilDateTime.toTimestamp(finAccount.thruDate)))|| UtilValidate.isEmpty(finAccount.thruDate)){
		if(("CENTER".equals(finAccount.facilityType))){		
			if(UtilValidate.isNotEmpty(finAccount)){
				facilityId =finAccount.facilityId;
				GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,facilityId), false);
				if(UtilValidate.isEmpty(facility.getString("categoryTypeEnum")) || ((UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))&&!"RECVYCENTER".equals(facility.getString("categoryTypeEnum"))))){
					finAccountId=finAccount.finAccountId;
					bankName = finAccount.finAccountName;
					branchName = finAccount.finAccountBranch;
					centerCode= finAccount.facilityCode;
					ifscNo= finAccount.ifscCode;
					accNo=finAccount.finAccountCode;
					facilityId =finAccount.facilityId;
					isDisplay="Y";
					//here getting transfer account details
					if(UtilValidate.isNotEmpty(finAccountId)){
						 transferAccountDetails=delegator.findOne("FinAccountAttribute",[finAccountId : finAccountId,attrName:"TRANSFER_ACCOUNT"], false);
						if(UtilValidate.isNotEmpty(transferAccountDetails)){
							trnsferAccId= transferAccountDetails.get("attrValue");
							trnsferFinAccount=delegator.findOne("FinAccount",[finAccountId : trnsferAccId], false);
							if(UtilValidate.isNotEmpty(trnsferFinAccount)){
								trnsferBranchMap=[:];
								if(UtilValidate.isNotEmpty(transferBankMap[bankName])){
									trnsferBranchMap=transferBankMap[bankName];									
								}
								amountValue=0;
								if(UtilValidate.isNotEmpty(context.getAt("billsCenterWiseMap"))){
									billsCenterWiseMap=context.getAt("billsCenterWiseMap");
									if(UtilValidate.isNotEmpty(billsCenterWiseMap.get(facilityId))){
										amountValue=billsCenterWiseMap.get(facilityId);
									
									}
								}
								tempTrnsfMap=[:];
								if(UtilValidate.isEmpty(branchName)){
									branchName = "nonBranch";
								}
								branchName=branchName.toUpperCase();
								if(UtilValidate.isNotEmpty(trnsferBranchMap[branchName])){
									tempTrnsfMap =trnsferBranchMap[branchName];
									tempTrnsfMap["amount"] +=amountValue;
								}else{
									tempTrnsfMap.put("TrnsferBankName", trnsferFinAccount.finAccountName);
									tempTrnsfMap.put("TrnsferBranch", trnsferFinAccount.finAccountBranch);
									tempTrnsfMap.put("TrnsfIfcNo", trnsferFinAccount.ifscCode);
									tempTrnsfMap.put("TrnsfAcNo", trnsferFinAccount.finAccountCode);
									tempTrnsfMap.put("partyName", "A/c Tr. to Other Branch");
									tempTrnsfMap.put("amount", amountValue);
								}
								
								trnsferBranchMap.putAt(branchName, tempTrnsfMap);
								transferBankMap.put(bankName, trnsferBranchMap);
								isDisplay="N";
								
								/*bankName = trnsferFinAccount.finAccountName;
								branchName = trnsferFinAccount.finAccountBranch;
								ifscNo= trnsferFinAccount.ifscCode;
								accNo=trnsferFinAccount.finAccountCode;*/
							}
						}
					}
					if(UtilValidate.isNotEmpty(bankName)){
						bankName = bankName.trim();
						}
					
					if(UtilValidate.isEmpty(bankName)){
						bankName = "noBankName";
					}
					if(UtilValidate.isNotEmpty(bankName)){
						facilityId =finAccount.facilityId;							
						centerDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:facilityId]);
						centerName=centerDetails.get("centerFacility").get("facilityName");
						unitId = centerDetails.get("unitFacility").get("facilityId");
						unitName = centerDetails.get("unitFacility").get("facilityName");
						ownerPartyId=centerDetails.get("centerFacility").get("ownerPartyId");
						accountHolderName=PartyHelper.getPartyName(delegator, ownerPartyId, false)
						//getting center wise price for given period
						totalPrice =0;
						/*if(UtilValidate.isNotEmpty(centerWiseTotals[facilityId])){
							Map centerWiseValues = ((Map)((Map)((Map)centerWiseTotals[facilityId].get("dayTotals")).get("TOT")).get("TOT")).get("TOT");
							totalPrice = centerWiseValues.get("price")+centerWiseValues.get("sPrice");
						}*/					
						totAdditions=0;
						totDeductions=0;
						/*centerAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: facilityId]);
						if(UtilValidate.isNotEmpty(centerAdjustments)){
							adjustmentsTypeValues = centerAdjustments.get("adjustmentsTypeMap");
							if(adjustmentsTypeValues !=null){
								adjustmentsTypeValues.each{ adjustmentValues ->
									if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
										additionsList = adjustmentValues.getValue();
										additionsList.each{ additionValues ->
											totAdditions += additionValues.getValue();
										}
									}else{
										deductionsList = adjustmentValues.getValue();
										deductionsList.each{ deductionValues ->
											totDeductions += deductionValues.getValue();
										}
									}
								}
							}
						}*/
						// cartage
						/*billingValues = ProcurementReports.getProcurementBillingValues(dctx , [userLogin: userLogin ,customTimePeriodId: parameters.shedCustomTimePeriodId, facilityId: facilityId]);
						billingVal = billingValues.get("FacilityBillingMap");*/
						billingCartage =0;
						billingCommission =0;
						/*if (UtilValidate.isNotEmpty(billingVal)) {
							billingFac = billingVal.get(facilityId);
							billingTot = billingFac.get("tot");
							billingCartage = billingTot.get("cartage");
							billingCommission = billingTot.get("commAmt");
						}*/
						amount=0;
						if(UtilValidate.isNotEmpty(context.getAt("billsCenterWiseMap"))){
							billsCenterWiseMap=context.getAt("billsCenterWiseMap");
							if(UtilValidate.isNotEmpty(billsCenterWiseMap.get(facilityId))){
								amount=billsCenterWiseMap.get(facilityId);
							
							}
						}
						/*amount= Math.round(totalPrice+totAdditions+billingCartage+billingCommission-totDeductions);	*/		
							
						Map centerWiseDetails=FastMap.newInstance();
						
						centerWiseDetails["centerCode"]=centerCode;
						centerWiseDetails["centerName"]=centerName;
						centerWiseDetails["bankName"]=bankName;
						centerWiseDetails["branchName"]=branchName;
						centerWiseDetails["unitName"]=unitName;
						centerWiseDetails["accNo"]=accNo;
						centerWiseDetails["ifscNo"]=ifscNo;
						centerWiseDetails["amount"]=amount;
						centerWiseDetails["isDisplay"]=isDisplay;
						centerWiseDetails["partyName"]=accountHolderName;
						Map centerWiseMap=FastMap.newInstance();
						centerWiseMap.put(facilityId, centerWiseDetails);
						Map unitWiseMap=FastMap.newInstance();
						List unitWiseCenterList =FastList.newInstance();
						
						if(UtilValidate.isNotEmpty(bankWiseMap) && UtilValidate.isNotEmpty(bankWiseMap[bankName])){
							unitWiseMap = bankWiseMap[bankName];
						}
						if(UtilValidate.isNotEmpty(unitWiseMap) && UtilValidate.isNotEmpty(unitWiseMap[unitId])){
							unitWiseCenterList.addAll(unitWiseMap[unitId]);
						}
						if(amount!=0){
							unitWiseCenterList.add(centerWiseMap);
						}
						if(UtilValidate.isNotEmpty(unitWiseCenterList)){
							unitWiseMap.put(unitId, unitWiseCenterList);
						}
						if(UtilValidate.isNotEmpty(unitWiseMap)){		
							bankWiseMap.put(bankName, unitWiseMap);	
						}
						// This is for BankBranchWiseReport
						if(UtilValidate.isNotEmpty(branchName)){
								branchName = branchName.trim();
							}
						if(UtilValidate.isEmpty(branchName)){
							branchName = "nonBranch";
						}
								
						//This is For Bank BranchWise Unit CenterWise Report
						Map branchUnitWiseMap = FastMap.newInstance();
						Map tempUnitWiseMap = FastMap.newInstance();
						List tempUnitCenterWiseList = FastList.newInstance();
						if(UtilValidate.isNotEmpty(bankBranchUnitCenterWiseMap) && UtilValidate.isNotEmpty(bankBranchUnitCenterWiseMap[bankName])){
							branchUnitWiseMap = bankBranchUnitCenterWiseMap[bankName];
						}
						if(UtilValidate.isNotEmpty(branchUnitWiseMap) && UtilValidate.isNotEmpty(branchUnitWiseMap[branchName])){
							tempUnitWiseMap = branchUnitWiseMap[branchName];
						}
						if(UtilValidate.isNotEmpty(tempUnitWiseMap) && UtilValidate.isNotEmpty(tempUnitWiseMap[unitId])){
							tempUnitCenterWiseList.addAll(tempUnitWiseMap[unitId]);
						}
						if(amount!=0){
							tempUnitCenterWiseList.add(centerWiseMap);
						}
						if(UtilValidate.isNotEmpty(tempUnitCenterWiseList)){
							tempUnitWiseMap.put(unitId, tempUnitCenterWiseList);
						}
						if(UtilValidate.isNotEmpty(tempUnitWiseMap)){
							branchUnitWiseMap.put(branchName,tempUnitWiseMap);
						}
						if(UtilValidate.isNotEmpty(branchUnitWiseMap)){
							bankBranchUnitCenterWiseMap.put(bankName, branchUnitWiseMap);
							}
						
						
						
						Map branchMap = FastMap.newInstance();
						branchMap.put("branchName", branchName);
						branchMap.put("amount", amount);
						Map bankBranchMap = FastMap.newInstance();
						
						if(UtilValidate.isEmpty(bankBranchWiseMap)||(UtilValidate.isNotEmpty(bankBranchWiseMap) &&(UtilValidate.isEmpty(bankBranchWiseMap.get(bankName))))){
								//bankBranchList.add(branchMap);
								bankBranchMap.put(branchName,amount);
							}else{
								Map tempBranchMap = FastMap.newInstance();
								tempBranchMap.putAll(bankBranchWiseMap.get(bankName))
							    if(UtilValidate.isNotEmpty(tempBranchMap.get(branchName))){
										tempBranchMap.putAt(branchName, tempBranchMap.get(branchName)+amount);
									}else{
										tempBranchMap.putAt(branchName,amount);
									}
									bankBranchMap.putAll(tempBranchMap);
							}
						bankBranchWiseMap.putAt(bankName, bankBranchMap);
						
						//populating branch wise totals
						
						
						List centerDetailsList=FastList.newInstance();
						Map branchCenterWiseMap=FastMap.newInstance();
						centerDetailsList.add(centerWiseMap);
						
						if(UtilValidate.isNotEmpty(branchName)){
							if(UtilValidate.isNotEmpty(bankBranchCenterWiseMap[bankName])){
								branchCenterWiseMap=bankBranchCenterWiseMap[bankName];
							}
							if(UtilValidate.isNotEmpty(branchCenterWiseMap) && UtilValidate.isNotEmpty(branchCenterWiseMap[branchName])){
								centerDetailsList.addAll(branchCenterWiseMap[branchName]);
							}
							if(amount!=0){
								branchCenterWiseMap.put(branchName, centerDetailsList);
								bankBranchCenterWiseMap.put(bankName, branchCenterWiseMap);
							}
							if(UtilValidate.isEmpty(ifscNo)){
								ifscNo =0;
							}
								Map tempIfscMap = FastMap.newInstance();
								Map tempIfscBranchMap = FastMap.newInstance();
								Map tempbranchIfscMap = FastMap.newInstance();
								if(UtilValidate.isNotEmpty(bankBranchIfscWiseMap.get(bankName))){
									Map tempBranchDetailsMap = FastMap.newInstance();
									tempBranchDetailsMap = 	(Map)bankBranchIfscWiseMap.get(bankName);
									tempIfscBranchMap.putAll(tempBranchDetailsMap);
									}
								if(amount!=0){
									tempIfscMap.put(ifscNo, centerDetailsList);
									tempIfscBranchMap.put(branchName,tempIfscMap);
									tempbranchIfscMap.put(bankName,tempIfscBranchMap);
									bankBranchIfscWiseMap.putAll(tempbranchIfscMap);
								}
						}
						
					}
					
				  }
			}
		}
		}		
	}
}
//for taking parameter bankName in bankWiseMap
if(UtilValidate.isNotEmpty(parameters.ecsBankName)){
	parameters.bankName=parameters.ecsBankName;
}
if((UtilValidate.isNotEmpty(parameters.bankName) && parameters.bankName !="All")){
	Map tempUnitMap=FastMap.newInstance();
	tempUnitMap= bankWiseMap[parameters.bankName];
	Map tempBankMap=FastMap.newInstance();
	tempBankMap.put(parameters.bankName, tempUnitMap);
	bankWiseMap=tempBankMap;
}

if((UtilValidate.isNotEmpty(parameters.bankName) && parameters.bankName !="All")){
	Map tempBranhUnitMap=FastMap.newInstance();
	tempBranhUnitMap= bankBranchUnitCenterWiseMap[parameters.bankName];
	Map tempBankBranchUnitMap=FastMap.newInstance();
	tempBankBranchUnitMap.put(parameters.bankName, tempBranhUnitMap);
	bankBranchUnitCenterWiseMap=tempBankBranchUnitMap;
}
context.putAt("bankBranchUnitCenterWiseMap", bankBranchUnitCenterWiseMap);
context.putAt("bankBranchIfscWiseMap", bankBranchIfscWiseMap);
context.putAt("bankBranchCenterWiseMap", bankBranchCenterWiseMap);
context.putAt("bankWiseMap", bankWiseMap);
context.putAt("bankBranchWiseMap", bankBranchWiseMap);
context.putAt("transferBankMap", transferBankMap);
