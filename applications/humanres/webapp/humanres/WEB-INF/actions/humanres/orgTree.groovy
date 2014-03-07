
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(org, treeNode) {
	JSONArray childNodesList= new JSONArray();
	boolean recurse = false;
	
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
		if (recurse) {
			populateChildren(internalOrg, groupNode);
		}
		childNodesList.add(groupNode);
	}	
	
	employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", [partyIdFrom : org.partyId, roleTypeIdTo : "EMPLOYEE"],["firstName"]));
	employments.each { employment ->
		JSONObject personNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		attrNode.put("nodetype", "employee");
		attrNode.put("id", employment.partyId);
		personNode.put("attr", attrNode);		
		JSONObject dataNode= new JSONObject();			
		dataNode.put("title", employment.firstName + " " + employment.lastName);
		JSONObject href = new JSONObject();
		href.put("href", "#");
		href.put("onClick","callDocument('EmployeeProfile?partyId=" + employment.partyId + "')");
		dataNode.put("attr", href);
		personNode.put("data", dataNode);		
		childNodesList.add(personNode);
	}
	
	treeNode.put("children",childNodesList);
}

String partyId = request.getParameter("partyId");

JSONArray treeNodesList = new JSONArray();

if (!partyId) {
	JSONObject rootNode= new JSONObject();
	rootNode.put("state","open"); 
	company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
	JSONObject attrNode= new JSONObject();
	attrNode.put("nodetype", "company");
	rootNode.put("attr", attrNode);
	JSONObject dataNode= new JSONObject();
	dataNode.put("title", company.groupName);
	JSONObject hrefNode = new JSONObject();
	hrefNode.put("href", "#");
	hrefNode.put("onClick","callDocument('viewprofile?partyId=" + company.partyId + "')");				
	dataNode.put("attr", hrefNode);
	rootNode.put("data", dataNode);
	populateChildren(company, rootNode);
	treeNodesList.add(rootNode);
}
else {
	JSONObject currNode= new JSONObject();
	currentOrg = delegator.findByPrimaryKey("PartyAndGroup", [partyId : partyId]);
	populateChildren(currentOrg, currNode);
	treeNodesList.add(currNode);
}

if (parameters.ajaxLookup == "Y") {
	request.setAttribute("treeNodeJSON", treeNodesList[0].getJSONArray("children").toString());
}
else {
	context.put("treeNodesListJSON", treeNodesList.toString());
}


//Debug.logError("partyId="+partyId,"");
//Debug.logError("treeNodesList="+treeNodesList,"");