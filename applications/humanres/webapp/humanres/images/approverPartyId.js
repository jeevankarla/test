jQuery(document).ready(function() {
	jQuery('[name=levelApproverPartyId]').parent().parent().parent().hide();
});
function approverPartyIdChange(){
	var leaveStatus = jQuery('[name=leaveStatus]').val();
	if( leaveStatus == 'LEAVE_APPROVE_LEVEL1'){
		jQuery('[name=levelApproverPartyId]').parent().parent().parent().show();
	}else if (leaveStatus == 'LEAVE_APPROVE_LEVEL2'){
		jQuery('[name=levelApproverPartyId]').parent().parent().parent().show();
	}else if (leaveStatus == 'LEAVE_APPROVED'){
		jQuery('[name=levelApproverPartyId]').parent().parent().parent().hide();
	}else if (leaveStatus == 'LEAVE_REJECTED'){
		jQuery('[name=levelApproverPartyId]').parent().parent().parent().hide();
	}
}