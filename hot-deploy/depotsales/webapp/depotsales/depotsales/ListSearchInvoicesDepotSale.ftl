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

	var voucherPaymentMethodTypeMap = ${StringUtil.wrapString(voucherPaymentMethodJSON)!'{}'};
	var paymentMethodList = [];
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
		
        if(checkedInvoices.size() > 0) {
        	var runningTotalVal = 0;
        	var testList = new Array();
        	jQuery.each(checkedInvoices, function() {
            	if (jQuery(this).is(':checked')) {
            		var domObj = $(this).parent().parent();
					var test = $($(domObj).find("#fromPartyId")).val();
					if(testList.length>0 && jQuery.inArray(test, testList)==-1){
						$("#paymentButton").hide();	
					}
					testList.push(test);
	            }
    	    });
    	    var uniqueList = testList.filter(function(itm,i,testList){
    			return i==testList.indexOf(itm);
			});
			if(uniqueList.length == 1){
				$("#paymentButton").show();
			}
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
        if ( selection.value == 'massInvoicesToApprove' || selection.value == 'massInvoicesToSent' || selection.value == 'massInvoicesToReady' || selection.value == 'massInvoicesToPaid' || selection.value == 'massInvoicesToWriteoff' || selection.value == 'massInvoicesToCancel') {
            jQuery('#listInvoices').attr('action', jQuery('#invoiceStatusChange').val());
        } else {
            jQuery('#listInvoices').attr('action', selection.value);
        }

        
        if (selection.value == 'massInvoicesToApprove') {
            jQuery('#statusId').val("INVOICE_APPROVED");
        } else if (selection.value == 'massInvoicesToSent') {
            jQuery('#statusId').val("INVOICE_SENT");
        } else if (selection.value == 'massInvoicesToReady') {
            jQuery('#statusId').val("INVOICE_READY");
        } else if (selection.value == 'massInvoicesToPaid') {
            jQuery('#statusId').val("INVOICE_PAID");
        } else if (selection.value == 'massInvoicesToWriteoff') {
            jQuery('#statusId').val("INVOICE_WRITEOFF");
        } else if (selection.value == 'massInvoicesToCancel') {
            jQuery('#statusId').val("INVOICE_CANCELLED");
        } else if (selection.value == 'bulkSms') {
            jQuery('#statusId').val("bulkSms");
        } else if (selection.value == 'bulkEmail') {
            jQuery('#statusId').val("bulkEmail");
        }         

        var invoices = jQuery("#listInvoices :checkbox[name='invoiceIds']");
        // check if any checkbox is checked
        var anyChecked = false;
        jQuery.each(invoices, function() {
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
	

   var partyIdFrom;
	var partyIdTo;
	var invoiceId;
	var voucherType;
	var amount;
	var partyName;
	var comments;
function showPaymentEntryQTip(partyIdFrom1,partyIdTo1,invoiceId1,voucherType1,amount1,partyNameTemp,purposeTypeId1) {
		var message = "";
		 partyIdFrom=partyIdFrom1;
	     partyIdTo=partyIdTo1;
		 invoiceId=invoiceId1;
		 voucherType=voucherType1;
		 amount=amount1;
		 partyName=partyNameTemp;
		 var methodOptionList =[];
		 var payMethodList="";
		 purposeTypeId=purposeTypeId1;
		 
		 if(voucherType != undefined && voucherType != "" && voucherPaymentMethodTypeMap != undefined){
		  payMethodList=voucherPaymentMethodTypeMap[voucherType];
		  //alert("=IN IFFF==="+partyIdFrom+"==partyIdTo="+partyIdTo+"===voucherType=="+voucherType+"===payMethodList==="+payMethodList);
	 	 if(payMethodList != undefined && payMethodList != ""){
			$.each(payMethodList, function(key, item){
			  methodOptionList.push('<option value="'+item.value+'">'+item.text+'</option>');
			});
	 	   }
		 }else{
		  //alert("=IN=ELSEE==="+partyIdFrom+"==partyIdTo="+partyIdTo+"===voucherType=="+voucherType+"===payMethodList==="+payMethodList);
	 	   payMethodList=voucherPaymentMethodTypeMap['ALL'];
	 	   	$.each(payMethodList, function(key, item){
			  methodOptionList.push('<option value="'+item.value+'">'+item.text+'</option>');
			});
	 	   }
		  paymentMethodList = methodOptionList;
		  
			message += "<html><head></head><body><form action='createApDepotApPayment' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Payment Type :</td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId'  class='h4'>"+
						"<#if paymentTypes?has_content><#list paymentTypes as eachMethodType><option value='${eachMethodType.paymentTypeId?if_exists}' >${eachMethodType.description?if_exists}</option></#list></#if>"+            
						"</select><input class='h4' type='hidden' id='paymentPurposeType' name='paymentPurposeType' value='"+purposeTypeId+"'/></td></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%'><#if parentTypeId?exists && parentTypeId=="SALES_INVOICE">Payment Method Type :<#else>Payment Method:</#if> </td><td align='left' width='60%'><select <#if parentTypeId?exists && parentTypeId=="SALES_INVOICE"> name='paymentMethodTypeId' <#else> name='paymentMethodId' </#if> id='paymentMethodTypeId'  class='h4'>"+
						"</select></td></tr>"+						"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='useFifo' value='TRUE'/><input class='h4' type='hidden' id='parentTypeId' name='parentTypeId' value='${parentTypeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount' name='amount'/><input class='h4' type='hidden' id='partyIdFrom' name='partyIdFrom' /><input class='h4' type='hidden' id='partyIdTo' name='partyIdTo'/><input class='h4' type='hidden' id='invoiceId' name='invoiceId' /><input class='h4' type='hidden' id='voucherType' name='voucherType' /></td></tr>"+
				 		<#--"<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' /></td></tr>"+-->

						"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>";
			<#--if(voucherType != 'CASH'){
				message += 	"<tr class='h3'><td align='left'class='h3' width='60%'>Financial Account:</td><td align='left' width='60%'><select name='finAccountId' id='finAccountId'  class='h4'>"+
							"<#if finAccountList?has_content><#list finAccountList as finAccount><option value='${finAccount.finAccountId?if_exists}' >${finAccount.finAccountName?if_exists}</option></#list></#if>"+            
							"</select></td></tr>";
						
				message += "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='effectiveDate' name='instrumentDate' onmouseover='datepick()'/></td></tr>" +
						   "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
					 	   "<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' /></td></tr>";
			}-->
			message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			message +=	"</table></form></body></html>";
		var title = "Payment Entry : ";
		Alert(message, title);
	}	
	function populateDate(){
		jQuery("#partyIdFrom").val(partyIdFrom);
		jQuery("#partyIdTo").val(partyIdTo);
		jQuery("#invoiceId").val(invoiceId);
		jQuery("#voucherType").val(voucherType);
		jQuery("#amount").val(amount);
		jQuery("#inFavourOf").val("DepotSales");
		jQuery("#comments").val(comments);
		//jQuery("#paymentMethodTypeId").val("FUND_TRANSFER");
		
		$('#paymentMethodTypeId').html(paymentMethodList.join(''));
		//$("#paymentMethodTypeId").addOption(paymentMethodList, false); 
		//$("#paymentMethodTypeId")[0].options.add(paymentMethodList);
		//alert("==amount=="+amount);
		
	};
	
	
	function massPaymentSubmit(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var index = 0;
    	var invoices = jQuery("#listInvoices :checkbox[name='invoiceIds']");
    	var appendStr = "<table id=parameters>";
        jQuery.each(invoices, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var amtObj = $(domObj).find("#amt");
            	var partyIdObj = $(domObj).find("#partyId");
            	var fromPartyIdObj = $(domObj).find("#fromPartyId");
            	var partyIdNameObj = $(domObj).find("#partyIdName");
            	var voucherTypeIdObj = $(domObj).find("#voucherTypeId");
            	
            	var invId = $(this).val();
            	var amt = $(amtObj).val();
            	var partyId = $(partyIdObj).val();
            	var fromPartyId = $(fromPartyIdObj).val();
            	var partyIdName = $(partyIdNameObj).val();
            	var voucherTypeId = $(voucherTypeIdObj).val();
            	
            	appendStr += "<tr><td><input type=hidden name=invId id=invId value="+invId+" />";
            	appendStr += "<input type=hidden name=partyId id=partyId value="+partyId+" />";
            	appendStr += "<input type=hidden name=fromPartyId id=fromPartyId value="+fromPartyId+" />";
            	appendStr += "<input type=hidden name=partyIdName id=partyIdName value="+partyIdName+" />";
            	appendStr += "<input type=hidden name=voucherTypeId id=voucherTypeId value="+voucherTypeId+" />";
            	appendStr += "<input type=hidden name=amt id=amt value="+amt+" /></td></tr>";
                
            }
            index = index+1;
            
        });
        appendStr += "</table>";
        $("#paymentSubmitForm").append(appendStr);
        
        var form = $("#paymentSubmitForm");
        massInvoicePayments(form);
        
    }
        
	function confirmInvoiceCancel(invoiceId) {
        var msg = "You want to cancel this invoice "+invoiceId+" ?";
    	var agree = confirm(msg);
    	if (agree) {
        	showCancelInvoiceQtip(invoiceId);
    	}
    	return false;
	}	
	function showCancelInvoiceQtip(invoiceId){
		var invoiceId = invoiceId;
     	var message = "";
            message += "<html><head></head><body><form id='invoicestatuschange' method='post' action='cancelPurchaseInvoice' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
      		message += "<tr class='h2'><td align='left' class='h5' width='60%'>Invoice Id:</td><td align='left'  width='90%'>"+invoiceId+"</td></tr>";
  			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'>"	
  			message += "<input type='hidden' id='invId' name='invoiceId' value='"+invoiceId+"' >"
  			message += "<input class='h4' type='text' id='cancelComments' name='cancelComments' required /><input class='h4' type='hidden' id='statusId' name='statusId' value='INVOICE_CANCELLED' /></td></tr>";
 		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='return submitForm();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
		    message +=	"</table></form></body></html>";		    
		var title = "Reason for cancel";
    	Alert(message, title);
	}
	
	
	
</script>
<#if invoices?has_content>
  <#assign invoiceList  =  invoices.getCompleteList() />
  <#assign eliClose = invoices.close() />
</#if>
<#if invoiceList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>
<form name="paymentSubmitForm" id="paymentSubmitForm" method="post" action="makeMassInvoicePayments">
</form>
  <div>
    <span class="label">${uiLabelMap.AccountingTotalInvoicesCount} :${invoiceList?size}</span>  
    <span class="label">${uiLabelMap.AccountingRunningTotalOutstanding} (${uiLabelMap.AccountingSelectedInvoicesCount}) :</span>
    <span class="label" id="showInvoiceRunningTotal"></span>
  </div>
  <form name="listInvoices" id="listInvoices"  method="post" action="">
    <div align="right">
     <#--<select name="serviceName" id="serviceName" onchange="javascript:setServiceName(this);">
        <option value="">${uiLabelMap.AccountingSelectAction}</option>
        <option value="<@ofbizUrl>PrintInvoices</@ofbizUrl>">${uiLabelMap.AccountingPrintInvoices}</option>
        <option value="massInvoicesToApprove">${uiLabelMap.AccountingInvoiceStatusToApproved}</option>
        <option value="massInvoicesToSent">${uiLabelMap.AccountingInvoiceStatusToSent}</option>
        <option value="massInvoicesToReady">${uiLabelMap.AccountingInvoiceStatusToReady}</option>
        <option value="massInvoicesToPaid">${uiLabelMap.AccountingInvoiceStatusToPaid}</option>
        <option value="massInvoicesToWriteoff">${uiLabelMap.AccountingInvoiceStatusToWriteoff}</option>
        <option value="massInvoicesToCancel">${uiLabelMap.AccountingInvoiceStatusToCancelled}</option>
        <option value="bulkSms">${uiLabelMap.sendBulkSms}</option>
        <option value="bulkEmail">${uiLabelMap.sendBulkEmail}</option>
      </select> -->

      <#--<input id="submitButton" type="button"  onclick="javascript:jQuery('#listInvoices').submit();" value="${uiLabelMap.CommonRun}" disabled="disabled" />-->

      <input type="hidden" name="organizationPartyId" value="${defaultOrganizationPartyId}"/>
      <input type="hidden" name="partyIdFrom" value="${parameters.partyIdFrom?if_exists}"/>
      <input type="hidden" name="statusId" id="statusId" value="${parameters.statusId?if_exists}"/>
      <input type="hidden" name="fromInvoiceDate" value="${parameters.fromInvoiceDate?if_exists}"/>
      <input type="hidden" name="thruInvoiceDate" value="${parameters.thruInvoiceDate?if_exists}"/>
      <input type="hidden" name="fromDueDate" value="${parameters.fromDueDate?if_exists}"/>
      <input type="hidden" name="thruDueDate" value="${parameters.thruDueDate?if_exists}"/>
      <input type="hidden" name="invoiceStatusChange" id="invoiceStatusChange" value="<@ofbizUrl>massChangeInvoiceStatus</@ofbizUrl>"/>
      <input type="hidden" name="bulkSms" id="bulkSms" value="<@ofbizUrl>bulkSms</@ofbizUrl>"/>
      <input type="hidden" name="bulkEmail" id="bulkEmail" value="<@ofbizUrl>bulkEmail</@ofbizUrl>"/>
	  <input id="paymentButton" type="button"  onclick="javascript:massPaymentSubmit(this);" value="Make Payment" />
	 </div>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>${uiLabelMap.FormFieldTitle_invoiceId}</td>
         <#--> <td>${uiLabelMap.FormFieldTitle_invoiceTypeId}</td>-->
          <td>${uiLabelMap.AccountingInvoiceDate}</td>
          <td>Shipment Id</td>
          <td>${uiLabelMap.CommonStatus}</td>
         <#--- <td>${uiLabelMap.CommonDescription}</td>
          <td>Reason For Cancellation</td>-->
          <td>Customer</td>
          <td>Supplier</td>
          <td>Branch</td>
          <td>${uiLabelMap.AccountingAmount}</td>
          <td>${uiLabelMap.FormFieldTitle_paidAmount}</td>
          <td>${uiLabelMap.FormFieldTitle_outstandingAmount}</td>
          <td>Edit Invoice</td>
          <#--<td>Rise Sales Invoice</td>-->
         <#--- <td>Invoice voucher</td>-->
          <td>Make Payment</td> 
          <#--<td>Payment Advice</td>-->
         <#-- <td>Cheque</td>-->
         <td>Cancel</td> 
          <td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllInvoices" name="checkAllInvoices" onchange="javascript:toggleInvoiceId(this);"/></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list invoiceList as invoice>
          <#assign invoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", invoice.invoiceId, "userLogin", userLogin))/>
          <#assign invoicePaymentInfo = invoicePaymentInfoList.get("invoicePaymentInfoList").get(0)?if_exists>
          	<#assign invoiceSequence = Static["org.ofbiz.accounting.invoice.InvoiceServices"].getInvoiceSequence(delegator, invoice.invoiceId)?if_exists/>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><a class="buttontext" target='_blank' href="<@ofbizUrl>APInvoiceOverview?invoiceId=${invoice.invoiceId}&amp;subTabButtonValue=${tabButtonItem5}</@ofbizUrl>">${invoiceSequence}</a></td>
              <#--><td>
                <#assign invoiceType = delegator.findOne("InvoiceType", {"invoiceTypeId" : invoice.invoiceTypeId}, true) />
                
                ${invoiceType.description?default(invoice.invoiceTypeId)}
              </td>-->
              <td>${(invoice.invoiceDate)?if_exists}</td>
              <#--><td>${(invoice.dueDate)?if_exists}</td> -->
              
               <td><a class="buttontext" target='_blank' href="<@ofbizUrl>MaterialShipmentOverview?shipmentId=${invoice.shipmentId}</@ofbizUrl>">${invoice.shipmentId}</a></td>
              <td>
                <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : invoice.statusId}, true) />
                ${statusItem.description?default(invoice.statusId)}
              </td>
 		       <#if (invoice.shipmentId?has_content) && invoice.shipmentId != "OBC">
              <#assign shipmentId = invoice.shipmentId>
               <#assign shipment = delegator.findOne("Shipment", {"shipmentId" : shipmentId}, false)?if_exists />
               <#if shipment?has_content && shipment.primaryOrderId?has_content>
                <#assign orderRole = delegator.findByAnd("OrderRole", {"orderId" : shipment.primaryOrderId?if_exists, "roleTypeId" : "SHIP_TO_CUSTOMER" })>
                  <#if (orderRole?has_content) && (orderRole[0].partyId?has_content)>
	                  <#assign supplierId = (orderRole[0].partyId)>
	                  <#assign customerName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplierId, false)/>
		 		       <td>
		                <a href="/partymgr/control/viewprofile?partyId=${supplierId}">${customerName}[${supplierId}</a>
		              </td>
		             <#else>
		              <td>
		              </td>
		         </#if>     
               </#if>
			 </#if>
 			
             <#--- <td>${(invoice.description)?if_exists}</td>
 			  <td>${(invoice.cancelComments)?if_exists}</td>-->
              <#assign partyName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom, false)?if_exists/>
            
            
              <input type = "hidden" name = "partyId" id = "partyId" value = "${invoice.partyId}">
              <input type = "hidden" name = "fromPartyId" id = "fromPartyId" value = "${invoice.partyIdFrom}">
              <input type = "hidden" name = "amt" id = "amt" value = "${invoicePaymentInfo.outstandingAmount}">
              <input type = "hidden" name = "invId" id = "invId" value = "${invoice.invoiceId}">
              <input type = "hidden" name = "partyIdName" id = "partyIdName" value = "${partyName}">
              <input type = "hidden" name = "voucherTypeId" id = "voucherTypeId" value = "${invoice.prefPaymentMethodTypeId?if_exists}">
              
              
              <td><a href="/partymgr/control/viewprofile?partyId=${invoice.partyIdFrom}">${partyName}[${(invoice.partyIdFrom)?if_exists}]</a></td>
              <td><a href="/partymgr/control/viewprofile?partyId=${invoice.partyId}">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyId, false)?if_exists} [${(invoice.partyId)?if_exists}]</a></td>
              <td><@ofbizCurrency amount=invoicePaymentInfo.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><@ofbizCurrency amount=invoicePaymentInfo.paidAmount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><@ofbizCurrency amount=invoicePaymentInfo.outstandingAmount isoCode=defaultOrganizationPartyCurrencyUomId/></td>        
              <#--- <td><a class="buttontext" target="_BLANK" href="<@ofbizUrl>purchaseInvoiceDetails.pdf?invoiceId=${invoice.invoiceId}</@ofbizUrl>">Print</a></td>-->
               <#--<td><a class="buttontext" target="_BLANK" href="<@ofbizUrl>processSalesInvoice?invoiceId=${invoice.invoiceId}</@ofbizUrl>">Rise Sales Invoice</a></td>-->
               
               <#if invoice.statusId != "INVOICE_CANCELLED"><td><a class="buttontext" target='_blank' href="<@ofbizUrl>purchaseInvoiceEdit?invoiceId=${invoice.invoiceId}&amp;partyId=${invoice.partyId}&amp;partyName=${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyId, false)}</@ofbizUrl>">Edit Invoice</a></td>
                  <#else>
 		   	          <td></td>
               </#if>
               <#if ((invoice.statusId != "INVOICE_IN_PROCESS") && (invoice.statusId != "INVOICE_CANCELLED") && (invoicePaymentInfo.outstandingAmount >0)) >
              	  <#if (invoice.parentTypeId == "PURCHASE_INVOICE")||(invoice.prefPaymentMethodTypeId?exists) >
              		  <#if invoice.purposeTypeId?has_content>
              		  	<#assign purposeTypeId=invoice.purposeTypeId>
              		  </#if>
              		  <td align="center"><input type="button"  name="paymentBuuton" value="Payment" onclick="javascript:showPaymentEntryQTip('${invoice.partyId}','${invoice.partyIdFrom}','${invoice.invoiceId}','${invoice.prefPaymentMethodTypeId?if_exists}','${invoicePaymentInfo.outstandingAmount}','${partyName}','${purposeTypeId}');"/></td>
               	    <#else>
                	  <td align="center"></td>
               	  </#if>
                <#else>
                	<td align="center"></td>
              </#if>
              <#--><td><a class="buttontext" target="_BLANK" href="<@ofbizUrl>invoiceVoucher?invoiceId=${invoice.invoiceId}</@ofbizUrl>">Print</a></td>-->
            <#--<#if invoice.parentTypeId?has_content>
              <td><#if ((invoice.statusId != "INVOICE_CANCELLED") &&(invoice.parentTypeId == "PURCHASE_INVOICE"))><a class="buttontext" target="_BLANK" href="<@ofbizUrl>printChecks.pdf?invoiceId=${invoice.invoiceId}</@ofbizUrl>">Cheque</a></#if></td>
              <#else>
               <td align="center"></td>
               </#if> -->
              <td align="center"><#if invoice.statusId != "INVOICE_CANCELLED"><input type="button" name="cancel" value="Cancel" onclick="javascript:confirmInvoiceCancel('${invoice.invoiceId}')"/></#if></td>
              <td align="right"><input type="checkbox" id="invoiceId_${invoice_index}" name="invoiceIds" value="${invoice.invoiceId}" onclick="javascript:getInvoiceRunningTotal();"/></td>
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
