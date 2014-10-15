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

// Purchase abstract Sales report
reportTypeFlag = parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseDetails"){
		invoiceMap = [:];
		invoiceDtlsMap = [:];
		tempVariantMap =FastMap.newInstance();
		finalMap=FastMap.newInstance();
			salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
			if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
				invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");
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
							if(UtilValidate.isNotEmpty(prodTotals)){
								prodTotals.each{productValue ->
									virtualProductId="";
									if(UtilValidate.isNotEmpty(productValue)){
										currentProduct = productValue.getKey();
										product = delegator.findOne("Product", [productId : currentProduct], false);
										productId = productValue.getKey();
											exprList=[];
											prodMap=[:];
											exprList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, "PURCHASE_ACCOUNT"));
											exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, product.primaryProductCategoryId));
											condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
											productcatList = delegator.findList("ProductCategoryGlAccount", condition, null, null, null, false);
												if(UtilValidate.isNotEmpty(productcatList)){
												 productcatList=EntityUtil.getFirst(productcatList);
												}
												if(UtilValidate.isNotEmpty(productcatList)){
													virtualProductId = productcatList.get("productCategoryId");
													if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
														totalRevenue = productValue.getValue().get("totalRevenue");
														invoiceMap=[:];
														tempMap = [:];
														tempMap["invoiceDate"] = invoiceDate;
														tempMap["supInvNumber"] = supInvNumber;
														tempMap[productId] = totalRevenue;
														invoiceMap[invoiceId]=tempMap;
														temp=FastMap.newInstance();
														temp.putAll(invoiceMap);
														tempVariantMap[virtualProductId] = temp;
													}else{
														tempMap = [:];
														tempMap.putAll(tempVariantMap.get(virtualProductId));
														if(UtilValidate.isEmpty(tempMap[invoiceId])){
															totalRevenue = productValue.getValue().get("totalRevenue");
															prodMap=[:];
															invoiceMap=[:]
															prodMap["invoiceDate"] = invoiceDate;
															prodMap["supInvNumber"] = supInvNumber;
															prodMap[productId] = totalRevenue;
															tempMap[invoiceId]=prodMap;
															temp=FastMap.newInstance();
															temp.putAll(tempMap);
															tempVariantMap[virtualProductId] = temp;
														}else{
														    invoiceMap=[:];
															productMap = [:];
															totalRevenue = productValue.getValue().get("totalRevenue");
															tempInvoiceMap=[:];
															tempInvoiceMap.putAll(tempMap.get(invoiceId));
															tempInvoiceMap[productId]=totalRevenue;
															tempMap[invoiceId] = tempInvoiceMap;
															temp=FastMap.newInstance();
															temp.putAll(tempMap);
															tempVariantMap[virtualProductId] = temp;
														}
													}
										    } 
									}
								}
							}
						}
					}
				}
			}
	context.put("dayWiseInvoice",tempVariantMap);
}
// Purchase Abstract report
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseSummary"){
	finalInvoiceDateMap = [:];
			salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
			if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
				invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");
				tempVariantMap =FastMap.newInstance();
				if(UtilValidate.isNotEmpty(invoiceTotals)){
					invoiceTotals.each { invoice ->
						if(UtilValidate.isNotEmpty(invoice)){
							prodTotals = invoice.getValue().get("productTotals");
							if(UtilValidate.isNotEmpty(prodTotals)){
								prodTotals.each{productValue ->
									virtualProductId="";
									if(UtilValidate.isNotEmpty(productValue)){
										currentProduct = productValue.getKey();
										product = delegator.findOne("Product", [productId : currentProduct], false);
										productId = productValue.getKey();
											exprList=[];
											exprList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, "PURCHASE_ACCOUNT"));
											exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, product.primaryProductCategoryId));
											condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
											productcatList = delegator.findList("ProductCategoryGlAccount", condition, null, null, null, false);
												if(UtilValidate.isNotEmpty(productcatList)){
												 productcatList=EntityUtil.getFirst(productcatList);
												}
												if(UtilValidate.isNotEmpty(productcatList)){
													virtualProductId = productcatList.get("productCategoryId");
													if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
														totalRevenue = productValue.getValue().get("totalRevenue");
														tempProdMap = FastMap.newInstance();
														tempProdMap["totalRevenue"] = totalRevenue;
														tempVariantMap[virtualProductId] = tempProdMap;
													}else{
														tempMap = [:];
														productMap = [:];
														tempMap.putAll(tempVariantMap.get(virtualProductId));
														totalRevenue = productValue.getValue().get("totalRevenue");
														tempMap["totalRevenue"]+= productValue.getValue().get("totalRevenue");
														tempVariantMap[virtualProductId] = tempMap;
													}
										    } 
									}
								}
							}
						}
					}
				}
			}
	context.put("reportTypeFlag",reportTypeFlag);
	context.put("prodMap",tempVariantMap);
}

