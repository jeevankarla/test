	
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

import org.ofbiz.party.contact.ContactMechWorker;


	purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
	purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
	purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");
	rounding = RoundingMode.FLOOR;
	shipmentId = parameters.shipmentId;
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
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
		condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		invoice = delegator.findList("Invoice", condition1, null, null, null, false);
		
		orderId = shipment.primaryOrderId;
		def sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		 effectiveDateBegin= UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse((String)shipment.createdDate).getTime()));
		 effectiveDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse((String)shipment.createdDate).getTime()));
		// //Debug.log("effectiveDateBegin========"+effectiveDateBegin+"effectiveDateEnd========"+effectiveDateEnd);
		if(!invoice && orderId){
			
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
			if(orderId){
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			}
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_ACCEPTED", "DOCUMENTS_ENDORSED"]));
			condition2 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			shipmentReceipts = delegator.findList("ShipmentReceipt", condition2, null, null, null, false);
			
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			
			
			exprCondList=[];
			exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
			EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
			OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
			
			
			actualOrderId = "";
			/*tallyRefNo = "";
			if(OrderAss){
				
				actualOrderId=OrderAss.toOrderId;
				
			}
			
			actualOrderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", actualOrderId), false);
			
			tallyRefNo = actualOrderHeader.tallyRefNo;
			
			context.tallyRefNo = tallyRefNo;*/
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN , UtilMisc.toList("SUPPLIER_AGENT","BILL_FROM_VENDOR","SHIP_TO_CUSTOMER","BILL_TO_CUSTOMER") ));
			condition3 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderRole = delegator.findList("OrderRole", condition3, null, null, null, false);
			
			partyId = "";
			
			billToPartyId="";
			weaverPartyId ="";
			if(orderRole){
				billToPartyIdList=EntityUtil.filterByCondition(orderRole, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
				if(billToPartyIdList){
					billToPartyId=(EntityUtil.getFirst(billToPartyIdList)).getString("partyId");
				}
				supplierPartyIdList=EntityUtil.filterByCondition(orderRole, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
				if(supplierPartyIdList){
					partyId = (EntityUtil.getFirst(supplierPartyIdList)).getString("partyId");
				}
				weaverPartyIdList=EntityUtil.filterByCondition(orderRole, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
				if(weaverPartyIdList){
					weaverPartyId = (EntityUtil.getFirst(weaverPartyIdList)).getString("partyId");
				}
			}
			context.weaverPartyId= weaverPartyId;
			invoiceTypeId = "";
			orderTypeId = orderHeader.orderTypeId;
			if(orderTypeId == "PURCHASE_ORDER"){
				invoiceTypeId = "PURCHASE_INVOICE";
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
			Debug.log("transactionTypeTaxMap =================="+transactionTypeTaxMap);
			context.transactionTypeTaxMap = transactionTypeTaxMap;
			
			Debug.log("supplier =================="+partyId);
			Debug.log("branch =================="+billToPartyId);
			
			String supplierGeoId = null;
			List supplierContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false, "TAX_CONTACT_MECH");
			if(UtilValidate.isNotEmpty(supplierContactMechValueMaps)){
				supplierGeoId = (String)((GenericValue) ((Map) supplierContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
			}
			
			String branchGeoId = null;
			List branchContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, billToPartyId, false, "TAX_CONTACT_MECH");
			if(UtilValidate.isNotEmpty(branchContactMechValueMaps)){
				branchGeoId = (String)((GenericValue) ((Map) branchContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
			}
			
			Debug.log("supplierGeoId =================" +supplierGeoId);
			Debug.log("branchGeoId ================" +branchGeoId);
			
			context.supplierGeoId = supplierGeoId;
			context.branchGeoId = branchGeoId;
			
			
			orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			
			purchaseTaxType = null;
			purchaseTitleTransferEnumId = null;
			orderAttr.each{ eachAttr ->
				if(eachAttr.attrName == "purchaseTaxType"){
					purchaseTaxType =  eachAttr.attrValue;
				}
				if(eachAttr.attrName == "purchaseTitleTransferEnumId"){
					purchaseTitleTransferEnumId = eachAttr.attrValue;
				}
			}
			if(UtilValidate.isEmpty(purchaseTitleTransferEnumId)){
				if(supplierGeoId == branchGeoId){
					purchaseTaxType = "Intra-State";
					purchaseTitleTransferEnumId = "NO_E2_FORM";
				}
				else{
					purchaseTaxType = "Inter-State";
					purchaseTitleTransferEnumId = "CST_CFORM";
				}
			}
			
			context.purchaseTaxType = purchaseTaxType;
			context.purchaseTitleTransferEnumId = purchaseTitleTransferEnumId;
			
			purTaxList = transactionTypeTaxMap.get(purchaseTitleTransferEnumId);
			context.purTaxListReady = purTaxList;
			
			
			
			
			
			//invoiceItemAdjs = delegator.findList("InvoiceItemTypeMap", EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId), null, null, null, false);
			//adjIds = EntityUtil.getFieldListFromEntityList(invoiceItemAdjs, "invoiceItemTypeId", true);
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, ["TEN_PER_CHARGES","TEN_PER_DISCOUNT"]));
			condit = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			
			invoiceItemTypes = delegator.findList("InvoiceItemType", condit, null, null, null, false);
			////Debug.log("invoiceItemTypes =========="+invoiceItemTypes);
			additionalChgs = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
			dicounts = EntityUtil.filterByCondition(invoiceItemTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISCOUNTS"));
			Debug.log("additionalChgs =========="+additionalChgs);
			////Debug.log("dicounts =========="+dicounts);
			
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
			Debug.log("invoiceAdjItemsJSON =========="+invoiceAdjItemsJSON);
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
			
			
			
			
			context.orderId = orderId;
			context.partyId = partyId;
			context.billToPartyId = billToPartyId;
			context.shipmentDate = shipment.estimatedShipDate;
			context.vehicleId = shipment.vehicleId;
			
			products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
			
			condExpr = [];
			condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_IN, UtilMisc.toList("BED_PUR", "VAT_PUR","CST_PUR", "BEDCESS_PUR", "BEDSECCESS_PUR", "CESS_PUR")));
			cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
			orderAdjustments = delegator.findList("OrderAdjustment", cond, null, null, null, false);
			
			prodQty = [];
			adjustmentTypes = [];
			
			otherCharges = [];
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
			}
			
			//Debug.log("orderItems=================="+orderItems);
			
			
			productQty = [];
			orderItems.each{ eachItem ->
				
				Debug.log("orderId =========="+eachItem.orderId);
				taxResultCtx = 0;
				taxValueMap = [:];
				defaultTaxMap = [:];
				if( (UtilValidate.isNotEmpty(supplierGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
					Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);
					prodCatTaxCtx.put("productId", eachItem.productId);
					prodCatTaxCtx.put("taxAuthGeoId", branchGeoId);
					
					taxResultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",prodCatTaxCtx);
					taxValueMap = taxResultCtx.get("taxValueMap");
					defaultTaxMap = taxResultCtx.get("defaultTaxMap");
				}
				
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
				if(UtilValidate.isNotEmpty(orderId)){
					List conditionlist=[];
					conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
					conditionlist.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
					conditionlist.add(EntityCondition.makeCondition("changeTypeEnumId", EntityOperator.EQUALS, "ODR_ITM_AMEND"));
					conditionlist.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO, shipment.createdDate));
					EntityCondition conditionMain1=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
					def orderBy = UtilMisc.toList("changeDatetime");
					OrderItemChangeDetails = delegator.findList("OrderItemChange", conditionMain1 , null ,orderBy, null, false );
					//Debug.log("OrderItemChangeDetails================="+OrderItemChangeDetails);
					
					if(OrderItemChangeDetails)
					OrderItemChangeDetails=(OrderItemChangeDetails).getLast();
					
					//Debug.log("OrderItemChangeDetails================="+OrderItemChangeDetails);
					
					if(UtilValidate.isNotEmpty(OrderItemChangeDetails)){
						tempMap.put("UPrice",OrderItemChangeDetails.unitPrice);
					}
				}
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
			
			//Debug.log("resultCtx=================="+resultCtx);
			
			
			JSONObject productIdLabelJSON = new JSONObject();
			JSONObject productLabelIdJSON=new JSONObject();
			
			condExpr = [];
			condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, UtilMisc.toList("VAT_PUR","CST_PUR","CST_SURCHARGE","VAT_SURCHARGE")));
			cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
			taxDetails = delegator.findList("OrderAdjustment", cond, null, null, null, false);
			Debug.log("taxDetails=================="+taxDetails);
			
			
			
			shipmentReceipts.each{ eachItem ->
				
				relOrderItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				origQty = (relOrderItem.get(0)).get("quantity");
				
				String productId = eachItem.productId;
				adjUnitAmtMap = [:];
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
				}
				qty = eachItem.quantityAccepted;
				
				
				condExpr.clear();
				condExpr.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachItem.orderId));
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				cond = EntityCondition.makeCondition(condExpr, EntityOperator.AND);
	
				ordItem = EntityUtil.filterByCondition(orderItems, cond);
				
				orderItem = EntityUtil.getFirst(ordItem);
				
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
				condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "CST_PUR"));
				cstItems = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
				
				if(UtilValidate.isNotEmpty(cstItems)){
					cstPercent = (EntityUtil.getFirst(cstItems)).get("sourcePercentage");
				}
				
				
				vatAmt = BigDecimal.ZERO;
				cstAmt = BigDecimal.ZERO;*/
				
				unitPrice = (orderItem.unitPrice);
				
				JSONObject newObj = new JSONObject();
				newObj.put("cProductId",eachItem.productId);
				newObj.put("cProductName",prodValue.description);
				newObj.put("quantity",qty);
				newObj.put("UPrice", unitPrice);
				
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
				if(purchaseTitleTransferEnumId){
					//purTaxList = transactionTypeTaxMap.get(purchaseTitleTransferEnumId);
					for(int i=0; i<purTaxList.size(); i++){
						taxItem = purTaxList.get(i);
						Debug.log("taxItem ============="+taxItem);
						purTaxItem = taxItem.replace("_SALE", "_PUR");
						
						Debug.log("taxItemtaxItem ============="+taxItem);
						taxInfo = defaultTaxMap.get(taxItem);
						surChargeList = taxInfo.get("surchargeList");
						
						condExpr = [];
						condExpr.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
						condExpr.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, purTaxItem));
						taxItemList = EntityUtil.filterByCondition(taxDetails, EntityCondition.makeCondition(condExpr, EntityOperator.AND));
						Debug.log("taxItemList ============="+taxItemList);
						taxPercent = 0;
						taxValue = 0;
						actualTaxValue = 0;
						if(UtilValidate.isNotEmpty(taxItemList)){
							taxPercent = (EntityUtil.getFirst(taxItemList)).get("sourcePercentage");
							actualTaxValue = (EntityUtil.getFirst(taxItemList)).get("amount");
							taxValue = (actualTaxValue/origQty)*qty;
						}
						Debug.log("taxValue ============="+taxValue);
						newObj.put(taxItem+"_PUR", taxPercent);
						newObj.put(taxItem+"_PUR_AMT" , taxValue);
						
						totalTaxAmt = totalTaxAmt + taxValue;
						Debug.log("totalTaxAmt ============="+totalTaxAmt);
						for(int j=0; j<surChargeList.size(); j++){
							surchargeItem = (surChargeList.get(j)).get("taxAuthorityRateTypeId");
							Debug.log("surchargeItem ============="+surchargeItem);
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
							newObj.put(surchargeItem+"_PUR_AMT" , surTaxValue);
							
							totalTaxAmt = totalTaxAmt + surTaxValue;
							
						}
						
					}
				}
				
				newObj.put("taxAmt", totalTaxAmt);
				amount = unitPrice*qty  ;
				/*vatAmt = ((unitPrice*vatPercent)/100)*qty;
				cstAmt = ((unitPrice*cstPercent)/100)*qty;*/
				newObj.put("amount", amount);
				/*newObj.put("VatPercent", vatPercent);
				newObj.put("VAT", vatAmt);
				newObj.put("CSTPercent", cstPercent);
				newObj.put("CST", cstAmt);*/
				invoiceItemsJSON.add(newObj);
				
				JSONObject newObjProd = new JSONObject();
				newObjProd.put("value",eachItem.productId);
				newObjProd.put("label",prodValue.description);
				//productItemsJSON.add(newObjProd);
				productIdLabelJSON.put(eachItem.productId, prodValue.description);
				productLabelIdJSON.put(prodValue.description, eachItem.productId);
			}
			context.productIdLabelJSON = productIdLabelJSON;
			context.productLabelIdJSON = productLabelIdJSON;
			
			//Debug.log("productIdLabelJSON=================="+productIdLabelJSON);
			
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
				
							
				if(UtilValidate.isEmpty(applicableTo) || applicableTo == "_NA_"){
					applicableTo = "ALL";
				}
				else{
					originalOrderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", applicableTo));
					applicableTo = originalOrderItem.get("itemDescription");
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
				newObj.put("applicableTo", applicableTo);
				newObj.put("adjAmount", totalAdjAmt.setScale(0, rounding));
				if(eachOdrAdj.isAssessableValue && eachOdrAdj.isAssessableValue == "Y"){
					newObj.put("assessableValue", true);
				}
				if(!(adjTypeId == "COGS_DISC" || adjTypeId == "COGS_DISC_BASIC" || adjTypeId == "COGS_PCK_FWD" || adjTypeId == "COGS_INSURANCE")){
					adjustmentJSON.add(newObj);
				}
				
				
			}
			
			
			context.adjustmentJSON = adjustmentJSON;
		}
	}
	context.invoiceItemsJSON = invoiceItemsJSON;