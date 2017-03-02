package in.vasista.vbiz.rest.filter;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.net.InetAddress;
import javolution.util.FastMap;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;

public class RestHitCounter implements Filter {
    public static final String module = RestHitCounter.class.getName();
	private Delegator delegator;

    void saveHit(Map<String, Object> context) {
        String apiHitId = delegator.getNextSeqId("ApiHit");   	
        GenericValue apiHit = delegator.makeValue("ApiHit");
        apiHit.set("apiHitId", apiHitId);
        apiHit.set("contentId", context.get("webappName")+ "." + context.get("serviceName"));
        apiHit.set("hitTypeId", context.get("REST"));
        apiHit.set("userLoginId", context.get("userLoginId"));       
        apiHit.set("webappName", context.get("webappName"));
        Timestamp startDateTime = (Timestamp)context.get("startDateTime");
        Timestamp endDateTime = (Timestamp)context.get("endDateTime");        
        apiHit.set("startDateTime", startDateTime);
        apiHit.set("endDateTime", endDateTime);
        apiHit.set("totalTimeMillis", Long.valueOf(endDateTime.getTime() - startDateTime.getTime()));
        // ::TODO:: verify transaction/multi-threading safety      
        try {
            apiHit.create();
        } catch (GenericEntityException e) {
            Debug.logWarning("Error saving ApiHit: " + e.toString(), module);
        }
    }
	public void init(FilterConfig config) throws ServletException {

	}

	public void  doFilter(ServletRequest request, 
			ServletResponse response,
			FilterChain chain) 
					throws java.io.IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
   	
		Map<String, Object> apiHitMap = FastMap.newInstance();
        apiHitMap.put("startDateTime", UtilDateTime.nowTimestamp());
        
		// Pass request back down the filter chain
		chain.doFilter(request,response);

		Map<String, Object> restHitMap = (Map<String, Object>) httpRequest.getAttribute("restHitMap");
		if (restHitMap == null) {
			Debug.logError("restHitMap is null", RestHitCounter.class.getName());     	    	  
			return;
		}
		delegator = (Delegator) restHitMap.get("delegator");
		String methodName = (String) restHitMap.get("methodName");      
		String userName = (String) restHitMap.get("userName");
//Debug.logError("hitMap: {" + delegator.getDelegatorName() + ", " + methodName + ", " + userName + "}", module);     	    	  

		apiHitMap.put("endDateTime", UtilDateTime.nowTimestamp());
		apiHitMap.put("userLoginId", userName);
        apiHitMap.put("serviceName", methodName);
		apiHitMap.put("webappName", "rest.suprabhat");         		
		saveHit(apiHitMap);
	}

	public void destroy() {
		// This is optional step but if you like you
		// can write hitCount value in your database.
	}
}
