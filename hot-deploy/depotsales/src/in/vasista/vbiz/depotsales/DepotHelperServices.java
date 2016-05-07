package in.vasista.vbiz.depotsales;
import java.text.DateFormat;

import in.vasista.vbiz.depotsales.DepotPurchaseServices;

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
import org.json.JSONArray;
import org.json.JSONObject;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

public class DepotHelperServices{

   public static final String module = DepotHelperServices.class.getName();
   private static int decimals;
   private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    public static Map<String, Object> getRoBranchList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();   
        Map<String, Object> result = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
       
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String productStoreId = (String) context.get("productStoreId");
	    	List societyList = FastList.newInstance();

        // If productStoreId is not empty, fetch only courses related to the productStore
        List partyList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(productStoreId)){
        	List condList =FastList.newInstance();
   	    	condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, productStoreId));
   	    	
   	    	/*condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimeStamp));
   	    	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimeStamp)));*/
   	    	EntityCondition prodStrFacCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
   	    	try{
   	    		partyList = delegator.findList("PartyRelationship", prodStrFacCondition, null, null, null, false);
   	    	}catch (GenericEntityException e) {
   				// TODO: handle exception
   	    		Debug.logError(e, module);
   			}
   	    }
        result.put("partyList", partyList);
        return result;
    }

    
    
}