import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
import org.ofbiz.entity.GenericValue;


Debug.log("orderList=====Other====="+orderList);


partyIdsList = []as HashSet;

for (eachList in orderList) {
	
	partyIdsList.add(eachList.partyId);
	
}

//List partyIdsList = ["36456"];


JSONObject eachAdvancePaymentOrderMap = new JSONObject();
for (String eachPartyList : partyIdsList) {
	
	   
		  /* condtList = [];
		   condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, eachList.orderId));
		   cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
		   OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
		   getFirstOrderPayment = EntityUtil.getFirst(OrderPaymentPreference);
		   orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
	     */	  
	       
	       
		   conditonList = [];
		   conditonList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,eachPartyList));
		   conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
		   conditonList.add(EntityCondition.makeCondition("paymentTypeId" ,EntityOperator.IN,["ADVFRMOTHERS_PAYIN","ADVFRMDAIRYCU_PAYIN","ADVFRMDEEP_PAYIN","ADVFRMOTHERS_PAYIN","ADVFRMICPCUST_PAYIN"]));
		   cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
		   PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
		   
		   //Debug.log("PaymentList============"+PaymentList.size());
		   
		   JSONArray advanceList = new JSONArray();
		   
		   if(UtilValidate.isNotEmpty(PaymentList)){
			   
			   
			   for (eachPaymentList in PaymentList) {
				   JSONObject tempMap = new JSONObject();
				   
				   //==============for Advance Payments============
				   
				      mainBalance = eachPaymentList.amount;
				   
				   conditonList = [];
				   conditonList.add(EntityCondition.makeCondition("paymentId" ,EntityOperator.EQUALS,eachPaymentList.paymentId));
				   cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
				   OrderPreferencePaymentApplicationList = delegator.findList("OrderPreferencePaymentApplication", cond, null, null, null ,false);
				
				  // Debug.log("OrderPreferencePaymentApplicationList============="+OrderPreferencePaymentApplicationList);
				   
				   appliedAmount = 0;
				   
				   if(UtilValidate.isNotEmpty(OrderPreferencePaymentApplicationList)){
				   //appliedAmount = OrderPreferencePaymentApplicationList[0].amountApplied;
				   
				   for (eachPreferenceList in OrderPreferencePaymentApplicationList) {
					   appliedAmount = appliedAmount+eachPreferenceList.get("amountApplied");
				   }
				   
				   
				   }
				   balance = mainBalance-appliedAmount;
				   if(balance>0){
				   tempMap.put("paymentId",eachPaymentList.paymentId);
				   tempMap.put("amount",mainBalance);
				   tempMap.put("balance",balance);
				   advanceList.add(tempMap);
				   eachAdvancePaymentOrderMap.put(eachPartyList, advanceList);
				   }else{
				   JSONObject tempMap1 = new JSONObject();
				   tempMap1.put("paymentId","NoAdvance");
				   tempMap1.put("amount","noAmount");
				   tempMap1.put("balance","noBalance");
				   advanceList.add(tempMap1);
				   eachAdvancePaymentOrderMap.put(eachPartyList, advanceList);
				   }
			       
			   }
	          }
		      else{
				  JSONObject tempMap = new JSONObject();
				  tempMap.put("paymentId","NoAdvance");
				  tempMap.put("amount","noAmount");
				  tempMap.put("balance","noBalance");
				  advanceList.add(tempMap);
				  eachAdvancePaymentOrderMap.put(eachPartyList, advanceList);
			    }
}

Debug.log("eachAdvancePaymentOrderMap============"+eachAdvancePaymentOrderMap);

context.eachAdvancePaymentOrderMap = eachAdvancePaymentOrderMap;


balanceAmountMap = [:];



for (eachOrder in orderList) {
	
	
	
    orderHeaderList=delegator.findOne("OrderHeader",[orderId : eachOrder.orderId], false);
	
	
	 grandTotal = orderHeaderList.get("grandTotal");
	
	 condtList = [];
	 condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, eachOrder.orderId));
	 condtList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	 cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
	 OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
	 getFirstOrderPayment = EntityUtil.getFirst(OrderPaymentPreference);
	 orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
	
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("orderPaymentPreferenceId" ,EntityOperator.IN,orderPreferenceIds));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderPreferencePaymentApplicationList = delegator.findList("OrderPreferencePaymentApplication", cond, null, null, null ,false);
	
	//Debug.log("OrderPreferencePaymentApplicationList=============="+OrderPreferencePaymentApplicationList);
	
	appliedAmount = 0;
	
	if(UtilValidate.isNotEmpty(OrderPreferencePaymentApplicationList)){
	
		for (eachPreferenceList in OrderPreferencePaymentApplicationList) {
			appliedAmount = appliedAmount+eachPreferenceList.get("amountApplied");
		}
		
		/*if(UtilValidate.isNotEmpty(OrderPreferencePaymentApplicationList)){
		appliedAmount = OrderPreferencePaymentApplicationList[0].amountApplied;
		}
		*/
		
		//Debug.log("grandTotal=============="+grandTotal);
		
		//Debug.log("appliedAmount=============="+appliedAmount);
		
		balance = grandTotal-appliedAmount;
		
		    tempMap = [:];
			tempMap.put("grandTotal", eachOrder.orderTotal);
			tempMap.put("balance", balance);
			tempMap.put("receivedAMT", appliedAmount);
			
		balanceAmountMap.put(eachOrder.orderId, tempMap);
		
	}
	else{
		tempMap = [:];
		tempMap.put("grandTotal", eachOrder.orderTotal);
		tempMap.put("balance", grandTotal);
		tempMap.put("receivedAMT", -1);
		
	balanceAmountMap.put(eachOrder.orderId, tempMap);
	}
	
}

Debug.log("balanceAmountMap=========="+balanceAmountMap);


context.balanceAmountMap = balanceAmountMap;

