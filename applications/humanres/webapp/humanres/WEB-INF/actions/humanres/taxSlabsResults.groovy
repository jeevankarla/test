import java.sql.*;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
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

finYearContext = [:];
finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
finYearContext.put("organizationPartyId", "Company");
finYearContext.put("userLogin", userLogin);
finYearContext.put("findDate", UtilDateTime.nowTimestamp());
finYearContext.put("excludeNoOrganizationPeriods", "Y");
List customTimePeriodList = FastList.newInstance();
Map resultCtx = FastMap.newInstance();
try{
	resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in fetching financial year ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
String finYearId = "";
if(UtilValidate.isNotEmpty(customTimePeriodList)){
	GenericValue fiscalcustomTimePeriod = EntityUtil.getFirst(customTimePeriodList);
	finYearId = (String)fiscalcustomTimePeriod.get("customTimePeriodId");
}
context.finYearId=finYearId;