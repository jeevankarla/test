	import org.ofbiz.base.util.UtilDateTime;
	
	
	import java.sql.Timestamp;
	import java.text.SimpleDateFormat;
	
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.util.List;
	import net.sf.json.JSONObject;
	import javolution.util.FastList;
	import org.ofbiz.base.util.*;
	import net.sf.json.JSONObject;
	import net.sf.json.JSONArray;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import org.ofbiz.entity.util.EntityUtil;
	import org.ofbiz.entity.util.EntityFindOptions;
	import org.ofbiz.party.party.PartyHelper;
	import org.ofbiz.entity.GenericValue;
	import org.ofbiz.party.contact.ContactMechWorker;
	
	dctx = dispatcher.getDispatchContext();
	Map boothsPaymentsDetail = [:];
	
	partyId = userLogin.get("partyId");
	
	
	resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
	
	
	Map formatMap = [:];
	List formatList = [];
	
		for (eachList in resultCtx.get("productStoreList")) {
			
			formatMap = [:];
			formatMap.put("productStoreName",eachList.get("storeName"));
			formatMap.put("payToPartyId",eachList.get("payToPartyId"));
			formatList.addAll(formatMap);
			
		}
	context.formatList = formatList;
	
	branchId = parameters.branchId;
	
	salesChannel = parameters.salesChannelEnumId;
 
	/*salesChannel = "";
	
	if(UtilValidate.isNotEmpty(parameters.salesChannelEnumId)){
		salesChannel = parameters.salesChannelEnumId;
	}else{
	salesChannel = "BRANCH_CHANNEL";
	}*/
	searchOrderId = parameters.orderId;
	
	facilityOrderId = parameters.orderId;
	facilityDeliveryDate = parameters.estimatedDeliveryDate;
	productId = parameters.productId;
	facilityStatusId = parameters.statusId;
	facilityPartyId = parameters.partyId;
	facilityDateStart = null;
	facilityDateEnd = null;
	if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			transDate = new java.sql.Timestamp(sdf.parse(facilityDeliveryDate+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + facilityDeliveryDate, "");
		}
		facilityDateStart = UtilDateTime.getDayStart(transDate);
		facilityDateEnd = UtilDateTime.getDayEnd(transDate);
	}
	
	orderList=[];
	condList = [];
	if(UtilValidate.isNotEmpty(searchOrderId)){
		condList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.LIKE, "%"+searchOrderId + "%"));
	}
	if(UtilValidate.isNotEmpty(facilityStatusId)){
		condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS, facilityStatusId));
	}
	else{
		condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_CREATED")));
	}
	condList.add(EntityCondition.makeCondition("salesChannelEnumId" ,EntityOperator.EQUALS, salesChannel));
	condList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null)); // Review
	if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
		condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, facilityDateStart));
		condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, facilityDateEnd));
	}
	List<String> orderBy = UtilMisc.toList("-orderDate");
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	orderHeader = delegator.findList("OrderHeader", cond, null, orderBy, null ,false);
	
	orderIds = EntityUtil.getFieldListFromEntityList(orderHeader, "orderId", true);
	
	custCondList = [];
	//give preference to ShipToCustomer
	custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	if(UtilValidate.isNotEmpty(facilityPartyId)){
		custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facilityPartyId));
	}
	custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
	shipCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
	orderRoles = delegator.findList("OrderRole", shipCond, null, null, null, false);
	if(UtilValidate.isEmpty(orderRoles)){
		custCondList.clear();
		custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
		if(UtilValidate.isNotEmpty(facilityPartyId)){
			custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facilityPartyId));
		}
		custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
		custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		orderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
	}
	
	customerBasedOrderIds = EntityUtil.getFieldListFromEntityList(orderRoles, "orderId", true);
	orderHeader = EntityUtil.filterByCondition(orderHeader, EntityCondition.makeCondition("orderId", EntityOperator.IN, customerBasedOrderIds));
	
		
	custCondList.clear();
	custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	// query based on branch
	if(UtilValidate.isNotEmpty(branchId)){
		custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, branchId));
	}
	custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	billFromVendorOrderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(custCondList, EntityOperator.AND), null, null, null, false);
	
	vendorBasedOrderIds = EntityUtil.getFieldListFromEntityList(billFromVendorOrderRoles, "orderId", true);
	orderHeader = EntityUtil.filterByCondition(orderHeader, EntityCondition.makeCondition("orderId", EntityOperator.IN, vendorBasedOrderIds));
	
	orderDetailsMap=[:];
	Set partyIdsSet=new HashSet();
	orderHeader.each{ eachHeader ->
		orderId = eachHeader.orderId;
		orderParty = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		partyId = "";
		if(orderParty){
			partyId = orderParty.get(0).get("partyId");
		}
		
		billFromOrderParty = EntityUtil.filterByCondition(billFromVendorOrderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		billFromVendorPartyId = "";
		if(billFromOrderParty){
			billFromVendorPartyId = billFromOrderParty.get(0).get("partyId");
		}
		
		partyName = PartyHelper.getPartyName(delegator, partyId, false);
		tempData = [:];
		tempData.put("partyId", partyId);
		tempData.put("billFromVendorPartyId", billFromVendorPartyId);
		tempData.put("partyName", partyName);
		tempData.put("orderId", eachHeader.orderId);
		tempData.put("orderDate", eachHeader.estimatedDeliveryDate);
		tempData.put("statusId", eachHeader.statusId);
		if(UtilValidate.isNotEmpty(eachHeader.getBigDecimal("grandTotal"))){
			tempData.put("orderTotal", eachHeader.getBigDecimal("grandTotal"));
		}
		/*creditPartRoleList=delegator.findByAnd("PartyRole", [partyId :partyId,roleTypeId :"CR_INST_CUSTOMER"]);
		creditPartyRole = EntityUtil.getFirst(creditPartRoleList);
		if(UtilValidate.isNotEmpty(eachHeader.productSubscriptionTypeId)&&("CREDIT"==eachHeader.productSubscriptionTypeId) || creditPartyRole) {
			tempData.put("isCreditInstution", "Y");
		}else{
			tempData.put("isCreditInstution", "N");
		}*/
		partyIdsSet.add(partyId);
		orderList.add(tempData);
		
		isgeneratedPO="N";
		// Also check if associated order is cancelled. If cancelled show generate PO button
		exprCondList=[];
		exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
		exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
		OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
		if(OrderAss){
			isgeneratedPO="Y";
		}
		
		exprList=[];
		exprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
		EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		supplierPartyId="";
		productStoreId="";
		supplierDetails = EntityUtil.getFirst(delegator.findList("OrderRole", discontinuationDateCondition, null,null,null, false));
			
		if(supplierDetails){
			supplierPartyId=supplierDetails.get("partyId");
		}
		productStoreId=eachHeader.productStoreId;
		tempMap=[:];
		tempMap.put("supplierPartyId", supplierPartyId);
		tempMap.put("isgeneratedPO", isgeneratedPO);
		supplierPartyName="";
		if(supplierPartyId){
			supplierPartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
		}
		tempMap.put("supplierPartyName", supplierPartyName);
		tempMap.put("productStoreId", productStoreId);
		orderDetailsMap.put(orderId,tempMap);
		
	}
	context.orderDetailsMap=orderDetailsMap;
	
	//obDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(UtilDateTime.nowTimestamp()), 1));
	
	/*partyOBMap=[:];
	partyIdsSet.each{partyId->
		arPartyOB  =BigDecimal.ZERO;
		arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate:obDate, partyId:partyId]));
		if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
			arPartyOB=arOpeningBalanceRes.get("openingBalance");
		}
		//Debug.log("===============arPartyOB="+arPartyOB);
		if(arPartyOB<0){
			partyOBMap.put(partyId, arPartyOB *(-1));
		}else{
			partyOBMap.put(partyId, BigDecimal.ZERO);
		}
	}*/
	//Debug.log("===============partyOBMap="+partyOBMap+"==obDate=="+obDate);
	
	/*	context.orderList = finalFilteredList;
	 context.partyOBMap = partyOBMap;
	 */
	
	//all suppliers shipping address Json
	JSONObject supplierAddrJSON = new JSONObject();
	
	shippingPartyList = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
	if(shippingPartyList){
		shippingPartyList.each{ supplier ->
			JSONObject newObj = new JSONObject();
			//shipping details
			contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, supplier.partyId, false,"POSTAL_ADDRESS");
			if(contactMechesDetails){
				contactMec=contactMechesDetails.getLast();
				if(contactMec){
					partyPostalAddress=contactMec.get("postalAddress");
				//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
					if(partyPostalAddress){
						address1="";
						address2="";
						state="";
						city="";
						postalCode="";
						if(partyPostalAddress.get("address1")){
						address1=partyPostalAddress.get("address1")
						}
						if(partyPostalAddress.get("address2")){
							address2=partyPostalAddress.get("address2")
							}
						if(partyPostalAddress.get("city")){
							city=partyPostalAddress.get("city")
							}
						if(partyPostalAddress.get("state")){
							state=partyPostalAddress.get("state")
							}
						if(partyPostalAddress.get("postalCode")){
							postalCode=partyPostalAddress.get("postalCode")
							}
						newObj.put("address1",address1);
						newObj.put("address2",address2);
						newObj.put("city",city);
						newObj.put("postalCode",postalCode);
						supplierAddrJSON.put(supplier.partyId,newObj);
					}
				}
			}
		}
	}
	context.supplierAddrJSON=supplierAddrJSON;
	
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
	
	orderIdsList = [];
	orderPreferenceMap = [:];
	paymentSatusMap = [:];
	
	
	JSONObject eachPaymentOrderMap = new JSONObject();
	
	
	//  eachPaymentOrderMap = [:];
	
	for (eachList in orderList) {
	
		   condtList = [];
		   condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, eachList.orderId));
		   cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
		   OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
		   getFirstOrderPayment = EntityUtil.getFirst(OrderPaymentPreference);
		   
		   orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
		   
		  if(UtilValidate.isNotEmpty(orderPreferenceIds)){
		   
		   orderPreferenceMap.put(eachList.orderId, orderPreferenceIds[0]);
		   conditonList = [];
		   conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN, orderPreferenceIds));
		   conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL,"PMNT_VOID"));
		   cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
		   PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
		   
		   
		   totAmount = 0;
		   tempMap = [:];
		   
		   JSONObject allOrderPayments = new JSONObject();
		   
		   
		 //  allOrderPayments = [:];
		   
		   if(UtilValidate.isNotEmpty(PaymentList)){
		   
			   JSONArray orderPaymentList = new JSONArray();
			   
			     for (eachpayment in PaymentList) {
					 
				      totAmount = totAmount+eachpayment.get("amount");
					 
					  allOrderPayments.put("paymentPreferenceId",eachpayment.get("paymentPreferenceId"));
					  allOrderPayments.put("amount",eachpayment.get("amount"));
					  
					  orderPaymentPreferenceList = delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", eachpayment.get("paymentPreferenceId")), false);
					  
					  statusId  = orderPaymentPreferenceList.get("statusId");
					  
					  allOrderPayments.put("statusId",statusId);
					  
					  orderPaymentList.add(allOrderPayments);
					  
					  }
				 
			      PaymentStatusList = EntityUtil.getFieldListFromEntityList(PaymentList,"statusId", true);
				  
				  if(PaymentStatusList.contains("PMNT_CONFIRMED")){
					  tempMap.put("statusId", "PMNT_CONFIRMED");
				  }
				  else{
					  tempMap.put("statusId", "PMNT_RECEIVED");
				  }
				     tempMap.put("amount", totAmount);
					 
					 eachPaymentOrderMap.put(eachList.orderId, orderPaymentList);
			   
		   }else{
				   tempMap = [:];
				   tempMap.put("statusId", "NotReceived");
				   tempMap.put("amount", 0);
				   paymentSatusMap.put(eachList.orderId, tempMap);
				   //allOrderPayments = [:];
				   
				   JSONObject allOrderPayments1 = new JSONObject();
				   
				   allOrderPayments1.put("paymentPreferenceId","empty");
				   allOrderPayments1.put("amount",-1);
				   allOrderPayments1.put("statusId","empty");
				   
				   //allOrderPayments.put("NoPreferenceId", "NillAmout");
				   eachPaymentOrderMap.put(eachList.orderId, allOrderPayments1);
				   
		   }
		   paymentSatusMap.put(eachList.orderId, tempMap);
		  }
		  else{
			  
			  tempMap = [:];
			  tempMap.put("statusId", "NotReceived");
			  tempMap.put("amount", 0);
			  paymentSatusMap.put(eachList.orderId, tempMap);
			  
			 /* allOrderPayments = [:];
			  allOrderPayments.put("NoPreferenceId", "NillAmout");*/
			  
			  JSONObject allOrderPayments2 = new JSONObject();
			  
			  allOrderPayments2.put("paymentPreferenceId","empty");
			  allOrderPayments2.put("amount",-1);
			  allOrderPayments2.put("statusId","empty");
			  
			  eachPaymentOrderMap.put(eachList.orderId, allOrderPayments2);
		  }
	}
	
	condtList = [];
	condtList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS, "MONEY"));
	cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
	PaymentMethodType = delegator.findList("PaymentMethodType", cond, UtilMisc.toSet("paymentMethodTypeId","description"), null, null ,false);
	
	
	allOrderIds = eachPaymentOrderMap.keySet();
	
	statusConfirmMap = [:];
	
	for (eachOrderId in allOrderIds) {
		preferenceList = eachPaymentOrderMap.get(eachOrderId);
		statusList = [];
		if((preferenceList.amount)!=-1){
			for (eachList in preferenceList) {
				statusList.add(eachList.get("statusId"));
			}
		}
		else{
			statusList.add("PaymentNotDone");
		}
		if(statusList.contains("PMNT_RECEIVED")){
			statusConfirmMap.put(eachOrderId, "visible");
		}
		else{
			statusConfirmMap.put(eachOrderId, "NotVisible");
		}
	}
	
	context.statusConfirmMap = statusConfirmMap;
	context.eachPaymentOrderMap = eachPaymentOrderMap;
	context.PaymentMethodType = PaymentMethodType;
	context.orderPreferenceMap = orderPreferenceMap;
	context.paymentSatusMap = paymentSatusMap;
	
	sortedOrderMap =  [:]as TreeMap;
	for (eachList in orderList) {
		sortedOrderMap.put(eachList.orderId, eachList);
	}
	Collection allValues = sortedOrderMap.values();
	List basedList = [];
	basedList.addAll(allValues);
	
	context.orderList = basedList.reverse();
	
	
	//context.partyOBMap = partyOBMap;