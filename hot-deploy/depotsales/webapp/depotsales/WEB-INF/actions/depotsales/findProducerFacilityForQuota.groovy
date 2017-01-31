
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.entity.DelegatorFactory;



HttpServletRequest httpRequest = (HttpServletRequest) request;
HttpServletResponse httpResponse = (HttpServletResponse) response;
dctx = dispatcher.getDispatchContext();
delegator = DelegatorFactory.getDelegator("default#NHDC");


//resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


/*Map formatMap = [:];
List formatList = [];

	for (eachList in resultCtx.get("productStoreList")) {
		
		formatMap = [:];
		formatMap.put("productStoreName",eachList.get("storeName"));
		formatMap.put("payToPartyId",eachList.get("payToPartyId"));
		formatList.addAll(formatMap);
		
	}
context.formatList = formatList;*/


Map formatMap = [:];
List formatList = [];

List formatROList = [];

JSONObject branchProductSroreMap = new JSONObject();
	
	List<GenericValue> partyClassificationList = null;
		partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("BRANCH_OFFICE","REGIONAL_OFFICE")), UtilMisc.toSet("partyId","partyClassificationGroupId"), null, null,false);
	if(partyClassificationList){
		for (eachList in partyClassificationList) {
			//Debug.log("eachList========================"+eachList.get("partyId"));
			
			if(eachList.partyClassificationGroupId == "BRANCH_OFFICE"){
			formatMap = [:];
			partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
			   formatMap.put("productStoreName",partyName);
			formatMap.put("payToPartyId",eachList.get("partyId"));
			formatList.addAll(formatMap);
			
			}
			
			if(eachList.partyClassificationGroupId == "REGIONAL_OFFICE"){
			formatMapRO = [:];
			
			partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
			formatMapRO.put("productStoreName",partyName);
		    formatMapRO.put("payToPartyId",eachList.get("partyId"));
		    formatROList.addAll(formatMapRO);
			
			}
			
			  
			
			cndList=[];
			cndList.add(EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS,eachList.get("partyId")));
			EntityCondition cnd1 = EntityCondition.makeCondition(cndList, EntityOperator.AND);
			ProductStoreList =delegator.findList("ProductStore", cnd1,UtilMisc.toSet("productStoreId"), null, null, false);
			
			if(ProductStoreList){
			productStoreId = ProductStoreList[0].productStoreId;
			branchProductSroreMap.put(eachList.get("partyId"), productStoreId);
			}
		}
	}

	context.formatList = formatList;
	
	context.formatROList = formatROList;
	
	Debug.log("formatROList============="+formatROList);
	
	
	context.branchProductSroreMap = branchProductSroreMap;


	partyClassificationList = delegator.findList("PartyClassificationGroup", EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "CUST_CLASSIFICATION"), UtilMisc.toSet("partyClassificationGroupId","description"), null, null,false);
	
	context.partyClassificationList = partyClassificationList;
	
	
	
	

branchId = "";
if(parameters.branchId2)
branchId = parameters.branchId2;

branchName = branchProductSroreMap.get(branchId);

context.branchName = branchName;


passbookNumber = "";
if(parameters.passbookNumber)
passbookNumber = parameters.passbookNumber;

findData = "";
if(parameters.findData)
findData = parameters.findData;


partyId = "";
if(parameters.partyId)
partyId = parameters.partyId;


partyClassification = "";
if(parameters.partyClassificationId2)
partyClassification = parameters.partyClassificationId2;



isDepot = "";
if(parameters.isDepot)
isDepot = parameters.isDepot;

satate = "";
if(parameters.satate)
satate = parameters.satate;

stateWise = "";
if(parameters.stateWise)
stateWise = parameters.stateWise;


district = "";
if(parameters.district)
district = parameters.district;


passGreater = "";
if(parameters.passGreater)
passGreater = parameters.passGreater;

effectiveDate = "";

if(parameters.effectiveDate)
effectiveDate = parameters.effectiveDate;

roWise = "";
if(parameters.roWise)
roWise = parameters.roWise;

lom = "";
if(parameters.lom)
lom = parameters.lom;


/*
partyClassification = "";
if(parameters.partyClassificationId)
partyClassification = parameters.partyClassificationId;



isDepot = "";
if(parameters.isDepot)
isDepot = parameters.isDepot;

*/
context.branchId = branchId;
context.passbookNumber = passbookNumber;
context.partyId = partyId;
context.partyClassification = partyClassification;
context.isDepot = isDepot;
context.satate = satate;
context.district = district;
context.passGreater = passGreater;
context.effectiveDate = effectiveDate;
context.stateWise = stateWise;
context.roWise = roWise;
context.lom = lom;

context.findData = findData;




partyClassifiName = EntityUtil.filterByCondition(partyClassificationList, EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, partyClassification));
partyClasificationName = "";
if(partyClassifiName)
partyClasificationName = partyClassifiName[0].description;



context.partyClasificationName = partyClasificationName;

conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
statesList = delegator.findList("Geo",conditionDepo,null,null,null,false);


JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoId);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;



stateBranchsList=[];
stateRosList =[];
conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
statesList = delegator.findList("Geo",conditionDepo,null,null,null,false);
statesIdsList=EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);



JSONObject stateJSON = new JSONObject();

for(stateid in statesIdsList){
	result = dispatcher.runSync("getRegionalAndBranchOfficesByState",UtilMisc.toMap("state",stateid,"userLogin",userLogin));
	stateBranchsList=result.get("stateBranchsList");
	stateRosList=result.get("stateRosList");
	
	JSONArray stateBranchAndRoJSON = new JSONArray();
	stateBranchsList.each{ eachState ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachState.partyId);
			newObj.put("label",eachState.groupName);
			stateBranchAndRoJSON.add(newObj);
	}
	/*stateRosList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.partyId);
		newObj.put("label",eachState.groupName);
		stateBranchAndRoJSON.add(newObj);
	}*/
	stateJSON.put(stateid, stateBranchAndRoJSON)
}
context.stateJSON = stateJSON;
	
stateNameList = EntityUtil.filterByCondition(statesList, EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, stateWise));
stateName = "";
if(stateNameList)	
stateName = stateNameList[0].geoName;
context.stateName = stateName;

JSONObject branchByState1 = new JSONObject();
for (eachRoList in formatROList) {
	
	condListb = [];
	
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, eachRoList.payToPartyId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	
	JSONArray branchByState = new JSONArray();
	branchList.each{ eachBranch ->
		partyName = PartyHelper.getPartyName(delegator, eachBranch, false);
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachBranch);
			newObj.put("label",partyName);
			branchByState.add(newObj);
	}
	branchByState1.put(eachRoList.payToPartyId,branchByState);
	
	
}

context.branchByState1 = branchByState1;


