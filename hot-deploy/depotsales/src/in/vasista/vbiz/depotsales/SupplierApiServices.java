package in.vasista.vbiz.depotsales;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;
import in.vasista.vbiz.depotsales.DepotHelperServices;
import in.vasista.vbiz.depotsales.DepotSalesServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.party.PartyHelper;

public class SupplierApiServices {
	
	public static final String module = SupplierApiServices.class.getName();
	private static int decimals;
	private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    
    public static Map<String, Object> getSupplierDetails(DispatchContext dctx,Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		
		String partyId = (String) context.get("partyId");
		GenericValue partyDetail = null;
		Map resultMap = FastMap.newInstance();
		try{
			partyDetail = delegator.findOne("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyId", partyId,"roleTypeId","SUPPLIER"), false);
			if(UtilValidate.isEmpty(partyDetail)){
				Debug.logError("Not a valid party", module);
				return ServiceUtil.returnError("Not a valid party");
			}
			resultMap.put("partyId",partyDetail.get("partyId"));
			resultMap.put("partyName",partyDetail.get("groupName"));
			
			Map inputMap = FastMap.newInstance();
			Map addressMap = FastMap.newInstance();
			inputMap.put("partyId", partyDetail.get("partyId"));
			inputMap.put("userLogin", userLogin);
			try{
				addressMap  = dispatcher.runSync("getPartyPostalAddress", inputMap);
			} catch(Exception e){
				Debug.logError("Not a valid party", module);
			}
			String contactNumber = "";
			String email = "";
			try{
				Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("partyId", partyDetail.get("partyId"));
				Map<String, Object> serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	            } 
	            if(UtilValidate.isNotEmpty(serviceResult.get("contactNumber"))){
	            	contactNumber = (String) serviceResult.get("contactNumber");
	            	/*if(!UtilValidate.isEmpty(serviceResult.get("countryCode"))){
	            		contactNumber = (String) serviceResult.get("countryCode") +" "+ (String) serviceResult.get("contactNumber");
	            	}*/
	            }
			}
            catch (Exception e) {
				Debug.logError(e, "Error fetching contact number from getPartyTelephone service", module);
			}
			try{
		        Map<String, Object> partyEmail = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", partyDetail.get("partyId"), "userLogin", userLogin));
		        if (UtilValidate.isNotEmpty(partyEmail.get("emailAddress"))) {
		            email = (String) partyEmail.get("emailAddress");
		        }
			}catch (GenericServiceException e) {
                Debug.logError(e, "Problem while getting email address", module);
            }
			
			resultMap.put("addressMap",addressMap);
			resultMap.put("contactNumber",contactNumber);
			resultMap.put("email",email);
			
			String cstNum = "";
			String tanNum = "";
			String panNum = "";
			
			List condList = FastList.newInstance();
			List<GenericValue> PartyIdentification = null;
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyDetail.get("partyId")));
			condList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.IN, UtilMisc.toList("CST_NUMBER","PAN_NUMBER","TAN_NUMBER")));
	    	try{
	    		PartyIdentification = delegator.findList("PartyIdentification", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	    		if(UtilValidate.isNotEmpty(PartyIdentification)){
	    			List cstList = EntityUtil.filterByCondition(PartyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
	    			if(UtilValidate.isNotEmpty(cstList)){
	    				cstNum = (String) (EntityUtil.getFirst(cstList)).get("idValue");
	    			}
	    			List tanList = EntityUtil.filterByCondition(PartyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TAN_NUMBER"));
	    			if(UtilValidate.isNotEmpty(tanList)){
	    				tanNum = (String) (EntityUtil.getFirst(tanList)).get("idValue");
	    			}
	    			List panList = EntityUtil.filterByCondition(PartyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PAN_NUMBER"));
	    			if(UtilValidate.isNotEmpty(panList)){
	    				panNum = (String) (EntityUtil.getFirst(panList)).get("idValue");
	    			}
	    		}
	    	} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
	    	resultMap.put("cstNum",cstNum);
	    	resultMap.put("tanNum",tanNum);
	    	resultMap.put("panNum",panNum);
			
			result.put("supplierDetails",resultMap);
		}catch(GenericEntityException e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		
		return result;
    }
}