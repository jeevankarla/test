import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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

import org.ofbiz.base.util.UtilNumber;

import java.math.RoundingMode;
import java.util.Map;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
Map ptcBankMap = FastMap.newInstance();
List ptcBankList= FastList.newInstance();


customTimePeriodId=parameters.customTimePeriodId;
finAccountId=parameters.finAccountId;
context.finAccountId=finAccountId;
context.customTimePeriodId=customTimePeriodId;
BigDecimal totAmount=BigDecimal.ZERO;
periodBillingId="";

//customTimePeriodId Number
periodBillingId="";
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["APPROVED","GENERATED","APPROVED_PAYMENT"]));
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , "PB_PTC_TRSPT_MRGN"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling",condition , null, null, null, false);
	if(UtilValidate.isNotEmpty(periodBillingList)){
		 periodBillingList = EntityUtil.getFirst(periodBillingList);
		 periodBillingId=periodBillingList.periodBillingId;
	}
}

if(UtilValidate.isEmpty(customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = " customTimePeriod Cannot Be Empty...!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;

Map vehicleFineMap = FastMap.newInstance();
//List ptcContractors = FastList.newInstance();
Set ptcContractors=null;
vehicleRoleList=[];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityCommissionList = delegator.findList("PtcBillingCommissionAndMilkTransfer",condition , null, null, null, false);
if(UtilValidate.isNotEmpty(facilityCommissionList)){
	
	containerIds = EntityUtil.getFieldListFromEntityList(facilityCommissionList, "containerId", false);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.IN , containerIds));
	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fineRecoveries = delegator.findList("FineRecovery",condition, null,null, null, false);
	fineRecoveries.each{eachFineRecovery->
		 String recoveryTypeId=eachFineRecovery.recoveryTypeId;
		 BigDecimal fineAmount=eachFineRecovery.amount;
		 if(UtilValidate.isEmpty(fineAmount)){
			 fineAmount=BigDecimal.ZERO;
		 }
		vehicleId=eachFineRecovery.vehicleId;
		ptcAddDed = delegator.findOne("Enumeration",["enumId":recoveryTypeId],false);
		if(UtilValidate.isNotEmpty(ptcAddDed)){
			if(UtilValidate.isEmpty(vehicleFineMap) || (UtilValidate.isNotEmpty(vehicleFineMap) && UtilValidate.isEmpty(vehicleFineMap.get(vehicleId)))){
				Map qtyDetMap1 = FastMap.newInstance();
				qtyDetMap1.put("ptcAdd",BigDecimal.ZERO);
				qtyDetMap1.put("ptcDed",BigDecimal.ZERO);
				if(("PTC_ADDN".equals(ptcAddDed.get("enumTypeId"))) ){
					qtyDetMap1.put("ptcAdd",fineAmount);
				}else{
					qtyDetMap1.put("ptcDed",fineAmount);
				}
				vehicleFineMap.put(vehicleId, qtyDetMap1);
			}else{
				Map tempIssueQtyMap = FastMap.newInstance();
				tempIssueQtyMap.putAll(vehicleFineMap.get(vehicleId));
				if(UtilValidate.isNotEmpty(tempIssueQtyMap) &&("PTC_ADDN".equals(ptcAddDed.get("enumTypeId"))) ){
					if(UtilValidate.isNotEmpty(fineAmount)){
						tempIssueQtyMap.putAt("ptcAdd", tempIssueQtyMap.get("ptcAdd") + fineAmount);
					}
				}else{
					if(UtilValidate.isNotEmpty(fineAmount)){
						tempIssueQtyMap.putAt("ptcDed", tempIssueQtyMap.get("ptcDed") + fineAmount);
					}
				}
				vehicleFineMap.put(vehicleId, tempIssueQtyMap);
			}
		}
	}
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.IN , containerIds));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "PTC_VEHICLE"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	vehicleRoleList = delegator.findList("VehicleRole",condition , null, null, null, false);
	if(UtilValidate.isNotEmpty(vehicleRoleList)){
		ptcContractors = new HashSet(EntityUtil.getFieldListFromEntityList(vehicleRoleList, "partyId", false));
	}
}

finAccountParties = [];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "PTC_BILL"));
/*conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
				  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd))); */
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
finAccountRoleList = delegator.findList("FinAccountRole", condition, null, null, null, false);
finAccountParties = EntityUtil.getFieldListFromEntityList(finAccountRoleList, "partyId", false);

if(UtilValidate.isNotEmpty(ptcContractors)){
	ptcContractors.sort();
	ptcContractors.each { eacPtcContractor ->
		if(finAccountParties.contains(eacPtcContractor)){
			List<GenericValue> finAccountDetails = delegator.findList("FinAccount", EntityCondition.makeCondition([ownerPartyId: eacPtcContractor, finAccountTypeId: "BANK_ACCOUNT" ,statusId: "FNACT_ACTIVE"]), null, null, null, false);
			if(UtilValidate.isNotEmpty(finAccountDetails)){
				Map finAcctMap =FastMap.newInstance();
				String partyName = "";
				String partyPan = "";
				String finAccountCode="";
				BigDecimal amount=BigDecimal.ZERO;
				
				finAccount = EntityUtil.getFirst(finAccountDetails);
				if(UtilValidate.isNotEmpty(finAccount.finAccountCode) && "FNACT_ACTIVE".equals(finAccount.statusId)){
					finAccountCode = finAccount.finAccountCode;
				}
				partyName = PartyHelper.getPartyName(delegator, eacPtcContractor, true);
				
				partyIdentificationDetails = delegator.findList("PartyIdentification", EntityCondition.makeCondition([partyId: eacPtcContractor, partyIdentificationTypeId: "PAN_NUMBER"]), null, null, null, false);
				if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
					partyIdentification = EntityUtil.getFirst(partyIdentificationDetails);
					if(UtilValidate.isNotEmpty(partyIdentification.idValue)){
						partyPan = partyIdentification.idValue;
					}
				}
				
				BigDecimal contractAmt=BigDecimal.ZERO;
				BigDecimal contractAdd=BigDecimal.ZERO;
				BigDecimal contractDed=BigDecimal.ZERO;
				
				partyWiseVehicles = EntityUtil.filterByCondition(vehicleRoleList,EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,eacPtcContractor));
				partyWiseVehicles.each{eachVehicle->
					vehicleAddDeduction=vehicleFineMap.get(eachVehicle.vehicleId);
					if(UtilValidate.isNotEmpty(vehicleAddDeduction)){
						if(UtilValidate.isNotEmpty(vehicleAddDeduction.ptcAdd)){
							contractAdd=contractAdd+vehicleAddDeduction.ptcAdd;
						}
						if(UtilValidate.isNotEmpty(vehicleAddDeduction.ptcDed)){
							contractDed=contractDed+vehicleAddDeduction.ptcDed;
						}
					}
					ptcCommissionAmounts = EntityUtil.filterByCondition(facilityCommissionList,EntityCondition.makeCondition("containerId",EntityOperator.EQUALS, eachVehicle.vehicleId));
						ptcCommissionAmounts.each{eachVehicleAmt->
						if(UtilValidate.isNotEmpty(eachVehicleAmt.commissionAmount)){
						contractAmt=contractAmt+(eachVehicleAmt.commissionAmount);
						}
					}			
				}
				if(UtilValidate.isNotEmpty(contractAmt)){
					amount=contractAmt+contractAdd-contractDed;
					amount = ((new BigDecimal(amount)).setScale(2,BigDecimal.ROUND_HALF_UP));
					List invAdjConditionList = FastList.newInstance();
					String referenceNumber = "PTC_TRSPT_MRGN_";
					BigDecimal adjustedAmt = BigDecimal.ZERO;
					referenceNumber = referenceNumber.concat(periodBillingId);
					invAdjConditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,eacPtcContractor));
					invAdjConditionList.add(EntityCondition.makeCondition("referenceNumber",EntityOperator.EQUALS,referenceNumber));
					invAdjConditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"ROUNDING_ADJUSTMENT"));
					EntityCondition invAdjCondition = EntityCondition.makeCondition(invAdjConditionList); 
					List adjustmentList = delegator.findList("InvoiceAndItem",invAdjCondition,null,null,null,false);
					if(UtilValidate.isNotEmpty(adjustmentList)){
						for(GenericValue adjustmentVal in adjustmentList){
							BigDecimal roundAdjValue = (BigDecimal)adjustmentVal.get("amount");
							adjustedAmt = adjustedAmt.add(roundAdjValue);
						}
						amount = amount+adjustedAmt;
					}
					totAmount=totAmount+amount;
				}
				finAcctMap.put("partyId", eacPtcContractor);
				finAcctMap.put("partyName", partyName);
				finAcctMap.put("partyPan", partyPan);
				finAcctMap.put("finAccountCode", finAccountCode);
				finAcctMap.put("amount", amount);
				ptcBankMap.put(eacPtcContractor, finAcctMap)
				ptcBankList.add(finAcctMap);
			}
		}
	}
}
context.ptcBankMap=ptcBankMap;
context.totAmount=totAmount;

// ptc Bank csv
if(UtilValidate.isNotEmpty(totAmount)){
	totAmtMap=[:];
	totAmtMap.put("partyId", "Total Amount");
	totAmtMap.put("amount", totAmount);
	ptcBankList.add(totAmtMap);
}
context.ptcBankList=ptcBankList;