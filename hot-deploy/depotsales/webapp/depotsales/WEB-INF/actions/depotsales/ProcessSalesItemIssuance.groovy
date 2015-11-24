import org.ofbiz.service.ServiceUtil
import org.ofbiz.entity.condition.*
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.depotsales.DepotSalesServices;

orderId = parameters.orderId;
partyId = parameters.partyId;
dctx = dispatcher.getDispatchContext();

inputMap = [:];
inputMap.put("orderId", orderId);
inputMap.put("userLogin", userLogin);
try{
	//Map issuanceResultCtx = DepotSalesServices.createIssuanceForDepotOrder(dctx, inputMap);
	issuanceResultCtx = dispatcher.runSync("createIssuanceForDepotOrder", [orderId : orderId, userLogin : userLogin]);
	if (!ServiceUtil.isError(issuanceResultCtx)) {
		request.setAttribute("_EVENT_MESSAGE_", "Indent Issuance successful for party : "+partyId+" OrderId: "+orderId);
		return "success";
	}else{
		request.setAttribute("_ERROR_MESSAGE_", "Failed to Issue Indent for orderId: : "+orderId);
		return "error";
	}
}catch(Exception e){
	Debug.logError(e, "Failed to Issue Indent  ");
	request.setAttribute("_ERROR_MESSAGE_", "Failed to Issue Indent.!");
	return "error";
}

