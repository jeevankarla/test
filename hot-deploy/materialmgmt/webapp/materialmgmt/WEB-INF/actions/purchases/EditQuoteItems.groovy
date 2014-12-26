import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.util.Map;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;binding



if(parameters.quoteId){
	quoteId=parameters.quoteId;
}else{
return;
}
quoteList= delegator.findByPrimaryKey("Quote", [quoteId : quoteId]);
context.partyId = quoteList.get("partyId");
context.issueDate= UtilDateTime.toDateString(quoteList.get("issueDate"), "dd MMMM, yyyy");
context.quoteName=quoteList.get("quoteName");
context.validFromDate=UtilDateTime.toDateString(quoteList.get("validFromDate"), "dd MMMM, yyyy");
context.validThruDate=UtilDateTime.toDateString(quoteList.get("validThruDate"), "dd MMMM, yyyy");
context.quoteId=quoteId;
quoteList=[];
quoteItemList=[];
itemList=[];
condition = EntityCondition.makeCondition([
	EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId)],
	EntityOperator.AND);
quoteItemList=delegator.findList("QuoteItem",condition,null,null,null,false);

if(quoteItemList){
	quoteItemList.each{ item ->
		itemsMap=[:];
		itemsMap.put("productId", item.productId);
		productName="";
		product = delegator.findOne("Product",["productId":item.productId],false);
		if(product){
			productName=product.get("productName");
		}
		itemsMap.put("productName", productName);
		itemsMap.put("quantity", item.quantity);
		itemsMap.put("unitPrice", item.quoteUnitPrice);
		itemsMap.put("quoteItemSeqId", item.quoteItemSeqId);
		itemList.add(itemsMap);
	}
}
context.putAt("itemList", itemList);
