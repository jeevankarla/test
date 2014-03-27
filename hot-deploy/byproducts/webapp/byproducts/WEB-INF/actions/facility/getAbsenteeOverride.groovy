import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

JSONObject boothsDuesDaywiseJSON = new JSONObject();

dctx = dispatcher.getDispatchContext();
shipListType = "";
facilityId = null;
hideSearch ="Y";
supplyDate = UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp());
if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
if(context.hideSearch){
	hideSearch = context.hideSearch;
}

if(parameters.supplyDate){
	supplyDate = parameters.supplyDate;
}
if(context.supplyDate){
	supplyDate = context.supplyDate;
}
if(parameters.shipListType){
	shipListType = parameters.shipListType;
}
if(context.shipListType){
	shipListType = context.shipListType;
}
if(parameters.facilityId){
	facilityId = parameters.facilityId;
}
if(context.facilityId){
	facilityId = context.facilityId;
}
if(facilityId){
	facility = delegator.findOne("Facility",[facilityId : facilityId], false);
	if (facility == null) {
		Debug.logInfo("Booth '" + facilityId + "' does not exist!","");
		context.errorMessage = "Booth '" + facilityId + "' does not exist!";
		return;
	}
}
if(shipListType.equals("overrideList")){
	absenteeOverrideMap = ByProductNetworkServices.getAbsenteeOverrideBooths(dctx , [overrideSupplyDate:supplyDate]);
	context.absenteeOverrideList = absenteeOverrideMap.getAt("overrideList");
	
}else {
	if(hideSearch == "N"){
		stopShipMap = ByProductNetworkServices.getStopShipList(dctx , [supplyDate:supplyDate , facilityId : facilityId ,userLogin : userLogin]);
		stopShipList = 	stopShipMap["boothPendingPaymentsList"];
		context.boothPaymentsList = stopShipList;
		stopShipList.each { booth ->
			boothDuesDetail = ByProductNetworkServices.getDaywiseBoothDues(dctx, [userLogin: userLogin, facilityId:booth.facilityId]);
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
			boothsDuesDaywiseJSON.put(booth.facilityId, boothDuesMap);
		}
//Debug.logInfo("boothsDuesDaywiseJSON="+boothsDuesDaywiseJSON,"");
	}
	
}

context.boothsDuesDaywiseJSON = boothsDuesDaywiseJSON;
