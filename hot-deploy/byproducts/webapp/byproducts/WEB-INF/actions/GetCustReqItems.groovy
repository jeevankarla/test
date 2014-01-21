import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.text.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

List conditionList=[];

custReqItemsList = delegator.findList("CustRequestItem", null , null, null, null, false );

custReqItemList = [];
custReqItemsList.each{eachItem ->
	
	custReqItemMap = [:];
	custRequestId = eachItem.getAt("custRequestId");
	custRequestItemSeqId = eachItem.getAt("custRequestItemSeqId");
	statusId = eachItem.getAt("statusId");
	productId = eachItem.getAt("productId");
	quantity = eachItem.getAt("quantity");
	
	product = delegator.findOne("Product", [productId : productId], false);
	productName = product.brandName;
	
		custReqItemMap.putAt("custRequestId", custRequestId);
		custReqItemMap.putAt("custRequestItemSeqId", custRequestItemSeqId);
		custReqItemMap.putAt("statusId", statusId);
		custReqItemMap.putAt("productId", productId);
		custReqItemMap.putAt("quantity", quantity);
		custReqItemMap.putAt("productName", productName);
		custReqItemList.add(custReqItemMap);
}

JSONObject custReqJSON = new JSONObject();
custReqJSON.put("custReqItems", custReqItemList);
context.custReqJSON = custReqJSON;

