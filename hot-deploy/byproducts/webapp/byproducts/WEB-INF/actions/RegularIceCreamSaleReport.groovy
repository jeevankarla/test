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
categoryType=parameters.categoryType;
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
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
maxIntervalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(maxIntervalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
partyIds=[];
if(categoryType.equals("ICE_CREAM_NANDINI")||categoryType.equals("All")){
nandiniPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "IC_WHOLESALE"]).get("partyIds");
partyIds.addAll(nandiniPartyIds);
}
if(categoryType.equals("ICE_CREAM_AMUL")||categoryType.equals("All")){
amulPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "EXCLUSIVE_CUSTOMER"]).get("partyIds");
partyIds.addAll(amulPartyIds);
}
dayWiseTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceIdTotals");
facilityMap=[:];
dayWiseInvoice=FastMap.newInstance();
// Populating sales for Ice cream products
List invoiceList=FastList.newInstance();
dayWiseTotals.each{eachInvoice ->
	invoiceMap=[:];
	finalMap=FastMap.newInstance();
	invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : eachInvoice.getKey()]);
	invoiceDateStr=eachInvoice.getValue().get("invoiceDateStr");
	//finalMap.put("invoiceDateStr",eachInvoice.getValue().get("invoiceDateStr"));
	//partyName = PartyHelper.getPartyName(delegator, invoice.partyId, false);
	facilityMap.put(eachInvoice.getKey(),invoice.partyId);
	prodTotals = eachInvoice.getValue().get("productTotals");
	tempVariantMap =FastMap.newInstance();
	if(UtilValidate.isNotEmpty(prodTotals)){
		prodTotals.each{productValue ->
			if(UtilValidate.isNotEmpty(productValue)){
				currentProduct = productValue.getKey();
				product = delegator.findOne("Product", [productId : currentProduct], false);
				productId = productValue.getKey();
				Debug.log("productId===="+productId);
					exprList=[];
					exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "IC_CAT_RPT"));
					exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				    condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
				    productList = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
					if(UtilValidate.isNotEmpty(productList)){
					 productList=EntityUtil.getFirst(productList);
			        }
				    
				if(UtilValidate.isNotEmpty(categoryType)&& categoryType.equals(product.primaryProductCategoryId)){
					if(categoryType.equals(product.primaryProductCategoryId)){
						if(UtilValidate.isNotEmpty(productList)){
							virtualProductId = productList.get("productCategoryId");
						}
						if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
							quantity =productValue.getValue().get("total");
							basicRevenue = productValue.getValue().get("basicRevenue");
							bedRevenue=productValue.getValue().get("bedRevenue");
							vatRevenue = productValue.getValue().get("vatRevenue");
							cstRevenue = productValue.getValue().get("cstRevenue");
							tempProdMap = FastMap.newInstance();
							tempProdMap["quantity"] = quantity;
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
							totalVal = basicRevenue+bedRevenue+vatRevenue+cstRevenue;
							if(totalVal>0){
							 tempProdMap["totalVal"] = totalVal;
							}else{
							 tempProdMap["totalVal"] = 0;
							}
							temp=FastMap.newInstance();
							temp.putAll(tempProdMap);
							tempVariantMap[virtualProductId] = temp;
						}else{
							tempMap = [:];
							productMap = [:];
							tempMap.putAll(tempVariantMap.get(virtualProductId));
							productMap.putAll(tempMap);
							
							quantity =productValue.getValue().get("total");
							basicRevenue = productValue.getValue().get("basicRevenue");
							bedRevenue=productValue.getValue().get("bedRevenue");
							vatRevenue = productValue.getValue().get("vatRevenue");
							cstRevenue = productValue.getValue().get("cstRevenue");
							totalVal = basicRevenue+bedRevenue+vatRevenue+cstRevenue;
							productMap["quantity"] += productValue.getValue().get("total");
							productMap["basicRevenue"] += productValue.getValue().get("basicRevenue");
							productMap["bedRevenue"] += productValue.getValue().get("bedRevenue");
							productMap["vatRevenue"] += productValue.getValue().get("vatRevenue");
							productMap["cstRevenue"] += productValue.getValue().get("cstRevenue");
							productMap["totalVal"] += totalVal;
							temp=FastMap.newInstance();;
							temp.putAll(productMap);
							tempVariantMap[virtualProductId] = temp;
						}
						
				 }
					
				}else if(UtilValidate.isNotEmpty(categoryType)&& categoryType.equals("All")){
						if(UtilValidate.isNotEmpty(productList)){
							virtualProductId = productList.get("productCategoryId");
						}
						if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
							quantity =productValue.getValue().get("total");
							basicRevenue = productValue.getValue().get("basicRevenue");
							bedRevenue=productValue.getValue().get("bedRevenue");
							vatRevenue = productValue.getValue().get("vatRevenue");
							cstRevenue = productValue.getValue().get("cstRevenue");
							tempProdMap = FastMap.newInstance();
							tempProdMap["quantity"] = quantity;
							if(basicRevenue>0){
							 tempProdMap["basicRevenue"] = basicRevenue;
							}else{
							 basicRevenue=0;
							 tempProdMap["basicRevenue"] = 0;
							}
							if(bedRevenue>0){
								tempProdMap["bedRevenue"] = bedRevenue;
							}else{
							    bedRevenue=0
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
							totalVal = basicRevenue+bedRevenue+vatRevenue+cstRevenue;
							if(totalVal>0){
								tempProdMap["totalVal"] = totalVal;
							}else{
							   tempProdMap["totalVal"]=0;
							}
							
							temp=FastMap.newInstance();
							temp.putAll(tempProdMap);
							tempVariantMap[virtualProductId] = temp;
						}else{
							tempMap = [:];
							productMap = [:];
							tempMap.putAll(tempVariantMap.get(virtualProductId));
							productMap.putAll(tempMap);
							
							quantity =productValue.getValue().get("total");
							basicRevenue = productValue.getValue().get("basicRevenue");
							bedRevenue=productValue.getValue().get("bedRevenue");
							vatRevenue = productValue.getValue().get("vatRevenue");
							cstRevenue = productValue.getValue().get("cstRevenue");
							
							productMap["quantity"] += productValue.getValue().get("total");
							productMap["basicRevenue"] += productValue.getValue().get("basicRevenue");
							productMap["bedRevenue"] += productValue.getValue().get("bedRevenue");
							productMap["vatRevenue"] += productValue.getValue().get("vatRevenue");
							productMap["cstRevenue"] += productValue.getValue().get("cstRevenue");
							totalVal = basicRevenue+bedRevenue+vatRevenue+cstRevenue;
							productMap["totalVal"] += totalVal;
							temp=FastMap.newInstance();;
							temp.putAll(productMap);
							tempVariantMap[virtualProductId] = temp;
						}
						
				 
			  }
				finalMap.put("productTotals",tempVariantMap);
			}
		}
	}
	invoiceMap.put(eachInvoice.getKey(),finalMap);
	invoiceList.add(invoiceMap);
	
	
}

if(UtilValidate.isNotEmpty(invoiceList)){
	invoiceList.each{invoiceValue ->
		if(UtilValidate.isNotEmpty(invoiceValue)){
			invoice = invoiceValue.entrySet();
			invoice.each{invValues ->
			tempInvoiceMap=[:];
			invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : invValues.getKey()]);
			invoiceDate=UtilDateTime.toDateString(invoice.getTimestamp("invoiceDate"), "dd-MMM-yyyy");
			tempInvoiceMap.putAt(invValues.getKey(), invValues.getValue());
			if(UtilValidate.isEmpty(dayWiseInvoice[invoiceDate])){
				dayWiseInvoice.put(invoiceDate, tempInvoiceMap);
			}else{
			  tempMap =FastMap.newInstance();
			  tempMap.putAll(dayWiseInvoice.get(invoiceDate));
			  tempMap.put(invValues.getKey(), invValues.getValue());
			  dayWiseInvoice.put(invoiceDate, tempMap);
			
			}
			}
		}
	}
}
context.facilityMap=facilityMap;
context.categoryType=categoryType;
context.dayWiseInvoice=dayWiseInvoice;
context.invoiceMap=invoiceMap;



