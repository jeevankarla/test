import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
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
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.humanres.PayrollService;
	if (parameters.customTimePeriodId == null) {
		return;
	}
	dctx = dispatcher.getDispatchContext();
	orgPartyId = null;
	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
	
	timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	customTimePeriodId = customTimePeriod.customTimePeriodId;
	context.timePeriodStart= timePeriodStart;
	context.timePeriodEnd= timePeriodEnd;
	
	
	fromDate=UtilDateTime.getDayStart(timePeriodStart);
	thruDate=UtilDateTime.getDayEnd(timePeriodEnd);
	
	periodTypeId = null;
	
	daPeriodBillingIdList = [];
	periodBillingIdList = [];
	List condList = FastList.newInstance();
	condList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"SP_DA_ARREARS"));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
	condList.add(EntityCondition.makeCondition("basicSalDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	condList.add(EntityCondition.makeCondition("basicSalDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
	List<GenericValue> PeriodBillingAndCustomTimePeriodList = delegator.findList("PeriodBillingAndCustomTimePeriod", cond, null, UtilMisc.toList("fromDate"), null, false);
	if(UtilValidate.isNotEmpty(PeriodBillingAndCustomTimePeriodList)){
		daPeriodBillingIdList = EntityUtil.getFieldListFromEntityList(PeriodBillingAndCustomTimePeriodList, "periodBillingId", true);
	}
	
	EachEmployeeMap=[:];
	partyIdsList=[];
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
	if(UtilValidate.isNotEmpty(parameters.periodBillingId)){
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,parameters.periodBillingId));
	}
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
	periodBillingIdList = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	if(UtilValidate.isNotEmpty(daPeriodBillingIdList)){
		periodBillingIdList.add(daPeriodBillingIdList);
	}
	
	periodBillingId = periodBillingList.periodBillingId;
	
	emplInputMap = [:];
	emplInputMap.put("userLogin", userLogin);
	emplInputMap.put("orgPartyId", "Company");
	emplInputMap.put("fromDate", fromDate);
	emplInputMap.put("thruDate", thruDate);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	employments=EmploymentsMap.get("employementList");
	if(UtilValidate.isNotEmpty(employments)){
		employmentsList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
	}
	TypeconditionList=[];
	if((parameters.EmplType).equals("MDStaff")){
		TypeconditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"EMPLOYEE"));
	}
	if((parameters.EmplType).equals("DeputationStaff")){
		TypeconditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"DEPUTATION_EMPLY"));
	}
	TypeconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN ,employmentsList));
	emplTypecondition = EntityCondition.makeCondition(TypeconditionList,EntityOperator.AND);
	StaffList = delegator.findList("PartyRole", emplTypecondition, null, null, null, false);
	
	if((parameters.EmplType).equals("MDStaff")){
		condList = [];
		condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN ,EntityUtil.getFieldListFromEntityList(StaffList, "partyId", true)));
		condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"DEPUTATION_EMPLY"));
		typecondn = EntityCondition.makeCondition(condList,EntityOperator.AND);
		depStaffList = delegator.findList("PartyRole", typecondn, null, null, null, false);
		StaffList = EntityUtil.filterByCondition(StaffList, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(depStaffList, "partyId", true)));
	}
	if(UtilValidate.isNotEmpty(StaffList)){
		partyIdsList = EntityUtil.getFieldListFromEntityList(StaffList, "partyId", true);
	}
	
	headerConditionList=[];
	headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingIdList));
	headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,partyIdsList));
	headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("partyIdFrom");
	payrollHeader = delegator.findList("PayrollHeader", headerCondition, null, orderBy, null, false);
	payrollHeaderIdsList = EntityUtil.getFieldListFromEntityList(payrollHeader, "payrollHeaderId", true);
	partyIdFromList = EntityUtil.getFieldListFromEntityList(payrollHeader, "partyIdFrom", true);
	
	pfConditionList=[];
	pfConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN ,partyIdFromList));
	pfCondition = EntityCondition.makeCondition(pfConditionList,EntityOperator.AND);
	def pforderBy = UtilMisc.toList("presentEpf");
	pfList = delegator.findList("PartyPersonAndEmployeeDetail", pfCondition, null, pforderBy, null, false);
	
if(UtilValidate.isNotEmpty(pfList)){
	pfList.each{ pf ->
		if(UtilValidate.isNotEmpty(payrollHeader)){
			payrollHeader.each{ HeaderId ->
				employeesMap=[:];
				wages= 0;
				epf = fpf = 0;
				pfNum = pf.presentEpf;
				firstName = pf.get("firstName");
				lastName = pf.get("lastName");
				pfPartyId = pf.get("partyId");
				employerFpf = employerVpf = employerEpf = 0;
				payrollHeaderId = HeaderId.get("payrollHeaderId");
				partyId = HeaderId.get("partyIdFrom");
				if((partyId).equals(pfPartyId)){
					headerItemsList = delegator.findList("PayrollHeaderItem", EntityCondition.makeCondition(["payrollHeaderId" : payrollHeaderId]), null, null, null, false);
					for(j=0; j<headerItemsList.size(); j++){
						eachItem = headerItemsList[j];
						if((eachItem.payrollHeaderItemTypeId=="PAYROL_BEN_SALARY")&&(eachItem.amount==0)){
							break;
						}
						if((eachItem.payrollHeaderItemTypeId=="PAYROL_BEN_SALARY")||(eachItem.payrollHeaderItemTypeId=="PAYROL_BEN_DA")||(eachItem.payrollHeaderItemTypeId=="PAYROL_BEN_SPELPAY")){
							wages = wages+eachItem.amount;
						}
						if((eachItem.payrollHeaderItemTypeId=="PAYROL_DD_EMP_PR")){
							epf = epf-eachItem.amount;
						}else{
							if((eachItem.payrollHeaderItemTypeId=="PAYROL_DD_PF")){
								epf = epf-eachItem.amount;
							}
						}
						if((eachItem.payrollHeaderItemTypeId=="PAYROL_BEN_SALARY")&&(eachItem.amount==0)){
							break;
						}
						if((eachItem.payrollHeaderItemTypeId=="PAYROL_DD_VLNT_PR")){
							employerVpf = employerVpf-eachItem.amount;
						}
					}
				
					employerPFList = delegator.findList("PayrollHeaderItemEc", EntityCondition.makeCondition(["payrollHeaderId" : payrollHeaderId]), null, null, null, false);
					
					if(UtilValidate.isNotEmpty(employerPFList)){
						employerPFList.each{ employer ->
							if(employer.get("payrollHeaderItemTypeId")=="PAYROL_BEN_PFEMPLYR"){
								employerEpf=employer.get("amount");
							}
							if(employer.get("payrollHeaderItemTypeId")=="PAYROL_BEN_PENSION"){
								employerFpf=employer.get("amount");
							}
						}
					}
				
					employeedetailMap = [:];
					employeedetailMap.put("partyId", partyId);
					employeedetailMap.put("wages", wages);
					employeedetailMap.put("pfNum", pfNum);
					employeedetailMap.put("firstName", firstName);
					employeedetailMap.put("lastName", lastName);
					employeedetailMap.put("epf", epf);
					employeedetailMap.put("fpf", fpf);
					employeedetailMap.put("total",epf+fpf);
					
					employerdetailMap = [:];
					employerdetailMap.put("employerFpf", employerFpf);
					employerdetailMap.put("employerVpf", employerVpf);
					employerdetailMap.put("employerEpf", employerEpf);
					employerdetailMap.put("total",employerEpf+employerFpf);
					employeesMap.put("EmployeesMap",employeedetailMap);
					employeesMap.put("EmployersMap",employerdetailMap);
					EachEmployeeMap.put(partyId,employeesMap);
				}
			}
		}
	}
}
	context.EachEmployeeMap = EachEmployeeMap;
		
	
