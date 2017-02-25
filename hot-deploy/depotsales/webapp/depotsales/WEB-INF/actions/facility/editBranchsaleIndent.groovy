	
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
	
	tallyReferenceNo = "";
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
	
	//Debug.log("changeFlag====1454121545============"+changeFlag);
	
	
	subscriptionProdList = [];
	displayGrid = true;
	effDateDayBegin="";
	effDateDayEnd="";
	
	effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	
	tallyRefNumber = orderHeader.get("tallyRefNo");
	
	//Debug.log("tallyReferenceNo====1454121545============"+tallyReferenceNo);
	
	context.tallyRefNumber=tallyRefNumber;
	
	context.changeFlag = changeFlag;
	
	
	
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
	
	//Debug.log("tallyReferenceNo====3232==========="+tallyReferenceNo);
	context.suplierPartyId=suplierPartyId;
	context.suplierPartyName=suppPartyName;
	parameters.suplierPartyId=suplierPartyId;
	parameters.suplierPartyName=suppPartyName;
	parameters.societyPartyId=partyId;
	
	if(UtilValidate.isNotEmpty(suplierPartyId)){
		suplierAdd="";
		try{
			 partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:suplierPartyId, userLogin: userLogin]);
		 if(UtilValidate.isNotEmpty(partyPostalAddress)){
			 if(UtilValidate.isNotEmpty(partyPostalAddress.address1)){
			  supAddress1=partyPostalAddress.address1;
			 }else{
			    supAddress1="";			 
			 }
			if(UtilValidate.isNotEmpty(partyPostalAddress.address2)){
				supAddress2=partyPostalAddress.address2;
			}else{
				supAddress2="";
			}
			if(UtilValidate.isNotEmpty(partyPostalAddress.city)){
				supCity=partyPostalAddress.city;
			}else{
				supCity="";
			}
		 }
		 
		}catch(Exception e){}
		
	}
	
	suplierAdd=supAddress1+" "+supAddress2+" "+supCity;
	context.supplierAddress=suplierAdd;
	
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
	
	
	//Debug.log("orderTaxType====3232==========="+orderTaxType);
	
	JSONArray orderItemsJSON = new JSONArray();
	JSONObject usedQuotaForExistingProd = new JSONObject();
	
	JSONArray orderAdjustmentJSON = new JSONArray();//Orderadjustment Json
	JSONObject partyUsedQuotaJSON = new JSONObject();
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
		
		//Debug.log("orderItems====3232==========="+orderItems);
		
		
		
		productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
		
		//Debug.log("productIds====3232==========="+productIds);
		
		
		productCategorySelect = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		productCategorySelectIds = EntityUtil.getFieldListFromEntityList(productCategorySelect, "productCategoryId", true);
		
		
		//Debug.log("productCategorySelectIds====3232==========="+productCategorySelectIds);
		List condsList = [];
		condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productIds[0]));
		condsList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "YARN_SALE"));
		/*condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, invoiceDate));
		condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, invoiceDate)));
*/	 
		 prodCategoryMembers = delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), UtilMisc.toSet("primaryParentCategoryId"), null, null, true);
		  
		 //Debug.log("prodCategoryMembers====3232==========="+prodCategoryMembers);
		 
		  
		  prodCategoryMember = EntityUtil.getFirst(prodCategoryMembers);
		  productCategoryId = (String)prodCategoryMember.get("primaryParentCategoryId");
		  
		  //Debug.log("productCategoryId====3232==========="+productCategoryId);
		  
		
		JSONArray productCategoryJSON = new JSONArray();
		category=productCategoryId;
		catType="";
		productCategorySelectIds.each{eachCatId ->
			productCategoryJSON.add(eachCatId);
		}
		
		//Debug.log("productCategoryJSON====3232==========="+productCategoryJSON);
		
		
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
		
		//Debug.log("orderAdjustmentsList====3232==========="+orderAdjustmentsList);
		
		
		//Quotas handling
		
		productCategoryQuotasMap = [:];
		resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
	    productCategoryQuotasMap = resultCtx.get("schemesMap");
		productCategoryUsedQuotaMap= resultCtx.get("usedQuotaMap");
	
		condsList = [];
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
		
		//Debug.log("orderItemDtl====3232==========="+orderItemDtl);
		
		  
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
			usedQuota=eachItem.quotaQuantity;
			
			if(productQuotaDetails){
				schemeCatId = (productQuotaDetails.get(0)).get("productCategoryId");
				if(productCategoryQuotasMap.containsKey(schemeCatId)){
					if(UtilValidate.isNotEmpty(productCategoryQuotasMap.get(schemeCatId))){
						quota = productCategoryQuotasMap.get(schemeCatId);
					}
				}
				if(usedQuotaForExistingProd.containsKey(schemeCatId)){
					usedTotalQuota=(usedQuotaForExistingProd.get(schemeCatId)).add(new BigDecimal(usedQuota));
					usedQuotaForExistingProd.put(schemeCatId, usedTotalQuota);
				}else{
					usedQuotaForExistingProd.put(schemeCatId, usedQuota);
				}
			}
			
			baleQty=0;
			yarnUOM=eachItem.Uom;
			bundleWeight=0;
			baleQty=eachItem.baleQuantity;
			remrk=eachItem.remarks;
			wieverName="";
			weaverId=eachItem.partyId;
			psbNo="";
			
			if(weaverId){
				partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", weaverId, "partyIdentificationTypeId", "PSB_NUMER"), false);
				if(partyIdentification){
					psbNo = partyIdentification.get("idValue");
				}
				wieverName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, weaverId, false);
			}
			bundleWeight=eachItem.bundleWeight;
			newObj.put("customerName",wieverName+"["+weaverId+"]"+psbNo);
			newObj.put("customerId",weaverId);
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
			newObj.put("usedQuota",usedQuota);
			newObj.put("SUBSIDY", Math.abs(eachItem.discountAmount));
			amount=0;
			if("onbehalfof".equals(orderType)){
				if(eachItem.bundleUnitPrice){
					newObj.put("unitPrice",eachItem.bundleUnitPrice);
				}else{
					newObj.put("unitPrice",eachItem.unitPrice);
				}
				
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
				if( !("KGs".equals(yarnUOM)) && eachItem.bundleUnitPrice){
					amount=eachItem.bundleUnitPrice*noOfBundles;
				}else{
					amount=eachItem.unitPrice*eachItem.quantity;
				}
				
				quotaResultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", weaverId));
	    		weaverQuotasMap = quotaResultCtx.get("schemesMap");
				weaverUsedQuotaMap= quotaResultCtx.get("usedQuotaMap");
				
				weaverQuota = 0;
				
				schemeCategoryId = null;
				if(productQuotaDetails){
					schemeCategoryId = (productQuotaDetails.get(0)).get("productCategoryId");
					if(weaverQuotasMap.containsKey(schemeCatId)){
						if(UtilValidate.isNotEmpty(weaverQuotasMap.get(schemeCatId))){
							weaverQuota = weaverQuotasMap.get(schemeCatId);
						}
					}
					
				}
				
				newObj.put("quota",weaverQuota);
				
				orderUsedQuota = 0;
				if(UtilValidate.isNotEmpty(schemeCategoryId)){
					
					JSONObject tempPartyObj = new JSONObject();
					JSONObject tempSchemeCatObj = new JSONObject();
					if(UtilValidate.isNotEmpty(partyUsedQuotaJSON.get(weaverId))){
						tempPartyObj = partyUsedQuotaJSON.get(weaverId);
						if(UtilValidate.isNotEmpty(tempPartyObj.get(schemeCategoryId))){
							tempSchemeCatObj = tempPartyObj.get(schemeCategoryId);
							orderUsedQuota = tempSchemeCatObj.get("orderUsedQuota");
							orderUsedQuota = orderUsedQuota + usedQuota;
							tempSchemeCatObj.put("orderUsedQuota", orderUsedQuota);
						}
						else{
							tempSchemeCatObj.put("orderUsedQuota", usedQuota);
						}
					}
					else{
						tempSchemeCatObj.put("orderUsedQuota", usedQuota);
					}
					
					tempPartyObj.put(schemeCategoryId, tempSchemeCatObj);
					partyUsedQuotaJSON.put(weaverId,tempPartyObj);
					
				}
				
				//newObj.put("usedQuota",20);
			}else{
			
			if(eachItem.bundleUnitPrice){
				newObj.put("unitPrice",eachItem.bundleUnitPrice);
			}else{
				newObj.put("unitPrice",eachItem.unitPrice);
			}
			
				amount=eachItem.unitPrice*eachItem.quantity;
				
			}
			newObj.put("KgunitPrice",eachItem.unitPrice);
			 
			newObj.put("amount", amount);
			
			// Temporatily Preparing for taxes until login is been build to get it from the UI. 
			/*totalTaxAmt = 0;
			cond=[];
			cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
			cond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["VAT_SALE","VAT_SURCHARGE","CST_SALE","CST_SURCHARGE"]));
			
			expr = EntityCondition.makeCondition(cond,EntityOperator.AND);
	
			taxList = EntityUtil.filterByCondition(orderAdjustmentsList, expr);
			for(i=0; i<taxList.size(); i++){
				totalTaxAmt += (taxList.get(i)).get("amount");
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId"), (taxList.get(i)).get("sourcePercentage"));
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId") + "_AMT", (taxList.get(i)).get("amount"));
			}
			
			serviceChgPercent = 0;
			serviceChg = 0;
			cond1=[];
			cond1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
			cond1.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["SERVICE_CHARGE"]));
			
			expr1 = EntityCondition.makeCondition(cond1,EntityOperator.AND);
			serviceChargeList = EntityUtil.filterByCondition(orderAdjustmentsList,expr1);
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
			
			JSONArray cstSurchargeList = new JSONArray();
			cstSurchargeList.add("CST_SURCHARGE");
			newObj.put("cstSurchargeList", cstSurchargeList);
			
			JSONArray taxList = new JSONArray();
			taxList.add("VAT_SALE");
			taxList.add("CST_SALE");
			taxList.add("VAT_SURCHARGE");
			taxList.add("CST_SURCHARGE");
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
			
			List orderAdjustmentsList = [];
			adjCondList = [];
			adjCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
				orderAdjustmentsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition(adjCondList, EntityOperator.AND), UtilMisc.toSet("orderAdjustmentTypeId", "description"), null, null, false);
						
				newObj.put("orderAdjustmentsList", orderAdjustmentsList);
				
			//Debug.log("orderAdjustmentsList===================="+orderAdjustmentsList);
			
			
			*/
			
			
			
			//populate Taxes Details====================
			
			
			resultCtx = dispatcher.runSync("calculateTaxesByGeoIdTest",UtilMisc.toMap("userLogin",userLogin, "taxAuthGeoId", "IN-UP","taxAuthorityRateTypeId","CST_SALE","productId",eachItem.productId));
			
			defaultTaxMap = resultCtx.defaultTaxMap;
			
			taxValueMap = resultCtx.taxValueMap;
			
			
			totalTaxAmt = 0;
			cond=[];
			cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
			cond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["VAT_SALE","VAT_SURCHARGE","CST_SALE","CST_SURCHARGE"]));
			
			expr = EntityCondition.makeCondition(cond,EntityOperator.AND);
	
			taxList = EntityUtil.filterByCondition(orderAdjustmentsList, expr);
			for(i=0; i<taxList.size(); i++){
				totalTaxAmt += (taxList.get(i)).get("amount");
				
				
				if((taxList.get(i)).get("orderAdjustmentTypeId") == "VAT_SURCHARGE" || (taxList.get(i)).get("orderAdjustmentTypeId") == "CST_SURCHARGE"){
					
					newObj.put((taxList.get(i)).get("orderAdjustmentTypeId")+"_SALE" , (taxList.get(i)).get("sourcePercentage"));
					newObj.put((taxList.get(i)).get("orderAdjustmentTypeId") + "_SALE_AMT", (taxList.get(i)).get("amount"));
				
				}else{
				  
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId"), (taxList.get(i)).get("sourcePercentage"));
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId") + "_AMT", (taxList.get(i)).get("amount"));
				
				}
				
			}
			
			
			serviceChgPercent = 0;
			serviceChg = 0;
			cond1=[];
			cond1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
			cond1.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["SERVICE_CHARGE"]));
			
			expr1 = EntityCondition.makeCondition(cond1,EntityOperator.AND);
			serviceChargeList = EntityUtil.filterByCondition(orderAdjustmentsList,expr1);
			if(UtilValidate.isNotEmpty(serviceChargeList)){
				serviceChg = (serviceChargeList.get(0)).get("amount");
				serviceChgPercent = (serviceChargeList.get(0)).get("sourcePercentage");
			}
			newObj.put("taxAmt", totalTaxAmt);
			newObj.put("SERVICE_CHARGE_AMT", serviceChg);
			newObj.put("SERVICE_CHARGE", serviceChgPercent);
			
			//========orderAdjustments===============
			
			adjustmentTotal = 0
			cond.clear();
			cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
			cond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_IN, ["VAT_SALE","VAT_SURCHARGE","CST_SALE","CST_SURCHARGE","TEN_PERCENT_SUBSIDY"]));
			
			expr = EntityCondition.makeCondition(cond,EntityOperator.AND);
	
			taxList = EntityUtil.filterByCondition(orderAdjustmentsList, expr);
			
			//Debug.log("taxList============="+taxList);
			
			
			for(i=0; i<taxList.size(); i++){
				adjustmentTotal += (taxList.get(i)).get("amount");
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId")+"_SALE", (taxList.get(i)).get("sourcePercentage"));
				newObj.put((taxList.get(i)).get("orderAdjustmentTypeId") + "_SALE_AMT", (taxList.get(i)).get("amount"));
			}

			
			//Debug.log("adjustmentTotal========4444====="+adjustmentTotal);
			
			newObj.put("OTH_CHARGES_AMT", adjustmentTotal);
			
			
			subsidyAmt = 0
			cond.clear();
			cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
			cond.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
			
			expr = EntityCondition.makeCondition(cond,EntityOperator.AND);
	
			taxList = EntityUtil.filterByCondition(orderAdjustmentsList, expr);
			
			if(taxList){
				for(i=0; i<taxList.size(); i++){
					subsidyAmt += (taxList.get(i)).get("amount");
				}
			}

			
			
			//Debug.log("amount==============="+amount);
			
			//Debug.log("totalTaxAmt==============="+totalTaxAmt);
			
			//Debug.log("serviceChg==============="+serviceChg);
			
			//Debug.log("adjustmentTotal==============="+adjustmentTotal);
			
			//Debug.log("subsidyAmt==============="+eachItem.discountAmount);
			
			totPayable = (amount + totalTaxAmt + serviceChg + adjustmentTotal+eachItem.discountAmount);
			
			newObj.put("saleAmount",amount + totalTaxAmt + serviceChg + adjustmentTotal);
			
			newObj.put("totPayable", totPayable);
			
			newObj.put("defaultTaxMap", defaultTaxMap);
			
			newObj.put("taxValueMap", taxValueMap);
			
			
			////Debug.log("resultCtx=============="+resultCtx);
			
			
			List orderAdjustmentsList = [];
			adjCondList = [];
			adjCondList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "ADDITIONAL_CHARGES"));
				orderAdjustmentsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition(adjCondList, EntityOperator.AND), UtilMisc.toSet("orderAdjustmentTypeId", "description"), null, null, false);
						
				newObj.put("orderAdjustmentsList", orderAdjustmentsList);
			
			
				
				
				
				taxList1 = [];
				taxList1.add("VAT_SALE");
				taxList1.add("CST_SALE");
				taxList1.add("VAT_SURCHARGE");
				taxList1.add("CST_SURCHARGE");
					
					newObj.put("taxList", taxList1);
				
				
			    purTaxList = [];
				purTaxList.add("VAT_SALE");
				purTaxList.add("CST_SALE");
				purTaxList.add("VAT_SURCHARGE");
				purTaxList.add("CST_SURCHARGE");
					 
					 
					 newObj.put("purTaxList", purTaxList);
					 
				
				
			
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
	
	//Debug.log("tallyReferenceNo=================="+tallyReferenceNo);
	context.dataJSON = orderItemsJSON;
	context.usedQuotaForExistingProd = usedQuotaForExistingProd;
	context.partyUsedQuotaJSON = partyUsedQuotaJSON;
	
	
	
