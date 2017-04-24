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


<script type="text/javascript">

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
	//endof qtip;

   function togglecustRequestId(master) {
        var custRequestIds = jQuery("#custRequestList :checkbox[name='custRequestIds']");
        jQuery.each(custRequestIds, function() {
            this.checked = master.checked;
        });
    }
    var custRequestIds = jQuery("#custRequestList :checkbox[name='custRequestIds']");
    // check if any checkbox is checked
    var anyChecked = false;
    jQuery.each(custRequestIds, function() {
        if (jQuery(this).is(':checked')) {
            anyChecked = true;
            return false;
        }
    });

    if(anyChecked && (jQuery('#serviceName').val() != "")) {
        jQuery('#submitButton').removeAttr('disabled');
    }

function cancelForm(){		 
		return false;
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	function custRequestIdDisbursement(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var index = 0;
    	var custRequestList = jQuery("#custRequestList :checkbox[name='custRequestIds']");
  
    	var appendStr = "<table id=parameters>";
        jQuery.each(custRequestList, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var amtObj = $(domObj).find("#amount");
                var partyIdObj = $(domObj).find("#partyId");
            	
            	var custRequestId = $(this).val();
            	var partyId = $(partyIdObj).val();
            	var amount = $(amtObj).val();
            	
            	
            	appendStr += "<tr><td><input type=hidden name=custRequestId id=custRequestId value="+custRequestId+" />";
				appendStr += "<input type=hidden name=partyId id=partyId value="+partyId+" />";
            	appendStr += "<input type=hidden name=amount id=amount value="+amount+" /></td></tr>";
                
            }
            index = index+1;
            
        });
        appendStr += "</table>";
        $("#paymentSubmitForm").append(appendStr);
        
        var form = $("#paymentSubmitForm");
        createdisbursement(form);
    }
	

</script>
<#if custRequestList?has_content>
<form name="paymentSubmitForm" id="paymentSubmitForm" method="post" action="createEmpAdvDisbursement">
</form>
  <form name="custRequestList" id="custRequestList"  method="post" action="">
    <div align="right">
    <input id="paymentButton" type="button"  onclick="javascript:custRequestIdDisbursement(this);" value="Make Disbursement" />
	 </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>CustRequest Id</td>
          <td>Employee</td>
          <td>Account Type</td>
          <td>FinAccountTransType</td>
          <td>Status</td>
          <td>AccParentTypeId</td>
          <td>Amount</td>
          <td>FinAccountParentId</td>
          <td>SegmentId</td>
          <td>CostCenterId</td>
          <td>ReferenceNumber</td>
          <td>CustRequestDate</td>
          <td>Reason</td> 
           <td>Cancel</td>
          <#--<td>Voucher</td>-->
          <td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllcustRequestIds" name="checkAllcustRequestIds" onchange="javascript:togglecustRequestId(this);"/></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        
        	 <#list custRequestList as cust>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
			  <input type = "hidden" name = "custRequestId" id = "custRequestId" value = "${cust.custRequestId}">
        	  <input type = "hidden" name = "amount" id = "amount" value = "${cust.amount}">
        	  <input type = "hidden" name = "partyId" id = "partyId" value = "${cust.fromPartyId}">
        	
              <td>${(cust.custRequestId)?if_exists}</td>
              <td>${(cust.fromPartyId)?if_exists}</td>
              <td>${(cust.finAccountTypeId)?if_exists}</td>
              <td>${(cust.finAccountTransTypeId)?if_exists}</td>
              <td>${(cust.finstatusId)?if_exists}</td>
              <td>${(cust.accParentTypeId)?if_exists}</td>
              <td>${(cust.amount)?if_exists}</td>
              <td>${(cust.finAccountParentId)?if_exists}</td>
              <td>${(cust.segmentId)?if_exists}</td>
              <td>${(cust.costCenterId)?if_exists}</td>
              <td>${(cust.referenceNumber)?if_exists}</td>
              <td>${(cust.custRequestDate)?if_exists}</td>
              <td>${(cust.reason)?if_exists}</td>
              <td>
              <a href="<@ofbizUrl>cancelRequest?custRequestId=${cust.custRequestId?if_exists}</@ofbizUrl>">Cancel</a>
            </td>
              <td align="right"><input type="checkbox" id="custRequestId_${employeecustRequestId_index}" name="custRequestIds" value="${cust.custRequestId}" /></td>
            </tr>
            </#list>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Records Found...</h3>
</#if>
