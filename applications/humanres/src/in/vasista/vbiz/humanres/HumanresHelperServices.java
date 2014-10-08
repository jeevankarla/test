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
	 
}