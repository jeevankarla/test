
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(org, treeNode) {
	JSONArray childNodesList= new JSONArray();
	
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["groupName"]));
	internalOrgs.each { internalOrg ->
		JSONObject groupNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		attrNode.put("nodetype", "department");
		groupNode.put("attr", attrNode);		
		JSONObject dataNode= new JSONObject();	
		dataNode.put("nodetype", "department");				
		dataNode.put("title", internalOrg.groupName);
		JSONObject href = new JSONObject();
		href.put("href", "#");
		href.put("onClick","callDocument('viewprofile?partyId=" + internalOrg.partyId + "')");
		dataNode.put("attr", href);
		groupNode.put("data", dataNode);
		groupNode.put("state","open"); 					
		populateChildren(internalOrg, groupNode);
		childNodesList.add(groupNode);
	}	
	
	employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", [partyIdFrom : org.partyId, roleTypeIdTo : "EMPLOYEE"],["firstName"]));
	employments.each { employment ->
		JSONObject personNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		attrNode.put("nodetype", "employee");
		personNode.put("attr", attrNode);		
		JSONObject dataNode= new JSONObject();			
		dataNode.put("title", employment.firstName + " " + employment.lastName);
		JSONObject href = new JSONObject();
		href.put("href", "#");
		href.put("onClick","callDocument('viewprofile?partyId=" + employment.partyId + "')");
		dataNode.put("attr", href);
		personNode.put("data", dataNode);		
		childNodesList.add(personNode);
	}
	
	treeNode.put("children",childNodesList);
}

JSONArray treeNodesList = new JSONArray();
JSONObject rootNode= new JSONObject();
rootNode.put("state","open"); 
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : partyId]);
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
context.put("treeNodesListJSON", treeNodesList.toString());