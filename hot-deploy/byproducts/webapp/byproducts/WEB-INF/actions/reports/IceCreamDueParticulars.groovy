import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import org.ofbiz.base.util.Debug;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;

dctx=dispatcher.getDispatchContext();

fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
categoryType = parameters.categoryType;
context.categoryType = categoryType;
fromDateTime=UtilDateTime.nowTimestamp();
thruDateTime=UtilDateTime.nowTimestamp();

if (UtilValidate.isNotEmpty(fromDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + fromDateStr, "");
	}
}
if (UtilValidate.isNotEmpty(thruDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruDateStr, "");
	}
}

dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

context.fromDateTime = fromDateTime;
context.thruDateTime = thruDateTime;

roleTypeId = "";

if(categoryType && categoryType == "NANDINI"){
	roleTypeId = "IC_WHOLESALE";
}
else if(categoryType && categoryType == "AMUL"){
	roleTypeId = "EXCLUSIVE_CUSTOMER";
}
result = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: roleTypeId]);

partyIdsList = result.get("partyIds");
partyNameDetails = result.get("partyDetails");
partyLedgerList = [];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "PMNT_VOID"));
conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, "CREDITNOTE_PAYIN"));
conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN, partyIdsList));
EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
creditNotes = delegator.findList("Payment", condition, UtilMisc.toSet("partyIdFrom", "amount"), null, null, false);
partyCreditNoteMap = [:];

creditNotes.each{ eachNote ->
	if(partyCreditNoteMap.get(eachNote.partyIdFrom)){
		extAmt = partyCreditNoteMap.get(eachNote.partyIdFrom);
		totalAmt = extAmt+eachNote.amount;
		partyCreditNoteMap.putAt(eachNote.partyIdFrom, totalAmt);
	}
	else{
		partyCreditNoteMap.putAt(eachNote.partyIdFrom, eachNote.amount);
	}
	
}

partyIdsList.each{eachParty ->
	partyDetail = [:];
	partyReceipts = ByProductNetworkServices.getPartyPaymentDetails(dctx, UtilMisc.toMap("fromDate",dayBegin,"thruDate" ,dayEnd,"partyIdsList", [eachParty])).get("partyPaidMap");
	partyInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [partyIds:[eachParty], isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]).get("partyTotals");
	openingBalance = (ByProductNetworkServices.getOpeningBalanceForParty( dctx , [userLogin: userLogin, saleDate: dayBegin, partyId:eachParty])).get("openingBalance");
	partyTaxMap = SalesInvoiceServices.getInvoiceSalesTaxItems(dctx, [partyIds:[eachParty],fromDate:dayBegin, thruDate:dayEnd]).get("partyTaxMap");
	if(openingBalance!=0 || partyInvoiceTotals || partyReceipts){
		
		partyName = EntityUtil.filterByCondition(partyNameDetails, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachParty));
		
		partyDetail.putAt("partyCode", eachParty);
		partyDetail.putAt("partyName", (EntityUtil.getFirst(partyName)).get("groupName"));
		if(openingBalance>0){
			partyDetail.putAt("obDebit", openingBalance);
			partyDetail.putAt("obCredit", 0);
		}else{
			partyDetail.putAt("obDebit", 0);
			partyDetail.putAt("obCredit", openingBalance.abs());
		}
		saleAmount = 0;
		
		if(UtilValidate.isNotEmpty(partyInvoiceTotals)){
			basicRevenue=partyInvoiceTotals.get(eachParty).get("basicRevenue")
			vatRevenue=partyInvoiceTotals.get(eachParty).get("vatRevenue")
			bedRevenue=partyInvoiceTotals.get(eachParty).get("bedRevenue")
			cstRevenue=partyInvoiceTotals.get(eachParty).get("cstRevenue")
			//saleAmount=partyInvoiceTotals.get(eachParty).get("totalRevenue");
			saleAmount=basicRevenue+vatRevenue+bedRevenue+cstRevenue;
			if(UtilValidate.isNotEmpty(partyTaxMap) && partyTaxMap.containsKey(eachParty)){
				if(partyTaxMap.get(eachParty).containsKey("PPD_PROMO_ADJ") ){
							 ppd=partyTaxMap.get(eachParty).get("PPD_PROMO_ADJ");
							 vatAdj=partyTaxMap.get(eachParty).get("VAT_SALE_ADJ");
							 saleAmount=saleAmount+ppd+vatAdj;
				}
			}else{
			   saleAmount=saleAmount;
			}
			partyDetail.putAt("saleDebit", saleAmount);
		}else{
		   	partyDetail.putAt("saleDebit", 0);
		}
		
		saleReceipt = 0;
		
		if(UtilValidate.isNotEmpty(partyReceipts)){
			saleReceipt=partyReceipts.get(eachParty);
			partyDetail.putAt("saleCredit", saleReceipt);
		}else{
			partyDetail.putAt("saleCredit", 0);
		}
		
		creditNoteAmt = partyCreditNoteMap.get(eachParty);
		if(creditNoteAmt){
			saleCr = partyDetail.get("saleCredit");
			partyDetail.putAt("saleCredit", saleCr+creditNoteAmt);
		}
		//closingBalance=openingBalance+saleAmount-saleReceipt;
		closingBalance=openingBalance+partyDetail["saleDebit"]-partyDetail["saleCredit"];		
		if(closingBalance>0){
			partyDetail.put("cbCredit", 0);
			partyDetail.put("cbDebit", closingBalance.abs());
		}
		else{
			partyDetail.put("cbCredit", closingBalance.abs());
			partyDetail.put("cbDebit", 0);
		}
		partyLedgerList.add(partyDetail);
	}
	
}
context.put("partyLedgerList", partyLedgerList);

