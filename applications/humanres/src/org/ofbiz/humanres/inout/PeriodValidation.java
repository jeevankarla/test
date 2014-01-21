package org.ofbiz.humanres.inout;

import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

public class PeriodValidation {
	
				public static final String module = PunchService.class.getName();
				
			public static Map<String, Object> checkPeriod(DispatchContext dctx, Map<String, Object> context) throws Exception{
				 Delegator delegator = dctx.getDelegator();
				 //LocalDispatcher dispatcher=dctx.getDispatcher();
				 Date fromDate= (java.sql.Date) context.get("fromDate");
				 Date thruDate= (java.sql.Date) context.get("thruDate");				 
				try {
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("periodTypeId" , "HR_MONTH"));
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,fromDate));
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod", condition, null, null, null, false);
					Debug.logInfo("CustomTimePeriod==========..........."+ customTimePeriod ,module);
					if(customTimePeriod.size()<1){
						return ServiceUtil.returnError("Calender month doesn't exist in this period");
					}
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,thruDate));
			        condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
			        customTimePeriod = delegator.findList("CustomTimePeriod", condition, null, null, null, false);
			        if(customTimePeriod.size()<1){
			        	return ServiceUtil.returnError("fromDate and thruDate should lie between one HR Calender month");
			        }
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				 //Debug.logInfo("CustomTimePeriod==========..........."+ periods ,module);
				 return ServiceUtil.returnSuccess();
		
			}

}
