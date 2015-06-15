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
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;
context.timePeriodEnd= timePeriodEnd;

Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);

Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
if(UtilValidate.isNotEmpty(parameters.partyIdTo)){
	employementIds=UtilMisc.toList(parameters.partyIdTo);
}



prodConditionList=[];
prodConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS ,"83"));
prodCondition = EntityCondition.makeCondition(prodConditionList,EntityOperator.AND);
productList = delegator.findList("Product", prodCondition, null, null, null, false);

context.put("productList", productList);

EmployeeSubsidyGheeMap = [:];

if(UtilValidate.isNotEmpty(employementIds)){
	employementIds.each{ qtyEmpl->
		prodMap=[:];
		if(UtilValidate.isNotEmpty(productList)){
			productList = EntityUtil.getFirst(productList);
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,qtyEmpl));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productList.productId));
			conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , parameters.customTimePeriodId));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			employeeSubsidyGhee = delegator.findList("EmployeeSubsidyProductIssue", condition, null, null, null, false);
			qty="";
			if(UtilValidate.isNotEmpty(employeeSubsidyGhee)){
				employeeGhee = EntityUtil.getFirst(employeeSubsidyGhee);
				if(UtilValidate.isNotEmpty(employeeGhee)){
					qty = employeeGhee.quantity;
				}
			}
			prodMap.putAt(productList.productId, qty);
		}
		if(UtilValidate.isNotEmpty(prodMap)){
			EmployeeSubsidyGheeMap.putAt(qtyEmpl, prodMap)
		}
	}
}


JSONArray headSubsidyGheeJSON = new JSONArray();
if(UtilValidate.isNotEmpty(EmployeeSubsidyGheeMap)){
	Iterator BenfIter = EmployeeSubsidyGheeMap.entrySet().iterator();
	while(BenfIter.hasNext()){
		Map.Entry entry = BenfIter.next();
		emplyId= entry.getKey();
		JSONObject newObj = new JSONObject();
		//getting MilkCards booth
		conList=[]
		conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,emplyId));
		conList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , parameters.customTimePeriodId));
		cond = EntityCondition.makeCondition(conList,EntityOperator.AND);
		partySubsidyGheeList = delegator.findList("EmployeeSubsidyProductIssue", cond, null, null, null, false);
		newObj.put("id",emplyId);
		newObj.put("partyId",emplyId);
		newObj.put("boothId","");
		if(UtilValidate.isNotEmpty(partySubsidyGheeList)){
			subsidyGheeList = EntityUtil.getFirst(partySubsidyGheeList);
			if(UtilValidate.isNotEmpty(subsidyGheeList)){
				boothId = subsidyGheeList.facilityId;
				newObj.put("boothId",boothId);
			}
		}
		newObj.put("periodId",parameters.customTimePeriodId);
		if(UtilValidate.isNotEmpty(entry.getValue())){
			Iterator headerItemIter = (entry.getValue()).entrySet().iterator();
			while(headerItemIter.hasNext()){
				Map.Entry itemEntry = headerItemIter.next();
				benefitAmt=((itemEntry.getValue()));
				String key = itemEntry.getKey();
				newObj.put(key,(itemEntry.getValue()));
			}
		}
		headSubsidyGheeJSON.add(newObj);
	}
}


context.headItemsJson=headSubsidyGheeJSON;





