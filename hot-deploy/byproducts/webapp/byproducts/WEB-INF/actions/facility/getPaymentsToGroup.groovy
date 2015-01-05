import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();

hideSearch = parameters.hideSearch;
if(hideSearch == "Y"){
	roleTypeId = parameters.roleTypeId;
	context.roleTypeId = roleTypeId;
	partyId = parameters.partyId;
	paymentDate = parameters.paymentDate;
	finAccountId = parameters.finAccountId;
	dayStart = null;
	dayEnd = null;
	if(paymentDate){
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
		try {
		fromDateTs = new java.sql.Timestamp(sdfo.parse(paymentDate).getTime());	} catch (ParseException e) {
		}
		dayStart = UtilDateTime.getDayStart(fromDateTs);
		dayEnd = UtilDateTime.getDayEnd(fromDateTs);
	}else{
		dayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
	
	partyIdsList = [];
	condList = [];
	if(roleTypeId){
		
		condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, roleTypeId));
		if(partyId){
			condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, partyId));
		}
		if(finAccountId){
			finAccountDetail = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId),false);
			context.finAccountDetail = finAccountDetail;
			condList.add(EntityCondition.makeCondition("finAccountId" ,EntityOperator.EQUALS, finAccountId));
			
		}
		cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
		finAccountRole = delegator.findList("FinAccountRole", cond, null, null, null ,false);
		
		finAccountRole = EntityUtil.filterByDate(finAccountRole, dayStart);
		partyIdsList = EntityUtil.getFieldListFromEntityList(finAccountRole, "partyId", true);
	}
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
	condList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdsList));
	condList.add(EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, null));
	
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	if(paymentDate){
		condList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
		condList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	}
	condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
	payments = delegator.findList("Payment", condExpr, null, null, null, false);
	
	payPartyIds = EntityUtil.getFieldListFromEntityList(payments, "partyIdTo", true);
	
	paymentDetailsList = [];
	payments.each{ eachPay ->
		partyName = PartyHelper.getPartyName(delegator, eachPay.partyIdTo, false);
		tempMap = [:];
		tempMap.put("paymentId", eachPay.paymentId);
		tempMap.put("partyId", eachPay.partyIdTo);
		tempMap.put("partyName", partyName);
		tempMap.put("paymentDate", eachPay.paymentDate);
		tempMap.put("amount", eachPay.amount);
		paymentDetailsList.add(tempMap);
	}
	context.paymentDetailsList = paymentDetailsList;
	
	paymentDate = null;
}

