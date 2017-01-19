import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;

dctx = dispatcher.getDispatchContext();
employeeId = parameters.employeeId;
conditionList=[];
if(UtilValidate.isNotEmpty(employeeId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employeeId));
}
PerfReviewCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
personTrainingList = delegator.findList("PersonTraining", PerfReviewCond, null, null, null, false);
performanceRatingList = [];
trainingList = [];
if(UtilValidate.isNotEmpty(personTrainingList)){
	for(int i=0;i<personTrainingList.size();i++){
		tempMap = [:]
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
		fromDate = UtilDateTime.toDateString(fromDateTime, "dd-MM-yyy");
		thruDate = UtilDateTime.toDateString(thruDateTime, "dd-MM-yyyy");
		
		tempMap.put("partyId", partyId);
		tempMap.put("trainingRequestId", trainingRequestId);
		tempMap.put("topicsCoverd", topicsCoverd);
		tempMap.put("trainingLocation", trainingLocation);
		tempMap.put("fromDate", fromDate);
		tempMap.put("thruDate", thruDate);
		tempMap.put("duration", duration);
		tempMap.put("trgCategory", trgCategory);
		tempMap.put("facultyType", facultyType);
		tempMap.put("nameOfInstitute", nameOfInstitute);
		tempMap.put("traingCost", traingCost);
		if(UtilValidate.isNotEmpty(tempMap)){
			trainingList.addAll(tempMap);
		}
}
}

context.trainingList = trainingList;
