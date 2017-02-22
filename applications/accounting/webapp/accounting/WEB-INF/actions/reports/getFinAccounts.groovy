import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.util.EntityUtil;

condList = [];
/*condList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));*/
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
if(UtilValidate.isNotEmpty(parameters.ownerPartyId) && parameters.ownerPartyId!=null){
	condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

finAccounts = delegator.findList("FinAccount", cond, null, null, null, false);

finAccountList=[];
finAccounts.each{ eachvalue ->
	if(eachvalue.finAccountName){
		finAccountList.addAll(eachvalue);
	}
}
context.finAccountList = finAccountList;
context.finAccountIdList = finAccountList;


condList.clear();
condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "CASH"));
if(UtilValidate.isNotEmpty(parameters.ownerPartyId) && parameters.ownerPartyId!=null){
	condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

cashAccountList = delegator.findList("FinAccount", cond, null, null, null, false);
context.cashAccountList =cashAccountList; 