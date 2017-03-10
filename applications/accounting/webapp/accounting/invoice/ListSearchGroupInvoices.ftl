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

<#-- This Ftl we are Using for Icp Sales-->
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="text/javascript">

	var voucherPaymentMethodTypeMap = ${StringUtil.wrapString(voucherPaymentMethodJSON)!'{}'};
	var paymentMethodList;
/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 var booth;
	 var dueAmount;
	 var paymentMethod;
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
					populateDate();
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


function cancelForm(){		 
		return false;
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}

function datepick()
	{		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1});
		$( "#paymentDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		$( "#instrumentDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	function paymentFieldsOnchange(){
	var str=jQuery("#paymentMethodTypeId").val();
	var paymentMethodType = jQuery("select[name='paymentTypeId']").val();
	//alert("=======paymentMethodType====="+str);
	if(str == undefined){
		return;
	}
	if(str.search(/(CASH)+/g) >= 0){
		jQuery("input[name='instrumentDate']").parent().parent().hide();
		jQuery("input[name='paymentRefNum']").parent().parent().hide();
		jQuery("input[name='issuingAuthority']").parent().parent().hide();
		jQuery("input[name='inFavourOf']").parent().parent().hide();
		jQuery("#finAccountId").parent().parent().hide();
		jQuery("#finAccountId").val("");
		//jQuery("input[name='issuingAuthority']").removeClass("required");
		//jQuery("input[name='issuingAuthorityBranch']").removeClass("required");
	}
	/*
	else if(str.search(/(FUND)+/g) >= 0){
		jQuery("input[name='instrumentDate']").parent().parent().show();
		jQuery("input[name='paymentRefNum']").parent().parent().show();
		jQuery("#finAccountId").parent().parent().show();
		//jQuery("input[name='issuingAuthority']").removeClass("required");
		//jQuery("input[name='issuingAuthorityBranch']").removeClass("required");
	} */
	else{
		jQuery("input[name='instrumentDate']").parent().parent().show();
		jQuery("#finAccountId").parent().parent().show();
		//jQuery("#finAccountId").val("");
		jQuery("input[name='paymentRefNum']").parent().parent().show();
		jQuery("input[name='issuingAuthority']").parent().parent().show();
		jQuery("input[name='issuingAuthorityBranch']").parent().parent().show();
		jQuery("input[name='inFavourOf']").parent().parent().show();
		
		jQuery("input[name='paymentRefNum']").addClass("required");
		jQuery("input[name='inFavourOf']").addClass("required");
		jQuery("input[name='instrumentDate']").addClass("required");
		jQuery("input[name='issuingAuthority']").addClass("required");	
		jQuery("input[name='issuingAuthorityBranch']").addClass("required");	
	}
	
}
//<![CDATA[

    function toggleInvoiceId(master) {
        var invoices = jQuery("#listInvoices :checkbox[name='invoiceIds']");

        jQuery.each(invoices, function() {
            this.checked = master.checked;
        });
        getInvoiceRunningTotal();
    }
    
	
    function getInvoiceRunningTotal() {
		var checkedInvoices = jQuery("input[name='invoiceIds']:checked");
		var index = 0;
		var invoiceRunningTotal = 0;
        if(checkedInvoices.size() > 0) {
        jQuery.ajax({
                url: 'getInvoiceRunningTotal',
                type: 'POST',
                async: true,
                data: jQuery('#listInvoices').serialize(),
                success: function(data) { jQuery('#showInvoiceRunningTotal').html(data.invoiceRunningTotal + '  (' + checkedInvoices.size() + ')') }
            });
            
     jQuery.each(checkedInvoices, function() {
     	if (jQuery(this).is(':checked')) {
     	var domObj = $(this).parent().parent();
		var tempAmountObj = $(domObj).find("#tempAmount");
		var tempAmount = parseFloat($(tempAmountObj).val());
		invoiceRunningTotal = invoiceRunningTotal+tempAmount;
     	}
    });
    
            if(jQuery('#serviceName').val() != "") {
            	jQuery('#submitButton').removeAttr('disabled');                
            }

        } else {
            jQuery('#submitButton').attr('disabled', 'disabled');
            jQuery('#showInvoiceRunningTotal').html("");
        }
    
    $('#payAmount').val(invoiceRunningTotal);
        
    }
    
    
    function processPayments(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var invoices = $('input[name=invoiceIds]:checked');
    	 if(invoices.size() <=0) {
			 alert("Please Select at least One Request..!")
		 	 return false;
			}
    	
        var index = 0;
        var paymentDate = $("#paymentDate").val();
        var paymentMethodId = $("#paymentMethodId").val();
        var issuingAuthority = $("#issuingAuthority").val();
        var instrumentDate = $("#instrumentDate").val();
        var paymentRefNum = $("#paymentRefNum").val();
        var inFavor = $("#inFavor").val();
        var paymentGroupTypeId = $("#paymentGroupTypeId").val();
        var finAccountId = $("#finAccountId").val();
        jQuery.each(invoices, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var invoiceObj = $(domObj).find("[name='invoiceId']");
            	var eachAmtObj = $(domObj).find("[name='tempAmount']");
            	var partyIdObj = $(domObj).find("[name='partyId']");
            	var fromPartyIdObj = $(domObj).find("[name='fromPartyId']");
            	var invoiceId = $(invoiceObj).val();
            	var amount = parseFloat($(eachAmtObj).val());
            	var partyId = $(partyIdObj).val();
            	var fromPartyId = $(fromPartyIdObj).val();
            	
               	var appendStr = "<input type=hidden name=invoiceId_o_"+index+" value="+invoiceId+" />";
            		appendStr += "<input type=hidden name=amount_o_"+index+" value='"+amount+"' />";
            		appendStr += "<input type=hidden name=partyId_o_"+index+" value='"+partyId+"' />";
            		appendStr += "<input type=hidden name=fromPartyId_o_"+index+" value='"+fromPartyId+"' />";
                $("#groupInvoiceForm").append(appendStr);
            }
            index = index+1;
        });
        var appStr = "";
    	appStr += "<input type=hidden name=paymentDate value='"+ paymentDate +"' />";
    	appStr += "<input type=hidden name=inFavor value='"+ inFavor +"' />";
    	appStr += "<input type=hidden name=paymentGroupTypeId value='"+ paymentGroupTypeId +"' />";
    	appStr += "<input type=hidden name=paymentMethodId value='"+ paymentMethodId +"' />";
    	appStr += "<input type=hidden name=issuingAuthority value='"+ issuingAuthority +"' />";
    	appStr += "<input type=hidden name=instrumentDate value='"+ instrumentDate +"' />";
    	appStr += "<input type=hidden name=paymentRefNum value='"+ paymentRefNum +"' />";
    	appStr += "<input type=hidden name=finAccountId value='"+ finAccountId +"' />";
    	$("#groupInvoiceForm").append(appStr);
    	jQuery('#groupInvoiceForm').submit();
    }
   
//]]>
	

   var partyIdFrom;
	var partyIdTo;
	var invoiceId;
	var voucherType;
	var amount;
	var partyName;
	var comments;
	var paymentPurpose;

	function populateDate(){
		jQuery("#partyIdFrom").val(partyIdFrom);
		jQuery("#partyIdTo").val(partyIdTo);
		jQuery("#invoiceId").val(invoiceId);
		jQuery("#voucherType").val(voucherType);
		jQuery("#amount").val(amount);
		jQuery("#inFavourOf").val(partyName);
		jQuery("#comments").val(comments);
		jQuery("#paymentPurposeType").val(paymentPurpose);
		
		
		$('#paymentMethodTypeId').html(paymentMethodList.join(''));
		//$("#paymentMethodTypeId").addOption(paymentMethodList, false); 
		//$("#paymentMethodTypeId")[0].options.add(paymentMethodList);
		//alert("==amount=="+amount);
		paymentFieldsOnchange();
	};
	
</script>
<#if invoices?has_content>
  <#assign invoiceList  =  invoices.getCompleteList() />
  <#assign eliClose = invoices.close() />
</#if>
<#if invoiceList?has_content>
<form name="groupInvoiceForm" id="groupInvoiceForm" method="post" action="makeGroupInvoicePayments">
</form>


  <div>
    <span class="label">${uiLabelMap.AccountingTotalInvoicesCount} :${invoiceList?size}</span>  
    <span class="label">${uiLabelMap.AccountingRunningTotalOutstanding} (${uiLabelMap.AccountingSelectedInvoicesCount}) :</span>
    <span class="label" id="showInvoiceRunningTotal"></span>
  </div>
  <form name="listInvoices" id="listInvoices"  method="post" action="">
  	<div>
	<table width="100%">
    		<tr>
    			<td><span class="label">Payment Method:</span><select class='h3' name="paymentMethodId" id="paymentMethodId" style="width:50%"><#list paymentMethods as eachMethod><option value="${eachMethod.paymentMethodId?if_exists}">${eachMethod.description?if_exists}</option></#list></select></td>
    			<td><span class="label">Cheque No :</span><input class='h3' type='text' id='paymentRefNum' name='paymentRefNum'/></td>
    			<td><span class="label"> Cheque Date :</span><input class='h3' type='text' id='instrumentDate' name='instrumentDate' value='${defaultEffectiveDate?if_exists}' onmouseover='datepick()'/></td>
    			<td><span class="label">Cheque in Favor (<font color='red'>*</font>):</span><input class='h3' type='text' id='inFavor' name='inFavor'/></td>	
    		</tr>
    		<tr>
    			<#--<td><span class="label"> Issuing Authority:</span>
    				<input class='h3' type='text' id='issuingAuthority' name='issuingAuthority'/>
    			</td>-->
    			<td><span class="label">Fin Account</span>
    				<select name="finAccountId" id="finAccountId" style="width:50%">
    				<option value=""></option>
    				<#if finAccounts1?has_content>
    				<#list finAccounts1 as eachFinAccount>
    					<option value="${eachFinAccount.finAccountId}">${eachFinAccount.finAccountName}[${eachFinAccount.finAccountId}]</option>	
    				</#list>
    				</#if>
    				</select>
    			</td>
    			<input type="hidden" name="paymentGroupTypeId" id="paymentGroupTypeId" value="${roleTypeId?if_exists}">
    			<td><span class="label">${roleTypeId?if_exists} Amount:</span><input class='h3' type='text' id='payAmount' name='payAmount' readonly/></td>
    			<td><span class="label"> Payment Date (<font color='red'>*</font>):</span><input class='h3' type='text' id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td>
    			<#if security.hasEntityPermission("BATCHPAY", "_CREATE", session)>
    				<td><input id="submitButton" type="button"  onclick="javascript:processPayments(this);" value="Make Payment"/></td>
    			</#if>
    		</tr>
    	</table>
</div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>${uiLabelMap.FormFieldTitle_invoiceId}</td>
          <td>${uiLabelMap.FormFieldTitle_invoiceTypeId}</td>
          <td>${uiLabelMap.AccountingInvoiceDate}</td>
          <td>Due Date</td>
          <td>${uiLabelMap.CommonStatus}</td>
          <td>${uiLabelMap.CommonDescription}</td>
           <td>Miller Bill Number</td>
          <td>${uiLabelMap.AccountingFromParty}</td> 
          <td>${uiLabelMap.AccountingToParty}</td>
          <td>${uiLabelMap.AccountingAmount}</td>
          <td>${uiLabelMap.FormFieldTitle_paidAmount}</td>
          <td>${uiLabelMap.FormFieldTitle_outstandingAmount}</td>
          <td>Pay</td>
          <td>select</td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list invoiceList as invoice>
          <#assign invoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", invoice.invoiceId, "userLogin", userLogin))/>
          <#assign invoicePaymentInfo = invoicePaymentInfoList.get("invoicePaymentInfoList").get(0)?if_exists>
          <#if invoicePaymentInfo.get("outstandingAmount") gt 0 && invoice.get("invoiceTypeId")!="PAYROL_INVOICE">
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><a class="buttontext" href="<@ofbizUrl>invoiceOverview?invoiceId=${invoice.invoiceId}</@ofbizUrl>">${invoice.get("invoiceId")}</a></td>
              <td>
                <#assign invoiceType = delegator.findOne("InvoiceType", {"invoiceTypeId" : invoice.invoiceTypeId}, true) />
                
                ${invoiceType.description?default(invoice.invoiceTypeId)}
              </td>
              <td>${(invoice.invoiceDate)?if_exists}</td>
              <td>${(invoice.dueDate)?if_exists}</td>
              <td>
                <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : invoice.statusId}, true) />
                ${statusItem.description?default(invoice.statusId)}
              </td>
              <td>${(invoice.description)?if_exists}</td>
              <td>
             	<#if invoice.shipmentId?has_content && invoice.shipmentId!="OBC">
					<#assign Shipment = delegator.findOne("Shipment", {"shipmentId" : invoice.shipmentId}, true) />
                	${Shipment.supplierInvoiceId?default(invoice.shipmentId)}
			  	</#if>
             </td>
              <#assign partyName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom, false)?if_exists/>
            
            
              <input type = "hidden" name = "partyId" id = "partyId" value = "${invoice.partyId}">
              <input type = "hidden" name = "fromPartyId" id = "fromPartyId" value = "${invoice.partyIdFrom}">
         <#-->     <input type = "hidden" name = "amt" id = "amt" value = "${invoicePaymentInfo.outstandingAmount}">  -->
              <input type = "hidden" name = "invoiceId" id = "invoiceId" value = "${invoice.invoiceId}">
              <input type = "hidden" name = "partyIdName" id = "partyIdName" value = "${partyName}">
              <input type = "hidden" name = "voucherTypeId" id = "voucherTypeId" value = "${invoice.prefPaymentMethodTypeId?if_exists}">
              <input type="hidden" name="organizationPartyId" value="${defaultOrganizationPartyId}"/>
              
              <td>
                            </td>
              <td><a href="/partymgr/control/viewprofile?partyId=${invoice.partyIdFrom}">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom, false)?if_exists} [${(invoice.partyIdFrom)?if_exists}]</a></td>
              <td><a href="/partymgr/control/viewprofile?partyId=${invoice.partyId}">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyId, false)?if_exists} [${(invoice.partyId)?if_exists}]</a></td>
              <td><@ofbizCurrency amount=invoicePaymentInfo.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><@ofbizCurrency amount=invoicePaymentInfo.paidAmount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><@ofbizCurrency amount=invoicePaymentInfo.outstandingAmount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              
              <td>
              	<input type="text" name="tempAmount" id="tempAmount" value="${invoicePaymentInfo.outstandingAmount}" size="10" onblur="javascript:getInvoiceRunningTotal();">
              </td>	
             <td align="right"><input type="checkbox" id="invoiceId_${invoice_index}" name="invoiceIds" value="${invoice.invoiceId}" onclick="javascript:getInvoiceRunningTotal();"/></td>
            </tr>
            </#if>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>${uiLabelMap.AccountingNoInvoicesFound}</h3>
</#if>
