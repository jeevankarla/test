
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

import org.ofbiz.party.contact.ContactMechWorker;
import java.math.RoundingMode;

ROUNDING = RoundingMode.HALF_UP;

/*import applications.accounting.src.org.ofbiz.accounting.invoice.BigDecimal;
import applications.accounting.src.org.ofbiz.accounting.invoice.GenericEntityException;
import applications.accounting.src.org.ofbiz.accounting.invoice.GenericServiceException;
import applications.accounting.src.org.ofbiz.accounting.invoice.GenericValue;
import applications.accounting.src.org.ofbiz.accounting.invoice.List;
import applications.accounting.src.org.ofbiz.accounting.invoice.Map;
import applications.accounting.src.org.ofbiz.accounting.invoice.Object;
import applications.accounting.src.org.ofbiz.accounting.invoice.String;*/


conditionList=[];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, parameters.shipmentId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_REJECTED")));
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoice = delegator.findList("Invoice", condition1, null, null, null, false);
if(UtilValidate.isNotEmpty(invoice)){
	////Debug.log("Sales Invoice Already Created with invoiceId :"+invoice[0].invoiceId,"");
	context.errorMessage = "Sales Invoice Already Created with invoiceId :"+invoice[0].invoiceId;
	return "error";
}
purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
rounding = RoundingMode.FLOOR;
shipmentId = parameters.shipmentId;

actualOrderId = parameters.orderId;
dctx = dispatcher.getDispatchContext();
conditionList = [];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN, ["BRANCH_SHIPMENT","DEPOT_SHIPMENT"]));
//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.NOT_EQUAL, null));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
shipments = delegator.findList("Shipment", condition, null, null, null, false);
JSONArray invoiceItemsJSON = new JSONArray();

if(shipments){
	
	shipment = EntityUtil.getFirst(shipments);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_REJECTED")));
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	invoice = delegator.findList("Invoice", condition1, null, null, null, false);
	
	orderId = actualOrderId;
	primaryOrderId = shipments.primaryOrderId;
	
	
	invoiceLists = EntityUtil.getFirst(invoice);
	
	purchaceInvoiceId = invoiceLists.invoiceId
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, purchaceInvoiceId));
	//conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, UtilMisc.toList("INV_RAWPROD_ITEM", "VAT_PUR", "CST_PUR", "VAT_SURCHARGE", "CST_SURCHARGE")));
	List<GenericValue> invoiceItemList =null;
   
	 invoiceItemList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	
	 
	 roList = dispatcher.runSync("getRegionalOffices",UtilMisc.toMap("userLogin",userLogin));
	 roPartyList = roList.get("partyList");
	 
	 //Debug.log("roPartyList================"+roPartyList);
	 
	 ro = roPartyList[0].partyId;
	 
	 
     JSONArray invoiceDiscountJSON = new JSONArray();
	 JSONArray invoiceAdditionalJSON = new JSONArray();
	 
	 
	  disCountFlag = "";
	 
	   /*double totAdjustment = 0;
	    
	   if(invoiceItemList){
		   invoiceItemList.each{eachItem ->
			
			
			JSONObject newObj = new JSONObject();
			newObj.put("invoiceItemTypeId",eachItem.invoiceItemTypeId);
			newObj.put("applicableTo",eachItem.description);
			
			////Debug.log("eachItem.amount============="+eachItem.amount);
			
			totAdjustment = totAdjustment+(eachItem.amount*eachItem.quantity);
			
			newObj.put("adjAmount",Math.abs((eachItem.amount*eachItem.quantity)));
			newObj.put("discQty",eachItem.quantity);
			
			if(eachItem.isAssessableValue && eachItem.isAssessableValue == "Y"){
				newObj.put("assessableValue", true);
			}
			
			
			if(eachItem.amount > 0)
			invoiceAdditionalJSON.add(newObj);
			else
			invoiceDiscountJSON.add(newObj);
			////Debug.log("invoiceDiscountJSON============="+invoiceDiscountJSON);
		   }
	   }else{
	   
	   		disCountFlag = "N";
	   }*/	
		
	   
	   context.disCountFlag = disCountFlag;
	   
		
		/*if(totAdjustment>0){
		JSONObject TenewObj = new JSONObject();
		
		TenewObj.put("invoiceItemTypeId", "TEN_PER_CHARGES");
		TenewObj.put("applicableTo", "");
		TenewObj.put("adjAmount", (totAdjustment*10)/100);
		TenewObj.put("discQty", 0);
		
		invoiceAdditionalJSON.add(TenewObj);
		
		}else{
		
		
		JSONObject TenewObjD = new JSONObject();
		
		TenewObjD.put("invoiceItemTypeId", "TEN_PER_DISCOUNT");
		TenewObjD.put("applicableTo", "");
		TenewObjD.put("adjAmount", Math.abs((totAdjustment*10)/100));
		TenewObjD.put("discQty", 0);
		
		
		invoiceDiscountJSON.add(TenewObjD);
		
		
		}*/
		
		
		
		
		
		/*context.invoiceDiscountJSON = invoiceDiscountJSON;
		context.invoiceAdditionalJSON = invoiceAdditionalJSON;
	////Debug.log("invoiceDiscountJSON======================="+invoiceDiscountJSON);
	 
	////Debug.log("invoiceAdditionalJSON======================="+invoiceAdditionalJSON);*/
	 
	
	
	context.purchaceInvoiceId = purchaceInvoiceId;
	
	////Debug.log("purchaceInvoiceId======================="+purchaceInvoiceId);
	////Debug.log("orderId======================="+orderId);
	
	//if(!invoice && orderId){
		
		orderedInvoice = Boolean.FALSE;
		
		if(orderId){
			orderedInvoice = Boolean.TRUE;
		}
		
		orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		
		
		poValue = 0;
		if(orderHeader){
			poValue = orderHeader.grandTotal;
		}
		
		conditionList.clear();
		if(primaryOrderId){
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
		}
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_ACCEPTED", "SR_RECEIVED"]));
		condition2 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		shipmentReceipts = delegator.findList("ShipmentReceipt", condition2, null, null, null, false);
		
		////Debug.log("shipmentReceipts======================="+shipmentReceipts);
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,primaryOrderId));
		conditionList.add(EntityCondition.makeCondition("invoiceId" ,EntityOperator.EQUALS,purchaceInvoiceId));
		orderItemBillingList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null ,false);
		
		
		orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		////Debug.log("orderItems======================="+orderItems);
		productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
		
		
		/*exprCondList=[];
		exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
		OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
		
		scheme = "";
		if(OrderAss){
			
			salesOreder=OrderAss.orderId;
			
			////Debug.log("salesOreder=================="+salesOreder);
			
		
		}*/
		
		
		orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		
		if(UtilValidate.isNotEmpty(orderAttr)){
			orderAttr.each{ eachAttr ->
				if(eachAttr.attrName == "SCHEME_CAT"){
					scheme =  eachAttr.attrValue;
					context.scheme = scheme;
				}
				
			}
		   }
		
		//Debug.log("scheme============="+scheme);
		
	
		
		
		condExpr = [];
		condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		condExpr.add(EntityCondition.makeCondition("amount", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
		cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
		orderAdjustForTen = delegator.findList("OrderAdjustment", cond, UtilMisc.toSet("orderId","amount"), null, null, false);
		
		
		
		tenperValue = 0;
		if(orderAdjustForTen){
			amount=(EntityUtil.getFirst(orderAdjustForTen)).getString("amount");
			tenperValue = Double.valueOf(amount);
		}
		
		context.tenperValue = tenperValue;
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN , UtilMisc.toList("SUPPLIER","BILL_FROM_VENDOR",,"BILL_TO_CUSTOMER") ));
		condition3 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		orderRole = delegator.findList("OrderRole", condition3, null, null, null, false);
		
		partyId = "";
		
		billToPartyId="";
        branchPartyId = "";
		if(orderRole){
			billToPartyIdList=EntityUtil.filterByCondition(orderRole, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
			if(billToPartyIdList){
				billToPartyId=(EntityUtil.getFirst(billToPartyIdList)).getString("partyId");
			}
			supplierPartyIdList=EntityUtil.filterByCondition(orderRole, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
			if(supplierPartyIdList){
				partyId = (EntityUtil.getFirst(supplierPartyIdList)).getString("partyId");
			}
			branchPartyIdList=EntityUtil.filterByCondition(orderRole, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
			if(branchPartyIdList){
				branchPartyId = (EntityUtil.getFirst(branchPartyIdList)).getString("partyId");
			}
		}
		context.branchPartyId=branchPartyId;
		invoiceTypeId = "";
		orderTypeId = orderHeader.orderTypeId;
		if(orderTypeId == "SALES_ORDER"){
			invoiceTypeId = "SALES_ORDER";
		}
		
		titleTransferEnumIdsList = [];
		taxAuthorityTypeTitleTransferList = delegator.findList("TaxAuthorityTypeTitleTransfer", null, null, null, null, false);
		titleTransferEnumIdsList = EntityUtil.getFieldListFromEntityList(taxAuthorityTypeTitleTransferList, "titleTransferEnumId", true);
		
		
		// Transaction Type Tax Details
		JSONObject transactionTypeTaxMap = new JSONObject();
		for(int i=0; i<titleTransferEnumIdsList.size(); i++){
			titleTransferEnumId = titleTransferEnumIdsList.get(i);
			
			filteredTitleTransfer = EntityUtil.filterByCondition(taxAuthorityTypeTitleTransferList, EntityCondition.makeCondition("titleTransferEnumId", EntityOperator.EQUALS, titleTransferEnumId));
			taxIdsList = EntityUtil.getFieldListFromEntityList(filteredTitleTransfer, "taxAuthorityRateTypeId", true);
			
			JSONArray applicableTaxList = new JSONArray();
			for(int j=0; j<taxIdsList.size(); j++){
				applicableTaxList.add(taxIdsList.get(j));
			}
			transactionTypeTaxMap.putAt(titleTransferEnumId, applicableTaxList);
		}
		////Debug.log("transactionTypeTaxMap =================="+transactionTypeTaxMap);
		context.transactionTypeTaxMap = transactionTypeTaxMap;
		
		////Debug.log("customer =================="+billToPartyId);
		////Debug.log("branch =================="+branchPartyId);
		
		String customerGeoId = null;
		List supplierContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, billToPartyId, false, "TAX_CONTACT_MECH");
		if(UtilValidate.isNotEmpty(supplierContactMechValueMaps)){
			customerGeoId = (String)((GenericValue) ((Map) supplierContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
		}
		
		String branchGeoId = null;
		List branchContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, branchPartyId, false, "TAX_CONTACT_MECH");
		if(UtilValidate.isNotEmpty(branchContactMechValueMaps)){
			branchGeoId = (String)((GenericValue) ((Map) branchContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
		}
		
		////Debug.log("customerGeoId =================" +customerGeoId);
		////Debug.log("branchGeoId ================" +branchGeoId);
		
		context.customerGeoId = customerGeoId;
		context.branchGeoId = branchGeoId;
		
		orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		
		saleTaxType = null;
		saleTitleTransferEnumId = null;
		orderAttr.each{ eachAttr ->
			if(eachAttr.attrName == "saleTaxType"){
				saleTaxType =  eachAttr.attrValue;
			}
			if(eachAttr.attrName == "saleTitleTransferEnumId"){
				saleTitleTransferEnumId = eachAttr.attrValue;
			}
		}
		if(UtilValidate.isEmpty(saleTitleTransferEnumId)){
			if(customerGeoId == branchGeoId){
				saleTaxType = "Intra-State";
				saleTitleTransferEnumId = "NO_E2_FORM";
			}
			else{
				saleTaxType = "Inter-State";
				saleTitleTransferEnumId = "CST_CFORM";
			}
		}
		
		context.saleTaxType = saleTaxType;
		context.saleTitleTransferEnumId = saleTitleTransferEnumId;
		
		taxList = transactionTypeTaxMap.get(saleTitleTransferEnumId);
		context.taxListReady = taxList;
		
		
		//invoiceItemAdjs = delegator.findList("InvoiceItemTypeMap", EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId), null, null, null, false);
		//adjIds = EntityUtil.getFieldListFromEntityList(invoiceItemAdjs, "invoiceItemTypeId", true);
		
		invoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]), null, null, null, false);
		invoiceItemTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemTypes, "invoiceItemTypeId", true);
		
		
		additionalChgs = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
		dicounts = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISCOUNTS"));
		////Debug.log("additionalChgs =========="+additionalChgs);
		////Debug.log("dicounts =========="+dicounts);
		
		additionalChgTypeIdsList = EntityUtil.getFieldListFromEntityList(additionalChgs, "invoiceItemTypeId", true);
		discountTypeIdsList = EntityUtil.getFieldListFromEntityList(dicounts, "invoiceItemTypeId", true);
		
		
		// Other Charges
		
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
		
		// Discounts
		
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
		
		////Debug.log("discountItemsJSON =========="+discountItemsJSON);
		
		orderNo="";
		salesOrderSeqDetails = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , UtilMisc.toSet("orderNo"), null, null, false );
		if(UtilValidate.isNotEmpty(salesOrderSeqDetails)){
			orderNo = EntityUtil.getFirst(salesOrderSeqDetails).orderNo;
		}
		context.orderNo=orderNo;
		context.orderId = orderId;
		context.partyId = partyId;
		context.billToPartyId = billToPartyId;
		milliseconds=(shipment.estimatedShipDate).getTime();
		context.shipmentDate = shipment.supplierInvoiceDate;
		context.milliseconds=milliseconds;
		context.vehicleId = shipment.vehicleId;		
		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		
		condExpr = [];
		condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_IN, UtilMisc.toList("BED_PUR", "VAT_PUR","CST_PUR", "BEDCESS_PUR", "BEDSECCESS_PUR")));
		cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
		orderAdjustments = delegator.findList("OrderAdjustment", cond, null, null, null, false);
		
		prodQty = [];
		adjustmentTypes = [];
		
		/*otherCharges = [];
		orderAdjustments.each{ eachOdrAdj ->
			tempMap = [:];
			
			seqId = eachOdrAdj.orderItemSeqId;
			if(seqId && seqId != "_NA_"){
				ordItm = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seqId));
				if(ordItm){
					applicableTo = (EntityUtil.getFirst(ordItm)).get("productId");
				}
			}
			else{
				applicableTo = "ALL";
			}
			
			tempMap.put("otherTermId", eachOdrAdj.orderAdjustmentTypeId);
			tempMap.put("applicableTo", applicableTo);
			tempMap.put("termValue", eachOdrAdj.amount);
			tempMap.put("uomId", "INR");
			tempMap.put("termDays", null);
			tempMap.put("description", "");
			otherCharges.add(tempMap);
		}*/
		
		productQty = [];
		orderItems.each{ eachItem ->
			
			////Debug.log("orderId =========="+eachItem.orderId);
			
			taxResultCtx = 0;
			taxValueMap = [:];
			defaultTaxMap = [:];
			/*if( (UtilValidate.isNotEmpty(customerGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
				Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);
				prodCatTaxCtx.put("productId", eachItem.productId);
				prodCatTaxCtx.put("taxAuthGeoId", branchGeoId);
				
				taxResultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",prodCatTaxCtx);
				
				
				taxValueMap = taxResultCtx.get("taxValueMap");
				defaultTaxMap = taxResultCtx.get("defaultTaxMap");
			}*/
			
			resultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",UtilMisc.toMap("userLogin",userLogin, "taxAuthGeoId", "IN-UP","taxAuthorityRateTypeId","CST_SALE","productId",eachItem.productId));
			
			defaultTaxMap = resultCtx.defaultTaxMap;
			
			taxValueMap = resultCtx.taxValueMap;
			
			
			
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
		
		/*Map resultCtx = dispatcher.runSync("getMaterialItemValuationDetails", UtilMisc.toMap("productQty", productQty, "otherCharges", otherCharges, "userLogin", userLogin, "incTax", ""));
		if(ServiceUtil.isError(resultCtx)){
				String errMsg =  ServiceUtil.getErrorMessage(resultCtx);
				return ServiceUtil.returnError(errMsg);
		}
		Map adjPerUnit = (Map)resultCtx.get("productAdjustmentPerUnit");*/
		
		JSONObject productIdLabelJSON = new JSONObject();
		JSONObject productLabelIdJSON=new JSONObject();
		
		condExpr = [];
		condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE","TEN_PERCENT_SUBSIDY")));
		cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
		taxDetails = delegator.findList("OrderAdjustment", cond, null, null, null, false);
		
		////Debug.log("orderItems =========="+orderItems);
		
		
		////Debug.log("orderItemBillingList =========="+orderItemBillingList);
		
		////Debug.log("invoiceItemList======================="+invoiceItemList);
		
		shipmentReceipts.each{ eachItem ->
			
			poSeqNo = eachItem.orderItemSeqId;
			////Debug.log("poSeqNo =========="+poSeqNo);
			
			relatedInvoiceItems = EntityUtil.filterByCondition(orderItemBillingList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, poSeqNo));
			invoiceItemSeqIdsList = EntityUtil.getFieldListFromEntityList(relatedInvoiceItems, "invoiceItemSeqId", true);
			////Debug.log("relatedInvoiceItems =========="+relatedInvoiceItems);
			
			////Debug.log("invoiceItemSeqIdsList =========="+invoiceItemSeqIdsList);
			
			
				condExpr = [];
				condExpr.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, eachItem.orderId));
				condExpr.add(EntityCondition.makeCondition("toOrderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
				OrderItemAssoc = delegator.findList("OrderItemAssoc", cond, null, null, null, false);
				
				orderId = OrderItemAssoc[0].orderId;
				
				orderItemSeqId = OrderItemAssoc[0].orderItemSeqId;
				
				
				relOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				////Debug.log("relOrderItem =========="+relOrderItem);
				
				origQty = (relOrderItem.get(0)).get("quantity");
				
				////Debug.log("orderId===================="+orderId);
				
				////Debug.log("orderItemSeqId===================="+orderItemSeqId);
				
				orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
				
				////Debug.log("orderAttr==================="+orderAttr);
				scheme = "";
				if(UtilValidate.isNotEmpty(orderAttr)){
					orderAttr.each{ eachAttr ->
						if(eachAttr.attrName == "SCHEME_CAT"){
							scheme =  eachAttr.attrValue;
						}
						
					}
				   }
				
				
				condExpr.clear();
				condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				condex = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
				OrderItem = delegator.findList("OrderItem", condex, null, null, null, false);
								
				String productId = OrderItem[0].productId;
				/*adjUnitAmtMap = [:];
				if(adjPerUnit && adjPerUnit.get(productId)){
					adjUnitAmtMap = adjPerUnit.get(productId);
				}
				deductAmt = 0;
				addAmt = 0;
				if(adjUnitAmtMap && adjUnitAmtMap.get("COGS_DISC")){
					discAmt = adjUnitAmtMap.get("COGS_DISC");
					deductAmt = deductAmt+discAmt;
				}
				
				if(adjUnitAmtMap && adjUnitAmtMap.get("COGS_DISC_BASIC")){
					discAmt = adjUnitAmtMap.get("COGS_DISC_BASIC");
					deductAmt = deductAmt+discAmt;
				}
				if(adjUnitAmtMap && adjUnitAmtMap.get("COGS_PCK_FWD")){
					packFwdAmt = adjUnitAmtMap.get("COGS_PCK_FWD");
					addAmt = addAmt+packFwdAmt;
				}
				if(adjUnitAmtMap && adjUnitAmtMap.get("COGS_INSURANCE")){
					insuranceAmt = adjUnitAmtMap.get("COGS_INSURANCE");
					addAmt = addAmt+insuranceAmt;
				}*/
				qty = eachItem.quantityAccepted;
				
				
				
				
				/*condiList = [];
				condiList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachItem.orderId));
				condiList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				cond = EntityCondition.makeCondition(condiList, EntityOperator.AND);
				adjOrderItems = delegator.findList("OrderItem", cond, null, null, null, false);
				ordItem = EntityUtil.filterByCondition(adjOrderItems, cond);
				orderItem = EntityUtil.getFirst(ordItem);*/
				
				
				prodValue = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
				
				// Fetch Tax details from order adjustment
				/*vatPercent = 0;
				
				condExpr = [];
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_PUR"));
				vatItems = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
				
				if(UtilValidate.isNotEmpty(vatItems)){
					vatPercent = (EntityUtil.getFirst(vatItems)).get("sourcePercentage");
				}
				
				cstPercent = 0;
				
				condExpr = [];
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, OrderItem[0].orderItemSeqId));
				condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "CST_PUR"));
				cstItems = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
				
				if(UtilValidate.isNotEmpty(cstItems)){
					cstPercent = (EntityUtil.getFirst(cstItems)).get("sourcePercentage");
				}*/
				
				//double tenPercent = 0;
				condExpr.clear();
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				tenPercentItems = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
				
				////Debug.log("tenPercentItems=============="+tenPercentItems);
				
				/*vatAmt = BigDecimal.ZERO;
				cstAmt = BigDecimal.ZERO;*/
				
				unitPrice = (OrderItem[0].unitPrice);
				
				
				
				JSONObject newObj = new JSONObject();
				newObj.put("cProductId",OrderItem[0].productId);
				newObj.put("orderItemSeqId",OrderItem[0].orderItemSeqId);
				newObj.put("ro",ro);
				
				
				
				
				productName = ""
				prod=delegator.findOne("Product",[productId:OrderItem[0].productId],false);
				if(UtilValidate.isNotEmpty(prod)){
					productName = prod.get("productName");
				}
				newObj.put("cProductName",productName);
				newObj.put("quantity",qty);
				newObj.put("UPrice", unitPrice);
				
				
				condExpr.clear();
				condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
				condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SERVICE_CHARGE"));
				cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
				OrderAdjustmentForServiceCha = delegator.findList("OrderAdjustment", cond, null, null, null, false);
				
				serviceChrg = 0;
				if(OrderAdjustmentForServiceCha){
					
					sourcePercentage = OrderAdjustmentForServiceCha.sourcePercentage;
					
					newObj.put("SERVICE_CHARGE_AMT", ((qty*unitPrice)*sourcePercentage)/100);
					newObj.put("SERVICE_CHARGE", (OrderAdjustmentForServiceCha[0].sourcePercentage));
					serviceChrg = ((qty*unitPrice)*sourcePercentage)/100;
				}else{
				newObj.put("SERVICE_CHARGE_AMT", 0);
				}
				
				
				if(UtilValidate.isNotEmpty(orderId)){
					List conditionlist=[];
					conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					conditionlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
					conditionlist.add(EntityCondition.makeCondition("changeTypeEnumId", EntityOperator.EQUALS, "ODR_ITM_AMEND"));
					conditionlist.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO, shipment.createdDate));
					EntityCondition conditionMain1=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
					def orderBy = UtilMisc.toList("changeDatetime");
					OrderItemChangeDetails = delegator.findList("OrderItemChange", conditionMain1 , null ,orderBy, null, false );
					////Debug.log("OrderItemChangeDetails================="+OrderItemChangeDetails);
					if(OrderItemChangeDetails)
					OrderItemChangeDetails=(OrderItemChangeDetails).getLast();
					if(UtilValidate.isNotEmpty(OrderItemChangeDetails)){
						newObj.put("UPrice",OrderItemChangeDetails.unitPrice);
						unitPrice=OrderItemChangeDetails.unitPrice;
					}
				}
				
				totalTaxAmt = 0;
				if(saleTitleTransferEnumId){
					//purTaxList = transactionTypeTaxMap.get(purchaseTitleTransferEnumId);
					for(int i=0; i<taxList.size(); i++){
						taxItem = taxList.get(i);
						////Debug.log("taxItemtaxItem ============="+taxItem);
						
						////Debug.log("defaultTaxMap ============="+defaultTaxMap);
						
						surChargeList = [];
						if(defaultTaxMap){
						taxInfo = defaultTaxMap.get(taxItem);
						surChargeList = taxInfo.get("surchargeList");
						}
						
						condExpr = [];
						condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
						condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, taxItem));
						taxItemList = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
						
						
						////Debug.log("taxItemList ============="+taxItemList);
						
						
						taxPercent = 0;
						taxValue = 0;
						actualTaxValue = 0;
						if(UtilValidate.isNotEmpty(taxItemList)){
							taxPercent = (EntityUtil.getFirst(taxItemList)).get("sourcePercentage");
							actualTaxValue = (EntityUtil.getFirst(taxItemList)).get("amount");
							taxValue = (actualTaxValue/origQty)*qty;
						}
						
						//Debug.log("taxItem================"+taxItem);
						
						//Debug.log("taxValue================"+taxValue);
						
						taxItem = taxItem.replace("SALE", "PUR");
						
						newObj.put(taxItem, taxPercent);
						newObj.put(taxItem+"_AMT", taxValue);
						
						totalTaxAmt = totalTaxAmt + taxValue;
						////Debug.log("totalTaxAmt ============="+totalTaxAmt);
						for(int j=0; j<surChargeList.size(); j++){
							surchargeItem = (surChargeList.get(j)).get("taxAuthorityRateTypeId");
							////Debug.log("surchargeItem ============="+surchargeItem);
							condExpr = [];
							condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
							condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, surchargeItem));
							surItemList = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
							
							surTaxPercent = 0;
							surTaxValue = 0;
							if(UtilValidate.isNotEmpty(surItemList)){
								surTaxPercent = (EntityUtil.getFirst(surItemList)).get("sourcePercentage");
								surTaxValue = (surTaxPercent/100)*taxValue;
							}
							newObj.put(surchargeItem+"_PUR", surTaxPercent);
							newObj.put(surchargeItem+"_PUR_AMT", surTaxValue);
							
							totalTaxAmt = totalTaxAmt + surTaxValue;
							
						}
						
					}
				}
				
				////Debug.log("totalTaxAmt =========="+totalTaxAmt);
				
				newObj.put("taxAmt", totalTaxAmt);
				
				newObj.put("taxValueMap",taxValueMap);
				
				newObj.put("defaultTaxMap",defaultTaxMap);
				
				////Debug.log("newObj ============="+newObj);
				
				totalItemAdjAmt = 0;
				incBaseAmt = 0;
				
				JSONArray itemAdjustmentJSON = new JSONArray();
				
				for(int i=0; i<additionalChgTypeIdsList.size(); i++){
					invItemTypeId = additionalChgTypeIdsList.get(i);
					////Debug.log("invItemTypeId ============="+invItemTypeId);
					
					JSONObject newItemAdjObj = new JSONObject();
					newItemAdjObj.put("orderAdjustmentTypeId", invItemTypeId);
					
					/*originalOrderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", eachItem.orderItemSeqId));
					applicableTo = originalOrderItem.get("itemDescription");
					*/
					//newItemAdjObj.put("applicableTo", applicableTo);
					newItemAdjObj.put("adjValue", 0);
					newItemAdjObj.put("percentage", 0);
					newItemAdjObj.put("uomId", "INR");
					
					////Debug.log("invoiceItemSeqIdsList======================="+invoiceItemSeqIdsList);
					
					
					
					conditionList = [];
					conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.IN, invoiceItemSeqIdsList));
					itemAdditionalChgs = [];
					if(UtilValidate.isNotEmpty(invoiceItemList)){
						itemAdditionalChgs = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
					}
					////Debug.log("itemAdditionalChgs======================="+itemAdditionalChgs);
					
					if(UtilValidate.isNotEmpty(itemAdditionalChgs)){
						itemOrdAdj = EntityUtil.filterByCondition(itemAdditionalChgs, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invItemTypeId));
						
						if(UtilValidate.isNotEmpty(itemOrdAdj)){
							adjItem = EntityUtil.getFirst(itemOrdAdj);
							
							itemValue = adjItem.amount //(adjItem.amount/origQty)*qty;
							
							newItemAdjObj.put("adjValue", itemValue);
							if(adjItem.sourcePercentage){
								newItemAdjObj.put("percentage", adjItem.sourcePercentage);
							}
							else{
								newItemAdjObj.put("percentage", 0);
							}
							
							if(adjItem.isAssessableValue && adjItem.isAssessableValue == "Y"){
								newItemAdjObj.put("assessableValue", "checked");
								newObj.put(invItemTypeId + "_INC_BASIC", "TRUE");
								incBaseAmt = incBaseAmt + itemValue;
							}
							else{
								newObj.put(invItemTypeId + "_INC_BASIC", "FALSE");
							}
							
							// Update adjustments for item
							
							
							//Debug.log("invItemTypeId============="+invItemTypeId);
							
							//Debug.log("itemValue============="+itemValue);
							
							newObj.put(invItemTypeId + "_PUR", adjItem.sourcePercentage);
							newObj.put(invItemTypeId + "_PUR_AMT", itemValue);
							
							totalItemAdjAmt = totalItemAdjAmt + itemValue;
						}
						
					}
					
					itemAdjustmentJSON.add(newItemAdjObj);
					
				}
				////Debug.log("itemAdjustmentJSON ========================= "+itemAdjustmentJSON);
				
				
				totalDiscAmt = 0;
				JSONArray discItemAdjustmentJSON = new JSONArray();
				
				for(int i=0; i<discountTypeIdsList.size(); i++){
					invItemTypeId = discountTypeIdsList.get(i);
					////Debug.log("invItemTypeId ============="+invItemTypeId);
					
					JSONObject newItemAdjObj = new JSONObject();
					newItemAdjObj.put("orderAdjustmentTypeId", invItemTypeId);
					
					/*originalOrderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", eachItem.orderItemSeqId));
					applicableTo = originalOrderItem.get("itemDescription");
					*/
					//newItemAdjObj.put("applicableTo", applicableTo);
					newItemAdjObj.put("adjValue", 0);
					newItemAdjObj.put("percentage", 0);
					newItemAdjObj.put("uomId", "INR");
					
					conditionList = [];
					conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.IN, invoiceItemSeqIdsList));
					itemAdditionalChgs = [];
					if(UtilValidate.isNotEmpty(invoiceItemList)){
						itemAdditionalChgs = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
					}
					
					if(UtilValidate.isNotEmpty(itemAdditionalChgs)){
						itemOrdAdj = EntityUtil.filterByCondition(itemAdditionalChgs, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invItemTypeId));
						
						if(UtilValidate.isNotEmpty(itemOrdAdj)){
							adjItem = EntityUtil.getFirst(itemOrdAdj);
							
							itemValue = adjItem.amount //(adjItem.amount/origQty)*qty;
							
							newItemAdjObj.put("adjValue", itemValue*(-1));
							if(adjItem.sourcePercentage){
								newItemAdjObj.put("percentage", adjItem.sourcePercentage);
							}
							else{
								newItemAdjObj.put("percentage", 0);
							}
							
							if(adjItem.isAssessableValue && adjItem.isAssessableValue == "Y"){
								newItemAdjObj.put("assessableValue", "checked");
								newObj.put(invItemTypeId + "_INC_BASIC", "TRUE");
								incBaseAmt = incBaseAmt + itemValue;
							}
							else{
								newObj.put(invItemTypeId + "_INC_BASIC", "FALSE");
							}
							
							// Update adjustments for item
							
							
							newObj.put(invItemTypeId + "_PUR", adjItem.sourcePercentage);
							newObj.put(invItemTypeId + "_PUR_AMT", itemValue*(-1));
							
							
							totalDiscAmt = totalDiscAmt + itemValue;
						}
						
					}
					
					discItemAdjustmentJSON.add(newItemAdjObj);
					
				}
				////Debug.log("discItemAdjustmentJSON ========================= "+discItemAdjustmentJSON);
				
				
				amount = unitPrice*qty;
				
				newObj.put("additionalChgTypeIdsList", additionalChgTypeIdsList);
				newObj.put("discountTypeIdsList", discountTypeIdsList);
				
				newObj.put("itemAdjustments",itemAdjustmentJSON);
				newObj.put("discItemAdjustments",discItemAdjustmentJSON);
				newObj.put("OTH_CHARGES_AMT",totalItemAdjAmt);
				newObj.put("DISCOUNT_AMT",totalDiscAmt);
				newObj.put("incBaseAmt",incBaseAmt);
				
				
				BigDecimal totalQuota =BigDecimal.ZERO;
				BigDecimal tenPercentAdjQty =BigDecimal.ZERO;
				BigDecimal tenPercent = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(tenPercentItems) && scheme == "MGPS_10Pecent"){
					//tenPercent = (EntityUtil.getFirst(tenPercentItems)).get("amount");
					// Get Already Billed Qty
					// Check against OrderAdjustmentBilling to see how much of this adjustment has already been invoiced
					
					adj = EntityUtil.getFirst(tenPercentItems);
					////Debug.log("adj ========================="+adj);
					
					BigDecimal adjAlreadyInvoicedQty = BigDecimal.ZERO;
					BigDecimal adjAlreadyInvoicedAmount = null;
					Map<String, Object> checkResult = dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
					adjAlreadyInvoicedAmount = (BigDecimal) checkResult.get("invoicedTotal");
					if(UtilValidate.isNotEmpty(checkResult.get("invoicedQty"))){
						adjAlreadyInvoicedQty = (BigDecimal) checkResult.get("invoicedQty");
					}
					
					tenPercent = (EntityUtil.getFirst(tenPercentItems)).get("amount");
					
					detCondsList = [];
					detCondsList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					detCondsList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
					  
					BigDecimal quota =BigDecimal.ZERO;
					
					List OrderItemDetailList = delegator.findList("OrderItemDetail", EntityCondition.makeCondition(detCondsList,EntityOperator.AND), UtilMisc.toSet("partyId","quotaQuantity","productId"), null, null, true);
					
					if(UtilValidate.isNotEmpty(OrderItemDetailList)){
						for(GenericValue OrderItemDetailValue : OrderItemDetailList){
							if(UtilValidate.isNotEmpty(OrderItemDetailValue.get("quotaQuantity"))){
								quota = OrderItemDetailValue.getBigDecimal("quotaQuantity");
								totalQuota = totalQuota.add(quota);
							}
						}
					}
					
					tenPercentAdjQty = totalQuota.subtract(adjAlreadyInvoicedQty);
					if(qty.compareTo(tenPercentAdjQty) < 0){
						tenPercentAdjQty = qty;
					}
					//tenPercent = (amount * -10)/100;
					
					if ( (adj.get("amount") != null) && (tenPercentAdjQty.compareTo(BigDecimal.ZERO) > 0) ) {
						// pro-rate the amount
						// set decimals = 100 means we don't round this intermediate value, which is very important
						
						//tenPercent = (adj.getBigDecimal("amount").divide(totalQuota, 100, ROUNDING)).setScale(2, ROUNDING);;
						
						tenPercent = adj.getBigDecimal("amount").doubleValue()/totalQuota.doubleValue();
						
						tenPercent = tenPercent.multiply(tenPercentAdjQty);
						// Tax needs to be rounded differently from other order adjustments
						/*if (adj.getString("orderAdjustmentTypeId").equals("SALES_TAX")) {
							amount = amount.setScale(TAX_DECIMALS, TAX_ROUNDING);
						} else {
							amount = amount.setScale(invoiceTypeDecimals, ROUNDING);
						}*/
					}
				}
				
				vatAmt = ((unitPrice*vatPercent)/100)*qty;
				cstAmt = ((unitPrice*cstPercent)/100)*qty;
				newObj.put("amount", amount);
				newObj.put("VatPercent", vatPercent);
				newObj.put("VAT", vatAmt);
				newObj.put("CSTPercent", cstPercent);
				newObj.put("tenPercent", tenPercent);
				newObj.put("usedQuota", tenPercentAdjQty);
				newObj.put("CST", cstAmt);
				newObj.put("saleAmount",amount + totalTaxAmt + totalItemAdjAmt + totalDiscAmt);
				
				
				//Debug.log("amount=============="+amount);
				
				//Debug.log("totalTaxAmt=============="+totalTaxAmt);
				
				//Debug.log("totalItemAdjAmt=============="+totalItemAdjAmt);
				
				//Debug.log("tenPercent=============="+tenPercent);
				
				//Debug.log("totalDiscAmt=============="+totalDiscAmt);
				
				
				newObj.put("totPayable",amount + totalTaxAmt + totalItemAdjAmt + tenPercent + totalDiscAmt + serviceChrg);
				
				
				taxList1 = [];
				taxList1.add("VAT");
				taxList1.add("CST");
				taxList1.add("VAT_SURCHARGE");
				taxList1.add("CST_SURCHARGE");
				if(scheme == "General")
				taxList1.add("SERVICE_CHARGE");
				if(scheme == "MGPS_10Pecent" && tenPercent != 0)
				taxList1.add("TEN_PERCENT_SUBSIDY");
				
					newObj.put("taxList1", taxList1);
				
				
				invoiceItemsJSON.add(newObj);
				
				JSONObject newObjProd = new JSONObject();
				newObjProd.put("value",OrderItem[0].productId);
				newObjProd.put("label",productName);
				//productItemsJSON.add(newObjProd);
				productIdLabelJSON.put(eachItem.productId, OrderItem[0].productId);
				productLabelIdJSON.put(prodValue.description, productName);
			
			
		}
		context.productIdLabelJSON = productIdLabelJSON;
		context.productLabelIdJSON = productLabelIdJSON;
		
		shipmentAttribute = delegator.findList("ShipmentAttribute", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
		////Debug.log("shipmentAttribute ========================="+shipmentAttribute);
		
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
		
		/*orderAdjustments.each{ eachOdrAdj ->
			tempMap = [:];
			adjTypeId = eachOdrAdj.orderAdjustmentTypeId;
			////Debug.log("adjTypeId ========================="+adjTypeId);
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
			if(eachOdrAdj.isAssessableValue && eachOdrAdj.isAssessableValue == "Y"){
				newObj.put("assessableValue", true);
			}
			if(!(adjTypeId == "COGS_DISC" || adjTypeId == "COGS_DISC_BASIC" || adjTypeId == "COGS_PCK_FWD" || adjTypeId == "COGS_INSURANCE")){
				adjustmentJSON.add(newObj);
			}
			
			
		}*/
		////Debug.log("adjustmentJSON============="+adjustmentJSON);
		//context.adjustmentJSON = adjustmentJSON;
	//}
}
context.invoiceItemsJSON = invoiceItemsJSON;

////Debug.log("invoiceItemsJSON============="+invoiceItemsJSON);