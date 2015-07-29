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
	
	function datepick()	{
		$( "#statusDatetime" ).datetimepicker({
			dateFormat:'yy-mm-dd',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
	        changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
 	function cancelForm(){                 
         return false;
 	}
 	
	function prepareSuspendPOForm(orderId) {
		var message = "";
		message += "<form action='suspendPO' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>OrderId :</td><td align='left' width='50%'><input class='h3' type='text' readonly id='orderId' name='orderId' value='"+orderId+"'/></td></tr>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Reason :</td><td align='left' width='50%'><input class='h3' type='text'  id='changeReason' name='changeReason'/></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='60%'>StatusDateTime:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='statusDatetime' name='statusDatetime' onmouseover='datepick()'/></td></tr>";
		
		<#assign partyId = userLogin.partyId?if_exists>
		<#assign userLoginId = userLogin.userLoginId?if_exists>
		<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>UserLogin :</td><td align='left' width='50%'><input class='h3' type='text'  id='partyName' name='partyName' value='${partyName}(${partyId})'/></td><input class='h4' type='hidden' readonly id='statusUserLogin' name='statusUserLogin' value='${userLoginId}'/></tr>";
		
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Suspend' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Suspend PC<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
</script>
