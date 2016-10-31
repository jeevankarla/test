import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

RosHoList = [];
tempMap1 = [:];
if(!newHolidayFlag.equals("Y")){
	tempMap1.put("orgPartyId", "COMPANY");
	tempMap1.put("groupName", "NHDC");
	RosHoList.add(tempMap1);
}
RosHoListIds = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,"Company") , null, null, null, false );
if(UtilValidate.isNotEmpty(RosHoListIds)){
	for(int i=0;i<RosHoListIds.size();i++){
		tempMap = [:];
		RosHoListId = RosHoListIds.get(i);
		partyIdTo = RosHoListId.partyIdTo;
		tempMap.put("orgPartyId", partyIdTo);
		RosHoListNames = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyIdTo) , null, null, null, false );
			if(UtilValidate.isNotEmpty(RosHoListNames)){
			RosHoListName = EntityUtil.getFirst(RosHoListNames);
			groupName = RosHoListName.groupName;
			tempMap.put("groupName", groupName);
		}
		RosHoList.add(tempMap);
	}
}

context.RosHoList = RosHoList;