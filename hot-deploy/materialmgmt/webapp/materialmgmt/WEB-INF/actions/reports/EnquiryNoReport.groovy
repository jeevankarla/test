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

custRequestId=parameters.issueToEnquiryNo;
custReqDetails = delegator.findOne("CustRequest", [custRequestId : custRequestId], false);
if(UtilValidate.isNotEmpty(custReqDetails)){
	enquiryDate = custReqDetails.custRequestDate;
	context.put("enquiryDate",enquiryDate);		
}

List EnquiryDetList=[];
quoteItemList=delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId),null, null,null,false);
partyIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "partyId", true);
productIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "productId", true);
productPriceMap = [:];
if(UtilValidate.isNotEmpty(productIds)){	
	productIds.each{eachproductId->		
		
	if(UtilValidate.isNotEmpty(partyIds)){		
		partyPriceMap = [:];
	    partyIds.each{eachPartyId->	
			
			partyNameMap=[:];
			partyNameMap["price"]=0;
			partyNameMap["quantity"]=0;							
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,eachproductId));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachPartyId));
			conditionList.add(EntityCondition.makeCondition("crStatusId", EntityOperator.NOT_EQUAL,"CRQ_CANCELLED"));			
			cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			prodList=delegator.findList("QuoteAndItemAndCustRequest",cond,null,null,null,false);
			if(prodList)
			{
			    price=(prodList.get(0)).quoteUnitPrice;
				quantity=(prodList.get(0)).quantity;	
				amount=price*quantity;
				priceMap = [:];
				priceMap.put("price",price);
				priceMap.put("quantity",quantity);
				priceMap.put("amount",amount);
				partyPriceMap.put(eachPartyId, priceMap);
			}			
		}		
		productPriceMap.put(eachproductId, partyPriceMap);
      }		
    }
}	
context.productPriceMap=productPriceMap;
conditionList=[];
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN,productIds));
cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productDetails=delegator.findList("Product",cond,UtilMisc.toSet("productId","productName","quantityIncluded","piecesIncluded"),null,null,false);
prodNameMap = [:];
for(i=0; i<productDetails.size(); i++){
	
	eachProdDetail = productDetails.get(i);
	
	priceMap = [:];
	priceMap.put("productName",eachProdDetail.productName);
	priceMap.put("quantity",eachProdDetail.quantityIncluded);
	prodNameMap.put(eachProdDetail.productId, priceMap);	
}
context.prodNameMap=prodNameMap;

partyDetList = [];
if(UtilValidate.isNotEmpty(partyIds)){	
	partyIds.each{eachPartyId->
		partyNameMap=[:];
		partyNameMap["partyId"] = eachPartyId;
		partyNameMap["partyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, eachPartyId, false);
		partyDetList.addAll(partyNameMap);
	}
}
context.partyDetList=partyDetList;

quoteDetailList =[];
quoteIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "quoteId", true);
if(UtilValidate.isNotEmpty(quoteIds)){
	quoteIds.each{eachQuoteId->		
		quoteDetailsMap=[:];
	    conditionList=[];
	    conditionList.add(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS,eachQuoteId));
		cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		quoteList=delegator.findList("QuoteTerm",cond,UtilMisc.toSet("quoteId","termTypeId","termValue"),null,null,false);		
		if(UtilValidate.isNotEmpty(quoteList)){
			quoteDetails=EntityUtil.getFirst(quoteList);
			termTypeId=quoteDetails.termTypeId;
			termValue=quoteDetails.termValue;
			quoteDetailsMap["termTypeId"]=termTypeId;
			quoteDetailsMap["termValue"]=termValue;
			quoteDetailList.addAll(quoteDetailsMap)	
	   }
   }
}
context.quoteDetailList=quoteDetailList;
