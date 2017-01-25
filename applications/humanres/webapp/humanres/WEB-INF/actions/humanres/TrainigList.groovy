import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.party.party.PartyHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import applications.humanres.src.in.vasista.vbiz.humanres.FastList;
//import applications.humanres.src.in.vasista.vbiz.humanres.ParseException;
//import applications.humanres.src.in.vasista.vbiz.humanres.SimpleDateFormat;
//import applications.humanres.src.in.vasista.vbiz.humanres.String;
//import applications.humanres.src.in.vasista.vbiz.humanres.Timestamp;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;

dctx = dispatcher.getDispatchContext();
topicsCoverd = parameters.topicsCoverd;
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
Timestamp fromDate=null;
Timestamp thruDate=null;
try {
	if(UtilValidate.isNotEmpty(fromDateStr)){
		fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(fromDateStr)));
	}
}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+ fromDateStr, module);
	}
try {
	if(UtilValidate.isNotEmpty(thruDateStr)){
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(thruDateStr)));
	}
}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+ thruDateStr, module);
	}
conditionList=[];
if(UtilValidate.isNotEmpty(topicsCoverd)){
	conditionList.add(EntityCondition.makeCondition("topicsCoverd", EntityOperator.EQUALS ,topicsCoverd));
}
if(UtilValidate.isNotEmpty(fromDate)){
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
if(UtilValidate.isNotEmpty(thruDate)){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
}

PerfReviewCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
personTrainingList = delegator.findList("PersonTraining", PerfReviewCond, null, null, null, false);
trainingList = [];
if(UtilValidate.isNotEmpty(personTrainingList)){
		tempMap = [:]	
		personTrainingDetails = EntityUtil.getFirst(personTrainingList);
		partyId = personTrainingDetails.partyId;
		trainingRequestId = personTrainingDetails.trainingRequestId;
		topicsCoverd = personTrainingDetails.topicsCoverd;
		trainingLocation = personTrainingDetails.trainingLocation;
		fromDateTime = personTrainingDetails.fromDate;
		thruDateTime = personTrainingDetails.thruDate;
		duration = personTrainingDetails.duration;
		trgCategory = personTrainingDetails.trgCategory;
		facultyType = personTrainingDetails.facultyType;
		nameOfInstitute = personTrainingDetails.nameOfInstitute;
		traingCost = personTrainingDetails.traingCost;
		fromDate1 = UtilDateTime.toDateString(fromDateTime, "dd-MM-yyy");
		thruDate1 = UtilDateTime.toDateString(thruDateTime, "dd-MM-yyyy");
		
		tempMap.put("partyId", partyId);
		tempMap.put("trainingRequestId", trainingRequestId);
		tempMap.put("topicsCoverd", topicsCoverd);
		tempMap.put("trainingLocation", trainingLocation);
		tempMap.put("fromDate", fromDate1);
		tempMap.put("thruDate", thruDate1);
		tempMap.put("duration", duration);
		tempMap.put("trgCategory", trgCategory);
		tempMap.put("facultyType", facultyType);
		tempMap.put("nameOfInstitute", nameOfInstitute);
		tempMap.put("traingCost", traingCost);
		if(UtilValidate.isNotEmpty(tempMap)){
			trainingList.addAll(tempMap);
		}
}

context.trainingList = trainingList;
