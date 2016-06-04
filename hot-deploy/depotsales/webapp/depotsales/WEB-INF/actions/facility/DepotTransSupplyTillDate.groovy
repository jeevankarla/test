import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
if(UtilValidate.isNotEmpty(result.listIt)){
	list = [];
	list=result.listIt.getCompleteList();
	receiptList=[];
	shipmentList=result.listIt.getCompleteList();
	if(UtilValidate.isNotEmpty(parameters.orderNo)){
		draftOrderNo = parameters.orderNo;
		draftOrderIdDetails = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderNo", EntityOperator.EQUALS , draftOrderNo)  , UtilMisc.toSet("orderId"), null, null, false );
		if(UtilValidate.isNotEmpty(draftOrderIdDetails)){
			draftOrderIdDetails = EntityUtil.getFirst(draftOrderIdDetails);
			orderId = draftOrderIdDetails.orderId;
			list = EntityUtil.filterByCondition(list, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		}
	}
	list.each{receipt->
		tempMap=[:];
		receiptId="";
		orderId="";
		productId="";
		shipmentId="";
		statusId="";
		quantityAccepted=0;
		datetimeReceived="";
		quantityRejected=0;
		receivedByUserLoginId="";
		orderItemSeqId="";
		supplyTillDate=0;
		if(UtilValidate.isNotEmpty(receipt.receiptId)){
			receiptId=receipt.receiptId;
		}
		tempMap.put("receiptId", receiptId)
		if(UtilValidate.isNotEmpty(receipt.orderId)){
			orderId=receipt.orderId;
		}
		orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , UtilMisc.toSet("orderNo"), null, null, false );
		if(UtilValidate.isNotEmpty(orderHeaderSequences)){
			orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
			salesOrder = orderSeqDetails.orderNo;
			tempMap.put("orderId",salesOrder);
		}else{
			tempMap.put("orderId", orderId);
		}
		//tempMap.put("orderId", orderId);
		if(UtilValidate.isNotEmpty(receipt.orderItemSeqId)){
			orderItemSeqId=receipt.orderItemSeqId;
		}
		if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderItemSeqId)){
			condition = EntityCondition.makeCondition([EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
			EntityCondition.makeCondition("orderItemSeqId",EntityOperator.EQUALS, orderItemSeqId)],EntityOperator.AND);
			shipmentReceipts=delegator.findList("ShipmentReceipt",condition,null,null,null,false);
			shipmentReceipts.each{shipmentReceipt->
				supplyTillDate=supplyTillDate+shipmentReceipt.quantityAccepted;
			}
		}
		tempMap.put("supplyTillDate", supplyTillDate);
		if(UtilValidate.isNotEmpty(receipt.productId)){
			productId=receipt.productId;
		}
		tempMap.put("productId", productId);
		if(UtilValidate.isNotEmpty(receipt.datetimeReceived)){
			datetimeReceived=receipt.datetimeReceived;
		}
		tempMap.put("datetimeReceived", datetimeReceived);
		if(UtilValidate.isNotEmpty(receipt.shipmentId)){
			shipmentId=receipt.shipmentId;
		}
		tempMap.put("shipmentId", shipmentId);
		if(UtilValidate.isNotEmpty(receipt.statusId)){
			statusId=receipt.statusId;
		}
		tempMap.put("statusId", statusId);
		if(UtilValidate.isNotEmpty(receipt.quantityAccepted)){
			quantityAccepted=receipt.quantityAccepted;
		}
		tempMap.put("quantityAccepted", quantityAccepted);
		if(UtilValidate.isNotEmpty(receipt.quantityRejected)){
			quantityRejected=receipt.quantityRejected;
		}
		tempMap.put("quantityRejected", quantityRejected);
		if(UtilValidate.isNotEmpty(receipt.receivedByUserLoginId)){
			receivedByUserLoginId=receipt.receivedByUserLoginId;
		}
		tempMap.put("receivedByUserLoginId", receivedByUserLoginId);
		receiptList.add(tempMap);
	}
	context.listIt=receiptList;
}
orderRolesList = [];
orderRoles = delegator.findList("OrderRole",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , parameters.orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(orderRoles)){
	orderRoles.each{eachOrderRole->
		
		tempMap=[:];
		partyId = eachOrderRole.partyId;
		roleTypeId = eachOrderRole.roleTypeId;
		tempMap["partyName"] = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, partyId, false);
		roleTypes = delegator.findOne("RoleType",["roleTypeId":roleTypeId],false);
		if(UtilValidate.isNotEmpty(roleTypes)){
			 orderRoleType = roleTypes.description;
			tempMap.put("orderRoleType",orderRoleType);
			orderRolesList.add(tempMap);
		}
		
	}
	context.orderRolesList = orderRolesList;
}

