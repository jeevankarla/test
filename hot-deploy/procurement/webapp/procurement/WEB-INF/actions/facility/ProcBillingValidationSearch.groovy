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
if(UtilValidate.isNotEmpty(parameters.shedId)){
	List facilityList = (ProcurementNetworkServices.getShedAgents(dctx, UtilMisc.toMap("shedId", parameters.shedId))).get("agentsList");
	parameters.centerId = facilityList;
	parameters.centerId_op = "in";
}

if(UtilValidate.isNotEmpty(parameters.unitCode)){
	List<GenericValue> facilityList= (ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("shedId", parameters.shedId ,"unitCode", parameters.unitCode))).get("agentsList");
	parameters.centerId = EntityUtil.getFieldListFromEntityList(facilityList,"facilityId" , true);
	parameters.centerId_op = "in";
}