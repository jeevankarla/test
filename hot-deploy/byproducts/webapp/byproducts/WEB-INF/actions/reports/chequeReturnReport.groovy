import org.ofbiz.base.util.*;
import java.math.RoundingMode;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;
import java.text.ParseException;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.network.LmsServices;
import org.ofbiz.entity.util.EntityFindOptions;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

fromDate = null;
thruDate = null;
dctx = dispatcher.getDispatchContext();

if (UtilValidate.isEmpty(parameters.fromDate)) {
	fromDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.fromDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.fromDate, "");
	}
}

if (UtilValidate.isEmpty(parameters.thruDate)) {
	thruDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruDate = new java.sql.Timestamp(sdf.parse(parameters.thruDate+" 00:00:00").getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.thruDate, "");
	}
}
dayBegin = UtilDateTime.getDayStart(fromDate);
dayEnd = UtilDateTime.getDayEnd(thruDate);
context.periodFromDate = UtilDateTime.toDateString(dayBegin, "dd/MM/yyyy");
context.periodThruDate = UtilDateTime.toDateString(dayEnd, "dd/MM/yyyy");

dateType = parameters.dateType;
penaltyResult = ByProductNetworkServices.getChequePenaltyTotals(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd, "userLogin", userLogin, "dateType", dateType));
chequeReturnDetails = penaltyResult.get("chequeReturnDetails");
paymentIds = [];
chequeReturnMap = [:];
chequeReturnDetails.each{eachReturn ->
	
	paymentIds.add(eachReturn.paymentId);
	
	if(chequeReturnMap.get(eachReturn.facilityId)){
		existTempList = [];
		existTempList = chequeReturnMap.get(eachReturn.facilityId);
		existTempList.add(eachReturn);
		chequeReturnMap.put(eachReturn.facilityId, existTempList);
	}else{
		tempList = [];
		tempList.add(eachReturn);
		chequeReturnMap.put(eachReturn.facilityId, tempList);
	}
}
condList  = [];
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
finAccount = delegator.findList("FinAccount", cond, null, null, null, false);

finAccountMap = [:];

finAccount.each{eachAccount ->
	finAccountMap.put(eachAccount.finAccountId, eachAccount.finAccountName);
}
finAccountTrans = delegator.findList("FinAccountTrans", EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIds), null, null, null, false);
finTransMap = [:];
finAccountTrans.each{eachTrans ->
	finTransMap.put(eachTrans.paymentId, finAccountMap.get(eachTrans.finAccountId));
}

context.chequeReturnMap = chequeReturnMap;
context.finTransMap = finTransMap;


 	


