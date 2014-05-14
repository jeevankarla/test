import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

dctx = dispatcher.getDispatchContext();

condList = [];
condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
if(parameters.facilityId){
	condList.add(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS, parameters.facilityId));
}
if(parameters.paymentRefNum){
	condList.add(EntityCondition.makeCondition("paymentRefNum" ,EntityOperator.EQUALS, parameters.paymentRefNum));
}
condList.add(EntityCondition.makeCondition("paymentMethodTypeId" ,EntityOperator.EQUALS, "CHEQUE_PAYIN"));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
paymentList = delegator.findList("Payment", cond, null, null, null ,false);
context.paymentList = paymentList;

