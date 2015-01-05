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

   function toggleLoanId(master) {
        var loans = jQuery("#employeeLoanList :checkbox[name='loanIds']");
        jQuery.each(loans, function() {
            this.checked = master.checked;
        });
    }
    var loans = jQuery("#employeeLoanList :checkbox[name='loans']");
    // check if any checkbox is checked
    var anyChecked = false;
    jQuery.each(loans, function() {
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
	
	function loanDisbursement(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var index = 0;
    	var employeeLoanList = jQuery("#employeeLoanList :checkbox[name='loanIds']");
    	var appendStr = "<table id=parameters>";
        jQuery.each(employeeLoanList, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var amtObj = $(domObj).find("#amount");
                var partyIdObj = $(domObj).find("#partyId");
            	
            	var loanId = $(this).val();
            	var partyId = $(partyIdObj).val();
            	var amount = $(amtObj).val();
            	
            	
            	appendStr += "<tr><td><input type=hidden name=loanId id=loanId value="+loanId+" />";
				appendStr += "<input type=hidden name=partyId id=partyId value="+partyId+" />";
            	appendStr += "<input type=hidden name=amount id=amount value="+amount+" /></td></tr>";
                
            }
            index = index+1;
            
        });
        appendStr += "</table>";
        $("#paymentSubmitForm").append(appendStr);
        
        var form = $("#paymentSubmitForm");
        createLoandisbursement(form);
    }
	

</script>
<#if employeeLoanList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>
<form name="paymentSubmitForm" id="paymentSubmitForm" method="post" action="createLoandisbursement">
</form>
  <form name="employeeLoanList" id="employeeLoanList"  method="post" action="">
    <div align="right">
    <input id="paymentButton" type="button"  onclick="javascript:loanDisbursement(this);" value="Make Loan Disbursement" />
	 </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Loan Id</td>
          <td>Employee</td>
          <td>Employee Name</td>
          <td>Loan Type</td>
          <td>Status</td>
          <td>ExtLoanRefNum</td>
          <td>Prncpl.Amt</td>
          <td>Interest Amt</td>
          <td>Interest Inst</td>
          <td>Prin Inst</td>
          <td>Created By</td>
          <td>Created Date</td> 
          <#--<td>Voucher</td>-->
          <td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllLoans" name="checkAllLoans" onchange="javascript:toggleLoanId(this);"/></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list employeeLoanList as employeeLoan>
        	<#assign loanType = delegator.findOne("LoanType", {"loanTypeId" : employeeLoan.loanTypeId}, true) />
        	<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : employeeLoan.statusId}, true) />
        	
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
			  <input type = "hidden" name = "loanId" id = "loanId" value = "${employeeLoan.loanId}">
        	  <input type = "hidden" name = "amount" id = "amount" value = "${employeeLoan.principalAmount}">
        	  <input type = "hidden" name = "partyId" id = "partyId" value = "${employeeLoan.partyId}">
        	
              <td>${(employeeLoan.loanId)?if_exists}</td>
              <td>${(employeeLoan.partyId)?if_exists}</td>
              <td>${(employeeLoan.partyName)?if_exists}</td>
              <td>${(loanType.description)?if_exists}</td>
              <td>${(statusItem.description)?if_exists}</td>
              <td>${(employeeLoan.extLoanRefNum)?if_exists}</td>
              <td>${(employeeLoan.principalAmount)?if_exists}</td>
              <td>${(employeeLoan.interestAmount)?if_exists}</td>
              <td>${(employeeLoan.numInterestInst)?if_exists}</td>
              <td>${(employeeLoan.numPrincipalInst)?if_exists}</td>
              <td>${(employeeLoan.createdByUserLogin)?if_exists}</td>
              <td>${(employeeLoan.createdDate)?if_exists}</td>
              <#--<td><a class="buttontext" target="_BLANK" href="<@ofbizUrl>printDepositReport.pdf?loanId=${employeeLoan.loanId}</@ofbizUrl>">Voucher</a></td>-->
              <td align="right"><input type="checkbox" id="loanId_${employeeLoan_index}" name="loanIds" value="${employeeLoan.loanId}" /></td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Loans Found...</h3>
</#if>
