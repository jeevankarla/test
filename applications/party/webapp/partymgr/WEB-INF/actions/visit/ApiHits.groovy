import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import java.sql.Date;
import java.sql.Timestamp;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONArray;

endTime =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

startTime = UtilDateTime.addDaysToTimestamp(endTime, -30);
startTime = UtilDateTime.getDayStart(startTime);


//Debug.logInfo("startTime="+startTime+"; endTime=" + endTime, "");
JSONArray listJSON= new JSONArray();

iterTime = startTime;
while (iterTime <= endTime) {
	curDate = new Date(iterTime.getTime());
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(iterTime)));
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(iterTime)));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	apiHits = delegator.findList("ApiHit", condition, null, null, null, false);
	
	JSONArray dayList= new JSONArray();
	dayList.add(iterTime.getTime());
	dayList.add(apiHits.size());
	listJSON.add(dayList);
	
	iterTime = UtilDateTime.addDaysToTimestamp(iterTime, 1);
}

Debug.logInfo("listJSON="+listJSON, "");
context.listJSON=listJSON;

