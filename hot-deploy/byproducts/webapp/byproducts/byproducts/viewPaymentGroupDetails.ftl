
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
					 
	var paymentGroupData;			
	var paymentGroupId;
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
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function cancelForm(){
		cancelShowSpinner();
		return false;
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function showPaymentGroupDetails() {
		
		var message = "";
		var title = "";
		if(paymentGroupData){
			var groupTotal = 0;
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Payment Id</td><td align='center' class='h3'> Party Code</td><td align='center' class='h3'> Party Name</td><td align='center' class='h3'> Payment Date</td><td align='center' class='h3'> Amount</td>";
			for (i = 0; i < paymentGroupData.length; ++i) {
				message += "<tr><td align='center' class='h4'>" + paymentGroupData[i].paymentId + "</td><td align='left' class='h4'>" + paymentGroupData[i].partyId + "</td><td align='center' class='h4'>"+ paymentGroupData[i].partyName +"</td><td align='center' class='h4'>"+ paymentGroupData[i].paymentDate +"</td>";
				message += "<td align='center' class='h4'>" + paymentGroupData[i].amount + "</td></tr>";
				groupTotal = groupTotal+paymentGroupData[i].amount;
			}
			message += "<tr class='h3'><td></td><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();' class='submit'>Close</button></span></td><td></td></tr>";
			title = "<center>Batch No : " + paymentGroupId + "<center><br /><br /> Total Batch Payment Value = "+ groupTotal +" ";
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
	
	function paymentDetails(payGroupId) {
		paymentGroupId = payGroupId;
		var dataJson = {"paymentGroupId": paymentGroupId};
		showSpinner();
		jQuery.ajax({
                url: 'getPaymentGroupDetails',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
                success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						paymentGroupData = result["paymentDetailsJSON"];
					    cancelShowSpinner();
				  		showPaymentGroupDetails();
               		}
               	}							
		});
	}
</script>
