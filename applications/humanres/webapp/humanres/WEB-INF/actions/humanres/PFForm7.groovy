import java.util.*;
import java.lang.*;
import java.sql.*;
import java.util.Calendar;
import java.sql.Timestamp;

import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();

Timestamp fromDateStart = null;
Timestamp thruDateEnd = null;
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
    if(UtilValidate.isNotEmpty(customTimePeriod)){
	fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	context.put("fromDate",fromDateStart);
	context.put("thruDate",thruDateEnd);
}



Timestamp yearBegin = UtilDateTime.getMonthStart(fromDateStart);
Timestamp yearEnd = UtilDateTime.getMonthEnd(thruDateEnd, timeZone, locale);

CustomTimePeriodIDList = [];
TimePeriodcondlist = [];
TimePeriodcondlist.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS, "HR_MONTH"));
TimePeriodcondlist.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(yearBegin)));
TimePeriodcondlist.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(yearEnd)));
TimePeriods = EntityCondition.makeCondition(TimePeriodcondlist,EntityOperator.AND);
TimePeriodIDList = delegator.findList("CustomTimePeriod", TimePeriods, null, ["fromDate"], null, false);
	if(UtilValidate.isNotEmpty(TimePeriodIDList)){
		
	CustomTimePeriodIDList = EntityUtil.getFieldListFromEntityList(TimePeriodIDList, "customTimePeriodId", true);
	context.put("CustomTimePeriodIDList", CustomTimePeriodIDList);
	 }
	
employeesList = [];
    if(UtilValidate.isNotEmpty(parameters.employeeId)){
	employeesList.add(parameters.employeeId);
    }else{
	emplMap = [:];
	emplMap.put("userLogin", userLogin);
	emplMap.put("orgPartyId", "Company");
	emplMap.put("fromDate", yearBegin);
	emplMap.put("thruDate", yearEnd);
	Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplMap);
	employments=EmploymentsMap.get("employementList");
	if(UtilValidate.isNotEmpty(employments)){
		employeesList = EntityUtil.getFieldListFromEntityList(employments, "partyIdTo", true);
		
		}
    }
	
	employeeMap = [:];
	
	monthStartList = [];
	if(UtilValidate.isNotEmpty(employeesList)){
		employeesList.each{ employee ->
			employeeDetailsMap = [:];
			tempMap = [:];
			employeeDetails = delegator.findOne("EmployeeDetail", [partyId : employee], false);
			
			employeeDetailsMap = [:];
			employeeDetails = delegator.findOne("EmployeeDetail", [partyId : employee], false);
			if(UtilValidate.isNotEmpty(employeeDetails)){
				pfAccNo = employeeDetails.get("presentEpf");
				employeeDetailsMap.put("pfAccNo",pfAccNo);
			  
				
				}
			personDetails = delegator.findOne("Person", UtilMisc.toMap("partyId",employee), false);
			if(UtilValidate.isNotEmpty(personDetails)){
				firstName = personDetails.get("firstName");
				lastName = personDetails.get("lastName");
			
				
				if(lastName==null){
					lastName="";
				}
				fatherName = personDetails.get("fatherName");
				employeeName = firstName +" "+ lastName;
				employeeDetailsMap.put("employeeName",employeeName);
				employeeDetailsMap.put("fatherName",fatherName);
			}
			
			periodWiseMap = [:];
			if(UtilValidate.isNotEmpty(CustomTimePeriodIDList)){
				CustomTimePeriodIDList.each{ periodId ->
					timePeriodId = "";
					detailsMap = [:];
					GenericValue customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : periodId], false);
					if(UtilValidate.isNotEmpty(customTimePeriodDetails)){
						monthFromDate=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("fromDate"));
						monthThruDate=UtilDateTime.toTimestamp(customTimePeriodDetails.getDate("thruDate"));
						prevMonth = UtilDateTime.addDaysToTimestamp(monthFromDate, -1);
						prevMonthStart=UtilDateTime.getMonthStart(prevMonth);
						prevMonthEnd = UtilDateTime.getMonthEnd(prevMonthStart, timeZone, locale);
						List monthsConList=[];
						monthsConList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_MONTH"));
						monthsConList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(prevMonthStart)));
						monthsConList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(prevMonthEnd)));
						monthCond=EntityCondition.makeCondition(monthsConList,EntityOperator.AND);
						monthsList = delegator.findList("CustomTimePeriod", monthCond , null, null, null, false );
						if(UtilValidate.isNotEmpty(monthsList)){
							monthsList = EntityUtil.getFirst(monthsList);
							timePeriodId = monthsList.get("customTimePeriodId");
						   
							Timestamp monthBegin = UtilDateTime.getMonthStart(monthFromDate);
							Timestamp monthEnd = UtilDateTime.getMonthEnd(monthBegin, timeZone, locale);
							customTimePeriodTotals = PayrollService.getEmployeeSalaryTotalsForPeriod(dctx,UtilMisc.toMap("partyId",employee,"fromDate",monthBegin,"thruDate",monthEnd,"userLogin",userLogin)).get("periodTotalsForParty");
							periodDetailsMap=[:];
							if(UtilValidate.isNotEmpty(customTimePeriodTotals)){
								emplyrCont = 0;
								Iterator customTimePeriodIter = customTimePeriodTotals.entrySet().iterator();
								while(customTimePeriodIter.hasNext()){
									Map.Entry customTimePeriodEntry = customTimePeriodIter.next();
									if(customTimePeriodEntry.getKey() != "customTimePeriodTotals"){
										Map periodTotalsMap = [:];
										periodTotals = customTimePeriodEntry.getValue().get("periodTotals");
										wages = 0;
										wages = periodTotals.get("grossBenefitAmt");
										if(UtilValidate.isEmpty(wages)){
											wages = 0;
										}
										
										tempMap.put(monthFromDate,wages);
										employeeDetailsMap.put("wages",tempMap);
									}   
			                    }
		                    }
							monthStartList.add(monthFromDate);
							
						}
					}
				}
				employeeMap.put(employee,employeeDetailsMap);
			}
		}
	}
	context.put("employeeMap", employeeMap);
	context.put("monthStartList", monthStartList);
