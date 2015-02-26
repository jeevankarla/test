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

productId=parameters.productId;
custRequestId = parameters.custRequestId;
partyId = parameters.partyId;
conditionList=[];
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
}
if(UtilValidate.isNotEmpty(parameters.custRequestId)){
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
}
if(UtilValidate.isNotEmpty(partyId)){
	conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
}
orderBy = UtilMisc.toList("lastModifiedDate");	
conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "CRQ_ISSUED"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
CustRequestAndItemAndAttribute = delegator.findList("CustRequestAndItemAndAttribute", condition, null, orderBy, null, false);
if(UtilValidate.isNotEmpty(CustRequestAndItemAndAttribute)){
   context.indentItems = CustRequestAndItemAndAttribute;
}

	