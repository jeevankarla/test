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
/*partyIds=[];
if(categoryType.equals("ICE_CREAM_NANDINI")||categoryType.equals("All")){
   nandiniPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "IC_WHOLESALE"]).get("partyIds");
   partyIds.addAll(nandiniPartyIds);
}
if(categoryType.equals("ICE_CREAM_AMUL")||categoryType.equals("All")){
   amulPartyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: "EXCLUSIVE_CUSTOMER"]).get("partyIds");
   partyIds.addAll(amulPartyIds);
}*/
// Invoice No Purchase report
invoiceMap = [:];
purchaseRegisterList = [];
salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isPurchaseInvoice:true, isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
	invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");
	if(UtilValidate.isNotEmpty(invoiceTotals)){
		invoiceTotals.each { invoice ->
			if(UtilValidate.isNotEmpty(invoice)){
				invoiceId = "";
				partyName = "";
				tinNumber = "";
				basicRevenue=0;
				bedRevenue=0;
				vatRevenue=0;
				cstRevenue=0;
				totalRevenue=0;
				totalBedRevenue = 0;
				
				vatAmount = 0;
				quantity = 0;
				vatPercent = 0;
				
				invoiceId = invoice.getKey();
				if(UtilValidate.isNotEmpty(invoice.getValue().invoiceDateStr)){
					invoiceDate = invoice.getValue().invoiceDateStr;
				}
				invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);
				invoicePartyId = invoiceDetails.partyIdFrom;
				partyIdentificationDetails = delegator.findOne("PartyIdentification", [partyId : invoicePartyId, partyIdentificationTypeId : "TIN_NUMBER"], false);
				if(UtilValidate.isNotEmpty(partyIdentificationDetails)){
					tinNumber = partyIdentificationDetails.idValue;
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
				
				if(UtilValidate.isNotEmpty(invoice.getValue().bedCessRevenue)){
					bedCessRevenue = invoice.getValue().bedCessRevenue;
				}
				if(UtilValidate.isNotEmpty(invoice.getValue().bedSecCessRevenue)){
					bedSecCessRevenue = invoice.getValue().bedSecCessRevenue;
				}
				if(UtilValidate.isNotEmpty(invoice.getValue().cstRevenue)){
					cstRevenue = invoice.getValue().cstRevenue;
				}
				if(UtilValidate.isNotEmpty(invoice.getValue().totalRevenue)){
					totalRevenue = invoice.getValue().totalRevenue;
				}
				totalBedRevenue = bedRevenue+bedCessRevenue+bedSecCessRevenue;
				
				freightAmount = 0;
				discountAmount = 0;
				insuranceAmount = 0;
				packForwAmount = 0;
				otherAmount = 0;
				invoiceItemsList = delegator.findList("InvoiceItem",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , null, null, null, false );
				if(UtilValidate.isNotEmpty(invoiceItemsList)){
					invoiceItemsList.each{ invoiceItem ->
						if(UtilValidate.isNotEmpty(invoiceItem)){
							invoiceItemTypeId = invoiceItem.invoiceItemTypeId;
							if(UtilValidate.isNotEmpty(invoiceItemTypeId) && invoiceItemTypeId.equals("COGS_ITEM16")){
								freightAmount = invoiceItem.amount;
							}
						}
						if(UtilValidate.isNotEmpty(invoiceItem)){
							invoiceItemTypeId = invoiceItem.invoiceItemTypeId;
							if(UtilValidate.isNotEmpty(invoiceItemTypeId) && invoiceItemTypeId.equals("COGS_ITEM17")){
								discountAmount = invoiceItem.amount;
							}
						}
						if(UtilValidate.isNotEmpty(invoiceItem)){
							invoiceItemTypeId = invoiceItem.invoiceItemTypeId;
							if(UtilValidate.isNotEmpty(invoiceItemTypeId) && invoiceItemTypeId.equals("COGS_ITEM18")){
								insuranceAmount = invoiceItem.amount;
							}
						}
						if(UtilValidate.isNotEmpty(invoiceItem)){
							invoiceItemTypeId = invoiceItem.invoiceItemTypeId;
							if(UtilValidate.isNotEmpty(invoiceItemTypeId) && invoiceItemTypeId.equals("COGS_ITEM19")){
								packForwAmount = invoiceItem.amount;
							}
						}
						if(UtilValidate.isNotEmpty(invoiceItem)){
							invoiceItemTypeId = invoiceItem.invoiceItemTypeId;
							if(UtilValidate.isNotEmpty(invoiceItemTypeId) && invoiceItemTypeId.equals("COGS_ITEM20")){
								otherAmount = invoiceItem.amount;
							}
						}
						totalFreight = 0;
						totalFreight = freightAmount+packForwAmount+otherAmount;
					}
				}
				orderId = null;
				mrrNumber = null;
				poNumber = null;
				supInvNumber = null;
				supInvDateTime = null;
				orderItemBillingList = delegator.findList("OrderItemBilling",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , null, null, null, false );
				if(UtilValidate.isNotEmpty(orderItemBillingList)){
					orderItemBilling = EntityUtil.getFirst(orderItemBillingList);
					if(UtilValidate.isNotEmpty(orderItemBilling)){
						orderId = orderItemBilling.orderId;
						if(UtilValidate.isNotEmpty(orderId)){
							mrnOrderAttributeDetails = delegator.findOne("OrderAttribute", [orderId : orderId, attrName : "MRN_NUMBER"], false);
							if(UtilValidate.isNotEmpty(mrnOrderAttributeDetails)){
								mrrNumber = mrnOrderAttributeDetails.attrValue;
							}
							poOrderAttributeDetails = delegator.findOne("OrderAttribute", [orderId : orderId, attrName : "PO_NUMBER"], false);
							if(UtilValidate.isNotEmpty(poOrderAttributeDetails)){
								poNumber = poOrderAttributeDetails.attrValue;
							}
							supInvOrderAttributeDetails = delegator.findOne("OrderAttribute", [orderId : orderId, attrName : "SUP_INV_NUMBER"], false);
							if(UtilValidate.isNotEmpty(supInvOrderAttributeDetails)){
								supInvNumber = supInvOrderAttributeDetails.attrValue;
							}
							supInvDateOrderAttributeDetails = delegator.findOne("OrderAttribute", [orderId : orderId, attrName : "SUP_INV_DATE"], false);
							if(UtilValidate.isNotEmpty(supInvDateOrderAttributeDetails)){
								supInvDate = supInvDateOrderAttributeDetails.attrValue;
								def sdf1 = new SimpleDateFormat("MM/dd/yy HH:mm");
								try {
									supInvDateTime = new java.sql.Timestamp(sdf1.parse(supInvDate).getTime());
								} catch (ParseException e) {
									Debug.logError(e, "Cannot parse date string: "+supInvDate, "");
								}
								supInvDateTime = UtilDateTime.getDayStart(supInvDateTime);
								if(UtilValidate.isNotEmpty(supInvDateTime)){
									supInvDateTime = UtilDateTime.toDateString(supInvDateTime, "dd/MM/yyyy");
								}
							}
						}
					}
				}
				grandTotal = 0;
				grandTotal = totalRevenue+totalFreight+discountAmount+insuranceAmount;
				
				totalMap = [:];
				totalMap["invoiceId"]=invoiceId;
				totalMap["invoiceDate"]=invoiceDate;
				totalMap["basicRevenue"]=basicRevenue;
				totalMap["partyName"]=partyName;
				totalMap["bedRevenue"]=totalBedRevenue;
				totalMap["vatRevenue"]=vatRevenue;
				totalMap["cstRevenue"]=cstRevenue;
				totalMap["totalRevenue"]=totalRevenue;
				totalMap["freightAmount"]=totalFreight;
				totalMap["discountAmount"]=discountAmount;
				totalMap["insuranceAmount"]=insuranceAmount;
				totalMap["otherAmount"]=otherAmount;
				totalMap["grandTotal"]=grandTotal;
				totalMap["tinNumber"]=tinNumber;
				totalMap["mrrNumber"]=mrrNumber;
				totalMap["poNumber"]=poNumber;
				totalMap["supInvNumber"]=supInvNumber;
				totalMap["supInvDate"]=supInvDateTime;
				tempMap = [:];
				tempMap.putAll(totalMap);
				if(UtilValidate.isNotEmpty(tempMap)){
					invoiceMap.put(invoiceId,tempMap);
					// for Purchase Report CSV
					purchaseRegisterList.addAll(tempMap);
				}
			}
		}
	}
	context.put("invoiceMap",invoiceMap);
	context.putAt("purchaseRegisterList", purchaseRegisterList);
}


InvoicePartyMapReg=[:];
invoiceMap.each { invoiceMap ->
	invoiceId = invoiceMap.getKey();
	//Debug.log("invoiceId==========================="+invoiceId);
	invoiceRolelist = delegator.findList("InvoiceRole",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId),EntityOperator.AND,EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"))  , null, null, null, false );
	//Debug.log("invoiceRolelist===================="+invoiceRolelist);
	invoiceRolelist.each { invoicelist ->
		partyId = invoicelist.get("partyId");
		//Debug.log("partyId===================="+partyId);
		InvoicePartyMapReg.put(invoiceId,partyId);
	}
}
context.put("InvoicePartyMapReg",InvoicePartyMapReg);
//Debug.log("InvoicePartyMapReg================================"+InvoicePartyMapReg);
 /*// for vat totals
invoiceIdList = [];
if(UtilValidate.isNotEmpty(salesInvoiceTotals)){
	invoiceTotals = salesInvoiceTotals.get("invoiceIdTotals");
	if(UtilValidate.isNotEmpty(invoiceTotals)){
		invoiceTotals.each { invoice ->
			if(UtilValidate.isNotEmpty(invoice)){
				invoiceIdList.add(invoice.getKey());
				vatMap = [:];
				if(UtilValidate.isNotEmpty(invoiceIdList)){
					invoiceItemsList = delegator.findList("InvoiceItem",EntityCondition.makeCondition("invoiceId", EntityOperator.IN , invoiceIdList)  , null, null, null, false );
					invoiceItemTypeList = EntityUtil.getFieldListFromEntityList(invoiceItemsList, "invoiceItemTypeId", true);
					invoiceItemTypeList.each{ invoiceItemTypeId
						invoiceItemTypesList = EntityUtil.filterByAnd(invoiceItemsList, [invoiceItemTypeId : invoiceItemTypeId]);
						invoiceItemTypesList.each { invoiceItem
							if(invoiceItemTypeId.equals("COGS_ITEM16")){
								freightAmount = invoiceItem.amount;
								
							}
						}
					}
					vatPercentList.each { vatPercent->
						invoiceItemsVatList = EntityUtil.filterByAnd(invoiceItemsList, [vatPercent : vatPercent]);
						totalVatAmount = 0;
						vatAmount = 0;
						quantity = 0;
						if(UtilValidate.isNotEmpty(invoiceItemsVatList)){
							invoiceItemsVatList.each{ invoiceItem ->
								vatAmount = invoiceItem.vatAmount;
								quantity = invoiceItem.quantity;
								totalVatAmount += (vatAmount*quantity);
							}
						}
						vatMap.put(vatPercent,totalVatAmount);
						context.put("vatMap",vatMap);
					}
				}
			}
		}
	}
}*/





            







