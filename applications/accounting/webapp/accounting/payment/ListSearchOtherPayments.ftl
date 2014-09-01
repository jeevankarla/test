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
<script type="text/javascript">
//<![CDATA[

    function togglePaymentId(master) {
        var payments = jQuery("#listPayments :checkbox[name='paymentIds']");

        jQuery.each(payments, function() {
            this.checked = master.checked;
        });
        getInvoiceRunningTotal();
    }

    function getInvoiceRunningTotal() {
		var checkedInvoices = jQuery("input[name='invoiceIds']:checked");
        if(checkedInvoices.size() > 0) {
            jQuery.ajax({
                url: 'getInvoiceRunningTotal',
                type: 'POST',
                async: true,
                data: jQuery('#listInvoices').serialize(),
                success: function(data) { jQuery('#showInvoiceRunningTotal').html(data.invoiceRunningTotal + '  (' + checkedInvoices.size() + ')') }
            });

            if(jQuery('#serviceName').val() != "") {
            	jQuery('#submitButton').removeAttr('disabled');                
            }

        } else {
            jQuery('#submitButton').attr('disabled', 'disabled');
            jQuery('#showInvoiceRunningTotal').html("");
        }
    }

    function setServiceName(selection) {
        jQuery('#submitButton').attr('disabled' , 'disabled');    
        if ( selection.value == 'massPaymentsToSent' || selection.value == 'massPaymentsToCancel' || selection.value == 'massPaymentsToVoid' || selection.value == 'massPaymentsToReceived') {
            jQuery('#listPayments').attr('action', jQuery('#paymentStatusChange').val());
        } else {
            jQuery('#listPayments').attr('action', selection.value);
        }

        if (selection.value == 'massPaymentsToSent') {
            jQuery('#statusId').val("PMNT_SENT");
        } else if (selection.value == 'massPaymentsToCancel') {
            jQuery('#statusId').val("PMNT_CANCELLED");
        } else if (selection.value == 'massPaymentsToVoid') {
            jQuery('#statusId').val("PMNT_VOID");
        } else if (selection.value == 'massPaymentsToReceived') {
            jQuery('#statusId').val("PMNT_RECEIVED");
        }         

        var payments = jQuery("#listPayments :checkbox[name='paymentIds']");
        // check if any checkbox is checked
        var anyChecked = false;
        jQuery.each(payments, function() {
            if (jQuery(this).is(':checked')) {
                anyChecked = true;
                return false;
            }
        });

        if(anyChecked && (jQuery('#serviceName').val() != "")) {
            jQuery('#submitButton').removeAttr('disabled');
        }
    }
//]]>

function setVoidPaymentParameters(currentPayment){
      
    	jQuery(currentPayment).attr( "disabled", "disabled");
    	var currentEle = jQuery(currentPayment);
    	formName=document.forms['cancelPayment'];
    	var domObj = $(currentEle).parent().parent();
        var rowObj = $(domObj).html();
        var method = $(domObj).find("#paymentMethodTypeId");
        var payment = $(domObj).find("#paymentId");
        var methodValue = $(method).val();
        var payId = $(payment).val(); 
         var ifCancel = confirm("Are You Sure To Cancel ReceiptId:"+payId);
		   if(!ifCancel){
		location.reload(true);
		   return false;
		   }
        var appendStr = "<input type=hidden name=paymentMethodTypeId value="+methodValue+" />";  
        $("#cancelPayment").append(appendStr);  
    	appendStr = "<input type=hidden name=paymentId value="+payId+" />";    		
	    $("#cancelPayment").append(appendStr); 
	    $("#cancelPayment").submit();	
    
    }
</script>
<#if payments?has_content>
  <#assign paymentList  =  payments.getCompleteList() />
  <#assign eliClose = payments.close() />
</#if>
<#if paymentList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>
  <div>
    <span class="label">Total Payments :${paymentList?size}</span>  
  </div>
  
  <#if isCashierPortalScreen?has_content>
    <form name="cancelPayment" id="cancelPayment"  method="post" action="voidCashPayment">
  <#else>
   <form name="cancelPayment" id="cancelPayment"  method="post" action="voidPayment">
  </#if>
 
  </form>
  <form name="listPayments" id="listPayments"  method="post" action="">
    <div align="right">
   <!--   <select name="serviceName" id="serviceName" onchange="javascript:setServiceName(this);">
        <option value="">${uiLabelMap.AccountingSelectAction}</option>
        <option value="<@ofbizUrl>PrintPayments</@ofbizUrl>">Print Payments</option>
        <option value="massPaymentsToSent">Status To 'Sent'</option>
        <option value="massPaymentsToVoid">Status To 'Void'</option>
        <option value="massPaymentsToCancel">Status To 'Cancelled'</option>
        <option value="massPaymentsToReceived">Status To 'Received'</option>
      </select>
      <input id="submitButton" type="button"  onclick="javascript:jQuery('#listPayments').submit();" value="${uiLabelMap.CommonRun}" disabled="disabled" /> -->
      <input type="hidden" name="organizationPartyId" value="${defaultOrganizationPartyId}"/>
      <input type="hidden" name="partyIdFrom" value="${parameters.partyIdFrom?if_exists}"/>
      <input type="hidden" name="statusId" id="statusId" value="${parameters.statusId?if_exists}"/>
      <input type="hidden" name="fromInvoiceDate" value="${parameters.fromInvoiceDate?if_exists}"/>
      <input type="hidden" name="thruInvoiceDate" value="${parameters.thruInvoiceDate?if_exists}"/>
      <input type="hidden" name="fromDueDate" value="${parameters.fromDueDate?if_exists}"/>
      <input type="hidden" name="thruDueDate" value="${parameters.thruDueDate?if_exists}"/>
      <input type="hidden" name="paymentStatusChange" id="paymentStatusChange" value="<@ofbizUrl>massChangePaymentStatus</@ofbizUrl>"/>
    </div>

    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>paymentId</td>
          <td>Payment Date</td>    
          <td>Facility</td>                          
          <td>${uiLabelMap.AccountingPaymentType}</td>
          <td>Payment Method</td>
          <td>${uiLabelMap.CommonStatus}</td>
          <td>Channel</td> 
          <td>${uiLabelMap.AccountingFromParty}</td> 
          <#--<td>${uiLabelMap.AccountingToParty}</td> -->
          <td>Effective Date</td>
          <td>Amount</td> 
          <td>Amt To Apply</td> 
         <#if hasPaymentCancelPermission?has_content && nowDate?has_content>
          <td>Cancel</td> 
           </#if>
          <td>PrintReceipt</td>
          <#-- <td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllPayments" name="checkAllPayments" onchange="javascript:togglePaymentId(this);"/></td> -->
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list paymentList as payment>
        	<#assign amountToApply = Static["org.ofbiz.accounting.payment.PaymentWorker"].getPaymentNotApplied(delegator, payment.paymentId)>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><a class="buttontext" href="<@ofbizUrl>paymentOverview?paymentId=${payment.paymentId}</@ofbizUrl>">${payment.get("paymentId")}</a>
              <input type="hidden" name="paymentId" id="paymentId" value="${payment.paymentId?if_exists}">
              </td>
              <td><#if payment.paymentDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.paymentDate ,"dd/MM/yyyy HH:mm:ss")}</#if></td>              
              <td>${(payment.facilityId)?if_exists}</td>
              <td>
                <#assign paymentType = delegator.findOne("PaymentType", {"paymentTypeId" : payment.paymentTypeId}, true) />
                ${paymentType.description?default(payment.paymentTypeId)}
              </td>
              <td>
              	<input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="${payment.paymentMethodTypeId?if_exists}"> 
                <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true) />
                ${paymentMethodType.description?default(payment.paymentMethodTypeId)}
              </td>              
              <td>
                <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : payment.statusId}, true) />
                ${statusItem.description?default(payment.statusId)}
              </td>
              <td>
                <#assign channelType = delegator.findOne("Enumeration", {"enumId" : payment.paymentPurposeType?if_exists}, true) />
                ${channelType.description}
              </td>
             <td><a href="/partymgr/control/viewprofile?partyId=${payment.partyIdFrom}">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, payment.partyIdFrom, false)?if_exists} [${(payment.partyIdFrom)?if_exists}] </a></td> 
             <#-- <td><a href="/partymgr/control/viewprofile?partyId=${payment.partyIdTo}">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, payment.partyIdTo, false)?if_exists} [${(payment.partyIdTo)?if_exists}]</a></td>-->
              <td><#if payment.effectiveDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.effectiveDate ,"dd/MM/yyyy")}</#if></td>              
              <td><@ofbizCurrency amount=payment.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><@ofbizCurrency amount=amountToApply isoCode=defaultOrganizationPartyCurrencyUomId/></td>
             
              <#if hasPaymentCancelPermission?has_content && nowDate?has_content && payment.effectiveDate?has_content>
              <#assign paymentDateCompare= Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.effectiveDate ,"yyyy-MM-dd")>
              <td>
              <#if nowDate==paymentDateCompare  && payment.statusId!="PMNT_VOID">
              <input id="submitButton" type="button"  onclick="javascript:return setVoidPaymentParameters(this);" value="Cancel"/>  
              </#if>
              </td>
               </#if>
             
              <td>
              	<a target="_blank" class="buttontext" href="<@ofbizUrl>printReceipt.pdf?paymentIds=${payment.paymentId}</@ofbizUrl>" >
              		PrintReceipt
              	</a>
              </td>
              
             <#--  <td align="right"><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId}" onclick="javascript:getInvoiceRunningTotal();"/></td> -->
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>${uiLabelMap.AccountingNoInvoicesFound}</h3>
</#if>
