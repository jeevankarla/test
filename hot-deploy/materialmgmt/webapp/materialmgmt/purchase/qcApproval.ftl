<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
					 
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	var organisationList;
	var partyName;
	var partyIdVal;
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
				  //getEmploymentDetails(partyIdVal);
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	
 	function cancelForm(){                 
         return false;
 	}
 	
	function showQCApprovalQTip(receiptId) {
		var message = "";
		message += "<form action='sendReceiptQtyForQC' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>ReceiptId:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='receiptId' name='receiptId' value='"+receiptId+"'/></td><input class='h4' type='hidden' readonly id='statusIdTo' name='statusIdTo' value='SR_QUALITYCHECK'/></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='50%'>Department :</td><td align='left' width='50%'><select name='partyId' id='partyId'  class='h4'>"+
					"<#if finalDepartmentList?has_content><#list finalDepartmentList as department><option value='${department.partyId?if_exists}' >${department.groupName?if_exists}</option></#list></#if>"+            
					"</select></td></tr>";
		
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Send To QC<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	function QCApprovalQTip(shipmentId) {
		var message = "";
		message += "<form action='shipmentSendForQC' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>ShipmentId:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='shipmentId' name='shipmentId' value='"+shipmentId+"'/></td><input class='h4' type='hidden' readonly id='statusIdTo' name='statusIdTo' value='SR_QUALITYCHECK'/></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='50%'>Department :</td><td align='left' width='50%'><select name='partyId' id='partyId'  class='h4'>"+
					"<#if finalDepartmentList?has_content><#list finalDepartmentList as department><option value='${department.partyId?if_exists}' >${department.groupName?if_exists}</option></#list></#if>"+            
					"</select></td></tr>";
		
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Send To QC<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	
</script>
