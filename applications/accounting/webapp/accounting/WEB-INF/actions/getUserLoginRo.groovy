import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
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
import java.sql.*;

parameters.ownerPartyId = "";
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
employment = delegator.findList("Employment", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);
if(UtilValidate.isNotEmpty(employment)){
	GenericValue empDetail = EntityUtil.getFirst(employment);
	if(empDetail.partyIdFrom != "Company" || empDetail.partyIdFrom != "HO"){
		parameters.ownerPartyId = empDetail.partyIdFrom;
	}
}
else{
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "BRANCH_EMPLOYEE"));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	employment = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList,EntityOperator.AND),null, null, null, false);	
	if(UtilValidate.isNotEmpty(employment)){
		GenericValue empDetail = EntityUtil.getFirst(employment);
		if(empDetail.partyIdFrom != "Company" || empDetail.partyIdFrom != "HO"){
			parameters.ownerPartyId = empDetail.partyIdFrom;
		}
	}
}
