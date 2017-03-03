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
branchId = parameters.branchId2;
productCategory=parameters.productCategory;
reportType=parameters.reportType;
context.reportType=reportType;
context.partyfromDate=partyfromDate;
context.partythruDate=partythruDate;

productCategoryDetails = delegator.findOne("ProductCategory",[productCategoryId : productCategory] , false);
if(UtilValidate.isNotEmpty(productCategoryDetails)){
	prodCatName=productCategoryDetails.description
}else if(productCategory == "ALL"){
	prodCatName="ALL CATEGORIES"
	}else{
 	prodCatName="PRODUCTS OTHER THAN SILK AND JUTE"
}

context.prodCatName=prodCatName;
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

condListCat = [];
if(productCategory == "ALL"){
	productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId" ,EntityOperator.EQUALS,"NATURAL_FIBERS"), null, null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
	
	productPrimaryCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.IN,productCategoryIds), null, null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productPrimaryCategories, "productCategoryId", true);
}else if(productCategory == "OTHER"){
	productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition([EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "NATURAL_FIBERS"), EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_IN, UtilMisc.toList("COTTON","SILK"))], EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
	
	productPrimaryCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.IN,productCategoryIds), null, null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productPrimaryCategories, "productCategoryId", true);
}else{
	productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.EQUALS,productCategory), UtilMisc.toSet("productCategoryId","primaryParentCategoryId"), null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
	}

condListCat.clear();
condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);

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

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
  
fromDateForCSV=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForCSV=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");
context.fromDateForCSV=fromDateForCSV;
context.thruDateForCSV=thruDateForCSV;

branchContext=[:];
branchContext.put("branchId",branchId);

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
StateTotals=[:];
stylesMap=[:];
if(branchId){
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", BOAddress);
	stylesMap.put("mainHeader3", "DEPOT REIMBURSMENT SUMMARY REPORT");
	stylesMap.put("mainHeader4", "From "+ fromDateForCSV +" to "+thruDateForCSV);
}
else{
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", "DEPOT REIMBURSMENT SUMMARY REPORT");
	stylesMap.put("mainHeader3", "From "+ fromDateForCSV +" to "+thruDateForCSV);
}
stylesMap.put("mainHeaderFontName","Arial");
stylesMap.put("mainHeadercellHeight",300);
stylesMap.put("mainHeaderFontSize",10);
stylesMap.put("mainHeadingCell",1);
stylesMap.put("mainHeaderBold",true);
stylesMap.put("columnHeaderBgColor",false);
stylesMap.put("columnHeaderFontName","Arial");
stylesMap.put("columnHeaderFontSize",10);
stylesMap.put("autoSizeCell",true);
stylesMap.put("columnHeaderCellHeight",300);
request.setAttribute("stylesMap", stylesMap);
request.setAttribute("enableStyles", true);

headingMap=[:];
headingMap.put("partyId", "State");
headingMap.put("partyName", "Customer");
headingMap.put("invoiceQTY", "Quantity Supplied in KGS");
headingMap.put("invoiceAMT", "Value Of Yarn Supplied RS");
headingMap.put("shippingCost", "Actual Cost Of Transportation");
headingMap.put("reimbursentAMT", "Actual Transportation Eligible For Reimbursment");
headingMap.put("depotCharges", "Depot Charges At 2.5%");

finalCSVList.add(stylesMap);
finalCSVList.add(headingMap);

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
	
	stateMap=[:];
	
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
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, stateParties));
	if(UtilValidate.isNotEmpty(branchId) && !"ALL".equals(branchId)){
		//This block is to get Ro related branches 
		innerConditionList=[];
		innerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
		innerConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
		stateRoBranchList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(innerConditionList, EntityOperator.AND),UtilMisc.toSet("partyIdFrom","partyIdTo"), null, null, false);
		if(UtilValidate.isNotEmpty(stateRoBranchList)){
			stateRoBranchs = EntityUtil.getFieldListFromEntityList(stateRoBranchList, "partyIdTo", true);
			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, stateRoBranchs));
		}else{
			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
		}	
		
	} 
	
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	stateBranchList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdFrom","partyIdTo"), null, null, false);
	stateBranchs = EntityUtil.getFieldListFromEntityList(stateBranchList, "partyIdFrom", true);
	
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
	stateBranchWisePartiesMapData=[:];
	sr=1;
	stateTotalMap=[:];
	double totStateinvoiceAMT = 0;
	double totStateinvoiceQTY = 0;
	double totStateshippingCost = 0;
	double totStatereimbursentAMT = 0;
	double totStatedepotCharges = 0;
	
	for(eachBranch in stateBranchs){
		
		String branchName = PartyHelper.getPartyName(delegator,eachBranch,false);
		
		tempCSVMap1.put("partyId", stateName);
		tempCSVMap1.put("partyName", "--"+branchName);
		tempCSVMap1.put("invoiceAMT", "_");
		tempCSVMap1.put("invoiceQTY",  "_");
		tempCSVMap1.put("shippingCost",  "_");
		tempCSVMap1.put("reimbursentAMT",  "_");
		tempCSVMap1.put("depotCharges",  "_");
		finalCSVList.add(tempCSVMap1);
		
		statePartiesListData=[];
		eachBranchPartiesList =EntityUtil.filterByCondition(stateBranchList, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,eachBranch));
		stateParties=EntityUtil.getFieldListFromEntityList(eachBranchPartiesList, "partyIdTo", true);
		totMap=[:];
		double totBranchinvoiceAMT = 0;
		double totBranchinvoiceQTY = 0;
		double totBranchshippingCost = 0;
		double totBranchreimbursentAMT = 0;
		double totBranchdepotCharges = 0;
		
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
					conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE")));
					InvoiceItem = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
					if(InvoiceItem){
						double eachInvoiceAMT = 0;
						for (eachInvoiceItem in InvoiceItem) {
							productId =eachInvoiceItem.productId
							invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
							eachInvoiceAMT=eachInvoiceAMT+(eachInvoiceItem.itemValue);
							invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
							totStateinvoiceAMT=totStateinvoiceAMT+eachInvoiceItem.itemValue;
							totStateinvoiceQTY=totStateinvoiceQTY+eachInvoiceItem.quantity;
							totBranchinvoiceAMT=totBranchinvoiceAMT+eachInvoiceItem.itemValue
							totBranchinvoiceQTY=totBranchinvoiceQTY+eachInvoiceItem.quantity
							
						}
						double maxAmt = 0;
						if(invoiceQTY && invoiceQTY>0){
							if(productCategory.equals("SILK")){
								maxAmt = (eachInvoiceAMT*1)/100;
							}else if(productCategory.equals("JUTE_YARN")){
								maxAmt = (eachInvoiceAMT*10)/100;
							}else{
								maxAmt = (eachInvoiceAMT*2.5)/100;
							}
						}
						shipmentId = eachInvoice.shipmentId;
						if(shipmentId){
							 shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
							 if(UtilValidate.isNotEmpty(shipmentList) && UtilValidate.isNotEmpty(shipmentList.estimatedShipCost)){
								 shippingCost=shippingCost+shipmentList.estimatedShipCost
								 totStateshippingCost=totStateshippingCost+shipmentList.estimatedShipCost;
								 totBranchshippingCost=totBranchshippingCost+shipmentList.estimatedShipCost;
								 estimatedShipCost=shipmentList.estimatedShipCost;
								 if(estimatedShipCost && maxAmt > estimatedShipCost){
									 reimbursentAMT=reimbursentAMT+estimatedShipCost;
									 totStatereimbursentAMT=totStatereimbursentAMT+estimatedShipCost
									 totBranchreimbursentAMT=totBranchreimbursentAMT+estimatedShipCost
								 }else{
									  reimbursentAMT=reimbursentAMT+maxAmt;
									  totStatereimbursentAMT=totStatereimbursentAMT+maxAmt
									  totBranchreimbursentAMT=totBranchreimbursentAMT+maxAmt
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
				totStatedepotCharges=totStatedepotCharges+depotCharges;
				totBranchdepotCharges=totBranchdepotCharges+depotCharges;
			}
			if(invoiceAMT>0 ||shippingCost>0 || reimbursentAMT>0){
				statePartiesListData.add(tempMap);
				finalCSVList.add(tempMap);
				sr=sr+1;
			}
		}
		totMap.put("partyId","TOTAL");
		totMap.put("partyName", "BRANCH SUB-TOTAL");
		totMap.put("invoiceAMT",totBranchinvoiceAMT);
		totMap.put("invoiceQTY",totBranchinvoiceQTY);
		totMap.put("shippingCost",totBranchshippingCost);
		totMap.put("reimbursentAMT",totBranchreimbursentAMT);
		totMap.put("depotCharges",totBranchdepotCharges);
		if(totBranchinvoiceAMT>0)
		statePartiesListData.add(totMap);
		finalCSVList.add(totMap);
		if(UtilValidate.isNotEmpty(statePartiesListData)){
			stateBranchWisePartiesMapData.put(branchName, statePartiesListData);
		}
	}

	stateTotalMap.put("partyId","TOTAL");
	stateTotalMap.put("partyName", "STATE SUB-TOTAL");
	stateTotalMap.put("invoiceAMT",totStateinvoiceAMT);
	stateTotalMap.put("invoiceQTY",totStateinvoiceQTY);
	stateTotalMap.put("shippingCost",totStateshippingCost);
	stateTotalMap.put("reimbursentAMT",totStatereimbursentAMT);
	stateTotalMap.put("depotCharges",totStatedepotCharges);
	
	finalCSVList.add(stateTotalMap);
	stateMap.put(stateName, stateBranchWisePartiesMapData);
	finalList.add(stateMap);
	StateTotals.put(stateName, stateTotalMap);
}
context.finalList = finalList;
context.finalCSVList=finalCSVList;
context.StateTotals=StateTotals;









