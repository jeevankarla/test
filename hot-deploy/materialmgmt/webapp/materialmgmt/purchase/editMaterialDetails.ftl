
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
	
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	

	function showUpdateProductForm(productId,productName,longDescription,uomId) {	

		var message = "";
		message += "<div style='width:100%;height:200px;overflow-x:auto;overflow-y:auto;' ><form action='updateProductDetails' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10  width='100%' > " ; 
			 message += "<tr ><td align='right' class='h2' width='15%' >Specification: </td><td align='left'  width='75%'  > <input type='text' size='60' class='h2' id='longDescription' name='longDescription'  value='"+longDescription+"'/><input type='hidden' name='productId'  value='"+productId+"'/></td></tr>";
			 message += "<tr ><td align='right' class='h2' width='15%' >Product Name: </td><td align='left'  width='75%'  > <input type='text' size='60' class='h2' id='productName' name='description'  value='"+productName+"'/></td></tr>";
			message +="<tr ><td align='right' class='h2' width='15%' >Uom Id: </td><td align='left' width='60%'><select name='quantityUomId' id='quantityUomId' class='h3'>"+
              		"<#list uomList as uom><option value='${uom.uomId}' >${uom.description}[${uom.abbreviation}]</option></#list>"+            
					"</select></td></tr>";
	          message +="<tr ><td align='right' class='h2' width='15%' ><span align='center'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td><td width='20%' align='center' class='h3' ><input type='submit' value='Update' id='updateProduct' class='smallSubmit'/></td></tr>";
	          message += "</table></form>";
	      	
		message += "</div>";				
		var title = "<center  class='h2' >Update Material <center> ";
		Alert(message, title);
	};
	
	function populateDate(){
	};
</script>
