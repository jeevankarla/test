import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;


dctx = dispatcher.getDispatchContext();
customTimePeriodId = parameters.customTimePeriodId;
finalMap = context.get("finalMap");
if(UtilValidate.isNotEmpty(finalMap)){
	finalMap.each { route->
		if(UtilValidate.isNotEmpty(route)){
			routeId = route.getKey();
			routeAmount  = (BigDecimal)route.getValue().get("routeAmount").setScale(0,BigDecimal.ROUND_HALF_UP);
			totalFine  = (BigDecimal)route.getValue().get("totalFine").setScale(0,BigDecimal.ROUND_HALF_UP);
			netAmount  = (BigDecimal)route.getValue().get("netAmount").setScale(0,BigDecimal.ROUND_HALF_UP);
			smsService = dispatcher.runSync("sendTransporterMarginSMS", [facilityId: routeId,routeAmount: routeAmount,totalFine: totalFine, netAmount: netAmount, customTimePeriodId: customTimePeriodId, userLogin: userLogin]);
		}
	}
}
result = ServiceUtil.returnSuccess("SMS Successfully Sent...!");



