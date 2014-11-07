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
reportTypeFlag = parameters.reportTypeFlag;
taxType=parameters.taxType;
EntityListIterator invoiceItemsIter = null;

taxDetails5pt5List=[];
taxDetails14pt5List=[];
taxDetailsOthrList=[];

taxDetails5pt5Map=[:];
taxDetails14pt5Map=[:];


tax5pt5TotalMap=[:];
tax5pt5TotalMap["invTotalVal"]=BigDecimal.ZERO;
tax5pt5TotalMap["vatAmount"]=BigDecimal.ZERO;
tax14pt5TotalMap=[:];
tax14pt5TotalMap["invTotalVal"]=BigDecimal.ZERO;
tax14pt5TotalMap["vatAmount"]=BigDecimal.ZERO;

		invoiceMap = [:];
		invoiceDtlsMap = [:];
		
		try {
			// lets populate sales date  Map
			/*for (int i = 0; i < intervalDays; i++) {
				Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate,i);
				dayWiseSaleMap.put(UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"),null);
			}
			Debug.log("====>dayWiseSaleMap ===!"+dayWiseSaleMap);*/
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			/*if (UtilValidate.isNotEmpty(partyIds)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIds));
			}*/
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			
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
			
			if(UtilValidate.isNotEmpty((invoiceItem.vatPercent)&&(invoiceItem.vatAmount))){
				if(invoiceItem.vatPercent==5.5){
				BigDecimal vatRevenue = invoiceItem.vatAmount;
				invDetailMap=taxDetails5pt5Map[invoiceItem.invoiceId];
					if(UtilValidate.isEmpty(invDetailMap)){
					innerTaxItemMap=[:];
					innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
					innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
					innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
					innerTaxItemMap["tinNumber"]="";
					innerTaxItemMap["vchrType"]="Purchase";
					innerTaxItemMap["crOrDbId"]="D";
					invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceItem.invoiceId);
					invTotalVal=invTotalVal-vatRevenue;
					innerTaxItemMap["invTotalVal"]=invTotalVal;
					innerTaxItemMap["vatAmount"]=vatRevenue;
					tax5pt5TotalMap["invTotalVal"]+=invTotalVal;
					tax5pt5TotalMap["vatAmount"]+=vatRevenue;
					fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
						if(UtilValidate.isNotEmpty(fromPartyDetail)){
							innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
					      }
					taxDetails5pt5List.addAll(innerTaxItemMap);
					//intilize inner map when empty
					taxDetails5pt5Map[invoiceItem.invoiceId]=innerTaxItemMap;
					}else if(UtilValidate.isNotEmpty(invDetailMap)){
					invDetailMap["vatAmount"]+=vatRevenue;
					tax5pt5TotalMap["vatAmount"]+=vatRevenue;
					
					invDetailMap["invTotalVal"]-=vatRevenue;
					tax5pt5TotalMap["invTotalVal"]-=vatRevenue;
					
					taxDetails5pt5Map[invoiceItem.invoiceId]=invDetailMap;
					}
				}
				if(invoiceItem.vatPercent==14.5){
					BigDecimal vatRevenue = invoiceItem.vatAmount;
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
						invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceItem.invoiceId);
						invTotalVal=invTotalVal-vatRevenue;
						innerTaxItemMap["invTotalVal"]=invTotalVal;
						innerTaxItemMap["vatAmount"]=vatRevenue;
						
						tax14pt5TotalMap["invTotalVal"]+=invTotalVal;
						tax14pt5TotalMap["vatAmount"]+=vatRevenue;
						taxDetails14pt5List.addAll(innerTaxItemMap);
						//intilize inner map when empty
						taxDetails14pt5Map[invoiceItem.invoiceId]=innerTaxItemMap;
						//Debug.log("=invoiceId==FOR FOURTEEnnn=="+invoiceItem.invoiceId+"==ANdAmouunt=="+invoiceItem.vatAmount+"==percent="+invoiceItem.vatPercent+"=Total="+invTotalVal);
						}else if(UtilValidate.isNotEmpty(invDetailMap)){
						invDetailMap["vatAmount"]+=vatRevenue;
						tax14pt5TotalMap["vatAmount"]+=vatRevenue;
						
						invDetailMap["invTotalVal"]-=vatRevenue;
						tax14pt5TotalMap["invTotalVal"]-=vatRevenue;
						
						taxDetails14pt5Map[invoiceItem.invoiceId]=invDetailMap;
						}
					}
			}
			//Debug.log("=invoiceItem=="+invoiceItem);
		}
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT5PT5")){
			//context.put("taxDetails5pt5List",taxDetails5pt5List);
			context.put("taxDetails5pt5List",taxDetails5pt5Map.entrySet());
		}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT14PT5")){
		     context.put("taxDetails14pt5List",taxDetails14pt5Map.entrySet());
		    //context.put("taxDetails14pt5List",taxDetails14pt5List);
		}else{
		//context.put("taxDetails5pt5List",taxDetails5pt5List);
		//context.put("taxDetails14pt5List",taxDetails14pt5List);
		context.put("taxDetails5pt5List",taxDetails5pt5Map.entrySet());
		context.put("taxDetails14pt5List",taxDetails14pt5Map.entrySet());
		}
		context.put("tax5pt5TotalMap",tax5pt5TotalMap);
		context.put("tax14pt5TotalMap",tax14pt5TotalMap);
		
		
		
		taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX4"), false);
		taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX4"), false);
		context.taxParty = taxParty;
		context.taxAuthority = taxAuthority;
		invItemTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "VAT_PUR"), false);
		context.invItemTypeGl = invItemTypeGl;
// Purchase Abstract report


