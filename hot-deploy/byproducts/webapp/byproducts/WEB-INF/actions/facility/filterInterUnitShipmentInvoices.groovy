import org.apache.http.util.EntityUtils;
import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.*;

if(parameters.invoiceId){
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, parameters.invoiceId));
   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
   orders = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, UtilMisc.toSet("orderId"), null, null, false);
	if(orders){
		order = EntityUtil.getFirst(orders);
		parameters.orderId = order.orderId;
	}
	
}