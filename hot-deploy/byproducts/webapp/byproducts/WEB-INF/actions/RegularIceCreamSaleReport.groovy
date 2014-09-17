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
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(totalDays > 32){
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
if(categoryType.equals("UNITS")||categoryType.equals("All")){
	unitPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNITS"]).get("partyIds");
	partyIds.addAll(unitPartyIds);
}
if(categoryType.equals("UNION")||categoryType.equals("All")){
	unionPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "UNION"]).get("partyIds");
	partyIds.addAll(unionPartyIds);
}
if(categoryType.equals("DEPOT_CUSTOMER")||categoryType.equals("All")){
	depotPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "DEPOT_CUSTOMER"]).get("partyIds");
	partyIds.addAll(depotPartyIds);
}
//dayWiseTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceIdTotals");
facilityMap=[:];
dayWiseInvoice=FastMap.newInstance();
// Invoice Sales Abstract
	finalInvoiceDateMap = [:];
	for( i=0 ; i <= (totalDays); i++){
		currentDay =UtilDateTime.addDaysToTimestamp(fromDateTime, i);
		dayBegin=UtilDateTime.getDayStart(currentDay);
		dayEnd=UtilDateTime.getDayEnd(currentDay);
		invoiceMap = [:];
		if(UtilValidate.isNotEmpty(partyIds)){
			salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
			if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
				invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");
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
							if(UtilValidate.isNotEmpty(invoice.getValue().invoiceDateStr)){
								invoiceDate = invoice.getValue().invoiceDateStr;
							}
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
									virtualProductId="";
									if(UtilValidate.isNotEmpty(productValue)){
										currentProduct = productValue.getKey();
										product = delegator.findOne("Product", [productId : currentProduct], false);
										productId = productValue.getKey();
											exprList=[];
											exprList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "IC_CAT_RPT"));
											exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
											condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
											productList = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
											if(UtilValidate.isNotEmpty(productList)){
											 productList=EntityUtil.getFirst(productList);
											}
												if(UtilValidate.isNotEmpty(productList)){
													virtualProductId = productList.get("productCategoryId");
												}
												if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
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
													totalRevenue = productValue.getValue().get("totalRevenue");
													productMap["quantity"] += productValue.getValue().get("total");
													productMap["basicRevenue"] += productValue.getValue().get("basicRevenue");
													productMap["bedRevenue"] += productValue.getValue().get("bedRevenue");
													productMap["vatRevenue"] += productValue.getValue().get("vatRevenue");
													productMap["cstRevenue"] += productValue.getValue().get("cstRevenue");
													productMap["totalRevenue"] += productValue.getValue().get("totalRevenue");
													temp=FastMap.newInstance();
													temp.putAll(productMap);
													tempVariantMap[virtualProductId] = temp;
												}
												//tempVariantMap["InvoiceId"]=invoiceId;
										finalMap.put("productTotals",tempVariantMap);
										finalMap.put("invTotals",totalMap);
									}
								}
							}
							invoiceList = [];
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
context.categoryType=categoryType;
context.dayWiseInvoice=dayWiseInvoice;



