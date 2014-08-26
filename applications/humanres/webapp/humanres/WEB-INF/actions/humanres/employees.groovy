
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import in.vasista.vbiz.humanres.HumanresService;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
JSONArray employeesJSON = new JSONArray();
Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
emplInputMap.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()));
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("firstName"));
for (int i = 0; i < employementList.size(); ++i) {
	GenericValue employment = employementList.get(i);
	employeeId = employment.getString("partyIdTo");
	lastName="";
	if(employment.getString("lastName") !=null){
		lastName = employment.getString("lastName");
	}
	name = employment.getString("firstName") + " " + lastName;
	JSONObject employee = new JSONObject();
	employee.put("employeeId", employeeId);
	employee.put("name", name);
	employeesJSON.add(employee);
}

//Debug.logError("employeesJSON="+employeesJSON,"");
context.employeesJSON = employeesJSON;
