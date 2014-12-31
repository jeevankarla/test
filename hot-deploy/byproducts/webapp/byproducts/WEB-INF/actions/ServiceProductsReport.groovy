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
import org.ofbiz.party.party.PartyHelper;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
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

dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
categoryType=parameters.categoryType;
exprList=[];
//exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "CONVERSION_CHARGES"));
exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryType));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
//get product from ProductCategory
productCategoryMember = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
productCatMap=[:]
productCategoryMember.each{prodCatMember ->
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
}
Debug.log("productCatMap==="+productCatMap);
dayWiseInvoice=FastMap.newInstance();
prodTempMap=[:];
// Invoice Sales Abstract
	finalInvoiceDateMap = [:];
	for( i=0 ; i <= (totalDays); i++){
		currentDay =UtilDateTime.addDaysToTimestamp(fromDateTime, i);
		dayBegin=UtilDateTime.getDayStart(currentDay);
		dayEnd=UtilDateTime.getDayEnd(currentDay);
		invoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isQuantityLtrs:true,isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceIdTotals");
		invoiceMap = [:];
		if(UtilValidate.isNotEmpty(invoiceTotals)){
				if(UtilValidate.isNotEmpty(invoiceTotals)){
					invoiceTotals.each { invoice ->
						if(UtilValidate.isNotEmpty(invoice)){
							invoiceId = "";
							partyName = "";
							idValue = "";
							basicRevenue=0;
							bedRevenue=0;
							vatRevenue=0;
							cstRevenue=0;
							totalRevenue=0;
							finalMap=FastMap.newInstance();
							invoiceId = invoice.getKey();
									invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
									invoicePartyId = invoiceDetails.partyId;
									prodTotals = invoice.getValue().get("productTotals");
									invQuantity=0;
									invBasicRevenue=0;
									invBedRevenue=0;
									invVatRevenue=0;
									invCstRevenue=0;
									InvTotal=0;
									totalMap=[:];
									quantity = invoice.getValue().get("total");
									basicRevenue = invoice.getValue().get("basicRevenue");
									bedRevenue = invoice.getValue().get("bedRevenue");
									vatRevenue =invoice.getValue().get("vatRevenue");
									cstRevenue = invoice.getValue().get("cstRevenue");
									totalRevenue = invoice.getValue().get("totalRevenue");
									totalMap["quantity"]=quantity;
									totalMap["basicRevenue"]=basicRevenue;
									totalMap["bedRevenue"]=bedRevenue;
									totalMap["vatRevenue"]=vatRevenue;
									totalMap["cstRevenue"]=cstRevenue;
									totalMap["totalRevenue"]=totalRevenue;
									tempVariantMap =FastMap.newInstance();
									if(UtilValidate.isNotEmpty(prodTotals)){
										prodTotals.each{productValue ->
											productCategoryId="";
											if(UtilValidate.isNotEmpty(productValue)){
												productId = productValue.getKey();
												if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.containsKey(productId)){
													//productCategoryId=productId;
													Debug.log("productCategoryId===="+productId);
														if(UtilValidate.isEmpty(tempVariantMap[productId])){
															quantity =productValue.getValue().get("total");
															basicRevenue = productValue.getValue().get("basicRevenue");
															bedRevenue=productValue.getValue().get("bedRevenue");
															vatRevenue = productValue.getValue().get("vatRevenue");
															cstRevenue = productValue.getValue().get("cstRevenue");
															totalRevenue = productValue.getValue().get("totalRevenue");
															tempProdMap = FastMap.newInstance();
															tempProdMap["quantity"] = quantity;
															tempProdMap["invoiceId"]=invoiceId;
															if(basicRevenue>0){
															 tempProdMap["basicRevenue"] = basicRevenue;
															}else{
															 basicRevenue=0;
															 tempProdMap["basicRevenue"] = 0;
															}
															if(bedRevenue>0){
																tempProdMap["bedRevenue"] = bedRevenue;
															}else{
																bedRevenue=0;
																tempProdMap["bedRevenue"] = 0;
															}
															if(vatRevenue>0){
																tempProdMap["vatRevenue"] = vatRevenue;
															}else{
																vatRevenue=0;
																tempProdMap["vatRevenue"] = 0;
															}
															if(cstRevenue>0){
															   tempProdMap["cstRevenue"] = cstRevenue;
															}else{
															   cstRevenue=0;
															   tempProdMap["cstRevenue"] = 0;
															}
															if(totalRevenue>0){
															 tempProdMap["totalRevenue"] = totalRevenue;
															}else{
															 tempProdMap["totalRevenue"] = 0;
															}
															  temp=FastMap.newInstance();
															  temp.putAll(tempProdMap);
															  tempVariantMap[productId] = temp;
															  if(UtilValidate.isEmpty(prodTempMap[productId])){
															     productTemp =[:]
															     productTemp.put("qtyLtrs", productValue.getValue().get("total"));
															     productTemp.put("amount" , productValue.getValue().get("totalRevenue"));
															     prodTempMap.put(productId, productTemp);
															  }else{
																  prodTotMap=[:];
																  prodTotMap.putAll(prodTempMap.get(productId));
																  prodTotMap["qtyLtrs"]+= productValue.getValue().get("total");
																  prodTotMap["amount"]+= productValue.getValue().get("totalRevenue");
																  prodTempMap[productId] = prodTotMap;
															  }
														}else{
															tempMap = [:];
															productMap = [:];
															tempMap.putAll(tempVariantMap.get(productId));
															productMap.putAll(tempMap);
															
															quantity =productValue.getValue().get("total");
															basicRevenue = productValue.getValue().get("basicRevenue");
															bedRevenue=productValue.getValue().get("bedRevenue");
															vatRevenue = productValue.getValue().get("vatRevenue");
															cstRevenue = productValue.getValue().get("cstRevenue");
															totalRevenue = productValue.getValue().get("totalRevenue");
															productMap["quantity"] += productValue.getValue().get("total");
															productMap["basicRevenue"] += productValue.getValue().get("basicRevenue");
															productMap["bedRevenue"] += productValue.getValue().get("bedRevenue");
															productMap["vatRevenue"] += productValue.getValue().get("vatRevenue");
															productMap["cstRevenue"] += productValue.getValue().get("cstRevenue");
															productMap["totalRevenue"] += productValue.getValue().get("totalRevenue");
															temp=FastMap.newInstance();
															temp.putAll(productMap);
															tempVariantMap[productId] = temp;
														}
														finalMap.put("productTotals",tempVariantMap);
														finalMap.put("invTotals",totalMap);
											          }
												
										}
									   }
									}
								
								invoiceList = [];
								if(UtilValidate.isNotEmpty(finalMap)){
								if(UtilValidate.isNotEmpty(invoiceMap[invoiceId])){
									invoiceList = invoiceMap.get(invoiceId);
								}
								invoiceList.add(finalMap);
								invoiceMap[invoiceId] = invoiceList;
								}
							}
						}
					}
		}
		if(UtilValidate.isNotEmpty(invoiceMap)){
			tempMap = [:];
			tempMap.putAll(invoiceMap);
			dayWiseInvoice.put(dayBegin,tempMap);
		}
	}
	/*dayWiseInvoice.each{invoice->
	    invoiceMap=invoice.getValue();
		invoiceMap.each{invoiceDtls->
			productMap=invoiceDtls.getValue();
			productMap.each{prodDtls->
				productTotals=prodDtls.get("productTotals");
				productTotals.each{prod->
					Debug.log("prodTempMap====="+prodDtls);
					productId=prod.getKey();
					if(UtilValidate.isEmpty(prodTempMap[productId])){
						productTemp =[:]
						productTemp.put("qtyLtrs", prod.getValue().get("quantity"));
						productTemp.put("amount" , prod.getValue().get("totalRevenue"));
						prodTempMap.put(productId, productTemp);
					}else{
					   prodTotMap=[:];
					   prodTotMap.putAll(prodTempMap.get(productId));
					   prodTotMap["qtyLtrs"]+= prod.getValue().get("quantity");
					   prodTotMap["amount"]+= prod.getValue().get("totalRevenue");
					   prodTempMap[productId] = prodTotMap;
					}
				}
			}
	    }
	}*/
 //Debug.log("prodTempMap====="+prodTempMap);
context.dayWiseInvoice=dayWiseInvoice;
context.prodTempMap=prodTempMap;



