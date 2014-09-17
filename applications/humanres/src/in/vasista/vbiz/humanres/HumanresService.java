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



public class HumanresService {

    public static final String module = HumanresApiService.class.getName();
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
			List<GenericValue> employments = delegator.findList("EmploymentAndPerson", condition, null, UtilMisc.toList("firstName"), null, false);
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
				GenericValue org = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", orgPartyId));
				Map<String, Object> inputParamMap = FastMap.newInstance();
				inputParamMap.put("userLogin", userLogin);			
				inputParamMap.put("org", org);
				inputParamMap.put("fromDate", fromDate);
				inputParamMap.put("thruDate", thruDate);
				populateOrgEmployements(dctx, inputParamMap, employementList);	
			}catch(GenericEntityException e){
	  			Debug.logError("Error fetching employments " + e.getMessage(), module);
	  		}
	    	Map result = FastMap.newInstance();  
	    	result.put("employementList", employementList);
	Debug.logInfo("result:" + result, module);		 
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
							fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -30));
						}
						GenericValue employeeDetail = delegator.findOne("EmployeeDetail",UtilMisc.toMap("partyId",partyId),false);
						List conditionList = FastList.newInstance();
			    		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
					    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(fromDate)));
					    conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(thruDate)));
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
			    			
			    			if(weekName.equalsIgnoreCase(emplWeeklyOffDay) && UtilValidate.isNotEmpty(dayShiftList) || (dayShiftList.size() >= 2)){
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
					for(GenericValue workedHoliday : tempWorkedHolidaysList){
						Map tempDayMap = FastMap.newInstance();
						Date tempDate = workedHoliday.getDate("date");
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
										 if(((hours*60)+minutes) >=210){
											 tempDayMap.put("punchDetails", ((List)punMap.get("punchDataList")).get(0));
											 tempDayMap.put("date",UtilDateTime.toDateString(tempDate,"dd-MM-yyyy"));
											 workedHolidaysList.add(tempDayMap);
										 }
									}
								}
								
							}
							
						}
						
					}
				  result.put("workedHolidaysList", workedHolidaysList);
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
			List conditionList=FastList.newInstance();
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
				
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, loanTypeId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
				conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO, disbDateStart));
				conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO, disbDateEnd));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		List<GenericValue> loanList = FastList.newInstance();
	    		loanList = delegator.findList("Loan", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(loanList)){
	    			return ServiceUtil.returnError("Loan already created...!"); 
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
				loan.set("numPrincipalInst", numPrincipalInst);
				loan.set("disbDate", disbDateStart);
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
	    	Date setlDate = (Date) context.get("setlDate");
	    	
	    	Timestamp setlDateTime = UtilDateTime.toTimestamp(setlDate);
	    	Timestamp setlDateStart = UtilDateTime.getDayStart(setlDateTime);
	    	Timestamp setlDateEnd = UtilDateTime.getDayEnd(setlDateTime);
	    	
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue loanDetails = null;
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			try {
				loanDetails = delegator.findOne("Loan",UtilMisc.toMap("loanId", loanId), false);
				if(UtilValidate.isNotEmpty(loanDetails)){
					loanDetails.set("setlDate", setlDateEnd);
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
}