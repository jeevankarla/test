package org.ofbiz.common.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class SmsServices {
    public final static String module = SmsServices.class.getName();

	   /**
     * Basic SMS Service
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
	 * @throws MalformedURLException 
	 * @throws UnsupportedEncodingException 
     */
    public static Map<String, Object> sendSms(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();    	
		String postData="";
		String retval = "";
        String communicationEventId = (String) context.get("communicationEventId");
        if (communicationEventId != null) {
            Debug.logInfo("sendSms: communicationEventId=" + communicationEventId, module);
        }
		//give all Parameters In String 
		String user = UtilProperties.getPropertyValue("general.properties", "sms.auth.user");
		String passwd = UtilProperties.getPropertyValue("general.properties", "sms.auth.password");
		String mobileNumber = (String) context.get("contactNumberTo");		
		String message = (String) context.get("text");
		if (UtilValidate.isEmpty(mobileNumber)) {
            String errMsg = "Received empty mobile number [ " + message + "]";
			Debug.logInfo(errMsg, module);
            return ServiceUtil.returnSuccess();		
		}		
		String sid = UtilProperties.getPropertyValue("general.properties", "sms.sid");
		String mtype = UtilProperties.getPropertyValue("general.properties", "sms.mtype");
		String DR = UtilProperties.getPropertyValue("general.properties", "sms.DR");
		String gateway = UtilProperties.getPropertyValue("general.properties", "sms.gateway.url");		
		String allowedtestlist = UtilProperties.getPropertyValue("general.properties", "sms.allowedtestlist");	
		if (UtilValidate.isNotEmpty(allowedtestlist)) {
            boolean allowSms = false;
			try {	
	            List<EntityCondition> conditionList = UtilMisc.toList(
	                    EntityCondition.makeCondition("contactListId", EntityOperator.EQUALS, allowedtestlist),
	                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CLPT_ACCEPTED"),
	                    EntityCondition.makeCondition("preferredContactMechId", EntityOperator.NOT_EQUAL, null),
	                    EntityUtil.getFilterByDateExpr(), EntityUtil.getFilterByDateExpr("contactFromDate", "contactThruDate"));
	
	            EntityConditionList<EntityCondition> conditions = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Set<String> fieldsToSelect = UtilMisc.toSet("partyId", "preferredContactMechId", "fromDate", "contactNumber", "countryCode");
	            EntityListIterator eli = null;
	            eli = delegator.find("ContactListPartyAndContactMechAndTelecomNumber", conditions, null, fieldsToSelect, null,
	            		new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
	            for (GenericValue contactListPartyAndContactMech; (contactListPartyAndContactMech = eli.next()) != null;) {
	Debug.logInfo("Contact info: " + contactListPartyAndContactMech, module);
	                String contactListItemNo = contactListPartyAndContactMech.getString("countryCode") + 
	                	contactListPartyAndContactMech.getString("contactNumber");
	                if (UtilValidate.isNotEmpty(contactListItemNo) && contactListItemNo.equals(mobileNumber)) {
	                    allowSms = true;
	                    break;
	                }
	            }
			}
			catch (GenericEntityException gee) {
				// nothing more to do here, sms will not be sent anyways
				Debug.logError(gee, module);				
			}
            if (allowSms == false) {
            	// don't send sms, just return
	            String errMsg = "Not sending sms to " + "[" + mobileNumber + "]. Test sms not enabled for this number. ";
				Debug.logError(errMsg, module);
	            return ServiceUtil.returnError(errMsg);	            	
            }
		}

        Debug.logInfo("sendSms: contactNumberTo=" + mobileNumber, module);

		
		try {
			postData += "User=" + URLEncoder.encode(user,"UTF-8") + "&passwd=" + passwd + "&mobilenumber=" + mobileNumber + "&message=" + URLEncoder.encode(message,"UTF-8") + "&sid=" + sid + "&mtype=" + mtype + "&DR=" + DR;
			URL url = new URL(gateway);
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

			// If You Are Behind The Proxy Server Set IP And PORT else Comment Below 4 Lines
			//Properties sysProps = System.getProperties();
			//sysProps.put("proxySet", "true");
			//sysProps.put("proxyHost", "Proxy Ip");
			//sysProps.put("proxyPort", "PORT");

			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(postData);
			out.close();
			int status = ((HttpURLConnection) urlconnection).getResponseCode();
			if (status >= 400) {
	            String errMsg = "Received " + status + " error when sending message to [" + mobileNumber + "] ";
				Debug.logError(errMsg, module);
	            return ServiceUtil.returnError(errMsg);		
			}
			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				retval += decodedString;
			}
			in.close();
			Debug.logInfo("decoded String=" + decodedString, module);
		} catch (UnsupportedEncodingException e) {
            String errMsg = "UnsupportedEncodingException when sending message to [" + mobileNumber + "] ";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);			
		} catch (MalformedURLException e) {
            String errMsg = "MalformedURLException when sending message to [" + mobileNumber + "] ";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);            
		} catch (IOException e) {
            String errMsg = "IOException when sending message to [" + mobileNumber + "] ";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);            
		}
        Map<String, Object> results = ServiceUtil.returnSuccess();
        results.put("communicationEventId", communicationEventId);        
        return results;
    }
}
