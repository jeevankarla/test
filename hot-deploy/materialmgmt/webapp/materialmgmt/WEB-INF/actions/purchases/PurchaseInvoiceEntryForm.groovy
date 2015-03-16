
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
import in.vasista.vbiz.purchase.MaterialHelperServices;
import in.vasista.vbiz.purchase.PurchaseStoreServices;
import java.math.RoundingMode;

purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
rounding = RoundingMode.FLOOR;
shipmentId = parameters.shipmentId;
dctx = dispatcher.getDispatchContext();
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
		
		orderedInvoice = Boolean.FALSE;
		orderId = shipment.primaryOrderId;
		if(orderId){
			orderedInvoice = Boolean.TRUE;
		}
		
		orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		
		poValue = 0;
		if(orderHeader){
			poValue = orderHeader.grandTotal;
		}
		
		conditionList.clear();
		if(orderId){
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		}
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED"));
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
		
		condExpr = [];
		condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_IN, UtilMisc.toList("BED_PUR", "VAT_PUR","CST_PUR", "BEDCESS_PUR", "BEDSECCESS_PUR")));
		cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
		orderAdjustments = delegator.findList("OrderAdjustment", cond, null, null, null, false);
		
		prodQty = [];
		adjustmentTypes = [];
		shipmentReceipts.each{ eachItem ->
			
			String productId = eachItem.productId;
			qty = eachItem.quantityAccepted;
			ordItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			orderItem = EntityUtil.getFirst(ordItem);
			
			vatAmt = BigDecimal.ZERO;
			cstAmt = BigDecimal.ZERO;
			if(orderItem.vatAmount){
				vatAmt = (orderItem.vatAmount).divide((orderItem.quantity), purchaseTaxFinalDecimals, purchaseTaxRounding);
				vatAmt = vatAmt.multiply(qty);
			}
			if(orderItem.cstAmount){
				cstAmt = (orderItem.cstAmount).divide((orderItem.quantity), purchaseTaxFinalDecimals, purchaseTaxRounding);
				cstAmt = cstAmt.multiply(qty);
			}
			prodValue = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
			totalBedPrice = eachItem.bedAmount+eachItem.bedcessAmount+eachItem.bedseccessAmount;
			
			totalBedPrice = 0;
			
			if(orderItem.bedAmount){
				totalBedPrice += (orderItem.bedAmount)/(orderItem.quantity);
			}
			if(orderItem.bedcessAmount){
				totalBedPrice += (orderItem.bedcessAmount)/(orderItem.quantity);
			}
			if(orderItem.bedseccessAmount){
				totalBedPrice += (orderItem.bedseccessAmount)/(orderItem.quantity);
			}
			unitPrice = (orderItem.unitPrice)+totalBedPrice;
			amount = unitPrice*qty;
			JSONObject newObj = new JSONObject();
			newObj.put("cProductId",eachItem.productId);
			newObj.put("cProductName",prodValue.productName +" [ "+eachItem.productId+"]");
			newObj.put("quantity",qty);
			newObj.put("UPrice", unitPrice);
			newObj.put("amount", amount);
			newObj.put("VatPercent", orderItem.vatPercent);
			newObj.put("VAT", vatAmt);
			newObj.put("CSTPercent", orderItem.cstPercent);
			newObj.put("CST", cstAmt);
			invoiceItemsJSON.add(newObj);
		}
		
		otherCharges = [];
		orderAdjustments.each{ eachOdrAdj ->
			tempMap = [:];
			
			seqId = eachOdrAdj.orderItemSeqId;
			if(seqId && seqId == "_NA_"){
				applicableTo = "ALL";
			}
			else{
				ordItm = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seqId));
				if(ordItm){
					applicableTo = (EntityUtil.getFirst(ordItm)).get("productId");
				}
			}
			
			tempMap.put("otherTermId", eachOdrAdj.orderAdjustmentTypeId);
			tempMap.put("applicableTo", applicableTo);
			tempMap.put("termValue", eachOdrAdj.amount);
			tempMap.put("uomId", "INR");
			tempMap.put("termDays", null);
			tempMap.put("description", "");
			otherCharges.add(tempMap);
		}
		
		productQty = [];
		orderItems.each{ eachItem ->
			tempMap = [:];
			tempMap.put("productId", eachItem.productId);
			tempMap.put("quantity", eachItem.quantity);
			vatPercent = BigDecimal.ZERO;
			bedPercent = BigDecimal.ZERO;
			cstPercent = BigDecimal.ZERO;
			if(eachItem.cstPercent){
				cstPercent = eachItem.cstPercent;
			}
			if(eachItem.bedPercent){
				resultCtx = MaterialHelperServices.getOrderTaxRateForComponentRate(dctx, UtilMisc.toMap("userLogin", userLogin, "taxType", "EXCISE_DUTY_PUR", "componentRate", eachItem.bedPercent, "effectiveDate", orderHeader.orderDate));
				bedPercent = resultCtx.get("taxRate");
			}
			if(eachItem.vatPercent){
				vatPercent = eachItem.vatPercent;
			}
			tempMap.put("unitPrice", eachItem.unitPrice);
			tempMap.put("bedPercent", bedPercent);
			tempMap.put("cstPercent", cstPercent);
			tempMap.put("vatPercent", vatPercent);
			productQty.add(tempMap);
		}
		
		Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQty, "otherCharges", otherCharges, "userLogin", userLogin, "incTax", ""));
		if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
		}
		Map adjPerUnit = (Map)resultCtx.get("productAdjustmentPerUnit");
		
		shipmentAttribute = delegator.findList("ShipmentAttribute", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
		JSONArray adjustmentJSON = new JSONArray();

		adjustmentTypes = [];
		shipmentAttribute.each{ eachAdj ->
			amt = new BigDecimal(eachAdj.attrValue);
			JSONObject newObj = new JSONObject();
			newObj.put("invoiceItemTypeId", eachAdj.attrName);
			newObj.put("adjAmount", amt);
			adjustmentJSON.add(newObj);
			
			tempMap = [:];
			tempMap.otherTermId = eachAdj.attrName;
			tempMap.applicableTo = "ALL";
			tempMap.termValue = amt;
			tempMap.uomId = "INR";
			tempMap.termDays = null;
			tempMap.description = "";
			adjustmentTypes.add(tempMap);
		}
		
		orderAdjustments.each{ eachOdrAdj ->
			tempMap = [:];
			adjTypeId = eachOdrAdj.orderAdjustmentTypeId;
			applicableTo = eachOdrAdj.orderItemSeqId;
			if(eachOdrAdj.orderItemSeqId && eachOdrAdj.orderItemSeqId == "_NA_"){
				applicableTo = "ALL";
			}
			totalAdjAmt = BigDecimal.ZERO;
			shipmentReceipts.each{ eachItem ->
				String productId = eachItem.productId;
				qty = eachItem.quantityAccepted;
				if(adjPerUnit.get(productId)){
					prodAdjs = adjPerUnit.get(productId);
					if(prodAdjs && prodAdjs.get(adjTypeId)){
						unitAdjPrice = prodAdjs.get(adjTypeId);
						totalAdjAmt = totalAdjAmt.add(unitAdjPrice.multiply(qty));
					}
				}
			}
			
			JSONObject newObj = new JSONObject();
			newObj.put("invoiceItemTypeId", adjTypeId);
			newObj.put("adjAmount", totalAdjAmt.setScale(0, rounding));
			adjustmentJSON.add(newObj);
			
		}
		
		context.adjustmentJSON = adjustmentJSON;
	}
}
context.invoiceItemsJSON = invoiceItemsJSON;