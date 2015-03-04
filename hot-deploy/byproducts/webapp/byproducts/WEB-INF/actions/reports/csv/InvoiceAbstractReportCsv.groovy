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

invoiceAbstractList=[];
invoiceMap=context.get("invoiceMap");
shippingDetails=context.get("shippingDetails");
 totalBasicRev=0;
 totalBedRev=0;
 totalVatRev=0;
 totalCstRev=0;
 totalPpd=0;
totalRevenue=0;
grandTotal=[:];
invoiceMap.each{ invoiceDet ->
	TempMap=[:];	
	totalBasicRev=totalBasicRev+invoiceDet.getValue().get("basicRevenue");
	
	totalBedRev=totalBedRev+invoiceDet.getValue().get("bedRevenue");
	
	totalVatRev=totalVatRev+invoiceDet.getValue().get("vatRevenue");
	
	totalCstRev=totalCstRev+invoiceDet.getValue().get("cstRevenue");
	totalPpd=totalPpd+invoiceDet.getValue().get("ppd");
	totalRevenue=totalRevenue+invoiceDet.getValue().get("totalRevenue");
	
	tinnumbe=invoiceDet.getValue().get("idValue");
	invoiceDate=invoiceDet.getValue().get("invoiceDate");
	invoiceId=invoiceDet.getKey();
	invoiceSeqId=invoiceDet.getValue().get("invoiceSequenceId");
	partyId=[shippingDetails.get(invoiceDet.getKey()).get("partyId")]
	partyName=shippingDetails.get(invoiceDet.getKey()).get("partyName")+partyId;
	
	basicRevenue=invoiceDet.getValue().get("basicRevenue");
	bedRevenue=invoiceDet.getValue().get("bedRevenue");
	vatRevenue=invoiceDet.getValue().get("vatRevenue");
	cstRevenue=invoiceDet.getValue().get("cstRevenue");
	ppd=invoiceDet.getValue().get("ppd");
	total=invoiceDet.getValue().get("totalRevenue");
	
	TempMap.put("tinnumbe",tinnumbe);
	TempMap.put("invoiceDate",invoiceDate);
	TempMap.put("invoiceId",invoiceId);
	TempMap.put("invoiceSeqId",invoiceSeqId);
	TempMap.put("partyName",partyName);
	
	TempMap.put("basicRevenue",basicRevenue);
	TempMap.put("bedRevenue",bedRevenue);
	TempMap.put("vatRevenue",vatRevenue);
	TempMap.put("cstRevenue",cstRevenue);
	TempMap.put("ppd",ppd);
	TempMap.put("total",total);
	
	
	
	invoiceAbstractList.addAll(TempMap);
}
grandTotal.put("tinnumbe","Total");
grandTotal.put("basicRevenue",totalBasicRev);
grandTotal.put("bedRevenue",totalBedRev);
grandTotal.put("vatRevenue",totalVatRev);
grandTotal.put("cstRevenue",totalCstRev);
grandTotal.put("ppd",totalPpd);
grandTotal.put("total",totalRevenue);
invoiceAbstractList.addAll(grandTotal);
context.invoiceAbstractList=invoiceAbstractList;


//DayWise Report
finalInvoiceDateMap=context.get("finalInvoiceDateMap");
 grandTotalBasicRev = 0;
 grandTotalBedRev = 0;
 grandTotalVatRev = 0;
 grandTotalCstRev = 0;
 grandTotalPpd = 0;
 grandTotalRev = 0;
 grandTotalMrpValue =0;
 grandTotalMap=[:];
 finalInvoiceDateList=[];
finalInvoiceDateMap.each{ invoiceDetails ->
	tempMap1=[:];
	invoiceDate=invoiceDetails.getKey();
	invoiceDate1=UtilDateTime.toDateString(invoiceDate ,"dd/MM/yyyy");
	
	dayTotalBasicRev = 0;
	dayTotalBedRev = 0;
	dayTotalVatRev = 0;
	dayTotalCstRev = 0;
	dayTotalPpd = 0;
	dayTotalRev = 0;
	dayTotalMrpValue = 0;
	DaytotalMap=[:];
	invoiceTotList = invoiceDetails.getValue();
	invoiceTotList.each{ invoiceTot ->
		invoicePartyTotals = invoiceTot.getValue();
		invoicePartyTotals.each{ invoicePartyTot ->
			invoicePartyTotDetails=[:];			
			 totalBasicRev=0;
			 totalBedRev=0;
			 totalVatRev=0;
			 totalCstRev=0;
			 totalRevenue=0;
			 totalPpd=0;
			 totalMrpValue = 0;
			 temppartyName=PartyHelper.getPartyName(delegator, invoiceTot.getKey(), false);
			 partyId=[shippingDetails.get(invoicePartyTot.get("invoiceId")).get("partyId")]
			 partyName=shippingDetails.get(invoicePartyTot.get("invoiceId")).get("partyName")+partyId;
			 totalBasicRev=totalBasicRev+invoicePartyTot.get("basicRevenue");
			 totalBedRev=totalBedRev+invoicePartyTot.get("bedRevenue");
			 totalVatRev=totalVatRev+invoicePartyTot.get("vatRevenue");
			 totalCstRev=totalCstRev+invoicePartyTot.get("cstRevenue");
			 totalPpd=totalPpd+invoicePartyTot.get("ppd");
			 totalRevenue=totalRevenue+invoicePartyTot.get("totalRevenue");
			  totalMrpValue=totalMrpValue+invoicePartyTot.get("totalMrpValue");
			  invoicePartyTotDetails.put("partyName",partyName);
			  invoicePartyTotDetails.put("invoiceDate",invoiceDate1);
			  
			  invoicePartyTotDetails.put("invoiceId",invoicePartyTot.get("invoiceId"));
			  invoicePartyTotDetails.put("invoiceSeqId",invoicePartyTot.get("invoiceSequenceId"));
			  invoicePartyTotDetails.put("totalMrpValue",invoicePartyTot.get("totalMrpValue"));
			  invoicePartyTotDetails.put("basicRevenue",invoicePartyTot.get("basicRevenue"));
			  invoicePartyTotDetails.put("bedRevenue",invoicePartyTot.get("bedRevenue"));
			  invoicePartyTotDetails.put("vatRevenue",invoicePartyTot.get("vatRevenue"));
			  invoicePartyTotDetails.put("cstRevenue",invoicePartyTot.get("cstRevenue"));
			  invoicePartyTotDetails.put("ppd",invoicePartyTot.get("ppd"));
			  invoicePartyTotDetails.put("total",invoicePartyTot.get("totalRevenue"));
			  invoicePartyTotDetails.put("tinnumbe",invoicePartyTot.get("idValue"));
			  dayTotalMrpValue = dayTotalMrpValue + totalMrpValue;
			  dayTotalBasicRev = dayTotalBasicRev + totalBasicRev;
			  dayTotalBedRev = dayTotalBedRev + totalBedRev;
			  dayTotalVatRev = dayTotalVatRev + totalVatRev;
			  dayTotalCstRev = dayTotalCstRev + totalCstRev;
			  dayTotalPpd = dayTotalPpd + totalPpd;
			  dayTotalRev = dayTotalRev + totalRevenue;
			  totalMap=[:];
			  totalMap.put("invoiceId","Total");
			  totalMap.put("totalMrpValue",totalMrpValue);
			  totalMap.put("basicRevenue",totalBasicRev);
			  totalMap.put("bedRevenue",totalBedRev);
			  
			  totalMap.put("vatRevenue",totalVatRev);
			  
			  totalMap.put("cstRevenue",totalCstRev);
			  
			  totalMap.put("ppd",totalPpd);
			  
			  totalMap.put("total",totalRevenue);
			  
			  
			  finalInvoiceDateList.addAll(invoicePartyTotDetails);
			  finalInvoiceDateList.addAll(totalMap);
		}
	finalInvoiceDateList.addAll(tempMap1);
	
}
	grandTotalMrpValue = grandTotalMrpValue + dayTotalMrpValue;
	grandTotalBasicRev = grandTotalBasicRev + dayTotalBasicRev;
	grandTotalBedRev = grandTotalBedRev + dayTotalBedRev;
	grandTotalVatRev = grandTotalVatRev + dayTotalVatRev;
	grandTotalCstRev = grandTotalCstRev + dayTotalCstRev;
	grandTotalPpd = grandTotalPpd + dayTotalPpd;
	grandTotalRev = grandTotalRev + dayTotalRev;
	DaytotalMap.put("invoiceDate","DayTotal");
	DaytotalMap.put("totalMrpValue",dayTotalMrpValue);
	DaytotalMap.put("basicRevenue",dayTotalBasicRev);
	DaytotalMap.put("bedRevenue",dayTotalBedRev);
	DaytotalMap.put("vatRevenue",dayTotalVatRev);
	DaytotalMap.put("cstRevenue",dayTotalCstRev);
	DaytotalMap.put("ppd",dayTotalPpd);
	DaytotalMap.put("total",dayTotalRev);
	finalInvoiceDateList.addAll(DaytotalMap);
}

grandTotalMap.put("invoiceDate","GrandTotal");
grandTotalMap.put("totalMrpValue",grandTotalMrpValue);
grandTotalMap.put("basicRevenue",grandTotalBasicRev);
grandTotalMap.put("bedRevenue",grandTotalBedRev);
grandTotalMap.put("vatRevenue",grandTotalVatRev);
grandTotalMap.put("cstRevenue",grandTotalCstRev);
grandTotalMap.put("ppd",grandTotalPpd);
grandTotalMap.put("total",grandTotalRev);
finalInvoiceDateList.addAll(grandTotalMap);
context.finalInvoiceDateList=finalInvoiceDateList;















