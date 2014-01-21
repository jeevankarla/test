import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.text.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
/*import org.ofbiz.network.NetworkServices;*/

indentItemList = [];
custRequestId = context.custRequestId;
validIndents = delegator.findList("CustRequest",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId) , null, null, null, false);
if(UtilValidate.isNotEmpty(validIndents)){
	indent_status = validIndents.getAt(0).getAt("statusId");
}
validIndentItems = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId) , null, null, null, false);
if(UtilValidate.isNotEmpty(validIndentItems)){
	validIndentItems.each{eachItem ->
		itemMap = [:];
		custRequestItemSeqId =  eachItem.getAt("custRequestItemSeqId");
		productId = eachItem.getAt("productId");
		totalATP = BigDecimal.ZERO;
		ATPTotalsMap = dispatcher.runSync("getCustReqATP", [custRequestId: custRequestId, userLogin: userLogin]);
		if (ATPTotalsMap) {
			Map tempMap = ATPTotalsMap["ATPTotalsMap"];
			totalATP = tempMap[productId];
		}
		statusId = eachItem.getAt("statusId");
		quantity = eachItem.getAt("quantity");
		priority = eachItem.getAt("priority");
		itemCreatedTime = eachItem.getAt("createdStamp");
		itemMap.putAt("custRequestItemSeqId",custRequestItemSeqId);
		itemMap.putAt("productId",productId);
		itemMap.putAt("statusId",statusId);
		itemMap.putAt("quantity",quantity);
		itemMap.putAt("ATP",totalATP);
		itemMap.putAt("priorities",priority);
		itemMap.putAt("itemCreatedTime",itemCreatedTime);
		itemMap.putAt("indent_status",indent_status);
		selectField = ["unitPrice","createdStamp"] as Set;
		validRates = delegator.findList("OrderItem",EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId) , selectField, null, null, false);
		validPastRates = validRates.size();
		if(validRates.size() == 0){
				unitPrice = "";
				createdStamp = "";
				/*context.errorMessage = "No Valid Rates for the Product"+productId;
				return;*/
		}
		else{
			validRates.each{itemRates ->
				if(validPastRates == 1){
					unitPrice = itemRates.getAt("unitPrice");
					createdStamp = itemRates.getAt("createdStamp");
					itemMap.putAt("unitPrice",unitPrice);
					itemMap.putAt("createdStamp",createdStamp);
				}
				validPastRates = validPastRates - 1;
			}
		}
		indentItemList.add(itemMap);
	}
}
	
context.put("indentItemList",indentItemList);