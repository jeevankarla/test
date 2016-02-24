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

orderId = parameters.orderId;

orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "quotaQty"));
orderAttributes = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
OrderItemRemarks = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);


JSONArray orderInformationDetails = new JSONArray();

for (eachItem in orderItems) {
	
	adjustmentAmount = 0;
	quotaAvbl = 0;
	if(UtilValidate.isNotEmpty(orderAdjustments)){
		orderItemAdj = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		if(UtilValidate.isNotEmpty(orderItemAdj)){
			adjustmentAmount = (orderItemAdj.get(0)).get("amount");
		}
	}
	
	if(UtilValidate.isNotEmpty(orderAttributes)){
		orderItemAttributes = EntityUtil.filterByCondition(orderAttributes, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		if(UtilValidate.isNotEmpty(orderItemAttributes)){
			quotaAvbl = (orderItemAttributes.get(0)).get("attrValue");
		}
	}
	remarks = "";
	if(UtilValidate.isNotEmpty(OrderItemRemarks)){
		orderRemarks = EntityUtil.filterByCondition(OrderItemRemarks, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
	
		if(UtilValidate.isNotEmpty(orderRemarks)){
			remarks = (orderRemarks.get(0)).get("attrValue");
		}
	}
	
	
	JSONObject orderDetail = new JSONObject();
	orderDetail.put("productId", eachItem.productId);
	orderDetail.put("prductName", eachItem.itemDescription);
	orderDetail.put("quantity", eachItem.quantity);
	orderDetail.put("unitPrice", eachItem.unitPrice);
	orderDetail.put("itemAmt", (eachItem.quantity)*(eachItem.unitPrice));
	orderDetail.put("statusId", eachItem.statusId);
	orderDetail.put("adjustmentAmount", adjustmentAmount);
	orderDetail.put("quotaAvbl", quotaAvbl);
	orderDetail.put("remarks", remarks);

	orderDetail.put("payableAmt", (eachItem.quantity)*(eachItem.unitPrice) + adjustmentAmount);
	orderInformationDetails.add(orderDetail);
	   
}


request.setAttribute("orderInformationDetails", orderInformationDetails);
return "success";

