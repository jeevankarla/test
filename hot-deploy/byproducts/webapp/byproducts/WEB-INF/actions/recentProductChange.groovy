import java.util.*;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import java.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import  org.ofbiz.network.NetworkServices;
import java.text.SimpleDateFormat;
uiLabelMap = UtilProperties.getResourceBundleMap("OrderUiLabels", locale);

lastChangeSubProdMap=[:];
facilityList = [];
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
conditionList = [];
conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
conditionList.add(EntityCondition.makeCondition("preRevisedQuantity", EntityOperator.NOT_EQUAL, null));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
subProdList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null,["-lastModifiedDate"], null, false);
if(subProdList){
	latestEntry = EntityUtil.getFirst(subProdList);
	modifiedBy = latestEntry.lastModifiedByUserLogin;
	modifiedDate = latestEntry.lastModifiedDate;
	modificationTime = dateFormat.format(modifiedDate);
	productId = latestEntry.productId;
	latestChangeByDate = EntityUtil.filterByCondition(subProdList, EntityCondition.makeCondition("lastModifiedDate", EntityOperator.EQUALS, modifiedDate));
	latestProductChange = EntityUtil.filterByCondition(latestChangeByDate, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	if(latestProductChange){
		latestProductChange.each{eachItem ->
			facilityMap = [:];
			facilityMap.facilityId = eachItem.facilityId;
			facilityList.add(facilityMap);
			lastChangeSubProdMap[eachItem.facilityId] = eachItem.quantity;
			lastChangeSubProdMap["productId"] = eachItem.productId;
			lastChangeSubProdMap["modifiedBy"] = modifiedBy;
			lastChangeSubProdMap["modificationTime"] = modificationTime;//dateFormat.format(eachItem.changeDatetime);
		}
		context.facilityList = facilityList;
	}
}
context.lastChangeSubProdMap = lastChangeSubProdMap;
	