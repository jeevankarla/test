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
	
	
	function toggleFacilityId(master) {
        var facilities = jQuery("#listBooths :checkbox[name='boothIds']");
        jQuery.each(facilities, function() {
            this.checked = master.checked;
        });
        //getBoothRunningTotal();
    }
    function getBoothRunningTotal() {
		var checkedBoothIds = jQuery("input[name='boothIds']:checked");
        if(checkedBoothIds.size() > 0) {
            jQuery.ajax({
                url: 'getBoothDuesRunningTotal',
                type: 'POST',
                async: true,
                data: jQuery('#listBooths').serialize(),
                success: function(data) { 
                	
        			var str = "<font>"+data.boothRunningTotal+"</font> ( "+checkedBoothIds.size()+" )";
                	jQuery('#showBoothRunningTotal').html(str);
                	testVal = data.boothRunningTotal; 
                }
            });

            if(jQuery('#serviceName').val() != "") {
            	jQuery('#submitButton').removeAttr('disabled');                
            }

        } else {
            jQuery('#submitButton').attr('disabled', 'disabled');
            jQuery('#showBoothRunningTotal').html("");
        }
    }
	function setServiceName(selection) {
        jQuery('#submitButton').attr('disabled' , 'disabled');
        jQuery('#listBooths').attr('action', selection.value);
        

        var facilities = jQuery("#listBooths :checkbox[name='boothIds']");
        // check if any checkbox is checked
        var anyChecked = false;
        jQuery.each(facilities, function() {
            if (jQuery(this).is(':checked')) {
                anyChecked = true;
                return false;
            }
        });

        if(anyChecked && (jQuery('#serviceName').val() != "")) {
            jQuery('#submitButton').removeAttr('disabled');
        }
    }
    
    function setCreatePaymentParameters(currentFacility){
    	jQuery(currentFacility).attr( "disabled", "disabled");
    	var currentEle = jQuery(currentFacility);
    	formName=document.forms['listBooths'];    	
		var hiddenBoothId = document.createElement("input");
	    hiddenBoothId.setAttribute("type", "hidden");
	    hiddenBoothId.setAttribute("name", "boothIds");
	  	hiddenBoothId.setAttribute("value", currentEle.parent().siblings(":nth-child(1)").text());	  	
	    formName.appendChild(hiddenBoothId);
	    formName.submit();   
    }
    
    function massPaymentSubmit(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var facilities = jQuery("#listBooths :checkbox[name='boothIds']");
        var index = 0;
        var tabItemValue;
        var methodTypeId;
        var routeId;
        var paymentDate = $("#paymentDate").val();
        jQuery.each(facilities, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var amountObj = $(domObj).find("#paymentAmount");
            	var dateObj = $(domObj).find("#paymentDate");
            	var payMethodObj = $(domObj).find("#paymentMethodTypeId");
            	var boothId = $(this).val();
            	var amt = $(amountObj).val();
            	var methodType = $(payMethodObj).val();
            	var tabItemObj = $(domObj).find("#tabItem");
            	tabItemValue = $(tabItemObj).val();
            	methodTypeId = methodType;
            	var appendStr = "";
            	if(methodTypeId == "CHALLAN_PAYIN"){
            		var bankObj = $(domObj).find("#finAccountId");
            		var finAccId = $(bankObj).val();
            		appendStr += "<input type=hidden name=finAccountId_o_"+index+" value='"+finAccId+"' />";
            	} 
            	appendStr += "<input type=hidden name=facilityId_o_"+index+" value="+boothId+" />";
            	appendStr += "<input type=hidden name=amount_o_"+index+" value="+amt+" />";
                $("#paymentSubmitForm").append(appendStr);
            }
            index = index+1;
            
        });
        
        var appStr = "<input type=hidden name=paymentMethodTypeId value="+methodTypeId+" />";
        $("#paymentSubmitForm").append(appStr);
       routeId= $("#routeFacilityId").val();
       if(routeId != "undefined" && routeId != null && routeId != ""){
       		appStr = "<input type=hidden name=routeId value="+routeId+" />";
       }
        $("#paymentSubmitForm").append(appStr);
        appStr = "<input type=hidden name=subTabItem value="+tabItemValue+" />";
        if(paymentDate != "undefined" && paymentDate != null){
    		appStr += "<input type=hidden name=paymentDate value='"+ paymentDate +"' />";
    	}
        
        $("#paymentSubmitForm").append(appStr);
    	jQuery('#paymentSubmitForm').submit();
    }
    
    function getPaymentTotal(){
    	var payAmount = 0;
    	var checkedBoothIds = jQuery("input[name='boothIds']:checked");
    	var facilities = jQuery("#listBooths :checkbox[name='boothIds']");
        jQuery.each(facilities, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var amountObj = $(domObj).find("#paymentAmount");
            	var amt = parseFloat($(amountObj).val());
            	if(amt != undefined || amt != null){
            		payAmount = payAmount+amt;
            	}
            }
            
        });
        var str = payAmount+" ( "+checkedBoothIds.size()+" ) ";
        jQuery('#showPaymentTotal').html(str);
    }
    
    function recalcAmounts(){
    	getBoothRunningTotal();
    	
    	var checkedBoothIds = jQuery("input[name='boothIds']:checked");
		jQuery.each(checkedBoothIds, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var amountObj = $(domObj).find("#currDue");
            	var currValue = $(amountObj).val();
            	var paymentObj = $(domObj).find("#paymentAmount");
            	var checkEntry = $(paymentObj).val();
            	if(checkEntry == null || checkEntry == undefined || checkEntry == ""){
            		$(paymentObj).val(currValue);
            	}
            }
        });
        var unCheckedBoothIds = jQuery("input[name='boothIds']:unchecked");
        jQuery.each(unCheckedBoothIds, function() {
            if (jQuery(this).is(':unchecked')) {
            	var domObj = $(this).parent().parent();
            	var paymentObj = $(domObj).find("#paymentAmount");
            	$(paymentObj).val('');
            }
        });
        getPaymentTotal();
        
    }
        
//]]>
</script>
<set field="screenFlag" value=""/>
<#if screenFlag?exists && screenFlag="MyPortalCashierScreen" >
<form name="paymentSubmitForm" id="paymentSubmitForm" method="post" action="cashierPortalMakeMassPayments">
</form>
<#else>
<form name="paymentSubmitForm" id="paymentSubmitForm" method="post" action="makeMassPayments">
</form>
</#if>

<#if boothPaymentsList?has_content>
  <div>
    <span class="label">Total Booths:${boothPaymentsList?size}</span>  
    <span class="label"> Today's Due Total  (No.of selected Booths) :</span>
    <span class="label" id="showBoothRunningTotal"></span>
    <span class="label"> Payment Amount Total :</span>
    <span class="label" id="showPaymentTotal"></span>
  </div>
  <form name="listBooths" id="listBooths"  method="post" action="makeMassPayments">
    <div align="right">
    	<#if parameters.paymentMethodTypeId == "CHALLAN_PAYIN">
          		<span class="label"> Payment Date:</span><input class='h3' type='text' id='paymentDate' name='paymentDate' value='${defaultEffectiveDate?if_exists}' onmouseover='datepick()'/>
         </#if>     
      <#if parameters.paymentMethodTypeId?exists && parameters.paymentMethodTypeId != "CHEQUE_PAYIN"><input id="submitButton" type="button"  onclick="javascript:massPaymentSubmit(this);" value="Make Payment"/></#if>
    </div>
	
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Retailer Code<input type="hidden" name="facilityId" id="routeFacilityId" value="${parameters.facilityId?if_exists}"></td>
          <td>Retailer Name</td>
          <td>${uiLabelMap.Route}</td>
          <td>PaymentMethodType</td>
          <td>Todays Due</td>
          <td>Full Dues</td>
          <#if parameters.paymentMethodTypeId == "CHALLAN_PAYIN">
          		<td>Bank</td>
          </#if>
          <#if parameters.paymentMethodTypeId != "CHEQUE_PAYIN">
          		<td>Payment Amount</td>
          		<td align="right">${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllFacilities" name="checkAllFacilities" onchange="javascript:toggleFacilityId(this);"/></td>
          <#else>
          		<td>Make Payment</td>
          </#if>		
          
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list boothPaymentsList as payment>
        	<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : payment.facilityId}, false)>   
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="${parameters.paymentMethodTypeId?if_exists}">
            	<input type="hidden" name="currDue" id="currDue" value="${payment.grandTotal?if_exists}">
            	<input type="hidden" name="tabItem" id="tabItem" value="${parameters.subTabItem?if_exists}">
            	<#if parameters.paymentMethodTypeId == 'CHEQUE_PAYIN'>
            		<input type="hidden" name="routeId" id="routeId" value="${payment.routeId?if_exists}">
            	</#if>
            	<input type="hidden" name="tabItem" id="tabItem" value="${parameters.subTabItem?if_exists}">
              <td>${(payment.facilityId)?if_exists}</td>
              <td>${facilityDetails.get('facilityName')?if_exists}</td>
              <td>               
                ${(payment.routeId)?if_exists}
              </td>
              <td>               
                ${(payment.paymentMethodTypeDesc)?if_exists}
              </td>
              <td><@ofbizCurrency amount=payment.grandTotal isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><input type="button" name="viewDues" id="pastDues" value="View Details" onclick="javascript:showRetailerDueHistory('${payment.facilityId}');"/></td>
              <#if parameters.paymentMethodTypeId == "CHALLAN_PAYIN">
              	<#assign selectVal = "">
              	<#if partyFinAccMap?has_content && (partyFinAccMap.get(payment.facilityId)?exists)>
              		<#assign selectVal = partyFinAccMap.get(payment.facilityId)>	
              	</#if>
              	
          		<td><select name='finAccountId' id='finAccountId' class='h4'>
					<#if accountNameList?has_content><#list accountNameList as eachBank><option value='${eachBank.finAccountId?if_exists}' <#if selectVal?has_content && selectVal.finAccountCode == eachBank.finAccountCode>selected</#if>>${eachBank.finAccountName?if_exists}</option></#list></#if>            
				</select></td>
          	  </#if>
              <#if parameters.paymentMethodTypeId != "CHEQUE_PAYIN">
              		<td><input type="text" name="paymentAmount" id="paymentAmount" onchange="javascript: getPaymentTotal();"></td>
              		<td>${(payment.facilityId)?if_exists}<input type="checkbox" id="facilityId_${payment_index}" name="boothIds" value="${payment.facilityId}" onclick="javascript:recalcAmounts();"/></td>
              <#else>
              		<td><input id="submitButton" type="button"  onclick="javascript:showPaymentEntry('${payment.routeId}','${payment.facilityId}' ,'${payment.grandTotal}', '${parameters.paymentMethodTypeId}', '${facilityDetails.get("facilityName")}');" value="Make Payment"/></td>
              </#if>
	          
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Payments Found</h3>
</#if>
