import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

dctx = dispatcher.getDispatchContext();
if(parameters.boothId){
	facilityId = parameters.boothId;
}else{
	facilityId = context.facilityId;
}

JSONObject boothsDuesDaywiseJSON = new JSONObject();
facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
if(facility){
	categoryTypeEnum = facility.categoryTypeEnum;
	boothDuesDetail = [:];
	if(categoryTypeEnum == "CR_INST"){
		boothDuesDetail = ByProductNetworkServices.getDaywiseCRInstDues(dctx, [userLogin: userLogin, facilityId:facilityId, isByParty:Boolean.TRUE, enableCRInst:Boolean.TRUE]);
	}
	else{
		boothDuesDetail = ByProductNetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId:facilityId, isByParty:Boolean.TRUE, enableCRInst:Boolean.TRUE]);
	}
	
	duesList = boothDuesDetail["boothDuesList"];
	JSONArray boothDuesList= new JSONArray();
	duesList.each { due ->
		JSONObject dueJSON = new JSONObject();
		dueJSON.put("supplyDate", UtilDateTime.toDateString(due.supplyDate, "dd MMM, yyyy"));
		dueJSON.put("amount", due.amount);
		dueJSON.put("amount", UtilFormatOut.formatCurrency(due.amount, context.get("currencyUomId"), locale));
		boothDuesList.add(dueJSON);
	}
	JSONObject boothDuesMap = new JSONObject();
	boothDuesMap.put("totalAmount", UtilFormatOut.formatCurrency(boothDuesDetail["totalAmount"], context.get("currencyUomId"), locale));
	boothDuesMap.put("boothDuesList", boothDuesList);
	boothsDuesDaywiseJSON.put(facilityId, boothDuesMap);
	context.boothsDuesDaywiseJSON = boothsDuesDaywiseJSON;
	request.setAttribute("boothsDuesDaywiseJSON", boothsDuesDaywiseJSON);
}

return "success";