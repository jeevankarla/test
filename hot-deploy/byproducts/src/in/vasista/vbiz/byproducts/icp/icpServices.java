package in.vasista.vbiz.byproducts.icp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;

import in.vasista.vbiz.byproducts.ByProductServices;
public class icpServices {

    public static final String module = icpServices.class.getName();

   
    
	    
public static Map<String, Object> getIceCreamFactoryStore(Delegator delegator){
        
    	Map<String, Object> result = FastMap.newInstance(); 
    	String productStoreGroupId = "ICECREAM_PRODUCTS";
        List<GenericValue> byProdStores =FastList.newInstance();
            try{
           	if(UtilValidate.isNotEmpty(productStoreGroupId)){
           		byProdStores = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(EntityOperator.AND, "productStoreGroupId", productStoreGroupId), null, null, null, false);
            }
         }catch (GenericEntityException e) {
            	Debug.logError(e, module);
            }
         for (GenericValue byProdStore : byProdStores) {
        	 GenericValue productStore = null;
     		try {
     			productStore = byProdStore.getRelatedOne("ProductStore");
     		} catch (GenericEntityException e) {
     			Debug.logError(e, module); 
     		}
     		if(UtilValidate.isNotEmpty(productStore.getString("isFactoryStore")) && (productStore.getString("isFactoryStore").equals("Y")  )){
     			String productStoreId = productStore.getString("productStoreId");
     			result.put("factoryStore", productStore);
     			result.put("factoryStoreId", productStoreId);
     			continue;
     		}
         }
         
    	return result;
	}
public static String processIcpSale(HttpServletRequest request, HttpServletResponse response) {
	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  DispatchContext dctx =  dispatcher.getDispatchContext();
	  Locale locale = UtilHttp.getLocale(request);
	  String boothId = (String) request.getParameter("boothId");
	  
	  Map resultMap = FastMap.newInstance();
	  List invoices = FastList.newInstance(); 
	  //String chequeDate = (String) request.getParameter("chequeDate");
	  String routeId = (String) request.getParameter("routeId");
	 String vehicleId = (String) request.getParameter("vehicleId");
	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
	  String shipmentTypeId = (String) request.getParameter("shipmentTypeId");
	  String subscriptionTypeId = "AM";
	  String partyIdFrom = "";
	 String shipmentId = "";
	  if(UtilValidate.isEmpty(shipmentTypeId)){
		  routeId = (String) request.getAttribute("routeId");
	  	   productSubscriptionTypeId = (String) request.getAttribute("productSubscriptionTypeId");
	  	  shipmentTypeId = (String) request.getAttribute("shipmentTypeId");
	  	shipmentId=(String)request.getAttribute("shipmentId");
		boothId=(String)request.getAttribute("boothId");
	  }
	  if(shipmentTypeId.equals("BYPROD_PM_SUPPL")){
     	subscriptionTypeId = "PM";       	
       }
	   String salesChannel = "";
	   
	  
	  if(shipmentTypeId.equals("RM_DIRECT_SHIPMENT")){
		 salesChannel = "RM_DIRECT_CHANNEL";      	
       }
	  if(shipmentTypeId.equals("ICP_DIRECT_SHIPMENT")){
			 salesChannel = "ICP_NANDINI_CHANNEL";      	
	     }
	  if(UtilValidate.isNotEmpty(request.getParameter("salesChannel"))){
		  salesChannel=(String)request.getParameter("salesChannel");
	  }
	  //String shipmentTypeId = "BYPRODUCTS_SUPPL"; 
	  // String salesChannel = "BYPROD_SALES_CHANNEL";
   List<String> leakProductList =UtilMisc.toList("1");//for BMP 200 ml leaks to be added
	  String productId = null;
	  String quantityStr = null;
	  Timestamp effectiveDate=null;
	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
	  BigDecimal quantity = BigDecimal.ZERO;
	  BigDecimal butterLeakQty = BigDecimal.ZERO;
	  List<GenericValue> subscriptionList=FastList.newInstance();
	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  HttpSession session = request.getSession();
	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	  GenericValue subscription = null;
	  GenericValue facility = null;
	  List custTimePeriodList = FastList.newInstance();  
	  Timestamp instrumentDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
		  try {
			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
			/*if(UtilValidate.isNotEmpty(chequeDate)){
				instrumentDate = new java.sql.Timestamp(sdf.parse(chequeDate).getTime());
			  }*/
				  
		  } catch (ParseException e) {
			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
           // effectiveDate = UtilDateTime.nowTimestamp();
		  } catch (NullPointerException e) {
			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
            //effectiveDate = UtilDateTime.nowTimestamp();
		  }
	  }
	  if (boothId == "") {
			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
			return "error";
	  }
	  
	 if (UtilValidate.isNotEmpty(request.getAttribute("estimatedDeliveryDate"))) {
		effectiveDate = (Timestamp) request.getAttribute("estimatedDeliveryDate");
	 }
   // Get the parameters as a MAP, remove the productId and quantity params.
	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  if (rowCount < 1) {
		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
		  return "success";
	  }
	  try{
		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
		routeId=facility.getString("parentFacilityId");
		  if(UtilValidate.isEmpty(facility)){
			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
			  return "error";
		  }
			  
	  }catch (GenericEntityException e) {
		  Debug.logError(e, "Booth does not exist", module);
		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+ boothId +"'"+" does not exist");
		  return "error";
	  }
	  try {
		  List conditionList =FastList.newInstance();
		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));			 
       conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId),EntityOperator.OR,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
       
		EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
		  subscriptionList=delegator.findList("SubscriptionAndFacility", subCond, null, null, null, false);
		  subscriptionList = EntityUtil.filterByDate(subscriptionList ,effectiveDate);
		  if(UtilValidate.isEmpty(subscriptionList)){
			  request.setAttribute("_ERROR_MESSAGE_", "Booth subscription does not exist");
			  return "error";     		
		  }
		  subscription = EntityUtil.getFirst(subscriptionList);
	  }  catch (GenericEntityException e) {
		  Debug.logError(e, "Problem getting Booth subscription", module);
		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
		  return "error";
	  }
	// attempt to create a Shipment entity       
   
    List shipmentList=FastList.newInstance();
    Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
    Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
    List conditionList = FastList.newInstance();
    
    if(shipmentTypeId.equals("ICP_DIRECT_SHIPMENT")){//for Direct sale Each Time we will create Shipment ...
 	  if(UtilValidate.isEmpty(shipmentId)){
 	   GenericValue newDirShip = delegator.makeValue("Shipment");        	 
 	   newDirShip.set("estimatedShipDate", effectiveDate);
 	   newDirShip.set("shipmentTypeId", shipmentTypeId);
 	   newDirShip.set("statusId", "GENERATED");
 	   newDirShip.set("originFacilityId", boothId);
 	   newDirShip.set("vehicleId", vehicleId);
 	   newDirShip.set("routeId", routeId);
 	   newDirShip.set("createdDate", nowTimeStamp);
 	   newDirShip.set("createdByUserLogin", userLogin.get("userLoginId"));
 	   newDirShip.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
          try {
              delegator.createSetNextSeqId(newDirShip);            
             shipmentId = (String) newDirShip.get("shipmentId");
          } catch (GenericEntityException e) {
              Debug.logError(e, module);
              request.setAttribute("_ERROR_MESSAGE_", "un able to create shipment id for DirectOrder.");
  			return "error";                 
          }  
 	  }
    }/*else{//for Gatepass Type same shipment need to be considered
 	   // lets get the shipment if already exits else create new one
	  	shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, UtilDateTime.getDayStart(effectiveDate), UtilDateTime.getDayEnd(effectiveDate), shipmentTypeId);
	  	if(UtilValidate.isEmpty(shipmentList)){
     	 GenericValue newEntity = delegator.makeValue("Shipment");        	 
          newEntity.set("estimatedShipDate", effectiveDate);
          newEntity.set("shipmentTypeId", shipmentTypeId);
          newEntity.set("statusId", "GENERATED");
          newEntity.set("routeId", routeId);
          newEntity.set("createdDate", nowTimeStamp);
          newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
          newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
          
          try {
              delegator.createSetNextSeqId(newEntity);            
             shipmentId = (String) newEntity.get("shipmentId");
             
          } catch (GenericEntityException e) {
              Debug.logError(e, module);
              request.setAttribute("_ERROR_MESSAGE_", "un able to create shipment id.");
              
  			return "error";                 
          }  
     	
     }else{
     	shipmentId = (String)shipmentList.get(0);
     }
    }*/
     request.setAttribute("shipmentId",shipmentId);
     
      conditionList.clear();
	  List<GenericValue> subscriptionProductsList =FastList.newInstance();
	  
	  for (int i = 0; i < rowCount; i++) {
		  GenericValue subscriptionFacilityProduct = delegator.makeValue("SubscriptionFacilityAndSubscriptionProduct");
		  subscriptionFacilityProduct.set("facilityId", subscription.get("facilityId"));
		  subscriptionFacilityProduct.set("subscriptionId", subscription.get("subscriptionId"));
		  subscriptionFacilityProduct.set("categoryTypeEnum", subscription.get("categoryTypeEnum"));
		  subscriptionFacilityProduct.set("ownerPartyId", subscription.get("ownerPartyId"));
		  subscriptionFacilityProduct.set("productSubscriptionTypeId", productSubscriptionTypeId);
		  
		  Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
		 
		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  if (paramMap.containsKey("productId" + thisSuffix)) {
			  productId = (String) paramMap.get("productId" + thisSuffix);
		  }
		  else {
			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
			  return "error";			  
		  }
		  	  
		  
		  if (paramMap.containsKey("quantity" + thisSuffix)) {
			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
		  }
		  else {
			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
			  return "error";			  
		  }		  
		  if (quantityStr.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
			  return "error";	
		  }
		  try {
			  quantity = new BigDecimal(quantityStr);
		  } catch (Exception e) {
			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
			  return "error";
		  } 
		subscriptionFacilityProduct.set("productId", productId);
		subscriptionFacilityProduct.set("quantity", quantity);
		subscriptionProductsList.add(subscriptionFacilityProduct);
	  }//end row count for loop
	  
	 if( UtilValidate.isEmpty(subscriptionProductsList)){
		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
		 request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
		  return "success";
	 }
	 List<String> orderBy = UtilMisc.toList("subscriptionId", "productSubscriptionTypeId","-productId"); 
	subscriptionProductsList=EntityUtil.orderBy(subscriptionProductsList,orderBy);
	
  List<GenericValue> orderSubProdsList = FastList.newInstance();  
	 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	 processChangeIndentHelperCtx.put("shipmentId", shipmentId);
	 processChangeIndentHelperCtx.put("estimatedDeliveryDate", effectiveDate);
	 processChangeIndentHelperCtx.put("salesChannel", salesChannel);
	 processChangeIndentHelperCtx.put("enableAdvancePaymentApp",  Boolean.FALSE);
	 String productStoreId = (String) (getIceCreamFactoryStore(delegator)).get("factoryStoreId");//to get Factory storeId
	 processChangeIndentHelperCtx.put("productStoreId",  productStoreId);
	 String orderId = null;
	 String invoiceId = null;
	   String tempSubId = "";
    String tempTypeId = "";
    String subId;
    String typeId;
  for (int j = 0; j < subscriptionProductsList.size(); j++) {
  	subId = subscriptionProductsList.get(j).getString("subscriptionId");
  	typeId = subscriptionProductsList.get(j).getString("productSubscriptionTypeId");
      	if (tempSubId == "") {
      		tempSubId = subId;
      		tempTypeId = typeId;        		
      	}
          /*condition: "!(typeId.startsWith(tempTypeId))" is to generate same order for CASH_FS and CASH*/
      	if (!tempSubId.equals(subId) || (!tempTypeId.equals(typeId))) {
				
					processChangeIndentHelperCtx.put("subscriptionProductsList", orderSubProdsList);
					processChangeIndentHelperCtx.put("shipmentId" , shipmentId);
					result =ByProductServices.createSalesOrderSubscriptionProductType(dctx, processChangeIndentHelperCtx);
					if (ServiceUtil.isError(result)) {
          			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
          			  request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For :" + boothId);
          			return "error";
          			//break;
          		}
					orderId = (String) result.get("orderId");
		  			 invoiceId = (String) result.get("invoiceId");
		  			 partyIdFrom = (String) result.get("partyId");
		  			invoices.add(invoiceId);
					
      		// quantity = (BigDecimal)result.get("quantity");                		
      		orderSubProdsList.clear();
      		tempSubId = subId;
      		tempTypeId = typeId;
      	}
      	orderSubProdsList.add(subscriptionProductsList.get(j));
  }
  if (orderSubProdsList.size() > 0) {
		
			processChangeIndentHelperCtx.put("subscriptionProductsList", orderSubProdsList);
			processChangeIndentHelperCtx.put("shipmentId" , shipmentId);
			result = ByProductServices.createSalesOrderSubscriptionProductType(dctx, processChangeIndentHelperCtx); 
			if (ServiceUtil.isError(result)) {
  			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
  		    request.setAttribute("_ERROR_MESSAGE_", "Problem creating Indent For AdhocSale :" + boothId);
  		    return "error";
			} 
			orderId = (String) result.get("orderId");
			 invoiceId = (String) result.get("invoiceId");
			 partyIdFrom = (String) result.get("partyId");
			invoices.add(invoiceId);
			 
  }
 
	request.setAttribute("supplyDate", effectiveDate);
	request.setAttribute("_EVENT_MESSAGE_", "Successfully made entry from party: "+boothId);	  	 
	return "success";
	}	
    
}