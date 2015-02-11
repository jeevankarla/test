package in.vasista.vbiz.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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
    public List<Object> fetchMaterials() {
 
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
    public Map<String, Object> fetchMaterialsInventory(@QueryParam("productId") String productId) {
 
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
        if (productId == null || UtilValidate.isEmpty(productId)) {
            Debug.logError("Empty product Id ", MaterialManagementResource.class.getName());        	
        	//::TODO:: error handling
        	result = ServiceUtil.returnError("Product Id is empty");
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
 
        GenericValue userLogin = null;
		GenericValue productDetails = null;
        try{
        	userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);

        	productDetails = delegator.findOne("Product",UtilMisc.toMap("productId", productId),false);
        	if(UtilValidate.isEmpty(productDetails)){
        		Debug.logError("Product Id does not exist " + productId, MaterialManagementResource.class.getName());        	
        		result = ServiceUtil.returnError("Product does not exist");
        		return result; 
        	}
        	
        	result.put("productId", productId);				
        	result.put("name", productDetails.getString("internalName"));	
        	result.put("description", productDetails.getString("description"));	
        	result.put("categoryId", productDetails.getString("primaryProductCategoryId"));  

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
	        	if (UtilValidate.isNotEmpty(productSupplyDetails.get("supplierRate"))) {
	        		supplierRate = (BigDecimal)productSupplyDetails.get("supplierRate");
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
    	Debug.logError("result = " + result,MaterialManagementResource.class.getName());       
        return result;

    }    
}