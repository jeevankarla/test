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
        getBoothRunningTotal();
    }

    function getBoothRunningTotal() {
		var checkedBoothIds = jQuery("input[name='boothIds']:checked");
        if(checkedBoothIds.size() > 0) {
            jQuery.ajax({
                url: 'getBoothDuesRunningTotal',
                type: 'POST',
                async: true,
                data: jQuery('#listBooths').serialize(),
                success: function(data) { jQuery('#showBoothRunningTotal').html(data.boothRunningTotal + '  (' + checkedBoothIds.size() + ')') }
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
    	jQuery('#listBooths').submit();
    }
    
    
//]]>
</script>

<#if boothPaymentsList?has_content>
  <div>
    <span class="label">Total Booths:${boothPaymentsList?size}</span>  
    <span class="label"> Running Total  (No.of selected Booths) :</span>
    <span class="label" id="showBoothRunningTotal"></span>
  </div>
  <form name="listBooths" id="listBooths"  method="post" action="massMakeBoothPayments">
 <!--   <div align="right">     
      <input id="submitButton" type="button"  onclick="javascript:massPaymentSubmit(this);" value="Make Payment"/>
    </div>
-->
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>${uiLabelMap.OrderFacilityBooth}</td>
          <td>${uiLabelMap.Route}</td>
            <td>PaymentMethodType</td>
          <td>${uiLabelMap.DueAmount}</td>
          <td>${uiLabelMap.makePayment}</td> 
        <!-- <td align="right">${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllFacilities" name="checkAllFacilities" onchange="javascript:toggleFacilityId(this);"/></td> -->
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list boothPaymentsList as payment>          
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>${(payment.facilityId)?if_exists}</td>
              <td>               
                ${(payment.routeId)?if_exists}
              </td>
              <td>               
                ${(payment.paymentMethodType)?if_exists}
              </td>
              <td><@ofbizCurrency amount=payment.grandTotal isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td><input id="submitButton" type="button"  onclick="javascript:showPaymentEntry('${payment.facilityId}' ,'${payment.grandTotal}', '${payment.paymentMethodType}');" value="Make Payment"/></td>
             <!-- <td>${(payment.facilityId)?if_exists}<input type="checkbox" id="facilityId_${payment_index}" name="boothIds" value="${payment.facilityId}" onclick="javascript:getBoothRunningTotal();"/></td> -->
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
