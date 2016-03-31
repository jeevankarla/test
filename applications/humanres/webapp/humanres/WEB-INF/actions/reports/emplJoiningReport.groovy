
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
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.NewEmplfromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.NewEmplfromDate).getTime()));
		}
	if (parameters.NewEmplthruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.NewEmplthruDate).getTime()));
		}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);


Map FinalMap = [:];


Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
emplInputMap.put("orgPartyId", "Company");
cadreEmployeeList = [];
employementIds = [];
partyIdsList = [];
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

if(UtilValidate.isNotEmpty(employementIds)){
	sNo = 0;
	employementIds.each{ employee->
		sNo = sNo+1;
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
			Emplcode = EmplJoiningDateDetails.get("partyIdTo");
			DetailsMap.put("AppointmentDate",EmplappointmentDate);
			DetailsMap.put("Emplcode",Emplcode);
			

		
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

condition4List = [];
condition4List.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , employee));
condition4 = EntityCondition.makeCondition(condition4List,EntityOperator.AND);
EmplJoiningGenderList = delegator.findList("Person", condition4, null, null, null, false);
	if(UtilValidate.isNotEmpty(EmplJoiningGenderList)){
			EmplJoiningGenderDetails = EntityUtil.getFirst(EmplJoiningGenderList);
			EmplappointmentGenderList = EntityUtil.getFieldListFromEntityList(EmplJoiningGenderList, "gender", true);
			EmplappointmentGender = EmplJoiningGenderDetails.get("gender");
			DetailsMap.put("gender",EmplappointmentGender);		
}

condition5List = [];
condition5List.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , employee));
condition5 = EntityCondition.makeCondition(condition5List,EntityOperator.AND);
EmplPlaceOfJoiningList = delegator.findList("EmployeeDetail", condition4, null, null, null, false);
	if(UtilValidate.isNotEmpty(EmplPlaceOfJoiningList)){
				EmplPlaceOfJoiningDetails = EntityUtil.getFirst(EmplPlaceOfJoiningList);
				PlaceOfJoiningList = EntityUtil.getFieldListFromEntityList(EmplPlaceOfJoiningList, "placeOfJoining", true);
				PlaceOfJoining = EmplPlaceOfJoiningDetails.get("placeOfJoining");
				JoiningDate = EmplPlaceOfJoiningDetails.get("joinDate");
				DetailsMap.put("PlaceOfJoining",PlaceOfJoining);
				DetailsMap.put("JoiningDate",JoiningDate);
				Debug.log("JoiningDate======="+employee+"===="+JoiningDate);
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

conditionnnList = [];
conditionnnList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , employee));
conditionnnList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

conditionnn = EntityCondition.makeCondition(conditionnnList,EntityOperator.AND);
EmplLocationList = delegator.findList("Employment", conditionnn, null, null, null, false);
if(UtilValidate.isNotEmpty(EmplLocationList)){
	EmplLocationDetails = EntityUtil.getFirst(EmplLocationList);
	EmplLocations = EntityUtil.getFieldListFromEntityList(EmplLocationList, "locationGeoId", true);
	EmplLocation = EmplLocationDetails.get("locationGeoId");
	conditionnn1List = [];
	conditionnn1List.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS , EmplLocation));
	conditionnn1 = EntityCondition.makeCondition(conditionnn1List,EntityOperator.AND);
	EmplLocationsList = delegator.findList("Geo", conditionnn1, null, null, null, false);
		if(UtilValidate.isNotEmpty(EmplLocationsList)){
			PostingDetails = EntityUtil.getFirst(EmplLocationsList);
			EmplPostings = EntityUtil.getFieldListFromEntityList(EmplLocationsList, "geoName", true);
			EmplPosting= PostingDetails.get("geoName");
			DetailsMap.put("EmplPosting",EmplPosting);
			}

}

FinalMap.put(employee, DetailsMap);

}
}



context.FinalMap = FinalMap;


