import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;


parameters.isPublic = 'Y';
if(userLogin){
	if(security.hasEntityPermission("CONTENTMGR", "_ADMIN", session)){
		parameters.isPublic = '';
	}
}
if(UtilValidate.isEmpty(parameters.dataCategoryFieldId)){
	dataCategoryIdList = delegator.findList("DataCategory", EntityCondition.makeCondition("parentCategoryId", EntityOperator.EQUALS , "OMS"), null, null, null, false);
	
	if(!UtilValidate.isEmpty(dataCategoryIdList)){
		parameters.dataCategoryId = EntityUtil.getFieldListFromEntityList(dataCategoryIdList,"dataCategoryId",true);
		parameters.dataCategoryId_op = "in";			
	}
}
else {
	parameters.dataCategoryId = parameters.dataCategoryFieldId;
}