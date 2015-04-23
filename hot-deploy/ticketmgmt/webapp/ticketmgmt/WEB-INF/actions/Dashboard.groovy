import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		//fromDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		fromDate = null;
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		thruDate = null;
	}
	thruDateEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	context.fromDate = fromDate;
	context.thruDate = thruDate;
	context.thruDateEnd = thruDateEnd;
	
	if (parameters.productId) {
		context.productId = parameters.productId;
	}
	if (parameters.statusId) {
		context.statusId = parameters.statusId;
	}
	if (parameters.custRequestTypeId) {
		context.custRequestTypeId = parameters.custRequestTypeId;
	}
	if (parameters.severity) {
		context.severity = parameters.severity;
	}
	if (parameters.project) {
		context.project = parameters.project;
	}
	
	context.fromDateStr = UtilDateTime.toDateString(fromDate,"MMMM dd, yyyy");
	context.thruDateStr = UtilDateTime.toDateString(thruDate,"MMMM dd, yyyy");
	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

custRequestList=[];
custRequestProdList = [];
custRequestStatusList = [];
custRequestSeverityList = [];

/*typeList = delegator.findList("CustRequestType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "Customer_comp_type") , null, null, null, false);
typeIds = EntityUtil.getFieldListFromEntityList(typeList, "custRequestTypeId", true);*/
typeList = complaintTypeList;
custRequestTypes =[];
for(custRequestType in typeList){
	custRequestTypes.add(custRequestType.get("custRequestTypeId"));
	}
typeIds =custRequestTypes;


project = parameters.project;
custReqIdsList = [];
if(UtilValidate.isNotEmpty(project)){
	 attrList  = [];
	 attrList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "PROJECT"));
	 attrList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, project));
	 attrCond = EntityCondition.makeCondition(attrList, EntityOperator.AND);
	 attrValueList = delegator.findList("CustRequestAttribute",attrCond, null, null, null, false );
	 custReqIdsList = EntityUtil.getFieldListFromEntityList(attrValueList, "custRequestId", false);
}

List exprList = [];
if(fromDate !=null){
	exprList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
if(thruDate !=null){
	exprList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.LESS_THAN, thruDate));
}else{
	exprList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.LESS_THAN, UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())));
}

if(UtilValidate.isNotEmpty(parameters.custRequestTypeId) && (parameters.custRequestTypeId != "All")){
	exprList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, parameters.custRequestTypeId));
}else{
	exprList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, typeIds));
}
if(UtilValidate.isNotEmpty(parameters.productId) && (parameters.productId != "All")){
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
}
if(UtilValidate.isNotEmpty(parameters.statusId) && (parameters.statusId != "All")){
	exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameters.statusId));
}
if(UtilValidate.isNotEmpty(parameters.severity) && (parameters.severity != "All")){
	exprList.add(EntityCondition.makeCondition("severity", EntityOperator.EQUALS, parameters.severity));
}
if(UtilValidate.isNotEmpty(custReqIdsList)){
	exprList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custReqIdsList));
}
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
reqList = delegator.findList("CustRequestAndCustRequestItem", condition,null, null, null, false);
//Debug.log("reqList :"+reqList);


JSONArray labelsJSON = new JSONArray();

typeCountMap = [:];
typeProdCountMap = [:];
typeStatusCountMap = [:];
typeSeverityCountMap = [:];
statusTypeCountMap = [:];
statusTypeInitMap = [:];



i = 1;
typeIds.each { typeId ->
	statusTypeInitMap[typeId] = 0;
	JSONArray labelsList= new JSONArray();
	labelsList.add(i);
	labelsList.add(typeId);
	labelsJSON.add(labelsList);
	++i;
}
Debug.logInfo("typeIds=" + typeIds, "");
totalQuantity = 0;
if (reqList) {
	reqList.each { req ->
		type = (req.custRequestTypeId);
		totalQuantity++;
		if (typeCountMap[type]) {
			typeCountMap[type] = typeCountMap[type] + 1;
		}
		else {
			typeCountMap[type] = 1;
		}
		
		product = req.productId;
		if (typeProdCountMap[product]) {
			typeProdCountMap[product] = typeProdCountMap[product] + 1;
		}
		else {
			typeProdCountMap[product] = 1;
		}
		
		
		status = req.statusId;
		if (typeStatusCountMap[status]) {
			typeStatusCountMap[status] = typeStatusCountMap[status] + 1;
		}
		else {
			typeStatusCountMap[status] = 1;
		}
		
		severity = req.severity;
		if (typeSeverityCountMap[severity]) {
			typeSeverityCountMap[severity] = typeSeverityCountMap[severity] + 1;
		}
		else {
			typeSeverityCountMap[severity] = 1;
		}
		
		
//Debug.logInfo("status=" + status, "");		
		if (statusTypeCountMap[status]) {
			typeMap = statusTypeCountMap[status];
			typeMap[type] = typeMap[type] + 1;
		}
		else {
			statusTypeCountMap[status] = [:];
			statusTypeCountMap[status].putAll(statusTypeInitMap);
			typeMap = statusTypeCountMap[status];
			typeMap[type] = 1;
		}
		
	}
}

Debug.logInfo("typeCountMap=" + typeCountMap, "");
Debug.logInfo("statusTypeCountMap=" + statusTypeCountMap, "");

Iterator mapIter = typeCountMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	name =entry.getKey();
	total = typeCountMap[name];
	temp = [:];
	temp["name"] = name;
	temp["total"] = total;
	custRequestList.add(temp);
}
Iterator mapIter1 = typeProdCountMap.entrySet().iterator();
while (mapIter1.hasNext()) {
	Map.Entry entry = mapIter1.next();
	productId =entry.getKey();
	productDetails = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	name = "";
	if(UtilValidate.isNotEmpty(productDetails)){
		name = productDetails.brandName;
	}
	total = typeProdCountMap[productId];
	temp = [:];
	temp["name"] = name;
	temp["total"] = total;
	custRequestProdList.add(temp);
}
Iterator mapIter2 = typeStatusCountMap.entrySet().iterator();
while (mapIter2.hasNext()) {
	Map.Entry entry = mapIter2.next();
	statusId =entry.getKey();
	statusDetails = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
	name = "";
	if(UtilValidate.isNotEmpty(statusDetails)){
		name = statusDetails.description;
	}
	total = typeStatusCountMap[statusId];
	temp = [:];
	temp["name"] = name;
	temp["total"] = total;
	custRequestStatusList.add(temp);
}
Iterator mapIter3 = typeSeverityCountMap.entrySet().iterator();
while (mapIter3.hasNext()) {
	Map.Entry entry = mapIter3.next();
	severityId =entry.getKey();
	statusDetails = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", severityId), false);
	name = "";
	if(UtilValidate.isNotEmpty(statusDetails)){
		name = statusDetails.description;
	}
	total = typeSeverityCountMap[severityId];
	temp = [:];
	temp["name"] = name;
	temp["total"] = total;
	custRequestSeverityList.add(temp);
}

j = 0.0;
statusTypeDataListJSON = new JSONArray();
Iterator statusIter = statusTypeCountMap.entrySet().iterator();
while (statusIter.hasNext()) {
	Map.Entry entry = statusIter.next();
	statusName =entry.getKey();
	
	JSONObject statusJSON = new JSONObject();
	statusJSON.put("label", statusName);
	JSONArray typesDataList= new JSONArray();
	typesMap = statusTypeCountMap[statusName];
	i = 1;
	typeIds.each { typeId ->
		statusTypeInitMap[typeId] = 0;
		JSONArray typeDataList= new JSONArray();
		typeDataList.add(i+j);
		typeDataList.add(typesMap[typeId]);
		typesDataList.add(typeDataList);
		++i;
	}
	j = j + 0.2;
	statusJSON.put("data", typesDataList);
	statusTypeDataListJSON.add(statusJSON);
}
Debug.logInfo("statusTypeDataListJSON=" + statusTypeDataListJSON, "");
context.put("statusTypeDataListJSON",statusTypeDataListJSON);
context.put("labelsJSON",labelsJSON);
context.put("custRequestList",custRequestList);
context.put("custRequestProdList",custRequestProdList);
context.put("custRequestStatusList",custRequestStatusList);
context.put("custRequestSeverityList",custRequestSeverityList);
context.put("totalQuantity",totalQuantity);