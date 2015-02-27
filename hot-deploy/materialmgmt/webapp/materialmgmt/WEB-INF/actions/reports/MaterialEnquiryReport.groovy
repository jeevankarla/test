import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import org.ofbiz.party.contact.ContactHelper;

conditionList =[];
dctx = dispatcher.getDispatchContext();
mailIdConfig = delegator.findOne("TenantConfiguration",["propertyName":"PURCHASEDEPT","propertyTypeEnumId":"PURCHASE_OR_STORES"],false);
if(mailIdConfig){
	propertyValue=mailIdConfig.get("propertyValue");

   partyEmail= dispatcher.runSync("getPartyEmail", [partyId:propertyValue,contactMechPurposeTypeId:" ", userLogin: userLogin]);
if(partyEmail){
	companyMail=partyEmail.emailAddress;
	context.companyMail=companyMail;
	
   }
partyOtherEmail= dispatcher.runSync("getPartyEmail", [partyId:propertyValue,contactMechPurposeTypeId:"SECONDARY_EMAIL",userLogin: userLogin]);
if(partyOtherEmail){
	companyOtherMail=partyOtherEmail.emailAddress;
	context.companyOtherMail=companyOtherMail;
   }
companyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId:propertyValue,  contactMechPurposeTypeId:"FAX_BILLING", userLogin: userLogin]);
if (companyFaxNumber) {
	companyFax = companyFaxNumber.contactNumber;
	context.companyFax=companyFax;	
}

companyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: mailIdConfig.propertyValue,contactMechPurposeTypeId:"PRIMARY_PHONE", userLogin: userLogin]);
if(companyTelephone) {
	companyPhone = companyTelephone.contactNumber;
	context.companyPhone=companyPhone;
}
companyAnotherTelephone= dispatcher.runSync("getPartyTelephone", [partyId: mailIdConfig.propertyValue,contactMechPurposeTypeId:"PHONE_WORK_SEC", userLogin: userLogin]);
if(companyAnotherTelephone){
	companyAnotherPhone = companyAnotherTelephone.contactNumber;
	context.companyAnotherPhone=companyAnotherPhone;
}
 
 }

tinDetails = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "Company")  , null, null, null, false );

compantTinDetails = EntityUtil.filterByCondition(tinDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
compantTinDetail = EntityUtil.getFirst(compantTinDetails);
if(UtilValidate.isNotEmpty(compantTinDetail) && UtilValidate.isNotEmpty(compantTinDetail.idValue)){
   companyTinNumber=compantTinDetail.idValue;
   context.companyTinNumber=companyTinNumber;   
}
compantTinDetails = EntityUtil.filterByCondition(tinDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "KST_NUMBER"));
compantTinDetail = EntityUtil.getFirst(compantTinDetails);
if(UtilValidate.isNotEmpty(compantTinDetail) && UtilValidate.isNotEmpty(compantTinDetail.idValue)){
   kstNumber=compantTinDetail.idValue;
   context.kstNumber=kstNumber;   
}
compantTinDetails = EntityUtil.filterByCondition(tinDetails, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
compantTinDetail = EntityUtil.getFirst(compantTinDetails);
if(UtilValidate.isNotEmpty(compantTinDetail) && UtilValidate.isNotEmpty(compantTinDetail.idValue)){
   cstNumber=compantTinDetail.idValue;
   context.cstNumber=cstNumber;   
}



signature=parameters.signature;
context.signature=signature;
if(parameters.issueToCustReqId){
      custRequestId=parameters.issueToCustReqId;
      context.custRequestId=custRequestId;
	  CustRequestSequenceDetails = delegator.findList("CustRequestSequence",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
	  CustRequestSequence= EntityUtil.getFirst(CustRequestSequenceDetails);	  
	  if(CustRequestSequence){
		  if(UtilValidate.isNotEmpty(CustRequestSequence.custRequestNo)){
			  enquirySequenceNo=CustRequestSequence.custRequestNo;
			  context.enquirySequenceNo=enquirySequenceNo;
		  }
	  }
	  conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	  conditionList.add(EntityCondition.makeCondition("noteType", EntityOperator.EQUALS, "EXTERNAL_NOTE_ID"));
	  condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	  custReqNoteList = delegator.findList("CustRequestAndNote", condition, null, null, null, false);
	  noteList = EntityUtil.getFieldListFromEntityList(custReqNoteList, "noteInfo", true);
	  context.noteList = noteList;
}
else{
     fromPartyId=parameters.partyId;
     custRequestId=parameters.custRequestId;
     context.custRequestId=custRequestId;
	 CustRequestSequenceDetails = delegator.findList("CustRequestSequence",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
	 CustRequestSequence= EntityUtil.getFirst(CustRequestSequenceDetails);	 
	 if(CustRequestSequence){
		 if(UtilValidate.isNotEmpty(CustRequestSequence.custRequestNo)){
			 enquirySequenceNo=CustRequestSequence.custRequestNo;
			 context.enquirySequenceNo=enquirySequenceNo;
		 }
	 }
	 conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	 conditionList.add(EntityCondition.makeCondition("noteType", EntityOperator.EQUALS, "EXTERNAL_NOTE_ID"));
	 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	 custReqNoteList = delegator.findList("CustRequestAndNote", condition, null, null, null, false);
	 noteList = EntityUtil.getFieldListFromEntityList(custReqNoteList, "noteInfo", true);
	 context.noteList = noteList;
}
partyAddressMap=[:];
custReqDetails = delegator.findOne("CustRequest", [custRequestId : custRequestId], false);
if(UtilValidate.isNotEmpty(custReqDetails)){
      custReqDate = custReqDetails.custRequestDate;   
      dueDate= custReqDetails.closedDateTime;
	  conditionList.clear();
	  if(UtilValidate.isNotEmpty(parameters.partyId)){
		  conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
	  }
	  conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	  
	  custRequestPartyCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
      partyDetails=delegator.findList("CustRequestParty",custRequestPartyCondition , null, null, null, false );   
     if(UtilValidate.isNotEmpty(partyDetails)){
	       partyIds = EntityUtil.getFieldListFromEntityList(partyDetails, "partyId", true);
	       partyIds.each{eachPartyId->
		   partyMap=[:];
			  partyMap["partyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, eachPartyId, false);			   
              partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:eachPartyId, userLogin: userLogin]);
              if(UtilValidate.isNotEmpty(partyPostalAddress)){
                   if(UtilValidate.isNotEmpty(partyPostalAddress.address1)){
                          address1=partyPostalAddress.address1;
		                  partyMap.put("address1",address1);
                    }	  
	               if(UtilValidate.isNotEmpty(partyPostalAddress.address2)){
		                  address2=partyPostalAddress.address2;
		                  partyMap.put("address2",address2);
	                }
	               if(UtilValidate.isNotEmpty(partyPostalAddress.city)){
		                  city=partyPostalAddress.city;		
		                  partyMap.put("city",city);
	                }	  
	               if(UtilValidate.isNotEmpty(partyPostalAddress.postalCode)){
		                  postalCode=partyPostalAddress.postalCode;
		                  partyMap.put("postalCode",postalCode);
	                }	  
              } 	    
               partyContactDetails=dispatcher.runSync("getPartyTelephone", [partyId:eachPartyId, userLogin: userLogin]);
               if(UtilValidate.isNotEmpty(partyContactDetails)){
	                if(UtilValidate.isNotEmpty(partyContactDetails.contactNumber)){
		                  contactNumber=partyContactDetails.contactNumber;
		                  partyMap.put("contactNumber",contactNumber);
	                  }
               } 
			   faxId="FAX_BILLING";
					   partyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId: eachPartyId, contactMechPurposeTypeId: faxId, userLogin: userLogin]);
					   faxNumber = "";
					   if (partyFaxNumber != null && partyFaxNumber.contactNumber != null) {
						   faxNumber = partyFaxNumber.contactNumber;
						   partyMap.put("faxNumber", faxNumber);
					   }		
			   context.partyMap=partyMap;
			   context.put("custReqDate",custReqDate);
			   context.put("dueDate",dueDate);
			   partyAddressMap.put(eachPartyId,partyMap);
			   //productDetails
			    enquiryMap=[:];
			    productId ="";
				key = 0;
			    custReqItemDetails = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
				if(UtilValidate.isNotEmpty(custReqItemDetails)){
					 custReqItemDetails.each{custReqItem->
							   productMap=[:];
							   requrdqty=custReqItem.quantity;
							   productId=custReqItem.productId;
							   
							   productMap.put("productId",productId);
							   productDetails = delegator.findOne("Product",["productId":productId],false);
							   if(UtilValidate.isNotEmpty(productDetails)){
								   itemCode=productDetails.internalName;
								   longDescription=productDetails.longDescription;
								   description=productDetails.description;
								   productMap.put("itemCode",itemCode);
								   productMap.put("description",description);
								   productMap.put("longDescription",longDescription);
								   uomId=productDetails.quantityUomId;
							   }
							   if(UtilValidate.isNotEmpty(uomId)){
								   unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
								   productMap.put("unit",unitDesciption.abbreviation);
							   }
							   productMap.put("requrdqty",requrdqty);
							   key=key+1;
							   enquiryMap.put(key,productMap);
						   }						   
				   }					 
				context.enquiryMap=enquiryMap;
             
    }
  }
     context.partyAddressMap=partyAddressMap;
}
