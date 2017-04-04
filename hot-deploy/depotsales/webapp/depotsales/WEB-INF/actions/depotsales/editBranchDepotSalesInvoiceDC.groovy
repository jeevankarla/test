
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

invoiceId= parameters.invoiceId;

if(invoiceId){

	context.invoiceId= invoiceId;
	billToPartyId= parameters.partyId;
	
	context.billToPartyId = billToPartyId;
	
	partyName= parameters.partyName;
	
	invoiceList = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
	partyId = invoiceList.get("partyId");
	shipmentId = invoiceList.get("shipmentId");
	
	branchPartyId = invoiceList.get("costCenterId");
	context.branchPartyId = branchPartyId;
	
	////////Debug.log("invoDate================"+invoDate);
	invoDate = "";
	if(invoiceList.get("invoiceDate")){
		invoDate = invoiceList.get("invoiceDate");
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy");
		invoDate = formatter.format(invoDate);
	}

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
	
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	Shipmentlist = delegator.findList("Shipment", cond, null, null, null, false);
	if(Shipmentlist){
		shiplist=EntityUtil.getFirst(Shipmentlist);
			supplierInvoiceDate=shiplist.supplierInvoiceDate;
			milliseconds=(shiplist.supplierInvoiceDate).getTime();
	}
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
	shipmentDate = sdf.format(supplierInvoiceDate);
	context.shipmentDate=shipmentDate;
	context.milliseconds=milliseconds;
	
orderAttr = delegator.findList("InvoiceAttribute", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId), null, null, null, false);
	
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
	
	invoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]), null, null, null, false);
	//////Debug.log("invoiceItemTypes =========="+invoiceItemTypes);
	additionalChgs = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
	dicounts = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISCOUNTS"));
	
	additionalChgTypeIdsList = EntityUtil.getFieldListFromEntityList(additionalChgs, "invoiceItemTypeId", true);
	discountTypeIdsList = EntityUtil.getFieldListFromEntityList(dicounts, "invoiceItemTypeId", true);
	
	
	
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
			
		context.tallyRefNo = tallyRefNo;
		JSONArray invoiceItemsJSON = new JSONArray();

		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		invoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);

		invoiceItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
		invoiceAdjItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, ["INV_FPROD_ITEM","TEN_PERCENT_SUBSIDY","VAT_PUR", "CST_PUR","CST_SALE","VAT_SALE","CESS_SALE","CESS_PUR","VAT_SURHARGE","TEN_PER_CHARGES","TEN_PER_DISCOUNT"]));

		orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

		//////Debug.log("orderAttr==================="+orderAttr);
		scheme = "";
		if(UtilValidate.isNotEmpty(orderAttr)){
			orderAttr.each{ eachAttr ->
				if(eachAttr.attrName == "SCHEME_CAT"){
					scheme =  eachAttr.attrValue;
				}
			}
		}
		context.scheme = scheme;

		/*condExpr = [];
		condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
		orderAdjustForTen = delegator.findList("OrderAdjustment", cond, UtilMisc.toSet("orderId","amount"), null, null, false);

		tenperValue = 0;
		if(orderAdjustForTen){
			amount=(EntityUtil.getFirst(orderAdjustForTen)).getString("amount");
			tenperValue = Double.valueOf(amount);
		}

		context.tenperValue = tenperValue;*/


		for (eachItem in invoiceItemList) {
			
			taxValueMap = [:];
			defaultTaxMap = [:];
			/*//if( (UtilValidate.isNotEmpty(customerGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
				Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);
				prodCatTaxCtx.put("productId", eachItem.productId);
				prodCatTaxCtx.put("taxAuthGeoId", branchGeoId);
				
				taxResultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",prodCatTaxCtx);
				taxValueMap = taxResultCtx.get("taxValueMap");
				defaultTaxMap = taxResultCtx.get("defaultTaxMap");
			//}
			*/
				
				
				resultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",UtilMisc.toMap("userLogin",userLogin, "taxAuthGeoId", "IN-UP","taxAuthorityRateTypeId","CST_SALE","productId",eachItem.productId));
				
				defaultTaxMap = resultCtx.defaultTaxMap;
				
				taxValueMap = resultCtx.taxValueMap;
				
			
			
			JSONObject newObj = new JSONObject();
			newObj.put("cProductId",eachItem.productId);
			newObj.put("invoiceItemSeqId",eachItem.invoiceItemSeqId);
			
			
			newObj.put("taxValueMap",taxValueMap);
			newObj.put("defaultTaxMap",defaultTaxMap);
			
			productName = ""
			prod=delegator.findOne("Product",[productId:eachItem.productId],false);
			if(UtilValidate.isNotEmpty(prod)){
				productName = prod.get("productName");
			}
			
			totalTaxAmt = 0;
			
			condExpr = [];
			condExpr.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
			condExpr.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
			tenPercentItems = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
			
			if(saleTitleTransferEnumId){
				//purTaxList = transactionTypeTaxMap.get(purchaseTitleTransferEnumId);
				for(int i=0; i<taxList.size(); i++){
					taxItem = taxList.get(i);
					////Debug.log("taxItemtaxItem ============="+taxItem);
					taxInfo = defaultTaxMap.get(taxItem);
					surChargeList = taxInfo.get("surchargeList");
					
					condExpr = [];
					condExpr.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
					condExpr.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, taxItem));
					////Debug.log("condExpr ============="+condExpr);
					taxItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
					
					taxPercent = 0;
					taxValue = 0;
					actualTaxValue = 0;
					if(UtilValidate.isNotEmpty(taxItemList)){
						taxValue = (EntityUtil.getFirst(taxItemList)).get("amount");
						taxPercent = (taxValue*100)/(eachItem.amount*eachItem.quantity);
						//taxValue = (actualTaxValue/origQty)*qty;
					}
					
					taxItem = taxItem.replace("SALE", "PUR");
					
					//Debug.log("taxItem ============="+taxItem);
					
					
					newObj.put(taxItem, taxPercent);
					newObj.put(taxItem+"_AMT", taxValue);
					
					totalTaxAmt = totalTaxAmt + taxValue;
					
					
					
					
					if(taxValue > 0){
						////Debug.log("totalTaxAmt ============="+totalTaxAmt);
						for(int j=0; j<surChargeList.size(); j++){
							surchargeItem = (surChargeList.get(j)).get("taxAuthorityRateTypeId");
							//Debug.log("surchargeItem ============="+surchargeItem);
							condExpr = [];
							condExpr.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
							condExpr.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, surchargeItem));
							surItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
							
							surTaxPercent = 0;
							surTaxValue = 0;
							if(UtilValidate.isNotEmpty(surItemList)){
								//surTaxPercent = (EntityUtil.getFirst(surItemList)).get("sourcePercentage");
								surTaxValue = (EntityUtil.getFirst(surItemList)).get("amount");
								surTaxPercent = (surTaxValue*100)/(taxValue);
							}
							newObj.put(surchargeItem+"_PUR", surTaxPercent);
							newObj.put(surchargeItem+"_PUR_AMT", surTaxValue);
							
							totalTaxAmt = totalTaxAmt + surTaxValue;
							
						}
					}
					
					
					
				}
			}
			
			////Debug.log("totalTaxAmt =========="+totalTaxAmt);
			
			newObj.put("taxAmt", totalTaxAmt);
			
			
			totalItemAdjAmt = 0;
			incBaseAmt = 0;
			
			JSONArray itemAdjustmentJSON = new JSONArray();
			
			
			////Debug.log("additionalChgTypeIdsList ============="+additionalChgTypeIdsList);
			
			
			
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
				
				
				conditionList = [];
				conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
				itemAdditionalChgs = [];
				if(UtilValidate.isNotEmpty(invoiceAdjItemList)){
					itemAdditionalChgs = EntityUtil.filterByCondition(invoiceAdjItemList, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
				}
				
			//Debug.log("itemAdditionalChgs ============="+itemAdditionalChgs);
				
				
				
				if(UtilValidate.isNotEmpty(itemAdditionalChgs)){
					itemOrdAdj = EntityUtil.filterByCondition(itemAdditionalChgs, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invItemTypeId));
					
					if(itemOrdAdj)
					//Debug.log("itemOrdAdj =====3232========"+itemOrdAdj[0].invItemTypeId);
					
					
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
						
						//Debug.log("invItemTypeId ========================= "+invItemTypeId);
						
						
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
				conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
				itemAdditionalChgs = [];
				if(UtilValidate.isNotEmpty(invoiceAdjItemList)){
					itemAdditionalChgs = EntityUtil.filterByCondition(invoiceAdjItemList, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
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
				
				
	
			////Debug.log("discItemAdjustmentJSON ========================= "+discItemAdjustmentJSON);
			
			newObj.put("additionalChgTypeIdsList", additionalChgTypeIdsList);
			newObj.put("discountTypeIdsList", discountTypeIdsList);
			
			newObj.put("itemAdjustments",itemAdjustmentJSON);
			newObj.put("discItemAdjustments",discItemAdjustmentJSON);
			newObj.put("OTH_CHARGES_AMT",totalItemAdjAmt);
			newObj.put("DISCOUNT_AMT",totalDiscAmt);
			newObj.put("incBaseAmt",incBaseAmt);
			
			
			tenPercent = 0;
			usedQuota = 0;
			/*if(scheme == "MGPS_10Pecent" && tenperValue != 0){
				amount = (Double.valueOf((eachItem.quantity))*(Double.valueOf(eachItem.amount)));
				tenPercent = (amount * -10)/100;
			}*/
			
			
			if(UtilValidate.isNotEmpty(tenPercentItems)){
				
				tenPercItem = EntityUtil.getFirst(tenPercentItems);
				
				tenPercent = tenPercItem.get("amount");
				tenPercSeq = tenPercItem.get("invoiceItemSeqId");
				
				List tenPercCond = [];
				tenPercCond.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
				tenPercCond.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, tenPercSeq));
				tenPercAdjBilling = delegator.findList("OrderAdjustmentBilling", EntityCondition.makeCondition(tenPercCond, EntityOperator.AND), null, null, null, false);
				
				if(UtilValidate.isNotEmpty(tenPercAdjBilling)){
					usedQuota = (EntityUtil.getFirst(tenPercAdjBilling)).get("quantity");
				}
				
			}
			
			
			newObj.put("cProductName",productName);
			newObj.put("quantity",eachItem.quantity);
			newObj.put("invoiceItemSeqId",eachItem.invoiceItemSeqId);
			newObj.put("UPrice", eachItem.amount);
			newObj.put("amount", (eachItem.amount)*(eachItem.quantity));
			newObj.put("tenPercent", tenPercent);
			newObj.put("usedQuota", usedQuota);
			newObj.put("VatPercent", 0.00);
			newObj.put("VAT", 0.00);
			newObj.put("CSTPercent", 0.00);
			newObj.put("CST", 0.00);
			
			
			condExpr = [];
			//condExpr.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
			condExpr.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INVOICE_ITM_ADJ"));
			condExpr.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, "Service Charge"));
			////Debug.log("condExpr ============="+condExpr);
			serviceChrAmt = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition(condExpr, EntityOperator.AND));

			serviceChgValue = 0;
			serviceChgPercent = 0;
			if(UtilValidate.isNotEmpty(serviceChrAmt)){
				
				serviceTot = (eachItem.itemValue + totalTaxAmt  + totalItemAdjAmt+totalDiscAmt+tenPercent);
				
				
				serviceChgValue = (EntityUtil.getFirst(serviceChrAmt)).get("amount");
				serviceChgPercent = (serviceChgValue)/(serviceTot);
				
				serviceChgPercent = serviceChgPercent*100;
				
				//taxValue = (actualTaxValue/origQty)*qty;
			}
			newObj.put("SERVICE_CHARGE_AMT", serviceChgValue);
			newObj.put("SERVICE_CHARGE", Math.round(serviceChgPercent));
			
			
			
		}
			
			
		//	totPayable = (eachItem.itemValue + totalTaxAmt + serviceChgValue + totalItemAdjAmt+totalDiscAmt+tenPercent);
			
		//	newObj.put("totPayable", totPayable);
			/*totPayable = (amount + totalTaxAmt + serviceChg + adjustmentTotal+tenPercent);
			
			newObj.put("totPayable", totPayable);*/
			
			newObj.put("saleAmount",eachItem.itemValue + totalTaxAmt + totalItemAdjAmt + totalDiscAmt);
			
			
			////Debug.log("amount=============="+amount);
			
			////Debug.log("totalTaxAmt=============="+totalTaxAmt);
			
			////Debug.log("totalItemAdjAmt=============="+totalItemAdjAmt);
			
			////Debug.log("tenPercent=============="+tenPercent);
			
			////Debug.log("totalDiscAmt=============="+totalDiscAmt);
			
			
			newObj.put("totPayable",eachItem.itemValue + totalTaxAmt + totalItemAdjAmt + tenPercent + totalDiscAmt + serviceChgValue);
			
			
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

		}
		
		context.invoiceItemsJSON = invoiceItemsJSON;

		
		
		/*
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
		
		////Debug.log("additionalChgs ================="+additionalChgs);
		
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

			
		disCountFlag = "";
		
		JSONArray invoiceDiscountJSON = new JSONArray();
		JSONArray invoiceAdditionalJSON = new JSONArray();
		
		if(invoiceAdjItemList){
			invoiceAdjItemList.each{eachItem ->
			   
				JSONObject newObj = new JSONObject();
				newObj.put("invoiceItemTypeId",eachItem.invoiceItemTypeId);
				newObj.put("applicableTo",eachItem.description);
			   
				amount = 0;
				if(eachItem.amount){
					amount = eachItem.amount;
				}
				
				//De000000000bug.log("eachItem.amount============="+eachItem.amount);
			   
				newObj.put("adjAmount",Math.abs((amount*eachItem.quantity)));
				newObj.put("discQty",eachItem.quantity);
			   
				if(amount > 0)
					invoiceAdditionalJSON.add(newObj);
				else
					invoiceDiscountJSON.add(newObj);
			   
			}
		}
		else{
			disCountFlag = "N";
		}
	
 
		context.disCountFlag = disCountFlag;
	
	   //////Debug.log("invoiceDiscountJSON================="+invoiceDiscountJSON);
	   //////Debug.log("invoiceAdditionalJSON================="+invoiceAdditionalJSON);
	   context.invoiceDiscountJSON = invoiceDiscountJSON;
	   context.invoiceAdditionalJSON = invoiceAdditionalJSON;*/

}

