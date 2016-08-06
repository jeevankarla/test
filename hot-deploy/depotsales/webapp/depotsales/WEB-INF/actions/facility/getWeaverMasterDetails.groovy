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

uniqueOrderId = parameters.uniqueOrderId;


uniqueOrderIdsList = Eval.me(uniqueOrderId)


Debug.log("branchId================="+branchId);
Debug.log("passbookNumber================="+passbookNumber);
Debug.log("partyId================="+partyId);

JSONArray weaverDetailsList = new JSONArray();


double totalIndents = 0

List condList = [];


if(UtilValidate.isNotEmpty(partyId)){
	condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, partyId));
}

if(UtilValidate.isNotEmpty(branchId)){
	condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS, branchId));
}

condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.EQUALS,"PSB_NUMER"));

if(UtilValidate.isNotEmpty(passbookNumber)){
	condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.EQUALS, passbookNumber));
}
/*if(!partyId || passbookNumber)
condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.NOT_EQUAL,"_NA_"));
*/

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

Debug.log("totalIndents================="+totalIndents);


if(uniqueOrderIdsList){
	partyList = EntityUtil.filterByCondition(partyList, EntityCondition.makeCondition("partyId" ,EntityOperator.NOT_IN, uniqueOrderIdsList));
}


partyIdRepeat = []as HashSet;
partyList.each{ partyList ->
	
	JSONObject tempData = new JSONObject();
	
	
	
	partyId = partyList.partyId;
	
	if(!partyIdRepeat.contains(partyId)){
	
		partyIdRepeat.add(partyId);
		
	tempData.put("partyId", partyList.partyId);
	String partyName = PartyHelper.getPartyName(delegator,partyList.partyId,false);
	
	if(partyName)
	tempData.put("partyName", partyName);
	else
	tempData.put("partyName", "");
	
	tempData.put("totalIndents", totalIndents);
	
	
/*	conditionList = [];
	
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condListb = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdFrom"), null, null, false);
	
	Debug.log("PartyRelationship============"+PartyRelationship);
	
	partyIdFrom = "";
	if(PartyRelationship){
	PartyRelationshipList = EntityUtil.getFirst(PartyRelationship);
	partyIdFrom = PartyRelationshipList.partyIdFrom;
	}*/
	
	branchName = PartyHelper.getPartyName(delegator, partyList.partyIdFrom, false);
	
	tempData.put("branchName", branchName);
	
	
	/*passNo = "";
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
	if(PartyIdentificationList){
	passNo = PartyIdentificationList[0].get("idValue");
	}*/
	
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
	
	weaverDetailsList.add(tempData);
	}
	
}
	


request.setAttribute("weaverDetailsList", weaverDetailsList);
return "success";

