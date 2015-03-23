
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;

// CHECK TO SEE IF THE ORGANIZATION PARTY HAS INTERNAL ORGANIZATIONS
	

	internalOrgList = delegator.findList("PartyRelationship",
		EntityCondition.makeCondition([partyIdFrom : parameters.organizationPartyId, roleTypeIdTo : 'ORGANIZATION_UNIT', roleTypeIdFrom : 'PARENT_ORGANIZATION', partyRelationshipTypeId : 'GROUP_ROLLUP']),
		null, ['-fromDate'], null, false);
	
	intOrgIdsList = [];
	if(UtilValidate.isNotEmpty(internalOrgList)){
		intOrgIdsList = EntityUtil.getFieldListFromEntityList(internalOrgList, "partyIdTo", true);
	}
	intOrgIdsList.add(organizationPartyId);
	
context.put("intOrgIdsList", intOrgIdsList);
