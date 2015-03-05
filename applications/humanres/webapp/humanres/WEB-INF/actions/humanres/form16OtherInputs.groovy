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

customTimePeriodId = "";
surchargePercentage = "";
educationalCessPercentage = "";
name = "";
fatherName = "";
designation = "";
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriodId = parameters.customTimePeriodId;
}

from16OtherInputsList = [];
List percentageCondList=[];
percentageCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
percentageCondList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
percentageCondition=EntityCondition.makeCondition(percentageCondList,EntityOperator.AND);
from16OtherInputsList = delegator.findList("TDSRemittances", percentageCondition , null, null, null, false );

context.put("from16OtherInputsList",from16OtherInputsList);

