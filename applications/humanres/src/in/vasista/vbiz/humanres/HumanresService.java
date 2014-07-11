package in.vasista.vbiz.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

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
        if (org == null) {
        	return;
        }
        if(UtilValidate.isEmpty(fromDate)){
        	fromDate = UtilDateTime.nowTimestamp();
        }
		List<GenericValue> internalOrgs = FastList.newInstance();
  		try{
  			internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", UtilMisc.toMap("partyIdFrom", org.getString("partyId"),
				"partyRelationshipTypeId", "GROUP_ROLLUP"),UtilMisc.toList("groupName")),fromDate);
  			for(GenericValue internalOrg : internalOrgs){
  				Map<String, Object> inputParamMap = FastMap.newInstance();
  				inputParamMap.put("userLogin", userLogin);			  				
  				inputParamMap.put("org", internalOrg);
  				populateOrgEmployements(dctx, inputParamMap, employementList);
  			}
			List<GenericValue> employments = EntityUtil.filterByDate(delegator.findByAnd("EmploymentAndPerson", UtilMisc.toMap("partyIdFrom", org.getString("partyId"), 
					"roleTypeIdTo", "EMPLOYEE"), UtilMisc.toList("firstName")),fromDate);
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
	        Security security = dctx.getSecurity();
	            	
			List employementList = FastList.newInstance();        
			try {
				GenericValue org = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", orgPartyId));
				Map<String, Object> inputParamMap = FastMap.newInstance();
				inputParamMap.put("userLogin", userLogin);			
				inputParamMap.put("org", org);
				inputParamMap.put("fromDate", fromDate);
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
}