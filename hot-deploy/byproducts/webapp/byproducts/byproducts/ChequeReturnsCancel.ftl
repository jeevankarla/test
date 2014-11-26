
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
					chequeBounceFieldsOnchange();
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
		$( "#returnDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
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
	
	function chequeBounceFieldsOnchange(){
		jQuery("input[name='bounceReason']").parent().parent().hide();
		jQuery("input[name='returnDate']").parent().parent().hide();
		var str=jQuery("select[name='chequeBounce']").val();	
		if(str == "Y"){	
			jQuery("input[name='bounceReason']").parent().parent().show();
			jQuery("input[name='returnDate']").parent().parent().show();
		}else{
			$('#bounceReason').attr('value','');
			jQuery("input[name='bounceReason']").parent().parent().hide();
			jQuery("input[name='returnDate']").parent().parent().hide();
			
		}
	}
	
	function chequeReturns(paymentId) {
		var message = "";
		message += "<html><head></head><body><form action='cancelPaymentForChequeReturn' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Bounce/Returns :</td><td align='left' width='60%'><select name='chequeBounce'  id='chequeBounce' onchange='javascript:chequeBounceFieldsOnchange();' class='h4'>"+
						"<option value='N' >No</option>"+  
						"<option value='Y' >Yes</option>"+          
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Return Date :</td><td align='left' width='70%'><input class='h4' type='text' id='returnDate' name='returnDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Reason For Bounce/Return :</td><td align='left' width='70%'><input class='h4' type='text' id='bounceReason' name='bounceReason' /></td></tr>" +
						"<tr class='h3'><td></td><td><input type='hidden' name='paymentId' id='paymentId' value='"+paymentId+"'></td></tr>"+
					"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
					message +=	"</table></form></body></html>";
		var title = "Cancel Payment For paymentId : "+paymentId;
		Alert(message, title);
		
	}
		function nonroutechequeReturns(paymentId) {
		var message = "";
		message += "<html><head></head><body><form action='cancelNonRoutePaymentForChequeReturn' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Bounce/Returns :</td><td align='left' width='60%'><select name='chequeBounce'  id='chequeBounce' onchange='javascript:chequeBounceFieldsOnchange();' class='h4'>"+
						"<option value='N' >No</option>"+  
						"<option value='Y' >Yes</option>"+          
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Return Date :</td><td align='left' width='70%'><input class='h4' type='text' id='returnDate' name='returnDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Reason For Bounce/Return :</td><td align='left' width='70%'><input class='h4' type='text' id='bounceReason' name='bounceReason' /></td></tr>" +
						"<tr class='h3'><td></td><td><input type='hidden' name='paymentId' id='paymentId' value='"+paymentId+"'></td></tr>"+
					"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
					message +=	"</table></form></body></html>";
		var title = "Cancel Payment For paymentId : "+paymentId;
		Alert(message, title);
		
	}

</script>
