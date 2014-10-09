import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.*;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import org.ofbiz.order.order.OrderServices;


//getting  shed total TipAmount and deductions (other than feed in ) for DDACCOUNT
/*ddAmountMap =[:];
if(UtilValidate.isNotEmpty(context.get("shedWiseTotalsMap"))){
	
	shedWiseTotalsMap = context.get("shedWiseTotalsMap");	
	tipAmount = shedWiseTotalsMap.get("tipAmount");
	totalDeductions = shedWiseTotalsMap.get("DednsTot");
	feedDeductions= shedWiseTotalsMap.get("MILKPROC_FEEDDED");
	totalDDAmount = tipAmount + (totalDeductions-feedDeductions);	
	
	shedId= parameters.shedId;
	customTimePeriodId = parameters.customTimePeriodId;
	ddAccDetailsMap = [:];
	shedDDAccountMap = ProcurementReports.getShedDDAccount(dctx, [userLogin:userLogin,facilityId:shedId,customTimePeriodId:customTimePeriodId]);
	if(ServiceUtil.isSuccess(shedDDAccountMap)){
		ddAccDetailsMap = shedDDAccountMap.get("ddAccDetailsMap");
		ddAmountMap.putAll(ddAccDetailsMap);
		ddAmountMap["unitCode"] =ddAccDetailsMap.partyId;
		ddAmountMap["amount"]=totalDDAmount;
		}	
}
context.putAt("ddAmountMap", ddAmountMap);*/
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
condList =[];

if(UtilValidate.isNotEmpty(parameters.shedId)){
	/*shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: parameters.shedId]);
	unitsList = shedUnitDetails.get("unitsDetailList");*/
	shedUnitsDetails = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);
	unitsList = shedUnitsDetails.customTimePeriodUnitsDetailList;	
}else{
 return;
}

bankWiseTotalAmountMap = [:];
unitBankWiseAbstract = [];
bankTotalAmount = 0;

for(int i=0;i<unitsList.size();i++){ unit = unitsList.get(i);
	unitTotals = ProcurementReports.getPeriodTransferTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: unit.facilityId]);
	bankTotalsMap = [:];
	
	try{
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,unit.facilityId));
		conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"FNACT_ACTIVE"));
		//conditionList.add(EntityCondition.makeCondition("finAccountCode", EntityOperator.NOT_EQUAL,null));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		unitFinAccountList = delegator.findList("FacilityPersonAndFinAccount",condition,null,null,null,false);
		unitFinAccountList = UtilMisc.sortMaps(unitFinAccountList, UtilMisc.toList("gbCode"));
		
		if(UtilValidate.isEmpty(unitFinAccountList)){
			continue;
		}
		
		unitDetail = unitFinAccountList.get(0);
		bankTotalsMap["nameOfTheBank"] = unitDetail.finAccountName;
		bankTotalsMap["nameOfTheBrch"] = unitDetail.finAccountBranch;
		bankTotalsMap["ifscCode"] = unitDetail.get("ifscCode");
		bankTotalsMap["accountHolder"] = OrderServices.nameTrim((PartyHelper.getPartyName(delegator, unitDetail.get("ownerPartyId"), true)).replace(',', ''),25);
		bankTotalsMap["bankAccNo"] = unitDetail.get("finAccountCode");
	} catch (Exception e) {
         Debug.logError("Error retrieving FinAccount Details For Unit : "+unit.facilityId +"------------>"+e.toString(), "");
		 //return;
    }
	
	if(UtilValidate.isNotEmpty(unitTotals)){
		bankTotalsMap["nameOfUnit"] = unit.facilityName;
		bankTotalsMap["unitCode"] = unit.facilityCode;
		unitAddnTot = 0;
		unitDednsTot = 0;
		milkValue = 0;
		unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: unit.facilityId]);
		if(UtilValidate.isNotEmpty(unitAdjustments)){
		  adjustmentsTypeValues = unitAdjustments.get("adjustmentsTypeMap");
		   if(adjustmentsTypeValues !=null){
			   adjustmentsTypeValues.each{ adjustmentValues ->
				  if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
					  additionsList = adjustmentValues.getValue();
					  additionsList.each{ additionValues ->
						  unitAddnTot += additionValues.getValue();
					  }
				  }else{
					  deductionsList = adjustmentValues.getValue();
					  deductionsList.each{ deductionValues ->
						  unitDednsTot += deductionValues.getValue();
					  }
				  }
			   }
			}
		 }
		
		totEntry = unitTotals.get("periodTransferTotalsMap");
		unitTotEntry = totEntry.get(unit.facilityId);
		/*transferValues = unitTotEntry.get("transfers");
		periodTotals = transferValues.get("procurementPeriodTotals");
		periodDayTotals = periodTotals.get("dayTotals");
		if(UtilValidate.isNotEmpty(periodDayTotals.get("TOT"))){
			milkValue = (periodDayTotals.get("TOT").get("price")+periodDayTotals.get("TOT").get("sPrice"));
		}
		cartage =0;
		opCost =0;
		if(UtilValidate.isNotEmpty(shedWiseAmountAbstractMap)){
			cartage =shedWiseAmountAbstractMap.get(unit.facilityId).get("cartage");
			opCost =shedWiseAmountAbstractMap.get(unit.facilityId).get("opCost");			
		}
		netAmount = milkValue+unitAddnTot+cartage+opCost-unitDednsTot;
		netAmount =netAmount.setScale(0,BigDecimal.ROUND_HALF_UP);
		//shortages
		shortages = transferValues.get("shortages");
		totalRecoveryAmt = shortages.get("kgFatAmt")+shortages.get("kgSnfAmt");
		//netPayment Amount
		netTotAmount = netAmount+totalRecoveryAmt;*/
		netTotAmount=0;
		//taking from Unit Milk bill Net Payable groovy
		if(UtilValidate.isNotEmpty(context.getAt("UnitWiseDetailsMap"))){
			UnitDetailsMap=context.getAt("UnitWiseDetailsMap");
			netTotAmount = UnitDetailsMap.get(unit.facilityId).getAt("netAmtPayable");
		}
		bankTotalsMap["amount"] = netTotAmount;
		if(netTotAmount<0){
			unitBankWiseAbstract=[];
			context.put("negativeAmtMsg", "UNIT :"+unit.facilityCode+"  HAS NEGATIVE AMOUNT");
			return;
		}
		unitBankWiseAbstract.add(bankTotalsMap);
	}
	//for total bank payments report
	if(UtilValidate.isEmpty(bankWiseTotalAmountMap.get(unitDetail.finAccountName))){
		bankWiseTotalAmountMap[unitDetail.finAccountName] = netTotAmount;
	} else {
		prevAmount = bankWiseTotalAmountMap.get(unitDetail.finAccountName);
		finalAmount = prevAmount + netTotAmount;
		bankWiseTotalAmountMap[unitDetail.finAccountName] = finalAmount;
	}
	bankTotalAmount += netTotAmount;
}
bankWiseTotalAmountMap["TOTAL"] = bankTotalAmount;
unitBankWiseAbstract = UtilMisc.sortMaps(unitBankWiseAbstract, UtilMisc.toList("nameOfTheBank"));

context.put("bankWiseTotalAmountMap", bankWiseTotalAmountMap);
context.put("unitBankWiseAbstract", unitBankWiseAbstract);
