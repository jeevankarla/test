
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import javolution.util.FastList;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.List;

import in.vasista.vbiz.purchase.MaterialHelperServices;

//
changeFlag=parameters.changeFlag;
String facilityId = context.getAt("facilityId");
List facilityIdsValues = FastList.newInstance();
if(UtilValidate.isEmpty(facilityId) && UtilValidate.isNotEmpty(context.getAt("facilityList"))){
	List facilityIdsList = context.get("facilityList");
	facilityIdsValues = EntityUtil.getFieldListFromEntityList(facilityIdsList, "facilityId", false);
	//	facilityId ="ICP_FLOOR";
}else{
	facilityIdsValues.add(facilityId);
}
List<String> workEffortTypeIdsList = FastList.newInstance();
workEffortTypeIdsList.add("ROUTING");
workEffortTypeIdsList.add("ROU_TASK");
workEffortEcl = EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortTypeId",EntityOperator.IN,workEffortTypeIdsList),
	                                           EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIdsValues)],EntityOperator.AND);
List workEffort = delegator.findList("WorkEffort",workEffortEcl,UtilMisc.toSet("workEffortId"),null,null,false);
List workEffortIds = EntityUtil.getFieldListFromEntityList(workEffort,"workEffortId",true);

workEffortAsscEcl = EntityCondition.makeCondition([EntityCondition.makeCondition("workEffortIdFrom",EntityOperator.IN,workEffortIds),
	EntityCondition.makeCondition("workEffortAssocTypeId",EntityOperator.EQUALS,"ROUTING_COMPONENT")],EntityOperator.AND);
List workEffortAssoc = delegator.findList("WorkEffortAssoc",workEffortAsscEcl,UtilMisc.toSet("workEffortIdTo"),null,null,false);
workEffortIds.clear();
if(UtilValidate.isNotEmpty(workEffortAssoc)){
	workEffortIds = EntityUtil.getFieldListFromEntityList(workEffortAssoc, "workEffortIdTo", true);
}
List<String> workEffortGoodStdTypesList = FastList.newInstance();
workEffortGoodStdTypesList.add("PRUN_PROD_DELIV");
workEffortGoodStdTypesList.add("PRUNT_PROD_NEEDED");

List workEffortCondList = FastList.newInstance();
workEffortCondList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId",EntityOperator.IN,workEffortGoodStdTypesList));
workEffortCondList.add(EntityCondition.makeCondition("workEffortId",EntityOperator.IN,workEffortIds));

EntityCondition workEffortCond = EntityCondition.makeCondition(workEffortCondList);

List<GenericValue> workEffortGoods = delegator.findList("WorkEffortGoodStandard",workEffortCond,null,null,null,false);

List workEffortDeliverables = EntityUtil.filterByCondition(workEffortGoods, EntityCondition.makeCondition("workEffortGoodStdTypeId",EntityOperator.EQUALS,"PRUN_PROD_DELIV"));

List<String> deliverableProducts = EntityUtil.getFieldListFromEntityList(workEffortDeliverables, "productId", false);
List neededProdConditionList = FastList.newInstance();
neededProdConditionList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId",EntityOperator.EQUALS,"PRUNT_PROD_NEEDED"));
//neededProdConditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_IN,deliverableProducts));
EntityCondition neededProdCondition = EntityCondition.makeCondition(neededProdConditionList);
List workEffortNeededProductsList = EntityUtil.filterByCondition(workEffortGoods, neededProdCondition);
List<String> needProductsList = EntityUtil.getFieldListFromEntityList(workEffortNeededProductsList, "productId", false);
List productIds = FastList.newInstance();
if(changeFlag == "InnerScreen"){
	partyId = parameters.partyId;
	fromPartyId=parameters.partyIdFrom;
	fromParty = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", fromPartyId), false);
	party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
	if(partyId.contains("SUB")){
		roleTypeId = "DIVISION";
	}else{
		roleTypeId = parameters.roleTypeId;
	}
	if(fromPartyId.contains("SUB")){
		fromPartRoleTypeId = "DIVISION";
	}else{
		fromPartRoleTypeId = parameters.roleTypeId;
	}
	fromPartyRole=null;
	partyRole = null;
	if(party){
		partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
	}
	if(!party || !partyRole){
		context.errorMessage = partyId+" incorrect for the transaction !!";
		displayGrid = false;
		return result;
	}
	if(fromParty){
		fromPartyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", fromPartyId, "roleTypeId", fromPartRoleTypeId), false);
	}
	if(!fromParty || !fromPartyRole){
		context.errorMessage = fromPartyId+" incorrect for the transaction !!";
		displayGrid = false;
		return result;
	}
	context.party = party;
	context.fromParty=fromParty;
	partyIdFrom = party.partyId;
	ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyIdFrom),
		                               EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT")],EntityOperator.AND);
	List facilityList = delegator.findList("Facility",ecl,UtilMisc.toSet("facilityId"),null,null,false);
	List facilityIds = EntityUtil.getFieldListFromEntityList(facilityList, "facilityId", true);
	List productFacility = delegator.findList("ProductFacility",EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIds),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.IN,needProductsList)),UtilMisc.toSet("productId"),null,null,false);
	 productIds = EntityUtil.getFieldListFromEntityList(productFacility, "productId", true);
}
needProductsList.clear();
needProductsList.addAll(productIds);
Map result = (Map)MaterialHelperServices.getProductUOM(delegator, needProductsList);
uomLabelMap = result.get("uomLabel");
productUomMap = result.get("productUom");

List productsList = delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.IN,needProductsList),null,null,null,false);
JSONObject productUOMJSON = new JSONObject();
JSONObject uomLabelJSON=new JSONObject();

JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
context.productList = productsList;
productsList.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label","[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.description);
	productLabelIdJSON.put("[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName, eachItem.productId);
	
	if(productUomMap){
		uomId = productUomMap.get(eachItem.productId);
		if(uomId){
			productUOMJSON.put(eachItem.productId, uomId);
			uomLabelJSON.put(uomId, uomLabelMap.get(uomId));
		}
	}
	
	
}
context.productUOMJSON = productUOMJSON;
context.uomLabelJSON = uomLabelJSON;


JSONObject productCostJSON = new JSONObject();
JSONObject prodIndentQtyCat = new JSONObject();
JSONObject qtyInPieces = new JSONObject();

context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productCostJSON = productCostJSON;
context.productLabelIdJSON = productLabelIdJSON;
	context.partyCode = facilityId;

	
	
	