import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

JSONArray headItemsJson = new JSONArray();
JSONObject newObj = new JSONObject();

gender = "";
customTimePeriodId = "";
age = "";
operatorEnumId = "";
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriodId = parameters.customTimePeriodId;
}
if(UtilValidate.isNotEmpty(parameters.gender)){
	gender = parameters.gender;
}
if(UtilValidate.isNotEmpty(parameters.age)){
	age = parameters.age;
}

if(age.equals("below")){
	operatorEnumId = "PRC_LT";
}else{
	operatorEnumId = "PRC_GTE";
}

List taxSlabList=[];
taxSlabList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
taxSlabList.add(EntityCondition.makeCondition("gender", EntityOperator.EQUALS, gender));
taxSlabList.add(EntityCondition.makeCondition("age", EntityOperator.EQUALS, "60"));
taxSlabList.add(EntityCondition.makeCondition("operatorEnumId", EntityOperator.EQUALS, operatorEnumId));
taxCondition=EntityCondition.makeCondition(taxSlabList,EntityOperator.AND);
slabsList = delegator.findList("TaxSlabs", taxCondition , null, null, null, false );
if(UtilValidate.isNotEmpty(slabsList)){
	slabsList.each{ slab->
		totalIncomeFrom = slab.get("totalIncomeFrom");
		totalIncomeTo = slab.get("totalIncomeTo");
		taxPercentage = slab.get("taxPercentage");
		refundAmount = slab.get("refundAmount");
		newObj.put("id",totalIncomeFrom+"["+totalIncomeFrom+"]");
		newObj.put("totalIncomeFrom",totalIncomeFrom);
		newObj.put("totalIncomeTo",totalIncomeTo);
		newObj.put("taxPercentage",taxPercentage);
		newObj.put("refundAmount",refundAmount);
		headItemsJson.add(newObj);
	}
}
context.headItemsJson=headItemsJson;