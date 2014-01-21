
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
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});	
		$("jqueryselector").qtip({
   			content: 'I have rounded corners... I hear they\'re all the rage at the moment!',
   		style: { 
      		border: {
        	 width: 3,
         	radius: 15,
         	color: '#6699CC'
      	},
      	width: 200
		}  }); 	
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
		$( "#responseRequiredDate" ).datepicker({
			dateFormat:'yy-mm-dd',
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
	
	function editIndentItems() {	
	
		var message = "";
		var title = "";
		
		title += "<table cellspacing=10 cellpadding=10 width=350>"
		title += "<tr class='h3'>"+
					"<td align='center' class='h3' width='100%'>Create New Indent</td>"+
				 "</tr>"
		title += "</table>"
		
		message += "<form action='createRequest' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=350>" ; 		
		
			//message += "<br/><br/>";
			message += "<tr class='h3'>"+
				            "<td align='left' class='h3' width='50%'>Indent Name</td>"+
				            "<td align='left' width='50%'><input class='h3' type='text' id ='custRequestName' name= 'custRequestName' size='10'/></td>"+
				       "</tr>"+ 
				       "<tr class='h3'>"+
				       		"<td align='left' class='h3' width='50%'>Request To Store:</td>"+
				       		"<td align='left' width='50%'>"+
				       			"<select name='productStoreId' id='productStoreId' class='h4'>"+
                               		"<#list storesList as eachStore>"+
                               			"<option value='${eachStore.productStoreId}' >${eachStore.productStoreId}</option>"+
                               		"</#list>"+            
                                "</select>"+
                            "</td>"+
                       "</tr>"+
				       "<tr class='h3'>"+    
				            "<td align='left' class='h3' width='50%'>Requesting Party</td>"+
				            "<td align='left' width='50%'><input class='h3' type='text' id ='fromPartyId' name= 'fromPartyId' size='5'/></td>"+
				       "</tr>"+     
				       "<tr class='h3'>"+     
				            "<td align='left' class='h3' width='40%'>Required Date:</td>"+
				            "<td align='left' width='60%'><input class='h3' type='text' id='responseRequiredDate' name='responseRequiredDate' onmouseover='datepick()' size='20'/></td>"+
				       "</tr>"
					   "<tr></tr>"+
					   "<tr></tr>"+
					   "<tr></tr>";
			message += "<tr class='h3'>"+
							"<td align='center' width='50%'><span align='right'><input type='submit' value='DONE' id='DONE' class='smallSubmit'/></span></td>"+
							"<td class='h3' width='50%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>CANCEL</button></span></td>"+
					   "</tr>";
			
		message += "</table></form>";				
		
		Alert(message, title);
	
	};	

</script>
