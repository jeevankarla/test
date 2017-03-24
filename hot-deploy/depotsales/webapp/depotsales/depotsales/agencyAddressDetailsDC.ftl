<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>





<script type="application/javascript">

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
				//getAllIndentRejects();
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
		function disableButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){	
		return false;
	}

	function cancelForm(){		 
		return false;
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");		  	
	}
function submitForm(){
cancelForm(); 		 
		 jQuery('#agencyAddress').submit();
		  
	}



function showAgencyAddress(orderId){
	  var action;
     var message = "";

                message += "<html><head></head><body><form id='agencyAddress' target='_blank'  name='agencyAddress'method='post' action='PurchaseOrderViewDepotSalesDC.pdf' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'><font size=45%>Include User Agency Address:</font> </td><td align='left'  width='90%'><input type='checkbox' name='agencyAddress' id='agencyAddress'/><input class='h4' type='hidden' id='orderId' name='orderId' value='"+orderId+"' /></td></tr>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'><font size=45%></font><input type='radio' id='docType' name='docType' value='Original'>Original </td><td align='left'  width='90%'><input type='radio' id='docType' name='docType' value='Duplicate'>Duplicate</td></tr>";
     		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='newtab' onclick='return submitForm();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
			    message +=	"</table></form></body></html>";
	var title = "Agency Address Check  orderId:"+orderId+"";
    Alert(message, title);
};


</script>







