<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


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
					$('button', api.elements.content).click(api.hide);
					// It will remove special characters except - symbol
					$('.capsOnly').keyup(function()
         				{ 
         					$(this).val($(this).val().toUpperCase().replace(/[&\/\\#,+()$~%'":*?<>^{}`~,\]\[ ]/g, ''));
         			});
         			datetimepick("testDate");
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
	
	function datepick()
	{		
		var currentTime = new Date();
	 	// First Date Of the month 
	 	var startDateFrom = new Date(currentTime.getFullYear(),currentTime.getMonth(),1);
	 	// Last Date Of the Month 
	 	var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
		$( "#testDate" ).datetimepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
			minDate: startDateFrom,
			maxDate:0,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
 //handle cancel event
	function cancelForm(){		 
		return false;
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
		   $("input[type=submit]").attr("disabled", "disabled");
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	function getProductTestComponents(productId){
		var dataJson = {"productId":productId};
		var productTestComponentDetails = {};
		$.ajax({
			 type: "POST",
             url: 'getProductTestComponents',
             data: dataJson,
             dataType: 'json',
             async:false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               	alert('Product test components not found, Please contact Admin');
               }else{
               		productTestComponentDetails = result['productTestComponentDetails'];
               }
             } 
		});
		return productTestComponentDetails;
	}	
	function showQcForm(productId,url,fieldName,fieldValue) {	
		var message = "";
		var productTestComponentDetails = {};
		productTestComponentDetails = getProductTestComponents(productId);
		var productName = productTestComponentDetails['productName'];
		var productTestComponents  = productTestComponentDetails['productTestComponents'];
		message += "<form action="+url+"  method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		message +="<tr>"+"<td>"+
					"<input  type='hidden' required='required' name='"+fieldName+"' value='"+fieldValue+"' />"+
					"<input  type='hidden' required='required' name='productId' value='"+productId+"' />"+
					"</td></tr>";
			//message += "<br/><br/>";
			
			message +="<tr class='h3'><td align='left' class='h3' width='25%'>Product:   </td><td align='left' width='30%'>"+productName+"</td><td align='left' width='30%'>MIN</td><td align='left' width='30%' keep-together='always'>MAX</td></tr>";
			
			message +="<tr class='h3'><td align='left' class='h3' width='40%'>Date:   </td><td align='left' width='80%'><input type='text' name='testDate' id='testDate' onmouseover='datepick()'/></td></tr>";
			
			for(i=0; i<productTestComponents.length;i++){
				var productTestComponent = productTestComponents[i];
				var testName = productTestComponent['testComponent']+"_testComponent" ;
				var testDescription = productTestComponent['testDescription'] ;
				var minimamValue = productTestComponent['minimamValue'] ;
				var maximamValue = productTestComponent['maximamValue'] ;
				if(maximamValue == null){
					maximamValue = "-";
				}
				if(minimamValue == null){
					minimamValue = "-";
				}
				
				message +="<tr class='h3'><td align='left' class='h3' width='40%'>"+testDescription+"  </td><td align='left' width='30%'><input class='capsOnly' autoComplete='off' type='text' required='required' name='"+testName+"' /><eom>*</eom> </td>"+
				"<td align='left' width='30%'>"+minimamValue+"  </td><td align='left' width='30%'>"+maximamValue+"</td>"+"</tr>";
			}		
			message += "<tr><td class='h2' width='40%'>RESULT</td><td colspan='4' width='50%'>"+
                      "<select name='statusId' class='h3'><option value='QC_ACCEPT'>ACCEPT</option><option value='QC_REJECT'>REJECT</option> </select></td></tr>"
			message += "<tr><td class='h2' valign='center' width='40%'>REMARKS</td><td colspan='4' width='80%'>"+
                      "<textarea cols='30' rows='3' wrap='hard' name='comments' maxlength='255'></textarea></td></tr>"
					
			message += "<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Submit' id="+url+" class='smallSubmit'/></span></td></td><td width='10%' align='center' class='h3'><span align='right'><button class='styled-button' value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallbutton'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title =productName+" QC DETAILS ";
		Alert(message, title);
	};	
</script>

