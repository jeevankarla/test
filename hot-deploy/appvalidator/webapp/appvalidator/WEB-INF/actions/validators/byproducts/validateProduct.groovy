/*
* validateProduct
* The following validations are performed by this validator for each byproducts product:
* 1) Check for product name (error)
* 2) Check for product brand name (warn) 
* 3) Check for product description (warn)
* 4) Check for product quantityUomId (error)
* 5) Check for product quantityIncluded (error)
*
*
*/

import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

def validationResult = new StringBuffer();
numOks = 0;
numWarns = 0;
numErrors = 0;

dctx = dispatcher.getDispatchContext();
prodList = ByProductNetworkServices.getByProductProducts(dctx, UtilMisc.toMap());
prodList.each{ product ->
	status = "OK";	
	hasError = false;
	hasWarn = false;
	message = "";
	if (UtilValidate.isEmpty(product.productName))
	{
		hasError = true;
		message ="productName is empty;";
	}
	if (UtilValidate.isEmpty(product.brandName))
	{
		hasWarn = true;
		message += "brandName is empty;";
	}
	if (UtilValidate.isEmpty(product.description))
	{
		hasWarn = true;
		message += "description is empty;";
	}
	if (UtilValidate.isEmpty(product.quantityUomId))
	{
		hasError = true;
		message ="quantityUomId is empty;";
	}
	if (UtilValidate.isEmpty(product.quantityIncluded))
	{
		hasError = true;
		message ="quantityIncluded is empty;";
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
	validationResult.append( " [" + status + "] {" + product.productId + "} {" + product.productName + "}" +
		" {" + product.brandName + "} {" +  product.description + "} {" + product.quantityUomId + "} " + 
		product.quantityIncluded + "} " + message + "\n");
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