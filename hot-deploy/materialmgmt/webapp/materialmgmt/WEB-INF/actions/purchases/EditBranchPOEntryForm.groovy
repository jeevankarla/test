
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
/*conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));*/
condEXr = EntityCondition.makeCondition(consList, EntityOperator.AND);
orderItemAttr = delegator.findList("OrderItemAttribute", condEXr, null, null, null, false);
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
if(orderHeader && orderHeader.statusId == "ORDER_CREATED"){
	
	orderInfoDetail = [:];
	conditionList = [];
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
	
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SUPPLIER", "BILL_FROM_VENDOR","SHIP_TO_CUSTOMER")));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
	//orderRole = EntityUtil.getFirst(orderRoles);
	if(orderRoles){
		roleCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPPLIER")],EntityOperator.AND);
		orderRole=EntityUtil.filterByCondition(orderRoles,roleCondition);
		supplierRole = EntityUtil.getFirst(orderRole);
		partyName = PartyHelper.getPartyName(delegator, supplierRole.partyId, false);
		orderInfoDetail.putAt("supplierId", supplierRole.partyId);
		orderInfoDetail.putAt("supplierName", partyName);
		vendorCond=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"BILL_FROM_VENDOR")],EntityOperator.AND);
		vendOrderRole=EntityUtil.filterByCondition(orderRoles,vendorCond);
		vendorRole=EntityUtil.getFirst(vendOrderRole);
		orderInfoDetail.putAt("billToPartyId", vendorRole.partyId);
		
		shipToCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SHIP_TO_CUSTOMER")],EntityOperator.AND);
		shipToPartyRole=EntityUtil.filterByCondition(orderRoles,shipToCondition);
		shipToParty=EntityUtil.getFirst(shipToPartyRole);
		shipingAdd=[:];
		if(shipToParty.partyId){
		//contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, shipToParty.partyId, false,"POSTAL_ADDRESS");
		
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
				Debug.log("partyPostalAddress=========================="+partyPostalAddress);
			//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
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
	/*condList=[];
	condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	orderPartys = delegator.findList("OrderRole", cond, null, null, null, false);
	orderParty = EntityUtil.getFirst(orderPartys);
	if(orderParty){
		orderInfoDetail.putAt("billToPartyId", orderParty.partyId);
		
	}*/
	orderEditParamMap.putAt("orderHeader", orderInfoDetail);
	orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	orderAdjDetail = [:];
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
	orderEditParamMap.put("orderAdjustment", orderAdjDetail);
	
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	quoteNo = EntityUtil.getFirst(orderItems);
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
	orderEditParamMap.put("quoteDetails", quoteDetailMap);
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	
	quoteIds = EntityUtil.getFieldListFromEntityList(orderItems, "quoteId", true);
	
	termTypes = delegator.findList("TermType", null, null, null, null, false);
	
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
	}
	

		
		Map<String, Object> orderDtlMap = FastMap.newInstance();
		orderDtlMap.put("orderId", orderId);
		orderDtlMap.put("userLogin", userLogin);	
		result = dispatcher.runSync("getOrderItemSummary",orderDtlMap);
		if(ServiceUtil.isError(result)){
			Debug.logError("Unable get Order item: " + ServiceUtil.getErrorMessage(result), module);
			return ServiceUtil.returnError(null, null, null,result);
		}
		productSummaryMap=result.get("productSummaryMap");
		
		
		JSONArray orderItemsJSON = new JSONArray();
		
			Iterator eachProductIter = productSummaryMap.entrySet().iterator();
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
			
			
			
			
			}
			
			
		
	
	
	/*orderItems.each{ eachItem ->
		amount = eachItem.quantity*eachItem.unitPrice;
		if(!amount){
			amount = 0;
		}
		
		bedTaxPercent = 0;
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
			
		}
		cond = [];
		cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "WIEVER_CUSTOMER"));
		cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
		condExpWiever = EntityCondition.makeCondition(cond, EntityOperator.AND);
		WieverAttr = EntityUtil.filterByCondition(orderItemAttr,condExpWiever);
		
		cond1 = [];
		cond1.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
		cond1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
		condExpRmrk = EntityCondition.makeCondition(cond1, EntityOperator.AND);
		RmrkAttr = EntityUtil.filterByCondition(orderItemAttr,condExpRmrk);
		
		wieverName="";
		wieverId="";
		psbNo="";
		remarks="";
		
		
		if(WieverAttr){
			wieverId=(WieverAttr.get(0)).get("attrValue");
			partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", wieverId, "partyIdentificationTypeId", "PSB_NUMER"), false);
				if(partyIdentification){
					psbNo = partyIdentification.get("idValue");
				}
			wieverName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, wieverId, false);
			}
		
		if(RmrkAttr){
			remarks=(RmrkAttr.get(0)).get("attrValue");
			}
		prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		prodDetail = EntityUtil.getFirst(prodDetails);
		JSONObject newObj = new JSONObject();
		newObj.put("customerName",wieverName+"["+psbNo+"]");
		newObj.put("customerId",wieverId);
		newObj.put("psbNumber",psbNo);
		newObj.put("remarks",remarks);
		
		newObj.put("cProductId",eachItem.productId);
		newObj.put("cProductName", prodDetail.brandName+" [ "+prodDetail.description +"]("+prodDetail.internalName+")");
		newObj.put("quantity",eachItem.quantity);
		newObj.put("unitPrice",eachItem.unitPrice);
		newObj.put("amount", amount);
		if(eachItem.bedPercent){
			newObj.put("bedPercent", bedTaxPercent);
		}
		else{
			newObj.put("bedPercent", 0);
		}
		//newObj.put("bedPercent", eachItem.bedPercent);
		newObj.put("cstPercent", eachItem.cstPercent);
		newObj.put("vatPercent", eachItem.vatPercent);
		orderItemsJSON.add(newObj);
	}*/
	context.put("orderItemsJSON", orderItemsJSON);
	
	JSONArray orderAdjustmentJSON = new JSONArray();
	
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
	context.put("orderAdjustmentJSON", orderAdjustmentJSON);
	
	orderTerms.put("paymentTerms", paymentTerms);
	orderTerms.put("deliveryTerms", deliveryTerms);
	
	orderEditParamMap.putAt("orderTerms", orderTerms);
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
