import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
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
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
reportTypeFlag = parameters.reportTypeFlag;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}

// Purchase abstract Sales report
reportTypeFlag = parameters.reportTypeFlag;
taxType=parameters.taxType;
//get Product Catagory 
exprList=[];
exprList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, "PURCHASE_ACCOUNT"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
productcatList = delegator.findList("ProductCategoryGlAccount", condition, null, null, null, false);
productCategoryId = EntityUtil.getFieldListFromEntityList(productcatList, "productCategoryId", true);
//get product from ProductCategory
productCategoryMember = delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryId), null, null, null, false);
productCatMap=[:];
productCategoryMember.each{prodCatMember ->
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
}
//Debug.log("==productCatMap="+productCatMap.size()+"==productCatMap=="+productCatMap);
EntityListIterator invoiceItemsIter = null;
//get KMf Units
kmfUnitPartyIdsList=[];
kmfUnitPartyIdsList = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNITS"]).get("partyIds");
//get Unions
kmfUnionPartyIdsList=[];
kmfUnionPartyIdsList = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNION"]).get("partyIds");

excludePartyIdsList=[];
excludePartyIdsList.addAll(kmfUnitPartyIdsList);
excludePartyIdsList.addAll(kmfUnionPartyIdsList);
//taxable PurchasITEM types
taxbleInvoiceItemTypeIdList=UtilMisc.toList("VAT_PUR","CST_PUR","BED_PUR","BEDCESS_PUR","BEDSECCESS_PUR");

fuelProdList=org.ofbiz.product.product.ProductWorker.getProductsByCategory(delegator,"FUEL",null);
fuelProdIdsList=EntityUtil.getFieldListFromEntityList(fuelProdList, "productId", true);
oilLubProdList=org.ofbiz.product.product.ProductWorker.getProductsByCategory(delegator,"OIL_LUB",null);
oilLubProdIdsList=EntityUtil.getFieldListFromEntityList(oilLubProdList, "productId", true);

wsdProdList=org.ofbiz.product.product.ProductWorker.getProductsByCategory(delegator,"PUR_WSD",null);
wsdProdIdsList=EntityUtil.getFieldListFromEntityList(oilLubProdList, "productId", true);

purchaseExemptProdList=[];
purchaseExemptProdList.addAll(fuelProdIdsList);
purchaseExemptProdList.addAll(oilLubProdIdsList);
purchaseExemptProdList.addAll(wsdProdIdsList);


/*Debug.log("==kmfUnitPartyIdsList="+kmfUnitPartyIdsList);
Debug.log("==kmfUnionPartyIdsList="+kmfUnionPartyIdsList);
Debug.log("==fuelProdListIds="+fuelProdIdsList);
Debug.log("==oilLubProdListIds="+oilLubProdIdsList);*/
taxDetails5pt5List=[];
taxDetails14pt5List=[];
taxDetailsOthrList=[];

taxDetails5pt5Map=[:];
taxDetails14pt5Map=[:];


purchaseGrandTotMap=[:];
purchaseGrandTotMap["DR"]=BigDecimal.ZERO;
purchaseGrandTotMap["CR"]=BigDecimal.ZERO;
purchaseGrandTotMap["total"]=BigDecimal.ZERO;


prchaseCategoryDetaildMap=[:];
prchaseCategorySummeryMap=[:];
purchaseSumCatDetaildMap=[:];
purchaseSumInvDetaildMap=[:];


		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			if (UtilValidate.isNotEmpty(purchaseExemptProdList)) {
				conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_IN, purchaseExemptProdList));//want to skip other than product items
			}
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			if (UtilValidate.isNotEmpty(kmfUnitPartyIdsList)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_IN, kmfUnitPartyIdsList));
			}
			conditionList.add(EntityCondition.makeCondition("vatPercent", EntityOperator.EQUALS,null));
			conditionList.add(EntityCondition.makeCondition("vatAmount", EntityOperator.EQUALS,null));
			conditionList.add(EntityCondition.makeCondition("cstPercent", EntityOperator.EQUALS,null));
			conditionList.add(EntityCondition.makeCondition("cstAmount", EntityOperator.EQUALS,null));
			
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		purchaseCatMap=[:];
		purchaseCatMap["DR"]=BigDecimal.ZERO;
		purchaseCatMap["CR"]=BigDecimal.ZERO;
		purchaseCatMap["total"]=BigDecimal.ZERO;
		purchasExemptList=[];
		
		purchasExemptProdCatMap=[:];
		purchasExemptProdCatMap["discount"]=BigDecimal.ZERO;
		
		invoiceItemsIter.each{invoiceItem->
			invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
			//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
			productId=invoiceItem.productId;
			purchaseCatMap["DR"]+=invTotalVal;
			purchaseCatMap["total"]+=invTotalVal;
			//Debug.log("=invoiceItem=="+invoiceItem);
			purchaseGrandTotMap["DR"]+=invTotalVal;
			purchaseGrandTotMap["total"]+=invTotalVal;
			
			//Detailed Map starts Here
			
			
			//innerTaxItemMap["vatAmount"]=vatRevenue;
			
			//preparing Another Map here for Category
			innerItemMap=[:];
			innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
			innerItemMap["invoiceId"]=invoiceItem.invoiceId;
			innerItemMap["partyId"]=invoiceItem.partyIdFrom;
			innerItemMap["productId"]=invoiceItem.productId;
			innerItemMap["tinNumber"]="";
			innerItemMap["vchrType"]="Purchase";
			innerItemMap["crOrDbId"]="D";
			innerItemMap["invTotalVal"]=invTotalVal;
			innerItemMap["taxAmount"]=0;
			purchasExemptList.addAll(innerItemMap);
			// get category
			if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
				prodCategoryId=productCatMap.get(productId);
				if(UtilValidate.isEmpty(purchasExemptProdCatMap[prodCategoryId])){
					innerTaxCatMap=[:];
					innerTaxCatMap["totalValue"]=invTotalVal;
					innerTaxCatMap["taxAmount"]=0;
					invoiceList=[];
					invoiceList.addAll(innerItemMap);
					innerTaxCatMap["invoiceList"]=invoiceList;
					purchasExemptProdCatMap[prodCategoryId]=innerTaxCatMap;
				}else if(UtilValidate.isNotEmpty(purchasExemptProdCatMap[prodCategoryId])){
					Map innerTaxCatMap=purchasExemptProdCatMap[prodCategoryId];
					innerTaxCatMap["totalValue"]+=invTotalVal;
					innerTaxCatMap["taxAmount"]+=0;
					invoiceList=innerTaxCatMap["invoiceList"];
					invoiceList.addAll(innerItemMap);
					innerTaxCatMap["invoiceList"]=invoiceList;
					purchasExemptProdCatMap[prodCategoryId]=innerTaxCatMap;
				}
			}
			//category ends here
			
		}
		purchaseSumInvDetaildMap["Purchase-Exempt-Total"]=purchasExemptList;
		prchaseCategorySummeryMap["Purchase-Exempt-Total"]=purchaseCatMap;
		purchaseSumCatDetaildMap["Purchase-Exempt-Total"]=purchasExemptProdCatMap;
		
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//interState summery prepare here
		purchaseInterCatMap=[:];
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_IN, purchaseExemptProdList));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			if (UtilValidate.isNotEmpty(kmfUnitPartyIdsList)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_IN, excludePartyIdsList));
			}
			conditionList.add(EntityCondition.makeCondition("cstPercent", EntityOperator.NOT_EQUAL,null));
			conditionList.add(EntityCondition.makeCondition("cstAmount", EntityOperator.NOT_EQUAL,null));
			
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		
		purchaseInterCatMap["DR"]=BigDecimal.ZERO;
		purchaseInterCatMap["CR"]=BigDecimal.ZERO;
		purchaseInterCatMap["total"]=BigDecimal.ZERO;
		
		purchaseCstProdCatMap=[:];
		purchaseCstProdCatMap["discount"]=BigDecimal.ZERO;
		purchaseCstInvList=[];
		
		invoiceItemsIter.each{invoiceItem->
			if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				BigDecimal cstRevenue = invoiceItem.cstAmount;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				//preparing Another Map here for Category
				productId=invoiceItem.productId;
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["taxAmount"]=cstRevenue;
				purchaseCstInvList.addAll(innerItemMap)
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(purchaseCstProdCatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["taxAmount"]=cstRevenue;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchaseCstProdCatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(purchaseCstProdCatMap[prodCategoryId])){
						Map innerTaxCatMap=purchaseCstProdCatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["taxAmount"]+=cstRevenue;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchaseCstProdCatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				purchaseInterCatMap["DR"]+=invTotalVal;
				purchaseInterCatMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		//get Category of CST from other groovy
		purchaseSumInvDetaildMap["Purchase-InterState"]=purchaseCstInvList;
		purchaseSumCatDetaildMap["Purchase-InterState"]=purchaseCstProdCatMap;
		prchaseCategorySummeryMap["Purchase-InterState"]=purchaseInterCatMap;
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//Diesel exempt
		dieselAndFurnceProdList=[];
		dieselAndFurnceProdList.addAll(fuelProdIdsList);
		dieselAndFurnceProdList.addAll(oilLubProdIdsList);

		
		purchaseDiselExCatMap=[:];
		purchaseDiselExCatMap["DR"]=BigDecimal.ZERO;
		purchaseDiselExCatMap["CR"]=BigDecimal.ZERO;
		purchaseDiselExCatMap["total"]=BigDecimal.ZERO;
		
		
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, dieselAndFurnceProdList));//want to get only this type of product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			if (UtilValidate.isNotEmpty(kmfUnitPartyIdsList)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_IN, excludePartyIdsList));
			}
			conditionList.add(EntityCondition.makeCondition("vatPercent", EntityOperator.EQUALS,null));
			conditionList.add(EntityCondition.makeCondition("vatAmount", EntityOperator.EQUALS,null));
			conditionList.add(EntityCondition.makeCondition("cstPercent", EntityOperator.EQUALS,null));
			conditionList.add(EntityCondition.makeCondition("cstAmount", EntityOperator.EQUALS,null));
			
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		purchasDiselExProdCatMap=[:];
		purchaseDiselExInvList=[];
		purchasDiselExProdCatMap["discount"]=BigDecimal.ZERO;
		invoiceItemsIter.each{invoiceItem->
			invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
			
			//preparing Another Map here for Category
			productId=invoiceItem.productId;
			innerItemMap=[:];
			innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
			innerItemMap["invoiceId"]=invoiceItem.invoiceId;
			innerItemMap["partyId"]=invoiceItem.partyIdFrom;
			innerItemMap["productId"]=invoiceItem.productId;
			innerItemMap["tinNumber"]="";
			innerItemMap["vchrType"]="Purchase";
			innerItemMap["crOrDbId"]="D";
			innerItemMap["invTotalVal"]=invTotalVal;
			innerItemMap["taxAmount"]=0;
			purchaseDiselExInvList.addAll(innerItemMap);
			// get category
			if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
				prodCategoryId=productCatMap.get(productId);
				if(UtilValidate.isEmpty(purchasDiselExProdCatMap[prodCategoryId])){
					innerTaxCatMap=[:];
					innerTaxCatMap["totalValue"]=invTotalVal;
					innerTaxCatMap["taxAmount"]=0;
					invoiceList=[];
					invoiceList.addAll(innerItemMap);
					innerTaxCatMap["invoiceList"]=invoiceList;
					purchasDiselExProdCatMap[prodCategoryId]=innerTaxCatMap;
				}else if(UtilValidate.isNotEmpty(purchasExemptProdCatMap[prodCategoryId])){
					Map innerTaxCatMap=purchasDiselExProdCatMap[prodCategoryId];
					innerTaxCatMap["totalValue"]+=invTotalVal;
					innerTaxCatMap["taxAmount"]+=0;
					invoiceList=innerTaxCatMap["invoiceList"];
					invoiceList.addAll(innerItemMap);
					innerTaxCatMap["invoiceList"]=invoiceList;
					purchasDiselExProdCatMap[prodCategoryId]=innerTaxCatMap;
				}
			}
			//category ends here
				purchaseDiselExCatMap["DR"]+=invTotalVal
				purchaseDiselExCatMap["total"]+=invTotalVal
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
		}
		purchaseSumInvDetaildMap["Purchase-DieselExempt"]=purchaseDiselExInvList;
		purchaseSumCatDetaildMap["Purchase-DieselExempt"]=purchasDiselExProdCatMap;
		prchaseCategorySummeryMap["Purchase-DieselExempt"]=purchaseDiselExCatMap;
		
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		// Funase INter State tax catagoery
		
		purchaseDiselInterCatMap=[:];
		purchaseDiselInterCatMap["DR"]=BigDecimal.ZERO;
		purchaseDiselInterCatMap["CR"]=BigDecimal.ZERO;
		purchaseDiselInterCatMap["total"]=BigDecimal.ZERO;
		
		
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, dieselAndFurnceProdList));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			if (UtilValidate.isNotEmpty(kmfUnitPartyIdsList)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_IN, excludePartyIdsList));
			}
			conditionList.add(EntityCondition.makeCondition("cstPercent", EntityOperator.NOT_EQUAL,null));
			conditionList.add(EntityCondition.makeCondition("cstAmount", EntityOperator.NOT_EQUAL,null));
			
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		purchasDiselCSTProdCatMap=[:];
		purchasDiselCSTProdCatMap["discount"]=BigDecimal.ZERO;
		purchasDiselCSTInvList=[];
		invoiceItemsIter.each{invoiceItem->
			if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				BigDecimal cstRevenue = invoiceItem.cstAmount;
				
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				//preparing Another Map here for Category
				productId=invoiceItem.productId;
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["taxAmount"]=cstRevenue;
				purchasDiselCSTInvList.addAll(innerItemMap);
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(purchasDiselCSTProdCatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["taxAmount"]=cstRevenue;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchasDiselCSTProdCatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(purchasDiselCSTProdCatMap[prodCategoryId])){
						Map innerTaxCatMap=purchasDiselCSTProdCatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["taxAmount"]+=cstRevenue;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchasDiselCSTProdCatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				purchaseDiselInterCatMap["DR"]+=invTotalVal;
				purchaseDiselInterCatMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		purchaseSumInvDetaildMap["Purchase-FurnaceOilInterState"]=purchasDiselCSTInvList;
		purchaseSumCatDetaildMap["Purchase-FurnaceOilInterState"]=purchasDiselCSTProdCatMap;
		prchaseCategorySummeryMap["Purchase-FurnaceOilInterState"]=purchaseDiselInterCatMap;
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//Purchase Inter Unit Transfer
		purchaseInterUnitStTrMap=[:];
		purchaseInterUnitStTrMap["DR"]=BigDecimal.ZERO;
		purchaseInterUnitStTrMap["CR"]=BigDecimal.ZERO;
		purchaseInterUnitStTrMap["total"]=BigDecimal.ZERO;
		
		
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			//conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, dieselAndFurnceProdList));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			if (UtilValidate.isNotEmpty(kmfUnitPartyIdsList)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, kmfUnitPartyIdsList));
			}
			/*conditionList.add(EntityCondition.makeCondition("cstPercent", EntityOperator.NOT_EQUAL,null));
			conditionList.add(EntityCondition.makeCondition("cstAmount", EntityOperator.NOT_EQUAL,null));
			*/
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		purchasInterStateProdCatMap=[:];
		purchasInterStateProdCatMap["discount"]=BigDecimal.ZERO;
		purchasInterStateInvList=[];
		invoiceItemsIter.each{invoiceItem->
			//if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,false);
				
				//preparing Another Map here for Category
				productId=invoiceItem.productId;
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["taxAmount"]=0;
				purchasInterStateInvList.addAll(innerItemMap);
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(purchasInterStateProdCatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["taxAmount"]=0;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchasInterStateProdCatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(purchasInterStateProdCatMap[prodCategoryId])){
						Map innerTaxCatMap=purchasInterStateProdCatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["taxAmount"]+=0;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchasInterStateProdCatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				purchaseInterUnitStTrMap["DR"]+=invTotalVal;
				purchaseInterUnitStTrMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			//}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		purchaseSumInvDetaildMap["Purchase-InterUnitStkTransfer"]=purchasInterStateInvList;
		purchaseSumCatDetaildMap["Purchase-InterUnitStkTransfer"]=purchasInterStateProdCatMap;
		prchaseCategorySummeryMap["Purchase-InterUnitStkTransfer"]=purchaseInterUnitStTrMap;
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//kmf Unions 
	    purchaseUnionWsdMap=[:];
		purchaseUnionWsdMap["DR"]=BigDecimal.ZERO;
		purchaseUnionWsdMap["CR"]=BigDecimal.ZERO;
		purchaseUnionWsdMap["total"]=BigDecimal.ZERO;
		
		
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			//conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, dieselAndFurnceProdList));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			if (UtilValidate.isNotEmpty(kmfUnitPartyIdsList)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, kmfUnionPartyIdsList));
			}
			/*conditionList.add(EntityCondition.makeCondition("cstPercent", EntityOperator.NOT_EQUAL,null));
			conditionList.add(EntityCondition.makeCondition("cstAmount", EntityOperator.NOT_EQUAL,null));
			*/
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		purchaseUnionProdCatMap=[:];
		purchaseUnionProdCatMap["discount"]=BigDecimal.ZERO;
		purchaseUnionInvList=[];
		invoiceItemsIter.each{invoiceItem->
			//if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				//preparing Another Map here for Category
				productId=invoiceItem.productId;
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["taxAmount"]=0;
				purchaseUnionInvList.addAll(innerItemMap);
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(purchaseUnionProdCatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["taxAmount"]=0;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchaseUnionProdCatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(purchaseUnionProdCatMap[prodCategoryId])){
						Map innerTaxCatMap=purchaseUnionProdCatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["taxAmount"]+=0;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchaseUnionProdCatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				purchaseUnionWsdMap["DR"]+=invTotalVal;
				purchaseUnionWsdMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			//}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		purchaseSumInvDetaildMap["Purchase-WSD-ProductsFromKMF"]=purchaseUnionInvList;
		purchaseSumCatDetaildMap["Purchase-WSD-ProductsFromKMF"]=purchaseUnionProdCatMap;
		prchaseCategorySummeryMap["Purchase-WSD-ProductsFromKMF"]=purchaseUnionWsdMap;
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//Vat classfication
		purchaseAt14pt5Map=[:];
		purchaseAt14pt5Map["DR"]=BigDecimal.ZERO;
		purchaseAt14pt5Map["CR"]=BigDecimal.ZERO;
		purchaseAt14pt5Map["total"]=BigDecimal.ZERO;
		
		Map tax14pt5TotalMap=context.get("tax14pt5TotalMap");
		Map tax14pt5CatMap=context.get("tax14pt5CatMap");
		List tax14pt5InvList=context.get("tax14pt5InvList");
		
		if(UtilValidate.isNotEmpty(tax14pt5TotalMap)){
			purchaseAt14pt5Map["DR"]+=(tax14pt5TotalMap.get("invTotalVal"));
			purchaseAt14pt5Map["total"]+=(tax14pt5TotalMap.get("invTotalVal"));
			
			purchaseGrandTotMap["DR"]+=(tax14pt5TotalMap.get("invTotalVal"));
			purchaseGrandTotMap["total"]+=(tax14pt5TotalMap.get("invTotalVal"));
		
		/*	purchaseAt14pt5Map["DR"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
			purchaseAt14pt5Map["total"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
			
			purchaseGrandTotMap["DR"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
			purchaseGrandTotMap["total"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));*/
		}
		purchaseSumInvDetaildMap["Purchase-14.5%VAT"]=tax14pt5InvList;
		prchaseCategorySummeryMap["Purchase-14.5%VAT"]=purchaseAt14pt5Map;
		purchaseSumCatDetaildMap["Purchase-14.5%VAT"]=tax14pt5CatMap;
		purchaseAt5pt5Map=[:];
		purchaseAt5pt5Map["DR"]=BigDecimal.ZERO;
		purchaseAt5pt5Map["CR"]=BigDecimal.ZERO;
		purchaseAt5pt5Map["total"]=BigDecimal.ZERO;
		
		Map tax5pt5TotalMap=context.get("tax5pt5TotalMap");
		Map tax5pt5CatMap=context.get("tax5pt5CatMap");
		List tax5pt5InvList=context.get("tax5pt5InvList");
		if(UtilValidate.isNotEmpty(tax5pt5TotalMap)){
			purchaseAt5pt5Map["DR"]+=(tax5pt5TotalMap.get("invTotalVal"));
			purchaseAt5pt5Map["total"]+=(tax5pt5TotalMap.get("invTotalVal"));
			
			purchaseGrandTotMap["DR"]+=(tax5pt5TotalMap.get("invTotalVal"));
			purchaseGrandTotMap["total"]+=(tax5pt5TotalMap.get("invTotalVal"));
			
			/*purchaseAt5pt5Map["DR"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
			purchaseAt5pt5Map["total"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
			
			purchaseGrandTotMap["DR"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
			purchaseGrandTotMap["total"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));*/
		}
		purchaseSumInvDetaildMap["Purchase-5.5%VAT"]=tax5pt5InvList;
		prchaseCategorySummeryMap["Purchase-5.5%VAT"]=purchaseAt5pt5Map;
		purchaseSumCatDetaildMap["Purchase-5.5%VAT"]=tax5pt5CatMap;
		
		//fright Total Value
		purchaseFreightMap=[:];
		purchaseFreightMap["DR"]=BigDecimal.ZERO;
		purchaseFreightMap["CR"]=BigDecimal.ZERO;
		purchaseFreightMap["total"]=BigDecimal.ZERO;
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, null));//want to skip other than product items
			//conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, dieselAndFurnceProdList));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS, "COGS_ITEM16"));
			
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		purchasFreightProdCatMap=[:];
		purchasFreightProdCatMap["discount"]=BigDecimal.ZERO;
		purchasFreightInvList=[];
		invoiceItemsIter.each{invoiceItem->
			//if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				
				productId=invoiceItem.productId;
				//preparing Another Map here for Category
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["taxAmount"]=0;
				purchasFreightInvList.addAll(innerItemMap);
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(purchasFreightProdCatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["taxAmount"]=0;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchasFreightProdCatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(purchasFreightProdCatMap[prodCategoryId])){
						Map innerTaxCatMap=purchasFreightProdCatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["taxAmount"]+=0;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						purchasFreightProdCatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				purchaseFreightMap["DR"]+=invTotalVal;
				purchaseFreightMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			//}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		//Debug.log("==#####==purchasFreightProdCatMap=="+purchasFreightProdCatMap);
		
		purchaseSumInvDetaildMap["Purchase-FreightCharges"]=purchasFreightInvList;
		prchaseCategorySummeryMap["Purchase-FreightCharges"]=purchaseFreightMap;
		purchaseSumCatDetaildMap["Purchase-FreightCharges"]=purchasFreightProdCatMap;
		
		prchaseCategorySummeryMap["Total"]=purchaseGrandTotMap;
		
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//Debug.log("=purchaseSumInvDetaildMap####=="+purchaseSumInvDetaildMap);
		
		context.prchaseCategorySummeryMap = prchaseCategorySummeryMap;
		context.prchaseCategoryDetaildMap = prchaseCategoryDetaildMap;
		context.purchaseSumCatDetaildMap = purchaseSumCatDetaildMap;
		context.purchaseSumInvDetaildMap = purchaseSumInvDetaildMap;
		
		
		taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX4"), false);
		taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX4"), false);
		context.taxParty = taxParty;
		context.taxAuthority = taxAuthority;
		invItemTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "VAT_PUR"), false);
		context.invItemTypeGl = invItemTypeGl;
// Purchase Abstract report


