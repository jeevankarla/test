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

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class SmsServices {
    public final static String module = SmsServices.class.getName();

	   /**
     * Basic JavaMail Service
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
	 * @throws MalformedURLException 
	 * @throws UnsupportedEncodingException 
     */
    public static Map<String, Object> sendSms(DispatchContext ctx, Map<String, ? extends Object> context) {
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
