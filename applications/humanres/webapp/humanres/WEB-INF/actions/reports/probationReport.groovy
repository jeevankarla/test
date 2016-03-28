
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import javolution.util.FastList;
import javolution.util.FastMap;
import in.vasista.vbiz.humanres.HumanresService;
import java.sql.Timestamp;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import java.lang.String;
import java.lang.Object;

dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("yyyy-MM-dd");


partyId=parameters.employeeId;

Debug.log("partyId============"+partyId);

Timestamp effectiveDate = UtilDateTime.nowTimestamp();
String todayDate = UtilDateTime.toDateString(effectiveDate,"yyyy-MM-dd");

fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(todayDate).getTime()));

thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(todayDate).getTime()));
Map FinalMap = [:];
List employeeList = FastList.newInstance();
List employeeIdList = FastList.newInstance();
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("orgPartyId","Company");
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);


employments=EmploymentsMap.get("employementList");
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyIdTo"));

if(UtilValidate.isNotEmpty(employments)){
	employeeIdList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
	}

if(UtilValidate.isNotEmpty(parameters.employeeId)){
	employeeList = UtilMisc.toList(parameters.employeeId);
}else{
	employeeList = employeeIdList;
}
Debug.log("employeeList=============="+employeeList);
if(UtilValidate.isNotEmpty(employeeList)){
	employeeList.each{ employee->
		Map DetailsMap=FastMap.newInstance();
		partyId=parameters.employeeId;


		nameList=delegator.findByAnd("Person", [partyId: employee],["firstName","middleName","lastName"]);
		firstName=nameList.get(0).firstName;
		middleName=nameList.get(0).middleName;
		lastName=nameList.get(0).lastName;
		DetailsMap.put("firstName",firstName);
		DetailsMap.put("middleName",middleName);
		DetailsMap.put("lastName",lastName);
		
condition3List = [];
condition3List.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , employee));
condition3List.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
condition3 = EntityCondition.makeCondition(condition3List,EntityOperator.AND);
EmplJoiningDateList = delegator.findList("Employment", condition3, null, null, null, false);
	if(UtilValidate.isNotEmpty(EmplJoiningDateList)){
			EmplJoiningDateDetails = EntityUtil.getFirst(EmplJoiningDateList);
			EmplappointmentDates = EntityUtil.getFieldListFromEntityList(EmplJoiningDateList, "appointmentDate", true);
			EmplappointmentDate = EmplJoiningDateDetails.get("appointmentDate");
			DetailsMap.put("AppointmentDate",EmplappointmentDate);
			

		
}
		
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , employee));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
EmplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", condition, null, null, null, false);
if(UtilValidate.isNotEmpty(EmplPositionFulfillmentList)){
	EmplPositionDetails = EntityUtil.getFirst(EmplPositionFulfillmentList);
	EmplPositionIds = EntityUtil.getFieldListFromEntityList(EmplPositionFulfillmentList, "emplPositionId", true);
	EmplPositionId = EmplPositionDetails.get("emplPositionId");
condition1List = [];
condition1List.add(EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS , EmplPositionId));
condition1List.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee));
condition1 = EntityCondition.makeCondition(condition1List,EntityOperator.AND);
EmplPositionList = delegator.findList("EmplPosition", condition1, null, null, null, false);
	if(UtilValidate.isNotEmpty(EmplPositionList)){
		EmplPositionTypeDetails = EntityUtil.getFirst(EmplPositionList);
		EmplPositiontypeIds = EntityUtil.getFieldListFromEntityList(EmplPositionList, "emplPositionTypeId", true);
		EmplPositiontypeId = EmplPositionTypeDetails.get("emplPositionTypeId");

		condition2List = [];
		condition2List.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS , EmplPositiontypeId));
		condition2 = EntityCondition.makeCondition(condition2List,EntityOperator.AND);
		EmplPositionnList = delegator.findList("EmplPositionType", condition2, null, null, null, false);
	
			if(UtilValidate.isNotEmpty(EmplPositionnList)){
				EmplPositionDetails = EntityUtil.getFirst(EmplPositionnList);
				EmplPositionDescription = EntityUtil.getFieldListFromEntityList(EmplPositionnList, "description", true);
				EmplPositionDesc = EmplPositionDetails.get("description");
				DetailsMap.put("designation",EmplPositionDesc);
			   
				}
	}
	
}
conditionnList = [];
conditionnList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , employee));
conditionnList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

conditionn = EntityCondition.makeCondition(conditionnList,EntityOperator.AND);
EmplPayHistoryList = delegator.findList("PayHistory", conditionn, null, null, null, false);
if(UtilValidate.isNotEmpty(EmplPayHistoryList)){
	EmplPayHistoryDetails = EntityUtil.getFirst(EmplPayHistoryList);
	EmplpayHistoryGradeIds = EntityUtil.getFieldListFromEntityList(EmplPayHistoryList, "payGradeId", true);
	EmplpayHistoryGradeId = EmplPayHistoryDetails.get("payGradeId");
	conditionn1List = [];
	conditionn1List.add(EntityCondition.makeCondition("payGradeId", EntityOperator.EQUALS , EmplpayHistoryGradeId));
	conditionn1 = EntityCondition.makeCondition(conditionn1List,EntityOperator.AND);
	EmplPayGradeList = delegator.findList("PayGrade", conditionn1, null, null, null, false);
		if(UtilValidate.isNotEmpty(EmplPayGradeList)){
			EmplPayGradeDetails = EntityUtil.getFirst(EmplPayGradeList);
			Emplscale = EntityUtil.getFieldListFromEntityList(EmplPayGradeList, "payScale", true);
			EmplPayscale= EmplPayGradeDetails.get("payScale");
			DetailsMap.put("payScale",EmplPayscale);
		}

}
		

FinalMap.put(employee, DetailsMap);
Debug.log("FinalMap================="+FinalMap);
		
}
}


context.FinalMap = FinalMap;


