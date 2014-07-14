package in.vasista.vbiz.humanres;


import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class PunchService {

	public static final String module = PunchService.class.getName();
	
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
	
	private static String getShiftTypeByTime(DispatchContext dctx, Map<String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp punchDateTime =  (Timestamp)context.get("punchDateTime");
        Locale locale = (Locale) context.get("locale");
        TimeZone timeZone = TimeZone.getDefault();
        
        String shiftTypeId = null;
		try {
			Time punchTime = UtilDateTime.toSqlTime(UtilDateTime.toDateString(punchDateTime, "HH:mm:ss"));
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("endTime", EntityOperator.GREATER_THAN_EQUAL_TO, punchTime));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			//punchTime.
			List<GenericValue> workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap", null, null, UtilMisc.toList("-startTime"),null, false);
		    Time nearestTime = null;
			if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
				nearestTime = (EntityUtil.getFirst(workShiftTypePeriodAndMap)).getTime("startTime");
				for(GenericValue workShiftTypePeriod : workShiftTypePeriodAndMap){
					Time tempNearestTime = workShiftTypePeriod.getTime("startTime");
					if((Math.abs(punchTime.getTime()-tempNearestTime.getTime())) <= (Math.abs(punchTime.getTime()-nearestTime.getTime()))){
						nearestTime = tempNearestTime;
					}
					
				}
				workShiftTypePeriodAndMap = EntityUtil.filterByAnd(workShiftTypePeriodAndMap, UtilMisc.toMap("startTime",nearestTime));
				shiftTypeId = (EntityUtil.getFirst(workShiftTypePeriodAndMap)).getString("shiftTypeId");
				
			}
		   	
			
		}catch(Exception e){
  			Debug.logError("Error updating  Empl Punch :" + e.getMessage(), module);
  		}
    	
    	return shiftTypeId;
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
			if(UtilValidate.isNotEmpty(context.get("PunchType"))){
				condList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS,context.get("PunchType")));
			}
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
			condList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS,UtilDateTime.toSqlDate(punchDateTime)));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplShiftList = delegator.findList("EmplDailyAttendanceDetail", cond, UtilMisc.toSet("partyId","date","shiftType"),null,efo, false);
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
	        Map result = ServiceUtil.returnSuccess();
	        Locale locale = (Locale) context.get("locale");
	        String shiftTypeId = null;
			try {
				Time punchtime = UtilDateTime.toSqlTime(UtilDateTime.toDateString(punchDateTime, "HH:mm:ss"));
				//Debug.log("punchDateTime====="+punchDateTime);
				GenericValue lastEmplPunch = fetchLastEmplPunch(dctx,context);
				Debug.logInfo("lastEmplPunch====="+lastEmplPunch,module);
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
						Debug.logWarning("Empl Punch entry ignored last punch record less than min", module);
						Debug.log("Empl Punch entry ignored last punch record less than min", module);
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
				
				
				Debug.logInfo("shiftTimeGap====="+shiftTimeGap,module);
				
				if(UtilValidate.isNotEmpty(lastEmplPunch) && (lastEmplPunch.getString("InOut")).equals("IN") && (shiftTimeGap <= (3600000*12))){
					emplPunchMap.put("InOut", "OUT");
				}else{
					//here populate shift details(EmplDailyAttendanceDetail)
					shiftTypeId = getShiftTypeByTime(dctx,context);
					emplPunchMap.put("InOut", "IN");
					if(UtilValidate.isNotEmpty(shiftTypeId) && (UtilValidate.isEmpty(lastEmplPunch) || (shiftTimeGap >= (3600000*9)) || shiftTimeGap == 0)){
						Map employeeDailyAttendanceMap = UtilMisc.toMap("userLogin", userLogin);
						employeeDailyAttendanceMap.put("shiftType", shiftTypeId);
						employeeDailyAttendanceMap.put("date", UtilDateTime.toSqlDate(punchDateTime));
						employeeDailyAttendanceMap.put("partyId", partyId);
						result = dispatcher.runSync("createorUpdateEmployeeDailyAttendance", employeeDailyAttendanceMap);
						if(ServiceUtil.isError(result)){
							Debug.logError(ServiceUtil.getErrorMessage(result), module);
							return result;
						}
					}
					
				}
				
				result = dispatcher.runSync("emplPunch", emplPunchMap);
				if(ServiceUtil.isError(result)){
					Debug.logError(ServiceUtil.getErrorMessage(result), module);
					return result;
				}
			}catch(Exception e){
	  			Debug.logError("Error updating  Empl Punch :" + e.getMessage(), module);
	  		}
	    	return result;
	 } 
  
  /*public static Map<String, Object> populateEmplPunchFromRawForPeriod(DispatchContext dctx, Map<String, Object> context) throws Exception{
		 Delegator delegator = dctx.getDelegator();
		 LocalDispatcher dispatcher=dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 Timestamp fromDate =(Timestamp)context.get("fromDate");
		 Timestamp thruDate =(Timestamp)context.get("thruDate");
		 Map result = ServiceUtil.returnSuccess();
		try {
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("punchDateTime", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
			condList.add(EntityCondition.makeCondition("punchDateTime", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruDate)));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			
			List<GenericValue> emplPunchRawList = delegator.findList("EmplPunchRaw", cond, UtilMisc.toSet("partyId","punchDateTime"), UtilMisc.toList("-createdDateTime","-punchDateTime"),null, false);
			//Debug.log("emplPunchRaw===="+emplPunchRaw);
			Map  punchEntryMap = UtilMisc.toMap("userLogin",userLogin);
			for(GenericValue emplPunchRaw:emplPunchRawList){
				punchEntryMap.putAll(emplPunchRaw);
				result = dispatcher.runSync("populateEmplPunch", punchEntryMap);
				if(ServiceUtil.isError(result)){
					Debug.logError(ServiceUtil.getErrorMessage(result), module);
					return result;
				}
			}
     } catch (Exception e) {
	   Debug.logError(e, module);
	   return ServiceUtil.returnError(e.getMessage());
	}
    result = ServiceUtil.returnSuccess();
	return result;

	}*/
  
  
  public static Map<String, Object> createorUpdateEmployeeDailyAttendance(DispatchContext dctx, Map<String, ? extends Object> context){
	    Delegator delegator = dctx.getDelegator();
      LocalDispatcher dispatcher = dctx.getDispatcher();
      GenericValue userLogin = (GenericValue) context.get("userLogin");
      String partyId = (String) context.get("partyId");
      String availedVehicleAllowance = (String)context.get("availedVehicleAllowance");
      String availedCanteen = (String)context.get("availedCanteen");
      String shiftType = (String)context.get("shiftType");
      Date date = (java.sql.Date)context.get("date");
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
			List<GenericValue> emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition, null, null, null, false);
			if(UtilValidate.isEmpty(emplDailyAttendanceDetailList)){
				GenericValue newEntity = delegator.makeValue("EmplDailyAttendanceDetail");
				newEntity.set("partyId", partyId);
				newEntity.set("date", date);
				newEntity.set("availedVehicleAllowance", availedVehicleAllowance);
				newEntity.set("availedCanteen", availedCanteen);
				newEntity.set("shiftType", shiftType);
		        try {		
		        	delegator.setNextSubSeqId(newEntity, "seqId", 5, 1);
		        	delegator.create(newEntity);
		        } catch (Exception e) {
		        	Debug.logError("", module);
		            return ServiceUtil.returnError(e.getMessage());
		        }
			}else{	
				for (int i = 0; i < emplDailyAttendanceDetailList.size(); ++i) {
					GenericValue employDetails = emplDailyAttendanceDetailList.get(i);
					employDetails.set("partyId",employDetails.getString("partyId"));
					employDetails.set("availedVehicleAllowance", availedVehicleAllowance);
					employDetails.set("availedCanteen", availedCanteen);
					employDetails.set("shiftType", shiftType);
					employDetails.store();
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