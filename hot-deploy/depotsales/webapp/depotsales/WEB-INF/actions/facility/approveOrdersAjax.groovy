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

orderId = parameters.orderId;

statusId = parameters.statusId;

partyId = parameters.partyId;




response = "";
if(UtilValidate.isNotEmpty(orderId) && statusId != "ORDER_APPROVED"){

inputCtx = [:];
inputCtx.put("userLogin",userLogin);
inputCtx.put("orderId", orderId);
inputCtx.put("statusId", statusId);
try{
	
	
	
 resultCtx = dispatcher.runSync("changeOrderStatus", inputCtx);
 
 
 
 response = resultCtx.responseMessage;
 Debug.log("resultCtx============"+resultCtx.responseMessage);
 
}catch(Exception e){}

}
else{

inputCtx = [:];
inputCtx.put("userLogin",userLogin);
inputCtx.put("orderId", orderId);
inputCtx.put("partyId", partyId);

try{
resultCtx = dispatcher.runSync("CreditapproveDepotSalesOrder", inputCtx);

response = resultCtx.responseMessage;



}catch(Exception e){}

}

createdStatus = "";

JSONObject tempMap = new JSONObject();

tempMap.put("response", response);


orderList.add(tempMap);


request.setAttribute("orderList", orderList);
return "success";
