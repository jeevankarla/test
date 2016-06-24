import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;


tallyRefNoList=[];
parameters.tallyRefsearch="N";
if(UtilValidate.isNotEmpty(parameters.tallyRefNo)){
	
	condList=[];
	condList.add(EntityCondition.makeCondition("tallyRefNo", EntityOperator.LIKE, "%"+parameters.tallyRefNo+"%"));
	condList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	orderDetList = delegator.findList("OrderHeader", cond, null, null, null, false);
	if(orderDetList){
		condList.clear();
		condList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.IN, orderDetList.orderId));
		cond1 = EntityCondition.makeCondition(condList, EntityOperator.AND);
		OrderAssoc = EntityUtil.getFirst(delegator.findList("OrderAssoc", cond1, null, null, null, false));		
		if(OrderAssoc.orderId){
			parameters.orderId=OrderAssoc.orderId;
			parameters.refNo=parameters.tallyRefNo;
			parameters.tallyRefNo="";
		}
	}
	parameters.tallyRefsearch="Y";
	
}
