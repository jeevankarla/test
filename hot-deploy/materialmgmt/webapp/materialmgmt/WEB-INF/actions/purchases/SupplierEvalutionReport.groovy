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

orderId=parameters.orderId;
context.orderId=orderId;
conditionList = [];
supplierEvalMap = [:];
OrderItemDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(OrderItemDetails)){
	   quoteId=OrderItemDetails.get(0).quoteId;
	   if(UtilValidate.isNotEmpty(quoteId)){		   
			   QuoteAndItemAndCustRequest = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
			   custRequestId=QuoteAndItemAndCustRequest.custRequestId;			   
			   QuoteAndItemAndCustRequestDetails =  delegator.findList("QuoteAndItemAndCustRequest", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),  null, null, null, false );			  
			   if(UtilValidate.isNotEmpty(QuoteAndItemAndCustRequestDetails)){
				      quoteIds = EntityUtil.getFieldListFromEntityList(QuoteAndItemAndCustRequestDetails, "quoteId", true);					  
					  quoteIds.each{eachQuoteId->
							  quoteItemMap=[:];
							  
							  supplierDetails = EntityUtil.filterByCondition(QuoteAndItemAndCustRequestDetails, EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, eachQuoteId));
							  productId = supplierDetails.productId;
							  productId.each{eachproductId->
									  productDetails = delegator.findOne("Product",["productId":eachproductId],false);
									  if(UtilValidate.isNotEmpty(productDetails)){
											productName =  productDetails.productName;
											quoteItemMap.put("productName", productName);
									  }									  									  
							  }				  						  
							  eachpartyId = supplierDetails.partyId;
							  eachpartyId.each{partyId->
									  partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);			   
									  quoteItemMap.put("partyName", partyName);
							  }
							  OrderHeaderDetails = delegator.findOne("OrderHeader",["orderId":orderId],false);
							  if(UtilValidate.isNotEmpty(OrderHeaderDetails)){
								  orderDate =  OrderHeaderDetails.orderDate;
								  quoteItemMap.put("orderDate", orderDate);								  
							  }							  
							  QuoteStatus = delegator.findList("QuoteStatus",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , eachQuoteId)  , null, null, null, false );
							  QuoteStatusList = EntityUtil.orderBy(QuoteStatus,UtilMisc.toList("-statusDatetime"));							  
							  QuoteStatusDetails= EntityUtil.getFirst(QuoteStatusList);
							  if(UtilValidate.isNotEmpty(QuoteStatusDetails)){							       
									statusId = QuoteStatusDetails.statusId;										
									if(statusId){
										quoteItemMap.put("statusId", statusId);										
								    }
							  }
							  supplierEvalMap.put(eachQuoteId, quoteItemMap);
				     }
              }
        }
}
context.supplierEvalMap=supplierEvalMap;
