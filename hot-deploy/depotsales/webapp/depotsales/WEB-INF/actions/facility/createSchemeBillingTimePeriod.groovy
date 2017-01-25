
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;


periodName = parameters.periodName;
frmDateStr = parameters.fromDate;
toDateStr = parameters.toDate;
dctx = dispatcher.getDispatchContext();
subscriptionProdList = [];
displayGrid = true;
fromDate=null;
thruDate=null;

SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
if(UtilValidate.isNotEmpty(frmDateStr)){
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(frmDateStr).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + frmDateStr, "");
		request.setAttribute("_ERROR_MESSAGE_","Cannot parse date string: "+ frmDateStr);
	}
	fromDate = UtilDateTime.getDayStart(fromDate);
}
if(UtilValidate.isNotEmpty(toDateStr)){
	try {
		thruDate = new java.sql.Timestamp(sdf.parse(toDateStr).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + toDateStr, "");
		request.setAttribute("_ERROR_MESSAGE_","Cannot parse date string: "+ toDateStr);
	}
	thruDate = UtilDateTime.getDayEnd(thruDate);
}

Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "fromDate", fromDate, "thruDate",thruDate, "periodName", periodName ,"periodTypeId","TEN_SUB_REIMB_PERIOD");
Map<String, Object> result = dispatcher.runSync("createSchemeTimePeriod", input);

JSONArray billingPeriodsList = new JSONArray();

conditionList=[];
conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "TEN_SUB_REIMB_PERIOD"));
conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
schemeTimePeriod = delegator.findList("SchemeTimePeriod",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("periodName","fromDate","thruDate","schemeTimePeriodId"), null, null, false);

for(eachId in schemeTimePeriod)
{
	JSONObject newObj = new JSONObject();
	fromDateStr=eachId.getString("fromDate");
	thruDateStr=eachId.getString("thruDate");
	newObj.put("label", fromDateStr + "-" + thruDateStr + "[" + eachId.periodName +"]")
	newObj.put("value", eachId.schemeTimePeriodId)
	billingPeriodsList.add(newObj);
}
context.billingPeriodsList=billingPeriodsList;

request.setAttribute("billingPeriodsList",billingPeriodsList);
return "success";













