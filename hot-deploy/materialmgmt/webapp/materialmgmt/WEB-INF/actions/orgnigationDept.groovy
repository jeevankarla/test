
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;

dctx = dispatcher.getDispatchContext();
JSONArray orgJSON = new JSONArray();
//orginazation Dept data
orgList=[];
def populateChildren(org, employeeList) {
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["partyIdFrom","groupName"]));
	internalOrgs.each { internalOrg ->
		populateChildren(internalOrg, employeeList);
	}
	orgList.add(org);
}

employeeList = [];
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);
context.orgList=orgList;
/*orgList.each{eachOrg ->
	JSONObject newPartyObj = new JSONObject();
	newPartyObj.put("value",eachOrg.partyId);
	newPartyObj.put("label",eachOrg.groupName);
	orgJSON.add(newPartyObj);
}
context.orgJSON = orgJSON;
