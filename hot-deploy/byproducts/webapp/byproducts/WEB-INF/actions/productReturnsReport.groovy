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

effectiveDate = null;
effectiveDateStr = parameters.prodReturnDate;
if (UtilValidate.isEmpty(effectiveDateStr)) {
	effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		effectiveDate = new java.sql.Timestamp(dateFormat.parse(effectiveDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, "");
	}
}
context.put("effectiveDateStr",effectiveDateStr);
dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(effectiveDate);

returnProductList = [];
date = "";
boothId = "";
routeId = "";
shipmentTypeId = "";
productId = "";
returnQuantity = "";
returnReasonId = "";

shipmentIds=[];
shipmentIdList = [];

if(parameters.subscriptionTypeId == "ALL"){
		shipmentIds  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
		shipmentIdList.addAll(shipmentIds);
}else{
	   shipmentIds = ByProductNetworkServices.getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),parameters.subscriptionTypeId);
	   shipmentIdList.addAll(shipmentIds);
}

conditionList=[];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdList));
conditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
returnCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
returnHeaderItemsList = delegator.findList("ReturnHeaderItemsAndShipment", returnCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(returnHeaderItemsList)){
	returnHeaderItemsList.each{ returnItem->
			productReturnMap = [:];
			date = returnItem.estimatedShipDate;
			boothId = returnItem.originFacilityId;
			routeId = returnItem.routeId;
			shipmentTypeId = returnItem.shipmentTypeId;
			productId = returnItem.productId;
			returnQuantiy = returnItem.returnQuantity;
			returnReasonId = returnItem.returnReasonId;
			userLogin = returnItem.createdBy; 
			productReturnMap["date"]=dayBegin;
			productReturnMap["boothId"]=boothId;
			productReturnMap["routeId"]=routeId;
			productReturnMap["shipmentTypeId"]=shipmentTypeId;
			productReturnMap["productId"]=productId;
			productReturnMap["returnQuantity"]=returnQuantiy;
			productReturnMap["returnReasonId"]=returnReasonId;
			productReturnMap["userLoginId"]= userLogin;
		returnProductList.add(productReturnMap);
	}
}
returnProductList = UtilMisc.sortMaps(returnProductList, UtilMisc.toList("routeId"));
context.put("returnProductList",returnProductList);
