import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;


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



JSONArray orderList =new JSONArray();

organizationId = parameters.organizationId;

Debug.log("organizationId================"+organizationId);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, organizationId));
conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "CHEQUE_PAYOUT"));
List paymentMethodsList = delegator.findList("PaymentMethod", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

Debug.log("paymentMethodsList================"+paymentMethodsList);
/*
JSONObject tempMap = new JSONObject();

tempMap.put("facilityId", "Sucess");
tempMap.put("createdStatus", createdStatus);


orderList.add(tempMap);

*/
request.setAttribute("orderList", paymentMethodsList);
return "success";
