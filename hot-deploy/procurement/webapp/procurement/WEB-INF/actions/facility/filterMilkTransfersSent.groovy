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
List tempUnitList = FastList.newInstance();
List<GenericValue> tempSendList = list;
if(UtilValidate.isNotEmpty(tempSendList)){
	if(UtilValidate.isNotEmpty(context.unitName)){
		tempUnitList.add(context.unitId);
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId",EntityOperator.IN,tempUnitList));
		if(UtilValidate.isNotEmpty(parameters.isMilkRcpt)){
			conditionList.add(EntityCondition.makeCondition("isMilkRcpt",EntityOperator.EQUALS,"Y"));
			}
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		if(UtilValidate.isNotEmpty(tempSendList)){
			tempSendList = EntityUtil.filterByCondition(tempSendList, condition);
		}
	}else if(UtilValidate.isNotEmpty(context.shedName)){
			shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : context.shedId]);
			List unitIds = FastList.newInstance();
			unitIds = shedUnits.unitsList;
			tempUnitList.addAll(unitIds);
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId",EntityOperator.IN,tempUnitList));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			if(UtilValidate.isNotEmpty(tempSendList)){
				tempSendList = EntityUtil.filterByCondition(tempSendList, condition);
			}
	}
		
		context.putAt("listIt", tempSendList);
}