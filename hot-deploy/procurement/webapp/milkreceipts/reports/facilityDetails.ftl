
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
					 button: true	
					},
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
			style: {
				name : 'cream',
				width : 500,
				height:400,
			}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
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
			ok = $('<button />', { text: 'Ok', 'class': 'full' });
 
		dialogue( message.add(ok), title );
	}
	
	function showFacilityDetailsForm() {	
		var message = "";
		message += "<table cellspacing=10 cellpadding=10>" ;
		message += "<thead><tr  class='h2'><td align='left'>MCC CODE</td> <td width='100%'>MCC NAME</td></tr></thead>" ; 		
		message +="<#list unitWiseList as shed><tr class='h3'><td align='center' width='20%'><h1>${shed.mccCode?if_exists}</h1>"+   		
						"</td><td>${shed.mccName?if_exists}</td></tr></#list>";
		message += "</table>";	
		var title = "Facility Details ";
		Alert(message, title);
	};
</script>