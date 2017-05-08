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

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();
orderId = parameters.orderId;
signature = parameters.sign;
context.signature=signature;

orderDetailsList=[];
allDetailsMap=[:];
orderTermList=[];
branchId="";
allDetailsMap.put("orderId",orderId);
allDetailsMap["total"]=BigDecimal.ZERO;
allDetailsMap["grandTotal"]=BigDecimal.ZERO;
roundedGrandTotal=BigDecimal.ZERO;
orderHeader=null;
orderDesctioption="";
productStoreId = "";
if (orderId) {
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	orderDesctioption=orderHeader.orderName;
	productStoreId = orderHeader.productStoreId;
	
	if (productStoreId) {
		productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
		branchId=productStore.payToPartyId;
	}
	//get Report Header
	branchContext=[:];
	if(branchId == "INT12" || branchId == "INT49" || branchId == "INT55")
	branchId = "INT12";
	else if(branchId == "INT8" || branchId == "INT16" || branchId == "INT20" || branchId == "INT21" || branchId == "INT22" || branchId == "INT44")
	branchId = "INT8";
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



destAddr="";
//destination Address
DstAddr = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"DST_ADDR"],false);
if(DstAddr){
	destAddr=DstAddr.get("attrValue");
	allDetailsMap.put("DstAddr",destAddr);
}
if(UtilValidate.isEmpty(DstAddr)){
	allDetailsMap.put("DstAddr",productStoreId);
}


// partyId,partyName
if(UtilValidate.isNotEmpty(orderId)){
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
	vendorDetail=EntityUtil.getFirst(vendorDetails);
	if(UtilValidate.isNotEmpty(vendorDetail)){
	partyId=vendorDetail.partyId;
	allDetailsMap.put("partyId",partyId);
		partyName =  PartyHelper.getPartyName(delegator, partyId, false);
		allDetailsMap.put("partyName",partyName);
	  }
	}

		  //to get company details


partyIdentification = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , branchId)  , null, null, null, false );

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
	
	
	for (vendorDetail in vendorDetailsList) {
		
		if(UtilValidate.isNotEmpty(vendorDetail)){
			partyId=vendorDetail.partyId;
			
				partyName =  PartyHelper.getPartyName(delegator, partyId, false);
				
				allDetailsMap.put("SupplierpartyId",partyId);
				allDetailsMap.put("supplierName",partyName);
				
				 }
			}
		
	}
TransactionTypeId=delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"purchaseTitleTransferEnumId"],false);
if(TransactionTypeId){
	TransactionType=TransactionTypeId.get("attrValue");
	if(TransactionType == "CST_CFORM"){
		TransactionTypevalue = "Transaction With C Form";
	}else if(TransactionType == "EXEMPTED_GOODS"){
		TransactionTypevalue = "Transaction With exempted goods";
	}else{
		TransactionTypevalue = "Transaction Without C Form";
	}
}
context.TransactionTypevalue=TransactionTypevalue;

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
		
		
		
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderitems.orderId));
		conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderitems.orderItemSeqId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
		
		OrderItemDetail=EntityUtil.getFirst(OrderItemDetail);
		
		
		remarks = "";
		packQuantity="";
		packets="";
		//if(OrderItemDetail)
		//remarks = OrderItemDetail.remarks;
		if(UtilValidate.isNotEmpty(orderitems.orderId)){
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderitems.orderId));
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderitems.orderItemSeqId));
			//conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
			condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
			packetQuantityDetails = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "packQuantity"));
			if(UtilValidate.isNotEmpty(packetQuantityDetails)){
				packetQuantityDetails=EntityUtil.getFirst(packetQuantityDetails);
				packQuantity=packetQuantityDetails.attrValue;
			}
			packetsDetails = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "packets"));
			if(UtilValidate.isNotEmpty(packetsDetails)){
				packetsDetails=EntityUtil.getFirst(packetsDetails);
				packets=packetsDetails.attrValue;
			}
			remarksDetails = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "remarks"));
			if(UtilValidate.isNotEmpty(remarksDetails)){
				remarksDetails=EntityUtil.getFirst(remarksDetails);
				remarks=remarksDetails.attrValue;
			}
		}
		/*if(OrderItemDetail)
		packQuantity = OrderItemDetail.packQuantity;
		if(OrderItemDetail)
		packets = OrderItemDetail.packets;*/
		
		orderDetailsMap["remarks"]=remarks;
		orderDetailsMap["quantity"]=orderitems.quantity;
		orderDetailsMap["packetQuantity"]=packQuantity;
		orderDetailsMap["packets"]=packets;
		orderDetailsMap["unitPrice"]=orderitems.unitPrice;
		
		orderDetailsMap["numQuantity"]=OrderItemDetail.baleQuantity;
		orderDetailsMap["bundleWeight"]=OrderItemDetail.bundleWeight;
		orderDetailsMap["Uom"]=OrderItemDetail.Uom;
		
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

conditionList=[];

conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SUPPLIER_AGENT", "BILL_FROM_VENDOR","SHIP_TO_CUSTOMER")));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
shipToCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SHIP_TO_CUSTOMER")],EntityOperator.AND);
shipToPartyRole=EntityUtil.filterByCondition(orderRoles,shipToCondition);
shipToParty=EntityUtil.getFirst(shipToPartyRole);
shipingPartyId="";
if(UtilValidate.isNotEmpty(shipToParty)){
	shipingPartyId=shipToParty.partyId;
	}

supplierDtl=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPPLIER_AGENT")],EntityOperator.AND);
supplierDtlRole=EntityUtil.filterByCondition(orderRoles,supplierDtl);
supplierDtls=EntityUtil.getFirst(supplierDtlRole);
fromPartyId="";

if(UtilValidate.isNotEmpty(supplierDtls)){
	fromPartyId=supplierDtls.partyId;
	}

	/*List conlist=[];
	conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
	vendorDetail=EntityUtil.getFirst(vendorDetails);*/

	/*if(UtilValidate.isNotEmpty(vendorDetail)){
	fromPartyId=vendorDetail.partyId;
	}*/
	   if(UtilValidate.isNotEmpty(fromPartyId)){
		   partyName =  PartyHelper.getPartyName(delegator, partyId, false);
		   allDetailsMap.put("partyName",partyName);
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
	 shipingAdd=[:];
	   if(UtilValidate.isNotEmpty(shipingPartyId)){
		   partyName =  PartyHelper.getPartyName(delegator, partyId, false);
		   shipingAdd.put("partyName",partyName);
		   partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:shipingPartyId, userLogin: userLogin]);
			 address1="";address2="";city="";postalCode="";
			  if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
				 if(partyPostalAddress.address1){
			 address1=partyPostalAddress.address1;
			 shipingAdd.put("address1",address1);
				 }
				 if(partyPostalAddress.address2){
			 address2=partyPostalAddress.address2;
			 shipingAdd.put("address2",address2);
				  }
				 if(partyPostalAddress.city){
			 city=partyPostalAddress.city;
			 shipingAdd.put("city",city);
				  }
				 if(partyPostalAddress.postalCode){
			 postalCode=partyPostalAddress.postalCode;
			 shipingAdd.put("postalCode",postalCode);
				  }
			   }
			  partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: fromPartyId, userLogin: userLogin]);
			  phoneNumber = "";
			  if (partyTelephone != null && partyTelephone.contactNumber != null) {
				  phoneNumber = partyTelephone.contactNumber;
			  }
			  shipingAdd.put("phoneNumber", phoneNumber);
   }
	   
	   Debug.log("allDetailsMap================="+allDetailsMap);

context.shipingAdd=shipingAdd;
context.allDetailsMap=allDetailsMap;
context.orderDetailsList=orderDetailsList;


context.orderTermList=orderTermList;
context.addressFlag="Y";
bedAmount=0;
vatAmount=0;
cstAmount=0;
listSize=0;
bedPercent=0;
orderTerms= delegator.findList("OrderTerm",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
parentMap=[:];

orderAdjustmentsMap=[:];
List Newcondition = [];
Newcondition.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
newcond=EntityCondition.makeCondition(Newcondition,EntityOperator.AND);
orderAdjts= delegator.findList("OrderAdjustment",newcond , UtilMisc.toSet("orderAdjustmentTypeId","amount","sourcePercentage"), null, null, false );
if(orderAdjts){
	orderAdjts.each{orderAdjt->
		if(orderAdjt.orderAdjustmentTypeId == "VAT_PUR"){
			if(orderAdjt.amount){
			  orderAdjustmentsMap.put("VATAmount", orderAdjt.amount)
			  orderAdjustmentsMap.put("VATPer", orderAdjt.sourcePercentage)
			}
	   }
	   if(orderAdjt.orderAdjustmentTypeId == "CST_PUR"){
			if(orderAdjt.amount){
				orderAdjustmentsMap.put("CSTAmount", orderAdjt.amount)
				orderAdjustmentsMap.put("CSTPer", orderAdjt.sourcePercentage)
			  }
		}
	   if(orderAdjt.orderAdjustmentTypeId == "CESS"){
		   if(orderAdjt.amount){
			   orderAdjustmentsMap.put("CESS", orderAdjt.amount)
			   orderAdjustmentsMap.put("CessPer", orderAdjt.sourcePercentage)
			 }
	   }
	   if(orderAdjt.orderAdjustmentTypeId == "INSURANCE_CHGS"){
		   if(orderAdjt.amount){
			   orderAdjustmentsMap.put("INSURANCE_CHGS", orderAdjt.amount)
			   orderAdjustmentsMap.put("InsuPer", orderAdjt.sourcePercentage)
			 }
	   }
	   if(orderAdjt.orderAdjustmentTypeId == "OTHER_CHARGES"){
		   if(orderAdjt.amount){
			   orderAdjustmentsMap.put("OTHER_CHARGES", orderAdjt.amount)
			   orderAdjustmentsMap.put("OtherPer", orderAdjt.sourcePercentage)
			 }
	   }
	 }
}
context.orderAdjustmentsMap=orderAdjustmentsMap;

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
				EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN,UtilMisc.toList("BEDCESS_PUR","BEDSECCESS_PUR","BED_PUR") )],
			EntityOperator.AND);
			orderAdjts= delegator.findList("OrderAdjustment",condition , UtilMisc.toSet("orderAdjustmentTypeId","amount"), null, null, false );
			orderAdjts.each{orderAdjt->
				Amount+=orderAdjt.amount;
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
}
context.parentMap=parentMap;
context.Amount=bedAmount;
context.vatAmount=vatAmount;
context.cstAmount=cstAmount;
context.bedPercent=bedPercent;
context.listSize=listSize;

