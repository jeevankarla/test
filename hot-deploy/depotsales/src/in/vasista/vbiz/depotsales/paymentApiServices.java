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

import net.sf.json.JSONSerializer; 

import org.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.party.party.PartyHelper;

import java.util.Map.Entry;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.net.ssl.HttpsURLConnection;

import java.text.DecimalFormat;

import java.util.Arrays;
public class paymentApiServices {
	
	
	public static final String module = paymentApiServices.class.getName();
	private static int decimals;
	private static int rounding;
    public static final String resource = "AccountingUiLabels";
    private static String SucUrl = "https://nhdc-test.vasista.in/myportal/control/paymentSucUrl";
    private static String failUrl = "https://nhdc-test.vasista.in/myportal/control/paymentFailUrl";
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
		
		Debug.log("context==================="+context);
		
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
			
			String txnRefNo = (String) context.get("txnRefNo");
			GenericValue pgTrans = null;
		    try {
		    	pgTrans = delegator.findOne("PaymentGatewayTrans", UtilMisc.toMap("transactionId", txnRefNo), false);
		    } catch (GenericEntityException e) {
	            Debug.logError(e, "error fetching transactionId :"+txnRefNo, module);
	            return ServiceUtil.returnError("error fetching transactionId :"+txnRefNo);
	        }
			
		    if (UtilValidate.isNotEmpty(pgTrans)) {
		    	String paymentChannel = pgTrans.getString("paymentChannel");
		    	if(paymentChannel.equalsIgnoreCase("airtel")){
		    		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
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
		    	if(paymentChannel.equalsIgnoreCase("atom")){
		    		SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		    		try {
						eventDate = new java.sql.Timestamp(sdf1.parse(paymentDate).getTime());
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
		    	
		    		    			  
		    }
			
						  					
	      
			if (UtilValidate.isEmpty(paymentDate)) {
	    	  eventDate = UtilDateTime.nowTimestamp();
			}
		    Debug.log("eventDate=============="+eventDate);
		    String paymentId = "";
		    try {
		    	 Map<String, Object> OrderPref = ServiceUtil.returnSuccess();
		    	 OrderPref = dispatcher.runSync("createOrderPaymentPreference", serviceContext);
		         orderPaymentPreferenceId = (String) OrderPref.get("orderPaymentPreferenceId");
		         Map<String, Object> serviceCustPaymentContext = UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId,"amount",amount,"eventDate",eventDate,"userLogin", userLogin, "purposeTypeId",purposeTypeId);
		         createCustPaymentFromPreferenceMap = dispatcher.runSync("createCustPaymentFromPreference", serviceCustPaymentContext);
		         paymentId = (String)createCustPaymentFromPreferenceMap.get("paymentId");
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
		    try {
		    	String pgTransStatus = (String)context.get("txnStatus");
			    String txnMessage = (String)context.get("txnMessage");
		    	pgTrans.set("pgTransId",paymentRefNum);
		    	pgTrans.set("pgTransDate",eventDate);
		    	
		    	if (UtilValidate.isNotEmpty(paymentId)) {
		    		pgTrans.set("paymentId",paymentId);
		    	}
		    	if (UtilValidate.isNotEmpty(txnMessage)) {
		    		//pgTrans.set("pgTransMessage",txnMessage);
		    	}
		    	if(pgTransStatus.equalsIgnoreCase("SUC")){
		    		pgTrans.set("pgTransStatus","SUCCESS");
		    		pgTrans.set("transactionStatus","SUCCESS");
		    	}
		    	else{
		    		pgTrans.set("pgTransStatus","FAILED");
		    		pgTrans.set("transactionStatus","FAILED");
		    	}
		    	pgTrans.store();
		    }
		    catch (GenericEntityException e) {
		    	Debug.logError("Error While Payment gateway trans", module);
		    	return ServiceUtil.returnError("Error While Payment gateway trans" + txnRefNo);
		    }	
		    
		    
		 }

       return result;
	}
    
    
    public static Map<String, Object> createPaymentGatewayTrans(DispatchContext dctx,Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String orderId = (String)context.get("orderId");       
        String partyId = (String)context.get("partyId");
        String amt = (String)context.get("amount");
        Double amount = Double.valueOf(amt);
        String amtStr = amount.toString();
        String paymentChannel = (String)context.get("paymentChannel");
        String paymentMode = (String)context.get("paymentMode");
        String mobileNumber = (String)context.get("mobileNumber");
        String email = "";
        if (UtilValidate.isNotEmpty((String)context.get("email"))) {
        	email = (String)context.get("email");
        }
        Map resultMap = FastMap.newInstance();	       
        Date txnDate = new Date();
        String txnRefNo = generateTxnNum(txnDate,partyId, orderId);
    	String date = getDateInFormat(txnDate, "ddMMyyyyHHmmss");
          
        try{
    		Map transMap = FastMap.newInstance();
    		Timestamp transDate = null;
    		transMap.put("transactionId",txnRefNo);
    		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        	try {
        		transDate = new java.sql.Timestamp(sdf.parse(date).getTime());
        	} catch (ParseException e) {
        		Debug.logError(e, "Cannot parse date string: " + date, "");
        	}
    		transMap.put("transactionDate",transDate);
    		transMap.put("partyId",partyId);
    		transMap.put("orderId",orderId);
    		
    		transMap.put("paymentChannel",paymentChannel);
    		transMap.put("paymentMode",paymentMode);
    		transMap.put("mobileNumber",mobileNumber);
    		transMap.put("email",email);
    		transMap.put("transactionStatus","IN_PROCESS");  
    		BigDecimal tranAmount = new BigDecimal(amtStr);
    		transMap.put("amount",tranAmount);
    		      		
			GenericValue PaymentGatewayTrans = delegator.makeValue("PaymentGatewayTrans", transMap);
			delegator.createOrStore(PaymentGatewayTrans);
		}catch (Exception e) {
			Debug.logError(e, "Error While Creating paymentGateWayTrans ", module);
			return ServiceUtil.returnError("Error While paymentGateWayTrans : "+txnRefNo);
  	 	}
        
        if(paymentChannel.equalsIgnoreCase("airtel"))
        {   
        	String rosList[] = {"INT28","INT26","INT4","INT6","INT3","INT1","INT47","INT2"};
        	String midList[] = {"25672457","25672458","25672459","25672460","25672461","25672462","25672463","25672464"};
        	String saltList[] = {"fghjfgh456","fghjfgh457","fghjfgh458","fghjfgh459","fghjfgh460","fghjfgh461","fghjfgh462","fghjfgh463"};
        	
        	String userLoginParty = null;
            if (userLogin != null && userLogin.get("partyId") != null) {
        		userLoginParty = (String)userLogin.get("partyId");
            }
            Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        	List conditionList = FastList.newInstance();
            conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLoginParty));
            conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT" ));
            conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPANELLED_CUSTOMER" ));
    		conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN,UtilMisc.toList( "GROUP_ROLLUP","BRANCH_CUSTOMER")));
    		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
    				EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimeStamp)));
    		List branchList = FastList.newInstance();
    		try{
    			branchList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("partyIdFrom"), null, false);
        	}catch (GenericEntityException e) {
    			// TODO: handle exception
        		Debug.logError(e, module);
    		}
    		String roId = "";
    		if(UtilValidate.isNotEmpty(branchList)){
    			conditionList.clear();
    			List roList = FastList.newInstance();
    			try{
    				conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(branchList, "partyIdFrom", true)));
    		        conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION" ));
    		        conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT" ));
    				conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN,UtilMisc.toList( "GROUP_ROLLUP","BRANCH_CUSTOMER")));
    				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
    				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
    						EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimeStamp)));
    				roList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toList("partyIdFrom"), null, false);
    				if(UtilValidate.isNotEmpty(roList)){
    					roId = EntityUtil.getFirst(roList).getString("partyIdFrom");
    				}
    	    	}catch (GenericEntityException e) {
    				// TODO: handle exception
    	    		Debug.logError(e, module);
    			}
    		}
        	
        	
        	
        	
        	/*String mId = "25649255";
        	String salt = "34602fa0";*/
    		
    		String mId = midList[Arrays.asList(rosList).indexOf(roId)];
        	String salt = saltList[Arrays.asList(rosList).indexOf(roId)];
        	
        	String cur = "INR";        	
        	String url = initiatePaymentUrl(mId,txnRefNo,amtStr,date,salt,SucUrl,failUrl,cur,paymentMode,mobileNumber,email);
        	   
    		resultMap.put("msg","success");    	
    		resultMap.put("su", SucUrl);
    		resultMap.put("fu", failUrl);
    		resultMap.put("url",url);
    		    		
    		result.put("resultMap",resultMap);
    		return result;
        }
        
        if(paymentChannel.equalsIgnoreCase("atom"))
        {
            String transdate = getDateInFormat(txnDate, "dd/mm/yyyy HH:mm:ss");
    		resultMap.put("txnId", txnRefNo);
            resultMap.put("date", transdate);
            resultMap.put("mId", "21089");
    		resultMap.put("password", "NHDC@1234");
    		resultMap.put("url","https://payment.atomtech.in/mobilesdk/param");
    		    		
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
    
        return url;
    }
    
    public static String generateHash(String mid,String txnRefNo,String amt,String date,String service,String salt) throws  UnsupportedEncodingException{
        String text = mid+"#"+txnRefNo+"#"+amt+"#"+date+"#"+service+"#"+salt;
        
        Debug.log("text=================================="+text);
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
    
    public static String generateInquiryORRefundHash(String mid,String txnRefNo,String amt,String date,String service,String salt) throws  UnsupportedEncodingException{
        String text = mid+"#"+txnRefNo+"#"+amt+"#"+date+"#"+salt;
        
        Debug.log("text=================================="+text);
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
    
    public static Map<String, Object> transactionInquiryApi(DispatchContext dctx,Map<String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String transactionId = (String)context.get("transactionId");       
		Timestamp transactionDate = (Timestamp)context.get("transactionDate");
        String amount = (String)context.get("amount");
        Debug.log("context================="+context);
        Double amt = Double.valueOf(amount);
                
        DecimalFormat df = new DecimalFormat("###.##");                       
        String amtStr = df.format(amt);
        
        String feSessionId = "F1223ee323";
        String txnDate = getDateInFormat(transactionDate,"ddMMyyyyHHmmss");
        String request = "ECOMM_INQ";
        String mId = "25649255";
    	String salt = "34602fa0";
        String hash = "";
        
        try{
            hash=generateInquiryORRefundHash(mId,transactionId,amtStr,txnDate,"",salt);
        } catch (UnsupportedEncodingException e) {
        }
		
		String action = "inquiry";
        String baseURL = airtelBaseUrl+action+"?";                      
        
        Debug.log("txnDate================="+txnDate);
        Debug.log("hash===================="+hash);
        
        JSONObject postDataObject = new JSONObject();
        
        try{
        	
        	/*postDataObject.put("feSessionId",feSessionId);
    		postDataObject.put("txnRefNO", transactionId);
    		postDataObject.put("txnDate", txnDate);
    		postDataObject.put("request", request);
    		postDataObject.put("merchantId", mId);
    		postDataObject.put("hash", hash);
    		postDataObject.put("amount", amtStr);*/
        	
        	postDataObject.put("feSessionId","4323878555");
    		postDataObject.put("txnRefNO", "452345435");
    		postDataObject.put("txnDate", "28012017204540");
    		postDataObject.put("request", "ECOMM_INQ");
    		postDataObject.put("merchantId", "25649255");
    		postDataObject.put("hash", "a4e8f86a55b85fefa675bb85ac52e8ca8fc5113b5ad364c0c6cd3b1d60c3c83c8cffae9ecd18a2bc446168ec7505a0a1485c1f19b8bea993f33240272d695b63");
    		postDataObject.put("amount", "14");
    		
        	URL url = new URL(baseURL);
        	HttpsURLConnection  urlconnection = (HttpsURLConnection ) url.openConnection();
    		urlconnection.setRequestMethod("POST");
    		urlconnection.setRequestProperty("Content-Type", "application/json");
    		urlconnection.setRequestProperty("Accept", "application/json");
    		urlconnection.setDoOutput(true);
    		OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
    		out.write((postDataObject).toString());
    		out.close();
    		int status = ((HttpURLConnection) urlconnection).getResponseCode();
    		
    		BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			String decodedString = "";
			String retval = "";
			while ((decodedString = in.readLine()) != null) {
				retval += decodedString;
			}
			in.close();
            Debug.log("retval============="+retval);
            result = ServiceUtil.returnSuccess(retval);
            
        } catch (UnsupportedEncodingException e) {
            String errMsg = "UnsupportedEncodingException when sending payment ";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);			
		} catch (MalformedURLException e) {
            String errMsg = "MalformedURLException when sending payment ";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);            
		} catch (IOException e) {
            String errMsg = "IOException when doing payment ";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);            
		}
        

		return result;
    }
    
    public static Map<String, Object> transactionReversalApi(DispatchContext dctx,Map<String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String transactionId = (String)context.get("transactionId");       
		Timestamp transactionDate = (Timestamp)context.get("transactionDate");
		String amount = (String)context.get("amount");
        Debug.log("context================="+context);
        Double amt = Double.valueOf(amount);
                
        DecimalFormat df = new DecimalFormat("###.##");                       
        String amtStr = df.format(amt);
        
        String feSessionId = "F1223ee323";
        String txnDate = getDateInFormat(transactionDate,"ddMMyyyyHHmmss");
        String request = "ECOMM_REVERSAL";
        String mId = "25649255";
    	String salt = "34602fa0";
        String hash = "";
        
        GenericValue pgTrans = null;
	    try {
	    	pgTrans = delegator.findOne("PaymentGatewayTrans", UtilMisc.toMap("transactionId", transactionId), false);
	    } catch (GenericEntityException e) {
            Debug.logError(e, "error fetching transactionId :"+transactionId, module);
            return ServiceUtil.returnError("error fetching transactionId :"+transactionId);
        }
        
	    if (UtilValidate.isNotEmpty(pgTrans)) {
	    	
	    	String pgTransId = pgTrans.getString("pgTransId");
	    	try{
	            hash=generateInquiryORRefundHash(mId,pgTransId,amtStr,txnDate,"",salt);
	        } catch (UnsupportedEncodingException e) {
	        }
			
			String action = "reversal";
	        String baseURL = airtelBaseUrl+action+"?";                      
	        
	        Debug.log("txnDate================="+txnDate);
	        Debug.log("hash===================="+hash);
	        
	        JSONObject postDataObject = new JSONObject();
	        
	        try{
	        	
	        	/*postDataObject.put("feSessionId",feSessionId);
	    		postDataObject.put("txnId", pgTransId);
	    		postDataObject.put("txnDate", txnDate);
	    		postDataObject.put("request", request);
	    		postDataObject.put("merchantId", mId);
	    		postDataObject.put("hash", hash);
	    		postDataObject.put("amount", amtStr);*/
	        	
	        	postDataObject.put("feSessionId","4323878555");
	    		postDataObject.put("txnRefNO", "452345435");
	    		postDataObject.put("txnDate", "28012017204540");
	    		postDataObject.put("request", "ECOMM_REVERSAL");
	    		postDataObject.put("merchantId", "25649255");
	    		postDataObject.put("hash", "a4e8f86a55b85fefa675bb85ac52e8ca8fc5113b5ad364c0c6cd3b1d60c3c83c8cffae9ecd18a2bc446168ec7505a0a1485c1f19b8bea993f33240272d695b63");
	    		postDataObject.put("amount", "14");
	    		
	        	URL url = new URL(baseURL);
	        	HttpsURLConnection  urlconnection = (HttpsURLConnection ) url.openConnection();
	    		urlconnection.setRequestMethod("POST");
	    		urlconnection.setRequestProperty("Content-Type", "application/json");
	    		urlconnection.setRequestProperty("Accept", "application/json");
	    		urlconnection.setDoOutput(true);
	    		OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
	    		out.write((postDataObject).toString());
	    		out.close();
	    		int status = ((HttpURLConnection) urlconnection).getResponseCode();
	    		
	    		BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
				String decodedString = "";
				String retval = "";
				while ((decodedString = in.readLine()) != null) {
					retval += decodedString;
				}
				in.close();
	            Debug.log("retval=====refund========"+retval);
	            result = ServiceUtil.returnSuccess(retval);
	            
	        } catch (UnsupportedEncodingException e) {
	            String errMsg = "UnsupportedEncodingException when sending payment ";
	            Debug.logError(e, errMsg, module);
	            return ServiceUtil.returnError(errMsg);			
			} catch (MalformedURLException e) {
	            String errMsg = "MalformedURLException when sending payment ";
	            Debug.logError(e, errMsg, module);
	            return ServiceUtil.returnError(errMsg);            
			} catch (IOException e) {
	            String errMsg = "IOException when doing payment ";
	            Debug.logError(e, errMsg, module);
	            return ServiceUtil.returnError(errMsg);            
			}
	    }
        
                        
		return result;
    }
	
    
}
