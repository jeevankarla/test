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


ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE")],EntityOperator.AND);
vehicleRole=delegator.findList("VehicleRole",ecl,null,null,null,false);
partyIds=EntityUtil.getFieldListFromEntityList(vehicleRole,"partyId",true);
if(partyIds){
	partyIds.each{partyId->
		String partyName = PartyHelper.getPartyName(delegator, partyId, false);
		JSONArray transporterJSON = new JSONArray();
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: partyId, userLogin: userLogin]);
		phoneNumber = "";
		if (partyTelephone != null && partyTelephone.contactNumber != null) {
			phoneNumber = partyTelephone.contactNumber;
		}
		transporterJSON.add(partyId);
		transporterJSON.add(partyName);
		transporterJSON.add(phoneNumber);
		partyDetails=EntityUtil.filterByCondition(vehicleRole,EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		partyDetail=EntityUtil.getFirst(partyDetails);
		
		fromDate = "";
		if (partyDetail.fromDate != null) {
			fromDate = UtilDateTime.toDateString(partyDetail.fromDate, "dd/MM/yyyy");
		}
		thruDate = "";
		if (partyDetail.thruDate != null) {
			thruDate = UtilDateTime.toDateString(partyDetail.thruDate, "dd/MM/yyyy");
		}
		transporterJSON.add(fromDate);
		transporterJSON.add(thruDate);
		transportersJSON.add(transporterJSON);
	}
}

Debug.logError("transportersJSON="+transportersJSON,"");
context.transportersJSON = transportersJSON;