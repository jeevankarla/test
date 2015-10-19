import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import java.sql.*;
import org.ofbiz.base.util.UtilDateTime;
import java.util.Calendar;
import java.math.BigDecimal;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;

JSONArray headItemsJson = new JSONArray();
JSONObject newObj = new JSONObject();
statusId = "";
if (UtilValidate.isNotEmpty(parameters.periodBillingId)) {
	GenericValue periodBilling = delegator.findOne("PeriodBilling", [periodBillingId : parameters.periodBillingId], false);
	if(UtilValidate.isNotEmpty(periodBilling)){
		statusId = periodBilling.get("statusId");
		newObj.put("id",parameters.periodBillingId+"["+parameters.periodBillingId+"]");
		newObj.put("statusId",statusId);
		headItemsJson.add(newObj);
	}
}
context.put("statusId", statusId);
request.setAttribute("headItemsJson", headItemsJson);
request.setAttribute("statusId", statusId);
