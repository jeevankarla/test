
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
	
	function makeDSCorrectionPartyUppercase() {
		document.DSPartyChange.newBoothId.value = document.DSPartyChange.newBoothId.value.toUpperCase();
		document.DSPartyChange.boothId.value = document.DSPartyChange.boothId.value.toUpperCase();
	}
	function makeIndentPartyUppercase() {
		document.IndentPartyChange.newBoothId.value = document.IndentPartyChange.newBoothId.value.toUpperCase();
		document.IndentPartyChange.boothId.value = document.IndentPartyChange.boothId.value.toUpperCase();
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
	
	function processRouteChangeEntry() {
		var message = "";
		message += "<form action='processChangeIndentRoute' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";	
			message += "<tr class='h3'><td align='left' class='h3' width='40%'></td><td><input class='h3' type='hidden' id='boothId' name='boothId' value='${boothId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td></td><td align='left' width='60%'><input class='h3' type='hidden' id='routeId' name='routeId' value='${parameters.routeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Change Route '${parameters.routeId?if_exists}' to:</td><td align='left' width='60%'><input type='text' name='newRouteId' id='newRouteId'></td></tr>"+
						"<tr class='h3'><td></td><td><input type='hidden' name='supplyDate' id='supplyDate' value= '${effectiveDate?if_exists}'></td></tr>"+
						"<tr class='h3'><td></td><td><input class='h3' type='hidden' id='productSubscriptionTypeId' name='productSubscriptionTypeId' value='${productSubscriptionTypeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Change Route' id='changeIndentRoute' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title = "Route Change for "+'${boothId?if_exists}'+" on "+'${effectiveDate?if_exists}';
		Alert(message, title);
	};
	
	function processPartyChangeEntry() {
		var message = "";
		message += "<form action='processChangePartyCode' method='post' onsubmit='return disableGenerateButton();' id='DSPartyChange' name='DSPartyChange'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";	
			message += "<tr class='h3'><td align='left' class='h3' width='40%'></td><td><input class='h3' type='hidden' id='boothId' name='boothId' value='${boothId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td></td><td align='left' width='60%'><input class='h3' type='hidden' id='routeId' name='routeId' value='${parameters.routeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>'${boothId?if_exists}' &nbsp;&nbsp;&nbsp;TO ::</td><td align='left' width='60%'><input type='text' name='newBoothId' id='newBoothId'></td></tr>"+
						"<tr class='h3'><td></td><td><input type='hidden' name='supplyDate' id='supplyDate' value= '${effectiveDate?if_exists}'></td></tr>"+
						"<tr class='h3'><td></td><td><input class='h3' type='hidden' id='productSubscriptionTypeId' name='productSubscriptionTypeId' value='${productSubscriptionTypeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Change Party' id='changeIndentRoute' class='smallSubmit' onClick='javascript: makeDSCorrectionPartyUppercase();'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title = "Party Change on "+'${effectiveDate?if_exists}';
		Alert(message, title);
	};
	function processChangeIndentParty(){
		var message = "";
		message += "<form action='processChangeIndentParty' method='post' onsubmit='return disableGenerateButton();' id='IndentPartyChange'  name='IndentPartyChange'><table cellspacing=10 cellpadding=10>"; 		
		
			//message += "<br/><br/>";	
			message += "<tr class='h3'><td align='left' class='h3' width='40%'></td><td><input class='h3' type='hidden' id='boothId' name='boothId' value='${boothId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td></td><td align='left' width='60%'><input class='h3' type='hidden' id='routeId' name='routeId' value='${parameters.routeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>'${boothId?if_exists}' &nbsp;&nbsp;&nbsp;TO ::</td><td align='left' width='60%'><input type='text' name='newBoothId' id='newBoothId'></td></tr>"+
						"<tr class='h3'><td></td><td><input type='hidden' name='supplyDate' id='supplyDate' value= '${effectiveDate?if_exists}'></td></tr>"+
						"<tr class='h3'><td></td><td><input class='h3' type='hidden' id='productSubscriptionTypeId' name='productSubscriptionTypeId' value='${productSubscriptionTypeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Change Party' id='changeIndentRoute' class='smallSubmit' onClick='javascript: makeIndentPartyUppercase();'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title = "Party Change on "+'${effectiveDate?if_exists}';
		Alert(message, title);
	}
</script>
