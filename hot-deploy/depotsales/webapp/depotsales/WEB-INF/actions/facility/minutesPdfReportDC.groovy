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

context.partyName = parameters.partyName;
context.indentId=parameters.orderId;
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
 
 context.orderDate = orderDate;
 context.externalOrderId = externalOrderId;
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
}else if(branchId == "INT8" || branchId == "INT16" || branchId == "INT20" || branchId == "INT21" ||  branchId == "INT44"){
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
advacneAmtList=[];
paymentAmt=0;
paymentDetailList.each{eachPayment->
	BigDecimal amount=eachPayment.amount;
	paymentAmt=paymentAmt+amount;
	if((eachPayment) && (eachPayment.paymentRefNum)){
		paymentRefNum = eachPayment.paymentRefNum;
		paymentRefNumList.add(paymentRefNum);
	}
	advacneAmtList.add(amount);
}
context.paymentAmt=paymentAmt;
context.advacneAmtList=advacneAmtList;
context.paymentRefNumList = paymentRefNumList;
totAmt = 0;


/*if(UtilValidate.isNotEmpty(orderPreferenceIds)){
conditonList = [];
conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN, orderPreferenceIds));
conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL,"PMNT_VOID"));
cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
  if(UtilValidate.isNotEmpty(PaymentList)){
   for (eachPayment in PaymentList) {
	   totAmt = totAmt+eachPayment.amount;
	  }
   }
 }
*/

/*inputCtx = [:];
inputCtx.put("userLogin",userLogin);
inputCtx.put("partyId", partyId);
try{
 resultCtx = dispatcher.runSync("getWeaverPayments", inputCtx);
 partyPayments = (resultCtx.paymentSearchResults).paymentSearchResultsList;
 
 if(partyPayments){
 for (eachPayment in partyPayments) {
	 totAmt = totAmt+eachPayment.balanceAmount;
	}
 }
}catch(Exception e){}

*/

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
/*
condtList.clear();
condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, parameters.orderId));
condtList.add(EntityCondition.makeCondition("orderAdjustmentTypeId" ,EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));//
cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
OrderAdjustmentList = delegator.findList("OrderAdjustment", cond, null, null, null ,false);

Scheam = "";


if(UtilValidate.isNotEmpty(OrderAdjustmentList)){
	
	
	Scheam = "Sales Under SchemeMGP 10% Scheme";
}
else{
	
	Scheam = "MGPS Scheme";
}

context.Scheam =Scheam;
*/
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
			
		/*	for (eachOrderItem in OrderItemList) {
				
				conditionList1=[];
					
					
				conditionList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderItem.orderId));
				
				//conditionList1.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
				conditionList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachOrderItem.orderItemSeqId));
				
				condExpr = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
				orderItemAttr = delegator.findList("OrderItem", condExpr, null, null, null, false);
				if((orderItemAttr) && (orderItemAttr.get(0).attrValue)){
					remarkMap.put(eachOrderItem.orderItemSeqId, orderItemAttr.get(0).attrValue);
					
				}
				 tempMap = [:];
				 productName = ""
				 prod=delegator.findOne("Product",[productId:eachOrderItem.productId],false);
				 
				 changeDatetime = eachOrderItem.changeDatetime;
				 context.changeDatetime = changeDatetime;
				 
				 unitPrice = eachOrderItem.unitPrice;
				 context.unitPrice = unitPrice;
				 
				 String srNoStr = SrNo;
				 char firSrno = srNoStr.charAt(0);
				 srNoStr = String.valueOf(firSrno)+srNoStr;
				 tempMap.put("SrNo", srNoStr);
				 
				 if(UtilValidate.isNotEmpty(prod)){
					 productName = prod.get("productName");
					 String productNameStr = productName;
					 char firstProChr = productNameStr.charAt(0);
					 productNameStr = String.valueOf(firstProChr)+productNameStr;
					 tempMap.put("productName", productNameStr);
					 
				 }else{
					 tempMap.put("productName", productName);
				 }
				
				 
				 
				 String qtyStr = Math.round(eachOrderItem.get("quantity"));
				 char firstChr = qtyStr.charAt(0);
				 qtyStr = String.valueOf(firstChr)+qtyStr;
				 tempMap.put("quantity", qtyStr);
				 
				 String unitStr = Math.round(eachOrderItem.get("unitPrice"));
				 char firstUChr = unitStr.charAt(0);
				 unitStr = String.valueOf(firstUChr)+unitStr;
				 
				 tempMap.put("unitPrice", unitStr);
				 
				  double annum = 0;
				 if(UtilValidate.isNotEmpty(eachOrderItem.get("quantity"))&& UtilValidate.isNotEmpty(eachOrderItem.get("unitPrice"))){
				 
					 annum = (double) (eachOrderItem.get("unitPrice")*eachOrderItem.get("quantity"));
					
					 String anumtr = String.valueOf(Math.round(annum));
					 char firstAnnuChr = anumtr.charAt(0);
					 anumtr = String.valueOf(firstAnnuChr)+anumtr;
					 
					  tempMap.put("annum", anumtr);
				 }else{
				 tempMap.put("annum", 0);
				 }
				 
				 totannum = totannum+annum;
				 totQuantity = totQuantity+eachOrderItem.get("quantity");
				 orderedHindiItemList.add(tempMap);
				 
				 
				 SrNo = SrNo+1;
				 
			}*/
			
			totalsMap = [:];
			
			/*String totQuantityStr = String.valueOf(Math.round(totQuantity));
			char firstTOTQChr = totQuantityStr.charAt(0);
			totQuantityStr = String.valueOf(firstTOTQChr)+totQuantityStr;
			totalsMap.put("totQuantity", totQuantityStr);
			
			String totannumStr = String.valueOf(Math.round(totannum));
			char firstTOTANNUQChr = totannumStr.charAt(0);
			totannumStr = String.valueOf(firstTOTANNUQChr)+totannumStr;
			
			totalsMap.put("totannum", totannumStr);
			
			totalsList.add(totalsMap);
			
			Map<String, Object> orderDtlMap = FastMap.newInstance();
			orderDtlMap.put("orderId", parameters.orderId);
			orderDtlMap.put("userLogin", userLogin);
			result = dispatcher.runSync("getOrderItemSummary",orderDtlMap);
			
			
			//Debug.log("result================="+result)
			
			
			if(ServiceUtil.isError(result)){
				Debug.logError("Unable get Order item: " + ServiceUtil.getErrorMessage(result), module);
				return ServiceUtil.returnError(null, null, null,result);
			}
			productSummaryMap=result.get("productSummaryMap");
			Iterator eachProductIter = productSummaryMap.entrySet().iterator();
			while(eachProductIter.hasNext()) {
				Map.Entry entry = (Entry)eachProductIter.next();
				String productId = (String)entry.getKey();
				 productSummary=entry.getValue();
				 quantity=productSummary.get("quantity");
				 unitPrice=productSummary.get("unitListPrice");
				 bedPercent=productSummary.get("bedPercent")
				 cstPercent=productSummary.get("cstPercent")
				 vatPercent=productSummary.get("vatPercent")
				 itemSeqList=productSummary.get("itemSeqList");
				 bundleWeight=productSummary.get("bundleQuantity");
				 unit=productSummary.get("Unit");
				 bundleUnitListPrice=productSummary.get("bundleUnitListPrice");
				 //Debug.log("bundleWeight================="+bundleWeight+"========"+unit);
				 baleqty=0;
				 if(bundleWeight && bundleWeight!="0"){
					 if("Bale".equals(unit)){
						 baleqty=bundleWeight/40;
					 }else if("Half-Bale".equals(unit)){
						 baleqty=bundleWeight/20;
					 }else{
						 baleqty=bundleWeight;
					 }
				 }
				 conditionList1=[];
				 conditionList1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
				// conditionList1.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
				 conditionList1.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.IN, itemSeqList));
				 condExpr = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
				 orderItemAttr = delegator.findList("OrderItem", condExpr, null, null, null, false);
				 AttrName="";
				 double schemeAmt = 0;
				 orderItemAttr.each{ eachAttr ->
					 AttrName=eachAttr.attrValue;
					 remarkMap.put(eachAttr.orderItemSeqId, eachAttr.attrValue);
					 if(eachAttr.attrName == "quotaQty"){
						 schemeAmt =  schemeAmt+Double.valueOf(eachAttr.attrValue);
					 }
					 if(eachAttr.attrName == "REMARKS"){
						 AttrName =  schemeAmt+Double.valueOf(eachAttr.attrValue);
					 }
					 
					// schemeAmt = eachAttr
					 
					 
					 
				 }
				 Map tempMap = [:];
				  productName = ""
				  prod=delegator.findOne("Product",[productId:productId],false);
				  tempMap.put("productId", productId);
				  tempMap.put("remarks", AttrName);
				  tempMap.put("quantity", quantity);
				  tempMap["Unit"]=unit;
				  tempMap["baleqty"]=baleqty;
				  tempMap["bundleUnitListPrice"]=bundleUnitListPrice;
				  mgpsQty =0;
				  if(quantity > schemeAmt)
				  mgpsQty = schemeAmt;
				  else
				  mgpsQty = quantity;
				  tempMap.put("mgpsQty", mgpsQty);
				  changeDatetime = eachOrderItem.changeDatetime;
				  context.changeDatetime = changeDatetime;
				  unitPrice=productSummary.get("unitListPrice");
				  context.unitPrice = unitPrice;
				  if(unitPrice)
				  tempMap.put("unitPrice", unitPrice);
				  else
				  tempMap.put("unitPrice", "");
				  String srNoStr = SrNo;
				  char firSrno = srNoStr.charAt(0);
				  srNoStr = String.valueOf(firSrno)+srNoStr;
				  tempMap.put("SrNo", srNoStr);
				  
				  if(UtilValidate.isNotEmpty(prod)){
					  productName = prod.get("productName");
					  String productNameStr = productName;
					  char firstProChr = productNameStr.charAt(0);
					  productNameStr = String.valueOf(firstProChr)+productNameStr;
					  tempMap.put("productName", productNameStr);
				  }else{
					  tempMap.put("productName", productName);
				  }
				  double annum = 0;
				  if(UtilValidate.isNotEmpty(quantity)&& UtilValidate.isNotEmpty(unitPrice)){
				  
					  annum = (double) (unitPrice*quantity);
					 
					  String anumtr = String.valueOf(Math.round(annum));
					  char firstAnnuChr = anumtr.charAt(0);
					  anumtr = String.valueOf(firstAnnuChr)+anumtr;
					  
					   tempMap.put("annum", anumtr);
				  }else{
				  tempMap.put("annum", 0);
				  }
				  totannum = totannum+annum;
				  if(quantity)
				  totQuantity = totQuantity+quantity;
				  orderedHindiItemList.add(tempMap);
				  SrNo = SrNo+1;
				 OrderItems.add(tempMap);
			}
			
		*/
			
			
			/*if(partyPostalAddress){
				
			   if(partyPostalAddress.address1){
				   address1=partyPostalAddress.address1;
			   }
			   tempMap=[:];
			   tempMap.put("key1","Road / Street / Lane");
			   tempMap.put("key2",address1);
			   finalAddresList.add(tempMap);
			   if(partyPostalAddress.address2){
				   address2=partyPostalAddress.address2;
			   }
			   tempMap=[:];
			   tempMap.put("key1","Area / Locality");
			   tempMap.put("key2",address2);
			   finalAddresList.add(tempMap);
			   if(partyPostalAddress.city){
				   
				   city=partyPostalAddress.city;
			   }
			   tempMap=[:];
			   tempMap.put("key1","Town / District / City");
			   tempMap.put("key2",city);
			   finalAddresList.add(tempMap);
			   
			   if(partyPostalAddress.postalCode){
				   postalCode=partyPostalAddress.postalCode;
			   }
			   tempMap=[:];
			   tempMap.put("key1","PIN Code");
			   tempMap.put("key2",postalCode);
			   finalAddresList.add(tempMap);
			   
			}*/
			
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItem = delegator.findList("OrderItem", condExpr, null, null, null, false);
			double priceDiscountAmount=0;
			for (eachItem in OrderItem) {
				
				
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
				double packQuantity=0;
				double packets=0;
				
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
					 
					 /*if(OrderAdjustment[0]){
					 if(UtilValidate.isNotEmpty(OrderAdjustment[0].orderAdjustmentTypeId))
					 {
					 if(OrderAdjustment[0].orderAdjustmentTypeId == "SERVICE_CHARGE"){
						 if(OrderAdjustment[0].amount){
						   serviceAmount=OrderAdjustment[0].amount;
						   sourcePercentage = OrderAdjustment[0].sourcePercentage;
						 }
					}
					 }
					 
					 }
					*/
					 
					 if(OrderAdjustment){
					 
					 for (eachAdjust in OrderAdjustment) {
						
						 if(eachAdjust.orderAdjustmentTypeId == "SERVICE_CHARGE"){
							 if(eachAdjust.amount){
							   serviceAmount=eachAdjust.amount;
							   sourcePercentage = eachAdjust.sourcePercentage;
							 }
						}
						 
						 if(eachAdjust.orderAdjustmentTypeId == "PRICE_DISCOUNT"){
							 if(eachAdjust.amount){
								 priceDiscountAmount=priceDiscountAmount+eachAdjust.amount;
								 //sourcePercentage = eachAdjust.sourcePercentage;
							   }
							 
						 }
					}
					 
					 
				 }
					 
					 
					 
					 
				 }
				
				 //Debug.log("priceDiscountAmount=======@@@@@========"+priceDiscountAmount);
				 
				for (eachOrderItem in OrderItemDetail) {
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
				  
				  if(OrderItemDetail[0]){
					  
					 if(OrderItemDetail[0].packQuantity){
					  tempMap.put("packQuantity", OrderItemDetail[0].packQuantity);
					 }
					 else{
					  tempMap.put("packQuantity",packQuantity);
					 }
					  if(OrderItemDetail[0].packQuantity){
					  tempMap.put("packets", OrderItemDetail[0].packets);
					  }
					  else{
					  tempMap.put("packets",packets);
					  }
					 				  
				  }
				  
				/*if(eachOrderItem.quotaQuantity)
				  quotaQuantity = Double.valueOf(eachOrderItem.quotaQuantity);*/
				  
				  
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
			context.priceDiscountAmount = priceDiscountAmount;
			//Debug.log("OrderItems================="+OrderItems);
			
						
		/*	conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderAdjustment = delegator.findList("OrderAdjustment", condExpr, null, null, null, false);
			
			OrderAdjustmentWithOutSubsidy = EntityUtil.filterByCondition(OrderAdjustment, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "TEN_PERCENT_SUBSIDY"));
			orderAdjustmentTypeIds = EntityUtil.getFieldListFromEntityList(OrderAdjustmentWithOutSubsidy,"orderAdjustmentTypeId", true);
			
			Debug.log("OrderAdjustment================="+OrderAdjustment);
	*/
			typeBasedMap = [:];
			
/*			for (eachType in orderAdjustmentTypeIds) {
				eachTypeOrderAdjustment = EntityUtil.filterByCondition(OrderAdjustmentWithOutSubsidy, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, eachType));
					  adjustmentAmounts = EntityUtil.getFieldListFromEntityList(eachTypeOrderAdjustment,"sourcePercentage", true);
					  
				sameTypeAmount = 0;
				SAmeAmountMap = [:];
				 for (eachAmount in adjustmentAmounts) {
					 eachTypeOrderAdjustmentBasedOnAmount = EntityUtil.filterByCondition(eachTypeOrderAdjustment, EntityCondition.makeCondition("sourcePercentage", EntityOperator.EQUALS, eachAmount));
					  
						tempMap = [:];
					   for (eachAdjList in eachTypeOrderAdjustmentBasedOnAmount) {
						  sameTypeAmount = sameTypeAmount+Double.valueOf(eachAdjList.amount);
					}
					   
					  SAmeAmountMap.put(eachAmount, sameTypeAmount);
				}
				 
				 OrderAdjustmentType = delegator.findOne("OrderAdjustmentType", [orderAdjustmentTypeId : eachType], false);
				 
				 description = "";
				 if(OrderAdjustmentType)
				  description = OrderAdjustmentType.description;
				 else
				 description =eachType;
				 
				 typeBasedMap.put(description.trim(), SAmeAmountMap);
				 
			}*/
			
			
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
	Debug.log("OrderItemList================="+OrderItemList);
	/*contextMap = UtilMisc.toMap("translateList", orderedHindiItemList);
	dayWiseEntriesLidast = (ByProductNetworkServices.icu4JTrans(dctx, contextMap)).getAt("translateList");
	
	contextMap = UtilMisc.toMap("translateList", totalsList);
	totalsHindiList = (ByProductNetworkServices.icu4JTrans(dctx, contextMap)).getAt("translateList");
	
	context.totalsHindiList = totalsHindiList[0];
	context.dayWiseEntriesLidast = dayWiseEntriesLidast;*/