
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
conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,"Company"));
List finAccountsList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

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

