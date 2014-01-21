import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;

List facilityList1 = [];
List facilityList2 = [];
List facilityList = [];
facilityId = null;

facilityId = parameters.facilityId;

facilityList1 = delegator.findList("FacilityParty", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId), null, null, null, false);
facilityList2 = delegator.findList("FacilityParty", EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS , facilityId), null, null, null, false);

facilityList.addAll(facilityList1);
facilityList.addAll(facilityList2);

context.put("facilityParties", facilityList);







