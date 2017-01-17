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
fromDateStr = parameters.assessmentFromDate;
thruDateStr = parameters.assessmentThruDate;
conditionList = [];

if(UtilValidate.isNotEmpty(employeeId)){
	conditionList.add(EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS ,employeeId));
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

conditionList.add(EntityCondition.makeCondition("employeeRoleTypeId", EntityOperator.EQUALS ,"EMPLOYEE"));
PerfReviewCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
perfReviewList = delegator.findList("PerfReview", PerfReviewCond, null, null, null, false);
performanceRatingList = [];
sNo = 0;
if(UtilValidate.isNotEmpty(perfReviewList)){
	for(int i=0;i<perfReviewList.size();i++){
		tempMap = [:]
		perfReviewDetails = perfReviewList.get(i);
		perfReviewId = perfReviewDetails.perfReviewId;
		employeePartyId = perfReviewDetails.employeePartyId;
		emplPositionId = perfReviewDetails.emplPositionId;
		fromDateTime = perfReviewDetails.fromDate;
		thruDateTime = perfReviewDetails.thruDate;
		fromDate = UtilDateTime.toDateString(fromDateTime, "MMM yy");
		thruDate = UtilDateTime.toDateString(thruDateTime, "MMM yy");
		assessmentPeriod = fromDate + " to " + thruDate;
		designation = null;
		EmplPosition = delegator.findOne("EmplPosition",[emplPositionId : emplPositionId], true);
		if(UtilValidate.isNotEmpty(EmplPosition)){
			emplPositionTypeId = EmplPosition.emplPositionTypeId;
			emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionTypeId], true);
			if (emplPositionType != null) {
				designation = emplPositionType.getString("description");  //map
				
			}
		}
		
		partyName = PartyHelper.getPartyName(delegator, employeePartyId, true);
		
		
		if(UtilValidate.isNotEmpty(perfReviewId)){
			condList = [];
			condList.add(EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS ,employeePartyId));
			condList.add(EntityCondition.makeCondition("employeeRoleTypeId", EntityOperator.EQUALS ,"EMPLOYEE"));
			condList.add(EntityCondition.makeCondition("perfReviewId", EntityOperator.EQUALS ,perfReviewId));
			PerfReviewItemCond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			PerfReviewItemList = delegator.findList("PerfReviewItem", PerfReviewItemCond, null, null, null, false);
			if(UtilValidate.isNotEmpty(PerfReviewItemList)){
				for(int j=0;j<PerfReviewItemList.size();j++){
					PerfReviewItemDetails = PerfReviewItemList.get(j);
					sNo = sNo + 1;
					perfRatingTypeId = PerfReviewItemDetails.perfRatingTypeId;
					perfReviewItemTypeId = PerfReviewItemDetails.perfReviewItemTypeId;
					PromotionDateTime = PerfReviewItemDetails.PromotionDate;
					ConfirmationDateTime = PerfReviewItemDetails.ConfirmationDate;
					perfReviewItemSeqId = PerfReviewItemDetails.perfReviewItemSeqId;
					
					PromotionDate = UtilDateTime.toDateString(PromotionDateTime, "dd-MM-yyy");
					ConfirmationDate = UtilDateTime.toDateString(ConfirmationDateTime, "dd-MM-yyyy");
					tempMap.put("sNo", sNo);
					tempMap.put("partyId", employeePartyId);
					tempMap.put("partyName", partyName);
					
					tempMap.put("assessmentPeriod", assessmentPeriod);
					tempMap.put("perfReviewId", perfReviewId);
					tempMap.put("promotion", designation);
					
					tempMap.put("perfReviewItemSeqId", perfReviewItemSeqId);
					tempMap.put("PerfRatingType", perfRatingTypeId);
					tempMap.put("PromotionDate", PromotionDate);
					tempMap.put("ConfirmationDate", ConfirmationDate);
					exprList = [];
					deptName = "";
					exprList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"DEPATMENT_NAME"));
					exprList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
					exprList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employeePartyId));
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
					casteName="";
					casteIds=delegator.findByAnd("PartyClassification", [partyId: employeePartyId],["partyClassificationGroupId"]);
					if(UtilValidate.isNotEmpty(casteIds)){
						casteId=casteIds.get(0).partyClassificationGroupId;
						casteList=delegator.findByAnd("PartyClassificationGroup", [partyClassificationGroupId: casteId],["description"]);
						casteName=casteList.get(0).description;
						tempMap.put("caste",casteName);
					}
					else{
						tempMap.put("caste",casteName);
					}	
					exprList1 = [];
					deptName = "";
					exprList1.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
					exprList1.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
					exprList1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,"COMPANY"));
					exprList1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employeePartyId));
					exprList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
					EntityCondition exprCond1 = EntityCondition.makeCondition(exprList1,EntityOperator.AND);
					employmentList = delegator.findList("EmploymentAndPerson", exprCond1, null, null, null, false);
					if(UtilValidate.isNotEmpty(employmentList)){
						employmentDetails = EntityUtil.getFirst(employmentList);
						joinDate = UtilDateTime.toDateString(employmentDetails.appointmentDate, "dd-MMM-yy");
						birthDate = UtilDateTime.toDateString(employmentDetails.birthDate, "dd-MMM-yy");
						tempMap.put("joinDate", joinDate);
						tempMap.put("birthDate", birthDate);
					}		
					
					if(UtilValidate.isNotEmpty(tempMap)){
						performanceRatingList.addAll(tempMap);
					}
			}
		}
	}
		
}
		
}
context.performanceRatingList = performanceRatingList;
