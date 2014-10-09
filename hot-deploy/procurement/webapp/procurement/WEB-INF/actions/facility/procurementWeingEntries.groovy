import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;

fromDate = parameters.fromDate;
java.sql.Timestamp fromTimestamp =null;
if(UtilValidate.isNotEmpty(fromDate)){
java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
java.util.Date fromParsedDate = dateFormat.parse(fromDate);
 fromTimestamp = new java.sql.Timestamp(fromParsedDate.getTime());


}else{
 fromTimestamp =UtilDateTime.nowTimestamp();
}

dayBegin=UtilDateTime.getDayStart(fromTimestamp, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(fromTimestamp, timeZone, locale);
dctx = dispatcher.getDispatchContext();
conditionList = [];
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
conditionList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS , parameters.supplyTypeEnumId));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS , "PURCHASE_ORDER"));
conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS , "ORDER_CREATED"));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

orderItemsList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,["estimatedDeliveryDate","changeDatetime"],null,false);

JSONArray orderItemsJSON = new JSONArray();
if(UtilValidate.isNotEmpty(orderItemsList)){
	i=0;
	orderItemsList.each{ orderItem->
		++i;
		centerDetails = ProcurementNetworkServices.getCenterDtails(dctx,[centerId:orderItem.originFacilityId]);
		centerName=centerDetails.get("centerFacility").get("facilityName");
		centerCode=centerDetails.get("centerFacility").get("facilityCode");		
		unitCode = centerDetails.get("unitFacility").get("facilityCode");		
		estimatedDeliveryDate=orderItem.estimatedDeliveryDate;
		JSONObject newObj = new JSONObject();
		newObj.put("id",i);
		newObj.put("orderId",orderItem.orderId);
		newObj.put("orderItemSeqId",orderItem.orderItemSeqId);
		estimatedDeliveryDate=UtilDateTime.toDateString(orderItem.estimatedDeliveryDate,"dd-MM-yyyy");
		newObj.put("estimatedDeliveryDate",estimatedDeliveryDate);
		newObj.put("purchaseTime",orderItem.supplyTypeEnumId);
		newObj.put("centerName",centerName);
		newObj.put("unitCode",unitCode);
		newObj.put("centerCode",centerCode);
		newObj.put("qtyLtrs",orderItem.quantityLtrs);
		newObj.put("quantity",orderItem.quantity);
		newObj.put("fat",orderItem.fat);
		newObj.put("snf",orderItem.snf);
		newObj.put("sQuantityLtrs",orderItem.sQuantityLtrs);
		newObj.put("sFat",orderItem.sFat);
		newObj.put("cQuantityLtrs",orderItem.cQuantityLtrs);
		newObj.put("ptcQuantity",orderItem.ptcQuantity);
		newObj.put("productId",orderItem.productId);
		orderItemsJSON.add(newObj);
	}
}
context.dataJson=orderItemsJSON;