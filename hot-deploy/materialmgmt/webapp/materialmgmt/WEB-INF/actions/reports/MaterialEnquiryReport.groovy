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
tinDetails = delegator.findList("PartyGroup",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "Company")  , null, null, null, false );
tinDetails=EntityUtil.getFirst(tinDetails);
companyTinNumber="";
if(UtilValidate.isNotEmpty(tinDetails.tinNumber)){
	companyTinNumber=tinDetails.tinNumber;
	context.put("companyTinNumber",companyTinNumber);
}
cstNumber="";
if(UtilValidate.isNotEmpty(tinDetails.cstNumber)){
	cstNumber=tinDetails.cstNumber;
	context.put("cstNumber",cstNumber);
}
if(parameters.issueToCustReqId){
      custRequestId=parameters.issueToCustReqId;
      context.custRequestId=custRequestId;
	  CustRequestSequenceDetails = delegator.findList("CustRequestSequence",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
	  CustRequestSequence= EntityUtil.getFirst(CustRequestSequenceDetails);	  
	  if(CustRequestSequence){
		  if(UtilValidate.isNotEmpty(CustRequestSequence.sequenceId)){
			  enquirySequenceNo=CustRequestSequence.sequenceId;
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
		 if(UtilValidate.isNotEmpty(CustRequestSequence.sequenceId)){
			 enquirySequenceNo=CustRequestSequence.sequenceId;
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
      partyDetails=delegator.findList("CustRequestParty",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );   
     if(UtilValidate.isNotEmpty(partyDetails)){
	       partyIds = EntityUtil.getFieldListFromEntityList(partyDetails, "partyId", true);
	       partyIds.each{eachPartyId->
		   partyMap=[:];
		   
	       if((parameters.partyId))
		   {			   
			    if(eachPartyId.equals(parameters.partyId)){	
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
								   description=productDetails.description;
								   longDescription=productDetails.longDescription;
								   productMap.put("itemCode",itemCode);
								   productMap.put("description",description);
								   productMap.put("longDescription",longDescription);
								   uomId=productDetails.quantityUomId;
							   }
							   if(UtilValidate.isNotEmpty(uomId)){
								   unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
								   productMap.put("unit",unitDesciption.description);
							   }
							   productMap.put("requrdqty",requrdqty);
							   key=key+1;
							   enquiryMap.put(key,productMap);
						   }						   
					  }					 
					  context.enquiryMap=enquiryMap;
			      }  			   
		   }
           else{	
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
								   productMap.put("unit",unitDesciption.description);
							   }
							   productMap.put("requrdqty",requrdqty);
							   key=key+1;
							   enquiryMap.put(key,productMap);
						   }						   
				   }					 
				  context.enquiryMap=enquiryMap;
            }	   	  
        }    
    }
    context.partyAddressMap=partyAddressMap;
}

