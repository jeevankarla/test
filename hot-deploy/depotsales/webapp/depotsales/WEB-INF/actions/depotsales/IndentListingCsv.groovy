import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;

fromDate = parameters.IndentRegisterFromDate;
thruDate = parameters.IndentRegisterThruDate;

entryFromDate = parameters.IndentRegisterEntryFromDate;
entryThruDate = parameters.IndentRegisterEntryThruDate;

salesChannelEnumId = parameters.salesChannel;

//////Debug.log("entryFromDate==========="+entryFromDate);
//////Debug.log("entryThruDate==========="+entryThruDate);

context.fromDate=fromDate;
context.thruDate=thruDate;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(fromDate)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
	if (UtilValidate.isNotEmpty(entryFromDate)) {
		entryFromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(entryFromDate).getTime()));
		entryThruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(entryThruDate).getTime()));
	}
} catch (ParseException e) {
	//////Debug.logError(e, "Cannot parse date string: " + e, "");
context.errorMessage = "Cannot parse date string: " + e;
	return;
}

dayStart = null;
dayEnd = null;

entryDayStart = null;
entryDayEnd = null;

if (UtilValidate.isNotEmpty(fromDate)) {
dayStart = UtilDateTime.getDayStart(fromDate);
dayEnd = UtilDateTime.getDayEnd(thruDate);

}
if (UtilValidate.isNotEmpty(entryFromDate)) {
entryDayStart = UtilDateTime.getDayStart(entryFromDate);
entryDayEnd = UtilDateTime.getDayEnd(entryThruDate);

}

//////Debug.log("entryDayStart==========="+entryDayStart);
//////Debug.log("entryDayEnd==========="+entryDayEnd);

BranchList=[];
	branchMap = [:];
	branchName="";
	 branchId = parameters.branchId;
	 if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	 }
	branchMap.put("branchName", branchName);
	BranchList.add(branchMap);
	context.BranchList=BranchList;
	dctx = dispatcher.getDispatchContext();

	salesChannel = parameters.salesChannelEnumId;
	searchOrderId = parameters.orderId;
	facilityOrderId = parameters.orderId;
	facilityDeliveryDate = parameters.estimatedDeliveryDate;
	facilityDeliveryThruDate = parameters.estimatedDeliveryThruDate;
	productId = parameters.productId;
	facilityStatusId = parameters.statusId;
	facilityPartyId = parameters.partyId;
	screenFlag = parameters.screenFlag;
	tallyRefNO = parameters.tallyRefNO;
	
	facilityDateStart = null;
	facilityDateEnd = null;
	if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
		def sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			transDate = new java.sql.Timestamp(sdf1.parse(facilityDeliveryDate+" 00:00:00").getTime());
		} catch (ParseException e) {
			//////Debug.logError(e, "Cannot parse date string: " + facilityDeliveryDate, "");
		}
		facilityDateStart = UtilDateTime.getDayStart(transDate);
		facilityDateEnd = UtilDateTime.getDayEnd(transDate);
	}
	transThruDate = null;
	if(UtilValidate.isNotEmpty(facilityDeliveryThruDate)){
		def sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			transThruDate = new java.sql.Timestamp(sdf2.parse(facilityDeliveryThruDate+" 00:00:00").getTime());
		} catch (ParseException e) {
			//////Debug.logError(e, "Cannot parse date string: " + facilityDeliveryThruDate, "");
		}
		facilityDateEnd = UtilDateTime.getDayEnd(transThruDate);
	}
	
	JSONArray orderList=new JSONArray();
	 List condList = [];
	
	inputFields = [:];
	inputFields.put("noConditionFind", "Y");
	inputFields.put("hideSearch","Y");
	
	branchList=[];
	condListb = [];
	if(UtilValidate.isNotEmpty(branchId)){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	}
	
	if(!branchList)
	branchList.add(branchId);
	
	orderRoles = [];
	branchBasedOrderIds=[];
	if(UtilValidate.isNotEmpty(branchList)){
		custCondList = [];
		if((UtilValidate.isNotEmpty(branchList))){
			custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
		}
		custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
		custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		orderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
		branchBasedOrderIds = EntityUtil.getFieldListFromEntityList(orderRoles, "orderId", true);
	}
	
	
	custOrderRoles =[];
	custBasededOrderIds=[];
	if(UtilValidate.isNotEmpty(facilityPartyId)){
		custCondList = [];
		custCondList.add(EntityCondition.makeCondition("partyId",  EntityOperator.EQUALS, facilityPartyId));
		custCondList.add(EntityCondition.makeCondition("orderId",  EntityOperator.IN, branchBasedOrderIds));
		custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
		custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		custOrderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
		branchBasedOrderIds = EntityUtil.getFieldListFromEntityList(custOrderRoles, "orderId", true);
	}
	
	condList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.IN, branchBasedOrderIds));
	
	if(dayStart){
	condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
	condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	}
	
	if(UtilValidate.isNotEmpty(entryDayStart)){
		condList.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, entryDayStart));
		condList.add(EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, entryDayEnd));
		
	}
	
	if(UtilValidate.isNotEmpty(salesChannelEnumId)){
		condList.add(EntityCondition.makeCondition("salesChannelEnumId" ,EntityOperator.EQUALS, salesChannelEnumId));
	}
	
	condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
	condList.add(EntityCondition.makeCondition("purposeTypeId" ,EntityOperator.EQUALS, "BRANCH_SALES"));
	condList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null)); // Review

	if(parameters.indentDateSort)
	dateSort = parameters.indentDateSort;
	else
	dateSort = "-orderDate";
	
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	List<String> payOrderBy = UtilMisc.toList(dateSort,"-orderId");
	
	resultList = [];
	forIndentsCount = [];
	int totalIndents = 0
	orderHeader = delegator.findList("OrderHeader", cond, null, payOrderBy, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(orderHeader, "orderId", true);
    
	if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
	headerData2=[:];
	headerData2.put("orderDate", "_");
	headerData2.put("orderId", "_");
	headerData2.put("orderNo", "_");
	headerData2.put("Qty", "_");
	headerData2.put("indentPrice", "_");
	headerData2.put("IndentValue", "_");
	headerData2.put("poQty", "_");
	headerData2.put("salInv", "_");
	headerData2.put("salDate", "_");
	headerData2.put("salVal", "_");
	headerData2.put("transporter", "_");
	headerData2.put("milInv", "_");
	headerData2.put("value", "_");
	headerData2.put("paymentReceipt", "_");
	headerData2.put("amount", "_");
	headerData2.put("weaverName", "_");
	headerData2.put("poNo", "____I N D E N T ");
	headerData2.put("poDate", "_");
	headerData2.put("supplierName", "R E G I S T E R");
	orderList.add(headerData2);
	
	headerData=[:];
	headerData.put("orderDate", "Indent Date");
	headerData.put("orderId", "Cust Order");
	headerData.put("orderNo", "IndentSeqId");
	headerData.put("Qty", "Indent Qty(Kgs)");
	headerData.put("indentPrice", "Indent UnitPrice");
	headerData.put("IndentValue", "Indent Value");
	headerData.put("poQty", "PO Qty");
	headerData.put("salInv", "Sal Invoice");
	headerData.put("salDate", "Sal Date");
	headerData.put("salVal", "Sal Value");
	headerData.put("transporter", "Transporter");
	headerData.put("milInv", "Mil Invoice");
	headerData.put("value", "Value");
	headerData.put("paymentReceipt", "Payment Receipt");
	headerData.put("amount", "Amount");
	headerData.put("weaverName", "Weaver Name");
	headerData.put("poSquenceNo", "PoSequenceId");
	headerData.put("poDate", "PO Date");
	headerData.put("supplierName", "Supplier Name");
	orderList.add(headerData);
	}
	
	
	orderHeader.each{ eachHeader ->
		orderId = eachHeader.orderId;
		JSONObject tempData = new JSONObject();
		orderParty = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	
		custOrderRoles.clear();
		partyId = "";
		if(UtilValidate.isEmpty(partyId)){
			custCondList = [];
			custCondList.add(EntityCondition.makeCondition("orderId",  EntityOperator.EQUALS, orderId));
			custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
			custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
			custOrderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
		}
			partyId = EntityUtil.getFirst(custOrderRoles).get("partyId");
		
		billFromOrderParty = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		billFromVendorPartyId = "";
		if(billFromOrderParty){
			billFromVendorPartyId = billFromOrderParty.get(0).get("partyId");
		}
		
		partyName = PartyHelper.getPartyName(delegator, partyId, false);
		billFromVendor = PartyHelper.getPartyName(delegator, billFromVendorPartyId, false);
		
		tempData.put("partyId", partyId);
		tempData.put("weaverName", partyName);
		tempData.put("partyName", partyName);
		
		if(eachHeader.tallyRefNo)
		 tempData.put("tallyRefNo", eachHeader.tallyRefNo);
		else
		 tempData.put("tallyRefNo", "NA");
		
		orderNo ="NA";
		orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , eachHeader.orderId)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(orderHeaderSequences)){
			orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
			orderNo = orderSeqDetails.orderNo;
		}
		exprCondList=[];
		exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
		exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
		OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
		
		POorder="NA";
		isgeneratedPO="N";
		if(OrderAss){
			POorder=OrderAss.get("orderId");
			isgeneratedPO = "Y";
		}
		poSquenceNo="NA";
		poOrderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , POorder)  , null, null, null, false );
		if(UtilValidate.isNotEmpty(poOrderHeaderSequences)){
			poOrderSeqDetails = EntityUtil.getFirst(poOrderHeaderSequences);
			poSquenceNo = poOrderSeqDetails.orderNo;
		}
		tempData.put("POorder", POorder);
		tempData.put("poSquenceNo", poSquenceNo);
		tempData.put("isgeneratedPO", isgeneratedPO);
	
		exprList=[];
		exprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
		EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		supplierPartyId="";
		productStoreId="";
		supplierDetails = EntityUtil.getFirst(delegator.findList("OrderRole", discontinuationDateCondition, null,null,null, false));
		if(supplierDetails){
			supplierPartyId=supplierDetails.get("partyId");
		}
		supplierPartyName="";
		if(supplierPartyId){
			supplierPartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
		}
		productStoreId=eachHeader.productStoreId;
		tempData.put("supplierPartyId", supplierPartyId);
		tempData.put("totalIndents", totalIndents);
		tempData.put("storeName", productStoreId);
		tempData.put("supplierName", supplierPartyName);
		tempData.put("orderNo", orderNo);
		tempData.put("orderId", eachHeader.orderId);
		tempData.put("orderDate", UtilDateTime.toDateString(eachHeader.estimatedDeliveryDate, "MM/dd/yyyy"));
		tempData.put("statusId", eachHeader.statusId);
		if(UtilValidate.isNotEmpty(eachHeader.getBigDecimal("grandTotal"))){
			tempData.put("orderTotal", eachHeader.getBigDecimal("grandTotal"));
		}
		ordQty=0;
		IndentValue=0;
		poId="";
		salValue=0;
		poQty=0;
		custCondList.clear();
		custCondList.add(EntityCondition.makeCondition("toOrderId",  EntityOperator.EQUALS, orderId));
		custCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
		custCond1 = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		orderAssocList = delegator.findList("OrderAssoc", custCond1, null, null, null, false);
		if(UtilValidate.isNotEmpty(orderAssocList)){
			orderAssoc = EntityUtil.getFirst(orderAssocList);
			poId=orderAssoc.get("orderId");
			custCondList.clear();
			custCondList.add(EntityCondition.makeCondition("orderId",  EntityOperator.IN, UtilMisc.toList(orderId,poId)));
			custCond2 = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
			orderItemList = delegator.findList("OrderItem", custCond2, null, null, null, false);
			indentPrice=0;
			if(UtilValidate.isNotEmpty(orderItemList)){
				orderItemList1 = EntityUtil.filterByCondition(orderItemList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				if(UtilValidate.isNotEmpty(orderItemList1)){
					for(orderItem in orderItemList1){
						ordQty=ordQty+orderItem.quantity;
						indentPrice=indentPrice+orderItem.unitPrice;
					}
					indentPrice=indentPrice/orderItemList1.size()
					IndentValue=IndentValue+((ordQty)*indentPrice)
				}
				orderItemList2 = EntityUtil.filterByCondition(orderItemList, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poId));
				if(UtilValidate.isNotEmpty(orderItemList)){
					for(orderItem in orderItemList2){
						poQty=poQty+orderItem.quantity;
					}
				}
				
				
			}
			tempData.put("poNo", poId);
			tempData.put("poQty",poQty);
			tempData.put("Qty", ordQty);
			tempData.put("indentPrice", indentPrice);
			tempData.put("IndentValue", IndentValue);
			orderHeader = delegator.findOne("OrderHeader",[orderId : poId] , false);
			if(orderHeader){
				tempData.put("poDate", UtilDateTime.toDateString(orderHeader.orderDate, "MM/dd/yyyy"));
			}
			shipments = delegator.findList("Shipment", EntityCondition.makeCondition("primaryOrderId",  EntityOperator.EQUALS, poId), null, null, null, false);
			if(shipments){
				shipment = EntityUtil.getFirst(shipments);
				tempData.put("milInv", shipment.supplierInvoiceId);
				tempData.put("transporter", shipment.carrierName);
			}
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId",  EntityOperator.EQUALS, poId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			cond3 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemBillingList = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond3, UtilMisc.toSet("quantity","invoiceId"), null, null, false);
			if(OrderItemBillingList){
				for(OrderItemBilling in OrderItemBillingList){
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("invoiceId",  EntityOperator.EQUALS, OrderItemBilling.invoiceId));
					cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					invoiceItemList = delegator.findList("InvoiceItem", cond1, null, null, null, false);
					for(invoiceItem in invoiceItemList){
						if(UtilValidate.isNotEmpty(invoiceItem.itemValue)){
							salValue=salValue+invoiceItem.itemValue;
						}
					}
					tempData.put("salVal", salValue);
					tempData.put("salInv", OrderItemBilling.invoiceId);
					Invoice = delegator.findOne("Invoice",[invoiceId : OrderItemBilling.invoiceId] , false);
					tempData.put("salDate", UtilDateTime.toDateString(Invoice.invoiceDate, "MM/dd/yyyy"));
				}
			}
			
		
			tempData.put("value", "-");
			tempData.put("paymentReceipt", "-");
			tempData.put("amount", "-");
			
			
			
			
			
			
		}
		
		productStoreId=eachHeader.productStoreId;
		
		conditonList = [];
		conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, orderId));
		cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
		OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
		double paidAmt = 0;
		
		paymentIdsOfIndentPayment = [];
		if(OrderPaymentPreference){
		orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
		conditonList.clear();
		conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN,orderPreferenceIds));
		conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
		cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
		PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
		paymentIdsOfIndentPayment = EntityUtil.getFieldListFromEntityList(PaymentList,"paymentId", true);
		
		for (eachPayment in PaymentList) {
			paidAmt = paidAmt+eachPayment.get("amount");
		}
		
	  }
		conditonList.clear();
		conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,orderId));
		cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
		OrderItemBillingList = delegator.findList("OrderItemBilling", cond, null, null, null ,false);
		invoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBillingList,"invoiceId", true);
		
		if(invoiceIds){
		conditonList.clear();
		conditonList.add(EntityCondition.makeCondition("invoiceId" ,EntityOperator.IN,invoiceIds));
		cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
		PaymentApplicationList = delegator.findList("PaymentApplication", cond, null, null, null ,false);
		
			for (eachList in PaymentApplicationList) {
				 if(!paymentIdsOfIndentPayment.contains(eachList.paymentId))
					paidAmt = paidAmt+eachList.amountApplied;
			}
		}
		tempData.put("paidAmt", paidAmt);
		grandTOT = eachHeader.getBigDecimal("grandTotal");
		balance = grandTOT-paidAmt;
		tempData.put("balance", balance);
		orderList.add(tempData);
	}
	
	if (UtilValidate.isNotEmpty(resultList)) {
		try {
			resultList.close();
		} catch (Exception e) {
			//////Debug.logWarning(e, module);
		}
	}
	context.orderList=orderList;
