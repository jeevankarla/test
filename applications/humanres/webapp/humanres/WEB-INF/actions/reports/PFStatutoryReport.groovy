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
	
	EachEmployeeMap=[:];
	partyIdsList=[];
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","APPROVED")));
	if(UtilValidate.isNotEmpty(parameters.periodBillingId)){
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,parameters.periodBillingId));
	}
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
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
		TypeconditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
	}
	if((parameters.EmplType).equals("DeputationStaff")){
		TypeconditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"DEPUT_EMPL"));
	}
	TypeconditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN ,employmentsList));
	emplTypecondition = EntityCondition.makeCondition(TypeconditionList,EntityOperator.AND);
	StaffList = delegator.findList("PartyRelationship", emplTypecondition, null, null, null, false);
	if(UtilValidate.isNotEmpty(StaffList)){
		partyIdsList = EntityUtil.getFieldListFromEntityList(StaffList, "partyIdTo", true);
	}
	
	headerConditionList=[];
	headerConditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN ,periodBillingId));
	headerConditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,partyIdsList));
	headerCondition = EntityCondition.makeCondition(headerConditionList,EntityOperator.AND);
	def orderBy = UtilMisc.toList("partyIdFrom");
	payrollHeader = delegator.findList("PayrollHeader", headerCondition, null, orderBy, null, false);
	payrollHeaderIdsList = EntityUtil.getFieldListFromEntityList(payrollHeader, "payrollHeaderId", true);
	
	if(UtilValidate.isNotEmpty(payrollHeader)){
		payrollHeader.each{ HeaderId ->
			employeesMap=[:];
			wages= 0;
			epf = fpf = 0;
			employerFpf = employerVpf = employerEpf = 0;
			payrollHeaderId = HeaderId.get("payrollHeaderId");
			partyId = HeaderId.get("partyIdFrom");
			if(UtilValidate.isNotEmpty(partyId)){
				pfNumList = delegator.findList("PartyPersonAndEmployeeDetail", EntityCondition.makeCondition(["partyId" : partyId]), null, null, null, false);
				pfNum = pfNumList[0].presentEpf;
				firstName = pfNumList[0].firstName;
				lastName = pfNumList[0].lastName;
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
	context.EachEmployeeMap = EachEmployeeMap;
		
	
