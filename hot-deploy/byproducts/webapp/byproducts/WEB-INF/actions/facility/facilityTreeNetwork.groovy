
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(facility, treeNodesList, enableEdit) {
	boolean recurse = true;
	JSONArray childNodesList= new JSONArray();
	boothMembers = delegator.findList("FacilityGroupMemberAndFacility", EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, facility.facilityGroupId), null, null, null, false);
	boothMembers = EntityUtil.filterByDate(boothMembers);
	boothMembers = EntityUtil.filterByDate(boothMembers, UtilDateTime.nowTimestamp(), "openedDate", "closedDate", false); 
	Debug.logInfo("boothMembers: " + boothMembers, "");
	
	//childFacilities = delegator.findByAnd("Facility", [zoneId : facility.facilityId],["sequenceNum","facilityName","facilityId"]);
	boothMembers.each { boothMember ->
		childFacility = delegator.findByPrimaryKey("Facility", [facilityId : boothMember.facilityId]);
		JSONObject groupNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		JSONObject dataNode= new JSONObject();
		groupNode.put("state","open");
		attrNode.put("id", childFacility.facilityId);
		attrNode.put("nodetype", "childType");
		dataNode.put("nodetype", "childType");
		title = childFacility.facilityName+" ["+childFacility.facilityId+"]";
		if (childFacility.facilityTypeId == 'ZONE') {
			attrNode.put("nodetype", "zoneType");
			dataNode.put("nodetype", "zoneType");
		}
		else if (childFacility.categoryTypeEnum == 'DEWS_PARLOURS') {
			attrNode.put("nodetype", "dewsParlorType");
			dataNode.put("nodetype", "dewsParlorType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'FROS') {
			attrNode.put("nodetype", "frosType");
			dataNode.put("nodetype", "frosType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'MCCS') {
			attrNode.put("nodetype", "mccsType");
			dataNode.put("nodetype", "mccsType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'PARLOUR') {
			attrNode.put("nodetype", "parlourType");
			dataNode.put("nodetype", "parlourType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'AVM_FROS') {
			attrNode.put("nodetype", "avmFrosType");
			dataNode.put("nodetype", "avmFrosType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'INSTITUTIONS') {
			attrNode.put("nodetype", "institutionType");
			dataNode.put("nodetype", "institutionType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'KFROS') {
			attrNode.put("nodetype", "kfrosType");
			dataNode.put("nodetype", "kfrosType");
			groupNode.put("state","leaf");
			recurse = false;
		}
		else if (childFacility.categoryTypeEnum == 'WHOLESALE_DEALERS') {
			attrNode.put("nodetype", "wholesaleType");
			dataNode.put("nodetype", "wholesaleType");
			groupNode.put("state","leaf");
			recurse = false;
		}else{
			attrNode.put("nodetype", "otherType");
			dataNode.put("nodetype", "otherType");
			groupNode.put("state","leaf");
			recurse = false;
		}
			
		groupNode.put("attr", attrNode);
		dataNode.put("title", title);
		if (enableEdit)  {
			JSONObject href = new JSONObject();
			href.put("href", "#");
			href.put("onClick","callDocument('FacilityProfile?facilityId=" + childFacility.facilityId + "')");
			dataNode.put("attr", href);
		}
		groupNode.put("data", dataNode);
		if (recurse) {
			populateChildren(childFacility, groupNode, enableEdit);
		}
		childNodesList.add(groupNode);
	}	
	JSONObject treeNode= new JSONObject();
	treeNode.put("children",childNodesList);
	treeNodesList.add(treeNode);
}

def populateRootNodes(region, treeNodesList, enableEdit) {
	JSONObject rootNode= new JSONObject();
	rootNode.put("state","open"); 
	JSONObject attrNode= new JSONObject();
	attrNode.put("nodetype", "shiftType");
	
	rootNode.put("attr", attrNode);
	
	JSONObject href = new JSONObject();
	href.put("href", "#");
	href.put("onClick","callDocument('EditFacilityGeoPoint?facilityGroupId=" + region.facilityGroupId + "')");
	JSONObject dataNode= new JSONObject();
	dataNode.put("title", region.facilityGroupName);
	dataNode.put("nodetype", "shiftType");	
	dataNode.put("attr", href);
	rootNode.put("data", dataNode);

	JSONArray childNodesList= new JSONArray();
	routeMembers = delegator.findList("FacilityGroupMember", EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, region.facilityGroupId), null, null, null, false);
	routeMembers.each { routeMember ->
		routeFacility = delegator.findByPrimaryKey("Facility", [facilityId : routeMember.facilityId]);
		JSONObject routeNode= new JSONObject();		
		routeNode.put("state","closed");
		JSONObject attr2Node= new JSONObject();
		attr2Node.put("id", routeMember.facilityId);
		attr2Node.put("nodetype", "routeType");
		JSONObject href2 = new JSONObject();
		href2.put("href", "#");
		href2.put("onClick","callDocument('FacilityProfile?facilityId=" + routeMember.facilityId + "')");
		routeNode.put("attr", attr2Node);
		JSONObject data2Node= new JSONObject();
		data2Node.put("title", routeFacility.facilityName);
		data2Node.put("nodetype", "routeType");
		data2Node.put("attr", href2);
		routeNode.put("data", data2Node);
		//populateChildren(routeFacility, routeNode, enableEdit);
		childNodesList.add(routeNode);
	}
	rootNode.put("children",childNodesList);
	treeNodesList.add(rootNode);
}

boolean enableEdit = true;
JSONArray treeNodesList = new JSONArray();
String facilityId = request.getParameter("facilityId");
boolean zoneOwner = false;
userLogin = session.getAttribute("userLogin");
if (userLogin.partyId) {
	roleTypeAndParty = delegator.findByAnd("RoleTypeAndParty", ['partyId': userLogin.partyId, 'roleTypeId': 'ZONE_OWNER']);
	if (roleTypeAndParty) {
		enableEdit = false;
		zoneOwner = true;
	}
}

//if (isTreeRoot=='Y') {
if (!facilityId) {
	regions = delegator.findList("FacilityGroup", EntityCondition.makeCondition("primaryParentGroupId", EntityOperator.EQUALS, "SUPPLYTIME_RT_GROUP"), null, null, null, false);
	regions.each { region ->
		populateRootNodes(region, treeNodesList, enableEdit);
	}
}
else {
	rootFacilityGrp = delegator.findByPrimaryKey("FacilityGroup", [facilityGroupId : facilityId]);
	populateChildren(rootFacilityGrp, treeNodesList, enableEdit);
	rootFacility = delegator.findByPrimaryKey("Facility", [facilityId : facilityId]);
	context.facility = rootFacility;
}

if (parameters.ajaxLookup == "Y") {
	request.setAttribute("treeNodeJSON", treeNodesList[0].getJSONArray("children").toString());
}
else {
	context.put("treeNodesListJSON", treeNodesList.toString());
	
}
