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

facilityId = parameters.facilityId;
customTimePeriodId = parameters.customTimePeriodId;
createdByUserLogin = parameters.createdByUserLogin;

List conditionList=[];
if(UtilValidate.isNotEmpty(facilityId)){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
}
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
}
if(UtilValidate.isNotEmpty(createdByUserLogin)){
	conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createdByUserLogin));
}
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"), EntityOperator.OR, EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, null)));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
listIt = delegator.findList("FacilityCustomTimePeriod", condition , null, null, null, false );
context.put("listIt",listIt);