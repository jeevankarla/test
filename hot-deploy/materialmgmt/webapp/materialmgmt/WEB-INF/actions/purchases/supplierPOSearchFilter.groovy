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

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
parameters.supplierId=parameters.partyId;

Map formatMap = [:];
List formatList = [];
List branchIds = [];
	for (eachList in resultCtx.get("productStoreList")) {
		
		formatMap = [:];
		formatMap.put("storeName",eachList.get("storeName"));
		formatMap.put("payToPartyId",eachList.get("payToPartyId"));
		formatList.addAll(formatMap);
		branchIds.add(eachList.get("payToPartyId"));
		
	}
context.branchList = formatList;
 userPartyId = userLogin.partyId;
 partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", userPartyId, "roleTypeId", "SUPPLIER"), false);
 if(partyRole){
	 
	 context.userLog = "Y";
	context.partyId = userPartyId;
 }else{
     context.userLog = "N";
 }
 // To filter based on suppliers
 
 supplierFilteredOrderIds = [];
 
 if(UtilValidate.isNotEmpty(parameters.poRefNo)){
	 if(UtilValidate.isNotEmpty(result.listIt)){
		 list3=result.listIt;
		 list2=[];
		 list1=[];
		 templist=[];
		 conditionList=[];
		 conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REF_NUMBER"));
		 conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, parameters.poRefNo));
		 condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 orderList = delegator.findList("OrderAttribute", condition, null, null, null, false);
		 if(UtilValidate.isNotEmpty(orderList)){
			 orderIds = EntityUtil.getFieldListFromEntityList(orderList, "orderId", true);
			 while (list3.hasNext() && (lst=list3.next()) != null) {
				 if(orderIds.contains(lst.orderId)){
					 list2.add(lst);
				 }
			 }
		 }
		 for(lt in list2)
		 {
			 if(!templist.contains(lt.orderId))
			 {
				 list1.add(lt);
				 templist.add(lt.orderId)
			 }
		 }
		 context.listIt=list1;
	 }
 }
 
 if(parameters.supplierId){
	 suppCondList = [];
	 suppCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	 suppCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.supplierId));
	 orderRoleList = delegator.findList("OrderRole", EntityCondition.makeCondition(suppCondList, EntityOperator.AND), null, null, null, false);
	 if(UtilValidate.isNotEmpty(orderRoleList)){
		 supplierFilteredOrderIds = EntityUtil.getFieldListFromEntityList(orderRoleList, "orderId", true);
	 }
 }
  
 // To filter based on Branch
 if(parameters.partyIdFrom){
	 branchCondList = [];
	 if(UtilValidate.isNotEmpty(supplierFilteredOrderIds)){
		 branchCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, supplierFilteredOrderIds));
	 }
	 branchCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
	 branchCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyIdFrom));
	 orderRoleList = delegator.findList("OrderRole", EntityCondition.makeCondition(branchCondList, EntityOperator.AND), null, null, null, false);
	 if(UtilValidate.isNotEmpty(orderRoleList)){
		 supplierFilteredOrderIds = EntityUtil.getFieldListFromEntityList(orderRoleList, "orderId", true);
	 }
 }
if(UtilValidate.isEmpty(parameters.productId) && UtilValidate.isEmpty(parameters.poRefNo)){
	
	
	if(UtilValidate.isNotEmpty(result.listIt)){
		list=result.listIt;
		resultList = [];
		GenericValue poEntry = null;
		while ((poEntry=list.next()) != null) {
			
			if(UtilValidate.isNotEmpty(supplierFilteredOrderIds)){
				if(!supplierFilteredOrderIds.contains(poEntry.orderId)){
					continue;
				}
			}
			
//			if(validOrderIds.contains(poEntry.orderId)){
				if(parameters.findPoFlag=="Y"){
					if(parameters.orderTypeId=="PURCHASE_ORDER"){    // For branch sales purchase order
						if((poEntry.roleTypeId).equals("SUPPLIER_AGENT")){
							resultList.add(poEntry);
						}
						/*if((poEntry.roleTypeId).equals("BILL_TO_CUSTOMER")){
							resultList.add(poEntry);
						}*/
					}else{
						if((poEntry.roleTypeId).equals("BILL_TO_CUSTOMER")){
							resultList.add(poEntry);
						}
					}
				}else{
					if((poEntry.roleTypeId).equals("BILL_TO_CUSTOMER")){
						resultList.add(poEntry);
					}
					
				}
//			}
		
		}
		sortedOrderMap =  [:]as TreeMap;
		for (eachList in resultList) {
			if(eachList.orderNo !=null){
				eachList.orderNo=eachList.orderNo
			}
			sortedOrderMap.put(eachList.orderId, eachList);
		}
		Collection allValues = sortedOrderMap.values();
		List basedList = [];
		basedList.addAll(allValues);
		context.listIt = basedList.reverse();
		//context.listIt=resultList;
	}
}
else if(UtilValidate.isEmpty(parameters.poRefNo)) {
productId = parameters.productId;
if(UtilValidate.isNotEmpty(result.listIt)){
	list1=result.listIt;
	
	poListNew=[];
	list=[];
	ordList=[];
	while (list1.hasNext() && (lst=list1.next()) != null) {
	
		if(!(ordList.contains(lst.orderId))){
			list.add(lst);
			ordList.add(lst.orderId);
		}
	}
	for (poEntry in list) {
		if(UtilValidate.isNotEmpty(supplierFilteredOrderIds)){
			if(!supplierFilteredOrderIds.contains(poEntry.orderId)){
				continue;
			}
		}
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poEntry.orderId));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderList = delegator.findList("OrderItem", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(orderList)){
			poListNew.add(poEntry);
			}
		}
	//list.close();
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








