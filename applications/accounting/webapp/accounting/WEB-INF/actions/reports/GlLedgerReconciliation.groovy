
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
import org.ofbiz.accounting.invoice.InvoiceWorker;

import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.accounting.ledger.GeneralLedgerServices;
import org.ofbiz.party.party.PartyHelper;

userLogin= context.userLogin;

fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;

dctx = dispatcher.getDispatchContext();

SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
Timestamp fromDateTs = null;
if(fromDateStr){
	try {
		fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
	} catch (ParseException e) {
	}
}
Timestamp thruDateTs = null;
if(thruDateStr){
	try {
		thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
	} catch (ParseException e) {
	}
}
fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);
invoiceType=parameters.invoiceType;
purposeTypeId=parameters.purposeTypeId;
List glLedgerReconciliationList=FastList.newInstance();
tempMap=[:];
Map resultMap = GeneralLedgerServices.getLedgerAmountByInvoiceAndPayments(dctx,UtilMisc.toMap("fromDate",fromDate,"thruDate",thruDate,"invoiceTypeId",invoiceType,"purposeTypeId",purposeTypeId));
tempMap.debitAmount=resultMap.get("debitAmount");
tempMap.creditAmount=resultMap.get("creditAmount");
endingBalance=resultMap.get("endingBalance");
if(endingBalance>0){
	tempMap.endingBalance=endingBalance+"(Dr)";
}else if(endingBalance<0){
	tempMap.endingBalance=-(endingBalance)+"(Cr)";
}else{
   tempMap.endingBalance=endingBalance;
}
if(UtilValidate.isNotEmpty(purposeTypeId)){
	tempMap.purposeTypeId=purposeTypeId;
}else{
    tempMap.purposeTypeId="ALL";
}
glLedgerReconciliationList.add(tempMap);
context.glLedgerReconciliationList=glLedgerReconciliationList;


