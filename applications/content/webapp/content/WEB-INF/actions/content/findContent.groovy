


parameters.isPublic = 'Y';
if(userLogin){
	if(security.hasEntityPermission("CONTENTMGR", "_ADMIN", session)){
		parameters.isPublic = '';
	}
}
if(UtilValidate.isEmpty(parameters.dataCategoryId)){
	dataCategoryIdList = delegator.findList("DataCategory", EntityCondition.makeCondition("parentCategoryId", EntityOperator.EQUALS , "OMS"), null, null, null, false);
	
	if(!UtilValidate.isEmpty(dataCategoryIdList)){
		parameters.dataCategoryId = EntityUtil.getFieldListFromEntityList(dataCategoryIdList,"dataCategoryId",true);
		parameters.dataCategoryId_op = "in";			
	}
}