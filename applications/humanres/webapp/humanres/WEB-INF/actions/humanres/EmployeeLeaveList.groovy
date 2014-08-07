import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;


List employeeLeaveList = [];
List conditionList=[];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
if(UtilValidate.isNotEmpty(parameters.leaveTypeId)){
	conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, parameters.leaveTypeId));
}
if(UtilValidate.isNotEmpty(parameters.emplLeaveReasonTypeId)){
	conditionList.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", EntityOperator.EQUALS, parameters.emplLeaveReasonTypeId));
}
if(UtilValidate.isNotEmpty(parameters.approverPartyId)){
	conditionList.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, parameters.approverPartyId));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
LeaveDetails = delegator.findList("EmplLeave", condition , null, null, null, false );
context.put("employeeLeaveList",LeaveDetails);
