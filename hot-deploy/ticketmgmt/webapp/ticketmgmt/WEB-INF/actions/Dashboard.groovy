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
		/*fromDate = UtilDateTime.getYearStart(UtilDateTime.nowTimestamp());*/
		fromDate = null;
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
	parameters.fromDisplayDate = fromDate;
	parameters.thruDisplayDate = thruDate;
	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

custRequestList=[];

/*typeList = delegator.findList("CustRequestType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "Customer_comp_type") , null, null, null, false);
typeIds = EntityUtil.getFieldListFromEntityList(typeList, "custRequestTypeId", true);*/
typeList = complaintTypeList;
custRequestTypes =[];
for(custRequestType in typeList){
	custRequestTypes.add(custRequestType.get("custRequestTypeId"));
	}
typeIds =custRequestTypes;

List exprList = [];
if(fromDate !=null){
	exprList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
exprList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.LESS_THAN, thruDate));
exprList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, typeIds));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);

reqList = delegator.findList("CustRequest", condition,null, null, null, false);
//Debug.log("reqList :"+reqList);
JSONArray labelsJSON = new JSONArray();

typeCountMap = [:];
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
		
		status = req.statusId;
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
context.put("totalQuantity",totalQuantity);