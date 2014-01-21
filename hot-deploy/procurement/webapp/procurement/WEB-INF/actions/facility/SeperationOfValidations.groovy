import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
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
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;



if(UtilValidate.isNotEmpty(context.listProcBillingValidation)){
	listProcurementBillingValidation =context.listProcBillingValidation;
	listProcValidations = listProcurementBillingValidation.getCompleteList();
	negativeAmtList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "NEGATIVE_AMOUNT"]);
	qtySnfFatCheckList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "QTYSNFFAT_CHECK"]);
	outLierList = EntityUtil.filterByAnd(listProcValidations, [validationTypeId : "QTY_OUTLIER"]);
    
	qtySnfFatFinalList =[];
	//This is for Snf and Fat check
if(UtilValidate.isNotEmpty(qtySnfFatCheckList)){
	qtySnfFatCheckList.each{ qtySnfFatCheck ->	
		Map qtySnfFatCheckMap = FastMap.newInstance();
		tempMap =[:];
		tempMap.putAll(qtySnfFatCheck)
		qtySnfFatCheckMap.putAll(tempMap);
		
		orderId =qtySnfFatCheck.getAt("orderId");
		orderItemSeqId =qtySnfFatCheck.getAt("orderItemSeqId");
		
		conditionList =[];
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS ,"ORDER_CREATED")));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		qtySnfFinalList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
		if(UtilValidate.isNotEmpty(qtySnfFinalList)){
			qtySnfList =EntityUtil.getFirst(qtySnfFinalList);
			tempMap1=[:];
			tempMap1.putAll(qtySnfList);
			qtySnfFatCheckMap.putAll(tempMap1);
			qtySnfFatFinalList.add(qtySnfFatCheckMap);
		}
		
	}
}
// this is for quantity out lier	
	outLierFinalList =[];
if(UtilValidate.isNotEmpty(outLierList)){	
	outLierList.each{ outLiers ->
		Map outLierCheckMap = FastMap.newInstance();
		tempOutMap =[:];
		tempOutMap.putAll(outLiers)
		outLierCheckMap.putAll(tempOutMap);
		
		orderId =outLiers.getAt("orderId");
		orderItemSeqId =outLiers.getAt("orderItemSeqId");
		
		conditionList =[];
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,orderItemSeqId)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS ,"ORDER_CREATED")));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		outLierQtyList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
		if(UtilValidate.isNotEmpty(outLierQtyList)){
			qtyOutLier =EntityUtil.getFirst(outLierQtyList);			
			tempMap2=[:];
			tempMap2.putAll(qtyOutLier);
			outLierCheckMap.putAll(tempMap2);
			outLierFinalList.add(outLierCheckMap);
		}
		
	}
}
	context.putAt("negativeAmtList", negativeAmtList);
	context.putAt("qtySnfFinalList", qtySnfFatFinalList);
	context.putAt("outLierFinalList", outLierFinalList);

}
