import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

dctx = dispatcher.getDispatchContext();
statusIds = [];
statusIds.add("CRQ_DRAFT");
statusIds.add("CRQ_SUBMITTED");
statusIds.add("CRQ_INPROCESS");
parameters.statusId = statusIds;
parameters.statusId_op = "in";


if((parameters.fromPartyId)  instanceof String){
	String partyIdstr = parameters.fromPartyId;
	if(partyIdstr.contains("{")){
		parameters.fromPartyId=context.get("partyId");
		parameters.fromPartyId_op="in";
	}
}
if(UtilValidate.isEmpty(parameters.fromPartyId)){
	parameters.fromPartyId=context.get("partyId");
	parameters.fromPartyId_op="in";
}else{
	parameters.partyId=parameters.fromPartyId;
}
/*if(UtilValidate.isNotEmpty(context.get("partyId"))){
	parameters.partyId=context.get("partyId");
	parameters.partyId_op="in";
}*/
/*
shedId = parameters.shedId;
if(UtilValidate.isEmpty(parameters.statusId)){
	parameters.statusId="COM_CANCELLED";
	parameters.statusId_op="notEqual"
	}
if(UtilValidate.isEmpty(parameters.facilityId)){
shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx, UtilMisc.toMap("shedId", shedId));
unitsList = shedUnitDetails.get("unitsList");
parameters.facilityId = unitsList;
parameters.facilityId_op = "in";
}
*/