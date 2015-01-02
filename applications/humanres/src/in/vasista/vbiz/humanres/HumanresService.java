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
				GenericValue tempEmployement = EntityUtil.getFirst(EntityUtil.filterByAnd(employementList,UtilMisc.toMap("partyIdTo",employeeId)));
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
			    			Timestamp cTimeEnd = UtilDateTime.getDayEnd(cTime);
			    			String weekName = (c1.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
			    			List<GenericValue> dayShiftList = EntityUtil.filterByCondition(emplDailyAttendanceDetailList, EntityCondition.makeCondition("date",EntityOperator.EQUALS,UtilDateTime.toSqlDate(cTime)));
			    			
			    			if((weekName.equalsIgnoreCase(emplWeeklyOffDay) && UtilValidate.isNotEmpty(dayShiftList)) || (dayShiftList.size() >= 2)){
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
							Map punMap = PunchService.emplDailyPunchReport(dctx, UtilMisc.toMap("partyId", partyId ,"punchDate",tempDate));
							if(UtilValidate.isNotEmpty(punMap.get("punchDataList"))){
								Map punchDetails = (Map)(((List)punMap.get("punchDataList")).get(0));
								if(UtilValidate.isNotEmpty(punchDetails)){
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
	    	String loanTypeId = (String) context.get("loanTypeId");
	    	String statusId = (String)context.get("statusId");
	    	String description=(String)context.get("description");
	    	String extLoanRefNum=(String)context.get("extLoanRefNum");
	    	Date disbDate =  (Date)context.get("disbDate");
	    	BigDecimal principalAmount = (BigDecimal)context.get("principalAmount");
	    	BigDecimal interestAmount = (BigDecimal)context.get("interestAmount");
	    	Long numInterestInst = (Long)context.get("numInterestInst");
	    	Long numPrincipalInst = (Long)context.get("numPrincipalInst");
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	Timestamp disbDateTime = UtilDateTime.toTimestamp(disbDate);
	    	Timestamp disbDateStart = UtilDateTime.getDayStart(disbDateTime);
	    	Timestamp disbDateEnd = UtilDateTime.getDayEnd(disbDateTime);
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			String payHeadTypeId = null;
			String partyIdFrom = null;
			String customTimePeriodId = null;
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
				GenericValue LoanTypeDetails=null;
				String finAccountTypeId="";
				LoanTypeDetails =delegator.findOne("LoanType", UtilMisc.toMap("loanTypeId", loanTypeId), false);
				if(UtilValidate.isNotEmpty(LoanTypeDetails)){
					finAccountTypeId=LoanTypeDetails.getString("finAccountTypeId");
				}	
				String finAccountId=null;
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
		 			delegator.createSetNextSeqId(finAccount);
		 			if(UtilValidate.isNotEmpty(finAccount)){
		 				finAccountId=finAccount.getString("finAccountId");
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
				loan.set("extLoanRefNum", extLoanRefNum);
				loan.set("principalAmount", principalAmount);
				loan.set("interestAmount", interestAmount);
				loan.set("numInterestInst", numInterestInst);
				loan.set("numPrincipalInst", numPrincipalInst);
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
			if(UtilValidate.isEmpty(setlDateEnd)){
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
			}
			try {
				loanDetails = delegator.findOne("Loan",UtilMisc.toMap("loanId", loanId), false);
				if(UtilValidate.isNotEmpty(loanDetails)){
					if(UtilValidate.isNotEmpty(setlDateEnd)){
						loanDetails.set("setlDate", setlDateEnd);
					}
					if(UtilValidate.isNotEmpty(statusId)){
						loanDetails.set("statusId", statusId);
					}
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
	 
	 	
	 public static Map<String, Object> createNewEmployment(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String partyIdFrom = (String) context.get("partyIdFrom");
	    	String partyIdTo = (String) context.get("partyIdTo");
	    	String fromDateStr = (String) context.get("fromDate");
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
			}
	    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
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
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,fromDateStart));
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS ,null));
		    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
				List<GenericValue> activeEmploymentList = delegator.findList("Employment", condition, null, UtilMisc.toList("-fromDate"), null, false);
				if(UtilValidate.isNotEmpty(activeEmploymentList)){
					GenericValue activeEmployment = EntityUtil.getFirst(activeEmploymentList);
					appointmentDate = activeEmployment.getTimestamp("appointmentDate");
					locationGeoId = activeEmployment.getString("locationGeoId");
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
				}else{
					return ServiceUtil.returnError("Department already exists.....!");
				}
	        }catch(GenericEntityException e){
				Debug.logError("Error while creating new Employment"+e.getMessage(), module);
			}
	        result = ServiceUtil.returnSuccess("New Employment Created Sucessfully...!");
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
	    	  List<GenericValue> finAccountList = delegator.findList("FinAccount", condition, null, null, null, false);
	    		  if(UtilValidate.isEmpty(finAccountList)){
						GenericValue newEntity = delegator.makeValue("FinAccount");
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
	    	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
	      result = ServiceUtil.returnSuccess("Success");
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
			String partyId = (String) context.get("partyId");
			
			String firstName = (String) context.get("firstName");
			String lastName = (String) context.get("lastName");
			String middleName = (String) context.get("middleName");
			Date birthDate =  (Date)context.get("birthDate");
			String birthPlace = (String) context.get("birthPlace");
			String bloodGroup =(String)context.get("bloodGroup");
			String gender =(String)context.get("gender");
			String maritalStatus =(String)context.get("maritalStatus");
			String emergencyContactName =(String)context.get("emergencyContactName");
			String emergencyContactNumber =(String)context.get("emergencyContactNumber");
			String countryCode =(String)context.get("countryCode");
			String mobileNumber =(String)context.get("mobileNumber");
			address1 =(String)context.get("address1");
			address2 =(String)context.get("address2");
			String city =(String)context.get("city");
			String state =(String)context.get("state");
			String postalCode =(String)context.get("postalCode");
			String country =(String)context.get("country");
			String email =(String)context.get("email");
			String userName =(String)context.get("userName");
			String pasword =(String)context.get("pasword");
			String confirmPassword =(String)context.get("confirmPassword");
			String vehicleType =(String)context.get("vehicleType");
			String quarterType =(String)context.get("quarterType");
			String motherTongue =(String)context.get("motherTongue");
			String religion =(String)context.get("religion");
			String caste =(String)context.get("caste");
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
			String partyIdFrom =(String)context.get("partyIdFrom");
			Date dateOfJoining =  (Date)context.get("dateOfJoining");
			Date passportExpireDate =  (Date)context.get("passportExpireDate");
			String backgroundVerification =(String)context.get("backgroundVerification");
			String emplPositionTypeId = (String) context.get("emplPositionTypeId");
			String locationGeoId = (String) context.get("locationGeoId");
			
			Map<String, Object> resultMap = FastMap.newInstance();
			Map<String, Object> input = FastMap.newInstance();
			Map<String, Object> outMap = FastMap.newInstance();
			Timestamp openedDate = null;
			GenericValue parentFacility=null;
			GenericValue facility;
			GenericValue person = null;
			try{
				// create Person / Party
				try {
			        person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
		        } catch (GenericEntityException e) {
		            Debug.logWarning(e.getMessage(), module);
		        }
		        if(UtilValidate.isNotEmpty(person)){
		        	return ServiceUtil.returnError("Error while creating  Party, PartyId already exists" +partyId); 
		        }
				Object tempInput = "PARTY_ENABLED";
				input = UtilMisc.toMap("firstName", firstName, "lastName", lastName, "middleName",middleName, "birthDate",birthDate, "placeOfBirth",birthPlace, "bloodGroup",bloodGroup,"gender",gender, "maritalStatus",maritalStatus, "motherTongue",motherTongue, "religion",religion,  "nationality",nationality, "passportNumber",passportNumber, "passportExpireDate",passportExpireDate, "statusId", tempInput,"partyId",partyId);
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
				
				// create PostalAddress
				if (UtilValidate.isNotEmpty(address1)){
					input = UtilMisc.toMap("userLogin", userLogin, "partyId",ownerPartyId, "address1",address1, "address2", address2, "city", (String)context.get("city"), "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", (String)context.get("postalCode"), "contactMechId", contactMechId);
					resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
					if (ServiceUtil.isError(resultMap)) {
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
				
				// create Partyclassification
				try{
					GenericValue newPartyClassification = delegator.makeValue("PartyClassification");
					newPartyClassification.set("partyId", ownerPartyId);
					newPartyClassification.set("partyClassificationGroupId", caste);
					newPartyClassification.set("fromDate", UtilDateTime.nowTimestamp());
					delegator.create(newPartyClassification);
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating  PartyClassification" + e);	
				}
				
				// Create Employement
				if (UtilValidate.isNotEmpty(ownerPartyId)){
		            input.clear();
		            input.put("userLogin", userLogin);
		            input.put("partyIdTo", ownerPartyId);
		            input.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
		            input.put("roleTypeIdTo", "EMPLOYEE");
		            input.put("fromDate", UtilDateTime.nowTimestamp());
		            input.put("partyIdFrom", partyIdFrom);
		            input.put("appointmentDate", dateOfJoining);
		            input.put("locationGeoId", locationGeoId);
		            outMap = dispatcher.runSync("createEmployment", input);
		            if(ServiceUtil.isError(outMap)){
		           	 	Debug.logError("faild service create Employee:"+ServiceUtil.getErrorMessage(outMap), module);
		           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
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
			            input.put("fromDate", UtilDateTime.nowTimestamp());
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
					GenericValue newEntity = delegator.makeValue("EmployeeDetail");
					newEntity.set("partyId", ownerPartyId);
					newEntity.set("employeeId", ownerPartyId);
					newEntity.set("emergencyContactName", emergencyContactName);
					newEntity.set("emergencyContactNumber", emergencyContactNumber);
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
					newEntity.set("punchType", punchType);
					newEntity.set("shiftType", shiftType);
					newEntity.set("backgroundVerification", backgroundVerification);
					//delegator.setNextSubSeqId(newEntity,"employeeId", 5, 1);
					delegator.create(newEntity);
				}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating  EmployeeDetail" + e);	
				}
			}catch (Exception e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError("Error while creating Employee" + e);	
				}
			  //result.put("partyId", ownerPartyId);
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
	        String holiDayDateStr = (String)context.get("holiDayDate");
	        Timestamp holiDayDate = null;
	        String description = (String)context.get("description");
	        
	        
	        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			try {
				holiDayDate = UtilDateTime.toTimestamp(formatter.parse(holiDayDateStr));
			} catch (ParseException e) {
			}
			try{
				GenericValue holList = delegator.findOne("HolidayCalendar", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "organizationPartyId",organizationPartyId, "holiDayDate", UtilDateTime.toTimestamp(holiDayDate)), true);
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
					result = ServiceUtil.returnSuccess("This date already selected as Holiday please select other date");
				}
			}catch (Exception e) {
				/*Debug.logError(e, module);
				return ServiceUtil.returnError("Error while creating  EmployeeDetail" + e);	*/
			}
	       // result.put("batchId", newEntity.get("batchId"));
	        
	        return result;
	        
	    }
}