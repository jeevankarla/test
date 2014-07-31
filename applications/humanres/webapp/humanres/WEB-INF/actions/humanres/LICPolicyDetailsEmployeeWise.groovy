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
try {
	if (parameters.LICfromDate) {
		fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.LICfromDate).getTime()));
	}
	if (parameters.LICthruDate) {
		thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.LICthruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);

mdLicFinalMap=[:];
dairyLicFinalMap=[:];
emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyId"));

	employments.each { employment ->
		partyId=employment.get("partyId");
		context.put("partyId",partyId);
		List conditionList=[];
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart));
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateEnd));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		InsuranceDetails = delegator.findList("PartyInsurance", condition , null, null, null, false );
		
		if(UtilValidate.isNotEmpty(InsuranceDetails)){
			InsuranceDetails.each { insurance ->
				if((parameters.InsuranceType).equals("MD LIC")){
					if(insurance.get("insuranceTypeId").equals("LIC_MD_INSR")){
						mdLicDetailsMap=[:];
						insuranceId=insurance.get("insuranceId")
						employeeName=employment.get("firstName");
						referenceNo=insurance.get("insuranceNumber");
						employeeNo=insurance.get("partyId");
						amount=insurance.get("insuredValue");
						mdLicDetailsMap.put("employeeName",employeeName);
						mdLicDetailsMap.put("referenceNo",referenceNo);
						mdLicDetailsMap.put("employeeNo",employeeNo);
						mdLicDetailsMap.put("amount",amount);
						mdLicFinalMap.put(insuranceId,mdLicDetailsMap);
					}
				}
				if((parameters.InsuranceType).equals("Mother Dairy LIC")){
					if(insurance.get("insuranceTypeId").equals("LIC_DAIRY_INSR")){
						Map dairyLicDetailsMap=[:];
						insuranceId=insurance.get("insuranceId")
						employeeName=employment.get("firstName");
						referenceNo=insurance.get("insuranceNumber");
						employeeNo=insurance.get("partyId");
						amount=insurance.get("insuredValue");
						dairyLicDetailsMap.put("employeeName",employeeName);
						dairyLicDetailsMap.put("referenceNo",referenceNo);
						dairyLicDetailsMap.put("employeeNo",employeeNo);
						dairyLicDetailsMap.put("amount",amount);
						dairyLicFinalMap.put(insuranceId,dairyLicDetailsMap);
					}
				}
			}
		}	
	}
	context.put("mdLicFinalMap",mdLicFinalMap);
	context.put("dairyLicFinalMap",dairyLicFinalMap);
	
