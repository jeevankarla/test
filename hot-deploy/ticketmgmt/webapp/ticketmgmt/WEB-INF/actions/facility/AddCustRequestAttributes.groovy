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
	custRequestItems = delegator.findList("CustRequestAndCustRequestItem",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId),null,null,null,false);
	for(attribute in custRequestAttributes){
		newCustRequest.put(attribute.attrName, attribute.attrValue);	
	}
	newCustRequest.custRequestId = custRequestId;
	newCustRequest.severity = custRequest.severity;
	newCustRequest.description = custRequest.description;
	newCustRequest.statusId = custRequest.statusId;
	newCustRequest.custRequestTypeId = custRequest.custRequestTypeId;
	newCustRequest.custRequestDate = custRequest.custRequestDate;
	newCustRequest.salesChannelEnumId = custRequest.salesChannelEnumId;
	newCustRequest.remarks = newCustRequest.REMARKS;
	newCustRequest.categoryId = newCustRequest.CATEGORY_ID;
	newCustRequest.email = newCustRequest.EMAIL;
	newCustRequest.environment = newCustRequest.ENVIRONMENT;
	newCustRequest.groupClient = newCustRequest.GROUP_CLIENT;
	newCustRequest.assetMapping = newCustRequest.ASSET_MAPPING;
	newCustRequest.project = newCustRequest.PROJECT;
	newCustRequest.productCategoryId = newCustRequest.PRODUCT_CATEGORY_ID;
	newCustRequest.sla = newCustRequest.SLA;
	newCustRequest.subject = newCustRequest.SUBJECT;
	
	
	if(custRequestItems.isNotEmpty){	
		newCustRequest.custRequestItemSeqId=custRequestItems[0].custRequestItemSeqId;
		newCustRequest.productId=custRequestItems[0].productId;
	}
}else{
	newCustRequest = null;
}
context.putAt("newCustRequest", newCustRequest);
