import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementServices;
fromDateTime=null;
thruDateTime=null;
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
}

dctx = dispatcher.getDispatchContext();
totalAmt =0;
if(UtilValidate.isNotEmpty(parameters.centerId)){
	agentDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDateTime , thruDate: thruDateTime , facilityId: parameters.centerId]);
	Iterator agentDayTotalsItr =agentDayTotals.entrySet().iterator();
	while (agentDayTotalsItr.hasNext()) {
		Map.Entry agentEntry = agentDayTotalsItr.next();
		Map agentValuesMap = (Map)agentEntry.getValue();
		Map agentWiseTotalValues = ((Map)((Map)((Map)agentValuesMap.get("dayTotals")).get("TOT")).get("TOT")).get("TOT");
		totalAmt = agentWiseTotalValues.get("price")+ agentWiseTotalValues.get("sPrice");		
	}
}
context.putAt("totalAmt", totalAmt);