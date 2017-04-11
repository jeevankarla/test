
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilNumber;

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

import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.contact.ContactMechWorker;

import java.util.Map.Entry;

purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");

consList=[];
consList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
condEXr = EntityCondition.makeCondition(consList, EntityOperator.AND);
orderItemAttr = delegator.findList("OrderItemAttribute", condEXr, null, null, null, false);

orderItemDetails = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

condist=[];
condist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
expr = EntityCondition.makeCondition(condist, EntityOperator.AND);
partyOrders = delegator.findList("OrderRole", expr, null, null, null, false);

orderType="direct";
onbehalfof = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ON_BEHALF_OF"));
if(onbehalfof){
	orderType = "onbehalfof";
}
context.orderType=orderType;

orderEditParamMap = [:];
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
orderDateToCompPO=(orderHeader.orderDate).getTime();;

context.orderDateToCompPO=orderDateToCompPO;
//Debug.log("orderDateToCompPO =================" +orderDateToCompPO);
orderAdjustments = [];
additionalChgs = [];
if(orderHeader && orderHeader.statusId != "ORDER_COMPLETED"){
	
	orderInfoDetail = [:];
	orderInfoDetail.putAt("orderId", orderHeader.orderId);
	orderInfoDetail.putAt("orderName", orderHeader.orderName);
	orderInfoDetail.putAt("orderDate", UtilDateTime.toDateString(orderHeader.orderDate, "dd MMMMM, yyyy"));
	orderInfoDetail.putAt("orderTypeId", orderHeader.orderTypeId);
	estDeliveryDate = "";
	if(orderHeader.estimatedDeliveryDate){
		estDeliveryDate = UtilDateTime.toDateString(orderHeader.estimatedDeliveryDate, "dd MMMMM, yyyy")
	}
	orderInfoDetail.putAt("estimatedDeliveryDate", estDeliveryDate);
	orderInfoDetail.putAt("PONumber", orderHeader.externalId);
	orderInfoDetail.putAt("productStoreId", orderHeader.productStoreId);
	orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	fileNo = "";
	refNo = "";
	thruDate = "";
	fromDate = "";
	quotationNo = "";
	indentShipmentAddress = "";
	orderAttr.each{ eachAttr ->
		if(eachAttr.attrName == "FILE_NUMBER"){
			fileNo =  eachAttr.attrValue;
		}
		if(eachAttr.attrName == "REF_NUMBER"){
			refNo = eachAttr.attrValue;
		}
		if(eachAttr.attrName == "QUOTATION_NUMBER"){
			quotationNo = eachAttr.attrValue;
		}
		if(eachAttr.attrName == "VALID_FROM"){
			fromDate = eachAttr.attrValue;
		}
		if(eachAttr.attrName == "VALID_THRU"){
			indentShipmentAddress = eachAttr.attrValue;
		}
		if(eachAttr.attrName == "SHIPPING_PREF"){
			indentShipmentAddress = eachAttr.attrValue;
		}

	}
	orderInfoDetail.putAt("fileNo", fileNo);
	orderInfoDetail.putAt("refNo", refNo);
	orderInfoDetail.putAt("quotationNo", quotationNo);
	orderInfoDetail.putAt("validFromDate", fromDate);
	orderInfoDetail.putAt("validThruDate", thruDate);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SUPPLIER", "BILL_FROM_VENDOR","SHIP_TO_CUSTOMER")));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
	
	//orderRole = EntityUtil.getFirst(orderRoles);
	if(orderRoles){
		roleCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPPLIER")],EntityOperator.AND);
		orderRole=EntityUtil.filterByCondition(orderRoles,roleCondition);
		supplierRole = EntityUtil.getFirst(orderRole);
		//partyName = PartyHelper.getPartyName(delegator, supplierRole.partyId, false);
		//orderInfoDetail.putAt("supplierId", supplierRole.partyId);
		//orderInfoDetail.putAt("supplierName", partyName);
		
		vendorCond=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"BILL_FROM_VENDOR")],EntityOperator.AND);
		vendOrderRole=EntityUtil.filterByCondition(orderRoles,vendorCond);
		vendorRole=EntityUtil.getFirst(vendOrderRole);
		orderInfoDetail.putAt("billToPartyId", vendorRole.partyId);
		
		shipToCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SHIP_TO_CUSTOMER")],EntityOperator.AND);
		shipToPartyRole=EntityUtil.filterByCondition(orderRoles,shipToCondition);
		shipToParty=EntityUtil.getFirst(shipToPartyRole);
		shipingAdd=[:];
		if(shipToParty.partyId){
		
			conditionListAddress = [];
			conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, shipToParty.partyId));
			conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
			conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
			contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
		
			if(indentShipmentAddress){
		
				conditionListAddress.clear();
				conditionListAddress.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, indentShipmentAddress));
				conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
				conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
				listAddressList = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
				
				listAddress = EntityUtil.getFirst(listAddressList);
				JSONObject tempMap = new JSONObject();
		
				if(listAddress){
			
					if(listAddress.address1)
					   shipingAdd.put("address1",listAddress.address1);
					else
					   shipingAdd.put("address1","");
			  
					if(listAddress.address2)
					   shipingAdd.put("address2",listAddress.address2);
					else
					   shipingAdd.put("address2","");
			  
					if(listAddress.country)
						shipingAdd.put("country",listAddress.country);
					else
						shipingAdd.put("country","");
			  
					if(listAddress.state)
						shipingAdd.put("state",listAddress.state);
					else
						shipingAdd.put("state","");
			  
					if(listAddress.city)
						shipingAdd.put("city",listAddress.city);
					else
						shipingAdd.put("city","");
			  
					if(listAddress.postalCode)
						shipingAdd.put("postalCode",listAddress.postalCode);
					else
						shipingAdd.put("postalCode","");
		   
				}
			  
			}else if(contactMechesDetails){
				contactMec=contactMechesDetails.getFirst();
				if(contactMec){
					partyPostalAddress=contactMec;
					if(partyPostalAddress){
						address1="";
						address2="";
						state="";
						city="";
						postalCode="";
						districtGeoId="";
						if(partyPostalAddress.get("address1")){
							address1=partyPostalAddress.get("address1");
						}
						if(partyPostalAddress.get("address2")){
							address2=partyPostalAddress.get("address2");
						}
						if(partyPostalAddress.get("city")){
							city=partyPostalAddress.get("city");
						}
						if(partyPostalAddress.get("state")){
							state=partyPostalAddress.get("state");
						}
						if(partyPostalAddress.get("postalCode")){
							postalCode=partyPostalAddress.get("postalCode");
						}
						if(partyPostalAddress.get("districtGeoId")){
							districtGeoId=partyPostalAddress.get("districtGeoId");
						}
						String districtName="";
						if(districtGeoId){
							districtName = districtGeoId ;
							GenericValue geo = delegator.findOne("Geo", [geoId:districtGeoId], false);
							if(UtilValidate.isNotEmpty(geo)&& UtilValidate.isNotEmpty(geo.get("geoName"))){
								districtName = geo.get("geoName");
							}
						}
						shipingAdd.put("address1",address1);
						shipingAdd.put("address2",address2);
						shipingAdd.put("city",city);
						shipingAdd.put("districtGeoId",districtName);
						shipingAdd.put("postalCode",postalCode);
					}
				}
			}
		}
		
		context.shipingAdd=shipingAdd;
		
		orderInfoDetail.putAt("shipToPartyId", shipToParty.partyId);
		
		shipParty=PartyHelper.getPartyName(delegator, shipToParty.partyId, false)
		orderInfoDetail.putAt("shipToPartyName", shipParty+"["+shipToParty.partyId+"]");
		
	}
	branchId = orderInfoDetail.get("billToPartyId");
	supplierId = orderInfoDetail.get("supplierId");
	
	//Debug.log("branchId =================" +branchId);
	//Debug.log("supplierId ================" +supplierId);
	
	String supplierGeoId = null;
	List supplierContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, supplierId, false, "TAX_CONTACT_MECH");
	if(UtilValidate.isNotEmpty(supplierContactMechValueMaps)){
		supplierGeoId = (String)((GenericValue) ((Map) supplierContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
	}
	
	String branchGeoId = null;
	List branchContactMechValueMaps = (List) ContactMechWorker.getPartyContactMechValueMaps(delegator, branchId, false, "TAX_CONTACT_MECH");
	if(UtilValidate.isNotEmpty(branchContactMechValueMaps)){
		branchGeoId = (String)((GenericValue) ((Map) branchContactMechValueMaps.get(0)).get("contactMech")).get("infoString");
	}
	
	//Debug.log("supplierGeoId =================" +supplierGeoId);
	//Debug.log("branchGeoId ================" +branchGeoId);
	
	orderInfoDetail.putAt("supplierGeoId", supplierGeoId);
	orderInfoDetail.putAt("branchGeoId", branchGeoId);
	
	String purchaseTitleTransferEnumId = "CST_CFORM";
	orderInfoDetail.putAt("purchaseTaxType", "Inter-State");
	if(supplierGeoId == branchGeoId){
		orderInfoDetail.putAt("purchaseTaxType", "Intra-State");
		purchaseTitleTransferEnumId = "NO_E2_FORM";
	}
	else{
		orderInfoDetail.putAt("purchaseTaxType", "Inter-State");
		purchaseTitleTransferEnumId = "CST_CFORM";
	}
	orderInfoDetail.putAt("purchaseTitleTransferEnumId", purchaseTitleTransferEnumId);
	context.purchaseTitleTransferEnumId = purchaseTitleTransferEnumId;
	
	//Debug.log("orderId =================="+orderId);
	orderEditParamMap.putAt("orderHeader", orderInfoDetail);
	orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	//Debug.log("orderAdjustments =================="+orderAdjustments);
	
	/*orderAdjDetail = [:];
	orderAdjustments.each{ eachAdj ->
		if(eachAdj.orderAdjustmentTypeId == "COGS_FREIGHT"){
			orderAdjDetail.putAt("freightCharges", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_DISC"){
			orderAdjDetail.putAt("discount", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_INSURANCE"){
			orderAdjDetail.putAt("insurence", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_PCK_FWD"){
			orderAdjDetail.putAt("packAndFowdg", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_OTH_CHARGES"){
			orderAdjDetail.putAt("otherCharges", eachAdj.amount);
		}
		
	}
	orderEditParamMap.put("orderAdjustment", orderAdjDetail);*/
	
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	/*quoteNo = EntityUtil.getFirst(orderItems);
	quoteDetailMap = [:];
	quoteDate = delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteNo.quoteId), false);
		if(quoteNo)
		 {
		quoteDetailMap.putAt("quoteId", quoteNo.quoteId);
		 }
		if(quoteDate)
		 {
		quoteDetailMap.putAt("quoteIssueDate", UtilDateTime.toDateString(quoteDate.issueDate, "dd MMMMM, yyyy"));
		 }
	orderEditParamMap.put("quoteDetails", quoteDetailMap);*/
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	//quoteIds = EntityUtil.getFieldListFromEntityList(orderItems, "quoteId", true);
	
	/*termTypes = delegator.findList("TermType", null, null, null, null, false);
	
	paymentTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "FEE_PAYMENT_TERM"));
	paymentTermTypeIds = EntityUtil.getFieldListFromEntityList(paymentTermTypes, "termTypeId", true);

	deliveryTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DELIVERY_TERM"));
	deliveryTermTypeIds = EntityUtil.getFieldListFromEntityList(deliveryTermTypes, "termTypeId", true);
	
	otherTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "OTHERS"));
	otherTermTypeIds = EntityUtil.getFieldListFromEntityList(otherTermTypes, "termTypeId", true);
	
	orderTerms = [:];
	List<GenericValue> terms = [];
	terms = delegator.findList("OrderTerm", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	termIds = EntityUtil.getFieldListFromEntityList(terms, "termTypeId", true);
	if(termIds.contains("INC_TAX")){
		context.includeTax="Y";
	}
	termTypesForDesc = delegator.findList("TermType", EntityCondition.makeCondition("termTypeId", EntityOperator.IN, termIds), null, null, null, false);
	
	termDescMap = [:];
	termTypesForDesc.each{ eachTermItem ->
		termDescMap.put(eachTermItem.termTypeId, eachTermItem.description);
	}
	
	paymentTerms = [];
	deliveryTerms = [];
	otherTerms = [];
	terms.each{ eachTerm ->
		termMap = [:];
		termMap.put("termTypeId", eachTerm.termTypeId);
		termMap.put("termTypeDescription", termDescMap.get(eachTerm.termTypeId));
		termMap.put("termValue", eachTerm.termValue);
		termMap.put("sequenceId", eachTerm.orderItemSeqId);
		termMap.put("termDays", eachTerm.termDays);
		termMap.put("description", eachTerm.description);
		termMap.put("uomId", eachTerm.uomId);
		if(paymentTermTypeIds.contains(eachTerm.termTypeId)){
			paymentTerms.add(termMap);
		}
		if(deliveryTermTypeIds.contains(eachTerm.termTypeId)){
			deliveryTerms.add(termMap);
		}
		
		if(otherTermTypeIds.contains(eachTerm.termTypeId)){
			otherTerms.add(termMap);
		}
			
	}
	if(otherTerms){
		context.termExists = "Y";
	}*/
	
	titleTransferEnumIdsList = [];
	taxAuthorityTypeTitleTransferList = delegator.findList("TaxAuthorityTypeTitleTransfer", null, null, null, null, false);
	titleTransferEnumIdsList = EntityUtil.getFieldListFromEntityList(taxAuthorityTypeTitleTransferList, "titleTransferEnumId", true);
	
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
	//Debug.log("transactionTypeTaxMap =================="+transactionTypeTaxMap);
	context.transactionTypeTaxMap = transactionTypeTaxMap;
	
	
		/*Map<String, Object> orderDtlMap = FastMap.newInstance();
		orderDtlMap.put("orderId", orderId);
		orderDtlMap.put("userLogin", userLogin);
		result = dispatcher.runSync("getOrderItemSummary",orderDtlMap);
		if(ServiceUtil.isError(result)){
			//Debug.logError("Unable get Order item: " + ServiceUtil.getErrorMessage(result), module);
			return ServiceUtil.returnError(null, null, null,result);
		}
		productSummaryMap=result.get("productSummaryMap");
	*/
		
		/*	Iterator eachProductIter = productSummaryMap.entrySet().iterator();
			while(eachProductIter.hasNext()) {
				Map.Entry entry = (Entry)eachProductIter.next();
				String productId = (String)entry.getKey();
				 productSummary=entry.getValue();
				 
				 amount=productSummary.get("amount");
				 quantity=productSummary.get("quantity");
				 unitPrice=productSummary.get("unitListPrice");
				 bedPercent=productSummary.get("bedPercent")
				 cstPercent=productSummary.get("cstPercent")
				 vatPercent=productSummary.get("vatPercent")
				 itemSeqList=productSummary.get("itemSeqList");
				 String orderItemSeqId=itemSeqList.get(0);
				 if(!amount){
					 amount = 0;
				 }
				 bedTaxPercent = 0;
				 if(productSummary.get("bedPercent")){
					 bedCompare = (productSummary.get("bedPercent")).setScale(6);
					 condList = [];
					 condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "EXCISE_DUTY_PUR"));
					 condList.add(EntityCondition.makeCondition("componentRate", EntityOperator.EQUALS, bedCompare));
					 cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
					 
					 taxComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
					 taxComponent = EntityUtil.filterByDate(taxComponent, UtilDateTime.nowTimestamp());
					 
					 if(taxComponent){
						 bedTaxPercent = (BigDecimal)(EntityUtil.getFirst(taxComponent)).get("taxRate");
					 }
					 
				 }
				 
				 
				 prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS,productId));
				 prodDetail = EntityUtil.getFirst(prodDetails);
				 
				 remarks="";
				 cond1 = [];
				 cond1.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
				 cond1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,orderItemSeqId));
				 condExpRmrk = EntityCondition.makeCondition(cond1, EntityOperator.AND);
				 
				 RmrkAttr = EntityUtil.filterByCondition(orderItemAttr,condExpRmrk);
				 
				 if(RmrkAttr){
					 remarks=(RmrkAttr.get(0)).get("attrValue");
				 }
				 JSONObject newObj = new JSONObject();
				 newObj.put("remarks",remarks);
				 newObj.put("cProductId",productId);
				 newObj.put("cProductName", prodDetail.brandName+" [ "+prodDetail.description +"]("+prodDetail.internalName+")");
				 newObj.put("quantity",quantity);
				 newObj.put("unitPrice",unitPrice);
				 newObj.put("amount", amount);
				 if(bedPercent){
					 newObj.put("bedPercent", bedTaxPercent);
				 }
				 else{
					 newObj.put("bedPercent", 0);
				 }
				 //newObj.put("bedPercent", eachItem.bedPercent);
				 newObj.put("cstPercent", cstPercent);
				 newObj.put("vatPercent", vatPercent);
				 orderItemsJSON.add(newObj);
			
			
			
			
			}*/
			
			
		
	
	////Debug.log("orderitemdetails================="+orderItemDetails)
	
	adjCondList = [];
	adjCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]));
	orderAdjustmentTypeList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition(adjCondList, EntityOperator.AND), UtilMisc.toSet("orderAdjustmentTypeId", "description"), null, null, false);
	orderAdjustmentTypeIdsList = EntityUtil.getFieldListFromEntityList(orderAdjustmentTypeList, "orderAdjustmentTypeId", true);
	
	
	
	if(UtilValidate.isNotEmpty(orderAdjustments)){
		additionalChgs = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, orderAdjustmentTypeIdsList));
	}
	//Debug.log("additionalChgs =================="+additionalChgs);
	
	
	JSONArray orderItemsJSON = new JSONArray();
	orderItems.each{ eachItem ->
		
		
		resultCtx = dispatcher.runSync("getRemainingOrderItems",UtilMisc.toMap("userLogin",userLogin, "orderId", eachItem.orderId,"orderItemSeqId",eachItem.orderItemSeqId));
		
		//Debug.log("resultCtx =================="+resultCtx);
		
		usedQuantity = resultCtx.usedQuantity;
		
		//Debug.log("usedQuantity =================="+usedQuantity);
		
		if(usedQuantity == 0){
		
		amount = eachItem.quantity*eachItem.unitPrice;
		if(!amount){
			amount = 0;
		}
		
		taxResultCtx = 0;
		taxValueMap = [:];
		if( (UtilValidate.isNotEmpty(supplierGeoId)) && (UtilValidate.isNotEmpty(branchGeoId))   ){
			Map prodCatTaxCtx = UtilMisc.toMap("userLogin",userLogin);
			prodCatTaxCtx.put("productId", eachItem.productId);
			prodCatTaxCtx.put("taxAuthGeoId", branchGeoId);
			//Debug.log("prodCatTaxCtx ====="+prodCatTaxCtx);
			taxResultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",prodCatTaxCtx);
			//Debug.log("taxResultCtx ====="+taxResultCtx);
			taxValueMap = taxResultCtx.get("taxValueMap");
		}
		
		
		/*bedTaxPercent = 0;
		if(eachItem.bedPercent){
			bedCompare = (eachItem.bedPercent).setScale(6);
			condList = [];
			condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "EXCISE_DUTY_PUR"));
			condList.add(EntityCondition.makeCondition("componentRate", EntityOperator.EQUALS, bedCompare));
			cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			
			taxComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
			taxComponent = EntityUtil.filterByDate(taxComponent, UtilDateTime.nowTimestamp());
			
			if(taxComponent){
				bedTaxPercent = (BigDecimal)(EntityUtil.getFirst(taxComponent)).get("taxRate");
			}
			
		}*/
		JSONObject newObj = new JSONObject();
		
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		itemAdditionalChgs = [];
		if(UtilValidate.isNotEmpty(orderAdjustments)){
			itemAdditionalChgs = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
		}
		
		totalItemAdjAmt = 0;
		JSONArray itemAdjustmentJSON = new JSONArray();
		
		for(int i=0; i<orderAdjustmentTypeIdsList.size(); i++){
			orderAdjustmentTypeId = orderAdjustmentTypeIdsList.get(i);
			
			JSONObject newItemAdjObj = new JSONObject();
			newItemAdjObj.put("orderAdjustmentTypeId", orderAdjustmentTypeId);
			
			originalOrderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", eachItem.orderItemSeqId));
			applicableTo = originalOrderItem.get("itemDescription");
			
			newItemAdjObj.put("applicableTo", applicableTo);
			newItemAdjObj.put("adjValue", 0);
			newItemAdjObj.put("percentage", 0);
			newItemAdjObj.put("uomId", "INR");
			
			if(UtilValidate.isNotEmpty(itemAdditionalChgs)){
				itemOrdAdj = EntityUtil.filterByCondition(itemAdditionalChgs, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderAdjustmentTypeId));
				
				if(UtilValidate.isNotEmpty(itemOrdAdj)){
					adjItem = EntityUtil.getFirst(itemOrdAdj);
					
					newItemAdjObj.put("adjValue", adjItem.amount);
					newItemAdjObj.put("percentage", adjItem.sourcePercentage);
					if(adjItem.isAssessableValue && adjItem.isAssessableValue == "Y"){
						newItemAdjObj.put("assessableValue", "checked");
						newObj.put(orderAdjustmentTypeId + "_INC_BASIC", "TRUE");
					}
					else{
						newObj.put(orderAdjustmentTypeId + "_INC_BASIC", "FALSE");
					}
					
					// Update adjustments for item
					
					newObj.put(orderAdjustmentTypeId+"_PUR", adjItem.sourcePercentage);
					newObj.put(orderAdjustmentTypeId + "_PUR_AMT", adjItem.amount);
					
					totalItemAdjAmt = totalItemAdjAmt + adjItem.amount;
				}
				
			}
			
			itemAdjustmentJSON.add(newItemAdjObj);
			
		}
		//Debug.log("itemAdjustmentJSON ========================= "+itemAdjustmentJSON);
		
		
		remarks="";
		packets ="";
		packQuantity = "";
		uom="";
		baleQty=0;
		bundleWght=0;
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		orderItemDtl = EntityUtil.filterByCondition(orderItemDetails, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
		if(UtilValidate.isNotEmpty(orderItemDtl)){
			remarks = (orderItemDtl.get(0)).get("remarks");
			packets = (orderItemDtl.get(0)).get("packets");
			packQuantity = (orderItemDtl.get(0)).get("packQuantity");
			uom = (orderItemDtl.get(0)).get("Uom");
			bundleWght=(orderItemDtl.get(0)).get("bundleWeight");
			if(uom==null){
				uom="KGs";
			}
			if(uom == "Bale" || uom =="Half-Bale"|| uom=="Bundle"){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
				orderItemPrdDtl = EntityUtil.filterByCondition(orderItemDetails, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
				orderItemPrdDtl.each{ eachprd ->
					baleQty=baleQty+eachprd.baleQuantity;
				}
				
			}
		}
		prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		prodDetail = EntityUtil.getFirst(prodDetails);
		
		newObj.put("remarks",remarks);
		newObj.put("orderItemSeqId",eachItem.orderItemSeqId);
		newObj.put("uom",uom);
		newObj.put("bundleWght",bundleWght);
		newObj.put("baleQty",baleQty);
		newObj.put("cProductId",eachItem.productId);
		newObj.put("cProductName", prodDetail.description);
		newObj.put("quantity",eachItem.quantity-usedQuantity);
		newObj.put("unitPrice",eachItem.unitPrice);
		newObj.put("itemAdjustments",itemAdjustmentJSON);
		newObj.put("OTH_CHARGES_AMT",totalItemAdjAmt);
		newObj.put("packets",packets);
		newObj.put("Packaging",packQuantity);
		
		totalTaxAmt = 0;
		if(purchaseTitleTransferEnumId){
			purTaxList = transactionTypeTaxMap.get(purchaseTitleTransferEnumId);
			for(int i=0; i<purTaxList.size(); i++){
				taxItem = purTaxList.get(i);
				newObj.put(taxItem, taxValueMap.get(taxItem));
				//newObj.put("vatPercent", vatPercent);
				if(taxValueMap.get(taxItem)){
					totalTaxAmt = totalTaxAmt + taxValueMap.get(taxItem);
				}
			}
		}
		newObj.put("taxAmt", totalTaxAmt);
		newObj.put("amount", amount);
		
		newObj.put("totPayable",amount + totalTaxAmt + totalItemAdjAmt);
		
		taxList1 = [];
		taxList1.add("VAT");
		taxList1.add("CST");
		taxList1.add("VAT_SURCHARGE");
		taxList1.add("CST_SURCHARGE");
		//taxList1.add("SERVICE_CHARGE");
		//taxList1.add("TEN_PERCENT_SUBSIDY");
		
			newObj.put("taxList1", taxList1);
			
			newObj.put("orderAdjustmentTypeIdsList", orderAdjustmentTypeIdsList);
			
			
			
		
		/*if(eachItem.bedPercent){
			newObj.put("bedPercent", bedTaxPercent);
		}
		else{
			newObj.put("bedPercent", 0);
		}*/
		//newObj.put("bedPercent", eachItem.bedPercent);
		/*newObj.put("cstPercent", eachItem.cstPercent);
		newObj.put("vatPercent", eachItem.vatPercent);*/
		
		orderItemsJSON.add(newObj);
		
		
	}
	context.put("orderItemsJSON", orderItemsJSON);
	
	
	/*JSONArray orderAdjustmentJSON = new JSONArray();
	
	otherTerms.each{ eachOtherTerm ->
		
		sequenceId = eachOtherTerm.sequenceId;
		if(!(sequenceId == "_NA_")){
			
			sequenceItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachOtherTerm.sequenceId));
			if(sequenceItem){
				prodId = (EntityUtil.getFirst(sequenceItem)).productId;
				sequenceId = productIdLabelJSON.get(prodId);
			}
		}
		else{
			sequenceId = "ALL"
		}
		JSONObject newObj = new JSONObject();
		newObj.put("adjustmentTypeId",eachOtherTerm.termTypeId);
		newObj.put("applicableTo", sequenceId);
		newObj.put("adjValue",eachOtherTerm.termValue);
		newObj.put("uomId", eachOtherTerm.uomId);
		newObj.put("termDays", eachOtherTerm.termDays);
		newObj.put("description", eachOtherTerm.description);
		orderAdjustmentJSON.add(newObj);
	}
	context.put("orderAdjustmentJSON", orderAdjustmentJSON);*/
	
	//orderTerms.put("paymentTerms", paymentTerms);
	//orderTerms.put("deliveryTerms", deliveryTerms);
	
	//orderEditParamMap.putAt("orderTerms", orderTerms);
}

}

context.orderEditParam = orderEditParamMap;

// preparing Country List Json
dctx = dispatcher.getDispatchContext();

List<GenericValue> countries= org.ofbiz.common.CommonWorkers.getCountryList(delegator);
JSONArray countryListJSON = new JSONArray();
countries.each{ eachCountry ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachCountry.geoId);
		newObj.put("label",eachCountry.geoName);
		countryListJSON.add(newObj);
}
context.countryListJSON = countryListJSON;

// preparing state List Json


dctx = dispatcher.getDispatchContext();

List<GenericValue> statesList = org.ofbiz.common.CommonWorkers.getAssociatedStateList(delegator, "IND");
JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoCode);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;


/*adjCondList = [];
adjCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
orderAdjustmentTypeList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition(adjCondList, EntityOperator.AND), UtilMisc.toSet("orderAdjustmentTypeId", "description"), null, null, false);
*/



if(additionalChgs){
	context.termExists = "Y";
}

JSONArray adjustmentJSON = new JSONArray();
additionalChgs.each{ eachOdrAdj ->
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
	
	totalAdjAmt = eachOdrAdj.amount;
	percentage = eachOdrAdj.sourcePercentage;
	
	
	JSONObject newObj = new JSONObject();
	newObj.put("adjustmentTypeId", adjTypeId);
	newObj.put("applicableTo", applicableTo);
	newObj.put("adjValue", totalAdjAmt);
	newObj.put("uomId", "INR");
	if(eachOdrAdj.isAssessableValue && eachOdrAdj.isAssessableValue == "Y"){
		newObj.put("assessableValue", "checked");
	}
	adjustmentJSON.add(newObj);
}
//Debug.log("adjustmentJSON =================="+adjustmentJSON);
context.orderAdjustmentJSON = adjustmentJSON;

//InvoiceItem = delegator.findList("InvoiceItem", null, null, null, null, false);

//Debug.log("InvoiceItem =================="+EntityUtil.getFieldListFromEntityList(InvoiceItem, "invoiceItemTypeId", true));


