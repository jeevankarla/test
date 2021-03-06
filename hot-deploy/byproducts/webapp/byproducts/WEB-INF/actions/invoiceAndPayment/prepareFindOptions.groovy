import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;


if(UtilValidate.isEmpty(parameters.noConditionFind)){
return "";
}
condList = [];
//reset purposeType If Empty before search
if(UtilValidate.isEmpty(parameters.purposeTypeIdField)){
	condList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "ORDER_SALES_CHANNEL"));
	condList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_IN, UtilMisc.toList("BYPROD_SALES_CHANNEL","RM_DIRECT_CHANNEL")));
	enumCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	salesChannelList = delegator.findList("Enumeration",enumCond, null, null, null, false);
	if(!UtilValidate.isEmpty(salesChannelList)){
		parameters.paymentPurposeType = EntityUtil.getFieldListFromEntityList(salesChannelList,"enumId",true);
		//parameters.purposeTypeId=UtilMisc.toList("BYPROD_SALES_CHANNEL","RM_DIRECT_CHANNEL")
		parameters.paymentPurposeType_op = "in";
	}
}
else {
	parameters.paymentPurposeType = parameters.purposeTypeIdField;
}
condList.clear();
//reset purposeType If Empty before search
if(UtilValidate.isNotEmpty(parameters.ScreenFlagName)){
if(UtilValidate.isEmpty(parameters.recoveryTypeId)){
	condList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "FAC_DTC_HIKE"));
	//condList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_IN, UtilMisc.toList("BYPROD_SALES_CHANNEL","RM_DIRECT_CHANNEL")));
	enumCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	recoveryTypeList = delegator.findList("Enumeration",enumCond, null, null, null, false);
	Debug.log("====recoveryIdsList===="+EntityUtil.getFieldListFromEntityList(recoveryTypeList,"enumId",true));
	if(!UtilValidate.isEmpty(recoveryTypeList)){
		parameters.recoveryTypeId = EntityUtil.getFieldListFromEntityList(recoveryTypeList,"enumId",true);
		parameters.recoveryTypeId = "in";
	}
}else {
	parameters.recoveryTypeId = parameters.recoveryTypeId;
}
}
