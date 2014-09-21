import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import java.sql.Date;
import java.sql.Timestamp;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import net.sf.json.JSONArray;

endTime =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

startTime = UtilDateTime.addDaysToTimestamp(endTime, -30);
startTime = UtilDateTime.getDayStart(startTime);


//Debug.logInfo("startTime="+startTime+"; endTime=" + endTime, "");
JSONArray listJSON= new JSONArray();
JSONArray listRetailerJSON= new JSONArray();
JSONArray listEmployeeJSON= new JSONArray();
JSONArray listChangeIndentsJSON= new JSONArray();

dayHitsMap = [:];
EntityListIterator apiHitsIter = null;
try {
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, startTime));
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.LESS_THAN_EQUAL_TO, endTime));
	conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_EQUAL, 'hrmsapi'));
	
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	apiHitsIter = delegator.find("ApiHit", condition, null, null, null, null);
	while ((apiHit = apiHitsIter.next()) != null) {
		hitDate = UtilDateTime.getDayEnd(apiHit.get("startDateTime"));
		String userLoginId= apiHit.getString("userLoginId");
		String contentId= apiHit.getString("contentId");

		String partyId = "";
		String role = "";
		if (userLoginId) {
			userLogin = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
			if (userLogin) {
				partyId = userLogin.partyId;
				if (partyId) {
					partyRoles = delegator.findByAnd("PartyRole", [partyId : partyId]);
					partyRoles.each { partyRole ->
						if (partyRole.roleTypeId.equals("Retailer")) {
							role = "Retailer";
						}
						else if (partyRole.roleTypeId.equals("EMPLOYEE")) {
							role = "Employee";
						}
					}
				}
			}
		}
		def hitMap = [totalHits : 0, retailerHits : 0, employeeHits : 0, changeIndentHits : 0];		
		if (dayHitsMap.containsKey(hitDate.getTime())) {
			hitMap = dayHitsMap[hitDate.getTime()];		
		}

		hitMap["totalHits"] +=  1;
		if (role == "Retailer") {
			hitMap["retailerHits"] += 1;
		}
		if (role == "Employee") {
			hitMap["employeeHits"] += 1;
		}
		if (contentId == "webtools.processChangeIndentApi") {
			hitMap["changeIndentHits"] += 1;		
		}
		dayHitsMap[hitDate.getTime()] = hitMap;
	}
} finally {
	if (apiHitsIter != null) {
		apiHitsIter.close();
	}
}
	
iterTime = UtilDateTime.getDayEnd(startTime);
while (iterTime <= endTime) {
	totalHits = 0;
	retailerHits = 0;
	employeeHits = 0;
	changeIndentHits = 0;
	
	if (dayHitsMap.containsKey(iterTime.getTime())) {
		hitMap = dayHitsMap[iterTime.getTime()];
		if (hitMap.containsKey("totalHits")) {
			totalHits = hitMap["totalHits"];
		}
		if (hitMap.containsKey("retailerHits")) {
			retailerHits = hitMap["retailerHits"];
		}
		if (hitMap.containsKey("employeeHits")) {
			employeeHits = hitMap["employeeHits"];
		}
		if (hitMap.containsKey("changeIndentHits")) {
			changeIndentHits = hitMap["changeIndentHits"];
		}		
	}
	JSONArray dayList= new JSONArray();
	dayList.add(iterTime.getTime());
	dayList.add(totalHits);
	listJSON.add(dayList);
	
	JSONArray dayRetailerList= new JSONArray();
	dayRetailerList.add(iterTime.getTime());
	dayRetailerList.add(retailerHits);
	listRetailerJSON.add(dayRetailerList);
	
	JSONArray dayEmployeeList= new JSONArray();
	dayEmployeeList.add(iterTime.getTime());
	dayEmployeeList.add(employeeHits);
	listEmployeeJSON.add(dayEmployeeList);

	JSONArray changeIndentsList= new JSONArray();
	changeIndentsList.add(iterTime.getTime());
	changeIndentsList.add(changeIndentHits);
	listChangeIndentsJSON.add(changeIndentsList);	
	
	iterTime = UtilDateTime.addDaysToTimestamp(iterTime, 1);
}


//Debug.logInfo("listJSON="+listJSON, "");
context.listJSON=listJSON;
context.listRetailerJSON=listRetailerJSON;
context.listEmployeeJSON=listEmployeeJSON;
context.listChangeIndentsJSON=listChangeIndentsJSON;


