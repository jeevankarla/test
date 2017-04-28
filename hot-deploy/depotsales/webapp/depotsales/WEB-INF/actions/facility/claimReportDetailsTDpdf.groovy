import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import java.util.Map.Entry;
reportTypeFlag=parameters.reportTypeFlag;
context.reportTypeFlag=reportTypeFlag;
claimFromDate=parameters.claimFromDateTD;
claimThruDate=parameters.claimThruDateTD;
geoId = parameters.geoId;
branchId = parameters.branchId;

rowiseTsPercentageMap=[:];
hydRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
cmbRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
kolRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
kanRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
vjyRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
panRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
gwhRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
varRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
bhuRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2);
rowiseTsPercentageMap= UtilMisc.toMap("INT1",varRoMap,"INT2",panRoMap,"INT3",kolRoMap,"INT4",cmbRoMap,"INT5",hydRoMap,"INT6",kanRoMap,"INT26",bhuRoMap,"INT28",gwhRoMap,"INT47",vjyRoMap);

Debug.log("rowiseTsPercentageMap======"+ rowiseTsPercentageMap);

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
finalList = [];
def sdf = new SimpleDateFormat("yyyy, MMM dd");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(claimFromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(claimThruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
branchIds=[];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.EQUALS,"REGIONAL_OFFICE"));
partyClassification = delegator.findList("PartyClassification",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyId"), null, null, false );
roPartIds = EntityUtil.getFieldListFromEntityList(partyClassification, "partyId", true);

// getting branchs from state 
if(UtilValidate.isNotEmpty(geoId)){
	conditionList.clear();
	if("ALL".equals(geoId)){
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.LIKE,"IN-%"));
	}else{
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.EQUALS,geoId));
	}
	
	partyAndPostalAddress = delegator.findList("PartyAndPostalAddress",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyId"), null, null, false );
	if(UtilValidate.isNotEmpty(partyAndPostalAddress)){
		branchIds= EntityUtil.getFieldListFromEntityList(partyAndPostalAddress,"stateProvinceGeoId", true);
	}
}

//getting branchs from selected RO or Branch 
if(UtilValidate.isNotEmpty(branchId)){
	branchName =  PartyHelper.getPartyName(delegator, branchId, false);
	context.branchName=branchName;
	BOAddress="";
	BOEmail="";
	try{
		resultCtx = dispatcher.runSync("getBoHeader", UtilMisc.toMap("branchId",branchId));
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
	
	if(branchId.equals("HO")){
		roPartIds.each{ eachRo ->
				resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",eachRo));
				if(resultCtx && resultCtx.get("partyList")){
					partyList=resultCtx.get("partyList");
					partyList.each{eachparty ->
					branchIds.add(eachparty.partyIdTo);
					}
				}
				branchIds.add(eachRo);
		}
	}
	else{
		resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",branchId));
		if(resultCtx && resultCtx.get("partyList")){
			partyList=resultCtx.get("partyList");
			branchIds= EntityUtil.getFieldListFromEntityList(partyList,"partyIdTo", true);
		}
		branchIds.add(branchId);
	}
}

silkDepotList=[]
silkNonDepotList=[]

cottonDepotList=[]
cottonNonDepotList=[]

juteDepotList=[]
juteNonDepotList=[]

otherDepotList=[]
otherNonDepotList=[]


// getting Silk,Cotton,Jute  and other product categories
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS,"YARN_SALE"));
conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId",EntityOperator.EQUALS,"SILK"));
productCategorys= delegator.findList("ProductCategory",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
silkProdCats= EntityUtil.getFieldListFromEntityList(productCategorys,"productCategoryId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS,"YARN_SALE"));
conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId",EntityOperator.EQUALS,"COTTON"));
productCategorys= delegator.findList("ProductCategory",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
cottonProdCats= EntityUtil.getFieldListFromEntityList(productCategorys,"productCategoryId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS,"YARN_SALE"));
conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId",EntityOperator.EQUALS,"JUTE_YARN"));
productCategorys= delegator.findList("ProductCategory",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
juteProdCats= EntityUtil.getFieldListFromEntityList(productCategorys,"productCategoryId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS,"NATURAL_FIBERS"));
conditionList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.NOT_IN,UtilMisc.toList("JUTE_YARN","SILK","COTTON")));
productCategorys= delegator.findList("ProductCategory",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
otherProdCats= EntityUtil.getFieldListFromEntityList(productCategorys,"productCategoryId", true);



// getting Silk,Cotton,Jute  and other products
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.IN,silkProdCats));
products= delegator.findList("Product",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
silkProdIds= EntityUtil.getFieldListFromEntityList(products,"productId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.IN,cottonProdCats));
products= delegator.findList("Product",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
cottonProdIds= EntityUtil.getFieldListFromEntityList(products,"productId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.IN,juteProdCats));
products= delegator.findList("Product",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
juteProdIds= EntityUtil.getFieldListFromEntityList(products,"productId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.IN,otherProdCats));
products= delegator.findList("Product",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false );
otherProdIds= EntityUtil.getFieldListFromEntityList(products,"productId", true);




conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE")); 
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );

index=1;
for(invoice in silkinvoicesAndItems)
{
	tempMap=[:]
	BigDecimal actualFrightCharges = BigDecimal.ZERO;
	BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
	BigDecimal depotCharges = BigDecimal.ZERO;
	invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
	shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
	Debug.log("shipment.estimatedShipCost=============="+ shipment.estimatedShipCost)
	if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
		actualFrightCharges=shipment.estimatedShipCost
	}
	product = delegator.findOne("Product",[productId : invoice.productId] , false);
	productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
	roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
	schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
	eligibleFrightCharges=(invoiceAmount.multiply(schemePercentage)).divide(100);
	depotCharges=(invoiceAmount.multiply(2)).divide(100);
	String partyName = PartyHelper.getPartyName(delegator,invoice.partyId,false);
	tempMap.put("sNo", index);
	tempMap.put("partyName", partyName);
	tempMap.put("totInvQty", invoice.quantity);
	tempMap.put("totInvValue", invoiceAmount);
	tempMap.put("actualFrightCharges", actualFrightCharges);
	if(UtilValidate.isNotEmpty(actualFrightCharges)){
		if(actualFrightCharges.compareTo(eligibleFrightCharges)>0){
			tempMap.put("frightCharges", eligibleFrightCharges);
		}else{
			tempMap.put("frightCharges", actualFrightCharges);
		}
	}else{
		tempMap.put("frightCharges", BigDecimal.ZERO);
	}
	
	facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
	if(UtilValidate.isNotEmpty(facility)){
		tempMap.put("depotCharges", depotCharges);
		silkDepotList.add(tempMap);
	}else{
	    tempMap.put("depotCharges", BigDecimal.ZERO);
		silkNonDepotList.add(tempMap);
	}
	++index;
}
index=1;
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );

for(invoice in cottonInvoicesAndItems)
{
	tempMap=[:]
	BigDecimal actualFrightCharges = BigDecimal.ZERO;
	BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
	BigDecimal depotCharges = BigDecimal.ZERO;
	invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
	shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
	Debug.log("shipment.estimatedShipCost=============="+ shipment.estimatedShipCost)
	if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
		actualFrightCharges=shipment.estimatedShipCost
	}
	eligibleFrightCharges=(invoiceAmount.multiply(2)).divide(100);
	depotCharges=(invoiceAmount.multiply(2)).divide(100);
	String partyName = PartyHelper.getPartyName(delegator,invoice.partyId,false);
	tempMap.put("sNo", index);
	tempMap.put("partyName", partyName);
	tempMap.put("totInvQty", invoice.quantity);
	tempMap.put("totInvValue", invoiceAmount);
	tempMap.put("actualFrightCharges", actualFrightCharges);
	if(UtilValidate.isNotEmpty(actualFrightCharges)){
		if(actualFrightCharges.compareTo(eligibleFrightCharges)>0){
			tempMap.put("frightCharges", eligibleFrightCharges);
		}else{
			tempMap.put("frightCharges", actualFrightCharges);
		}
	}else{
		tempMap.put("frightCharges", BigDecimal.ZERO);
	}
	
	facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
	if(UtilValidate.isNotEmpty(facility)){
		tempMap.put("depotCharges", depotCharges);
		cottonDepotList.add(tempMap);
	}else{      
	    tempMap.put("depotCharges", BigDecimal.ZERO);
		cottonNonDepotList.add(tempMap);
	}
	++index;
	
}

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );


for(invoice in juteInvoicesAndItems)
{
	tempMap=[:]
	BigDecimal actualFrightCharges = BigDecimal.ZERO;
	BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
	BigDecimal depotCharges = BigDecimal.ZERO;
	invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
	shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
	Debug.log("shipment.estimatedShipCost=============="+ shipment.estimatedShipCost)
	if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
		actualFrightCharges=shipment.estimatedShipCost
	}
	eligibleFrightCharges=(invoiceAmount.multiply(2)).divide(100);
	depotCharges=(invoiceAmount.multiply(2)).divide(100);
	String partyName = PartyHelper.getPartyName(delegator,invoice.partyId,false);
	tempMap.put("sNo", index);
	tempMap.put("partyName", partyName);
	tempMap.put("totInvQty", invoice.quantity);
	tempMap.put("totInvValue", invoiceAmount);
	tempMap.put("actualFrightCharges", actualFrightCharges);
	if(UtilValidate.isNotEmpty(actualFrightCharges)){
		if(actualFrightCharges.compareTo(eligibleFrightCharges)>0){
			tempMap.put("frightCharges", eligibleFrightCharges);
		}else{
			tempMap.put("frightCharges", actualFrightCharges);
		}
	}else{
		tempMap.put("frightCharges", BigDecimal.ZERO);
	}
	
	facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
	if(UtilValidate.isNotEmpty(facility)){
		tempMap.put("depotCharges", depotCharges);
		juteDepotList.add(tempMap);
	}else{    
	    tempMap.put("depotCharges", BigDecimal.ZERO);
		juteNonDepotList.add(tempMap);
	}
	++index;
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );


for(invoice in otherInvoicesAndItems)
{
	tempMap=[:]
	BigDecimal actualFrightCharges = BigDecimal.ZERO;
	BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
	BigDecimal depotCharges = BigDecimal.ZERO;
	invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
	shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
	Debug.log("shipment.estimatedShipCost=============="+ shipment.estimatedShipCost)
	if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
		actualFrightCharges=shipment.estimatedShipCost
	}
	eligibleFrightCharges=(invoiceAmount.multiply(2)).divide(100);
	depotCharges=(invoiceAmount.multiply(2)).divide(100);
	String partyName = PartyHelper.getPartyName(delegator,invoice.partyId,false);
	tempMap.put("sNo", index);
	tempMap.put("partyName", partyName);
	tempMap.put("totInvQty", invoice.quantity);
	tempMap.put("totInvValue", invoiceAmount);
	tempMap.put("actualFrightCharges", actualFrightCharges);
	if(UtilValidate.isNotEmpty(actualFrightCharges)){
		if(actualFrightCharges.compareTo(eligibleFrightCharges)>0){
			tempMap.put("frightCharges", eligibleFrightCharges);
		}else{
			tempMap.put("frightCharges", actualFrightCharges);
		}
	}else{
		tempMap.put("frightCharges", BigDecimal.ZERO);
	}
	
	facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
	if(UtilValidate.isNotEmpty(facility)){
		tempMap.put("depotCharges", depotCharges);
		otherDepotList.add(tempMap);
	}else{
	    tempMap.put("depotCharges", BigDecimal.ZERO);
		otherNonDepotList.add(tempMap);
	}
	++index;
	
}

Debug.log("silkNonDepotList============"+ silkNonDepotList)
Debug.log("silkDepotList============"+ silkDepotList)
context.silkDepotList=silkDepotList
context.silkNonDepotList=silkNonDepotList
  
context.cottonDepotList=cottonDepotList  
context.cottonNonDepotList=cottonNonDepotList

context.juteDepotList=juteDepotList
context.juteNonDepotList=juteNonDepotList

context.otherDepotList=otherDepotList
context.otherNonDepotList=otherNonDepotList








