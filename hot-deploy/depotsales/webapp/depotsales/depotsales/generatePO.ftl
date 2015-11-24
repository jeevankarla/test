
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	
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
				//getAllIndentRejects();
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
		function disableButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}

	function cancelForm(){		 
		return false;
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
function submitForm(){		 
		
		 jQuery('#purchaseOrderEntry').submit();
	}

	function datepick1()	{

		$( "#orderDate" ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}

function purchaseOrder(orderId, salesChannel,supplierPartyId,supplierPartyName,productStoreId,orderDate){
	  var action;
     var message = "";

                message += "<html><head></head><body><form id='purchaseOrderEntry' method='post' action='createPOByOrder' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>OrderId:</td><td align='left'  width='90%'><font size=45%>"+orderId+"</font><input class='h4' type='hidden' value="+orderId+" id='orderId' name='orderId' /></td></tr>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>Branch Name:</td><td align='left'  width='90%'><font size=45%>"+productStoreId+"</font><input class='h4' type='hidden' value="+productStoreId+" id='productStoreId' name='productStoreId' /></td></tr>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>supplierPartyId:</td><td align='left'  width='90%'><font size=45%>"+supplierPartyName+"["+supplierPartyId+"]</font><input class='h4' type='hidden'  id='supplierId' name='supplierId' value="+supplierPartyId+"   readOnly /></td></tr>";
				message +=	"<tr class='h3'><td align='left' class='h3' width='40%'>Order Date:</td><td align='left' width='60%'><input class='h4' type='text' class='required' readonly  value="+orderDate+" id='orderDate' name='orderDate' onmouseover='datepick1()' required /></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
				message +=	"<tr class='h3'><td align='left' width='60%'></td><td align='left' class='h3' width='40%'><font size=55%><b><u>Shipping Deatils</u></b></font></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='return submitForm();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
			    message +=	"</table></form></body></html>";
	var title = "Generate Purchase Order";
    Alert(message, title);
     //action= makeMassReject;
     //jQuery('#ListIndentSubmit').attr("action", action);
     //jQuery('#ListIndentSubmit').submit();
};
	
	
</script>
