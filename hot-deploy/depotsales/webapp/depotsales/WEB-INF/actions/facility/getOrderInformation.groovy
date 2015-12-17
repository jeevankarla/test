import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.math.RoundingMode;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;




Debug.log("orderId==========================="+parameters.orderId);

orderId = parameters.orderId;




orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

Debug.log("orderItems==========================="+orderItems);

JSONArray orderInformationDetails = new JSONArray();

for (eachItem in orderItems) {
	
	
	JSONObject orderDetail = new JSONObject();
	
	   orderDetail.put("productId", eachItem.productId);
	   orderDetail.put("prductName", eachItem.itemDescription);
	   orderDetail.put("quantity", eachItem.quantity);
	   orderDetail.put("unitPrice", eachItem.unitPrice);
	   orderDetail.put("statusId", eachItem.statusId);
	
	   orderInformationDetails.add(orderDetail);
	   
}


request.setAttribute("orderInformationDetails", orderInformationDetails);
return "success";

