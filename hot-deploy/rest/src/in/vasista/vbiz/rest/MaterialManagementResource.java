package in.vasista.vbiz.rest;


import java.util.Map;
import java.util.List;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
 
import javolution.util.FastMap;
import javolution.util.FastList;
 
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javax.ws.rs.core.MediaType;

import in.vasista.vbiz.purchase.MaterialHelperServices;

@Path("/materialmgmt")
public class MaterialManagementResource {
 
    @Context
    HttpHeaders headers;

    @GET
    @Path("/fetchMaterials")        
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> fetchMaterialsJSON() {
 
        String username = null;
        String password = null;
        String tenantId = null;
        List<Object> result = FastList.newInstance();

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
        Map<String, Object> productListMap = MaterialHelperServices.getMaterialProducts(dispatcher.getDispatchContext(),  context);
        List<GenericValue> productList = (List<GenericValue>)productListMap.get("productList");
		List resultList = FastList.newInstance();

    	for(GenericValue product : productList){
            Map productMap = FastMap.newInstance();
    		productMap.put("id",(String)product.get("productId"));             
    		productMap.put("name",(String)product.get("internalName")); 
    		productMap.put("description",(String)product.get("description"));                		
    		productMap.put("productCategoryId",(String)product.get("primaryProductCategoryId"));  
    		productMap.put("trackInventory","true");                		    		
		    resultList.add(productMap);	
    	}
        return resultList;
    }
    
    @GET
    @Path("/fetchMaterialInventory")        
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> fetchMaterialsJSON() {
 
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
        	result = ServiceUtil.returnError("UserName or Password or tenantId is empty");
        	return result;
        }
 
        if (username == null || password == null || tenantId == null) {
            Debug.logError("Problem reading http header(s): login.username or login.password or tenantId", MaterialManagementResource.class.getName());        	
        	//::TODO:: error handling
        	result = ServiceUtil.returnError("UserName or Password or tenantId is empty");
            return result;        
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
        Map<String, Object> productListMap = MaterialHelperServices.getMaterialProducts(dispatcher.getDispatchContext(),  context);

        return result;

    }    
}