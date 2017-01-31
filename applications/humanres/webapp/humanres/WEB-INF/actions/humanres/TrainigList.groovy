import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.party.party.PartyHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;

dctx = dispatcher.getDispatchContext();
topicsCoverd = parameters.topicsCoverd;
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
Timestamp fromDate=null;
Timestamp thruDate=null;
finalMap = [:];
prevTopicCoverd = null;
prevTrainingLocation = null;
prevfromDate = null;
prevthruDate = null;
prevduration = null;
prevtrgCategory = null;
prevfacultyType = null;
prevnameOfInstitute = null;
prevtraingCost = null;

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
if(UtilValidate.isNotEmpty(personTrainingList)){
	personTrainingDetails1 = EntityUtil.getFirst(personTrainingList);
	prevTopicCoverd = personTrainingDetails1.topicsCoverd;
	prevTrainingLocation = personTrainingDetails1.trainingLocation
	prevfromDateTime = personTrainingDetails1.fromDate;
	prevfromDate =  UtilDateTime.toDateString(prevfromDateTime, "dd-MM-yyyy");
	prevthruDateTime = personTrainingDetails1.thruDate;
	prevthruDate =  UtilDateTime.toDateString(prevthruDateTime, "dd-MM-yyyy");
	prevduration = personTrainingDetails1.duration;
	prevtrgCategory = personTrainingDetails1.trgCategory;
	prevfacultyType = personTrainingDetails1.facultyType;
	prevnameOfInstitute = personTrainingDetails1.nameOfInstitute;
	prevtraingCost = personTrainingDetails1.traingCost;
}
prevTrainingList = [];
trainingList = [];
prevTempMap = [:];
tempMap1 = [:];
tempMap1.put("topicsCoverd", prevTopicCoverd);
tempMap1.put("trainingLocation", prevTrainingLocation);
tempMap1.put("fromDate", prevfromDate);
tempMap1.put("thruDate", prevthruDate);
tempMap1.put("duration", prevduration);
tempMap1.put("trgCategory", prevtrgCategory);
tempMap1.put("facultyType", prevfacultyType);
tempMap1.put("nameOfInstitute", prevnameOfInstitute);
tempMap1.put("traingCost", prevtraingCost);
if(UtilValidate.isNotEmpty(personTrainingList)){
		for(int i=0;i<personTrainingList.size();i++){
		tempMap = [:];
		personTrainingDetails = personTrainingList.get(i);
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
		fromDate1 = UtilDateTime.toDateString(fromDateTime, "dd-MM-yyyy");
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
		if((prevTopicCoverd.equals(topicsCoverd)) && (prevTrainingLocation.equals(trainingLocation)) && (prevfromDate.equals(fromDate1)) && (prevthruDate.equals(thruDate1)) && (prevduration.equals(duration)) && (prevtrgCategory.equals(trgCategory)) && (prevfacultyType.equals(facultyType)) && (prevnameOfInstitute.equals(nameOfInstitute)) && (prevtraingCost.equals(traingCost))){
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
				prevTempMap = tempMap;
		}else{
		
			tempMap.put("topicsCoverd", prevTopicCoverd);
			tempMap.put("trainingLocation", prevTrainingLocation);
			tempMap.put("fromDate", prevfromDate);
			tempMap.put("thruDate", prevthruDate);
			tempMap.put("duration", prevduration);
			tempMap.put("trgCategory", prevtrgCategory);
			tempMap.put("facultyType", prevfacultyType);
			tempMap.put("nameOfInstitute", prevnameOfInstitute);
			tempMap.put("traingCost", prevtraingCost);
			prevTopicCoverd = topicsCoverd;
			prevTrainingLocation = trainingLocation;
			prevfromDate = fromDate1;
			prevthruDate = thruDate1;
			prevduration = duration;
			prevtrgCategory = trgCategory;
			prevfacultyType = facultyType;
			prevnameOfInstitute = nameOfInstitute;
			prevtraingCost = traingCost;
			if(UtilValidate.isNotEmpty(tempMap)){
				trainingList.addAll(tempMap);
			}
		}
		
	}
}
if(UtilValidate.isNotEmpty(parameters.topicsCoverd)){
	if(UtilValidate.isNotEmpty(tempMap1)){
		trainingList.addAll(tempMap1);
	}
}
context.trainingList = trainingList;
