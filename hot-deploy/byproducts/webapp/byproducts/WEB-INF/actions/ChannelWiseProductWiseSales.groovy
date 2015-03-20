import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.party.party.PartyHelper;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
categoryType=parameters.categoryType;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
partyIds=[];
roleTypeList=[];
roleType=parameters.roleType;
if(UtilValidate.isNotEmpty(roleType)&&!roleType.equals("All")){
	roleTypeList.add(roleType);
}
if(UtilValidate.isNotEmpty(roleType)&&roleType.equals("All")){
	roleTypeList.add("UNITS");
	roleTypeList.add("UNION");
	roleTypeList.add("Retailer");
	roleTypeList.add("TRADE_CUSTOMER");
}

prodCatWiseMap = [:];
conditionList = [];
productType=parameters.productType;
if(UtilValidate.isNotEmpty(productType)){
	  if(UtilValidate.isEmpty(parameters.productId)){
		ProdDetails = delegator.findList("Product",EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS,productType),null,null,null,false);
	  }else{
			conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS,productType));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
			EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    ProdDetails = delegator.findList("Product",cond,null,null,null,false);
	  }
	  productIds = EntityUtil.getFieldListFromEntityList(ProdDetails, "productId", false);

		if(UtilValidate.isNotEmpty(parameters.categoryType)){
		    conditionList.clear();
		    conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,categoryType));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, productIds));
			EntityCondition cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    ProdDetails = delegator.findList("ProductCategoryMember",cond1,null,null,null,false);
		    productIds = EntityUtil.getFieldListFromEntityList(ProdDetails, "productId", false);
			
	    }
}
partyIdList=[];
count=0;
if(UtilValidate.isNotEmpty(productIds)){
	roleTypeList.each{roleType->
	count=count+1;
	try {
		partyIds = ByProductNetworkServices.getPartyByRoleType(dctx, [userLogin: userLogin, roleTypeId: roleType]).get("partyIds");
		if(count==0){
		 partyIdList.add(partyIds);
		}
		if(count==1){
		  partyIds.removeAll(partyIdList);
		  partyIdList.add(partyIds);
		}
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIds));
		conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("invoiceId","partyId");
		invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		productTotals = [:];
		invoiceItemsIter.each{invoiceItem->
		BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
		String productId = invoiceItem.getString("productId");
		   try{
			    GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId", productId), true);
			    productCategoryId=productDetails.getString("primaryProductCategoryId");
				if(UtilValidate.isNotEmpty(productDetails.getBigDecimal("quantityIncluded"))) {
				    quantity = quantity.multiply(productDetails.getBigDecimal("quantityIncluded"));
				}
			}catch (GenericEntityException e) {
				Debug.logError(e, module);
				
			}
				// get category
				if(UtilValidate.isEmpty(prodCatWiseMap[productCategoryId])){
					tempMap = [:];
					prodWiseMap = [:];
					tempMap.put(roleType, quantity);
					prodWiseMap.put(productId, tempMap);
					prodCatWiseMap[productCategoryId]=prodWiseMap;
				 }else{
					    categoryMap = [:];
					    categoryMap.putAll(prodCatWiseMap.get(productCategoryId));
						if(UtilValidate.isEmpty(categoryMap[productId])){
							catMap = [:]
							catMap.put(roleType,quantity);
							temp=FastMap.newInstance();
							temp.putAll(catMap);
							categoryMap[productId] = temp;
							prodCatWiseMap[productCategoryId]=categoryMap;
						}else{
						    catMap = [:];
							catMap.putAll(categoryMap.get(productId));
							if(UtilValidate.isEmpty(catMap[roleType])){
							  catMap[roleType]=quantity;
						      categoryMap[productId] = catMap;
						      temp=FastMap.newInstance();
							  temp.putAll(categoryMap);
						      prodCatWiseMap[productCategoryId]=temp;
							}else{
								catMap[roleType]+=quantity;
							    categoryMap[productId] = catMap;
							    temp=FastMap.newInstance();
								temp.putAll(categoryMap);
							    prodCatWiseMap[productCategoryId]=temp;
							}
					  }
				}
			} 
		invoiceItemsIter.close();
		context.put("prodCatWiseMap",prodCatWiseMap);
		context.put("roleTypeList",roleTypeList);
		context.put("categoryType",parameters.categoryType);
		
	} catch (GenericEntityException e) {
		Debug.logError(e, module);
	}
  }
}