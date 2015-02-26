package in.vasista.vbiz.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;

import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.inventory.InventoryServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

@Path("/materialmgmt")
public class MaterialManagementResource {
 
    @Context
    HttpHeaders headers;

    @GET
    @Path("/fetchMaterials")        
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> fetchMaterials(@Context HttpServletRequest request) {
 
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
            return result;        
        }
        String tenantDelegatorName =  "default#" + tenantId;
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
        
        Map<String, Object> paramMap = FastMap.newInstance();
        paramMap.put("login.username", username);
        paramMap.put("login.password", password);

        Map<String, Object> resp;
        try {
            resp = dispatcher.runSync("userLogin", paramMap);
        } catch (GenericServiceException e) {
  			Debug.logWarning("Authentication failed for " +username + " " +  e.getMessage(), MaterialManagementResource.class.getName());
			return result;	  
        }

        if (ServiceUtil.isError(resp)) {
  			Debug.logWarning("userLogin authentication service failed for " +username, MaterialManagementResource.class.getName());
			return result;	       
		}

  		GenericValue userLogin = null;
  		try{
  	    	userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), MaterialManagementResource.class.getName());
			return result;	   
  		}        
AuditUtil auditUtil = new AuditUtil(delegator, request);
Map<String, Object> apiHitMap = FastMap.newInstance();
apiHitMap.put("userLoginId", username);
apiHitMap.put("serviceName", "fetchMaterials");
apiHitMap.put("startDateTime", UtilDateTime.nowTimestamp());
        Security security = dispatcher.getDispatchContext().getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_INVENTORY", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to view inventory!", MaterialManagementResource.class.getName());
            return result;
        }
 
        Map<String, Object> context = FastMap.newInstance();
        Map<String, Object> productListMap = MaterialHelperServices.getMaterialProducts(dispatcher.getDispatchContext(),  context);
        List<GenericValue> productList = (List<GenericValue>)productListMap.get("productList");
		List resultList = FastList.newInstance();

    	for(GenericValue product : productList){
            Map productMap = FastMap.newInstance();
    		productMap.put("id",(String)product.get("productId"));             
    		productMap.put("name",(String)product.get("internalName")); 
    		productMap.put("description",(String)product.get("description")); 
    		
    		String productCategoryId = (String)product.get("primaryProductCategoryId"); 
    		if (UtilValidate.isNotEmpty(productCategoryId)) {
    	        try{
    	        	GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), true);
    	        	if (UtilValidate.isNotEmpty(productCategory) && 
    	        			UtilValidate.isNotEmpty(productCategory.getString("description"))) {
    	        		productCategoryId = productCategory.getString("description");
    	        	}
    	        }catch(GenericEntityException e){
    	        	Debug.logError("Error while getting product category"+e,MaterialManagementResource.class.getName());
    	        }
    		}
    		productMap.put("productCategoryId",productCategoryId); 

    		productMap.put("trackInventory","true");                		    		
		    resultList.add(productMap);	
    	}
apiHitMap.put("endDateTime", UtilDateTime.nowTimestamp());
auditUtil.saveHit(apiHitMap);  	
        return resultList;
    }
    
    @GET
    @Path("/fetchMaterialInventory")        
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> fetchMaterialInventory(@QueryParam("productId") String productId, @Context HttpServletRequest request) {
 
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

        Map<String, Object> paramMap = FastMap.newInstance();
        paramMap.put("login.username", username);
        paramMap.put("login.password", password);        
        Map<String, Object> resp;
        try {
            resp = dispatcher.runSync("userLogin", paramMap);
        } catch (GenericServiceException e) {
  			Debug.logWarning("Authentication failed for " +username + " " +  e.getMessage(), MaterialManagementResource.class.getName());
			return result;	  
        }

        if (ServiceUtil.isError(resp)) {
  			Debug.logWarning("userLogin authentication service failed for " +username, MaterialManagementResource.class.getName());
			return result;	       
		}
        
  		GenericValue userLogin = null;
  		try{
  	    	userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), MaterialManagementResource.class.getName());
			return result;	   
  		}        
AuditUtil auditUtil = new AuditUtil(delegator, request);
Map<String, Object> apiHitMap = FastMap.newInstance();
apiHitMap.put("userLoginId", username);
apiHitMap.put("serviceName", "fetchMaterialInventory");
apiHitMap.put("startDateTime", UtilDateTime.nowTimestamp());
        Security security = dispatcher.getDispatchContext().getSecurity();
        // security check
        if (!security.hasEntityPermission("MOB_INVENTORY", "_VIEW", userLogin)) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to view inventory!", MaterialManagementResource.class.getName());
            return result;
        }
        
        if (productId == null || UtilValidate.isEmpty(productId)) {
            Debug.logError("Empty product Id ", MaterialManagementResource.class.getName());        	
        	//::TODO:: error handling
        	result = ServiceUtil.returnError("Product Id is empty");
            return result;        
        }         
 
		GenericValue productDetails = null;
        try{
        	productDetails = delegator.findOne("Product",UtilMisc.toMap("productId", productId),false);
        	if(UtilValidate.isEmpty(productDetails)){
        		Debug.logError("Product Id does not exist " + productId, MaterialManagementResource.class.getName());        	
        		result = ServiceUtil.returnError("Product does not exist");
        		return result; 
        	}
        	
        	result.put("productId", productId);				
        	result.put("name", productDetails.getString("internalName"));	
        	result.put("description", productDetails.getString("description"));	
    		String productCategoryId = productDetails.getString("primaryProductCategoryId"); 
    		if (UtilValidate.isNotEmpty(productCategoryId)) {
    			GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), true);
    	        if (UtilValidate.isNotEmpty(productCategory) && 
    	        	UtilValidate.isNotEmpty(productCategory.getString("description"))) {
    	        	productCategoryId = productCategory.getString("description");
    	        }
    		}
        	result.put("categoryId", productCategoryId);  

        	String uom = "";
        	GenericValue uomDetails = delegator.findOne("Uom",UtilMisc.toMap("uomId", productDetails.getString("quantityUomId")),false);
        	if (UtilValidate.isNotEmpty(uomDetails)) {
        		uom = uomDetails.getString("description");
        	}
        	result.put("uom", uom);        	
        	result.put("specification", productDetails.getString("longDescription"));
        	Map<String, Object> lastSupplyDetails = 
				MaterialHelperServices.getLastSupplyMaterialDetails(dispatcher.getDispatchContext(), UtilMisc.toMap("productId", productId, "userLogin",userLogin));
        	if (UtilValidate.isNotEmpty(lastSupplyDetails) && UtilValidate.isNotEmpty(lastSupplyDetails.get("productSupplyDetails"))) {
        		Map<String, Object> productSupplyDetails = (Map<String, Object>)lastSupplyDetails.get("productSupplyDetails");
        		result.put("supplierId", productSupplyDetails.get("supplierPartyId"));
        		String supplierName = PartyHelper.getPartyName(delegator, (String)productSupplyDetails.get("supplierPartyId"), false);
        		result.put("supplierName", supplierName);
	        	BigDecimal supplierRate = BigDecimal.ZERO;
	        	if (UtilValidate.isNotEmpty(productSupplyDetails.get("supplyRate"))) {
	        		supplierRate = (BigDecimal)productSupplyDetails.get("supplyRate");
	        	}
        		result.put("supplierRate", supplierRate);
        	}
        	Map<String, Object> inventoryDetails = 
        			InventoryServices.getProductInventoryOpeningBalance(dispatcher.getDispatchContext(), UtilMisc.toMap("productId", productId, "ownerPartyId","Company"));
        	if (UtilValidate.isNotEmpty(inventoryDetails)) {
	        	BigDecimal inventoryCount = BigDecimal.ZERO;
	        	BigDecimal inventoryCost = BigDecimal.ZERO;
	        	if (UtilValidate.isNotEmpty(inventoryDetails.get("inventoryCount"))) {
	        		inventoryCount = (BigDecimal)inventoryDetails.get("inventoryCount");
	        	}
	        	if (UtilValidate.isNotEmpty(inventoryDetails.get("inventoryCost"))) {
	        		inventoryCost = (BigDecimal)inventoryDetails.get("inventoryCost");
	        	}	        	
        		result.put("inventoryCount", inventoryCount);
        		result.put("inventoryCost", inventoryCost);        		
        	}
        }catch(GenericEntityException e){
        	Debug.logError("Error while getting inventory details"+e,MaterialManagementResource.class.getName());
        	result = ServiceUtil.returnError("Error while getting inventory details.");
        	return result;
        }
    	//Debug.logError("result = " + result,MaterialManagementResource.class.getName());   
apiHitMap.put("endDateTime", UtilDateTime.nowTimestamp());
auditUtil.saveHit(apiHitMap);     	
        return result;

    }    
}