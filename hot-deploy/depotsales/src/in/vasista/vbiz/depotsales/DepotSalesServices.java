package in.vasista.vbiz.depotsales;

import java.math.BigDecimal;
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

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
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


public class DepotSalesServices{

   public static final String module = DepotSalesServices.class.getName();

   public static Map<String, Object> approveDepotOrder(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String salesChannelEnumId = (String) context.get("salesChannelEnumId");
		String partyId=(String) context.get("partyId");
		String orderId = (String) context.get("orderId");
		//Debug.log("====Before Approving Depot Order==============partyId===>"+partyId);
		try{
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
			BigDecimal orderTotal = orderHeader.getBigDecimal("grandTotal");
			Timestamp obDate=UtilDateTime.nowTimestamp();
			if(UtilValidate.isNotEmpty(orderHeader.getTimestamp("estimatedDeliveryDate"))){
				obDate=	orderHeader.getTimestamp("estimatedDeliveryDate");
				obDate=UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(obDate, 1));
			}
		
			BigDecimal arPartyOB  =BigDecimal.ZERO;
			Map arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , UtilMisc.toMap("userLogin", userLogin, "tillDate",obDate, "partyId",partyId)));
			if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
				arPartyOB=(BigDecimal)arOpeningBalanceRes.get("openingBalance");
				 Debug.log("============arPartyOB===>"+arPartyOB+"=============Before Approve====>");
				 if (arPartyOB.compareTo(BigDecimal.ZERO) < 0) {
					 arPartyOB=arPartyOB.negate();
				 }
			 }
			 if (arPartyOB.compareTo(orderTotal) < 0) {
				 Debug.log("============arPartyOB is Lessthan than the OrderTotal====>");
				 Debug.logError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId, module);
				 return ServiceUtil.returnError("Available Balance:"+arPartyOB+" Less Than The Order Amount:"+orderTotal+" For Party:"+ partyId);
			 }
		
            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
	   }catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
       result.put("salesChannelEnumId", salesChannelEnumId);
       return result;
	}
}