import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.accounting.finaccount.FinAccountServices;


dctx = dispatcher.getDispatchContext();
effectiveDate=null;
fromDate=parameters.eObFromDate;
parameters.fromDate=fromDate;
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy, MMM dd");
try {
	effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(fromDate));
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
}
List finAccountIds=["EMP_CONTRI","EMPR_CONTRI","VPF_CONTRI"];
context.finAccountIds=finAccountIds;
condListb=[];
condListb.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
fieldToSelect = UtilMisc.toSet("partyId");
EntityListIterator partyRoleList = delegator.find("PartyRole",condListb,null,fieldToSelect,null,null);
partyIds=EntityUtil.getFieldListFromEntityListIterator(partyRoleList, "partyId", true);
partyRoleList.close();

condList=[];
condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, partyIds));
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, finAccountIds));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
EntityListIterator finAccountList = delegator.find("FinAccount",cond,null,null,null,null);

iter = finAccountList.iterator();

partyWiseFinMap=[:];
while (iter.hasNext()) {
   finAccount = iter.next();
   finAccTransMap = FinAccountServices.getFinAccountTransOpeningBalances(dctx, UtilMisc.toMap("userLogin",userLogin,"finAccountId",finAccount.finAccountId,"transactionDate",effectiveDate));
   Ob=finAccTransMap.get("openingBalance");
   if( partyWiseFinMap && partyWiseFinMap.get(finAccount.ownerPartyId)){
	   extMap=partyWiseFinMap.get(finAccount.ownerPartyId);
	   extMap.put(finAccount.finAccountId, Ob)
	   partyWiseFinMap.put(finAccount.ownerPartyId, extMap);
	   
   }else{
   tempMap=[:];
   
   tempMap.put(finAccount.finAccountId, Ob);
   partyWiseFinMap.put(finAccount.ownerPartyId, tempMap);
   }
}
context.partyWiseFinMap=partyWiseFinMap;


