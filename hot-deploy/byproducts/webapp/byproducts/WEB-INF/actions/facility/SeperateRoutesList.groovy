import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.service.ServiceUtil;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.byproducts.ByProductServices;


routesIdsList = ByProductServices.getByproductRoutes(delegator).get("routeIdsList");
List routeList = FastList.newInstance();
Map allRoutes = FastMap.newInstance();
allRoutes.put("facilityId","AllRoutes");
routeList.add(allRoutes);
JSONArray amRouteList = new JSONArray();
JSONArray pmRouteList = new JSONArray();
Map routeMap =FastMap.newInstance();
routesIdsList.each{ routeId ->
	groupMemberList = delegator.findList("FacilityGroupMember",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,routeId) , null, null, null, false);
	groupMemberList.each{ groupMember ->
		JSONObject tempJson = new JSONObject();
		tempJson.put("facilityId",groupMember.facilityId );
		if("AM_RT_GROUP".equals(groupMember.get("facilityGroupId"))){
			amRouteList.add(tempJson);
			routeList.add(tempJson);
		}
		if("PM_RT_GROUP".equals(groupMember.get("facilityGroupId"))){
			pmRouteList.add(tempJson);
		}		
	}	
}
context.putAt("routeList", routeList);
JSONObject facilityItemsJSON = new JSONArray();
facilityItemsJSON.putAt("AM_SHIPMENT", amRouteList) ;
facilityItemsJSON.putAt("PM_SHIPMENT", pmRouteList) ;	
context.facilityItemsJSON = facilityItemsJSON;
