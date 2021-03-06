package in.vasista.vbiz.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.nio.ByteBuffer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.fop.fo.properties.CondLengthProperty;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.inout.PunchService;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.base.util.StringUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.util.formula.Evaluator;



public class HumanresService {

    public static final String module = HumanresService.class.getName();
    /*
     * Helper that returns full employee profile.  This method expects the employee's EmploymentAndPerson
     * record as an input.
     */
   
    
	static void populateOrgEmployements(DispatchContext dctx, Map<String, ? extends Object> context, List employementList) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");		
        GenericValue org = (GenericValue) context.get("org");
        Timestamp fromDate =  (Timestamp)context.get("fromDate");
        Timestamp thruDate =  (Timestamp)context.get("thruDate");
        Boolean isGroup = (Boolean)context.get("isGroup");
        if (org == null) {
        	return;
        }
        if(UtilValidate.isEmpty(fromDate)){
        	fromDate = UtilDateTime.nowTimestamp();
        }
        if(UtilValidate.isEmpty(thruDate)){
        	thruDate = UtilDateTime.getDayEnd(fromDate);
        }
        fromDate = UtilDateTime.getDayStart(fromDate);
        thruDate = UtilDateTime.getDayEnd(thruDate);
		List<GenericValue> internalOrgs = FastList.newInstance();
  		try{
  			List conditionList = FastList.newInstance();
  			if(UtilValidate.isNotEmpty(isGroup) && !isGroup){
  				// for single employee
  				conditionList.clear();
  	  			conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, org.getString("partyId")));
  	  			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS,  "EMPLOYEE"));
  	  			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
  				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
  						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
  				
  				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
  				List<GenericValue> employments = delegator.findList("EmploymentAndPerson", condition, null, UtilMisc.toList("firstName","-thruDate"), null, false);
  				employementList.addAll(employments);
  				return;
  				
  			}
  			conditionList.clear();
  			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, org.getString("partyId")));
  			conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
  			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
			
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
			internalOrgs = delegator.findList("PartyRelationshipAndDetail", condition, null, UtilMisc.toList("groupName"), null, false);
			
  			/*internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", UtilMisc.toMap("partyIdFrom", org.getString("partyId"),
				"partyRelationshipTypeId", "GROUP_ROLLUP"),UtilMisc.toList("groupName")),fromDate);*/
  			for(GenericValue internalOrg : internalOrgs){
  				Map<String, Object> inputParamMap = FastMap.newInstance();
  				inputParamMap.put("userLogin", userLogin);			  				
  				inputParamMap.put("org", internalOrg);
  				inputParamMap.put("fromDate", fromDate);
  				inputParamMap.put("thruDate", thruDate);
  				populateOrgEmployements(dctx, inputParamMap, employementList);
  			}
  			conditionList.clear();
  			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, org.getString("partyId")));
  			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS,  "EMPLOYEE"));
  			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
			
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
			List<GenericValue> employments = delegator.findList("EmploymentAndPerson", condition, null, UtilMisc.toList("firstName","-thruDate"), null, false);
			/*List<GenericValue> employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", UtilMisc.toMap("partyIdFrom", org.getString("partyId"), 
					"roleTypeIdTo", "EMPLOYEE"), UtilMisc.toList("firstName")),fromDate);*/
			
			employementList.addAll(employments);
			
  		}catch(GenericEntityException e){
  			Debug.logError("Error fetching employments " + e.getMessage(), module);
  		}
		catch (Exception e) {
  			Debug.logError("Error fetching employments " + e.getMessage(), module);
		}  		
	}
	
	 public static Map<String, Object> getActiveEmployements(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();    	
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String orgPartyId =  (String)context.get("orgPartyId");
	        Timestamp fromDate =  (Timestamp)context.get("fromDate");
	        Timestamp thruDate = (Timestamp)context.get("thruDate");
	        
	        Security security = dctx.getSecurity();
	            	
			List employementList = FastList.newInstance();        
			try {
				Map<String, Object> inputParamMap = FastMap.newInstance();
				inputParamMap.put("userLogin", userLogin);	
				GenericValue org = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", orgPartyId));
				if(UtilValidate.isEmpty(org)){
					org = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", orgPartyId));
					inputParamMap.put("isGroup", false);
				}
						
				inputParamMap.put("org", org);
				inputParamMap.put("fromDate", fromDate);
				inputParamMap.put("thruDate", thruDate);
				populateOrgEmployements(dctx, inputParamMap, employementList);	
			}catch(GenericEntityException e){
	  			Debug.logError("Error fetching employments " + e.getMessage(), module);
	  		}
			List employeeIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
			List tempEmployementList = FastList.newInstance();
			for(int i=0;i<employeeIds.size();i++){
				String employeeId = (String)employeeIds.get(i);
				GenericValue tempEmployement = EntityUtil.getFirst(EntityUtil.orderBy(EntityUtil.filterByAnd(employementList,UtilMisc.toMap("partyIdTo",employeeId)),UtilMisc.toList("thruDate","-fromDate")));
				tempEmployementList.add(tempEmployement);
			}
	    	Map result = FastMap.newInstance();  
	    	result.put("employementList", tempEmployementList);
	    //Debug.logInfo("result:" + result, module);		 
	    	return result;
	    }    
	
	 public static Map<String, Object> getGeneralHoliDays(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();    	
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String orgPartyId =  (String)context.get("orgPartyId");
	        Timestamp fromDate =  (Timestamp)context.get("fromDate");
	        Timestamp thruDate =  (Timestamp)context.get("thruDate");
	        if(UtilValidate.isEmpty(orgPartyId)){
	        	orgPartyId = "Company";
	        }
			List<GenericValue> holiDayList = FastList.newInstance();        
			try {
				List conditionList = UtilMisc.toList(
			            EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, orgPartyId));
					conditionList.add(EntityCondition.makeCondition("holiDayDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
					conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("holiDayDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
					holiDayList = delegator.findList("HolidayCalendar", condition, null, UtilMisc.toList("holiDayDate" ,"description"), null, false);
			}catch(GenericEntityException e){
	  			Debug.logError("Error fetching holiday calendar " + e.getMessage(), module);
	  		}
	    	Map result = FastMap.newInstance();  
	    	result.put("holiDayList", holiDayList);
	Debug.logInfo("result:" + result, module);		 
	    	return result;
	    }
	 public static Map<String, Object> getGeneralHoliDayOrSSWorkedDays(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();    	
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String partyId =  (String)context.get("partyId");
	        Timestamp fromDate =  (Timestamp)context.get("fromDate");
	        Timestamp thruDate =  (Timestamp)context.get("thruDate");
	        String isSS =  (String)context.get("isSS");
	        String isGH =  (String)context.get("isGH");
	        String isWeeklyOff = (String)context.get("isWeeklyOff");
	        Locale locale = new Locale("en","IN");
			TimeZone timeZone = TimeZone.getDefault();
	        String orgPartyId = "Company";
			List<GenericValue> holiDayList = FastList.newInstance();
			Map result = FastMap.newInstance();
			if(UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)){
				thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			}
			thruDate = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
			try {
				
					if(UtilValidate.isNotEmpty(isGH) && isGH.equals("Y")){
						if(UtilValidate.isEmpty(fromDate)){
							fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -60));
						}
						List conditionList = UtilMisc.toList(
					            EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, orgPartyId));
							conditionList.add(EntityCondition.makeCondition("holiDayDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
							conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("holiDayDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
							EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
							holiDayList = delegator.findList("HolidayCalendar", condition, null, UtilMisc.toList("holiDayDate" ,"description"), null, false);
					}
					List<Date> holidays = FastList.newInstance();
					if(UtilValidate.isNotEmpty(isSS) && isSS.equals("Y")){
						if(UtilValidate.isEmpty(fromDate)){
							fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -30));
						}
						Timestamp secondSaturDay = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(thruDate),0,2,timeZone,locale), -1);
						holidays.add(UtilDateTime.toSqlDate(secondSaturDay));
					}
					if(UtilValidate.isNotEmpty(isWeeklyOff) && isWeeklyOff.equals("Y")){
						if(UtilValidate.isEmpty(fromDate)){
							fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -60));
						}
						GenericValue employeeDetail = delegator.findOne("EmployeeDetail",UtilMisc.toMap("partyId",partyId),false);
						List conditionList = FastList.newInstance();
			    		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
					    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(fromDate)));
					    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(thruDate)));
					    conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("encashmentStatus",EntityOperator.EQUALS,null),
								EntityOperator.OR,EntityCondition.makeCondition("encashmentStatus",EntityOperator.NOT_IN,UtilMisc.toList("CASH_ENCASHMENT","LEAVE_ENCASHMENT"))));
					    EntityCondition condition= EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					    List<GenericValue> emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null,null, null, false);
					    String emplWeeklyOffDay = "SUNDAY";
					    
				        if(UtilValidate.isNotEmpty(employeeDetail) && UtilValidate.isNotEmpty(employeeDetail.getString("weeklyOff"))){
				        	emplWeeklyOffDay = employeeDetail.getString("weeklyOff");
				        }
						Calendar c1=Calendar.getInstance();
			    		c1.setTime(UtilDateTime.toSqlDate(fromDate));
			    		Calendar c2=Calendar.getInstance();
			    		c2.setTime(UtilDateTime.toSqlDate(thruDate));
						while(c2.after(c1)){
							Timestamp cTime = new Timestamp(c1.getTimeInMillis());
							Timestamp cTimeStart = UtilDateTime.getDayStart(cTime);
			    			Timestamp cTimeEnd = UtilDateTime.getDayEnd(cTime);
			    			String dayName = (c1.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
			    			List<GenericValue> dayShiftList = EntityUtil.filterByCondition(emplDailyAttendanceDetailList, EntityCondition.makeCondition("date",EntityOperator.EQUALS,UtilDateTime.toSqlDate(cTime)));
			    			List weeklyCondList = FastList.newInstance();
							weeklyCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
							weeklyCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,cTimeEnd));
							weeklyCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, cTimeStart)));
							EntityCondition weekCond = EntityCondition.makeCondition(weeklyCondList,EntityOperator.AND);   
							List<GenericValue> activeEmpWeeklyOffCalList = delegator.findList("EmployeeWeeklyOffCalendar", weekCond, null, UtilMisc.toList("-fromDate"), null, false);
							if(UtilValidate.isNotEmpty(activeEmpWeeklyOffCalList)){
					            GenericValue activeEmpWeeklyOffCalendar = EntityUtil.getFirst(activeEmpWeeklyOffCalList);
					            emplWeeklyOffDay = (String) activeEmpWeeklyOffCalendar.get("weeklyOffDay");
							}
							if((dayName.equalsIgnoreCase(emplWeeklyOffDay) && UtilValidate.isNotEmpty(dayShiftList)) || (dayShiftList.size() >= 2)){
								for(int i=0 ;i<dayShiftList.size() ;i++){
			    					GenericValue dayShift = dayShiftList.get(i);
			    					List<GenericValue> emplPunchList = delegator.findByAnd("EmplPunch", UtilMisc.toMap("shiftType",dayShift.get("shiftType"),"punchdate", dayShift.get("date")));
			    					emplPunchList = EntityUtil.orderBy(emplPunchList,UtilMisc.toList("-punchtime"));
			    					GenericValue firstPunch = EntityUtil.getFirst(emplPunchList);
			    					emplPunchList = EntityUtil.orderBy(emplPunchList,UtilMisc.toList("punchtime"));
			    					GenericValue lastPunch = EntityUtil.getFirst(emplPunchList);
			    				}
				    			holidays.add(UtilDateTime.toSqlDate(cTime));
							}
			    			c1.add(Calendar.DATE,1);
						}	
					}
					
				  if(UtilValidate.isNotEmpty(holiDayList)){
						for(GenericValue holiDay : holiDayList){
							holidays.add(UtilDateTime.toSqlDate(holiDay.getTimestamp("holiDayDate")));
						}
					}
					List workedHolidaysList =FastList.newInstance();
					result.put("workedHolidaysList", workedHolidaysList);
					if(UtilValidate.isEmpty(holidays)){
						return result;
					}
					List conList=UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
					conList.add(EntityCondition.makeCondition("date",EntityOperator.IN,holidays));
					conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("encashmentStatus",EntityOperator.EQUALS,null),
							EntityOperator.OR,EntityCondition.makeCondition("encashmentStatus",EntityOperator.NOT_IN,UtilMisc.toList("CASH_ENCASHMENT","LEAVE_ENCASHMENT"))));
					
					EntityCondition con= EntityCondition.makeCondition(conList,EntityOperator.AND);
					List<GenericValue> tempWorkedHolidaysList = delegator.findList("EmplDailyAttendanceDetail", con ,null,UtilMisc.toList("date" ,"partyId"), null, false );
					if(UtilValidate.isNotEmpty(tempWorkedHolidaysList)){
						for(int i=0;i<tempWorkedHolidaysList.size();i++){
							GenericValue workedHoliday = tempWorkedHolidaysList.get(i);
							Map tempDayMap = FastMap.newInstance();
							if(UtilValidate.isEmpty(workedHoliday)){
								continue;
							}
							Date tempDate = workedHoliday.getDate("date");
							/*if(!holidays.contains(tempDate)){
								continue;
							}*/
							
							if((UtilValidate.isNotEmpty(isGH) && (isGH.equals("Y"))) || (UtilValidate.isNotEmpty(isSS) && (isSS.equals("Y")))){
								
							}else{
								Timestamp tempDateTime = UtilDateTime.toTimestamp(tempDate);
								Timestamp nextDayStart = UtilDateTime.getNextDayStart(tempDateTime);
								Timestamp nextDayEnd = UtilDateTime.getDayEnd(nextDayStart);
								
								List emplLeaveCondList = FastList.newInstance();
								emplLeaveCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
								emplLeaveCondList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS ,"RL"));
								emplLeaveCondList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS ,"LEAVE_APPROVED"));
								emplLeaveCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO ,nextDayStart));
								emplLeaveCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,nextDayEnd));
								EntityCondition leaveCond = EntityCondition.makeCondition(emplLeaveCondList,EntityOperator.AND);   
								List<GenericValue> emplLeaveList = delegator.findList("EmplLeave", leaveCond, null, UtilMisc.toList("-fromDate"), null, false);
								if(UtilValidate.isNotEmpty(emplLeaveList)){
									tempDate = null;
								}
							}
							if(UtilValidate.isNotEmpty(tempDate) && (!tempDate.equals(null))){
								Map punMap = PunchService.emplDailyPunchReport(dctx, UtilMisc.toMap("partyId", partyId ,"punchDate",tempDate));
								int minimumTime = 0;
								int totalMinutes = 0;
								int index = 0;
								if(UtilValidate.isNotEmpty(punMap.get("punchDataList"))){
									List punchDetailsList = (List)punMap.get("punchDataList");
									if(UtilValidate.isNotEmpty(punchDetailsList)){
										/*Map firstPunchDetails = (Map) punchDetailsList.get(0);
						        		String totalPunchTime = (String)firstPunchDetails.get("totalTime");
						        		if(UtilValidate.isNotEmpty(totalPunchTime)){
						        			totalPunchTime = totalPunchTime.replace(" Hrs", "");
											List<String> punchTimeSplit = StringUtil.split(totalPunchTime, ":");
											if(UtilValidate.isNotEmpty(punchTimeSplit)){
												hours = Integer.parseInt(punchTimeSplit.get(0));
												minutes = Integer.parseInt(punchTimeSplit.get(1));
												totalMinutes = (hours*60)+minutes;
												minimumTime = totalMinutes;
												index = 0;
											}
						        		}
										for (int j = 0; j < punchDetailsList.size(); ++j) {		
							        		Map punchDetails = (Map) punchDetailsList.get(j);
							        		String totalTime = (String)punchDetails.get("totalTime");
							        		if(UtilValidate.isNotEmpty(totalTime)){
												totalTime = totalTime.replace(" Hrs", "");
												List<String> timeSplit = StringUtil.split(totalTime, ":");
												if(UtilValidate.isNotEmpty(timeSplit)){
													hours = Integer.parseInt(timeSplit.get(0));
													minutes = Integer.parseInt(timeSplit.get(1));
													totalMinutes = (hours*60)+minutes;
													if (totalMinutes < minimumTime){
														minimumTime = totalMinutes;
														index = j;
												    }
												}
							        		}
										}*/
										Map shiftWiseMap = FastMap.newInstance();
										for (int j = 0; j < punchDetailsList.size(); ++j) {	
											Map punchDetails = (Map) punchDetailsList.get(j);
											String inTime = (String)punchDetails.get("inTime");
											String outTime = (String)punchDetails.get("outTime");
											String totalTime = (String)punchDetails.get("totalTime");
											if(UtilValidate.isNotEmpty(totalTime)){
												totalTime = totalTime.replace(" Hrs", "");
												List<String> timeSplit = StringUtil.split(totalTime, ":");
												if(UtilValidate.isNotEmpty(timeSplit)){
													 int hours = Integer.parseInt(timeSplit.get(0));
													 int minutes = Integer.parseInt(timeSplit.get(1));
													 int timeInMinutes = (hours*60)+minutes;
													 totalMinutes = totalMinutes + timeInMinutes;
												}
											}
											String inTimeVal = "inTime" + j;
											String outTimeVal = "outTime" + j;
											String totalTimeVal = "totalTime" + j;
											shiftWiseMap.put(inTimeVal,inTime);
											shiftWiseMap.put(outTimeVal,outTime);
											shiftWiseMap.put(totalTimeVal,totalTime);
											if((punchDetailsList.size()) == 1){
												shiftWiseMap.put("inTime1","");
												shiftWiseMap.put("outTime1","");
												shiftWiseMap.put("totalTime1","");
											}
										}
										
										if(totalMinutes >= 225){
											tempDayMap.put("punchDetails", shiftWiseMap);
											tempDayMap.put("date",UtilDateTime.toDateString(tempDate,"dd-MM-yyyy"));
											workedHolidaysList.add(tempDayMap);
										}
									}
									//Map punchDetails = (Map)(((List)punMap.get("punchDataList")).get(0));
									/*if(UtilValidate.isNotEmpty(punchDetails)){
										String totalTime = (String)punchDetails.get("totalTime");
										if(UtilValidate.isNotEmpty(totalTime)){
											totalTime = totalTime.replace(" Hrs", "");
											List<String> timeSplit = StringUtil.split(totalTime, ":");
											if(UtilValidate.isNotEmpty(timeSplit)){
												 int hours = Integer.parseInt(timeSplit.get(0));
												 int minutes = Integer.parseInt(timeSplit.get(1));
												 if(((hours*60)+minutes) >=225){
												 //if(((hours*60)+minutes) >=240){
													 tempDayMap.put("punchDetails", ((List)punMap.get("punchDataList")).get(0));
													 tempDayMap.put("date",UtilDateTime.toDateString(tempDate,"dd-MM-yyyy"));
													 workedHolidaysList.add(tempDayMap);
												 }
											}
										}
									}*/
								}
							}
							//tempWorkedHolidaysList.removeAll(EntityUtil.filterByAnd(tempWorkedHolidaysList, UtilMisc.toMap("date",tempDate)));
							//holidays.remove(tempDate);
						}
					}
					Set workedHolidaysSet = new HashSet(workedHolidaysList);
					List workedHolList = new ArrayList(workedHolidaysSet);
				    result.put("workedHolidaysList", workedHolList);
			}catch(GenericEntityException e){
	  			Debug.logError("Error fetching  holidays worked " + e.getMessage(), module);
	  		}
	    	
	    	//Debug.log("result:" + result, module);		 
	    	return result;
	 }
	 public static Map<String, Object> createEmployeeLoan(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String partyId = (String) context.get("partyId");
	    	String loanTypeFlag=(String)context.get("loanType");
	    	String loanTypeId = (String) context.get("loanTypeId");
	    	/*String statusId = (String)context.get("statusId");*/
	    	String statusId = "LOAN_APPROVED";
	    	String status = (String) context.get("statusId");
	    	String description=(String)context.get("description");
	    	String issuedPartyId=(String)context.get("issuedPartyId");
	    	String extLoanRefNum=(String)context.get("extLoanRefNum");
	    	Date disbDate =  (Date)context.get("disbDate");
	    	BigDecimal principalAmount = (BigDecimal)context.get("principalAmount");
	    	BigDecimal interestAmount = (BigDecimal)context.get("interestAmount");
	    	Long numInterestInst = (Long)context.get("numInterestInst");
	    	Long numPrincipalInst = (Long)context.get("numPrincipalInst");
	    	Long numCompInterestInst = (Long)context.get("numCompInterestInst");
	    	Long numCompPrincipalInst = (Long)context.get("numCompPrincipalInst");
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	Timestamp disbDateTime = UtilDateTime.toTimestamp(disbDate);
	    	Timestamp disbDateStart = UtilDateTime.getDayStart(disbDateTime);
	    	Timestamp disbDateEnd = UtilDateTime.getDayEnd(disbDateTime);
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			String payHeadTypeId = null;
			String partyIdFrom = null;
			String customTimePeriodId = null;
			
			if(UtilValidate.isNotEmpty(status)){
				statusId = status;
			}
			try {
				List condList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
				condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(disbDateStart.getTime())));
				condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(disbDateEnd.getTime())));
				EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 	
				List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, null, null, false);
				if(UtilValidate.isNotEmpty(customTimePeriodList)){
					GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
					customTimePeriodId = customTimePeriod.getString("customTimePeriodId");
				}
				GenericValue loanTypeDetails = delegator.findOne("LoanType",UtilMisc.toMap("loanTypeId", loanTypeId), false);
				if(UtilValidate.isNotEmpty(loanTypeDetails)){
					payHeadTypeId = loanTypeDetails.getString("payHeadTypeId");
				}
				Map partyDeductionMap = FastMap.newInstance();
				partyDeductionMap.put("userLogin",userLogin);
				partyDeductionMap.put("amountNullFlag","Y");
				partyDeductionMap.put("partyId",partyId);
				partyDeductionMap.put("payHeadTypeId",payHeadTypeId);
				partyDeductionMap.put("customTimePeriodId",customTimePeriodId);
				try {
					Map resultValue = dispatcher.runSync("createOrUpdatePartyBenefitOrDeduction", partyDeductionMap);
					if(ServiceUtil.isError(result)){
						Debug.logError(ServiceUtil.getErrorMessage(resultValue), module);
						return result;
					}
				} catch (GenericServiceException s) {
					Debug.logError("Error while creating Party Deduction"+s.getMessage(), module);
				} 
				//Creating finAccount related to loan
				String finAccountId=null;
				if(!loanTypeFlag.equals("external")){
					GenericValue LoanTypeDetails=null;
					String finAccountTypeId="";
					LoanTypeDetails =delegator.findOne("LoanType", UtilMisc.toMap("loanTypeId", loanTypeId), false);
					if(UtilValidate.isNotEmpty(LoanTypeDetails)){
						finAccountTypeId=LoanTypeDetails.getString("finAccountTypeId");
					}	
					String glAccountId = null;
					if(UtilValidate.isNotEmpty(finAccountTypeId)){
						GenericValue finAccountTypeGlAccount =delegator.findOne("FinAccountTypeGlAccount", UtilMisc.toMap("finAccountTypeId", finAccountTypeId, "organizationPartyId", "Company"), false);
						glAccountId = finAccountTypeGlAccount.getString("glAccountId");
						if(UtilValidate.isEmpty(glAccountId)){
							Debug.logError("Accounting GL is missing", module);
							return ServiceUtil.returnError("Accounting GL is missing...!");
						}
					}
					
					List finCondList=FastList.newInstance();
					finCondList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
					finCondList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, finAccountTypeId));
					finCondList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
					finCondList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
					EntityCondition finCondition = EntityCondition.makeCondition(finCondList, EntityOperator.AND);
					List<GenericValue> finAccountList=FastList.newInstance();
					finAccountList = delegator.findList("FinAccount", finCondition, null,null, null, false);
					if(UtilValidate.isEmpty(finAccountList)){
						GenericValue finAccount = delegator.makeValue("FinAccount");
						finAccount.set("ownerPartyId", partyId);
						finAccount.set("finAccountTypeId", finAccountTypeId);
						finAccount.set("statusId", "FNACT_ACTIVE");
						finAccount.set("organizationPartyId", "Company");
						finAccount.set("postToGlAccountId", glAccountId);
			 			delegator.createSetNextSeqId(finAccount);
			 			if(UtilValidate.isNotEmpty(finAccount)){
			 				finAccountId=finAccount.getString("finAccountId");
			 			}	
					}else{
						GenericValue finAccount = EntityUtil.getFirst(finAccountList);
						if(UtilValidate.isNotEmpty(finAccount)){
			 				finAccountId=finAccount.getString("finAccountId");
			 			}	
					}
				}
				
				List conditionList=FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, loanTypeId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS, null));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		List<GenericValue> loanList = FastList.newInstance();
	    		loanList = delegator.findList("Loan", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(loanList)){
	    			return ServiceUtil.returnError("Loan already exists for that loan type for Employee "+partyId); 
	    		}
	    		GenericValue loan = delegator.makeValue("Loan");
				loan.set("partyId", partyId);
				loan.set("loanTypeId", loanTypeId);
				loan.set("statusId", statusId);
				loan.set("description", description);
				loan.set("issuedPartyId", issuedPartyId);
				loan.set("extLoanRefNum", extLoanRefNum);
				loan.set("principalAmount", principalAmount);
				loan.set("interestAmount", interestAmount);
				loan.set("numInterestInst", numInterestInst);
				loan.set("numPrincipalInst", numPrincipalInst);
				loan.set("numCompInterestInst", numCompInterestInst);
				loan.set("numCompPrincipalInst", numCompPrincipalInst);
				loan.set("disbDate", disbDateStart);
				loan.set("loanFinAccountId", finAccountId);
				loan.set("createdDate", UtilDateTime.nowTimestamp());
				loan.set("createdByUserLogin", userLogin.get("userLoginId"));
	 			delegator.createSetNextSeqId(loan);
	        }catch(GenericEntityException e){
				Debug.logError("Error while creating Loan"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("Loan Created Sucessfully for Employee "  +partyId);
	        return result;
	    }
	 
	 public static Map<String, Object> updateEmployeeLoan(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String loanId = (String) context.get("loanId");
	    	String statusId = (String) context.get("statusId");
	    	Timestamp setlDate = null;
	    	String setlDateStr = (String) context.get("setlDate");
	        if (UtilValidate.isNotEmpty(setlDateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					setlDate = new java.sql.Timestamp(sdf.parse(setlDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ setlDateStr, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ setlDateStr, module);
				}
			}
	    	Timestamp setlDateTime = null;
	    	Timestamp setlDateStart = null;
	    	Timestamp setlDateEnd = null;
	    	if(UtilValidate.isNotEmpty(setlDate)){
	    		setlDateTime = UtilDateTime.toTimestamp(setlDate);
		    	setlDateStart = UtilDateTime.getDayStart(setlDateTime);
		    	setlDateEnd = UtilDateTime.getDayEnd(setlDateTime);
	    	}
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue loanDetails = null;
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			/*if(UtilValidate.isEmpty(setlDateEnd)){
				if(UtilValidate.isNotEmpty(statusId)){
					Timestamp fromDate = UtilDateTime.nowTimestamp();
					Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
					Map customTimePeriodIdMap = PayrollService.checkPayrollGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"date",UtilDateTime.toSqlDate(fromDateStart)));
					if (ServiceUtil.isError(customTimePeriodIdMap)) {
						return customTimePeriodIdMap;
					}
				}
			}else{
				if(UtilValidate.isNotEmpty(statusId)){
					Timestamp fromDate = UtilDateTime.nowTimestamp();
					Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
					Map customTimePeriodIdMap = PayrollService.checkPayrollGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"date",UtilDateTime.toSqlDate(fromDateStart)));
					if (ServiceUtil.isError(customTimePeriodIdMap)) {
						return customTimePeriodIdMap;
					}
				}
			}*/
			if(UtilValidate.isNotEmpty(setlDateEnd)){
				Timestamp fromDate = UtilDateTime.nowTimestamp();
				Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
				Map customTimePeriodIdMap = PayrollService.checkPayrollGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"date",UtilDateTime.toSqlDate(fromDateStart)));
				if (ServiceUtil.isError(customTimePeriodIdMap)) {
					return customTimePeriodIdMap;
				}
			}
			try {
				loanDetails = delegator.findOne("Loan",UtilMisc.toMap("loanId", loanId), false);
				if(UtilValidate.isNotEmpty(loanDetails)){
					if(UtilValidate.isNotEmpty(setlDateEnd)){
						loanDetails.set("setlDate", setlDateEnd);
					}
					/*if(UtilValidate.isNotEmpty(statusId)){
						loanDetails.set("statusId", statusId);
					}*/
					loanDetails.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					loanDetails.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					loanDetails.store();
				}
	        }catch(GenericEntityException e){
				Debug.logError("Error while updating Loan"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("Loan Updated Sucessfully...!");
	        return result;
	    }
	 public static Map<String, Object> cancelEmployeeLoan(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String loanId = (String) context.get("loanId");
	    	String statusId = "LOAN_CANCELLED";
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue loanDetails = null;
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			try {
				loanDetails = delegator.findOne("Loan",UtilMisc.toMap("loanId", loanId), false);
				if(UtilValidate.isNotEmpty(loanDetails)){
					if(UtilValidate.isNotEmpty(statusId)){
						loanDetails.set("statusId", statusId);
					}
					loanDetails.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					loanDetails.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					loanDetails.store();
				}
	        }catch(GenericEntityException e){
				Debug.logError("Error while cancelling Loan"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("Loan cancelled sucessfully...!");
	        return result;
	    }
	 
	 public static Map<String, Object> getLoanAmountsByLoanType(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String loanTypeId = (String) context.get("loanTypeId");
	    	String partyId = (String) context.get("partyId");
	    	
			GenericValue loanTypeDetails = null;
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			BigDecimal principalAmount = BigDecimal.ZERO;
			Long numPrincipalInst = null;
			Long numInterestInst = null;
			Double rateOfInterest = 1.0;
			BigDecimal interestAmount = BigDecimal.ZERO;
			String retirementDate = null;
			String noOfMonthsToRetire = null;
			
			Locale locale = new Locale("en","IN");
			TimeZone timeZone = TimeZone.getDefault();
			
			Timestamp fromDate = UtilDateTime.nowTimestamp();
	    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
			
			try {
				loanTypeDetails = delegator.findOne("LoanType",UtilMisc.toMap("loanTypeId", loanTypeId), false);
				if(UtilValidate.isNotEmpty(loanTypeDetails)){
					if(UtilValidate.isNotEmpty(loanTypeDetails)){
						if(UtilValidate.isNotEmpty(loanTypeDetails.getBigDecimal("maxAmount"))){
							principalAmount = loanTypeDetails.getBigDecimal("maxAmount");
						}
						if(UtilValidate.isNotEmpty(loanTypeDetails.getLong("numPrincipalInst"))){
							numPrincipalInst = loanTypeDetails.getLong("numPrincipalInst");
						}
						if(UtilValidate.isNotEmpty(loanTypeDetails.getLong("numInterestInst"))){
							numInterestInst = loanTypeDetails.getLong("numInterestInst");
						}
						if(UtilValidate.isNotEmpty(loanTypeDetails.getDouble("rateOfInterest"))){
							rateOfInterest = loanTypeDetails.getDouble("rateOfInterest");
						}
						Long totalInstallments = numPrincipalInst;
						if(UtilValidate.isNotEmpty(totalInstallments)){
							Evaluator evltr = new Evaluator(dctx);
							HashMap<String, Double> variables = new HashMap<String, Double>();
							variables.put("totalInstallments",totalInstallments.doubleValue());
							if(UtilValidate.isNotEmpty(rateOfInterest)){
								variables.put("rateOfInterest",rateOfInterest.doubleValue());
							}
							variables.put("principalAmount",principalAmount.doubleValue());
							String formulaId = "INTEREST_AMNT_CALC";
							evltr.setFormulaIdAndSlabAmount(formulaId,0.0);
							evltr.addVariableValues(variables);
							interestAmount = new BigDecimal( evltr.evaluate());
							interestAmount = interestAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
						}
						
						List conditionList = FastList.newInstance();
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			        	EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			        	List<GenericValue> employmentAndPersonList = delegator.findList("EmploymentAndPerson",cond, null, UtilMisc.toList("-thruDate"), null, false);
			        	if(UtilValidate.isNotEmpty(employmentAndPersonList)){
			        		GenericValue employmentAndPerson = EntityUtil.getFirst(employmentAndPersonList);
			        		if(UtilValidate.isNotEmpty(employmentAndPerson)){
			        			Date birthDate = employmentAndPerson.getDate("birthDate");
			        			if(UtilValidate.isNotEmpty(birthDate)){
				        			int day =  UtilDateTime.getDayOfMonth(UtilDateTime.toTimestamp(birthDate), timeZone, locale);
				        			int month = UtilDateTime.getMonth(UtilDateTime.toTimestamp(birthDate), timeZone, locale) + 1;
				        			if (day == 1) { // need to take the prev month last date
				        				month--;
				        			}
				        			int year = UtilDateTime.getYear(UtilDateTime.toTimestamp(birthDate), timeZone, locale) + 60;
				        			Timestamp retDate = UtilDateTime.toTimestamp(month, day, year, 0, 0, 0);
				        			retDate = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(retDate), timeZone, locale);
				        			if(UtilValidate.isNotEmpty(retDate)){
				        				double noOfCalenderDays = UtilDateTime.getIntervalInDays(fromDateStart, retDate);
					        			BigDecimal calenderDays = new BigDecimal(noOfCalenderDays);
					        			if(UtilValidate.isNotEmpty(calenderDays)){
					        				BigDecimal retireMonths = (BigDecimal)(calenderDays.divide(new BigDecimal(30),1,BigDecimal.ROUND_HALF_UP));
					        				if(UtilValidate.isNotEmpty(retireMonths)){
					        					List<String> monthSplit = StringUtil.split(retireMonths.toString(), ".");
												if(UtilValidate.isNotEmpty(monthSplit)){
													 int months = Integer.parseInt(monthSplit.get(0));
													 int days = Integer.parseInt(monthSplit.get(1));
													 retirementDate = UtilDateTime.toDateString(retDate,"dd/MM/yyyy");
								        			 int retirementMonths = months;
								        			 int retirementDays = (days*3);								        			 
								        			 noOfMonthsToRetire = + retirementMonths+ " Months " + " " + retirementDays+ " Days ";
												}
					        				}
					        			}
				        			}
			        			}
			        		}
			        	}
					}
				}
	        }catch(GenericEntityException e){
				Debug.logError("Error while getting Loan Amounts"+e.getMessage(), module);
			}
			result.put("principalAmount", principalAmount);
			result.put("interestAmount", interestAmount);
			result.put("numPrincipalInst", numPrincipalInst);
			result.put("numInterestInst", numInterestInst);
			result.put("rateOfInterest", rateOfInterest);
			result.put("retirementDate", retirementDate);
			result.put("noOfMonthsToRetire", noOfMonthsToRetire);
	        return result;
	    }
	 public static Map<String, Object> createEmployeeLoanRecovery(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String employeeId = (String) context.get("employeeId");
	    	String loanTypeId = (String) context.get("loanTypeId");
	    	String description = (String)context.get("description");
	    	String finAccountId = (String)context.get("finAccountId");
	    	String contraRefNum = (String) context.get("contraRefNum");
	    	String deducteePartyId = (String) context.get("deducteePartyId");
	    	BigDecimal loanRecoveryAmount = (BigDecimal)context.get("amount");
	    	Date loanRecoveryDate =  (Date)context.get("loanRecoveryDate");
	    	
	    	Timestamp loanRecDateTime = UtilDateTime.toTimestamp(loanRecoveryDate);
	    	Timestamp loanRecoveryDateStart = UtilDateTime.getDayStart(loanRecDateTime);
	    	Timestamp loanRecoveryDateEnd = UtilDateTime.getDayEnd(loanRecDateTime);
	    	
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			String payHeadTypeId = null;
			String loanId = null;
			try {
				GenericValue loanTypeDetails = delegator.findOne("LoanType",UtilMisc.toMap("loanTypeId", loanTypeId), false);
				if(UtilValidate.isNotEmpty(loanTypeDetails)){
					payHeadTypeId = loanTypeDetails.getString("payHeadTypeId");
				}
				try {
						Map loanRecoveryMap = FastMap.newInstance();
						loanRecoveryMap.put("userLogin",userLogin);
						loanRecoveryMap.put("employeeId",employeeId);
						loanRecoveryMap.put("payHeadTypeId",payHeadTypeId);
						loanRecoveryMap.put("timePeriodStart",loanRecoveryDateStart);
						loanRecoveryMap.put("timePeriodEnd",loanRecoveryDateEnd);
						Map resultValue = dispatcher.runSync("calculateLoanPayHeadAmount", loanRecoveryMap);
						if(ServiceUtil.isError(resultValue)){
							Debug.logError(ServiceUtil.getErrorMessage(resultValue), module);
							return resultValue;
						}
						Map loanRecovery = (Map) resultValue.get("loanRecovery");
						if(UtilValidate.isEmpty(loanRecovery)){
							Debug.logError(ServiceUtil.getErrorMessage(loanRecovery), module);
							return ServiceUtil.returnError("Error while getting loan amounts for Employee"+employeeId);
						}
						if(UtilValidate.isNotEmpty(loanRecovery)){
							loanId = (String) loanRecovery.get("loanId");
							BigDecimal principalAmount = (BigDecimal) loanRecovery.get("principalAmount");
							if(UtilValidate.isNotEmpty(principalAmount)){
								principalAmount = loanRecoveryAmount;
							}
							//Long principalInstNum = (Long) loanRecovery.get("principalInstNum");
							BigDecimal interestAmount = (BigDecimal) loanRecovery.get("interestAmount");
							if(UtilValidate.isNotEmpty(interestAmount)){
								interestAmount = loanRecoveryAmount;
							}
							//Long interestInstNum = (Long) loanRecovery.get("interestInstNum");
							if(UtilValidate.isNotEmpty(loanId)){
								List conditionList = FastList.newInstance();
								conditionList.add(EntityCondition.makeCondition("loanId", EntityOperator.EQUALS, loanId));
								conditionList.add(EntityCondition.makeCondition("recoveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,loanRecoveryDateStart));
								conditionList.add(EntityCondition.makeCondition("recoveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,loanRecoveryDateEnd));
					        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					        	List<GenericValue> loanRecoveryList = delegator.findList("LoanRecovery",condition, null, null, null, false);
					        	if(UtilValidate.isNotEmpty(loanRecoveryList)){
					        		return ServiceUtil.returnError("Loan Recovery already exists for that loan type for Employee "+employeeId);
					        	}
					    GenericValue newEntity = delegator.makeValue("LoanRecovery");	
						newEntity.set("loanId", loanId);
						newEntity.set("recoveryDate", loanRecoveryDateStart);
						newEntity.set("principalInstNum", loanRecovery.get("principalInstNum"));
						newEntity.set("principalAmount", principalAmount);
						newEntity.set("interestAmount", interestAmount);
						newEntity.set("interestInstNum", loanRecovery.get("interestInstNum"));
						newEntity.set("deducteePartyId", deducteePartyId);
						delegator.setNextSubSeqId(newEntity,"sequenceNum", 5, 1);
						delegator.createOrStore(newEntity);
					
					if(UtilValidate.isNotEmpty(loanRecoveryAmount)){
						GenericValue loanDetails = delegator.findOne("Loan",UtilMisc.toMap("loanId", loanId), false);
							if(UtilValidate.isEmpty(loanDetails)){
								Debug.logError("Loan  Id does not exists for Employee ", module);
								return ServiceUtil.returnError("Loan  Id does not exists for Employee "+loanId);
							}
							String loanFinAccountId = (String) loanDetails.get("loanFinAccountId");
							if(UtilValidate.isEmpty(loanFinAccountId)){
								Debug.logError("Loan Fin Account Id does not exists for Employee ", module);
								return ServiceUtil.returnError("Loan Fin Account Id does not exists for Employee "+employeeId);
							}
							
				             Map<String, Object> transCtxMap = FastMap.newInstance();
				             transCtxMap.put("statusId", "FINACT_TRNS_CREATED");
				             transCtxMap.put("entryType", "Contra");
				             transCtxMap.put("transactionDate", loanRecoveryDateStart);
				             transCtxMap.put("amount", loanRecoveryAmount);
				             transCtxMap.put("comments", description);
				             transCtxMap.put("contraRefNum", contraRefNum);
				           	 transCtxMap.put("contraFinAccountId", loanFinAccountId);
				             transCtxMap.put("finAccountId", finAccountId); 
				           	 transCtxMap.put("finAccountTransTypeId", "DEPOSIT");
				             transCtxMap.put("userLogin", userLogin);
				             Map<String, Object> createResult = dispatcher.runSync("preCreateFinAccountTrans", transCtxMap);
				             if (ServiceUtil.isError(createResult)) {
				                 return createResult;
				             }
				             String finAccountTransId = (String)createResult.get("finAccountTransId");
				             if(UtilValidate.isNotEmpty(finAccountTransId)){
				            	 newEntity.set("finAccountTransId", finAccountTransId);
				            	 delegator.store(newEntity);
				             }
									
								
							}
				        	
						}
					}
				} catch (GenericServiceException s) {
					Debug.logError("Error while creating loan recovery"+s.getMessage(), module);
				} 
	        }catch(GenericEntityException e){
				Debug.logError("Error while creating Loan"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("Loan Recovery Created Sucessfully for Employee "  +employeeId);
	        return result;
	    }
	 public static String createLoanDisbursement(HttpServletRequest request, HttpServletResponse response) {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
		  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		  	  Locale locale = UtilHttp.getLocale(request);
		  	  Map<String, Object> result = ServiceUtil.returnSuccess();
		  	  HttpSession session = request.getSession();
		  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
		      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
		  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		  	  BigDecimal totalAmount = BigDecimal.ZERO;
		  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		  	  if (rowCount < 1) {
		  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
				  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
		  		  return "error";
		  	  }
		  	  String finAccountId = "";
		  	  String instrumentDateStr = "";
		  	  String contraRefNum = "";
		  	  String inFavourOf = "";
		  	  String loanId = "";
		  	  String description = "";
		  	  String partyId = "";
		  	  
		  	boolean beganTransaction = false;
		  	List finAccountTransIds = FastList.newInstance();
		  	Timestamp instrumentDate=UtilDateTime.nowTimestamp();
				
		  	try{
		  		for (int i = 0; i < rowCount; i++){
			  		  
			  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
			  		  
			  		  BigDecimal amount = BigDecimal.ZERO;
			  		  String amountStr = "";
			  		  
			  		  if (paramMap.containsKey("loanId" + thisSuffix)) {
			  			loanId = (String) paramMap.get("loanId"+thisSuffix);
			  		  }
			  		  if (paramMap.containsKey("partyId" + thisSuffix)) {
			  			partyId = (String) paramMap.get("partyId"+thisSuffix);
			  		  }
			  		  if (paramMap.containsKey("amount" + thisSuffix)) {
			  			amountStr = (String) paramMap.get("amount"+thisSuffix);
			  		  }
			  		  if(UtilValidate.isNotEmpty(amountStr)){
						  try {
				  			  amount = new BigDecimal(amountStr);
				  		  } catch (Exception e) {
				  			  Debug.logError(e, "Problems parsing amount string: " + amountStr, module);
				  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + amountStr);
				  			  return "error";
				  		  }
			  		  }
				  	finAccountId = (String) paramMap.get("finAccountId");
				  	instrumentDateStr = (String) paramMap.get("instrumentDate");
				  	contraRefNum = (String) paramMap.get("contraRefNum");
				  	inFavourOf = (String) paramMap.get("inFavourOf");
				  	description = (String) paramMap.get("description");
			  	
			        if (UtilValidate.isNotEmpty(instrumentDateStr)) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
						try {
							instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
							instrumentDate = UtilDateTime.getDayStart(instrumentDate);
						} catch (ParseException e) {
							Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
						} catch (NullPointerException e) {
							Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
						}
					}
		  			if (UtilValidate.isNotEmpty(loanId)) {
						    GenericValue loanDetails = delegator.findOne("Loan",UtilMisc.toMap("loanId", loanId), false);
							if(UtilValidate.isEmpty(loanDetails)){
								Debug.logError("Loan  Id does not exists for Employee ", module);
								request.setAttribute("_ERROR_MESSAGE_", "Loan  Id does not exists for Employee: "+loanId);	  		  
						  		return "error";
							}
							String loanFinAccountId = (String) loanDetails.get("loanFinAccountId");
							if(UtilValidate.isEmpty(loanFinAccountId)){
								Debug.logError("Loan Fin Account Id does not exists for Employee ", module);
								request.setAttribute("_ERROR_MESSAGE_", "Loan Fin Account Id does not exists for Employee: "+partyId);	  		  
						  		return "error";
							}
							if(UtilValidate.isNotEmpty(loanDetails)){
								loanDetails.set("statusId","LOAN_DISBURSED");
								loanDetails.store();
							}
							//creating  fin account transactions here
				             Map<String, Object> transCtxMap = FastMap.newInstance();
				             transCtxMap.put("statusId", "FINACT_TRNS_CREATED");
				             transCtxMap.put("entryType", "Contra");
				             transCtxMap.put("transactionDate", instrumentDate);
				             transCtxMap.put("amount", amount);
				             transCtxMap.put("comments", description);
				             transCtxMap.put("contraRefNum", contraRefNum);
				             transCtxMap.put("inFavourOf", inFavourOf);
				           	 transCtxMap.put("contraFinAccountId", loanFinAccountId);
				             transCtxMap.put("finAccountId", finAccountId); 
				           	 transCtxMap.put("finAccountTransTypeId", "WITHDRAWAL");
				           	 transCtxMap.put("partyId", partyId);
				             transCtxMap.put("userLogin", userLogin);
				             Map<String, Object> createResult = dispatcher.runSync("preCreateFinAccountTrans", transCtxMap);
				             if (ServiceUtil.isError(createResult)) {
				            	 return "error";
				             }
				             String finAccountTransId = (String)createResult.get("finAccountTransId");
				             finAccountTransIds.add(finAccountTransId);
						}
					  totalAmount = totalAmount.add(amount);
					}//end of for loop
		  			
		  		//creating batch fin account transactions here
			  	if(UtilValidate.isNotEmpty(finAccountTransIds) && finAccountTransIds.size() > 0 ){
			  		  Map serviceCtx = FastMap.newInstance();
			  		  serviceCtx.put("finAccountTransIds", finAccountTransIds);
			  		  serviceCtx.put("instrumentDate", instrumentDate);
			  		  serviceCtx.put("finAccntTransDate", instrumentDate);
			  		  serviceCtx.put("fromDate", instrumentDate);
			  		  serviceCtx.put("contraRefNum", contraRefNum);
			  		  serviceCtx.put("inFavor", inFavourOf);
			  		  serviceCtx.put("issuingAuthority", finAccountId);
			  		  serviceCtx.put("amount", totalAmount);
			  		  serviceCtx.put("statusId", "FNACTTRNSGRP_CREATED");
			  		  serviceCtx.put("finAccountId", finAccountId);
			  		  serviceCtx.put("finAcntTrnsGrpTypeId", "FIN_ACNT_TRNS_BATCH");
			  		  serviceCtx.put("createdDate", UtilDateTime.nowTimestamp());
			  		  serviceCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
			  		  serviceCtx.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			  		  serviceCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
			  		  serviceCtx.put("userLogin", userLogin);
		  			  Map resultCtx = dispatcher.runSync("createFinAccountTransGroupAndMember", serviceCtx);
			  		  if(ServiceUtil.isError(resultCtx)){
			    			Debug.logError("Error while creating fin account trans group: " + ServiceUtil.getErrorMessage(resultCtx), module);
			    			request.setAttribute("_ERROR_MESSAGE_", "Error while creating fin account trans group");
				  			TransactionUtil.rollback();
				  			return "error";
			  		  }
				  	  String finAccntTransGroupId = (String)resultCtx.get("finAccntTransGroupId");
			  	  }
		  	}catch (GenericEntityException e) {
		  		  try {
		  			  // only rollback the transaction if we started one...
		  			  TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
		  		  } catch (GenericEntityException e2) {
		  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		  		  }
		  		  Debug.logError("An entity engine error occurred while fetching data", module);
		  	  }
		  	  catch (GenericServiceException e) {
		  		  try {
		  			  // only rollback the transaction if we started one...
		  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
		  		  } catch (GenericEntityException e2) {
		  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
		  		  }
		  		  Debug.logError("An entity engine error occurred while calling services", module);
		  	  }
		  	  finally {
		  		  // only commit the transaction if we started one... this will throw an exception if it fails
		  		  try {
		  			  TransactionUtil.commit(beganTransaction);
		  		  } catch (GenericEntityException e) {
		  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
		  		  }
		  	  }
		  	request.setAttribute("_EVENT_MESSAGE_", "Successfully made processed group fin account trans entries ");
	        request.setAttribute("_EVENT_MESSAGE_", "Loan Disbursement successfully done for Loan "+loanId);
	        result = ServiceUtil.returnSuccess("Loan Disbursement successfully done "+loanId+" ..!");
	        request.setAttribute("loanId",loanId);
	        result.put("finAccountTransIds", finAccountTransIds);
	        return "success"; 
		}
	 	
	 public static Map<String, Object> createNewEmployment(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String partyIdFrom = (String) context.get("partyIdFrom");
	    	String partyIdTo = (String) context.get("partyIdTo");
	    	String fromDateStr = (String) context.get("fromDate");
	    	String editFromDateStr = (String) context.get("editFromDate");
	    	String reportingDateStr =  (String)context.get("reportingDate");
	    	String newLocationGeoId = (String)context.get("locationGeoId");
	    	Timestamp reportingDateStamp=null;
	    	Timestamp fromDate = UtilDateTime.nowTimestamp();
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				try {
					fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ fromDateStr, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ fromDateStr, module);
				}
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					fromDate = new java.sql.Timestamp(sdf.parse(editFromDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ editFromDateStr, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ editFromDateStr, module);
				}
			}
	        if (UtilValidate.isNotEmpty(reportingDateStr)) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
				try {
					reportingDateStamp = new java.sql.Timestamp(sdf1.parse(reportingDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ reportingDateStr, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ reportingDateStr, module);
				}
			}
	    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
	    	Timestamp reportingDateStart = UtilDateTime.getDayStart(reportingDateStamp);
	    	Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Timestamp appointmentDate = null;
			String locationGeoId = null;
			try {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyIdTo));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
				//conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,fromDateStart));
				//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart)));
		    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
				List<GenericValue> activeEmploymentList = delegator.findList("Employment", condition, null, UtilMisc.toList("-fromDate"), null, false);
				if(UtilValidate.isNotEmpty(activeEmploymentList)){
					GenericValue activeEmployment = EntityUtil.getFirst(activeEmploymentList);
					appointmentDate = activeEmployment.getTimestamp("appointmentDate");
					locationGeoId = activeEmployment.getString("locationGeoId");
					Timestamp newFromDate = activeEmployment.getTimestamp("fromDate");
					if(newFromDate.compareTo(fromDateStart)>= 0){
						return ServiceUtil.returnError("Department already exists.....!");
					}else{
						activeEmployment.set("thruDate", previousDayEnd);
						activeEmployment.store();
						GenericValue newEntity = delegator.makeValue("Employment");
						newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
						newEntity.set("roleTypeIdTo", "EMPLOYEE");
						newEntity.set("partyIdFrom", partyIdFrom);
						newEntity.set("partyIdTo", partyIdTo);
						newEntity.set("fromDate", fromDateStart);
						newEntity.set("reportingDate", reportingDateStart);
						newEntity.set("appointmentDate", appointmentDate);
						if(UtilValidate.isNotEmpty(newLocationGeoId)){
							newEntity.set("locationGeoId", newLocationGeoId);
						}else{
							newEntity.set("locationGeoId", locationGeoId);
						}
						newEntity.create();
					}
				}else{
					GenericValue newEntity = delegator.makeValue("Employment");
					newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
					newEntity.set("roleTypeIdTo", "EMPLOYEE");
					newEntity.set("partyIdFrom", partyIdFrom);
					newEntity.set("partyIdTo", partyIdTo);
					newEntity.set("fromDate", fromDateStart);
					newEntity.set("reportingDate", reportingDateStart);
					newEntity.set("appointmentDate", appointmentDate);
					if(UtilValidate.isNotEmpty(newLocationGeoId)){
						newEntity.set("locationGeoId", newLocationGeoId);
					}else{
						newEntity.set("locationGeoId", locationGeoId);
					}
					newEntity.create();
				}
	        }catch(GenericEntityException e){
				Debug.logError("Error while creating new Employment"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("New Employment Created Sucessfully...!");
	        return result;
	    }
	 public static Map<String, Object> updateLocationGeo(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String partyIdTo = (String) context.get("partyId");
	    	String partyIdFrom = (String) context.get("company");
	    	String locationNewGeoId = (String) context.get("locationGeoId");
	    	String fromDateStr = (String) context.get("fromDate");
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Timestamp appointmentDate = null;
			String locationGeoId = null;
			Timestamp fromDate = null;
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
			
			if(UtilValidate.isEmpty(fromDateStr)){
	    		fromDate = UtilDateTime.nowTimestamp();
	    	}
	    	
			if(UtilValidate.isNotEmpty(fromDateStr)){
				try {
						fromDate = UtilDateTime.toTimestamp(sdf1.parse(fromDateStr));
					} catch (ParseException e) {
						}
			}
			Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
		    Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
				
			try {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyIdTo));
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,partyIdFrom));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS ,"INTERNAL_ORGANIZATIO"));
				conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS ,"EMPLOYEE"));
	            EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);   
	            List<GenericValue> activeEmploymentList = delegator.findList("Employment", condition, null, UtilMisc.toList("-fromDate"), null, false);
	            if(UtilValidate.isNotEmpty(activeEmploymentList)){
	            	GenericValue activeEmployment = EntityUtil.getFirst(activeEmploymentList);
			    	locationGeoId = activeEmployment.getString("locationGeoId");
					appointmentDate = activeEmployment.getTimestamp("appointmentDate");
					Timestamp newFromDate = activeEmployment.getTimestamp("fromDate");
					if(newFromDate.compareTo(fromDateStart)>= 0){
						activeEmployment.set("locationGeoId", locationNewGeoId);
						activeEmployment.store();
					}else{
						activeEmployment.set("locationGeoId", locationNewGeoId);
						activeEmployment.set("thruDate", previousDayEnd);
						activeEmployment.store();
						GenericValue newEntity = delegator.makeValue("Employment");
						newEntity.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
						newEntity.set("roleTypeIdTo", "EMPLOYEE");
						newEntity.set("partyIdFrom", partyIdFrom);
						newEntity.set("partyIdTo", partyIdTo);
						newEntity.set("fromDate", fromDateStart);
						newEntity.set("appointmentDate", appointmentDate);
						newEntity.set("locationGeoId", locationGeoId);
						newEntity.create();
					}
				}else{
					return ServiceUtil.returnError("Location already exists.....!");
				}
	        }catch(GenericEntityException e){
				Debug.logError("Error while creating new Employment"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("New Location updated Sucessfully...!");
	        return result;
	    }
	 public static Map<String, Object> updateEmployeeFinancialAccount(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      String partyId = (String) context.get("partyId");
	      String finAccountCode = (String)context.get("finAccountCode");
	      String finAccountName = (String)context.get("finAccountName");
	      String finAccountBranch = (String)context.get("finAccountBranch");
	      String ifscCode = (String)context.get("ifscCode");
	      String finAccountId = (String)context.get("disbursmentBank");
	      String date =  (String)context.get("date");
	      Map result = ServiceUtil.returnSuccess();
	      try{
	    	  List conditionList = FastList.newInstance();
	    	  conditionList.add(EntityCondition.makeCondition("finAccountId",EntityOperator.EQUALS,partyId));
	    	  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	  List<GenericValue> finAccountList = delegator.findList("EmpFinAccount", condition, null, null, null, false);
	    		  if(UtilValidate.isEmpty(finAccountList)){
						GenericValue newEntity = delegator.makeValue("EmpFinAccount");
						newEntity.set("finAccountId", partyId);
						newEntity.set("finAccountCode", finAccountCode);
						newEntity.set("finAccountName", finAccountName);
						newEntity.set("organizationPartyId", "Company");
						newEntity.set("ownerPartyId", partyId);
						newEntity.set("finAccountTypeId", "BANK_ACCOUNT");
						newEntity.set("statusId", "FNACT_ACTIVE");
						if(UtilValidate.isNotEmpty(finAccountBranch)){
							newEntity.set("finAccountBranch", finAccountBranch);
						}
						if(UtilValidate.isNotEmpty(ifscCode)){
							newEntity.set("ifscCode", ifscCode);
						}
						newEntity.create();
					}else{	
						GenericValue finAccount = finAccountList.get(0);
						if(!finAccountCode.equals(finAccount.getString("finAccountCode"))){
							finAccount.set("finAccountCode",finAccountCode);
						}
						if(!finAccountName.equals(finAccount.getString("finAccountName"))){
							finAccount.set("finAccountName",finAccountName);
						}
						if((UtilValidate.isNotEmpty(finAccountBranch))){
							if(!finAccountBranch.equals(finAccount.getString("finAccountBranch"))){
								finAccount.set("finAccountBranch", finAccountBranch);
							}
						}						
						if((UtilValidate.isNotEmpty(ifscCode))){
							if(!ifscCode.equals(finAccount.getString("ifscCode"))){
								finAccount.set("ifscCode", ifscCode);
							}
						}
						finAccount.store();
					}
	    		  if(UtilValidate.isNotEmpty(finAccountId) && UtilValidate.isNotEmpty(date)){
	    			  Map resultMap=updateDisbursmentBank(dctx,UtilMisc.toMap("userLogin",userLogin,"disbursmentBank",finAccountId,"partyId",partyId,"date",date));
	    		  }
	      }catch(GenericEntityException e){
				Debug.logError("Error while creating new FinAccount"+e.getMessage(), module);
			}
	      result = ServiceUtil.returnSuccess("New FinAccount Created Sucessfully...!");
	      return result;
	    }
	 
	 public static Map<String, Object> updateEmployeeFinancialAccountRole(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      String partyId = (String) context.get("partyId");
	      String finAccountId = (String)context.get("disbursmentBank");
	      String date =  (String)context.get("date");
	      String finAccountCode = (String)context.get("finAccountCode");
	      String finAccountName = (String)context.get("finAccountName");
	      String finAccountBranch = (String)context.get("finAccountBranch");
	      String ifscCode = (String)context.get("ifscCode");
	      Timestamp thruDate=null;
	      Timestamp dateTime=null;
	      Map result = ServiceUtil.returnSuccess();
	      if(UtilValidate.isNotEmpty(date)){
	    	  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	    	  try{
	    		  dateTime=new java.sql.Timestamp(sdf.parse(date+" 00:00:00").getTime());
	    		  thruDate=UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(dateTime), -1);
	    	  }catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ date, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ date, module);
				}
	      }
	      try{
	    	  
	    	  List conditionList = FastList.newInstance();
	    	  conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
	    	  conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
	    	  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	  List<GenericValue> finAccountRoleList = delegator.findList("EmpFinAccountRole", condition, null, null, null, false);
	    	  if(UtilValidate.isNotEmpty(finAccountRoleList)){
	    		  GenericValue finAccountRole = EntityUtil.getFirst(finAccountRoleList); 
	    		  	finAccountRole.set("thruDate",thruDate);
	    		  	finAccountRole.store();
	    		  GenericValue newEntity = delegator.makeValue("EmpFinAccountRole");
	    		  	newEntity.set("roleTypeId","EMPLOYEE");
	    		  	newEntity.set("finAccountId",finAccountId);
	    		  	newEntity.set("partyId",partyId);
	    		  	newEntity.set("fromDate",dateTime);
	    		  	if(UtilValidate.isNotEmpty(finAccountCode)){
	    		  		newEntity.set("finAccountCode", finAccountCode);
	    		  	}
	    		  	if(UtilValidate.isNotEmpty(finAccountName)){
	    		  		newEntity.set("finAccountName", finAccountName);
	    		  	}
	    		  	if(UtilValidate.isNotEmpty(finAccountBranch)){
	    		  		newEntity.set("finAccountBranch", finAccountBranch);
	    		  	}
	    		  	if(UtilValidate.isNotEmpty(ifscCode)){
	    		  		newEntity.set("ifscCode", ifscCode);
	    		  	}
	    		  	newEntity.create();
	    		  
	    	  }else{
	    		  GenericValue newEntity = delegator.makeValue("EmpFinAccountRole");
		  		  	newEntity.set("roleTypeId","EMPLOYEE");
		  		  	newEntity.set("finAccountId",finAccountId);
		  		  	newEntity.set("partyId",partyId);
		  		  	newEntity.set("fromDate",dateTime);
		  		  if(UtilValidate.isNotEmpty(finAccountCode)){
	    		  		newEntity.set("finAccountCode", finAccountCode);
	    		  	}
	    		  	if(UtilValidate.isNotEmpty(finAccountName)){
	    		  		newEntity.set("finAccountName", finAccountName);
	    		  	}
	    		  	if(UtilValidate.isNotEmpty(finAccountBranch)){
	    		  		newEntity.set("finAccountBranch", finAccountBranch);
	    		  	}
	    		  	if(UtilValidate.isNotEmpty(ifscCode)){
	    		  		newEntity.set("ifscCode", ifscCode);
	    		  	}
		  		  	newEntity.create();
	    	  }
	      }catch(GenericEntityException e){
				Debug.logError("Error while creating new FinAccount"+e.getMessage(), module);
			}
	      result = ServiceUtil.returnSuccess("New FinAccount Created Sucessfully...!");
	      return result;
	    }
	 
	    public static Map<String, Object> updateDisbursmentBank(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      String partyId = (String) context.get("partyId");
	      String finAccountId = (String)context.get("disbursmentBank");
	      String date =  (String)context.get("date");
	      Timestamp thruDate=null;
	      Timestamp dateTime=null;
	      Map result = ServiceUtil.returnSuccess();
	      if(UtilValidate.isNotEmpty(date)){
	    	  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	    	  try{
	    		  dateTime=new java.sql.Timestamp(sdf.parse(date+" 00:00:00").getTime());
	    		  thruDate=UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(dateTime), -1);
	    	  }catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ date, module);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "	+ date, module);
				}
	      }
	      try{
	    	  
	    	  List conditionList = FastList.newInstance();
	    	  conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
	    	  conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
	    	  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	  List<GenericValue> finAccountRoleList = delegator.findList("FinAccountRole", condition, null, null, null, false);
	    	  if(UtilValidate.isNotEmpty(finAccountRoleList)){
	    		  GenericValue finAccountRole = EntityUtil.getFirst(finAccountRoleList); 
	    		  if(!finAccountId.equals(finAccountRole.getString("finAccountId"))){
	    		  	finAccountRole.set("thruDate",thruDate);
	    		  	finAccountRole.store();
	    		  GenericValue newEntity = delegator.makeValue("FinAccountRole");
	    		  	newEntity.set("roleTypeId","EMPLOYEE");
	    		  	newEntity.set("finAccountId",finAccountId);
	    		  	newEntity.set("partyId",partyId);
	    		  	newEntity.set("fromDate",dateTime);
	    		  	newEntity.create();
	    		  }	
	    	  }else{
	    		  GenericValue newEntity = delegator.makeValue("FinAccountRole");
		  		  	newEntity.set("roleTypeId","EMPLOYEE");
		  		  	newEntity.set("finAccountId",finAccountId);
		  		  	newEntity.set("partyId",partyId);
		  		  	newEntity.set("fromDate",dateTime);
		  		  	newEntity.create();
	    	  }
	      }catch(GenericEntityException e){
				Debug.logError("Error while creating new FinAccount"+e.getMessage(), module);
			}
	      result = ServiceUtil.returnSuccess("New FinAccount Created Sucessfully...!");
	      return result;
	    }
	    
	    public static Map<String, Object> createPartyIdentificationTypeConv(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			Locale locale = (Locale) context.get("locale");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = (String) context.get("partyId");
			String partyIdentificationTypeId = (String) context.get("partyIdentificationTypeId");
			String idValue = (String) context.get("idValue");
			String issueDate = (String) context.get("issueDate");
			String expiryDate = (String) context.get("expiryDate");
			String updateFlag = (String) context.get("updateFlag");
			Timestamp issueDateTime = null;
			Timestamp expiryDateTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			Map<String, Object> outMap = FastMap.newInstance();
			
			if(UtilValidate.isNotEmpty(updateFlag)){
				if(UtilValidate.isNotEmpty(issueDate)){
					try {
					   		issueDateTime = UtilDateTime.toTimestamp(sdf1.parse(issueDate));
				        	Debug.log("issueDateTime========upd============="+issueDateTime);
						} catch (ParseException e) {
							}
				}
				if(UtilValidate.isNotEmpty(expiryDate)){
				   try {
					   		expiryDateTime = UtilDateTime.toTimestamp(sdf.parse(expiryDate));
						    Debug.log("expiryDateTime========upd============="+expiryDateTime);
						} catch (ParseException e) {
							}
				}
				
				try{
				outMap = dispatcher.runSync("updatePartyIdentification", UtilMisc.toMap("partyIdentificationTypeId",partyIdentificationTypeId,"idValue",idValue,"partyId",partyId, "issueDate", issueDateTime, "expiryDate", expiryDateTime, "userLogin", userLogin));
				Debug.log("outMap=======upd========="+outMap);
				if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("failed service createPartyIdentification:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		        }
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating party Identification" + e);	
				}
			}else{
				if(UtilValidate.isNotEmpty(issueDate)){
					   try {
						   		issueDateTime = UtilDateTime.toTimestamp(sdf.parse(issueDate));
					        	Debug.log("issueDateTime====================="+issueDateTime);
						} catch (ParseException e) {
							}
				    }
					if(UtilValidate.isNotEmpty(expiryDate)){
						   try {
							   		expiryDateTime = UtilDateTime.toTimestamp(sdf.parse(expiryDate));
						        	Debug.log("expiryDateTime====================="+expiryDateTime);
							} catch (ParseException e) {
								}
					}
				try{
					
					outMap = dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId",partyIdentificationTypeId,"idValue",idValue,"partyId",partyId, "issueDate", issueDateTime, "expiryDate", expiryDateTime, "userLogin", userLogin));
					Debug.log("outMap================"+outMap);
					if(ServiceUtil.isError(outMap)){
			           	 	Debug.logError("failed service createPartyIdentification:"+ServiceUtil.getErrorMessage(outMap), module);
			           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
			        }
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating party Identification" + e);	
				}
			}
			result.put("partyId", partyId);
		    return result;
	    }
	    public static Map<String, Object> createNewEmployeeMasters(DispatchContext dctx, Map context) {
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
			Locale locale = (Locale) context.get("locale");
		
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String ownerPartyId = null;
			String address1 = null;
			String address2 = null;
			String contactMechId = null;
			String partyId = (String) context.get("employeeId");
			//String partyId = null;
			String firstName = (String) context.get("firstName");
			String lastName = (String) context.get("lastName");
			String middleName = (String) context.get("middleName");
			String fatherName = (String) context.get("fatherName");
			String motherName = (String) context.get("motherName");
			String birthDate =  (String)context.get("birthDate");
			String birthPlace = (String) context.get("birthPlace");
			String birthState = (String) context.get("birthState");
			String birthDistrict = (String) context.get("birthDistrict");
			String bloodGroup =(String)context.get("bloodGroup");
			String gender =(String)context.get("gender");
			String spouseName =(String)context.get("spouseName");
			String maritalStatus =(String)context.get("maritalStatus");
			String alternatemobileNumber =(String)context.get("alternatemobileNumber");
			String emergencyContactName =(String)context.get("emergencyContactName");
			String emergencyContactNumber =(String)context.get("emergencyContactNumber");
			String emergencyContactRelationship =(String)context.get("emergencyContactRelationship");
			String countryCode =(String)context.get("countryCode");
			String mobileNumber =(String)context.get("mobileNumber");
			address1 =(String)context.get("address1");
			address2 =(String)context.get("address2");
			String city =(String)context.get("city");
			String state =(String)context.get("state");
			String postalCode =(String)context.get("postalCode");
			String country =(String)context.get("country");
			String prsAddress1 =(String)context.get("prsAddress1");
			String prsAddress2 =(String)context.get("prsAddress2");
			String prsCity =(String)context.get("prsCity");
			String prsState =(String)context.get("prsState");
			String prsPostalCode =(String)context.get("prsPostalCode");
			String prsCountry =(String)context.get("prsCountry");
			String email =(String)context.get("email");
			String secondaryEmail =(String)context.get("secondaryEmail");
			String userName =(String)context.get("userName");
			String pasword =(String)context.get("pasword");
			String confirmPassword =(String)context.get("confirmPassword");
			String vehicleType =(String)context.get("vehicleType");
			String quarterType =(String)context.get("quarterType");
			String motherTongue =(String)context.get("motherTongue");
			String religion =(String)context.get("religion");
			//String caste =(String)context.get("caste");
			String nationality =(String)context.get("nationality");
			String punchType =(String)context.get("punchType");
			String weeklyOff =(String)context.get("weeklyOff");
			String attendanceIndn =(String)context.get("attendanceIndn");
			String paymentMode =(String)context.get("paymentMode");
			String canteenFacin =(String)context.get("canteenFacin");
			String companyBus =(String)context.get("companyBus");
			String flexibleShift =(String)context.get("flexibleShift");
			String busRouteNo =(String)context.get("busRouteNo");
			String shiftType =(String)context.get("shiftType");
			String passportNumber =(String)context.get("passportNumber");
			String pfNumber =(String)context.get("pfNumber");
			String aadharNumber =(String)context.get("aadharNumber");
			String panNumber =(String)context.get("panNumber");
			String partyIdFrom =(String)context.get("partyIdFrom");
			Date dateOfJoining =  (Date)context.get("dateOfJoining");
			String employmentDate = (String)context.get("employmentDate");
			Date passportExpireDate =  (Date)context.get("passportExpireDate");
			String backgroundVerification =(String)context.get("backgroundVerification");
			String emplPositionTypeId = (String) context.get("emplPositionTypeId");
			String locationGeoId = (String) context.get("locationGeoId");
			String deprtName = (String) context.get("deprtName");
			Map<String, Object> resultMap = FastMap.newInstance();
			Map<String, Object> resultMap1 = FastMap.newInstance();
			Map<String, Object> input = FastMap.newInstance();
			Map<String, Object> outMap = FastMap.newInstance();
			Timestamp openedDate = null;
			GenericValue parentFacility=null;
			GenericValue facility;
			GenericValue person = null;
			Timestamp employmentDateTime = null;
			Timestamp birthDateTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
			
			try{
				// create Person / Party 
				/*String employeeId = null;
				Map<String, Object> seqResult = dispatcher.runSync("getNextEmployeeSeqID", UtilMisc.toMap("userLogin", userLogin));
				Debug.log("seqResult========="+seqResult);
				if (ServiceUtil.isError(seqResult)) {
					return ServiceUtil.returnError("Error while creating new party Sequence"); 
				}
				if(UtilValidate.isNotEmpty(seqResult)){
					employeeId = (String)seqResult.get("employeeId");
				}
				Debug.log("employeeId========="+employeeId);
				partyId = employeeId;*/
				
				try {
			        person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
			        
		        } catch (GenericEntityException e) {
		            Debug.logWarning(e.getMessage(), module);
		        }
		        if(UtilValidate.isNotEmpty(person)){
		        	return ServiceUtil.returnError("Error while creating  Party, PartyId already exists" +partyId); 
		        }
		        if(UtilValidate.isNotEmpty(birthDate)){
			        try {
			        	birthDateTime = UtilDateTime.toTimestamp(sdf.parse(birthDate));
					} catch (ParseException e) {
					}
		        }
				Object tempInput = "PARTY_ENABLED";
				input = UtilMisc.toMap("firstName", firstName, "lastName", lastName, "middleName",middleName, "fatherName",fatherName, "motherName",motherName, "birthDate",UtilDateTime.toSqlDate(birthDateTime), "placeOfBirth",birthPlace, "bloodGroup",bloodGroup,"gender",gender, "maritalStatus",maritalStatus, "spouseName",spouseName, "motherTongue",motherTongue, "religion",religion,  "nationality",nationality, "passportNumber",passportNumber, "passportExpireDate",passportExpireDate, "statusId", tempInput,"partyId",partyId);
				resultMap = dispatcher.runSync("createPerson", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                return resultMap;
	            }
				ownerPartyId = (String) resultMap.get("partyId");
				//create partyrole
				Object tempInputId = "EMPLOYEE";
				input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "roleTypeId", tempInputId);
				resultMap = dispatcher.runSync("createPartyRole", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                return resultMap;
	            }
				
				//create panNumber and aadharNumber
				
				if(UtilValidate.isNotEmpty(panNumber)){
	            	 dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PAN_NUMBER","idValue",panNumber,"partyId",ownerPartyId,"userLogin", userLogin));
	       	    }
	       	    if(UtilValidate.isNotEmpty(aadharNumber)){
	       	    	dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","ADR_NUMBER","idValue",aadharNumber,"partyId",ownerPartyId,"userLogin", userLogin));
	       	    }
	       	    
				// create phone number
				if (UtilValidate.isNotEmpty(mobileNumber)){
					if (UtilValidate.isEmpty(countryCode)){
						countryCode	="91";
					}
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("contactNumber",mobileNumber);
		            input.put("contactMechPurposeTypeId","PRIMARY_PHONE");
		            input.put("countryCode",countryCode);	
		            input.put("partyId", ownerPartyId);
		            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
				
				if (UtilValidate.isNotEmpty(alternatemobileNumber)){
					if (UtilValidate.isEmpty(countryCode)){
						countryCode	="91";
					}
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("contactNumber",alternatemobileNumber);
		            input.put("contactMechPurposeTypeId","PHONE_MOBILE_OTHER");
		            input.put("countryCode",countryCode);	
		            input.put("partyId", ownerPartyId);
		            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("failed service create party alternate contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
				
				if (UtilValidate.isNotEmpty(emergencyContactNumber)){
					if (UtilValidate.isEmpty(countryCode)){
						countryCode	="91";
					}
					input.clear();
		            input.put("userLogin", userLogin);
		            input.put("contactNumber",emergencyContactNumber);
		            input.put("contactMechPurposeTypeId","PHONE_WORK_EMRGNCY"); 
		            input.put("countryCode",countryCode);	
		            input.put("partyId", ownerPartyId);
		            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
					if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("failed service create party emergency contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
				
				// create PostalAddress
				input.clear();
				if (UtilValidate.isNotEmpty(address1)){
					input = UtilMisc.toMap("userLogin", userLogin, "partyId",ownerPartyId, "address1",address1, "address2", address2, "city", (String)context.get("city"), "birthState",birthState,  "birthDistrict",birthDistrict, "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", (String)context.get("postalCode"), "contactMechId", contactMechId);
					resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
					if (ServiceUtil.isError(resultMap)) {
						Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                return resultMap;
		            }
				}
				input.clear();
				if (UtilValidate.isNotEmpty(prsAddress1)){
					input = UtilMisc.toMap("userLogin", userLogin, "partyId",ownerPartyId, "address1",prsAddress1, "address2", prsAddress2, "city", (String)context.get("prsCity"), "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", (String)context.get("prsPostalCode"), "contactMechTypeId","POSTAL_ADDRES2", "contactMechId", contactMechId);
					resultMap1 =  dispatcher.runSync("createPartyPostalAddress", input);
					if (ServiceUtil.isError(resultMap1)) {
						Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                return resultMap;
		            }
				}
				
				// Create Party Email
				if (UtilValidate.isNotEmpty(email)){
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
		            input.put("emailAddress", email);
		            input.put("partyId", ownerPartyId);
		            input.put("verified", "Y");
		            input.put("fromDate", UtilDateTime.nowTimestamp());
		            outMap = dispatcher.runSync("createPartyEmailAddress", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
				
				
				// Create party secondary Email
				if (UtilValidate.isNotEmpty(secondaryEmail)){
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("contactMechPurposeTypeId", "SECONDARY_EMAIL");
		            input.put("emailAddress", secondaryEmail);
		            input.put("partyId", ownerPartyId);
		            input.put("verified", "Y");
		            input.put("fromDate", UtilDateTime.nowTimestamp());
		            outMap = dispatcher.runSync("createPartyEmailAddress", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("faild service create party Alternate Email:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
				
				
				// create Partyclassification
				try{
					//GenericValue newPartyClassification = delegator.makeValue("PartyClassification");
					//newPartyClassification.set("partyId", ownerPartyId);
					//newPartyClassification.set("partyClassificationGroupId", caste);
					//newPartyClassification.set("fromDate", UtilDateTime.nowTimestamp());
					//Debug.log("ownerPartyId====================",ownerPartyId);
					//Debug.log("caste===================",caste);
					//delegator.create(newPartyClassification);
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating  PartyClassification" + e);	
				}
				
				Timestamp employmentDateStart = null;
				if (UtilValidate.isNotEmpty(employmentDate)){
					try {
						employmentDateTime = UtilDateTime.toTimestamp(sdf.parse(employmentDate));
					} catch (ParseException e) {
					}
					employmentDateStart = UtilDateTime.getDayStart(employmentDateTime);
				}else{
					employmentDateStart =  UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				}
				Timestamp dateOfJoiningStart = null;
				if (UtilValidate.isNotEmpty(dateOfJoining)){
					dateOfJoiningStart = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(dateOfJoining));
				}else{
					dateOfJoiningStart =  UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				}
				
				// Create Employement
				if (UtilValidate.isNotEmpty(ownerPartyId)){
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("partyIdTo", ownerPartyId);
		            input.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
		            input.put("roleTypeIdTo", "EMPLOYEE");
		            input.put("fromDate", employmentDateStart);
		            input.put("partyIdFrom", partyIdFrom);
		            input.put("appointmentDate", dateOfJoiningStart);
		            input.put("locationGeoId", locationGeoId);
		            outMap = dispatcher.runSync("createEmployment", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("faild service create Employee:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
				
				
				//create Department
				if(UtilValidate.isNotEmpty(deprtName)){
					try{
						GenericValue newEntity1 = delegator.makeValue("PartyRelationship");
						newEntity1.set("partyIdFrom",deprtName);
						newEntity1.set("partyIdTo",partyId);
						newEntity1.set("fromDate",employmentDateStart);
						newEntity1.set("roleTypeIdFrom","DEPATMENT_NAME");
						newEntity1.set("roleTypeIdTo","EMPLOYEE");
					    delegator.create(newEntity1);
					}catch (Exception e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError("Error while creating  UnitDetails" + e);	
					}
				}
				

				// Create Employee Position
				if (UtilValidate.isNotEmpty(emplPositionTypeId)){
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("partyId", ownerPartyId);
		            input.put("emplPositionTypeId", emplPositionTypeId);
		            outMap = dispatcher.runSync("createEmplPosition", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("faild service create Employee Position:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
		            String emplPositionId = (String) outMap.get("emplPositionId");
		            // Create Employee Position Fulfillment
					if (UtilValidate.isNotEmpty(emplPositionTypeId)){
			            input.clear();
			            input.put("userLogin", userLogin);
			            input.put("partyId", ownerPartyId);
			            input.put("fromDate", employmentDateStart);
			            input.put("emplPositionId", emplPositionId);
			            outMap = dispatcher.runSync("createEmplPositionFulfillment", input);
			            if(ServiceUtil.isError(outMap)){
			           	 	Debug.logError("faild service create Employee Position Fulfillment:"+ServiceUtil.getErrorMessage(outMap), module);
			           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
			            }
					}
				}
				// Create Employee Detail
				try{
					if(UtilValidate.isNotEmpty(pfNumber)){
						List employeeDetailsList = delegator.findList("EmployeeDetail", EntityCondition.makeCondition("presentEpf",EntityOperator.EQUALS,pfNumber), null, null, null, false);
						if(UtilValidate.isNotEmpty(employeeDetailsList)){
							return ServiceUtil.returnError("PF Number is Already existing, Please Give Your Unique PF Number");
						}
					}
					GenericValue newEntity = delegator.makeValue("EmployeeDetail");
					newEntity.set("partyId", ownerPartyId);
					newEntity.set("employeeId", ownerPartyId);
					newEntity.set("emergencyContactName", emergencyContactName);
					newEntity.set("emergencyContactNumber", emergencyContactNumber);
					newEntity.set("emergencyContactRelationship", emergencyContactRelationship);
					newEntity.set("weeklyOff", weeklyOff);
					newEntity.set("attendanceIndn", attendanceIndn);
					newEntity.set("paymentMode", paymentMode);
					newEntity.set("canteenFacin", canteenFacin);
					newEntity.set("vehicleType", vehicleType);
					newEntity.set("quarterType", quarterType);
					newEntity.set("companyBus", companyBus);
					newEntity.set("busRouteNo", busRouteNo);
					newEntity.set("flexibleShift", flexibleShift);
					newEntity.set("presentEpf", pfNumber);
					if (UtilValidate.isNotEmpty(dateOfJoiningStart)){
						newEntity.set("joinDate", dateOfJoiningStart);
					}else{
						dateOfJoiningStart =  UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
						newEntity.set("joinDate", dateOfJoiningStart);
					}
					newEntity.set("punchType", punchType);
					newEntity.set("shiftType", shiftType);
					newEntity.set("backgroundVerification", backgroundVerification);
					//delegator.setNextSubSeqId(newEntity,"employeeId", 5, 1);
					 newEntity.create();
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating  EmployeeDetail" + e);	
				}
			}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating Employee" + e);	
				}
			  result.put("partyId", ownerPartyId);
		      return result;
	    }
	    
	    public static Map<String, Object> createLanguage(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Map result = ServiceUtil.returnSuccess();
	    	Delegator delegator = ctx.getDelegator();
	    	Locale locale = (Locale) context.get("locale");
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String language = (String)context.get("language");
	        language = language.toUpperCase();
	        if(UtilValidate.isNotEmpty(language)){
	        	try{
	        		GenericValue newEntity = delegator.makeValue("SkillType");
	        		newEntity.set("skillTypeId",language);
	        		newEntity.set("parentTypeId","LANGUAGE");
	        		newEntity.set("description",language);
	        		delegator.create(newEntity);
	        	}catch (GenericEntityException e) {
		            Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
		        }
	        }
	        result = ServiceUtil.returnSuccess("New Laguage has been successfully created");
	        return result;
	    }
	    public static Map<String, Object> createHoliday(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	 
	    	Map result = ServiceUtil.returnSuccess();
	    	Delegator delegator = ctx.getDelegator();
	    	Locale locale = (Locale) context.get("locale");
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        
	        Map<String, Object> inMap = FastMap.newInstance();

	        String customTimePeriodId = (String)context.get("customTimePeriodId");
	        String organizationPartyId = (String)context.get("orgPartyId");
	        String holiDayDateStr = (String)context.get("holidayDate");
	        String stateName = (String)context.get("state");
	        Timestamp holiDayDate = null;
	        String description = (String)context.get("description");
	        String stateNamee=null;    
	        String groupName = null;
	        List RosHoListNames = FastList.newInstance();
	        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			try {
				holiDayDate = UtilDateTime.toTimestamp(formatter.parse(holiDayDateStr));
			} catch (ParseException e) {
			}
			
			try{
				RosHoListNames = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,organizationPartyId) , null, null, null, false );
				if(UtilValidate.isNotEmpty(RosHoListNames)){
					GenericValue RosHoListName = EntityUtil.getFirst(RosHoListNames);
					groupName = RosHoListName.getString("groupName");
				}
				
				GenericValue holList = delegator.findOne("HolidayCalendar", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "organizationPartyId",organizationPartyId, "holiDayDate", UtilDateTime.toTimestamp(holiDayDate)), true);
				GenericValue stateList = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateName), true);
				if(UtilValidate.isNotEmpty(stateList)){
				        stateNamee = stateList.getString("geoName");
						}
				    if(UtilValidate.isEmpty(holList)){
					GenericValue newEntity = delegator.makeValue("HolidayCalendar");
			        if(UtilValidate.isNotEmpty(customTimePeriodId)){
			        	newEntity.set("customTimePeriodId", customTimePeriodId);
			        }
			        if(UtilValidate.isNotEmpty(organizationPartyId)){
			        	newEntity.set("organizationPartyId", organizationPartyId);
			        }
			        if(UtilValidate.isNotEmpty(holiDayDate)){
			        	newEntity.put("holiDayDate", UtilDateTime.toTimestamp(holiDayDate));
			        } 
			        if(UtilValidate.isNotEmpty(description)){
			        	newEntity.set("description", description);
			        }
			        if(UtilValidate.isNotEmpty(groupName)){
			        	newEntity.set("RO", groupName);
			        }
			        try {
			        	if(UtilValidate.isNotEmpty(holiDayDate)){
			        		delegator.create(newEntity); 
			        	}
			        } catch (GenericEntityException e) {
			            Debug.logError(e, module);
			            return ServiceUtil.returnError(e.getMessage());
			        }
			        result = ServiceUtil.returnSuccess("New Holiday has been successfully created");
				}else{
					result = ServiceUtil.returnError("This date already selected as Holiday please select other date");
				}
			}catch (Exception e) {
				/*Debug.logError(e, module);
				return ServiceUtil.returnError("Error while creating  EmployeeDetail" + e);	*/
			}
	       // result.put("batchId", newEntity.get("batchId"));
	        
	        return result;
	        
	    }
	    
	    public static Map<String, Object> updateHoliday(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Map result = ServiceUtil.returnSuccess();
	    	Delegator delegator = ctx.getDelegator();
	    	Locale locale = (Locale) context.get("locale");
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map inMap = FastMap.newInstance();
	        String stateName = (String)context.get("state");
	        String customTimePeriodId = (String)context.get("customTimePeriodId");
	        String organizationPartyId = (String)context.get("organizationPartyId");
	        String holiDayDateStr = (String)context.get("holiDayDate");
	        Timestamp prevholiDayDate = (Timestamp)context.get("prevHoliDayDate");
	        Timestamp holiDayDate = null;
	        String description = (String)context.get("description");
	        String groupName = null;
	       List RosHoListNames = FastList.newInstance();
	        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			try {
				holiDayDate = UtilDateTime.toTimestamp(formatter.parse(holiDayDateStr));
			} catch (ParseException e) {
			}
			try{
				RosHoListNames = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,organizationPartyId) , null, null, null, false );
				if(UtilValidate.isNotEmpty(RosHoListNames)){
					GenericValue RosHoListName = EntityUtil.getFirst(RosHoListNames);
					groupName = RosHoListName.getString("groupName");
				}
				GenericValue holDetails = delegator.findOne("HolidayCalendar", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "organizationPartyId",organizationPartyId, "holiDayDate", prevholiDayDate), false);
				if(UtilValidate.isNotEmpty(holDetails)){
					inMap.put("userLogin", userLogin);
					inMap.put("customTimePeriodId",customTimePeriodId);
					inMap.put("organizationPartyId",organizationPartyId);
					inMap.put("holiDayDate",prevholiDayDate);
					try {
						Map resultValue = dispatcher.runSync("DeleteHoliday", inMap);
						if(ServiceUtil.isError(result)){
							Debug.logError(ServiceUtil.getErrorMessage(resultValue), module);
							return result;
						}
					} catch (GenericServiceException s) {
						Debug.logError("Error while deleting Holiday"+s.getMessage(), module);
					} 
					
					GenericValue newEntity = delegator.makeValue("HolidayCalendar");
					newEntity.set("customTimePeriodId", customTimePeriodId);
					newEntity.set("organizationPartyId", organizationPartyId);
					newEntity.set("holiDayDate", UtilDateTime.toTimestamp(holiDayDate));
					newEntity.set("description", description);
					 if(UtilValidate.isNotEmpty(groupName)){
				        	newEntity.set("RO", groupName);
				        }
					 delegator.create(newEntity); 
					result = ServiceUtil.returnSuccess("Holiday has been successfully updated");
				}else{
					result = ServiceUtil.returnError("Can Not Update Holiday");
				}
			}catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error while updating  holiday" + e);
			}
	        
	        return result;
	        
	    }
	    
	    public static Map<String, Object> createPartyResumeAndContent(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String resumeId = (String) context.get("resumeId");
	    	String contentId = null;
	    	String partyId = (String) context.get("partyId");
	    	String resumeText = (String) context.get("resumeText");
	    	String dataResourceTypeId = "IMAGE_OBJECT";
	    	String contentTypeId = "SR_DOCUMENT";
	    	ByteBuffer uploadedFile = (ByteBuffer) context.get("uploadedFile");
	    	ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
	    	
	    	String dataResourceId = null;
	    	Timestamp resumeDate = (Timestamp) context.get("resumeDate");
	    	if(UtilValidate.isEmpty(resumeDate)){
	    		resumeDate = UtilDateTime.nowTimestamp();
	    	}
	    	Timestamp resumeDateStart = UtilDateTime.getDayStart(resumeDate);
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			try {
				  Map inputMap = FastMap.newInstance();
				  inputMap.put("partyContentTypeId", "INTERNAL");
				  inputMap.put("dataResourceTypeId", dataResourceTypeId);
				  inputMap.put("contentTypeId", contentTypeId);
				  inputMap.put("statusId", "CTNT_AVAILABLE");
				  inputMap.put("dataCategoryId", "SR_DOCUMENT");
				  inputMap.put("isPublic", "N");
				  inputMap.put("partyId", partyId);
				  inputMap.put("_uploadedFile_fileName", resumeText);
				  inputMap.put("_uploadedFile_contentType", contentTypeId);
				  inputMap.put("uploadedFile", fileBytes);
				  inputMap.put("userLogin", userLogin);
				  try{
					  Map contentCtx = dispatcher.runSync("uploadPartyContentFile", inputMap);
			  		  if(ServiceUtil.isError(contentCtx)){
			  			  Debug.logError("Error while creating Party Content", module);
			  			  return ServiceUtil.returnError("Error while creating Party Content");
			  		  }
			  		  contentId = (String) contentCtx.get("contentId");
				  }catch (Exception e) {
		    		 Debug.logError(e, module);
		             return ServiceUtil.returnError("Error while creating Party Resume" + e);
				  } 
		  		  if(UtilValidate.isNotEmpty(contentId)){
		  			  inputMap.clear();
		  			  inputMap.put("userLogin", userLogin);
					  inputMap.put("resumeId", resumeId);
					  inputMap.put("contentId", contentId);
					  inputMap.put("partyId", partyId);
					  inputMap.put("resumeText", resumeText);
					  inputMap.put("resumeDate", resumeDate);
					  try {
						  Map resultCtx = dispatcher.runSync("createPartyResume", inputMap);
				  		  if(ServiceUtil.isError(resultCtx)){
				  			  Debug.logError("Error while creating Party Resume", module);
				  			  return ServiceUtil.returnError("Error while creating Party Resume");
				  		  }
					  }catch (Exception e) {
			    		 Debug.logError(e, module);
			             return ServiceUtil.returnError("Error while creating Party Resume" + e);
					  } 
		  		  }
	        }catch(Exception e){
				Debug.logError("Error while creating Party Resume"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("Party Resume created sucessfully...!");
	        return result;
	    }
    public static Map<String, Object> createOrUpdateWeeklyOffCalendar(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String partyId = (String) context.get("partyId");
    	String weeklyOff = (String) context.get("weeklyOff");
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	if(UtilValidate.isEmpty(fromDate)){
    		fromDate = UtilDateTime.nowTimestamp();
    	}
    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
    	Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			GenericValue employeeDetail = delegator.findOne("EmployeeDetail",UtilMisc.toMap("partyId",partyId),false);
			if(UtilValidate.isNotEmpty(employeeDetail)){
				String existingWeeklyOff = (String) employeeDetail.get("weeklyOff");
				if(!weeklyOff.equals(existingWeeklyOff)){
					List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS ,null));
		            EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);   
		            List<GenericValue> activeEmpWeeklyOffCalList = delegator.findList("EmployeeWeeklyOffCalendar", condition, null, UtilMisc.toList("-fromDate"), null, false);
		            if(UtilValidate.isNotEmpty(activeEmpWeeklyOffCalList)){
		            	GenericValue activeEmpWeeklyOffCalendar = EntityUtil.getFirst(activeEmpWeeklyOffCalList);
	            		activeEmpWeeklyOffCalendar.set("thruDate", previousDayEnd);
	            		activeEmpWeeklyOffCalendar.set("lastModifiedDate", UtilDateTime.nowTimestamp());
	            		activeEmpWeeklyOffCalendar.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            		Timestamp oldFromDate = activeEmpWeeklyOffCalendar.getTimestamp("fromDate");
	            		String oldFromDateReq = UtilDateTime.toDateString(oldFromDate,"dd/MM/yyyy");
						if(oldFromDate.compareTo(fromDateStart)>= 0){
							return ServiceUtil.returnError("Weekly off mention date cannot be before "+oldFromDateReq);
						}else{
							activeEmpWeeklyOffCalendar.store();
							GenericValue newEntity = delegator.makeValue("EmployeeWeeklyOffCalendar");
							newEntity.set("partyId", partyId);
							newEntity.set("weeklyOffDay", weeklyOff);
							newEntity.set("fromDate", fromDateStart);
							newEntity.set("createdDate", UtilDateTime.nowTimestamp());
							newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
							newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
							newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
							newEntity.create();
						}
					}else{
						GenericValue newEntity = delegator.makeValue("EmployeeWeeklyOffCalendar");
						newEntity.set("partyId", partyId);
						newEntity.set("weeklyOffDay", weeklyOff);
						newEntity.set("fromDate", fromDateStart);
						newEntity.set("createdDate", UtilDateTime.nowTimestamp());
						newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
						newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
						newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						newEntity.create();
					}
				}
			}
        }catch(GenericEntityException e){
			Debug.logError("Error while populating employee weekly off calendar"+e.getMessage(), module);
		}
        result = ServiceUtil.returnSuccess("uploaded successfully...!");
        return result;
    }
    public static Map<String, Object> populateWeeklyOffCalendar(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String orgPartyId =  (String)context.get("orgPartyId");
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	if(UtilValidate.isEmpty(fromDate)){
    		fromDate = UtilDateTime.nowTimestamp();
    	}
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	if(UtilValidate.isEmpty(thruDate)){
    		thruDate = UtilDateTime.nowTimestamp();
    	}
    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
    	Timestamp thruDateEnd = UtilDateTime.getDayEnd(thruDate);
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			Map emplInputMap = FastMap.newInstance();
			emplInputMap.put("userLogin", userLogin);
			emplInputMap.put("orgPartyId", orgPartyId);
			emplInputMap.put("fromDate", fromDateStart);
			emplInputMap.put("thruDate", thruDateEnd);
        	Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
        	List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
        	for (int i = 0; i < employementList.size(); ++i) {		
        		GenericValue employment = employementList.get(i);
        		String partyId = employment.getString("partyIdTo");
        		GenericValue employeeDetail = delegator.findOne("EmployeeDetail",UtilMisc.toMap("partyId",partyId),false);
    			if(UtilValidate.isNotEmpty(employeeDetail)){
    				String weeklyOff = (String) employeeDetail.get("weeklyOff");
    				if(UtilValidate.isNotEmpty(weeklyOff)){
    					GenericValue newEntity = delegator.makeValue("EmployeeWeeklyOffCalendar");
    					newEntity.set("partyId", partyId);
    					newEntity.set("weeklyOffDay", weeklyOff);
    					newEntity.set("fromDate", fromDateStart);
    					newEntity.set("createdDate", UtilDateTime.nowTimestamp());
    					newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
    					newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
    					newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
    					newEntity.create();
    				}
    			}
        	}
        }catch(GenericEntityException e){
			Debug.logError("Error while populating employee weekly off calendar"+e.getMessage(), module);
		}
        result = ServiceUtil.returnSuccess("Employee weekly off calendar populated...!");
        return result;
    }
    public static Map<String, Object> updateEmployeePosition(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> inMap = FastMap.newInstance();
        String emplPositionId = (String)context.get("emplPositionId");
        String emplPositionTypeId = (String)context.get("emplPositionTypeId");
        String partyId = (String)context.get("partyId");
        String fromDate = (String) context.get("actualFromDate");
        Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		Timestamp fromDateStart = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");	
		try {
    		fromDateStart = UtilDateTime.toTimestamp(sdf.parse(fromDate));
		} catch (ParseException e) {
		}
		
		 Timestamp timePeriodStart = UtilDateTime.getDayStart(fromDateStart);
		 Timestamp prevDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(timePeriodStart, -1));
		 
        try{
			List designationconditionList = FastList.newInstance();
			designationconditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
			//designationconditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"EMPL_POS_ACTIVE"));
			designationconditionList.add(EntityCondition.makeCondition("emplPositionId",EntityOperator.EQUALS,emplPositionId));
			EntityCondition designationcondition = EntityCondition.makeCondition(designationconditionList,EntityOperator.AND);
	    	List<GenericValue> emplPositionAndFulfillments = delegator.findList("EmplPositionFulfillment", designationcondition, null, null, null, false);
			
        	if(UtilValidate.isNotEmpty(emplPositionAndFulfillments)){
        		GenericValue emplPositionFulfillments = EntityUtil.getFirst(emplPositionAndFulfillments);
    			emplPositionFulfillments.set("thruDate",prevDayEnd);
    			emplPositionFulfillments.store();
        		
        		if (UtilValidate.isNotEmpty(emplPositionTypeId)){
        			input.put("userLogin", userLogin);
    	            input.put("partyId", partyId);
    	            input.put("actualFromDate",timePeriodStart);
    	            input.put("emplPositionTypeId", emplPositionTypeId);
    	            input.put("statusId", "EMPL_POS_ACTIVE");
    	            outMap = dispatcher.runSync("createEmplPosition", input);
    	            if(ServiceUtil.isError(outMap)){
    	           	 	Debug.logError("faild service create Employee Position:"+ServiceUtil.getErrorMessage(outMap), module);
    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    	            }
    	            if (UtilValidate.isNotEmpty(emplPositionTypeId)){
    	            	String newEmplPositionId = (String)outMap.get("emplPositionId");
    	            	if (UtilValidate.isNotEmpty(newEmplPositionId)){
    	            		input.clear();
    	            		input.put("userLogin", userLogin);
    			            input.put("partyId", partyId);
    			            input.put("fromDate",timePeriodStart);
    			            input.put("emplPositionId", newEmplPositionId);
    			            outMap = dispatcher.runSync("createEmplPositionFulfillment", input);
    			            if(ServiceUtil.isError(outMap)){
    			           	 	Debug.logError("faild service create Employee Position Fulfillment:"+ServiceUtil.getErrorMessage(outMap), module);
    			           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
    			            }
    	            	}
    	            }
				}
        		result = ServiceUtil.returnSuccess("Employee position Updated successfully.");
        	}else{
        		result = ServiceUtil.returnError("This Designation already exists for employee");
        	}
        }catch (Exception e) {
			/*Debug.logError(e, module);
			return ServiceUtil.returnError("Error while creating  EmployeeDetail" + e);	*/
		}
       return result;
    }
    public static Map<String, Object> updateEmployeeJoiningDate(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> inMap = FastMap.newInstance();
        GenericValue updateEmployeeDetail = null;
        String partyId = (String)context.get("partyId");
        String oldJoiningDate = (String)context.get("oldJoiningDate");
        String newJoiningDate = (String) context.get("newJoiningDate");
        
        Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		
		Timestamp fromDateStart = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");	
		try {
    		fromDateStart = UtilDateTime.toTimestamp(sdf.parse(newJoiningDate));
		} catch (ParseException e) {
		}
		
		Timestamp oldJoiningDateTs = null;
		SimpleDateFormat   sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		try {
			oldJoiningDateTs = (Timestamp)UtilDateTime.toTimestamp(sdformat.parse(oldJoiningDate));
		} catch (ParseException e) {
		}
		try {
			updateEmployeeDetail = delegator.findOne("EmployeeDetail",UtilMisc.toMap("partyId", partyId), false);
			if(UtilValidate.isNotEmpty(updateEmployeeDetail)){
				if(UtilValidate.isNotEmpty(newJoiningDate)){
					updateEmployeeDetail.set("joinDate", fromDateStart);
				}
				updateEmployeeDetail.set("partyId", partyId);
				updateEmployeeDetail.set("joinDate", fromDateStart);
				updateEmployeeDetail.store();
				delegator.createOrStore(updateEmployeeDetail);
			}
		}catch(GenericEntityException e){
			Debug.logError("Error while updating joining date"+e.getMessage(), module);
		}	
	     return result;
    }
    public static Map<String, Object> updateEmployeeAppointmentDate(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> inMap = FastMap.newInstance();
        String partyId = (String)context.get("partyId");
        String oldAppointmentDate = (String)context.get("oldAppointmentDate");
        String newAppointmentDate = (String) context.get("newAppointmentDate");
        Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		
		Timestamp fromDateStart = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");	
		try {
    		fromDateStart = UtilDateTime.toTimestamp(sdf.parse(newAppointmentDate));
		} catch (ParseException e) {
		}
		Timestamp oldAppointmentDateTs = null;
		SimpleDateFormat   sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		try {
			oldAppointmentDateTs = (Timestamp)UtilDateTime.toTimestamp(sdformat.parse(oldAppointmentDate));
		} catch (ParseException e) {
		}
		List conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
        conditionList.add(EntityCondition.makeCondition("appointmentDate", EntityOperator.EQUALS, oldAppointmentDateTs));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        GenericValue employment = null;
        try {
        	 List<GenericValue> availableEmployment = delegator.findList("Employment", condition, null, null, null, false);
        	 employment = EntityUtil.getFirst(availableEmployment);
		} catch (GenericEntityException e) {
			Debug.logError("Unable to Fetch Employment"+e, module);
    		return ServiceUtil.returnError("Unable to Fetch Employment"); 
		}
        String roleTypeIdFrom = (String) employment.get("roleTypeIdFrom");
        String roleTypeIdTo = (String) employment.get("roleTypeIdTo");
        String partyIdFrom = (String) employment.get("partyIdFrom");
        Timestamp joiningDate = (Timestamp) employment.get("fromDate");
        String locationGeoId = (String) employment.get("locationGeoId");
        String stateGeoId = (String) employment.get("stateGeoId");
        Timestamp reportingDate = (Timestamp) employment.get("reportingDate");
        Timestamp resignationDate = (Timestamp) employment.get("resignationDate");
        String terminationReasonId = (String) employment.get("terminationReasonId");
        String terminationTypeId = (String) employment.get("terminationTypeId");
        
        Map deleteEmploymentMap = FastMap.newInstance();
        deleteEmploymentMap.put("userLogin",userLogin);
        deleteEmploymentMap.put("roleTypeIdFrom",roleTypeIdFrom);
        deleteEmploymentMap.put("partyIdTo",partyId);
        deleteEmploymentMap.put("partyIdFrom",partyIdFrom);
        deleteEmploymentMap.put("roleTypeIdTo",roleTypeIdTo);
        deleteEmploymentMap.put("appointmentDate",oldAppointmentDate);
        deleteEmploymentMap.put("fromDate",joiningDate);
		try {
			Map resultValue = dispatcher.runSync("deleteEmployment", deleteEmploymentMap);
			if(ServiceUtil.isError(result)){
				Debug.logError(ServiceUtil.getErrorMessage(resultValue), module);
				return result;
			}
		} catch (GenericServiceException s) {
			Debug.logError("Error while deleting Employment"+s.getMessage(), module);
		} 
        
        Map createEmploymentMap = FastMap.newInstance();
        createEmploymentMap.put("userLogin",userLogin);
        createEmploymentMap.put("roleTypeIdFrom",roleTypeIdFrom);
        createEmploymentMap.put("partyIdTo",partyId);
        createEmploymentMap.put("partyIdFrom",partyIdFrom);
        createEmploymentMap.put("roleTypeIdTo",roleTypeIdTo);
        createEmploymentMap.put("fromDate",joiningDate);
        createEmploymentMap.put("appointmentDate",fromDateStart);
        createEmploymentMap.put("locationGeoId",locationGeoId);
        createEmploymentMap.put("stateGeoId",stateGeoId);
        createEmploymentMap.put("reportingDate",reportingDate);
        createEmploymentMap.put("resignationDate",resignationDate);
        createEmploymentMap.put("terminationReasonId",terminationReasonId);
        createEmploymentMap.put("terminationTypeId",terminationTypeId);
        try {
			Map resultValue = dispatcher.runSync("createEmployment", createEmploymentMap);
			if(ServiceUtil.isError(result)){
				Debug.logError(ServiceUtil.getErrorMessage(resultValue), module);
				return result;
			}
		} catch (GenericServiceException s) {
			Debug.logError("Error while creating Employment"+s.getMessage(), module);
		} 
		return result;
    }
    
    public static Map<String, Object> makeEmployeeAppraisalPromotion(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> inMap = FastMap.newInstance();
        String employeeId = (String)context.get("employeeId");
        String PerfRatingType = (String)context.get("PerfRatingType");
        String fromDateStr = (String)context.get("fromDate");
        String thruDateStr = (String)context.get("thruDate");
        String emplPositionTypeId = (String) context.get("Promotion");
        String dateOfPromotionStr = (String) context.get("dateOfPromotion");
        String dateOfConfirmationStr = (String) context.get("dateOfConfirmation");
        Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		Timestamp dateOfPromotion = null;
		Timestamp dateOfConfirmation = null;
		try{
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
			 if(UtilValidate.isNotEmpty(fromDateStr)){
				try {
		    		fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(fromDateStr)));
				} catch (ParseException e) {
				}
			 }
			 if(UtilValidate.isNotEmpty(thruDateStr)){
					try {
						thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(thruDateStr)));
					} catch (ParseException e) {
					}
			}
			 if(UtilValidate.isNotEmpty(dateOfPromotionStr)){
					try {
						dateOfPromotion = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(dateOfPromotionStr)));
					} catch (ParseException e) {
					}
			}
			 if(UtilValidate.isNotEmpty(dateOfConfirmationStr)){
					try {
						dateOfConfirmation = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(dateOfConfirmationStr)));
					} catch (ParseException e) {
					}
			}
			
			// Create Employee Position  
			String emplPositionId = null;
			if (UtilValidate.isNotEmpty(emplPositionTypeId)){
	            input.clear();
	            input.put("userLogin", userLogin);
	            input.put("partyId", employeeId);
	            input.put("emplPositionTypeId", emplPositionTypeId);
	            outMap = dispatcher.runSync("createEmplPosition", input);
	            if(ServiceUtil.isError(outMap)){
	           	 	Debug.logError("faild service create Employee Position:"+ServiceUtil.getErrorMessage(outMap), module);
	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
	            }
	            emplPositionId = (String) outMap.get("emplPositionId");
	            // Create Employee Position Fulfillment
				if (UtilValidate.isNotEmpty(emplPositionTypeId)){
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("partyId", employeeId);
		            input.put("fromDate", dateOfConfirmation);
		            input.put("emplPositionId", emplPositionId);
		            outMap = dispatcher.runSync("createEmplPositionFulfillment", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("faild service create Employee Position Fulfillment:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
		            }
				}
			}
			
			
	 		 if(UtilValidate.isNotEmpty(employeeId)){
	 			  GenericValue perfReview = delegator.makeValue("PerfReview");
	 			  perfReview.set("employeePartyId", employeeId);
	 			  perfReview.set("employeeRoleTypeId", "EMPLOYEE");
	 			 if(UtilValidate.isNotEmpty(emplPositionId)){
	 				 perfReview.set("emplPositionId", emplPositionId);
	 			 }
	 			 perfReview.set("fromDate", fromDate);
	 			 perfReview.set("thruDate", thruDate);
	 			 delegator.setNextSubSeqId(perfReview, "perfReviewId", 5, 1);
				 delegator.create(perfReview);
	 			//creating review item here
	 			String perfReviewId  = (String) perfReview.get("perfReviewId");
	 			if(UtilValidate.isNotEmpty(perfReviewId)){  	 
	 				GenericValue perfReviewItem = delegator.makeValue("PerfReviewItem");
				  	perfReviewItem.set("employeePartyId", employeeId);
				  	perfReviewItem.set("employeeRoleTypeId", "EMPLOYEE");
				  	perfReviewItem.set("perfReviewId", perfReviewId);
				  	perfReviewItem.set("perfReviewItemTypeId", "GRADING");
				  	perfReviewItem.set("perfRatingTypeId", PerfRatingType);
				  	perfReviewItem.set("PromotionDate", dateOfPromotion);
				  	perfReviewItem.set("ConfirmationDate", dateOfConfirmation);
				  	delegator.setNextSubSeqId(perfReviewItem, "perfReviewItemSeqId", 5, 1);
				  	delegator.create(perfReviewItem);
	 			}
			result = ServiceUtil.returnSuccess("Employee Performance Dating Created successfully.");
			
	    }
	} catch (Exception e) {
		Debug.logError("Error while creating Performance Rating", module);
	  }
		return result;	
    }
    
    public static Map<String, Object> UpdatePerformanceRating(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> inMap = FastMap.newInstance();
        String employeeId = (String)context.get("partyId");
        String PerfRatingType = (String)context.get("PerfRatingType");
        String perfReviewId = (String)context.get("perfReviewId");
        String perfReviewItemSeqId = (String)context.get("perfReviewItemSeqId");
        String fromDateStr = (String)context.get("fromDate");
        String thruDateStr = (String)context.get("thruDate");
        String emplPositionTypeId = (String) context.get("promotion");
        String dateOfPromotionStr = (String) context.get("PromotionDate");
        String dateOfConfirmationStr = (String) context.get("ConfirmationDate");
        Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		GenericValue perfReviewDetails = null;
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		Timestamp dateOfPromotion = null;
		Timestamp dateOfConfirmation = null;
		try{
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
			 if(UtilValidate.isNotEmpty(fromDateStr)){
				try {
		    		fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(fromDateStr)));
				} catch (ParseException e) {
				}
			 }
			 if(UtilValidate.isNotEmpty(thruDateStr)){
					try {
						thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(thruDateStr)));
					} catch (ParseException e) {
					}
			}
			 if(UtilValidate.isNotEmpty(dateOfPromotionStr)){
					try {
						dateOfPromotion = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(dateOfPromotionStr)));
					} catch (ParseException e) {
					}
			}
			 if(UtilValidate.isNotEmpty(dateOfConfirmationStr)){
					try {
						dateOfConfirmation = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(dateOfConfirmationStr)));
					} catch (ParseException e) {
					}
			}
			
			Timestamp prevDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dateOfConfirmation, -1));
			 if(UtilValidate.isNotEmpty(perfReviewId)){
				 perfReviewDetails =delegator.findOne("PerfReview", UtilMisc.toMap("employeePartyId", employeeId, "employeeRoleTypeId", "EMPLOYEE", "perfReviewId", perfReviewId), false);
				 if(UtilValidate.isNotEmpty(perfReviewDetails)){
					 String emplPositionId = (String)perfReviewDetails.get("emplPositionId");
					 List designationconditionList = FastList.newInstance();
						designationconditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employeeId));
						designationconditionList.add(EntityCondition.makeCondition("emplPositionId",EntityOperator.EQUALS,emplPositionId));
						EntityCondition designationcondition = EntityCondition.makeCondition(designationconditionList,EntityOperator.AND);
				    	List<GenericValue> emplPositionAndFulfillments = delegator.findList("EmplPositionFulfillment", designationcondition, null, null, null, false);
						
			        	if(UtilValidate.isNotEmpty(emplPositionAndFulfillments)){
			        		GenericValue emplPositionFulfillments = EntityUtil.getFirst(emplPositionAndFulfillments);
			    			emplPositionFulfillments.set("thruDate",prevDayEnd);
			    			emplPositionFulfillments.store();
			        		
			        		if (UtilValidate.isNotEmpty(emplPositionTypeId)){
			        			input.put("userLogin", userLogin);
			    	            input.put("partyId", employeeId);
			    	            input.put("actualFromDate",dateOfConfirmation);
			    	            input.put("emplPositionTypeId", emplPositionTypeId);
			    	            input.put("statusId", "EMPL_POS_ACTIVE");
			    	            outMap = dispatcher.runSync("createEmplPosition", input);
			    	            if(ServiceUtil.isError(outMap)){
			    	           	 	Debug.logError("faild service create Employee Position:"+ServiceUtil.getErrorMessage(outMap), module);
			    	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
			    	            }
			    	            
			    	            	String newEmplPositionId = (String)outMap.get("emplPositionId");
			    	            	 perfReviewDetails.set("emplPositionId",newEmplPositionId);
			    	            	 perfReviewDetails.store();
			    	            	if (UtilValidate.isNotEmpty(newEmplPositionId)){
			    	            		input.clear();
			    	            		input.put("userLogin", userLogin);
			    			            input.put("partyId", employeeId);
			    			            input.put("fromDate",dateOfConfirmation);
			    			            input.put("emplPositionId", newEmplPositionId);
			    			            outMap = dispatcher.runSync("createEmplPositionFulfillment", input);
			    			            if(ServiceUtil.isError(outMap)){
			    			           	 	Debug.logError("faild service create Employee Position Fulfillment:"+ServiceUtil.getErrorMessage(outMap), module);
			    			           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
			    			            }
			    	            	}
			    	           
							}
			        		
			        		
			        	}
			        	
			        	GenericValue perfReviewItemDetails =delegator.findOne("PerfReviewItem", UtilMisc.toMap("employeePartyId", employeeId, "employeeRoleTypeId", "EMPLOYEE", "perfReviewId", perfReviewId, "perfReviewItemSeqId", perfReviewItemSeqId), false);
		  	  			if (UtilValidate.isNotEmpty(perfReviewItemDetails)){
		  	  				perfReviewItemDetails.set("perfRatingTypeId",PerfRatingType);
		  	  				perfReviewItemDetails.set("PromotionDate",dateOfPromotion);
		  	  				perfReviewItemDetails.set("ConfirmationDate",dateOfConfirmation);
		  	  				perfReviewItemDetails.store();
		  	  			}
		  	  			
			 }
			 
			 }	
			 result = ServiceUtil.returnSuccess("Employee Performance Rating Updated successfully.");
		}catch (Exception e) {
			Debug.logError("Error while Updating Performance Rating", module);
		  }
			return result;	
    }
    public static Map<String, Object> createNewEmplTraining(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	//String partyId = (String) context.get("partyId");
    	String nameOfInstitute = (String) context.get("nameOfInstitute");
    	String fromDateStr = (String) context.get("fromDate");
    	String thruDateStr = (String) context.get("thruDate");
    	String topicsCoverd = (String) context.get("topicsCoverd");
    	BigDecimal traingCost = (BigDecimal) context.get("traingCost");
    	String trainingLocation = (String) context.get("trainingLocation");
    	String duration = (String) context.get("duration");
    	String trgCategory = (String) context.get("trgCategory");
    	String facultyType = (String) context.get("facultyType");
   		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Timestamp fromDate=null;
		Timestamp thruDate=null;
		FastList partyIdsList = (FastList)context.get("partyIdList");
    	String partyIds[] = ((String)partyIdsList.get(0)).split(",");
		try {
		    if(UtilValidate.isNotEmpty(fromDateStr)){
				fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(fromDateStr)));
			}
		}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ fromDateStr, module);
			} 
		try {
		    if(UtilValidate.isNotEmpty(thruDateStr)){
		    	thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(thruDateStr)));
			}
		}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ thruDateStr, module);
			} 
		

		try {
			List TrainingList = FastList.newInstance();
			 if(UtilValidate.isNotEmpty(partyIdsList)){
				 for(int i=0;i<partyIds.length;i++){
					 String partyId = (String)partyIds[i];
					if(UtilValidate.isNotEmpty(partyId)){
						TrainingList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
					}
					if(UtilValidate.isNotEmpty(topicsCoverd)){
						TrainingList.add(EntityCondition.makeCondition("topicsCoverd",EntityOperator.EQUALS,topicsCoverd));
					}
			    	EntityCondition condition = EntityCondition.makeCondition(TrainingList,EntityOperator.AND);
			    	List<GenericValue> PersonTrainingList = delegator.findList("PersonTraining", condition, null, null, null, false);
			    	PersonTrainingList = EntityUtil.orderBy(PersonTrainingList,UtilMisc.toList("-createdStamp"));
			    	if(UtilValidate.isEmpty(PersonTrainingList)){
			    		GenericValue PersonTraining = delegator.makeValue("PersonTraining");
			    		if(UtilValidate.isNotEmpty(partyId)){
			    			PersonTraining.set("partyId", partyId);
			    		}
			    		if(UtilValidate.isNotEmpty(nameOfInstitute)){
			    			PersonTraining.set("nameOfInstitute", nameOfInstitute);
			    		}
			    		if(UtilValidate.isNotEmpty(fromDate)){
			    			PersonTraining.set("fromDate", fromDate);
			    		}
			    		if(UtilValidate.isNotEmpty(thruDate)){
			    			PersonTraining.set("thruDate", thruDate);
			    		}
			    		if(UtilValidate.isNotEmpty(topicsCoverd)){
			    			PersonTraining.set("topicsCoverd", topicsCoverd);
			    		}
			    		if(UtilValidate.isNotEmpty(traingCost)){
			    			PersonTraining.set("traingCost", traingCost);
			    		}
			    		if(UtilValidate.isNotEmpty(trainingLocation)){
			    			PersonTraining.set("trainingLocation", trainingLocation);
			    		}
			    		if(UtilValidate.isNotEmpty(duration)){
			    			PersonTraining.set("duration", duration);
			    		}
			    		if(UtilValidate.isNotEmpty(trgCategory)){
			    			PersonTraining.set("trgCategory", trgCategory);
			    		}
			    		if(UtilValidate.isNotEmpty(facultyType)){
			    			PersonTraining.set("facultyType", facultyType);
			    		}
			  	        delegator.createSetNextSeqId(PersonTraining);
			  	       String trainingRequestId=(String)PersonTraining.get("trainingRequestId");
			    	  }
				 }
			 }
		}catch(GenericEntityException e){
			Debug.logError("Error while creating PersonTraining"+e.getMessage(), module);
		}
		result = ServiceUtil.returnSuccess("PersonTraining Created Sucessfully for Employees ");
		return result;

    }
    public static Map<String, Object> UpdateTraining(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String partyId = (String) context.get("partyId");
    	String nameOfInstitute = (String) context.get("nameOfInstitute");
    	String trainingRequestId = (String) context.get("trainingRequestId");
    	String fromDateStr = (String) context.get("fromDate");
    	String thruDateStr = (String) context.get("thruDate");
    	String topicsCoverd = (String) context.get("topicsCoverd");
    	BigDecimal traingCost = (BigDecimal) context.get("traingCost");
    	String trainingLocation = (String) context.get("trainingLocation");
    	String duration = (String) context.get("duration");
    	String trgCategory = (String) context.get("trgCategory");
    	String facultyType = (String) context.get("facultyType");
   		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Timestamp fromDate=null;
		Timestamp thruDate=null;
		try {
		    if(UtilValidate.isNotEmpty(fromDateStr)){
				fromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(sdf.parse(fromDateStr)));
			}
		}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ fromDateStr, module);
			} 
		try {
		    if(UtilValidate.isNotEmpty(thruDateStr)){
		    	thruDate = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(sdf.parse(thruDateStr)));
			}
		}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ thruDateStr, module);
			} 
		

		try {
				List conditionList = FastList.newInstance();
				if(UtilValidate.isNotEmpty(topicsCoverd)){
					conditionList.add(EntityCondition.makeCondition("topicsCoverd",EntityOperator.EQUALS,topicsCoverd));
				}
				if(UtilValidate.isNotEmpty(trainingLocation)){
					conditionList.add(EntityCondition.makeCondition("trainingLocation",EntityOperator.EQUALS,trainingLocation));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    	List<GenericValue> PersonTrainingList = delegator.findList("PersonTraining", condition, null, null, null, false);
				if(UtilValidate.isNotEmpty(PersonTrainingList)){
		    	for(int i=0;i<PersonTrainingList.size();i++){
					GenericValue personTrainingDetail = PersonTrainingList.get(i);
					personTrainingDetail.set("nameOfInstitute",nameOfInstitute);
					personTrainingDetail.set("fromDate",fromDate);
					personTrainingDetail.set("thruDate",thruDate);
					personTrainingDetail.set("topicsCoverd",topicsCoverd);
					personTrainingDetail.set("traingCost",traingCost);
					personTrainingDetail.set("trainingLocation",trainingLocation);
					personTrainingDetail.set("duration",duration);
					personTrainingDetail.set("trgCategory",trgCategory);
					personTrainingDetail.set("facultyType",facultyType);
					personTrainingDetail.store();
				}
			
		    }
		}catch(GenericEntityException e){
			Debug.logError("Error while creating PersonTraining"+e.getMessage(), module);
		}
		result = ServiceUtil.returnSuccess("PersonsTraining Updated Sucessfully for Employees ");
		return result;

    }
    
    
    
}