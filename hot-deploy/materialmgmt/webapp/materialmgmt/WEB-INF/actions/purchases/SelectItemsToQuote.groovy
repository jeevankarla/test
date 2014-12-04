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
import org.ofbiz.base.util.UtilDateTime;

tenderId = context.custRequestId;
itemList = [];
tenderItemsList = delegator.findList("CustRequestItem", EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, tenderId), null , null, null, false);
if(tenderItemsList.size()>0){
	tenderItemsList.each{eachItem ->
		tenderItemsMap = [:];
		tenderItem = eachItem.getAt("custRequestItemSeqId");
		productId = eachItem.getAt("productId");
		quantity = eachItem.getAt("quantity");
		product = delegator.findOne("Product",["productId":productId],false);
		tenderItemsMap.putAt("custRequestId", tenderId);
		tenderItemsMap.putAt("custRequestItemSeqId", tenderItem);
		tenderItemsMap.putAt("productId", productId);
		tenderItemsMap.putAt("productName", product.get("productName"));
		tenderItemsMap.putAt("quantity", quantity);
		itemList.add(tenderItemsMap);		
	}
}
context.putAt("itemList", itemList);