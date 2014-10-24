
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
	
	function datepick()
	{		
		$( "#transactionDate" ).datepicker({
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
	
	function showDepositRefundEntry(finAccountId) {
		
		finAccId = finAccountId;
		var message = "";
		var title = "";
		var htmlMsg = "";
		htmlMsg += "<select name='contraFinAccountId' id='contraFinAccountId'>"+
						"<option value=''>-- Select -- </option>"+
						"<#if companyBanksList?has_content><#list companyBanksList as eachFinAccount><option value='${eachFinAccount.finAccountId?if_exists}' >${eachFinAccount.finAccountName?if_exists}</option></#list></#if>"+            
						"</select>";
		message += "<form action='refundDepositContraFinAccTrans' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ;
		message += "<input type=hidden name=entryType value=Contra><input type=hidden name=finAccountTransTypeId value=DEPOSIT><input type=hidden name=statusId value=FINACT_TRNS_CREATED><input type=hidden name=finAccountId value="+finAccId+">";
		message += "<tr class='h3'><td>From/To Account</td><td>"+htmlMsg+"</td></tr>";
		message += "<tr class='h3'><td>Transaction Date</td><td><input type=text name=transactionDate id=transactionDate onmouseover='datepick()'></td></tr>";
		message += "<tr class='h3'><td>Amount</td><td><input type=text name=amount></td></tr>";
		message += "<tr class='h3'><td>Instrument No</td><td><input type=text name=contraRefNum></td></tr>";
		message += "<tr class='h3'><td>Cheque In Favor of</td><td><input type=text name=inFavor></td></tr>";
		message += "<tr class='h3'><td>Comment</td><td><input type=text name=comments></td></tr>";
		message += "<tr class='h3'><td><input type='submit' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></td><td><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></td></tr>";
		title = "<h2><center><font color='#0A007A'>Deposit Refund</font> </center></h2>";
		message += "</table></form></body></html>";
		Alert(message, title);
		
	};
		
</script>
