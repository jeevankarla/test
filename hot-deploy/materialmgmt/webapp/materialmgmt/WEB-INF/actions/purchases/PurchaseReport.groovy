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
exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PUR_ANLS_CODE"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
//get product from ProductCategory
productCategoryMember = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
productCatMap=[:]
productPrimaryCatMap=[:]
productCategoryMember.each{prodCatMember ->
	prodCatMember.productId
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
	productPrimaryCatMap[prodCatMember.productCategoryId] = prodCatMember.primaryParentCategoryId;
}
invoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceIdTotals");
// Purchase abstract Sales report
reportTypeFlag = parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseDetails"){
		invoiceMap = [:];
		invoiceDtlsMap = [:];
		prodCatAnalysisMap =FastMap.newInstance();
		prodPrimaryCategoryMap =FastMap.newInstance();
		finalMap=FastMap.newInstance();
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
									cstRevenue = productValue.getValue().get("cstRevenue");
									if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.containsKey(productId)){
										// get category
										prodCategoryId=productCatMap.get(productId);
										prodPrimaryCategoryId=productPrimaryCatMap.get(prodCategoryId);
									if(UtilValidate.isEmpty(prodPrimaryCategoryMap[prodPrimaryCategoryId])){
										categoryInvoiceMap = [:];
										invoiceMap=[:];
										tempMap = [:];
										tempMap["invoiceDate"] = invoiceDate;
										tempMap["supInvNumber"] = supInvNumber;
										tempMap[productId] = invItemVal+cstRevenue;
										invoiceMap[invoiceId]=tempMap;
										temp=FastMap.newInstance();
										temp.putAll(invoiceMap);
										categoryInvoiceMap[prodCategoryId] = temp;
										prodPrimaryCategoryMap[prodPrimaryCategoryId]=categoryInvoiceMap;
										
									 }else{
									    categoryInvoiceMap = [:];
									    categoryInvoiceMap.putAll(prodPrimaryCategoryMap.get(prodPrimaryCategoryId));
										if(UtilValidate.isEmpty(categoryInvoiceMap[prodCategoryId])){
											invoiceMap=[:];
											tempMap = [:];
											tempMap["invoiceDate"] = invoiceDate;
											tempMap["supInvNumber"] = supInvNumber;
											tempMap[productId] = invItemVal+cstRevenue;
											invoiceMap[invoiceId]=tempMap;
											temp=FastMap.newInstance();
											temp.putAll(invoiceMap);
											categoryInvoiceMap[prodCategoryId] = temp;
											prodPrimaryCategoryMap[prodPrimaryCategoryId]=categoryInvoiceMap;
										}else{
										    catInvoiceMap = [:];
											catInvoiceMap.putAll(categoryInvoiceMap.get(prodCategoryId));
											if(UtilValidate.isEmpty(catInvoiceMap[invoiceId])){
												prodMap=[:];
												prodMap["invoiceDate"] = invoiceDate;
												prodMap["supInvNumber"] = supInvNumber;
												prodMap[productId] = invItemVal+cstRevenue;
												catInvoiceMap[invoiceId]=prodMap;
												temp=FastMap.newInstance();
												temp.putAll(catInvoiceMap);
												categoryInvoiceMap[prodCategoryId] = temp;
												prodPrimaryCategoryMap[prodPrimaryCategoryId]=categoryInvoiceMap;
											}else{
												  tempInvoiceMap=[:];
												  tempInvoiceMap.putAll(catInvoiceMap.get(invoiceId));
												  tempInvoiceMap[productId]=invItemVal+cstRevenue;
												  catInvoiceMap[invoiceId] = tempInvoiceMap;
												  temp=FastMap.newInstance();
												  temp.putAll(catInvoiceMap);
												  categoryInvoiceMap[prodCategoryId] = temp;
												  prodPrimaryCategoryMap[prodPrimaryCategoryId]=categoryInvoiceMap;
										}
									  
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
	context.put("dayWiseInvoice",prodPrimaryCategoryMap);
}
// Purchase Abstract report
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseSummary"){
				prodCatMap =FastMap.newInstance();
				prodPrimaryCategoryMap=FastMap.newInstance();
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
											prodPrimaryCategoryId=productPrimaryCatMap.get(prodCategoryId);
											if(UtilValidate.isEmpty(prodPrimaryCategoryMap[prodPrimaryCategoryId])){
												prodCatMap=[:];
												cstRevenue = productValue.getValue().get("cstRevenue");
												tempProdMap = FastMap.newInstance();
												tempProdMap["totalRevenue"] = invItemVal+cstRevenue;
												prodCatMap[prodCategoryId] = tempProdMap;
												prodPrimaryCategoryMap[prodPrimaryCategoryId]=prodCatMap;
											 }else{
												prodCatMap = [:];
												prodCatMap.putAll(prodPrimaryCategoryMap.get(prodPrimaryCategoryId));
													if(UtilValidate.isEmpty(prodCatMap[prodCategoryId])){
														cstRevenue = productValue.getValue().get("cstRevenue");
														tempProdMap = FastMap.newInstance();
														tempProdMap["totalRevenue"] = invItemVal+cstRevenue;
														prodCatMap[prodCategoryId] = tempProdMap;
													}else{
														tempMap = [:];
														productMap = [:];
														tempMap.putAll(prodCatMap.get(prodCategoryId));
														totalRevenue = productValue.getValue().get("totalRevenue");
														tempMap["totalRevenue"]+= invItemVal+productValue.getValue().get("cstRevenue");
														prodCatMap[prodCategoryId] = tempMap;
													}
													prodPrimaryCategoryMap[prodPrimaryCategoryId]=prodCatMap;
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
	Debug.log("prodCatMap==="+prodPrimaryCategoryMap)
	context.put("prodMap",prodPrimaryCategoryMap);
}

