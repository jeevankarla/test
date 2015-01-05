
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
					datepick();
					$('input[name=changeComments]').focusout(function(){
	     		            appendParams();
	  
	 		         });
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
	
	function datepick()	{		
		$( "#tempAmendedDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: false,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#amendedDate" ).val(selectedDate);
			}
			
			});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		//
	}
	
	function appendParams(){
	   alert("alert");
	    $("input[name=changeComments]").val($("#changeComments").val());
	}
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	function prepareAmendPoFrom() {	
	
		var message = "";
		var title = "";
		var i=0;
		
		title += "<table cellspacing=10 cellpadding=10 width=500>"
		title += "<tr class='h3'>"+
					"<td align='center' class='h3' colspan=8><font color='black'>Amend PO : ${orderId?if_exists}</font></td>"+
				 "</tr>"
		title += "</table>"
		message += "<table cellspacing=10 cellpadding=10 width=250><tr class='h3'><td>Amend Date</td><td><input type='text' id='tempAmendedDate' name='tempAmendedDate' onclick='javascript:datepick()'/></td></tr>"+		
		           "<tr class='h3'><td>Comments</td><td><input type='text' id='changeComments' name='changeComments'/></td></tr></table>";
		message += "<br/><br/>";
		message += "<form action='amendPOItemEvent' method='post' onsubmit='return disableGenerateButton();' required><table cellspacing=10 cellpadding=10 width=250>" ; 		
	    message +="<input type='hidden'  id='amendedDate' name='amendedDate'/><input type='hidden' name='changeComments'/>";
			
			message += "<tr class='h3'>"+
							"<td align='center' ><b>ProductId</br></td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center'>Quantity</td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center'>Price</td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center'>Amended Quantity</td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center'>Amended Price</td>";
			
			message +=   <#list orderItems as orderItem>
							"<tr class='h3'>"+
							"<input type='hidden' value='${orderItem.orderId}' name='orderId_o_"+${orderItem.orderItemSeqId}+"'/><input type='hidden' value='${orderItem.orderItemSeqId}' name='orderItemSeqId_o_"+${orderItem.orderItemSeqId}+"'/>"+
							"<input type='hidden' value='${orderItem.productId}' name='productId_o_"+${orderItem.orderItemSeqId}+"'/>"+
							"<td align='center' >${orderItem.productId}</td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center'>${orderItem.quantity}</td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center'>${orderItem.unitPrice}</td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center' class='h2'><input type='text'  id='amendedQuantity' name='amendedQuantity_o_"+${orderItem.orderItemSeqId}+"'  size='12'/></td>"+
				            "<td align='center'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
				            "<td align='center' class='h2'><input type='text'  id='amendedPrice' name='amendedPrice_o_"+${orderItem.orderItemSeqId}+"' size='12'/></td>"+
				            </#list>"";
			
			
			message += "<tr class='h3'>"+
							"<td align='center' width='50%' class='h3' colspan='3'></td>"+
							"<td align='center' width='10%' class='h3'><span align='right'><input type='submit' class='styled-button' value='DONE' id='DONE'/></span></td>"+
							"<td align='center' width='5%' class='h3'></td>"+
							"<td class='h3' width='10%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>CANCEL</button></span></td>"+
					   "</tr>";
			
		message += "</table></form>";				
		
		Alert(message, title);
	};	

</script>
