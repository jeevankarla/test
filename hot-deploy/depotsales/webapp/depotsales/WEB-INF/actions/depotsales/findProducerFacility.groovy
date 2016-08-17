
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



resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


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
	
	List<GenericValue> partyClassificationList = null;
		partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("REGIONAL_OFFICE","BRANCH_OFFICE")), UtilMisc.toSet("partyId"), null, null,false);
	if(partyClassificationList){
		for (eachList in partyClassificationList) {
			//Debug.log("eachList========================"+eachList.get("partyId"));
			formatMap = [:];
			partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
			formatMap.put("productStoreName",partyName);
			formatMap.put("payToPartyId",eachList.get("partyId"));
			formatList.addAll(formatMap);
		}
	}

	context.formatList = formatList;


	partyClassificationList = delegator.findList("PartyClassificationGroup", EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "CUST_CLASSIFICATION"), UtilMisc.toSet("partyClassificationGroupId","description"), null, null,false);
	
	context.partyClassificationList = partyClassificationList;


branchId = "";
if(parameters.branchId)
branchId = parameters.branchId;

passbookNumber = "";
if(parameters.passbookNumber)
passbookNumber = parameters.passbookNumber;


partyId = "";
if(parameters.partyId)
partyId = parameters.partyId;


partyClassification = "";
if(parameters.partyClassificationId)
partyClassification = parameters.partyClassificationId;


isDepot = "";
if(parameters.isDepot)
isDepot = parameters.isDepot;

satate = "";
if(parameters.satate)
satate = parameters.satate;

district = "";
if(parameters.district)
district = parameters.district;

/*partyClassification = "";
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



/*partyRoleAndIde = new DynamicViewEntity();
partyRoleAndIde.addMemberEntity("PRPD", "PartyRoleDetailAndPartyDetail");
partyRoleAndIde.addMemberEntity("PID", "PartyIdentification");
partyRoleAndIde.addMemberEntity("PRS", "PartyRelationship");
partyRoleAndIde.addAliasAll("PRPD", null, null);
partyRoleAndIde.addAliasAll("PID", null, null);
partyRoleAndIde.addAliasAll("PRS", null, null);
partyRoleAndIde.addViewLink("PRPD","PID", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
partyRoleAndIde.addViewLink("PRPD","PRS", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId","partyIdTo"));

condList = [];
condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.EQUALS, "PSB_NUMER"));

if(partyId!=null && partyId!=""){
	condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,partyId.trim()));
}
if(groupName!=null && groupName!=""){
	group1=EntityCondition.makeCondition(EntityCondition.makeCondition("firstName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%") ,EntityOperator.OR,EntityCondition.makeCondition("lastName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%"));
	group2=EntityCondition.makeCondition(EntityCondition.makeCondition("middleName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%") ,EntityOperator.OR,EntityCondition.makeCondition("groupName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%"));
	condList.add(EntityCondition.makeCondition(group1 ,EntityOperator.OR,group2));
}
if(branchId!=null && branchId!=""){
	condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,branchId.trim()));
}
if(passbookNumber!=null && passbookNumber!=""){
	condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.EQUALS,passbookNumber.trim()));
}

cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
prodsEli = delegator.findListIteratorByCondition(partyRoleAndIde, cond, null,UtilMisc.toSet("partyId","firstName","idValue","partyIdFrom") , null, null);
groupNameList = prodsEli.getCompleteList();

finalList =[];

for(customer in groupNameList){
	eachPartyId=String.valueOf(customer.get("partyId"));
	branchId=String.valueOf(customer.get("partyIdFrom"));
	tempMap = [:];
	tempMap.put("partyId",eachPartyId);
	String partyName = PartyHelper.getPartyName(delegator,eachPartyId,false);
	tempMap.put("groupName",partyName);
	tempMap.put("passbookId",customer["idValue"])
	
	if(UtilValidate.isNotEmpty(branchId)){
		result = EntityUtil.filterByCondition(resultCtx.get("productStoreList"), EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS, branchId));
		if(UtilValidate.isNotEmpty(result[0].get("storeName"))){
			tempMap.put("storeName",result[0].get("storeName"));
		}else{
		  tempMap.put("storeName","");
		}
		
	}else{
		  tempMap.put("storeName","");
		}
	
	finalList.addAll(tempMap);
	
}

context.listIt = finalList;

*/


/*String partyId = parameters.partyId;

String facilityPartyName = parameters.PartyName;

String roleTypeId = parameters.roleTypeId;


condList = [];
condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"EMPANELLED_CUSTOMER"));
partyRoleAndPartyDetail = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);

finalPartyDetails = [];

  for (eachList in partyRoleAndPartyDetail) {
	
		tempMap = [:];
		String partyName = PartyHelper.getPartyName(delegator,eachList.partyId,false);
		tempMap.put("partyId", eachList.partyId);
		if(UtilValidate.isNotEmpty(partyName)){
		tempMap.put("partyName", partyName);
		}
		else{
			tempMap.put("partyName", "");
		}
		
		tempMap.put("roleTypeId", eachList.roleTypeId);
		finalPartyDetails.add(tempMap);
}


  finalFilteredList = []as LinkedHashSet;
  
  for (eachOrderList in finalPartyDetails) {
	  
	 if(UtilValidate.isNotEmpty(partyId)){
	  
		  if(partyId.equals(eachOrderList.get("partyId"))){
			  finalFilteredList.add(eachOrderList);
		  }
		 
	 }
	 if(UtilValidate.isNotEmpty(facilityPartyName)){
		 
		 if(facilityPartyName.equals(eachOrderList.get("partyName"))){
			 
			 finalFilteredList.add(eachOrderList);
		 }
		 
	 }
	 if(UtilValidate.isNotEmpty(roleTypeId)){
		 
		 if(roleTypeId.equals(eachOrderList.get("roleTypeId"))){
			 
			 finalFilteredList.add(eachOrderList);
		 }
		 
	 }
	 if(UtilValidate.isEmpty(partyId) && UtilValidate.isEmpty(facilityPartyName) && UtilValidate.isEmpty(roleTypeId)){
		 
		 Debug.log("enter3erer")
		 
		 finalFilteredList.add(eachOrderList);
	 }
 }
  
  tempfinalFilteredList = [];
  tempfinalFilteredList.addAll(finalFilteredList);

context.partyRoleAndPartyDetail = tempfinalFilteredList;*/