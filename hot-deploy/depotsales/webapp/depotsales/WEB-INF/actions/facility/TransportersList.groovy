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



orderId = parameters.orderId;

JSONArray transporterJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();

conditionList =[];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["BILL_TO_CUSTOMER"]));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);


exprCondList=[];
exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));

carrierName = "";

if(UtilValidate.isNotEmpty(OrderAss)){
	salesOrder = OrderAss.get("toOrderId");
	indentTransporter = delegator.findOne("OrderAttribute", [orderId : salesOrder, attrName : "TRANSPORTER_PREF"], false);
	 if(UtilValidate.isNotEmpty(indentTransporter)){
		 carrierName = indentTransporter.attrValue;
	 }
}

 
if(!carrierName){

branchId = "";

for (eachRole in OrderRoleList) {
	if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
	 branchId = eachRole.get("partyId");
}

if(parameters.prodStoreId){
prodStoreId = parameters.prodStoreId;

productStore = delegator.findOne("ProductStore",UtilMisc.toMap("productStoreId", prodStoreId), false);

branchId = productStore.payToPartyId;
}


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , branchId));
conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS ,"BRANCH_TRANSPORTER"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
supplierList = delegator.findList("PartyRelationship", condition, null, null, null, false);



if(supplierList){
	supplierList.each{ supplier ->
		JSONObject newObj = new JSONObject();
		conditionList =[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , supplier.partyIdTo));
		//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PARTY_ENABLED"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		partyList = delegator.findList("Party", condition, null, null, null, false);
		
		partyDetails = EntityUtil.getFirst(partyList);
		if(UtilValidate.isNotEmpty(partyDetails)){
			partyName=PartyHelper.getPartyName(delegator, partyDetails.partyId, false);
			newObj.put("value",partyName);
			newObj.put("label",partyName+"["+partyDetails.partyId+"]");
			transporterJSON.add(newObj);
			partyNameObj.put(partyDetails.partyId,partyName);
		}
	}
}

 

context.transporterJSON=transporterJSON;

request.setAttribute("transporterJSON", transporterJSON);

return "sucess";

}else{

 context.carrierName = carrierName;
}