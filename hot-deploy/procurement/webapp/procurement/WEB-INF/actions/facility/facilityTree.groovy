
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(facility, treeNode, enableEdit) {
	boolean recurse = true;
	JSONArray childNodesList= new JSONArray();
	
	childFacilities = delegator.findByAnd("Facility", [parentFacilityId : facility.facilityId],["facilityCode","facilityName"]);
	
	facilitiesList = [];
	childFacilities.each { rootFacility ->
		facilityMap = [:];
		facilityMap.putAll(rootFacility);
		facilityMap.putAt("intFacCode",Integer.parseInt((String)rootFacility.get("facilityCode")));
		facilitiesList.add(facilityMap);
	}
	
	facilitiesList = UtilMisc.sortMaps(facilitiesList, UtilMisc.toList("intFacCode"));
	
	facilitiesList.each { childFacility ->
		JSONObject groupNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		JSONObject dataNode= new JSONObject();		
		groupNode.put("state","closed"); 
		attrNode.put("id", childFacility.facilityId);
		attrNode.put("nodetype", "childType");
		dataNode.put("nodetype", "childType");	
		title =  childFacility.facilityCode + " (" + childFacility.facilityName + ")";			
		if (childFacility.facilityTypeId == 'UNIT') {
			attrNode.put("nodetype", "zoneType");
			dataNode.put("nodetype", "zoneType");							
			groupNode.put("state","open");
			//title = title+"["+childFacility.facilityCode+"]";
			
		}
		else if (childFacility.facilityTypeId == 'PROC_ROUTE') {
			attrNode.put("nodetype", "routeType");
			dataNode.put("nodetype", "routeType");												
			groupNode.put("state","closed"); 
			recurse = false;								
		} 	
		else if (childFacility.facilityTypeId == 'CENTER') {
			attrNode.put("nodetype", "boothType");
			dataNode.put("nodetype", "boothType");	
			groupNode.put("state","leaf"); 	
			//title = title + " [" + childFacility.description + "]["+childFacility.facilityCode+"]";
		} 		
		groupNode.put("attr", attrNode);		
		dataNode.put("title", title);
		if (enableEdit)  {
			JSONObject href = new JSONObject();
			href.put("href", "#");
			href.put("onClick","callDocument('EditProcFacility?facilityId=" + childFacility.facilityId + "')");
			dataNode.put("attr", href);
		}
		groupNode.put("data", dataNode);
		if (recurse) {		
			populateChildren(childFacility, groupNode, enableEdit);
		}
		childNodesList.add(groupNode);
	}	
	
	treeNode.put("children",childNodesList);
}

def populateRootNodes(rootFacility, treeNodesList, enableEdit) {
	JSONObject rootNode= new JSONObject();
	rootNode.put("state","open"); 
	JSONObject attrNode= new JSONObject();
	attrNode.put("nodetype", "rootType");
	rootNode.put("attr", attrNode);
	JSONObject dataNode= new JSONObject();
	dataNode.put("title", rootFacility.facilityName);		
	if (rootFacility.facilityTypeId == 'UNIT') {
		attrNode.put("nodetype", "zoneType");
		dataNode.put("nodetype", "zoneType");							
	}	
	if (rootFacility.facilityTypeId == 'PROC_ROUTE') {
		attrNode.put("nodetype", "routeType");
		dataNode.put("nodetype", "routeType");							
	}		
	if (enableEdit) {		
		JSONObject href = new JSONObject();
		href.put("href", "#");
		href.put("onClick","callDocument('EditProcFacility?facilityId=" + rootFacility.facilityId + "')");
		dataNode.put("attr", href);	
	}	
	rootNode.put("attr", attrNode);
	rootNode.put("data", dataNode);
	populateChildren(rootFacility, rootNode, enableEdit);
	treeNodesList.add(rootNode);
}

boolean enableEdit = true;
JSONArray treeNodesList = new JSONArray();
String facilityId = request.getParameter("facilityId");

boolean zoneOwner = false;
userLogin = session.getAttribute("userLogin");
roleTypeAndParty = null;
if (userLogin.partyId) {
	roleTypeAndParty = delegator.findByAnd("FacilityParty", ['partyId': userLogin.partyId, 'roleTypeId': 'PROCUREMENT_ROLE']);
	roleTypeAndParty = EntityUtil.filterByDate(roleTypeAndParty);
	if (roleTypeAndParty) {
		enableEdit = true;
		zoneOwner = true;
		roleTypeAndParty = EntityUtil.getFirst(roleTypeAndParty)
	}
}

if (facilityId == null && zoneOwner) {
	// for now we assume a user can own only a single zone. 
	// Should not be a big deal to handle the other scenario..
	owningFacility = delegator.findOne("Facility", [facilityId : roleTypeAndParty.facilityId] , false);
	if (owningFacility)  {
		facilityId = owningFacility.facilityId;
		context.facilityId = facilityId;
		context.facility = owningFacility;
		//Debug.logInfo("context="+context,"");
	}	
}

//if (isTreeRoot=='Y') {
if (!facilityId) {
// start from the root facility(s)
	rootFacilities = delegator.findByAnd("Facility", [facilityTypeId : "PLANT"],["facilityCode","facilityName"]);
	rootFacilities.each { rootFacility ->
		populateRootNodes(rootFacility, treeNodesList, enableEdit);
	}
}
else {
	rootFacility = delegator.findByPrimaryKey("Facility", [facilityId : facilityId]);
	populateRootNodes(rootFacility, treeNodesList, enableEdit);
	context.facility = rootFacility;
}

//Debug.logInfo("facilityId="+facilityId,"");


if (parameters.ajaxLookup == "Y") {
request.setAttribute("treeNodeJSON", treeNodesList[0].getJSONArray("children").toString());
}
else {
context.put("treeNodesListJSON", treeNodesList.toString());
}