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
poRefNoList=[];
if(UtilValidate.isNotEmpty(parameters.refrenceNo)){
  poReferNumDetails = delegator.findList("OrderAttribute",EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS , parameters.refrenceNo)  , UtilMisc.toSet("orderId"), null, null, false );
  poReferNumDetails = EntityUtil.getFirst(poReferNumDetails);
  parameters.orderId=poReferNumDetails.orderId;
}