import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);

LicFinalMap=[:];
CumulativeFinalList=[:];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");

employeeIdsList=[];
employeeNameMap=[:];

if(UtilValidate.isNotEmpty(employments)){
	employments.each { employment ->
		employeeIdsList.add(employment.get("partyId"));
		employeeNameMap.putAt(employment.get("partyId"),employment.get("firstName"));
	}
}

List LICconditionList=[];
LICconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employeeIdsList));
LICconditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, (parameters.insuranceTypeId)));
LICconditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
LICconditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart)));
LICcondition=EntityCondition.makeCondition(LICconditionList,EntityOperator.AND);
orderInsuranceDetails = delegator.findList("PartyInsurance", LICcondition , null, null, null, false);
if(UtilValidate.isNotEmpty(orderInsuranceDetails)){
	List tempLicDetailsList = FastList.newInstance();
	orderInsuranceDetails.each { insurance ->
		LicDetailsMap=[:];
		insuranceId=insurance.get("insuranceId");
		employeeName=employeeNameMap.get(insurance.get("partyId"));
		if(UtilValidate.isNotEmpty(insurance.get("insuranceNumber"))){
			 referenceNo=new BigDecimal(insurance.get("insuranceNumber"));
		}
		employeeNo=insurance.get("partyId");
		amount=insurance.get("premiumAmount");
		LicDetailsMap.put("employeeName",employeeName);
		LicDetailsMap.put("referenceNo",referenceNo);
		LicDetailsMap.put("employeeNo",employeeNo);
		LicDetailsMap.put("amount",amount);
		LicDetailsMap.put("insuranceId",insuranceId);
		tempLicDetailsList.add(LicDetailsMap);
	}
	tempLICList = UtilMisc.sortMaps(tempLicDetailsList, UtilMisc.toList("referenceNo"));
	tempLICList.each{ policy ->
		LicInsuranceMap=[:];
		LicInsuranceMap.put("employeeName",policy.employeeName);
		LicInsuranceMap.put("referenceNo",policy.referenceNo);
		LicInsuranceMap.put("employeeNo",policy.employeeNo);
		LicInsuranceMap.put("amount",policy.amount);
		LicFinalMap.put(policy.insuranceId,LicInsuranceMap);
	}
}

finalcumulativeMap=[:];
if(UtilValidate.isNotEmpty(LicFinalMap)){
	ReccuringValues=LicFinalMap.keySet();
	Iterator rdIter = LicFinalMap.entrySet().iterator();
	while(rdIter.hasNext()){
		Map.Entry rdEntry = rdIter.next();
		rdpartyId=rdEntry.getValue().get("employeeNo");
		cummulativeMap=[:];
		List conditionList1=[];
		conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, rdpartyId));
		conditionList1.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, "RECCR_DEPOSIT"));
		conditionList1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd)));
		condition2=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
		CumulativeDetails = delegator.findList("PartyInsurance", condition2 , null, null, null, false );
		if(UtilValidate.isNotEmpty(CumulativeDetails)){
			CumulativeDetails.each { cumulative ->
				cumulativeAmount = 0;
				employeeId=cumulative.get("partyId");
				amount=cumulative.get("premiumAmount");
				if(UtilValidate.isEmpty(cummulativeMap.get(employeeId))){
					cumulativeAmount=amount;
				}else{
					cumulativeAmount=cummulativeMap.get(employeeId);
					cumulativeAmount = cumulativeAmount+amount;
				}
				cummulativeMap.put(employeeId,cumulativeAmount);
				
			}
		}
		finalcumulativeMap.put(rdpartyId,cummulativeMap);
	}
}

context.put("LicFinalMap",LicFinalMap);
context.put("finalcumulativeMap",finalcumulativeMap);

