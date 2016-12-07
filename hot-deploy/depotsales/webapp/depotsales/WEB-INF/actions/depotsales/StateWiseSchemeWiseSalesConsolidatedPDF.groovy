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
branchId = parameters.SWSWSCbranchId;
productCategory=parameters.productCategory;
reportType=parameters.reportType;
context.reportType=reportType;
context.partyfromDate=partyfromDate;
context.partythruDate=partythruDate;

productCategoryDetails = delegator.findOne("ProductCategory",[productCategoryId : productCategory] , false);
if(UtilValidate.isNotEmpty(productCategoryDetails)){
	prodCatName=productCategoryDetails.description
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
// block of code for getting product category wise transportation percentage reimbursement ===============================
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.IN, ["SILK","JUTE_YARN","COTTON"]));
produtCategoriesForPer = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productCategoryId"), null, null, false);
allCategories = EntityUtil.getFieldListFromEntityList(produtCategoriesForPer, "productCategoryId", true);

silkCategories =EntityUtil.filterByCondition(produtCategoriesForPer, EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS,"SILK"));

juteCategories =EntityUtil.filterByCondition(statesList, EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS,"JUTE_YARN"));

otherAllCategories =EntityUtil.filterByCondition(statesList, EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN,["SILK","JUTE_YARN"]));

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, allCategories));
produtCategorieMemberForPer = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId","productCategoryId"), null, null, false);

silkCategoriesProducts =EntityUtil.filterByCondition(produtCategorieMemberForPer, EntityCondition.makeCondition("productCategoryId", EntityOperator.IN,silkCategories));

juteCategoriesProducts =EntityUtil.filterByCondition(produtCategorieMemberForPer, EntityCondition.makeCondition("productCategoryId", EntityOperator.IN,juteCategories));

otherAllCategoriesProducts =EntityUtil.filterByCondition(produtCategorieMemberForPer, EntityCondition.makeCondition("productCategoryId", EntityOperator.IN,otherAllCategories));

// block end ================================

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
finalCSVList2=[];
context.finalCSVList2=finalCSVList2;
finalList = [];
finalCSVList=[];
StateTotals=[:];
csvHeaderMap=[:];

csvHeaderMap.put("partyName","STATE");
csvHeaderMap.put("totInvoiceAMT", "TOTAL");
csvHeaderMap.put("totInvoiceQTY","_");
csvHeaderMap.put("generalInvoiceAMT","GENERAL SCHEME");
csvHeaderMap.put("generalInvoiceQTY","_");

csvHeaderMap.put("mgpsTenPerDepotInvoiceAMT","MGP 10% Scheme(Depot)");
csvHeaderMap.put("mgpsTenPerDepotInvoiceQTY","_");
csvHeaderMap.put("mgpsTenPerInvoiceAMT","MGP 10% Scheme(Non Depot)");
csvHeaderMap.put("mgpsTenPerInvoiceQTY","_");

csvHeaderMap.put("mgpsDepotInvoiceAMT","MGP Scheme(Depot)");
csvHeaderMap.put("mgpsDepotInvoiceQTY","_");

csvHeaderMap.put("mgpsInvoiceAMT","MGP Scheme(Non Depot)");
csvHeaderMap.put("mgpsInvoiceQTY","_");

//finalCSVList.add(csvHeaderMap);

for(state in indianStates){
	
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
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	if(stateParties)
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,stateParties));
	FacilityList = delegator.find("Facility", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("ownerPartyId"), null, null);
	statePartiesWithDepot = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);
	
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
	OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", condition5, UtilMisc.toSet("orderId","invoiceId"), null, null, false);
	orderIds=EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
	conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.IN, ["MGPS_10Pecent","MGPS","General"]));
	orderAttr = delegator.findList("OrderAttribute",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	
	orderAttrMgpsTenPerList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "MGPS_10Pecent"));
	orderAttrMgpsList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, "MGPS"));
	orderAttrGeneralList = EntityUtil.filterByCondition(orderAttr, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, "General"));
	
	mgpsTenPerOrderIds=EntityUtil.getFieldListFromEntityList(orderAttrMgpsTenPerList, "orderId", true);
	mgpsOrderIds=EntityUtil.getFieldListFromEntityList(orderAttrMgpsList, "orderId", true);
	generalOrderIds=EntityUtil.getFieldListFromEntityList(orderAttrGeneralList, "orderId", true);

	mgpsTenPerInvoiceIdsList = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, mgpsTenPerOrderIds));
	mgpsInvoiceIdsList = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, mgpsOrderIds));
	generalInvoiceIdsList = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, generalOrderIds));
	
	mgpsTenPerInvoiceIds=EntityUtil.getFieldListFromEntityList(mgpsTenPerInvoiceIdsList, "invoiceId", true);
	mgpsInvoiceIds=EntityUtil.getFieldListFromEntityList(mgpsInvoiceIdsList, "invoiceId", true);
	generalInvoiceIds=EntityUtil.getFieldListFromEntityList(generalInvoiceIdsList, "invoiceId", true);
	
	OrderItemBillingFilter = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	invoiceIds=EntityUtil.getFieldListFromEntityList(OrderItemBillingFilter, "invoiceId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
	Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	stateBranchWisePartiesMapData=[:];
	sr=1;
	stateTotalMap=[:];
	double totStatemgpsTenPerInvoiceAMT = 0;
	double totStatemgpsInvoiceAMT = 0; 
	double totStategeneralInvoiceAMT = 0; 
	
	double totStatemgpsTenPerInvoiceQTY = 0;
	double totStatemgpsInvoiceQTY = 0;
	double totStategeneralInvoiceQTY = 0;
	
	double totStatemgpsTenPerDepotInvoiceAMT = 0;   
	double totStatemgpsDepotInvoiceAMT = 0;  
	
	double totStatemgpsTenPerDepotInvoiceQTY = 0;
	double totStatemgpsDepotInvoiceQTY = 0;
	
	double totStateinvoiceAMT = 0;
	double totStateInvoiceQTY = 0;
		
	for(eachBranch in stateBranchs){
		String branchName = PartyHelper.getPartyName(delegator,eachBranch,false);
	
		statePartiesListData=[];
		eachBranchPartiesList =EntityUtil.filterByCondition(stateBranchList, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,eachBranch));
		stateParties=EntityUtil.getFieldListFromEntityList(eachBranchPartiesList, "partyIdTo", true);
		totMap=[:];
		double totBranchmgpsTenPerInvoiceAMT = 0;
		double totBranchmgpsInvoiceAMT = 0; 
		double totBranchgeneralInvoiceAMT = 0; 
		
		double totBranchmgpsTenPerInvoiceQTY = 0;
		double totBranchmgpsInvoiceQTY = 0;
		double totBranchgeneralInvoiceQTY = 0;
		
		double totBranchmgpsTenPerDepotInvoiceAMT = 0;   
		double totBranchmgpsDepotInvoiceAMT = 0;  
		
		double totBranchmgpsTenPerDepotInvoiceQTY = 0;
		double totBranchmgpsDepotInvoiceQTY = 0;
		
		double totBranchInvoiceAMT = 0;
		double totBranchInvoiceQTY = 0;
		
		for (partyId in stateParties) {
			tempMap=[:];
			double mgpsTenPerInvoiceAMT = 0;
			double mgpsInvoiceAMT = 0;
			double generalInvoiceAMT = 0;
			
			double mgpsTenPerInvoiceQTY = 0;
			double mgpsInvoiceQTY = 0;
			double generalInvoiceQTY = 0;
			
			double mgpsTenPerDepotInvoiceAMT = 0; 
			double mgpsDepotInvoiceAMT = 0;
			
			double mgpsTenPerDepotInvoiceQTY = 0;
			double mgpsDepotInvoiceQTY = 0;
			
			double totInvoiceAMT = 0;
			double totInvoiceQTY = 0;
			
			partyInvoices =EntityUtil.filterByCondition(Invoice, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			if(partyInvoices){
				for(eachInvoice in partyInvoices){
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
					conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE")));
					InvoiceItem = delegator.findList("InvoiceItem",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
					if(InvoiceItem){
						double eachInvoiceAmt = 0;
						double eachInvoiceQty = 0;
						for (eachInvoiceItem in InvoiceItem) {
							productId =eachInvoiceItem.productId
							eachInvoiceAmt = eachInvoiceAmt+(eachInvoiceItem.itemValue);
							eachInvoiceQty=eachInvoiceQty+eachInvoiceItem.quantity;
							totInvoiceAMT=totInvoiceAMT+(eachInvoiceItem.itemValue);
							totInvoiceQTY=totInvoiceQTY+eachInvoiceItem.quantity;
							totBranchInvoiceAMT=totBranchInvoiceAMT+eachInvoiceItem.itemValue
							totBranchInvoiceQTY=totBranchInvoiceQTY+eachInvoiceItem.quantity
							 totStateinvoiceAMT= totStateinvoiceAMT+eachInvoiceItem.itemValue;
							totStateInvoiceQTY=totStateInvoiceQTY+eachInvoiceItem.quantity;
						}
						
						if(mgpsTenPerInvoiceIds.contains(eachInvoice.invoiceId) && statePartiesWithDepot.contains(eachInvoice.partyId)){
							mgpsTenPerDepotInvoiceAMT=mgpsTenPerDepotInvoiceAMT+eachInvoiceAmt;
							mgpsTenPerDepotInvoiceQTY=mgpsTenPerDepotInvoiceQTY+eachInvoiceQty;
							totBranchmgpsTenPerDepotInvoiceAMT=totBranchmgpsTenPerDepotInvoiceAMT+eachInvoiceAmt;
							totBranchmgpsTenPerDepotInvoiceQTY=totBranchmgpsTenPerDepotInvoiceQTY+eachInvoiceQty;
							totStatemgpsTenPerDepotInvoiceAMT=totStatemgpsTenPerDepotInvoiceAMT+eachInvoiceAmt;
							totStatemgpsTenPerDepotInvoiceQTY=totStatemgpsTenPerDepotInvoiceQTY+eachInvoiceQty;
						}else if(mgpsTenPerInvoiceIds.contains(eachInvoice.invoiceId) && !statePartiesWithDepot.contains(eachInvoice.partyId)){
							mgpsTenPerInvoiceAMT=mgpsTenPerInvoiceAMT+eachInvoiceAmt;
							mgpsTenPerInvoiceQTY=mgpsTenPerInvoiceQTY+eachInvoiceQty;
							totBranchmgpsTenPerInvoiceAMT=totBranchmgpsTenPerInvoiceAMT+eachInvoiceAmt;
							totBranchmgpsTenPerInvoiceQTY=totBranchmgpsTenPerInvoiceQTY+eachInvoiceQty;
							totStatemgpsTenPerInvoiceAMT=totStatemgpsTenPerInvoiceAMT+eachInvoiceAmt;
							totStatemgpsTenPerInvoiceQTY=totStatemgpsTenPerInvoiceQTY+eachInvoiceQty;
						}else if(mgpsInvoiceIds.contains(eachInvoice.invoiceId) && statePartiesWithDepot.contains(eachInvoice.partyId)){
							mgpsDepotInvoiceAMT=mgpsDepotInvoiceAMT+eachInvoiceAmt;   
							mgpsDepotInvoiceQTY=mgpsDepotInvoiceQTY+eachInvoiceQty;  
							totBranchmgpsDepotInvoiceAMT=totBranchmgpsDepotInvoiceAMT+eachInvoiceAmt;
							totBranchmgpsDepotInvoiceQTY=totBranchmgpsDepotInvoiceQTY+eachInvoiceQty;
							totStatemgpsDepotInvoiceAMT=totStatemgpsDepotInvoiceAMT+eachInvoiceAmt;
							totStatemgpsDepotInvoiceQTY=totStatemgpsDepotInvoiceQTY+eachInvoiceQty;
						}else if(mgpsInvoiceIds.contains(eachInvoice.invoiceId) && !statePartiesWithDepot.contains(eachInvoice.partyId)){
							mgpsInvoiceAMT=mgpsInvoiceAMT+eachInvoiceAmt;
							mgpsInvoiceQTY=mgpsInvoiceQTY+eachInvoiceQty;
							totBranchmgpsInvoiceAMT=totBranchmgpsInvoiceAMT+eachInvoiceAmt;
							totBranchmgpsInvoiceQTY=totBranchmgpsInvoiceQTY+eachInvoiceQty;
							totStatemgpsInvoiceAMT=totStatemgpsInvoiceAMT+eachInvoiceAmt;
							totStatemgpsInvoiceQTY=totStatemgpsInvoiceQTY+eachInvoiceQty;
						}else if(generalInvoiceIds.contains(eachInvoice.invoiceId)){  
							generalInvoiceAMT=generalInvoiceAMT+eachInvoiceAmt;  
							generalInvoiceQTY=generalInvoiceQTY+eachInvoiceQty;
							totBranchgeneralInvoiceAMT=totBranchgeneralInvoiceAMT+eachInvoiceAmt;
							totBranchgeneralInvoiceQTY=totBranchgeneralInvoiceQTY+eachInvoiceQty;
							totStategeneralInvoiceAMT=totStategeneralInvoiceAMT+eachInvoiceAmt
							totStategeneralInvoiceQTY=totStategeneralInvoiceQTY+eachInvoiceQty;
						}
					
					}
				}
			}
			
			String partyName = PartyHelper.getPartyName(delegator,partyId,false);
			tempMap.put("sr", sr);
			tempMap.put("partyId", "_");
			tempMap.put("partyName", partyName);
			tempMap.put("totInvoiceAMT", totInvoiceAMT);
			tempMap.put("totInvoiceQTY", totInvoiceQTY);
			tempMap.put("mgpsTenPerDepotInvoiceAMT", mgpsTenPerDepotInvoiceAMT);
			tempMap.put("mgpsTenPerDepotInvoiceQTY", mgpsTenPerDepotInvoiceQTY);
			tempMap.put("mgpsTenPerInvoiceAMT", mgpsTenPerInvoiceAMT);
			tempMap.put("mgpsTenPerInvoiceQTY", mgpsTenPerInvoiceQTY);
			tempMap.put("mgpsDepotInvoiceAMT", mgpsDepotInvoiceAMT);
			tempMap.put("mgpsDepotInvoiceQTY", mgpsDepotInvoiceQTY);
			tempMap.put("mgpsInvoiceAMT", mgpsInvoiceAMT);
			tempMap.put("mgpsInvoiceQTY", mgpsInvoiceQTY);
			tempMap.put("generalInvoiceAMT", generalInvoiceAMT);
			tempMap.put("generalInvoiceQTY", generalInvoiceQTY);
			
			if(totInvoiceAMT>0 && reportType=="DETAIL"){
				statePartiesListData.add(tempMap);
				finalCSVList.add(tempMap);
			}
			sr=sr+1;
		}
     
		totMap.put("partyId","TOTAL");
		totMap.put("partyName", "BRANCH SUB-TOTAL");
		totMap.put("totInvoiceAMT",totBranchInvoiceAMT);
		totMap.put("totInvoiceQTY",totBranchInvoiceQTY);
		totMap.put("mgpsTenPerDepotInvoiceAMT",totBranchmgpsTenPerDepotInvoiceAMT);
		totMap.put("mgpsTenPerDepotInvoiceQTY",totBranchmgpsTenPerDepotInvoiceQTY);
		totMap.put("mgpsTenPerInvoiceAMT",totBranchmgpsTenPerInvoiceAMT);
		totMap.put("mgpsTenPerInvoiceQTY",totBranchmgpsTenPerInvoiceQTY);
		
		totMap.put("mgpsDepotInvoiceAMT",totBranchmgpsDepotInvoiceAMT);
		totMap.put("mgpsDepotInvoiceQTY",totBranchmgpsDepotInvoiceQTY);
		
		totMap.put("mgpsInvoiceAMT",totBranchmgpsInvoiceAMT);
		totMap.put("mgpsInvoiceQTY",totBranchmgpsInvoiceQTY);
		
		totMap.put("generalInvoiceAMT",totBranchgeneralInvoiceAMT);
		totMap.put("generalInvoiceQTY",totBranchgeneralInvoiceQTY);
		
		if(totBranchInvoiceAMT && reportType=="DETAIL"){
			statePartiesListData.add(totMap);
			finalCSVList.add(totMap);
		}
		if(UtilValidate.isNotEmpty(statePartiesListData)){
			stateBranchWisePartiesMapData.put(branchName, statePartiesListData);
		}
	}

	stateTotalMap.put("partyId","TOTAL");
	if(reportType=="DETAIL"){
		stateTotalMap.put("partyName", "STATE SUB-TOTAL");
	}else{
		stateTotalMap.put("partyName",stateName);
	}
	stateTotalMap.put("totInvoiceAMT", totStateinvoiceAMT);
	stateTotalMap.put("totInvoiceQTY",totStateInvoiceQTY);
	stateTotalMap.put("mgpsTenPerDepotInvoiceAMT",totStatemgpsTenPerDepotInvoiceAMT);
	stateTotalMap.put("mgpsTenPerDepotInvoiceQTY",totStatemgpsTenPerDepotInvoiceQTY);
	stateTotalMap.put("mgpsTenPerInvoiceAMT",totStatemgpsTenPerInvoiceAMT);
	stateTotalMap.put("mgpsTenPerInvoiceQTY",totStatemgpsTenPerInvoiceQTY);
	
	stateTotalMap.put("mgpsDepotInvoiceAMT",totStatemgpsDepotInvoiceAMT);
	stateTotalMap.put("mgpsDepotInvoiceQTY",totStatemgpsDepotInvoiceQTY);
	
	stateTotalMap.put("mgpsInvoiceAMT",totStatemgpsInvoiceAMT);
	stateTotalMap.put("mgpsInvoiceQTY",totStatemgpsInvoiceQTY);
	
	stateTotalMap.put("generalInvoiceAMT",totStategeneralInvoiceAMT);
	stateTotalMap.put("generalInvoiceQTY",totStategeneralInvoiceQTY);
	
	finalCSVList.add(stateTotalMap);
	stateMap.put(stateName, stateBranchWisePartiesMapData);
	finalList.add(stateMap);
	StateTotals.put(stateName, stateTotalMap);
}
context.finalList = finalList;
context.finalCSVList=finalCSVList;
context.StateTotals=StateTotals;






