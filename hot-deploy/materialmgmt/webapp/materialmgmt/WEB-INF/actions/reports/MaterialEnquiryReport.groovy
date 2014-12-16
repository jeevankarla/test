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

dctx = dispatcher.getDispatchContext();
companyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, "Company")).get("partyDetails");
	if(UtilValidate.isNotEmpty(companyDetail)){
	  companyTinNumber=companyDetail.get('TIN_NUMBER');
	}
context.put("companyTinNumber",companyTinNumber);
custRequestId=parameters.issueToCustReqId;
partyAddressMap=[:];
custReqDetails = delegator.findOne("CustRequest", [custRequestId : custRequestId], false);
if(UtilValidate.isNotEmpty(custReqDetails)){
   custReqDate = custReqDetails.custRequestDate;
   fromPartyId	 = custReqDetails.fromPartyId;
   partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:fromPartyId, userLogin: userLogin]);
   if(partyPostalAddress){     
      if(partyPostalAddress.address1){
        address1=partyPostalAddress.address1;
       }
	  partyAddressMap.put("address1",address1);
	  if(partyPostalAddress.address2){
		  address2=partyPostalAddress.address2;
	  }
	  partyAddressMap.put("address2",address2);
	  if(partyPostalAddress.city){
		  city=partyPostalAddress.city;
	  }
	  partyAddressMap.put("city",city);
	  if(partyPostalAddress.postalCode){
		  postalCode=partyPostalAddress.postalCode;
	  }
	  partyAddressMap.put("postalCode",postalCode);	  
   } 
   context.partyAddressMap=partyAddressMap;
 context.put("custReqDate",custReqDate);
}
enquiryMap=[:];
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