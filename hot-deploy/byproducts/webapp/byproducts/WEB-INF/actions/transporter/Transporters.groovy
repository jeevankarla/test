import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilMisc;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();
Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
JSONArray transportersJSON = new JSONArray();
Map<String, String> facilityPartyMap = FastMap.newInstance();
conditionList=[];
//conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
//conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, "Contractor"));
conditionList.add(EntityCondition.makeCondition("facilityTypeId",  EntityOperator.EQUALS,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderBy = UtilMisc.toList("-thruDate");
routeMap = FastMap.newInstance();

facilityPartyList = delegator.findList("FacilityFacilityPartyAndPerson", condition, null, orderBy, null, false);

facilityPartyList.each { facilityParty->			
	routeId = facilityParty.getString("facilityId");
	if (routeMap.containsKey(routeId)) {
		mapFacilityParty = routeMap.get(routeId);
		if (mapFacilityParty.getTimestamp("thruDate") == null ||  
			(facilityParty.getTimestamp("thruDate") != null && 
				mapFacilityParty.getTimestamp("thruDate") > facilityParty.getTimestamp("thruDate"))) {
			return;
		}
	}
	routeMap.put(routeId, facilityParty);
}

routeMap.each{ routeId, facilityParty ->
	String partyId= facilityParty.getString("partyId");
	String partyName = PartyHelper.getPartyName(delegator, partyId, false);
	JSONArray transporterJSON = new JSONArray();
	partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: partyId, userLogin: userLogin]);
	phoneNumber = "";
	if (partyTelephone != null && partyTelephone.contactNumber != null) {
		phoneNumber = partyTelephone.contactNumber;
	}		
	Map inputRateAmt =  UtilMisc.toMap("userLogin", userLogin);
	inputRateAmt.put("rateCurrencyUomId", "INR");
	inputRateAmt.put("facilityId", routeId);
	inputRateAmt.put("fromDate",nowTimestamp );
	inputRateAmt.put("rateTypeId", "TRANSPORTER_MRGN");
	facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
	
	Map inputFacilitySize =  UtilMisc.toMap("userLogin", userLogin);
	inputFacilitySize.put("rateCurrencyUomId", "LEN_km");
	inputFacilitySize.put("facilityId", routeId);
	inputFacilitySize.put("fromDate",nowTimestamp );
	inputFacilitySize.put("rateTypeId", "FACILITY_SIZE");
	facilitySizeResult = dispatcher.runSync("getRouteDistance", inputFacilitySize);

	String rateAmount =  ((BigDecimal) facilityRateResult.get("rateAmount")).setScale(2,rounding);
	String routeLength = ((BigDecimal) facilitySizeResult.get("facilitySize")).setScale(2,rounding);
	// don't show zero lengths for now, since it's misleading
	if ("0.00".equals(routeLength)) {
		routeLength = "";
	}
	transporterJSON.add(routeId);
	transporterJSON.add(partyId);
	transporterJSON.add(partyName);
	transporterJSON.add(phoneNumber);
	transporterJSON.add(routeLength);
	transporterJSON.add(rateAmount);
	fromDate = "";
	if (facilityParty.getTimestamp("fromDate") != null) {
		fromDate = UtilDateTime.toDateString(facilityParty.getTimestamp("fromDate"), "dd/MM/yyyy");			
	}
	thruDate = "";
	if (facilityParty.getTimestamp("thruDate") != null) {
		thruDate = UtilDateTime.toDateString(facilityParty.getTimestamp("thruDate"), "dd/MM/yyyy");			
	}
	transporterJSON.add(fromDate);
	transporterJSON.add(thruDate);
	transportersJSON.add(transporterJSON);
}


Debug.logError("transportersJSON="+transportersJSON,"");
context.transportersJSON = transportersJSON;

