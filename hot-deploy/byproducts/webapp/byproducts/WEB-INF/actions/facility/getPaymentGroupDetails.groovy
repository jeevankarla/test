import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.math.RoundingMode;
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

paymentGroupId = parameters.paymentGroupId;
paymentGroup = delegator.findOne("PaymentGroup", UtilMisc.toMap("paymentGroupId", paymentGroupId),false);

paymentGroupMember = delegator.findList("PaymentGroupMember", EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS, paymentGroupId), null, null, null, false);
paymentIds = EntityUtil.getFieldListFromEntityList(paymentGroupMember, "paymentId", true);

payments = delegator.findList("Payment", EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIds), null, null, null, false);
JSONArray paymentDetailsJSON = new JSONArray();
payments.each { eachItem ->
	JSONObject itemJSON = new JSONObject();
	itemJSON.put("paymentId", eachItem.paymentId);
	itemJSON.put("partyId", eachItem.partyIdTo);
	partyName = PartyHelper.getPartyName(delegator, eachItem.partyIdTo, false);
	itemJSON.put("partyName", partyName);
	itemJSON.put("paymentDate", UtilDateTime.toDateString(eachItem.paymentDate, "dd MMM, yyyy"));
	itemJSON.put("amount", eachItem.amount);
	paymentDetailsJSON.add(itemJSON)
}
request.setAttribute("paymentDetailsJSON", paymentDetailsJSON);
return "success";
