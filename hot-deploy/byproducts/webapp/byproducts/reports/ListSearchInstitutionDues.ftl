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
  
  <form name="listBooths" id="listBooths"  method="post" action="makeMassPayments">
    
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Code<input type="hidden" name="facilityId" id="routeFacilityId" value="${parameters.facilityId?if_exists}"></td>
          <td>Name</td>
          <td>Total Dues</td>
          <td>View Details</td>
	   	  <td>Make Payment</td>
          
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list boothPaymentsList as payment>
        	<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : payment.facilityId}, false)>   
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<input type="hidden" name="currDue" id="currDue" value="${payment.grandTotal?if_exists}">
            	<input type="hidden" name="tabItem" id="tabItem" value="${parameters.subTabItem?if_exists}">
              <td>${(payment.facilityId)?if_exists}</td>
              <td>${facilityDetails.get('facilityName')?if_exists}</td>
              <td><@ofbizCurrency amount=payment.totalDue isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><input type="button" name="viewDues" id="pastDues" value="View Details" onclick="javascript:showRetailerDueHistory('${payment.facilityId}');"/></td>
       		  <td><input id="submitButton" type="button"  onclick="javascript:showPaymentEntryQTip('${payment.facilityId}' ,'${payment.totalDue}', '${facilityDetails.facilityName}');" value="Make Payment"/></td>
	          
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
