
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtipAddItems.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">		

	var productIds = ${StringUtil.wrapString(productIdsJSON)}
	var brandNames = ${StringUtil.wrapString(brandNameJSON)}
			 
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
	function showAddItemsForm(custRequestId) {	
	
		var prodList = productIds["productId"];
		var nameList = brandNames["brandName"];
		
		var message = "";
		var title = "";
		
		title += "<table cellspacing=10 cellpadding=10 width=700>"
		title += "<tr class='h3'>"+
					"<td align='center' class='h1' colspan=4><font color='black'>Add Items</font></td>"+
				 "</tr>"+
				 "<tr class='h3'></tr>"+
				 "<tr class='h3'>"+
				 	"<td align='left' class='h3' width='20%'>[ID] :: Product Name</td>"+
				 	"<td align='left' class='h3' width='10%'>Quantity</td>"+
				 	"<td align='left' class='h3' width='20%'>[ID] :: Product Name</td>"+
				 	"<td align='left' class='h3' width='10%'>Quantity</td>"+
				 "</tr>"
		title += "</table>"
		
		message += "<form action='addItemsToCustRequest' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=700 height=500>" ; 		
		
			//message += "<br/><br/>";
			
			for (i = 0; i < (prodList.length); ++i) {
					
					message += "<tr class='h3'>"+
				             	 "<td align='left' class='h3' width='20%'>"+"["+prodList[i]+"]  ::  "+nameList[i]+"</td>"+
				             	 "<td align='left' width='10%'><input class='h3' type='text' id ="+"qty_"+prodList[i]+" name="+prodList[i]+" size='5'/></td>"
				    i++;   
				          	 
				    if(i < prodList.length){ 
				   		 message +=  "<td align='left' class='h3' width='20%' >"+"["+prodList[i]+"]  ::  "+nameList[i]+"</td>"+
				         		      "<td align='left' width='10%'><input class='h3' type='text' id ="+"qty_"+prodList[i]+" name="+prodList[i]+" size='5'/></td>"
				    } 
			}
			message += "<td><input name='custRequestId' type='hidden' value="+custRequestId+"></input></td>"+
					   "</tr>";
			message += "<tr class='h3'>"+
							"<td align='center' width='10%' class='h3'><span align='right'><input type='submit' class='styled-button' value='Add Items' id='addItems'/></span></td>"+
							"<td align='center' width='10%'></td>"+
							"<td width='10%' align='center' class='h3'><span align='right'><button class='styled-button' value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallbutton'>${uiLabelMap.CommonCancel}</button></span></td>"+
					   "</tr>";
			
		message += "</table></form>";				
		
		Alert(message, title);
	};	

</script>
