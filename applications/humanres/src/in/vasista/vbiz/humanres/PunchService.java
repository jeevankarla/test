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
			//efo.setFetchSize(1);
			EntityCondition cond = null;
			if(UtilValidate.isNotEmpty(context.get("deviceId"))){
				cond = EntityCondition.makeCondition("deviceId", EntityOperator.EQUALS,context.get("deviceId"));
			}
			List<GenericValue> emplPunchRaw = delegator.findList("EmplPunchRaw", cond, null, UtilMisc.toList("-createdDateTime"),efo, false);
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
}