
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;

supplyDate = null;
if(parameters.supplyDate){
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
	try {
		supplyDate = new java.sql.Timestamp(sdf.parse(parameters.supplyDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + supplyDate, "");
	}
}
context.supDate = supplyDate;