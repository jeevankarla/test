


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
}