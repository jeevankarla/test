	
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
	import in.vasista.vbiz.purchase.MaterialHelperServices;
	
	
	
	if(parameters.boothId){
		parameters.boothId = parameters.boothId.toUpperCase();
	}
	if(UtilValidate.isNotEmpty(parameters.productStoreIdFrom)){
		parameters.productStoreId = parameters.productStoreIdFrom;
		productStoreId=parameters.productStoreIdFrom;
	}
	boothId = parameters.boothId;
	
	subscriptionTypeId = parameters.subscriptionTypeId;
	productSubscriptionTypeId = parameters.productSubscriptionTypeId;
	shipmentTypeId = parameters.shipmentTypeId;
	dctx = dispatcher.getDispatchContext();
	effectiveDate = parameters.effectiveDate;
	priceTypeId=parameters.priceTypeId;
	changeFlag=parameters.changeFlag;
	
	productCatageoryId=parameters.productCatageoryId;
	
	if(changeFlag=="DepotSales"){
		//productCatageoryId="DEPO_STORE";
	}
	
	boolean prodCatString = productCatageoryId instanceof String;
	
	displayGrid = true;
	effDateDayBegin="";
	effDateDayEnd="";
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
	if(UtilValidate.isNotEmpty(effectiveDate)){
		try {
			effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
		}catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effDate, "");
			displayGrid = false;
		}
		effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
		effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
	}else{
		effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		effDateDayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
	conditionList = [];
	exprList = [];
	result = [:];
	routeId = parameters.routeId;
	partyId="";
	orderTaxType = parameters.orderTaxType;
	packingType = parameters.packingType;
	facility = null;
	prodPriceMap = [:];
	
	parentRoleTypeId="CUSTOMER_TRADE_TYPE";
	if(UtilValidate.isNotEmpty(parameters.parentRoleTypeId)){
		parentRoleTypeId=parameters.parentRoleTypeId;
	}
	Debug.log("==productCatageoryId==="+productCatageoryId);
	Debug.log("==changeFlag==="+changeFlag);
	suppPartyName="";
	partyId = parameters.partyId;
	supplierPartyId=parameters.suplierPartyId;
	if(supplierPartyId){
		suppPartyName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, supplierPartyId, false);
	}
	
	party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
	if(UtilValidate.isEmpty(party)){
		party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
	}
	context.suppPartyName=suppPartyName;
	roleTypeId = parameters.roleTypeId;
	partyRole = null;
	if(party){
		if(UtilValidate.isNotEmpty(parentRoleTypeId) && UtilValidate.isEmpty(parameters.roleTypeId)) {//to handle parentRoleTypeIds only when roleTypeId is empty
			roleTypeAndPartyList = delegator.findByAnd("RoleTypeAndParty",["parentTypeId" :parentRoleTypeId,"partyId":partyId]);
			partyRole=EntityUtil.getFirst(roleTypeAndPartyList);
		}else{
			partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
	    }
    }
	if(!party){
		context.errorMessage = partyId+" incorrect for the transaction !!";
		displayGrid = false;
		return result;
	}
	/*if(UtilValidate.isEmpty(productCatageoryId)){
		context.errorMessage = "Please Select At Least One productCatageoryId !";
		displayGrid = false;
		return result;
	}*/
	context.productCategoryId = parameters.productCatageoryId;
	context.party = party;
	context.orderTaxType = parameters.orderTaxType;
	context.packingType = parameters.packingType;
	
	partyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null,null,null, false);
	if(partyPostalAddress){
		partyPostalAddress = EntityUtil.getFirst(partyPostalAddress);
		partyAddress = partyPostalAddress.address1;
		context.partyAddress = partyAddress;
	}
	
	prodList=[];
	exprList.clear();
	//exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
	//exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
	if(parameters.schemeCategory && "MGPS_10Pecent".equals(parameters.schemeCategory)){
		catIds=["COTTON_40ABOVE","COTTON_UPTO40","SILK_YARN","WOOLYARN_BELOW10NM","WOOLYARN_10STO39NM","WOOLYARN_40SNMABOVE"];
		cndList=[];
		cndList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN,catIds));
		EntityCondition cnd1 = EntityCondition.makeCondition(cndList, EntityOperator.AND);
		prodIdsList =delegator.findList("ProductCategoryMember", cnd1,null, null, null, false);
		prodIdsList= EntityUtil.getFieldListFromEntityList(prodIdsList,"productId", true);
		if(prodIdsList){
			exprList.add(EntityCondition.makeCondition("productId", EntityOperator.IN,prodIdsList));
		}
	}else if(parameters.schemeCategory && "MGPS".equals(parameters.schemeCategory)){
		exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_LIKE,"%CONE%"));
	}else{
		exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.LIKE,"%CONE%"));
	}
	
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
			  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		Debug.log("exprList =============="+exprList);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
	
	if(UtilValidate.isNotEmpty(productCatageoryId)){
		
		
	      
	}
	//Debug.log("==prodList==="+prodList);
	
	/*
	exprList.clear();
	exprList.add(EntityCondition.makeCondition("productFeatureTypeId", EntityOperator.EQUALS, "COUNT"));
	EntityCondition prodFeatureCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
			  
	productFeature =delegator.findList("ProductFeature", prodFeatureCond, null, null, null, false);
	
	JSONArray featuresJSON = new JSONArray();
	JSONObject featuresLabelJSON = new JSONObject();
	JSONObject featuresLabelIdJSON=new JSONObject();
	productFeature.each{eachFeature ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachFeature.featureId);
		newObj.put("label",eachFeature.description +" [ " +eachFeature.productFeatureTypeId+"]");
		featuresJSON.add(newObj);
		featuresLabelJSON.put(eachFeature.productFeatureTypeId, eachFeature.description);
		featuresLabelIdJSON.put(eachFeature.description +" [ " +eachFeature.productFeatureTypeId+"]", eachFeature.productFeatureTypeId);
		
	}
	
	context.featuresJSON = featuresJSON;
	context.featuresLabelJSON = featuresLabelJSON;
	context.featuresLabelIdJSON = featuresLabelIdJSON;*/
	
	
	
	Map inputProductRate = FastMap.newInstance();
	inputProductRate.put("facilityId",boothId);
	inputProductRate.put("partyId",partyId);
	inputProductRate.put("userLogin",userLogin);
	priceResultMap = [:];
	if(facility){
		inputProductRate.put("fromDate",effDateDayBegin);
		inputProductRate.put("facilityCategory",facility.categoryTypeEnum);
		inputProductRate.put("productsList",prodList);
		priceResultMap = ByProductNetworkServices.getProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	}else{
		inputProductRate.put("priceDate",effDateDayBegin);
		inputProductRate.put("productList",prodList);
		//inputProductRate.put("productCategoryId", productCatageoryId);
		if(orderTaxType){
			if(orderTaxType == "INTRA"){
				inputProductRate.put("geoTax", "VAT");
			}
			else{
				inputProductRate.put("geoTax", "CST");
			}
		}
		if(packingType){
			inputProductRate.put("productPriceTypeId", packingType);
		}
		priceResultMap = ByProductNetworkServices.getStoreProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	}
	
	productIds = EntityUtil.getFieldListFromEntityList(prodList, "productId", true);
	Map result = (Map)MaterialHelperServices.getProductUOM(delegator, productIds);
	uomLabelMap = result.get("uomLabel");
	productUomMap = result.get("productUom");
	prodPriceMap=[:];
	prodPriceMap = (Map)priceResultMap.get("priceMap");
	conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productList", prodList, "userLogin", userLogin));
	conversionMap = conversionResult.get("productConversionDetails");
	if(conversionMap){
		Iterator prodConvIter = conversionMap.entrySet().iterator();
		JSONObject conversionJSON = new JSONObject();
		while (prodConvIter.hasNext()) {
			Map.Entry entry = prodConvIter.next();
			productId = entry.getKey();
			convDetail = entry.getValue();
			
			Iterator detailIter = convDetail.entrySet().iterator();
			JSONObject conversionDetailJSON = new JSONObject();
			while (detailIter.hasNext()) {
				Map.Entry entry1 = detailIter.next();
				attrName = entry1.getKey();
				attrValue = entry1.getValue();
				conversionDetailJSON.put(attrName,attrValue);
			}
			conversionJSON.put(productId, conversionDetailJSON);
		}
		context.conversionJSON = conversionJSON;
	}
	JSONObject productUOMJSON = new JSONObject();
	JSONObject uomLabelJSON=new JSONObject();
	
	JSONArray productItemsJSON = new JSONArray();
	JSONObject productIdLabelJSON = new JSONObject();
	JSONObject productLabelIdJSON=new JSONObject();
	context.productList = prodList;
	JSONObject productPiecesJSON = new JSONObject();
	prodList.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.productId);
		newObj.put("label",eachItem.description);
		productItemsJSON.add(newObj);
		productIdLabelJSON.put(eachItem.productId, eachItem.description);
		productLabelIdJSON.put(eachItem.description, eachItem.productId);
		if(UtilValidate.isNotEmpty(eachItem.piecesIncluded)){
			productPiecesJSON.putAt(eachItem.productId, eachItem.piecesIncluded);
		}
		/*if(productUomMap){
			uomId = productUomMap.get(eachItem.productId);
			if(uomId){
				productUOMJSON.put(eachItem.productId, uomId);
				uomLabelJSON.put(uomId, uomLabelMap.get(uomId));
			}
		}*/
		productUOMJSON.put(eachItem.productId, "BALE");
		uomLabelJSON.put("BALE", "Bale");
	}
	context.productUOMJSON = productUOMJSON;
	context.uomLabelJSON = uomLabelJSON; 
	context.productPiecesJSON = productPiecesJSON;
	productPrices = [];
	
	JSONObject productCostJSON = new JSONObject();
	productCostJSON=prodPriceMap;
	JSONObject prodIndentQtyCat = new JSONObject();
	JSONObject qtyInPieces = new JSONObject();
	context.productItemsJSON = productItemsJSON;
	context.productIdLabelJSON = productIdLabelJSON;
	context.productCostJSON = productCostJSON;
	context.productLabelIdJSON = productLabelIdJSON;
	if(displayGrid){
		context.partyCode = facility;
	}
	//adding order adjustments
	orderAdjTypes = delegator.findList("OrderAdjustmentType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALE_ORDER_ADJUSTMNT"), null, null, null, false);
	
	JSONArray orderAdjItemsJSON = new JSONArray();
	JSONObject orderAdjLabelJSON = new JSONObject();
	JSONObject orderAdjLabelIdJSON=new JSONObject();
	orderAdjTypes.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.orderAdjustmentTypeId);
		newObj.put("label",eachItem.description +" [ " +eachItem.orderAdjustmentTypeId+"]");
		orderAdjItemsJSON.add(newObj);
		orderAdjLabelJSON.put(eachItem.orderAdjustmentTypeId, eachItem.description);
		orderAdjLabelIdJSON.put(eachItem.description +" [ " +eachItem.orderAdjustmentTypeId+"]", eachItem.orderAdjustmentTypeId);
		
	}
	
	context.orderAdjItemsJSON = orderAdjItemsJSON;
	context.orderAdjLabelJSON = orderAdjLabelJSON;
	context.orderAdjLabelIdJSON = orderAdjLabelIdJSON;
	
	
	
	//Quotas handling
	
	resultCtx = dispatcher.runSync("getPartySchemeEligibility",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
	schemesMap = resultCtx.get("schemesMap");
	
	productCategoryQuotasMap = [:];
	if(UtilValidate.isNotEmpty(schemesMap.get("TEN_PERCENT_MGPS"))){
		productCategoryQuotasMap = schemesMap.get("TEN_PERCENT_MGPS");
	}
	
	// Get Scheme Categories
	schemeCategoryIds = [];
	productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
	schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);

	condsList = [];
	//condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
	if(effectiveDate){
	condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
	condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
	 }
	  prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), null, null, null, true);	  
	    //productCategoriesList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productCategoryId", true);
	  JSONObject productQuotaJSON=new JSONObject();
	  for(int i=0; i<prodCategoryMembers.size(); i++){
		  schemeProdId = (prodCategoryMembers.get(i)).get("productId");
		  schemeCatId = (prodCategoryMembers.get(i)).get("productCategoryId");
		  if(productCategoryQuotasMap.containsKey(schemeCatId)){
			  quota = (productCategoryQuotasMap.get(schemeCatId)).get("availableQuota");
			  productQuotaJSON.put(schemeProdId, quota);
		  }
	  }
	  context.productQuotaJSON = productQuotaJSON;
	
	