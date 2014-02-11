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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

def validationResult = new StringBuffer();
numOks = 0;
numWarns = 0;
numErrors = 0;

dctx = dispatcher.getDispatchContext();
productList = ByProductServices.getByProductProducts(delegator, UtilDateTime.nowTimestamp());
productList.each{ eachProd ->
   status = "OK";
   hasError = false;
   hasWarn = false;
   message = "";
   conditionList = [];
   conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
   conditionList.add(EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "COMPONENT_PRICE"));
   EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
   prices = delegator.findList("ProductPrice", cond, UtilMisc.toSet("price"), null, null, false);
   price = EntityUtil.filterByDate(prices, UtilDateTime.nowTimestamp());
   if (UtilValidate.isEmpty(price)) {
	   hasError = true;
	   message += "price missing";
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
   validationResult.append( " [" + status + "] {" + eachProd.productId + "} {" + eachProd.productName + "}" + message + "\n");
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