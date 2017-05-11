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

<script language="JavaScript" type="text/javascript">

function togglefinAccountTransId(master) {
    var form = document.selectAllForm;
    var finAccountTransList = form.elements.length;
    for (var i = 0; i < finAccountTransList; i++) {
        var element = form.elements[i];
        if (element.type == "checkbox") {
            element.checked = master.checked;
        }
    }
    getFinAccountTransRunningTotalAndBalances();
    
}

function calculateTotal(){
    var invoices = jQuery("#listFinAccTra :checkbox[name='finAccntTransIds']");
 	 total=0;
    jQuery.each(invoices, function() {
        if (jQuery(this).is(':checked')) {
        	var domObj = $(this).parent().parent();
        	var amtObj = $(domObj).find("#amt");
        	var amt = $(amtObj).val();
        	 total = (+total) + (+amt);
        }
        
    });
    jQuery('#finTransRunningTotal').html(total);
}

function getFinAccountTransRunningTotalAndBalances() {

    var isSingle = true;
    var isAllSelected = true;
    var finAccountTxs = jQuery('input[name^="_rowSubmit_o_"]');  
    jQuery.each(finAccountTxs, function() {
    	if (jQuery(this).is(':checked')) {
        	isSingle = false;
        } else {
        	isAllSelected = false;
        }
    });
              
    if (isAllSelected) {
        jQuery('#checkAllTransactions').attr('checked', true);
    } else {
        jQuery('#checkAllTransactions').attr('checked', false);
    }
    
    if (!isSingle) {
        jQuery('#submitButton').removeAttr('disabled');
        if (jQuery('#showFinAccountTransRunningTotal').length) {
      
            jQuery.ajax({
                url: 'getFinAccountTransRunningTotalAndBalances',
                async: false,
                type: 'POST',
                data: jQuery('#listFinAccTra').serialize(),
                success: function(data) {
                    jQuery('#showFinAccountTransRunningTotal').html(data.finAccountTransRunningTotal);
                    jQuery('#finAccountTransRunningTotal').html(data.finAccountTransRunningTotal);
                    jQuery('#numberOfFinAccountTransaction').html(data.numberOfTransactions);
                    jQuery('#endingBalance').html(data.endingBalance);
                }
            });
        }
    } else {
        if (jQuery('#showFinAccountTransRunningTotal').length) {
            jQuery('#showFinAccountTransRunningTotal').html("");
            jQuery('#finAccountTransRunningTotal').html("");
            jQuery('#numberOfFinAccountTransaction').html("");
            jQuery('#endingBalance').html(jQuery('#endingBalanceInput').val());

        }
        jQuery('#submitButton').removeAttr('disabled');        
    }
}

function toggleFinAccntTransId(master) {
        var finTransactions = jQuery("#listFinAccTra :checkbox[name='finAccntTransIds']");
       
        jQuery.each(finTransactions, function() {
            this.checked = master.checked;
        });
    }
    function toggleFinTransIdForReconsile(master) {
        var finTransactions = jQuery("#listFinAccTra :checkbox[name='finAccntTransIds']");
        
        jQuery.each(finTransactions, function() {
            this.checked = master.checked;
        });
         calculateTotal()
    }
    
    function getFinAccountTransInfo() {
    var finAccountTansList=[];
        $('.chkFinTransId:checked').each(function() {
        var finAccountTrnsId=$(this).val();
        $('#massCancelFinTrans').append('<input type="hidden" name="finAccountTransIds" value="'+finAccountTrnsId+'" />');
        });
        }
    function appendFinTransToReconsileForm() {// for Recinsilation Automation
    var finAccountTansList=[];
    var finTransSize=0;
        $('.chkRecFinTransId:checked').each(function() {
        finTransSize=finTransSize+1;
        var finAccountTrnsId=$(this).val();
        $('#newFinTransReconsileId').append('<input type="hidden" name="finAccountTransIds" value="'+finAccountTrnsId+'" />');
        });
        
        if(finTransSize<=0){
        alert("Bank Transactions Not Selected To Reconciled  !");
        return false;
        }
       
        }
        
        $(document).ready(function(){
        		$("#realisationDate_i18n").attr("required","required")
        		
         });
	function datePick()
	{
	$(document).ready(function(){
	
	//for date Picker 
	makeDatePicker("remitDate","thruDate");
	makeDatePicker("onlineFromDate","thruDate");
	makeDatePicker("onlineThruDate","thruDate");
	});

	function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: true,
			numberOfMonths: 1,
			
			maxDate:0,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	}
	function setFormParams(){
		$("#onlineFromDate").datepicker( "option", "dateFormat", "yy-mm-dd 00:00:00");
	}
	
}


</script>

<div class="screenlet screenlet-body">
  <#if finAccountTransList?has_content && parameters.noConditionFind?exists && parameters.noConditionFind == 'Y'>
    <#if !grandTotal?exists>
      <div>
        <span class="label">${uiLabelMap.AccountingRunningTotal} :</span>
        <span class="label" id="showFinAccountTransRunningTotal"></span>
      </div>
    </#if>
    <div align="center" >
        <span class="label" font-size="25pt">Selected Total Amount :</span>
        <span class="label" id="finTransRunningTotal"></span>
     </div>
    <#--
   <form name="massCancelFinTrans" id="massCancelFinTrans"  method="post" action="setMassFinAccountTransStatus">
      <div align="right">
       <input type="submit" value="Bulk Cancel"  class="buttontext" id="MassCancaltionId" name="MassCancaltionName" onclick="javascript:getFinAccountTransInfo();" />
        <input name="statusId" type="hidden" value="FINACT_TRNS_CANCELED"/>
         <input name="finAccountId" type="hidden" value="${parameters.finAccountId}"/>
       </div>
     </form>-->
     
   <form name="newFinTransReconsile" id="newFinTransReconsileId"  method="post" action="createReconsileToFinAccountTrans">
      <div align="right">
     <b>Bank Realisation Date<input class='h4' type='text' readonly id='onlineFromDate' name='Bank Realisation Date' onmouseover='datePick()'/>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>
       <input type="submit" value="Create Reconcile"  class="buttontext" id="bulkReconsileId" name="bulkReconsileName" onclick="javascript:return appendFinTransToReconsileForm();" />
         <input name="statusId" type="hidden" value="FINACT_TRNS_APPROVED"/>
         <input name="finAccountId" type="hidden" value="${parameters.finAccountId}"/>
         <input name="organizationPartyId" type="hidden" value="${defaultOrganizationPartyId}"/>
       </div>
     </form>
    <form id="listFinAccTra" name="selectAllForm" method="post" action="<@ofbizUrl><#if !grandTotal?exists>reconcileFinAccountTrans?clearAll=Y<#else>assignGlRecToFinAccTrans?clearAll=Y</#if></@ofbizUrl>">
      <input name="_useRowSubmit" type="hidden" value="Y"/>
      <input name="finAccountId" type="hidden" value="${parameters.finAccountId}"/>
      <input name="statusId" type="hidden" value="${parameters.statusId?if_exists}"/>
      <#if !grandTotal?exists>
        <input name="reconciledBalance" type="hidden" value="${(glReconciliation.reconciledBalance)?if_exists}"/>
        <input name="reconciledBalanceWithUom" type="hidden" id="reconciledBalanceWithUom" value="<@ofbizCurrency amount=(glReconciliation.reconciledBalance)?default('0')/>"/>
      </#if>
      
      <table class="basic-table hover-bar" cellspacing="0">
        <#-- Header Begins -->
        <tr class="header-row-2">
          <th>${uiLabelMap.FormFieldTitle_finAccountTransId}</th>
          <th>${uiLabelMap.FormFieldTitle_finAccountTransTypeId}</th>
          <th>${uiLabelMap.PartyParty}</th>
           <th>PaymentParty</th>
          <#--<th>${uiLabelMap.FinAccountReconciliationName}</th>-->
          <th>${uiLabelMap.FormFieldTitle_transactionDate}</th>
          <th>InstrumentNo.</th>
          <#--<th>${uiLabelMap.FormFieldTitle_entryDate}</th>-->
          <th>${uiLabelMap.CommonAmount}</th>
          <th>${uiLabelMap.FormFieldTitle_paymentId}</th>
          <th>${uiLabelMap.OrderPaymentType}</th>
          <th>${uiLabelMap.FormFieldTitle_paymentMethodTypeId}</th>
          <th>${uiLabelMap.CommonStatus}</th>
          <th>${uiLabelMap.CommonComments}</th>
          <#-- 
          <#if grandTotal?exists>
            <th>${uiLabelMap.AccountingCancelTransactionStatus}</th>
          </#if>-->
          <#if !grandTotal?exists>
            <#if (parameters.glReconciliationId?has_content && parameters.glReconciliationId != "_NA_")>
              <th>${uiLabelMap.AccountingRemoveFromGlReconciliation}</th>
            </#if>
          </#if>
           <#--
          <#if ((glReconciliationId?has_content && glReconciliationId == "_NA_") && (glReconciliations?has_content && finAccountTransList?has_content)) || !grandTotal?exists>
            <th>${uiLabelMap.CommonSelectAll} <input name="selectAll" type="checkbox" value="N" id="checkAllTransactions" onclick="javascript:togglefinAccountTransId(this);"/></th>
          </#if>-->
           
           <th align="right">${uiLabelMap.CommonSelectAll} 
           <input type="checkbox"  name="finAccountTransIdsList" onchange="javascript:toggleFinTransIdForReconsile(this);" />
           </th>
          <#--
           <th align="right">${uiLabelMap.CommonSelectAll} 
           <input type="checkbox" id="checkAllInvoices" name="finAccountTransIdsList" onchange="javascript:toggleFinAccntTransId(this);" />
           </th>-->
        </tr>
        <#-- Header Ends-->
        <#assign alt_row = false>
        <#list finAccountTransList as finAccountTrans>
          <#assign payment = "">
          <#assign payments = "">
          <#assign status = "">
          <#assign paymentType = "">
          <#assign paymentMethodType = "">
          <#assign glReconciliation = "">
          <#assign partyName = "">
           <#assign paymentPartyName = "">
           <#assign paymentPartyId = "">
           <#assign paymentRefNum = "">
          <#assign companyCheck = ""/>
          <#if finAccountTrans.paymentId?has_content>
            <#assign payment = delegator.findOne("Payment", {"paymentId" : finAccountTrans.paymentId}, true)>
          <#else>
            <#assign payments = delegator.findByAnd("Payment", {"finAccountTransId" : finAccountTrans.finAccountTransId})>
          </#if>
          <#assign finAccountTransType = delegator.findOne("FinAccountTransType", {"finAccountTransTypeId" : finAccountTrans.finAccountTransTypeId}, true)>
          <#if finAccountTrans.statusId?has_content>
            <#assign status = delegator.findOne("StatusItem", {"statusId" : finAccountTrans.statusId}, true)>
          </#if>
          <#if payment?has_content && payment.partyIdTo?has_content>
            <#assign paymentType = delegator.findOne("PaymentType", {"paymentTypeId" : payment.paymentTypeId}, true)>
          </#if>
          <#if payment?has_content && finAccountTrans.finAccountTransTypeId?has_content>
	            <#if finAccountTrans.finAccountTransTypeId=="WITHDRAWAL">
		             <#assign paymentPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, payment.partyIdTo, false)>
		             <#assign paymentPartyId = payment.partyIdTo>
	            <#elseif finAccountTrans.finAccountTransTypeId=="DEPOSIT">
		             <#assign paymentPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, payment.partyIdFrom, false)>
		             <#assign paymentPartyId = payment.partyIdFrom>
	             </#if>
	       <#elseif finAccountTrans.reasonEnumId?has_content && finAccountTrans.reasonEnumId=="FATR_CONTRA" >   
	         <#assign contraFinTransEntry = (delegator.findOne("FinAccountTransAttribute", {"finAccountTransId" : finAccountTrans.finAccountTransId,"attrName" : "FATR_CONTRA"}, true))?if_exists />
	           
	           <#if contraFinTransEntry?has_content >
	                  <#assign contraFinAccountTrans = delegator.findOne("FinAccountTrans", {"finAccountTransId" : contraFinTransEntry.finAccountTransId}, false)>
	                  <#if contraFinAccountTrans?has_content && contraFinAccountTrans.finAccountId?has_content>
	                   <#assign contraFinAccount= delegator.findOne("FinAccount", {"finAccountId" :contraFinAccountTrans.finAccountId}, false)>
	                     <#assign paymentPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, contraFinAccount.ownerPartyId, false)>
			             <#assign paymentPartyId = contraFinAccount.ownerPartyId>
	                  </#if>
                </#if>
              <#assign paymentRefNum = finAccountTrans.contraRefNum?if_exists>    
          </#if>
           
          <#if payment?has_content && payment.paymentMethodTypeId?has_content>
            <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
            <#assign paymentRefNum = payment.paymentRefNum?if_exists>
          </#if>
          <#if finAccountTrans.glReconciliationId?has_content>
            <#assign glReconciliation = delegator.findOne("GlReconciliation", {"glReconciliationId" : finAccountTrans.glReconciliationId}, true)>
            <input name="openingBalance_o_${finAccountTrans_index}" type="hidden" value="${glReconciliation.openingBalance?if_exists}"/>
          </#if>
          <#if finAccountTrans.partyId?has_content>
            <#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : finAccountTrans.partyId}, true))!>
          </#if>
          <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            <td>
              <#if payments?has_content>
                <a id="togglePayment_${finAccountTrans.finAccountTransId}" href="javascript:void(0)"><img src="<@ofbizContentUrl>/images/expand.gif</@ofbizContentUrl>" alt=""/></a> ${finAccountTrans.finAccountTransId}
                <div id="displayPayments_${finAccountTrans.finAccountTransId}" style="display: none;width: 650px;">
                  <table class="basic-table hover-bar" cellspacing="0" style"width :">
                    <tr class="header-row-2">
                      <th>${uiLabelMap.AccountingDepositSlipId}</th>
                      <th>${uiLabelMap.FormFieldTitle_paymentId}</th>
                      <th>${uiLabelMap.OrderPaymentType}</th>
                      <th>${uiLabelMap.FormFieldTitle_paymentMethodTypeId}</th>
                      <th>${uiLabelMap.CommonAmount}</th>
                      <th>${uiLabelMap.PartyPartyFrom}</th>
                      <th>${uiLabelMap.PartyPartyTo}</th>
                    </tr>
                    <#list payments as payment>
                      <#if payment?exists && payment.paymentTypeId?has_content>
                        <#assign paymentType = delegator.findOne("PaymentType", {"paymentTypeId" : payment.paymentTypeId}, true)>
                      </#if>
                      <#if payment?has_content && payment.paymentMethodTypeId?has_content>
                        <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
                      </#if>
                      <#if payment?has_content>
                        <#assign paymentGroupMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(payment.getRelated("PaymentGroupMember")?if_exists) />
                        <#assign fromParty = payment.getRelatedOne("FromParty")?if_exists />
                        <#assign fromPartyName = delegator.findOne("PartyNameView", {"partyId" : fromParty.partyId}, true) />
                        <#assign toParty = payment.getRelatedOne("ToParty")?if_exists />
                        <#assign toPartyName = delegator.findOne("PartyNameView", {"partyId" : toParty.partyId}, true) />
                        <#assign toPartyDetail = toPartyName />
                        
                        <#if paymentGroupMembers?has_content>
                          <#assign paymentGroupMember = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(paymentGroupMembers) />
                        </#if>
                      </#if>
                      <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                        <td><#if paymentGroupMember?has_content><a href="<@ofbizUrl>EditDepositSlipAndMembers?paymentGroupId=${paymentGroupMember.paymentGroupId?if_exists}&amp;finAccountId=${parameters.finAccountId?if_exists}</@ofbizUrl>">${paymentGroupMember.paymentGroupId?if_exists}</a></#if></td>
                        <td><#if payment?has_content><a href="<@ofbizUrl>paymentOverview?paymentId=${payment.paymentId?if_exists}</@ofbizUrl>">${payment.paymentId?if_exists}</a></#if></td>
                        <td><#if paymentType?has_content>${paymentType.description?if_exists}</#if></td>
                        <td><#if paymentMethodType?has_content>${paymentMethodType.description?if_exists}</#if></td>
                        <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=payment.amount?if_exists/></td>
                        <td><#if fromPartyName?has_content>${fromPartyName.groupName?if_exists}${fromPartyName.firstName?if_exists} ${fromPartyName.lastName?if_exists}<a href="/partymgr/control/viewprofile?partyId=${fromPartyName.partyId?if_exists}">[${fromPartyName.partyId?if_exists}]</a></#if></td>
                        <td><#if toPartyName?has_content>${toPartyName.groupName?if_exists}${toPartyName.firstName?if_exists} ${toPartyName.lastName?if_exists}<a href="/partymgr/control/viewprofile?partyId=${toPartyName.partyId?if_exists}">[${toPartyName.partyId?if_exists}]</a></#if></td>
                      </tr>
                    </#list>
                  </table>
                </div>
                <script type="text/javascript">
                   jQuery(document).ready( function() {
                        jQuery("#displayPayments_${finAccountTrans.finAccountTransId}").dialog({autoOpen: false, modal: true,
                                buttons: {
                                '${uiLabelMap.CommonClose}': function() {
                                    jQuery(this).dialog('close');
                                    }
                                }
                           });
                   jQuery("#togglePayment_${finAccountTrans.finAccountTransId}").click(function(){jQuery("#displayPayments_${finAccountTrans.finAccountTransId}").dialog("open")});
                   });
                </script>
                <a href="<@ofbizUrl>DepositSlip.pdf?finAccountTransId=${finAccountTrans.finAccountTransId}</@ofbizUrl>" target="_BLANK" class="buttontext">${uiLabelMap.AccountingDepositSlip}</a>
              <#else>
                ${finAccountTrans.finAccountTransId}
              </#if>
            </td>
            <td>${finAccountTransType.description?if_exists}</td>
            <td><#if partyName?has_content>${(partyName.firstName)!} ${(partyName.lastName)!} ${(partyName.groupName)!}<a href="/partymgr/control/viewprofile?partyId=${partyName.partyId}">[${(partyName.partyId)!}]</a></#if></td>
            <td><#if paymentPartyName?has_content> ${paymentPartyName?if_exists}<a href="/partymgr/control/viewprofile?partyId=${paymentPartyId?if_exists}">[${paymentPartyId?if_exists}]</a></#if></td>
            <#--
            <td><#if glReconciliation?has_content>${glReconciliation.glReconciliationName?if_exists}<a href="ViewGlReconciliationWithTransaction?glReconciliationId=${glReconciliation.glReconciliationId?if_exists}&amp;finAccountId=${parameters.finAccountId?if_exists}">[${glReconciliation.glReconciliationId?if_exists}]</a></#if></td>
            -->
            <td>${finAccountTrans.transactionDate?if_exists}</td>
             <td>${paymentRefNum?if_exists}</td>
             <#--<td>${finAccountTrans.entryDate?if_exists}</td>-->
            <td>${finAccountTrans.amount?if_exists}</td>
            <input type = "hidden" name = "amt" id = "amt" value = "${finAccountTrans.amount}">
            <td>
              <#if finAccountTrans.paymentId?has_content>
                <a href="<@ofbizUrl>paymentOverview?paymentId=${finAccountTrans.paymentId}</@ofbizUrl>">${finAccountTrans.paymentId}</a>
              </#if>
            </td>
            <td><#if paymentType?has_content>${paymentType.description?if_exists}</#if></td>
            <td><#if paymentMethodType?has_content>${paymentMethodType.description?if_exists}
            		<#assign  companyCheck=paymentMethodType.paymentMethodTypeId?if_exists/>
	            </#if>
	        </td>
            <td><#if status?has_content>${status.description?if_exists}</#if></td>
             <td>${finAccountTrans.comments?if_exists}</td>
            <input name="finAccountTransId_o_${finAccountTrans_index}" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
            <input name="organizationPartyId_o_${finAccountTrans_index}" type="hidden" value="${defaultOrganizationPartyId}"/>
            <#if glReconciliationId?has_content && glReconciliationId != "_NA_">
              <input name="glReconciliationId_o_${finAccountTrans_index}" type="hidden" value="${glReconciliationId}"/>
            </#if>
             <#--
            <#if !(grandTotal?exists)>
              <#if (parameters.glReconciliationId?has_content && parameters.glReconciliationId != "_NA_")>
                <#if finAccountTrans.statusId == "FINACT_TRNS_CREATED">
                  <td><a href="javascript:document.removeFinAccountTransFromReconciliation_${finAccountTrans.finAccountTransId}.submit();" class="buttontext">${uiLabelMap.CommonRemove}</a></td>
                </#if>
              </#if>
            </#if>
            -->
              <#if finAccountTrans.statusId == "FINACT_TRNS_CREATED">
                <td align="center" >
                <#--><input id="finAccountTransId_${finAccountTrans_index}" name="_rowSubmit_o_${finAccountTrans_index}" type="checkbox" value="Y" onclick="javascript:getFinAccountTransRunningTotalAndBalances();"/>-->
                <input type="checkbox" id="finAccountTransId_${finAccountTrans.finAccountTransId}" class="chkRecFinTransId" name="finAccntTransIds" value="${finAccountTrans.finAccountTransId}" onclick="javascript:calculateTotal()" />
                </td>
              </#if>
          </tr>
          <#-- toggle the row color -->
          <#assign alt_row = !alt_row>
        </#list>
      </table>
    </form>
    
    <#list finAccountTransList as finAccountTrans>
      <form name="removeFinAccountTransFromReconciliation_${finAccountTrans.finAccountTransId}" method="post" action="<@ofbizUrl>removeFinAccountTransFromReconciliation</@ofbizUrl>">
        <input name="finAccountTransId" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
        <input name="finAccountId" type="hidden" value="${finAccountTrans.finAccountId}"/>
      </form>
    </#list>
    <#if grandTotal?exists>
      <#list finAccountTransList as finAccountTrans>
        <#if finAccountTrans.statusId?has_content && finAccountTrans.statusId == 'FINACT_TRNS_CREATED'>
          <form name="cancelFinAccountTrans_${finAccountTrans.finAccountTransId}" method="post" action="<@ofbizUrl>setFinAccountTransStatus</@ofbizUrl>">
            <input name="noConditionFind" type="hidden" value="Y"/>
            <input name="finAccountTransId" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
            <input name="finAccountId" type="hidden" value="${finAccountTrans.finAccountId}"/>
            <input name="statusId" type="hidden" value="FINACT_TRNS_CANCELED"/>
          </form>
        </#if>
      </#list>
      <table class="basic-table">
        <tr>
          <th>${uiLabelMap.FormFieldTitle_grandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</th>
          <th>${uiLabelMap.AccountingCreatedGrandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</th>
          <th>${uiLabelMap.AccountingApprovedGrandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</th>
          <th>${uiLabelMap.AccountingCreatedApprovedGrandTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</th>
        </tr>
        <tr>
          <td>${grandTotal} / ${searchedNumberOfRecords}</td>
          <td>${createdGrandTotal} / ${totalCreatedTransactions}</td>
          <td>${approvedGrandTotal} / ${totalApprovedTransactions}</td>
          <td>${createdApprovedGrandTotal} / ${totalCreatedApprovedTransactions}</td>
        </tr>
      </table>
    <#else>
      <table class="basic-table">
        <tr>
          <th>${uiLabelMap.AccountingRunningTotal} / ${uiLabelMap.AccountingNumberOfTransaction}</th>
          <th>${uiLabelMap.AccountingOpeningBalance}</th>
          <th>${uiLabelMap.FormFieldTitle_reconciledBalance}</th>
          <th>${uiLabelMap.FormFieldTitle_closingBalance}</th>
        </tr>
        <tr>
          <td>
            <span id="finAccountTransRunningTotal"></span> / 
            <span id="numberOfFinAccountTransaction"></span>
          </td>
          <td><@ofbizCurrency amount=glReconciliation.openingBalance?default('0')/></td>
          <td><@ofbizCurrency amount=glReconciliation.reconciledBalance?default('0')/></td>
          <td id="endingBalance"><@ofbizCurrency amount=glReconciliationApprovedGrandTotal?if_exists/></td>
          <input type="hidden" id="endingBalanceInput" value="<@ofbizCurrency amount=glReconciliationApprovedGrandTotal?if_exists/>"/>
        </tr>
      </table>
    </#if>
  <#else>
    <h2>${uiLabelMap.CommonNoRecordFound}</h2>  
  </#if>
</div>
