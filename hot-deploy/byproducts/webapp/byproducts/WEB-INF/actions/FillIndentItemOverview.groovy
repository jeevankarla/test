import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;

orderStatusList = [];
conditionList=[];
validIndentItemList = [];
validIndentList = [];
indentItemMap = [:];
	custRequestId = context.custRequestId;
	custRequestItemSeqId = context.custRequestItemSeqId;
	selectField = ["custRequestTypeId","custRequestId","custRequestName"] as Set;
	validIndentList = delegator.findList("CustRequest", EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS, custRequestId), selectField , null, null, false);
	
	requestType = validIndentList.getAt("custRequestTypeId").getAt(0);
	custReqId = validIndentList.getAt("custRequestId").getAt(0);
	custReqName = validIndentList.getAt("custRequestName").getAt(0);
	indentItemMap = [:];
	if(requestType == "STOCK_XFER_REQUEST"){
		select = ["custRequestItemSeqId","statusId","priority","requiredByDate","productId","quantity","lastUpdatedStamp","createdStamp"] as Set;
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId));
		conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		validIndentItemList = delegator.findList("CustRequestItem", condition, select , null, null, false);
		priority = validIndentItemList.getAt(0).get("priority");
		priorityInWords = null
		if(priority == 1)
			priorityInWords = "High";
		else if(priority == 2)
			priorityInWords = "Medium";
		else
			priorityInWords = "Low";

		indentItemMap.putAt("custRequestId",custReqId);
		indentItemMap.putAt("custReqName",custReqName);
		indentItemMap.putAt("custRequestItemSeqId",validIndentItemList.getAt(0).get("custRequestItemSeqId"));
		indentItemMap.putAt("statusId",validIndentItemList.getAt(0).getAt("statusId"));
		indentItemMap.putAt("priority",priorityInWords);
		indentItemMap.putAt("productId",validIndentItemList.getAt(0).getAt("productId"));
		indentItemMap.putAt("requiredByDate",validIndentItemList.getAt(0).getAt("requiredByDate"));
		indentItemMap.putAt("quantity",validIndentItemList.getAt(0).getAt("quantity"));
		indentItemMap.putAt("lastUpdatedStamp",validIndentItemList.getAt(0).getAt("lastUpdatedStamp"));
		indentItemMap.putAt("createdStamp",validIndentItemList.getAt(0).getAt("createdStamp"));
	}
	context.put("indentItemMap",indentItemMap);
	