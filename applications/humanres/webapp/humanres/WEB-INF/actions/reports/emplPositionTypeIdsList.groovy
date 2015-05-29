
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import in.vasista.vbiz.humanres.HumanresService;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
JSONArray emplPositionJSON = new JSONArray();

List EmplPositionTypeCondList=[];
EmplPositionTypeList = delegator.findList("EmplPositionType", null , null, null, null, false );

JSONObject emplPosition = new JSONObject();
emplPosition.put("emplPositionTypeId","");
emplPosition.put("description","");
emplPositionJSON.add(emplPosition);
for (int i = 0; i < EmplPositionTypeList.size(); ++i) {
	GenericValue emplPositionType = EmplPositionTypeList.get(i);
	emplPositionTypeId = emplPositionType.getString("emplPositionTypeId");
	description = emplPositionType.getString("description");
	emplPosition.put("emplPositionTypeId", emplPositionTypeId);
	emplPosition.put("description", description);
	emplPositionJSON.add(emplPosition);
}

//Debug.logError("employeesJSON="+employeesJSON,"");
context.emplPositionJSON = emplPositionJSON;

partyId = "";
EmplPositionDetails = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId",parameters.emplPositionId), false);
if(UtilValidate.isNotEmpty(EmplPositionDetails)){
	partyId = EmplPositionDetails.get("partyId");
}
context.put("partyId",partyId);





