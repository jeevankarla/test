/* groovy for listing Items in Priority wise */
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.util.Map;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
conditionList=[];
itemWiseList=[];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "CRQ_COMPLETED"));
/*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TNDR_ISSUED"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TNDR_BID_RECEIVED"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TNDR_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TNDR_FINANCIAL"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TNDR_PREBID"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "TNDR_APPROVED"));*/
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
itemWiseList = delegator.findList("CustRequestItem", condition, null , null, null, false);
context.put("itemWiseList", UtilMisc.sortMaps(itemWiseList, UtilMisc.toList("custRequestId","priority")));
