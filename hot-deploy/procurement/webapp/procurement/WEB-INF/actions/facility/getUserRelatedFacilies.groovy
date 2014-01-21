import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONObject;

dctx = dispatcher.getDispatchContext();

Map userRelatedFacilities = ProcurementNetworkServices.getFacilityShedByUserLogin(dctx,UtilMisc.toMap("userLogin",context.userLogin));
if(ServiceUtil.isSuccess(userRelatedFacilities)){
		Map unitDetails = userRelatedFacilities.get("unitDetails");
		Map shedDetails = userRelatedFacilities.get("shedDetails");
		if(UtilValidate.isNotEmpty(unitDetails)){
			context.put("unitId",unitDetails.get("facilityId"));
			context.put("unitName",unitDetails.get("facilityName"));
			context.put("unitCode",unitDetails.get("facilityCode"));
			}
		if(UtilValidate.isNotEmpty(shedDetails)){
			context.put("shedId",shedDetails.get("facilityId"));
			context.put("shedName",shedDetails.get("facilityName"));
			context.put("shedCode",shedDetails.get("facilityCode"));
			}
	
}else{
	Debug.log("no Facility Found");
}
