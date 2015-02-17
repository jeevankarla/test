package in.vasista.vbiz.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Locale;
import java.util.Map;
import java.net.InetAddress;
import java.sql.Timestamp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;


public class AuditUtil {
    public static final String module = AuditUtil.class.getName();
    
    private Delegator delegator;
    private HttpServletRequest request;
    
    public AuditUtil(Delegator inDelegator, HttpServletRequest inRequest) {
    	delegator = inDelegator;
    	request = inRequest;
    }

    void saveHit(Map<String, Object> context) {
    	
    	HttpSession session = request.getSession();
        String webappName = "rest";
        Locale initialLocaleObj = (Locale) session.getAttribute("_CLIENT_LOCALE_");
        String initialLocale = initialLocaleObj != null ? initialLocaleObj.toString() : "";
        String clientIpAddress = (String)session.getAttribute("_CLIENT_REMOTE_ADDR_");
        String clientHostName = (String)session.getAttribute("_CLIENT_REMOTE_HOST_");
        String clientUser = (String)session.getAttribute("_CLIENT_REMOTE_USER_");   
        if (UtilValidate.isEmpty(webappName)) {
            Debug.logInfo(new Exception(), "The webappName was empty, somehow the initial request settings were missing.", module);
        }    	
        String apiHitId = delegator.getNextSeqId("ApiHit");   	
        GenericValue apiHit = delegator.makeValue("ApiHit");
        apiHit.set("apiHitId", apiHitId);
        apiHit.set("contentId", webappName + "." + context.get("serviceName"));
        apiHit.set("hitTypeId", "SERVICE");
        apiHit.set("userLoginId", context.get("userLoginId"));
        try {
            InetAddress address = InetAddress.getLocalHost();

            if (address != null) {
            	apiHit.set("serverIpAddress", address.getHostAddress());
            	apiHit.set("serverHostName", address.getHostName());
            } else {
                Debug.logError("Unable to get localhost internet address, was null", module);
            }
        } catch (java.net.UnknownHostException e) {
            Debug.logError("Unable to get localhost internet address: " + e.toString(), module);
        }        
        apiHit.set("webappName", webappName);
        apiHit.set("initialLocale", context.get("initialLocale"));
        apiHit.set("clientIpAddress", context.get("clientIpAddress"));
        apiHit.set("clientHostName", context.get("clientHostName"));
        apiHit.set("clientUser", context.get("clientUser"));
        apiHit.set("clientIpIspName", context.get("clientIpIspName"));
        apiHit.set("clientIpPostalCode", context.get("clientIpPostalCode"));
        apiHit.set("clientIpStateProvGeoId", context.get("clientIpStateProvGeoId"));
        apiHit.set("clientIpCountryGeoId", context.get("clientIpCountryGeoId"));
        Timestamp startDateTime = (Timestamp)context.get("startDateTime");
        Timestamp endDateTime = (Timestamp)context.get("endDateTime");        
        apiHit.set("startDateTime", startDateTime);
        apiHit.set("endDateTime", endDateTime);
        apiHit.set("totalTimeMillis", Long.valueOf(endDateTime.getTime() - startDateTime.getTime()));
        // ::TODO:: verify transaction/multi-threading safety   
Debug.logError("ApiHit: " + apiHit, module);

        try {
            apiHit.create();
        } catch (GenericEntityException e) {
            Debug.logWarning("Error saving ApiHit: " + e.toString(), module);
        }
    }
}
