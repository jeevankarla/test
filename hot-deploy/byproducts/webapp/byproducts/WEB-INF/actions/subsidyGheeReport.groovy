import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();

effectiveDateStr = parameters.fromDate;
thruEffectiveDateStr = parameters.thruDate;

if (UtilValidate.isEmpty(effectiveDateStr)) {
	effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
}
if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
	thruEffectiveDate = effectiveDate;
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruEffectiveDate = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr+" 00:00:00").getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
	}
}

monthBegin = UtilDateTime.getDayStart(effectiveDate);
monthEnd = UtilDateTime.getDayEnd(thruEffectiveDate);
context.put("dayBegin",monthBegin);
Timestamp pMonthEnd=UtilDateTime.getMonthEnd(monthEnd,TimeZone.getDefault(),Locale.getDefault());
Timestamp nMonthStart=UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(pMonthEnd, 1));
context.nMonthStart=nMonthStart;
printDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy");
context.printDate = printDate;
conditionList = [];
finalList=[];

Map employeeMap = UtilMisc.toMap("userLogin", userLogin);
employees = dispatcher.runSync("getActiveEmployees", employeeMap);
employeesList=employees.get("employeesResult").get("employeeList");
employeeIdList=[];
for(i=0;i<employeesList.size();i++){
	employeeId=employeesList.get(i).get("employeeId");
	employeeIdList.add(employeeId);
	
}

conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN ,employeeIdList));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthEnd)));

conditionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS , "PAYROL_DD_GH_DED"));
cond =EntityCondition.makeCondition(conditionList,EntityOperator.AND);
partyDeductionList = delegator.findList("PartyDeduction", cond,null, ["partyIdTo"], null, false);
totalAmount=0;
totalQty=0;
if (UtilValidate.isNotEmpty(partyDeductionList)) {
	for (GenericValue partyDeduction : partyDeductionList) {
		partyWiseMap=[:];
		quantity=1;
		tempMap=[:];
		partyName="";
		cost=0;
		partyId=(String) partyDeduction.getString("partyIdTo");
		//Debug.log("partyId ######################"+partyId);
		if (UtilValidate.isNotEmpty(partyId)) {
		    partyName=PartyHelper.getPartyName(delegator, partyId, false);
		}
		if (UtilValidate.isNotEmpty(partyDeduction.cost)) {
			cost=partyDeduction.cost;
		}
		partyWiseMap["partyName"]=partyName;
		partyWiseMap["quantity"]=quantity;
		partyWiseMap["cost"]=cost;
		tempMap[partyId]=partyWiseMap;
		totalQty=totalQty+quantity;
		if(partyDeduction.cost){
			totalAmount=totalAmount+partyDeduction.cost;
		}
		finalList.add(tempMap);
		context.finalList=finalList;
		context.totalQty = totalQty;
		context.totalAmount = totalAmount;
	}

}

