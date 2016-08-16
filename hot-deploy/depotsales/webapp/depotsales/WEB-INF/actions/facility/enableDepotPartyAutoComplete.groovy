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
	
	userPartyId = userLogin.partyId;
	
	// To check if logged in user is Customer
	partyRoles = delegator.findList("PartyRole", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userPartyId), null,null,null, false);
	userCustomerId = null;
	if(UtilValidate.isNotEmpty(partyRoles)){
		customerParty = EntityUtil.filterByCondition(partyRoles, EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
		if(UtilValidate.isNotEmpty(customerParty)){
			context.partyId = userPartyId;
			userCustomerId = (EntityUtil.getFirst(customerParty)).get("partyId");
			userParty = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", userCustomerId), false);
			if(userParty){
				context.party = userParty;
			}else{
				personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",userCustomerId), false);
				context.party = personDetails;
			}
		}
	}
	
	// To check if logged in user is CFC personnel
	
	condList = [];
	condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CFC_INDENTOR"));
	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userPartyId));
	productStoreRole = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(condList,EntityOperator.AND), null,null,null, false);
	if(UtilValidate.isNotEmpty(productStoreRole)){
		context.cfcs = (EntityUtil.getFirst(productStoreRole)).get("productStoreId");
		parameters.cfcs = (EntityUtil.getFirst(productStoreRole)).get("productStoreId");
	}
		
	
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
	
	resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));	
	productStoreIds=[];
	productStoreDetails = resultCtx.get("productStoreList");
	//productStoreDetails = delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN,UtilMisc.toList("1003","1012","9000","STORE") ), null,null,null, false);
	if(parameters.productStoreId){
		productStoreIds.add(parameters.productStoreId);
	}else{
		productStoreIds = EntityUtil.getFieldListFromEntityList(productStoreDetails, "productStoreId", true);
	}
	
	if(productStoreIds.size() == 1){
		context.productStoreId = productStoreIds.get(0);
		parameters.productStoreId = productStoreIds.get(0);
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
			if("WARANGAL".equals(storeName)){
				newbranchObj.put("label",productStoreId+"("+storeName+")");
			}else{
			newbranchObj.put("label",storeName);
			}
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
	JSONObject supplierIdJson = new JSONObject();
	
	Condition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId", "EMPANELLED_SUPPLIER")],EntityOperator.AND);
	supplierList=delegator.findList("PartyRole",Condition,null,null,null,false);
	
	partyIdsFromSuppList = EntityUtil.getFieldListFromEntityList(supplierList, "partyId", true);
	
	if(supplierList){
		supplierList.each{ supplier ->
			JSONObject newObj = new JSONObject();
			
			condList.clear();
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplier.partyId));
			 List<String> orderBy = UtilMisc.toList("-statusDate");
			PartyStatusList = delegator.findList("PartyStatus", EntityCondition.makeCondition(condList,EntityOperator.AND), UtilMisc.toSet("statusId"),orderBy,null, false);
			
			PartyStatus = "";
			if(PartyStatusList)
			PartyStatus = (EntityUtil.getFirst(PartyStatusList)).get("statusId");
			else
			PartyStatus = "NORECORD";
			
			if(PartyStatus == "PARTY_ENABLED" || PartyStatus=="NORECORD")
			{
			newObj.put("value",supplier.partyId);
			partyName=PartyHelper.getPartyName(delegator, supplier.partyId, false);
			partyNameObj.put(supplier.partyId,partyName);
			newObj.put("label",partyName+"["+supplier.partyId+"]");
			supplierIdJson.put(supplier.partyId, partyName);
			supplierJSON.add(newObj);
			}
			
		}
	}
	context.supplierJSON=supplierJSON;
	context.supplierIdJson=supplierIdJson;
	context.partyNameObj = partyNameObj;
	
	//societyParty  json.
	JSONArray societyJSON = new JSONArray();
	conditionDeopoList=[];
	conditionDeopoList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL,null));
	conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
	facilityDepoList = delegator.findList("Facility",conditionDepo,null,null,null,false);
	cond = [];
	cond.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.NOT_EQUAL, "INDIVIDUAL_WEAVERS"));
	if(facilityDepoList && facilityDepoList.ownerPartyId){
	cond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, facilityDepoList.ownerPartyId));
	}
	conditionSociety=EntityCondition.makeCondition(cond,EntityOperator.AND);
	societyList=delegator.findList("PartyClassification",conditionSociety,null,null,null,false);
	if(societyList){
		societyList.each{ society ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",society.partyId);
			partyName=PartyHelper.getPartyName(delegator, society.partyId, false);
			custPartyName=partyName+"["+society.partyId+"]"
			
			regno="";
			partyRegIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", society.partyId, "partyIdentificationTypeId", "REGISTRATION_NUMBER"), false);
			if(partyRegIdentification){
				regno = partyRegIdentification.get("idValue");
			}
			if(regno){
				custPartyName=custPartyName+"[ RegNo: "+regno+"]";
			}
			newObj.put("label",custPartyName);
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
	
	
	dctx = dispatcher.getDispatchContext();
	
	List<GenericValue> countries= org.ofbiz.common.CommonWorkers.getCountryList(delegator);
	JSONArray countryListJSON = new JSONArray();
	countries.each{ eachCountry ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachCountry.geoId);
			newObj.put("label",eachCountry.geoName);
			countryListJSON.add(newObj);
	}
	context.countryListJSON = countryListJSON;
	
	
	// preparing state List Json
	
	
	//dctx = dispatcher.getDispatchContext();
	
	//List<GenericValue> statesList = org.ofbiz.common.CommonWorkers.getAssociatedStateList(delegator, "IND");
	
	conditionDeopoList.clear();
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
	
	
/*	JSONArray supplierFacilityListJSON = new JSONArray();
	
	
	
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	faciList = delegator.findList("Facility", EntityCondition.makeCondition(condList,EntityOperator.AND), null,null,null, false);
	
     Debug.log("faciList================"+faciList);	
	
	 facilityTypeList = EntityUtil.getFieldListFromEntityList(faciList, "facilityTypeId", true);
	
	Debug.log("facilityTypeList================"+facilityTypeList);
	
	
	 filterredFeciList = [];
	
	   for (eachType in facilityTypeList) {
		
		   
		   condList.clear();
		   condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
		   condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, eachType));
		    List<String> orderBy = UtilMisc.toList("-facilityId");
		   faciListBasedOnTypeList = delegator.findList("Facility", EntityCondition.makeCondition(condList,EntityOperator.AND), null,orderBy,null, false);
		   
		   if(faciListBasedOnTypeList[0])
		   filterredFeciList.add(faciListBasedOnTypeList[0]);
		   
	}
	
	   
	
	if(UtilValidate.isNotEmpty(filterredFeciList)){
		
		filterredFeciList.each{ echFaci ->
				JSONObject newObj = new JSONObject();
				newObj.put("value",echFaci.facilityId);
				newObj.put("label",echFaci.facilityName);
				supplierFacilityListJSON.add(newObj);
		}
		
	}
	
	context.supplierFacilityListJSON = supplierFacilityListJSON;
	
	Debug.log("supplierFacilityListJSON==================="+supplierFacilityListJSON);
	*/
