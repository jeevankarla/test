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
import javax.ws.rs.POST;
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
import org.ofbiz.entity.condition.EntityJoinOperator;
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

    @POST
    @Produces("text/plain")
    public Response createWeighBridgeData(
    		@QueryParam("login.username") String username,@QueryParam("login.password") String password,
    		@QueryParam("tenantId") String tenantId,
    		@QueryParam("date") String date,	@QueryParam("vehicleNumber") String vehicleId,
    		@QueryParam("weight") String weight,@QueryParam("weighmentType") String weighmentType) {
    	Map<String, Object> result = FastMap.newInstance();
    	String productId = "";
    	Locale locale = Locale.getDefault();
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	
    	Timestamp entryDate = UtilDateTime.nowTimestamp();
    	Debug.log("username========="+username);
    	Debug.log("password========="+password);
    	Debug.log("tenantId========="+tenantId);
    	Debug.log("date========="+date);
    	Debug.log("vehicleId========="+vehicleId);
    	Debug.log("weight========="+weight);
    	Debug.log("weighmentType========="+weighmentType);
    	
    	try {
    		entryDate = UtilDateTime.toTimestamp(sdf.parse(date)); 
    				
		} catch (ParseException e) {
			Debug.logError("Cannot parse date string: "+ e, CreateWeighBridgeData.class.getName());
			return Response.serverError().entity("Cannot parse date string:"+date).build();
		} catch (NullPointerException e) {
			Debug.logError("Cannot parse date string: "+ e, CreateWeighBridgeData.class.getName());
			return Response.serverError().entity("Cannot parse date string: "+ date).build();
		}
    	Debug.log("entryDate=========="+entryDate);
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
        
        if(UtilValidate.isNotEmpty(weighmentType)){
        	if((!weighmentType.equalsIgnoreCase("G") )&& (!weighmentType.equalsIgnoreCase("T"))){
        		Debug.logError("weighment Type must be either G or T ",CreateWeighBridgeData.class.getName());
        		return Response.serverError().entity("weighment Type must be either G or T ").build();
        	 }
        }
        GenericValue weighBridgeDetails = delegator.makeValue("WeighBridgeDetails");
        try{
        	weightKgs = new BigDecimal(weight);
        	weighBridgeDetails.set("vehicleId",vehicleId);
        	weighBridgeDetails.set("weightKgs",weightKgs);
        	weighBridgeDetails.set("statusId","ITEM_CREATED");
        	weighBridgeDetails.set("createdDate",entryDate);
        	weighBridgeDetails.set("weighmentType",weighmentType);
        	weighBridgeDetails.set("weighmentId",delegator.getNextSeqId("WeighBridgeDetails"));
        	
        	delegator.create(weighBridgeDetails);		
        	
        }catch (Exception e) {
			// TODO: handle exception
        	Debug.logError("Error While creating Weigh BridgeDetails"+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error While creating WeighBridge Details").build();
		}
        
        List vehicleTripStatusCondList = FastList.newInstance();
    	vehicleTripStatusCondList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
    	vehicleTripStatusCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.LIKE,"MR_%"));
    	vehicleTripStatusCondList.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.LESS_THAN_EQUAL_TO,entryDate));
    	vehicleTripStatusCondList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
    	
    	EntityCondition vehicleTripStatusCond =  EntityCondition.makeCondition(vehicleTripStatusCondList,EntityJoinOperator.AND);
    	List<GenericValue> vehicleTripStatusList = FastList.newInstance();
    	
        // Here  we are trying to get the vehicle status pending 
    	Debug.log("vehicleTripStatusCond===================="+vehicleTripStatusCond);
    	try{
        	vehicleTripStatusList = delegator.findList("VehicleTripStatus", vehicleTripStatusCond, null, null, null, false);
        }catch(Exception e){
        	Debug.logError("Error While getting previous status of the vehicle  "+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error While getting previous status of the vehicle").build();
        }	
    	Debug.log("vehicleTripStatusList===========Size========="+vehicleTripStatusList.size());
    	
        if(UtilValidate.isEmpty(vehicleTripStatusList)){
        	Debug.logError("Previous status not found for vehicle :"+vehicleId, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Previous status not found for vehicle :"+vehicleId).build();
        }
        
        GenericValue vehicleStatus = EntityUtil.getFirst(vehicleTripStatusList);
        String sequenceNum = (String)vehicleStatus.get("sequenceNum");
        Debug.log("vehicleStatus===================="+vehicleStatus);
        List MilkTransferCondList = FastList.newInstance();
        Debug.log("vehicleStatus========"+vehicleStatus);
        
        MilkTransferCondList.add(EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,vehicleId));
        MilkTransferCondList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,sequenceNum));
        EntityCondition milkTransferCondition =  EntityCondition.makeCondition(MilkTransferCondList,EntityJoinOperator.AND);
        
        GenericValue MilkTransfer  = null;
        try{
        	List MilkTransfersList = delegator.findList("MilkTransfer",milkTransferCondition,null,null,null,false);
        	MilkTransfer = EntityUtil.getFirst(MilkTransfersList);
        }catch(GenericEntityException e){
        	Debug.logError("Error while getting Milk TrasnsfersList :"+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error while getting Milk TrasnsfersList :").build();
        }
        Debug.log("MilkTransfer========"+MilkTransfer);
        if(UtilValidate.isEmpty(MilkTransfer)){
        	Debug.logError("No valid Transfer Found to Store weight :", CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("No valid Transfer Found to Store weight :").build();
        }
        if(weighmentType.equalsIgnoreCase("G")){
        	BigDecimal tareweight = (BigDecimal)MilkTransfer.get("tareWeight");
    		if(UtilValidate.isNotEmpty(tareweight) && tareweight.compareTo(weightKgs)==1){
    			Debug.logError("Gross weight should be more than Tare weight :Existed tare weight is "+tareweight, CreateWeighBridgeData.class.getName());
    			return Response.serverError().entity("Gross weight should be more than Tare weight :Existed tare weight is "+tareweight).build();
    		}
        	MilkTransfer.set("grossWeight",weightKgs);
        }
        if(weighmentType.equalsIgnoreCase("T")){
        	if(UtilValidate.isNotEmpty(MilkTransfer.get("grossWeight"))){
        		BigDecimal grossweight = (BigDecimal)MilkTransfer.get("grossWeight");
        		if(UtilValidate.isNotEmpty(grossweight) && weightKgs.compareTo(grossweight)==1){
        			Debug.logError("Tare weight should be less than grossweight :Existed gross weight is "+grossweight, CreateWeighBridgeData.class.getName());
        			return Response.serverError().entity("Tare weight should be less than grossweight :Existed gross weight is "+grossweight).build();
        		}
        		
        	}
        	MilkTransfer.set("tareWeight",weightKgs);
        }
        // Here we are trying to store MilkTransfer
        
        try{
        	delegator.store(MilkTransfer);
        }catch(GenericEntityException e){
        	Debug.logError("Error while storing weight details to Transfer :"+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error while storing weight details to Transfer :"+e.getMessage()).build();
        }
        try{
        	weighBridgeDetails.set("statusId", "ITEM_APPROVED");
        	delegator.store(weighBridgeDetails);
        }catch(GenericEntityException e){
        	Debug.logError("Error while storing weight details  :"+e, CreateWeighBridgeData.class.getName());
        	return Response.serverError().entity("Error while storing weight details :"+e.getMessage()).build();
        }
        return Response.ok("Weighment Details Collected successfully . weighmentId :"+weighBridgeDetails.get("weighmentId")).type("text/plain").build();
        //throw new RuntimeException("Invalid ");
    }//End of the service
    
    
    
}