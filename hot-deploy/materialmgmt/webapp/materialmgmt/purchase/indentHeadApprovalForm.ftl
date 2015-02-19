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
	function submitForm(){	
	
     jQuery('#ListIndentSubmit').submit();
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}

function getdescription(){
 var message = "";
      			message += "<html><head></head><body><form id='indentstatuschange' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
      			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>";
     		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='return submitForm();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
			    message +=	"</table></form></body></html>";
	var title = "Description";
    Alert(message, title);	
}

function checkAllIndentApprovalStatus(master){
	 var shipIds = $('input[name="IndentHeadApproval"]');
    jQuery.each(shipIds, function() {
         this.checked = master.checked;
     });
 }

function getAllIndentApprovals(){
	 var custRqstList = $('input[name=IndentHeadApproval]:checked');
	 if(custRqstList.size() <=0) {
	 alert("Please Select at least One Indent..from Approval!")
		 return false;
	 }
	 var paramMap='finallist';
	 var finallist=[];
	 var index = 0;
	 var makeMassApproval = 'makeMassApproval';	
	 var action;
     jQuery.each(custRqstList, function() {
   	 var curreElem = $(this);
 	 var varform = curreElem.parent().parent();
 	 var form = curreElem.parent().parent().find("form");
 	 var formId = form.attr('id');
 	 var str = "#"+formId;
 	 var tempQtyObj=$(varform).find("[name='"+"tempQty"+"']");
 	 var qty=$(tempQtyObj).val();
 	 var tempQty=qty.replace(/[^0-9]/g,'');
   	 var custRqst=$(this).val();
     var inputElementIdSplit = custRqst.split('_');
   	 var statusId="CRQ_SUBMITTED";   
   	 var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
        appendStr += "<input type=hidden name=description_o_"+index+"  value="+inputElementIdSplit[3]+" />";
        appendStr += "<input type=hidden name=quantity_o_"+index+"  value="+tempQty+" />";
        appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
        appendStr += "<input type=hidden name=statusId_o_"+index+"  value="+statusId+" />";
 	$("#ListIndentSubmit").append(appendStr);
 	index = index+1;
    });
     action= makeMassApproval;
     jQuery('#ListIndentSubmit').attr("action", action);
     jQuery('#ListIndentSubmit').submit();
}


function getAllIndentRejects(){
	 var custRqstList = $('input[name=IndentHeadApproval]:checked');
	 if(custRqstList.size() <=0) {
	 alert("Please Select at least One Indent..!")
		 return false;
	 }
	  
	  var paramMap='finallist';
	  var finallist=[];
	  var index = 0;
	  var makeMassReject = 'makeMassReject';	
	  var action;
      jQuery.each(custRqstList, function() {
   	  var curreElem = $(this);
 	  var varform = curreElem.parent().parent();
 	  var form = curreElem.parent().parent().find("form");
 	  var formId = form.attr('id');
 	  var str = "#"+formId;
 	  var tempQtyObj=$(varform).find("[name='"+"tempQty"+"']");
 	  var qty=$(tempQtyObj).val();
 	  var tempQty=qty.replace(/[^0-9]/g,'');
   	  var custRqst=$(this).val();
      var inputElementIdSplit = custRqst.split('_');

   	  var statusId="CRQ_REJECTED";
   	  var appendStr = "<input type=hidden name=custRequestId_o_"+index+" value="+inputElementIdSplit[0]+" />";
          appendStr += "<input type=hidden name=description_o_"+index+"  value="+inputElementIdSplit[3]+" />";
          appendStr += "<input type=hidden name=quantity_o_"+index+"  value="+tempQty+" />";
          appendStr += "<input type=hidden name=custRequestItemSeqId_o_"+index+"  value="+inputElementIdSplit[1]+" />";
          appendStr += "<input type=hidden name=statusId_o_"+index+"  value="+statusId+" />";
      $("#ListIndentSubmit").append(appendStr);
 	  index = index+1;
    });
    action= makeMassReject;
    jQuery('#ListIndentSubmit').attr("action", action);
    jQuery('#ListIndentSubmit').submit();
}

</script>

  <form name="submitBulkStatus" id="ListIndentSubmit"  method="post" align="right">

   <td align="right" >    
      <input id="submitButton" type="button"  onclick="javascript:getAllIndentApprovals();" value="Accept"/>
      <input type="checkbox" id="bulkCheckBox" name="submitBulkStatus" onchange="javascript:checkAllIndentApprovalStatus(this);"/>   
      
  </td>
    
 <#-- </form>
   <form name="submitBulkRejectStatus" id="ListIndentReject"  method="post" action="makeMassReject">-->

  </form>

