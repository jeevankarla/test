import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import java.text.SimpleDateFormat;

def sdf = new SimpleDateFormat("yyyy-MM-dd");
dctx = dispatcher.getDispatchContext();
List facilityList = EntityUtil.getFieldListFromEntityList(context.get("facilityList"), "facilityId", true);
/*if(UtilValidate.isNotEmpty(facilityList)){
	parameters.facilityId=facilityList;
	parameters.facilityId_op="in";
}else{
	parameters.facilityId=parameters.facilityId;
	parameters.facilityId_op="in";
}*/
workEffortId = parameters.workEffortId;
currentStatusId = parameters.currentStatusId;
productId = parameters.productId;
workEffortName = parameters.workEffortName;
estimatedStartDate = parameters.estimatedStartDate;
facilityId = parameters.facilityId;



List conditionList = FastList.newInstance();

conditionList.add(EntityCondition.makeCondition("workEffortTypeId",EntityOperator.EQUALS,"PROD_ORDER_HEADER"));
if(UtilValidate.isNotEmpty(estimatedStartDate)){
	startDate="";
	endDate = "";
	try {
			startDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(estimatedStartDate).getTime()));
			endDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(estimatedStartDate).getTime()));
	} catch(Exception e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	conditionList.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDate));
	conditionList.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.LESS_THAN_EQUAL_TO,endDate));
}


if(UtilValidate.isNotEmpty(facilityId)){
	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
}else{
	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityList));
}
if(UtilValidate.isNotEmpty(currentStatusId)){
	conditionList.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS,currentStatusId));
}
if(UtilValidate.isNotEmpty(productId)){
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
}
if(UtilValidate.isNotEmpty(workEffortId)){
	conditionList.add(EntityCondition.makeCondition("workEffortId",EntityOperator.EQUALS,workEffortId));
}

EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

List workEffortAndGoods = delegator.findList("WorkEffortAndGoods",condition,null,UtilMisc.toList("-estimatedStartDate"),null,false);

context.productionRunList = workEffortAndGoods;



