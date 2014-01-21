
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
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
					populateAccountDetails();
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	function datepick()
	{		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});
		$( "#paymentDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	var accountInfo;
	var ownerParty;
	var dueAmount;
	function showInstitutionPaymentEntry(ownerPartyId, duesPay) {
		var message = "";
		ownerParty = ownerPartyId;
		dueAmount = duesPay;
		message += "<html><head></head><body><form action='createPostPayment' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Party Code :</td><td align='left' width='60%'><input class='h4' type='label' id='facilityId' name='facilityId' value='${facilityId?if_exists}' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Method Type :</td><td align='left' width='60%'><select name='paymentMethodTypeId' id='paymentMethodTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						"<#list paymentMethodList as eachMethodType><option value='${eachMethodType.paymentMethodTypeId}' >${eachMethodType.paymentMethodTypeId}</option></#list>"+            
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Issue Authority/ Bank :</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Bank Branch:</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthorityBranch' name='issuingAuthorityBranch' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Date:</td><td align='left' width='60%'><input class='h4' type='text' id='effectiveDate' name='effectiveDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Ref. No. :</td><td align='left' width='60%'><input class='h4' type='text' id='paymentRefNum' name='paymentRefNum' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount' name='amount'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='paymentTypeId' name='paymentTypeId' value='SALES_PAYIN'/></td></tr>"+
                		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='organizationPartyId' name='organizationPartyId' value='Company'/></td></tr>"+
                		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='partyIdFrom' name='partyIdFrom' /></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='paymentPurposeType' name='paymentPurposeType' value='BYPROD_PAYMENT'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : ${facilityId?if_exists} ";
		Alert(message, title);
		$.ajax({
             type: "POST",
             url: 'getPartyFinAccountInfo',
             data: {partyId : ownerPartyId},
             dataType: 'json',
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	   accountInfo = result["accountInfo"];
            		populateAccountDetails();   
            	      	   
               }
               
             } 
        });
		//jQuery("#issuingAuthority").val(finAccountName);
		
	}	
	function showPaymentEntry(ownerPartyId) {
		var message = "";
		ownerParty = ownerPartyId;
		message += "<html><head></head><body><form action='makeAdvancePayment' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Party Code :</td><td align='left' width='60%'><input class='h4' type='label' id='facilityId' name='facilityId' value='${facilityId?if_exists}' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Method Type :</td><td align='left' width='60%'><select name='paymentMethodTypeId' id='paymentMethodTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						"<#list paymentMethodList as eachMethodType><option value='${eachMethodType.paymentMethodTypeId}' >${eachMethodType.paymentMethodTypeId}</option></#list>"+            
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Issue Authority/ Bank :</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Bank Branch:</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthorityBranch' name='issuingAuthorityBranch' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Date:</td><td align='left' width='60%'><input class='h4' type='text' id='effectiveDate' name='effectiveDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Ref. No. :</td><td align='left' width='60%'><input class='h4' type='text' id='paymentRefNum' name='paymentRefNum' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount' name='amount'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='paymentTypeId' name='paymentTypeId' value='ADVDEPOSIT_PAYIN'/></td></tr>"+
                		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='organizationPartyId' name='organizationPartyId' value='Company'/></td></tr>"+
                		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='partyIdFrom' name='partyIdFrom' /></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='paymentPurposeType' name='paymentPurposeType' value='BYPROD_PAYMENT'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : ${facilityId?if_exists} ";
		Alert(message, title);
		$.ajax({
             type: "POST",
             url: 'getPartyFinAccountInfo',
             data: {partyId : ownerPartyId},
             dataType: 'json',
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	   accountInfo = result["accountInfo"];
            		populateAccountDetails();   
            	      	   
               }
               
             } 
        });
		//jQuery("#issuingAuthority").val(finAccountName);
		
	}
	function populateAccountDetails(){
		if(accountInfo != null){
			jQuery("#amount").val(dueAmount);
			jQuery("#partyIdFrom").val(ownerParty);
			jQuery("#issuingAuthority").val(accountInfo["finAccountName"]);
			jQuery("#issuingAuthorityBranch").val(accountInfo["finAccountBranch"]);
		}			
	}

</script>
