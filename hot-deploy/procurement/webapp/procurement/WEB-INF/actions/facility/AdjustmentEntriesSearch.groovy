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

List condList=[];

dctx = dispatcher.getDispatchContext();
if(!UtilValidate.isEmpty(parameters.unitCode) && !UtilValidate.isEmpty(parameters.facilityCode)){
	GenericValue facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, UtilMisc.toMap("shedId", parameters.shedId,"centerCode", parameters.facilityCode,"unitCode",parameters.unitCode))).get("agentFacility");
	if(UtilValidate.isEmpty(facility)){
		context.errorMessage = "No Facility found with UnitCode:"+parameters.unitCode+" and  center Code : "+parameters.facilityCode ;
		return ;
		}else{
			parameters.originFacilityId = UtilMisc.toList(facility.getString("facilityId"));
			parameters.originFacilityId_op = "in";
		}
}else if(!UtilValidate.isEmpty(parameters.unitCode)){
	List<GenericValue> facilityList= (ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("shedId", parameters.shedId ,"unitCode", parameters.unitCode))).get("agentsList");
	parameters.originFacilityId = EntityUtil.getFieldListFromEntityList(facilityList,"facilityId" , true);
	parameters.originFacilityId_op = "in";
}else if(!UtilValidate.isEmpty(parameters.facilityCode)){
	condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN,UtilMisc.toList("CENTER")));
	condList.add(EntityCondition.makeCondition("facilityCode", parameters.facilityCode));
	List<GenericValue> facilityList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	parameters.originFacilityId = EntityUtil.getFieldListFromEntityList(facilityList,"facilityId" , true);
	parameters.originFacilityId_op = "in";
}else if(!UtilValidate.isEmpty(parameters.shedId)){
	List facilityList = (ProcurementNetworkServices.getShedAgents(dctx, UtilMisc.toMap("shedId", parameters.shedId))).get("agentsList");
	parameters.originFacilityId = facilityList;
	parameters.originFacilityId_op = "in";
}