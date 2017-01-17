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
	conditionList.add(EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS ,employeeId));
}
conditionList.add(EntityCondition.makeCondition("employeeRoleTypeId", EntityOperator.EQUALS ,"EMPLOYEE"));
PerfReviewCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
perfReviewList = delegator.findList("PerfReview", PerfReviewCond, null, null, null, false);
performanceRatingList = [];
if(UtilValidate.isNotEmpty(perfReviewList)){
	for(int i=0;i<perfReviewList.size();i++){
		tempMap = [:]
		perfReviewDetails = perfReviewList.get(i);
		perfReviewId = perfReviewDetails.perfReviewId;
		employeePartyId = perfReviewDetails.employeePartyId;
		emplPositionId = perfReviewDetails.emplPositionId;
		fromDateTime = perfReviewDetails.fromDate;
		thruDateTime = perfReviewDetails.thruDate;
		fromDate = UtilDateTime.toDateString(fromDateTime, "dd-MM-yyy");
		thruDate = UtilDateTime.toDateString(thruDateTime, "dd-MM-yyyy");
		designation = null;
		EmplPosition = delegator.findOne("EmplPosition",[emplPositionId : emplPositionId], true);
		if(UtilValidate.isNotEmpty(EmplPosition)){
			emplPositionTypeId = EmplPosition.emplPositionTypeId;
			emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionTypeId], true);
			if (emplPositionType != null) {	
				designation = emplPositionType.getString("description");  //map
				
			}
		}
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
					perfRatingTypeId = PerfReviewItemDetails.perfRatingTypeId;
					perfReviewItemTypeId = PerfReviewItemDetails.perfReviewItemTypeId;
					PromotionDateTime = PerfReviewItemDetails.PromotionDate;
					ConfirmationDateTime = PerfReviewItemDetails.ConfirmationDate; 
					perfReviewItemSeqId = PerfReviewItemDetails.perfReviewItemSeqId;
					
					PromotionDate = UtilDateTime.toDateString(PromotionDateTime, "dd-MM-yyy");
					ConfirmationDate = UtilDateTime.toDateString(ConfirmationDateTime, "dd-MM-yyyy");
					tempMap.put("partyId", employeePartyId);
					tempMap.put("fromDate", fromDate);
					tempMap.put("thruDate", thruDate);
					tempMap.put("perfReviewId", perfReviewId);
					tempMap.put("promotion", designation);
					
					tempMap.put("perfReviewItemSeqId", perfReviewItemSeqId);
					tempMap.put("PerfRatingType", perfRatingTypeId);
					tempMap.put("PromotionDate", PromotionDate);
					tempMap.put("ConfirmationDate", ConfirmationDate);
					if(UtilValidate.isNotEmpty(tempMap)){
						performanceRatingList.addAll(tempMap);
					}
			}
		}
	}
		
}
		
}
context.performanceRatingList = performanceRatingList;
