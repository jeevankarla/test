import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

dctx = dispatcher.getDispatchContext();
shedId = parameters.shedId;
if(UtilValidate.isEmpty(shedId)){
	shedId= context.getAt("shedId");
	}
if(UtilValidate.isEmpty(shedId)){
	return;
	}
if(UtilValidate.isEmpty(parameters.facilityId)){
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx, UtilMisc.toMap("shedId", shedId));
	unitsList = shedUnitDetails.get("unitsList");
	parameters.facilityId = unitsList;
	parameters.facilityId_op = "in";
}
