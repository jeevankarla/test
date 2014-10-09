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
List tempReceivedList = FastList.newInstance();
List facilityIdsList = FastList.newInstance();
List facilityIdToList = FastList.newInstance();
if(UtilValidate.isNotEmpty(list)){
	tempReceivedList=list;
	if(UtilValidate.isNotEmpty(context.unitName)){
			facilityIdsList.add(context.unitId);
			List conditionList = FastList.newInstance();
			List conditionList1 = FastList.newInstance();
			List conditionList2 = FastList.newInstance(); 
			conditionList1.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIdsList));
			conditionList1.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
			if(UtilValidate.isNotEmpty(parameters.isMilkRcpt)){
				conditionList1.add(EntityCondition.makeCondition("isMilkRcpt",EntityOperator.EQUALS,"Y"));
				}
			EntityCondition condition1 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
			conditionList2.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,facilityIdsList));
			EntityCondition condition2 = EntityCondition.makeCondition(conditionList2,EntityOperator.AND);
			conditionList.add(condition1);
			conditionList.add(condition2);
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
			tempReceivedList = EntityUtil.filterByCondition(tempReceivedList,condition );
		}else if(UtilValidate.isNotEmpty(context.shedName)){
			shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : context.shedId]);
			List unitIds = FastList.newInstance();
			unitIds = shedUnits.unitsList;
			facilityIdsList.addAll(unitIds);
			List conditionList = FastList.newInstance();
			List conditionList1 = FastList.newInstance();
			List conditionList2 = FastList.newInstance(); 
			conditionList1.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIdsList));
			conditionList1.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
			EntityCondition condition1 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
			conditionList2.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,facilityIdsList));
			EntityCondition condition2 = EntityCondition.makeCondition(conditionList2,EntityOperator.AND);
			conditionList.add(condition1);
			conditionList.add(condition2);
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
			tempReceivedList = EntityUtil.filterByCondition(tempReceivedList,condition );
		}
		
		context.putAt("list", tempReceivedList);
}