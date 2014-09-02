package in.vasista.vbiz.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	        Locale locale = new Locale("en","IN");
			TimeZone timeZone = TimeZone.getDefault();
	        String orgPartyId = "Company";
			List<GenericValue> holiDayList = FastList.newInstance(); 
			Map result = FastMap.newInstance();
			if(UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)){
				thruDate = UtilDateTime.nowTimestamp();
				
			}
			try {
				
					if(UtilValidate.isNotEmpty(isGH) && isGH.equals("Y")){
						if(UtilValidate.isEmpty(fromDate)){
							fromDate = UtilDateTime.addDaysToTimestamp(thruDate, -60);
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
							fromDate = UtilDateTime.addDaysToTimestamp(thruDate, -30);
						}
						Timestamp secondSaturDay = UtilDateTime.addDaysToTimestamp(UtilDateTime.getWeekStart(UtilDateTime.getMonthStart(thruDate),0,2,timeZone,locale), -1);
						holidays.add(UtilDateTime.toSqlDate(secondSaturDay));
					}
					
					if(UtilValidate.isNotEmpty(holiDayList)){
						for(GenericValue holiDay : holiDayList){
							holidays.add(UtilDateTime.toSqlDate(holiDay.getTimestamp("holiDayDate")));
						}
						//holidays =EntityUtil.getFieldListFromEntityList(holiDayList, "holiDayDate", true);
					}
					List workedHolidaysList =FastList.newInstance();
					result.put("workedHolidaysList", workedHolidaysList);
					if(UtilValidate.isEmpty(holidays)){
						return result;
					}
					List conList=UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
					conList.add(EntityCondition.makeCondition("date",EntityOperator.IN,holidays));
					EntityCondition con= EntityCondition.makeCondition(conList,EntityOperator.AND);
					Debug.log("con===="+con);
					
					List<GenericValue> tempWorkedHolidaysList = delegator.findList("EmplDailyAttendanceDetail", con ,null,UtilMisc.toList("date" ,"partyId"), null, false );
					for(GenericValue workedHoliday : tempWorkedHolidaysList){
						Map tempDayMap = FastMap.newInstance();
						Date tempDate = workedHoliday.getDate("date");
						Map punMap = PunchService.emplDailyPunchReport(dctx, UtilMisc.toMap("partyId", partyId ,"punchDate",tempDate));
						if(UtilValidate.isNotEmpty(punMap.get("punchDataList"))){
							tempDayMap.put("punchDetails", ((List)punMap.get("punchDataList")).get(0));
							tempDayMap.put("date",UtilDateTime.toDateString(tempDate,"dd-mm-yyyy"));
						}
						workedHolidaysList.add(tempDayMap);
					}
				  result.put("workedHolidaysList", workedHolidaysList);
			}catch(GenericEntityException e){
	  			Debug.logError("Error fetching  holidays worked " + e.getMessage(), module);
	  		}
	    	
	    	
	    	Debug.log("result:" + result, module);		 
	    	return result;
	    }
	 
	 
}