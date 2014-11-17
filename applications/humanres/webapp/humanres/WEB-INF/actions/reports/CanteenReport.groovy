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
fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);

resultMap = [:];
resultMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:fromDateStart,timePeriodEnd:thruDateEnd,timePeriodId:parameters.customTimePeriodId,locale:locale]);
lastClosePeriod=resultMap.get("lastCloseAttedancePeriod");
if(UtilValidate.isNotEmpty(lastClosePeriod)){
	customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
	shiftFinalMap=[:];
	shift1finalMap=[:];
	shift2finalMap=[:];
	shift3finalMap=[:];
	genshiftfinalMap=[:];
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
			if(UtilValidate.isNotEmpty(partyId)){
				List typeConditionList=[];
				typeConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				typeConditionList.add(EntityCondition.makeCondition("deductionTypeId", EntityOperator.EQUALS,"PAYROL_DD_SAL_CANT"));
				typeCondition=EntityCondition.makeCondition(typeConditionList,EntityOperator.AND);
				partyDeductionDetails = delegator.findList("PartyDeduction", typeCondition , null, null, null, false);
				if(UtilValidate.isNotEmpty(partyDeductionDetails)){
					List conditionList=[];
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
					condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					attendanceDetails = delegator.findList("PayrollAttendanceShiftWise", condition , null, null, null, false);
					if(UtilValidate.isNotEmpty(attendanceDetails)){
						attendanceDetails.each { employee ->
							if((employee.get("shiftTypeId")).equals("SHIFT_01")){
								shift1Map=[:];
								sh1noOfDays =0;
								shiftTypeId=employee.get("shiftTypeId");
								sh1noOfDays=employee.get("availedCanteenDays");
								employeeName=employment.get("firstName");
								shift1Map.put("employeeName",employeeName);
								shift1Map.put("sh1noOfDays",sh1noOfDays);
								shift1Map.put("shiftTypeId",shiftTypeId);
								shift1Map.put("partyId",partyId);
								if(UtilValidate.isNotEmpty(shift1Map)){
									shift1finalMap.put(partyId,shift1Map);
								}
								if(UtilValidate.isNotEmpty(shift1finalMap)){
									shiftFinalMap.put(partyId,shift1finalMap);
								}
							}
							if((employee.get("shiftTypeId")).equals("SHIFT_02")){
								shift2Map=[:];
								shiftTypeId=employee.get("shiftTypeId");
								sh2noOfDays=employee.get("availedCanteenDays");
								employeeName=employment.get("firstName");
								shift2Map.put("employeeName",employeeName);
								shift2Map.put("sh2noOfDays",sh2noOfDays);
								shift2Map.put("shiftTypeId",shiftTypeId);
								shift2Map.put("partyId",partyId);
								if(UtilValidate.isNotEmpty(shift2Map)){
									shift2finalMap.put(partyId,shift2Map);
								}
								if(UtilValidate.isNotEmpty(shift2finalMap)){
									shiftFinalMap.put(partyId,shift2finalMap);
								}
							}
							if((employee.get("shiftTypeId")).equals("SHIFT_NIGHT")){
								shiftMap=[:];
								shiftTypeId=employee.get("shiftTypeId");
								sh3noOfDays=employee.get("availedCanteenDays");
								employeeName=employment.get("firstName");
								shiftMap.put("employeeName",employeeName);
								shiftMap.put("sh3noOfDays",sh3noOfDays);
								shiftMap.put("shiftTypeId",shiftTypeId);
								shiftMap.put("partyId",partyId);
								if(UtilValidate.isNotEmpty(shiftMap)){
									shift3finalMap.put(partyId,shiftMap);
								}
								if(UtilValidate.isNotEmpty(shift3finalMap)){
									shiftFinalMap.put(partyId,shift3finalMap);
								}
							}
							if((employee.get("shiftTypeId")).equals("SHIFT_GEN")){
								genshiftMap=[:];
								shiftTypeId=employee.get("shiftTypeId");
								shgennoOfDays=employee.get("availedCanteenDays");
								employeeName=employment.get("firstName");
								genshiftMap.put("employeeName",employeeName);
								genshiftMap.put("shgennoOfDays",shgennoOfDays);
								genshiftMap.put("shiftTypeId",shiftTypeId);
								genshiftMap.put("partyId",partyId);
								if(UtilValidate.isNotEmpty(genshiftMap)){
									genshiftfinalMap.put(partyId,genshiftMap);
								}
								if(UtilValidate.isNotEmpty(genshiftfinalMap)){
									shiftFinalMap.put(partyId,genshiftfinalMap);
								}
							}
						}
					}
				}
			}
		}
	}
}


context.put("shift1finalMap",shift1finalMap);
context.put("shift2finalMap",shift2finalMap);
context.put("shift3finalMap",shift3finalMap);
context.put("genshiftfinalMap",genshiftfinalMap);
context.put("shiftFinalMap",shiftFinalMap);

