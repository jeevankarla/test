import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
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
import org.webslinger.resolver.UtilDateResolver;

import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.party.party.PartyHelper;
/*List vehicleTypesList = FastList.newInstance();

vehicleTypesList = delegator.findList("RoleType",EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"VEHICLE_TYPE"),null,null,null,false);

context.vehicleTypesList=vehicleTypesList;*/

List vehicleIds = FastList.newInstance();

List vehicleRole = delegator.findList("VehicleRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"),UtilMisc.toSet("vehicleId"),null,null,false);
vehicleIds = EntityUtil.getFieldListFromEntityList(vehicleRole,"vehicleId", true);

ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("vehicleId",EntityOperator.IN,vehicleIds),
	                                 EntityCondition.makeCondition("vehicleCapacity",EntityOperator.NOT_EQUAL,null),
									 EntityCondition.makeCondition("vehicleCapacity",EntityOperator.GREATER_THAN,BigDecimal.ZERO)],EntityOperator.AND);
List vehicleCapacitys = delegator.findList("Vehicle",ecl,UtilMisc.toSet("vehicleCapacity"),null,null,false);
context.vehicleCapacitys=EntityUtil.getFieldListFromEntityList(vehicleCapacitys, "vehicleCapacity", true);

//ContractorJSON
JSONArray contractorJSON = new JSONArray();
JSONObject contracterNameObj = new JSONObject();
List contractorsList = delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"Contractor"),UtilMisc.toSet("partyId","roleTypeId"),null,null,false);
contractorsList.each{party->
	JSONObject contracter = new JSONObject();
	String partyId = party.partyId;
	partyName = PartyHelper.getPartyName(delegator, partyId, false);
	contracter.put("value",partyId);
	contracter.put("label",partyName);
	contracterNameObj.put(partyId, partyName);
	contractorJSON.add(contracter);
}

context.contracterNameObj=contracterNameObj;
context.contractorJSON = contractorJSON;





