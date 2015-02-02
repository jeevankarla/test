package in.vasista.vbiz.rest;


import java.util.Map;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
 
import javolution.util.FastMap;
 
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javax.ws.rs.core.MediaType;

import in.vasista.vbiz.purchase.MaterialHelperServices;

@Path("/fetchMaterials")
public class MaterialManagementResource {
 
    @Context
    HttpHeaders headers;

    @GET
    @Path("/plaintext")    
    @Produces("text/plain")
    public Response fetchMaterialsText() {
 
        String username = null;
        String password = null;
        String tenantId = null;
 
        try {
            username = headers.getRequestHeader("login.username").get(0);
            password = headers.getRequestHeader("login.password").get(0);
            tenantId = headers.getRequestHeader("tenantId").get(0);            
        } catch (NullPointerException e) {
            return Response.serverError().entity("Problem reading http header(s): login.username or login.password or tenantId").build();
        }
 
        if (username == null || password == null || tenantId == null) {
           return Response.serverError().entity("Problem reading http header(s): login.username or login.password or tenantId").build();
        }
        String tenantDelegatorName =  "default#" + tenantId;
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
 
        Map<String, String> paramMap = UtilMisc.toMap(
        		"login.username", username,
                "login.password", password,
                "tenantId", tenantId
            );
        Map<String, Object> context = FastMap.newInstance();
        Map<String, Object> result = MaterialHelperServices.getMaterialProducts(dispatcher.getDispatchContext(),  context);

 

        return Response.ok(" " + result + " ").type("text/plain").build();
    }
    @GET
    @Path("/json")        
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> fetchMaterialsJSON() {
 
        String username = null;
        String password = null;
        String tenantId = null;
        Map<String, Object> result = FastMap.newInstance();

        try {
            username = headers.getRequestHeader("login.username").get(0);
            password = headers.getRequestHeader("login.password").get(0);
            tenantId = headers.getRequestHeader("tenantId").get(0);            
        } catch (NullPointerException e) {
            Debug.logError("Problem reading http header(s): login.username or login.password or tenantId", MaterialManagementResource.class.getName());        	        	
        	//::TODO:: error handling
        	return result;
        }
 
        if (username == null || password == null || tenantId == null) {
            Debug.logError("Problem reading http header(s): login.username or login.password or tenantId", MaterialManagementResource.class.getName());        	
        	//::TODO:: error handling
            return result;        }
        String tenantDelegatorName =  "default#" + tenantId;
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
 
        Map<String, String> paramMap = UtilMisc.toMap(
        		"login.username", username,
                "login.password", password,
                "tenantId", tenantId
            );
 
        Map<String, Object> context = FastMap.newInstance();
        result = MaterialHelperServices.getMaterialProducts(dispatcher.getDispatchContext(),  context);
		return result;

    }
}