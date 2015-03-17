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

JSONArray partyJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();
dctx = dispatcher.getDispatchContext();

partyDetailsList=[];

if(UtilValidate.isNotEmpty(parameters.roleTypeId)){//to handle IceCream Parties
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
}
parentRoleTypeId="CUSTOMER_TRADE_TYPE";
if(UtilValidate.isNotEmpty(parameters.parentRoleTypeId)){
	parentRoleTypeId=parameters.parentRoleTypeId;
}
Debug.log("==parameters.roleTypeId===INNNN=="+parameters.roleTypeId+"===parentRoleTypeId=="+parentRoleTypeId);
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
		}
	}
}

partyDetailsList.each{eachParty ->
	JSONObject newPartyObj = new JSONObject();
	partyName=PartyHelper.getPartyName(delegator, eachParty.partyId, false);
	newPartyObj.put("value",eachParty.partyId);
	newPartyObj.put("label",partyName+" ["+eachParty.partyId+"]");
	partyNameObj.put(eachParty.partyId,partyName);
	partyJSON.add(newPartyObj);
}
context.partyNameObj = partyNameObj;
context.partyJSON = partyJSON;
//supplier json for supplier lookup.
JSONArray supplierJSON = new JSONArray();

Condition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId", "SUPPLIER")],EntityOperator.AND);
supplierList=delegator.findList("PartyRole",Condition,null,null,null,false);
if(supplierList){
	supplierList.each{ supplier ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",supplier.partyId);
		partyName=PartyHelper.getPartyName(delegator, supplier.partyId, false);
		newObj.put("label",partyName+"["+supplier.partyId+"]");
		supplierJSON.add(newObj);
	}
}
context.supplierJSON=supplierJSON;