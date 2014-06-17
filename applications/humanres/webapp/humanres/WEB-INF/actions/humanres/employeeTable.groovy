
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(org, employeeList) {
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["groupName"]));
	internalOrgs.each { internalOrg ->
		JSONObject groupNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		attrNode.put("nodetype", "department");
		attrNode.put("id", internalOrg.partyId);
		groupNode.put("attr", attrNode);
		JSONObject dataNode= new JSONObject();
		dataNode.put("nodetype", "department");
		dataNode.put("title", internalOrg.groupName);
		JSONObject href = new JSONObject();
		href.put("href", "#");
		href.put("onClick","callDocument('viewprofile?partyId=" + internalOrg.partyId + "')");
		dataNode.put("attr", href);
		groupNode.put("data", dataNode);
		groupNode.put("state","closed");
		populateChildren(internalOrg, employeeList);
	}
	
	employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", [partyIdFrom : org.partyId, roleTypeIdTo : "EMPLOYEE"],["firstName"]));
	employments.each { employment ->
		employee = [:];
		employee.put("department", org.groupName);
		employeePosition = "";
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", [employeePartyId : employment.partyId]));
		emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
		if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
			employeePosition = emplPositionAndFulfillment.getString("emplPositionTypeId");
		}
		employee.put("position", employeePosition);
		String lastName="";
		if(employment.lastName!=null){
			lastName=employment.lastName;
		}
		employee.put("name", employment.firstName + " " + lastName);
		employee.put("employeeId", employment.partyId);
		fromDate = UtilDateTime.toDateString(employment.fromDate, "dd/MM/yy");
		employee.put("joinDate", fromDate)
		
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
JSONArray employeesJSON = new JSONArray();
employeeList.each {employee ->
	JSONArray employeeJSON = new JSONArray();
	employeeJSON.add(employee.name);
	employeeJSON.add(employee.employeeId);
	employeeJSON.add(employee.department);
	employeeJSON.add(employee.position);
	employeeJSON.add(employee.joinDate);
	employeeJSON.add(employee.phoneNumber);
	//employeeJSON.add(employee.address);
	employeesJSON.add(employeeJSON);
}
context.employeesJSON = employeesJSON;
//Debug.logError("employeeList="+employeeList,"");
//Debug.logError("employeesJSON="+employeesJSON,"");


