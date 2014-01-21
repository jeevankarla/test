
import java.sql.Timestamp;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


routeList = [];
cratesCansAccntList = [];
cratesCansMap = [:];
cratesCansList = [];

decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
Timestamp currentDate = UtilDateTime.nowTimestamp();
java.sql.Date supplyDate = new java.sql.Date(currentDate.getTime());

routeList = delegator.findByAnd("Facility", [facilityTypeId : "ROUTE"],["facilityId"]);
if (routeList == null) {
	Debug.logInfo("No Routes Found!","");
	context.errorMessage ="No Routes Found!";
	return;
}
routes = routeList.facilityId;

for (int i=0;i< routes.size();i++){
	route = routes.get(i);
	cratesCansAccntList = delegator.findOne("CratesCansAccnt", [routeId : route, supplyDate: supplyDate], false);
	if(cratesCansAccntList){
		cratesCansMap["routeId"] = route;
		cratesCansMap["cratesSent"] = (cratesCansAccntList.cratesSent)?.setScale(decimals,rounding);
		cratesCansMap["cratesReceived"]= (cratesCansAccntList.cratesReceived)?.setScale(decimals,rounding);
		cratesCansMap["cansSent"] = (cratesCansAccntList.cansSent)?.setScale(decimals,rounding);
		cratesCansMap["cansReceived"]= (cratesCansAccntList.cansReceived)?.setScale(decimals,rounding);
		temp = [:];
		temp.putAll(cratesCansMap);
		cratesCansList.add(temp);
	}else{
		cratesCansMap["routeId"] = route;
		cratesCansMap["cratesSent"] = "";
		cratesCansMap["cratesReceived"]=  "";
		cratesCansMap["cansSent"] = "";
		cratesCansMap["cansReceived"]=  "";
		temp = [:];
		temp.putAll(cratesCansMap);
		cratesCansList.add(temp);
	}
}
context.cratesCansList = cratesCansList;


