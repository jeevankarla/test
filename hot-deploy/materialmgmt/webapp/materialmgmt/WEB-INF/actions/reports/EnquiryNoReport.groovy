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

if(UtilValidate.isNotEmpty(parameters.sign)){
	signature=parameters.sign;
	context.signature=signature;
}
CustRequestSequenceDetails = delegator.findList("CustRequestSequence",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
CustRequestSequence= EntityUtil.getFirst(CustRequestSequenceDetails);
if(CustRequestSequence){
	if(UtilValidate.isNotEmpty(CustRequestSequence.custRequestNo)){
		enquirySequenceNo=CustRequestSequence.custRequestNo;
		context.enquirySequenceNo=enquirySequenceNo;
	}
}
custReqDetails = delegator.findOne("CustRequest", [custRequestId : custRequestId], false);
if(UtilValidate.isNotEmpty(custReqDetails)){
	enquiryDate = custReqDetails.custRequestDate;
	context.put("enquiryDate",enquiryDate);		
}

productQtyMap = [:];
custRequestItem = delegator.findList("CustRequestItem", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId),null, null,null,false);
if(UtilValidate.isNotEmpty(custRequestItem)){
	custRequestItem.each{eachItem->
	  productQtyMap[eachItem.productId] = eachItem.quantity;
	}
	context.put("productQtyMap",productQtyMap);		
}
List EnquiryDetList=[];
EnquiryDetList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
/*EnquiryDetList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "QUO_REJECTED"));*/
EnquiryDetList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "QUO_CANCELED"));
cond = EntityCondition.makeCondition(EnquiryDetList, EntityOperator.AND);
quoteItemList=delegator.findList("QuoteAndItemAndCustRequest", cond,null, null,null,false);
partyIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "partyId", true);
quoteIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "quoteId", true);
productIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "productId", true);
productPriceMap = [:];
poDateMap = [:];
statusMap=[:];

if(UtilValidate.isNotEmpty(productIds)){	
	productIds.each{eachproductId->		
		/*List exprList = [];
		exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"PURCHASE_ORDER"));
		exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
		exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
		exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS ,eachproductId));
		condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		OrderHeaderAndItemsList = delegator.findList("OrderHeaderAndItems", condition, null, ["-orderDate"], null, false);
		if(UtilValidate.isNotEmpty(OrderHeaderAndItemsList)){
			OrderHeaderAndItemsList = EntityUtil.getFirst(OrderHeaderAndItemsList);
			poDateMap[eachproductId]=UtilDateTime.toDateString(OrderHeaderAndItemsList.orderDate, "dd/MM/yyyy");
		}*/
		
		if(UtilValidate.isNotEmpty(partyIds)){		
			partyPriceMap = [:];
		    partyIds.each{eachPartyId->	
				partyNameMap=[:];
				partyNameMap["price"]=0;
				partyNameMap["quantity"]=0;							
				conditionList=[];
				priceMap = [:];
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,eachproductId));
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachPartyId));
				conditionList.add(EntityCondition.makeCondition("quoteId", EntityOperator.IN,quoteIds));
				conditionList.add(EntityCondition.makeCondition("crStatusId", EntityOperator.NOT_EQUAL,"CRQ_CANCELLED"));			
				cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				prodList=delegator.findList("QuoteAndItemAndCustRequest",cond,null,null,null,false);
				if(prodList){
				    price=(prodList.get(0)).quoteUnitPrice;
					quantity=(prodList.get(0)).quantity;	
					amount=(prodList.get(0)).itemTotal;
					itemStatus=(prodList.get(0)).qiStatusId;
					priceMap.put("price",price);
					priceMap.put("quantity",quantity);
					priceMap.put("amount",amount);
					priceMap.putAt("itemStatus", itemStatus);
					if(UtilValidate.isEmpty(statusMap[eachPartyId])){
					statusMap[eachPartyId]=prodList.get(0).statusId;
					}
				}	
				partyPriceMap.put(eachPartyId, priceMap);
			}		
			productPriceMap.put(eachproductId, partyPriceMap);
	      }		
    }
}	
context.productPriceMap=productPriceMap;
//Debug.log("productpriceMap#####################"+productPriceMap);
context.statusMap=statusMap;
/*conditionList=[];
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
*/
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
context.poDateMap=poDateMap;
//Comapare  termValue Of All Vendors 
allTermsMap=[:];
termTypeDetails = delegator.findList("QuoteTerm", EntityCondition.makeCondition("quoteId", EntityOperator.IN,quoteIds),null, null,null,false);
termTypeIds = EntityUtil.getFieldListFromEntityList(termTypeDetails, "termTypeId", true);
context.termTypeIdList=termTypeIds;
/*termTypeIds.each{eachTermType->
	termDetailsMap=[:];	
	termTypeDetails = delegator.findOne("TermType", ["termTypeId" : eachTermType], false);
	description = termTypeDetails.description;	
	quoteIds.each{eachQuoteId->
		partyDetails = delegator.findOne("Quote", ["quoteId" : eachQuoteId], false);
		partyId=partyDetails.partyId;
		termTypeDetails = delegator.findList("QuoteTerm", EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS,eachQuoteId),null, null,null,false);
		termTypeDetail = EntityUtil.filterByCondition(termTypeDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, eachTermType));		
		if(UtilValidate.isNotEmpty(termTypeDetail)){
				termTypeDetail = EntityUtil.getFirst(termTypeDetail);
				termTypeId=termTypeDetail.termTypeId;
				
				termValue=termTypeDetail.termValue;
			    termDetailsMap.put(partyId,termValue);		
		}
		else{			
				termValue="";
				termDetailsMap.put(partyId,termValue);
			
		}
	}
	allTermsMap.put(description,termDetailsMap);
}
context.allTermsMap=allTermsMap;*/
newQuoteTerms=delegator.findList("QuoteTerm",EntityCondition.makeCondition("quoteId", EntityOperator.IN,quoteIds),null, null,null,false);
quotesMap=[:];
partyMap=[:];
quoteIds.each{quoteId->
	partyDetails = delegator.findOne("Quote", ["quoteId" : quoteId], false);
	partyId=partyDetails.partyId;
	partyMap[quoteId]=partyId;
	quoteTermsQuoteWise=EntityUtil.filterByCondition(newQuoteTerms,EntityCondition.makeCondition([EntityCondition.makeCondition("quoteId",EntityOperator.EQUALS,quoteId)],EntityOperator.AND));
		quotesMap[partyId]=quoteTermsQuoteWise;
	
}
//Debug.log("quotesMap==================="+quotesMap);

//quoteTermsList=delegator.findList("QuoteTermAndItemAndQuote", EntityCondition.makeCondition("quoteId", EntityOperator.IN,quoteIds),null, null,null,false);
finalMap=[:];
termTypeIds.each{termType->
	tempList=[];
		partyIds.each{partyId->
		tempMap=[:];
		quoteTerms=quotesMap.get(partyId);
		quoteTerms=EntityUtil.filterByCondition(quoteTerms,EntityCondition.makeCondition([EntityCondition.makeCondition("termTypeId",EntityOperator.EQUALS,termType)],EntityOperator.AND));
				tempMap.put(partyId, quoteTerms);	
			if(UtilValidate.isNotEmpty(tempMap)){
				tempList.add(tempMap);
			}
			
		finalMap.put(termType, tempList);	
	}
}
context.finalMap=finalMap;
/*partyFinalMap=[:];
partyIds.each{partyId->
	tempList=[];
		termTypeIds.each{termType->
		tempMap=[:];
		quoteTerms=quotesMap.get(partyId);
		quoteTerms=EntityUtil.filterByCondition(quoteTerms,EntityCondition.makeCondition([EntityCondition.makeCondition("termTypeId",EntityOperator.EQUALS,termType)],EntityOperator.AND));
				tempMap.put(termType, quoteTerms);
			if(UtilValidate.isNotEmpty(tempMap)){
				tempList.add(tempMap);
			}
			
		partyFinalMap.put(partyId, tempList);
	}
}

context.partyFinalMap=partyFinalMap;*/


