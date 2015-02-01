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

custRequestId=parameters.issueToCustReqId;
custReqDetails = delegator.findOne("CustRequest", [custRequestId : custRequestId], false);
if(UtilValidate.isNotEmpty(custReqDetails)){
   custReqDate = custReqDetails.custRequestDate;
   dueDate= custReqDetails.closedDateTime;
   context.put("dueDate",dueDate);
   context.put("custReqDate",custReqDate);
}
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
if(UtilValidate.isNotEmpty(productDetails)){
  productDetails=EntityUtil.getFirst(productDetails);
  itemCode=productDetails.internalName;
  description=productDetails.description;
  uomId=productDetails.quantityUomId;
  enquiryMap.put("itemCode",itemCode);
  enquiryMap.put("description",description);
  if(UtilValidate.isNotEmpty(uomId)){
	  uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
	  uomDesc=EntityUtil.getFirst(uomDesc);
	  enquiryMap.put("unit",uomDesc.description);
	  
  }
}
context.enquiryMap=enquiryMap;
vendorList=[];
partyIdsList=delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId),UtilMisc.toSet("quoteId","partyId"), null,null,false);
partyIds = EntityUtil.getFieldListFromEntityList(partyIdsList, "partyId", true);
if(UtilValidate.isNotEmpty(partyIds)){
	partyIds.each{eachPartyId->
		partyIdsDetailsMap=[:];	
		partyIdsDetailsMap.put("partyId",eachPartyId);
		partyIdsDetailsMap["partyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, eachPartyId, false);
		partyIdsDetailsMap["contactNumber"]="";			
		partyContactDetails=dispatcher.runSync("getPartyTelephone", [partyId: eachPartyId, userLogin: userLogin]);
		if(UtilValidate.isNotEmpty(partyContactDetails)){
			if(UtilValidate.isNotEmpty(partyContactDetails.contactNumber)){
				contactNumber=partyContactDetails.contactNumber;
				partyIdsDetailsMap.put("contactNumber",contactNumber);
			}
		 }
		faxId="FAX_BILLING";
			partyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId: eachPartyId, contactMechPurposeTypeId: faxId, userLogin: userLogin]);
			faxNumber = "";
			if (partyFaxNumber != null && partyFaxNumber.contactNumber != null) {
				faxNumber = partyFaxNumber.contactNumber;
				partyIdsDetailsMap.put("faxNumber", faxNumber);				
			}        
		vendorList.addAll(partyIdsDetailsMap);
	}	
}
context.vendorList=vendorList;

