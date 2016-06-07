	
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
	import org.ofbiz.party.party.PartyHelper;
	import org.ofbiz.party.contact.ContactMechWorker;
	
	
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
	suppPartyName="";
	partyId = parameters.partyId;
	supplierPartyId=parameters.suplierPartyId;
	if(supplierPartyId){
		suppPartyName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, supplierPartyId, false);
	}
	
	if(parameters.partyId){
		address1="";
		address2="";
		state="";
		city="";
		postalCode="";
	contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, parameters.partyId, false,"POSTAL_ADDRESS");
	//Debug.log("contactMechesDetails======================="+contactMechesDetails);
	if(contactMechesDetails){
		contactMec=contactMechesDetails.getLast();
		if(contactMec){
			partyPostalAddress=contactMec.get("postalAddress");
			//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
		//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
			if(partyPostalAddress){
				
				if(partyPostalAddress.get("address1")){
				address1=partyPostalAddress.get("address1");
				//Debug.log("address1=========================="+address1);
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
				
				//partyJSON.put("name",shippPartyName);
				
				
			
				
				//Debug.log("shipingAdd========================="+shipingAdd);
				
			}
		}
	}
	
	conditionList=[];
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
	issueDate="";
	partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", parameters.partyId, "partyIdentificationTypeId", "PSB_NUMER"), false);
	if(partyIdentification){
		psbNo = partyIdentification.get("idValue");
		if(UtilValidate.isNotEmpty(partyIdentification.get("issueDate"))){
			issueDate=UtilDateTime.toDateString(partyIdentification.issueDate,"dd-MM-yyyy");
		}
	}
	parameters.psbNo=psbNo;
	parameters.issueDate=issueDate;
	parameters.address=address1+address2+city;
	
	parameters.postalCode=postalCode;
	parameters.Depo=Depo;
	parameters.loomType=Desc;
	parameters.loomQuota=loomQuota;
	parameters.loomQty=loomQty;
	
	
	
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
		
		if(parameters.screenFlag){
			if(parameters.screenFlag == "CottonIndent"){
				catIds=["COTTON_40ABOVE","COTTON_UPTO40"];
			}else if(parameters.screenFlag == "SilkIndent"){
			catIds=["SILK_YARN"];
			}else if(parameters.screenFlag == "OtherIndent"){
			catIds=["WOOLYARN_BELOW10NM","WOOLYARN_10STO39NM","WOOLYARN_40SNMABOVE"];
			}
		} 
		
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
		if(parameters.screenFlag){
			if(parameters.screenFlag == "CottonIndent"){
				exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN,["COTTON_GREY","CHENILE", "SLUB", "WASTE"]));
			}else if(parameters.screenFlag == "SilkIndent"){
				condList = [];
				condList.add(EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.EQUALS, "SILK"));
				List prodCategoryIds = delegator.findList("ProductCategory", EntityCondition.makeCondition(condList, EntityOperator.AND),null,null,null,false);
				exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(prodCategoryIds, "productCategoryId", true)));
				
			}else if(parameters.screenFlag == "OtherIndent"){
				condList = [];
				condList.add(EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.NOT_IN, ["COTTON", "SILK"]));
				List prodCategoryIds = delegator.findList("ProductCategory", EntityCondition.makeCondition(condList, EntityOperator.AND),null,null,null,false);
				exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN,EntityUtil.getFieldListFromEntityList(prodCategoryIds, "productCategoryId", true)));
			}
		}
	}
	
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
			  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
	if(UtilValidate.isNotEmpty(productCatageoryId)){
		
		
	      
	}
	//Debug.log("==prodList==="+prodList);
	
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
	
	productCategoryQuotasMap = [:];
	if(parameters.schemeCategory && "MGPS_10Pecent".equals(parameters.schemeCategory)){
		resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId,"effectiveDate",effectiveDate));
		productCategoryQuotasMap = resultCtx.get("schemesMap");
	}	
	
	// Get Scheme Categories
	schemeCategoryIds = [];
	productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "SCHEME_MGPS"), UtilMisc.toSet("productCategoryId"), null, null, false);
	schemeCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);

	condsList = [];
	//condsList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	condsList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, schemeCategoryIds));
	/*if(effectiveDate){
		condsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
		condsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
				EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
	}*/
	prodCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(condsList,EntityOperator.AND), null, null, null, true);	  
	    
	//productCategoriesList = EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productCategoryId", true);
	  
	JSONObject productQuotaJSON=new JSONObject();
	JSONObject productCategoryJSON=new JSONObject();
	  
	for(int i=0; i<prodCategoryMembers.size(); i++){
		quota = 0;
		schemeProdId = (prodCategoryMembers.get(i)).get("productId");
		schemeCatId = (prodCategoryMembers.get(i)).get("productCategoryId");
		if(productCategoryQuotasMap.containsKey(schemeCatId)){
			if(UtilValidate.isNotEmpty(productCategoryQuotasMap.get(schemeCatId))){
				quota = productCategoryQuotasMap.get(schemeCatId);
			}
			String dateInString = "01 APRIL, 2016";
			targetDate = new java.sql.Timestamp(sdf.parse(dateInString).getTime());
			if(effectiveDate<targetDate){
				quota=parameters.manualQuota;
			}

			productQuotaJSON.put(schemeProdId, quota);
			productCategoryJSON.put(schemeProdId, schemeCatId);
		}
	  
	}
	context.productQuotaJSON = productQuotaJSON;
	context.productCategoryJSON = productCategoryJSON;
	
	