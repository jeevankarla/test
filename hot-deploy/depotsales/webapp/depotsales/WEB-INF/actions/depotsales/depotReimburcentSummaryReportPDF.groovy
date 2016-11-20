import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
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

SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");
dayend = null;
daystart = null;

Timestamp fromDate;
Timestamp thruDate;

partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyId=parameters.partyId;
state=parameters.state;
productCategory=parameters.productCategory;
reportType=parameters.reportType;
context.reportType=reportType;
context.partyfromDate=partyfromDate;
context.partythruDate=partythruDate;

indianStates =[];
statesList=[];
conditionList = [];
errorMessage="";
if("ALL".equals(state)){
	conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
	conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
	statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
	indianStates = EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);
	
    conditionList.clear();
}else{
	conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS,state));
	conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
	statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
	indianStates = EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);
}
	
productIds = [];
productCategoryIds = [];

conditionList.clear();
if(productCategory != "OTHER"){
	conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}else if(productCategory == "OTHER"){
	conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","JUTE_YARN"]));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
condition2 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
ProductCategoryMember = delegator.findList("ProductCategoryMember", condition2,UtilMisc.toSet("productId"), null, null, false);
productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
  
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
  
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
		 } catch (ParseException e) {
			 //////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
		}
   
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
	   
	   dayend = UtilDateTime.getDayEnd(thruDate);
	   
   } catch (ParseException e) {
	   //////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}
context.daystart=daystart
context.dayend=dayend
branchContext=[:];
branchContext.put("branchId","INT15");

BOAddress="";
BOEmail="";
try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
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
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;

finalList = [];
finalCSVList=[];
for(state in indianStates){
	tempCSVMap1=[:];
	tempCSVMap2=[:];
	double totInvoiceAMT = 0;
	double totInvoiceQTY = 0;
	double totShippingCost = 0;
	double totReimbursentAMT = 0; 
	double totDepotCharges = 0;
	
	stateDetails =EntityUtil.filterByCondition(statesList, EntityCondition.makeCondition("geoId", EntityOperator.EQUALS,state));
	stateDetail = EntityUtil.getFirst(stateDetails);
	stateName= stateDetail.geoName;
	tempCSVMap1.put("partyId", stateName);
	tempCSVMap1.put("partyName", "_");
	tempCSVMap1.put("invoiceAMT", "_");
	tempCSVMap1.put("invoiceQTY",  "_");
	tempCSVMap1.put("shippingCost",  "_");
	tempCSVMap1.put("reimbursentAMT",  "_");
	tempCSVMap1.put("depotCharges",  "_");
	finalCSVList.add(tempCSVMap1);
	stateMap=[:];
	totMap=[:];
	stateParties=[];
	conditionList.clear();
	if(UtilValidate.isNotEmpty(state)){
		conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		if(state == "IN-TS"){
			conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, "IN-TNG"));
		}else{
			conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
		}
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condition, null, UtilMisc.toSet("partyId"), null, null);
		stateParties = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	}
	if("DEPOT".equals(reportType) && UtilValidate.isNotEmpty(stateParties)){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
		if(stateParties)
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,stateParties));
		condition3 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		FacilityList = delegator.find("Facility", condition3, null, UtilMisc.toSet("ownerPartyId"), null, null);
		stateParties = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);
	}
	
	conditionList.clear();
	if(UtilValidate.isNotEmpty(daystart)){
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
	}
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
	if(productIds)
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	if(stateParties){
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, stateParties));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));
	condition4 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	fieldsToSelect = ["invoiceId"] as Set;
	invoice = delegator.find("InvoiceAndItem", condition4, null, fieldsToSelect, null, null);
	invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	condition5 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", condition5, null, null, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
	conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.IN, ["MGPS_10Pecent","MGPS"]));
	orderAttr = delegator.findList("OrderAttribute",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(orderAttr, "orderId", true);
	
	OrderItemBillingFilter = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	invoiceIds=EntityUtil.getFieldListFromEntityList(OrderItemBillingFilter, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
	Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	statePartiesListData=[];
	sr=1;
	for (partyId in stateParties) {
		tempMap=[:];
		double invoiceAMT = 0;
		double invoiceQTY = 0;
		double shippingCost = 0;
		double reimbursentAMT = 0;
		double depotCharges = 0;
		partyInvoices =EntityUtil.filterByCondition(Invoice, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
		if(partyInvoices){
			for(eachInvoice in partyInvoices){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
				InvoiceItem = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
				if(InvoiceItem){
					for (eachInvoiceItem in InvoiceItem) {
						invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
						invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
						totInvoiceAMT=totInvoiceAMT+eachInvoiceItem.itemValue;
						totInvoiceQTY=totInvoiceQTY+eachInvoiceItem.quantity;
					}
					double maxAmt = 0;
					if(invoiceQTY && invoiceQTY>0){
						maxAmt = (invoiceAMT*2.5)/100;
					}
					shipmentId = eachInvoice.shipmentId;
					if(shipmentId){
						 shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
						 if(UtilValidate.isNotEmpty(shipmentList) && UtilValidate.isNotEmpty(shipmentList.estimatedShipCost)){
							 shippingCost=shippingCost+shipmentList.estimatedShipCost
							 totShippingCost=totShippingCost+shipmentList.estimatedShipCost;
							 estimatedShipCost=shipmentList.estimatedShipCost;
							 if(estimatedShipCost && maxAmt > estimatedShipCost){
								 reimbursentAMT=reimbursentAMT+estimatedShipCost;
								 totReimbursentAMT=totReimbursentAMT+estimatedShipCost 
							 }else{
								  reimbursentAMT=reimbursentAMT+maxAmt;
								  totReimbursentAMT=totReimbursentAMT+maxAmt
							 }
						 }
					 }
				}
			}
		}
		
		String partyName = PartyHelper.getPartyName(delegator,partyId,false);
		tempMap.put("sr", sr);
		tempMap.put("partyId", "_");
		tempMap.put("partyName", partyName);
		tempMap.put("invoiceAMT", invoiceAMT);
		tempMap.put("invoiceQTY", invoiceQTY);
		tempMap.put("shippingCost", shippingCost);
		tempMap.put("reimbursentAMT", reimbursentAMT);
		if("DEPOT".equals(reportType)){
			depotCharges = (invoiceAMT*2)/100;
			tempMap.put("depotCharges", depotCharges);
			totDepotCharges=totDepotCharges+depotCharges;
		}
		if(invoiceAMT>0 ||shippingCost>0 || reimbursentAMT>0){
			statePartiesListData.add(tempMap);
			finalCSVList.add(tempMap);
			sr=sr+1;
		}
	}
	totMap.put("partyId","TOTAL");
	totMap.put("partyName", "TOTAL");
	totMap.put("invoiceAMT",totInvoiceAMT);
	totMap.put("invoiceQTY",totInvoiceQTY);
	totMap.put("shippingCost",totShippingCost);
	totMap.put("reimbursentAMT",totReimbursentAMT);
	totMap.put("depotCharges",totDepotCharges);
	
	statePartiesListData.add(totMap);
	
	tempCSVMap2.put("partyId","TOTAL");
	tempCSVMap2.put("partyName", "_");
	tempCSVMap2.put("invoiceAMT",totInvoiceAMT);
	tempCSVMap2.put("invoiceQTY",totInvoiceQTY);
	tempCSVMap2.put("shippingCost",totShippingCost);
	tempCSVMap2.put("reimbursentAMT",totReimbursentAMT);
	tempCSVMap2.put("depotCharges",totDepotCharges);
	finalCSVList.add(tempCSVMap2);
	stateMap.put(stateName, statePartiesListData);
	finalList.add(stateMap);
}
context.finalList = finalList;
context.finalCSVList=finalCSVList;











