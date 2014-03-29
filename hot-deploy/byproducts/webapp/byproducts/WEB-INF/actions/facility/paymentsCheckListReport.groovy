
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

int decimals;
int rounding;
dctx = dispatcher.getDispatchContext();
decimals = 0;//UtilNumber.getBigDecimalScale("order.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
reportTypeFlag=parameters.reportTypeFlag;
allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}
List exprList = [];
List checkListReportList = [];
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);

	exprList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
		EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	   ], EntityOperator.OR));
   if (!allChanges) {
	   exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
   }
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
   
   checkListItemList = delegator.findList("Payment", condition, null, ["lastModifiedDate"], null, false);

   checkListItemList.each { checkListItem -> 
	   lastPaymentMap = [:];
	   lastPaymentMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItem.lastModifiedDate, "HH:mm:ss");
	   lastPaymentMap["boothId"] = checkListItem.facilityId;
	   if (checkListItem.facilityId) {
		   facility = delegator.findOne("Facility",[facilityId : checkListItem.facilityId], false);
		   if (facility) {
			   lastPaymentMap["routeId"] = facility.parentFacilityId;
		   }
	   }
	   lastPaymentMap["lastModifiedBy"] = checkListItem.lastModifiedByUserLogin;
	   lastPaymentMap["amount"] = (new BigDecimal(checkListItem.amount)).setScale(0 ,rounding);
	   checkListReportList.add(lastPaymentMap);
   }

context.checkListReportList = checkListReportList;

//report invoking for Daily checklist report

boothPaymentCheckMap=[:];
bankPaidMap=[:];
boothRouteIdsMap=[:];
if(UtilValidate.isNotEmpty(reportTypeFlag) && "DailyPaymentCheckList".equals(reportTypeFlag)){
paymentDate=parameters.paymentDate;
paymentMethodTypeId = parameters.paymentMethodTypeId;

fromDateTime=UtilDateTime.nowTimestamp();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(paymentDate+" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+paymentDate, "");
}
dayStart = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(fromDateTime);

routeIds=[];
List<GenericValue> paymentsList = FastList.newInstance();
conditionList=[];
facilityIdsList=[];

	if(parameters.routeId !="All"){
		facilityIdsList =ByProductNetworkServices.getRouteBooths(dctx,  [routeId:parameters.routeId]);
	}
	facilityIdsList=ByProductNetworkServices.getAllBooths(delegator,null).get("boothsList");

	boothPaidDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , [fromDate:dayStart ,paymentMethodTypeId:paymentMethodTypeId,thruDate:dayEnd , facilityIdsList:facilityIdsList]);
	boothTempPaymentsList = boothPaidDetail["paymentsList"];
	boothRouteIdsMap= boothPaidDetail["boothRouteIdsMap"];
	boothTempPaymentsList.each{eachBoothPayment->
			if(UtilValidate.isNotEmpty(eachBoothPayment.issuingAuthority)){
				bankName=eachBoothPayment.issuingAuthority;
					if(UtilValidate.isEmpty(bankPaidMap.get(bankName))){
					List<GenericValue> tempboothPaidList=FastList.newInstance();
					tempboothPaidList.add(eachBoothPayment);
					bankPaidMap[bankName]=tempboothPaidList;
					}else{
					List<GenericValue> tempboothPaidList=FastList.newInstance();
					tempboothPaidList=bankPaidMap.get(bankName);
					tempboothPaidList.add(eachBoothPayment);
					bankPaidMap[bankName]=tempboothPaidList;
					}
			}else{
				if(UtilValidate.isEmpty(bankPaidMap.get("noBankName"))){
				List<GenericValue> tempboothPaidListnew=FastList.newInstance();
				tempboothPaidListnew.add(eachBoothPayment);
				bankPaidMap["noBankName"]=tempboothPaidListnew;
				}else{
				List<GenericValue> tempboothPaidList=FastList.newInstance();
				tempboothPaidList=bankPaidMap.get("noBankName");
				tempboothPaidList.add(eachBoothPayment);
				bankPaidMap["noBankName"]=tempboothPaidList;
				}
			}
	}
}
context.bankPaidMap=bankPaidMap;
context.boothRouteIdsMap=boothRouteIdsMap;
