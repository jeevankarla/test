
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
		$( "#estimatedDeliveryDate" ).datepicker({
			dateFormat:'MM dd, yy',
			changeMonth: false,
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
	function showTruckSheetGenerateForm() {	
		var message = "";
		message += "<form action='createLMSShipment' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";
			<#if enableLmsPmSales?exists && enableLmsPmSales>
				message +="<tr class='h3'><td align='left' class='h3' width='50%'>Shipment Type :</td><td align='left' width='80%'><select name='shipmentTypeId' id='shipmentTypeId' class='h3'>"+
	              		"<#list lmsShipTypeList as lmsShipType><option value='${lmsShipType.shipmentTypeId}' >${lmsShipType.description}</option></#list>"+            
						"</select></td></tr>";
				<#else>
				 message += "<input type='hidden' name='shipmentTypeId' id='shipmentTypeId' value='AM_SHIPMENT'/>";
			</#if>	
			message +="<tr class='h3'><td align='left' class='h3' width='40%'>Select Dairy :</td><td align='left' width='60%'><select name='facilityGroupId' id='facilityGroupId' class='h3'>"+
              		"<#list lmdDairyList as lmdDairy><option value='${lmdDairy.facilityGroupId}' >${lmdDairy.description}</option></#list>"+            
					"</select></td></tr>";	
			message += "<tr class='h3'><td align='left' class='h3' width='50%'>Supply Date:</td><td align='right' width='60%'><input class='h3' type='text' id='estimatedDeliveryDate' name='estimatedDeliveryDate' onmouseover='datepick()' size='13'/>" +
				 "</td></tr><tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.Generate}' id='generateTruckSheet' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		
		message += "</table></form>";				
		var title = "Generate TruckSheet";
		Alert(message, title);
	};	

</script>
