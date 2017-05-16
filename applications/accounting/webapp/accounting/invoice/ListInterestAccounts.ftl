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

//<![CDATA[

    function toggleInvoiceId(master) {
        var parties = jQuery("#listCpfAccounts :checkbox[name='partyIds']");
        jQuery.each(parties, function() {
            this.checked = master.checked;
        });
        getInvoiceRunningTotal();
    }
    
	
    function getInvoiceRunningTotal() {
		var checkedParties = jQuery("input[name='partyIds']:checked");
		var index = 0;
		var invoiceRunningTotal = 0;
        if(checkedParties.size() > 0) {
        jQuery.ajax({
                url: 'getInvoiceRunningTotal',
                type: 'POST',
                async: true,
                data: jQuery('#listCpfAccounts').serialize(),
                success: function(data) { jQuery('#showInvoiceRunningTotal').html(data.invoiceRunningTotal + '  (' + checkedParties.size() + ')') }
         });
            
     jQuery.each(checkedParties, function() {
     	if (jQuery(this).is(':checked')) {
     	var domObj = $(this).parent().parent();
		var empConObj = $(domObj).find("#empCon");		
		var tempEmpConAmt = parseFloat($(empConObj).val());
	    invoiceRunningTotal = invoiceRunningTotal+tempEmpConAmt;
		var emprConObj = $(domObj).find("#emprCon");	
	    var tempEmprConAmt = parseFloat($(emprConObj).val());
	    invoiceRunningTotal = invoiceRunningTotal+tempEmprConAmt;
	    var vpfConObj = $(domObj).find("#vpfCon");	
	    var tempVpfConObjAmt = parseFloat($(vpfConObj).val());
		invoiceRunningTotal = invoiceRunningTotal+tempVpfConObjAmt;
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
    
    
    function processCPFAccounts(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var parties = $('input[name=partyIds]:checked');
    	 if(parties.size() <=0) {
			 alert("Please Select at least One Request..!")
		 	 return false;
			}
    	
        var index = 0;
        var paymentDate = $("#paymentDate").val();
        //var issuingAuthority = $("#issuingAuthority").val();
        var instrumentDate = $("#instrumentDate").val();
        
        var description = $("#description").val();
        var receivepartyId = $("#party").val();
        var paymentGroupTypeId = $("#paymentGroupTypeId").val();
        
        var glAccountId = $("#glAccountId").val();
        var depositAmt=$("#payAmount").val();
        var appeStr = "";
        jQuery.each(parties, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var partyIdObj = $(domObj).find("[name='partyId']");
            	var empConAmtObj = $(domObj).find("[name='empCon']");
            	var emprConAmtObj = $(domObj).find("[name='emprCon']");
            	var vpfConAmtObj = $(domObj).find("[name='vpfCon']");
            	var partyId = $(partyIdObj).val();
            	var empConAmt = parseFloat($(empConAmtObj).val());
            	var emprConAmt = parseFloat($(emprConAmtObj).val());
            	var vpfConAmt = parseFloat($(vpfConAmtObj).val());
               	 var appendStr = "<input type=hidden name=partyId_o_"+index+" value="+partyId+" />";
            		appendStr += "<input type=hidden name=empCon_o_"+index+" value='"+empConAmt+"' />";
            		appendStr += "<input type=hidden name=emprCon_o_"+index+" value='"+emprConAmt+"' />";
            		appendStr += "<input type=hidden name=vpfCon_o_"+index+" value='"+vpfConAmt+"' />";
                    	$("#groupAmountForm").append(appendStr);
            }
            index = index+1;
        });
        
    	appeStr += "<input type=hidden name=paymentDate value='"+ paymentDate +"' />";
    	//appeStr += "<input type=hidden name=paymentGroupTypeId value='"+ paymentGroupTypeId +"' />";
    	appeStr += "<input type=hidden name=instrumentDate value='"+ instrumentDate +"' />";
    	appeStr += "<input type=hidden name=description value='"+ description +"' />";
    	appeStr += "<input type=hidden name=receivepartyId value='"+ receivepartyId +"' />";
    	appeStr += "<input type=hidden name=glAccountId value='"+ glAccountId +"' />";
    	appeStr += "<input type=hidden name=depositAmt value='"+ depositAmt +"' />";
    	
    	$("#groupAmountForm").append(appeStr);
    	jQuery('#groupAmountForm').submit();
    }
   //]]>
</script>
<#if empPartyIds?has_content>
<form name="groupAmountForm" id="groupAmountForm" method="post" action="makeInterestTransaction">
</form>
  <form name="listCpfAccounts" id="listCpfAccounts"  method="post" action="" >
    <div>
		<table width="100%">
		   <tr>
		      <td><span class="label">Gl Account</span>
    				<select name="glAccountId" id="glAccountId" style="width:50%">
    				<option value=""></option>
    				<#if glAccountIdsList?has_content>
    				<#list glAccountIdsList as eachgl>
    					<option value="${eachgl.glAccountId?if_exists}">${eachgl.description?if_exists}[${eachgl.glAccountId?if_exists}]</option>	
    				</#list>
    				</#if>
    				</select>
    			</td>
    			 <td><span class="label">Description</span><input id="description" class="input-medium" name="description" type="text" size="20"/></td>
    			<td><span class="label">PartyId</span>
    				<select name="party" id="party" style="width:50%">
    				<option value=""></option>
    			 <#list reqPartyIds as eachparty>
               <#assign partyName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,eachparty, false)?if_exists/>
    			<option value="${eachparty?if_exists}">${partyName?if_exists}[${eachparty?if_exists}]</option>	
              </#list>
              </select>
    			</td>
              <input type = "hidden" name = "partyId" id = "partyId" value = "${eachParty?if_exists}">
              <td>
    			
    			<input type="hidden" name="paymentGroupTypeId" id="paymentGroupTypeId" value="${roleTypeId?if_exists}">
    			<td><span class="label">${roleTypeId?if_exists} Amount:</span><input class='h3' type='text' id='payAmount' name='payAmount' readonly/></td>
    			<td><span class="label"> Transaction Date (<font color='red'>*</font>):</span><input class='h3' type='text' id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td>
		      <td><input id="submitButton" type="button"  onclick="javascript:processCPFAccounts(this);" value="Submit"/></td>
		   </tr>
	    </table>
    </div>  
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>PARTY CODE</td>
          <#if finAccountIds?has_content>
         <#list finAccountIds as eachFinAcctId>
          	
          <td>${eachFinAcctId?if_exists}</td>
         </#list> 
         </#if>
          <td>select</td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list empPartyIds as eachParty>
             <#assign partyName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,eachParty, false)?if_exists/>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><font <font color="green" >${partyName?if_exists}</font><a href="/partymgr/control/viewprofile?partyId=${eachParty?if_exists}" title="Supplier">[${eachParty}]</a></td>
              <input type = "hidden" name = "partyId" id = "partyId" value = "${eachParty?if_exists}">
              <td>
              	<input type="text" name="empCon" id="empCon" value="" size="10" onblur="javascript:getInvoiceRunningTotal();">
              </td>	
              <td>
              	<input type="text" name="emprCon" id="emprCon" value="" size="10" onblur="javascript:getInvoiceRunningTotal();">
              </td>	
              <td>
              	<input type="text" name="vpfCon" id="vpfCon" value="" size="10" onblur="javascript:getInvoiceRunningTotal();">
              </td>	
             <td align="right"><input type="checkbox" id="partyId_${eachParty_index}" name="partyIds" value="${eachParty?if_exists}" onclick="javascript:getInvoiceRunningTotal();"/></td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>NO Records Found</h3>
</#if>
