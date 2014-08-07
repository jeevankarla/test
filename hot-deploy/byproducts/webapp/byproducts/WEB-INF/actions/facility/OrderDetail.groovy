import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

dctx = dispatcher.getDispatchContext();

orderId = parameters.orderId;
JSONObject orderDetailJSON = new JSONObject();
orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

JSONArray orderItemListJSON = new JSONArray();
orderItems.each { eachItem ->
	taxPercent = 0;
	if(eachItem.vatPercent && eachItem.vatPercent>0){
		taxPercent = eachItem.vatPercent;
	}
	if(eachItem.cstPercent && eachItem.cstPercent>0){
		taxPercent = eachItem.cstPercent;
	}
	
	JSONObject itemJSON = new JSONObject();
	itemJSON.put("productId", eachItem.productId);
	itemJSON.put("itemDescription", eachItem.itemDescription);
	itemJSON.put("quantity", eachItem.quantity);
	itemJSON.put("taxPercent", taxPercent);
	itemJSON.put("itemTotal", eachItem.quantity*eachItem.unitListPrice);
	orderItemListJSON.add(itemJSON);
}

context.orderItemListJSON = orderItemListJSON;
request.setAttribute("orderItemListJSON", orderItemListJSON);
return "success";
