import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.RowFilter.NotFilter;

import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();
leaveList=[];
leaveMap=[:];

leaveTypeIds=[];
	leaveTypeList=delegator.findList("EmplLeaveType",null,null,null,null,false);
	if(leaveTypeList){
		leaveTypeList.each{ leaveType ->
			leaveTypeIds.add(leaveType.get("leaveTypeId"));
		}
	}
if(parameters.partyIdTo && parameters.customTimePeriodId){
	List conList=[];
	conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,parameters.partyIdTo));
	conList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,parameters.customTimePeriodId));
	con=EntityCondition.makeCondition(conList,EntityOperator.AND);
	emplLeaveBalanceList = delegator.findList("EmplLeaveBalanceStatus", con ,null,null, null, false );
	if(emplLeaveBalanceList){
		leaveMap.put("partyId", parameters.partyIdTo);
		leaveMap.put("customTimePeriodId", parameters.customTimePeriodId);
		leaveTypeIds.each{ leaveType ->
			leaveMap[leaveType]=0;
			emplLeaveBalanceList.each{ emplLeave ->
				if(emplLeave.leaveTypeId==leaveType){
					BigDecimal	noOfDays=BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(emplLeave.adjustedDays)){
						noOfDays=(BigDecimal)emplLeave.adjustedDays;
					}
					leaveMap.put(leaveType, noOfDays);
					BigDecimal openingBalance=(BigDecimal)emplLeave.openingBalance;
					leaveMap.put(leaveType+"_openingBalance", openingBalance);
				}
				
			}
		}
		leaveList.add(leaveMap);
	}else{
		leaveMap.put("partyId", parameters.partyIdTo);
		leaveMap.put("customTimePeriodId", parameters.customTimePeriodId);
		leaveTypeIds.each{ leaveType ->
			leaveMap[leaveType]=0;
			leaveMap[leaveType+"_openingBalance"]=0;
			}
		leaveList.add(leaveMap);
	}
	context.leavesList=leaveList;
} else{
return;
}








