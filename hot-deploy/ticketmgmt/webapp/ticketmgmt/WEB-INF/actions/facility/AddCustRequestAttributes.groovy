import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import java.sql.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

dctx = dispatcher.getDispatchContext();
newCustRequest = [:];
if(UtilValidate.isNotEmpty(custRequest)){
	custRequestId = custRequest.custRequestId;
	custRequestAttributes = [];
	custRequestAttributes = delegator.findList("CustRequestAttribute",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId),null,null,null,false);
	custRequestItems = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId),null,null,null,false);
	for(attribute in custRequestAttributes){
		newCustRequest.put(attribute.attrName, attribute.attrValue);	
	}
	newCustRequest.custRequestId = custRequestId;
	newCustRequest.description = custRequest.description;
	newCustRequest.statusId = custRequest.statusId;
	newCustRequest.custRequestTypeId = custRequest.custRequestTypeId;
	newCustRequest.custRequestDate = custRequest.custRequestDate;
	newCustRequest.salesChannelEnumId = custRequest.salesChannelEnumId;
	if(custRequestItems.isNotEmpty){	
		newCustRequest.custRequestItemSeqId=custRequestItems[0].custRequestItemSeqId;
		newCustRequest.productId=custRequestItems[0].productId;
	}
}else{
	newCustRequest = null;
}
context.putAt("newCustRequest", newCustRequest);
