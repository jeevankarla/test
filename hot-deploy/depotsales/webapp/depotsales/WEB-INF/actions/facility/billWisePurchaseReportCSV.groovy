import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.party.PartyHelper;

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
Timestamp fromDate;
Timestamp thruDate;
 
DateList=[];
DateMap = [:];
partyfromDate=parameters.billReportfromDate;
partythruDate=parameters.billReportthruDate;
branchId = parameters.branchId;
purposeType = parameters.purposeType;
//productCategory=parameters.productCategory;
//partyId=parameters.partyId;

DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);
DateList.add(DateMap);
context.DateList=DateList;
branchName = "";
if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	branchName = branch.get("groupName");
	DateMap.put("branchName", branchName);
}
branchList = [];
condListb = [];
if(branchId){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
 
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
 
	if(!branchList)
		branchList.add(branchId);
}
//Debug.log("branchList======@@@@====="+branchList);
/*branchBasedWeaversList = [];
condListb = [];
if(branchId){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
}*/
 
/*productIds = [];
productCategoryIds = [];
condListCat = [];
if(!partyId){
	if(productCategory != "OTHER" && productCategory != "ALL"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}else if(productCategory == "OTHER"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","COTTON"]));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}else{
	condListCat.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "NATURAL_FIBERS"));
	condition1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	ProductCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.IN,productCategoryIds),UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}
	
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
}*/
if(branchId){
	branchContext=[:];
	branchContext.put("branchId",branchId);
	BOAddress="";
	BOEmail="";
	try{
		resultCtx = dispatcher.runSync("getBoHeader", branchContext);
		if(ServiceUtil.isError(resultCtx)){
			return ServiceUtil.returnError("Problem in fetching financial year ");
		}
		if(resultCtx.get("boHeaderMap")){
			boHeaderMap=resultCtx.get("boHeaderMap");
		 
			if(boHeaderMap.get("header0")){
				BOAddress=boHeaderMap.get("header0");
			}
			if(boHeaderMap.get("header1")){
				BOEmail=boHeaderMap.get("header1");
			}
		}
	}catch(GenericServiceException e){
		return ServiceUtil.returnError(e.getMessage());
	}
	context.BOAddress=BOAddress;
	context.BOEmail=BOEmail;
}

daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.billReportfromDate)){
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.billReportfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
	} catch (ParseException e) {
	}
}
if(UtilValidate.isNotEmpty(parameters.billReportthruDate)){
   try {
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.billReportthruDate).getTime());
	   dayend = UtilDateTime.getDayEnd(thruDate);
   } catch (ParseException e) {
   }
}
context.daystart = daystart;
context.dayend = dayend;

fromDateForCSV = null;
thruDateForCSV = null;
fromDateForCSV=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForCSV=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");

context.fromDateForCSV=fromDateForCSV;
context.thruDateForCSV=thruDateForCSV;

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);

condList = [];
if(UtilValidate.isNotEmpty(daystart)){
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}
condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
//if(productIds)
//condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
if(branchList)
condList.add(EntityCondition.makeCondition("costCenterId", EntityOperator.IN,branchList));
/*if(partyId){
	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
}else if(!partyId && branchBasedWeaversList){
	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
}*/
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
if(purposeType){
	condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS,purposeType));
}
else{
	condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN, ["YARN_SALE","DEPOT_YARN_SALE"]));
}

//cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
//fieldsToSelect = ["invoiceId"] as Set;
invoice = delegator.findList("Invoice", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
//invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);

finalList=[];
/*stylesMap=[:];
stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
stylesMap.put("mainHeader2", "BILL WISE PURCHASE REPORT");
if(UtilValidate.isNotEmpty(fromDateForCSV) && UtilValidate.isNotEmpty(thruDateForCSV)){
	stylesMap.put("mainHeader3", "From "+ fromDateForCSV +" to "+thruDateForCSV);
}
stylesMap.put("mainHeaderFontName","Arial");
stylesMap.put("mainHeadercellHeight",300);
stylesMap.put("mainHeaderFontSize",10);
stylesMap.put("mainHeadingCell",4);
stylesMap.put("mainHeaderBold",true);
stylesMap.put("columnHeaderBgColor",false);
stylesMap.put("columnHeaderFontName","Arial");
stylesMap.put("columnHeaderFontSize",10);
stylesMap.put("autoSizeCell",true);
stylesMap.put("columnHeaderCellHeight",300);
request.setAttribute("stylesMap", stylesMap);
request.setAttribute("enableStyles", true);
headingMap=[:];
headingMap.put("invoiceId", "Invoice Id");
headingMap.put("invoiceSeqId", "Invoice Sequence Id");
headingMap.put("invoiceDate", "Invoice Date");
headingMap.put("shipmentId", "Shipment Id");
headingMap.put("partyIdFrom", "From Party");
headingMap.put("partyIdTo", "To Party");
headingMap.put("quantity", "Quantity");
headingMap.put("purTax", "Tax");
headingMap.put("adj", "Adjustments");
headingMap.put("totalItemValue", "Total Item Value");
 
finalList.add(stylesMap);
finalList.add(headingMap);*/

headingMap=[:];
headingMap.put("invoiceId", " ");
headingMap.put("invoiceSeqId", " ");
headingMap.put("invoiceDate", " ");
headingMap.put("shipmentId", " ");
headingMap.put("supplierInvoiceId", " ");
headingMap.put("supplierInvoiceDate", " ");
headingMap.put("partyIdFrom", "___BILL WISE PURCHASE REPORT___");
headingMap.put("partyIdTo", " ");
headingMap.put("quantity", " ");
headingMap.put("purTax", " ");
headingMap.put("adj", " ");
headingMap.put("totalItemValue", " ");
headingMap.put("estimatedShipCost", " ");

finalList.add(headingMap);

headingMap1=[:];
headingMap1.put("invoiceId", " ");
headingMap1.put("invoiceSeqId", " ");
headingMap1.put("invoiceDate", " ");
headingMap1.put("shipmentId", " ");
headingMap.put("supplierInvoiceId", " ");
headingMap.put("supplierInvoiceDate", " ");
headingMap1.put("partyIdFrom", "From "+ fromDateForCSV +" to "+thruDateForCSV);
headingMap1.put("partyIdTo", " ");
headingMap1.put("quantity", " ");
headingMap1.put("purTax", " ");
headingMap1.put("adj", " ");
headingMap1.put("totalItemValue", " ");
headingMap1.put("estimatedShipCost", " ");
finalList.add(headingMap1);

headingMap2=[:];
headingMap2.put("invoiceId", " ");
headingMap2.put("invoiceSeqId", " ");
headingMap2.put("invoiceDate", " ");
headingMap2.put("shipmentId", " ");
headingMap2.put("partyIdFrom", " ");
headingMap2.put("partyIdTo", " ");
headingMap2.put("quantity", " ");
headingMap2.put("purTax", " ");
headingMap2.put("adj", " ");
headingMap2.put("totalItemValue", " ");
headingMap2.put("estimatedShipCost", " ");
finalList.add(headingMap2);
 
headingMap3=[:];
headingMap3.put("invoiceId", "Invoice Id");
headingMap3.put("invoiceSeqId", "Invoice Sequence Id");
headingMap3.put("invoiceDate", "Invoice Date");
headingMap3.put("shipmentId", "Shipment Id");
headingMap3.put("supplierInvoiceId", "supplier Invoice Id");
headingMap3.put("supplierInvoiceDate", "supplier Invoice Date");
headingMap3.put("partyIdFrom", "From Party");
headingMap3.put("partyIdTo", "To Party");
headingMap3.put("quantity", "Quantity");
headingMap3.put("purTax", "Tax");
headingMap3.put("adj", "Adjustments");
headingMap3.put("totalItemValue", "Total Item Value");
headingMap3.put("estimatedShipCost", "Freight Chages");
finalList.add(headingMap3);

if(invoice){
	for(eachInvoice in invoice){
		conditionList=[];
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,eachInvoice.invoiceId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INV_RAWPROD_ITEM"));
		invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false );
		totalItemValue=0;
		if(invoiceItems){
			for(int j=0;j < invoiceItems.size();j++){
				purTax = 0;
				adjValue=0;
				invoiceItemValue=0;
				shipmentId="";
				eachInvoiceitem = invoiceItems.get(j);
				invoiceDetailMap = [:];
				invoiceDetailMap.put("invoiceId", eachInvoice.invoiceId);
				invoiceDetailMap.put("invoiceDate", UtilDateTime.toDateString(eachInvoice.invoiceDate, "dd/MM/yyyy"));
				invoiceItemValue=eachInvoiceitem.itemValue;
				billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoice.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
				if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
					invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
					invoiceSequence = invoiceSeqDetails.invoiceSequence;
					invoiceDetailMap.put("invoiceSeqId", invoiceSequence);
				}
				shipmentId=eachInvoice.shipmentId;
				
				ShipmentDetails = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), true);
				
				estimatedShipCost = ShipmentDetails.get("estimatedShipCost");
				
				supplierInvoiceId = ShipmentDetails.get("supplierInvoiceId");
				supplierInvoiceDate = ShipmentDetails.get("supplierInvoiceDate");
				
				invoiceDetailMap.put("supplierInvoiceId",supplierInvoiceId);
				invoiceDetailMap.put("supplierInvoiceDate", UtilDateTime.toDateString(supplierInvoiceDate, "dd/MM/yyyy"));
				invoiceDetailMap.put("shipmentId",shipmentId);
				invoiceDetailMap.put("estimatedShipCost",estimatedShipCost);
				invoiceDetailMap.put("quantity", eachInvoiceitem.quantity);
				invoiceDetails = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", eachInvoiceitem.invoiceId), true);
				if(invoiceDetails){
					partyIdFrom=invoiceDetails.partyIdFrom;
					partyIdTo=invoiceDetails.costCenterId;
				}
				partyIdFromName="";
				partyIdToName="";
				if(partyIdFrom){
					partyIdFromName = PartyHelper.getPartyName(delegator, partyIdFrom, false);
				}
				if(partyIdTo){
					partyIdToName = PartyHelper.getPartyName(delegator, partyIdTo, false);
				}
				invoiceDetailMap.put("partyIdFrom", partyIdFromName);
				invoiceDetailMap.put("partyIdTo", partyIdToName);
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceitem.invoiceId));
				conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceitem.invoiceItemSeqId));
				conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,UtilMisc.toList("VAT_PUR","CST_PUR","CST_SURCHARGE","VAT_SURCHARGE")));
				cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				invoiceVatCstListPO = delegator.findList("InvoiceItem", cond, null, null, null, false);
				
			
				if(invoiceVatCstListPO){
					for (eachTax in invoiceVatCstListPO) {		
						 purTax = purTax+eachTax.itemValue;		
					}
				}
				invoiceDetailMap.put("purTax", purTax);
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceitem.invoiceId));
				conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceitem.invoiceItemSeqId));
				conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, ["INV_RAWPROD_ITEM", "CST_SURCHARGE", "VAT_PUR", "CST_PUR", "CST_SALE", "VAT_SALE", "CESS_SALE", "CESS_PUR", "VAT_SURHARGE", "TEN_PER_CHARGES", "TEN_PER_DISCOUNT", "ENTRY_TAX"]));
				cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				invoiceAdjListPO = delegator.findList("InvoiceItem", cond, null, null, null, false);
				if(invoiceAdjListPO){
					for (eachAdj in invoiceAdjListPO) {
						 adjValue = adjValue+eachAdj.itemValue;
					}
				}
				invoiceDetailMap.put("adj", adjValue);
				totalItemValue=invoiceItemValue+purTax+adjValue;
				invoiceDetailMap.put("totalItemValue", totalItemValue);
				finalList.add(invoiceDetailMap);
			}
		
		
		}
	}	
}
context.finalList = finalList;