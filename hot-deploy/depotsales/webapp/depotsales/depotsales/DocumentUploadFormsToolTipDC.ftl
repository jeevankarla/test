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
function datepicker()
{		

	$( "#thruDate" ).datepicker({
		dateFormat:'yy-mm-dd',
		changeMonth: true,
		numberOfMonths: 1});	
	$( "#fromDate" ).datepicker({
		dateFormat:'yy-mm-dd',
		changeMonth: true,
		numberOfMonths: 1});				
}
//disable the generate button once the form submited
function disableGenerateButton(){			
	   $("input[type=submit]").attr("disabled", "disabled");
	  	
}
//handle cancel event
function cancelForm(){		 
	return false;
}
function addContent(shipmentId,PoNumber){
	 var shipmentId=shipmentId;
	 var PoNumber=PoNumber;
     var message = "";
                message += "<html><head></head><body><form method='post' action='/depotsales/control/createDepotSalesShipmentContent?shipmentId="+shipmentId+"' enctype='multipart/form-data'  id='AddShipmContent' onsubmit='return disableGenerateButton();' name='AddShipmentContent'><table cellspacing=10 cellpadding=10 width=400>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>Shipment Id</td><td align='left' width='90%'><input class='h4' type='text' id='shipmentId' name='shipmentId' readonly value='"+shipmentId+"'/></td></tr>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>PO No: </td><td align='left' width='90%'>"+PoNumber+"<font size=45%></font></td></tr>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>Content Type</td><td align='left' width='90%'><select name='contentTypeId' id='contentTypeId'><option value='LR_DOCUMENT' >LR-Document</option></select></td></tr>";
      			message += "<tr class='h3'><td align='left' class='h3' width='60%'>From Date</td><td align='left' width='60%'><input type='text' id='fromDate' name='fromDate' onmouseover='datepicker()'/></td></tr>";
      			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Thru Date</td><td align='left' width='60%'><input type='text' id='thruDate' name='thruDate' onmouseover='datepicker()'/></td></tr>";
      			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Upload</td><td align='left' width='60%'><input type='file' name='dataResourceName' size='25' required><input class='h4' type='hidden' id='statusId' name='statusId' value='CTNT_AVAILABLE' /><input type='hidden' name='dataResourceTypeId' value='IMAGE_OBJECT' id='AddShipmentContent_dataResourceTypeId'><input type='hidden' name='shipmentId' value='"+shipmentId+"' id='AddShipmentContent_shipmentId'></td></tr>";
     		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Upload' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
			    message += "</table></form></body></html>";
	var title = "Add Facility Content";
    Alert(message, title);
}
function viewContent(facilityId){
   alert("view======"+facilityId);			
}
</script>