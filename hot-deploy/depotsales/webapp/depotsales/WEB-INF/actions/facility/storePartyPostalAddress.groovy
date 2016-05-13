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


partyId = parameters.partyId;
address1 = parameters.address1;
address2 = parameters.address2;
city = parameters.city;
postalCode = parameters.postalCode;
countryCode = parameters.countryCode;
stateProvinceGeoId = parameters.stateProvinceGeoId;
country = parameters.country;




input = [:];

input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyId, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId",stateProvinceGeoId, "postalCode", postalCode, "contactMechId", "","contactMechPurposeTypeId","SHIPPING_LOCATION");
resultMap =  dispatcher.runSync("createPartyPostalAddress", input);


contactMechId = resultMap.get("contactMechId");

JSONObject  storeDate = new JSONObject();

storeDate.put("contactMechId",contactMechId);

request.setAttribute("orderList", storeDate);
return "success";
