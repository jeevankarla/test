	
	import in.vasista.vbiz.purchase.PurchaseStoreServices;
	import org.ofbiz.party.party.PartyHelper;
	
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
	import in.vasista.vbiz.byproducts.icp.ICPServices;
	import org.ofbiz.party.contact.ContactMechWorker;
	
	partyId = parameters.partyId;
	
	context.partyId=parameters.partyId;
	conditionList=[];
	if(parameters.partyId){
		address1="";
		address2="";
		state="";
		city="";
		postalCode="";
		contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, parameters.partyId, false,"POSTAL_ADDRESS");
		if(contactMechesDetails){
			contactMec=contactMechesDetails.getLast();
			if(contactMec){
				partyPostalAddress=contactMec.get("postalAddress");
				if(partyPostalAddress){
					
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
					
				}
			}
		}
	
		conditionList.clear();
		
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,parameters.partyId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		facilityDepo = delegator.findList("Facility",condition,null,null,null,false);
		String Depo="NO";
		if(facilityDepo){
		   Depo="YES";
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,parameters.partyId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		PartyLoomDetails =  EntityUtil.getFirst(delegator.findList("PartyLoom",condition,null,null,null,false));
		custPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, parameters.partyId, false);
		parameters.custName=custPartyName;
		
		loomType="";
		loomQuota="";
		loomQty="";
		Desc="";
		
		if(PartyLoomDetails){
			loomQuota=PartyLoomDetails.quotaPerLoom;
			loomQty=PartyLoomDetails.quantity;
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("loomTypeId", EntityOperator.EQUALS,PartyLoomDetails.loomTypeId));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			LoomTypeDetails =delegator.findList("LoomType",condition,null,null,null,false);
			if(LoomTypeDetails){
				type=LoomTypeDetails.loomTypeId;
				/*if(LoomTypeDetails.description){
					Desc=LoomTypeDetails.description
				}*/
				Desc +=type;
			}
		}
		
		psbNo="";
		partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", parameters.partyId, "partyIdentificationTypeId", "PSB_NUMER"), false);
		if(partyIdentification){
			psbNo = partyIdentification.get("idValue");
		}
		parameters.psbNo=psbNo;
		
		parameters.address=address1+address2+city;
		
		parameters.postalCode=postalCode;
		parameters.Depo=Depo;
		parameters.loomType=Desc;
		parameters.loomQuota=loomQuota;
		parameters.loomQty=loomQty;
	
	}
	
	partyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null,null,null, false);
	if(partyPostalAddress){
		partyPostalAddress = EntityUtil.getFirst(partyPostalAddress);
		partyAddress = partyPostalAddress.address1;
		context.partyAddress = partyAddress;
	}
	context.partyName =PartyHelper.getPartyName(delegator,partyId, false);
	
	orderEditParamMap = [:];
	
	
	partyId = parameters.partyId;
	salesChannel = parameters.salesChannel;
	dctx = dispatcher.getDispatchContext();
	effectiveDate = parameters.effectiveDate;
	changeFlag=parameters.changeFlag;
	subscriptionProdList = [];
	displayGrid = true;
	effDateDayBegin="";
	effDateDayEnd="";
	
	effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	if(UtilValidate.isNotEmpty(orderHeader)){
		effDateDayBegin=orderHeader.estimatedDeliveryDate;
	}
	partyId = parameters.partyId;
	orderTaxType = parameters.orderTaxType;
	packingType = parameters.packingType;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	partyOrders = delegator.findList("OrderRole", expr, null, null, null, false);
	
	orderType="direct"; 
	onbehalfof = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ON_BEHALF_OF"));
	if(onbehalfof){
		orderType = "onbehalfof";
	}
	context.orderType=orderType;
	
	suplierPartyId="";
	suppPartyName="";
	suppAttr = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
	if(suppAttr){
		suplierPartyId=suppAttr[0].get("partyId");
		if(suplierPartyId)
		{
			suppPartyName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, suplierPartyId, false);
		}
	}
	context.suplierPartyId=suplierPartyId;
	context.suplierPartyName=suppPartyName;
	parameters.suplierPartyId=suplierPartyId;
	parameters.suplierPartyName=suppPartyName;
	parameters.societyPartyId=partyId
	schemeCategory="";
	
	orderTaxType="";
	orderAttTax=delegator.findOne("OrderAttribute",[orderId :orderId, attrName:"INDET_TAXTYPE" ], false);
	if(orderAttTax){
		orderTaxType=orderAttTax.attrValue;
		parameters.orderTaxType=orderTaxType;
	}
	
	orderAttCat=delegator.findOne("OrderAttribute",[orderId :orderId, attrName:"SCHEME_CAT" ], false);
	if(orderAttCat){
		schemeCategory=orderAttCat.attrValue;
		parameters.schemeCategory=schemeCategory;
	}
	
	
	JSONArray orderItemsJSON = new JSONArray();
	
	JSONArray orderAdjustmentJSON = new JSONArray();//Orderadjustment Json
	
	partyOrderIds = EntityUtil.getFieldListFromEntityList(partyOrders, "orderId", true);
	if(partyOrderIds){
		
		updateOrderId = partyOrderIds.get(0);
		
		orderHeaderTemp=delegator.findOne("OrderHeader",[orderId :updateOrderId], false);
		if(UtilValidate.isNotEmpty(orderHeaderTemp)){
			context.orderMessage=orderHeaderTemp.orderMessage;
			parameters.productStoreId=orderHeaderTemp.productStoreId;
			parameters.referenceNo=orderHeaderTemp.externalId;
			parameters.salesChannel=orderHeaderTemp.salesChannelEnumId;
			parameters.effectiveDate= UtilDateTime.toDateString(orderHeaderTemp.estimatedDeliveryDate, "dd MMMMM, yyyy");
			parameters.indentReceivedDate= UtilDateTime.toDateString(orderHeaderTemp.estimatedDeliveryDate, "dd MMMMM, yyyy");
			context.effectiveDate=UtilDateTime.toDateString(orderHeaderTemp.estimatedDeliveryDate, "dd MMMMM, yyyy");
			context.productStoreId=orderHeaderTemp.productStoreId;
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId));
		condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
		
		orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId), null, UtilMisc.toList("-orderItemSeqId"), null, false);
		
		
		productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
		productCategorySelect = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		productCategorySelectIds = EntityUtil.getFieldListFromEntityList(productCategorySelect, "productCategoryId", true);
		
		JSONArray productCategoryJSON = new JSONArray();
		category="";
		catType="";
		productCategorySelectIds.each{eachCatId ->
			category=eachCatId;
			productCategoryJSON.add(eachCatId);
		}
		
		if(category.contains("SILK")){
			catType="Silk";
		}else if(category.contains("COTTON") || category.contains("HANK")){
			catType="Cotton";
		}else{
			catType="other";
		}
		context.catType=catType;
		context.productCategoryJSON = productCategoryJSON;
		
		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		
		// Get Scheme Categories
		schemeCategoryIds = [];
		productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
		schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);
	
		orderAdjustmentsList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId), null, null, null, false);
		
		
		
		
		
		//Quotas handling
		
		//resultCtx = dispatcher.runSync("getPartySchemeEligibility",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
	//        schemesMap = resultCtx.get("schemesMap");
	productCategoryQuotasMap = [:];
	resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
	       productCategoryQuotasMap = resultCtx.get("schemesMap");
	//        if(UtilValidate.isNotEmpty(schemesMap.get("TEN_PERCENT_MGPS"))){
	//                productCategoryQuotasMap = schemesMap.get("TEN_PERCENT_MGPS");
	//        }
		condsList = [];
		//condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
	//	condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
	//	condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
	//		  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
	//	  
		  prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), null, null, null, true);
	
		cList=[];
		cList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,updateOrderId ));
		
		  orderAdjList = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(cList,EntityOperator.AND), null, null, null, true);
		  orderItemDtl = delegator.findList("OrderItemDetail", EntityCondition.makeCondition(cList,EntityOperator.AND), null, null, null, true);
		  
		  JSONObject OrderItemUIJSON = new JSONObject();
		
		  orderItemDtl.each{ eachItem ->
			
			productDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
			prodDetail = null;
			if(productDetails){
				prodDetail = productDetails.get(0);
			}
			JSONObject newObj = new JSONObject();
			
			productQuotaDetails = EntityUtil.filterByCondition(prodCategoryMembers, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
			quota=0;		
			if(productQuotaDetails){
				schemeCatId = (productQuotaDetails.get(0)).get("productCategoryId");
				if(productCategoryQuotasMap.containsKey(schemeCatId)){
					if(UtilValidate.isNotEmpty(productCategoryQuotasMap.get(schemeCatId))){
						quota = productCategoryQuotasMap.get(schemeCatId);
					}
				}
			}
			
			baleQty=0;
			yarnUOM=eachItem.Uom;
			bundleWeight=0;
			baleQty=eachItem.baleQuantity;
			remrk=eachItem.remarks;
			wieverName="";
			wieverId=eachItem.partyId;
			psbNo="";
			
			if(wieverId){
				partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", wieverId, "partyIdentificationTypeId", "PSB_NUMER"), false);
				if(partyIdentification){
					psbNo = partyIdentification.get("idValue");
				}
				wieverName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, wieverId, false);
			}
			bundleWeight=eachItem.bundleWeight;
			newObj.put("customerName",wieverName+"["+psbNo+"]");
			newObj.put("customerId",wieverId);
			newObj.put("remarks",remrk);
			
			newObj.put("psbNumber",psbNo);
			
			newObj.put("cProductId",eachItem.productId);
			newObj.put("cProductName",prodDetail.description +" [ "+prodDetail.brandName+"]");
			newObj.put("baleQuantity",baleQty);
			newObj.put("cottonUom",yarnUOM);
			newObj.put("bundleWeight",bundleWeight);
			if(orderAdjList){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "quotaQty"));
				
				orderAdjDetails = EntityUtil.filterByCondition(orderAdjList, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
				if(orderAdjDetails && orderAdjDetails.get(0).get("attrValue")){
					Float f = new Float(orderAdjDetails.get(0).get("attrValue"));
					quota=quota+f;
				}
			}
			newObj.put("quantity",eachItem.quantity);
			newObj.put("quota",quota);
			newObj.put("unitPrice",eachItem.bundleUnitPrice);
			newObj.put("KgunitPrice",eachItem.unitPrice);
			 BigDecimal noOfBundles=0;
			if("Bale".equals(yarnUOM)){
				noOfBundles=baleQty*40;
			}
			if("Half-Bale".equals(yarnUOM)){
				noOfBundles=baleQty*20;
			}
			if("Bundle".equals(yarnUOM) || "KGs".equals(yarnUOM)){
				noOfBundles=baleQty;
			}
			amount=eachItem.bundleUnitPrice*noOfBundles;
			newObj.put("amount", amount);
			
			// Temporatily Preparing for taxes until login is been build to get it from the UI. 
			totalTaxAmt = 0;
			taxList = EntityUtil.filterByCondition(orderAdjustmentsList, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["VAT_SALE","VAT_SURCHARGE","CST_SALE"]));
			for(i=0; i<taxList.size(); i++){
				totalTaxAmt += (taxList.get(i)).get("amount");
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId"), (taxList.get(i)).get("sourcePercentage"));
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId") + "_AMT", (taxList.get(i)).get("amount"));
			}
			
			serviceChgPercent = 0;
			serviceChg = 0;
			serviceChargeList = EntityUtil.filterByCondition(orderAdjustmentsList, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["SERVICE_CHARGE"]));
			if(UtilValidate.isNotEmpty(serviceChargeList)){
				serviceChg = (serviceChargeList.get(0)).get("amount");
				serviceChgPercent = (serviceChargeList.get(0)).get("sourcePercentage");
			}
			newObj.put("taxAmt", totalTaxAmt);
			newObj.put("SERVICE_CHARGE_AMT", serviceChg);
			newObj.put("totPayable", amount + totalTaxAmt + serviceChg);
			
			JSONArray vatSurchargeList = new JSONArray();
			vatSurchargeList.add("VAT_SURCHARGE");
			newObj.put("vatSurchargeList", vatSurchargeList);
			
			JSONArray taxList = new JSONArray();
			taxList.add("VAT_SALE");
			taxList.add("CST_SALE");
			taxList.add("VAT_SURCHARGE");
			newObj.put("taxList", taxList);
			
			applicableTaxTypeAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "applicableTaxType"));
			checkCFormAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "checkCForm"));
			checkE2FormAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "checkE2Form"));
			
			if(UtilValidate.isNotEmpty(applicableTaxTypeAttr)){	
				newObj.put("applicableTaxType", (applicableTaxTypeAttr.get(0)).get("attrValue"));
			}
			if(UtilValidate.isNotEmpty(checkCFormAttr)){	
				newObj.put("checkCForm", (checkCFormAttr.get(0)).get("attrValue"));
			}
			if(UtilValidate.isNotEmpty(checkE2FormAttr)){	
				newObj.put("checkE2Form", (checkE2FormAttr.get(0)).get("attrValue"));
			}
			
			orderItemsJSON.add(newObj);
			if(OrderItemUIJSON.get(eachItem.productId)){
				JSONObject existsObj = new JSONObject();
				existsObj=OrderItemUIJSON.get(eachItem.productId);
				//existsObj["quantity"]=existsObj.get("quantity")+eachItem.quantity;
				//existsObj["baleQuantity"]=existsObj.get("baleQuantity")+baleQty;
				OrderItemUIJSON.put(eachItem.productId, existsObj);
				
			}else{
				OrderItemUIJSON.put(eachItem.productId, newObj);
			}
			
		}
		
		context.orderId = updateOrderId;
		
		
		
	}
	context.dataJSON = orderItemsJSON;
	
	
	
	
