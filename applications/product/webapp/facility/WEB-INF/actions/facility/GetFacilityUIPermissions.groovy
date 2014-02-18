
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

import javolution.util.FastList;

context.enableShipping = true;
facilityShippingCfg = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"FACILITY_SHIPPING", propertyName:"enableShipping"], true);
if (facilityShippingCfg && facilityShippingCfg.propertyValue == "N") {
	context.enableShipping = false;
}

context.enableInventory = true;
facilityShippingCfg = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"FACILITY_INVENTORY", propertyName:"enableInventory"], true);
if (facilityShippingCfg && facilityShippingCfg.propertyValue == "N") {
	context.enableInventory = false;
}

context.enablePhase2 = false;
enablePhase2 = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"LMS", propertyName:"enablePhase2"], true);
if (enablePhase2 && enablePhase2.propertyValue == "Y") {
	context.enablePhase2 = true;
}

context.enableLmsPmSales = false;
enableLmsPmSales = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"LMS", propertyName:"enableLmsPmSales"], true);
if (enableLmsPmSales && enableLmsPmSales.propertyValue == "Y") {
	context.enableLmsPmSales = true;
}
session.setAttribute("enableLmsPmSales", context.enableLmsPmSales);

//Tenant config to check  PM Sales enabled or not

context.enablePastPaymentService = true;
enablePastPaymentService = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"LMS", propertyName:"enablePastPaymentService"], true);
if (enablePastPaymentService && enablePastPaymentService.propertyValue == "N") {
	context.enablePastPaymentService = false;
}
session.setAttribute("enablePastPaymentService", context.enablePastPaymentService);

 

if (context.facilityId != null) {
	session.setAttribute("ctxFacilityId", context.facilityId);
} 

context.ctxFacilityId = session.getAttribute("ctxFacilityId");

if (context.ctxFacilityId != null) {
	ctxFacility = delegator.findByPrimaryKey("Facility", [facilityId : context.ctxFacilityId]);
	context.ctxFacility = ctxFacility;
}
/*//now setting the menu location for dashboard
if(!session.getAttribute("dairyLmsMenuLocation")){
	session.setAttribute("dairyLmsMenuLocation", "component://product/widget/facility/FacilityMenus.xml");
	dairyLmsMenuLocationConfig = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"LMS", propertyName:"dairyLmsMenuLocation"], true);
	if(UtilValidate.isNotEmpty(dairyLmsMenuLocationConfig) && UtilValidate.isNotEmpty(dairyLmsMenuLocationConfig.propertyValue)){
		session.setAttribute("dairyLmsMenuLocation", dairyLmsMenuLocationConfig.propertyValue);
	}	
}
context.dairyLmsMenuLocation = session.getAttribute("dairyLmsMenuLocation");*/

/*
else {
	if (session.getAttribute("ctxFacilityId") && !"/FindFacility".equals(request.getPathInfo())) {
		context.ctxFacilityId = session.getAttribute("ctxFacilityId");
Debug.logInfo("context.facilityId 3:" + context.facilityId, "");
		
	}
}
*/