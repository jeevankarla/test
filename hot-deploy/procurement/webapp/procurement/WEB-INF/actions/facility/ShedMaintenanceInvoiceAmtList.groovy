import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
shedMaintAmount=0;
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId) && UtilValidate.isNotEmpty(parameters.shedId)){	
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);	
	shedId= parameters.shedId;
		conditionList =[];
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PROC_MRGN")));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shedId)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED")));
		conditionPeriodBill = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		periodBillingList = delegator.findList("PeriodBilling",conditionPeriodBill,null,null,null,false);
	List periodBillingIds = periodBillingList.periodBillingId;
	
	conList=[];
	conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingIds)));
	conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shedId)));
	condition = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List procurementAbstract=FastList.newInstance();
	 procurementAbstract = delegator.findList("ProcurementAbstract",condition,null,null,null,false);
	 if(UtilValidate.isNotEmpty(procurementAbstract)){
		 procurementAbstract.each{ procAbstract->
			 shedMaintAmount=shedMaintAmount+procAbstract.get("grossAmt");
		 }
	 }
	context.putAt("procurementAbstract", procurementAbstract);
}
context.putAt("shedMaintAmount", shedMaintAmount);




