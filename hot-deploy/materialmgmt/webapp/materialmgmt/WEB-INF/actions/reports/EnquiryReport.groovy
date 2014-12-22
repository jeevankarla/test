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
   responseRequiredDate=custReqDetails.responseRequiredDate;   
   context.put("custReqDate",custReqDate);
   context.put("responseRequiredDate",responseRequiredDate);
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
  quantityUomId=productDetails.quantityUomId;
  enquiryMap.put("itemCode",itemCode);
  enquiryMap.put("description",description);
  enquiryMap.put("quantityUomId",quantityUomId);
}
context.enquiryMap=enquiryMap;
vendorList=[];
partyIdsList=delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId),UtilMisc.toSet("quoteId","partyId"), null,null,false);
partyIds = EntityUtil.getFieldListFromEntityList(partyIdsList, "partyId", true);
if(UtilValidate.isNotEmpty(partyIds)){
	partyIds.each{eachPartyId->
		partyIdsDetailsMap=[:];		
		partyIdsDetailsMap["contactNumber"]="";	
		partyIdsDetailsMap.put("partyId",eachPartyId);
		partyContactDetails=dispatcher.runSync("getPartyTelephone", [partyId: eachPartyId, userLogin: userLogin]);
		if(UtilValidate.isNotEmpty(partyContactDetails)){
			if(UtilValidate.isNotEmpty(partyContactDetails.contactNumber)){
				contactNumber=partyContactDetails.contactNumber;
				partyIdsDetailsMap.put("contactNumber",contactNumber);
			}
		 }
        partyIdsDetailsMap["partyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, eachPartyId, false);
		vendorList.addAll(partyIdsDetailsMap);		
	}	
}
context.vendorList=vendorList;