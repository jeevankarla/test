package in.vasista.vbiz.humanres;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.TimeDuration;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;

public class EmplLeaveService {
    public static final String module = EmplLeaveService.class.getName();
	
	public static Map<String, Object> getEmployeeLeaveBalance(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();

        Map<String, Object> result = FastMap.newInstance();
        String employeeId = (String) context.get("employeeId");
        try{		
        	List conditionList = UtilMisc.toList(
				EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));		
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"APPROVED"));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	List<GenericValue> periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);

        	List periodIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "customTimePeriodId", true);
        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, periodIds));
        	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List customTimePeriods = delegator.findList("CustomTimePeriod", condition, null, UtilMisc.toList("-thruDate"), null, true);
        	if (customTimePeriods != null && customTimePeriods.size() > 0) {
        		GenericValue latestHRPeriod = EntityUtil.getFirst(customTimePeriods);
        		result.put("leaveBalanceDate", latestHRPeriod.get("thruDate"));
                Map<String, Object> leaveBalancesMap = FastMap.newInstance();
            	conditionList.clear();
            	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, latestHRPeriod.getString("customTimePeriodId")));
            	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
            	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);        		
	            List<GenericValue> leaveBalances = delegator.findList("EmplLeaveBalanceStatus", condition, null, null, null, false);
				for (int i = 0; i < leaveBalances.size(); ++i) {		
					GenericValue leaveBalance = leaveBalances.get(i);
					String leaveTypeId = leaveBalance.getString("leaveTypeId");
					BigDecimal openingBalance = BigDecimal.ZERO;					
					BigDecimal closingBalance = BigDecimal.ZERO;
					
					if (UtilValidate.isNotEmpty(leaveBalance.getBigDecimal("openingBalance"))) {
						openingBalance = leaveBalance.getBigDecimal("openingBalance");
						closingBalance = closingBalance.add(openingBalance);
					}
					if (UtilValidate.isNotEmpty(leaveBalance.getBigDecimal("allotedDays"))) {
						closingBalance = closingBalance.add(leaveBalance.getBigDecimal("allotedDays"));
					}
					if (UtilValidate.isNotEmpty(leaveBalance.getBigDecimal("availedDays"))) {
						closingBalance = closingBalance.subtract(leaveBalance.getBigDecimal("availedDays"));
					}	
					if (UtilValidate.isNotEmpty(leaveBalance.getBigDecimal("encashedDays"))) {
						closingBalance = closingBalance.subtract(leaveBalance.getBigDecimal("encashedDays"));
					}
					if (UtilValidate.isNotEmpty(leaveBalance.getBigDecimal("lapsedDays"))) {
						closingBalance = closingBalance.subtract(leaveBalance.getBigDecimal("lapsedDays"));
					}
					if (UtilValidate.isNotEmpty(leaveBalance.getBigDecimal("adjustedDays"))) {
						closingBalance = closingBalance.add(leaveBalance.getBigDecimal("adjustedDays"));
					}					
					leaveBalancesMap.put(leaveTypeId, closingBalance);
				}
        		result.put("leaveBalances", leaveBalancesMap);
        	}
        } catch (Exception e) {
        	Debug.logError(e, "Error fetching leaves", module);
        	return ServiceUtil.returnError(e.toString());
        }

        return result;        
	}
	
	public static Map<String, Object> fetchLeaveDaysForPeriod(DispatchContext dctx, Map<String, Object> context) {
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				String errorMsg = "fetchLeaveDaysForPeriod failed";
				String partyId = (String) context.get("partyId");	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Timestamp timePeriodStart = (Timestamp)context.get("timePeriodStart");
				Timestamp timePeriodEnd = (Timestamp)context.get("timePeriodEnd");        
				Map<String, Object> serviceResults = ServiceUtil.returnSuccess();
				BigDecimal noOfLeaveDays = BigDecimal.ZERO;
				Map leaveDetailmap = FastMap.newInstance();
				BigDecimal lossOfPayDays = BigDecimal.ZERO;
				try{
					
					List conditionList = UtilMisc.toList(
			            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
					conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			    	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
					List<GenericValue> leaves = delegator.findList("EmplLeave", condition, null, null, null, false);
					serviceResults.put("leaves", leaves);
					for (int i = 0; i < leaves.size(); ++i) {		
						GenericValue leave = leaves.get(i);
						String leaveTypeId =leave.getString("leaveTypeId");
			            Timestamp from = leave.getTimestamp("fromDate");
			            Timestamp thru = leave.getTimestamp("thruDate");			
						if (from.compareTo(timePeriodStart) < 0 || thru.compareTo(timePeriodEnd) > 0) {
							Debug.logError("leave entry ========"+leave, module);
							Debug.logError( errorMsg + ": leave cannot span multiple payroll periods ,employeeId:"+partyId, module);
			            	return ServiceUtil.returnError(errorMsg + ": leave cannot span multiple payroll periods, employeeId:"+partyId, 
			            			null, null, null);				
					 	}
						int intv = (UtilDateTime.getIntervalInDays(from, thru)+1);
						BigDecimal temp = new BigDecimal(intv);	
						
						if(UtilValidate.isEmpty(leaveDetailmap.get(leaveTypeId))){
							leaveDetailmap.put(leaveTypeId,BigDecimal.ZERO);
						}
						if(UtilValidate.isNotEmpty(leave.getBigDecimal("lossOfPayDays"))){
							lossOfPayDays = lossOfPayDays.add(leave.getBigDecimal("lossOfPayDays"));
						}
						leaveDetailmap.put(leaveTypeId, ((BigDecimal)leaveDetailmap.get(leaveTypeId)).add(temp));
						noOfLeaveDays = noOfLeaveDays.add(temp);
					} 
				}catch(Exception e){
					Debug.logError(e, "Error fetching leave days", module);
		        	return ServiceUtil.returnError(e.toString());
				}
				
				serviceResults.put("lossOfPayDays", lossOfPayDays);
				serviceResults.put("leaveDetailmap", leaveDetailmap);
				serviceResults.put("noOfLeaveDays", noOfLeaveDays);
		        return serviceResults; 
			}	
}
