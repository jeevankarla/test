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
import org.ofbiz.entity.util.EntityFindOptions;
import java.sql.*;
import org.ofbiz.base.util.UtilDateTime;
import java.util.Calendar;
import java.math.BigDecimal;
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



//get Branch list start
resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


Map formatMap = [:];
List formatList = [];
context.branchList =resultCtx.get("productStoreList");
//	for (eachList in resultCtx.get("productStoreList")) {
//		
//		formatMap = [:];
//		formatMap.put("productStoreName",eachList.get("storeName"));
//		formatMap.put("payToPartyId",eachList.get("payToPartyId"));
//		formatList.addAll(formatMap);
//		
//	}
//context.formatList = formatList;

//get Branch list end


JSONArray headItemsJson = new JSONArray();
JSONObject newObj = new JSONObject();
statusId = "";
Debug.log("****************_______________1************************************************");
if (UtilValidate.isNotEmpty(parameters.periodBillingId)) {
	GenericValue periodBilling = delegator.findOne("PeriodBilling", [periodBillingId : parameters.periodBillingId], false);
	if(UtilValidate.isNotEmpty(periodBilling)){
		statusId = periodBilling.get("statusId");
		newObj.put("id",parameters.periodBillingId+"["+parameters.periodBillingId+"]");
		newObj.put("statusId",statusId);
		headItemsJson.add(newObj);
	}
}
context.put("statusId", statusId);
request.setAttribute("headItemsJson", headItemsJson);
request.setAttribute("statusId", statusId);

//get SchemeTimePeriod list start
	condList = [];
	condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "DEPOT_REIMB_YEAR"));
	condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate( UtilDateTime.nowTimestamp())));
	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp()))));
    depotReimbYearList = delegator.findList("SchemeTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	depotReimbYear = EntityUtil.getFirst(depotReimbYearList);
//	Debug.log(depotReimbYear+"****************************************************************");
	if(depotReimbYear) {
		condList.clear();
		condList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS,depotReimbYear.get("schemeTimePeriodId")));
		condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp())));
//		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
//		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.toSqlDate(UtilDateTime.nowTimestamp()))));
		List<String> orderBy = UtilMisc.toList("periodNum");
		 depotReimbPeriodList = delegator.findList("SchemeTimePeriod", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		List reimbPeriodList = [];
		context.reimbPeriodList = depotReimbPeriodList;
		//Debug.log(reimbPeriodList+"****************************************************************");
		for (eachList in depotReimbPeriodList) {
			
			depotReimbPeriodMap = [:];
			timePeriodDate=new SimpleDateFormat("MMM").format(eachList.get("fromDate"))+", "+new SimpleDateFormat("yyyy").format(eachList.get("fromDate"));
			
			if(UtilValidate.isNotEmpty(eachList.get("thruDate"))){
				timePeriodDate=timePeriodDate+" - "+new SimpleDateFormat("MMM").format(eachList.get("thruDate"))+", "+new SimpleDateFormat("yyyy").format(eachList.get("thruDate"));
			}
			depotReimbPeriodMap.put("schemeTimePeriodId",eachList.get("schemeTimePeriodId"));
			depotReimbPeriodMap.put("value",timePeriodDate);
			reimbPeriodList.addAll(depotReimbPeriodMap);
			
		}
		//Debug.log(reimbPeriodList+"****************************************************************");
	  //  context.reimbPeriodList = reimbPeriodList;

	
		}
//get SchemeTimePeriod list end
