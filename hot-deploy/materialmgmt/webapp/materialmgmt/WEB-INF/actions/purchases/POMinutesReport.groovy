import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;

rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
dctx = dispatcher.getDispatchContext();

context.orderId=parameters.orderId;
partyId = parameters.partyId;
consList=[];
consList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
/*conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));*/
condEXr = EntityCondition.makeCondition(consList, EntityOperator.AND);
orderItemAttr = delegator.findList("OrderItemAttribute", condEXr, null, null, null, false);
 orderHeaderList = delegator.findOne("OrderHeader", [orderId : parameters.orderId], false);
 heldOnDate = "";
 if(orderHeaderList.get("statusId") == "APPROVE_LEVEL3"){
	 heldOnDate = orderHeaderList.get("lastUpdatedStamp");
 }
context.heldOnDate = heldOnDate;
 orderDate = orderHeaderList.get("orderDate");
 
 externalOrderId = orderHeaderList.get("externalId");
 originFacilityId=orderHeaderList.get("originFacilityId");
 
 context.orderDate = orderDate;
 context.externalOrderId = externalOrderId;
 facilityName="";
 FacilityList=delegator.findOne("Facility",[facilityId:originFacilityId],false);
 facilityName=FacilityList.facilityName;
 context.facilityName = facilityName;
 allDetailsMap = [:];
grandTOt = orderHeaderList.get("grandTotal");
productStoreId = orderHeaderList.get("productStoreId");
branchId="";
if (productStoreId) {
	productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
	branchId=productStore.payToPartyId;
}
//get Report Header
branchContext=[:];

if(branchId == "INT12" || branchId == "INT49" || branchId == "INT55"){
	branchId = "INT12";
}else if(branchId == "INT8" || branchId == "INT16" || branchId == "INT20" || branchId == "INT21" || branchId == "INT22" || branchId == "INT44"){
   branchId = "INT8";
}

branchContext.put("branchId",branchId);
BOAddress="";
BOEmail="";

try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
	if(resultCtx.get("boHeaderMap")){
		boHeaderMap=resultCtx.get("boHeaderMap");
		
		if(boHeaderMap.get("header0")){
			BOAddress=boHeaderMap.get("header0");
			
		}
		if(boHeaderMap.get("header1")){
			BOEmail=boHeaderMap.get("header1");
		}
	}
}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;

condList = [];
condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
orderRoleList = delegator.findList("OrderRole", condition,null, null, null, false);
orderRoleList.each{eachparty ->
partyId = eachparty.partyId;
}
String partyName = PartyHelper.getPartyName(delegator,partyId,false);
context.put("partyName",partyName);
context.put("abstPartyName",partyName);

condList = [];
condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "SUPPLIER_%"));
condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
orderRoleList = delegator.findList("OrderRole", condition,null, null, null, false);
supplierpartyId="";
orderRoleList.each{eachparty ->
supplierpartyId = eachparty.partyId;
}
supplierpartyName="";
if(supplierpartyId){
	 supplierpartyName = PartyHelper.getPartyName(delegator, supplierpartyId, false);
}
context.put("supplierpartyName",supplierpartyName);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

FacilityList = delegator.findList("Facility", fcond, null, null, null, false);

isDepot = "";
if(FacilityList)
isDepot ="Y";
else
isDepot ="N";

context.isDepot = isDepot;

context.orderId = parameters.orderId;
orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , parameters.orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(orderHeaderSequences)){
	orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
	context.orderId = orderSeqDetails.orderNo;
}

condtList = [];
condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, parameters.orderId));
cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
getFirstOrderPayment = EntityUtil.getFirst(OrderPaymentPreference);
orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);

conditionList1 = [];
OrderPreferencePaymentApplicationDetailList = [];
conditionList1.add(EntityCondition.makeCondition("orderPaymentPreferenceId" ,EntityOperator.IN,	orderPreferenceIds));
cond1 = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
OrderPreferencePaymentApplicationDetailList = delegator.findList("OrderPaymentPreference", cond1, null, null, null ,false);
orderPaymentPreferenceIdS = EntityUtil.getFieldListFromEntityList(OrderPreferencePaymentApplicationDetailList,"orderPaymentPreferenceId", true);

conditionList2 = [];
paymentDetailList = [];
 
conditionList2.add(EntityCondition.makeCondition("paymentId" ,EntityOperator.IN,orderPaymentPreferenceIdS));
cond2 = EntityCondition.makeCondition(conditionList2, EntityOperator.AND);
paymentDetailList = delegator.findList("Payment", cond2, null, null, null ,false);
paymentRefNumList =[];
paymentDetailList.each{eachPayment->
	if((eachPayment) && (eachPayment.paymentRefNum)){
		paymentRefNum = eachPayment.paymentRefNum;
		paymentRefNumList.add(paymentRefNum);
	}
	
}
context.paymentRefNumList = paymentRefNumList;
totAmt = 0;
balanceAmt = 0;
context.totAmt=totAmt;
balanceAmt = grandTOt-totAmt;
context.balanceAmt = balanceAmt;

orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId), null, null, null, false);

scheme = "";
if(UtilValidate.isNotEmpty(orderAttr)){
	orderAttr.each{ eachAttr ->
		if(eachAttr.attrName == "SCHEME_CAT"){
			scheme =  eachAttr.attrValue;
		}
		
	}
   }
context.scheme = scheme;

			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemList = delegator.findList("OrderItem", condition, null, null, null, false);
			productIds = EntityUtil.getFieldListFromEntityList(OrderItemList, "productId", true);
			products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			//conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER"));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderRoleList = delegator.findList("OrderRole", condition,null, null, null, false);
			supperPartyDetails = EntityUtil.filterByCondition(orderRoleList, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
			
			supplierPartyId = "";
			supplierHindiPartyId = "";
			if(UtilValidate.isNotEmpty(supperPartyDetails)){
			   supplierPartyId = supperPartyDetails[0].get("partyId");
			   supplierHindiPartyId = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, supplierPartyId, false);
			}
			
			if(UtilValidate.isEmpty(partyId)){
				orderRoleAgencyList = EntityUtil.filterByCondition(orderRoleList, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
				if(UtilValidate.isNotEmpty(orderRoleAgencyList)){
					partyId=orderRoleAgencyList[0].partyId;
				}
			}
			billFromPartyDetails = EntityUtil.filterByCondition(orderRoleList, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
			billFromPartyDetails = EntityUtil.getFirst(billFromPartyDetails);
			boPartyId = billFromPartyDetails.partyId;
			partyIdentification = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , boPartyId)  , null, null, null, false );
			if(UtilValidate.isNotEmpty(partyIdentification)){
				tinNumber="";
				tinDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
				if(UtilValidate.isNotEmpty(tinDetails)){
					tinDetails=EntityUtil.getFirst(tinDetails);
					tinNumber=tinDetails.idValue;
					allDetailsMap.put("tinNumber",tinNumber);
				}
				cstNumber="";
				cstDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
				if(UtilValidate.isNotEmpty(cstDetails)){
					cstDetails=EntityUtil.getFirst(cstDetails);
					cstNumber=cstDetails.idValue;
					allDetailsMap.put("cstNumber",cstNumber);
				}
				cinNumber="";
				cinDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CIN_NUMBER"));
				if(UtilValidate.isNotEmpty(cinDetails)){
					cinDetails=EntityUtil.getFirst(cinDetails);
					cinNumber=cinDetails.idValue;
					allDetailsMap.put("cinNumber",cinNumber);
				}
				panNumber="";
				panDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PAN_NUMBER"));
				if(UtilValidate.isNotEmpty(panDetails)){
					panDetails=EntityUtil.getFirst(panDetails);
					panNumber=panDetails.idValue;
					allDetailsMap.put("panNumber",panNumber);
				}
			}
			context.allDetailsMap= allDetailsMap;
			context.supplierPartyId = supplierPartyId;
			
			context.supplierHindiPartyId = supplierHindiPartyId;
			
			orderedHindiItemList = [];
			
			totalsList = [];
			
			totQuantity = 0;
			totannum = 0;
			
			SrNo = 1;
			remarkMap=[:];
			
			OrderItems=[];
			totalsMap = [:];
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItem = delegator.findList("OrderItem", condExpr, null, null, null, false);

			 for(int a=0; a < OrderItem.size();a++){
				 	 orderItem = OrderItem.get(a);
					 eachItem=[];
					 eachItem=orderItem;
				tempMap = [:];
				tempMap.put("productId", eachItem.productId);
				productName = ""
				prod=delegator.findOne("Product",[productId:eachItem.productId],false);
				
				if(prod.get("productName"))
					tempMap.put("productName", prod.get("productName"));
				else
					tempMap.put("productName", "");
					
				tempMap.put("quantity", eachItem.quantity);
								
				double tenPerQty = 0;
				
				double quantity = 0;
				double quotaQuantity = 0;
				double baleQuantity = 0;
				double bundleUnitPrice = 0;
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachItem.orderId));
				conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
				condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				OrderItemDetail = delegator.findList("OrderItemDetail", condExpr, null, null, null, false);
	
				//eachOrderItem = OrderItemDetail[0];
				
				double serviceAmount = 0;
				double sourcePercentage = 0;
				
				 if(scheme == "General"){
					 conditionList.clear();
					 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachItem.orderId));
					 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
					 condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					 OrderAdjustment = delegator.findList("OrderAdjustment", condExpr, null, null, null, false);
					
					 if(OrderAdjustment){
					 for(int i=0; i< OrderAdjustment.size();i++){
							 orderAdjustment = OrderAdjustment.get(i);
							eachAdjust=[];
							eachAdjust=orderAdjustment;
						
						 if(eachAdjust.orderAdjustmentTypeId == "SERVICE_CHARGE"){
							 if(eachAdjust.amount){
							   serviceAmount=eachAdjust.amount;
							   sourcePercentage = eachAdjust.sourcePercentage;
							 }
						}
						 
					}
					 
				 }
					 
			 }
				for(int j=0; j< OrderItemDetail.size();j++){
						orderItemDetail = OrderItemDetail.get(j);
					   eachOrderItem=[];
					   eachOrderItem=orderItemDetail;
					quotaQuantity = quotaQuantity+Double.valueOf(eachOrderItem.quotaQuantity);
					baleQuantity = baleQuantity+Double.valueOf(eachOrderItem.baleQuantity);
					bundleUnitPrice = bundleUnitPrice+Double.valueOf(eachOrderItem.bundleUnitPrice);
				}
				
				if(eachItem.quantity)
				  quantity = Double.valueOf(eachItem.quantity);
				
				  if(OrderItemDetail[0]){
					  
					 if(OrderItemDetail[0].remarks)
					  tempMap.put("remarks", OrderItemDetail[0].remarks);
					 else
					 tempMap.put("remarks", "");
				  
				  }else{
				  tempMap.put("remarks", "");
				  }
			  if(scheme != "General"){
					  if(quantity > quotaQuantity)
					  {
						tempMap.put("tenPerQty", quotaQuantity);
						tenPerQty = quotaQuantity;
					  }
					  else
					  {
						tempMap.put("tenPerQty", quantity);
						tenPerQty = quantity;
					  }
				  }
				//mgps Qty
				  
				  if(scheme == "General")
				  tempMap.put("tenPerQty", 0);
				 
				  tempMap.put("baleQuantity", baleQuantity);
				  tempMap.put("bundleUnitPrice", bundleUnitPrice);


				  if(scheme == "General")
				  tempMap.put("mgpsQty", 0);
				  else
				  tempMap.put("mgpsQty", quantity-tenPerQty);
				  
				  if(scheme == "General"){
				  double perAmt = (eachItem.unitPrice*sourcePercentage)/100;
				  tempMap.put("unitPrice",(eachItem.unitPrice+perAmt));
				  }else{
				  tempMap.put("unitPrice", eachItem.unitPrice);
				  }
				  
				  tempMap.put("PurunitPrice", eachItem.unitPrice);
				  if(UtilValidate.isNotEmpty(OrderItemDetail[0])){
					  
					  if(OrderItemDetail[0].Uom)
					  tempMap.put("Uom", "/"+OrderItemDetail[0].Uom);
					  else
					  tempMap.put("Uom", "/KGs");
					  
				  }else{
				  tempMap.put("Uom", "/KGs");
				  }
			   //purChasesVal;
				  
				  tempMap.put("totalPurCost", (eachItem.quantity*eachItem.unitPrice));
				  
				  tempMap.put("totalCost", (eachItem.quantity*eachItem.unitPrice)+serviceAmount);
				  
				  OrderItems.add(tempMap);
			}
			//Debug.log("OrderItems================="+OrderItems);

			typeBasedMap = [:];
			
			finalAddresList=[];
			address1="";
			address2="";
			city="";
			postalCode="";
			panId="";
			tanId="";
			supplierPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:supplierPartyId, userLogin: userLogin]);
			weaverPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
			SupplierCity=supplierPostalAddress.city;
			weaverCity=weaverPostalAddress.city;
			context.SupplierCity = SupplierCity;
			context.weaverCity = weaverCity;
			
	context.finalAddresList = finalAddresList;
	context.OrderItemList = OrderItems;
	context.remarkMap=remarkMap;
	context.orderedHindiItemList = orderedHindiItemList;
	context.typeBasedMap = typeBasedMap;
	