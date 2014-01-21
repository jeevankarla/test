
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;



facilityRateMap = [:];
facilityRateList = [];
for (int i=0;i<10;i++){
	facilityRateMap["ProductId"] = "";
	facilityRateMap["Amount"] = "";
	facilityRateMap["fromDate"]= "";
	temp = [:];
	temp.putAll(facilityRateMap);
	facilityRateList.add(temp);
}
context.facilityRateList = facilityRateList;
