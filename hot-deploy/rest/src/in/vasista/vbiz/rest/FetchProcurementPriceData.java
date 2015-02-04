package in.vasista.vbiz.rest;



import java.util.List;
import java.util.Map;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
 
import javolution.util.FastMap;
 
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
import javax.ws.rs.core.MediaType;

 
@Path("/fetchProcPrices")
public class FetchProcurementPriceData {
 
    @Context
    HttpHeaders headers;

    @GET
    @Path("/json")        
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> fetchPriceChartJson(@QueryParam("shedCode") String shedCode,@QueryParam("unitCode") String unitCode,@QueryParam("centerCode") String centerCode,@QueryParam("product") String product) {
    	Map<String, Object> result = FastMap.newInstance();
    	String username = null;
        String password = null;
        
 
        String tenantId = null;
        String productId = null;
        try {
        	 username = headers.getRequestHeader("login.username").get(0);
             password = headers.getRequestHeader("login.password").get(0);
             tenantId = headers.getRequestHeader("tenantId").get(0);
             /*shedCode = headers.getRequestHeader("shedCode").get(0);
             unitCode = headers.getRequestHeader("unitCode").get(0);
             centerCode = headers.getRequestHeader("centerCode").get(0);
             product = headers.getRequestHeader("product").get(0);*/
             
             
        } catch (NullPointerException e) {
            Debug.logError("Problem reading http header(s): login.username or login.password or tenantId", FetchProcurementPriceData.class.getName());        	        	
        	//::TODO:: error handling
        	return result;
        }
 
        if (username == null  || password == null || tenantId ==null) {
            Debug.logError("Problem reading http header(s): login.username  or tenantId", FetchProcurementPriceData.class.getName());        	
        	result = ServiceUtil.returnError("UserName or Password or tenantId is empty");
            //::TODO:: error handling
            return result;        
         }
        
       
        
        if (shedCode == null || unitCode == null || centerCode == null || product == null) {
        	result = ServiceUtil.returnError("shedCode Or unitCode Or centerCode Or product is missing");
            return result;
         }
        String tenantDelegatorName =  "default#" + tenantId;
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
       
        GenericValue userLogin = null;
        try{
        	userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
        }catch(GenericEntityException e){
        	Debug.logError("Error while getting userLogin Value"+e,FetchProcurementPriceData.class.getName());
        	result = ServiceUtil.returnError("User Details Not Found..");
        	return result;
        }
        
        Map productMap = FastMap.newInstance();
        try{
        	List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,"MILK_PROCUREMENT"));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	List<GenericValue> productsList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
        	
        	
        	for(GenericValue products : productsList){
        		GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId",(String)products.get("productId")),false);
        		productMap.put((String)productDetails.get("brandName"),(String)productDetails.get("productId"));
        	}
        	
        	
        	/*for(GenericValue productDetails : productCatMembers){
        		productMap.put((String)productDetails.get("brandName"),(String)productDetails.get("productId"));
        	}*/
        	
        }catch(Exception e){
        	Debug.logError("Error while getting procurementProducts"+e,FetchProcurementPriceData.class.getName());
        	result = ServiceUtil.returnError("Error while getting ProductDetails=====");
        	return result;
        	
        }
        if(UtilValidate.isNotEmpty(productMap)){
        	productId = (String)productMap.get(product);
        }
        Map<String, String> paramMap = UtilMisc.toMap(
                "shedCode", shedCode,
                "unitCode", unitCode,
                "centerCode", centerCode,
                "productId", productId,
                "userLogin",userLogin
            );
        try {
            result = dispatcher.runSync("getFacilityProcurementPrices", paramMap);
            
        } catch (GenericServiceException e1) {
            Debug.logError(e1, FetchProcurementPriceData.class.getName());
            return result;
        }
 
        if (ServiceUtil.isSuccess(result)) {
            return result;
        }
 
        if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
            return result;
        }
 
        throw new RuntimeException("Invalid ");
    }// end of the Json 
    
    
    
    
    @GET
    @Path("/plaintext")    
    @Produces("text/plain")
    public Response fetchPriceChartText(@QueryParam("shedCode") String shedCode,@QueryParam("unitCode") String unitCode,@QueryParam("centerCode") String centerCode,@QueryParam("product") String product) {
    	Map<String, Object> result = FastMap.newInstance();
    	String username = null;
        String password = null;
        
 
        String tenantId = null;
        String productId = null;
        try {
        	 username = headers.getRequestHeader("login.username").get(0);
             password = headers.getRequestHeader("login.password").get(0);
             tenantId = headers.getRequestHeader("tenantId").get(0);
        } catch (NullPointerException e) {
            Debug.logError("Problem reading http header(s): login.username or login.password or tenantId", FetchProcurementPriceData.class.getName());        	        	
        	//::TODO:: error handling
            return Response.serverError().entity("Problem reading http header(s): login.username or login.password or tenantId").build();
        }
 
        if (username == null  || password == null || tenantId ==null) {
            Debug.logError("Problem reading http header(s): login.username  or tenantId", FetchProcurementPriceData.class.getName());        	
            return Response.serverError().entity("Problem reading http header(s): login.username or login.password or tenantId").build();
            //::TODO:: error handling
         }
        
       
        
        if (shedCode == null || unitCode == null || centerCode == null || product == null) {
        	return Response.serverError().entity("shedCode Or unitCode Or centerCode Or product is missing").build();
         }
        String tenantDelegatorName =  "default#" + tenantId;
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
       
        GenericValue userLogin = null;
        try{
        	userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
        }catch(GenericEntityException e){
        	Debug.logError("Error while getting userLogin Value"+e,FetchProcurementPriceData.class.getName());
        	return Response.serverError().entity("Error while getting userLogin Value").build();
        }
        
        Map productMap = FastMap.newInstance();
        try{
        	List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,"MILK_PROCUREMENT"));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	List<GenericValue> productsList = delegator.findList("ProductCategoryAndMember",condition,null,null,null,false);
        	
        	
        	for(GenericValue products : productsList){
        		GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId",(String)products.get("productId")),false);
        		productMap.put((String)productDetails.get("brandName"),(String)productDetails.get("productId"));
        	}
        	
        	
        	/*for(GenericValue productDetails : productCatMembers){
        		productMap.put((String)productDetails.get("brandName"),(String)productDetails.get("productId"));
        	}*/
        	
        }catch(Exception e){
        	Debug.logError("Error while getting procurement Products"+e,FetchProcurementPriceData.class.getName());
        	return Response.serverError().entity("Error while getting procurement Products").build();
        	
        }
        if(UtilValidate.isNotEmpty(productMap)){
        	productId = (String)productMap.get(product);
        }
        Map<String, String> paramMap = UtilMisc.toMap(
                "shedCode", shedCode,
                "unitCode", unitCode,
                "centerCode", centerCode,
                "productId", productId,
                "userLogin",userLogin
            );
        try {
            result = dispatcher.runSync("getFacilityProcurementPrices", paramMap);
            
        } catch (GenericServiceException e1) {
            Debug.logError(e1, FetchProcurementPriceData.class.getName());
            return Response.serverError().entity(e1.getMessage()).build();
        }
 
        if (ServiceUtil.isSuccess(result)) {
        	return Response.ok(" " + result + " ").type("text/plain").build();
        }
 
        if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
        	return Response.serverError().entity(ServiceUtil.getErrorMessage(result)).build();
        }
 
        throw new RuntimeException("Invalid ");
    }//End of the service
    
    
    
    
}