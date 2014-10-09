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
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
shedUnitsMap =[:];
shedList =[];
shedUnits = ProcurementNetworkServices.getShedUnits(dctx ,context);
shedUnitsMap = (Map)shedUnits.get("shedUnits");
shedList = (List)shedUnits.get("shedList");
List  unitsList = FastList.newInstance();
context.shedUnitsMap = shedUnitsMap;
context.shedList = shedList;
if(UtilValidate.isNotEmpty(parameters.shedId)){
	context.putAt("shedId", parameters.shedId)
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,context);
	unitsList = (List)shedUnitsMap[parameters.shedId];
}
if(UtilValidate.isNotEmpty(context.shedId)){
	context.putAt("shedId", context.shedId)
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,context);
	unitsList = (List)shedUnitsMap[context.shedId];
}
JSONObject shedUnitsJson = new JSONObject();
Iterator mapIter = shedUnitsMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	shedId =entry.getKey();
	shedUnitMap =[:];
	shedUnitMap = shedUnitsMap[shedId];	
	shedUnitsJson.put(shedId,shedUnitMap);	
}

//shedUnitsJson.put("id", shedUnitsMap);
context.shedUnitsJson = shedUnitsJson;
//Debug.log("shedUnitsJson========="+shedUnitsJson.toString());
routesList =[];
context.put("routesList", routesList);
context.put("unitsList", unitsList);
milkProductsList = [];
if(UtilValidate.isNotEmpty(context.productsList)){
	milkProductsList = context.productsList;
	tempProductMap = [:];
	tempProductMap.productName = "All";
	tempProductMap.brandName = "ALL";
	milkProductsList.add(0,tempProductMap);
}
context.putAt("milkProductsList", milkProductsList);

