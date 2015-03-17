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
exprList=[];
exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PUR_ANLS_CODE"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
//get product from ProductCategory
productCategoryMember = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
purchaseAnalysisProductIdsList=EntityUtil.getFieldListFromEntityList(productCategoryMember, "productId", true);
productCatMap=[:]
productPrimaryCatMap=[:];
eachProdPrimaryCatagoryMap=[:];
productCategoryMember.each{prodCatMember ->
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
	productPrimaryCatMap[prodCatMember.productCategoryId] = prodCatMember.primaryParentCategoryId;
}

//get Product Catagory
exprList.clear();
exprList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, "PURCHASE_ACCOUNT"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
productcatList = delegator.findList("ProductCategoryGlAccount", condition, null, null, null, false);
productCategoryId = EntityUtil.getFieldListFromEntityList(productcatList, "productCategoryId", true);
//get product from ProductCategory
exprList.clear();
exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryId));
exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, purchaseAnalysisProductIdsList));
condition2 = EntityCondition.makeCondition(exprList, EntityOperator.AND);
productCategoryGlMember = delegator.findList("ProductCategoryAndMember",condition2 , null, null, null, false);

productCategoryGlMember.each{prodCatMember ->
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
	productPrimaryCatMap[prodCatMember.productCategoryId] = prodCatMember.productCategoryId;
}


reportTypeFlag = parameters.reportTypeFlag;
taxType=parameters.taxType;
issueToDeptId=parameters.issueToDeptId;
issueToDeptInvMap = [:];
orgList=[];
   if(UtilValidate.isNotEmpty(parameters.purchaseTaxDeptFlag)){
	   orgList=context.getAt("orgList");
	   //get all InvoiceRole for Purchases
	   exprList.clear();
	   exprList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
	   exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	   exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
	   exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	   conditionInvRole = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	   allInvoiceRoleList = delegator.findList("InvoiceAndRole", conditionInvRole , null, null, null, false );
	   allInvoiceIdsRoleList=EntityUtil.getFieldListFromEntityList(allInvoiceRoleList, "invoiceId", true);
	   allSupplyagentList = EntityUtil.filterByAnd(allInvoiceRoleList, [invoiceRoleTypeId : "SUPPLIER_AGENT"]);
	   InvoicePartyMap=[:];
	   allSupplyagentList.each { allSupplyagent ->
		   invoiceId = allSupplyagent.invoiceId;
			   partyId = allSupplyagent.invoiceRolePartyId;
			   InvoicePartyMap.put(invoiceId,partyId);
		   }	  
	   context.put("InvoicePartyMap",InvoicePartyMap);
	  // Debug.log("InvoicePartyMap=======from else========================="+InvoicePartyMap);
	   deptInvoiceIdRoleList=[];
	       orgList.each{orgDept->
			partyId=orgDept.partyId;
			invoiceRoleList = delegator.findList("InvoiceRole",EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),EntityOperator.AND,EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ISSUE_TO_DEPT"))  , null, null, null, false );
			invoiceIdsRoleList=EntityUtil.getFieldListFromEntityList(invoiceRoleList, "invoiceId", true);
			if(UtilValidate.isNotEmpty(invoiceIdsRoleList)){
			populateDeptInvoiceDetail(partyId,invoiceIdsRoleList);
			deptInvoiceIdRoleList.addAll(invoiceIdsRoleList);
			}
			}
		   //call for Invoices RoleIds OtherThan Dept 
		   exprList.clear();
		   exprList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
		   exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		   exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		   exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		   //Debug.log("allInvoiceIdsRoleList==========================================="+allInvoiceIdsRoleList);
		   if(UtilValidate.isNotEmpty(allInvoiceIdsRoleList)){
		   exprList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, allInvoiceIdsRoleList));
		   }
		   if(UtilValidate.isNotEmpty(deptInvoiceIdRoleList)){
		   exprList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.NOT_IN, deptInvoiceIdRoleList));
		   }
		   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		   invoiceRoleOthrList = delegator.findList("InvoiceAndRole", condition, null, null, null, false);
		   invoiceRoleOthrIdsList=EntityUtil.getFieldListFromEntityList(invoiceRoleOthrList, "invoiceId", true);
		  // Debug.log("invoiceRoleOthrIdsList============>"+invoiceRoleOthrIdsList);
		   /*if(UtilValidate.isNotEmpty(invoiceRoleOthrIdsList) && UtilValidate.isEmpty(parameters.issueToDeptId)){
		   populateDeptInvoiceDetail("Other",invoiceRoleOthrIdsList);
		   }*/
		   if(UtilValidate.isEmpty(parameters.issueToDeptId)){
			   populateDeptInvoiceDetail("Other",invoiceRoleOthrIdsList);
			 }
    }else{
	dummyList=[];
        populateDeptInvoiceDetail("",dummyList);
		exprList.clear();
		exprList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		exprList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		exprList.add(EntityCondition.makeCondition("invoiceRoleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
		conditionInvRole = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		allSupplyagentList = delegator.findList("InvoiceAndRole", conditionInvRole , null, null, null, false );
		//Debug.log("allSupplyagentList==========================================="+allSupplyagentList);
		//allSupplyagentList = EntityUtil.filterByAnd(allInvoiceRoleList, [invoiceRoleTypeId : "SUPPLIER_AGENT"]);
		//Debug.log("allSupplyagentList==========================================="+allSupplyagentList);
		//allSupplyagentIdsList=EntityUtil.getFieldListFromEntityList(allSupplyagentList, "invoiceId", true);
		//Debug.log("allSupplyagentIdsList==============from else============================="+allSupplyagentIdsList);
		InvoicePartyMap=[:];
		allSupplyagentList.each { allSupplyagent ->
			invoiceId = allSupplyagent.invoiceId;
			partyId = allSupplyagent.invoiceRolePartyId;
				InvoicePartyMap.put(invoiceId,partyId);
		}
		context.put("InvoicePartyMap",InvoicePartyMap);
		//Debug.log("InvoicePartyMap=======from else========================="+InvoicePartyMap);
    }
//function  for Each Dept	
def populateDeptInvoiceDetail(departmentId, invoiceIdsList){
	EntityListIterator invoiceItemsIter = null;
	List taxDetails5pt5List=[];
	List taxDetails5pt0List=[];
	List taxDetails14pt5List=[];
	taxDetailsOthrList=[];

	taxDetails5pt5Map=[:];
	taxDetails5pt0Map=[:];
	taxDetails14pt5Map=[:];

	//Debug.log("invoiceIdsList====="+invoiceIdsList);
	tax5pt5TotalMap=[:];
	tax5pt5TotalMap["invTotalVal"]=BigDecimal.ZERO;
	tax5pt5TotalMap["vatAmount"]=BigDecimal.ZERO;
	
	tax5pt0TotalMap=[:];
	tax5pt0TotalMap["invTotalVal"]=BigDecimal.ZERO;
	tax5pt0TotalMap["vatAmount"]=BigDecimal.ZERO;
	
	tax14pt5TotalMap=[:];
	tax14pt5TotalMap["invTotalVal"]=BigDecimal.ZERO;
	tax14pt5TotalMap["vatAmount"]=BigDecimal.ZERO;

	taxExTotalMap=[:];
	taxExTotalMap["invTotalVal"]=BigDecimal.ZERO;
	taxExTotalMap["cstAmount"]=BigDecimal.ZERO;

	taxDetailsCstMap=[:];

	taxCstTotalMap=[:];
	taxCstTotalMap["invTotalVal"]=BigDecimal.ZERO;
	taxCstTotalMap["cstAmount"]=BigDecimal.ZERO;


	tax5pt5CatMap=[:];
	tax5pt5CatMap["discount"]=BigDecimal.ZERO;
	
	tax5pt0CatMap=[:];
	tax5pt0CatMap["discount"]=BigDecimal.ZERO;


	tax14pt5CatMap=[:];
	tax14pt5CatMap["discount"]=BigDecimal.ZERO;

	taxCstCatMap=[:];
	taxCstCatMap["discount"]=BigDecimal.ZERO;

	invoiceMap = [:];
	invoiceDtlsMap = [:];

	try {
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
		conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
		if (UtilValidate.isNotEmpty(invoiceIdsList)) {
		 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsList));
		 }
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
		invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
	} catch (GenericEntityException e) {
		Debug.logError(e, module);
	}
	invoiceItemsIter.each{invoiceItem->
		//innerTaxItemMap=[:];
		//Debug.log("invoiceItem.invoiceId====="+invoiceItem.invoiceId);
		if(UtilValidate.isNotEmpty(invoiceItem.vatPercent) && UtilValidate.isNotEmpty(invoiceItem.vatAmount)){
			if(invoiceItem.vatPercent==5.5){
				BigDecimal vatRevenue = invoiceItem.vatAmount;
				productId = invoiceItem.productId;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				BigDecimal totalBed = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
					totalBed+=invoiceItem.bedAmount;
				}
				if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
					totalBed+=invoiceItem.bedcessAmount;
				}
				if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
					totalBed+=invoiceItem.bedseccessAmount;
				}
				invTotalVal+=totalBed;

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
				innerItemMap["vatAmount"]=vatRevenue;
				taxDetails5pt5List.addAll(innerItemMap);
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					prodPrimaryCategoryId=productPrimaryCatMap.get(prodCategoryId);
					if(UtilValidate.isEmpty(tax5pt5CatMap[prodPrimaryCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["vatAmount"]=vatRevenue;
						
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						//inside category ProductWise starts
						productMap=[:];
						if(UtilValidate.isEmpty(productMap[prodCategoryId])){
							innerProdMap=[:];
							innerProdMap["totalValue"]=invTotalVal;
							innerProdMap["taxAmount"]=0;
							prodInvItemList=[];
							prodInvItemList.addAll(innerItemMap);
							innerProdMap["prodInvItemList"]=prodInvItemList;
							productMap[prodCategoryId]=innerProdMap;
						}
						innerTaxCatMap["productDetailMap"]=productMap;
						tax5pt5CatMap[prodPrimaryCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(tax5pt5CatMap[prodPrimaryCategoryId])){
						Map innerTaxCatMap=tax5pt5CatMap[prodPrimaryCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["vatAmount"]+=vatRevenue;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						//update proddetailsMap
						updateProductMap=innerTaxCatMap["productDetailMap"];
						if(UtilValidate.isEmpty(updateProductMap[prodCategoryId])){
							innerProdMap=[:];
							innerProdMap["totalValue"]=invTotalVal;
							innerProdMap["taxAmount"]=0;
							prodInvItemList=[];
							prodInvItemList.addAll(innerItemMap);
							innerProdMap["prodInvItemList"]=prodInvItemList;
							updateProductMap[prodCategoryId]=innerProdMap;
						}else{
						innerProdMap=updateProductMap[prodCategoryId];
						innerProdMap["totalValue"]+=invTotalVal;
						innerProdMap["taxAmount"]+=0;
						prodInvItemList=innerProdMap["prodInvItemList"];
						prodInvItemList.addAll(innerItemMap);
						innerProdMap["prodInvItemList"]=prodInvItemList;
						updateProductMap[prodCategoryId]=innerProdMap;
						}
						innerTaxCatMap["productDetailMap"]=updateProductMap;
					   //productWise update ends
						tax5pt5CatMap[prodPrimaryCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here

				invDetailMap=taxDetails5pt5Map[invoiceItem.invoiceId];
				if(UtilValidate.isEmpty(invDetailMap)){
					innerTaxItemMap=[:];
					innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
					innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
					innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
					innerTaxItemMap["tinNumber"]="";
					innerTaxItemMap["vchrType"]="Purchase";
					innerTaxItemMap["crOrDbId"]="D";
					invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
							EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_DISC"))  , null, null, null, false );
					if(UtilValidate.isNotEmpty(invoiceDisItemList)){
						discountInvoiceItem=invoiceDisItemList.getFirst();
						invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
						tax5pt5CatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
					}

					innerTaxItemMap["invTotalVal"]=invTotalVal;
					innerTaxItemMap["vatAmount"]=vatRevenue;
					tax5pt5TotalMap["invTotalVal"]+=invTotalVal;
					tax5pt5TotalMap["vatAmount"]+=vatRevenue;
					fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
					if(UtilValidate.isNotEmpty(fromPartyDetail)){
						innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
					}
					//intilize inner map when empty
					taxDetails5pt5Map[invoiceItem.invoiceId]=innerTaxItemMap;
				}else if(UtilValidate.isNotEmpty(invDetailMap)){
					invDetailMap["vatAmount"]+=vatRevenue;
					tax5pt5TotalMap["vatAmount"]+=vatRevenue;

					invDetailMap["invTotalVal"]+=invTotalVal;
					tax5pt5TotalMap["invTotalVal"]+=invTotalVal;

					taxDetails5pt5Map[invoiceItem.invoiceId]=invDetailMap;
				}
			}
			//vat 5.0 type  adding here
					if(invoiceItem.vatPercent==5.0){
					BigDecimal vatRevenue = invoiceItem.vatAmount;
					productId = invoiceItem.productId;
					invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
					BigDecimal totalBed = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
						totalBed+=invoiceItem.bedAmount;
					}
					if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
						totalBed+=invoiceItem.bedcessAmount;
					}
					if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
						totalBed+=invoiceItem.bedseccessAmount;
					}
					invTotalVal+=totalBed;
		
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
					innerItemMap["vatAmount"]=vatRevenue;
					taxDetails5pt0List.addAll(innerItemMap);
					// get category
					if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
						prodCategoryId=productCatMap.get(productId);
						prodPrimaryCategoryId=productPrimaryCatMap.get(prodCategoryId);
						if(UtilValidate.isEmpty(tax5pt0CatMap[prodPrimaryCategoryId])){
							innerTaxCatMap=[:];
							innerTaxCatMap["totalValue"]=invTotalVal;
							innerTaxCatMap["vatAmount"]=vatRevenue;
							
							invoiceList=[];
							invoiceList.addAll(innerItemMap);
							innerTaxCatMap["invoiceList"]=invoiceList;
							//inside category ProductWise starts
							productMap=[:];
							if(UtilValidate.isEmpty(productMap[prodCategoryId])){
								innerProdMap=[:];
								innerProdMap["totalValue"]=invTotalVal;
								innerProdMap["taxAmount"]=0;
								prodInvItemList=[];
								prodInvItemList.addAll(innerItemMap);
								innerProdMap["prodInvItemList"]=prodInvItemList;
								productMap[prodCategoryId]=innerProdMap;
							}
							innerTaxCatMap["productDetailMap"]=productMap;
							tax5pt0CatMap[prodPrimaryCategoryId]=innerTaxCatMap;
						}else if(UtilValidate.isNotEmpty(tax5pt0CatMap[prodPrimaryCategoryId])){
							Map innerTaxCatMap=tax5pt0CatMap[prodPrimaryCategoryId];
							innerTaxCatMap["totalValue"]+=invTotalVal;
							innerTaxCatMap["vatAmount"]+=vatRevenue;
							invoiceList=innerTaxCatMap["invoiceList"];
							invoiceList.addAll(innerItemMap);
							innerTaxCatMap["invoiceList"]=invoiceList;
							//update proddetailsMap
							updateProductMap=innerTaxCatMap["productDetailMap"];
							if(UtilValidate.isEmpty(updateProductMap[prodCategoryId])){
								innerProdMap=[:];
								innerProdMap["totalValue"]=invTotalVal;
								innerProdMap["taxAmount"]=0;
								prodInvItemList=[];
								prodInvItemList.addAll(innerItemMap);
								innerProdMap["prodInvItemList"]=prodInvItemList;
								updateProductMap[prodCategoryId]=innerProdMap;
							}else{
							innerProdMap=updateProductMap[prodCategoryId];
							innerProdMap["totalValue"]+=invTotalVal;
							innerProdMap["taxAmount"]+=0;
							prodInvItemList=innerProdMap["prodInvItemList"];
							prodInvItemList.addAll(innerItemMap);
							innerProdMap["prodInvItemList"]=prodInvItemList;
							updateProductMap[prodCategoryId]=innerProdMap;
							}
							innerTaxCatMap["productDetailMap"]=updateProductMap;
						   //productWise update ends
							tax5pt0CatMap[prodPrimaryCategoryId]=innerTaxCatMap;
						}
					}
					//category ends here
		
					invDetailMap=taxDetails5pt0Map[invoiceItem.invoiceId];
					if(UtilValidate.isEmpty(invDetailMap)){
						innerTaxItemMap=[:];
						innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
						innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
						innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
						innerTaxItemMap["tinNumber"]="";
						innerTaxItemMap["vchrType"]="Purchase";
						innerTaxItemMap["crOrDbId"]="D";
						invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
								EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_DISC"))  , null, null, null, false );
						if(UtilValidate.isNotEmpty(invoiceDisItemList)){
							discountInvoiceItem=invoiceDisItemList.getFirst();
							invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
							tax5pt0CatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
						}
		
						innerTaxItemMap["invTotalVal"]=invTotalVal;
						innerTaxItemMap["vatAmount"]=vatRevenue;
						tax5pt0TotalMap["invTotalVal"]+=invTotalVal;
						tax5pt0TotalMap["vatAmount"]+=vatRevenue;
						fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
						if(UtilValidate.isNotEmpty(fromPartyDetail)){
							innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
						}
						//intilize inner map when empty
						taxDetails5pt0Map[invoiceItem.invoiceId]=innerTaxItemMap;
					}else if(UtilValidate.isNotEmpty(invDetailMap)){
						invDetailMap["vatAmount"]+=vatRevenue;
						tax5pt0TotalMap["vatAmount"]+=vatRevenue;
		
						invDetailMap["invTotalVal"]+=invTotalVal;
						tax5pt0TotalMap["invTotalVal"]+=invTotalVal;
		
						taxDetails5pt0Map[invoiceItem.invoiceId]=invDetailMap;
					}
				}
			//vat 14.5
			if(invoiceItem.vatPercent==14.5){
				productId = invoiceItem.productId;
				BigDecimal vatRevenue = invoiceItem.vatAmount;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				BigDecimal totalBed = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
					totalBed+=invoiceItem.bedAmount;
				}
				if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
					totalBed+=invoiceItem.bedcessAmount;
				}
				if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
					totalBed+=invoiceItem.bedseccessAmount;
				}
				invTotalVal+=totalBed;

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
				innerItemMap["vatAmount"]=vatRevenue;
				innerItemMap["invTotalVal"]=invTotalVal;
				taxDetails14pt5List.addAll(innerItemMap);
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					prodPrimaryCategoryId=productPrimaryCatMap.get(prodCategoryId);
					if(UtilValidate.isEmpty(tax14pt5CatMap[prodPrimaryCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["vatAmount"]=vatRevenue;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						//inside category ProductWise starts
						productMap=[:];
						if(UtilValidate.isEmpty(productMap[prodCategoryId])){
							innerProdMap=[:];
							innerProdMap["totalValue"]=invTotalVal;
							innerProdMap["taxAmount"]=0;
							prodInvItemList=[];
							prodInvItemList.addAll(innerItemMap);
							innerProdMap["prodInvItemList"]=prodInvItemList;
							productMap[prodCategoryId]=innerProdMap;
						}
						innerTaxCatMap["productDetailMap"]=productMap;
						tax14pt5CatMap[prodPrimaryCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(tax14pt5CatMap[prodPrimaryCategoryId])){
						Map innerTaxCatMap=tax14pt5CatMap[prodPrimaryCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["vatAmount"]+=vatRevenue;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						//update proddetailsMap
						updateProductMap=innerTaxCatMap["productDetailMap"];
						if(UtilValidate.isEmpty(updateProductMap[prodCategoryId])){
							innerProdMap=[:];
							innerProdMap["totalValue"]=invTotalVal;
							innerProdMap["taxAmount"]=0;
							prodInvItemList=[];
							prodInvItemList.addAll(innerItemMap);
							innerProdMap["prodInvItemList"]=prodInvItemList;
							updateProductMap[prodCategoryId]=innerProdMap;
						}else{
						innerProdMap=updateProductMap[prodCategoryId];
						innerProdMap["totalValue"]+=invTotalVal;
						innerProdMap["taxAmount"]+=0;
						prodInvItemList=innerProdMap["prodInvItemList"];
						prodInvItemList.addAll(innerItemMap);
						innerProdMap["prodInvItemList"]=prodInvItemList;
						updateProductMap[prodCategoryId]=innerProdMap;
						}
						innerTaxCatMap["productDetailMap"]=updateProductMap;
					   //productWise update ends
						tax14pt5CatMap[prodPrimaryCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				invDetailMap=taxDetails14pt5Map[invoiceItem.invoiceId];
				if(UtilValidate.isEmpty(invDetailMap)){
					innerTaxItemMap=[:];
					innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
					innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
					innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;

					innerTaxItemMap["tinNumber"]="";
					fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
					if(UtilValidate.isNotEmpty(fromPartyDetail)){
						innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
					}
					innerTaxItemMap["vchrType"]="Purchase";
					innerTaxItemMap["crOrDbId"]="D";
					//to get Discount Item
					invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
							EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_DISC")) , null, null, null, false );
					if(UtilValidate.isNotEmpty(invoiceDisItemList)){
						discountInvoiceItem=invoiceDisItemList.getFirst();
						invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
						tax14pt5CatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
					}
					//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceItem.invoiceId);

					//invTotalVal=invTotalVal-vatRevenue;
					innerTaxItemMap["invTotalVal"]=invTotalVal;
					innerTaxItemMap["vatAmount"]=vatRevenue;

					tax14pt5TotalMap["invTotalVal"]+=invTotalVal;
					tax14pt5TotalMap["vatAmount"]+=vatRevenue;
					//taxDetails14pt5List.addAll(innerTaxItemMap);
					//intilize inner map when empty
					taxDetails14pt5Map[invoiceItem.invoiceId]=innerTaxItemMap;
					//Debug.log("=invoiceId==FOR FOURTEEnnn=="+invoiceItem.invoiceId+"==ANdAmouunt=="+invoiceItem.vatAmount+"==percent="+invoiceItem.vatPercent+"=Total="+invTotalVal);
				}else if(UtilValidate.isNotEmpty(invDetailMap)){
					invDetailMap["vatAmount"]+=vatRevenue;
					tax14pt5TotalMap["vatAmount"]+=vatRevenue;

					invDetailMap["invTotalVal"]+=invTotalVal;
					tax14pt5TotalMap["invTotalVal"]+=invTotalVal;

					taxDetails14pt5Map[invoiceItem.invoiceId]=invDetailMap;
				}
			}
		}
		//Caliculating CST
		if(UtilValidate.isNotEmpty(invoiceItem.cstPercent) && UtilValidate.isNotEmpty(invoiceItem.cstAmount)){
			productId = invoiceItem.productId;
			BigDecimal cstAmount = invoiceItem.cstAmount;
			invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
			BigDecimal totalBed = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
				totalBed+=invoiceItem.bedAmount;
			}
			if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
				totalBed+=invoiceItem.bedcessAmount;
			}
			if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
				totalBed+=invoiceItem.bedseccessAmount;
			}
			invTotalVal+=totalBed;


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
			innerItemMap["cstAmount"]=cstAmount;
			innerItemMap["invTotalVal"]=invTotalVal;
			taxDetailsOthrList.addAll(innerItemMap);
			// get category
			if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
				prodCategoryId=productCatMap.get(productId);
				if(UtilValidate.isEmpty(taxCstCatMap[prodCategoryId])){
					innerTaxCatMap=[:];
					innerTaxCatMap["totalValue"]=invTotalVal;
					innerTaxCatMap["cstAmount"]=cstAmount;
					invoiceList=[];
					invoiceList.addAll(innerItemMap);
					innerTaxCatMap["invoiceList"]=invoiceList;
					taxCstCatMap[prodCategoryId]=innerTaxCatMap;
				}else if(UtilValidate.isNotEmpty(taxCstCatMap[prodCategoryId])){
					Map innerTaxCatMap=taxCstCatMap[prodCategoryId];
					innerTaxCatMap["totalValue"]+=invTotalVal;
					innerTaxCatMap["cstAmount"]+=cstAmount;
					invoiceList=innerTaxCatMap["invoiceList"];
					invoiceList.addAll(innerItemMap);
					innerTaxCatMap["invoiceList"]=invoiceList;
					taxCstCatMap[prodCategoryId]=innerTaxCatMap;
				}
			}
			//category ends here
			invDetailMap=taxDetailsCstMap[invoiceItem.invoiceId];
			if(UtilValidate.isEmpty(invDetailMap)){
				innerTaxItemMap=[:];
				innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerTaxItemMap["tinNumber"]="";
				fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
				if(UtilValidate.isNotEmpty(fromPartyDetail)){
					innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
				}
				innerTaxItemMap["vchrType"]="Purchase";
				innerTaxItemMap["crOrDbId"]="D";
				//to get Discount Item
				invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
						EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_DISC"))  , null, null, null, false );
				if(UtilValidate.isNotEmpty(invoiceDisItemList)){
					discountInvoiceItem=invoiceDisItemList.getFirst();
					invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
					taxCstCatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
				}
				innerTaxItemMap["invTotalVal"]=invTotalVal;
				innerTaxItemMap["cstAmount"]=cstAmount;

				taxCstTotalMap["invTotalVal"]+=invTotalVal;
				taxCstTotalMap["cstAmount"]+=cstAmount;

				taxDetailsCstMap[invoiceItem.invoiceId]=innerTaxItemMap;
				//Debug.log("=invoiceId==FOR FOURTEEnnn=="+invoiceItem.invoiceId+"==ANdAmouunt=="+invoiceItem.vatAmount+"==percent="+invoiceItem.vatPercent+"=Total="+invTotalVal);
			}else if(UtilValidate.isNotEmpty(invDetailMap)){
				invDetailMap["cstAmount"]+=cstAmount;
				taxCstTotalMap["cstAmount"]+=cstAmount;

				invDetailMap["invTotalVal"]+=invTotalVal;
				taxCstTotalMap["invTotalVal"]+=invTotalVal;

				taxDetailsCstMap[invoiceItem.invoiceId]=invDetailMap;
			}

		}
	}
	//Debug.log("=taxDetailsCstMap==="+taxDetailsCstMap);
	if (invoiceItemsIter != null) {
		try {
			invoiceItemsIter.close();
		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
		}
	}
	if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT5PT5")){
		context.put("taxDetails5pt5List",taxDetails5pt5Map.entrySet());
	}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT5PT0")){
		context.put("taxDetails5pt0List",taxDetails5pt0Map.entrySet());
	}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT14PT5")){
		context.put("taxDetails14pt5List",taxDetails14pt5Map.entrySet());
	}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="CST")){
		context.put("taxDetailsCstList",taxDetailsCstMap.entrySet());
	}else{
		context.put("taxDetails5pt5List",taxDetails5pt5Map.entrySet());
		context.put("taxDetails5pt0List",taxDetails5pt0Map.entrySet());
		context.put("taxDetails14pt5List",taxDetails14pt5Map.entrySet());
		context.put("taxDetailsCstList",taxDetailsCstMap.entrySet());
	}
	context.put("tax5pt5TotalMap",tax5pt5TotalMap);
	context.put("tax5pt0TotalMap",tax5pt0TotalMap);
	context.put("tax14pt5TotalMap",tax14pt5TotalMap);
	context.put("taxCstTotalMap",taxCstTotalMap);
	
	context.put("tax5pt0InvList",taxDetails5pt0List);
	context.put("tax5pt5InvList",taxDetails5pt5List);
	context.put("tax14pt5InvList",taxDetails14pt5List);
	//
	
	//preparing catageoryMap for Vat and CST
	context.put("tax5pt0CatMap",tax5pt0CatMap);
	context.put("tax5pt5CatMap",tax5pt5CatMap);
	context.put("tax14pt5CatMap",tax14pt5CatMap);
	context.put("taxCstCatMap",taxCstCatMap);
	

	Map tempDeptCatItemMap=FastMap.newInstance();
	
	if( UtilValidate.isNotEmpty(taxDetails5pt5Map) || UtilValidate.isNotEmpty(taxDetails5pt0Map) || UtilValidate.isNotEmpty(taxDetails14pt5Map) || UtilValidate.isNotEmpty(taxDetailsCstMap)){
		//Debug.log("taxType====FO======>"+taxType);
		 if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT5PT0") && UtilValidate.isNotEmpty(taxDetails5pt0Map) ){
			tempDeptCatItemMap.put("tax5pt0CatMap",tax5pt0CatMap);
			tempDeptCatItemMap.put("tax5pt0TotalMap",tax5pt0TotalMap);
			//Debug.log("taxType====5.0=====invokinggg>"+taxType+"====FFFFFFFFF");
		}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT5PT5") && UtilValidate.isNotEmpty(taxDetails5pt5Map) ){
			tempDeptCatItemMap.put("tax5pt5CatMap",tax5pt5CatMap);
			tempDeptCatItemMap.put("tax5pt5TotalMap",tax5pt5TotalMap);
			//Debug.log("taxType====5.5=====invokinggg>"+taxType);
		}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT14PT5") && UtilValidate.isNotEmpty(taxDetails14pt5Map)){
			tempDeptCatItemMap.put("tax14pt5CatMap",tax14pt5CatMap);
			tempDeptCatItemMap.put("tax14pt5TotalMap",tax14pt5TotalMap);
			//Debug.log("taxType====14.5=====invokinggg>"+taxType);
		}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="CST") && UtilValidate.isNotEmpty(taxDetailsCstMap)){
			tempDeptCatItemMap.put("taxCstCatMap",taxCstCatMap);
			tempDeptCatItemMap.put("taxCstTotalMap",taxCstTotalMap);
		}else if(UtilValidate.isEmpty(taxType)){
		
			if(UtilValidate.isNotEmpty(taxDetails5pt5Map)){
				tempDeptCatItemMap.put("tax5pt5CatMap",tax5pt5CatMap);
				tempDeptCatItemMap.put("tax5pt5TotalMap",tax5pt5TotalMap);
				//Debug.log("taxType==ELSEE==5.5====invokinggg>"+taxType);
				
			} 
			if(UtilValidate.isNotEmpty(taxDetails5pt0Map)){
				tempDeptCatItemMap.put("tax5pt0CatMap",tax5pt0CatMap);
				tempDeptCatItemMap.put("tax5pt0TotalMap",tax5pt0TotalMap);
				//Debug.log("taxType==ELSEE==5.0=====invokinggg>"+taxType);
			}
			if(UtilValidate.isNotEmpty(taxDetails14pt5Map)){
				tempDeptCatItemMap.put("tax14pt5CatMap",tax14pt5CatMap);
				tempDeptCatItemMap.put("tax14pt5TotalMap",tax14pt5TotalMap);
				Debug.log("taxType==ELSEE==14.5=====invokinggg>"+taxType);
				//Debug.log("==taxDetails5pt5Map=="+taxDetails14pt5Map+"==tempDeptCatItemMap="+tempDeptCatItemMap);
			}
			if(UtilValidate.isNotEmpty(taxDetailsCstMap)){
				tempDeptCatItemMap.put("taxCstCatMap",taxCstCatMap);
				tempDeptCatItemMap.put("taxCstTotalMap",taxCstTotalMap);
			}
		}
		if(UtilValidate.isNotEmpty(tempDeptCatItemMap)){
		issueToDeptInvMap[departmentId]=tempDeptCatItemMap;
		}
	}
}
		

		context.issueToDeptInvMap = issueToDeptInvMap;
		
			
		
		
		taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX4"), false);
		taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX4"), false);
		context.taxParty = taxParty;
		context.taxAuthority = taxAuthority;
		invItemTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "VAT_PUR"), false);
		context.invItemTypeGl = invItemTypeGl;
		//cst GL
		taxCstParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX9"), false);
		taxCstAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX9"), false);
		context.taxCstParty = taxCstParty;
		context.taxCstAuthority = taxCstAuthority;
		invItemCstTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "CST_PUR"), false);
		context.invItemCstTypeGl = invItemCstTypeGl;
		
// Purchase Abstract report


