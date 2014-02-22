
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.UtilNumber;

int decimals;
int rounding;
decimals = 0;//UtilNumber.getBigDecimalScale("order.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
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