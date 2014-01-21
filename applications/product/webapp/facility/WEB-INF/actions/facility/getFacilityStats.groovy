import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import java.text.SimpleDateFormat;
import org.ofbiz.network.NetworkServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;


dctx = dispatcher.getDispatchContext();

JSONObject facilityJSON= new JSONObject();
String facilityId = request.getParameter("facilityId");
if (facilityId) {
	boothDetails = (NetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",facilityId,"userLogin", userLogin))).get("boothDetails");
	facilityJSON.put("name", boothDetails.get("boothName"));
	facilityJSON.put("owner", boothDetails.get("vendorName"));
	facilityJSON.put("route", boothDetails.get("routeId"));
	facilityJSON.put("phone", boothDetails.get("vendorPhone"))
	def sdf = new SimpleDateFormat("dd/MM/yyyy");
	fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse("01/05/2013").getTime()));
	
	thruDate = UtilDateTime.nowTimestamp();
//	salesDate = UtilDateTime.addDaysToTimestamp(salesDate, -39);
	boothsList = facilityId;
	boothTotalsMap = ByProductReportServices.getTotalSales(dispatcher.getDispatchContext(), ["userLogin":userLogin ,facilityList:UtilMisc.toList(boothsList),fromDate:fromDate, thruDate:thruDate]);
	//facilityJSON.put("quantity", dayTotals.totalQuantity + " litres");
	facilityJSON.put("revenue", "Rs" + new BigDecimal(boothTotalsMap.totalValue).setScale(0,BigDecimal.ROUND_HALF_UP));
}
request.setAttribute("facilityJSON", facilityJSON.toString());
Debug.logInfo("facilityJSON="+facilityJSON,"");
