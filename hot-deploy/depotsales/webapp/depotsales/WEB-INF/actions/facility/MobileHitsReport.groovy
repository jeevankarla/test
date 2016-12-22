import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import java.math.RoundingMode;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.inventory.InventoryServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

fromDate = parameters.mobileHitsFromDate;
thruDate = parameters.mobileHitsThruDate;
context.fromDate=fromDate;
context.thruDate=thruDate;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(fromDate)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
context.errorMessage = "Cannot parse date string: " + e;
	return;
}

Debug.log("aaaaaaaaaaaaa");
conditionList = [];
conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "MOB_RETAILER"));
UserLoginSecurityGroup = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, ["userLoginId"], null, false);
Debug.log("bbbbbbbbbbbbbbbbbb");
ApiHits = [];
if(UtilValidate.isNotEmpty(UserLoginSecurityGroup)){
	userLoginIdList = EntityUtil.getFieldListFromEntityList(UserLoginSecurityGroup, "userLoginId", true);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.GREATER_THAN_EQUAL_TO , fromDate));
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.LESS_THAN_EQUAL_TO , thruDate));
	conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, userLoginIdList));
	ApiHits = delegator.findList("ApiHit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
}
context.apiHits = ApiHits;
Debug.log("ccccccccccccccccccc");
Debug.log("ApiHits============"+ApiHits);
