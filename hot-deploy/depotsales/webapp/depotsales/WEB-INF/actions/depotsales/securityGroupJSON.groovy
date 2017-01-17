import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

JSONArray SecurityGroupJSON = new JSONArray();
SecurityGroup = delegator.findList("SecurityGroup", null, null, null, null, false);
for(int i=0; i< SecurityGroup.size();i++){
	eachGroup = SecurityGroup.get(i);
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachGroup.groupId);
	newObj.put("label",eachGroup.groupId+" "+eachGroup.description);
	if(!(eachGroup.groupId).contains("Milk") && !(eachGroup.description).contains("Milk") && !(eachGroup.groupId).contains("MILK") && !(eachGroup.description).contains("Tanker") && !(eachGroup.description).contains("MILK")){
		SecurityGroupJSON.add(newObj);
	}
}
context.SecurityGroupJSON = SecurityGroupJSON;