import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.Debug;

dctx = dispatcher.getDispatchContext();
custRequestId = context.custRequestId;
selectFields = ["statusId","custRequestId"] as Set;
List conditionList=FastList.newInstance();

conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
cancelFlag = "N";
quoteAndItemAndCustRequest = delegator.findList("QuoteAndItemAndCustRequest",condition, selectFields , null, null, false);
if (UtilValidate.isNotEmpty(quoteAndItemAndCustRequest)){
	 quoteAndItemAndCustRequest.each{ quoteAndItem ->
		statusId = quoteAndItem.statusId;
		if (!("QUO_REJECTED".equals(statusId))&&"N".equals(cancelFlag)) {
		     cancelFlag = "Y";
	    }
	}
}
if(UtilValidate.isEmpty(quoteAndItemAndCustRequest)){
	cancelFlag = "N";
}
context.cancelFlag = cancelFlag;