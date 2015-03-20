import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

fromDate=parameters.fromDateMr;
thruDate=parameters.thruDateMr;
productIds=[];
if(UtilValidate.isNotEmpty(parameters.productId)){	
if(UtilValidate.isEmpty(parameters.issueToFacilityId)){		
productIds.add(parameters.productId);
//context.productId=parameters.productId;
	}
 }
facilityId=parameters.issueToFacilityId;

fromDateTime = null;
thruDateTime = null;

if(UtilValidate.isNotEmpty(facilityId)){
context.facilityId=facilityId;

conditionList =[];
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
if(UtilValidate.isNotEmpty(parameters.productId)){
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
   }
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
ProductFacilityIds = delegator.findList("ProductFacility", condition, null,null, null, false);
productIds=EntityUtil.getFieldListFromEntityList(ProductFacilityIds, "productId", true);
}

if(UtilValidate.isEmpty(facilityId)){
prodDetails = delegator.findOne("Product", [productId : parameters.productId], false);
if(UtilValidate.isNotEmpty(prodDetails)){
   materialName = prodDetails.description;
   internalName = prodDetails.internalName;
   context.put("internalName",internalName);
   
  context.put("materialName",materialName);
  }
 }
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
	context.fromDateTime=fromDateTime;
	context.thruDateTime=thruDateTime;
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}


if(UtilValidate.isNotEmpty(parameters.Unions)){	
	partyRoleData = delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"Unions")  , null, null, null, false );
	partyRoleIds=EntityUtil.getFieldListFromEntityList(partyRoleData, "partyId", true);
	if(UtilValidate.isNotEmpty(partyRoleIds)){	
	OrderRoleData = delegator.findList("OrderRole",EntityCondition.makeCondition("partyId", EntityOperator.IN ,partyRoleIds)  , null, null, null, false );		
	OrderRoleIds=EntityUtil.getFieldListFromEntityList(OrderRoleData, "orderId", true);		
	}		
 }
	
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
shipmentMap=[:];mrrList=[];
if(UtilValidate.isNotEmpty(productIds)){	
condList =[];
condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
if(UtilValidate.isNotEmpty(parameters.Unions)){
	if(UtilValidate.isNotEmpty(partyRoleIds)){
		if(UtilValidate.isNotEmpty(OrderRoleIds)){
condList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, OrderRoleIds));
	} 
   }
 }	
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
shipmentReceiptList = delegator.findList("ShipmentReceipt", cond, null,null, null, false);


shipmentMap["totalInvoiceAmt"]=BigDecimal.ZERO;
shipmentMap["totalPaidAmt"]=BigDecimal.ZERO;

if(UtilValidate.isNotEmpty(shipmentReceiptList)){
shipmentReceiptList.each{shipmentData->	
   shipmentDetailMap=[:];
   shipmentDetailMap["receiptId"]=shipmentData.receiptId;
   shipmentDetailMap["datetimeReceived"]=shipmentData.datetimeReceived;
   shipmentDetailMap["shipmentId"]=shipmentData.shipmentId;
   shipmentDetailMap["inventoryItemId"]=shipmentData.inventoryItemId;

   coList =[];
coList.add(EntityCondition.makeCondition("receiptId", EntityOperator.EQUALS, shipmentData.receiptId));
coList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_QUALITYCHECK"));
EntityCondition con = EntityCondition.makeCondition(coList,EntityOperator.AND);
shipmentQCdate= delegator.findList("ShipmentReceiptStatus", con, null,null, null, false);   
shipmentQCdate=EntityUtil.getFirst(shipmentQCdate);
if(UtilValidate.isNotEmpty(shipmentQCdate)){
	if(UtilValidate.isNotEmpty(shipmentQCdate.statusDatetime)){
		statusDatetime=shipmentQCdate.statusDatetime;
		shipmentDetailMap.put("statusDatetime",statusDatetime);
}

}
 deptRole = delegator.findList("ShipmentReceiptRole",EntityCondition.makeCondition("receiptId", EntityOperator.EQUALS , shipmentData.receiptId)  , null, null, null, false );
    deptRole=EntityUtil.getFirst(deptRole);  
	
    if(UtilValidate.isNotEmpty(deptRole)){
	partyIdOfDept=deptRole.partyId;

   if(UtilValidate.isNotEmpty(partyIdOfDept)){
  deptGroupName= delegator.findList("PartyRelationshipAndDetail",EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyIdOfDept)  , null, null, null, false );
  deptGroupName=EntityUtil.getFirst(deptGroupName);
  if(UtilValidate.isNotEmpty(deptGroupName)){
  if(UtilValidate.isNotEmpty(deptGroupName.groupName)){
	  
   deptName=deptGroupName.groupName;  
   shipmentDetailMap.put("deptName",deptName);
	     }
       }
     }
   }
	   //invoiceno,date
   invoiceData = delegator.findOne("Shipment",["shipmentId":shipmentData.shipmentId],false);   
   if(invoiceData){
	   invoiceId=invoiceData.get("supplierInvoiceId");
	   invoiceDate=invoiceData.get("supplierInvoiceDate");
 
   shipmentDetailMap["invoiceId"]=invoiceId;
   shipmentDetailMap["invoiceDate"]=invoiceDate;
   }
      cList =[];
   cList.add(EntityCondition.makeCondition("statusId",  EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITOFF")));
   cList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentData.shipmentId));
    con = EntityCondition.makeCondition(cList,EntityOperator.AND);
   invoiceDetails = delegator.findList("Invoice", con, null,null, null, false);
   invoiceDetails=EntityUtil.getFirst(invoiceDetails);
   
   if(UtilValidate.isNotEmpty(invoiceDetails)){
	   
 //  shipmentDetailMap["invoiceId"]=invoiceDetails.invoiceId;
 //  shipmentDetailMap["invoiceDate"]=invoiceDetails.invoiceDate;
  // shipmentDetailMap["dueDate"]=invoiceDetails.dueDate;
   
   invoiceId=invoiceDetails.invoiceId;
   invoiceAmount= InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
   if(UtilValidate.isNotEmpty(invoiceAmount)){	   
	   shipmentDetailMap.put("invoiceAmount",invoiceAmount);
	   shipmentMap["totalInvoiceAmt"]+=invoiceAmount;
	   }
   
   invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : invoiceId]);    
   if(UtilValidate.isNotEmpty(invoice)){
    invoiceToApply = InvoiceWorker.getInvoiceNotApplied(invoice);
	if(UtilValidate.isNotEmpty(invoiceToApply)){
		shipmentDetailMap.put("invoiceToApply",invoiceToApply);
		}
   }
   if(UtilValidate.isNotEmpty(invoiceAmount)){
	   if(UtilValidate.isNotEmpty(invoiceToApply)){
		   paidAmount=invoiceAmount-invoiceToApply;
		   shipmentDetailMap.put("paidAmount",paidAmount);
		   shipmentMap["totalPaidAmt"]+=paidAmount;
		   
	   }
   }   
     }
   if(UtilValidate.isEmpty(invoiceDetails)){
	   inventoryItemDetails = delegator.findOne("InventoryItem",["inventoryItemId":shipmentData.inventoryItemId],false);
	   
	     if(inventoryItemDetails){
		   quantityAccepted=shipmentData.quantityAccepted;
		   unitCost=inventoryItemDetails.get("unitCost");
		   invoiceAmount=quantityAccepted*unitCost;		   
		   shipmentDetailMap.put("invoiceAmount",invoiceAmount);		   
		  shipmentMap["totalInvoiceAmt"]+=invoiceAmount;

//	   invoiceToApply=0;paidAmount=0;totalPaidAmt=0;
//	   shipmentDetailMap.put("invoiceToApply",invoiceToApply);
//	   shipmentDetailMap.put("paidAmount",paidAmount);
//	   shipmentDetailMap.put("totalPaidAmt",totalPaidAmt);
	   
	   }
	   
   }
   
   //get PartyId from Role for vendor and dept
   vendorDeptDetails = delegator.findList("OrderRole",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , shipmentData.orderId)  , null, null, null, false );
   
   vendorDetails = EntityUtil.filterByCondition(vendorDeptDetails, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
   vendorDetails=EntityUtil.getFirst(vendorDetails);
   if(UtilValidate.isNotEmpty(vendorDetails)){
    partyId=vendorDetails.partyId;
    shipmentDetailMap.put("partyId",partyId);
    partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	shipmentDetailMap.put("partyName",partyName);
     }
  if((invoiceAmount) != 0) {
   mrrList.addAll(shipmentDetailMap);
   } 
 
   }
  }
}
context.shipmentMap=shipmentMap;
context.mrrList=mrrList;
