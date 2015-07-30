
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	var routeData = ''
	var routeValuesList ;
	var routeList ='';
	var shipmentRouteList;
	var cancelDom;
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
				populateDate();
				setDropDown();
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
	function setDropDown(){
		
		
		var cancelParentDomObj = $(cancelDom).parent().parent();
		
		var dropdownDom = $(cancelParentDomObj).find( "[name='"+"shipmentId"+"']");
		
		$(dropdownDom).clone().appendTo($("#routeShipId"));
		$("#routeShipId").html($(dropdownDom).html());
	}
	
	function setCancelDomObj(thisObj){
		cancelDom = thisObj;
	}
	
	
	var billigIdval ;
	function showUpdateStatus(billingId, statusId) {
	var message = "";
		  billigIdval = billingId;
		 var statusIdVal = statusId;
		  var optionList = '';
		 // alert("=====statusIdVal="+statusIdVal+"===billigIdval=="+billigIdval);
		 if( statusIdVal ==="GENERATED" || statusIdVal==="APPROVED" || statusIdVal==="APPROVED_PAYMENT") {
		
		 }else{
		  alert("Not a Valid change==!");
		 return false;
		 }
		 
		// alert("=====statusIdVal="+statusIdVal+"===billigIdval=="+billigIdval);
		message += "<form action='updateDTCStatus' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
			message += "<tr class='h3'><td align='left' class='h3' width='40%'><input type='hidden' name='periodBillingId' id='periodBillingId'/>Status:</td><td align='left' width='60%'><select name='statusId' id='statusId'>";
						    if(statusIdVal=="GENERATED"){
							 // alert("=====statusIdVal=INNER="+statusIdVal+"===billigIdval=="+billigIdval);
							message +="<option value ="+"'APPROVED'" + " >" +"Approve Billing"+ "</option>";
							message +="<option value = " +"'REJECTED'" + " >" +"Reject Billing"+ "</option>"; 
							  }
						     if(statusIdVal=="APPROVED"){
							//  alert("=====statusIdVal=WE ARE IN APP="+statusIdVal+"===billigIdval=="+billigIdval);
							  message +="<option value ="+"'APPROVED_PAYMENT'" + " >" +"Approve Payment"+ "</option>";
							  message +="<option value = " +"'REJECTED'" + " >" +"Reject Billing"+ "</option>"; 
							  }
							  if(statusIdVal=="APPROVED_PAYMENT"){
							 // alert("=====statusIdVal=WE ARE IN=="+statusIdVal+"===billigIdval=="+billigIdval);
							  message +="<option value = " +"'REJECT_PAYMENT'" + " >" +"Reject Payment"+ "</option>"; 
							  }
		message +="</select></td></tr>"+            
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='cancelGenerateTruckSheet' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";
		var title = "<h2><center>Update Billing Status</center></h2>";
		Alert(message, title);
		
		};
		
			function populateDate(){
		jQuery("#periodBillingId").val(billigIdval);
		//jQuery("#estimatedDateFormatted").val(dateFormatted);
		//jQuery("#shipmentTypeId").val(shipmentTypeId);
	};
</script>
