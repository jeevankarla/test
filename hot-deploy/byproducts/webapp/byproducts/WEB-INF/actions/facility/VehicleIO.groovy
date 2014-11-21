import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;

routeIdsList = [];
dctx = dispatcher.getDispatchContext();

effectiveDate = null;
thruEffectiveDate = null;

effectiveDateStr = parameters.fromDate;
thruEffectiveDateStr = parameters.thruDate;

Timestamp fromDateTs = null;
if(effectiveDateStr){
	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d,yyyy");
	try {
		fromDateTs = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
	} catch (ParseException e) {
	}
}

Timestamp thruDateTs = null;
if(thruEffectiveDateStr){
	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d,yyyy");
	try {
		thruDateTs = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr).getTime());
	} catch (ParseException e) {
	}
}

dayBegin = UtilDateTime.getDayStart(fromDateTs);
dayEnd = UtilDateTime.getDayEnd(thruDateTs);

context.put("effectiveDateStr",effectiveDateStr);
context.put("thruEffectiveDateStr",thruEffectiveDateStr);

shipmentIds = [];
shipmentType = parameters.shipmentTypeId; 
if(shipmentType.equals("AM"))
	{
		amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"AM");
		shipmentIds.addAll(amShipmentIds);
		
	}
if(shipmentType.equals("PM"))
	{
		pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"PM");
		shipmentIds.addAll(pmShipmentIds);
	}

vehicleMap =[:];
List conditionList = [];
shipmentIds.each{ shipmentId ->
	vehicleInOutList = delegator.findList("VehicleTripAndStatusAndShipment", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS,shipmentId), null, null, null, false);
	tempMap = [:];
	tempMap["vehicleNum"] = "";
	tempMap["outTime"] = "";
	tempMap["dispatchedTime"] = "";
	tempMap["returnTime"]="";
	tempMap["statusId"] = "";
		vehicleInOutList.each{ vehicle ->
			estStartDate = vehicle.estimatedStartDate;
			statusId = vehicle.statusId;
			tempMap["vehicleNum"]=vehicle.vehicleId;
			if(statusId == "VEHICLE_OUT"){
				tempMap["outTime"]=UtilDateTime.toDateString(vehicle.estimatedStartDate, "dd-MM-yyyy HH:mm:ss");
						tempMap["statusId"] = statusId;
			}
			if(statusId == "VEHICLE_DISPACHED"){
				tempMap["dispatchedTime"]=UtilDateTime.toDateString(vehicle.estimatedStartDate, "dd-MM-yyyy HH:mm:ss");
						tempMap["statusId"] = statusId;
			}
			if(statusId == "VEHICLE_CRATE_RTN"){
				tempMap["returnTime"]=UtilDateTime.toDateString(vehicle.estimatedStartDate, "dd-MM-yyyy HH:mm:ss");
						tempMap["statusId"] = statusId;
			}
		}
	if(UtilValidate.isNotEmpty(tempMap)){
		vehicleMap.put(shipmentId,tempMap);

	}
}
context.put("vehicleMap",vehicleMap);
