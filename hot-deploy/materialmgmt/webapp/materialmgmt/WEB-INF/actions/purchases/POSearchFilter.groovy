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

if(UtilValidate.isEmpty(parameters.productId)){
	if(UtilValidate.isNotEmpty(result.listIt)){
		context.listIt=result.listIt;
	}
}
else{
productId = parameters.productId;

if(UtilValidate.isNotEmpty(result.listIt)){
	list=result.listIt;
	poListNew=[];
	GenericValue poEntry = null;
	while ((poEntry=list.next()) != null) {
		
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poEntry.orderId));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderList = delegator.findList("OrderItem", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(orderList)){
			poListNew.add(poEntry);
			}
		}
	list.close();
	}

context.listIt=poListNew;
}