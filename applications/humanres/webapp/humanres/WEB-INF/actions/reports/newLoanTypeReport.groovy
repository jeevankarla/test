import org.apache.avalon.framework.parameters.Parameters;
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
import in.vasista.vbiz.humanres.HumanresHelperServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
loanTypeId = parameters.loanTypeId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

partyIdFrom = parameters.partyId;

context.putAt("loanTypeId", loanTypeId);
def sdf = new SimpleDateFormat("MMMM dd,yyyy");
try {
	if (fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	}
	if (thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
employmentsList = [];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
if(UtilValidate.isNotEmpty(partyIdFrom)){
	emplInputMap.put("orgPartyId", partyIdFrom);
}else{
    emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
if(UtilValidate.isNotEmpty(employments)){
	employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
}
List loanTypeList=[];
if(UtilValidate.isNotEmpty(employmentsList)){
	employmentsList.each{ employeeId->
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, loanTypeId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "LOAN_DISBURSED"));
		conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
		conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		//conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		loanAndTypeList = delegator.findList("LoanAndType", condition , null, null, null, false );
		
		if(UtilValidate.isNotEmpty(loanAndTypeList)){
			loanAndTypeList.each { loanAndType ->
				if(UtilValidate.isNotEmpty(loanAndType)){
					loanId = null;
					employeeId = null;
					principalAmount = BigDecimal.ZERO;
					maxAmount = BigDecimal.ZERO;
					disbDate = null;
					employeeName = null;
					
					loanId = loanAndType.loanId;
					employeeId = loanAndType.partyId;
					if(UtilValidate.isNotEmpty(employeeId)){
						employeeName =  PartyHelper.getPartyName(delegator, employeeId, false);
					}
					disbDate = loanAndType.disbDate;
					
					if(UtilValidate.isNotEmpty(loanAndType.principalAmount)){
						principalAmount = loanAndType.principalAmount;
					}
					if(UtilValidate.isNotEmpty(loanAndType.maxAmount)){
						maxAmount = loanAndType.maxAmount;
					}
					loanTypeMap = [:];
					loanTypeMap["loanId"] = loanId;
					loanTypeMap["employeeId"] = employeeId;
					loanTypeMap["employeeName"] = employeeName;
					loanTypeMap["loanTypeId"] = loanTypeId;
					loanTypeMap["disbDate"] = disbDate;
					loanTypeMap["principalAmount"] = principalAmount;
					loanTypeMap["maxAmount"] = maxAmount;
					if(UtilValidate.isNotEmpty(loanTypeMap)){
						tempMap = [:];
						tempMap.putAll(loanTypeMap);
						if(UtilValidate.isNotEmpty(tempMap)){
							loanTypeList.addAll(tempMap);
						}
					}
				}
			}
		}
	}
}
loanTypeList = UtilMisc.sortMaps(loanTypeList, UtilMisc.toList("disbDate"));
context.putAt("loanTypeList", loanTypeList);

