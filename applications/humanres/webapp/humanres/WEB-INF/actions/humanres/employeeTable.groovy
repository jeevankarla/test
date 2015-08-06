
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;

flag = parameters.allEmployees;
dctx = dispatcher.getDispatchContext();
def populateChildren(org, employeeList) {
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["groupName"]));
	internalOrgs.each { internalOrg ->
		populateChildren(internalOrg, employeeList);
	}
	if(UtilValidate.isNotEmpty(flag) && flag.equals("true")){
		employments = delegator.findByAnd("EmploymentAndPerson", [partyIdFrom : org.partyId, roleTypeIdTo : "EMPLOYEE"],["firstName"]);
	}else{
		employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", [partyIdFrom : org.partyId, roleTypeIdTo : "EMPLOYEE"],["firstName"]));
	}
	
	employments.each { employment ->
		employee = [:];
		
		employee.put("department", org.groupName);
		employeePosition = "";
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : employment.partyId]));
     
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
		employee.put("position", employeePosition);
		String lastName="";
		if(employment.lastName!=null){
			lastName=employment.lastName;
		}
		employee.put("name", employment.firstName + " " + lastName);
		employee.put("employeeId", employment.partyId);
		joinDate = UtilDateTime.toDateString(employment.fromDate, "dd/MM/yyyy");
		employee.put("joinDate", joinDate);
		resignDate=UtilDateTime.toDateString(employment.resignationDate,"dd/MM/yyyy");
		employee.put("resignDate",resignDate);
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: employment.partyId, userLogin: userLogin]);
		phoneNumber = "";
		if (partyTelephone != null && partyTelephone.contactNumber != null) {
			phoneNumber = partyTelephone.contactNumber;
		}
		employee.put("phoneNumber", phoneNumber);
		gender = "";
		if (UtilValidate.isNotEmpty(employment.gender)) {
			gender = employment.gender;
		}		
		employee.put("gender",employment.gender);
		bloodGroup = "";
		if(UtilValidate.isNotEmpty(employment.bloodGroup)){
			bloodGroups=delegator.findByAnd("Enumeration",[enumId:employment.bloodGroup]);
			if(UtilValidate.isNotEmpty(bloodGroups)){
				bloodGroup=bloodGroups.get(0).description;
			}
			else {
				bloodGroup = employment.bloodGroup;
			}
		}
		employee.put("bloodGroup",bloodGroup);
		employeeList.add(employee);
		
		
	}
}

employeeList = [];
internalOrgs=[];
context.internalOrgs=internalOrgs;
context.employeeList=employeeList;
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);
JSONArray employeesJSON = new JSONArray();
employeeList.each {employee ->
	JSONArray employeeJSON = new JSONArray();
	employeeJSON.add(employee.name);
	employeeJSON.add(employee.employeeId);
	employeeJSON.add(employee.department);
	employeeJSON.add(employee.position);
	employeeJSON.add(employee.joinDate);
	employeeJSON.add(employee.phoneNumber);
	employeeJSON.add(employee.gender);
	employeeJSON.add(employee.bloodGroup);	
	employeesJSON.add(employeeJSON);
}
context.employeesJSON = employeesJSON;
//Debug.logError("employeeList="+employeeList,"");
//Debug.logError("employeesJSON="+employeesJSON,"");


