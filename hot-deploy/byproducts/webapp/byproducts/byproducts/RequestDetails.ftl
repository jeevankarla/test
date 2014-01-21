
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">

	var custReqItems = ${StringUtil.wrapString(custReqJSON)}

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
					on: false, // Make it modal (darken the rest of the page)...
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
		// Content will consist of the message and an ok button
		var message = $('<p />', { html: message }),
			ok = $('<button />', { text: 'Ok', 'class': 'full', 'align': 'center' });
 
		dialogue( message.add(ok), title );
	}
	
	function showCustRequestItems(custRequestId) {
		
		var custReqList = custReqItems["custReqItems"];
		var hasItems = "no";
		
		var title = "";
		title += "<table cellspacing=10 cellpadding=10 width=500>"
		title += "<tr class='h3'>"+
				 	"<td align='center' class='h3' width='20%'>S.No.</td>"+
				 	"<td align='center' class='h3' width='10%'>ProductId</td>"+
				 	"<td align='center' class='h3' width='20%'>ProductName</td>"+
				 	"<td align='center' class='h3' width='20%'>Quantity</td>"+
				 "</tr>"
		title += "</table>"
		
		var message = "";
		message += "<table cellspacing=10 cellpadding=10 width=500>" ; 
		
		for (i = 0; i < custReqList.length; ++i) {
			if((custReqList[i].custRequestId) == custRequestId){
				
				message += "<tr class='h3'>"+
				 				"<td align='center' class='h3' width='20%'>"+custReqList[i].custRequestItemSeqId+"</td>"+
				 				"<td align='center' class='h3' width='10%'>"+custReqList[i].productId+"</td>"+
				 				"<td align='center' class='h3' width='20%'>"+custReqList[i].productName+"</td>"+
				 				"<td align='center' class='h3' width='20%'>"+custReqList[i].quantity+"</td>"+
				 			"</tr>"
				hasItems = "yes"; 			
			}
		}
		
		if(hasItems == "no"){
			message += "<tr class='h3'>"+
				 				"<td align='center' class='h2' width='100%'>No Items Found</td>"+
				 		"</tr>"
		}
		
		
		message += "</table>";
		
		
		Alert(message, title);
		
	};
</script>