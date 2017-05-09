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
claimFromDate=parameters.claimFromDateTenPer;
claimThruDate=parameters.claimThruDateTenPer;
geoId = parameters.geoId;
branchId = parameters.branchId;
rowiseTsPercentageMap=[:];
hydRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2.5,"COIR_YARN",10, "OTHER",2.5,"serCharge",2);
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

cottonDepotList=[]

juteDepotList=[]

otherDepotList=[]


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
	DecimalFormat twoDForm = new DecimalFormat("#.##");
	BigDecimal totalReimbursementAmount= BigDecimal.ZERO;
	BigDecimal totalInvoiceQtyD = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueD = BigDecimal.ZERO;
	BigDecimal totalTenPerSubAmountD = BigDecimal.ZERO;
	BigDecimal totalServiceChargeD = BigDecimal.ZERO;
	BigDecimal totalSubAmountD = BigDecimal.ZERO;
	
	silkDepottotalsMap=[:];
	cottonDepottotalsMap=[:];
	juteDepottotalsMap=[:];
	otherDepottotalsMap=[:]; 
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","invoiceItemSeqId","quantity","productId","costCenterId","itemValue"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	for(invoice in silkinvoicesAndItems)
	{
		tempMap=[:];
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
		invoiceAmount=getInvocieAmount(invoice.invoiceId,invoice.invoiceItemSeqId)
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId));
		conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoice.invoiceItemSeqId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		fieldsToSelect = ["invoiceItemSeqId"] as Set;
		invoiceItems1 = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
		invoiceItem1=EntityUtil.getFirst(invoiceItems1)
		if(invoiceItem1!=null){
			invoiceQty=getSchemeQty(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
			tenPerSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
		}
		
		serviceCharge=tenPerSubAmount.multiply(0.05)
		subsidyAmount=tenPerSubAmount.add(tenPerSubAmount.multiply(0.05))
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("productName", product.productName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", twoDForm.format(invoiceAmount));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			silkDepotList.add(tempMap);
			index=index+1;
		}
		
	}
	
	silkDepottotalsMap.put("partyName","TOTAL");
	silkDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	silkDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	silkDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	silkDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	silkDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD) );
	silkDepotList.add(silkDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","invoiceItemSeqId","quantity","productId","costCenterId","itemValue"] as Set;
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND),fieldsToSelect, null, null, false );
	
	
	
	for(invoice in cottonInvoicesAndItems)
	{
		tempMap=[:];
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
		invoiceAmount=getInvocieAmount(invoice.invoiceId,invoice.invoiceItemSeqId)
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId));
		conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoice.invoiceItemSeqId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		fieldsToSelect = ["invoiceItemSeqId"] as Set;
		invoiceItems1 = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
		invoiceItem1=EntityUtil.getFirst(invoiceItems1)
		if(invoiceItem1!=null){
			invoiceQty=getSchemeQty(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
			tenPerSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
		}
		
		serviceCharge=tenPerSubAmount.multiply(0.05)
		subsidyAmount=tenPerSubAmount.add(tenPerSubAmount.multiply(0.05))
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("productName", product.productName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", twoDForm.format(invoiceAmount));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			cottonDepotList.add(tempMap);
			index=index+1;
		}
		
		
	}
	
	cottonDepottotalsMap.put("partyName","TOTAL");
	cottonDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	cottonDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	cottonDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	cottonDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	cottonDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	cottonDepotList.add(cottonDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","invoiceItemSeqId","quantity","productId","costCenterId","itemValue"] as Set;
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect , null, null, false );
	
	
	for(invoice in juteInvoicesAndItems)
	{
		tempMap=[:];
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
		invoiceAmount=getInvocieAmount(invoice.invoiceId,invoice.invoiceItemSeqId)
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId));
		conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoice.invoiceItemSeqId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		fieldsToSelect = ["invoiceItemSeqId"] as Set;
		invoiceItems1 = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
		invoiceItem1=EntityUtil.getFirst(invoiceItems1)
		if(invoiceItem1!=null){
			invoiceQty=getSchemeQty(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
			tenPerSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
		}
		
		serviceCharge=tenPerSubAmount.multiply(0.05)
		subsidyAmount=tenPerSubAmount.add(tenPerSubAmount.multiply(0.05))
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("productName", product.productName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", twoDForm.format(invoiceAmount));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			juteDepotList.add(tempMap);
		    index=index+1;
		}
		
	}
	
	juteDepottotalsMap.put("partyName","TOTAL");
	juteDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	juteDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	juteDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	juteDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	juteDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	juteDepotList.add(juteDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	
	schemePercentage=0;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","invoiceItemSeqId","quantity","productId","costCenterId","itemValue"] as Set;
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	
	for(invoice in otherInvoicesAndItems)
	{
		tempMap=[:];
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		product = delegator.findOne("Product",[productId : invoice.productId] , false);
		partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
		invoiceAmount=getInvocieAmount(invoice.invoiceId,invoice.invoiceItemSeqId)
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId));
		conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,invoice.invoiceItemSeqId));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		fieldsToSelect = ["invoiceItemSeqId"] as Set;
		invoiceItems1 = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
		invoiceItem1=EntityUtil.getFirst(invoiceItems1)
		if(invoiceItem1!=null){
			invoiceQty=getSchemeQty(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
			tenPerSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,invoiceItem1.invoiceItemSeqId)
		}
		serviceCharge=tenPerSubAmount.multiply(0.05)
		subsidyAmount=tenPerSubAmount.add(tenPerSubAmount.multiply(0.05))
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("productName", product.productName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", twoDForm.format(invoiceAmount));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceAmount)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			otherDepotList.add(tempMap);
		    index=index+1;
		}
		
	}
	
	otherDepottotalsMap.put("partyName","TOTAL");
	otherDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	otherDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	otherDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	otherDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	otherDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	otherDepotList.add(otherDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	context.totalReimbursementAmount=twoDForm.format(totalReimbursementAmount);
	
}



//=================================================================================================================================================

def getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd)
{
	result=[:];
	innerCondition=[];
	innerCondition.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	innerCondition.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	innerCondition.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	innerCondition.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	fieldsToSelect = ["invoiceId"] as Set;
	invoices = delegator.findList("Invoice",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	invoiceIds=EntityUtil.getFieldListFromEntityList(invoices,"invoiceId", true);
	innerCondition.clear()
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
	fieldsToSelect = ["orderId"] as Set;
	orderItemBilling = delegator.findList("OrderItemBilling",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	orderIds=EntityUtil.getFieldListFromEntityList(orderItemBilling,"orderId", true);
	innerCondition.clear()
	innerCondition.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds));
	innerCondition.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"SCHEME_CAT"));
	innerCondition.add(EntityCondition.makeCondition("attrValue",EntityOperator.EQUALS,"MGPS_10Pecent"));
	fieldsToSelect = ["orderId"] as Set;
	orderAttributetenper = delegator.findList("OrderAttribute",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	tenPerOrderIds=EntityUtil.getFieldListFromEntityList(orderAttributetenper,"orderId", true);
	innerCondition.clear()
	innerCondition.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds));
	innerCondition.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"SCHEME_CAT"));
	innerCondition.add(EntityCondition.makeCondition("attrValue",EntityOperator.EQUALS,"MGPS"));
	fieldsToSelect = ["orderId"] as Set;
	orderAttributemgps = delegator.findList("OrderAttribute",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	mgpsOrderIds=EntityUtil.getFieldListFromEntityList(orderAttributemgps,"orderId", true);
	innerCondition.clear()
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
	innerCondition.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,tenPerOrderIds));
	fieldsToSelect = ["invoiceId"] as Set;
	orderItemBillingtenper = delegator.findList("OrderItemBilling",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	invoiceIdsTenPer=EntityUtil.getFieldListFromEntityList(orderItemBillingtenper,"invoiceId", true);
	
	innerCondition.clear()
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,invoiceIds));
	innerCondition.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,mgpsOrderIds));
	fieldsToSelect = ["invoiceId"] as Set;
	orderItemBillingMgps = delegator.findList("OrderItemBilling",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	invoiceIdsMgps=EntityUtil.getFieldListFromEntityList(orderItemBillingMgps,"invoiceId", true);
	result.putAt("tenPerInvoiceIds", invoiceIdsTenPer)
	result.putAt("mgpsInvoiceIds", invoiceIdsMgps)
	return result;
}

def getInvocieAmount(invoiceId ,invoiceItemSeqId)
{
	BigDecimal invoiceAmount = BigDecimal.ZERO;
	innerCondition=[];
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
	innerCondition.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_EQUAL,"TEN_PERCENT_SUBSIDY"));
	innerCondition.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "ROUNDING_ADJUSTMENT"));
	innerCondition.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	if(UtilValidate.isNotEmpty(invoiceItemSeqId)){
		innerCondition.add(EntityCondition.makeCondition("invoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
	}
	fieldsToSelect = ["amount","invoiceId","invoiceItemSeqId","parentInvoiceItemSeqId","itemValue"] as Set;
	invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	for(eachItem in invoiceItems)
	{
		innerCondition.clear();
		innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
		innerCondition.add(EntityCondition.makeCondition("parentInvoiceItemSeqId",EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
		innerCondition.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		fieldsToSelect = ["invoiceItemSeqId"] as Set;
		invoiceItems1 = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
		invoiceItem1=EntityUtil.getFirst(invoiceItems1)
		if(invoiceItem1!=null){
			schemeQty = getSchemeQty(eachItem.invoiceId,invoiceItem1.invoiceItemSeqId)
			if(schemeQty>0)
			invoiceAmount=invoiceAmount.add(schemeQty.multiply(eachItem.amount));
			else if(eachItem.itemValue!=null)
			invoiceAmount=invoiceAmount.add(eachItem.itemValue);
		}
	}
	return invoiceAmount;
}

def getTenPerSubsidyAmount(invoiceId,invoiceItemSeqId)
{
	BigDecimal tenPerSubAmount = BigDecimal.ZERO;
	innerCondition=[];
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
	innerCondition.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
	if(UtilValidate.isNotEmpty(invoiceItemSeqId)){
		innerCondition.add(EntityCondition.makeCondition("invoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
	}
	fieldsToSelect = ["itemValue"] as Set;
	invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	for(eachItem in invoiceItems)
	{
		if(eachItem.itemValue!=null)
		tenPerSubAmount=tenPerSubAmount.add(eachItem.itemValue)
	}
	tenPerSubAmount=tenPerSubAmount.multiply(-1)
	
	return tenPerSubAmount;
	
}

def getSchemeQty(invoiceId,invoiceItemSeqId)
{
	BigDecimal schemeQty = BigDecimal.ZERO;
	innerCondition=[];
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
	if(UtilValidate.isNotEmpty(invoiceItemSeqId)){
		innerCondition.add(EntityCondition.makeCondition("invoiceItemSeqId",EntityOperator.EQUALS,invoiceItemSeqId));
	}
	fieldsToSelect = ["quantity"] as Set;
	result = delegator.findList("OrderAdjustmentBilling",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	for(eachItem in result)
	{
		if(eachItem.quantity!=null)
		schemeQty=schemeQty.add(eachItem.quantity)
	}
	return schemeQty;
}


def generatePartyWiseReport()
{
	index=1;
	duplicateInvoiceIds=[];
	DecimalFormat twoDForm = new DecimalFormat("#.##");
	BigDecimal totalReimbursementAmount= BigDecimal.ZERO;
	BigDecimal totalInvoiceQtyD = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueD = BigDecimal.ZERO;
	BigDecimal totalTenPerSubAmountD = BigDecimal.ZERO;
	BigDecimal totalServiceChargeD = BigDecimal.ZERO;
	BigDecimal totalSubAmountD = BigDecimal.ZERO;
	
	silkDepottotalsMap=[:];
	
	cottonDepottotalsMap=[:];
	
	juteDepottotalsMap=[:];
	
	otherDepottotalsMap=[:];
	  
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","quantity","productId","costCenterId","itemValue","invoiceItemSeqId"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	silkPartyIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"partyId", true);
	
	for(eachParty in silkPartyIds)
	{
		String partyName = "";
		silkinvoicesAndItems1 = EntityUtil.filterByCondition(silkinvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		schemePercentage=0;
		
		for(invoice in silkinvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		tempMap.put("Sno", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue) );
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount) );
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount) );
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			silkDepotList.add(tempMap);
		    index=index+1;
		}
		
	}
	duplicateInvoiceIds.clear()
	silkDepottotalsMap.put("partyName","TOTAL");
	silkDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD) );
	silkDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD) );
	silkDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD) );
	silkDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD) );
	silkDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD) );
	silkDepotList.add(silkDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","quantity","productId","costCenterId","itemValue"] as Set;
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	cottonPartyIds=EntityUtil.getFieldListFromEntityList(cottonInvoicesAndItems,"partyId", true);
	
	for(eachParty in cottonPartyIds)
	{
		String partyName = "";
		cottonInvoicesAndItems1 = EntityUtil.filterByCondition(cottonInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in cottonInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			cottonDepotList.add(tempMap);
		    index=index+1;
		}
	}
	duplicateInvoiceIds.clear()
	cottonDepottotalsMap.put("partyName","TOTAL");
	cottonDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	cottonDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	cottonDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	cottonDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	cottonDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	cottonDepotList.add(cottonDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","quantity","productId","costCenterId","itemValue"] as Set;
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	jutePartyIds=EntityUtil.getFieldListFromEntityList(juteInvoicesAndItems,"partyId", true);
	
	for(eachParty in jutePartyIds)
	{
		String partyName ="";
		juteInvoicesAndItems1 = EntityUtil.filterByCondition(juteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in juteInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			juteDepotList.add(tempMap);
		    index=index+1;
		}
	}
	duplicateInvoiceIds.clear()
	juteDepottotalsMap.put("partyName","TOTAL");
	juteDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	juteDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	juteDepottotalsMap.put("actualFrightCharges",totalTenPerSubAmountD);
	juteDepottotalsMap.put("frightCharges",totalServiceChargeD);
	juteDepottotalsMap.put("mgpsServiceCharge",totalSubAmountD);
	juteDepotList.add(juteDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","quantity","productId","costCenterId","itemValue"] as Set;
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	otherPartyIds=EntityUtil.getFieldListFromEntityList(otherInvoicesAndItems,"partyId", true);
	
	for(eachParty in otherPartyIds)
	{
		String partyName = "";
		otherInvoicesAndItems1 = EntityUtil.filterByCondition(otherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in otherInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
			
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		if(invoiceQty>0){
			otherDepotList.add(tempMap);
		    index=index+1;
		}
	}
	duplicateInvoiceIds.clear()
	otherDepottotalsMap.put("partyName","TOTAL");
	otherDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	otherDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	otherDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	otherDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	otherDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	otherDepotList.add(otherDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	context.totalReimbursementAmount=twoDForm.format(totalReimbursementAmount)
	
}






def generateSummaryReport(stateGeoIds)
{
	index=1;
	duplicateInvoiceIds=[];
	DecimalFormat twoDForm = new DecimalFormat("#.##");
	BigDecimal totalReimbursementAmount= BigDecimal.ZERO;
	BigDecimal totalInvoiceQtyD = BigDecimal.ZERO;
	BigDecimal totalInvoiceValueD = BigDecimal.ZERO;
	BigDecimal totalTenPerSubAmountD = BigDecimal.ZERO;
	BigDecimal totalServiceChargeD = BigDecimal.ZERO;
	BigDecimal totalSubAmountD = BigDecimal.ZERO;
	
	silkDepottotalsMap=[:];
	
	cottonDepottotalsMap=[:];
	
	juteDepottotalsMap=[:];
	
	otherDepottotalsMap=[:];
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","quantity","productId","costCenterId","itemValue"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	silkPartyIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"partyId", true);
	
	for(eachState in stateGeoIds)
	{
		tempMap=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
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
		
		for(invoice in stateSilkinvoicesAndItems)
		{
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", stateName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		if(invoiceQty>0){
			silkDepotList.add(tempMap);
		    index=index+1;
		}
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		
	}
	
	duplicateInvoiceIds.clear();
	silkDepottotalsMap.put("partyName","TOTAL");
	silkDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	silkDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	silkDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	silkDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	silkDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	silkDepotList.add(silkDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	cottonPartyIds=EntityUtil.getFieldListFromEntityList(cottonInvoicesAndItems,"partyId", true);
	
	
	for(eachState in stateGeoIds)
	{
		tempMap=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
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
		eachStateCottonPartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomersList,"partyIdTo", true);
		stateCottoninvoicesAndItems = EntityUtil.filterByCondition(cottonInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateCottonPartyIds));
		
		for(invoice in stateCottoninvoicesAndItems)
		{
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", stateName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		if(invoiceQty>0){
			cottonDepotList.add(tempMap);
		    index=index+1;
		}
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		
	}
	
	duplicateInvoiceIds.clear();
	cottonDepottotalsMap.put("partyName","TOTAL");
	cottonDepottotalsMap.put("totInvQty",twoDForm.format(totalInvoiceQtyD));
	cottonDepottotalsMap.put("totInvValue",twoDForm.format(totalInvoiceValueD));
	cottonDepottotalsMap.put("actualFrightCharges",twoDForm.format(totalTenPerSubAmountD));
	cottonDepottotalsMap.put("frightCharges",twoDForm.format(totalServiceChargeD));
	cottonDepottotalsMap.put("mgpsServiceCharge",twoDForm.format(totalSubAmountD));
	cottonDepotList.add(cottonDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,juteProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	jutePartyIds=EntityUtil.getFieldListFromEntityList(juteInvoicesAndItems,"partyId", true);
	
	for(eachState in stateGeoIds)
	{
		tempMap=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
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
		eachStateJutePartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomersList,"partyIdTo", true);
		stateJuteInvoicesAndItems = EntityUtil.filterByCondition(juteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateJutePartyIds));
		
		for(invoice in stateJuteInvoicesAndItems)
		{
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", stateName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		if(invoiceQty>0){
			juteDepotList.add(tempMap);
		    index=index+1;
		}
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		
	}
	
	duplicateInvoiceIds.clear();
	juteDepottotalsMap.put("partyName","TOTAL");
	juteDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	juteDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	juteDepottotalsMap.put("actualFrightCharges",totalTenPerSubAmountD);
	juteDepottotalsMap.put("frightCharges",totalServiceChargeD);
	juteDepottotalsMap.put("mgpsServiceCharge",totalSubAmountD);
	juteDepotList.add(juteDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	index=1;
	totalInvoiceQtyD = BigDecimal.ZERO;
	totalInvoiceValueD = BigDecimal.ZERO;
	totalTenPerSubAmountD = BigDecimal.ZERO;
	totalServiceChargeD = BigDecimal.ZERO;
	totalSubAmountD = BigDecimal.ZERO;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,otherProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerInvoiceIds));
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("invoiceId","quantity","partyId","partyIdFrom","shipmentId","productId"), null, null, false );
	otherPartyIds=EntityUtil.getFieldListFromEntityList(otherInvoicesAndItems,"partyId", true);
	
	for(eachState in stateGeoIds)
	{
		tempMap=[:]
		String stateName = "";
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal tenPerSubAmount = BigDecimal.ZERO;
		BigDecimal serviceCharge = BigDecimal.ZERO;
		BigDecimal subsidyAmount = BigDecimal.ZERO;
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
		eachStateOtherPartyIds=EntityUtil.getFieldListFromEntityList(eachStateCustomersList,"partyIdTo", true);
		stateOtherInvoicesAndItems = EntityUtil.filterByCondition(otherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateOtherPartyIds));
		
		for(invoice in stateOtherInvoicesAndItems)
		{
			invoiceAmount=getInvocieAmount(invoice.invoiceId,"")
			tenSubAmount=getTenPerSubsidyAmount(invoice.invoiceId,"")
			schemeQty=getSchemeQty(invoice.invoiceId,"")
			
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				invoiceQty=invoiceQty.add(schemeQty);
				invoiceValue=invoiceValue.add(invoiceAmount);
				tenPerSubAmount=tenPerSubAmount.add(tenSubAmount)
				serviceCharge=serviceCharge.add(tenSubAmount.multiply(0.05))
				subsidyAmount=subsidyAmount.add(tenSubAmount.add(tenSubAmount.multiply(0.05)))
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", stateName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(tenPerSubAmount));
		tempMap.put("frightCharges", twoDForm.format(serviceCharge));
		tempMap.put("mgpsServiceCharge", twoDForm.format(subsidyAmount));
		if(invoiceQty>0){
			otherDepotList.add(tempMap);
		    index=index+1;
		}
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalTenPerSubAmountD=totalTenPerSubAmountD.add(tenPerSubAmount)
		totalServiceChargeD=totalServiceChargeD.add(serviceCharge);
		totalSubAmountD=totalSubAmountD.add(subsidyAmount);
		
	}
	
	duplicateInvoiceIds.clear();
	otherDepottotalsMap.put("partyName","TOTAL");
	otherDepottotalsMap.put("totInvQty",totalInvoiceQtyD);
	otherDepottotalsMap.put("totInvValue",totalInvoiceValueD);
	otherDepottotalsMap.put("actualFrightCharges",totalTenPerSubAmountD);
	otherDepottotalsMap.put("frightCharges",totalServiceChargeD);
	otherDepottotalsMap.put("mgpsServiceCharge",totalSubAmountD);
	otherDepotList.add(otherDepottotalsMap);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSubAmountD)
	
	context.totalReimbursementAmount=twoDForm.format(totalReimbursementAmount)
	
}


context.silkDepotList=silkDepotList
  
context.cottonDepotList=cottonDepotList

context.juteDepotList=juteDepotList

context.otherDepotList=otherDepotList








