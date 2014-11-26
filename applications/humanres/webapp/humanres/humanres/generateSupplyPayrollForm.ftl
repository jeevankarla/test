
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	
	function datepick()
	{		
		$( "#basicSalDate" ).datepicker({
			dateFormat:'MM dd, yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		$( "#paymentDate" ).datepicker({
			dateFormat:'MM dd, yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	function dialogue(content, title) {
		
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
 
	function Alert(message, title)	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
function paymentOnchange(){
	var str=$('#billingTypeId').val();	
	alert(str);
	if(str){
		if(str=="SP_LEAVE_ENCASH"){
				jQuery("input[name='basicSalDate']").parent().parent().show();
		}else{			
			jQuery("input[name='basicSalDate']").parent().parent().hide();
		}	
	}
}	
function showSuplyPayrollGenerateForm() {	
		var message = "";
		message += "<form action='createSuplyPayrollBilling' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";
			message +="<tr class='h3'><td align='left' class='h3' width='30%'>Organization Party Id :</td><td align='left' width='40%'><select name='orgPartyId' allow-empty='true' id='orgPartyId' class='h3'>"+
	              		"<#list orgList as org><option value='${org.partyId?if_exists}' >${org.groupName?if_exists}</option></#list>"+            
						"</select></td></tr>";
			message +="<tr class='h3'><td align='left' class='h3' width='30%'>Suply Payroll Type :</td><td align='left' width='40%'><select name='billingTypeId' allow-empty='true' id='billingTypeId' class='h3' onchange='javascript:paymentOnchange();'>"+
	              		"<#list payrollTypeList as payrollType><option value='${payrollType.enumId?if_exists}' >${payrollType.description?if_exists}</option></#list>"+            
						"</select></td></tr>";		
			message +=	"<tr class='h3'><td align='left' class='h3' width='60%'>Basic Salary Date:</td><td align='left' width='60%'><input class='h4' type='text' id='basicSalDate' name='basicSalDate' onmouseover='datepick()' size='17'/></td></tr>" ;				
			message += 	"<tr class='h3'><td align='left' class='h3' width='40%'>Custom Time Period Id:</td><td align='left' width='40%'><select name='customTimePeriodId' id='customTimePeriodId'>"+
						"<#list customTimePeriodList as customTimePeriod><option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option></#list></select></td></tr>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='generatePayroll' class='smallSubmit'/></span></td><td class='h3' width='80%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			
		message += "</table></form>";				
		var title = "<h2><center>Generate Suply Payroll</center></h2>";
		Alert(message, title);
	}
	
	
</script>
