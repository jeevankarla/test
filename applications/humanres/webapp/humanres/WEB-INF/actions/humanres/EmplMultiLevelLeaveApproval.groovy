import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import java.util.*;
import java.lang.*;
import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityFindOptions;
import net.sf.json.JSONArray;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import in.vasista.vbiz.humanres.EmplLeaveService;
import org.ofbiz.party.party.PartyHelper;


dctx = dispatcher.getDispatchContext();
leaveTypeId = parameters.leaveTypeId;
leaveStatus = parameters.leaveStatus;
emplLeaveApplId = parameters.emplLeaveApplId;

partyId = "";
approverPartyId = "";
appliedBy = "";
emplLeaveReasonTypeId="";
fromDate = "";
thruDate= "";
effectedCreditDays = "";
lossOfPayDays = "";
description = "";
documentsProduced = "";
comment = "";
partyName = "";
if(UtilValidate.isNotEmpty(emplLeaveApplId)){
	emplLeaveDetails = delegator.findOne("EmplLeave",UtilMisc.toMap("emplLeaveApplId", emplLeaveApplId), false);
	if(UtilValidate.isNotEmpty(emplLeaveDetails)){
		
		leaveTypeId = emplLeaveDetails.leaveTypeId;
		leaveStatus = emplLeaveDetails.leaveStatus;
		
		//context.emplLeaveApplId = emplLeaveDetails.emplLeaveApplId;
		request.setAttribute("emplLeaveApplId", emplLeaveApplId);
		if(UtilValidate.isNotEmpty(emplLeaveDetails.partyId)){
			partyName = PartyHelper.getPartyName(delegator, emplLeaveDetails.partyId, false);
		}
		//context.partyName = partyName;
		request.setAttribute("partyName", partyName);
		//context.partyId = emplLeaveDetails.partyId;
		request.setAttribute("partyId", emplLeaveDetails.partyId);
		//context.approverPartyId = emplLeaveDetails.approverPartyId;
		request.setAttribute("approverPartyId", emplLeaveDetails.approverPartyId);
		//context.appliedBy= emplLeaveDetails.appliedBy;
		request.setAttribute("appliedBy", emplLeaveDetails.appliedBy);
		//context.leaveTypeId = emplLeaveDetails.leaveTypeId;
		request.setAttribute("leaveTypeId", emplLeaveDetails.leaveTypeId);
		//context.emplLeaveReasonTypeId = emplLeaveDetails.emplLeaveReasonTypeId;
		request.setAttribute("emplLeaveReasonTypeId", emplLeaveDetails.emplLeaveReasonTypeId);
		//context.fromDate = UtilDateTime.toDateString(emplLeaveDetails.fromDate,"dd-MM-yyyy");
		request.setAttribute("fromDate", UtilDateTime.toDateString(emplLeaveDetails.fromDate,"dd-MM-yyyy"));
		//context.thruDate = UtilDateTime.toDateString(emplLeaveDetails.thruDate,"dd-MM-yyyy");
		request.setAttribute("thruDate", UtilDateTime.toDateString(emplLeaveDetails.thruDate,"dd-MM-yyyy"));
		//context.effectedCreditDays = emplLeaveDetails.effectedCreditDays;
		request.setAttribute("effectedCreditDays", emplLeaveDetails.effectedCreditDays);
		//context.lossOfPayDays = emplLeaveDetails.lossOfPayDays;
		request.setAttribute("lossOfPayDays", emplLeaveDetails.lossOfPayDays);
		//context.description = emplLeaveDetails.description;
		request.setAttribute("description", emplLeaveDetails.description);
		//context.documentsProduced = emplLeaveDetails.documentsProduced;
		request.setAttribute("documentsProduced", emplLeaveDetails.documentsProduced);
		//context.comment = emplLeaveDetails.comment;
		request.setAttribute("comment", emplLeaveDetails.comment);
		//context.leaveStatus = emplLeaveDetails.leaveStatus;
		request.setAttribute("leaveStatus", emplLeaveDetails.leaveStatus);
	}
}


approveLevels = "";
if(UtilValidate.isNotEmpty(leaveTypeId)){
	emplLeaveTypeDetails = delegator.findOne("EmplLeaveType",[leaveTypeId : leaveTypeId] , false);
	if(UtilValidate.isNotEmpty(emplLeaveTypeDetails)){
		approveLevels = emplLeaveTypeDetails.approveLevels;
	}
}
if(UtilValidate.isEmpty(approveLevels)){
	approveLevels = "01";
}
request.setAttribute("approveLevels", approveLevels);
validStatusChangeList = EmplLeaveService.getEmployLeaveValidStatusChange(dctx,UtilMisc.toMap("leaveStatus",leaveStatus,"leaveTypeId",leaveTypeId ,"userLogin",userLogin)).get("validStatusChangeList");
//context.putAt("validStatusChangeList", validStatusChangeList);
request.setAttribute("validStatusChangeList", validStatusChangeList);
if(UtilValidate.isNotEmpty(emplLeaveApplId)){
	dateList = [];
	if(UtilValidate.isNotEmpty(emplLeaveApplId)){
		emplDailyAttendanceDetailList = delegator.findList("EmplDailyAttendanceDetail",EntityCondition.makeCondition("emplLeaveApplId", EntityOperator.EQUALS, emplLeaveApplId) , null, null, null, false);
		if(UtilValidate.isNotEmpty(emplDailyAttendanceDetailList)){
			dateList = EntityUtil.getFieldListFromEntityList(emplDailyAttendanceDetailList,"date",true);
			if(UtilValidate.isNotEmpty(dateList)){
				request.setAttribute("dateList", dateList);
			}
		}
	}
}



