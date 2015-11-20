
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
import org.ofbiz.party.party.PartyHelper;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;




String partyId = parameters.partyId;

String facilityPartyName = parameters.PartyName;

String roleTypeId = parameters.roleTypeId;


condList = [];
condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS ,"EMPANELLED_CUSTOMER"));
partyRoleAndPartyDetail = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);

finalPartyDetails = [];

  for (eachList in partyRoleAndPartyDetail) {
	
		tempMap = [:];
		String partyName = PartyHelper.getPartyName(delegator,eachList.partyId,false);
		tempMap.put("partyId", eachList.partyId);
		if(UtilValidate.isNotEmpty(partyName)){
		tempMap.put("partyName", partyName);
		}
		else{
			tempMap.put("partyName", "");
		}
		
		tempMap.put("roleTypeId", eachList.roleTypeId);
		finalPartyDetails.add(tempMap);
}


  finalFilteredList = []as LinkedHashSet;
  
  for (eachOrderList in finalPartyDetails) {
	  
	 if(UtilValidate.isNotEmpty(partyId)){
	  
		  if(partyId.equals(eachOrderList.get("partyId"))){
			  finalFilteredList.add(eachOrderList);
		  }
		 
	 }
	 if(UtilValidate.isNotEmpty(facilityPartyName)){
		 
		 if(facilityPartyName.equals(eachOrderList.get("partyName"))){
			 
			 finalFilteredList.add(eachOrderList);
		 }
		 
	 }
	 if(UtilValidate.isNotEmpty(roleTypeId)){
		 
		 if(roleTypeId.equals(eachOrderList.get("roleTypeId"))){
			 
			 finalFilteredList.add(eachOrderList);
		 }
		 
	 }
	 if(UtilValidate.isEmpty(partyId) && UtilValidate.isEmpty(facilityPartyName) && UtilValidate.isEmpty(roleTypeId)){
		 
		 Debug.log("enter3erer")
		 
		 finalFilteredList.add(eachOrderList);
	 }
 }
  
  tempfinalFilteredList = [];
  tempfinalFilteredList.addAll(finalFilteredList);

context.partyRoleAndPartyDetail = tempfinalFilteredList;