import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import net.sf.json.JSONObject;

dctx = dispatcher.getDispatchContext();
List procInOutTypes = delegator.findList("EnumerationType",EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"PROC_INOUT_TYPE"),null,null,null,false);

List inoutTypes = EntityUtil.getFieldListFromEntityList(procInOutTypes, "enumTypeId", true);
context.put("inoutTypes", inoutTypes);
List outputTypes = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId",EntityOperator.IN,inoutTypes),null,null,null,false);
context.put("outputTypes", outputTypes);
