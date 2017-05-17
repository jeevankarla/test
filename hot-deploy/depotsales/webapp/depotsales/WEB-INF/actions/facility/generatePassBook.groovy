import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.party.party.PartyHelper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;

customerId = parameters.customerId;

result= dispatcher.runSync("generatePassBook", [customerId: customerId]);
passBookNumber=result.get("passBookNumber")
partyIdentification = delegator.findOne("PartyIdentification",[partyIdentificationTypeId :"PSB_NUMER",partyId:customerId] , false);
delegator.removeValue(partyIdentification);
dispatcher.runSync("createPartyIdentification", UtilMisc.toMap("partyIdentificationTypeId","PSB_NUMER","idValue",passBookNumber,"partyId",customerId,"issueDate",UtilDateTime.nowTimestamp(),"userLogin", context.get("userLogin")));
request.setAttribute("passBookNumber", passBookNumber);
return "success";














