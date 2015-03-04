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



crInvoices=context.get("crInvoices");
creditNoteMap=context.get("creditNoteMap");
invoiceSequenceNumMap=context.get("invoiceSequenceNumMap");
grandTotal=0;
grandTotalMap=[:];
finalVatLsit=[];
crInvoices.each{ eachDayDetails ->
	subtotal=[:];
	eachDayInvoiceDetail = eachDayDetails.getValue();
	dayTotal = 0;
	returnAmtMap = [:];
	if(creditNoteMap){
		if(creditNoteMap.get(eachDayDetails.getKey())){
			returnAmtMap=creditNoteMap.get(eachDayDetails.getKey());
		}
	}
	eachDayInvoiceDetail.each{ eachInvoiceDetail ->
		tempMap=[:];
		invoiceDate=UtilDateTime.toDateString(eachDayDetails.getKey() ,"dd/MM/yyyy");
		tempMap.put("invoiceDate",invoiceDate);
		tempMap.put("retailerCode", eachInvoiceDetail.get("facilityId"));
		tempMap.put("retailerName", eachInvoiceDetail.get("facilityName"));
		if(invoiceSequenceNumMap){
			tempMap.put("invoiceSeqNo", invoiceSequenceNumMap.get(eachInvoiceDetail.get("invoiceId")));
		}else{
		tempMap.put("invoiceSeqNo", eachInvoiceDetail.get("invoiceId"));
		}
		tempMap.put("basicAmount", eachInvoiceDetail.get("amount"));
		bosNo=eachInvoiceDetail.get('invoiceId');
		tempMap.put("bosNo", bosNo);
		taxAmt = 0;
		taxAmt=eachInvoiceDetail.get("taxAmount");
		tempMap.put("taxAmount", eachInvoiceDetail.get("taxAmount"));
		
		netAmt = eachInvoiceDetail.get('amount')+taxAmt;
		tempMap.put("netAmount", netAmt);
		dayTotal = dayTotal+netAmt;
		finalVatLsit.addAll(tempMap);
	}
	subtotal.put("invoiceSeqNo", "subTotal");
	subtotal.put("netAmount", dayTotal);
	finalVatLsit.addAll(subtotal);
	grandTotal = grandTotal+dayTotal
}

grandTotalMap.put("invoiceSeqNo", "GrandTotal");
grandTotalMap.put("netAmount", grandTotal);
finalVatLsit.addAll(grandTotalMap);
context.finalVatLsit=finalVatLsit;
