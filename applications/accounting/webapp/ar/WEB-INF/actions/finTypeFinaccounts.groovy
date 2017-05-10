
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.accounting.util.UtilAccounting;
import javolution.util.FastList;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.sql.Date;


JSONObject finAccountTypeJSON = new JSONObject();
conditionList = [];
conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.IN, finAccountTypelist.finAccountTypeId));
//conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,"Company"));

if(parameters.ownerPartyId && parameters.ownerPartyId!=null){
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, parameters.ownerPartyId));
}
conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,"Company"));
List finAccountsList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
context.finAccountsList= finAccountsList;

finAccountTypeMap=[:];
finAccountTypelist.each{ eachfinAcc ->
	finAccountIdlist = EntityUtil.filterByCondition(finAccountsList, EntityCondition.makeCondition(UtilMisc.toMap("finAccountTypeId",eachfinAcc.finAccountTypeId,"organizationPartyId","Company")));
	JSONArray finAccIdsJSON = new JSONArray();
	finAccountIdlist.each{ eachfinId ->
		JSONObject newPMethodObj = new JSONObject();
		newPMethodObj.put("value",eachfinId.finAccountId);
		if(eachfinId.finAccountName){
		newPMethodObj.put("text",eachfinId.finAccountName+"["+eachfinId.finAccountId+"]");
		}else{
		newPMethodObj.put("text",""+"["+eachfinId.finAccountId+"]");
		}
		finAccIdsJSON.add(newPMethodObj);
	}
	finAccountTypeJSON.put(eachfinAcc.finAccountTypeId,finAccIdsJSON);
}
context.finAccountTypeJSON=finAccountTypeJSON;
entryType = parameters.entryType;
List<String> orderBy = UtilMisc.toList("description");

glAccountconditionList = [];
if(entryType && entryType == "Other"){
	glAccountconditionList.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, UtilMisc.toList("EXPENSE","INCOME", "REVENUE", "SGA_EXPENSE")));
	List glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition(glAccountconditionList, EntityOperator.AND), null, orderBy, null, false);
	context.glAccountList = glAccountList;
}
if(entryType && entryType == "AssetLiability"){
	List glAccountClass = delegator.findList("GlAccountClass", EntityCondition.makeCondition("parentClassId", EntityOperator.IN, UtilMisc.toList("ASSET","LIABILITY", "EQUITY")), null, null, null, false);
	if(glAccountClass){
		glClassIdlist= EntityUtil.getFieldListFromEntityList(glAccountClass,"glAccountClassId", true);
		List glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, glClassIdlist), null, orderBy, null, false);
		context.glAccountList = glAccountList;
	}
}