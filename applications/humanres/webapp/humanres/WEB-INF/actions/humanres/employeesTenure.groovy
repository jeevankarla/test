
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(org, employeeList) {
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["groupName"]));
	internalOrgs.each { internalOrg ->
		populateChildren(internalOrg, employeeList);
	}
	
	employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", [partyIdFrom : org.partyId, roleTypeIdTo : "EMPLOYEE"],["firstName"]));
	employments.each { employment ->
		employee = [:];
		employee.put("department", org.groupName);
		employeePosition = "";
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : employment.partyId]));
		emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
		if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionId") != null){
			emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionId")], true);
			if (emplPositionType != null) {
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
		employee.put("birthDate", employment.birthDate);
		joinDate = UtilDateTime.toDateString(employment.appointmentDate, "dd/MM/yyyy");
		employee.put("joinDate", joinDate)
		
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: employment.partyId, userLogin: userLogin]);
		phoneNumber = "";
		if (partyTelephone != null && partyTelephone.contactNumber != null) {
			phoneNumber = partyTelephone.contactNumber;
		}
		employee.put("phoneNumber", phoneNumber);
		address = "";
		//partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId: employment.partyId, userLogin: userLogin]);
		//if (partyPostalAddress != null) {
		//	address = partyPostalAddress.address1+partyPostalAddress.address2+partyPostalAddress.city+"-"+partyPostalAddress.postalCode;
		//}
		//employee.put("address", address);
		employeeList.add(employee);
	}
}

employeeList = [];
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);
//Debug.logError("employeeList="+employeeList,"");
departmentMap = [:];
upcomingRetirements = [];
thruDate=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 365);

JSONArray employeesJSON = new JSONArray();

employeeList.each {employee ->
	if (departmentMap.containsKey(employee.department)) {
		departmentMap[employee.department] += 1;
	}
	else {
		departmentMap[employee.department] = 1;
	}
	if (employee.birthDate) {
		int month = (employee.birthDate).getMonth();
		int day = (employee.birthDate).getDay();
		int year = UtilDateTime.getYear(UtilDateTime.toTimestamp(employee.birthDate), timeZone, locale) + 60;	
		retirementDate = UtilDateTime.toTimestamp(month, day, year, 0, 0, 0);
		if (retirementDate < thruDate) {
			JSONArray employeeJSON = new JSONArray();
			employeeJSON.add(employee.name);
			employeeJSON.add(employee.employeeId);
			employeeJSON.add(employee.department);
			employeeJSON.add(employee.position);
			employeeJSON.add(employee.joinDate);
			employeeJSON.add(UtilDateTime.toDateString(retirementDate, "dd/MM/yyyy"));
			employeesJSON.add(employeeJSON);
		}
	}
}
JSONArray deptLabelsJSON = new JSONArray();
JSONArray deptPieDataJSON = new JSONArray();

JSONArray deptEmployeesJSON = new JSONArray();
departmentMap.each { dept ->
	JSONArray deptJSON = new JSONArray();
	deptJSON.add(dept.getKey());
	deptJSON.add(dept.getValue());
	deptEmployeesJSON.add(deptJSON);
	
	JSONObject deptPie = new JSONObject();
	deptPie.put("label", dept.getKey());
	deptPie.put("data", dept.getValue());
	deptPieDataJSON.add(deptPie);
}
Debug.logError("departmentMap="+departmentMap,"");
Debug.logError("deptEmployeesJSON="+deptEmployeesJSON,"");
Debug.logError("deptPieDataJSON="+deptPieDataJSON,"");
Debug.logError("employeesJSON="+employeesJSON,"");
context.deptEmployeesJSON = deptEmployeesJSON;
context.deptPieDataJSON = deptPieDataJSON;
context.employeesJSON = employeesJSON;



