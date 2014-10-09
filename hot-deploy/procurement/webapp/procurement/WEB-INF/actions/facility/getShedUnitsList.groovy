import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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
import in.vasista.vbiz.procurement.ProcurementServices;
import net.sf.json.JSONObject;

dctx = dispatcher.getDispatchContext();
String shedId = parameters.shedId;
String customTimePeriodId = parameters.customTimePeriodId;
List missingDataList = FastList.newInstance();
if(UtilValidate.isNotEmpty(context.get("shedId"))){
	shedId = context.shedId;
	}
if(UtilValidate.isEmpty(shedId)){
	facilityDetails = delegator.findOne("Facility",[facilityId:parameters.getAt("facilityId")],false);
	shedId = facilityDetails.getAt("parentFacilityId");
	}
Map shedUnits = FastMap.newInstance();
shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : shedId]);
List unitsDetailList = shedUnits.get("unitsDetailList");
context.putAt("unitsList", unitsDetailList);