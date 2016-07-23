
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

invoiceId= parameters.invoiceId;

if(invoiceId){
	

context.invoiceId= invoiceId;		
billToPartyId= parameters.partyId;

context.billToPartyId = billToPartyId;

partyName= parameters.partyName;

invoiceList = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
partyId = invoiceList.get("partyId");
shipmentId = invoiceList.get("shipmentId");

invoDate = invoiceList.get("invoiceDate");

//Debug.log("invoDate================"+invoDate);


SimpleDateFormat formatter = new SimpleDateFormat("dd MMM,yyyy");
 invoDate = formatter.format(invoDate);


//Debug.log("invoDate================"+invoDate);

context.invoDate = invoDate;

tallySalesNo = invoiceList.get("referenceNumber");


orderItemBillings = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, null, null, false);
orderItemBillings = EntityUtil.getFirst(orderItemBillings);

orderId = orderItemBillings.orderId;

context.orderId = orderId;

if(orderId){
	orderAttrForPo = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	OrderHeaderList = delegator.findOne("OrderHeader",[orderId : orderId] , false);
	
	tallyRefNo = OrderHeaderList.get("tallyRefNo");
	
}



List conditionList = [];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));

cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
shipmentListForPOInvoiceId = delegator.findList("Invoice", cond, null, null, null, false);

purInvoiceId = "";

if(shipmentListForPOInvoiceId)
 purInvoiceId = shipmentListForPOInvoiceId[0].invoiceId;


if(purInvoiceId){
	purInvoiceList = delegator.findOne("Invoice",[invoiceId : purInvoiceId] , false);
	if(purInvoiceList.referenceNumber)
	tallyRefNo = purInvoiceList.referenceNumber;
}


if(tallySalesNo)
tallyRefNo = tallySalesNo;


Debug.log("tallyRefNo=============="+tallyRefNo);

context.tallyRefNo = tallyRefNo;

JSONArray invoiceItemsJSON = new JSONArray();


conditionList = [];
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);


invoiceItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
invoiceAdjItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_FPROD_ITEM","TEN_PERCENT_SUBSIDY","VAT_PUR", "CST_PUR","CST_SALE","VAT_SALE","CESS_SALE","CESS_PUR","VAT_SURHARGE")));







for (eachItem in invoiceItemList) {
	
	JSONObject newObj = new JSONObject();

newObj.put("cProductId",eachItem.productId);

productName = ""
prod=delegator.findOne("Product",[productId:eachItem.productId],false);
if(UtilValidate.isNotEmpty(prod)){
	productName = prod.get("productName");
}
newObj.put("cProductName",productName);
newObj.put("quantity",eachItem.quantity);
newObj.put("UPrice", eachItem.amount);
newObj.put("amount", eachItem.amount);
newObj.put("VatPercent", 0.00);
newObj.put("VAT", 0.00);
newObj.put("CSTPercent", 0.00);
newObj.put("CST", 0.00);
invoiceItemsJSON.add(newObj);



}

context.invoiceItemsJSON = invoiceItemsJSON;




invoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]), null, null, null, false);
Debug.log("invoiceItemTypes =========="+invoiceItemTypes);
additionalChgs = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
dicounts = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISCOUNTS"));

JSONArray discountItemsJSON = new JSONArray();
JSONObject discountLabelJSON = new JSONObject();
JSONObject discountLabelIdJSON=new JSONObject();
dicounts.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.invoiceItemTypeId);
	newObj.put("label",eachItem.description +" [ " +eachItem.invoiceItemTypeId+"]");
	discountItemsJSON.add(newObj);
	discountLabelJSON.put(eachItem.invoiceItemTypeId, eachItem.description);
	discountLabelIdJSON.put(eachItem.description +" [ " +eachItem.invoiceItemTypeId+"]", eachItem.invoiceItemTypeId);
	
}
context.discountItemsJSON = discountItemsJSON;
context.discountLabelJSON = discountLabelJSON;
context.discountLabelIdJSON = discountLabelIdJSON;


JSONArray invoiceAdjItemsJSON = new JSONArray();
JSONObject invoiceAdjLabelJSON = new JSONObject();
JSONObject invoiceAdjLabelIdJSON=new JSONObject();
additionalChgs.each{eachItem ->
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







JSONArray invoiceDiscountJSON = new JSONArray();
JSONArray invoiceAdditionalJSON = new JSONArray();
   invoiceAdjItemList.each{eachItem ->
	   
	   
	   JSONObject newObj = new JSONObject();
	   newObj.put("invoiceItemTypeId",eachItem.invoiceItemTypeId);
	   newObj.put("applicableTo",eachItem.description);
	   
	   //De000000000bug.log("eachItem.amount============="+eachItem.amount);
	   
	   newObj.put("adjAmount",Math.abs((eachItem.amount*eachItem.quantity)));
	   newObj.put("discQty",eachItem.quantity);
	   
	   if(eachItem.amount > 0)
	   invoiceAdditionalJSON.add(newObj);
	   else
	   invoiceDiscountJSON.add(newObj);
	   
   }
	   
   Debug.log("invoiceDiscountJSON================="+invoiceDiscountJSON);
   Debug.log("invoiceAdditionalJSON================="+invoiceAdditionalJSON);
   context.invoiceDiscountJSON = invoiceDiscountJSON;
   context.invoiceAdditionalJSON = invoiceAdditionalJSON;


}

