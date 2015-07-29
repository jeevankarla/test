import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;
 
 Debug.log("flag ========"+parameters.findPoFlag);
if(UtilValidate.isEmpty(parameters.productId)){
	if(UtilValidate.isNotEmpty(result.listIt)){
		list=result.listIt;
		resultList = [];
		GenericValue poEntry = null;
		while ((poEntry=list.next()) != null) {
			if(parameters.findPoFlag=="Y"){
				
				if(parameters.orderTypeId=="PURCHASE_ORDER"){
					if((poEntry.roleTypeId).equals("SUPPLIER_AGENT")){
						resultList.add(poEntry);
					}
				}
				else{
					Debug.log("poEntry ===1111====="+poEntry);
					if((poEntry.roleTypeId).equals("BILL_FROM_VENDOR")){
						resultList.add(poEntry);
					}
				}
			}
			else{
				if((poEntry.roleTypeId).equals("BILL_FROM_VENDOR")){
					resultList.add(poEntry);
					Debug.log("poEntry ========"+poEntry);
				}
			}
				
		}
			Debug.log("poEntry ========"+poEntry);
			context.listIt=resultList;
	}
}
	else{
		productId = parameters.productId;
		
		if(UtilValidate.isNotEmpty(result.listIt)){
			list=result.listIt;
			poListNew=[];
			GenericValue poEntry = null;
			while ((poEntry=list.next()) != null) {
					conditionList=[];
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poEntry.orderId));
					condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					orderList = delegator.findList("OrderItem", condition, null, null, null, false);
					if(UtilValidate.isNotEmpty(orderList)){
					poListNew.add(poEntry);
					}
				}
			list.close();
		}
		
		context.listIt=poListNew;

}

if(UtilValidate.isNotEmpty(context.orderId)){
	orderId=context.orderId;
	ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId)],EntityOperator.AND);
	orderItems=delegator.findList("OrderItem",ecl,null,null,null,false);
	condition=EntityCondition.makeCondition([EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId),EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"SR_REJECTED"),EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"SR_CANCELLED")],EntityOperator.AND);
	shipmentReceipts=delegator.findList("ShipmentReceipt",condition,null,null,null,false);
	orderItemsSize=orderItems.size();
	if(UtilValidate.isNotEmpty(shipmentReceipts)){
		orderItems.each{item->
			reeceivedQty=0;
			shipReceipts=EntityUtil.filterByCondition(shipmentReceipts,EntityCondition.makeCondition([EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,item.orderId),EntityCondition.makeCondition("orderItemSeqId",EntityOperator.EQUALS,item.orderItemSeqId)],EntityOperator.AND));
			shipReceipts.each{receipt->
				reeceivedQty+=receipt.quantityAccepted;
			}
			if(item.quantity!=reeceivedQty){
				orderItemsSize-=1;
			}
		}
	}else{
		orderItemsSize=0;
	}
	context.orderItemsSize=orderItemsSize;
	
	
}