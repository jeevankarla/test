import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;


dctx = dispatcher.getDispatchContext();

context.partyName = parameters.partyName;


conditionList=[];
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, "WS611351"));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemList = delegator.findList("OrderItem", condition, null, null, null, false);

			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER"));
			
						condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderRoleList = delegator.findList("OrderRole", condition,  UtilMisc.toSet("partyId"), null, null, false);

			supplierPartyId = "";
			if(UtilValidate.isNotEmpty(orderRoleList)){
			supplierPartyId = orderRoleList[0].get("partyId");
			}
			
			context.supplierPartyId = supplierPartyId;
			
	context.OrderItemList = OrderItemList;		
	
	contextMap = UtilMisc.toMap("translateList", OrderItemList);
	dayWiseEntriesLidast = (ByProductNetworkServices.icu4JTrans(dctx, contextMap)).getAt("translateList");
	
	Debug.log("dayWiseEntriesLidast========="+dayWiseEntriesLidast);
	
	
	context.dayWiseEntriesLidast = dayWiseEntriesLidast;
	
	
