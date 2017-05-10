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
hydRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2.5,"COIR_YARN",10, "OTHER",2.5,"serCharge",2);
cmbRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
kolRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
kanRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
vjyRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
panRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
gwhRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1.5,"COTTON",5,"COIR_YARN",10, "OTHER",5,"serCharge",1.25);
varRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
bhuRoMap=UtilMisc.toMap("JUTE_YARN",10,"SILK",1,"COTTON",2,"COIR_YARN",10, "OTHER",2,"serCharge",3);
rowiseTsPercentageMap= UtilMisc.toMap("INT1",varRoMap,"INT2",panRoMap,"INT3",kolRoMap,"INT4",cmbRoMap,"INT5",hydRoMap,"INT6",kanRoMap,"INT26",bhuRoMap,"INT28",gwhRoMap,"INT47",vjyRoMap);

rounding = RoundingMode.HALF_UP;

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
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

finalList=[];
if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
	
	stylesMap=[:];   //stylesMap
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");  //mainHeader
	stylesMap.put("mainHeader2", "STATEMENT FOR CLAIMING REIMBURSEMENT OF THE COST OF TRANSPORTATION AND OVERHEADS TOWARDS");
	stylesMap.put("mainHeader3", "THE QUANTUM OF YARN SUPPLIED UNDER MILL GATE PRICE SCHEME FOR THE PERIOD");
	stylesMap.put("mainHeader4", "TRANSPORTATION AND DEPOT EXPENSES FOR THE PERIOD FROM "+ claimFromDate +" TO "+claimThruDate);
	stylesMap.put("mainHeadercellHeight",300);  //mainHeadercellHeight
	stylesMap.put("mainHeaderfontName","Arial");  //URW Chancery L
	stylesMap.put("mainHeaderFontSize",10);
	stylesMap.put("mainHeadingCell",4);
	stylesMap.put("mainHeaderBold",true);
	stylesMap.put("columnHeaderBgColor",false);  //column_header
	stylesMap.put("columnHeaderFontName","Arial");
	stylesMap.put("columnHeaderFontSize",10);
	stylesMap.put("autoSizeCell",true);
	stylesMap.put("columnHeaderCellHeight",300);//columnHeaderCellHeight
	
	request.setAttribute("stylesMap", stylesMap);
	request.setAttribute("enableStyles", true);
	
	headingMap=[:];
	headingMap.put("sNo", "SNO");
	headingMap.put("partyName", "NAME OF USER AGENCIES");
	headingMap.put("totInvQty", "QUANTITY SUPPLEID IN Kg");
	headingMap.put("totInvValue", "VALUE OF YARN SUPPLIED Rs");
	headingMap.put("actualFrightCharges","YARN SUBSIDY @10% ON YARN VALUE BEFORE SUBSIDY(IN RS)");
	headingMap.put("frightCharges","SERVICE CHARGES @0.5% OF YARN VALUE BEFORE SUBSIDY(IN RS)");
	headingMap.put("depotCharges","DEPOT  Charges");
	headingMap.put("mgpsServiceCharge","TOTAL CLAIM FOR YARN SUBSIDY AND SERVICE CHARGES (IN RS)");
	
	finalList.add(stylesMap);
	finalList.add(headingMap);
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
	indexD=1;
	indexND=1
	BigDecimal totalReimbursementAmount = BigDecimal.ZERO;
	BigDecimal advanceAmount = BigDecimal.ZERO;
	BigDecimal balanceAmount = BigDecimal.ZERO;
	
	duplicateInvoiceIds=[];
	DecimalFormat twoDForm = new DecimalFormat("#.##");
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
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	mgpsInvoiceIds=result.getAt("mgpsInvoiceIds")
	
	tenPerAndMgpsInvoiceIds=[];
	tenPerAndMgpsInvoiceIds.addAll(tenPerInvoiceIds)
	tenPerAndMgpsInvoiceIds.addAll(mgpsInvoiceIds)
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue","invoiceGrandTotal"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	invoiceIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"invoiceId", true);
	
	tempMapSD=[:]
	tempMapSND=[:]
	tempMapCD=[:]
	tempMapCND=[:]
	tempMapJD=[:]
	tempMapJND=[:]
	tempMapOD=[:]
	tempMapOND=[:]
	for(invoiceId in invoiceIds)
	{
		String partyName = "";
		String partyId="";
		silkinvoicesAndItems1 = EntityUtil.filterByCondition(silkinvoicesAndItems, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,invoiceId));
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
			partyId=invoice.partyId
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				}
				
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue) );
		tempMap.put("actualFrightCharges", twoDForm.format(actualFrightCharges) );
		tempMap.put("frightCharges", twoDForm.format(eligibleFrightCharges));
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyId), UtilMisc.toSet("facilityId"), null, null, false );
		
		if(UtilValidate.isNotEmpty(facility)){
			
			tempMap.put("sNo", indexD);
			tempMap.put("depotCharges", twoDForm.format(depotCharges) );
			silkDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			indexD=indexD+1
			
		}else{
			tempMap.put("sNo", indexND);
			tempMap.put("depotCharges", BigDecimal.ZERO);
			silkNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
			indexND=indexND+1
		}
	}
	duplicateInvoiceIds.clear()
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
	
	tempMapSD.put("partyName","Silk Depot");
	finalList.add(tempMapSD);
	finalList.addAll(silkDepotList);
	tempMapSND.put("partyName","Silk Non Depot");
	finalList.add(tempMapSND);
	finalList.addAll(silkNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,cottonProdIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue","invoiceGrandTotal"] as Set;
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	invoiceIds=EntityUtil.getFieldListFromEntityList(cottonInvoicesAndItems,"invoiceId", true);
	
	for(invoiceId in invoiceIds)
	{
		String partyName = "";
		String partyId="";
		cottonInvoicesAndItems1 = EntityUtil.filterByCondition(cottonInvoicesAndItems, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,invoiceId));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0;
		
		for(invoice in cottonInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			partyId=invoice.partyId
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				}
				
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue) );
		tempMap.put("actualFrightCharges", twoDForm.format(actualFrightCharges) );
		tempMap.put("frightCharges", twoDForm.format(eligibleFrightCharges));
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyId), UtilMisc.toSet("facilityId"), null, null, false );
		
		if(UtilValidate.isNotEmpty(facility)){
			
			tempMap.put("sNo", indexD);
			tempMap.put("depotCharges", twoDForm.format(depotCharges) );
			cottonDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			indexD=indexD+1
			
		}else{
			tempMap.put("sNo", indexND);
			tempMap.put("depotCharges", BigDecimal.ZERO);
			cottonNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
			indexND=indexND+1
		}
	}
	duplicateInvoiceIds.clear()
	
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
	
	tempMapCD.put("partyName","Cotton Depot");
	finalList.add(tempMapCD);
	finalList.addAll(cottonDepotList);
	tempMapCND.put("partyName","Cotton Non Depot");
	finalList.add(tempMapCND);
	finalList.addAll(cottonNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue"] as Set;
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	invoiceIds=EntityUtil.getFieldListFromEntityList(juteInvoicesAndItems,"invoiceId", true);
	
	for(invoiceId in invoiceIds)
	{
		String partyName = "";
		String partyId="";
		juteInvoicesAndItems1 = EntityUtil.filterByCondition(juteInvoicesAndItems, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,invoiceId));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0;
		
		for(invoice in juteInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			partyId=invoice.partyId
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				}
				
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue) );
		tempMap.put("actualFrightCharges", twoDForm.format(actualFrightCharges) );
		tempMap.put("frightCharges", twoDForm.format(eligibleFrightCharges));
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyId), UtilMisc.toSet("facilityId"), null, null, false );
		
		if(UtilValidate.isNotEmpty(facility)){
			
			tempMap.put("sNo", indexD);
			tempMap.put("depotCharges", twoDForm.format(depotCharges) );
			juteDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			indexD=indexD+1
			
		}else{
			tempMap.put("sNo", indexND);
			tempMap.put("depotCharges", BigDecimal.ZERO);
			juteNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
			indexND=indexND+1
		}
	}
	duplicateInvoiceIds.clear()
	
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
	
	tempMapJD.put("partyName","Jute Depot");
	finalList.add(tempMapJD);
	finalList.addAll(juteDepotList);
	tempMapJND.put("partyName","Jute Non Depot");
	finalList.add(tempMapJND);
	finalList.addAll(juteNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue"] as Set;
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	invoiceIds=EntityUtil.getFieldListFromEntityList(otherInvoicesAndItems,"invoiceId", true);
	
	for(invoiceId in invoiceIds)
	{
		String partyName = "";
		String partyId="";
		otherInvoicesAndItems1 = EntityUtil.filterByCondition(otherInvoicesAndItems, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS,invoiceId));
		tempMap=[:]
		BigDecimal invoiceQty = BigDecimal.ZERO;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal actualFrightCharges = BigDecimal.ZERO;
		BigDecimal eligibleFrightCharges = BigDecimal.ZERO;
		BigDecimal depotCharges = BigDecimal.ZERO;
		BigDecimal mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0;
		
		for(invoice in otherInvoicesAndItems1)
		{
			partyName=PartyHelper.getPartyName(delegator,invoice.partyId,false);
			partyId=invoice.partyId
			invoiceQty=invoiceQty.add(invoice.quantity);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				}
				
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue) );
		tempMap.put("actualFrightCharges", twoDForm.format(actualFrightCharges) );
		tempMap.put("frightCharges", twoDForm.format(eligibleFrightCharges));
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyId), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			
			tempMap.put("sNo", indexD);
			tempMap.put("depotCharges", twoDForm.format(depotCharges) );
			otherDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			indexD=indexD+1
			
		}else{
			tempMap.put("sNo", indexND);
			tempMap.put("depotCharges", BigDecimal.ZERO);
			otherNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
			indexND=indexND+1
		}
	}
	duplicateInvoiceIds.clear()
	
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
	
	tempMapOD.put("partyName","Other Depot");
	finalList.add(tempMapOD);
	finalList.addAll(otherDepotList);
	tempMapOND.put("partyName","Other Non Depot");
	finalList.add(tempMapOND);
	finalList.addAll(otherNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
	context.totalReimbursementAmount=totalReimbursementAmount
	context.advanceAmount=advanceAmount
	context.balanceAmount=balanceAmount
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

def getInvocieAmount(invoiceId)
{
	BigDecimal invoiceAmount = BigDecimal.ZERO; 
	innerCondition=[];
	innerCondition.add(EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId));
	innerCondition.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_EQUAL,"TEN_PERCENT_SUBSIDY"));
	innerCondition.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "ROUNDING_ADJUSTMENT"));
	innerCondition.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
	fieldsToSelect = ["itemValue"] as Set;
	invoiceItems = delegator.findList("InvoiceItem",EntityCondition.makeCondition(innerCondition, EntityOperator.AND), fieldsToSelect, null, null, false );
	
	for(eachItem in invoiceItems)
	{
		if(eachItem.itemValue!=null)
		invoiceAmount=invoiceAmount.add(eachItem.itemValue)
		
	}
	return invoiceAmount;
}


def generatePartyWiseReport()
{
	indexD=1;
	indexND=1;
	BigDecimal totalReimbursementAmount = BigDecimal.ZERO;
	BigDecimal advanceAmount = BigDecimal.ZERO;
	BigDecimal balanceAmount = BigDecimal.ZERO;
	duplicateInvoiceIds=[];
	DecimalFormat twoDForm = new DecimalFormat("#.##");
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
	  
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	mgpsInvoiceIds=result.getAt("mgpsInvoiceIds")
	
	tenPerAndMgpsInvoiceIds=[];
	tenPerAndMgpsInvoiceIds.addAll(tenPerInvoiceIds)
	tenPerAndMgpsInvoiceIds.addAll(mgpsInvoiceIds)
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue","invoiceGrandTotal"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	silkPartyIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"partyId", true);
	
	tempMapSD=[:]
	tempMapSND=[:]
	tempMapCD=[:]
	tempMapCND=[:]
	tempMapJD=[:]
	tempMapJND=[:]
	tempMapOD=[:]
	tempMapOND=[:]
	
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				serviceChrgPercentage=roPercentagesMap.get("serCharge")
				mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue) );
		tempMap.put("actualFrightCharges", twoDForm.format(actualFrightCharges) );
        tempMap.put("frightCharges", twoDForm.format(eligibleFrightCharges));
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		
		if(UtilValidate.isNotEmpty(facility)){
			
			tempMap.put("sNo", indexD);
			mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
			tempMap.put("depotCharges", twoDForm.format(depotCharges) );
			silkDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			indexD=indexD+1
			
		}else{
		    tempMap.put("sNo", indexND);
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
			tempMap.put("depotCharges", BigDecimal.ZERO);
			silkNonDepotList.add(tempMap);
			
			totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
			totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
			totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
			totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
			totalDepotChargesND=totalDepotChargesND.add(0);
			totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
			indexND=indexND+1
		}
	}
	duplicateInvoiceIds.clear()
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
	
	tempMapSD.put("partyName","Silk Depot");
	finalList.add(tempMapSD);
	finalList.addAll(silkDepotList);
	tempMapSND.put("partyName","Silk Non Depot");
	finalList.add(tempMapSND);
	finalList.addAll(silkNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue"] as Set;
	cottonInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				
				serviceChrgPercentage=roPercentagesMap.get("serCharge")
				mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", twoDForm.format(invoiceQty));
		tempMap.put("totInvValue", twoDForm.format(invoiceValue));
		tempMap.put("actualFrightCharges", twoDForm.format(actualFrightCharges));
		tempMap.put("frightCharges", twoDForm.format(eligibleFrightCharges));
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
			tempMap.put("depotCharges", depotCharges);
			cottonDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
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
	duplicateInvoiceIds.clear()
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
	
	tempMapCD.put("partyName","Cotton Depot");
	finalList.add(tempMapCD);
	finalList.addAll(cottonDepotList);
	tempMapCND.put("partyName","Cotton Non Depot");
	finalList.add(tempMapCND);
	finalList.addAll(cottonNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue"] as Set;
	juteInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				serviceChrgPercentage=roPercentagesMap.get("serCharge")
				mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", invoiceValue);
		tempMap.put("actualFrightCharges", actualFrightCharges);
		tempMap.put("frightCharges", eligibleFrightCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
			tempMap.put("depotCharges", depotCharges);
			juteDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
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
	duplicateInvoiceIds.clear()
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
	
	tempMapJD.put("partyName","Jute Depot");
	finalList.add(tempMapJD);
	finalList.addAll(juteDepotList);
	tempMapJND.put("partyName","Jute Non Depot");
	finalList.add(tempMapJND);
	finalList.addAll(juteNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId","itemValue"] as Set;
	otherInvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				serviceChrgPercentage=roPercentagesMap.get("serCharge")
				mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		
		tempMap.put("sNo", index);
		tempMap.put("partyName", partyName);
		tempMap.put("totInvQty", invoiceQty);
		tempMap.put("totInvValue", invoiceValue);
		tempMap.put("actualFrightCharges", actualFrightCharges);
		tempMap.put("frightCharges", eligibleFrightCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		
		facility = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachParty), UtilMisc.toSet("facilityId"), null, null, false );
		if(UtilValidate.isNotEmpty(facility)){
			mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
			tempMap.put("depotCharges", depotCharges);
			otherDepotList.add(tempMap);
			
			totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
			totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
			totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
			totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
			totalDepotChargesD=totalDepotChargesD.add(depotCharges);
			totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
			
		}else{
			tempMap.put("mgpsServiceCharge", twoDForm.format(mgpsServiceCharge) );
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
	duplicateInvoiceIds.clear()
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
	
	tempMapOD.put("partyName","Other Depot");
	finalList.add(tempMapOD);
	finalList.addAll(otherDepotList);
	tempMapOND.put("partyName","Other Non Depot");
	finalList.add(tempMapOND);
	finalList.addAll(otherNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
	context.totalReimbursementAmount=totalReimbursementAmount
	context.advanceAmount=advanceAmount
	context.balanceAmount=balanceAmount
	
}






def generateSummaryReport(stateGeoIds)
{
	index=1;
	BigDecimal totalReimbursementAmount = BigDecimal.ZERO;
	BigDecimal advanceAmount = BigDecimal.ZERO;
	BigDecimal balanceAmount = BigDecimal.ZERO;
	
	duplicateInvoiceIds=[];
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
	
	result=getMgpsAnd10PerInvoiceIdForPeriod(dayBegin,dayEnd);
	
	tenPerInvoiceIds=result.getAt("tenPerInvoiceIds")
	mgpsInvoiceIds=result.getAt("mgpsInvoiceIds")
	
	tenPerAndMgpsInvoiceIds=[];
	tenPerAndMgpsInvoiceIds.addAll(tenPerInvoiceIds)
	tenPerAndMgpsInvoiceIds.addAll(mgpsInvoiceIds)
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin))
	conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd))
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"INVOICE_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS,"INV_FPROD_ITEM"));
	conditionList.add(EntityCondition.makeCondition("costCenterId",EntityOperator.IN,branchIds))
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,silkProdIds));
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
	fieldsToSelect = ["invoiceId","partyIdFrom","partyId","shipmentId","quantity","productId","costCenterId"] as Set;
	silkinvoicesAndItems = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), fieldsToSelect, null, null, false );
	silkPartyIds=EntityUtil.getFieldListFromEntityList(silkinvoicesAndItems,"partyId", true);
	
	tempMapSD=[:]
	tempMapSND=[:]
	tempMapCD=[:]
	tempMapCND=[:]
	tempMapJD=[:]
	tempMapJND=[:]
	tempMapOD=[:]
	tempMapOND=[:]
	
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId)
			}
		}
		duplicateInvoiceIds.clear()
		
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		tempMapD.put("depotCharges", depotCharges);
		if(invoiceQty>0){
			silkDepotList.add(tempMapD);
		}
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId)
			}
		}
		duplicateInvoiceIds.clear()
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		tempMapND.put("depotCharges", depotCharges);
		
		if(invoiceQty>0){
			silkNonDepotList.add(tempMapND);
		}
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
		index=index+1;
		
	}
	
	duplicateInvoiceIds.clear();
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
	
	tempMapSD.put("partyName","Silk Depot");
	finalList.add(tempMapSD);
	finalList.addAll(silkDepotList);
	tempMapSND.put("partyName","Silk Non Depot");
	finalList.add(tempMapSND);
	finalList.addAll(silkNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
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
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateCottonPartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateCottonPartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		cottoninvoicesAndItems1D = EntityUtil.filterByCondition(stateCottoninvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateCottonPartyIdsD));
		cottoninvoicesAndItems1ND = EntityUtil.filterOutByCondition(stateCottoninvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateCottonPartyIdsD));
		
		for(invoice in cottoninvoicesAndItems1D)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
			
			
		}
		duplicateInvoiceIds.clear()
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("depotCharges", depotCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		cottonDepotList.add(tempMapD);
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
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
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		duplicateInvoiceIds.clear();
		
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("depotCharges", depotCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		cottonNonDepotList.add(tempMapND);
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
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
	
	tempMapCD.put("partyName","Cotton Depot");
	finalList.add(tempMapCD);
	finalList.addAll(cottonDepotList);
	tempMapCND.put("partyName","Cotton Non Depot");
	finalList.add(tempMapCND);
	finalList.addAll(cottonNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
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
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateJutePartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateJutePartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		juteInvoicesAndItems1D = EntityUtil.filterByCondition(stateJuteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateJutePartyIdsD));
		juteInvoicesAndItems1ND = EntityUtil.filterOutByCondition(stateJuteInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateJutePartyIdsD));
		
		
		for(invoice in juteInvoicesAndItems1D)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		duplicateInvoiceIds.clear();
		
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("depotCharges", depotCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		juteDepotList.add(tempMapD);
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
		invoiceQty = BigDecimal.ZERO;
		invoiceValue = BigDecimal.ZERO;
		actualFrightCharges = BigDecimal.ZERO;
		eligibleFrightCharges = BigDecimal.ZERO;
		depotCharges = BigDecimal.ZERO;
		mgpsServiceCharge = BigDecimal.ZERO;
		
		for(invoice in juteInvoicesAndItems1ND)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
			}
		}
		duplicateInvoiceIds.clear();
		
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("depotCharges", depotCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		juteNonDepotList.add(tempMapND);
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
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
	
	tempMapJD.put("partyName","Jute Depot");
	finalList.add(tempMapJD);
	finalList.addAll(juteDepotList);
	tempMapJND.put("partyName","Jute Non Depot");
	finalList.add(tempMapJND);
	finalList.addAll(juteNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
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
	conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN,tenPerAndMgpsInvoiceIds));
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
		facilitys = delegator.findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,eachStateOtherPartyIds), UtilMisc.toSet("ownerPartyId"), null, null, false );
		eachStateOtherPartyIdsD=EntityUtil.getFieldListFromEntityList(facilitys,"ownerPartyId", true);
		otherInvoicesAndItems1D = EntityUtil.filterByCondition(stateOtherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateOtherPartyIdsD));
		otherInvoicesAndItems1ND = EntityUtil.filterOutByCondition(stateOtherInvoicesAndItems, EntityCondition.makeCondition("partyId", EntityOperator.IN,eachStateOtherPartyIdsD));
		
		for(invoice in otherInvoicesAndItems1D)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
			//	if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
			//	}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				depotCharges=depotCharges.add((invoiceAmount.multiply(2)).divide(100))
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
		     }
		}
		duplicateInvoiceIds.clear();
		
		
		tempMapD.put("sNo", index);
		tempMapD.put("partyName", stateName);
		tempMapD.put("totInvQty", invoiceQty);
		tempMapD.put("totInvValue", invoiceValue);
		tempMapD.put("actualFrightCharges", actualFrightCharges);
		tempMapD.put("frightCharges", eligibleFrightCharges);
		tempMapD.put("depotCharges", depotCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapD.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		otherDepotList.add(tempMapD)
		
		totalInvoiceQtyD=totalInvoiceQtyD.add(invoiceQty)
		totalInvoiceValueD=totalInvoiceValueD.add(invoiceValue)
		totalActualFrightChargesD=totalActualFrightChargesD.add(actualFrightCharges)
		totalFrightChargesD=totalFrightChargesD.add(eligibleFrightCharges);
		totalDepotChargesD=totalDepotChargesD.add(depotCharges);
		totalSerChargesD=totalSerChargesD.add(mgpsServiceCharge);
		
		invoiceQty = BigDecimal.ZERO;
		invoiceValue = BigDecimal.ZERO;
		actualFrightCharges = BigDecimal.ZERO;
		eligibleFrightCharges = BigDecimal.ZERO;
		depotCharges = BigDecimal.ZERO;
		mgpsServiceCharge = BigDecimal.ZERO;
		schemePercentage=0
		for(invoice in otherInvoicesAndItems1ND)
		{
			invoiceQty=invoiceQty.add(invoice.quantity);
			//invoiceAmount = InvoiceWorker.getInvoiceTotal(delegator,invoice.invoiceId);
			invoiceAmount=getInvocieAmount(invoice.invoiceId)
			
			shipment = delegator.findOne("Shipment",[shipmentId : invoice.shipmentId] , false);
			actulFrgtAmt=0
			if(UtilValidate.isNotEmpty(shipment.estimatedShipCost)){
				actulFrgtAmt=shipment.estimatedShipCost
			}
			if(!duplicateInvoiceIds.contains(invoice.invoiceId)){
				actualFrightCharges=actualFrightCharges.add(actulFrgtAmt)
				product = delegator.findOne("Product",[productId : invoice.productId] , false);
				productCategory = delegator.findOne("ProductCategory",[productCategoryId : product.primaryProductCategoryId] , false);
				roPercentagesMap=rowiseTsPercentageMap.get(invoice.partyIdFrom)
				schemePercentage=roPercentagesMap.get(productCategory.primaryParentCategoryId)
				if(schemePercentage==null){
					schemePercentage=roPercentagesMap.get("OTHER")
				}
				//if(mgpsInvoiceIds.contains(invoice.invoiceId)){
					serviceChrgPercentage=roPercentagesMap.get("serCharge")
					mgpsServiceCharge=mgpsServiceCharge.add((invoiceAmount.multiply(serviceChrgPercentage)).divide(100))
				//}
				eligFrgtAmt=(invoiceAmount.multiply(schemePercentage)).divide(100);
				if(actulFrgtAmt>=eligFrgtAmt){
					eligibleFrightCharges=eligibleFrightCharges.add(eligFrgtAmt);
				}else{
					eligibleFrightCharges=eligibleFrightCharges.add(actulFrgtAmt);
				}
				invoiceValue=invoiceValue.add(invoiceAmount);
				duplicateInvoiceIds.add(invoice.invoiceId);
		    }
		}
		duplicateInvoiceIds.clear()
		
		tempMapND.put("sNo", index);
		tempMapND.put("partyName", stateName);
		tempMapND.put("totInvQty", invoiceQty);
		tempMapND.put("totInvValue", invoiceValue);
		tempMapND.put("actualFrightCharges", actualFrightCharges);
		tempMapND.put("frightCharges", eligibleFrightCharges);
		tempMapND.put("depotCharges", depotCharges);
		mgpsServiceCharge=mgpsServiceCharge.add(eligibleFrightCharges)
		mgpsServiceCharge=mgpsServiceCharge.add(depotCharges)
		tempMapND.put("mgpsServiceCharge", mgpsServiceCharge);
		if(invoiceQty>0)
		otherNonDepotList.add(tempMapND)
		
		totalInvoiceQtyND=totalInvoiceQtyND.add(invoiceQty)
		totalInvoiceValueND=totalInvoiceValueND.add(invoiceValue)
		totalActualFrightChargesND=totalActualFrightChargesND.add(actualFrightCharges)
		totalFrightChargesND=totalFrightChargesND.add(eligibleFrightCharges);
		totalDepotChargesND=totalDepotChargesND.add(depotCharges);
		totalSerChargesND=totalSerChargesND.add(mgpsServiceCharge);
		
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
	
	tempMapOD.put("partyName","Other Depot");
	finalList.add(tempMapOD);
	finalList.addAll(otherDepotList);
	tempMapOND.put("partyName","Other Non Depot");
	finalList.add(tempMapOND);
	finalList.addAll(otherNonDepotList);
	
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesD)
	totalReimbursementAmount=totalReimbursementAmount.add(totalSerChargesND)
	
	context.totalReimbursementAmount=totalReimbursementAmount
	context.advanceAmount=advanceAmount
	context.balanceAmount=balanceAmount
}

context.finalList=finalList;
context.silkDepotList=silkDepotList
context.silkNonDepotList=silkNonDepotList
  
context.cottonDepotList=cottonDepotList  
context.cottonNonDepotList=cottonNonDepotList

context.juteDepotList=juteDepotList
context.juteNonDepotList=juteNonDepotList

context.otherDepotList=otherDepotList
context.otherNonDepotList=otherNonDepotList









