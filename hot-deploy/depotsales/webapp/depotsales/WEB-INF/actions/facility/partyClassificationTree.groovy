
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
def populateChildren(facility, treeNodesList, enableEdit) {
	boolean recurse = true;
	JSONArray childNodesList= new JSONArray();
	boothMembers = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, facility.partyIdFrom), null, null, null, false);
	boothMembers = EntityUtil.filterByDate(boothMembers);
	boothMembers = EntityUtil.filterByDate(boothMembers, UtilDateTime.nowTimestamp(), "openedDate", "closedDate", false);
	Debug.logInfo("boothMembers: " + boothMembers, "");
	
	//childFacilities = delegator.findByAnd("Facility", [zoneId : facility.facilityId],["sequenceNum","facilityName","facilityId"]);
	boothMembers.each { boothMember ->
		childFacility = delegator.findByPrimaryKey("Party", [partyId : boothMember.partyIdTo]);
		JSONObject groupNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		JSONObject dataNode= new JSONObject();
		groupNode.put("state","open");
		attrNode.put("id", childFacility.partyId);
		attrNode.put("nodetype", "childType");
		dataNode.put("nodetype", "childType");
		title=childFacility.partyId;
		//title= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, childFacility.partyId, false);
		
		//title = childFacility.facilityName+" ["+childFacility.partyId+"]";
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
			href.put("onClick","callDocument('FacilityProfile?facilityId=" + childFacility.partyId + "')");
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
	href.put("onClick","callDocument('EditFacilityGeoPoint?facilityGroupId=" + region.partyId + "')");
	JSONObject dataNode= new JSONObject();
	//title= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, region.partyId, false);
	title=region.partyId;
	dataNode.put("title", title);
	dataNode.put("nodetype", "shiftType");
	dataNode.put("attr", href);
	rootNode.put("data", dataNode);

	JSONArray childNodesList= new JSONArray();
	routeMembers = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, region.partyId), null, null, null, false);
	routeMembers.each { routeMember ->
		routeFacility = delegator.findByPrimaryKey("Party", [partyId : routeMember.partyIdTo]);
		JSONObject routeNode= new JSONObject();
		routeNode.put("state","closed");
		JSONObject attr2Node= new JSONObject();
		attr2Node.put("id", routeMember.partyIdTo);
		attr2Node.put("nodetype", "routeType");
		JSONObject href2 = new JSONObject();
		href2.put("href", "#");
		href2.put("onClick","callDocument('FacilityProfile?facilityId=" + routeMember.partyIdTo + "')");
		routeNode.put("attr", attr2Node);
		JSONObject data2Node= new JSONObject();
		//title= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, routeFacility.partyId, false);
		title=routeFacility.partyId;
		data2Node.put("title", title);
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
	//regions = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"), null, null, null, false);
	regions = delegator.findList("Party", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"), null, null, null, false);
	
	regions.each { region ->
		populateRootNodes(region, treeNodesList, enableEdit);
	}
	Debug.log("===regions==="+regions);
}
else {
	regions = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, facilityId), null, null, null, false);
	
	//rootFacilityGrp = delegator.findByPrimaryKey("PartyRelationship", [partyIdFrom : facilityId]);
	Debug.log("===rootFacilityGrp==="+regions);
	regions.each { rootFacilityGrp ->
		
	populateChildren(rootFacilityGrp, treeNodesList, enableEdit);
	}
	rootFacility = delegator.findByPrimaryKey("Party", [partyId : facilityId]);
	context.facility = rootFacility;
}

if (parameters.ajaxLookup == "Y") {
	request.setAttribute("treeNodeJSON", treeNodesList[0].getJSONArray("children").toString());
}
else {
	context.put("treeNodesListJSON", treeNodesList.toString());
	
}
