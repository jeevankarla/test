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
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
	Debug.log("#####periodBillingId####"+parameters.periodBillingId);
	Debug.log("######customTimePeriodId###"+parameters.customTimePeriodId);
	periodBillingList=[];
	facilityIds=[];
	conditionList=[];
	itemsList=[];
	itemsListMap=[:];
	invoiceListMap=[:];
	invoiceList=[];
	periodBillingIds=[];
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"COM_CANCELLED"));
	if(!((parameters.periodBillingId).equals("allInstitutions"))){
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, parameters.periodBillingId));
	}
	EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling", condExpr, null, null, null, false);
	facilityIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "facilityId", true);
	periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	
	customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", parameters.customTimePeriodId), false);
	Timestamp fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	Timestamp thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	facilityIds.each{eachFacilityId->
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS ,eachFacilityId));
	condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["originFacilityId","orderDate","orderId","productId","shipmentTypeId","itemDescription","quantity","unitListPrice"] as Set;
	itemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, fieldsToSelect , ["-estimatedDeliveryDate"], null, false);
	itemsListMap.put(eachFacilityId, itemsList);
		context.itemsListMap=itemsListMap;
	}
	periodBillingIds.each{eachperiodBillingId->
		conditionList.clear();
//		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "INVOICE_APPROVED"));
//		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
//		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,eachperiodBillingId));
		cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		invoiceList = delegator.findList("Invoice", cond, ["invoiceId","invoiceDate","facilityId"] as Set , ["-invoiceDate"], null, false);
		invoice=EntityUtil.getFirst(invoiceList);
		invoiceListMap.put(invoice.getString("facilityId"), invoice);
		context.invoiceListMap=invoiceListMap;
	}
	
	
	
