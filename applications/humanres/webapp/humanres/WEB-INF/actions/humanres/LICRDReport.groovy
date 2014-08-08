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
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyId"));

if(UtilValidate.isNotEmpty(employments)){
	employments.each { employment ->
		partyId=employment.get("partyId");
		context.put("partyId",partyId);
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("insuranceTypeId", EntityOperator.EQUALS, (parameters.InsuranceType)));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart));
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		InsuranceDetails = delegator.findList("PartyInsurance", condition , null, null, null, false );
		
		if(UtilValidate.isNotEmpty(InsuranceDetails)){
			InsuranceDetails.each { insurance ->
				LicDetailsMap=[:];
				insuranceId=insurance.get("insuranceId")
				employeeName=employment.get("firstName");
				referenceNo=insurance.get("insuranceNumber");
				employeeNo=insurance.get("partyId");
				amount=insurance.get("insuredValue");
				LicDetailsMap.put("employeeName",employeeName);
				LicDetailsMap.put("referenceNo",referenceNo);
				LicDetailsMap.put("employeeNo",employeeNo);
				LicDetailsMap.put("amount",amount);
				LicFinalMap.put(insuranceId,LicDetailsMap);
			}
		}
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
		conditionList1.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
		condition2=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
		CumulativeDetails = delegator.findList("PartyInsurance", condition2 , null, null, null, false );
		if(UtilValidate.isNotEmpty(CumulativeDetails)){
			CumulativeDetails.each { cumulative ->
				cumulativeAmount = 0;
				employeeId=cumulative.get("partyId");
				amount=cumulative.get("insuredValue");
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




