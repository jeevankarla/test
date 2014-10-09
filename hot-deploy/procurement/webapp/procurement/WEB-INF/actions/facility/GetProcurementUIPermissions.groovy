import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

import javolution.util.FastList;

context.enableWeighingAndLabEntry = "N";
enableWeighingAndLabEntryConfig = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"MILK_PROCUREMENT", propertyName:"enableWeighingAndLabEntry"], true);
if (enableWeighingAndLabEntryConfig && enableWeighingAndLabEntryConfig.propertyValue == "Y") {
	context.enableWeighingAndLabEntry = "Y";
}

