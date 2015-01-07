
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
<#-->
	var routeData = ${StringUtil.wrapString(facilityItemsJSON)} -->
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
				populateDate()
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
	
	
	
	var globalShipmentId ="";
	
	function showLinkGrnWithPOForm(shipmentId) {	
	globalShipmentId=shipmentId;
	//alert("==shipmentId=="+shipmentId);
		var message = "";
		message += "<div style='width:100%;height:380px;overflow-x:auto;overflow-y:auto;' ><table cellspacing=10 cellpadding=10  width='100%' > " ; 
		message += "<tr class='h3'><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		<#--		
			message +="<tr class='h3'><td >GRN Shipment Id :  <input class='h3' type='text' readonly id='grnShipmentId' name='grnShipmentId'  size='13'/></td></tr>";
          -->
           message += "<tr class='h3'><td align='left' class='h3' width='100%' ><table cellspacing=10 cellpadding=10 border=2 width='100%' >" ;
           message +="<thead class='h3'><th align='center' class='h3' width='50%' >Supplier Name</th><th  align='center' class='h3' width='20%' >OrderId</th><th align='center' width='20%' class='h3' >Order Date</th><th align='right' class='h3' >Link</th></thead>";
          	message += "</td></<thead></table>";
          <#list ordersListForGRNLink as eachOrderLink>
          message += "<tr ><td align='left'  width='100%'  ><form action='processUpdateGRNWithPO' method='post' onsubmit='return disableGenerateButton();'>";
          message += "<input type=hidden name=shipmentId  value='"+shipmentId+"'><input type=hidden name=orderId  value='${eachOrderLink.orderId?if_exists}'> <table cellspacing=10 cellpadding=10 border=2 width='100%' >" ;
          
           message +="<tr class='h4'><td align='left' width='50%'  class='h5' >${eachOrderLink.supplierName?if_exists}</td><td width='20%' align='left' class='h3' >${eachOrderLink.orderId?if_exists}</td><td width='20%' align='left' class='h4' >${eachOrderLink.entryDate?if_exists}</td><td width='20%' align='right' class='h3' ><input type='submit' value='Link PO' id='generateTruckSheet' class='smallSubmit'/></td></tr>";
          	message += "</form></td></tr></table>";
          </#list>
			
		message += "</table></div>";				
		var title = "<center>GRN Shipment Id : " + shipmentId + "<center><br /><br /> And Possibilities PO's Are  Below ";
		Alert(message, title);
	};
	
	function populateDate(){
	//alert("==shipmentId ==InPopulateData=="+globalShipmentId);
		jQuery("#grnShipmentId").val(globalShipmentId);
	};
</script>
