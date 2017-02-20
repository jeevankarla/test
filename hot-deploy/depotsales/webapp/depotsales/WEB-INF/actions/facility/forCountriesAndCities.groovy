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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.contact.ContactMechWorker;

JSONArray branchJSON = new JSONArray();
JSONObject branchPartyObj = new JSONObject();
JSONArray partyJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();
JSONObject partyGeoObj = new JSONObject();
dctx = dispatcher.getDispatchContext();

userPartyId = userLogin.partyId;







List<GenericValue> countries= org.ofbiz.common.CommonWorkers.getCountryList(delegator);
JSONArray countryListJSON = new JSONArray();
countries.each{ eachCountry ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachCountry.geoId);
		newObj.put("label",eachCountry.geoName);
		countryListJSON.add(newObj);
}
context.countryListJSON = countryListJSON;



conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
statesList = delegator.findList("Geo",conditionDepo,null,null,null,false);


JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoId);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;