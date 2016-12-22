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

conditionList = [];
conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "MOB_RETAILER"));
UserLoginSecurityGroup = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(conditionList,EntityOperator.AND), UtilMisc.toSet("userLoginId"), null, null, false);
ApiHits = [];
if(UtilValidate.isNotEmpty(UserLoginSecurityGroup)){
	userLoginIdList = EntityUtil.getFieldListFromEntityList(UserLoginSecurityGroup, "userLoginId", true);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.GREATER_THAN_EQUAL_TO , fromDate));
	conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.LESS_THAN_EQUAL_TO , thruDate));
	conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.IN, userLoginIdList));
	EntityFindOptions findOptions = new EntityFindOptions();
	findOptions.setDistinct(true);
	ApiHits = delegator.findList("ApiHit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), UtilMisc.toSet("userLoginId"), null, findOptions, false);
	userLoginIdList = EntityUtil.getFieldListFromEntityList(ApiHits, "userLoginId", true);
	Debug.log("userLoginIdList==============="+userLoginIdList);
}
mobileHitsCSVList = [];
if(ApiHits){
	for (GenericValue hit : ApiHits) {
		String userLoginId= hit.getString("userLoginId");
		String partyId = "";
		if (userLoginId) {
			userLogin = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
			if (userLogin) {
				partyId = userLogin.partyId;
			}
		}
		tempMap = [:];
		tempMap["userLoginId"] = userLoginId;
		tempMap["partyId"] = partyId;
		partyNameView = delegator.findByPrimaryKey("PartyNameView", [partyId : partyId]);
		if(UtilValidate.isNotEmpty(partyNameView.getString("groupName"))){
			tempMap["partyName"] = partyNameView.getString("groupName");
		}
		else{
			String partyName = "";
			if(partyNameView.getString("firstName")){
				partyName = partyNameView.getString("firstName");
			}
			if(partyNameView.getString("middleName")){
				partyName = " "+partyNameView.getString("middleName");
			}
			if(partyNameView.getString("lastName")){
				partyName = " "+partyNameView.getString("lastName");
			}
			tempMap["partyName"] = partyName;
		}
		mobileHitsCSVList.add(tempMap);
	}
}
context.apiHits = mobileHitsCSVList;

