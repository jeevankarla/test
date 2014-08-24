import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilNumber;

//${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalAmount?string("#0")), "%rupees-and-paise", locale).toUpperCase()}

paymentIds=FastList.newInstance();
tempPaymentIds=FastList.newInstance();
conditionList=[];
if(parameters.paymentId){
	paymentId=parameters.paymentId;
	tempPaymentIds.add(paymentId);
	parameters.paymentIds = tempPaymentIds;
}
paymentMethodTypeId = "";
paymentDescription = "";
printPaymentsList = FastList.newInstance();
if(parameters.paymentIds){
	
	paymentIds.addAll(parameters.paymentIds);
	tempprintPaymentsList = delegator.findList("Payment",EntityCondition.makeCondition("paymentId", EntityOperator.IN , paymentIds)  , null, null, null, false );
	tempprintPaymentsList.each{paymentRecipt->
		tempprintPaymentMap=[:];
		tempprintPaymentMap.putAll(paymentRecipt);
		totalAmount=paymentRecipt.amount;
		paymentMethodTypeId = paymentRecipt.paymentMethodTypeId;
		context.put("paymentMethodTypeId",paymentMethodTypeId);
		paymentMethodTypeDetails = delegator.findOne("PaymentMethodType", [paymentMethodTypeId : paymentMethodTypeId], false);
		if(UtilValidate.isNotEmpty(paymentMethodTypeDetails)){
			paymentDescription = paymentMethodTypeDetails.description;
			context.put("paymentDescription",paymentDescription);
		}
	
	amountwords=UtilNumber.formatRuleBasedAmount(totalAmount,"%rupees-and-paise", locale).toUpperCase();
	tempprintPaymentMap.put("amountWords",amountwords);
	printPaymentsList.add(tempprintPaymentMap);
	}
}

paymentType = delegator.findList("PaymentType", EntityCondition.makeCondition(["paymentTypeId" : printPaymentsList[0].get("paymentTypeId")]), null, null, null, false);
parentTypeId = paymentType[0].get("parentTypeId");
reportType = null;
if(parentTypeId == "RECEIPT"){
	reportType = "RECEIPT";
}
else{
	reportType = "PAYMENT";
}

context.reportType = reportType;
context.put("printPaymentsList",printPaymentsList);


