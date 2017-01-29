package in.vasista.vbiz.depotsales;
import java.text.DateFormat;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;
import in.vasista.vbiz.depotsales.DepotHelperServices;
import in.vasista.vbiz.depotsales.DepotSalesServices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.util.Iterator;

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.base.conversion.NumberConverters.BigDecimalToString;
import org.ofbiz.base.test.BaseUnitTests;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.order.order.OrderReadHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.party.party.PartyHelper;

import java.util.Map.Entry;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class paymentApiServices {
	
	
	public static final String module = paymentApiServices.class.getName();
	private static int decimals;
	private static int rounding;
    public static final String resource = "AccountingUiLabels";
    private static String SucUrl = "https:%2F%2Fnhdc-test.vasista.in/myportal/control/paymentSucUrl";
    private static String failUrl = "https:%2F%2Fnhdc-test.vasista.in/myportal/control/paymentFailUrl";
    private static String airtelBaseUrl = "https://sit.airtelmoney.in/ecom/v2/";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    
    /*
     * Security check to make userLogin partyId must equal facility owner party Id if the user
     * is a retailer (has MOB_RTLR_DB_VIEW). If user is a sales rep (MOB_SREP_DB_VIEW permission), 
     * then we just return true.
     */
    static boolean hasFacilityAccess(DispatchContext dctx, Map<String, ? extends Object> context) {  
        Security security = dctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	GenericValue party = (GenericValue) context.get("party");
        if (security.hasEntityPermission("MOB_SREP_DB", "_VIEW", userLogin)) {
            return true;
        } 		
        if (security.hasEntityPermission("MOB_RTLR_DB", "_VIEW", userLogin)) {
        	if (userLogin != null && userLogin.get("partyId") != null) {
        		String userLoginParty = (String)userLogin.get("partyId");
        		String ownerParty = (String)party.get("partyId");
        		if (userLoginParty.equals(ownerParty)) {
        			return true;
        		}
        	}
        }
    	return false;
    }
    
    
    public static Map<String, Object> makeOrderPayment(DispatchContext dctx,Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Map<String, Object> resultResult = dispatcher.runSync("createOrderPayment", context);
			if (ServiceUtil.isError(resultResult)) {
				Debug.logWarning("There was an error making Payment: "
						+ ServiceUtil.getErrorMessage(resultResult), module);
				return ServiceUtil
						.returnError("There was an error making Payment:"
								+ ServiceUtil.getErrorMessage(resultResult));
			}
			result = resultResult;
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error calling payment service",
					module);
		}
		return result;
	}
    
    public static Map<String, Object> makeInvoicePayment(DispatchContext dctx,Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			Map<String, Object> resultResult = dispatcher.runSync("createInvoiceApplyPayment", context);
			if (ServiceUtil.isError(resultResult)) {
				Debug.logWarning("There was an error making Payment: "
						+ ServiceUtil.getErrorMessage(resultResult), module);
				return ServiceUtil
						.returnError("There was an error making Payment:"
								+ ServiceUtil.getErrorMessage(resultResult));
			}
			result = resultResult;
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error calling payment service",
					module);
		}
		return result;
	}
    
    
    
    
    public static Map<String, Object> makeWeaverPayment(DispatchContext dctx,Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
    
		 String paymentDate = (String) context.get("paymentDate");
		 String partyIdFrom = (String) context.get("partyId");
		 String orderId = (String) context.get("orderId");
		 String partyIdTo = "";
		 String amount = (String) context.get("amount");
		 String paymentRefNum = (String) context.get("transactionId");
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 String orderPaymentPreferenceId = null;
		 Map<String, Object> createCustPaymentFromPreferenceMap = new HashMap();
		 if(UtilValidate.isNotEmpty(amount)){
			 Map<String, Object> serviceContext = UtilMisc.toMap("orderId", orderId,"paymentMethodTypeId", "MOBILE_PAYIN","statusId","PMNT_RECEIVED", "userLogin", userLogin);
			 
			 String purposeTypeId = null;
		  	GenericValue orderHeader = null;
		    try {
		    	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		    } catch (GenericEntityException e) {
	            Debug.logError(e, "error fetching order with order id :"+orderId, module);
	            return ServiceUtil.returnError("error fetching order with order id :"+orderId);
	        }
		    
		    Timestamp eventDate = null;
			Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
			  	
			if (UtilValidate.isNotEmpty(paymentDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");  
				try {
					eventDate = new java.sql.Timestamp(sdf.parse(paymentDate).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
					eventDate = UtilDateTime.nowTimestamp();
					Debug.log("paymentDate============="+paymentDate);
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
					eventDate = UtilDateTime.nowTimestamp();
					Debug.log("paymentDate============="+paymentDate);
				}
			}
	      
			if (UtilValidate.isEmpty(paymentDate)) {
	    	  eventDate = UtilDateTime.nowTimestamp();
			}
		    Debug.log("eventDate=============="+eventDate);
		    try {
		    	 Map<String, Object> OrderPref = ServiceUtil.returnSuccess();
		    	 OrderPref = dispatcher.runSync("createOrderPaymentPreference", serviceContext);
		         orderPaymentPreferenceId = (String) OrderPref.get("orderPaymentPreferenceId");
		         Map<String, Object> serviceCustPaymentContext = UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId,"amount",amount,"eventDate",eventDate,"userLogin", userLogin, "purposeTypeId",purposeTypeId);
		         createCustPaymentFromPreferenceMap = dispatcher.runSync("createCustPaymentFromPreference", serviceCustPaymentContext);
		         String paymentId = (String)createCustPaymentFromPreferenceMap.get("paymentId");
		         result.put("paymentId",paymentId);
		         GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
		         if(UtilValidate.isNotEmpty(payment)){
		        	 result.put("paymentMethodTypeId",payment.getString("paymentMethodTypeId"));
	        		 payment.set("paymentRefNum", paymentRefNum);
	        		 payment.store();
		         }
		         
		         
		  	} catch (Exception e) {
					 Debug.logError(e, e.toString(), module);
					  return ServiceUtil.returnError("AccountingTroubleCallingCreateOrderPaymentPreferenceService");	
		  	}
		 }

       return result;
	}
    
    
    public static Map<String, Object> createPaymentGateWayTrans(DispatchContext dctx,Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String orderId = (String)context.get("orderId");       
        String partyId = (String)context.get("partyId");
        String amt = (String)context.get("amount");
        Double amount = Double.valueOf(amt);
        String amtStr = amount.toString();
        String paymentMode = (String)context.get("paymentMode");
        String service = (String)context.get("service");
        String mobileNumber = (String)context.get("mobileNumber");
        String email = "";
        if (UtilValidate.isNotEmpty((String)context.get("email"))) {
        	email = (String)context.get("email");
        }
        	       
        Date txnDate = new Date();
        if(paymentMode.equalsIgnoreCase("airtel"))
        {
        	
        	String mId = "25649255";
        	String salt = "34602fa0";
        	String txnRefNo = generateTxnNum(txnDate,partyId, orderId);
        	String date = getDateInFormat(txnDate, "ddMMyyyyHHmmss");
        	String cur = "INR";        	
        	String url = initiatePaymentUrl(mId,txnRefNo,amtStr,date,salt,SucUrl,failUrl,cur,service,mobileNumber,email);
        	
        	Map resultMap = FastMap.newInstance();
    		resultMap.put("mId",mId);
    		resultMap.put("salt",salt);
    		resultMap.put("txnRefNo",txnRefNo);
    		resultMap.put("su", SucUrl);
    		resultMap.put("fu", failUrl);    	
    		resultMap.put("amt", amtStr);
    		resultMap.put("date", date);
    		resultMap.put("cur", cur);
    		resultMap.put("service", service);
    		resultMap.put("mobile", mobileNumber);
    		resultMap.put("email", email);
    		resultMap.put("msg","success");
    		resultMap.put("url",url);
    		
    		
    		result.put("resultMap",resultMap);
    		return result;
        }
                               
		return result;
    }
    
    public static String generateTxnNum(Date date,String partyId,String orderId) {  
    	String txnNum="";
        String tempDate = getDateInFormat(date,"ddMMmmss");
        if(orderId != null && orderId.length() != 0){
            txnNum = orderId+tempDate;
        }
        else{
            txnNum = partyId+tempDate;
        }
        return txnNum;
    }
    
    public static String getDateInFormat(Date d,String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(d);
    }

    public static String initiatePaymentUrl(String mid,String txnRefNo,String amt,String date,String salt,String su,String fu,String cur,String service,String mobile,String email) {
        String hash = "";
        try{
            hash=generateHash(mid,txnRefNo,amt,date,service,salt);
        } catch (UnsupportedEncodingException e) {
        }
        
        String action = "initiatePayment";
        String url = airtelBaseUrl+action+"?";
        url = url + "MID="+mid+"&";
        url = url + "TXN_REF_NO="+txnRefNo+"&";
        url = url + "SU="+su+"&";
        url = url + "FU="+fu+"&";
        url = url + "AMT="+amt+"&";
        url = url + "DATE="+date+"&";
        url = url + "CUR="+cur+"&";
        url = url + "HASH="+hash+"&";
        url = url + "service="+service+"&";
        url = url + "CUST_MOBILE="+mobile+"&";
        url = url + "CUST_EMAIL="+email;
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("sit.airtelmoney.in")
                .appendPath("ecom")
                .appendPath("v2")
                .appendPath("initiatePayment")
                .appendQueryParameter("MID", mid)
                .appendQueryParameter("TXN_REF_NO", txnRefNo)
                .appendQueryParameter("SU", su)
                .appendQueryParameter("FU", fu)
                .appendQueryParameter("AMT", amt)
                .appendQueryParameter("DATE", date)
                .appendQueryParameter("CUR", cur)
                .appendQueryParameter("HASH", hash)
                .appendQueryParameter("service",service)
                .appendQueryParameter("CUST_MOBILE",mobile)
                .appendQueryParameter("CUST_EMAIL",email);

        return builder.build().toString();*/
        
        return url;
    }
    
    public static String generateHash(String mid,String txnRefNo,String amt,String date,String service,String salt) throws  UnsupportedEncodingException{
        String text = mid+"#"+txnRefNo+"#"+amt+"#"+date+"#"+service+"#"+salt;
        StringBuffer sb = new StringBuffer();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(text.getBytes());
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        }catch (NoSuchAlgorithmException e) {
        }

        return sb.toString();
    }
	
}
