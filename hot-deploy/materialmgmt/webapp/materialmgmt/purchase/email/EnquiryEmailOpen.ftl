
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
				   $('div#emailData_spinner').html('<img src="/images/ajax-loader64.gif">');
				    populateDate();
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
	
	function showSpinner() {
		//alert("Spinner==Called");
		var message = "";
		var title = "";
		message += "<div align='center' name ='displayMsg' id='emailData_spinner'/><button onclick='return cancelForm();' class='submit'/>";
		Alert(message, title);
		
	};
	function cancelShowSpinner(){
		$('button').click();
		return false;
	}
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	//handle cancel event
	function cancelForm(){	
	 cancelShowSpinner();	 
		return false;
	}
	var globalSupplierPartyId;
  	var globalSendToEmail;
	var globalSendFromEmail;
	var globalSendCcEmail;
	 
	function showEnquiryEmail(supplierPartyId,custRequestId) {
	    showSpinner();
		globalSupplierPartyId=supplierPartyId;
       var dataString="custRequestId=" + custRequestId + "&supplierPartyId=" + supplierPartyId ;
      $.ajax({
             type: "POST",
             url: "sendEquiryEmailDataAjax",
           	 data: dataString ,
           	 dataType: 'json',
           	 async: false,
        	 success: function(result) {
              if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
           	  		 alert(result["_ERROR_MESSAGE_"]);
              			}else{
             				  var emailData;
           	  					 emailData =result["emailData"];
           	  					 cancelShowSpinner();
          	 	 				globalSendToEmail=emailData.sendToEmail;
          	 					 globalSendFromEmail=emailData.sendFromEmail;
          	 					  globalSendCcEmail=emailData.sendCcEmail;
          	 					  
          	 					  //alert("globalSendToEmail=========="+globalSendToEmail+"==globalSendCcEmail="+globalSendCcEmail);
          	 	 
          					  }
               
          	} ,
         	 error: function() {
          	 	alert(result["_ERROR_MESSAGE_"]);
         	 }
          }); 
          
          
		var message = "";
		message += "<div style='width:100%;height:350px;overflow-x:auto;overflow-y:auto;' ><form action='sendEquiryEmailToSupplier' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10  width='100%' > " ; 
			message +="<tr ><td align='right' class='h2' width='15%' >Send To: </td><td  class='h2' align='left' width='75%'>&nbsp;<input type='text' size='42'  id='sendTo' name='sendTo'/>"+
					"</td></tr>";
			message +="<tr ><td align='right' class='h2' width='15%' >Send CC: </td><td  class='h2' align='left' width='75%'>&nbsp;<input type='text' size='42'  id='sendCc' name='sendCc'/> <input type='hidden' id='sendFrom'  name='sendFrom'  /> "+
			"</td></tr>";
		   message += "<tr ><td align='right' class='h2' width='15%' >Subject &nbsp;: </td><td align='left' class='h2' width='75%'  >&nbsp;<input type='text' size='63'  id='subject' name='subject'/><input type='hidden' id='partyId'  name='partyId' value='"+supplierPartyId+"'  /><input type='hidden' id='custRequestId'  name='custRequestId' value='"+custRequestId+"'  /> </tr></tbody></table></td></tr>";
			 message += "<tr ><td width='100%' colspan='2' ><table  border='0' cellspacing='10' cellpadding='10'><tbody><tr><td width='15%' align='left' class='label labelFontCSS' >Content:</td><td align='left'  width='75%'  >";
              message += "<textarea name='longDescription' id='longDescription' cols='70' rows='10'></textarea> </td></tr>";
			
	          message +="<tr ><td align='right' class='h3' width='15%' ><input type='submit' value='Send Mail' id='sendMail' class='smallSubmit'/></td><td width='20%' align='center' class='h3' ><span align='center'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
	          message += "</table></form>";
	      	
		message += "</div>";				
		var title = "<center  class='h2' >Send Enquiry Email <center> ";
		Alert(message, title);
	};
	
	function populateDate(){
	
	     jQuery("#sendFrom").val(globalSendFromEmail);
	  
		  jQuery("#sendTo").val(globalSendToEmail);
		
		  jQuery("#sendCc").val(globalSendCcEmail);
      

	};
</script>
