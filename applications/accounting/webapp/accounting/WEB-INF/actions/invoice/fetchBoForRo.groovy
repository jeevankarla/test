import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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

JSONArray orderList =new JSONArray();
//organizationPartyId = parameters.organizationPartyId;
//Debug.log("organizationPartyId===@@@@@@@========"+parameters.organizationPartyId);
roId = parameters.roId;
condList = [];
if(UtilValidate.isNotEmpty(roId)){
	condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,roId));
}
condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"BRANCH_CUSTOMER"));
List roWiseBranchLists = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);

condList.clear();
condList.add(EntityCondition.makeCondition("partyIdFrom" , EntityOperator.EQUALS,"Company"));
if(UtilValidate.isNotEmpty(roId)){
	condList.add(EntityCondition.makeCondition("partyIdTo" , EntityOperator.EQUALS,roId));
}
condList.add(EntityCondition.makeCondition("roleTypeIdFrom" , EntityOperator.EQUALS,"PARENT_ORGANIZATION"));
condList.add(EntityCondition.makeCondition("roleTypeIdTo" , EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
condList.add(EntityCondition.makeCondition("partyRelationshipTypeId" , EntityOperator.EQUALS,"GROUP_ROLLUP"));
List roListDetails = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
request.setAttribute("getROList",roListDetails);
request.setAttribute("orderList",roWiseBranchLists);

return "success";



