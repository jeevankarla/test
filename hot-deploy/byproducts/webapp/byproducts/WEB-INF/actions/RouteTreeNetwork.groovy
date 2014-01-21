
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

def populateChildren(facility, treeNode, enableEdit) {
	boolean recurse = true;
	JSONArray childNodesList= new JSONArray();
	exprList = [];
	exprList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "BOOTH"));
	exprList.add(EntityCondition.makeCondition("byProdRouteId", EntityOperator.EQUALS, facility.facilityId));
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	childFacilities = delegator.findList("Facility", condition, ["facilityId","facilityName","categoryTypeEnum"] as Set, null, null, false);
	childFacilities.each { childFacility ->
		JSONObject groupNode = new JSONObject();
		JSONObject attrNode= new JSONObject();
		JSONObject dataNode= new JSONObject();
		groupNode.put("state","open");
		attrNode.put("id", childFacility.facilityId);
		attrNode.put("nodetype", "childType");
		dataNode.put("nodetype", "childType");
		title = childFacility.facilityName+" ["+childFacility.facilityId+"]";
		if (childFacility.categoryTypeEnum == 'DEWS_PARLOURS') {
			attrNode.put("nodetype", "dewsParlorType");
			dataNode.put("nodetype", "dewsParlorType");
			groupNode.put("state","leaf");
			recurese = false;
		}
		else if (childFacility.categoryTypeEnum == 'FROS') {
			attrNode.put("nodetype", "frosType");
			dataNode.put("nodetype", "frosType");
			groupNode.put("state","leaf");
			recurese = false;
		}
		else if (childFacility.categoryTypeEnum == 'MCCS') {
			attrNode.put("nodetype", "mccsType");
			dataNode.put("nodetype", "mccsType");
			groupNode.put("state","leaf");
			recurese = false;
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
			href.put("onClick","callDocument('EditFacility?facilityId=" + childFacility.facilityId + "')");
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

def populateRootNodes(rootFacility, treeRouteNodesList, enableEdit) {
	JSONObject rootNode= new JSONObject();
	rootNode.put("state","closed"); 
	JSONObject attrNode= new JSONObject();
	attrNode.put("nodetype", "rootType");
	rootNode.put("attr", attrNode);
	JSONObject dataNode= new JSONObject();
	dataNode.put("title", rootFacility.facilityName);
	if (rootFacility.facilityTypeId == 'ROUTE') {
		attrNode.put("nodetype", "routeType");
		dataNode.put("nodetype", "routeType");
	}
	if (enableEdit) {		
		JSONObject href = new JSONObject();
		href.put("href", "#");
		href.put("onClick","callDocument('EditFacility?facilityId=" + rootFacility.facilityId + "')");
		dataNode.put("attr", href);	
	}	
	rootNode.put("attr", attrNode);
	rootNode.put("data", dataNode);
	populateChildren(rootFacility, rootNode, enableEdit);
	treeRouteNodesList.add(rootNode);
}
boolean enableEdit = true;
JSONArray treeRouteNodesList = new JSONArray();
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
exprList = [];
if (!facilityId) {
	exprList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "BYPRODUCTS"));
	exprList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ROUTE"));
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	rootFacilities = delegator.findList("Facility", condition, null, ["sequenceNum"], null, false);
	if(rootFacilities){
		rootFacilities.each { rootFacility ->
			populateRootNodes(rootFacility, treeRouteNodesList, enableEdit);
		}
	}
	
}

if (parameters.ajaxLookup == "Y") {
request.setAttribute("treeRouteNodesJSON", treeRouteNodesList[0].getJSONArray("children").toString());
}
else {
	context.put("treeRouteNodesListJSON", treeRouteNodesList.toString());
}