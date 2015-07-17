
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}

</style>

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
				//populateDate()
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
	
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	//handle cancel event
	function cancelForm(){		 
		return false;
	}

	var globalVehicleId;

	function vehicleDashBoardQtip(vehicleId ,sequenceNum){
       var vehicleStatusDetails=[];
       var vehicleProdDetails={};
	   globalVehicleId=vehicleId;
       //var dataString="vehicleId=" + vehicleId ;
       var dataJson = {"vehicleId":vehicleId,"sequenceNum":sequenceNum};
       $.ajax({
             type: "POST",
             url: "getVehicleDashboardDetails",
           	 data: dataJson ,
           	 dataType: 'json',
           	 async: false,
        	 success: function(result) {
	            if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){ 
	      	  		 alert(result["_ERROR_MESSAGE_"]);
	        	 }else{
					 vehicleProdDetails =result["productObj"];
					 vehicleStatusDetails =result["vehicleStatusJSONList"];
			     }
          	 } ,
         	 error: function() {
          	 	alert(result["_ERROR_MESSAGE_"]);
         	 }
       });
        var receivedFat=vehicleProdDetails.receivedFat;
        var netWeight=vehicleProdDetails.netWeight;
    	var message = "";
        message += "<form action='' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 > " ; 
		message +="<tr>"+"<td>"+"<input  type='hidden' required='required' name='vehicleId' value='"+vehicleId+"' />"+"</td></tr>";
	    message +="<tr class='h3'><td align='left' class='h3' width='25%'>Product Name :</td><td align='left' width='25%'>"+vehicleProdDetails.productName+" [ "+vehicleProdDetails.productId+"] </td></tr>";
	    message +="<tr class='h3'><td align='left' class='h3' width='25%'>Union/Chilling Center :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.partyId+"</td><td align='left' width='30%'>DC No      :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.dcNo+"</td></tr>";
		
		if (netWeight) {
		message +="<tr class='h3'><td align='left' width='20%'>Gross Weight(Kgs) :</td><td align='left' width='10%'>"+vehicleProdDetails.grossWeight+"</td><td align='left' width='20%'>Tare Weight (Kgs) :</td><td align='left' width='10%' keep-together='always'>"+vehicleProdDetails.tareWeight+"</td><td align='left' width='20%' keep-together='always'></tr>";
		message +="<tr class='h3'><td align='left' class='h3' width='25%'>Silo Id :</td><td align='left' width='30%'>"+vehicleProdDetails.siloId+"</td><td align='left' width='25%' >Net Weight (Kgs) :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.netWeight+"</td></tr>";
		}
		if (receivedFat) {
		message +="<tr class='h3'><td align='left' class='h3' width='25%'>Fat % :</td><td align='left' width='30%'>"+vehicleProdDetails.receivedFat+"</td><td align='left' width='25%'>SNF % :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.receivedSnf+"</td></tr>";
		}
		for(var i=0 ; i<vehicleStatusDetails.length ; i++){
			var innerList=vehicleStatusDetails[i];
			var statusId=  innerList['statusId']; 
			var statusEntryDate=  innerList['statusEntryDate']; 
		    message +="<tr class='h3'><td align='left' width='30%' keep-together='always'>"+statusId+" Time :</td><td align='left' width='30%' keep-together='always'>"+statusEntryDate+"</td></tr>";
		}
		message += "<tr class='h3'><td></td><td width='10%' align='center' class='h3'><span align='right'><button id='cancelButton' class='styled-button' value='${uiLabelMap.CommonClose}' onclick='return cancelForm();' class='smallbutton'>${uiLabelMap.CommonClose}</button></span></td></tr>";
		message += "</table></form>";	    
  		//alert("#####"+message);
	    var title ="Tanker "+vehicleId+" Details ";
		Alert(message, title);   
        
 	}
	
</script>
