
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<style type="text/css">
.ui-tooltip1, .qtip{
	position: absolute;
	left: -10000em;
	top: -10000em;
 
	max-width: 600px; /* Change this? */
	min-width: 450px; /* ...and this! */
}
</style>

<script type="application/javascript">
					 
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	 
	    
		var loanId;
	 
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
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	var invoiceId;
	var amt;
	
	function datepick()	{

		$( "#instrumentDate" ).datepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
	function createLoandisbursement(form) {
		var test = $(form).html();
		var table = $("#parameters").parent().html();
		
		
		message = "";
		message += "<form action='createLoanDisbursement' method='post' onsubmit='return disableSubmitButton();'>";
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>";
		$('#parameters tr').each(function(i, row){
			var tdObj = $(row).find('td');
			message += "<tr>";
			$(tdObj).each(function(j, cell){
				var loanId = $($(cell).find("#loanId")).val();
				var amount = $($(cell).find("#amount")).val();
				var partyId = $($(cell).find("#partyId")).val();
				message += "<tr class='h2'><td align='left'class='h3' width='60%'>LoanId: <input type=hidden name='loanId_o_"+i+"' value='"+loanId+"'>"+loanId+"</td><td align='left'class='h3' width='60%'>PartyId: <input type=hidden name='partyId_o_"+i+"' value='"+partyId+"'>"+partyId+"</td>";
				message += "<td align='left'class='h3' width='60%'>Amount:</td><td><input type=hidden name='amount_o_"+i+"' value='"+amount+"'>"+amount+"</td></tr>";
			});
		});
			message += 	"<tr class='h2'><td align='left'class='h3' width='60%'>Financial Account:</td><td align='left' width='60%'><select name='finAccountId' id='finAccountId'  class='h4'>"+
							"<#if finAccountList?has_content><#list finAccountList as finAccount><option value='${finAccount.finAccountId?if_exists}' >${finAccount.finAccountName?if_exists}</option></#list></#if>"+            
							"</select></td></tr>";
						
		   message += "<tr class='h2'><td align='left' class='h3' width='60%'>Cheque Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='instrumentDate' name='instrumentDate' onmouseover='datepick()'/></td></tr>" +
					  "<tr class='h2'><td align='left' class='h3' width='60%'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='contraRefNum' name='contraRefNum'/></tr>" +
					  "<tr class='h2'><td align='left' class='h3' width='60%'>Description:</td><td align='left' width='60%'><input class='h4' type='text'  id='description' name='description'/></tr>" ;
			
		
			
			message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			message +=	"</table></form></body></html>";
			message += "</tr>";	
		title = "<center>Loan Disbursement<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
</script>
