package in.vasista.vbiz.rest;



import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
 
import javolution.util.FastList;
import javolution.util.FastMap;
 
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javax.ws.rs.core.MediaType;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;


@Path("/createWeighBridgeData")
public class CreateWeighBridgeData {
 
    @Context
    HttpHeaders headers;

    @GET
    @Path("/plaintext")    
    @Produces("text/plain")
    public Response createWeighBridgeData(
    		@QueryParam("login.username") String username,@QueryParam("login.password") String password,
    		@QueryParam("tenantId") String tenantId,
    		@QueryParam("date") String date,	@QueryParam("vehicleNumber") String vehicleId,
    		@QueryParam("weight") String weight) {
    	Map<String, Object> result = FastMap.newInstance();
    	String productId = "";
    	Locale locale = Locale.getDefault();
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    	Timestamp entryDate = UtilDateTime.nowTimestamp();
    	
    	try {
    		entryDate = UtilDateTime.toTimestamp(sdf.parse(date)); 
    				
		} catch (ParseException e) {
			Debug.logError("Cannot parse date string: "+ date, CreateWeighBridgeData.class.getName());
			return Response.serverError().entity("Cannot parse date string:"+date).build();
		} catch (NullPointerException e) {
			Debug.logError("Cannot parse date string: "+ date, CreateWeighBridgeData.class.getName());
			return Response.serverError().entity("Cannot parse date string: "+ date).build();
		}
 
        if (username == null  || password == null || tenantId ==null) {
            Debug.logError("Problem reading http header(s): login.username  or tenantId", CreateWeighBridgeData.class.getName());        	
            return Response.serverError().entity("username Or password Or tennatId is missing").build();
            //::TODO:: error handling
         }
       
        if (vehicleId == null || weight == null ) {
        	return Response.serverError().entity("VehicleId or Weight is Missing").build();
         }
        String tenantDelegatorName =  "default#" + tenantId;
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
        GenericValue vehicleDetails = null;	
        try{
        	vehicleDetails = delegator.findOne("Vehicle", false, UtilMisc.toMap("vehicleId",vehicleId));
        }catch (Exception e) {
			// TODO: handle exception
        	Debug.logError("Error While getting Vehicle Details"+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error While getting Vehicle Details"+vehicleId).build();
		}
        
        if(UtilValidate.isEmpty(vehicleDetails)){
        	Debug.logError("Vehicle Not Found===", CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Vehicle Not Found===").build();
        }
        BigDecimal weightKgs = BigDecimal.ZERO;
        
        
        try{
        	weightKgs = new BigDecimal(weight);
        	GenericValue weighBridgeDetails = delegator.makeValue("WeighBridgeDetails");
        	weighBridgeDetails.set("vehicleId",vehicleId);
        	weighBridgeDetails.set("weightKgs",weightKgs);
        	weighBridgeDetails.set("statusId","ITEM_CREATED");
        	weighBridgeDetails.set("createdDate",entryDate);
        	weighBridgeDetails.set("weighmentId",delegator.getNextSeqId("WeighBridgeDetails"));
        	
        	delegator.create(weighBridgeDetails);		
        	
        	return Response.ok("Weighment Details Collected successfully . weighmentId :"+weighBridgeDetails.get("weighmentId")).type("text/plain").build();
        }catch (Exception e) {
			// TODO: handle exception
        	Debug.logError("Error While creating Weigh BridgeDetails"+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error While creating WeighBridge Details").build();
		}
        
        
        
        
        //throw new RuntimeException("Invalid ");
    }//End of the service
    
    
    
}