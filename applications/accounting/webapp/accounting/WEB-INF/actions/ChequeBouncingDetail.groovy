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
import java.sql.*;
import org.ofbiz.base.util.UtilHttp;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
context.fromDate=fromDate;
context.thruDate=thruDate;
printChequeBouncingDetailList = [];
if(UtilValidate.isNotEmpty(fromDate)&&UtilValidate.isNotEmpty(thruDate)){
	def sdf = new SimpleDateFormat("yyyy, MMM dd");
	Timestamp fromdate1=null;
	Timestamp thrudate1=null;
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
	conditionList = [];
	conditionList1 = [];
	if(fromdate1){
		conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromdate1)));
	}
	if(thrudate1){
		conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thrudate1)));
	}
	conditionList.add(EntityCondition.makeCondition("chequeReturns", EntityOperator.EQUALS, "Y"));
	//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("PMNT_VOID","PMNT_NOT_PAID")));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	//Debug.log("Condition======"+cond);
	chequeBouncingList = delegator.findList("Payment", cond, null, null, null, false);
	//Debug.log("result of chequeBouncingList======"+chequeBouncingList);
	if(UtilValidate.isNotEmpty(chequeBouncingList)){
	for(Object cheuqeentry : chequeBouncingList){
		tempMap = [:];
		tempMap["issuingAuthority"]=cheuqeentry.issuingAuthority;
		tempMap["chequeNo"]=cheuqeentry.paymentRefNum;
		tempMap["chequeDate"]=cheuqeentry.paymentDate;
		tempMap["chequeAmount"]=cheuqeentry.amount;
		tempMap["cancelComments"] = cheuqeentry.comments;	
		tempMap["chrges"]="";
		tempMap["duescleared"]="";
		tempMap["remarks"] = cheuqeentry.cancelComments;
		printChequeBouncingDetailList.add(tempMap);
	}
	}
//	Debug.log("printChequeBouncingDetailList======"+printChequeBouncingDetailList);
}
context.printChequeBouncingDetailList = printChequeBouncingDetailList;
