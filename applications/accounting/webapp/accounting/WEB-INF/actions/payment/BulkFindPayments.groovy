import org.ofbiz.entity.*
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;

paymentId = parameters.paymentId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
paymentTypeId=parameters.paymentTypeId;
context.fromDate=fromDate;
context.thruDate=thruDate;
Timestamp fromdate1=null;
Timestamp thrudate1=null;

Debug.log("Date======"+parameters.ownerPartyId);
if(UtilValidate.isNotEmpty(fromDate)||UtilValidate.isNotEmpty(thruDate)){
	def sdf = new SimpleDateFormat("yy-MM-dd");
	
	try {
		if (UtilValidate.isNotEmpty(fromDate)) {
			fromdate1 = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	try {
		if (UtilValidate.isNotEmpty(thruDate)) {
			thrudate1 = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	thruDateEnd = UtilDateTime.getDayEnd(thrudate1, timeZone, locale);

}
List paymentList = [];
conditionList = [];
if(paymentId){
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, paymentId));
}
if(fromdate1){
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromdate1)));
}
if(thrudate1){
	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateEnd)));
}
/*if(parameters.partyIdTo){
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, parameters.partyIdTo));
}*/
if(paymentTypeId){
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, paymentTypeId));
}

conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, [ "PMNT_RECEIVED","PMNT_PAID","PMNT_SENT"]));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
Debug.log("condi====="+cond);
paymentList = delegator.findList("Payment", cond, null, null, null, false);
Debug.log("paymentList==="+paymentList.size());
context.paymentList = paymentList;
