import java.io.ObjectOutputStream.DebugTraceInfoStack;

import org.ofbiz.base.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.network.LmsServices;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;

cratesList = [];
crateCanReportList = [];
List exprList = [];
sqlFromDate = null;
sqlThruDate = null;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
try {
	sqlFromDate = new java.sql.Date(formatter.parse(fromDate).getTime());
	sqlThruDate = new java.sql.Date(formatter.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError("Error in Date Conversion: " + e.getMessage());
}

expr = EntityCondition.makeCondition(EntityCondition.makeCondition("supplyDate", EntityOperator.GREATER_THAN_EQUAL_TO,sqlFromDate),
	EntityOperator.AND,
	EntityCondition.makeCondition("supplyDate", EntityOperator.LESS_THAN_EQUAL_TO, sqlThruDate));
cratesList =  delegator.findList("CratesCansAccnt", expr, null, null, null, false);

crateCanTotalMap = [:];
totCratesSent = 0;
totCratesReceived = 0;
totcratesShort = 0;

totCanSent = 0;
totCansReceived = 0;
totcratesShort = 0;

for (int i=0; i < cratesList.size(); i++) {
	crateCanItem = cratesList.get(i);
	cratesCansMap = [:];
		cratesCansMap["routeId"] = crateCanItem.routeId;
		cratesCansMap["supplyDate"] = crateCanItem.supplyDate;
		//crates
		cratesCansMap["cratesSent"] = crateCanItem.cratesSent;
		cratesCansMap["cratesReceived"] = crateCanItem.cratesReceived;
		cratesCansMap["cratesShort"] = (cratesCansMap["cratesSent"])-(cratesCansMap["cratesReceived"]);
		//cans
		cratesCansMap["cansSent"] = crateCanItem.cansSent;
		cratesCansMap["cansReceived"] = crateCanItem.cansReceived;
		cratesCansMap["cansShort"] = (cratesCansMap["cansSent"])-(cratesCansMap["cansReceived"]);
		
		crateCanReportList.add(cratesCansMap);
		//crates Totals	
		totCratesSent += crateCanItem.cratesSent;
		totCratesReceived += crateCanItem.cratesReceived;
		totcratesShort += cratesCansMap["cratesShort"];
		//cans Totals
		totCanSent += crateCanItem.cansSent;
		totCansReceived += crateCanItem.cansReceived;
		totcratesShort += cratesCansMap["cansShort"];
}

crateCanTotalMap["supplyDate"] = "Total :";
//crates Totals	
crateCanTotalMap["cratesSent"] = totCratesSent;
crateCanTotalMap["cratesReceived"] = totCratesReceived;
crateCanTotalMap["cratesShort"] = totcratesShort;
//cans Totals
crateCanTotalMap["cansSent"] = totCanSent;
crateCanTotalMap["cansReceived"] = totCansReceived;
crateCanTotalMap["cansShort"] = totcratesShort;
context.put("crateCanTotalMap", crateCanTotalMap);
crateCanReportList.add(crateCanTotalMap);
context.crateCanReportList = crateCanReportList;
//Debug.logInfo("crateCanReportList=========================>"+crateCanReportList,"");

