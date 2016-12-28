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
conditionList = [];
productIds = [];
purchaseOrderIds =[];
productCategoryIds = [];
conditionList.clear();
if(productCategory != "OTHER" && productCategory != "ALL"){
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
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
orderHeaderItemAndRoles = delegator.findList("OrderHeaderItemAndRoles", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId","quantity","unitPrice","itemDescription","partyId"), null, null, false);
finalCSVList=[];
totalQty=0
totalValue=0;
totalRate=0;
prodCatMap=[:];
totalsMap=[:];
productId=EntityUtil.getFieldListFromEntityList(orderHeaderItemAndRoles, "productId", true);
headerData2=[:];
headerData2.put("prodcatName", "______");
headerData2.put("productName", "______");
headerData2.put("partyName", "______");
headerData2.put("orderQty", "______Supplierwise___");
/*headerData2.put("BdlWt", "____");*/
headerData2.put("rate", "_ Countwise");
headerData2.put("orderValue", "____ Purchase Report_______");
finalCSVList.add(headerData2);

headerData=[:];
headerData.put("prodcatName", "Product category");
headerData.put("productName", "product count");
headerData.put("partyName", "partyName");
headerData.put("orderQty", "orderQty");
/*headerData.put("BdlWt", "BdlWt");*/
headerData.put("rate", "rate");
headerData.put("orderValue", "orderValue");
finalCSVList.add(headerData);

for(productCategoryId in productCategoryIds){
	tempCSVMap1=[:];
	prodCatList=[];
	prodCatName="";
	productCategoryDetails = delegator.findOne("ProductCategory",[productCategoryId : productCategoryId] , false);
	if(UtilValidate.isNotEmpty(productCategoryDetails)){
		prodCatName=productCategoryDetails.description
	}
	tempCSVMap1.put("partyName", "");
	tempCSVMap1.put("orderQty", "");
	/*tempCSVMap1.put("BdlWt", "");*/
	tempCSVMap1.put("rate", "");
	tempCSVMap1.put("orderValue", "");
	finalCSVList.add(tempCSVMap1);
	singleCatProducts = EntityUtil.filterByCondition(produtCategorieMember, EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
	singleProductIds=EntityUtil.getFieldListFromEntityList(produtCategorieMember, "productId", true);
	
	for(singleProductId in singleProductIds){
		prodMap=[:];
		tempTotMap=[:];
		tempCSVMap2=[:];
		prodPartiesList=[];
		totOrderQty =0;
		totOrderValue =0;
		totRate=0
		singleCatProductsOrdersDetails = EntityUtil.filterByCondition(orderHeaderItemAndRoles, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, singleProductId));
		singleCatProductsOrdersDetail = EntityUtil.getFirst(singleCatProductsOrdersDetails);
		partyIds=EntityUtil.getFieldListFromEntityList(singleCatProductsOrdersDetails, "partyId", true);
		
		if(UtilValidate.isNotEmpty(singleCatProductsOrdersDetail)){
			tempCSVMap2.put("partyName", "");
			tempCSVMap2.put("orderQty", "");
			/*tempCSVMap2.put("BdlWt", "");*/
			tempCSVMap2.put("rate", "");
			tempCSVMap2.put("orderValue", "");
			finalCSVList.add(tempCSVMap2);
			
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
				unitPrice= unitPrice+eachItem.unitPrice;
			}
			
			if(UtilValidate.isNotEmpty(singleCatProductsForEachParty)){
				rate=unitPrice/singleCatProductsForEachParty.size();
			}
			orderValue = orderQty*rate;
			totOrderQty=totOrderQty+orderQty;
			totRate=totRate+rate;
			totOrderValue=totOrderValue+orderValue;
			String partyName = PartyHelper.getPartyName(delegator,partyId,false);
			tempMap.put("productName", singleCatProductsOrdersDetail.itemDescription);
			tempMap.put("prodcatName", prodCatName);
			tempMap.put("partyName", partyName);
			tempMap.put("orderQty", orderQty);
			/*tempMap.put("BdlWt", "");*/
			tempMap.put("rate", rate);
			tempMap.put("orderValue", orderValue);
			totalQty=totalQty+orderQty
			totalValue=totalValue+orderValue;
			totalRate=totalRate+rate;
			if(orderValue>0){
				prodPartiesList.add(tempMap);
				finalCSVList.add(tempMap)
			}
		}
		if(UtilValidate.isNotEmpty(singleCatProductsOrdersDetail) && UtilValidate.isNotEmpty(prodPartiesList)){
			
			
			tempTotMap.put("partyName", "SUB-TOTAL");
			tempTotMap.put("orderQty", totOrderQty);
			/*tempTotMap.put("BdlWt", "");*/
			tempTotMap.put("rate", totRate);
			tempTotMap.put("orderValue", totOrderValue);
			prodPartiesList.add(tempTotMap);
			finalCSVList.add(tempTotMap);
			
			prodMap.put(singleCatProductsOrdersDetail.itemDescription, prodPartiesList);
			prodCatList.add(prodMap);
			
		}
	}
	if(UtilValidate.isNotEmpty(prodCatList)){
		prodCatMap.put(prodCatName, prodCatList)
	}

}
totalsMap.put("orderQty", totalQty);
totalsMap.put("orderValue", totalValue);
totalsMap.put("rate", totalRate);
context.totalsMap=totalsMap;
context.prodCatMap=prodCatMap;
context.finalCSVList=finalCSVList;









