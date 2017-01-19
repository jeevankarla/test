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

JSONArray orderList=new JSONArray();
stateWise = "";
if(parameters.stateWise)
stateWise = parameters.stateWise;



branchList = [];
conditions = [];
if(UtilValidate.isNotEmpty(stateWise)){
	
	conditions.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateWise));
}
conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "INT%"));
stateWiseRosAndBranchList = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);

 roAndBranchIds = EntityUtil.getFieldListFromEntityList(stateWiseRosAndBranchList, "partyId", true);

 
 conditions.clear();
 conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, roAndBranchIds));
 conditions.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "BRANCH_OFFICE"));
 partyClassicationForBranch= delegator.findList("PartyClassification", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
 if(UtilValidate.isNotEmpty(partyClassicationForBranch)){
	  branchList = EntityUtil.getFieldListFromEntityList(partyClassicationForBranch, "partyId", true);
 }
 
 JSONObject partyNameObj = new JSONObject();
if(UtilValidate.isNotEmpty(branchList)){
	partyRelationship2 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,branchList)  , UtilMisc.toSet("partyIdTo"), null, null, false );
	if(UtilValidate.isNotEmpty(partyRelationship2)){
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship2, "partyIdTo", true);
		
		//Debug.log("partyIds==========="+partyIds.size());
		
	}
	
	

	if(partyIds){
		partyIds.each{ eachParty ->
			JSONObject newObj = new JSONObject();
			conditionList =[];
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , eachParty));
			/*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PARTY_ENABLED"));*/
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			partyList = delegator.findList("Party", condition, null, null, null, false);
			partyDetails = EntityUtil.getFirst(partyList);
			if(UtilValidate.isNotEmpty(partyDetails)){
				newObj.put("value",partyDetails.partyId);
				partyName=PartyHelper.getPartyName(delegator, partyDetails.partyId, false);
				newObj.put("label",partyName+"["+partyDetails.partyId+"]");
				orderList.add(newObj);
			}
		}
	}
	

}


request.setAttribute("partyNameObj", orderList);
return "success";

