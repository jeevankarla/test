package in.vasista.vbiz.facility.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.network.DeprecatedNetworkServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

 public class FacilityUtil {
	   public static final String module = FacilityUtil.class.getName();
	    
	 public static Map isFacilityAcitve(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	
	    	Delegator delegator = ctx.getDelegator();		
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String facilityId = (String) context.get("facilityId");
			Timestamp filterByDate = (Timestamp) context.get("fromDate");
			if(UtilValidate.isEmpty(filterByDate)){
				filterByDate = UtilDateTime.nowTimestamp();
			}
	    	try{
	    		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
	    		boolean isActive = EntityUtil.isValueActive(facility , filterByDate, "openedDate", "closedDate");
	    		if(!isActive){
	    			Debug.logError("is not active facility"+facilityId, module);    			
	    			return ServiceUtil.returnError("The  facility ' "+ facilityId+"' is not Active."); 
	    		}
	    	}catch (GenericEntityException e) {
				// TODO: handle exception
	    		Debug.logError(e, module);    			
				return ServiceUtil.returnError(e.getMessage());    		
			}
			
	    	
	        return ServiceUtil.returnSuccess();
	    }
	 
}
