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
district = parameters.district;
passGreater = parameters.passGreater;




uniqueOrderId = parameters.uniqueOrderId;


uniqueOrderIdsList = Eval.me(uniqueOrderId)


//Debug.log("branchId================="+branchId);
//Debug.log("passbookNumber================="+passbookNumber);
//Debug.log("partyId================="+partyId);
//Debug.log("partyClassification================="+partyClassification);

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
		if(partyIdsList)
		condListb1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsList));
		condListb1.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, partyClassification));
		condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);
		PartyClassificationList = delegator.find("PartyClassification", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		PartyClassificationPartyIds = EntityUtil.getFieldListFromEntityListIterator(PartyClassificationList, "partyId", true);
	
	}
	
	isDepotPartyIds = [];
	if(isDepot){
		condListb2 = [];
		if(parameters.isDepot == "Y"){
			
			condListb2.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
			if(partyIdsList)
			condListb2.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,partyIdsList));
			fcond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
			FacilityList = delegator.find("Facility", fcond, null, UtilMisc.toSet("ownerPartyId"), null, null);
			
			isDepotPartyIds = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);
			
			
		}
	
	}
	
	PartyContactDetailByPurposeIds = [];
	if(state){
		condListb4 = [];
		if(partyIdsList)
		condListb4.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsList));
		condListb4.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
		condListb = EntityCondition.makeCondition(condListb4, EntityOperator.AND);
		PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		PartyContactDetailByPurposeIds = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	
		////Debug.log("PartyContactDetailByPurposeIds================="+PartyContactDetailByPurposeIds);
		
	}
	
	PartyContactDetailByDistrict = [];
	
	if(district){
		condListb5 = [];
		if(partyIdsList)
		condListb5.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsList));
		condListb5.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		condListb5.add(EntityCondition.makeCondition("districtGeoId", EntityOperator.EQUALS, district));
		condListb = EntityCondition.makeCondition(condListb5, EntityOperator.AND);
		PartyContactDetailByDistrict = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
		
		PartyContactDetailByDistrict = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByDistrict, "partyId", true);
	
		//Debug.log("PartyContactDetailByDistrict================="+PartyContactDetailByDistrict);
		
	}
	
	
	
	
	List condList = [];
	if(!partyId){
		if(partyClassification && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, PartyClassificationPartyIds));
		}else{
		if(UtilValidate.isNotEmpty(branchList)&& UtilValidate.isEmpty(PartyClassificationPartyIds) && UtilValidate.isEmpty(isDepotPartyIds) && UtilValidate.isEmpty(PartyContactDetailByPurposeIds)){
		condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.IN, branchList));
		}
		
		if(UtilValidate.isNotEmpty(isDepotPartyIds) && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, isDepotPartyIds));
		}
		if(UtilValidate.isNotEmpty(PartyContactDetailByPurposeIds) && UtilValidate.isEmpty(PartyContactDetailByDistrict) && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, PartyContactDetailByPurposeIds));
		}else if(UtilValidate.isNotEmpty(PartyContactDetailByDistrict) && !partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, PartyContactDetailByDistrict));
		}
		}	
		
	}else{
	
	if(UtilValidate.isNotEmpty(partyId)){
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, partyId));
	}
	
	}
	
	
	
	
	
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.EQUALS,"PSB_NUMER"));
	
	if(UtilValidate.isNotEmpty(passbookNumber) && UtilValidate.isNotEmpty(passGreater)){
		condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.GREATER_THAN, passbookNumber));
	}else if(UtilValidate.isNotEmpty(passbookNumber)){
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
	
	/*	conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		if(state)
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
		condListb = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		PartyContactDetailByPurpose = EntityUtil.getFirst(delegator.findList("PartyContactDetailByPurpose",condListb,UtilMisc.toSet("partyId","stateProvinceGeoId","districtGeoId"),null,null,false));
		
		
		if(PartyContactDetailByPurpose){
		stateProvinceGeoId = PartyContactDetailByPurpose.get("stateProvinceGeoId");
		
		districtGeoId = PartyContactDetailByPurpose.get("districtGeoId");
		
		geo=delegator.findOne("Geo",[geoId : stateProvinceGeoId], false);
		if(geo)
		tempData.put("state",geo.geoName);
		
		geoDist=delegator.findOne("Geo",[geoId : districtGeoId], false);
		
		if(geoDist)
		tempData.put("district",geoDist.geoName);
		}
		else{
		tempData.put("state","");
		tempData.put("district","");
		}*/
	
	
	   //===============================state District================================
	
	
	contactMechesDetails = [];
	conditionListAddress = [];
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	 List<String> orderBy = UtilMisc.toList("-contactMechId");
	contactMech = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy, null, false);
	
	
	if(contactMech){
	contactMechesDetails = contactMech;
	}
	else{
		conditionListAddress.clear();
		conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		List<String> orderBy2 = UtilMisc.toList("-contactMechId");
		contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy2, null, false);
	}
	
	shipingAdd = [:];
	
	if(contactMechesDetails){
		contactMec=contactMechesDetails.getFirst();
		if(contactMec){
			//partyPostalAddress=contactMec.get("postalAddress");
			
			partyPostalAddress=contactMec;
			
			//////Debug.log("partyPostalAddress=========================="+partyPostalAddress);
		//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
			if(partyPostalAddress){
				address1="";
				address2="";
				state="";
				city="";
				postalCode="";
				districtGeoId = "";
				stateProvinceGeoId = "";
				if(partyPostalAddress.get("address1")){
				address1=partyPostalAddress.get("address1");
				//////Debug.log("address1=========================="+address1);
				}
				if(partyPostalAddress.get("address2")){
					address2=partyPostalAddress.get("address2");
					}
				if(partyPostalAddress.get("city")){
					city=partyPostalAddress.get("city");
					}
				if(partyPostalAddress.get("stateGeoName")){
					state=partyPostalAddress.get("stateGeoName");
					}
				if(partyPostalAddress.get("postalCode")){
					postalCode=partyPostalAddress.get("postalCode");
					}
				if(partyPostalAddress.get("districtGeoId")){
					districtGeoId=partyPostalAddress.get("districtGeoId");
					}
				if(partyPostalAddress.get("stateProvinceGeoId")){
					stateProvinceGeoId=partyPostalAddress.get("stateProvinceGeoId");
					}
				
				Debug.log("stateProvinceGeoId========="+stateProvinceGeoId)
				
				//shipingAdd.put("name",shippPartyName);
				shipingAdd.put("address1",address1);
				shipingAdd.put("address2",address2);
				shipingAdd.put("city",city);
				shipingAdd.put("state",state);
				shipingAdd.put("postalCode",postalCode);
				geo=delegator.findOne("Geo",[geoId : districtGeoId], false);
				if(geo)
				shipingAdd.put("district",geo.geoName);
				else
				shipingAdd.put("district","");
				
				if(!state){
					geo=delegator.findOne("Geo",[geoId : stateProvinceGeoId], false);
					if(geo)
					shipingAdd.put("state",geo.geoName);
					else
					shipingAdd.put("state","");
					
				}
				
				
			}
		}
	}
	//========================================
	tempData.put("state",shipingAdd.get("state"));
	
	tempData.put("district",shipingAdd.get("district"));
	
	weaverDetailsList.add(tempData);
	}
	}
	
	
}
	


request.setAttribute("weaverDetailsList", weaverDetailsList);
return "success";

