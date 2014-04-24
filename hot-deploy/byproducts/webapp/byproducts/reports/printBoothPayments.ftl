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
        var facilities = jQuery("#listBooths :checkbox[name='paymentIds']");

        jQuery.each(facilities, function() {
            this.checked = master.checked;
        });
        //getBoothRunningTotal();
    }

    function getBoothRunningTotal() {
		var checkedPaymentIds = jQuery("input[name='paymentIds']:checked");
        if(checkedPaymentIds.size() > 0) {
            jQuery.ajax({
                url: 'getBoothDuesRunningTotal',
                type: 'POST',
                async: true,
                data: jQuery('#listBooths').serialize(),
                success: function(data) { jQuery('#showBoothRunningTotal').html(data.boothRunningTotal + '  (' + checkedPaymentIds.size() + ')') }
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
        

        var facilities = jQuery("#listBooths :checkbox[name='paymentIds']");
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
    
    function setVoidPaymentParameters(currentPayment){
    	jQuery(currentPayment).attr( "disabled", "disabled");
    	var currentEle = jQuery(currentPayment);
    	
    	formName=document.forms['cancelPayment'];
    	var domObj = $(currentEle).parent().parent();
        var rowObj = $(domObj).html();
        var method = $(domObj).find("#paymentMethodTypeId");
        var payment = $(domObj).find("#paymentId");
        var tabItemObj = $(domObj).find("#tabItem");
        var methodValue = $(method).val();
        var payId = $(payment).val(); 
        var tabValue = $(tabItemObj).val(); 
        var appendStr = "<input type=hidden name=paymentMethodTypeId value="+methodValue+" />";  
        $("#cancelPayment").append(appendStr);  
        appendStr = "<input type=hidden name=subTabItem value="+tabValue+" />";
        $("#cancelPayment").append(appendStr);  	    	
    	appendStr = "<input type=hidden name=paymentId value="+payId+" />";    		
	    $("#cancelPayment").append(appendStr); 
	     $("#cancelPayment").submit();	
	   
    
    }
    function setPrintPaymentParameters(currentPayment){
    	jQuery(currentPayment).attr( "disabled", "disabled");
    	var currentEle = jQuery(currentPayment);
    	formName=document.forms['listBooths'];	
		var hiddenpaymentId = document.createElement("input");
	    hiddenpaymentId.setAttribute("type", "hidden");
	    hiddenpaymentId.setAttribute("name", "paymentId");	   
	    hiddenpaymentId.setAttribute("value", currentEle.parent().siblings(":nth-child(1)").children(":first-child").val());
	    formName.appendChild(hiddenpaymentId);
	    formName.submit();		  
    
    }
    
//]]>
</script>

<#if boothPaymentsList?has_content>
  <form name="cancelPayment" id="cancelPayment"  method="post" action="cancelBoothPayment">
  </form>
  <form name="listBooths" id="listBooths"  method="post" action="massPrintReceipt" target="_blank">
    <div align="right">
      <input id="submitButton" type="button"  onclick="javascript:jQuery('#listBooths').submit();" value="Print Receipt"/>
    </div>

    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td/>
          <td>RetailerId</td>
          <td>Retailer Name</td>
          <td>Payment Time</td>
          <td>${uiLabelMap.Route}</td>
          <td>${uiLabelMap.paymentLocation}</td>
          <td>${uiLabelMap.Amount}</td>
          <td>PaymentMethod Type</td>
          <td>Cancel</td>          
          <td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllFacilities" name="checkAllFacilities" onchange="javascript:togglePaymentId(this);" checked="true"/></td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list boothPaymentsList as payment>
        	       
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="${payment.paymentMethodTypeId?if_exists}">   
        		<input type="hidden" name="tabItem" id="tabItem" value="${parameters.subTabItem?if_exists}"> 
            	<td><input id="paymentId" type="hidden"  value="${payment.paymentId}"/></td>            
              <td>${(payment.facilityId)?if_exists}</td>
              <#assign facility = delegator.findOne("Facility", {"facilityId" : payment.facilityId}, false) />
              <td>${(facility.facilityName)?if_exists}</td>
              <td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.paymentDate ,"dd/MM/yyyy HH:mm:ss")}</td> 
              <td>               
                ${(payment.routeId)?if_exists}
              </td>
               <td>               
                ${(payment.paymentLocation)?if_exists}
              </td>
              <td><@ofbizCurrency amount=payment.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td>  
              	<#if (payment.paymentMethodTypeId)?has_content>
              		<#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true) />
              		 ${(paymentMethodType.description)?if_exists}
              	</#if>
              </td>
            <#--  <td><a class="buttontext" href="<@ofbizUrl>massPrintReceipt?paymentId=${payment.paymentId}</@ofbizUrl>">Print Receipt</a></td> 
              <td><a class="buttontext" href="javascript:setVoidPaymentParameters();">Cancel</a></td>   -->
              <td><#if hasPaymentCancelPermission>
              			 <input id="submitButton" type="button"  onclick="javascript:setVoidPaymentParameters(this);" value="Cancel"/> 
              	  </#if>
              </td>                     
              <td><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId}"  checked="true"/></td>
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
