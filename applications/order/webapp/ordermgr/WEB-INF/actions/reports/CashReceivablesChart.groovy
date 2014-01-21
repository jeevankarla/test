import java.sql.Timestamp;
import org.ofbiz.base.util.*;
import org.ofbiz.service.DispatchContext;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;

rounding = RoundingMode.HALF_UP;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.salesDate) {
		salesDate = new java.sql.Timestamp(sdf.parse(parameters.salesDate).getTime());
	}
	else {
		salesDate = UtilDateTime.nowTimestamp();
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

boothsPaymentsDetail = NetworkServices.getBoothReceivablePayments(delegator, dispatcher, userLogin, UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"), null, null, null, Boolean.TRUE ,Boolean.FALSE);
cashReceivables = boothsPaymentsDetail.get("invoicesTotalAmount");
cashReceivables =(cashReceivables.divide(new BigDecimal(100000))).setScale(1, rounding);

dctx = dispatcher.getDispatchContext();
context.put("paymentDate", UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"));
context.put("userLogin",userLogin);
boolean onlyCurrentDues = Boolean.TRUE;
context.put("onlyCurrentDues",onlyCurrentDues);
boothsPaymentsDetail = NetworkServices.getBoothPaidPayments(dctx , context);
cashReceived = boothsPaymentsDetail.get("invoicesTotalAmount");
cashReceived =(cashReceived.divide(new BigDecimal(100000))).setScale(1, rounding);

boothsPaymentsDetail = NetworkServices.getBoothReceivablePayments(delegator, dispatcher, userLogin, UtilDateTime.toDateString(UtilDateTime.getDayStart(salesDate, -1), "yyyy-MM-dd HH:mm:ss"), null, null,null,Boolean.FALSE, Boolean.TRUE);
pastDues = boothsPaymentsDetail.get("invoicesTotalDueAmount");
pastDues =(pastDues.divide(new BigDecimal(100000))).setScale(1, rounding);

context.cashReceivables= cashReceivables;
context.cashReceived= cashReceived;
context.pastDues= pastDues;
context.salesDate=salesDate;

Debug.logInfo("cashReceivables: "+cashReceivables+"; cashReceived: " + cashReceived +"; pastDues: " + pastDues, "");

