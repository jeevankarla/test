	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
	productId = parameters.productId;
	
	List condList =FastList.newInstance();
	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "BYPROD"));
	if(UtilValidate.isNotEmpty(productId)){
		condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	
	
	EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	productList =delegator.findList("ProductAndCategoryMember", discontinuationDateCondition,null, null, null, false);
	
	/*productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	Debug.log("productId==============================="+productId);
	productGenericList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId, "categoryId", "BYPROD")).get("productList");
	Debug.log("productGenericList==============================="+productGenericList);*/
	context.put("productList", productList);
	

