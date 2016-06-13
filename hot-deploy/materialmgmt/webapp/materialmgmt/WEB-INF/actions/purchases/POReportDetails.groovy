import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.*;
import java.math.RoundingMode;
import org.ofbiz.party.contact.ContactMechWorker;
import java.util.Map.Entry;

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();
orderId = parameters.orderId;
signature = parameters.sign;
context.signature=signature;

orderDetailsList=[];
allDetailsMap=[:];
orderTermList=[];


partyName = parameters.partyName;
context.partyName = partyName;
partyId = parameters.partyId;
context.partyId = partyId;

condtAdjList = [];
condtAdjList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,orderId));
cond2 = EntityCondition.makeCondition(condtAdjList, EntityOperator.AND);
OrderHeaderList = delegator.findList("OrderItemAndAdjustment", cond2, null, null, null ,false);
condtList = [];
condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,orderId));
cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
OrderHeaderList = delegator.findList("OrderHeader", cond, null, null, null ,false);

orderDate = OrderHeaderList[0].get("orderDate");
productStoreId = OrderHeaderList[0].get("productStoreId");
branchId="";
if (productStoreId) {
	productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
	branchId=productStore.payToPartyId;
	
}
//get Report Header
branchContext=[:];
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


context.orderDate = orderDate;

condtList = [];
condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,orderId));
cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);

orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);

total = 0

if(UtilValidate.isNotEmpty(orderPreferenceIds)){

conditonList = [];
conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN, orderPreferenceIds));
conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL,"PMNT_VOID"));
cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
PaymentList = delegator.findList("Payment", cond, null, null, null ,false);

if(UtilValidate.isNotEmpty(PaymentList)){
for (eachpayment in PaymentList) {
	total = total+eachpayment.amount;
}
}

}


context.payment = total;

poSquenceNo="";
poOrderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(poOrderHeaderSequences)){
	poOrderSeqDetails = EntityUtil.getFirst(poOrderHeaderSequences);
	poSquenceNo = poOrderSeqDetails.orderNo;
}
allDetailsMap.put("poSquenceNo",poSquenceNo);
allDetailsMap.put("orderId",orderId);
allDetailsMap["total"]=BigDecimal.ZERO;
allDetailsMap["grandTotal"]=BigDecimal.ZERO;
roundedGrandTotal=BigDecimal.ZERO;
orderHeader=null;
orderDesctioption="";
if (orderId) {
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	orderDesctioption=orderHeader.orderName;
	context.hasPermission = true;
	context.canViewInternalDetails = true;
	if(UtilValidate.isNotEmpty(orderHeader)){
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	orderAdjustments = orderReadHelper.getAdjustments();
	grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	roundedGrandTotal=grandTotal.setScale(0,rounding);
	allDetailsMap["grandTotal"] = grandTotal;
	statusId = orderHeader.statusId;
	context.statusId=statusId;
 }}
context.roundedGrandTotal=roundedGrandTotal;
context.orderDesctioption=orderDesctioption;
// orderDate
orderHeaderDetails=orderHeader;
if(UtilValidate.isNotEmpty(orderHeader)){
   orderDate=orderHeader.orderDate;
   allDetailsMap.put("orderDate",orderDate);
 }

// partyId,partyName

roleTypeList = ["SHIP_TO_CUSTOMER","SUPPLIER"];

partyAddressMap = [:];

if(UtilValidate.isNotEmpty(orderId)){
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	vendorDetailsList = delegator.findList("OrderRole", cond , null, null, null, false );
	
	vendorDetail=EntityUtil.getFirst(vendorDetailsList);
	fromPartyId="";
	if(UtilValidate.isNotEmpty(vendorDetail)){
	   fromPartyId	 = vendorDetail.partyId;
	   partyAddressMap.put("fromPartyId",fromPartyId);
	   
	   partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:fromPartyId, userLogin: userLogin]);
	   if(UtilValidate.isNotEmpty(partyPostalAddress)){
		   if(UtilValidate.isNotEmpty(partyPostalAddress.address1)){
			address1=partyPostalAddress.address1;
			partyAddressMap.put("address1",address1);
		   }
		  if(UtilValidate.isNotEmpty(partyPostalAddress.address2)){
			  address2=partyPostalAddress.address2;
			  partyAddressMap.put("address2",address2);
		  }
		  if(UtilValidate.isNotEmpty(partyPostalAddress.city)){
			  city=partyPostalAddress.city;
			  partyAddressMap.put("city",city);
		  }
		  if(UtilValidate.isNotEmpty(partyPostalAddress.postalCode)){
			  postalCode=partyPostalAddress.postalCode;
			  partyAddressMap.put("postalCode",postalCode);
		  }
		  partyContactDetails=dispatcher.runSync("getPartyTelephone", [partyId:fromPartyId, userLogin: userLogin]);
		  if(UtilValidate.isNotEmpty(partyContactDetails)){
			  if(UtilValidate.isNotEmpty(partyContactDetails.contactNumber)){
				  contactNumber=partyContactDetails.contactNumber;
				  partyAddressMap.put("contactNumber",contactNumber);
			  }
		  }
		  faxId="FAX_BILLING";
		  partyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId: fromPartyId, contactMechPurposeTypeId: faxId, userLogin: userLogin]);
		  faxNumber = "";
		  if (partyFaxNumber != null && partyFaxNumber.contactNumber != null) {
			  faxNumber = partyFaxNumber.contactNumber;
			  partyAddressMap.put("faxNumber", faxNumber);
			  
		  }
		  formPartyTinNumber=delegator.findOne("PartyIdentification",[partyId:fromPartyId,partyIdentificationTypeId:"TIN_NUMBER"],false);
		  if(formPartyTinNumber){
			  fromPartyTinNo=formPartyTinNumber.idValue;
			  context.fromPartyTinNo=fromPartyTinNo;
		  }
	   }
	}
	context.partyAddressMap=partyAddressMap;
	
	
	for (vendorDetail in vendorDetailsList) {
		
		if(UtilValidate.isNotEmpty(vendorDetail)){
			partyId=vendorDetail.partyId;
			
				partyName =  PartyHelper.getPartyName(delegator, partyId, false);
				
				allDetailsMap.put("SupplierpartyId",partyId);
				allDetailsMap.put("supplierName",partyName);
				
				 }
			}
		
	}
	
	

          //to get company details

//tinCstDetails = delegator.findList("PartyGroup",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "Company")  , null, null, null, false );
//tinDetails=EntityUtil.getFirst(tinCstDetails);
//tinNumber="";cstNumber="";kstNumber="";
//if(UtilValidate.isNotEmpty(tinDetails.tinNumber)){
//	tinNumber=tinDetails.tinNumber;
//	allDetailsMap.put("tinNumber",tinNumber);
//}
//if(UtilValidate.isNotEmpty(tinDetails.cstNumber)){
//	cstNumber=tinDetails.cstNumber;
//	allDetailsMap.put("cstNumber",cstNumber);
//}

mailIdConfig = delegator.findOne("TenantConfiguration",["propertyName":"PURCHASEDEPT","propertyTypeEnumId":"PURCHASE_OR_STORES"],false);
if(mailIdConfig){
propertyValue=mailIdConfig.get("propertyValue");

   partyEmail= dispatcher.runSync("getPartyEmail", [partyId:propertyValue, userLogin: userLogin]);
   if(partyEmail){
   companyMail=partyEmail.emailAddress;
   context.companyMail=companyMail;
   allDetailsMap.put("companyMail",companyMail);	
   } 
   partySecondEmail= dispatcher.runSync("getPartyEmail", [partyId:propertyValue,contactMechPurposeTypeId:"SECONDARY_EMAIL" ,userLogin: userLogin]);
   if(partySecondEmail){
	   allDetailsMap.put("compSecondMail", partySecondEmail.emailAddress);
   }
   companyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: propertyValue, userLogin: userLogin]);
   companyPhone = "";
   if(companyTelephone != null && companyTelephone.contactNumber != null) {
   companyPhone = companyTelephone.contactNumber;
   allDetailsMap.put("companyPhone",companyPhone);
   }
   partySecondPhone= dispatcher.runSync("getPartyTelephone", [partyId:propertyValue,contactMechPurposeTypeId:"PHONE_WORK_SEC" ,userLogin: userLogin]);
   if(partySecondPhone){
	   allDetailsMap.put("partySecondPhone", partySecondPhone.contactNumber);
   }
   faxId="FAX_BILLING";
   companyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId: propertyValue, contactMechPurposeTypeId: faxId, userLogin: userLogin]);
   companyFax = "";
   if (companyFaxNumber != null && companyFaxNumber.contactNumber != null) {
   companyFax = companyFaxNumber.contactNumber;
   allDetailsMap.put("companyFax",companyFax);
   }
}

//company Details-tin,cst,kst
tinCstKstDetails = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "Company")  , null, null, null, false );
if(UtilValidate.isNotEmpty(tinCstKstDetails)){

tinDetails = EntityUtil.filterByCondition(tinCstKstDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
tinNumber="";
if(UtilValidate.isNotEmpty(tinDetails)){
tinDetails=EntityUtil.getFirst(tinDetails);
tinNumber=tinDetails.idValue;
allDetailsMap.put("tinNumber",tinNumber);
  }
cstDetails = EntityUtil.filterByCondition(tinCstKstDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
cstNumber="";
if(UtilValidate.isNotEmpty(cstDetails)){
cstDetails=EntityUtil.getFirst(cstDetails);	
cstNumber=cstDetails.idValue;
allDetailsMap.put("cstNumber",cstNumber);
  }
kstDetails = EntityUtil.filterByCondition(tinCstKstDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "KST_NUMBER"));
kstNumber="";
if(UtilValidate.isNotEmpty(kstDetails)){
kstDetails=EntityUtil.getFirst(kstDetails);
kstNumber=kstDetails.idValue;
allDetailsMap.put("kstNumber",kstNumber);
  }
 }

//orderSequenceNO
OrderHeaderSequenceData = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(OrderHeaderSequenceData)){
OrderHeaderSequenceData=EntityUtil.getFirst(OrderHeaderSequenceData);
sequenceId=OrderHeaderSequenceData.sequenceId;
orderNo=OrderHeaderSequenceData.orderNo;
allDetailsMap.put("sequenceId",sequenceId);
allDetailsMap.put("orderNo",orderNo);
	}

//FileNo
fileNumber = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"FILE_NUMBER"],false);
if(fileNumber){
	fileNo=fileNumber.get("attrValue");
	allDetailsMap.put("fileNo",fileNo);
}
//referNumber
referNumber = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"REF_NUMBER"],false);
if(referNumber){
	refNo=referNumber.get("attrValue");
	allDetailsMap.put("refNo",refNo);
}
quotationNumber = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"QUOTATION_NUMBER"],false);
if(quotationNumber){
	quotationNo=quotationNumber.get("attrValue");
	allDetailsMap.put("quotationNo",quotationNo);
}
destAddr="";
//destination Address
DstAddr = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"DST_ADDR"],false);
if(DstAddr){
	destAddr=DstAddr.get("attrValue");
	allDetailsMap.put("DstAddr",destAddr);
}



//OrderHeaderNote
orderheadDetails = delegator.findList("OrderHeaderNote",EntityCondition.makeCondition([EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId),EntityCondition.makeCondition("internalNote",EntityOperator.EQUALS,"N")],EntityOperator.AND)  , null, null, null, false );
if(orderheadDetails){
	orderHeadDetails=EntityUtil.getFirst(orderheadDetails);
	noteId=orderHeadDetails.noteId;
	internalNote=orderHeadDetails.internalNote;
		if (internalNote.equals("N")) {
	noteInfoData = delegator.findOne("NoteData",["noteId":noteId],false);
	if(noteInfoData){
		noteInfo=noteInfoData.get("noteInfo");
		allDetailsMap.put("noteInfo",noteInfo);
	}
  }
	}
//bed percents
bedCondition=EntityCondition.makeCondition([
			EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
			EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "BED_PUR")],
		EntityOperator.AND);
orderBedPercents = delegator.findList("OrderTerm",bedCondition, null, null, null, false );
bedPercents=EntityUtil.getFieldListFromEntityList(orderBedPercents, "termValue", true);
context.bedPercents=bedPercents;
//to get product details
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );


vatpercents=EntityUtil.getFieldListFromEntityList(orderDetails, "vatPercent", true);
cstpercents=EntityUtil.getFieldListFromEntityList(orderDetails, "cstPercent", true);
context.cstpercents=cstpercents;
context.vatpercents=vatpercents;

exprCondList=[];
exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));

orderAssc = EntityUtil.getFirst(delegator.findList("OrderAssoc", EntityCondition.makeCondition(exprCondList, EntityOperator.AND), null, null, null, false));

toOrderId=orderAssc.toOrderId;


orderHeader = delegator.findOne("OrderHeader",["orderId":toOrderId],false);

tallyRefNo = orderHeader.tallyRefNo;
context.tallyRefNo = tallyRefNo;


Map<String, Object> orderDtlMap = FastMap.newInstance();
orderDtlMap.put("orderId", toOrderId);
orderDtlMap.put("userLogin", userLogin);
result = dispatcher.runSync("getOrderItemSummary",orderDtlMap);
if(ServiceUtil.isError(result)){
	Debug.logError("Unable get Order item: " + ServiceUtil.getErrorMessage(result), module);
	return ServiceUtil.returnError(null, null, null,result);
}
productSummaryMap=result.get("productSummaryMap");
productWiseMap=[:];
Iterator eachProductIter = productSummaryMap.entrySet().iterator();
while(eachProductIter.hasNext()) {
	Map.Entry entry = (Entry)eachProductIter.next();
	String productId = (String)entry.getKey();
	 productSummary=(Map)entry.getValue();
	 productWiseMap.put(productId,productSummary);
}
indentShipmentAddress = "";

indectAdd = delegator.findOne("OrderAttribute",["orderId":toOrderId[0],"attrName":"SHIPPING_PREF"],false);

if(indectAdd){
	indentShipmentAddress=indectAdd.get("attrValue");
}

if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderitems->
		
		orderDetailsMap=[:];
		orderDetailsMap["productId"]=orderitems.productId;
		product = delegator.findOne("Product",["productId":orderitems.productId],false);
		if(product){
		
		orderDetailsMap["description"]=product.get("description");
		orderDetailsMap["longDescription"]=product.get("longDescription");
		
		uomId=product.get("quantityUomId");
		if(UtilValidate.isNotEmpty(uomId)){
			unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
			if(UtilValidate.isNotEmpty(unitDesciption)){
		 orderDetailsMap["unit"]=unitDesciption.get("abbreviation");
			}
		}}
		remarks="";
		baleQty="";
		unit="";
		if(toOrderId){
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, toOrderId));
			
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderitems.orderItemSeqId));
			
			condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
			//Debug.log("orderItemAttr=================="+orderItemAttr);
			if(orderItemAttr){
				orderItemAttr.each{ attr ->
				//Debug.log("remark=================="+attr.attrValue);
					/*if(eachAttr.attrName == "quotaQty"){
						schemeAmt =  schemeAmt+Double.valueOf(eachAttr.attrValue);
					}*/
					
					if(attr.attrValue == "REMARKS"){
					 remarks=attr.attrValue;
					}
				}
			}
		}
		bundleQuantityList=productWiseMap.get(orderitems.productId);
		bundleWeight=bundleQuantityList.bundleQuantity;
		unit=bundleQuantityList.Unit;
		bundleUnitListPrice=bundleQuantityList.bundleUnitListPrice
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
		orderDetailsMap["remarks"]=remarks;
		orderDetailsMap["Unit"]=unit;
		orderDetailsMap["baleqty"]=baleqty;
		orderDetailsMap["bundleUnitListPrice"]=bundleUnitListPrice;		
		orderDetailsMap["quantity"]=orderitems.quantity;
		orderDetailsMap["unitPrice"]=orderitems.unitPrice;
		orderDetailsMap["createdDate"]=orderitems.createdDate;
		amount=((orderitems.quantity)*(orderitems.unitPrice));
		orderDetailsMap["amount"]=amount;
		allDetailsMap["total"]+=amount;
		
		orderDetailsList.addAll(orderDetailsMap);
	}
}
// to get quoteid,date and (custRequestId)enquiryid ,(custRequestDate)enquiryDate
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails=EntityUtil.getFirst(orderDetails);
	quoteId=orderDetails.quoteId;
	if(UtilValidate.isNotEmpty(quoteId)){
		QuoteDetails = delegator.findOne("Quote",["quoteId":quoteId],false);
		qutationDate=QuoteDetails.get("issueDate");
		quoteRef=QuoteDetails.get("quoteName");
		
		allDetailsMap.put("quoteId",quoteId);
		allDetailsMap.put("qutationDate",qutationDate);
		allDetailsMap.put("quoteRef",quoteRef);
		
		enquiryDetails = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
		enquiryDetails=EntityUtil.getFirst(enquiryDetails);
		enquiryId=enquiryDetails.custRequestId;
		enquiryDate=enquiryDetails.custRequestDate;
		allDetailsMap.put("enquiryId",enquiryId);
		allDetailsMap.put("enquiryDate",enquiryDate);
	}
	else{
		quoteNoAttr = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"QUOTE_NO"],false);
		if(quoteNoAttr){
			quoteNo=quoteNoAttr.get("attrValue");
			allDetailsMap.put("quoteId",quoteNo);
		}
		
		quoteDateAttr = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"QUOTE_DATE"],false);
		if(quoteDateAttr){
			quoteDate=quoteDateAttr.get("attrValue");
		  /*def sdf = new SimpleDateFormat("dd/MM/yyyy");
			quoteDateTimestamp= null
			try {
				quoteDateTimestamp = new java.sql.Timestamp(sdf.parse(quoteDate).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+quoteDate, "");
			}*/
			
			allDetailsMap.put("qutationDateAttr",quoteDate);
		}
		
	}
}
	

// party Address
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
	vendorDetail=EntityUtil.getFirst(vendorDetails);
	fromPartyId="";
	if(UtilValidate.isNotEmpty(vendorDetail)){
	fromPartyId=vendorDetail.partyId;
	}
	   if(UtilValidate.isNotEmpty(fromPartyId)){
		 partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:fromPartyId, userLogin: userLogin]);
		   address1="";address2="";city="";postalCode="";
			if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
			   if(partyPostalAddress.address1){
		   address1=partyPostalAddress.address1;
		   allDetailsMap.put("address1",address1);
			   }
			   if(partyPostalAddress.address2){
		   address2=partyPostalAddress.address2;
		   allDetailsMap.put("address2",address2);
				}
			   if(partyPostalAddress.city){
		   city=partyPostalAddress.city;
		   allDetailsMap.put("city",city);
				}
			   if(partyPostalAddress.postalCode){
		   postalCode=partyPostalAddress.postalCode;
		   allDetailsMap.put("postalCode",postalCode);
				}
			 }
			
			partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: fromPartyId, userLogin: userLogin]);
			phoneNumber = "";
			if (partyTelephone != null && partyTelephone.contactNumber != null) {
				phoneNumber = partyTelephone.contactNumber;
			}
			allDetailsMap.put("phoneNumber", phoneNumber);
			
			faxId="FAX_BILLING";
			partyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId: fromPartyId, contactMechPurposeTypeId: faxId, userLogin: userLogin]);
			faxNumber = "";
			if (partyFaxNumber != null && partyFaxNumber.contactNumber != null) {
				faxNumber = partyFaxNumber.contactNumber;
			}
			allDetailsMap.put("faxNumber", faxNumber);
			formPartyTinNumber=delegator.findOne("PartyIdentification",[partyId:fromPartyId,partyIdentificationTypeId:"TIN_NUMBER"],false);
			if(formPartyTinNumber){
				fromPartyTinNo=formPartyTinNumber.idValue;
				context.fromPartyTinNo=fromPartyTinNo;
			}
 }
	   

context.allDetailsMap=allDetailsMap;
context.orderDetailsList=orderDetailsList;
context.orderTermList=orderTermList;

bedAmount=0;
vatAmount=0;
cstAmount=0;
listSize=0;
bedPercent=0;
vatAdjAmount = 0;
cstAdjAmount = 0;


List Newcondition = [];
Newcondition.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
newcond=EntityCondition.makeCondition(Newcondition,EntityOperator.AND);
orderAdjts= delegator.findList("OrderAdjustment",newcond , UtilMisc.toSet("orderAdjustmentTypeId","amount"), null, null, false );

if(orderAdjts){
orderAdjts.each{orderAdjt->
	
	if(orderAdjt.orderAdjustmentTypeId == "VAT_PUR"){
		if(orderAdjt.amount){
		  vatAdjAmount=orderAdjt.amount;
		}
   }
	if(orderAdjt.orderAdjustmentTypeId == "CST_PUR"){
		if(orderAdjt.amount){
			cstAdjAmount=orderAdjt.amount;
		  }
	}
}
}


/*orderTerms= delegator.findList("OrderTerm",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
parentMap=[:];
if(UtilValidate.isNotEmpty(orderTerms)){
	orderTerms.each{orderTerm ->
		Amount=0;
		listSize+=1;
		ecl = EntityCondition.makeCondition([
			EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
			EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderTerm.termTypeId)],
		EntityOperator.AND);
		orderAdjustments= delegator.findList("OrderAdjustment",ecl , UtilMisc.toSet("orderAdjustmentTypeId","amount"), null, null, false );
		if(orderTerm.termTypeId == "BED_PUR"){
			condition = EntityCondition.makeCondition([
				EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
				EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN,UtilMisc.toList("BEDCESS_PUR","BEDSECCESS_PUR","BED_PUR","VAT_PUR","CST_PUR") )],
			EntityOperator.AND);
			orderAdjts= delegator.findList("OrderAdjustment",condition , UtilMisc.toSet("orderAdjustmentTypeId","amount"), null, null, false );
			
			
			orderAdjts.each{orderAdjt->
				Amount+=orderAdjt.amount;
				
				if(orderAdjt.orderAdjustmentTypeId == "VAT_PUR"){
					vatAdjAmount=orderAdjt.amount;
				}
				if(orderAdjt.orderAdjustmentTypeId == "CST_PUR"){
					cstAdjAmount=orderAdjt.amount;
				}
				
				
				
				
			}
			bedAmount=Amount;
			bedPercent=orderTerm.termValue;
		}
		
		orderAdjustment=[];
		if(UtilValidate.isNotEmpty(orderAdjustments)){
		orderAdjustment = EntityUtil.getFirst(orderAdjustments);
		}
		
		if(orderTerm.termTypeId == "VAT_PUR"){
			vatAmount=orderAdjustment.amount;
		}
		if(orderTerm.termTypeId == "CST_PUR"){
			cstAmount=orderAdjustment.amount;
		}
	GenericValue termTypes= delegator.findOne("TermType",[termTypeId:orderTerm.termTypeId],false);
	if(UtilValidate.isEmpty(parentMap[termTypes.parentTypeId])){
		termsList=[];
		tempMap=[:];
		tempMap.put("termTypeId", orderTerm.termTypeId);
		tempMap.put("parentTypeId", termTypes.parentTypeId);
		if(UtilValidate.isNotEmpty(orderAdjustments)){
			if(Amount>0){
				tempMap.put("amount",Amount);
			}else{
			tempMap.put("amount",orderAdjustment.amount);
			}
		}
		if(UtilValidate.isNotEmpty(orderTerm.orderItemSeqId)){
			tempMap.put("orderItemSeqId", orderTerm.orderItemSeqId);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termValue)){
			tempMap.put("termValue", orderTerm.termValue);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termDays)){
			tempMap.put("termDays", orderTerm.termDays);
		}
		if(UtilValidate.isNotEmpty(orderTerm.description)){
			tempMap.put("description", orderTerm.description);
		}
		if(UtilValidate.isNotEmpty(orderTerm.uomId)){
			tempMap.put("uomId", orderTerm.uomId);
		}
		tempMap.put("termTypeDes", termTypes.description);
		termsList.add(tempMap);
		parentMap[termTypes.parentTypeId]=termsList;
		
	}else{
		tempMap=[:];
		tempMap.put("termTypeId", orderTerm.termTypeId);
		tempMap.put("parentTypeId", termTypes.parentTypeId);
		if(UtilValidate.isNotEmpty(orderAdjustments)){
			if(Amount>0){
				tempMap.put("amount",Amount);
			}else{
			tempMap.put("amount",orderAdjustment.amount);
			}
		}
		if(UtilValidate.isNotEmpty(orderTerm.orderItemSeqId)){
			tempMap.put("orderItemSeqId", orderTerm.orderItemSeqId);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termValue)){
			tempMap.put("termValue", orderTerm.termValue);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termDays)){
			tempMap.put("termDays", orderTerm.termDays);
		}
		if(UtilValidate.isNotEmpty(orderTerm.description)){
			tempMap.put("description", orderTerm.description);
		}
		if(UtilValidate.isNotEmpty(orderTerm.uomId)){
			tempMap.put("uomId", orderTerm.uomId);
		}
		tempMap.put("termTypeDes", termTypes.description);
		testList = parentMap[termTypes.parentTypeId]
		testList.add(tempMap);
		parentMap[termTypes.parentTypeId]=testList;
		
		}
	
	}
}*/
//context.parentMap=parentMap;
context.Amount=bedAmount;
context.vatAmount=vatAdjAmount;
context.cstAmount=cstAdjAmount;
context.bedPercent=bedPercent;
context.listSize=listSize;

conditionList=[];

conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SUPPLIER_AGENT", "BILL_FROM_VENDOR","SHIP_TO_CUSTOMER")));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
//Debug.log("orderRoles========================"+orderRoles);

shipToCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SHIP_TO_CUSTOMER")],EntityOperator.AND);
shipToPartyRole=EntityUtil.filterByCondition(orderRoles,shipToCondition);
shipToParty=EntityUtil.getFirst(shipToPartyRole);
//Debug.log("shipToParty.partyId========================"+shipToParty.partyId);



shipingAdd=[:];
if(shipToParty.partyId){
shippPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, shipToParty.partyId, false);
//contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, shipToParty.partyId, false,"POSTAL_ADDRESS");

conditionListAddress = [];
conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, shipToParty.partyId));
conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);



//Debug.log("contactMechesDetails======================="+contactMechesDetails);
if(contactMechesDetails){
	contactMec=contactMechesDetails.getFirst();
	if(contactMec){
		//partyPostalAddress=contactMec.get("postalAddress");
		
		partyPostalAddress=contactMec;
		
		//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			address1="";
			address2="";
			state="";
			city="";
			postalCode="";
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
			shipingAdd.put("name",shippPartyName);
			shipingAdd.put("address1",address1);
			shipingAdd.put("address2",address2);
			shipingAdd.put("city",city);
			shipingAdd.put("postalCode",postalCode);
			//Debug.log("shipingAdd========================="+shipingAdd);
			
		}
	}
}
}

context.shipingAdd=shipingAdd;



conditionList=[];

conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SUPPLIER_AGENT", "BILL_FROM_VENDOR","SHIP_TO_CUSTOMER")));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
//Debug.log("orderRoles========================"+orderRoles);

shipToCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPPLIER_AGENT")],EntityOperator.AND);
shipToPartyRole=EntityUtil.filterByCondition(orderRoles,shipToCondition);
shipToParty=EntityUtil.getFirst(shipToPartyRole);
//Debug.log("shipToParty.partyId========================"+shipToParty.partyId);



suppAdd=[:];
supppartyName="";
if(shipToParty && shipToParty.partyId){
	supppartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, shipToParty.partyId, false);
	//Debug.log("supppartyName===================="+supppartyName);

contactMechesDetails = ContactMechWorker.getPartyContactMechValueMaps(delegator, shipToParty.partyId, false,"POSTAL_ADDRESS");
//Debug.log("contactMechesDetails======================="+contactMechesDetails);


if(indentShipmentAddress){
	
	conditionListAddress.clear();
	conditionListAddress.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, indentShipmentAddress));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	listAddressList = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
	
	listAddress = EntityUtil.getFirst(listAddressList);
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
	contactMec=contactMechesDetails.getLast();
	if(contactMec){
		partyPostalAddress=contactMec.get("postalAddress");
		//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			address1="";
			address2="";
			state="";
			city="";
			postalCode="";
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
			suppAdd.put("address1",address1);
			//Debug.log("address1-------------"+address1);
			suppAdd.put("address2",address2);
			//Debug.log("address2-------------"+address2);
			suppAdd.put("city",city);
			suppAdd.put("postalCode",postalCode);
			
		}
	}
}
}

context.suppAdd=suppAdd;

context.supppartyName=supppartyName;




conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderAdjustment = delegator.findList("OrderAdjustment", condExpr, null, null, null, false);


OrderAdjustmentWithOutSubsidy = EntityUtil.filterByCondition(OrderAdjustment, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "TEN_PERCENT_SUBSIDY"));

orderAdjustmentTypeIds = EntityUtil.getFieldListFromEntityList(OrderAdjustmentWithOutSubsidy,"orderAdjustmentTypeId", true);

typeBasedMap = [:];

for (eachType in orderAdjustmentTypeIds) {
	eachTypeOrderAdjustment = EntityUtil.filterByCondition(OrderAdjustmentWithOutSubsidy, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, eachType));
		  adjustmentAmounts = EntityUtil.getFieldListFromEntityList(eachTypeOrderAdjustment,"amount", true);
		  
	sameTypeAmount = 0;
	SAmeAmountMap = [:];
	 for (eachAmount in adjustmentAmounts) {
		 eachTypeOrderAdjustmentBasedOnAmount = EntityUtil.filterByCondition(eachTypeOrderAdjustment, EntityCondition.makeCondition("amount", EntityOperator.EQUALS, eachAmount));
		
		   for (eachAdjList in eachTypeOrderAdjustmentBasedOnAmount) {
			  sameTypeAmount = sameTypeAmount+Double.valueOf(eachAdjList.amount);
		}
		  SAmeAmountMap.put(eachAmount, sameTypeAmount);
	}
	 typeBasedMap.put(eachType, SAmeAmountMap);
}


context.typeBasedMap = typeBasedMap;

Debug.log("typeBasedMap=============="+typeBasedMap);