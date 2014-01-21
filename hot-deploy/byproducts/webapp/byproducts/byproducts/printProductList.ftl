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
				title: {
					 text : title,	
					 button: 'Close'	
					} 
				 
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
	function cancelForm(){		 
		return false;
	}
	
	function printProducts(msg) {	
		var message = "";
			message += "<form action='ListProducts' method='post' onsubmit='return cancelForm();'><table width=300 cellspacing=10 cellpadding=5>" ; 		
			message += "<tr><td align ='left' width='20%'  class='h2'><font color='BLUE'><u>Product-Code</u></font></td><td class='h2' align ='left' width='80%'><u><font color='BLUE'>Product-Name</font></u></td></tr>"; 
			if(msg=='Indented'){
				message += "<#list indentedProductsList as productDetails>";
				message +="<tr class='h3' ><td align='left' width='20%'>${productDetails.productId}</td><td border='1' align='left' width='80%'nowrap>"+
		              		"${productDetails.productName}"+            
							"</td></tr></#list>";
			}else{
				message += "<#list notIndentdProductIdsSet as productDetails>";
				message +="<tr class='h3' ><td align='left' width='20%'>${productDetails.productId}</td><td  border='1' align='left' width='80%' nowrap>"+
		              		"${productDetails.productName}"+            
							"</td></tr></#list>";
			}			
			message += "<tr class='h3'><td class='h3' width='100%' align='center'><span align='center'><button value='${uiLabelMap.CommonOk}' onclick='return cancelForm();' class='submit'>OK</button></span></td></tr>";
				 
			message += "</table></form>";		 			
		var title = msg+" Products";
		
		Alert(message, title);
	};	

</script>