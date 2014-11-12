
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
	var boothsData;			
	var boothId;			 
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
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
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
		
		dialogue( message, title );
	}
	
	function showPaymentHistory() {
		
		var message = "";
		var title = "";
		if(boothsData != undefined){
			
			message += "<table cellspacing=10 cellpadding=10>" ;
		
			var boothMap = boothsData[boothId];
			var boothPayments = boothMap["boothDuesList"];
		 	var boothAdvPayments = boothMap["boothAdvPaymentList"];
		 	
		 	message += "<tr><td align='left'><h2>Date</h2></td><td align='right'><h2>Amount</h2></td></tr>";
			message += "<tr><td align='left' colspan='2' align='center'><h3><i>Invoice Outstanding</i></h3></td></tr>";
						
			for (var i = 0; i < boothPayments.length; ++i) {
				message += "<tr><td align='left'>" + boothPayments[i].supplyDate + "</td><td align='right'>" +
					boothPayments[i].amount + "</td></tr>";
			}
			if(boothAdvPayments.length>0){
				message += "<tr><td align='left' colspan='2' align='center'><h3><i>Payment Unapplied</h3></i></td></tr>";
			}
			for (var i = 0; i < boothAdvPayments.length; ++i) {
				message += "<tr><td align='left'>" + boothAdvPayments[i].supplyDate + "</td><td align='right'>" +
					boothAdvPayments[i].amount + "</td></tr>";
			}
			message += "<tr class='h3'><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();' class='submit'>Close</button></span></td></tr>";
			title = "Dues for Party " + boothId + "<br /> [Total: " + boothMap["totalAmount"] + "]";
			message += "</table>";
			Alert(message, title);
		}
		
	};
	
	function showSpinner() {
		
		var message = "";
		var title = "";
		message += "<div align='center' name ='displayMsg' id='pastDues_spinner'/><button onclick='return cancelForm();' class='submit'/>";
		Alert(message, title);
		
	};
	function cancelShowSpinner(){
		$('button').click();
		return false;
	}
	
	function showRetailerDueHistory(retailerId) {
		boothId = retailerId;
		var dataJson = {"boothId": boothId};
		showSpinner();
		jQuery.ajax({
                url: 'getRetailerFullDues',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in getting past dues");
					}else{
					    boothsData = result["boothsDuesDaywiseJSON"];
					    cancelShowSpinner();
					  	showPaymentHistory(boothId);
               		}
               	}							
		});
	}	
</script>
