import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import java.util.Map.Entry;


dctx = dispatcher.getDispatchContext();

JSONObject facilityJSON= new JSONObject();
String facilityId = request.getParameter("facilityId");
if (facilityId) {
	boothDetails = (ByProductNetworkServices.getBoothDetails(dctx , UtilMisc.toMap("boothId",facilityId,"userLogin", userLogin))).get("boothDetails");
	facilityJSON.put("name", facilityId + " [" + boothDetails.get("boothName") + "]");
	facilityJSON.put("owner", boothDetails.get("vendorName"));
	facilityJSON.put("phone", boothDetails.get("vendorPhone"))
	boothRouteMap=ByProductNetworkServices.getBoothRoute(dispatcher.getDispatchContext(),[boothId:facilityId]).get("boothDetails");	
	facilityJSON.put("route", boothRouteMap.get("routeId"));
	def sdf = new SimpleDateFormat("dd/MM/yyyy");
	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	thruDate =  UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	Map catTotals = [:];

	shipmentIds = (List)ByProductNetworkServices.getShipmentIdsSupplyType(delegator, fromDate, thruDate, null);	
	if (shipmentIds) {
		todaysSalesTotals = ByProductReportServices.getDayDespatchDetails(dispatcher.getDispatchContext(), ["userLogin":userLogin ,facilityIds:UtilMisc.toList(facilityId),
		shipmentIds: shipmentIds, fromDate:fromDate, thruDate:thruDate]);
		catTotals = (Map)todaysSalesTotals.get("categoryTotals");
	}
	BigDecimal milkTotal = BigDecimal.ZERO;
	BigDecimal curdTotal = BigDecimal.ZERO;
	BigDecimal butterTotal = BigDecimal.ZERO;
	BigDecimal gheeTotal = BigDecimal.ZERO;
	BigDecimal butterMilkTotal = BigDecimal.ZERO;
	BigDecimal paneerTotal = BigDecimal.ZERO;
	BigDecimal otherTotal = BigDecimal.ZERO;
	
	Iterator categoryIter = catTotals.entrySet().iterator();
	while(categoryIter.hasNext()) {
		Map.Entry entry = (Entry)categoryIter.next();
		String categoryId = (String)entry.getKey();
		if(categoryId.equalsIgnoreCase("Milk")){
			milkTotal = (BigDecimal)catTotals.get(categoryId);
		}
		else if(categoryId.equalsIgnoreCase("Curd")){
			curdTotal = (BigDecimal)catTotals.get(categoryId);
		}
		else if(categoryId.equalsIgnoreCase("Butter")){
			butterTotal = (BigDecimal)catTotals.get(categoryId);
		}
		else if(categoryId.equalsIgnoreCase("Ghee")){
			gheeTotal = (BigDecimal)catTotals.get(categoryId);
		}
		else if(categoryId.equalsIgnoreCase("ButterMilk")){
			butterMilkTotal = (BigDecimal)catTotals.get(categoryId);
		}
		else if(categoryId.equalsIgnoreCase("Paneer")){
			paneerTotal = (BigDecimal)catTotals.get(categoryId);
		}
		else{
			otherTotal = otherTotal.add((BigDecimal)catTotals.get(categoryId));
		}
	}
	rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	milkTotal = milkTotal.setScale(0, rounding);
	curdTotal = curdTotal.setScale(0, rounding);
	gheeTotal = gheeTotal.setScale(0, rounding);
	butterTotal = butterTotal.setScale(0, rounding);
	paneerTotal = paneerTotal.setScale(0, rounding);
	butterMilkTotal = butterMilkTotal.setScale(0, rounding);
	otherTotal = otherTotal.setScale(0, rounding);
	String displayDate = UtilDateTime.toDateString(fromDate, "dd MMM, yyyy");
	
	
	//facilityJSON.put("quantity", dayTotals.totalQuantity + " litres");
	facilityJSON.put("dispatchTotals", displayDate +" Dispatch Totals --");
	facilityJSON.put("milk", milkTotal + "Ltrs");
	facilityJSON.put("curd", curdTotal + "Ltrs");
	
}
request.setAttribute("facilityJSON", facilityJSON.toString());
Debug.logInfo("facilityJSON="+facilityJSON,"");
