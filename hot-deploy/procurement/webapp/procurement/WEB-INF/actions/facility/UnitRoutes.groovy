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

// to get supervisors list for milkline grades report
supervisorsList = [];
supervisorsList = delegator.findList("FacilityFacilityPartyAndPerson",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPERVISOR"),["partyId","firstName","lastName"]as Set,null,null,false);
supervisorList = [];
if(UtilValidate.isNotEmpty(supervisorsList)){
	supervisorsHashList =  new HashSet(supervisorsList);
	for(supervisor in supervisorsHashList){
		supervisorMap = [:];
			name = "";
			if(UtilValidate.isNotEmpty(supervisor.lastName)){
					name = supervisor.lastName;
				}
			if(UtilValidate.isNotEmpty(supervisor.firstName)){
				name = name+" "+supervisor.firstName;
			}
			supervisorMap.put("partyId",supervisor.partyId);
			supervisorMap.put("name",name);
			supervisorList.add(supervisorMap);
		}
	}	
context.putAt("supervisorList", supervisorList);
