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
	if (UtilValidate.isEmpty(booth.facilityName))
	{
		hasError = true;
		message ="facilityName is empty;";
	}
	if (UtilValidate.isEmpty(booth.description))
	{
		hasWarn = true;
		message += "description is empty;";
	}
	// check for valid owner party
	ownerResult = ByProductServices.getBoothOwnerContactInfo(dctx, UtilMisc.toMap("facilityId", booth.facilityId, "userLogin", userLogin));
	if (UtilValidate.isEmpty(ownerResult) || UtilValidate.isEmpty(ownerResult.contactInfo))
	{
		hasError = true;
		message += "owner missing;";
	}
	else {
		ownerContactInfo = ownerResult.contactInfo;
		ownerName = ownerContactInfo.name;
		if (UtilValidate.isEmpty(ownerName))
		{
			hasWarn = true;
			message += "owner name is empty;";
		}
	}
	if (hasError) {
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
	validationResult.append( " [" + status + "] {" + booth.facilityId + "} {" + booth.facilityName + "}" +
		" {" + booth.description + "} {" +  ownerName + "}" + message + "\n");
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