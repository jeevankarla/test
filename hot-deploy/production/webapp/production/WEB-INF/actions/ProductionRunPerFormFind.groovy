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
if(UtilValidate.isNotEmpty(context.get("facilityId"))){
	parameters.facilityId=context.get("facilityId");
	parameters.facilityId_op="in";
}else{
	parameters.facilityId=parameters.facilityId;
	parameters.facilityId_op="in";
}
