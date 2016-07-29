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

orderId = parameters.orderId;


conditionList = [];
conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN, ["BRANCH_SHIPMENT","DEPOT_SHIPMENT"]));
conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.NOT_EQUAL, null));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
shipments = delegator.findList("Shipment", condition, null, null, null, false);

shipmentIds = EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", true);

double quantityAccepted = 0;
if(shipmentIds){

conditionList.clear();
if(shipmentIds){
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
}
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_ACCEPTED", "SR_RECEIVED"]));
condition2 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
shipmentReceipts = delegator.findList("ShipmentReceipt", condition2, null, null, null, false);


for (eachReceipt in shipmentReceipts) {
	quantityAccepted = quantityAccepted+Double.valueOf(eachReceipt.quantityAccepted);
}


}
JSONObject tempMap = new JSONObject();

tempMap.put("quantityAccepted", quantityAccepted);


orderList.add(tempMap);


request.setAttribute("orderList", orderList);
return "success";
