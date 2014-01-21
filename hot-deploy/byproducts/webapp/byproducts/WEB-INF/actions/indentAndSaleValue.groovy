import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Import;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import in.vasista.vbiz.byproducts.ByProductServices ;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

dctx = dispatcher.getDispatchContext();

fromDate = null;
thruDate = null;
if(parameters.indentDate){
	SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		context.indentDate = parameters.indentDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.indentDate).getTime()));
		thruDate = UtilDateTime.getDayEnd(fromDate);
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}else{
	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
}

List parloursOutlets= (List) ByProductServices.getByproductParlours(delegator,fromDate).get("parlourIdsList");
List totalOutlets = (List) ByProductNetworkServices.getByProductActiveFacilities(delegator,fromDate).get("boothsIdsList");
indentConditionList = [];
indentConditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate ));
indentConditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate ));
EntityCondition indentCondtion= EntityCondition.makeCondition(indentConditionList,EntityOperator.AND);
List<GenericValue> indentItemsList = null;
indentItemsList  = delegator.findList("SubscriptionFacilityAndSubscriptionProduct",indentCondtion,null,["productId","categoryTypeEnum"],null,false);
indentedParties = EntityUtil.getFieldListFromEntityList(indentItemsList, "facilityId", true);
nonParlourIndents = EntityUtil.filterByCondition(indentItemsList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL, "PARLOUR"));
nonParlourIndents = EntityUtil.getFieldListFromEntityList(nonParlourIndents, "facilityId", true);
productIdsSet= EntityUtil.getFieldListFromEntityList(indentItemsList, "productId", true);
List productsList = (List)ByProductNetworkServices.getByProductProducts(dctx,UtilMisc.toMap());
productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
parlourSpecialProducts = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct").get("PARLOR_PROD_CATEGORY");
context.parlourProducts = parlourSpecialProducts.size();
notIndentedProductsList = [];
for(product in productsList){
	if(!productIdsSet.contains(product.productId) && !parlourSpecialProducts.contains(product.productId)){
		productMap =[:];
		productMap["productId"]=product.productId;
		productMap["productName"]=product.productName;
		notIndentedProductsList.add(productMap);
	}
}
indentedProductsList = [];
for(productId in productIdsSet){
	productDetails =EntityUtil.filterByAnd(productsList,[EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)]);
	productMap = [:];
	if(UtilValidate.isNotEmpty(productDetails)){
		productMap.put("productId",productId);
		productMap.put("productName",productDetails[0].productName);
		indentedProductsList.add(productMap);
	}
}
indentedProducts = indentedProductsList.size();
context.put("notIndentdProductIdsSet",notIndentedProductsList);
context.put("indentedProductsList",indentedProductsList);

// getting no of orders having today as estimated deliverydate
conditionList =[];
condition = null;
conditionList.add(EntityCondition.makeCondition("salesChannelEnumId",EntityOperator.EQUALS,"PARLOR_SALES_CHANNEL"));
conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_IN,["ORDER_REJECTED","ORDER_CANCELLED"]));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
saleItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility",condition,["originFacilityId"] as Set,null,null,false);

parlourSaleOrders = EntityUtil.getFieldListFromEntityList(saleItemsList, "originFacilityId", true);


indentAndSaleValueMap = [:];
indentAndSaleValueMap.put("indentedParty", indentedParties.size());
indentAndSaleValueMap.put("parlourSaleParty", parlourSaleOrders.size());
indentAndSaleValueMap.put("totalOutlets", totalOutlets.size());
indentAndSaleValueMap.put("parlourOutlets", parloursOutlets.size());

paidIndentAmt = BigDecimal.ZERO;
indentPaymentParty = null;
paymentConditionList = [];
paymentConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,indentedParties));
paymentConditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
paymentConditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
paymentConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"PMNT_VOID"));
paymentCondition = EntityCondition.makeCondition(paymentConditionList,EntityOperator.AND);
indentPayments = delegator.findList("Payment",paymentCondition,["facilityId","amount"]as Set,null,null,false);
uniquePayments = EntityUtil.getFieldListFromEntityList(indentPayments, "facilityId", true);
for(payment in indentPayments){
   paidIndentAmt +=  payment.amount;
}
todaysSalesTotals = ByProductReportServices.getDayDespatchDetails(dctx, UtilMisc.toMap("userLogin", userLogin, "fromDate",fromDate, "thruDate", thruDate));
BigDecimal dayDespatchTotal = (BigDecimal)todaysSalesTotals.get("totalValue");
paymentsMap = [:];
paymentsMap.put("indPayments", uniquePayments.size());
paymentsMap.put("paidIndentAmt",new BigDecimal(paidIndentAmt).setScale(0,BigDecimal.ROUND_HALF_UP));
paymentsMap.put("totIndentSaleValue",dayDespatchTotal.setScale(0,BigDecimal.ROUND_HALF_UP));
context.put("indentAndSaleValueMap",indentAndSaleValueMap);
noOfProducts = productsList.size();
productsMap = [:];
productsMap.put("noOfProducts",noOfProducts);
productsMap.put("indentedProducts",indentedProducts);
context.put("paymentsMap",paymentsMap);
context.put("productsMap",productsMap);


