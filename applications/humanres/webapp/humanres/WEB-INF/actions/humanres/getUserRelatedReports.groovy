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
import java.sql.*;
import java.util.Calendar;
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
dctx = dispatcher.getDispatchContext();

tenantId = delegator.getDelegatorTenantId();

	List conditionList=[];
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, "_NA_"));
	conditionList.add(EntityCondition.makeCondition("moduleId", EntityOperator.EQUALS, "HUMANRES"));
	conditionList.add(EntityCondition.makeCondition("tenantId", EntityOperator.EQUALS , tenantId));
	facilityReportCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	facilityReportList = delegator.findList("FacilityWiseReportConfig", facilityReportCondition, null, null, null, false);
	reportDetailsMap=[:];
	if(UtilValidate.isNotEmpty(facilityReportList)){
		
		facilityReportList.each{ facilityReport ->
			if(UtilValidate.isNotEmpty(facilityReport)){
				ReportId = facilityReport.ReportId;
				showInScreen = facilityReport.showInScreen;
				if(UtilValidate.isEmpty(showInScreen)){
					showInScreen = "Y";
				}
				reportDetailsMap.put(ReportId,showInScreen);
			}
		}
	}
context.put("reportDetailsMap",reportDetailsMap);
