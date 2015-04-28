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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.ParseException;
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
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.inout.PunchService;


public class EmplLeaveService {
    public static final String module = EmplLeaveService.class.getName();
	
	public static Map<String, Object> getEmployeeLeaveBalance(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();

        Map<String, Object> result = FastMap.newInstance();
        String employeeId = (String) context.get("employeeId");
        Date balanceDate = (Date)context.get("balanceDate");
        String flag = (String) context.get("flag");
        if(UtilValidate.isEmpty(balanceDate)){
        	balanceDate = UtilDateTime.toSqlDate(UtilDateTime.nowDate()); 
        }
        String leaveTypeIdCtx = (String)context.get("leaveTypeId");
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
        		result.put("leaveBalanceDate", balanceDate);
        		//result.put("leaveBalanceDateTime", UtilDateTime.(latestHRPeriod.get("fromDate")));
                Map<String, Object> leaveBalancesMap = FastMap.newInstance();
            	conditionList.clear();
            	//conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, latestHRPeriod.getString("customTimePeriodId")));
            	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, balanceDate ));
            	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
            	
            	if(UtilValidate.isNotEmpty(context.get("leaveTypeId"))){
            		if(leaveTypeIdCtx.equals("CML")){
            			conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.IN, UtilMisc.toList("HPL")));
            		}else if(leaveTypeIdCtx.equals("CLP")){
            			conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.IN, UtilMisc.toList("CL")));
            		}else{
            			conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeIdCtx));
            		}
            		
            	}
            	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            List<GenericValue> leaveBalances = delegator.findList("EmplLeaveBalanceStatusAndPeriod", condition, null, UtilMisc.toList("thruDate"), null, false);
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
					if(closingBalance.compareTo(BigDecimal.ZERO) != 0){
						Map leaveCtx = FastMap.newInstance();
						leaveCtx.put("timePeriodStart", UtilDateTime.toTimestamp(leaveBalance.getDate("fromDate")));
						leaveCtx.put("partyId", employeeId);
						leaveCtx.put("leaveTypeId", leaveTypeId);
						if(UtilValidate.isNotEmpty(context.get("createleaveFlag"))){
				        	leaveCtx.put("createleaveFlag", "Y");
				        }
						if(UtilValidate.isNotEmpty(flag) && flag.equals("creditLeaves")){
							leaveCtx.put("timePeriodEnd", UtilDateTime.toTimestamp(balanceDate));
						}
						Map leaveResult = fetchLeaveDaysForPeriod(dctx,leaveCtx);
						if(!ServiceUtil.isError(leaveResult)){
							//result.put("leaveBalanceDate", latestHRPeriod.get("thruDate"));
							Map leaveDetailmap = (Map)leaveResult.get("leaveDetailmap");
							if(UtilValidate.isNotEmpty(leaveDetailmap)){
								closingBalance = closingBalance.subtract((BigDecimal)leaveDetailmap.get(leaveTypeId));
							}
						}
					}
				  if(UtilValidate.isNotEmpty(leaveTypeIdCtx) && (leaveTypeIdCtx.equals("CML") || leaveTypeIdCtx.equals("HPL"))){
					  
					  // closingBalance = closingBalance.divide(new BigDecimal(2), 1, BigDecimal.ROUND_HALF_UP);
					  Map leaveCtx = FastMap.newInstance();
						leaveCtx.put("timePeriodStart", UtilDateTime.toTimestamp(leaveBalance.getDate("fromDate")));
						leaveCtx.put("partyId", employeeId);
						leaveCtx.put("leaveTypeId","CML");
						if(UtilValidate.isNotEmpty(flag) && flag.equals("creditLeaves")){
							leaveCtx.put("timePeriodEnd", UtilDateTime.toTimestamp(balanceDate));
						}
						if(UtilValidate.isNotEmpty(context.get("createleaveFlag"))){
				        	leaveCtx.put("createleaveFlag", "Y");
				        }
						Map leaveResult = fetchLeaveDaysForPeriod(dctx,leaveCtx);
						if(!ServiceUtil.isError(leaveResult)){
							//result.put("leaveBalanceDate", latestHRPeriod.get("thruDate"));
							Map leaveDetailmap = (Map)leaveResult.get("leaveDetailmap");
							if(UtilValidate.isNotEmpty(leaveDetailmap)){
								closingBalance = closingBalance.subtract(((BigDecimal)leaveDetailmap.get("CML")).multiply(new BigDecimal(2)));
							}
						}
				  }
				  if(UtilValidate.isNotEmpty(leaveTypeIdCtx) && (leaveTypeIdCtx.equals("CL") || leaveTypeIdCtx.equals("CLP"))){
						  
						 // closingBalance = closingBalance.divide(new BigDecimal(2), 1, BigDecimal.ROUND_HALF_UP);
						  Map leaveCtx = FastMap.newInstance();
							leaveCtx.put("timePeriodStart", UtilDateTime.toTimestamp(leaveBalance.getDate("fromDate")));
							leaveCtx.put("partyId", employeeId);
							leaveCtx.put("leaveTypeId","CLP");
							if(UtilValidate.isNotEmpty(flag) && flag.equals("creditLeaves")){
								leaveCtx.put("timePeriodEnd", UtilDateTime.toTimestamp(balanceDate));
							}
							if(UtilValidate.isNotEmpty(context.get("createleaveFlag"))){
					        	leaveCtx.put("createleaveFlag", "Y");
					        }
							Map leaveResult = fetchLeaveDaysForPeriod(dctx,leaveCtx);
							if(!ServiceUtil.isError(leaveResult)){
								//result.put("leaveBalanceDate", latestHRPeriod.get("thruDate"));
								Map leaveDetailmap = (Map)leaveResult.get("leaveDetailmap");
								if(UtilValidate.isNotEmpty(leaveDetailmap)){
									closingBalance = closingBalance.subtract(((BigDecimal)leaveDetailmap.get("CLP")));
								}
							}
					  }
				  if(UtilValidate.isNotEmpty(leaveTypeIdCtx) && leaveTypeIdCtx.equals("CML")){
					   closingBalance = closingBalance.divide(new BigDecimal(2), 0, BigDecimal.ROUND_DOWN);
					   leaveBalancesMap.put(leaveTypeIdCtx, closingBalance);
				   }else if(UtilValidate.isNotEmpty(leaveTypeIdCtx) && leaveTypeIdCtx.equals("CLP")){
					   leaveBalancesMap.put(leaveTypeIdCtx, closingBalance);
				   }else{
					   leaveBalancesMap.put(leaveTypeId, closingBalance);
				   }
					
				}
				result.put("leaveBalances", leaveBalancesMap);
				//this is to return date for json request
				result.put("leaveBalanceDateStr",UtilDateTime.toDateString((java.sql.Date)result.get("leaveBalanceDate"),"dd-MM-yyyy"));
        		
        	}
        } catch (Exception e) {
        	Debug.logError(e, "Error fetching leaves", module);
        	return ServiceUtil.returnError(e.toString());
        }
        //Debug.log("result============"+result);
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
				String leaveTypeId = (String)context.get("leaveTypeId");
				Map<String, Object> serviceResults = ServiceUtil.returnSuccess();
				BigDecimal noOfLeaveDays = BigDecimal.ZERO;
				Map leaveDetailmap = FastMap.newInstance();
				BigDecimal lossOfPayDays = BigDecimal.ZERO;
				try{
					
					List conditionList = UtilMisc.toList(
			            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					if(UtilValidate.isNotEmpty(leaveTypeId)){
						conditionList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeId));
					}
					if(UtilValidate.isNotEmpty(context.get("createleaveFlag"))){
						conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.IN, UtilMisc.toList("LEAVE_APPROVED","LEAVE_CREATED")));
					}else{
						conditionList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
					}
					if(UtilValidate.isNotEmpty(timePeriodEnd)){
						conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, timePeriodEnd));
					}
					conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
			    	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, timePeriodStart)));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  		
					List<GenericValue> leaves = delegator.findList("EmplLeave", condition, null, null, null, false);
					serviceResults.put("leaves", leaves);
					for (int i = 0; i < leaves.size(); ++i) {		
						GenericValue leave = leaves.get(i);
						String leaveType =leave.getString("leaveTypeId");
			            Timestamp from = leave.getTimestamp("fromDate");
			            Timestamp thru = UtilDateTime.getDayEnd(leave.getTimestamp("thruDate"));			
						if ( UtilValidate.isNotEmpty(timePeriodEnd) && (from.compareTo(timePeriodStart) < 0 || thru.compareTo(timePeriodEnd) > 0)) {
							Debug.logError("leave entry ========"+leave, module);
							Debug.logError( errorMsg + ": leave cannot span multiple payroll periods ,employeeId:"+partyId, module);
			            	/*return ServiceUtil.returnError(errorMsg + ": leave cannot span multiple payroll periods, employeeId:"+partyId, 
			            			null, null, null);	*/			
					 	}
						int intv = (UtilDateTime.getIntervalInDays(from, thru)+1);
						BigDecimal temp = new BigDecimal(intv);
						if(UtilValidate.isNotEmpty(leave.getString("dayFractionId"))){
							temp = temp.divide(new BigDecimal(2), 1, BigDecimal.ROUND_HALF_UP);
						}
						
						if(UtilValidate.isEmpty(leaveDetailmap.get(leaveType))){
							leaveDetailmap.put(leaveType,BigDecimal.ZERO);
						}
						if(UtilValidate.isNotEmpty(leave.getBigDecimal("lossOfPayDays"))){
							lossOfPayDays = lossOfPayDays.add(leave.getBigDecimal("lossOfPayDays"));
						}
						leaveDetailmap.put(leaveType, ((BigDecimal)leaveDetailmap.get(leaveType)).add(temp));
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
	
	public static Map<String, Object> updateGhSsAvailedDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId =  (String)context.get("partyId");
        List chDateStringList =  (List)context.get("chDate");
        String emplLeaveApplId =  (String)context.get("emplLeaveApplId");
        String leaveStatus =  (String)context.get("leaveStatus");
        String serviceName = (String)context.get("serviceName");
        String dayFractionId = (String)context.get("dayFractionId");
		List<GenericValue> holiDayList = FastList.newInstance(); 
		Map result = FastMap.newInstance();
		List<Date> chDateList = FastList.newInstance();
		try {
				List<Date> holidays = FastList.newInstance();
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				if(UtilValidate.isNotEmpty(chDateStringList)){
					for(int i=0;i< chDateStringList.size();i++){
						String chDate = (String)chDateStringList.get(i);
						chDateList.add(UtilDateTime.toSqlDate(sdf.parse(chDate)));
					}
				}
				
				List conList=UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
				
				if(UtilValidate.isNotEmpty(emplLeaveApplId) && UtilValidate.isEmpty(chDateList)){
					conList.add(EntityCondition.makeCondition("emplLeaveApplId",EntityOperator.EQUALS,emplLeaveApplId));
				}else{
					
					conList.add(EntityCondition.makeCondition("date",EntityOperator.IN,chDateList));
				}
				
				EntityCondition con= EntityCondition.makeCondition(conList,EntityOperator.AND);
				List<GenericValue> tempWorkedHolidaysList = delegator.findList("EmplDailyAttendanceDetail", con ,null,UtilMisc.toList("date" ,"partyId"), null, false );
				for(GenericValue workedHoliday : tempWorkedHolidaysList){
					if((UtilValidate.isNotEmpty(leaveStatus) && leaveStatus.equals("LEAVE_REJECTED")) || (UtilValidate.isNotEmpty(serviceName) && serviceName.equals("deleteEmplLeave"))){
						workedHoliday.set("encashmentStatus", null);
						workedHoliday.set("emplLeaveApplId", null);
						
					}else{
						
						workedHoliday.set("encashmentStatus", "LEAVE_ENCASHMENT");
						workedHoliday.set("emplLeaveApplId", emplLeaveApplId);
					}
					
					workedHoliday.store();
				}
				if(UtilValidate.isNotEmpty(chDateStringList)){
					for(int i=0;i< chDateStringList.size();i++){
						String chDate = (String)chDateStringList.get(i);
						Date tempDate = UtilDateTime.toSqlDate(sdf.parse(chDate));
						Map tempDayMap = FastMap.newInstance();
						Map punMap = PunchService.emplDailyPunchReport(dctx, UtilMisc.toMap("partyId", partyId ,"punchDate",tempDate));
						List punchDataList = (List) punMap.get("punchDataList");
						if(UtilValidate.isNotEmpty(punchDataList)){
							//Map punchDetails = (Map)((punchDataList).get(0));
							Map punchDetails =  (Map) punchDataList.get(punchDataList.size()-1);
							if(UtilValidate.isNotEmpty(punchDetails)){
								String totalTime = (String)punchDetails.get("totalTime");
								if(UtilValidate.isNotEmpty(totalTime)){
									totalTime = totalTime.replace(" Hrs", "");
									List<String> timeSplit = StringUtil.split(totalTime, ":");
									if(UtilValidate.isNotEmpty(timeSplit)){
										 int hours = Integer.parseInt(timeSplit.get(0));
										 int minutes = Integer.parseInt(timeSplit.get(1));
										 if(UtilValidate.isEmpty(dayFractionId)){
											 if(((hours*60)+minutes) >=225 && ((hours*60)+minutes) <=465){
												 return ServiceUtil.returnError("Full Day Leave Not Applicable for GH/SS: "+tempDate); 
											 }
										 }
									}
								}
							}
						}
					}
				}
		}catch(Exception e){
  			Debug.logError("Error while updating EmplDailyAttendanceDetail " + e.getMessage(), module);
  			return ServiceUtil.returnError("Error while updating EmplDailyAttendanceDetail ");
  		}
    	
    	//Debug.log("result:" + result, module);		 
    	return result;
    }
	
	
	
	
	public static Map<String, Object> getEmployLeaveValidStatusChange(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = ServiceUtil.returnSuccess();
        String leaveTypeId = (String)context.get("leaveTypeId");
        String statusId = (String)context.get("leaveStatus");
		String approveLevels = null;
		List validStatusChangeList = FastList.newInstance();
		try {
			GenericValue emplLeaveTypeDetails = delegator.findOne("EmplLeaveType",UtilMisc.toMap("leaveTypeId", leaveTypeId), true);
			if(UtilValidate.isNotEmpty(emplLeaveTypeDetails)){
				approveLevels = (String) emplLeaveTypeDetails.get("approveLevels");
			}
			if(UtilValidate.isEmpty(approveLevels)){
				approveLevels = "01";
			}
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS ,"LEAVE_STATUS"));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,statusId));
     		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
     		List<GenericValue> StatusValidChangeList = delegator.findList("StatusValidChangeToDetail", condition, null,null, null, false);
     		for(GenericValue statusValidChange:StatusValidChangeList){
				String conditionExpression = statusValidChange.getString("conditionExpression");
				List<String> condExpSplitStr = StringUtil.split(conditionExpression, "|");
				if(condExpSplitStr.contains(approveLevels)){
					validStatusChangeList.add(statusValidChange);
				}
     		}
		}catch(Exception e){
  			Debug.logError("Error while retrieving status list" + e.getMessage(), module);
  			return ServiceUtil.returnError("Error while getting Employee Leave valid status");
  		}
		result.put("validStatusChangeList",validStatusChangeList);
        return result;
    }
	
	
	public static Map<String, Object> updateEmplLeave(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String)userLogin.get("userLoginId");
        String emplLeaveApplId =  (String)context.get("emplLeaveApplId");
        String leaveStatus =  (String)context.get("leaveStatus");
        String approverPartyId = (String)context.get("approverPartyId");
        String emplLeaveReasonTypeId = (String)context.get("emplLeaveReasonTypeId");
        String dayFractionId = (String)context.get("dayFractionId");
        String leaveTypeId = (String)context.get("leaveTypeId");
        String fromDateStr = (String) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String comment = (String) context.get("comment");
        Timestamp fromDate = null;
        Map<String, Object> result = ServiceUtil.returnSuccess("Leave Updated Sucessfully..!");
		GenericValue emplLeaveDetails = null;
		try {
			 
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
	        if(UtilValidate.isNotEmpty(fromDateStr)){
        	  try {
        		  fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
    		   } catch (ParseException e) {
    			   Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);
    			return ServiceUtil.returnError(e.toString());
    		   }
	        }
		    fromDate = UtilDateTime.getDayStart(fromDate);
		    thruDate = UtilDateTime.getDayEnd(thruDate);
			emplLeaveDetails = delegator.findOne("EmplLeave",UtilMisc.toMap("emplLeaveApplId", emplLeaveApplId), false);
			if(UtilValidate.isNotEmpty(emplLeaveDetails)){
				if(UtilValidate.isNotEmpty(thruDate)){
					emplLeaveDetails.set("thruDate", thruDate);
				}
				if(UtilValidate.isNotEmpty(approverPartyId)){
					emplLeaveDetails.set("approverPartyId", approverPartyId);
				}
				if(UtilValidate.isNotEmpty(dayFractionId)){
					emplLeaveDetails.set("dayFractionId", dayFractionId);
				}
				if(UtilValidate.isNotEmpty(comment)){
					emplLeaveDetails.set("comment", comment);
				}
				if(UtilValidate.isNotEmpty(emplLeaveReasonTypeId)){
					emplLeaveDetails.set("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
				}
				emplLeaveDetails.store();
			}
		}catch(Exception e){
  			Debug.logError("Error while updating emplLeave" + e.getMessage(), module);
  			return ServiceUtil.returnError("Error while updating EmplLeave");
  		}
		result.put("emplLeaveApplId",emplLeaveDetails.getString("emplLeaveApplId"));
		result.put("leaveStatus",emplLeaveDetails.getString("leaveStatus"));
    	return result;
    }
	
	public static Map<String, Object> updateEmplLeaveStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String)userLogin.get("userLoginId");
        String emplLeaveApplId =  (String)context.get("emplLeaveApplId");
        String leaveStatus =  (String)context.get("leaveStatus");
        String approverPartyId = (String)context.get("approverPartyId");
        String levelApproverPartyId = (String)context.get("levelApproverPartyId");
        String leaveTypeId = (String)context.get("leaveTypeId");
        String Date=(String)context.get("thruDate");
        Map<String, Object> result = ServiceUtil.returnSuccess(" "+ leaveStatus + " Sucessfully..!");
		GenericValue emplLeaveDetails = null;
		try {
			 SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		     Timestamp thruDate=UtilDateTime.toTimestamp(sdf.parse(Date));
		     thruDate = UtilDateTime.getDayEnd(thruDate);
			
			if(UtilValidate.isEmpty(leaveStatus.trim())){
				return ServiceUtil.returnError("Leave Status Cannot be Empty "); 
			}
			if(UtilValidate.isEmpty(approverPartyId) && (UtilValidate.isEmpty(levelApproverPartyId))){
				return ServiceUtil.returnError("Approver Party Cannot be Empty "); 
			}
			emplLeaveDetails = delegator.findOne("EmplLeave",UtilMisc.toMap("emplLeaveApplId", emplLeaveApplId), false);
			// Returning error if payroll already generated
			Map customTimePeriodIdMap = PayrollService.checkPayrollGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"date",UtilDateTime.toSqlDate(emplLeaveDetails.getTimestamp("fromDate"))));
			if (ServiceUtil.isError(customTimePeriodIdMap)) {
				return customTimePeriodIdMap;
			}
			if(UtilValidate.isNotEmpty(emplLeaveDetails)){
				emplLeaveDetails.set("leaveStatus", leaveStatus);
				if(UtilValidate.isNotEmpty(thruDate) && !emplLeaveDetails.get("thruDate").equals(thruDate)){
					emplLeaveDetails.set("thruDate", thruDate);
				}
				if(UtilValidate.isNotEmpty(levelApproverPartyId)){
					emplLeaveDetails.set("approverPartyId", levelApproverPartyId);
				}else{
					if(UtilValidate.isNotEmpty(approverPartyId)){
						emplLeaveDetails.set("approverPartyId",approverPartyId);
					}
				}
				emplLeaveDetails.store();
			}
		}catch(Exception e){
  			Debug.logError("Error while updating emplLeaveStatus " + e.getMessage(), module);
  			return ServiceUtil.returnError("Error while updating EmplLeaveStatus ");
  		}
		result.put("emplLeaveApplId",emplLeaveDetails.getString("emplLeaveApplId"));
		result.put("partyId",emplLeaveDetails.getString("partyId"));
		result.put("leaveTypeId",emplLeaveDetails.getString("leaveTypeId"));
		result.put("emplLeaveReasonTypeId",emplLeaveDetails.getString("emplLeaveReasonTypeId"));
		result.put("fromDate",emplLeaveDetails.getTimestamp("fromDate"));
		result.put("thruDate",emplLeaveDetails.getTimestamp("thruDate"));
		result.put("effectedCreditDays",emplLeaveDetails.getBigDecimal("effectedCreditDays"));
		result.put("dayFractionId",emplLeaveDetails.getString("dayFractionId"));
		result.put("appliedBy",emplLeaveDetails.getString("appliedBy"));
		result.put("lossOfPayDays",emplLeaveDetails.getBigDecimal("lossOfPayDays"));
		result.put("approverPartyId",emplLeaveDetails.getString("approverPartyId"));
		result.put("leaveStatus",emplLeaveDetails.getString("leaveStatus"));
		result.put("documentsProduced",emplLeaveDetails.getString("documentsProduced"));
		result.put("description",emplLeaveDetails.getString("description"));
		result.put("comment",emplLeaveDetails.getString("comment"));
    	return result;
    }
	
	
	public static Map<String, Object> storeEmplLeaveStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String)userLogin.get("userLoginId");
        String emplLeaveApplId =  (String)context.get("emplLeaveApplId");
        String leaveStatus =  (String)context.get("leaveStatus");
        String approverPartyId = (String)context.get("approverPartyId");
        String levelApproverPartyId = (String)context.get("levelApproverPartyId");
        String leaveTypeId = "";
        String ownerPartyId = "";
        String dayFractionId = "";
        Timestamp leaveFromDate = UtilDateTime.nowTimestamp();
        Timestamp leaveThruDate = UtilDateTime.nowTimestamp();
		Map result = FastMap.newInstance();
		GenericValue emplLeaveStatusDetails = null; 
  	    Boolean smsFlag = Boolean.FALSE;
		try {
			if(UtilValidate.isEmpty(approverPartyId) && (UtilValidate.isEmpty(levelApproverPartyId))){
				return ServiceUtil.returnError("Approver Party Cannot be Empty "); 
			}
			emplLeaveStatusDetails = delegator.findOne("EmplLeaveStatus",UtilMisc.toMap("emplLeaveApplId", emplLeaveApplId,"leaveStatus",leaveStatus), false);
			GenericValue emplLeaveStatus = delegator.makeValue("EmplLeaveStatus");
		    emplLeaveStatus.set("emplLeaveApplId", emplLeaveApplId );
		    emplLeaveStatus.set("leaveStatus", leaveStatus);
		    if(UtilValidate.isNotEmpty(levelApproverPartyId)){
		    	emplLeaveStatus.set("approverPartyId", levelApproverPartyId);
			}else{
				if(UtilValidate.isNotEmpty(approverPartyId)){
					emplLeaveStatus.set("approverPartyId",approverPartyId);
				}
			}
		    emplLeaveStatus.set("changedByUserLogin", userLoginId); 
		    emplLeaveStatus.set("changedDate", UtilDateTime.nowTimestamp());
		    delegator.createOrStore(emplLeaveStatus);    
		    
			GenericValue emplLeaveDetails = delegator.findOne("EmplLeave",UtilMisc.toMap("emplLeaveApplId", emplLeaveApplId), false);
			if(UtilValidate.isNotEmpty(emplLeaveDetails)){
				ownerPartyId = emplLeaveDetails.getString("partyId");				
				leaveTypeId = emplLeaveDetails.getString("leaveTypeId");
				dayFractionId = emplLeaveDetails.getString("dayFractionId");				
	            leaveFromDate = emplLeaveDetails.getTimestamp("fromDate");
	            leaveThruDate = emplLeaveDetails.getTimestamp("thruDate");				
			}
		    GenericValue tenantConfigEnableIndentSms = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","SMS", "propertyName","enableLeaveSms"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableIndentSms) && (tenantConfigEnableIndentSms.getString("propertyValue")).equals("Y")) {
				 smsFlag = Boolean.TRUE;
			}
		}catch(Exception e){
  			Debug.logError("Error while updating emplLeaveStatus " + e.getMessage(), module);
  			return ServiceUtil.returnError("Error while updating EmplLeaveStatus ");
  		}
		result.put("ownerPartyId", ownerPartyId);
		result.put("leaveTypeId", leaveTypeId);
		result.put("dayFractionId", dayFractionId);		
		result.put("leaveFromDate", leaveFromDate);
		result.put("leaveThruDate", leaveThruDate);
		result.put("approverPartyId", approverPartyId);
		result.put("leaveStatus", leaveStatus);	
		result.put("smsFlag", smsFlag);		
    	return result;
    }

    public static Map<String, Object>  sendLeaveStatusSms(DispatchContext dctx, Map<String, Object> context)  {
        GenericValue userLogin = (GenericValue) context.get("userLogin");      
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();  
        Map<String, Object> serviceResult;
        String ownerPartyId =  (String)context.get("ownerPartyId");
        String leaveTypeId =  (String)context.get("leaveTypeId"); 
        String dayFractionId =  (String)context.get("dayFractionId");                
        String leaveStatus =  (String)context.get("leaveStatus");
        String approverPartyId =  (String)context.get("approverPartyId");        
        Timestamp leaveFromDate = (Timestamp) context.get("leaveFromDate"); 
        Timestamp leaveThruDate = (Timestamp) context.get("leaveThruDate"); 
        String leaveFromDateStr = "";
        if (UtilValidate.isNotEmpty(leaveFromDate)) {
        	leaveFromDateStr = UtilDateTime.toDateString(leaveFromDate, "dd MMM");
        }
        String leaveThruDateStr = "";        
        if (UtilValidate.isNotEmpty(leaveThruDate)) {
        	leaveThruDateStr = UtilDateTime.toDateString(leaveThruDate, "dd MMM");
        	if (UtilValidate.isNotEmpty(dayFractionId)) {
        		leaveThruDateStr = leaveThruDateStr + " " + dayFractionId;
        	}
        }        
        String text;
        String smsPartyId;
        if (leaveStatus.equals("LEAVE_APPROVED")) {
        	text = "Your leave has been approved. (" + leaveTypeId + "; " +
        			leaveFromDateStr + " - " +
        			leaveThruDateStr + ")";
        	smsPartyId = ownerPartyId;
        }
        else if (leaveStatus.equals("LEAVE_REJECTED")) {
        	text = "Your leave has been rejected. (" + leaveTypeId + "; " +
        			leaveFromDateStr + " - " +
        			leaveThruDateStr + ")";
        	smsPartyId = ownerPartyId;        	
        }
        else {
			String employeeName = PartyHelper.getPartyName(delegator,
					ownerPartyId, false);
        	text = "You have a leave approval request. (" + ownerPartyId + 
        			" " + employeeName + "; " +
        			 leaveTypeId + "; " +
         			leaveFromDateStr + " - " +
         			leaveThruDateStr + ")";
        	smsPartyId = approverPartyId;        	
        }      
        
		try {
            Map<String, Object> getTelParams = FastMap.newInstance();
            getTelParams.put("partyId", smsPartyId);
            getTelParams.put("userLogin", userLogin);                    	
            serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
            if (ServiceUtil.isError(serviceResult)) {
            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
            	return ServiceUtil.returnSuccess();
            } 
            if(UtilValidate.isEmpty(serviceResult.get("contactNumber"))){
            	Debug.logError( "No  contactNumber found for employee : "+smsPartyId, module);
            	return ServiceUtil.returnSuccess();
            }
            String contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");            
            Map<String, Object> sendSmsParams = FastMap.newInstance();      
            sendSmsParams.put("contactNumberTo", contactNumberTo);          
            sendSmsParams.put("text", text);             
            serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);       
            if (ServiceUtil.isError(serviceResult)) {
            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
            	return ServiceUtil.returnSuccess();
            }               
            Debug.logInfo("text: " + text + " : " + smsPartyId + " : " + contactNumberTo, module);            
		}
		catch (Exception e) {
			Debug.logError(e, "Problem sending leave status sms", module);
			return ServiceUtil.returnError(e.getMessage());
		}       
        return ServiceUtil.returnSuccess();
    }
    public static Map<String, Object> getLeaveTypeValidRules(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String leaveTypeId = (String) context.get("leaveTypeId");
    	String dayFractionId = (String) context.get("dayFractionId");
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue EmplLeaveTypeDetails = null;
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = new Locale("en","IN");
		TimeZone timeZone = TimeZone.getDefault();
    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
    	Timestamp thruDateEnd = UtilDateTime.getDayEnd(thruDate);
    	int intervalDays = (UtilDateTime.getIntervalInDays(fromDateStart, thruDateEnd)+1);
    	int maxFullDayHours = (intervalDays*24);
    	int maxHalfDayHours = 0;
    	if(UtilValidate.isNotEmpty(dayFractionId)){
    		maxHalfDayHours = (intervalDays*12);
    	}
    	Map<String, Object> serviceResult = ServiceUtil.returnSuccess();
		try {
			//EmplLeaveTypeDetails = delegator.findOne("EmplLeaveType",UtilMisc.toMap("leaveTypeId", leaveTypeId), false);
			if(UtilValidate.isNotEmpty(leaveTypeId)){
				Map validLeaveRulesMap = FastMap.newInstance();
				validLeaveRulesMap.put("userLogin",userLogin);
				validLeaveRulesMap.put("leaveTypeId",leaveTypeId);
				validLeaveRulesMap.put("maxFullDayHours",maxFullDayHours);
				validLeaveRulesMap.put("maxHalfDayHours",maxHalfDayHours);
				//Half Pay Leave Rules
				if(UtilValidate.isNotEmpty(leaveTypeId) && (leaveTypeId.equals("HPL") || leaveTypeId.equals("CML"))){
					try{
						serviceResult = dispatcher.runSync("getHalfPayLeaveRules", validLeaveRulesMap);
			            if (ServiceUtil.isError(serviceResult)) {
			            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
			            	return ServiceUtil.returnSuccess();
			            } 
					}catch(Exception e){
						Debug.logError("Error while getting Half Pay Leave Type Rules"+e.getMessage(), module);
					}
				}
				//Earned Leave Rules
				if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("EL")){
					try{
						serviceResult = dispatcher.runSync("getEarnedLeaveRules", validLeaveRulesMap);
			            if (ServiceUtil.isError(serviceResult)) {
			            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
			            	return ServiceUtil.returnSuccess();
			            } 
					}catch(Exception e){
						Debug.logError("Error while getting Earned Leave Type Rules"+e.getMessage(), module);
					}
				}
				//Casual Leave Rules
				if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("CL")){
					try{
						serviceResult = dispatcher.runSync("getCasualLeaveRules", validLeaveRulesMap);
			            if (ServiceUtil.isError(serviceResult)) {
			            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
			            	return ServiceUtil.returnSuccess();
			            } 
					}catch(Exception e){
						Debug.logError("Error while getting Casual Leave Type Rules"+e.getMessage(), module);
					}
				}
				//Paternity Leave Rules
				if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("PL")){
					try{
						serviceResult = dispatcher.runSync("getPaternityLeaveRules", validLeaveRulesMap);
			            if (ServiceUtil.isError(serviceResult)) {
			            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
			            	return ServiceUtil.returnSuccess();
			            } 
					}catch(Exception e){
						Debug.logError("Error while getting Paternity Leave Type Rules"+e.getMessage(), module);
					}
				}
			}
		}catch(Exception e){
			Debug.logError("Error while getting Leave Type Details"+e.getMessage(), module);
		}
        return result;
    }
    // Half Pay Leave Rules
    public static Map<String, Object> getHalfPayLeaveRules(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String leaveTypeId = (String) context.get("leaveTypeId");
        int maxFullDayHours = (Integer) context.get("maxFullDayHours");
        int maxHalfDayHours = (Integer) context.get("maxHalfDayHours");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if(UtilValidate.isNotEmpty(leaveTypeId)){
				if(maxFullDayHours<72 || (maxHalfDayHours!=0 && maxHalfDayHours<72)){
					return ServiceUtil.returnError("You have to apply minimum 3 Full days or 6 Half Days for leave type : "+leaveTypeId); 
				}
			}
		}catch(Exception e){
			Debug.logError("Error while getting valid rules for leave type"+e.getMessage(), module);
		}
		return result;
    }
    // Earned Leave Rules
    public static Map<String, Object> getEarnedLeaveRules(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String leaveTypeId = (String) context.get("leaveTypeId");
        int maxFullDayHours = (Integer) context.get("maxFullDayHours");
        int maxHalfDayHours = (Integer) context.get("maxHalfDayHours");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if(UtilValidate.isNotEmpty(leaveTypeId)){
				if(maxFullDayHours<120 || (maxHalfDayHours!=0 && maxHalfDayHours<120)){
					return ServiceUtil.returnError("You have to apply minimum 5 days for leave type : Earned Leave"); 
				}
			}
		}catch(Exception e){
			Debug.logError("Error while getting valid rules for leave type"+e.getMessage(), module);
		}
		return result;
    }
    //Casual Leave Rules
    public static Map<String, Object> getCasualLeaveRules(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String leaveTypeId = (String) context.get("leaveTypeId");
        int maxFullDayHours = (Integer) context.get("maxFullDayHours");
        int maxHalfDayHours = (Integer) context.get("maxHalfDayHours");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if(UtilValidate.isNotEmpty(leaveTypeId)){
				if(maxFullDayHours>240 || (maxHalfDayHours!=0 && maxHalfDayHours>240)){
					return ServiceUtil.returnError("You cannot apply leave more than 10 full days or 20 half days for leave type : Casual Leave");
				}
			}
		}catch(Exception e){
			Debug.logError("Error while getting valid rules for leave type"+e.getMessage(), module);
		}
		return result;
    }
    //Paternity Leave Rules
    public static Map<String, Object> getPaternityLeaveRules(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String leaveTypeId = (String) context.get("leaveTypeId");
        int maxFullDayHours = (Integer) context.get("maxFullDayHours");
        int maxHalfDayHours = (Integer) context.get("maxHalfDayHours");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if(UtilValidate.isNotEmpty(leaveTypeId)){
				if(maxFullDayHours>360 || (maxHalfDayHours!=0 && maxHalfDayHours>360)){
					return ServiceUtil.returnError("You cannot apply leave more than 15 full days or 30 half days for leave type : Paternity Leave");
				}
			}
		}catch(Exception e){
			Debug.logError("Error while getting valid rules for leave type"+e.getMessage(), module);
		}
		return result;
    }
  //Earned Leave,Half Pay Leave and Casual Leave Half Yearly Credit
    public static Map<String, Object> populateELCLAndHPLBalanceCredit(DispatchContext dctx, Map context) {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String partyIdFrom = (String) context.get("partyIdFrom");
	    String leaveTypeId = (String) context.get("leaveTypeId");
	    
	    Map<String, Object> result = ServiceUtil.returnSuccess(" "+ leaveTypeId + " leaves credited sucessfully..!");
	    
	    BigDecimal allotedDays = BigDecimal.ZERO;
	    if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("EL")){
	    	allotedDays = new BigDecimal(15);
	    }
	    if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("HPL")){
	    	allotedDays = new BigDecimal(10);
	    }
	    if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("CL")){
	    	allotedDays = new BigDecimal(15);
	    }
	    
	    
	    Locale locale = new Locale("en","IN");
		TimeZone timeZone = TimeZone.getDefault();
	    Map<String, Object> serviceResult = ServiceUtil.returnSuccess();	
	    Timestamp previousDayEnd = null;
	    Timestamp timePeriodStart = null;
	    Timestamp timePeriodEnd = null;
	    
	    String monthName = null;
	    
	    try {
	        GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	        if (UtilValidate.isEmpty(customTimePeriod)) {
	        	Debug.logError("CustomTimePeriodId is Empty", module);
	        	return ServiceUtil.returnError("CustomTimePeriodId is Empty");
	        }
	        if (UtilValidate.isNotEmpty(customTimePeriod)) {
        		Timestamp fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
        		Timestamp thruDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
        		Timestamp monthStartDate = UtilDateTime.getMonthStart(fromDateTime, timeZone, locale);
        		monthName = UtilDateTime.toDateString(monthStartDate, "MMMM");
        		timePeriodStart = UtilDateTime.getDayStart(fromDateTime);
        		timePeriodEnd = UtilDateTime.getDayEnd(thruDateTime);
        		previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDateTime, -1));
        	}
        }catch (GenericEntityException e) {
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.getMessage());
		}
	    Map emplInputMap = FastMap.newInstance();
		emplInputMap.put("userLogin", userLogin);
		emplInputMap.put("orgPartyId", partyIdFrom);
		emplInputMap.put("fromDate", timePeriodStart);
		emplInputMap.put("thruDate", timePeriodEnd);
    	Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
    	List<GenericValue> activeEmployementList = (List<GenericValue>)resultMap.get("employementList");
    	List<String> partyIdList = EntityUtil.getFieldListFromEntityList(activeEmployementList, "partyIdTo", true);
    	// filtering probationary staff
    	try{
    		if(UtilValidate.isNotEmpty(activeEmployementList)){
        		List probStaffPartyList = FastList.newInstance();
        		List conditionList = FastList.newInstance();
        		conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdList));
            	EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
            	List<GenericValue> employmentList = delegator.findList("Employment",cond, null, UtilMisc.toList("-thruDate"), null, false);
            	if(UtilValidate.isNotEmpty(employmentList)){
            		for (int i = 0; i < employmentList.size(); ++i) {	
            			GenericValue employmentDetails = employmentList.get(i);
            			Timestamp appointmentDate = employmentDetails.getTimestamp("appointmentDate");
            			String partyIdTo = (String) employmentDetails.get("partyIdTo");
            			if(UtilValidate.isNotEmpty(appointmentDate)){
                			Timestamp appointmentDateStart = UtilDateTime.getDayStart(appointmentDate);
                			int intervalDays = (UtilDateTime.getIntervalInDays(appointmentDateStart, timePeriodStart)+1);
                			if(intervalDays < 730){
                				probStaffPartyList.add(partyIdTo);
                			}
            			}
            		}
            		partyIdList.removeAll(probStaffPartyList);
            		List partyClassList = FastList.newInstance();
            		partyClassList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdList));
            		partyClassList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "PROB_STAFF"));
                    EntityCondition partyClassCond = EntityCondition.makeCondition(partyClassList,EntityOperator.AND);
                    List<GenericValue> partyClassificationList = delegator.findList("PartyClassification",partyClassCond, null, UtilMisc.toList("-thruDate"), null, false);
                    List partyClassIdList = EntityUtil.getFieldListFromEntityList(partyClassificationList, "partyId", true);
                    partyIdList.removeAll(partyClassIdList);
            	}
        	}
    	}catch(Exception e){
			Debug.logError("Error while getting Employement"+e.getMessage(), module);
		}
        for (int j = 0; j < partyIdList.size(); ++j) {	
    		String partyId = (String) partyIdList.get(j);
    		try{
        		if(UtilValidate.isNotEmpty(partyId)){
        			Map getEmplLeaveBalMap = FastMap.newInstance();
        			getEmplLeaveBalMap.put("userLogin",userLogin);
        			getEmplLeaveBalMap.put("leaveTypeId",leaveTypeId);
        			getEmplLeaveBalMap.put("employeeId",partyId);
        			getEmplLeaveBalMap.put("balanceDate",new java.sql.Date(previousDayEnd.getTime()));
        			if(UtilValidate.isNotEmpty(getEmplLeaveBalMap)){
        				try{
        					serviceResult = dispatcher.runSync("getEmployeeLeaveBalance", getEmplLeaveBalMap);
        		            if (ServiceUtil.isError(serviceResult)){
        		            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
        		            	return ServiceUtil.returnSuccess();
        		            } 
        	    			Map leaveBalances = (Map)serviceResult.get("leaveBalances");
        	    			BigDecimal leaveClosingBalance = (BigDecimal) leaveBalances.get(leaveTypeId);
        	    			if((leaveTypeId.equals("EL") && leaveClosingBalance.compareTo(new BigDecimal(285)) >0)){
        	    				leaveClosingBalance = new BigDecimal(285);
        	    			}
        	    			if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("EL") || leaveTypeId.equals("HPL")){
        	    				if(UtilValidate.isNotEmpty(customTimePeriodId) && monthName.equals("July") || monthName.equals("January")){
            	    		    	GenericValue emplLeaveBalanceStatus = delegator.findOne("EmplLeaveBalanceStatus",UtilMisc.toMap("partyId",partyId, "leaveTypeId", leaveTypeId, "customTimePeriodId",customTimePeriodId), false);
            	    		    	if(UtilValidate.isEmpty(emplLeaveBalanceStatus)){
            	    		    		if (UtilValidate.isNotEmpty(leaveClosingBalance) && UtilValidate.isNotEmpty(allotedDays)) {
            	    						emplLeaveBalanceStatus = delegator.makeValue("EmplLeaveBalanceStatus");
            	    						emplLeaveBalanceStatus.set("customTimePeriodId",customTimePeriodId);
            	    						emplLeaveBalanceStatus.set("leaveTypeId",leaveTypeId);
            	    						emplLeaveBalanceStatus.set("partyId",partyId);
            	    						emplLeaveBalanceStatus.set("openingBalance",leaveClosingBalance);
                    	    		        emplLeaveBalanceStatus.set("allotedDays",allotedDays);
            		    					emplLeaveBalanceStatus.create();
            	    					}
            	    				}else{
            	    					if(UtilValidate.isNotEmpty(allotedDays)){
            	    		    			emplLeaveBalanceStatus.set("allotedDays",allotedDays);
            		    					emplLeaveBalanceStatus.store();
            	    		    		}
            	    				}
            	    		    }
        	    			}
        	    			if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("CL")){
        	    				if(UtilValidate.isNotEmpty(customTimePeriodId) && monthName.equals("January")){
            	    		    	GenericValue emplLeaveBalanceStatus = delegator.findOne("EmplLeaveBalanceStatus",UtilMisc.toMap("partyId",partyId, "leaveTypeId", leaveTypeId, "customTimePeriodId",customTimePeriodId), false);
            	    		    	if(UtilValidate.isEmpty(emplLeaveBalanceStatus)){
            	    		    		if(UtilValidate.isNotEmpty(allotedDays)) {
            	    						emplLeaveBalanceStatus = delegator.makeValue("EmplLeaveBalanceStatus");
            	    						emplLeaveBalanceStatus.set("customTimePeriodId",customTimePeriodId);
            	    						emplLeaveBalanceStatus.set("leaveTypeId",leaveTypeId);
            	    						emplLeaveBalanceStatus.set("partyId",partyId);
            	    						emplLeaveBalanceStatus.set("openingBalance",BigDecimal.ZERO);
            	    						emplLeaveBalanceStatus.set("allotedDays",allotedDays);
            		    					emplLeaveBalanceStatus.create();
            	    					}
            	    				}else{
            	    					if(UtilValidate.isNotEmpty(allotedDays)){
            	    						emplLeaveBalanceStatus.set("openingBalance",BigDecimal.ZERO);
            	    		    			emplLeaveBalanceStatus.set("allotedDays",allotedDays);
            		    					emplLeaveBalanceStatus.store();
            	    		    		}
            	    				}
            	    		    }
        	    			}
        				}catch(Exception e){
        					Debug.logError("Error while getting Employee Leave Balance"+e.getMessage(), module);
        				}
        			}
        	    }
    		}catch(Exception e){
				Debug.logError("Error while getting Employement"+e.getMessage(), module);
			}
    	}
	    return result;
    }
    public static Map<String, Object> updateEmployeeLeaveBalance(DispatchContext dctx, Map context) {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String partyId = (String) context.get("partyId");
	    String leaveTypeId = (String) context.get("leaveTypeId");
	    
	    Map<String, Object> result = ServiceUtil.returnSuccess(" "+ leaveTypeId + " leave balance updated sucessfully..!");
	    
	    BigDecimal openingBalance = (BigDecimal) context.get("openingBalance");
	    BigDecimal allotedDays = (BigDecimal) context.get("allotedDays");
	    BigDecimal availedDays = (BigDecimal) context.get("availedDays");
	    BigDecimal adjustedDays = (BigDecimal) context.get("adjustedDays");
	    BigDecimal encashedDays = (BigDecimal) context.get("encashedDays");
	    BigDecimal lapsedDays = (BigDecimal) context.get("lapsedDays");
	    
	    Locale locale = new Locale("en","IN");
		TimeZone timeZone = TimeZone.getDefault();
	    Map<String, Object> serviceResult = ServiceUtil.returnSuccess();	
    	try{
    		/*GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			if(UtilValidate.isNotEmpty(fromDateTime)){
    			Map customTimePeriodIdMap = PayrollService.checkPayrollGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"punchdate",fromDateTime));
    			if (ServiceUtil.isError(customTimePeriodIdMap)) {
    				return customTimePeriodIdMap;
    			}
			}*/
    		if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("EL") || leaveTypeId.equals("HPL") || leaveTypeId.equals("CL")){
		    	GenericValue emplLeaveBalanceStatus = delegator.findOne("EmplLeaveBalanceStatus",UtilMisc.toMap("partyId",partyId, "leaveTypeId", leaveTypeId, "customTimePeriodId",customTimePeriodId), false);
		    	if(UtilValidate.isEmpty(emplLeaveBalanceStatus)){
					emplLeaveBalanceStatus = delegator.makeValue("EmplLeaveBalanceStatus");
					emplLeaveBalanceStatus.set("customTimePeriodId",customTimePeriodId);
					emplLeaveBalanceStatus.set("leaveTypeId",leaveTypeId);
					emplLeaveBalanceStatus.set("partyId",partyId);
					if (UtilValidate.isNotEmpty(openingBalance) && (openingBalance.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("openingBalance",openingBalance);
					}
					if (UtilValidate.isNotEmpty(allotedDays) && (allotedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("allotedDays",allotedDays);
					}
					if (UtilValidate.isNotEmpty(availedDays) && (availedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("availedDays",availedDays);
					}
					if (UtilValidate.isNotEmpty(adjustedDays) && (adjustedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("adjustedDays",adjustedDays);
					}
					if (UtilValidate.isNotEmpty(encashedDays) && (encashedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("encashedDays",encashedDays);
					}
					if (UtilValidate.isNotEmpty(lapsedDays) && (lapsedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("lapsedDays",lapsedDays);
					}
					emplLeaveBalanceStatus.create();
				}else{
					if (UtilValidate.isNotEmpty(openingBalance) && (openingBalance.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("openingBalance",openingBalance);
					}
					if (UtilValidate.isNotEmpty(allotedDays) && (allotedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("allotedDays",allotedDays);
					}
					if (UtilValidate.isNotEmpty(availedDays) && (availedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("availedDays",availedDays);
					}
					if (UtilValidate.isNotEmpty(adjustedDays) && (adjustedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("adjustedDays",adjustedDays);
					}
					if (UtilValidate.isNotEmpty(encashedDays) && (encashedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("encashedDays",encashedDays);
					}
					if (UtilValidate.isNotEmpty(lapsedDays) && (lapsedDays.compareTo(BigDecimal.ZERO) >= 0)) {
						emplLeaveBalanceStatus.set("lapsedDays",lapsedDays);
					}
					emplLeaveBalanceStatus.store();
				}
			}
    	}catch(Exception e){
			Debug.logError("Error while getting balance days"+e.getMessage(), module);	
		}
	    return result;
    }
    public static Map<String, Object> checkAvailableLeaveDays(DispatchContext dctx, Map context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	String leaveTypeId = (String) context.get("leaveTypeId");
    	String partyId = (String) context.get("partyId");
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = new Locale("en","IN");
		TimeZone timeZone = TimeZone.getDefault();
    	Timestamp fromDateStart = UtilDateTime.getDayStart(fromDate);
    	Timestamp thruDateEnd = UtilDateTime.getDayEnd(thruDate);
    	int intervalDays = (UtilDateTime.getIntervalInDays(fromDateStart, thruDateEnd)+1);
    	Map<String, Object> serviceResult = ServiceUtil.returnSuccess();
    	
    	Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDateStart, -1));
		try {
			//if(UtilValidate.isNotEmpty(leaveTypeId) && leaveTypeId.equals("EL") || leaveTypeId.equals("HPL") || leaveTypeId.equals("CL")){
				if(UtilValidate.isNotEmpty(partyId)){
	    			Map getEmplLeaveBalMap = FastMap.newInstance();
	    			getEmplLeaveBalMap.put("userLogin",userLogin);
	    			getEmplLeaveBalMap.put("leaveTypeId",leaveTypeId);
	    			getEmplLeaveBalMap.put("employeeId",partyId);
	    			getEmplLeaveBalMap.put("flag","creditLeaves");
	    			getEmplLeaveBalMap.put("createleaveFlag","Y");
	    			getEmplLeaveBalMap.put("balanceDate",new java.sql.Date(previousDayEnd.getTime()));
	    			if(UtilValidate.isNotEmpty(getEmplLeaveBalMap)){
	    				try{
	    					serviceResult = dispatcher.runSync("getEmployeeLeaveBalance", getEmplLeaveBalMap);
	    		            if (ServiceUtil.isError(serviceResult)){
	    		            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	    		            	return ServiceUtil.returnSuccess();
	    		            } 
	    	    			Map leaveBalances = (Map)serviceResult.get("leaveBalances");
	    	    			BigDecimal leaveClosingBalance = (BigDecimal) leaveBalances.get(leaveTypeId);
	    	    			BigDecimal maxIntervalDays = new BigDecimal(intervalDays);
	    	    			if(UtilValidate.isNotEmpty(maxIntervalDays) && ((maxIntervalDays).compareTo(BigDecimal.ZERO) !=0)){
	    	    				if((maxIntervalDays.compareTo(leaveClosingBalance)) >0){
	    	    					return ServiceUtil.returnError("You cannot apply leave more than available leaves for leaveType: "+leaveTypeId);
	    	    				}
	    	    			}
	    				}catch(Exception e){
	        					Debug.logError("Error while getting Employee Leave Balance"+e.getMessage(), module);
	        			}
	    			}
				}
			//}
		}catch(Exception e){
			Debug.logError("Error while getting Leave Type Details"+e.getMessage(), module);
		}
        return result;
    }
}
