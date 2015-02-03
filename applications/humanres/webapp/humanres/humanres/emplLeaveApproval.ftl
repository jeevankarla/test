<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>
<style type="text/css">
.ui-tooltip1, .qtip{
	position: absolute;
	left: -10000em;
	top: -10000em;
 
	max-width: 550px; /* Change this? */
	min-width: 450px; /* ...and this! */
}
</style>
<script type="application/javascript">
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	var emplLeaveApplIdParam; 
	var partyId;
	var partyName;
	var approverPartyId;
	var appliedBy;
	var leaveTypeId;
	var emplLeaveReasonTypeId;
	var fromDate;
	var thruDate;
	var effectedCreditDays;
	var lossOfPayDays;
	var documentsProduced;
	var description = '';
	var leaveStatus;
	var dateList = '';
	var validStatusChangeList;
	
	var optionList;
	
	
	function dialogue(content, title) {
		/* 
		 * Since the dialogue isn't really a tooltip as such, we'll use a dummy
		 * out-of-DOM element as our target instead of an actual element like document.body
		 */
		$('<div />').qtip(
		
		{
			content: {
				text: content,
				title: title
			},
			position: {
				my: 'center', at: 'center', // Center it...
				target: $(window) // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: true, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
				  //getEmplLeaveApprovalDetails(emplLeaveApplIdParam);
				  approverPartyIdChange();
				  //picker();
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
		$("jqueryselector").qtip({
   			content: 'I have rounded corners... I hear they\'re all the rage at the moment!',
   		style: { 
      		border: {
        	 width: 3,
         	radius: 15,
         	color: '#6699CC'
      	},
      	width: 200
		}  }); 
	}
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function getEmplLeaveApprovalDetails(emplLeaveApplIdParam){
		$.ajax({
             type: "POST",
             url: 'getEmplLeaveApprovalDetails',
             data: {emplLeaveApplId : emplLeaveApplIdParam},
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	    partyId = result["partyId"];
            	    partyName = result["partyName"];
            	    approverPartyId = result["approverPartyId"];
            	    appliedBy = result["appliedBy"];
            	    leaveTypeId = result["leaveTypeId"];
            	    emplLeaveReasonTypeId = result["emplLeaveReasonTypeId"];
            	    fromDate = result["fromDate"];
            	    thruDate = result["thruDate"];
            	    if(result["description"]){
            	    	description = result["description"];
            	    }
            	    documentsProduced = result["documentsProduced"];
            	    comment = result["comment"];
            	    leaveStatus = result["leaveStatus"];
            	    if(result["dateList"]){
            	    	dateList = result["dateList"];
            	    }
            	    validStatusChangeList = result["validStatusChangeList"];
            	    
					var list= validStatusChangeList;
					if (list) {		       				        	
			        	for(var i=0 ; i<list.length ; i++){
							var innerList=list[i];	              			             
			                optionList += "<option value = '" + innerList['statusIdTo'] + "' >"+ innerList['transitionName'] +"</option>";          			
			      		}
			      	}		
               }
             } 
        });
	}
	
	jQuery(document).ready(function() {
		jQuery('#levelApproverPartyId').parent().parent().hide();
		approverPartyIdChange();
	});
	
	function approverPartyIdChange(){
		var leaveStatus = jQuery('#leaveStatus').val();
		if( leaveStatus == 'LEAVE_APPROVE_LEVEL1'){
			jQuery('#levelApproverPartyId').parent().parent().show();
			jQuery('#levelApproverPartyId').attr("required",true);
		}else if (leaveStatus == 'LEAVE_APPROVE_LEVEL2'){
			jQuery('#levelApproverPartyId').parent().parent().show();
			jQuery('#levelApproverPartyId').attr("required",true);
		}else if (leaveStatus == 'LEAVE_APPROVED'){
			jQuery('#levelApproverPartyId').parent().parent().hide();
		}else if (leaveStatus == 'LEAVE_REJECTED'){
			jQuery('#levelApproverPartyId').parent().parent().hide();
		}
		
		  var employees = ${StringUtil.wrapString(employeesJSON!'[]')};
		  $.each(employees, function(key, val){
		    $("#levelApproverPartyId").append('<option value="' + val.employeeId + '">' + val.name + " [" + val.employeeId + "]" + '</option>');
		  });
		  $("#levelApproverPartyId").flexselect({
		  								preSelection: false,
		  								allowClear: false,
		  								allowMismatch:false,
		  								allowEmpty:true,
		  								allowNewElements:false,
		  								placeholder: "select",
		  								hideDropdownOnEmptyInput: false});	
		 }
	
function leaveApprovalAjax() {
	  	var levelApproverPartyId = jQuery('#levelApproverPartyId').val();
	  	var emplLeaveApplId = jQuery('#emplLeaveApplId').val();
    	var leaveStatus = jQuery('#leaveStatus').val();
    	var approverPartyId = jQuery('#approverPartyId').val();
    	var thruDate = jQuery('#thruDate').val();
    	var leaveTypeId = jQuery('#leaveTypeId').val();
    	
    	if((leaveStatus != "LEAVE_APPROVED") && (leaveStatus!="LEAVE_REJECTED")){
    		if(!levelApproverPartyId){
    			alert("Please select next Level PartyId..!");
    			return false;
    		}
    	}
	  	
    	var data = "emplLeaveApplId="+emplLeaveApplId+"&leaveStatus="+leaveStatus+"&approverPartyId="+approverPartyId+"&thruDate="+thruDate+"&leaveTypeId="+leaveTypeId;
    	if(levelApproverPartyId){
    		data = data+"&levelApproverPartyId="+levelApproverPartyId;
    	}
    	$('div#updateEntryMsg')
    		  .html('<img src="/images/ajax-loader64.gif">'); 
    	$.ajax({
             type: "POST",
             url: 'updateEmplLeaveStatusAjax',
             data: data,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{       
            	    $("div#updateEntryMsg").fadeIn();               	         	   
            	    $('div#updateEntryMsg').html(); 
            	    $('div#updateEntryMsg').removeClass("errorMessage");           	 
            	    $('div#updateEntryMsg').addClass("messageStr");
            	    $('div#updateEntryMsg').html('<label>'+leaveStatus+' Successfully.....!</label>'); 
            	    $('div#updateEntryMsg').delay(5000).fadeOut('slow');
            	    alert("Leave Status Updated Successfully...!");
            	    $(updateEmplLeaveStatus).qtip('hide');
            	    $("#FindLeaveApprovals").submit();
               }
               return false;
             } 
        });
        return false;
}
	
function picker(){
	jQuery("#thruDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		numberOfMonths: 1,
		onSelect: function(selectedDate) {
			jQuery("#thruDate").datepicker('setDate', selectedDate);
		}
	});
}
	
function cancelForm(){                 
     return false;
}
 	
	function showLeaveApprovalQTip(emplLeaveApplId) {
		emplLeaveApplIdParam = emplLeaveApplId;
		getEmplLeaveApprovalDetails(emplLeaveApplIdParam);
		
		var message = "";
		message += "<form id = 'updateEmplLeaveStatus' name = 'updateEmplLeaveStatus' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10 width='100%'>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Party :</td><td align='left' width='50%'><input class='h4' type='text' readonly id='partyName' name='partyName' value='"+partyId+""+"("+""+partyName+""+")"+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>Approver PartyId:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='approverPartyId' name='approverPartyId' value='"+approverPartyId+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>Applied By:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='appliedBy' name='appliedBy' value='"+appliedBy+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>Leave Type:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='leaveTypeId' name='leaveTypeId' value='"+leaveTypeId+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>Leave Reason Type:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='emplLeaveReasonTypeId' name='emplLeaveReasonTypeId' value='"+emplLeaveReasonTypeId+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>From Date:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='fromDate' name='fromDate' value='"+fromDate+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>Thru Date:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='thruDate' name='thruDate' value='"+thruDate+"'<#if (leaveStatus?has_content && leaveStatus == "LEAVE_APPROVED") && security.hasEntityPermission("APPROVED_LEAVE", "_UPDATE", session)> onmouseover='picker()'</#if>/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>GH/SS Days:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='dateList' name='dateList' value='"+dateList+"'/></td></tr>"+
					"<tr class='h3'><td align='left' class='h3' width='50%'>Description:</td><td align='left' width='50%'><input class='h4' type='text' readonly id='description' name='description' value='"+description+"'/><input class='h4' type='hidden' readonly id='emplLeaveApplId' name='emplLeaveApplId' value='"+emplLeaveApplId+"'/><input class='h4' type='hidden' readonly id='partyId' name='partyId' value='"+partyId+"'/></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='50%'>Next Leave Status :</td><td align='left' width='50%'><select name='leaveStatus' onchange = 'javascript:approverPartyIdChange();' id='leaveStatus'  value='"+leaveStatus+"' class='h4'>"+optionList+   
					"</select></td></tr>";
					
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Level Approver Party:</td><td align='left' width='50%'><select id='levelApproverPartyId' name='levelApproverPartyId' class='flexselect'></select></tr>";			
					
		message +=	"<tr class='h3'><td align='left' class='h3' width='50%'>Documents Produced :</td><td align='left' width='50%'><select name='documentsProduced' id='documentsProduced' class='h4'><option value='N'>N</option><option value='Y'>Y</option></select></td></tr>";	
		
		message +=  "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Approve' class='smallSubmit' onclick='return leaveApprovalAjax();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h2' width='50%'></td><td align='left' class='h2' width='50%'><font color='blue'><div name ='updateEntryMsg' id='updateEntryMsg' width='50%'></div></font></td></tr>";
		
		title = "<h1><center>Employee Leave Approval<center></h1><br />";
		message += "</table></form>";
		Alert(message, title);
	};
</script>
