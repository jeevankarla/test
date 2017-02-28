import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.ofbiz.base.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.contact.ContactMechWorker;

import javolution.util.FastMap;
import javolution.util.FastList;
import java.util.List;

import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;


branchId = parameters.branchId;
partyId = parameters.partyId;
//passbookNumber = parameters.passbookNumber;
//partyId = parameters.partyId;
//partyClassification = parameters.partyClassification;
//isDepot = parameters.isDepot;
//state = parameters.satate;
//district = parameters.district;
//passGreater = parameters.passGreater;
//uniqueOrderId = parameters.uniqueOrderId;
//uniqueOrderIdsList = Eval.me(uniqueOrderId)

double totalIndents = 0;

//JSONArray weaverDetailsList = new JSONArray();
List CustomerDetails = FastList.newInstance();

//checking Is it RO or not
branchList = [];
List condList = [];
branchpartyIdsList = [];
if(UtilValidate.isEmpty(partyId)){
	if(branchId){
		condListb = [];
		condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
		condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
		condListb.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.NOT_EQUAL, "BRANCH_EMPLOYEE"));
		condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
		PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
		branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	}
	if(UtilValidate.isEmpty(branchList) && UtilValidate.isNotEmpty(branchId))
	{
		branchList.add(branchId);
	}
	
	partyList = [];
	condListba = [];
	condListba.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
	condListba.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	condListba.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condListb = EntityCondition.makeCondition(condListba, EntityOperator.AND);
	partyIdsList = delegator.findList("PartyRelationship", condListb, UtilMisc.toSet("partyIdTo"), null, null, false);
	partyClassification = parameters.partyClassification;
	if(partyClassification){
		partyClassificationIdList = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS,partyClassification), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		partyIdsList = EntityUtil.filterByCondition(partyIdsList,EntityCondition.makeCondition("partyIdTo",EntityOperator.IN,partyClassificationIdList));
	}
	branchpartyIdsList = EntityUtil.getFieldListFromEntityList(partyIdsList, "partyIdTo", true);
	
	if(branchpartyIdsList || branchId)
	{
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.IN, branchpartyIdsList));
	}
	if(branchList.size() == 1){
		condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS, branchId));
	}
}
else{
	condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, partyId));
}

//partyIdsList = delegator.find("PartyRelationship", condListb, null, UtilMisc.toSet("partyIdTo"), null, null);
//branchpartyIdsList = EntityUtil.getFieldListFromEntityListIterator(partyIdsList, "partyIdTo", true);
	

	
condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.EQUALS,"PSB_NUMER"));


cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
fieldsToSelect = ["partyId","idValue","partyIdFrom"] as Set;
List<String> payOrderBy = UtilMisc.toList("idValue");
resultList = delegator.find("PartyRelationAndIdentificationAndPerson", cond, null, fieldsToSelect, payOrderBy, null);

partyIdRepeat = []as HashSet;
resultList.each{ partyList ->
	Map tempData = FastMap.newInstance();
	partyId = "";
	//partyClassification = parameters.partyClassification;
	partyId = partyList.partyId;
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	FacilityList = delegator.findList("Facility", fcond, UtilMisc.toSet("facilityId"), null, null, false);
	isDepots = "";
	if(FacilityList){
		isDepots ="Y"
	}
	else{
		isDepots ="N"
	}
		if(!partyIdRepeat.contains(partyId)){
			partyIdRepeat.add(partyId);
			tempData.put("partyId", partyId);
			String partyName = PartyHelper.getPartyName(delegator,partyId,false);
			if(partyName)
				tempData.put("partyName", partyName);
			else
				tempData.put("partyName", "");
		
			tempData.put("totalIndents", totalIndents);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
			conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
			EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));
			fcondP = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			PartyRelationship = delegator.findList("PartyRelationship", fcondP, null, null, null, false);
			branchName = "";
			if(PartyRelationship.size() !=1){
				int i=0;
				for (eachEbranchList in PartyRelationship) {
					
					if(i!=0 && i != PartyRelationship.size())
					branchName = branchName +","+ PartyHelper.getPartyName(delegator, eachEbranchList.partyIdFrom, false);
					else
					branchName = branchName + PartyHelper.getPartyName(delegator, eachEbranchList.partyIdFrom, false);
					
					i++;
				}
			}else{
			  branchName = branchName + PartyHelper.getPartyName(delegator, partyList.partyIdFrom, false);
			}	
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
					LoomTypeDetails = EntityUtil.getFirst(delegator.findList("LoomType",condition,UtilMisc.toSet("description"),null,null,false));
					if(LoomTypeDetails)
						loomDetail = loomDetail+(LoomTypeDetails.description+" : "+Math.round(eachPartyLoom.quantity)+"   ");
				}
			}
		
			tempData.put("loomDetail", loomDetail);
		
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
			tempData.put("state",shipingAdd.get("state"));
			tempData.put("district",shipingAdd.get("district"));
			CustomerDetails.add(tempData);
		}
	
}
context.CustomerDetails = CustomerDetails;
