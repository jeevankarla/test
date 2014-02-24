import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;

/*if(UtilValidate.isNotEmpty(parameters.facilityId)){
	productList = NetworkServices.getDateWiseBoothDiscounts(dispatcher.getDispatchContext(), UtilMisc.toMap("facilityId", parameters.facilityId, "fromDate", UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()),"thruDate", UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()))).get("productRatesList");
}*/	
//context.productList = productList;