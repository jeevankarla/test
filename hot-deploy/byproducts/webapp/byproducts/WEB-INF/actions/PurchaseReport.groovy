import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
exprList=[];
exprList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, "PURCHASE_ACCOUNT"));
//exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, product.primaryProductCategoryId));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
productcatList = delegator.findList("ProductCategoryGlAccount", condition, null, null, null, false);
productCategoryId = EntityUtil.getFieldListFromEntityList(productcatList, "productCategoryId", true);
//get product from ProductCategory
productCategoryMember = delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryId), null, null, null, false);
productCatMap=[:]
productCategoryMember.each{prodCatMember ->
	prodCatMember.productId
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
}
productIds = EntityUtil.getFieldListFromEntityList(productCategoryMember, "productId", true);

prodGlCategoryList=[];
if(UtilValidate.isNotEmpty(productcatList)){
	prodGlCategoryList =EntityUtil.getFieldListFromEntityList(productcatList, "productCategoryId", true) ;
}
invoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceIdTotals");
// Purchase abstract Sales report
reportTypeFlag = parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseDetails"){
		invoiceMap = [:];
		invoiceDtlsMap = [:];
		prodCatAnalysisMap =FastMap.newInstance();
		finalMap=FastMap.newInstance();
		/*salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
		if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
			invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");*/
			if(UtilValidate.isNotEmpty(invoiceTotals)){
				invoiceTotals.each { invoice ->
					if(UtilValidate.isNotEmpty(invoice)){
						invoiceId = "";
						totalRevenue=0;
						invoiceId = invoice.getKey();
						if(UtilValidate.isNotEmpty(invoice.getValue().invoiceDateStr)){
							invoiceDate = invoice.getValue().invoiceDateStr;
						}
						invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
						supInvNumber="";
						orderItemBillingList = delegator.findList("OrderItemBilling",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , null, null, null, false );
						if(UtilValidate.isNotEmpty(orderItemBillingList)){
							orderItemBilling = EntityUtil.getFirst(orderItemBillingList);
							if(UtilValidate.isNotEmpty(orderItemBilling)){
								orderId = orderItemBilling.orderId;
								if(UtilValidate.isNotEmpty(orderId)){
									supInvOrderAttributeDetails = delegator.findOne("OrderAttribute", [orderId : orderId, attrName : "SUP_INV_NUMBER"], false);
									if(UtilValidate.isNotEmpty(supInvOrderAttributeDetails)){
										supInvNumber = supInvOrderAttributeDetails.attrValue;
									}
								}
							}
						}
						
						prodTotals = invoice.getValue().get("productTotals");
						invoiceItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , null, null, null, false );
						
						if(UtilValidate.isNotEmpty(prodTotals)){
							prodTotals.each{productValue ->
								prodCategoryId="";
								if(UtilValidate.isNotEmpty(productValue)){
									currentProduct = productValue.getKey();
									product = delegator.findOne("Product", [productId : currentProduct], false);
									productId = productValue.getKey();
									GenericValue prodInvoiceItem=null;
									List<GenericValue> prodInvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId),EntityOperator.AND,
										EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
									if(UtilValidate.isNotEmpty(prodInvoiceItemList)){
										prodInvoiceItem=prodInvoiceItemList.getFirst();
									}
									//to Exlude Tax Total call with Parameter False 
									invItemVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(prodInvoiceItem,false);
									if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
										// get category
										prodCategoryId=productCatMap.get(productId);
										if(UtilValidate.isEmpty(prodCatAnalysisMap[prodCategoryId])){
											//totalRevenue = productValue.getValue().get("totalRevenue");
											invoiceMap=[:];
											
											tempMap = [:];
											tempMap["invoiceDate"] = invoiceDate;
											tempMap["supInvNumber"] = supInvNumber;
											//tempMap[productId] = totalRevenue;
											tempMap[productId] = invItemVal;
											
											invoiceMap[invoiceId]=tempMap;
											
											temp=FastMap.newInstance();
											temp.putAll(invoiceMap);
											
											prodCatAnalysisMap[prodCategoryId] = temp;
										}else{
											tempMap = [:];
											tempMap.putAll(prodCatAnalysisMap.get(prodCategoryId));
											if(UtilValidate.isEmpty(tempMap[invoiceId])){
												cstRevenue = productValue.getValue().get("cstRevenue");
												prodMap=[:];
												invoiceMap=[:]
												prodMap["invoiceDate"] = invoiceDate;
												prodMap["supInvNumber"] = supInvNumber;
												//prodMap[productId] = totalRevenue;
												prodMap[productId] = invItemVal+cstRevenue;
												tempMap[invoiceId]=prodMap;
												temp=FastMap.newInstance();
												temp.putAll(tempMap);
												prodCatAnalysisMap[prodCategoryId] = temp;
											}else{
												invoiceMap=[:];
												cstRevenue = productValue.getValue().get("cstRevenue");
												tempInvoiceMap=[:];
												tempInvoiceMap.putAll(tempMap.get(invoiceId));
												tempInvoiceMap[productId]=invItemVal+cstRevenue;
												tempMap[invoiceId] = tempInvoiceMap;
												temp=FastMap.newInstance();
												temp.putAll(tempMap);
												prodCatAnalysisMap[prodCategoryId] = temp;
											}
										}
										
									}
									//end of Classfication		
								}
							}
						}
					}
				}
			}
		//end if of salesInvoiceTotals}
	context.put("dayWiseInvoice",prodCatAnalysisMap);
}
// Purchase Abstract report
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseSummary"){
	finalInvoiceDateMap = [:];
			/*salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
			if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
				invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");*/
				prodCatAnalysisMap =FastMap.newInstance();
				if(UtilValidate.isNotEmpty(invoiceTotals)){
					invoiceTotals.each { invoice ->
						if(UtilValidate.isNotEmpty(invoice)){
							invoiceId = invoice.getKey();
							prodTotals = invoice.getValue().get("productTotals");
							invoiceItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , null, null, null, false );
							if(UtilValidate.isNotEmpty(prodTotals)){
								prodTotals.each{productValue ->
									prodCategoryId="";
									if(UtilValidate.isNotEmpty(productValue)){
										currentProduct = productValue.getKey();
										product = delegator.findOne("Product", [productId : currentProduct], false);
										productId = productValue.getKey();
										GenericValue prodInvoiceItem=null;
										List<GenericValue> prodInvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId),EntityOperator.AND,
											EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
										if(UtilValidate.isNotEmpty(prodInvoiceItemList)){
											prodInvoiceItem=prodInvoiceItemList.getFirst();
										}
										//to Exlude Tax Total call with Parameter False 
										invItemVal=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(prodInvoiceItem,false);
										if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
											// get category
											prodCategoryId=productCatMap.get(productId);
													prodCategoryId =prodCategoryId;
													if(UtilValidate.isEmpty(prodCatAnalysisMap[prodCategoryId])){
														cstRevenue = productValue.getValue().get("cstRevenue");
														tempProdMap = FastMap.newInstance();
														tempProdMap["totalRevenue"] = invItemVal+cstRevenue;
														prodCatAnalysisMap[prodCategoryId] = tempProdMap;
													}else{
														tempMap = [:];
														productMap = [:];
														tempMap.putAll(prodCatAnalysisMap.get(prodCategoryId));
														totalRevenue = productValue.getValue().get("totalRevenue");
														tempMap["totalRevenue"]+= invItemVal+productValue.getValue().get("cstRevenue");
														prodCatAnalysisMap[prodCategoryId] = tempMap;
													}
										    } 
									}
								}
							}
						}
					}
				}
			//end if of salesInvoiceTotals}
	context.put("reportTypeFlag",reportTypeFlag);
	context.put("prodMap",prodCatAnalysisMap);
}

