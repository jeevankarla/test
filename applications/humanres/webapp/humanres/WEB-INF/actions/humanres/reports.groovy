
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;

dctx = dispatcher.getDispatchContext();
orgList=[];
def populateChildren(org, employeeList) {
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["partyIdFrom","groupName"]));
	internalOrgs.each { internalOrg ->
		populateChildren(internalOrg, employeeList);
	}
		
	
	orgList.add(org);
}
customTimePeriodList=[];
customTimePeriodList=delegator.findByAnd("CustomTimePeriod",[periodTypeId:"HR_MONTH"]);
employeeList = [];
internalOrgs=[];
context.internalOrgs=internalOrgs;
context.orgList=orgList;
context.customTimePeriodList=customTimePeriodList;
context.employeeList=employeeList;
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);



