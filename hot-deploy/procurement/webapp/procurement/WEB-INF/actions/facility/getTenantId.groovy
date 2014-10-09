import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import javolution.util.FastMap;

tenantId = delegator.getDelegatorTenantId();
context.tenantId =  tenantId.toUpperCase();

milkProductsList = [];
if(UtilValidate.isNotEmpty(context.productsList)){
	milkProductsList = context.productsList;
	tempProductMap = [:];
	tempProductMap.productName = "All";
	tempProductMap.brandName = "ALL";
	milkProductsList.add(0,tempProductMap);
}
context.putAt("milkProductsList", milkProductsList);
