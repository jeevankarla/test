import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;

JSONArray branchJSON = new JSONArray();
JSONObject branchPartyObj = new JSONObject();
JSONArray partyJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();
dctx = dispatcher.getDispatchContext();

partyDetailsList=[];
partyIdsList=[];

/*if(UtilValidate.isNotEmpty(parameters.roleTypeId)){//to handle IceCream Parties
	roleTypeId =parameters.roleTypeId;
	inputMap = [:];
	inputMap.put("userLogin", userLogin);
	inputMap.put("roleTypeId", roleTypeId);
	if(UtilValidate.isNotEmpty(parameters.partyStatusId)){
			inputMap.put("statusId", parameters.partyStatusId);	
	}
	Map partyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx, inputMap);
	if(UtilValidate.isNotEmpty(partyDetailsMap)){
		partyDetailsList = partyDetailsMap.get("partyDetails");
	}	
}*/
parentRoleTypeId="CUSTOMER_TRADE_TYPE";
if(UtilValidate.isNotEmpty(parameters.parentRoleTypeId)){
	parentRoleTypeId=parameters.parentRoleTypeId;
}

if(UtilValidate.isNotEmpty(parentRoleTypeId) && UtilValidate.isEmpty(parameters.roleTypeId) ){//to handle parentRoleTypeIds only when roleTypeId is empty
	roleTypeList = delegator.findByAnd("RoleType",["parentTypeId" :parentRoleTypeId]);
	roleTypeList.each{roleType->
		roleTypeId =roleType.roleTypeId;
		inputMap = [:];
		inputMap.put("userLogin", userLogin);
		inputMap.put("roleTypeId", roleTypeId);
		if(UtilValidate.isNotEmpty(parameters.partyStatusId)){
				inputMap.put("statusId", parameters.partyStatusId);
		}
		Map tempPartyDetailsMap = ByProductNetworkServices.getPartyByRoleType(dctx, inputMap);
		if(UtilValidate.isNotEmpty(tempPartyDetailsMap)){
			tempPartyDetailsList = tempPartyDetailsMap.get("partyDetails");
			partyDetailsList.addAll(tempPartyDetailsList);
			tempPartyList = tempPartyDetailsMap.get("partyIds");
			partyIdsList.addAll(tempPartyList);
		}
	}
}
//branch Party Json
productStoreIds=[];
productStoreDetails = delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN,UtilMisc.toList("1003","1012","9000","STORE") ), null,null,null, false);
if(parameters.productStoreId){
	productStoreIds.add(parameters.productStoreId);
}else{
productStoreIds = EntityUtil.getFieldListFromEntityList(productStoreDetails, "productStoreId", true);
}

productStoreIds.each{ productStoreId ->
	JSONArray brcpartyJSON = new JSONArray();
	customersList=[];
	JSONObject newbranchObj = new JSONObject();
	productStoreValue = EntityUtil.getFirst(EntityUtil.filterByCondition(productStoreDetails, EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS, productStoreId)));
	if(productStoreValue){
		storeName="";
		if(productStoreValue.get("storeName")){
			storeName=productStoreValue.get("storeName");
		}
		newbranchObj.put("value",productStoreId);
		newbranchObj.put("label",storeName);
		branchJSON.add(newbranchObj);
	}
	/*customersList = in.vasista.vbiz.depotsales.DepotSalesHelperServices.getBranchCustomers(dctx , UtilMisc.toMap("productStoreId",productStoreId,"userLogin", userLogin));
	customersList.each{eachParty ->
		JSONObject newPartyObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, eachParty, false);
		newPartyObj.put("value",eachParty);
		newPartyObj.put("label",partyName+" ["+eachParty+"]");
		partyNameObj.put(eachParty,partyName);
		brcpartyJSON.add(newPartyObj);
		partyJSON.add(newPartyObj);
		}
	branchPartyObj.put(productStoreId, brcpartyJSON);*/
	
	}
//Debug.log("branchPartyObj====================="+branchPartyObj);
/*
conditionListForPsbNum = [];
conditionListForPsbNum.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
conditionListForPsbNum.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyDetailsList.partyId));
conditionDeductor=EntityCondition.makeCondition(conditionListForPsbNum,EntityOperator.AND);
partyPsbNumList = delegator.findList("PartyIdentification", conditionDeductor, null, null, null, false);
partyDetailsList.each{eachParty ->
	psbnum="";
	if(partyDetailsList){
	partyPsbNumDetails = EntityUtil.getFirst(EntityUtil.filterByCondition(partyPsbNumList, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, eachParty.partyId)));
		if(partyPsbNumDetails && partyPsbNumDetails.get("idValue")){
		psbnum=partyPsbNumDetails.get("idValue");
		}
	}
JSONObject newPartyObj = new JSONObject();
partyName=PartyHelper.getPartyName(delegator, eachParty.partyId, false);
newPartyObj.put("value",eachParty.partyId);
newPartyObj.put("label",partyName+" ["+psbnum+"]");
partyNameObj.put(eachParty.partyId,partyName);
partyJSON.add(newPartyObj);
}
context.partyNameObj = partyNameObj;
context.partyJSON = partyJSON;*/
context.branchJSON=branchJSON;
context.branchPartyObj=branchPartyObj;
context.partyIdsListkkkk=partyIdsList;
//supplier json for supplier lookup.
JSONArray supplierJSON = new JSONArray();

Condition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId", "SUPPLIER")],EntityOperator.AND);
supplierList=delegator.findList("PartyRole",Condition,null,null,null,false);
if(supplierList){
	supplierList.each{ supplier ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",supplier.partyId);
		partyName=PartyHelper.getPartyName(delegator, supplier.partyId, false);
		partyNameObj.put(supplier.partyId,partyName);
		newObj.put("label",partyName+"["+supplier.partyId+"]");
		supplierJSON.add(newObj);
	}
}
context.supplierJSON=supplierJSON;
context.partyNameObj = partyNameObj;

//societyParty  json.
JSONArray societyJSON = new JSONArray();
cond = [];
cond.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.LIKE, "%SOCIETY%"));
conditionSociety=EntityCondition.makeCondition(cond,EntityOperator.AND);
societyList=delegator.findList("PartyClassification",conditionSociety,null,null,null,false);
if(societyList){
	societyList.each{ society ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",society.partyId);
		partyName=PartyHelper.getPartyName(delegator, society.partyId, false);
		newObj.put("label",partyName+"["+society.partyId+"]");
		societyJSON.add(newObj);
	}
}
context.societyJSON=societyJSON;

/*if(parameters.productStoreId){
	request.setAttribute("partyJSON", partyJSON);
	return "success";
}*/

JSONArray depotsJSON = new JSONArray();
Condition = EntityCondition.makeCondition([EntityCondition.makeCondition("facilityTypeId", "DEPOT")],EntityOperator.AND);
depotList=delegator.findList("Facility",Condition,null,null,null,false);
if(depotList){
	depotList.each{ depot ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",depot.ownerPartyId);
		depotName=depot.facilityName;
		newObj.put("label",depotName+"["+depot.ownerPartyId+"]");
		depotsJSON.add(newObj);
	}
}
context.depotsJSON=depotsJSON;
