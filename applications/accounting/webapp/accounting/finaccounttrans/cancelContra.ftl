
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	var finAccId;
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
	
		function datepick(){
		$( "#returnDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function cancelForm(){		 
		return false;
	}
	
	function cancelDepositContra(finAccountTransId) {
		var message = "";
		message += "<html><head></head><body><form action='cancelContraTrans' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Bounce/Returns :</td><td align='left' width='60%'><select name='chequeBounce'  id='chequeBounce' onchange='javascript:chequeBounceFieldsOnchange();' class='h4'>"+
						"<option value='N' >No</option>"+  
						"<option value='Y' >Yes</option>"+          
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>cancel Date :</td><td align='left' width='70%'><input class='h4' type='text' id='returnDate' name='returnDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td></td><td><input type='hidden' name='finAccountTransId' id='finAccountTransId' value='"+finAccountTransId+"'></td></tr>"+
					"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
					message +=	"</table></form></body></html>";
		var title = "Cancel Payment For finAccount : "+finAccountTransId;
		Alert(message, title);
		
	}
		
</script>
