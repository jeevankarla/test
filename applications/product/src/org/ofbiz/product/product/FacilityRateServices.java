


package org.ofbiz.product.product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.NullPointerException;
import java.lang.SecurityException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.image.ScaleImage;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericDelegator;




/**
 * Product Services
 */
public class FacilityRateServices {

    public static final String module = FacilityRateServices.class.getName();
    
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals;
    private static int rounding;
    public static final String resource_error = "OrderErrorUiLabels";
    static {
        decimals = 1;//UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
	public static String createNewMultiFacilityRate(HttpServletRequest request,HttpServletResponse response){
			
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Locale locale = UtilHttp.getLocale(request);
			
			String boothId = (String) request.getParameter("BoothId");
			String productId = null;
			String facilityId = null;
			BigDecimal discountAmount = BigDecimal.ZERO;
			String discountAmountStr = null;
			String fromDateStr = null;
			Timestamp fromDate = null;
			
			Map<String, Object> result = ServiceUtil.returnSuccess();
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			
			try {
				Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
				int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
				
				if (rowCount < 1) {
					Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
					request.setAttribute("_ERROR_MESSAGE_", "No Facility records found");	
					return "error";
				}
				
				for (int i = 0; i < rowCount; i++) {
					String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
					productId = (String) paramMap.get("productId"+ thisSuffix);
					facilityId = (String) paramMap.get("facilityId"+ thisSuffix);
					discountAmountStr = (String) paramMap.get("discountAmount"+ thisSuffix);
					fromDateStr = (String) paramMap.get("fromDate"+ thisSuffix);
					if(UtilValidate.isEmpty(productId)||UtilValidate.isEmpty(facilityId)||UtilValidate.isEmpty(discountAmountStr)||UtilValidate.isEmpty(fromDateStr)){
						continue;
					}
					
					if (UtilValidate.isNotEmpty(fromDateStr)) {
			        	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");             
			             try {
			            	 fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
			             } catch (ParseException e) {
			                 Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);
			                // effectiveDate = UtilDateTime.nowTimestamp();
			             } catch (NullPointerException e) {
			                 Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);
			                 //effectiveDate = UtilDateTime.nowTimestamp();
			             }
			        }
					
					discountAmount = new BigDecimal(discountAmountStr);
					
					Map<String, Object> facilityRateContext = FastMap.newInstance();
					facilityRateContext.put("userLogin", userLogin);   
					facilityRateContext.put("facilityId", facilityId);
					facilityRateContext.put("productId", productId);
					facilityRateContext.put("fromDate", fromDate);
					facilityRateContext.put("amount", discountAmount);
					result = dispatcher.runSync("createFacilityRate",facilityRateContext);
			      
					if( ServiceUtil.isError(result)) {
						String errMsg =  ServiceUtil.getErrorMessage(result);
						Debug.logWarning(errMsg , module);
						request.setAttribute("_ERROR_MESSAGE_",errMsg);
						return "error";
					}
				}	
				
			} catch (Exception e) {				
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
				Debug.logWarning(e.getMessage() , module);
				return "error";
			}
			request.setAttribute("facilityId", facilityId);
			request.setAttribute("_EVENT_MESSAGE_", "Facility Rates Successfully Created");
		    return "success";
		
			
	}
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createFacilityPartyRate(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess("Booth discount updated successfully.");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String)context.get("facilityId");
		String productId = (String)context.get("productId");
		BigDecimal amount = (BigDecimal)context.get("amount");
		String supplyTypeEnumId = (String)context.get("supplyTypeEnumId");
		String partyId = (String)context.get("partyId");
		String rateTypeId = (String)context.get("rateTypeId");
		String priceActionTypeId = (String)context.get("priceActionTypeId");
		BigDecimal threshold = (BigDecimal)context.get("threshold");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(context.get("fromDate"))){
			 fromDate = (Timestamp) context.get("fromDate");
		}	
		Timestamp dayStart = UtilDateTime.getDayStart(fromDate);
		Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayStart, -1));
		if(UtilValidate.isEmpty(productId)){
			productId = "_NA_";
		}
		if(UtilValidate.isEmpty(facilityId)){
			facilityId = "_NA_";
		}
		if(UtilValidate.isEmpty(supplyTypeEnumId)){
			supplyTypeEnumId = "_NA_";
		}
		try{			
			GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
			if(UtilValidate.isNotEmpty(facility)){
			if(!(facility.getString("facilityTypeId")).equals("BOOTH")){
				Debug.logError(facilityId+"====is not a booth", module);    			
	            return ServiceUtil.returnError(facilityId+"====is not a booth");
				}
			}else{
				Debug.logError(facilityId+" ====is not a valid facilityId", module);
				return ServiceUtil.returnError(facilityId+" is not a valid facilityId");
			}
			List condList = FastList.newInstance();
		//	condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "RATE_HOUR"));
			condList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, "INR"));
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facility.getString("ownerPartyId")));	    	
		//	condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, "_NA_"));
		//	condList.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, "_NA_"));	    	
			/*if((facility.getString("categoryTypeEnum")).equals("VENDOR") ){
				rateTypeId = "VENDOR_DEDUCTION";				
			}else if((facility.getString("categoryTypeEnum")).equals("SO_INST") ){
				rateTypeId = "SO_INST_MRGN";				
			}else if((facility.getString("categoryTypeEnum")).equals("CR_INST") ){
				rateTypeId = "CR_INST_MRGN";				
			}else{
				if(UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))){
						rateTypeId = facility.getString("categoryTypeEnum")+"_MRGN";
				}else{
					rateTypeId = "VENDOR_DEDUCTION";	
				}
			}*/
			condList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> rateAmounts = delegator.findList("FacilityPartyRate", condition, null, null, null, false);
			rateAmounts =EntityUtil.filterByDate(rateAmounts ,dayStart);
			GenericValue rateAmount = EntityUtil.getFirst(rateAmounts);
			if(UtilValidate.isNotEmpty(rateAmount)){
				rateAmount.put("thruDate", previousDayEnd);
				delegator.store(rateAmount);
				// lets create new rate amount record for the Booth
				rateAmount.put("thruDate", null);
				rateAmount.put("fromDate", dayStart);
				rateAmount.put("productId", productId);
				rateAmount.put("facilityId", facilityId);
				rateAmount.put("supplyTypeEnumId", supplyTypeEnumId);
				rateAmount.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				rateAmount.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				rateAmount.put("rateAmount", amount);
				delegator.createOrStore(rateAmount);
			}else{
				GenericValue newRateAmount = delegator.makeValue("FacilityPartyRate");				
			//	newRateAmount.put("periodTypeId", "RATE_HOUR");
				newRateAmount.put("rateCurrencyUomId", "INR");
				if(UtilValidate.isEmpty(partyId)){
				newRateAmount.put("partyId", facility.getString("ownerPartyId"));
				}else{
					newRateAmount.put("partyId", partyId);
				}
			//	newRateAmount.put("workEffortId", "_NA_");
			//	newRateAmount.put("emplPositionTypeId", "_NA_");				
				newRateAmount.put("rateTypeId", rateTypeId);
				newRateAmount.put("priceActionTypeId", priceActionTypeId);
				newRateAmount.put("threshold", threshold);
				newRateAmount.put("fromDate", dayStart);
				newRateAmount.put("productId", productId);
				newRateAmount.put("facilityId", facilityId);
				newRateAmount.put("supplyTypeEnumId", supplyTypeEnumId);
				newRateAmount.put("rateAmount", amount);	
				newRateAmount.put("createdDate",UtilDateTime.nowTimestamp());
				newRateAmount.put("createdByUserLogin",userLogin.getString("userLoginId"));
				newRateAmount.put("lastModifiedDate", UtilDateTime.nowTimestamp());
				newRateAmount.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				delegator.create(newRateAmount);				
			}	
			condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, dayStart));
			EntityCondition condition1 = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> futureDaysRateAmounts = delegator.findList("FacilityPartyRate", condition1, null, null, null, false);
			delegator.removeAll(futureDaysRateAmounts);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError( e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		
		return result;
	   }
}