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


BankAdvicePayRollMap  = context.BankAdvicePayRollMap;
timePeriodStart = context.timePeriodStart;
if(UtilValidate.isNotEmpty(BankAdvicePayRollMap)){
	for(Map.Entry entry : BankAdvicePayRollMap.entrySet()){
		partyId = entry.getKey();
		amountMap = entry.getValue();
		Map<String, Object> getTelParams = FastMap.newInstance();
		contactNumberTo = null;
		getTelParams.put("partyId", partyId);
		if(UtilValidate.isNotEmpty(partyId)){
			getTelParams.put("partyId", partyId);
		}
		getTelParams.put("userLogin", userLogin);
		serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
		if (ServiceUtil.isError(serviceResult)) {
			 Debug.logError(ServiceUtil.getErrorMessage(serviceResult),"");
		}
		if(UtilValidate.isNotEmpty(serviceResult.get("contactNumber"))){
			contactNumberTo = (String) serviceResult.get("contactNumber");
			if(!UtilValidate.isEmpty(serviceResult.get("countryCode"))){
				contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");
			}
		}
		
	   String text = "Your Gross Salary  for "+UtilDateTime.toDateString(timePeriodStart ,'MMMM yyyy')+"- Rs."+amountMap.getAt("totEarnings").setScale(2,BigDecimal.ROUND_HALF_UP)+
	                   ", Deductions - Rs."+amountMap.getAt("totDeductions").setScale(2,BigDecimal.ROUND_HALF_UP).abs()+", Net Pay - Rs."+amountMap.getAt("netAmt").setScale(2,BigDecimal.ROUND_HALF_UP)+" Automated message sent from Milkosoft,MIS";
	   Debug.log("Sms text: " + text);
	   Map<String, Object> sendSmsParams = FastMap.newInstance();
	  if(UtilValidate.isNotEmpty(contactNumberTo)){
			sendSmsParams.put("contactNumberTo", contactNumberTo);
			sendSmsParams.put("text",text);
			//dispatcher.runAsync("sendSms", sendSmsParams,false);
		}
	}
}
