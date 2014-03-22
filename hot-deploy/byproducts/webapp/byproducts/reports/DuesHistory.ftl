
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
/*
	var boothsData = { "2055" : {"boothDuesList" : [{"supplyDate" : "17/05/2012", "amount" : "Rs15.00"},
								 					{"supplyDate" : "13/05/2012", "amount" : "Rs217,000.00"}],
								 "totalAmount" : "Rs217,015.00"
								}
					 };
*/								 
	var boothsData = ${StringUtil.wrapString(boothsDuesDaywiseJSON)}							 
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
		// Content will consist of the message and an ok button
		var message = $('<p />', { html: message }),
			ok = $('<button />', { text: 'Ok', 'class': 'full' });
 
		dialogue( message.add(ok), title );
	}
	
	function showPaymentHistory(boothId) {
		//alert("boothId=" + boothId);
		
		var boothMap = boothsData[boothId];
		var boothPayments = boothMap["boothDuesList"];
		var message = "";
		message += "<table cellspacing=10 cellpadding=10>" ; 		
		for (i = 0; i < boothPayments.length; ++i) {
			//message += boothPayments[i].supplyDate + " ";
			//message += boothPayments[i].amount + " ";	
			//message += "<br/><br/>";	
			message += "<tr><td align='left'>" + boothPayments[i].supplyDate + "</td><td align='right'>" +
				boothPayments[i].amount + "</td></tr>";
		}
		message += "<tr class='h3'><td class='h3' align='center'><span align='center'><button value='${uiLabelMap.CommonOk}' onclick='return cancelForm();' class='submit'>Close</button></span></td></tr>";
		message += "</table>";	
		var title = "Dues for Booth " + boothId + "<br /> [Total: " + boothMap["totalAmount"] + "]";
		Alert(message, title);
	};
</script>
