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
categoryType=parameters.categoryType;
context.put("categoryType",categoryType);
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
if(categoryType.equals("ICP_TRANS_CUSTOMER")||categoryType.equals("All")){
	depotPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "ICP_TRANS_CUSTOMER"]).get("partyIds");
	partyIds.addAll(depotPartyIds);
}


// Invoice No Sales report
reportTypeFlag = parameters.reportTypeFlag;
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "InvoiceSales"){
	invoiceMap = [:];
	if(UtilValidate.isNotEmpty(partyIds)){
		invoiceTaxMap = SalesInvoiceServices.getInvoiceSalesTaxItems(dctx, [partyIds:partyIds,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceTaxMap");
		salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [categoryType:categoryType,partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
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
						invoiceId = invoice.getKey();
						ppd=0;
						vatAdj=0;
							    if(UtilValidate.isNotEmpty(invoiceTaxMap) && invoiceTaxMap.containsKey(invoiceId)){
								 if(invoiceTaxMap.get(invoiceId).containsKey("PPD_PROMO_ADJ") ){
									 ppd=invoiceTaxMap.get(invoiceId).get("PPD_PROMO_ADJ");
									 vatAdj=invoiceTaxMap.get(invoiceId).get("VAT_SALE_ADJ");
								 }
							    }
								if(UtilValidate.isNotEmpty(invoice.getValue().invoiceDateStr)){
									invoiceDate = invoice.getValue().invoiceDateStr;
								}
								invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
								invoicePartyId = invoiceDetails.partyId;
								partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : invoicePartyId, partyIdentificationTypeId : "TIN_NUMBER"], false);
								if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
									idValue = partyIdentificationDetails.idValue;
								}
								if(UtilValidate.isNotEmpty(invoicePartyId)){
									partyName = PartyHelper.getPartyName(delegator, invoicePartyId, false);
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().basicRevenue)){
									basicRevenue = invoice.getValue().basicRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().vatRevenue)){
									vatRevenue = invoice.getValue().vatRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().bedRevenue)){
									bedRevenue = invoice.getValue().bedRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().cstRevenue)){
									cstRevenue = invoice.getValue().cstRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().totalRevenue)){
									totalRevenue = invoice.getValue().totalRevenue;
								}
								invoiceSequenceId = null;
								if(UtilValidate.isNotEmpty(invoiceId)){
									invoiceSequenceList = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId) , null, null, null, false);
									if(UtilValidate.isNotEmpty(invoiceSequenceList)){
										invoiceSequence = EntityUtil.getFirst(invoiceSequenceList);
										if(UtilValidate.isNotEmpty(invoiceSequence)){
											invoiceSequenceId = invoiceSequence.sequenceId;
										}
									}
								}
								totalMap = [:];
								totalMap["invoiceDate"]=invoiceDate;
								totalMap["basicRevenue"]=basicRevenue;
								totalMap["partyName"]=partyName;
								totalMap["invoicePartyId"]=invoicePartyId;
								totalMap["bedRevenue"]=bedRevenue;
								totalMap["vatRevenue"]=vatRevenue+vatAdj;
								totalMap["cstRevenue"]=cstRevenue;
								totalMap["ppd"]=ppd;
								totalMap["totalRevenue"]=totalRevenue+ppd+vatAdj;
								totalMap["idValue"]=idValue;
								totalMap["invoiceSequenceId"]=invoiceSequenceId;
								tempMap = [:];
								tempMap.putAll(totalMap);
								if(UtilValidate.isNotEmpty(tempMap)){
									invoiceMap.put(invoiceId,tempMap);
								}
							//}
						//}
					}
				}
			}
			context.put("invoiceMap",invoiceMap);
		}
	}
}
// Invoice Sales Abstract
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "InvoiceSalesAbstract"){
	finalInvoiceDateMap = [:];
	for( i=0 ; i <= (totalDays); i++){
		currentDay =UtilDateTime.addDaysToTimestamp(fromDateTime, i);
		dayBegin=UtilDateTime.getDayStart(currentDay);
		dayEnd=UtilDateTime.getDayEnd(currentDay);
		invoicePartyMap = [:];
		if(UtilValidate.isNotEmpty(partyIds)){
			invoiceTaxMap = SalesInvoiceServices.getInvoiceSalesTaxItems(dctx, [partyIds:partyIds,fromDate:dayBegin, thruDate:dayEnd]).get("invoiceTaxMap");
			salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [categoryType:categoryType,partyIds:partyIds, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
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
							ppd=0;
							vatAdj=0;
							invoiceId = invoice.getKey();
							if(UtilValidate.isNotEmpty(invoiceTaxMap) && invoiceTaxMap.containsKey(invoiceId)){
								 if(invoiceTaxMap.get(invoiceId).containsKey("PPD_PROMO_ADJ") ){
									 ppd=invoiceTaxMap.get(invoiceId).get("PPD_PROMO_ADJ");
									 vatAdj=invoiceTaxMap.get(invoiceId).get("VAT_SALE_ADJ");
								 }
							    }
								if(UtilValidate.isNotEmpty(invoice.getValue().invoiceDateStr)){
									invoiceDate = invoice.getValue().invoiceDateStr;
								}
								invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
								invoicePartyId = invoiceDetails.partyId;
								partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : invoicePartyId, partyIdentificationTypeId : "TIN_NUMBER"], false);
								if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
									idValue = partyIdentificationDetails.idValue;
								}
								if(UtilValidate.isNotEmpty(invoicePartyId)){
									partyName = PartyHelper.getPartyName(delegator, invoicePartyId, false);
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().basicRevenue)){
									basicRevenue = invoice.getValue().basicRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().vatRevenue)){
									vatRevenue = invoice.getValue().vatRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().bedRevenue)){
									bedRevenue = invoice.getValue().bedRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().cstRevenue)){
									cstRevenue = invoice.getValue().cstRevenue;
								}
								if(UtilValidate.isNotEmpty(invoice.getValue().totalRevenue)){
									totalRevenue = invoice.getValue().totalRevenue;
								}
								invoiceSequenceId = null;
								if(UtilValidate.isNotEmpty(invoiceId)){
									invoiceSequenceList = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId) , null, null, null, false);
									if(UtilValidate.isNotEmpty(invoiceSequenceList)){
										invoiceSequence = EntityUtil.getFirst(invoiceSequenceList);
										if(UtilValidate.isNotEmpty(invoiceSequence)){
											invoiceSequenceId = invoiceSequence.sequenceId;
										}
									}
								}
								totalMrpValue = 0;
								invoiceItemsList = delegator.findList("InvoiceItem",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , null, null, null, false );
								if(UtilValidate.isNotEmpty(invoiceItemsList)){
									invoiceItemsList.each{ invoiceItem ->
										cxt = [:];
										cxt.put("userLogin", userLogin);
										cxt.put("productId", invoiceItem.productId);
										cxt.put("partyId", invoicePartyId);
										cxt.put("priceDate", UtilDateTime.nowTimestamp());
										cxt.put("productStoreId", "_NA_");
										cxt.put("productPriceTypeId", "MRP_IS");
										cxt.put("geoTax", "VAT");
										quantity = invoiceItem.quantity;
										result = ByProductNetworkServices.calculateStoreProductPrices(delegator, dispatcher, cxt);
										mrpPrice = result.get("totalPrice");
										if(UtilValidate.isNotEmpty(mrpPrice)){
											totQtyMrpPrice = quantity*mrpPrice;
											totalMrpValue = totalMrpValue+totQtyMrpPrice;
										}
									}
								}
								totalMap = [:];
								totalMap["invoiceId"]=invoiceId;
								totalMap["basicRevenue"]=basicRevenue;
								totalMap["bedRevenue"]=bedRevenue;
								totalMap["vatRevenue"]=vatRevenue+vatAdj;
								totalMap["cstRevenue"]=cstRevenue;
								totalMap["ppd"]=ppd;
								totalMap["totalRevenue"]=totalRevenue+ppd+vatAdj;
								totalMap["idValue"]=idValue;
								totalMap["invoiceSequenceId"]=invoiceSequenceId;
								totalMap["totalMrpValue"]=totalMrpValue;
								invoicePartyList = [];
								if(UtilValidate.isNotEmpty(invoicePartyMap[invoicePartyId])){
									invoicePartyList = invoicePartyMap.get(invoicePartyId);
								}
								invoicePartyList.add(totalMap);
								invoicePartyList = UtilMisc.sortMaps(invoicePartyList, UtilMisc.toList("invoiceSequenceId"));
								invoicePartyMap[invoicePartyId] = invoicePartyList;
							//}
						//}
					    
					   }
					}
				}
			}
		}
		tempMap = [:];
		if(UtilValidate.isNotEmpty(invoicePartyMap))
		tempMap.putAll(invoicePartyMap);
		if(UtilValidate.isNotEmpty(tempMap)){
			finalInvoiceDateMap.put(dayBegin,tempMap);
		}
	}
	context.put("finalInvoiceDateMap",finalInvoiceDateMap);
}











