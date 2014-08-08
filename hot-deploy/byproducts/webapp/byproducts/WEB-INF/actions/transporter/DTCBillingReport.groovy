import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilMisc;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
dctx = dispatcher.getDispatchContext();

month = parameters.month;
if(UtilValidate.isEmpty(month)){
	Debug.logError("Month Cannot Be Empty","");
	context.errorMessage = "Month Cannot Be Empty";
	return;
}

def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
try {
	monthTime = new java.sql.Timestamp(sdf.parse(month+"-01 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: ", "");
}
Timestamp monthBegin = UtilDateTime.getMonthStart(monthTime);
Timestamp monthEnd = UtilDateTime.getMonthEnd(monthTime, timeZone, locale);
Map<String, String> facilityPartyMap = FastMap.newInstance();
conditionList=[];
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, "Contractor"));
conditionList.add(EntityCondition.makeCondition("facilityTypeId",  EntityOperator.EQUALS,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

facilityPartyList = delegator.findList("FacilityFacilityPartyAndPerson", condition, null, null, null, false);

	for (GenericValue facilityParty : facilityPartyList) {
		tempMap=[:];
		partyWiseMap=[:];
		String routeId = (String) facilityParty.getString("facilityId");
		String partyId = (String) facilityParty.getString("partyId");
		if (facilityParty.getString("facilityTypeId").equals("ROUTE")) {
			String partyName = PartyHelper.getPartyName(delegator, partyId, true);
			partyWiseMap["thruDate"]=facilityParty.getTimestamp("thruDate");
			partyWiseMap["partyName"]=partyName;
			tempMap[partyId]=partyWiseMap;
			facilityPartyMap.put(facilityParty.getString("facilityId"),	tempMap);
		}
	}

	facilityWorkOrdrNumMap = [:];
	facilityWorkOrderNoList = delegator.findList("FacilityAttribute", EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "WORK_ORDER_NO"), null, null, null, false);
	facilityWorkOrderNoList.each{eachWork ->
		facilityWorkOrdrNumMap.put(eachWork.facilityId, eachWork.attrValue);
	}
context.facilityWorkOrdrNumMap = facilityWorkOrdrNumMap;//workOrder Numbers
//Debug.log("facilityPartyMap==="+facilityPartyMap);
context.facilityPartyMap = facilityPartyMap;
