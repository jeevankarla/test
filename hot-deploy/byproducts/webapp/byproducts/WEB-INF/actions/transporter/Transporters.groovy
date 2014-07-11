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

facilityPartyList = delegator.findList("FacilityFacilityPartyAndPerson", condition, null, null, null, false);

	for (GenericValue facilityParty : facilityPartyList) {
		String partyId= facilityParty.getString("partyId");
		String partyName = PartyHelper.getPartyName(delegator, partyId, true);
		JSONArray transporterJSON = new JSONArray();
		routeId = facilityParty.getString("facilityId");

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

		String rateAmount =  (BigDecimal) facilityRateResult.get("rateAmount");
		String routeLength = (BigDecimal) facilitySizeResult.get("facilitySize")
		
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

//	facilityWorkOrdrNumMap = [:];
//	facilityWorkOrderNoList = delegator.findList("FacilityAttribute", EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "WORK_ORDER_NO"), null, null, null, false);
//	facilityWorkOrderNoList.each{eachWork ->
//		facilityWorkOrdrNumMap.put(eachWork.facilityId, eachWork.attrValue);
//	}

Debug.logError("transportersJSON="+transportersJSON,"");
context.transportersJSON = transportersJSON;

