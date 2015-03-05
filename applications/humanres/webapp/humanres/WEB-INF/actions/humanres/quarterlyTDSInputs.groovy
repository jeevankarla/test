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

quarterPeriod = "";
customTimePeriodId = "";
if(UtilValidate.isNotEmpty(parameters.quarterPeriod)){
	quarterPeriod = parameters.quarterPeriod;
}
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriodId = parameters.customTimePeriodId;
}

List quarterPeriodIdsList=[];
List periodConditionList=[];
periodConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_QUARTER"));
periodConditionList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, customTimePeriodId));
periodCondition=EntityCondition.makeCondition(periodConditionList,EntityOperator.AND);
def orderBy = UtilMisc.toList("fromDate");
quarterlyCustomTimePeriodList = delegator.findList("CustomTimePeriod", periodCondition , null, orderBy, null, false );
if(UtilValidate.isNotEmpty(quarterlyCustomTimePeriodList)){
	quarterlyCustomTimePeriodList.each { period ->
		quarterPeriodId = period.get("customTimePeriodId");
		quarterPeriodIdsList.add(quarterPeriodId);
	}
}
List ConditionList1=[];
ConditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
ConditionList1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, quarterPeriodIdsList));
Condition1 =EntityCondition.makeCondition(ConditionList1,EntityOperator.AND);
TDSRemittancesDetails = delegator.findList("TDSRemittances", Condition1 , null, null, null, false );

context.put("TDSRemittancesDetails",TDSRemittancesDetails);