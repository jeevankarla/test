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

if((parameters.fromPartyId)  instanceof String){
	String partyIdstr = parameters.fromPartyId;
	if(partyIdstr.contains("{")){
		parameters.partyId=context.get("partyId");
		parameters.partyId_op="in";
	}
}

if(UtilValidate.isEmpty(parameters.fromPartyId)){
	parameters.partyId=context.get("partyId");
	parameters.partyId_op="in";
}else{
	parameters.partyId=parameters.fromPartyId;
}

