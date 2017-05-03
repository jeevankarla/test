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
hydRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
cmbRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
kolRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
kanRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
vjyRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
panRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
gwhRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
varRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
bhuRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
rowiseTsPercentageMap= UtilMisc.toMap("INT1",varRoMap,"INT2",panRoMap,"INT3",kolRoMap,"INT4",cmbRoMap,"INT5",hydRoMap,"INT6",kanRoMap,"INT26",bhuRoMap,"INT28",gwhRoMap,"INT47",vjyRoMap);

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
context.fromDate = claimFromDate;
context.thruDate = claimThruDate;
branchIds=[];
stateGeoIds=[];
partyAndPostalAddress=[];
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
	partyAndPostalAddress = delegator.findList("PartyAndPostalAddress",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyId","stateProvinceGeoId"), null, null, false );
	partyAndPostalAddress1 = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition("partyId",EntityOperator.LIKE,"INT%"));
	if(UtilValidate.isNotEmpty(partyAndPostalAddress1)){
		branchIds= EntityUtil.getFieldListFromEntityList(partyAndPostalAddress1,"partyId", true);
		stateGeoIds= EntityUtil.getFieldListFromEntityList(partyAndPostalAddress1,"stateProvinceGeoId", true);
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
	
	if(branchId=="HO"){
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
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.LIKE,"IN-%"));
	partyAndPostalAddress = delegator.findList("PartyAndPostalAddress",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyId","stateProvinceGeoId"), null, null, false );
	partyAndPostalAddress1 = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition("partyId",EntityOperator.LIKE,"INT%"));
	if(UtilValidate.isNotEmpty(partyAndPostalAddress1)){
		stateGeoIds= EntityUtil.getFieldListFromEntityList(partyAndPostalAddress1,"stateProvinceGeoId", true);
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
conditionList.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS,"YARN_SALE"));
conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId",EntityOperator.NOT_IN,UtilMisc.toList("JUTE_YARN","SILK","COTTON")));
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


silkDepotList =[]
silkNonDepotList =[]
cottonDepotList =[]
cottonNonDepotList =[]
juteDepotList =[]
juteNonDepotList =[]
otherDepotList =[]
otherNonDepotList =[]

if(reportTypeFlag=="BILL_WISE"){
	generateBillWiseReport();
}else if(reportTypeFlag=="PARTY_WISE"){
	generatePartyWiseReport();
}else{

	generateSummaryReport(stateGeoIds);
}
def generateBillWiseReport()
{
	index=1;
	BigDecimal totalInvoiceQtyD = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueD = BigDecimal.ZERO;
	BigDecimal totalActualFrightChargesD = BigDecimal.ZERO;
	BigDecimal totalFrightChargesD = BigDecimal.ZERO;
	BigDecimal totalDepotChargesD = BigDecimal.ZERO;
	BigDecimal totalSerChargesD = BigDecimal.ZERO;
	
	BigDecimal totalInvoiceQtyND = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueND = BigDecimal.ZERO;
	BigDecimal totalActualFrightChargesND = BigDecimal.ZERO;
	BigDecimal totalFrightChargesND = BigDecimal.ZERO;
	BigDecimal totalDepotChargesND = BigDecimal.ZERO;
	BigDecimal totalSerChargesND = BigDecimal.ZERO;
	
	silkDepottotalsMap=[:];
	silkNonDepottotalsMap=[:];
	
	cottonDepottotalsMap=[:];
	cottonNonDepottotalsMap=[:];
	
	juteDepottotalsMap=[:];
	juteNonDepottotalsMap=[:];
	
	otherDepottotalsMap=[:];
	otherNonDepottotalsMap=[:];
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	
	for(invoice in silkinvoicesAndItems)
	{
		tempMap=[:]
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0;
		invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
		shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
		if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
			actualFrightCharges=shipment.estimatedShipCost
		}
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
		roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
		schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
		if(!schemePercentage){
			schemePercentage=roPercentagesMap.get("OTHER")
		}	
		serviceChrgPercentage=roPercentagesMap.get("serCharge")
		mgpsServiceCharge=(invoiceAmount.multiply(serviceChrgPercentage)).divide(100);
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
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			silkDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoice.quantity)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			silkNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoice.quantity)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceAmount)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		}
		index=index+1;
	}
	silkDepottotalsMap.put("partyName","TOTAL");
	silkDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	silkDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	silkDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	silkDepottotalsMap.put("frightCharges",totalFrightChargesD);
	silkDepottotalsMap.put("depotCharges",totalDepotChargesD);
	silkDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	silkDepotList.add(silkDepottotalsMap);
	
	silkNonDepottotalsMap.put("partyName","TOTAL");
	silkNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	silkNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	silkNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	silkNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	silkNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	silkNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	silkNonDepotList.add(silkNonDepottotalsMap);
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	
	for(invoice in cottonInvoicesAndItems)
	{
		tempMap=[:]
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		schemePercentage=0
		invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
		shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
		if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
			actualFrightCharges=shipment.estimatedShipCost
		}
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
		roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
		schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
		if(schemePercentage==null){
			schemePercentage=roPercentagesMap.get("OTHER")
		}
		serviceChrgPercentage=roPercentagesMap.get("serCharge")
		mgpsServiceCharge=(invoiceAmount.multiply(serviceChrgPercentage)).divide(100);
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
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			cottonDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoice.quantity)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			cottonNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoice.quantity)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceAmount)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
		}
		
		index=index+1;
		
	}
	
	
	
	cottonDepottotalsMap.put("partyName","TOTAL");
	cottonDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	cottonDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	cottonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	cottonDepottotalsMap.put("frightCharges",totalFrightChargesD);
	cottonDepottotalsMap.put("depotCharges",totalDepotChargesD);
	cottonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	cottonDepotList.add(cottonDepottotalsMap);
	
	cottonNonDepottotalsMap.put("partyName","TOTAL");
	cottonNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	cottonNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	cottonNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	cottonNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	cottonNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	cottonNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	cottonNonDepotList.add(cottonNonDepottotalsMap);
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	
	
	for(invoice in juteInvoicesAndItems)
	{
		tempMap=[:]
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		schemePercentage=0;
		invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
		shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
		if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
			actualFrightCharges=shipment.estimatedShipCost
		}
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
		roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
		schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
		if(schemePercentage==null){
			schemePercentage=roPercentagesMap.get("OTHER")
		}
		serviceChrgPercentage=roPercentagesMap.get("serCharge")
		mgpsServiceCharge=(invoiceAmount.multiply(serviceChrgPercentage)).divide(100);
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
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			juteDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoice.quantity)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			juteNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoice.quantity)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceAmount)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
		}
		
		index=index+1;
	}
	
	
	juteDepottotalsMap.put("partyName","TOTAL");
	juteDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	juteDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	juteDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	juteDepottotalsMap.put("frightCharges",totalFrightChargesD);
	juteDepottotalsMap.put("depotCharges",totalDepotChargesD);
	juteDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	juteDepotList.add(juteDepottotalsMap);
	juteNonDepottotalsMap.put("partyName","TOTAL");
	juteNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	juteNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	juteNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	juteNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	juteNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	juteNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	juteNonDepotList.add(juteNonDepottotalsMap);
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	schemePercentage=0;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	
	for(invoice in otherInvoicesAndItems)
	{
		tempMap=[:]
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		schemePercentage=0;
		invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
		shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
		if(UtilValidate.isNotEmpty(shipment) && UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
			actualFrightCharges=shipment.estimatedShipCost
		}
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
		roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
		schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
		if(schemePercentage==null){
			schemePercentage=roPercentagesMap.get("OTHER")
		}
		serviceChrgPercentage=roPercentagesMap.get("serCharge")
		mgpsServiceCharge=(invoiceAmount.multiply(serviceChrgPercentage)).divide(100);
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
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,invoice.partyId), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			otherDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoice.quantity)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			otherNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoice.quantity)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceAmount)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
		}
	
		index=index+1;
		
	}
	
	
	otherDepottotalsMap.put("partyName","TOTAL");
	otherDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	otherDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	otherDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	otherDepottotalsMap.put("frightCharges",totalFrightChargesD);
	otherDepottotalsMap.put("depotCharges",totalDepotChargesD);
	otherDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	otherDepotList.add(otherDepottotalsMap);
	
	otherNonDepottotalsMap.put("partyName","TOTAL");
	otherNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	otherNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	otherNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	otherNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	otherNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	otherNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	otherNonDepotList.add(otherNonDepottotalsMap);
}



//=================================================================================================================================================




def generatePartyWiseReport()
{
	index=1;
	BigDecimal totalInvoiceQtyD = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueD = BigDecimal.ZERO;
	BigDecimal totalActualFrightChargesD = BigDecimal.ZERO;
	BigDecimal totalFrightChargesD = BigDecimal.ZERO;
	BigDecimal totalDepotChargesD = BigDecimal.ZERO;
	BigDecimal totalSerChargesD = BigDecimal.ZERO;
	
	BigDecimal totalInvoiceQtyND = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueND = BigDecimal.ZERO;
	BigDecimal totalActualFrightChargesND = BigDecimal.ZERO;
	BigDecimal totalFrightChargesND = BigDecimal.ZERO;
	BigDecimal totalDepotChargesND = BigDecimal.ZERO;
	BigDecimal totalSerChargesND = BigDecimal.ZERO;
	
	silkDepottotalsMap=[:];
	silkNonDepottotalsMap=[:];
	
	cottonDepottotalsMap=[:];
	cottonNonDepottotalsMap=[:];
	
	juteDepottotalsMap=[:];
	juteNonDepottotalsMap=[:];
	
	otherDepottotalsMap=[:];
	otherNonDepottotalsMap=[:];
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	silkPartyIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"partyId", true);
	
	for(eachParty in silkPartyIds)
	{
		String partyName = "";
		silkinvoicesAndItems1 = EntityUtil.filterByCondition(silkinvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0;
		for(invoice in silkinvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", invoiceValue);
		tempMap.put("actualFrightCharges", actualFrightCharges);
        tempMap.put("frightCharges", eligibleFrightCharges);
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			silkDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			silkNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		}
		index=index+1;
	}
	
	silkDepottotalsMap.put("partyName","TOTAL");
	silkDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	silkDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	silkDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	silkDepottotalsMap.put("frightCharges",totalFrightChargesD);
	silkDepottotalsMap.put("depotCharges",totalDepotChargesD);
	silkDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	silkDepotList.add(silkDepottotalsMap);
	
	silkNonDepottotalsMap.put("partyName","TOTAL");
	silkNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	silkNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	silkNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	silkNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	silkNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	silkNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	silkNonDepotList.add(silkNonDepottotalsMap);
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	cottonPartyIds=EntityUtil.getFieldListFromEntityList(cottonInvoicesAndItems,"partyId", true);
	
	for(eachParty in cottonPartyIds)
	{
		String partyName = "";
		cottonInvoicesAndItems1 = EntityUtil.filterByCondition(cottonInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in cottonInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", invoiceValue);
		tempMap.put("actualFrightCharges", actualFrightCharges);
		tempMap.put("frightCharges", eligibleFrightCharges);
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			cottonDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			cottonNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		}
		index=index+1;
	}
	
	cottonDepottotalsMap.put("partyName","TOTAL");
	cottonDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	cottonDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	cottonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	cottonDepottotalsMap.put("frightCharges",totalFrightChargesD);
	cottonDepottotalsMap.put("depotCharges",totalDepotChargesD);
	cottonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	cottonDepotList.add(cottonDepottotalsMap);
	
	cottonNonDepottotalsMap.put("partyName","TOTAL");
	cottonNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	cottonNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	cottonNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	cottonNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	cottonNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	cottonNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	cottonNonDepotList.add(cottonNonDepottotalsMap);
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	jutePartyIds=EntityUtil.getFieldListFromEntityList(juteInvoicesAndItems,"partyId", true);
	
	for(eachParty in jutePartyIds)
	{
		String partyName ="";
		juteInvoicesAndItems1 = EntityUtil.filterByCondition(juteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in juteInvoicesAndItems1)
		{
			partyName= PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", invoiceValue);
		tempMap.put("actualFrightCharges", actualFrightCharges);
		tempMap.put("frightCharges", eligibleFrightCharges);
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			juteDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			juteNonDepotList.add(tempMap);
			  
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		}
		index=index+1;
	}
	
	juteDepottotalsMap.put("partyName","TOTAL");
	juteDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	juteDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	juteDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	juteDepottotalsMap.put("frightCharges",totalFrightChargesD);
	juteDepottotalsMap.put("depotCharges",totalDepotChargesD);
	juteDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	juteDepotList.add(juteDepottotalsMap);
	juteNonDepottotalsMap.put("partyName","TOTAL");
	juteNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	juteNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	juteNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	juteNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	juteNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	juteNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	juteNonDepotList.add(juteNonDepottotalsMap);
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	otherPartyIds=EntityUtil.getFieldListFromEntityList(otherInvoicesAndItems,"partyId", true);
	
	for(eachParty in otherPartyIds)
	{
		String partyName = "";
		otherInvoicesAndItems1 = EntityUtil.filterByCondition(otherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in otherInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", invoiceValue);
		tempMap.put("actualFrightCharges", actualFrightCharges);
		tempMap.put("frightCharges", eligibleFrightCharges);
		tempMap.put("mgpsServiceCharge", mgpsServiceCharge);
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			tempMap.put("depotCharges", depotCharges);
			otherDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("depotCharges", BigDecimal.ZERO);
			otherNonDepotList.add(tempMap);
			 
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		}
		index=index+1;
	}
	
	otherDepottotalsMap.put("partyName","TOTAL");
	otherDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	otherDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	otherDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	otherDepottotalsMap.put("frightCharges",totalFrightChargesD);
	otherDepottotalsMap.put("depotCharges",totalDepotChargesD);
	otherDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	otherDepotList.add(otherDepottotalsMap);
	
	otherNonDepottotalsMap.put("partyName","TOTAL");
	otherNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	otherNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	otherNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	otherNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	otherNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	otherNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	otherNonDepotList.add(otherNonDepottotalsMap);
	
}






def generateSummaryReport(stateGeoIds)
{
	index=1;
	BigDecimal totalInvoiceQtyD = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueD = BigDecimal.ZERO;
	BigDecimal totalActualFrightChargesD = BigDecimal.ZERO;
	BigDecimal totalFrightChargesD = BigDecimal.ZERO;
	BigDecimal totalDepotChargesD = BigDecimal.ZERO;
	BigDecimal totalSerChargesD = BigDecimal.ZERO;
	
	BigDecimal totalInvoiceQtyND = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueND = BigDecimal.ZERO;
	BigDecimal totalActualFrightChargesND = BigDecimal.ZERO;
	BigDecimal totalFrightChargesND = BigDecimal.ZERO;
	BigDecimal totalDepotChargesND = BigDecimal.ZERO;
	BigDecimal totalSerChargesND = BigDecimal.ZERO;
	
	silkDepottotalsMap=[:];
	silkNonDepottotalsMap=[:];
	
	cottonDepottotalsMap=[:];
	cottonNonDepottotalsMap=[:];
	
	juteDepottotalsMap=[:];
	juteNonDepottotalsMap=[:];
	
	otherDepottotalsMap=[:];
	otherNonDepottotalsMap=[:];
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	silkPartyIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"partyId", true);
	
	for(eachState in stateGeoIds)
	{
		tempMapD=[:]
		tempMapND=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		stateDetails = delegator.findOne("Geo",[geoId : eachState] , false);
		stateName=stateDetails.geoName
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,branchIds));
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.EQUALS,eachState));
		eachStateBranchsList = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
		eachStateBranchIds=EntityUtil.getFieldListFromEntityList(eachStateBranchsList,"partyId", true);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN,eachStateBranchIds));
		conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"ORGANIZATION_UNIT"));
		conditionList.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS,"EMPANELLED_CUSTOMER"));
		eachStateCustomersList = delegator.findList("PartyRelationship",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false );
		eachStateSilkPartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomersList,"partyIdTo", true);
		stateSilkinvoicesAndItems = EntityUtil.filterByCondition(silkinvoicesAndItems, EntityCondition.makeCondition("costCenterId", EntityOperator.IN,eachStateBranchIds));
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateSilkPartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateSilkPartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		
		silkinvoicesAndItems1D = EntityUtil.filterByCondition(stateSilkinvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateSilkPartyIdsD));
		silkinvoicesAndItems1ND = EntityUtil.filterByCondition(stateSilkinvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN,eachStateSilkPartyIdsD));
		for(invoice in silkinvoicesAndItems1D)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		tempMapD.put("depotCharges", depotCharges);
		if(invoiceQty>0){
			silkDepotList.add(tempMapD);
		}
		 invoiceQty = BigDecimal.ZERO;
		 invoiceValue = BigDecimal.ZERO;
		 actualFrightCharges = BigDecimal.ZERO;
		 eligibleFrightCharges = BigDecimal.ZERO;
		 depotCharges = BigDecimal.ZERO;
		 mgpsServiceCharge = BigDecimal.ZERO;
		 schemePercentage=0
		for(invoice in silkinvoicesAndItems1ND)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
		}
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		tempMapND.put("depotCharges", depotCharges);
		
		if(invoiceQty>0){
			silkNonDepotList.add(tempMapND);
		}
		index=index+1;
		
	}
	
	
	silkDepottotalsMap.put("partyName","TOTAL");
	silkDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	silkDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	silkDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	silkDepottotalsMap.put("frightCharges",totalFrightChargesD);
	silkDepottotalsMap.put("depotCharges",totalDepotChargesD);
	silkDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	silkDepotList.add(silkDepottotalsMap);
	
	silkNonDepottotalsMap.put("partyName","TOTAL");
	silkNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	silkNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	silkNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	silkNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	silkNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	silkNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	silkNonDepotList.add(silkNonDepottotalsMap);
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	cottonPartyIds=EntityUtil.getFieldListFromEntityList(cottonInvoicesAndItems,"partyId", true);
	
	
	for(eachState in stateGeoIds)
	{
		tempMapD=[:]
		tempMapND=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		stateDetails = delegator.findOne("Geo",[geoId : eachState] , false);
		stateName=stateDetails.geoName
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,cottonPartyIds));
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.EQUALS,eachState));
		eachStateCustomerList = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
		eachStateCottonPartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomerList,"partyId", true);
		stateCottoninvoicesAndItems = EntityUtil.filterByCondition(cottonInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateCottonPartyIds));
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateCottonPartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateCottonPartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		cottoninvoicesAndItems1D = EntityUtil.filterByCondition(stateCottoninvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateCottonPartyIdsD));
		cottoninvoicesAndItems1ND = EntityUtil.filterOutByCondition(stateCottoninvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateCottonPartyIdsD));
		
		for(invoice in cottoninvoicesAndItems1D)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("depotCharges", depotCharges);
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		cottonDepotList.add(tempMapD);
		
		invoiceQty = BigDecimal.ZERO;
		invoiceValue = BigDecimal.ZERO;
		actualFrightCharges = BigDecimal.ZERO;
		eligibleFrightCharges = BigDecimal.ZERO;
		depotCharges = BigDecimal.ZERO;
		mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in cottoninvoicesAndItems1ND)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			
		}
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("depotCharges", depotCharges);
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		cottonNonDepotList.add(tempMapND);
		
		index=index+1;
	}
	
	cottonDepottotalsMap.put("partyName","TOTAL");
	cottonDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	cottonDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	cottonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	cottonDepottotalsMap.put("frightCharges",totalFrightChargesD);
	cottonDepottotalsMap.put("depotCharges",totalDepotChargesD);
	cottonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	cottonDepotList.add(cottonDepottotalsMap);
	
	cottonNonDepottotalsMap.put("partyName","TOTAL");
	cottonNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	cottonNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	cottonNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	cottonNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	cottonNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	cottonNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	cottonNonDepotList.add(cottonNonDepottotalsMap);
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	jutePartyIds=EntityUtil.getFieldListFromEntityList(juteInvoicesAndItems,"partyId", true);
	
	for(eachState in stateGeoIds)
	{
		tempMapD=[:]
		tempMapND=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		stateDetails = delegator.findOne("Geo",[geoId : eachState] , false);
		stateName=stateDetails.geoName
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,jutePartyIds));
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.EQUALS,eachState));
		eachStateCustomerList = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
		eachStateJutePartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomerList,"partyId", true);
		stateJuteInvoicesAndItems = EntityUtil.filterByCondition(juteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateJutePartyIds));
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateJutePartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateJutePartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		juteInvoicesAndItems1D = EntityUtil.filterByCondition(stateJuteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateJutePartyIdsD));
		juteInvoicesAndItems1ND = EntityUtil.filterOutByCondition(stateJuteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateJutePartyIdsD));
		
		
		for(invoice in juteInvoicesAndItems1D)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
			
		}
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("depotCharges", depotCharges);
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		juteDepotList.add(tempMapD);
		
		invoiceQty = BigDecimal.ZERO;
		invoiceValue = BigDecimal.ZERO;
		actualFrightCharges = BigDecimal.ZERO;
		eligibleFrightCharges = BigDecimal.ZERO;
		depotCharges = BigDecimal.ZERO;
		mgpsServiceCharge = BigDecimal.ZERO;
		
		for(invoice in juteInvoicesAndItems1ND)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
		}
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("depotCharges", depotCharges);
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		juteNonDepotList.add(tempMapND);
		
		index=index+1;
	}
	
	juteDepottotalsMap.put("partyName","TOTAL");
	juteDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	juteDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	juteDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	juteDepottotalsMap.put("frightCharges",totalFrightChargesD);
	juteDepottotalsMap.put("depotCharges",totalDepotChargesD);
	juteDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	juteDepotList.add(juteDepottotalsMap);
	juteNonDepottotalsMap.put("partyName","TOTAL");
	juteNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	juteNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	juteNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	juteNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	juteNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	juteNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	juteNonDepotList.add(juteNonDepottotalsMap);
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalActualFrightChargesD = BigDecimal.ZERO;
	totalFrightChargesD = BigDecimal.ZERO;
	totalDepotChargesD = BigDecimal.ZERO;
	totalSerChargesD = BigDecimal.ZERO;
	
	totalInvoiceQtyND = BigDecimal.ZERO;
	totalInvoiceValueND = BigDecimal.ZERO;
	totalActualFrightChargesND = BigDecimal.ZERO;
	totalFrightChargesND = BigDecimal.ZERO;
	totalDepotChargesND = BigDecimal.ZERO;
	totalSerChargesND=BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	otherPartyIds=EntityUtil.getFieldListFromEntityList(otherInvoicesAndItems,"partyId", true);
	
	for(eachState in stateGeoIds)
	{
		tempMapD=[:]
		tempMapND=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		stateDetails = delegator.findOne("Geo",[geoId : eachState] , false);
		stateName=stateDetails.geoName
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,otherPartyIds));
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.EQUALS,eachState));
		eachStateCustomerList = EntityUtil.filterByCondition(partyAndPostalAddress, EntityCondition.makeCondition(conditionList,EntityOperator.AND));
		eachStateOtherPartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomerList,"partyId", true);
		stateOtherInvoicesAndItems = EntityUtil.filterByCondition(otherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateOtherPartyIds));
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateOtherPartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateOtherPartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		otherInvoicesAndItems1D = EntityUtil.filterByCondition(stateOtherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateOtherPartyIdsD));
		otherInvoicesAndItems1ND = EntityUtil.filterOutByCondition(stateOtherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateOtherPartyIdsD));
		
		for(invoice in otherInvoicesAndItems1D)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
			depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
		}
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("depotCharges", depotCharges);
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		otherDepotList.add(tempMapD)
		
		invoiceQty = BigDecimal.ZERO;
		invoiceValue = BigDecimal.ZERO;
		actualFrightCharges = BigDecimal.ZERO;
		eligibleFrightCharges = BigDecimal.ZERO;
		depotCharges = BigDecimal.ZERO;
		mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in otherInvoicesAndItems1ND)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceValue=invoiceValue.add(invoiceAmount);
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actualFrightCharges=actualFrightCharges.add(shipment.estimatedShipCost)
			}
			product = delegator.findOne("Product",[productId : invoice.productId] , false);
			productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
			roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
			schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
			if(schemePercentage==null){
				schemePercentage=roPercentagesMap.get("OTHER")
			}
			serviceChrgPercentage=roPercentagesMap.get("serCharge")
			mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			eligibleFrightCharges=eligibleFrightCharges.add((invoiceAmount.multiply(schemePercentage)).divide(100))
		}
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("depotCharges", depotCharges);
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		otherNonDepotList.add(tempMapND)
		index=index+1;
	}
	
	otherDepottotalsMap.put("partyName","TOTAL");
	otherDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	otherDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	otherDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesD);
	otherDepottotalsMap.put("frightCharges",totalFrightChargesD);
	otherDepottotalsMap.put("depotCharges",totalDepotChargesD);
	otherDepottotalsMap.put("mgpsServiceCharge",totalSerChargesD);
	otherDepotList.add(otherDepottotalsMap);

	otherNonDepottotalsMap.put("partyName","TOTAL");
	otherNonDepottotalsMap.put("totInvQty",totalInvoiceQtyND);
	otherNonDepottotalsMap.put("totInvValue",totalInvoiceValueND);
	otherNonDepottotalsMap.put("actualFrightCharges",totalActualFrightChargesND);
	otherNonDepottotalsMap.put("frightCharges",totalFrightChargesND);
	otherNonDepottotalsMap.put("depotCharges",totalDepotChargesND);
	otherNonDepottotalsMap.put("mgpsServiceCharge",totalSerChargesND);
	otherNonDepotList.add(otherNonDepottotalsMap);
	

	
}


context.silkDepotList=silkDepotList
context.silkNonDepotList=silkNonDepotList
  
context.cottonDepotList=cottonDepotList  
context.cottonNonDepotList=cottonNonDepotList

context.juteDepotList=juteDepotList
context.juteNonDepotList=juteNonDepotList

context.otherDepotList=otherDepotList
context.otherNonDepotList=otherNonDepotList








