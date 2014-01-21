import java.io.ObjectOutputStream.DebugTraceInfoStack;

import org.ofbiz.base.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.network.LmsServices;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;

dctx = dispatcher.getDispatchContext();
paymentDate = parameters.paymentDate;

routeWisePaidMap=[:];
routeValueMap=[:];
GrTotalValueMap=[:];
routeValueMap["TOTAL_RECBLE"]=BigDecimal.ZERO;
routeValueMap["TRNSPTDUEAMT"]=BigDecimal.ZERO;
routeValueMap["NETVALUE"]=BigDecimal.ZERO;
GrTotalValueMap["TOTAL_RECBLE"] = BigDecimal.ZERO;
GrTotalValueMap["TRNSPTDUEAMT"] = BigDecimal.ZERO;
GrTotalValueMap["NETVALUE"] = BigDecimal.ZERO;
routes = NetworkServices.getRoutes(dctx , [facilityTypeId:"ROUTE"]);

routesList = routes.routesList;
routesList.each { route ->
	tempRouteValueMap=[:];
	paymentDate = paymentDate;
	transporterPaymentsDetail = NetworkServices.getBoothPaidPayments( dctx , [paymentDate:paymentDate, facilityId: route]);
	totalReceivable = transporterPaymentsDetail.get("invoicesTotalAmount");
	routeValueMap["TOTAL_RECBLE"]=totalReceivable;
	GrTotalValueMap["TOTAL_RECBLE"] =(BigDecimal)(GrTotalValueMap["TOTAL_RECBLE"]).add(totalReceivable);
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		supplyDateTime = new java.sql.Timestamp(sdf.parse(paymentDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+paymentDate, "");
	}
	context.put("userLogin",userLogin);
	context.put("fromDate", supplyDateTime);
	context.put("thruDate", supplyDateTime);
	context.put("invoiceTypeId", "TRANSPORTER_PAYIN");
	context.put("facilityId", route)
	transporterDuesMap = LmsServices.getTransporterDues(dctx , context);
	transporterDueAmount = transporterDuesMap.get("invoicesTotalAmount");
	routeValueMap["TRNSPTDUEAMT"]=transporterDueAmount;
	GrTotalValueMap["TRNSPTDUEAMT"] =(BigDecimal)(GrTotalValueMap["TRNSPTDUEAMT"]).add(transporterDueAmount);
	netValue = routeValueMap["TOTAL_RECBLE"]-routeValueMap["TRNSPTDUEAMT"];
	routeValueMap["NETVALUE"] = netValue;
	GrTotalValueMap["NETVALUE"] =(BigDecimal)(GrTotalValueMap["NETVALUE"]).add(netValue);
	tempRouteValueMap.putAll(routeValueMap);
	routeWisePaidMap.put(route, tempRouteValueMap);
}

context.put("routeWisePaidMap",routeWisePaidMap);
context.put("GrTotalValueMap",GrTotalValueMap);

