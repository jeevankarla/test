package in.vasista.vbiz.humanres;


import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Services for Party-punches maintenance
 */

public class HumanresPunchService {

	public static final String module = HumanresPunchService.class.getName();
	
	//API Services
	public static Map<String, Object> recordPunch(DispatchContext dctx, Map<String, Object> context) throws Exception{
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher=dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 List punchList = (List)context.get("punchList");
		try {
			for(int i=0;i<punchList.size(); i++){
				Map punchEntry = (Map)punchList.get(i);
				Iterator tempIter = punchEntry.entrySet().iterator();
				GenericValue createEmplPunchRawCtx = delegator.makeValue("EmplPunchRaw");
				
				while (tempIter.hasNext()) {
					Map.Entry tempEntry = (Entry) tempIter.next();
					String key = (String)tempEntry.getKey();
					//String value = (String)tempEntry.getValue(); 
					if(key.equals("partyId") || key.equals("punchDateTime") || key.equals("deviceId")){
						createEmplPunchRawCtx.put(key ,tempEntry.getValue());
						if(key.equals("punchDateTime")){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    			try {
			    				Timestamp punchDateTime = new java.sql.Timestamp(sdf.parse((String)tempEntry.getValue()).getTime());
			    				createEmplPunchRawCtx.put(key ,punchDateTime);
			    			} catch (Exception e) {
			    				Debug.logError("Cannot parse date string: "+tempEntry.getValue()+"==="+e.toString(), module);
				    			return ServiceUtil.returnError("Cannot parse date string"); 
			    			}
			    			
						}
					}
				}//end while
			createEmplPunchRawCtx.put("createdDateTime", UtilDateTime.nowTimestamp());
			delegator.createOrStore(createEmplPunchRawCtx);	
				
	  }//end of loop
			
    } catch (Exception e) {
	   Debug.logError(e, module);
	   return ServiceUtil.returnError(e.getMessage());
	}
    Map result = ServiceUtil.returnSuccess("Records updated successfully");
	return result;

	}
	 //API Services
	public static Map<String, Object> fetchLastPunch(DispatchContext dctx, Map<String, Object> context) throws Exception{
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher=dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 Map result = ServiceUtil.returnSuccess();
		try {
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true);
			List condList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(context.get("deviceId"))){
				condList.add(EntityCondition.makeCondition("deviceId", EntityOperator.EQUALS,context.get("deviceId")));
			}
			if(UtilValidate.isNotEmpty(context.get("partyId"))){
				condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,context.get("partyId")));
			}
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplPunchRaw = delegator.findList("EmplPunchRaw", cond, null, UtilMisc.toList("-createdDateTime","-punchDateTime"),efo, false);
			//Debug.log("emplPunchRaw===="+emplPunchRaw);
			Map  punchEntryMap = FastMap.newInstance();
			GenericValue punchEntry =  EntityUtil.getFirst(emplPunchRaw);
			if(UtilValidate.isNotEmpty(punchEntry)){
				punchEntryMap.put("partyId", punchEntry.getString("partyId"));
				punchEntryMap.put("deviceId", punchEntry.getString("deviceId"));
				punchEntryMap.put("punchDateTime", punchEntry.getTimestamp("punchDateTime"));
			}
			result.put("punchEntry", punchEntryMap);
        } catch (Exception e) {
	   Debug.logError(e, module);
	   return ServiceUtil.returnError(e.getMessage());
	}
   
	return result;

	}
	
	private static GenericValue getShiftTypeByTime(DispatchContext dctx, Map<String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp punchDateTime =  (Timestamp)context.get("punchDateTime");
        Boolean isOutTime = (Boolean)context.get("isOutTime");
        if(UtilValidate.isEmpty(isOutTime)){
        	isOutTime = Boolean.FALSE;
        }
        Locale locale = (Locale) context.get("locale");
        TimeZone timeZone = TimeZone.getDefault();
        
        GenericValue shiftType = null;
		try {
			Time punchTime = UtilDateTime.toSqlTime(UtilDateTime.toDateString(punchDateTime, "HH:mm:ss"));
			List condList = FastList.newInstance();
			
			/*if(isOutTime){
				condList.add(EntityCondition.makeCondition("startTime", EntityOperator.LESS_THAN_EQUAL_TO, punchTime));
			}else{
				condList.add(EntityCondition.makeCondition("endTime", EntityOperator.GREATER_THAN_EQUAL_TO, punchTime));
			}*/
			
			condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null)));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			//punchTime 
			List<GenericValue> workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap", cond, null, UtilMisc.toList("-startTime"),null, false);
		    Time nearestTime = null;
		    
			if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
				nearestTime = (EntityUtil.getFirst(workShiftTypePeriodAndMap)).getTime("startTime");
				if(isOutTime){
					nearestTime = (EntityUtil.getFirst(workShiftTypePeriodAndMap)).getTime("endTime");
				}
				for(GenericValue workShiftTypePeriod : workShiftTypePeriodAndMap){
					Time tempNearestTime = workShiftTypePeriod.getTime("startTime");
					if(isOutTime){
						 tempNearestTime = workShiftTypePeriod.getTime("endTime");
					}
					if((Math.abs(punchTime.getTime()-tempNearestTime.getTime())) <= (Math.abs(punchTime.getTime()-nearestTime.getTime()))){
						nearestTime = tempNearestTime;
					}
				}
				if(isOutTime){
					workShiftTypePeriodAndMap = EntityUtil.filterByAnd(workShiftTypePeriodAndMap, UtilMisc.toMap("endTime",nearestTime));
				}else{
					workShiftTypePeriodAndMap = EntityUtil.filterByAnd(workShiftTypePeriodAndMap, UtilMisc.toMap("startTime",nearestTime));
				}
				
				shiftType = (EntityUtil.getFirst(workShiftTypePeriodAndMap));
				
			}
		   	
			
		}catch(Exception e){
  			Debug.logError("Error updating  Empl Punch :" + e.getMessage(), module);
  		}
    	
    	return shiftType;
 }
	private static GenericValue fetchLastEmplPunch(DispatchContext dctx, Map<String, Object> context) throws Exception{
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher=dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 String partyId =  (String)context.get("partyId");
	     Timestamp punchDateTime =  (Timestamp)context.get("punchDateTime");
		 Map result = ServiceUtil.returnSuccess();
		 GenericValue punchEntry =null;
		try {
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true);
			List condList = FastList.newInstance();

			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			condList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(UtilDateTime.addDaysToTimestamp(punchDateTime, -1))));
			/*if(UtilValidate.isNotEmpty(context.get("PunchType"))){
				condList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS,context.get("PunchType")));
			}*/
			condList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS,"Normal"));
			if(UtilValidate.isNotEmpty(context.get("InOut"))){
				condList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS,context.get("InOut")));
			}
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplPunch = delegator.findList("EmplPunch", cond, UtilMisc.toSet("partyId","punchdate","PunchType","punchtime","InOut"), UtilMisc.toList("-punchdate","-punchtime"),efo, false);
			if(UtilValidate.isNotEmpty(emplPunch)){
			    punchEntry =  EntityUtil.getFirst(emplPunch);
			}
			
       } catch (Exception e) {
		   Debug.logError(e, module);
		   return punchEntry;
       }
		return punchEntry;
	}
	
   private static GenericValue fetchLastEmplShiftDetails(DispatchContext dctx, Map<String, Object> context) throws Exception{
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher=dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 String partyId =  (String)context.get("partyId");
	     Timestamp punchDateTime =  (Timestamp)context.get("punchDateTime");
		 Map result = ServiceUtil.returnSuccess();
		 GenericValue emplShift =null;
		try {
			EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true);
			List condList = FastList.newInstance();

			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			condList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(punchDateTime)));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplShiftList = delegator.findList("EmplDailyAttendanceDetail", cond, UtilMisc.toSet("partyId","date","shiftType"),UtilMisc.toList("-date","-seqId"),efo, false);
			if(UtilValidate.isNotEmpty(emplShiftList)){
				emplShift =  EntityUtil.getFirst(emplShiftList);
			}
			
      } catch (Exception e) {
		   Debug.logError(e, module);
		   return emplShift;
      }
		return emplShift;
	}	
    
  public static Map<String, Object> populateEmplPunch(DispatchContext dctx, Map<String, Object> context) {
	    	Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();    	
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String partyId =  (String)context.get("partyId");
	        Timestamp punchDateTime =  (Timestamp)context.get("punchDateTime");
	        String deviceId = (String)context.get("deviceId");
	        Map result = ServiceUtil.returnSuccess();
	        String shiftTypeId = null;
			try {
				int lateComeMin = 0;
	        	int earlyGoMin =0;
	        	int shiftThreshold = 0;   
	        	 GenericValue tenantLateCome = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_LATE_COME"), false);
	  	    	 if (UtilValidate.isNotEmpty(tenantLateCome)) {
	  	    		lateComeMin = (new Double(tenantLateCome.getString("propertyValue"))).intValue();
	  	    	 }
	  	    	GenericValue tenantEarlyGo = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_EARLY_GO"), false);
	  	    	if (UtilValidate.isNotEmpty(tenantEarlyGo)) {
	  	    		earlyGoMin = (new Double(tenantEarlyGo.getString("propertyValue"))).intValue();
	  	    	}
	  	    	GenericValue tenantShiftThreshold = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_THRESHOLD"), false);
	  	    	if (UtilValidate.isNotEmpty(tenantShiftThreshold)) {
	  	    		shiftThreshold = (new Double(tenantShiftThreshold.getString("propertyValue"))).intValue();
	  	    	}
	  	    	GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",partyId), false);
	  	    	
	  	    	
				Time punchtime = UtilDateTime.toSqlTime(UtilDateTime.toDateString(punchDateTime, "HH:mm:ss"));
				//Debug.log("punchDateTime====="+punchDateTime);
				GenericValue lastEmplPunch = fetchLastEmplPunch(dctx,context);
				//Debug.log("lastEmplPunch====="+lastEmplPunch);
				if(UtilValidate.isNotEmpty(lastEmplPunch)){
					Timestamp lastEmplPunchTime =
						    Timestamp.valueOf(
						        new SimpleDateFormat("yyyy-MM-dd ")
						        .format(lastEmplPunch.getDate("punchdate")) // get the current date as String
						        .concat(lastEmplPunch.getString("punchtime"))        // and append the time
						    );
					//if last punch is earlier than 10 min ignore
					//Debug.log("interval====="+UtilDateTime.getInterval(lastEmplPunchTime,punchDateTime));
					if( UtilDateTime.getInterval(lastEmplPunchTime ,punchDateTime) < 600000){
						Debug.logWarning("Empl Punch entry ignored last punch record less than 10 min", module);
						Debug.log("******* Empl Punch entry ignored last punch record less than  10 min", module);
						return result;
					}
				}
				
				Map emplPunchMap = UtilMisc.toMap("userLogin", userLogin);
				emplPunchMap.put("PunchType", "Normal");
				emplPunchMap.put("punchdate", UtilDateTime.toSqlDate(punchDateTime));
				emplPunchMap.put("punchtime", punchtime);
				emplPunchMap.put("partyId", partyId);
				emplPunchMap.put("InOut", "IN");
				
				context.put("PunchType", "Normal");
				lastEmplPunch = fetchLastEmplPunch(dctx,context);
				context.put("InOut", "IN");
				GenericValue last_In_EmplPunch = fetchLastEmplPunch(dctx,context);
				double shiftTimeGap  = 0;
				//Debug.logInfo("last_In_EmplPunch====="+last_In_EmplPunch,module);
				
				if(UtilValidate.isNotEmpty(last_In_EmplPunch)){
					Timestamp last_In_EmplPunchTime =
						    Timestamp.valueOf(
						        new SimpleDateFormat("yyyy-MM-dd ")
						        .format(last_In_EmplPunch.getDate("punchdate")) // get the current date as String
						        .concat(last_In_EmplPunch.getString("punchtime"))        // and append the time
						    );
					
					//shiftTimeGap = (punchDateTime.getTime()-(last_In_EmplPunchTime).getTime());
					shiftTimeGap   = UtilDateTime.getInterval(last_In_EmplPunchTime,punchDateTime);
					
					Debug.logInfo("interval====="+UtilDateTime.getInterval(last_In_EmplPunchTime,punchDateTime),module);
				}
				/*if(UtilValidate.isNotEmpty(lastEmplPunch)){
					Timestamp lastEmplPunchTime =
						    Timestamp.valueOf(
						        new SimpleDateFormat("yyyy-MM-dd ")
						        .format(lastEmplPunch.getDate("punchdate")) // get the current date as String
						        .concat(lastEmplPunch.getString("punchtime"))        // and append the time
						    );
					
					//shiftTimeGap = (punchDateTime.getTime()-(last_In_EmplPunchTime).getTime());
					shiftTimeGap   = UtilDateTime.getInterval(lastEmplPunchTime,punchDateTime);
					
					Debug.logInfo("interval====="+UtilDateTime.getInterval(lastEmplPunchTime,punchDateTime),module);
				}*/
				
				//Debug.log("shiftTimeGap====="+shiftTimeGap);
				Map employeeDailyAttendanceMap = UtilMisc.toMap("userLogin", userLogin);
				employeeDailyAttendanceMap.put("date", UtilDateTime.toSqlDate(punchDateTime));
				employeeDailyAttendanceMap.put("partyId", partyId);
				GenericValue shiftType = getShiftTypeByTime(dctx,context);
				shiftTypeId = shiftType.getString("shiftTypeId");
				GenericValue lastShiftType =fetchLastEmplShiftDetails(dctx,context);
				//Debug.log("lastShiftType====="+lastShiftType);
				if(UtilValidate.isNotEmpty(lastShiftType) && shiftTimeGap !=0 && shiftTimeGap <= (3600000*shiftThreshold)){
					shiftTypeId = lastShiftType.getString("shiftType");
					employeeDailyAttendanceMap.put("date", lastShiftType.getDate("date"));
					List condList = FastList.newInstance();
					condList.add(EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, shiftTypeId));
					condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null)));
					EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
					//punchTime 
					List<GenericValue> workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap", cond, null, UtilMisc.toList("-startTime"),null, false);
					if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
						shiftType = EntityUtil.getFirst(workShiftTypePeriodAndMap);
					}
					
				}
				//Debug.log("shiftTypeId===="+shiftTypeId);
				
				employeeDailyAttendanceMap.put("shiftType", shiftTypeId);
				double lateMin =0;
				double extraMin =0;
				List shiftCalList = FastList.newInstance();
				shiftCalList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, shiftTypeId));
				shiftCalList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				shiftCalList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(punchDateTime)));
				shiftCalList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(punchDateTime))));
				EntityCondition condShiftCal = EntityCondition.makeCondition(shiftCalList,EntityOperator.AND);
				List<GenericValue> employeeShiftCalendarList = delegator.findList("EmployeeShiftCalendar", condShiftCal, null, UtilMisc.toList("-thruDate"),null, false);
				GenericValue employeeShiftCalendar = EntityUtil.getFirst(employeeShiftCalendarList);
				
				if(UtilValidate.isNotEmpty(lastEmplPunch) && (lastEmplPunch.getString("InOut")).equals("IN") && (shiftTimeGap <= (3600000*shiftThreshold))){
						emplPunchMap.put("InOut", "OUT");
						//here get shiftType for out time
						//context.put("isOutTime", Boolean.TRUE);
						//GenericValue shiftType = getShiftTypeByTime(dctx,context);
						/*Map shiftCtx = UtilMisc.toMap("partyId",partyId);
						shiftCtx.put("punchDateTime", lastEmplPunch.getTimestamp("punchDateTime"));
						lastShiftType =fetchLastEmplShiftDetails(dctx,shiftCtx);*/
						
						if(UtilValidate.isNotEmpty(shiftType)){
							//shiftTypeId = shiftType.getString("shiftTypeId");
							String endTime = shiftType.getString("endTime");
							// here handle employee special shifts
							
							/*if(UtilValidate.isNotEmpty(employeeDetail.getString("shiftType")) && UtilValidate.isNotEmpty(employeeDetail.getString("shiftTypePeriodId")) && shiftTypeId.equalsIgnoreCase(employeeDetail.getString("shiftType"))){
								GenericValue workShiftPeriod = delegator.findOne("WorkShiftPeriod", UtilMisc.toMap("shiftPeriodId",employeeDetail.getString("shiftTypePeriodId")), false);
							    if(UtilValidate.isNotEmpty(workShiftPeriod)){
							    	endTime = workShiftPeriod.getString("endTime");
							    }
							}*/
							
							if(UtilValidate.isNotEmpty(employeeShiftCalendar)){
								GenericValue workShiftPeriod = delegator.findOne("WorkShiftPeriod", UtilMisc.toMap("shiftPeriodId",employeeShiftCalendar.getString("shiftTypePeriodId")), false);
							    if(UtilValidate.isNotEmpty(workShiftPeriod)){
							    	endTime = workShiftPeriod.getString("endTime");
							    }
							}
							//Debug.log("lastEmplPunch====="+lastEmplPunch);
							employeeDailyAttendanceMap.put("date", lastEmplPunch.getDate("punchdate"));
							Timestamp lagPunchTime =
								    Timestamp.valueOf(
								        new SimpleDateFormat("yyyy-MM-dd ")
								        .format(UtilDateTime.toSqlDate(UtilDateTime.getDayStart(punchDateTime))) // get the current date as String
								        .concat(endTime)        // and append the time
								    );
							lagPunchTime = new Timestamp(lagPunchTime.getTime()-(earlyGoMin*60*1000));
							if(punchDateTime.before(lagPunchTime)){
								 lateMin = ((UtilDateTime.getInterval(punchDateTime,lagPunchTime))/(60*1000));
							}else{
								extraMin = ((UtilDateTime.getInterval(lagPunchTime,punchDateTime))/(60*1000))-earlyGoMin;
							}
				      }
					
				}else{
					//here populate shift details(EmplDailyAttendanceDetail)
					
					if(UtilValidate.isNotEmpty(shiftType)){
						//shiftTypeId = shiftType.getString("shiftTypeId");
						String startTime = shiftType.getString("startTime");
						// here handle employee special shifts
						/*if(UtilValidate.isNotEmpty(employeeDetail.getString("shiftType")) && UtilValidate.isNotEmpty(employeeDetail.getString("shiftTypePeriodId")) && shiftTypeId.equalsIgnoreCase(employeeDetail.getString("shiftType"))){
							GenericValue workShiftPeriod = delegator.findOne("WorkShiftPeriod", UtilMisc.toMap("shiftPeriodId",employeeDetail.getString("shiftTypePeriodId")), false);
						    if(UtilValidate.isNotEmpty(workShiftPeriod)){
						    	startTime = workShiftPeriod.getString("startTime");
						    }
						}*/
						if(UtilValidate.isNotEmpty(employeeShiftCalendar)){
							GenericValue workShiftPeriod = delegator.findOne("WorkShiftPeriod", UtilMisc.toMap("shiftPeriodId",employeeShiftCalendar.getString("shiftTypePeriodId")), false);
						    if(UtilValidate.isNotEmpty(workShiftPeriod)){
						    	startTime = workShiftPeriod.getString("startTime");
						    }
						}
						Timestamp lagPunchTime =
							    Timestamp.valueOf(
							        new SimpleDateFormat("yyyy-MM-dd ")
							        .format(UtilDateTime.toSqlDate(UtilDateTime.getDayStart(punchDateTime))) // get the current date as String
							        .concat(startTime)        // and append the time
							    );
						lagPunchTime = new Timestamp(lagPunchTime.getTime()+(lateComeMin*60*1000));
						
						if(punchDateTime.after(lagPunchTime)){
							lateMin = ((UtilDateTime.getInterval(lagPunchTime, punchDateTime))/(60*1000));
						}
						emplPunchMap.put("InOut", "IN");
					}
					if(punchtime.after(shiftType.getTime("endTime"))){
						shiftType = getShiftTypeByTime(dctx,context);
						shiftTypeId = shiftType.getString("shiftTypeId");
						employeeDailyAttendanceMap.put("shiftType", shiftTypeId);
						employeeDailyAttendanceMap.put("date", UtilDateTime.toSqlDate(punchDateTime));
					}
				}
				//Debug.log("lateMin============"+lateMin);
				employeeDailyAttendanceMap.put("lateMin",new BigDecimal(lateMin));
				employeeDailyAttendanceMap.put("extraMin",new BigDecimal(extraMin));
				if(UtilValidate.isNotEmpty(employeeDetail.getString("canteenFacin")) && ("Y").equalsIgnoreCase(employeeDetail.getString("canteenFacin"))){
					employeeDailyAttendanceMap.put("availedCanteen","Y");
				}
				//Debug.log("employeeDailyAttendanceMap============"+employeeDailyAttendanceMap);
				result = dispatcher.runSync("createorUpdateEmployeeDailyAttendance", employeeDailyAttendanceMap);
				if(ServiceUtil.isError(result)){
					Debug.logError(ServiceUtil.getErrorMessage(result), module);
					return result;
				}
				emplPunchMap.put("isManual","N");
				emplPunchMap.put("sourceId",deviceId);
				emplPunchMap.put("shiftType",shiftTypeId);
				result = dispatcher.runSync("emplPunch", emplPunchMap);
				if(ServiceUtil.isError(result)){
					Debug.logError(ServiceUtil.getErrorMessage(result), module);
					return result;
				}
			}catch(Exception e){
	  			Debug.logError("Error updating  Empl Punch :" + e.getMessage(), module);
	  		}
			Map resultMap = ServiceUtil.returnSuccess("service Done successfully");
	    	return resultMap;
	 } 
  
  public static Map<String, Object> populateEmplDailyShiftDetails(DispatchContext dctx, Map<String, Object> context) {
  	   Delegator delegator = dctx.getDelegator();
	   LocalDispatcher dispatcher = dctx.getDispatcher();    	
      GenericValue userLogin = (GenericValue) context.get("userLogin");
      String partyId =  (String)context.get("partyId");
      Date punchdate =  (Date)context.get("punchdate");
      Time punchtime =  (Time)context.get("punchtime");
      String shiftTypeId = (String)context.get("shiftType");
      String inOut = (String)context.get("InOut");
     // Timestamp punchDateTime = UtilDateTime.nowTimestamp();
      Map result = ServiceUtil.returnSuccess();
     // String shiftTypeId = null;
		try {
			int lateComeMin = 0;
			int earlyGoMin =0;
			int shiftThreshold =0;
      	    GenericValue tenantLateCome = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_LATE_COME"), false);
	    	if (UtilValidate.isNotEmpty(tenantLateCome)) {
	    		lateComeMin = (new Double(tenantLateCome.getString("propertyValue"))).intValue();
	    	 }
	    	GenericValue tenantEarlyGo = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_EARLY_GO"), false);
	    	if (UtilValidate.isNotEmpty(tenantEarlyGo)) {
	    		earlyGoMin = (new Double(tenantEarlyGo.getString("propertyValue"))).intValue();
	    	}
	    	GenericValue tenantShiftThreshold = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","HUMANRES", "propertyName","HR_SHIFT_THRESHOLD"), false);
  	    	if (UtilValidate.isNotEmpty(tenantShiftThreshold)) {
  	    		shiftThreshold = (new Double(tenantShiftThreshold.getString("propertyValue"))).intValue();
  	    	}
  	    	Timestamp punchTimestamp =
				    Timestamp.valueOf(
				        new SimpleDateFormat("yyyy-MM-dd ")
				        .format(punchdate) // get the current date as String
				        .concat(punchtime.toString())        // and append the time
				    );
  	    	
  	    	Timestamp punchBeforeDateTime = new Timestamp(punchTimestamp.getTime()-(shiftThreshold*60*60*1000));
  	    	Timestamp punchAfterDateTime = new Timestamp(punchTimestamp.getTime()+(shiftThreshold*60*60*1000));
	    	GenericValue employeeDetail = delegator.findOne("EmployeeDetail", UtilMisc.toMap("partyId",partyId), true);
	    	List condList = FastList.newInstance();
 
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			condList.add(EntityCondition.makeCondition("punchDateTime", EntityOperator.BETWEEN,UtilMisc.toList(punchBeforeDateTime,punchAfterDateTime)));
			condList.add(EntityCondition.makeCondition("PunchType", EntityOperator.IN, UtilMisc.toList("Normal" ,"Ood")));
			
			condList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS,shiftTypeId));
			
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> emplPunch = delegator.findList("EmplPunch", cond, null, UtilMisc.toList("-punchdate","-punchtime"),null, false);
			
			if(UtilValidate.isEmpty(emplPunch)){
				Debug.logWarning("no normal emplPunch====="+emplPunch, module);
            	return result;
            }
			//here handle employee special shifts
			//geting previous day Shift calender also to handle night shift
			List shiftCalList = FastList.newInstance();
			shiftCalList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, shiftTypeId));
			shiftCalList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			shiftCalList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, punchdate));
			shiftCalList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(UtilDateTime.addDaysToTimestamp(Timestamp.valueOf(
					        new SimpleDateFormat("yyyy-MM-dd ")
					        .format(UtilDateTime.toSqlDate(punchdate)) // get the current date as String
					        .concat("00:00:00")        // and append the time
					    ), -1)))));
			EntityCondition condShiftCal = EntityCondition.makeCondition(shiftCalList,EntityOperator.AND);
			//Debug.log("condShiftCal=========="+condShiftCal);
			List<GenericValue> employeeShiftCalendarList = delegator.findList("EmployeeShiftCalendar", condShiftCal, null, UtilMisc.toList("-thruDate"),null, false);
			List dayShiftCalender = EntityUtil.filterByCondition(employeeShiftCalendarList , EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,punchdate) , EntityOperator.AND,
					EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,punchdate),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null))));
			GenericValue employeeShiftCalendar = EntityUtil.getFirst(dayShiftCalender);
			//Debug.log("employeeShiftCalendar=========="+employeeShiftCalendar);
			emplPunch = EntityUtil.filterByCondition(emplPunch, EntityCondition.makeCondition(EntityCondition.makeCondition("PunchType",EntityOperator.EQUALS,"Normal")));
		
			GenericValue shiftType = null;
			condList.clear();
			condList.add(EntityCondition.makeCondition("shiftTypeId", EntityOperator.EQUALS, shiftTypeId));
			/*if(UtilValidate.isEmpty(employeeShiftCalendar)){
				condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null)));
			}*/
			
			
			cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			//punchTime 
			List<GenericValue> workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap", cond, null, UtilMisc.toList("-startTime"),null, false);
			
			if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
				shiftType = EntityUtil.getFirst(EntityUtil.filterByCondition(workShiftTypePeriodAndMap,EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null))));
				if(UtilValidate.isNotEmpty(employeeShiftCalendar)){
					shiftType = EntityUtil.getFirst(EntityUtil.filterByAnd(workShiftTypePeriodAndMap, UtilMisc.toMap("shiftPeriodId",employeeShiftCalendar.getString("shiftTypePeriodId"))));
				}
				
			}
			double lateMin =0;
			double extraMin =0;
			double totalWorkMin =0;
			Date shiftDate = punchdate; 
			
			if(UtilValidate.isEmpty(shiftType)){
				return result;
			}
			   String startTime = shiftType.getString("startTime");
			   String endTime = shiftType.getString("endTime");
			   //Debug.log("startTime=============="+startTime);
			   //Debug.log("end time=============="+endTime);
			   //shiftTypeId = shiftType.getString("shiftTypeId");
				//late coming
			     List<GenericValue> inPunchList = EntityUtil.filterByCondition(emplPunch, EntityCondition.makeCondition(EntityCondition.makeCondition("InOut",EntityOperator.EQUALS,"IN")));
			     Timestamp firstInPunchTime = null;
			     Timestamp lastOutPunchTime = null;
			     if(UtilValidate.isNotEmpty(inPunchList)){
			    	 inPunchList = EntityUtil.orderBy(inPunchList, UtilMisc.toList("punchdate","punchtime"));
			    	 GenericValue firstInPunch = EntityUtil.getFirst(inPunchList);
			    	 firstInPunchTime = firstInPunch.getTimestamp("punchDateTime");
			    	 
			    	 if(firstInPunch.getString("PunchType").equals("Normal")){
			    		 shiftDate = firstInPunch.getDate("punchdate");
			    		 dayShiftCalender = EntityUtil.filterByCondition(employeeShiftCalendarList , EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,shiftDate) , EntityOperator.AND,
			 					EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,shiftDate),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null))));
			    		 employeeShiftCalendar = EntityUtil.getFirst(dayShiftCalender);
			    		 if(UtilValidate.isNotEmpty(employeeShiftCalendar)){
								shiftType = EntityUtil.getFirst(EntityUtil.filterByAnd(workShiftTypePeriodAndMap, UtilMisc.toMap("shiftPeriodId",employeeShiftCalendar.getString("shiftTypePeriodId"))));
								startTime = shiftType.getString("startTime");
								 endTime = shiftType.getString("endTime");
			    		 }else{
			    			 shiftType = EntityUtil.getFirst(EntityUtil.filterByCondition(workShiftTypePeriodAndMap,EntityCondition.makeCondition(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"),EntityOperator.AND ,EntityCondition.makeCondition("isDefault", EntityOperator.NOT_EQUAL, null))));
			    			 startTime = shiftType.getString("startTime");
							 endTime = shiftType.getString("endTime");
			    		 }
			    		 
				    	 Timestamp lagPunchTime =
								    Timestamp.valueOf(
								        new SimpleDateFormat("yyyy-MM-dd ")
								        .format(UtilDateTime.toSqlDate(shiftDate)) // get the current date as String
								        .concat(startTime)        // and append the time
								    );
				    	    if(UtilValidate.isEmpty(employeeShiftCalendar) && UtilValidate.isNotEmpty(employeeDetail.getString("companyBus")) && employeeDetail.getString("companyBus").equalsIgnoreCase("Y")){
				    	    	List<GenericValue> busInTimeList = delegator.findByAnd(
										"DailyBusTimings", UtilMisc.toMap("date",
												shiftDate, "shiftType", shiftType.getString("shiftTypeId")),
										null);
				    	    	busInTimeList = EntityUtil.orderBy(busInTimeList, UtilMisc.toList("-inTime"));
				    	    	GenericValue busInTime = EntityUtil.getFirst(busInTimeList);
				    	    	if(UtilValidate.isNotEmpty(busInTime)){
				    	    		Timestamp busInDateTime = 
				    	    				    Timestamp.valueOf(
											        new SimpleDateFormat("yyyy-MM-dd ")
											        .format(busInTime.getDate("date")) // get the current date as String
											        .concat(busInTime.getString("inTime"))        // and append the time
											    );
				    	    		if(busInDateTime.after(lagPunchTime)){
				    	    			lagPunchTime = busInDateTime;
									}
				    	    	}
				    	    }
							lagPunchTime = new Timestamp(lagPunchTime.getTime()+(lateComeMin*60*1000));
							/*Timestamp punchDateTime =
								    Timestamp.valueOf(
								        new SimpleDateFormat("yyyy-MM-dd ")
								        .format(UtilDateTime.toSqlDate(firstInPunch.getDate("punchdate"))) // get the current date as String
								        .concat(firstInPunch.getString("punchtime"))        // and append the time
								    );*/
							Timestamp punchDateTime = firstInPunch.getTimestamp("punchDateTime");
							//Debug.log("lagPunchTime  :"+lagPunchTime+"  ===punchDateTime :"+punchDateTime);
							if(punchDateTime.after(lagPunchTime)){
								lateMin = ((UtilDateTime.getInterval(lagPunchTime, punchDateTime))/(60*1000));
							}
							emplPunch.remove(firstInPunch);
			    	 }
			    	 
			     }
			    List<GenericValue> outPunchList = EntityUtil.filterByCondition(emplPunch, EntityCondition.makeCondition(EntityCondition.makeCondition("InOut",EntityOperator.EQUALS,"OUT")));
			   if(UtilValidate.isNotEmpty(outPunchList)){
				    outPunchList = EntityUtil.orderBy(outPunchList, UtilMisc.toList("-punchdate","-punchtime"));
			    	 GenericValue lastOutPunch = EntityUtil.getFirst(outPunchList);
			    	 lastOutPunchTime = lastOutPunch.getTimestamp("punchDateTime");
			    	 if(lastOutPunch.getString("PunchType").equals("Normal")){
			    		 Timestamp lagPunchTime =
								    Timestamp.valueOf(
								        new SimpleDateFormat("yyyy-MM-dd ")
								        .format(lastOutPunch.getDate("punchdate")) // get the current date as String
								        .concat(endTime)        // and append the time
								    );
							lagPunchTime = new Timestamp(lagPunchTime.getTime()-(earlyGoMin*60*1000));
							/*Timestamp punchDateTime =
								    Timestamp.valueOf(
								        new SimpleDateFormat("yyyy-MM-dd ")
								        .format(UtilDateTime.toSqlDate(lastOutPunch.getDate("punchdate"))) // get the current date as String
								        .concat(lastOutPunch.getString("punchtime"))        // and append the time
								    );*/
							Timestamp punchDateTime = lastOutPunch.getTimestamp("punchDateTime");
							//Debug.log(" in == lateMin  :"+lateMin);
							//Debug.log(" out == lagPunchTime  :"+lagPunchTime+"  === punchDateTime :"+punchDateTime);
							if(punchDateTime.before(lagPunchTime)){
								 lateMin = lateMin+((UtilDateTime.getInterval(punchDateTime,lagPunchTime))/(60*1000));
							}else{
								extraMin = ((UtilDateTime.getInterval(lagPunchTime,punchDateTime))/(60*1000));
							}
							//Debug.log(" out == lateMin  :"+lateMin);
							emplPunch.remove(lastOutPunch);
			    	 }
			    	 
			   }
			  
			   if(UtilValidate.isNotEmpty(lastOutPunchTime) && UtilValidate.isNotEmpty(firstInPunchTime))
				   	totalWorkMin =((UtilDateTime.getInterval(firstInPunchTime ,lastOutPunchTime))/(60*1000));
			  //calculate latemin for out between shifts
			   if(UtilValidate.isNotEmpty(emplPunch)){
				   for( GenericValue emplPunchEntry : emplPunch){
					   inPunchList = EntityUtil.filterByCondition(emplPunch, EntityCondition.makeCondition(EntityCondition.makeCondition("InOut",EntityOperator.EQUALS,"IN")));
					   inPunchList = EntityUtil.orderBy(inPunchList, UtilMisc.toList("punchdate","punchtime"));
					   outPunchList = EntityUtil.filterByCondition(emplPunch, EntityCondition.makeCondition(EntityCondition.makeCondition("InOut",EntityOperator.EQUALS,"OUT")));
					   outPunchList = EntityUtil.orderBy(outPunchList, UtilMisc.toList("punchdate","punchtime"));
					   GenericValue inPunchEntry = EntityUtil.getFirst(inPunchList);
					   GenericValue outPunchEntry = EntityUtil.getFirst(outPunchList);
					   if(UtilValidate.isNotEmpty(inPunchEntry) && UtilValidate.isNotEmpty(outPunchEntry)){
						   lateMin = lateMin+((UtilDateTime.getInterval(outPunchEntry.getTimestamp("punchDateTime"),inPunchEntry.getTimestamp("punchDateTime")))/(60*1000));
						   totalWorkMin = totalWorkMin -((UtilDateTime.getInterval(outPunchEntry.getTimestamp("punchDateTime"),inPunchEntry.getTimestamp("punchDateTime")))/(60*1000));
						   emplPunch.remove(inPunchEntry);
					       emplPunch.remove(outPunchEntry);
					   }
				   }
				   
			   }
			condList.clear();
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
			condList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS , shiftDate));
	        if(UtilValidate.isNotEmpty(shiftType)){
	        	condList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, shiftTypeId));
	        }
	        
	    	EntityCondition condition=EntityCondition.makeCondition(condList,EntityOperator.AND);
	    	//Debug.log("condition=============="+condition);
			List<GenericValue> emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null, UtilMisc.toList("-seqId"), null, false);
			if(UtilValidate.isNotEmpty(emplDailyAttendanceDetailList)){
				GenericValue employeeDailyAttendance = EntityUtil.getFirst(emplDailyAttendanceDetailList);
				employeeDailyAttendance.set("lateMin",new BigDecimal(lateMin));
				employeeDailyAttendance.set("extraMin",new BigDecimal(extraMin));
				employeeDailyAttendance.set("totalWorkMin",new BigDecimal(totalWorkMin));
				
				if(UtilValidate.isNotEmpty(employeeDetail.getString("canteenFacin")) && ("Y").equalsIgnoreCase(employeeDetail.getString("canteenFacin"))){
					employeeDailyAttendance.set("availedCanteen","Y");
				}
				delegator.store(employeeDailyAttendance);
			}else{
				Map employeeDailyAttendanceMap = UtilMisc.toMap("userLogin", userLogin);
				employeeDailyAttendanceMap.put("date",shiftDate);
				employeeDailyAttendanceMap.put("partyId", partyId);
				employeeDailyAttendanceMap.put("shiftType", shiftTypeId);
				employeeDailyAttendanceMap.put("lateMin",new BigDecimal(lateMin));
				employeeDailyAttendanceMap.put("extraMin",new BigDecimal(extraMin));
				employeeDailyAttendanceMap.put("totalWorkMin",new BigDecimal(totalWorkMin));
				result = dispatcher.runSync("createorUpdateEmployeeDailyAttendance", employeeDailyAttendanceMap);
			}
			
			if(ServiceUtil.isError(result)){
				Debug.logError(ServiceUtil.getErrorMessage(result), module);
				return result;
			}
			
		}catch(Exception e){
			Debug.logError("Error updating  Empl Punch :" + e.getMessage(), module);
		}
  	return result;
}
  
  public static Map<String, Object> populateEmplPunchFromRawForPeriod(DispatchContext dctx, Map<String, Object> context) throws Exception{
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher=dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 Timestamp fromDate =(Timestamp)context.get("fromDate");
		 Timestamp thruDate =(Timestamp)context.get("thruDate");
		 String orgPartyId =(String)context.get("orgPartyId");
		 Map result = ServiceUtil.returnSuccess();
		 List<GenericValue> employementList = FastList.newInstance();
		try {
			List condList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(orgPartyId)){
				Map emplInputMap = FastMap.newInstance();
				emplInputMap.put("userLogin", userLogin);
				emplInputMap.put("orgPartyId", orgPartyId);
				emplInputMap.put("fromDate", fromDate);
				emplInputMap.put("thruDate", thruDate);
	        	Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
	        	employementList = (List<GenericValue>)resultMap.get("employementList");
	        	if(UtilValidate.isEmpty(employementList)){
	        		Debug.logError("Invalid partyId"+orgPartyId, module);
					return ServiceUtil.returnError("Invalid partyId"+orgPartyId);
	        	}
				condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true)));
			}
			
			condList.add(EntityCondition.makeCondition("punchDateTime", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
			condList.add(EntityCondition.makeCondition("punchDateTime", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruDate)));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplPunchRawList = delegator.findList("EmplPunchRaw", cond, UtilMisc.toSet("partyId","punchDateTime","deviceId"), UtilMisc.toList("punchDateTime"),null, false);
			//Debug.log("emplPunchRaw===="+emplPunchRaw);
			//here first delete shiftdetails and emplPunch
			condList.clear();
            if(UtilValidate.isNotEmpty(employementList)){
            	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true)));
            }
			
			condList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(fromDate)));
			condList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(thruDate)));
			condList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS,"Normal"));
			condList.add(EntityCondition.makeCondition("sourceId", EntityOperator.NOT_EQUAL,null));
			cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplPunch = delegator.findList("EmplPunch", cond, null, null,null, false);
			List<GenericValue> emplPunchNocond = delegator.findList("EmplPunch", null, null, null,null, false);
			emplPunchNocond.removeAll(emplPunch);
			delegator.removeAll(emplPunch);
			//
			
			delegator.removeAll(emplPunchNocond);
			
			condList.clear();
            if(UtilValidate.isNotEmpty(employementList)){
            	condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true)));
            }
			
			condList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.toSqlDate(fromDate)));
			condList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.toSqlDate(thruDate)));
			
			cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplShiftDetails = delegator.findList("EmplDailyAttendanceDetail", cond, null, null,null, false);
			delegator.removeAll(emplShiftDetails);
			
			
			Map  punchEntryMap = UtilMisc.toMap("userLogin",userLogin);
			for(GenericValue emplPunchRaw:emplPunchRawList){
				punchEntryMap.putAll(emplPunchRaw);
				result = dispatcher.runSync("populateEmplPunch", punchEntryMap);
				if(ServiceUtil.isError(result)){
					Debug.logError(ServiceUtil.getErrorMessage(result), module);
					return result;
				}
			}
			delegator.storeAll(emplPunchNocond);
     } catch (Exception e) {
	   Debug.logError(e, module);
	   return ServiceUtil.returnError(e.getMessage());
	}
    result = ServiceUtil.returnSuccess();
	return result;

	}
  
  
  public static Map<String, Object> createorUpdateEmployeeDailyAttendance(DispatchContext dctx, Map<String, ? extends Object> context){
	    Delegator delegator = dctx.getDelegator();
      LocalDispatcher dispatcher = dctx.getDispatcher();
      GenericValue userLogin = (GenericValue) context.get("userLogin");
      String partyId = (String) context.get("partyId");
      String availedVehicleAllowance = (String)context.get("availedVehicleAllowance");
      String availedCanteen = (String)context.get("availedCanteen");
      String shiftType = (String)context.get("shiftType");
      Date date = (java.sql.Date)context.get("date");
      BigDecimal lateMin= (BigDecimal)context.get("lateMin");
      BigDecimal extraMin= (BigDecimal)context.get("extraMin");
      BigDecimal totalWorkMin = (BigDecimal)context.get("totalWorkMin");
      Locale locale = (Locale) context.get("locale");
      Map result = ServiceUtil.returnSuccess();
      
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS ,partyId));
	        conditionList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS , date));
	        if(UtilValidate.isNotEmpty(shiftType)){
	        	conditionList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS , shiftType));
	        }
	        
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND); 		
			List<GenericValue> emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null, UtilMisc.toList("-seqId"), null, false);
			if(UtilValidate.isEmpty(emplDailyAttendanceDetailList)){
				GenericValue newEntity = delegator.makeValue("EmplDailyAttendanceDetail");
				newEntity.set("partyId", partyId);
				newEntity.set("date", date);
				newEntity.set("availedVehicleAllowance", availedVehicleAllowance);
				newEntity.set("availedCanteen", availedCanteen);
				newEntity.set("shiftType", shiftType);
				newEntity.set("lateMin", lateMin);
				newEntity.set("extraMin", extraMin);
				newEntity.set("totalWorkMin", totalWorkMin);
		        try {		
		        	delegator.setNextSubSeqId(newEntity, "seqId", 5, 1);
		        	delegator.create(newEntity);
		        } catch (Exception e) {
		        	Debug.logError("", module);
		            return ServiceUtil.returnError(e.getMessage());
		        }
			}else{	
				for (int i = 0; i < emplDailyAttendanceDetailList.size(); ++i) {
					GenericValue employShiftDetails = emplDailyAttendanceDetailList.get(i);
					if(UtilValidate.isNotEmpty(employShiftDetails.getBigDecimal("lateMin"))){
						lateMin = lateMin.add(employShiftDetails.getBigDecimal("lateMin"));
					}
					employShiftDetails.set("partyId", employShiftDetails.getString("partyId"));
					employShiftDetails.set("availedVehicleAllowance", availedVehicleAllowance);
					employShiftDetails.set("availedCanteen", availedCanteen);
					employShiftDetails.set("shiftType", shiftType);
					employShiftDetails.set("lateMin", lateMin);
					employShiftDetails.set("extraMin", extraMin);
					employShiftDetails.set("totalWorkMin", totalWorkMin);
					employShiftDetails.store();
				}
			}
				
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
      result = ServiceUtil.returnSuccess("Successfully Updated!!");
      return result;
  }//end of service
	
}