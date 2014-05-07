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



paymentIds=FastList.newInstance();
tempPaymentIds=FastList.newInstance();
conditionList=[];
if(parameters.paymentId){
	paymentId=parameters.paymentId;
	tempPaymentIds.add(paymentId);
	parameters.paymentIds = tempPaymentIds;
}
printPaymentsList = FastList.newInstance();
if(parameters.paymentIds){
	paymentIds.addAll(parameters.paymentIds);
	printPaymentsList = delegator.findList("Payment",EntityCondition.makeCondition("paymentId", EntityOperator.IN , paymentIds)  , null, null, null, false );
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


