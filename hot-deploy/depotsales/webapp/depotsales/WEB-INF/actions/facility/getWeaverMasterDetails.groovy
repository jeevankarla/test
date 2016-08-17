import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.party.party.PartyHelper;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;




branchId = parameters.branchId;
passbookNumber = parameters.passbookNumber;
partyId = parameters.partyId;
partyClassification = parameters.partyClassification;
isDepot = parameters.isDepot;
state = parameters.satate;


uniqueOrderId = parameters.uniqueOrderId;


uniqueOrderIdsList = Eval.me(uniqueOrderId)


Debug.log("branchId================="+branchId);
Debug.log("passbookNumber================="+passbookNumber);
Debug.log("partyId================="+partyId);
Debug.log("partyClassification================="+partyClassification);

double totalIndents = 0;
JSONArray weaverDetailsList = new JSONArray();



branchList = [];

if(branchId){
condListb = [];
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
}

if(UtilValidate.isEmpty(branchList) && UtilValidate.isNotEmpty(branchId))
branchList.add(branchId);

partyList = []as HashSet;


condListba = [];
condListba.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
condListba.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
condListba.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
condListb = EntityCondition.makeCondition(condListba, EntityOperator.AND);

PartyRelationship = delegator.find("PartyRelationship", condListb, null, UtilMisc.toSet("partyIdTo"), null, null);


partyIdsList = EntityUtil.getFieldListFromEntityListIterator(PartyRelationship, "partyIdTo", true);

	PartyClassificationPartyIds = [];
	if(partyClassification){
		condListb1 = [];
		condListb1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsList));
		condListb1.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, partyClassification));
		condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);
		PartyClassificationList = delegator.find("PartyClassification", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		PartyClassificationPartyIds = EntityUtil.getFieldListFromEntityListIterator(PartyClassificationList, "partyId", true);
	
	}
	
	isDepotPartyIds = [];
	if(isDepot){
		condListb2 = [];
		if(isDepot == "Y"){
			condListb2.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
			condListb2.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,partyIdsList));
			fcond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
			FacilityList = delegator.find("Facility", fcond, null, UtilMisc.toSet("ownerPartyId"), null, null);
			
			isDepotPartyIds = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);
		}
	
	}
	
	PartyContactDetailByPurposeIds = [];
	if(state){
		condListb4 = [];
		condListb4.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsList));
		condListb4.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
		condListb = EntityCondition.makeCondition(condListb4, EntityOperator.AND);
		PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		PartyContactDetailByPurposeIds = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	
		//Debug.log("PartyContactDetailByPurposeIds================="+PartyContactDetailByPurposeIds);
		
	}
	
	Debug.log("satastatete================="+state);
	
	
	
	List condList = [];
	if(!partyId){
		if(PartyClassificationPartyIds && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, PartyClassificationPartyIds));
		}
		
		if(UtilValidate.isNotEmpty(branchList)&& UtilValidate.isEmpty(PartyClassificationPartyIds) && UtilValidate.isEmpty(isDepotPartyIds) && UtilValidate.isEmpty(PartyContactDetailByPurposeIds)){
		condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.IN, branchList));
		}
		
		if(UtilValidate.isNotEmpty(isDepotPartyIds) && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, isDepotPartyIds));
		}
		if(UtilValidate.isNotEmpty(PartyContactDetailByPurposeIds) && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, PartyContactDetailByPurposeIds));
		}
	}else{
	
	if(UtilValidate.isNotEmpty(partyId)){
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, partyId));
	}
	
	}
	
	
	
	
	
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.EQUALS,"PSB_NUMER"));
	
	if(UtilValidate.isNotEmpty(passbookNumber)){
		condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.EQUALS, passbookNumber));
	}
	
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	fieldsToSelect = ["partyId","idValue","partyIdFrom"] as Set;
	
	List<String> payOrderBy = UtilMisc.toList("idValue");
	
	
	resultList = delegator.find("PartyRelationAndIdentificationAndPerson", cond, null, fieldsToSelect, payOrderBy, null);
	
	partyList = resultList.getPartialList(Integer.valueOf(parameters.low),Integer.valueOf(parameters.high)-Integer.valueOf(parameters.low));
	
	
	
	if(!uniqueOrderIdsList){
		fieldsToSelect = ["partyId"] as Set;
		forIndentsCount = delegator.find("PartyRelationAndIdentificationAndPerson", cond, null, fieldsToSelect, null, null);
		totalIndents = forIndentsCount.size();
	}
	


partyIdRepeat = []as HashSet;
partyList.each{ partyList ->
	
	JSONObject tempData = new JSONObject();
	
	partyId = "";
	
	
	
	partyClassification = parameters.partyClassification;
	
	partyId = partyList.partyId;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	FacilityList = delegator.findList("Facility", fcond, null, null, null, false);
	
	
	isDepots = "";
	if(FacilityList)
	isDepots ="Y"
	else
	isDepots ="N"

	
	if((UtilValidate.isNotEmpty(isDepot) && isDepots==isDepot) ||  UtilValidate.isEmpty(isDepot)){
	if(!partyIdRepeat.contains(partyId)){
	
		partyIdRepeat.add(partyId);
		
	tempData.put("partyId", partyId);
	String partyName = PartyHelper.getPartyName(delegator,partyId,false);
	
	
	if(partyName)
	tempData.put("partyName", partyName);
	else
	tempData.put("partyName", "");
	
	tempData.put("totalIndents", totalIndents);
	
	branchName = PartyHelper.getPartyName(delegator, partyList.partyIdFrom, false);
	
	tempData.put("branchName", branchName);
	
	tempData.put("passNo", partyList.idValue);
	
	partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), UtilMisc.toSet("partyClassificationGroupId"), null, null,false);
	
	partyClassification = "";
	if(partyClassificationList){
		partyClassificationGroupId = partyClassificationList[0].get("partyClassificationGroupId");
		
		PartyClassificationGroup = delegator.findOne("PartyClassificationGroup",[partyClassificationGroupId : partyClassificationGroupId] , false);
		
		if(PartyClassificationGroup)
		partyClassification = PartyClassificationGroup.get("description");
		
	 }
	
	tempData.put("partyClassification", partyClassification);
	
	
	tempData.put("isDepot", isDepots);
	
	
	conditionListParty=[];
	conditionListParty.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
	Pcond = EntityCondition.makeCondition(conditionListParty, EntityOperator.AND);
	List partyLoomDetails = delegator.findList("PartyLoom",Pcond,UtilMisc.toSet("partyId","loomTypeId","quantity"),null,null,false);
	
	
	int totalLooms = 0;
	String loomDetail = "";
	if(partyLoomDetails){
		partyLoomDetails.each{ eachPartyLoom ->
		
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS,eachPartyLoom.loomTypeId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		LoomTypeDetails = EntityUtil.getFirst(delegator.findList("LoomType",condition,null,null,null,false));
		
		if(LoomTypeDetails)
		loomDetail = loomDetail+(LoomTypeDetails.description+" : "+Math.round(eachPartyLoom.quantity)+"   ");
		
		}
	}
	
	tempData.put("loomDetail", loomDetail);
	
	//============================state====================
	
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		if(state)
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
		condListb = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		PartyContactDetailByPurpose = EntityUtil.getFirst(delegator.findList("PartyContactDetailByPurpose",condListb,UtilMisc.toSet("partyId","stateProvinceGeoId"),null,null,false));
		
		
		stateProvinceGeoId = PartyContactDetailByPurpose.get("stateProvinceGeoId");
		
		geo=delegator.findOne("Geo",[geoId : stateProvinceGeoId], false);
		
		if(geo.geoName)
		tempData.put("state",geo.geoName);
		else
		tempData.put("state","");
	
	
	
	
	weaverDetailsList.add(tempData);
	}
	}
	
	
}
	


request.setAttribute("weaverDetailsList", weaverDetailsList);
return "success";

