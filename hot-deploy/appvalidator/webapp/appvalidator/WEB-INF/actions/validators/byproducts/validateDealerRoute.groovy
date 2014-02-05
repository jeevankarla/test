/*
 * validateBoothParty
 * The following validations are performed by this validator for each byproducts booth:
 * 1) Check for booth name (error)
 * 2) Check for booth description (warning)
 * 3) Check for valid owner party (error)
 * 4) Check for valid party classification (error)
 * 
 * 
 */

import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

def validationResult = new StringBuffer();
numOks = 0;
numWarns = 0;
numErrors = 0;

dctx = dispatcher.getDispatchContext();

resultMap = ByProductServices.getAllByproductBooths(delegator, UtilDateTime.nowTimestamp());
boothList = resultMap.get("boothsList");
boothList.each{ booth ->
	status = "OK";	
	hasError = false;
	hasWarn = false;
	message = "";
	ownerName = "";
	classification = "";
	// check for valid owner party
	boothRoutes = delegator.findList("FacilityGroupMember", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, booth.facilityId), UtilMisc.toSet("facilityGroupId"), null, null, false);
	//ownerResult = NetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", booth.facilityId, "userLogin", userLogin));
	boothRoutes = EntityUtil.getFieldListFromEntityList(boothRoutes, "facilityGroupId", true);
	routeId = "";
	tempHasError = false;
	tempMessage = "";
	if (UtilValidate.isEmpty(boothRoutes))
	{
		hasError = true;
		message += "route missing;";
	}
	else {
		boothRoutes.each{ eachRoute ->
			route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", eachRoute), false);
			if (UtilValidate.isEmpty(route))
			{
				tempHasError = true;
				tempMessage += "Route ["+ route.facilityId+"]Missing";
			}
			else{
				if(UtilValidate.isEmpty(route.ownerPartyId)){
					tempHasError = true;
					tempMessage += "Transporter for route ["+ route.facilityId +"] missing";
				}
			}
		}
	}
	if(tempHasError){
		message += tempMessage;
	}
	if (hasError || tempHasError) {
		status = "ERROR"
		numErrors++;
	}
	else if (hasWarn) {
		numWarns++;
		status = "WARN";
	}
	else {
		numOks++;
	}
	validationResult.append( " [" + status + "] {" + booth.facilityId + "} {" + booth.facilityName + "}" + message + "\n");
}


result = "\n***VALIDATION SUCCESS: " + numOks + " OKs\n" 
if (numWarns > 0 && numErrors ==0) {
	result = "\n***VALIDATION WARNING: " + numOks + " OKs; " + numWarns + " Warnings\n"
}
if (numErrors > 0) {
	result = "\n***VALIDATION ERROR: " + numOks + " OKs; " + numWarns + " Warnings; " + numErrors + " Errors\n"
}
validationResult.append(result);
return validationResult.toString();