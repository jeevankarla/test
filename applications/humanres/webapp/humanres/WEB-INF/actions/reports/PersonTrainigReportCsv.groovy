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
fromDateStr = parameters.TrainingFromDate;
thruDateStr = parameters.TrainingThruDate;
conditionList = [];
personTrainingMap = [:];
finalTempMap = [:];
if(UtilValidate.isNotEmpty(topicsCoverd)){
	conditionList.add(EntityCondition.makeCondition("topicsCoverd", EntityOperator.EQUALS ,topicsCoverd));
}
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
if(UtilValidate.isNotEmpty(fromDateStr)){
   try {
	   fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(fromDateStr)));
	   conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
   } catch (ParseException e) {
   }
}
if(UtilValidate.isNotEmpty(thruDateStr)){
	   try {
		   thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(thruDateStr)));
		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			   EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
	   } catch (ParseException e) {
	   }
}

Cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
personTrainingList = delegator.findList("PersonTraining", Cond, null, null, null, false);
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
if(UtilValidate.isNotEmpty(personTrainingList)){
	personTrainingDetails1 = EntityUtil.getFirst(personTrainingList);
	prevTopicCoverd = personTrainingDetails1.topicsCoverd;
	prevTrainingLocation = personTrainingDetails1.trainingLocation
	prevfromDateTime = personTrainingDetails1.fromDate;
	prevfromDate =  UtilDateTime.toDateString(prevfromDateTime, "dd-MMM-yy");
	prevthruDateTime = personTrainingDetails1.thruDate;
	prevthruDate =  UtilDateTime.toDateString(prevthruDateTime, "dd-MMM-yy");
	prevduration = personTrainingDetails1.duration;
	prevtrgCategory = personTrainingDetails1.trgCategory;
	prevfacultyType = personTrainingDetails1.facultyType;
	prevnameOfInstitute = personTrainingDetails1.nameOfInstitute;
	prevtraingCost = personTrainingDetails1.traingCost;
}
trainingList = [];
prevTrainingList = [];
if(UtilValidate.isNotEmpty(personTrainingList)){
	for(int i=0;i<personTrainingList.size();i++){
		tempMap = [:];
		personTrainingDetails = personTrainingList.get(i);
		topicsCoverd = personTrainingDetails.topicsCoverd;
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
		fromDate = UtilDateTime.toDateString(fromDateTime, "dd-MMM-yy");
		thruDate = UtilDateTime.toDateString(thruDateTime, "dd-MMM-yy");
		
		tempMap.put("partyId", partyId);
		partyName = PartyHelper.getPartyName(delegator, partyId, true);
		tempMap.put("partyName", partyName);
		employeePosition = "";
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : partyId]));
		emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
		if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
			emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
			if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && UtilValidate.isNotEmpty(emplPositionAndFulfillment.getString("name"))){
				employeePosition = emplPositionAndFulfillment.getString("name");
			}else if (emplPositionType != null) {
				employeePosition = emplPositionType.getString("description");
			}
			else {
				employeePosition = emplPositionAndFulfillment.getString("emplPositionId");
			}
		}
		tempMap.put("designation", employeePosition);
		exprList = [];
		deptName = "";
		exprList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"DEPATMENT_NAME"));
		exprList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
		exprList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
		exprList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		EntityCondition exprCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		partyRelationshipList = delegator.findList("PartyRelationship", exprCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(partyRelationshipList)){
			deptId = (EntityUtil.getFirst(partyRelationshipList)).get("partyIdFrom");
			partyGroupDetails = delegator.findOne("PartyGroup", [partyId : deptId], false);
			if(UtilValidate.isNotEmpty(partyGroupDetails)){
				deptName = 	partyGroupDetails.groupName;
			}
		}
		tempMap.put("deptName",deptName);
		finalTempMap.put("trainingLocation", trainingLocation);
		finalTempMap.put("fromDate", fromDate);
		finalTempMap.put("thruDate", thruDate);
		finalTempMap.put("duration", duration);
		finalTempMap.put("trgCategory", trgCategory);
		finalTempMap.put("facultyType", facultyType);
		finalTempMap.put("nameOfInstitute", nameOfInstitute);
		finalTempMap.put("traingCost", traingCost);
		
		if(prevTopicCoverd.equals(topicsCoverd)){
			if(UtilValidate.isNotEmpty(tempMap)){
					trainingList.addAll(tempMap);
				}
				finalTempMap.put("trainingLocation", trainingLocation);
				finalTempMap.put("fromDate", fromDate);
				finalTempMap.put("thruDate", thruDate);
				finalTempMap.put("duration", duration);
				finalTempMap.put("trgCategory", trgCategory);
				finalTempMap.put("facultyType", facultyType);
				finalTempMap.put("nameOfInstitute", nameOfInstitute);
				finalTempMap.put("traingCost", traingCost);
				finalTempMap.put("emplDetailsList", trainingList);
				finalMap.put(topicsCoverd, finalTempMap);
				prevTrainingList = trainingList;
			
		}else{
		
			finalTempMap.put("emplDetailsList", prevTrainingList);
			prevTrainingList = trainingList;
			finalTempMap.put("trainingLocation", prevTrainingLocation);
			finalTempMap.put("fromDate", prevfromDate);
			finalTempMap.put("thruDate", prevthruDate);
			finalTempMap.put("duration", prevduration);
			finalTempMap.put("trgCategory", prevtrgCategory);
			finalTempMap.put("facultyType", prevfacultyType);
			finalTempMap.put("nameOfInstitute", prevnameOfInstitute);
			finalTempMap.put("traingCost", prevtraingCost);
			
			finalMap.put(prevTopicCoverd, finalTempMap);
			prevTrainingList = trainingList;
			prevTopicCoverd = topicsCoverd;
			prevTrainingLocation = trainingLocation;
			prevfromDate = fromDate;
			prevthruDate = thruDate;
			prevduration = duration;
			prevtrgCategory = trgCategory;
			prevfacultyType = facultyType;
			prevnameOfInstitute = nameOfInstitute;
			prevtraingCost = traingCost;
			trainingList = [];
			if(UtilValidate.isNotEmpty(tempMap)){
				trainingList.addAll(tempMap);
			}
			
			finalTempMap = [:];
		}
		
	}
}

if(UtilValidate.isNotEmpty(finalMap)){
	context.finalMap = finalMap;	
}
