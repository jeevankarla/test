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

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
dayend = null;
daystart = null;
Timestamp fromDate;
Timestamp thruDate;

partyfromDate=parameters.fromDate;
partythruDate=parameters.thruDate;

branchIds=[];
branchId = parameters.branchId;
DateMap = [:];
branchName = "";
if(branchId){
branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
branchName = branch.get("groupName");
DateMap.put("branchName", branchName);
}
branchIdForAdd="";
branchList = [];
condListb = [];
if(branchId){
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
if(!branchList){
	condListb2 = [];
	//condListb2.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,"%"));
	condListb2.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, branchId));
	condListb2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb2.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	cond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
	
	PartyRelationship1 = delegator.findList("PartyRelationship", cond,UtilMisc.toSet("partyIdFrom"), null, null, false);
	branchDetails = EntityUtil.getFirst(PartyRelationship1);
	branchIdForAdd=branchDetails.partyIdFrom;
}
else{
	branchIdForAdd=branchId;
}
if(!branchList)
branchList.add(branchId);
}

branchBasedWeaversList = [];
condListb1 = [];
if(branchId){
condListb1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
condListb1.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

if(!branchBasedWeaversList)
branchBasedWeaversList.add(branchId);
}

productCategory=parameters.categoryId;
context.partyfromDate=partyfromDate;
context.partythruDate=partythruDate;
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(partyfromDate)){
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
		 } catch (ParseException e) {
			 //////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
		}
}
if(UtilValidate.isNotEmpty(partythruDate)){
   try {
	   thruDate = new java.sql.Timestamp(sdf.parse(partythruDate).getTime());
	   dayend = UtilDateTime.getDayEnd(thruDate);
   } catch (ParseException e) {
	   //////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}
context.daystart=daystart
context.dayend=dayend

fromDateForFtl=UtilDateTime.toDateString(daystart, "dd/MM/yyyy");
thruDateForFtl=UtilDateTime.toDateString(dayend, "dd/MM/yyyy");
context.fromDateForFtl=fromDateForFtl;
context.thruDateForFtl=thruDateForFtl;
branchContext=[:];
branchContext.put("branchId",branchIdForAdd);
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
conditionList = [];
productIds = [];
purchaseOrderIds =[];
productCategoryIds = [];

conditionList.clear();
if(productCategory != "OTHER"  && productCategory != "ALL"){
	conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}else if(productCategory == "OTHER"){
	conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","COTTON"]));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}else{
	conditionList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "NATURAL_FIBERS"));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	ProductCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.IN,productCategoryIds),UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
produtCategorieMember = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId","productCategoryId"), null, null, false);
productIds=EntityUtil.getFieldListFromEntityList(produtCategorieMember, "productId", true);
conditionList.clear(); 
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
invoiceItems = delegator.findList("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId","quantity","itemValue","amount","partyId"), null, null, false);
finalCSVList=[];
totalQty=0 
totalValue=0;
prodCatMap=[:];
totalsMap=[:];
productId=EntityUtil.getFieldListFromEntityList(invoiceItems, "productId", true);

stylesMap=[:];
if(branchId){
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", BOAddress);
	stylesMap.put("mainHeader3", "Fiber And Count Wise Sales Report");
	stylesMap.put("mainHeader4", "From"+ fromDateForFtl +" to "+thruDateForFtl);
}
else{
	stylesMap.put("mainHeader1", "NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. ");
	stylesMap.put("mainHeader2", "Fiber And Count Wise Sales Report");
	stylesMap.put("mainHeader3", "From"+fromDateForFtl +" to "+thruDateForFtl);
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
headingMap.put("prodcatName", "Product Category");
headingMap.put("productName", "Product Count");
headingMap.put("partyName", "Party Name");
headingMap.put("orderQty", "Order Qty(kgs)");
headingMap.put("rate", "Rate");
headingMap.put("orderValue","Order Value");

finalCSVList.add(stylesMap);
finalCSVList.add(headingMap);

for(productCategoryId in productCategoryIds){
	tempCSVMap1=[:];
	prodCatList=[];
	prodCatName="";
	productCategoryDetails = delegator.findOne("ProductCategory",[productCategoryId : productCategoryId] , false);
	if(UtilValidate.isNotEmpty(productCategoryDetails)){
		prodCatName=productCategoryDetails.description
	}
	/*tempCSVMap1.put("partyName", "");
	tempCSVMap1.put("orderQty", "");
	//tempCSVMap1.put("BdlWt", "");
	tempCSVMap1.put("rate", "");
	tempCSVMap1.put("orderValue", "");
	finalCSVList.add(tempCSVMap1);*/
	singleCatProducts = EntityUtil.filterByCondition(produtCategorieMember, EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
	singleProductIds=EntityUtil.getFieldListFromEntityList(singleCatProducts, "productId", true);
	
	for(singleProductId in singleProductIds){
		prodMap=[:];
		tempTotMap=[:];
		tempCSVMap2=[:];
		prodPartiesList=[];
		totOrderQty =0;
		totOrderValue =0;
		productDetails=null;
		singleCatProductsOrdersDetails = EntityUtil.filterByCondition(invoiceItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, singleProductId));
		singleCatProductsOrdersDetail = EntityUtil.getFirst(singleCatProductsOrdersDetails);
		partyIds=EntityUtil.getFieldListFromEntityList(singleCatProductsOrdersDetails, "partyId", true);
		
		if(UtilValidate.isNotEmpty(singleCatProductsOrdersDetail)){
			productDetails = delegator.findOne("Product",[productId : singleCatProductsOrdersDetail.productId] , false);
			/*tempCSVMap2.put("partyName", "");
			tempCSVMap2.put("orderQty", "");
			//tempCSVMap2.put("BdlWt", "");
			tempCSVMap2.put("rate", "");
			tempCSVMap2.put("orderValue", "");
			finalCSVList.add(tempCSVMap2);*/
			
		}
		
		for(partyId in partyIds){
			tempMap=[:];
			orderQty =0;
			orderValue =0;
			rate =0;
			unitPrice=0;
			singleCatProductsForEachParty = EntityUtil.filterByCondition(singleCatProductsOrdersDetails, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			for(eachItem in singleCatProductsForEachParty){
				orderQty=orderQty+eachItem.quantity;
				unitPrice= unitPrice+eachItem.amount;
			}
			
			if(UtilValidate.isNotEmpty(singleCatProductsForEachParty)){
				rate=unitPrice/singleCatProductsForEachParty.size();
			}  
			orderValue = orderQty*rate;
			totOrderQty=totOrderQty+orderQty;
			totOrderValue=totOrderValue+orderValue;
			totalQty=totalQty+orderQty;
			totalValue=totalValue+orderValue;
			String partyName = PartyHelper.getPartyName(delegator,partyId,false);
			tempMap.put("productName", productDetails.productName);
			tempMap.put("prodcatName", prodCatName);
			tempMap.put("partyName", partyName);
			tempMap.put("orderQty", orderQty);
			/*tempMap.put("BdlWt", "");*/
			tempMap.put("rate", rate);
			tempMap.put("orderValue", orderValue);
			if(orderValue>0){
				prodPartiesList.add(tempMap);
				finalCSVList.add(tempMap)
			}
		}
		if(UtilValidate.isNotEmpty(singleCatProductsOrdersDetail) && UtilValidate.isNotEmpty(prodPartiesList)){
			
			
			tempTotMap.put("partyName", "SUB-TOTAL");
			tempTotMap.put("orderQty", totOrderQty);
			/*tempTotMap.put("BdlWt", "");*/
			tempTotMap.put("rate", "");
			tempTotMap.put("orderValue", totOrderValue);
			prodPartiesList.add(tempTotMap);
			finalCSVList.add(tempTotMap);
			
			prodMap.put(productDetails.productName, prodPartiesList);
			prodCatList.add(prodMap);
			
		}
	}
	if(UtilValidate.isNotEmpty(prodCatList)){
		prodCatMap.put(prodCatName, prodCatList)
	}

}
totalsMap.put("prodcatName", "TOTAL");
totalsMap.put("orderQty", totalQty);
totalsMap.put("orderValue", totalValue);
finalCSVList.add(totalsMap);
context.totalsMap=totalsMap;
context.prodCatMap=prodCatMap;
context.finalCSVList=finalCSVList;


