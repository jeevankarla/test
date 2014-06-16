import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import org.ofbiz.base.util.UtilNumber;

import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

dctx = dispatcher.getDispatchContext();
periodBillingId = null;

facilityCommissionList = [];
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
fromDateStr = UtilDateTime.toDateString(fromDateTime,"MMM(dd-");
thruDateStr = UtilDateTime.toDateString(thruDateTime,"dd),yyyy");
context.put("fromDateStr",fromDateStr);
context.put("thruDateStr",thruDateStr);
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);

conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_LMS_TRSPT_MRGN"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
if(UtilValidate.isNotEmpty(periodBillingList)){
	for (int i = 0; i < periodBillingList.size(); ++i) {
		periodBillingDetails = periodBillingList.get(i);
		periodBillingId = periodBillingDetails.periodBillingId;
	}
}
Map partyFacilityMap=(Map)ByProductNetworkServices.getFacilityPartyContractor(dctx, UtilMisc.toMap("saleDate",monthBegin)).get("partyAndFacilityList");
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.EQUALS , monthBegin));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
routesList = delegator.findList("FacilityAndCommission",condition,["facilityId"]as Set, UtilMisc.toList("parentFacilityId","facilityId"),findOptions,false);
routeIdsList = EntityUtil.getFieldListFromEntityList(routesList, "facilityId", false);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityCommissionList = delegator.findList("FacilityCommission",condition , null, ["commissionDate"], null, false);

finAccountName = parameters.finAccountName;
dtcBankMap = [:];
if(UtilValidate.isNotEmpty(facilityCommissionList)){
	facilityCommissionList.each { facilityCommission ->
		facilityId = facilityCommission.facilityId;
		facilityRateResult=[:];
		facCondList = [];
		facCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
		facCondList.add(EntityCondition.makeCondition("finAccountName", EntityOperator.EQUALS , finAccountName));
		facCondition = EntityCondition.makeCondition(facCondList,EntityOperator.AND);
		List<GenericValue> facilities = delegator.findList("FacilityPersonAndFinAccount", facCondition, null, null, null, false);
		if(UtilValidate.isNotEmpty(facilities)){
			facility = EntityUtil.getFirst(facilities);
			Map inputRateAmt =  UtilMisc.toMap("userLogin", userLogin);
			inputRateAmt.put("rateCurrencyUomId", "INR");
			inputRateAmt.put("facilityId", facilityId);
			inputRateAmt.put("fromDate",monthBegin );
			inputRateAmt.put("rateTypeId", "TRANSPORTER_MRGN");
			facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
		
			String partyName = "";
			if(UtilValidate.isNotEmpty(facility.firstName)){
			   partyName=facility.firstName;
			}
			if(UtilValidate.isNotEmpty(facility.lastName)){
				partyName=facility.firstName+","+facility.lastName;
			}
			if(UtilValidate.isEmpty(dtcBankMap[facilityId])){
				tempMap = [:];
				tempMap["amount"] = ((new BigDecimal(facilityCommission.totalAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
				if(UtilValidate.isNotEmpty(partyName)){
					tempMap["facilityName"] = partyName;
				}
				if(UtilValidate.isNotEmpty(facility.facilityCode)){
					tempMap["facilityCode"] = facility.facilityCode;
				}
				if(UtilValidate.isNotEmpty(facility.panId)){
					tempMap["facilityPan"] = facility.panId;
				}
				if(UtilValidate.isNotEmpty(facility.finAccountCode) && "FNACT_ACTIVE".equals(facility.statusId)){
					tempMap["facilityFinAccount"] = facility.finAccountCode;
				}
				dtcBankMap[facilityId] = tempMap;
			}else{
				Map tempMap = FastMap.newInstance();
				tempMap.putAll(dtcBankMap.get(facilityId));
				totAmount = 0 ;
				totAmount = ((new BigDecimal(facilityCommission.totalAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
				if(UtilValidate.isNotEmpty(totAmount) && totAmount!=0){
					tempMap["amount"] += totAmount;
				}
				dtcBankMap[facilityId] = tempMap;
			}
		}
	}
}
facRecoveryMap=[:];
facilityRecoveryResult = TransporterServices.getFacilityRecvoryForPeriodBilling(dctx,UtilMisc.toMap("periodBillingId",periodBillingId,"fromDate",monthBegin,"userLogin",userLogin));
facRecoveryMap=facilityRecoveryResult.get("facilityRecoveryInfoMap");
partyRecoveryInfoMap=facilityRecoveryResult.get("partyRecoveryInfoMap");

finalMap = [:];
if(UtilValidate.isNotEmpty(dtcBankMap)){
	Iterator mapIter = dtcBankMap.entrySet().iterator();
	while (mapIter.hasNext()) {
		Map.Entry entry = mapIter.next();
		 netAmount = BigDecimal.ZERO;
		 totalFine = BigDecimal.ZERO;
		 routeId = entry.getKey();
		 routeAmount = entry.getValue().get("amount");
		 facilityPan = entry.getValue().get("facilityPan");
		 facilityName = entry.getValue().get("facilityName");
		 facilityCode = entry.getValue().get("facilityCode");
		 facilityFinAccount = entry.getValue().get("facilityFinAccount");
		 if(UtilValidate.isNotEmpty(facRecoveryMap.get(routeId))){
			 facilityRecvry = facRecoveryMap.get(routeId);
			 if(UtilValidate.isNotEmpty(facilityRecvry.totalFine)){
				 totalFine = facilityRecvry.totalFine;
				 netAmount = (routeAmount-totalFine);
			 }
		 }else{
			 netAmount = routeAmount;
		 }
		 if(netAmount!=0){
			 tempMap = [:];
			 if(UtilValidate.isNotEmpty(routeAmount)){
				 tempMap["routeAmount"] = routeAmount;
			 }
			 if(UtilValidate.isNotEmpty(totalFine)){
				  tempMap["totalFine"] = totalFine;
			 }
			 if(UtilValidate.isNotEmpty(netAmount)){
				  tempMap["netAmount"] = netAmount;
			 }
			 if(UtilValidate.isNotEmpty(facilityName)){
				 tempMap["facilityName"] = facilityName;
			 }
			 if(UtilValidate.isNotEmpty(facilityCode)){
				 tempMap["facilityCode"] = facilityCode;
			 }
			 if(UtilValidate.isNotEmpty(facilityPan)){
				 tempMap["facilityPan"] = facilityPan;
			 }
			 if(UtilValidate.isNotEmpty(facilityFinAccount)){
				 tempMap["facilityFinAccount"] = facilityFinAccount;
			 }
			 tempTempMap = [:];
			 tempTempMap.putAll(tempMap);
			 finalMap.put(routeId,tempTempMap);
		 }
	}
}
context.put("finalMap",finalMap);
