import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import java.net.URL;
import org.ofbiz.base.conversion.NetConverters;
import org.ofbiz.base.conversion.NetConverters.StringToURL;


context.defaultProductTypeList=new Boolean(true);
StringToURL stringToUrl=new StringToURL();
URL refererUri= stringToUrl.convert(request.getHeader("referer"));
pathInfo=refererUri.getPath();
if(parameters.ajaxLookup == "Y"){
restrictUrlList=[];
restrictUrlList.add("/byproducts/control/ChannelWiseSalesChart");
restrictUrlList.add("/byproducts/control/QuotaListing");
restrictUrlList.contains(pathInfo)
// removing raw material from product ajax lookup
if(restrictUrlList.contains(pathInfo)){
		context.defaultProductTypeList= new Boolean(false);
		condProductTypeList = [];
		condProductTypeList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, null));
		condition = EntityCondition.makeCondition(condProductTypeList,EntityOperator.AND);
		productTypeList = delegator.findList("ProductType", condition, null, null, null, false);
		conditionFields=[:];
		productTypeIdList=[];
		productTypeList.each{ eachType ->
			if(!("RAW_MATERIAL".equals(eachType.productTypeId)))
			productTypeIdList.add(eachType.productTypeId);
		}
			conditionFields.productTypeId=productTypeIdList;
			context.conditionFields=conditionFields;
	}
}