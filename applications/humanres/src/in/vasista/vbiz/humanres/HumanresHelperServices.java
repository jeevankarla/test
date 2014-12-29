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



public class HumanresHelperServices {

    public static final String module = HumanresHelperServices.class.getName();
    /*
     * Helper that returns full employee profile.  This method expects the employee's EmploymentAndPerson
     * record as an input.
     */
   
	 public static Map<String, Object> getEmployeeWeeklyOffDays(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String employeeId = (String) context.get("employeeId");
	    	Timestamp fromDate =  (Timestamp)context.get("fromDate");
	        Timestamp thruDate =  (Timestamp)context.get("thruDate");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Locale locale = new Locale("en","IN");
			TimeZone timeZone = TimeZone.getDefault();
			List weeklyOffDays = FastList.newInstance();
			try {
				GenericValue employeeDetail = delegator.findOne("EmployeeDetail",UtilMisc.toMap("partyId",employeeId),false);
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
	    			
	    			if(weekName.equalsIgnoreCase(emplWeeklyOffDay)){
	    				weeklyOffDays.add(UtilDateTime.toSqlDate(cTime));
	    			}
	    			c1.add(Calendar.DATE,1);
				}
				
	        }catch(Exception e){
				Debug.logError("Error while getting Loan Amounts"+e.getMessage(), module);
			}
			result.put("weeklyOffDays", weeklyOffDays);
	        return result;
	    }
	 
	 public static Map<String, Object> getLoanClosingBalanceByLoanType(DispatchContext dctx, Map context) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	String loanTypeId = (String) context.get("loanTypeId");
	    	String partyId = (String) context.get("partyId");
	    	String customTimePeriodId = (String) context.get("customTimePeriodId");
	    	
			GenericValue loanTypeDetails = null;
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			Timestamp fromDateStart = null;
	    	Timestamp thruDateEnd = null;
	    	
	    	Map loanClosingBalMap = FastMap.newInstance();
			try {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	        	if (UtilValidate.isNotEmpty(customTimePeriod)) {
	        		Timestamp fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        		Timestamp thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	        		
	        		fromDateStart = UtilDateTime.getDayStart(fromDate);
	        		thruDateEnd = UtilDateTime.getDayEnd(thruDate);
	        		
	        		List conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					conditionList.add(EntityCondition.makeCondition("loanTypeId", EntityOperator.EQUALS, loanTypeId));
					//conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDateStart));
					//conditionList.add(EntityCondition.makeCondition("disbDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDateEnd));
					conditionList.add(EntityCondition.makeCondition("setlDate", EntityOperator.EQUALS ,null));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	List<GenericValue> loanList = delegator.findList("Loan",condition, null, null, null, false);
		        	if(UtilValidate.isNotEmpty(loanList)){
		        		for(GenericValue loan:loanList){
		        			String loanId = loan.getString("loanId");
		        			List condList = FastList.newInstance();
		        			condList.add(EntityCondition.makeCondition("loanId", EntityOperator.EQUALS, loanId));
		        			condList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
				        	EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
				        	List<GenericValue> loanRecoveryList = delegator.findList("LoanRecovery",cond, null, null, null, false);
				        	for(GenericValue loanRecovery : loanRecoveryList){
				        		BigDecimal closingBalance = BigDecimal.ZERO;
				        		if(UtilValidate.isNotEmpty(loanRecovery.getBigDecimal("closingBalance"))){
				        			closingBalance = loanRecovery.getBigDecimal("closingBalance");
				        			if(UtilValidate.isNotEmpty(closingBalance)){
		    							loanClosingBalMap.put(partyId, closingBalance);
		    						}	
	    						}
				        	}
		        		}
		        	}
	        	}
	        }catch(GenericEntityException e){
				Debug.logError("Error while getting Loan Amounts"+e.getMessage(), module);
			}
			result.put("loanClosingBalMap", loanClosingBalMap);
	        return result;
	    }
	 
}