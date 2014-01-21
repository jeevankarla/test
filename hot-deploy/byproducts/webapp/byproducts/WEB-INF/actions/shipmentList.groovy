import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.network.LmsServices;
import org.ofbiz.widget.form.ModelFormField.EntityOptions;
import org.ofbiz.entity.util.EntityFindOptions;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "LMS_SHIPMENT"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));

condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
findOptions.setDistinct(true);
listDeliveryScheduleTemp = delegator.findList("ShipmentAndType", condition, UtilMisc.toSet("estimatedShipDate","shipmentTypeId","statusId"), ["-estimatedShipDate"], findOptions, false);

conditionList.clear();

context.put("listDeliverySchedule", listDeliveryScheduleTemp);


facilityRoutes = delegator.findList("Facility", null, UtilMisc.toSet("facilityId", "facilityName"), null, null, true);
routes = [:];
facilityRoutes.each{ eachRoute ->
	routes.put(eachRoute.facilityId, eachRoute.facilityName);
}
context.routes = routes;