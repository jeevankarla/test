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
Debug.log("===WSD===Size==="+wsdProdIdsList.size()+"==fuelProdIdsList="+fuelProdIdsList.size()+"==oilLubProdIdsList=="+oilLubProdIdsList.size());
purchaseExemptProdList=[];
purchaseExemptProdList.addAll(fuelProdIdsList);
purchaseExemptProdList.addAll(oilLubProdIdsList);
purchaseExemptProdList.addAll(wsdProdIdsList);

Debug.log("==purchaseExemptProdList="+purchaseExemptProdList.size());

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

		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_IN, purchaseExemptProdList));//want to skip other than product items
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
		
		invoiceItemsIter.each{invoiceItem->
			invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
			//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
			purchaseCatMap["DR"]+=invTotalVal;
			purchaseCatMap["total"]+=invTotalVal;
			//Debug.log("=invoiceItem=="+invoiceItem);
			purchaseGrandTotMap["DR"]+=invTotalVal;
			purchaseGrandTotMap["total"]+=invTotalVal;
			
			innerTaxItemMap=[:];
			innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
			innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
			innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
			innerTaxItemMap["tinNumber"]="";
			innerTaxItemMap["vchrType"]="Purchase";
			innerTaxItemMap["crOrDbId"]="D";
			//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceItem.invoiceId);
			//invTotalVal=invTotalVal-vatRevenue;
			innerTaxItemMap["invTotalVal"]=invTotalVal;
			purchasExemptList.addAll(innerTaxItemMap);
			//innerTaxItemMap["vatAmount"]=vatRevenue;
			
		}
		prchaseCategoryDetaildMap["Purchase-Exempt-Total"]=purchasExemptList;
		prchaseCategorySummeryMap["Purchase-Exempt-Total"]=purchaseCatMap;
		
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//interState 
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
		
		invoiceItemsIter.each{invoiceItem->
			if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				BigDecimal cstRevenue = invoiceItem.cstAmount;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				purchaseInterCatMap["DR"]+=invTotalVal;
				purchaseInterCatMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
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
		
		invoiceItemsIter.each{invoiceItem->
			invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				purchaseDiselExCatMap["DR"]+=invTotalVal
				purchaseDiselExCatMap["total"]+=invTotalVal
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
		}
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
		
		invoiceItemsIter.each{invoiceItem->
			if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				BigDecimal cstRevenue = invoiceItem.cstAmount;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				purchaseDiselInterCatMap["DR"]+=invTotalVal;
				purchaseDiselInterCatMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
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
		
		invoiceItemsIter.each{invoiceItem->
			//if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,false);
				purchaseInterUnitStTrMap["DR"]+=invTotalVal;
				purchaseInterUnitStTrMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			//}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
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
		
		invoiceItemsIter.each{invoiceItem->
			//if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(invoiceItem,true);
				purchaseUnionWsdMap["DR"]+=invTotalVal;
				purchaseUnionWsdMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			//}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
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
		if(UtilValidate.isNotEmpty(tax14pt5TotalMap)){
			purchaseAt14pt5Map["DR"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
			purchaseAt14pt5Map["total"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
			
			purchaseGrandTotMap["DR"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
			purchaseGrandTotMap["total"]+=(tax14pt5TotalMap.get("invTotalVal")+tax14pt5TotalMap.get("vatAmount"));
		}
		prchaseCategorySummeryMap["Purchase-14.5%VAT"]=purchaseAt14pt5Map;
		
		purchaseAt5pt5Map=[:];
		purchaseAt5pt5Map["DR"]=BigDecimal.ZERO;
		purchaseAt5pt5Map["CR"]=BigDecimal.ZERO;
		purchaseAt5pt5Map["total"]=BigDecimal.ZERO;
		
		Map tax5pt5TotalMap=context.get("tax5pt5TotalMap");
		if(UtilValidate.isNotEmpty(tax5pt5TotalMap)){
			purchaseAt5pt5Map["DR"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
			purchaseAt5pt5Map["total"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
			
			purchaseGrandTotMap["DR"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
			purchaseGrandTotMap["total"]+=(tax5pt5TotalMap.get("invTotalVal")+tax5pt5TotalMap.get("vatAmount"));
		}
		prchaseCategorySummeryMap["Purchase-5.5%VAT"]=purchaseAt5pt5Map;
		
		
		//fright Total Valu
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
		
		invoiceItemsIter.each{invoiceItem->
			//if(UtilValidate.isNotEmpty((invoiceItem.cstPercent)&&(invoiceItem.cstAmount))){
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				purchaseFreightMap["DR"]+=invTotalVal;
				purchaseFreightMap["total"]+=invTotalVal;
				
				purchaseGrandTotMap["DR"]+=invTotalVal;
				purchaseGrandTotMap["total"]+=invTotalVal;
			//}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		prchaseCategorySummeryMap["Purchase-FreightCharges"]=purchaseFreightMap;
		
		prchaseCategorySummeryMap["Total"]=purchaseGrandTotMap;
		
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		//Debug.log("=prchaseCategorySummeryMap=="+prchaseCategorySummeryMap);
		
		context.prchaseCategorySummeryMap = prchaseCategorySummeryMap;
		context.prchaseCategoryDetaildMap = prchaseCategoryDetaildMap;
		
		
		taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX4"), false);
		taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX4"), false);
		context.taxParty = taxParty;
		context.taxAuthority = taxAuthority;
		invItemTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "VAT_PUR"), false);
		context.invItemTypeGl = invItemTypeGl;
// Purchase Abstract report


