
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;



cardMap = [:];
cardList = [];
for (int i=0;i<10;i++){
	cardMap["sNo"] = i+1;
	cardMap["boothId"] = "";
	cardMap["quantity"]= "";
	temp = [:];
	temp.putAll(cardMap);
	cardList.add(temp);
}
context.cardList = cardList;



