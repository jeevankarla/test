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
import org.ofbiz.party.party.PartyHelper;


partyId = parameters.partyId;
List conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
employmentList = delegator.findList("Employment", condition , null, UtilMisc.toList("-fromDate"), null, false );
organisationList = delegator.findByAnd("PartyRoleAndPartyDetail", [roleTypeId : "INTERNAL_ORGANIZATIO"], ["groupName", "partyId"]);
request.setAttribute("organisationList", organisationList);
if(UtilValidate.isNotEmpty(employmentList)){
	employment = EntityUtil.getFirst(employmentList);
	tempMap = [:];
	partyName = PartyHelper.getPartyName(delegator, employment.partyIdFrom, false);
	request.setAttribute("partyName", partyName);
}