import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;

dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.CadrefromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.CadrefromDate).getTime()));
	}
	if (parameters.CadrethruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.CadrethruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);
Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
emplInputMap.put("orgPartyId", "Company");
cadreEmployeeList = [];
employementIds = [];
partyIdsList = [];
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

if(UtilValidate.isNotEmpty(employementIds)){
	sNo = 0;
	employementIds.each { partyId ->
		sNo = sNo+1;
		cadreMap = [:];
		
		partyDetails = delegator.findOne("Person",[ partyId : partyId ], false);
		if(UtilValidate.isNotEmpty(partyDetails)){
			String firstName = "";
			String LastName = "";
			if(UtilValidate.isNotEmpty(partyDetails.get("firstName"))){
				firstName = partyDetails.get("firstName");
			}
			if(UtilValidate.isNotEmpty(partyDetails.get("lastName"))){
				LastName = partyDetails.get("lastName");
			}
			partyName = firstName+LastName;
		}else{
			partyName  = "-";
		}
		deptName  = "-";
		employmentConditionList=[];
		employmentConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyId));
		employmentConditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
		employmentConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS ,null));
		employCondition = EntityCondition.makeCondition(employmentConditionList,EntityOperator.AND);
		employeeDepartmentList = delegator.findList("Employment", employCondition, null, null, null, false);
		if(UtilValidate.isNotEmpty(employeeDepartmentList)){
			employeeDepartmentList.each { departmentDet ->
				departmentId = departmentDet.get("partyIdFrom");
				deptName =  PartyHelper.getPartyName(delegator, departmentId, false);
			}
		}else{
			deptName  = "-";
		}
		//Debug.log("deptName====="+partyId+"=========="+deptName);
		designationId = "";
		gradeLevel = 0;
		emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : partyId]));
		emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
		if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
			emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
			gradeLevel = 0;
			if(UtilValidate.isNotEmpty(emplPositionType.get("gradeLevel"))){
				gradeLevel=emplPositionType.get("gradeLevel");
			}
			if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && UtilValidate.isNotEmpty(emplPositionAndFulfillment.getString("name"))){
				designationId = emplPositionAndFulfillment.getString("name");
			}else if (emplPositionType != null) {
				designationId = emplPositionType.getString("description");
			}
			else {
				designationId = emplPositionAndFulfillment.getString("emplPositionId");
			}
		}
		cadreMap.put("partyId",partyId);
		cadreMap.put("Name",partyName);
		cadreMap.put("designation",designationId);
		String gradeSorting = String.format("%15s", gradeLevel).replace(' ', '0');
		cadreMap.put("gradeSorting",gradeSorting);
		cadreMap.put("gradeLevel",gradeLevel);
		cadreMap.put("deptName",deptName);
		cadreEmployeeList.addAll(cadreMap);
	}
}
if(parameters.departmentFlag){
	if(UtilValidate.isNotEmpty(cadreEmployeeList)){
		cadreEmployeeList =UtilMisc.sortMaps(cadreEmployeeList, UtilMisc.toList("deptName","gradeSorting"));
	}
	context.put("cadreEmployeeList",cadreEmployeeList);
}else{
	if(UtilValidate.isNotEmpty(cadreEmployeeList)){
		cadreEmployeeList =UtilMisc.sortMaps(cadreEmployeeList, UtilMisc.toList("gradeSorting"));
	}
	context.put("cadreEmployeeList",cadreEmployeeList);
}
