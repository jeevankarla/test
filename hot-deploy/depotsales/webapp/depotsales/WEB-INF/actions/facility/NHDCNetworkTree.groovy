	
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.EntityUtil;
	import net.sf.json.JSONObject;
	import net.sf.json.JSONArray;
	
	
	
	
	JSONArray mainNodesList = new JSONArray();
	
	JSONObject hoNode= new JSONObject();
	hoNode.put("state","open");
	company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
	JSONObject attrNode= new JSONObject();
	attrNode.put("nodetype", "company");
	hoNode.put("attr", attrNode);
	JSONObject dataNode= new JSONObject();
	dataNode.put("title", company.groupName);
	JSONObject hrefNode = new JSONObject();
	hrefNode.put("href", "#");
	//hrefNode.put("onClick","callDocument('/partymgr/control/viewprofile?partyId=" + company.partyId + "')");
	dataNode.put("attr", hrefNode);
	hoNode.put("data", dataNode);
	
	populateHOTree(company, hoNode);
	//populateROTree(company, strNode);
	mainNodesList.add(hoNode);
	Debug.log("===="+mainNodesList);
	if (parameters.ajaxLookup == "Y") {
		request.setAttribute("treeNodeJSON", mainNodesList[0].getJSONArray("children").toString());
	}
	else {
		context.put("treeNodesListJSON", mainNodesList.toString());
	}
	
	def populateHOTree(company, hoNode){
		
		facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", "HO"), false);
		
		JSONArray hoNodesList = new JSONArray();
		
			JSONObject attrNode= new JSONObject();
			attrNode.put("nodetype", "HO");
			
			JSONObject hrefNode = new JSONObject();
			hrefNode.put("href", "#");
			hrefNode.put("onClick","callDocument('ViewFacilityGeoPoint?facilityId=" + facility.facilityId + "')");
			
			JSONObject dataNode= new JSONObject();
			dataNode.put("title", facility.facilityName);
			dataNode.put("attr", hrefNode);
			
			JSONObject strNode = new JSONObject();
			strNode.put("state","open");
			strNode.put("attr", attrNode);
			strNode.put("data", dataNode);
			
			populateROTree(company, strNode);
			
			hoNodesList.add(strNode);
			hoNode.put("children",hoNodesList);
		
	}
	
	
	def populateROTree(company, strNode){
		
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "RO"));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		roList = delegator.findList("Facility", condition , UtilMisc.toSet("facilityId", "facilityTypeId", "ownerPartyId", "facilityName"), null, null, false );
		
		JSONArray treeNodesList = new JSONArray();
		
		for(int i=0; i<roList.size(); i++){
			
			ro = roList.get(i);
			
			branchParties = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, ro.ownerPartyId), null, null, null, false);
			
			JSONObject attrNode= new JSONObject();
			attrNode.put("nodetype", "RO");
			
			JSONObject hrefNode = new JSONObject();
			hrefNode.put("href", "#");
			hrefNode.put("onClick","callDocument('ViewFacilityGeoPoint?facilityId=" + ro.facilityId + "')");
			
			JSONObject dataNode= new JSONObject();
			dataNode.put("title", ro.facilityName);
			dataNode.put("attr", hrefNode);
			
			JSONObject rootNode = new JSONObject();
			rootNode.put("state","open");
			rootNode.put("attr", attrNode);
			rootNode.put("data", dataNode);
			
			populateBranchesTree(ro.facilityId, branchParties, rootNode);
			
			treeNodesList.add(rootNode);
			strNode.put("children",treeNodesList);
		}
		
	}
	
	def populateBranchesTree(facilityId, branchParties, treeNode) {
		JSONArray clasifNodesList= new JSONArray();
		
		branchParties.each { branchParty ->
			
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN, UtilMisc.toList("DEPOT", "WAREHOUSE", "CFC")));
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, branchParty.partyIdTo));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
			depotList = delegator.findList("Facility", condition , UtilMisc.toSet("facilityId", "facilityTypeId", "ownerPartyId", "facilityName"), null, null, false );
			
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, UtilMisc.toList("BO")));
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, branchParty.partyIdTo));
			conditionBo=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			boList = delegator.findList("Facility", conditionBo , UtilMisc.toSet("facilityId", "facilityTypeId", "ownerPartyId", "facilityName"), null, null, false );
			if(UtilValidate.isNotEmpty(boList)){
				bo = boList.get(0);
				
				JSONObject childNode = new JSONObject();
				JSONObject attrNode= new JSONObject();
				attrNode.put("nodetype", "BO");
				childNode.put("state","closed");
				attrNode.put("id", bo.facilityId);
				
				
				childNode.put("attr", attrNode);
				JSONObject dataNode= new JSONObject();
				dataNode.put("title",bo.facilityName);
				
				JSONObject href = new JSONObject();
				href.put("href", "#");
				href.put("onClick","callDocument('ViewFacilityGeoPoint?facilityId=" + bo.facilityId + "')");
				dataNode.put("attr", href);
				childNode.put("data", dataNode);
				
				populateDepots(bo.facilityId, depotList, childNode);
				
				clasifNodesList.add(childNode);
				treeNode.put("children",clasifNodesList);
			}
			
			
		}
			
	}
	
	def populateDepots(facilityId, depotList, groupNode) {
		
		JSONArray childNodesList= new JSONArray();
		
		depotList.each { eachDepot ->
			
			
			//studentDetails = EntityUtil.filterByAnd(catApplicationList, UtilMisc.toMap("partyId", eachStudent));
			JSONObject studentNode = new JSONObject();
			JSONObject attrNode= new JSONObject();
			JSONObject dataNode= new JSONObject();
			studentNode.put("state","closed");
			attrNode.put("id", eachDepot.facilityId);
			attrNode.put("nodetype", "Depots");
			dataNode.put("nodetype", "Depots");
			title = eachDepot.facilityName;
			studentNode.put("attr", attrNode);
			dataNode.put("title", title);
			JSONObject href = new JSONObject();
			href.put("href", "#");
			href.put("onClick","callDocument('ViewFacilityGeoPoint?facilityId=" + eachDepot.facilityId + "')");
			dataNode.put("attr", href);
			studentNode.put("data", dataNode);
			childNodesList.add(studentNode);
		}
		
		groupNode.put("children",childNodesList);
	}
	
	