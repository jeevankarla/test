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

dctx = dispatcher.getDispatchContext();
/*if(UtilValidate.isNotEmpty(context.get("partyId"))){
	parameters.fromPartyId=context.get("partyId");
	parameters.fromPartyId_op="in";
}*/
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
}
/*if(UtilValidate.isNotEmpty(partyIdsList)){
	parameters.fromPartyId= partyIdsList;
	parameters.fromPartyId_op="in";
}*/

/*else{
	parameters.fromPartyId=context.get("partyId");
	parameters.fromPartyId_op="in";
}*/
