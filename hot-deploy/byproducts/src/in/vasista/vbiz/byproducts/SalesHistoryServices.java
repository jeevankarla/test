

package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.tools.ant.filters.TokenFilter.ContainsString;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.network.DeprecatedNetworkServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;






import java.text.SimpleDateFormat;

public class SalesHistoryServices {
	
	public static final String module = SalesHistoryServices.class.getName();  
	
	 public static Map<String, Object>  populatePeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String periodBillingId = null;
			String customTimePeriodId = (String) context.get("customTimePeriodId");
			String billingTypeId = (String) context.get("billingTypeId");			
			List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS , billingTypeId));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    		if(!UtilValidate.isEmpty(periodBillingList)){
	    			Debug.logError("Failed to create 'MarginReport': Already generated or In-process for the specified period", module);
	    			return ServiceUtil.returnError("Failed to create 'MarginReport': Already generated or In-process for the specified period");
	    		}
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			} 
	    	
	    	GenericValue newEntity = delegator.makeValue("PeriodBilling");
	        newEntity.set("billingTypeId", billingTypeId);
	        newEntity.set("customTimePeriodId", customTimePeriodId);
	        newEntity.set("statusId", "IN_PROCESS");
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
	        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    try {     
		        delegator.createSetNextSeqId(newEntity);
		        Debug.log("====Populate periodBilling is callled=====================in new services...====");
				periodBillingId = (String) newEntity.get("periodBillingId");	
				Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"billingTypeId", billingTypeId, "customTimePeriodId", customTimePeriodId,"userLogin", userLogin);
				if(billingTypeId.equals("PB_LMS_VNDR_MRGN")){
					dispatcher.runAsync("generateVendorMargin", runSACOContext);					
				}
				if(billingTypeId.equals("PB_LMS_TRSPT_MRGN")){					
					dispatcher.runAsync("generateTranporterMargin", runSACOContext);
				}
	    	} catch (GenericEntityException e) {
				Debug.logError(e,"Failed To Create New Period_Billing", module);
				e.printStackTrace();
			}
	        catch (GenericServiceException e) {
	            Debug.logError(e, "Error in calling 'generateVendorMargin' service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        } 
	        result.put("periodBillingId", periodBillingId);
	        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	    	return result;
	    }
	 
	  public static Map<String, Object>  populateFacilityCommissiions(DispatchContext dctx, Map<String, ? extends Object> context, List masterList, String periodBillingId)  {
			
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String billingTypeId = (String) context.get("billingTypeId");			
			if(billingTypeId.equals("PB_LMS_VNDR_MRGN")){
				if(masterList.size() == 0){
					Debug.logError("masterList is empty", module);
					return ServiceUtil.returnError("masterList is empty");
				}
				for(int i=0; i< masterList.size(); i++){
					Map boothMargins = FastMap.newInstance();
					boothMargins = (Map) masterList.get(i);
					Iterator boothMarginsIter = boothMargins.entrySet().iterator();
					while (boothMarginsIter.hasNext()) {
						Map.Entry boothEntry = (Entry) boothMarginsIter.next();
						String facilityId = (String) boothEntry.getKey();
						List dayWiseBoothValues = (List) boothEntry.getValue();
						for(int j=0; j< dayWiseBoothValues.size(); j++ ){
							Map dayTotalsMap = (Map) dayWiseBoothValues.get(j);
							Iterator dayTotalsIter = dayTotalsMap.entrySet().iterator();
							while (dayTotalsIter.hasNext()) {
								Map.Entry dayEntry = (Entry) dayTotalsIter.next();
								Timestamp commissionDate =  (Timestamp) dayEntry.getKey();
								Map<String, BigDecimal> valuesMap = (Map<String, BigDecimal>) dayEntry.getValue();
								try {
									GenericValue facilityCommission = delegator.findOne("FacilityCommission", UtilMisc.toMap("periodBillingId", periodBillingId, "commissionDate", commissionDate, "facilityId",facilityId), false);
									if (facilityCommission == null) {
										facilityCommission = delegator.makeValue("FacilityCommission");
										facilityCommission.put("periodBillingId", periodBillingId );
										facilityCommission.put("commissionDate", commissionDate);
										facilityCommission.put("facilityId", facilityId); 
										facilityCommission.put("totalQty", valuesMap.get("TOTAL"));
										facilityCommission.put("cardQty", valuesMap.get("CARD"));
										facilityCommission.put("cashQty", valuesMap.get("CASH"));
										facilityCommission.put("splOrderQty", valuesMap.get("SPECIAL_ORDER"));
										facilityCommission.put("totalAmount", valuesMap.get("TOTAL_MR"));
										facilityCommission.put("cardAmount", valuesMap.get("CARD_MR"));
										facilityCommission.put("cashAmount", valuesMap.get("CASH_MR"));
										facilityCommission.put("splOrderAmount", valuesMap.get("SPECIAL_ORDER_MR"));
										facilityCommission.put("dues", valuesMap.get("CASH_DUE"));
										facilityCommission.put("recovery", valuesMap.get("RECOVERY"));
		    		                
										facilityCommission.create();    
									}
									else { 
										Debug.logError("facilityCommission Already Exists", module);
										return ServiceUtil.returnError("facilityCommission Already Exists");
									}
								} catch (GenericEntityException e) {
									Debug.logError("Error While Creating New FacilityCommistion", module);
									return ServiceUtil.returnError("Error While Creating New FacilityCommistion");
								}
							}
						}
					}
				}
			}				
			if(billingTypeId.equals("PB_LMS_TRSPT_MRGN")){	
				for(int i=0; i< masterList.size(); i++){
					Map routeMargins = FastMap.newInstance();					
					routeMargins = (Map) masterList.get(i);
					Iterator routeMarginsIter = routeMargins.entrySet().iterator();					
					while (routeMarginsIter.hasNext()) {
						Map.Entry routeEntry = (Entry) routeMarginsIter.next();						
						String facilityId = (String) routeEntry.getKey();		
						Map dayWiseRouteValues = (Map) routeEntry.getValue();
						Iterator dayWiseRouteIter = dayWiseRouteValues.entrySet().iterator();
						while (dayWiseRouteIter.hasNext()) {
							Map.Entry dayRouteEntry = (Entry) dayWiseRouteIter.next();									
								Timestamp dateValue = (Timestamp) dayRouteEntry.getKey();
								Map dayRouteEntryMap = (Map) dayRouteEntry.getValue();								
							try{	
								GenericValue facilityCommission = delegator.findOne("FacilityCommission", UtilMisc.toMap("periodBillingId", periodBillingId, "commissionDate", dateValue, "facilityId",facilityId), false);
								if (facilityCommission == null) {
						    		BigDecimal dayTotQty = BigDecimal.ZERO;
						    		BigDecimal commision = BigDecimal.ZERO;
						    		
						    		if(UtilValidate.isNotEmpty(dayRouteEntryMap.get("quantity"))){
						    		 dayTotQty=(BigDecimal)dayRouteEntryMap.get("quantity");
						    		}
						    		if(UtilValidate.isNotEmpty(dayRouteEntryMap.get("commision"))){
						    			commision=(BigDecimal)dayRouteEntryMap.get("commision");
							    	}
						    		
									facilityCommission = delegator.makeValue("FacilityCommission");
									facilityCommission.put("periodBillingId", periodBillingId );
									facilityCommission.put("commissionDate", dateValue);
									facilityCommission.put("facilityId", facilityId); 
									facilityCommission.put("totalQty", dayTotQty);									
									facilityCommission.put("totalAmount", commision);
									facilityCommission.put("dues", dayRouteEntryMap.get("dueAmount"));
									facilityCommission.create();    
								}else { 
									Debug.logError("facilityCommission Already Exists", module);
									return ServiceUtil.returnError("facilityCommission Already Exists");
								}
							} catch (GenericEntityException e) {
									Debug.logError("Error While Creating New FacilityCommistion", module);
									return ServiceUtil.returnError("Error While Creating New FacilityCommistion");
							}						
							
						}					 
					}
				}
			}
	        return result;
	    }

}
