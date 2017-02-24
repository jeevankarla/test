
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

import java.sql.*;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

import org.ofbiz.party.party.PartyHelper;



supplierId = parameters.supplierId;



daystart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());


conditionList =[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , supplierId));
conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO ,daystart));
conditionList.add(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS ,null));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
FacilityList = delegator.findList("Facility", condition, null, null, null, false);


//Debug.log("FacilityList=============="+FacilityList);

/*conditionList.clear();
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS,attrName));
FacilityAttributeList = delegator.findList("FacilityAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
*/

JSONArray facilityAddressJSON = new  JSONArray();

FacilityList.each{ eachFacility ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachFacility.facilityId);
		newObj.put("label",eachFacility.facilityName);
		facilityAddressJSON.add(newObj);
}
context.facilityAddressJSON = facilityAddressJSON;







request.setAttribute("facilityAddressJSON", facilityAddressJSON);

return "sucess";