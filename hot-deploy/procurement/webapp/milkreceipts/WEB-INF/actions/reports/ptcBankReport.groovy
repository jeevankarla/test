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

//customTimePeriodId Number
periodBillingId="";
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , customTimePeriodId));
	//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "APPROVED_PAYMENT"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["APPROVED_PAYMENT","GENERATED"]));
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
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
//conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
//conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityCommissionList = delegator.findList("FacilityCommission",condition , null, UtilMisc.toList("partyId"), null, false);
contractorIds = EntityUtil.getFieldListFromEntityList(facilityCommissionList, "partyId", false);

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

if(UtilValidate.isNotEmpty(facilityCommissionList)){
	facilityCommissionList.each { facilityCommission ->
		partyId =  facilityCommission.partyId;
		if(UtilValidate.isNotEmpty(partyId)){
			if(finAccountParties.contains(partyId)){
				List<GenericValue> finAccountDetails = delegator.findList("FinAccount", EntityCondition.makeCondition([ownerPartyId: partyId, finAccountTypeId: "BANK_ACCOUNT" ,statusId: "FNACT_ACTIVE"]), null, null, null, false);
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
					partyName = PartyHelper.getPartyName(delegator, partyId, true);
					
					partyIdentificationDetails = delegator.findList("PartyIdentification", EntityCondition.makeCondition([partyId: partyId, partyIdentificationTypeId: "PAN_NUMBER"]), null, null, null, false);
					if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
						partyIdentification = EntityUtil.getFirst(partyIdentificationDetails);
						if(UtilValidate.isNotEmpty(partyIdentification.idValue)){
							partyPan = partyIdentification.idValue;
						}
					}
					if(UtilValidate.isNotEmpty(facilityCommission.totalAmount)){
						amount = ((new BigDecimal(facilityCommission.totalAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
						totAmount=totAmount+amount;
					}
					finAcctMap.put("partyId", partyId);
					finAcctMap.put("partyName", partyName);
					finAcctMap.put("partyPan", partyPan);
					finAcctMap.put("finAccountCode", finAccountCode);
					finAcctMap.put("amount", amount);
					
					ptcBankMap.put(partyId, finAcctMap)
					ptcBankList.add(finAcctMap);
				}
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











