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
employeeId = parameters.employeeId;
fromDateStr = parameters.TrainingFromDate;
thruDateStr = parameters.TrainingThruDate;
conditionList = [];

if(UtilValidate.isNotEmpty(employeeId)){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,employeeId));
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
sNo = 0;
trainingList = [];
if(UtilValidate.isNotEmpty(personTrainingList)){
	for(int i=0;i<personTrainingList.size();i++){
		tempMap = [:]
		personTrainingDetails = personTrainingList.get(i);
		sNo = sNo+1;
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
		tempMap.put("sNo", sNo);
		tempMap.put("partyId", partyId);
		tempMap.put("topicsCoverd", topicsCoverd);
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


