import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;

if(shipmentId){
	
	generateInvoice = "Y";
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "MATERIAL_SHIPMENT"));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
	shipmentHeader = delegator.findList("Shipment", condExpr, null, null, null, false);
	if(!shipmentHeader){
		return "error";
		generateInvoice = "N";
	}
	Debug.log("generateInvoice shipmentHeader##############"+generateInvoice);
	shipmentHeader = EntityUtil.getFirst(shipmentHeader);
	
	purchaseOrderId = shipmentHeader.primaryOrderId;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, purchaseOrderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderRole = delegator.findList("OrderRole", cond, null, null, null, false);
	supplierId = (EntityUtil.getFirst(orderRole)).get("partyId");
	if(!supplierId){
		generateInvoice = "N";
	}
	Debug.log("generateInvoice supplier##############"+generateInvoice);
	shipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null,false);
	
	if(shipmentItems.size() == 0){
		generateInvoice = "N";
	}
	Debug.log("generateInvoice shipmentitems##############"+generateInvoice);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, purchaseOrderId));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("quantityAccepted", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_ACCEPTED"));
	cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	shipmentReceipts = delegator.findList("ShipmentReceipt", cond, null, null, null, false);
	
	if(!shipmentReceipts){
		generateInvoice = "N";
	}
	Debug.log("generateInvoice receipts###############"+generateInvoice);
	context.showView = generateInvoice;
	context.shipmentItems = shipmentItems;
	context.shipmentHeader = shipmentHeader;
	context.shipmentReceipts = shipmentReceipts;
	context.supplierId = supplierId;
}
