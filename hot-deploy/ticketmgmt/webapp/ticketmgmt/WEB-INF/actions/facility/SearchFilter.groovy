import org.ofbiz.base.util.*;
import java.sql.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

dctx = dispatcher.getDispatchContext();
parameters.custRequestTypeId = parameters.custRequestTypeId_0;
if(UtilValidate.isEmpty(parameters.custRequestTypeId)&&UtilValidate.isNotEmpty(parameters.noConditionFind)){
	/*custRequestTypeList = delegator.findList("CustRequestType",EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"CUSTOMER_COMP_TYPE"),null,null,null,false);*/
	custRequestTypeList = complaintTypeList;
	parameters.custRequestTypeId_op = "in";
	custRequestTypes =[];
	for(custRequestType in custRequestTypeList){
		custRequestTypes.add(custRequestType.get("custRequestTypeId"));
		}
	parameters.custRequestTypeId =  custRequestTypes;
	/*parameters.custRequestTypeId = EntityUtil.getFieldListFromEntityList(custRequestTypeList, "custRequestTypeId", false);*/
}


