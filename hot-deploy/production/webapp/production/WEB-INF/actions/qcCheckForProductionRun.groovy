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
qcCheck=null;
if(UtilValidate.isNotEmpty(context.get("workEffortId"))){
	workEffortId=context.get("workEffortId");
    productQcTest = delegator.findList("ProductQcTest",EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS , workEffortId)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(productQcTest)){
		//String statusId = (EntityUtil.getFirst(productQcTest)).getString("statusId");
		//statusId="QC_ACCEPT"
		qcCheck="COMPLETE"
	}else
     qcCheck="INCOMPLETE"
}	
context.qcCheck=qcCheck;