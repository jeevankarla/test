
function showErrorAlert(errBoxTitle, errMessage) {
    var errMsgBox = jQuery("#contentarea").after(jQuery("<div id='errorAlertBox'>" + errMessage + "</div>"));

    if (errMsgBox.length) {
        errMsgBox.dialog({
            modal: true,
            title: errBoxTitle,
            buttons: {
                Ok: function() {
                    errMsgBox.remove();
                    jQuery( this ).dialog( "close" );
                }
            }
        });
    }	
}
function smsSend() {
	if (jQuery('[name=partyIdTo]').val() == '' && jQuery('[name=contactListId]').val() == '') {
		var errBoxTitle = "Incomplete Data";
		var errMessage = "Please fill in either a 'To Party' or a 'Contact List'";	
		showErrorAlert(errBoxTitle, errMessage);
		return;
	}	
	document.EditSms.form.value='list';
	document.EditSms.statusId.value='COM_IN_PROGRESS';
	document.EditSms.submit();
}

function emailSend() {
	if (jQuery('[name=partyIdTo]').val() == '' && jQuery('[name=contactListId]').val() == '') {
		var errBoxTitle = "Incomplete Data";
		var errMessage = "Please fill in either a 'To Party' or a 'Contact List'";	
		showErrorAlert(errBoxTitle, errMessage);
		return;
	}	
	document.EditEmail.form.value='list';
	document.EditEmail.submit();
}

function noteSend() {
	if (jQuery('[name=partyIdTo]').val() == '' && jQuery('[name=contactListId]').val() == '') {
		var errBoxTitle = "Incomplete Data";
		var errMessage = "Please fill in either a 'To Party' or a 'Contact List'";	
		showErrorAlert(errBoxTitle, errMessage);
		return;
	}	
	document.EditInternalNote.form.value='list';
	document.EditInternalNote.submit();
}

function uploadContent() {
	if (jQuery('[name=communicationEventTypeId]').val() == 'EMAIL_COMMUNICATION') {
		document.uploadContent.datetimeStarted.value=document.EditEmail.datetimeStarted.value;
		document.uploadContent.partyIdTo.value=document.EditEmail.partyIdTo.value;
		document.uploadContent.contactListId.value=document.EditEmail.contactListId.value;		
		document.uploadContent.subject.value=document.EditEmail.subject.value;
		document.uploadContent.content.value=document.EditEmail.content.value;
	}
	if (jQuery('[name=communicationEventTypeId]').val() == 'COMMENT_NOTE') {
		document.uploadContent.datetimeStarted.value=document.EditInternalNote.datetimeStarted.value;
		document.uploadContent.partyIdTo.value=document.EditInternalNote.partyIdTo.value;
		document.uploadContent.contactListId.value=document.EditInternalNote.contactListId.value;				
		document.uploadContent.subject.value=document.EditInternalNote.subject.value;
		document.uploadContent.content.value=document.EditInternalNote.content.value;
	}	
	document.uploadContent.submit();
}

function createCommunicationEventRole() {
	if (jQuery('[name=communicationEventTypeId]').val() == 'EMAIL_COMMUNICATION') {
		document.AddEventRole.communicationEventId.value=document.EditEmail.communicationEventId.value;
		document.AddEventRole.datetimeStarted.value=document.EditEmail.datetimeStarted.value;
		document.AddEventRole.partyIdTo.value=document.EditEmail.partyIdTo.value;
		document.AddEventRole.contactListId.value=document.EditEmail.contactListId.value;		
		document.AddEventRole.subject.value=document.EditEmail.subject.value;
		document.AddEventRole.content.value=document.EditEmail.content.value;
	}
	if (jQuery('[name=communicationEventTypeId]').val() == 'COMMENT_NOTE') {
		document.AddEventRole.communicationEventId.value=document.EditInternalNote.communicationEventId.value;
		document.AddEventRole.datetimeStarted.value=document.EditInternalNote.datetimeStarted.value;
		document.AddEventRole.partyIdTo.value=document.EditInternalNote.partyIdTo.value;
		document.AddEventRole.contactListId.value=document.EditInternalNote.contactListId.value;				
		document.AddEventRole.subject.value=document.EditInternalNote.subject.value;
		document.AddEventRole.content.value=document.EditInternalNote.content.value;
	}	
	if (jQuery('[name=communicationEventTypeId]').val() == 'SMS_COMMUNICATION') {
		document.AddEventRole.communicationEventId.value=document.EditSms.communicationEventId.value;
		document.AddEventRole.datetimeStarted.value=document.EditSms.datetimeStarted.value;
		document.AddEventRole.partyIdTo.value=document.EditSms.partyIdTo.value;
		document.AddEventRole.contactListId.value=document.EditSms.contactListId.value;				
		document.AddEventRole.content.value=document.EditSms.content.value;
	}	
	document.AddEventRole.submit();
}
