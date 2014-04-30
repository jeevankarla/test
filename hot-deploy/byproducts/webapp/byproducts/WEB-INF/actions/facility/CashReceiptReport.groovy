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

receiptDate = UtilDateTime.nowTimestamp();
effectiveDate = null;
effectiveDateStr = parameters.receiptDate;
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
dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(effectiveDate);

context.put("effectiveDateStr",effectiveDateStr);
context.put("receiptDate",receiptDate);

tempPaymentIds=FastList.newInstance();
conditionList=[];
if(parameters.paymentIds){
	paymentId=parameters.paymentIds;
	tempPaymentIds.addAll(paymentId);
}
context.tempPaymentIds=tempPaymentIds;

partyIdFrom = "";
amount = "";
paymentId = "";

paymentGrpMap = [:];
if(tempPaymentIds.size()>1){
	paymentGroupList = delegator.findList("PaymentGroup", null, null, null, null, false);
	paymentGroupIdList = EntityUtil.getFieldListFromEntityList(paymentGroupList, "paymentGroupId", true);
	paymentGroupIdList.each { paymentGroupId-> 
		paymentMap = [:];
		condList =[];
		condList.add(EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS,paymentGroupId));
		condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN,tempPaymentIds));
		EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
		paymentGroupMemberList = delegator.findList("PaymentGroupMember", cond, null, null, null, false);
		paymentIdList = EntityUtil.getFieldListFromEntityList(paymentGroupMemberList, "paymentId", true);
		conditionList =[];
		conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN,paymentIdList));
		conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS,"SALES_PAYIN"));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"PMNT_RECEIVED"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
		paymentList = delegator.findList("Payment", condition, null, null, null, false);
		if(UtilValidate.isNotEmpty(paymentList)){
			paymentList.each{ payment->
					cashReceivedMap = [:];
					partyIdFrom = payment.partyIdFrom;
					amount = payment.amount;
					paymentId = payment.paymentId;
					cashReceivedMap["partyIdFrom"]=partyIdFrom;
					cashReceivedMap["amount"]=amount;
					cashReceivedMap["paymentId"]=paymentId;
					paymentMap.put(paymentId,cashReceivedMap);
			}
		}
		if(UtilValidate.isNotEmpty(paymentMap)){
			paymentGrpMap.put(paymentGroupId,paymentMap);
		}
	}
}else{
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS,parameters.paymentIds));
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS,"SALES_PAYIN"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"PMNT_RECEIVED"));
	EntityCondition condition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
	paymentList = delegator.findList("Payment", condition, null, null, null, false);
	paymentMap = [:];
	if(UtilValidate.isNotEmpty(paymentList)){
		paymentList.each{ payment->
				cashReceivedMap = [:];
				partyIdFrom = payment.partyIdFrom;
				amount = payment.amount;
				paymentId = payment.paymentId;
				cashReceivedMap["partyIdFrom"]=partyIdFrom;
				cashReceivedMap["amount"]=amount;
				cashReceivedMap["paymentId"]=paymentId;
				paymentMap.put(paymentId,cashReceivedMap);
		}
	}
	paymentGroupId="NOGROUP";
	if(UtilValidate.isNotEmpty(paymentMap)){
		paymentGrpMap.put(paymentGroupId,paymentMap);
	}
}
context.put("paymentGrpMap",paymentGrpMap);
