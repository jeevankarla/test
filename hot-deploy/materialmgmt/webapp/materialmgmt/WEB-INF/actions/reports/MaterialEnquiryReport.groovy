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
companyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, "Company")).get("partyDetails");
	if(UtilValidate.isNotEmpty(companyDetail)){
	  companyTinNumber=companyDetail.get('TIN_NUMBER');
	}
context.put("companyTinNumber",companyTinNumber);
if(parameters.issueToCustReqId){
      custRequestId=parameters.issueToCustReqId;
      context.custRequestId=custRequestId;
}
else{
     fromPartyId=parameters.partyId;
     custRequestId=parameters.custRequestId;
     context.custRequestId=custRequestId;
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
					   conditionList.clear();
					   conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "FAX_BILLING"));
					   conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachPartyId));
					   cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					   faxNumber="";
					   listAddress = delegator.findList("PartyContactDetailByPurpose", cond , null, null, null, false );
					   if(UtilValidate.isNotEmpty(listAddress.contactNumber)){
						  faxNumber = listAddress.contactNumber;
						  partyMap.put("faxNuber", faxNumber);
						}
            
				      context.partyMap=partyMap;
				      context.put("custReqDate",custReqDate);
			          context.put("dueDate",dueDate);
			          partyAddressMap.put(eachPartyId,partyMap);
			          enquiryMap=[:];
			          productId ="";
			          custReqItemDetails = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
			          if(UtilValidate.isNotEmpty(custReqItemDetails)){
				           custReqItemDetails=EntityUtil.getFirst(custReqItemDetails);
				           productId=custReqItemDetails.productId;
				           requrdqty=custReqItemDetails.quantity;
				           enquiryMap.put("productId",productId);
				           enquiryMap.put("requrdqty",requrdqty);
			           }
			           productDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productId)  , null, null, null, false );
			           productUomDetails=EntityUtil.getFirst(productDetails);
			           if(UtilValidate.isNotEmpty(productUomDetails)){
				            uomId=productUomDetails.quantityUomId;
				            if(UtilValidate.isNotEmpty(uomId)){
				                    uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
				                    uomDesc=EntityUtil.getFirst(uomDesc);
				                    enquiryMap.put("unit",uomDesc.abbreviation);
				             }
			            }
			            if(UtilValidate.isNotEmpty(productDetails)){
				               productDetails=EntityUtil.getFirst(productDetails);
				               itemCode=productDetails.internalName;
				               description=productDetails.description;
				               quantityUomId=productDetails.quantityUomId;
				               enquiryMap.put("itemCode",itemCode);
				               enquiryMap.put("description",description);
				               enquiryMap.put("quantityUomId",quantityUomId);
			             }
			             context.enquiryMap=enquiryMap;
			      }  
			   
		   }
           else{	   	   
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
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "FAX_BILLING"));
			   conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachPartyId));
			   cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			   faxNumber="";
			   listAddress = delegator.findList("PartyContactDetailByPurpose", cond , null, null, null, false );
			   if(UtilValidate.isNotEmpty(listAddress.contactNumber)){
				  faxNumber = listAddress.contactNumber;
				  partyMap.put("faxNuber", faxNumber);
				}
			   context.partyMap=partyMap;
			   context.put("custReqDate",custReqDate);
			   context.put("dueDate",dueDate);
			   partyAddressMap.put(eachPartyId,partyMap);
			   enquiryMap=[:];
			   productId ="";
			   custReqItemDetails = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
			   if(UtilValidate.isNotEmpty(custReqItemDetails)){
				  custReqItemDetails=EntityUtil.getFirst(custReqItemDetails);
				  productId=custReqItemDetails.productId;
				  requrdqty=custReqItemDetails.quantity;
				  enquiryMap.put("productId",productId);
				  enquiryMap.put("requrdqty",requrdqty);
			   }
			   productDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productId)  , null, null, null, false );
			   productUomDetails=EntityUtil.getFirst(productDetails);
			   if(UtilValidate.isNotEmpty(productUomDetails)){
				     uomId=productUomDetails.quantityUomId;
				     if(UtilValidate.isNotEmpty(uomId)){
				        uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
				        uomDesc=EntityUtil.getFirst(uomDesc);
				        enquiryMap.put("unit",uomDesc.abbreviation);
				      }
			   }
			  if(UtilValidate.isNotEmpty(productDetails)){
				   productDetails=EntityUtil.getFirst(productDetails);
				   itemCode=productDetails.internalName;
				   description=productDetails.description;
				   quantityUomId=productDetails.quantityUomId;
				   enquiryMap.put("itemCode",itemCode);
				   enquiryMap.put("description",description);
				   enquiryMap.put("quantityUomId",quantityUomId);
			   }
			   context.enquiryMap=enquiryMap;
			  
            }
	   
	  
        }    
    }
    context.partyAddressMap=partyAddressMap;
}

