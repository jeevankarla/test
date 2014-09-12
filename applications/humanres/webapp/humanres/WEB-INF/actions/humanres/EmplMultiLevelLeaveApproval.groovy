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
		context.emplLeaveApplId = emplLeaveDetails.emplLeaveApplId;
		if(UtilValidate.isNotEmpty(emplLeaveDetails.partyId)){
			partyName = PartyHelper.getPartyName(delegator, emplLeaveDetails.partyId, false);
		}
		context.partyName = partyName;
		context.partyId = emplLeaveDetails.partyId;
		context.approverPartyId = emplLeaveDetails.approverPartyId;
		context.appliedBy= emplLeaveDetails.appliedBy;
		context.leaveTypeId = emplLeaveDetails.leaveTypeId;
		context.emplLeaveReasonTypeId = emplLeaveDetails.emplLeaveReasonTypeId;
		context.fromDate = UtilDateTime.toDateString(emplLeaveDetails.fromDate,"dd-MM-yyyy");
		context.thruDate = UtilDateTime.toDateString(emplLeaveDetails.thruDate,"dd-MM-yyyy");
		context.effectedCreditDays = emplLeaveDetails.effectedCreditDays;
		context.lossOfPayDays = emplLeaveDetails.lossOfPayDays;
		context.description = emplLeaveDetails.description;
		context.documentsProduced = emplLeaveDetails.documentsProduced;
		context.comment = emplLeaveDetails.comment;
		context.leaveStatus = emplLeaveDetails.leaveStatus;
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

validStatusChangeList = EmplLeaveService.getEmployLeaveValidStatusChange(dctx,UtilMisc.toMap("leaveStatus",leaveStatus,"leaveTypeId",leaveTypeId ,"userLogin",userLogin)).get("validStatusChangeList");
context.putAt("validStatusChangeList", validStatusChangeList);

