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

vehicleId=context.eachVehicleId;
if(UtilValidate.isNotEmpty(vehicleId)){
	vehicleRoleList=delegator.findList("VehicleRole",EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , vehicleId), null, null, null, false);
	if(UtilValidate.isNotEmpty(vehicleRoleList)){
		vehicleRoleList =EntityUtil.getFirst(vehicleRoleList);
		partyId=vehicleRoleList.partyId;
		context.contractorId=partyId;
	}
}



