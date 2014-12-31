
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;

import in.vasista.vbiz.purchase.PurchaseStoreServices;

shipmentId = parameters.shipmentId;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "MATERIAL_SHIPMENT"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.NOT_EQUAL, null));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
shipments = delegator.findList("Shipment", condition, null, null, null, false);
JSONArray invoiceItemsJSON = new JSONArray();

if(shipments){
	
	shipment = EntityUtil.getFirst(shipments);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_REJECTED")));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	invoice = delegator.findList("Invoice", condition1, null, null, null, false);
	
	if(!invoice){
		orderId = shipment.primaryOrderId;
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_QUALITYCHECK"));
		condition2 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		shipmentReceipts = delegator.findList("ShipmentReceipt", condition2, null, null, null, false);
		
		orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
		condition3 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		orderRole = delegator.findList("OrderRole", condition3, null, null, null, false);
		
		partyId = "";
		
		if(orderRole){
			partyId = (EntityUtil.getFirst(orderRole)).getString("partyId");
		}
		
		orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		
		orderTypeId = orderHeader.orderTypeId;
		
		invoiceTypeId = "";
		if(orderTypeId == "PURCHASE_ORDER"){
			invoiceTypeId = "PURCHASE_INVOICE";	
		}
		
		
		
		invoiceItemAdjs = delegator.findList("InvoiceItemTypeMap", EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId), null, null, null, false);
		adjIds = EntityUtil.getFieldListFromEntityList(invoiceItemAdjs, "invoiceItemTypeId", true);
		invoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, adjIds), null, null, null, false);
		
		JSONArray invoiceAdjItemsJSON = new JSONArray();
		JSONObject invoiceAdjLabelJSON = new JSONObject();
		JSONObject invoiceAdjLabelIdJSON=new JSONObject();
		invoiceItemTypes.each{eachItem ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachItem.invoiceItemTypeId);
			newObj.put("label",eachItem.description +" [ " +eachItem.invoiceItemTypeId+"]");
			invoiceAdjItemsJSON.add(newObj);
			invoiceAdjLabelJSON.put(eachItem.invoiceItemTypeId, eachItem.description);
			invoiceAdjLabelIdJSON.put(eachItem.description +" [ " +eachItem.invoiceItemTypeId+"]", eachItem.invoiceItemTypeId);
			
		}
		
		context.invoiceAdjItemsJSON = invoiceAdjItemsJSON;
		context.invoiceAdjLabelJSON = invoiceAdjLabelJSON;
		context.invoiceAdjLabelIdJSON = invoiceAdjLabelIdJSON;
		context.orderId = orderId;
		context.partyId = partyId;
		context.shipmentDate = shipment.estimatedShipDate;
		context.vehicleId = shipment.vehicleId;
		
		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		
		shipmentReceipts.each{ eachItem ->
			partyId
			String productId = eachItem.productId;
			qty = eachItem.quantityAccepted;
			ordItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			orderItem = EntityUtil.getFirst(ordItem);
			prodValue = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			unitPrice = 0;
			vatPercent = 0;
			cstPercent = 0;
			bedPercent = 0;
			if(orderItem.unitPrice){
				unitPrice = orderItem.unitPrice;
			}
			if(orderItem.vatPercent){
				vatPercent = orderItem.vatPercent;
			}
			if(orderItem.bedPercent){
				bedPercent = orderItem.bedPercent;
			}
			if(orderItem.cstPercent){
				cstPercent = orderItem.cstPercent;
			}
			amount = unitPrice*qty;
			JSONObject newObj = new JSONObject();
			newObj.put("cProductId",productId);
			newObj.put("cProductName",prodValue.productName +" [ "+productId+"]");
			newObj.put("quantity",eachItem.quantityAccepted);
			newObj.put("UPrice",unitPrice);
			newObj.put("amount", amount);
			newObj.put("VatPercent",vatPercent);
//			newObj.put("VAT",(amount*vatPercent)/100);
			newObj.put("CSTPercent",cstPercent);
	//		newObj.put("CST",(amount*cstPercent)/100);
			newObj.put("ExcisePercent",bedPercent);
			newObj.put("bedCessPercent",0);
			newObj.put("bedSecCessPercent",0);
			invoiceItemsJSON.add(newObj);
		
		}
	}
}
context.invoiceItemsJSON = invoiceItemsJSON;
