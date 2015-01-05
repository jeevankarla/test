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
        recalcAmounts();
    }
    
    function datepick()
	{		
	
		$( "#paymentDate" ).datetimepicker({
			dateFormat:'dd MM, yy',
			timeFormat: 'hh:mm:ss',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});
			
		$( "#instrumentDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});
			
		
			
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	
	function recalcAmounts(){
    	
    	var checkedPaymentIds = jQuery("input[name='paymentIds']:checked");
		var runningTotalVal = 0;
        if(checkedPaymentIds.size() > 0) {
        	jQuery.each(checkedPaymentIds, function() {
            	if (jQuery(this).is(':checked')) {
            		var domObj = $(this).parent().parent();
            		var amountObj = $(domObj).find("#amount");
            		var currValue = parseFloat($(amountObj).val());
            		runningTotalVal = runningTotalVal+currValue;
	            }
    	    });
        }
        $('#payAmount').val(runningTotalVal);
        
    }
        
    function processPayments(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var payments = jQuery("#listPayments :checkbox[name='paymentIds']");
        var index = 0;
        var paymentDate = $("#paymentDate").val();
        var paymentMethodId = $("#paymentMethodId").val();
        var issuingAuthority = $("#issuingAuthority").val();
        var instrumentDate = $("#instrumentDate").val();
        var paymentRefNum = $("#paymentRefNum").val();
        var inFavor = $("#inFavor").val();
        var paymentGroupTypeId = $("#paymentGroupTypeId").val();
        var amount = $("#payAmount").val();
        var finAccountId = $("#finAccountId").val();
        jQuery.each(payments, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var paymentObj = $(domObj).find("[name='paymentId']");
            	var paymentId = $(paymentObj).val();
            	var appendStr = "<input type=hidden name=paymentId_o_"+index+" value="+paymentId+" />";
                $("#processPaymentsForm").append(appendStr);
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
    	appStr += "<input type=hidden name=amount value='"+ amount +"' />";
    	appStr += "<input type=hidden name=finAccountId value='"+ finAccountId +"' />";
    	$("#processPaymentsForm").append(appStr);
    	jQuery('#processPaymentsForm').submit();
    }
        
//]]>
</script>
<form name="processPaymentsForm" id="processPaymentsForm" method="post" action="processGroupPaymentDeposit">
</form>

<#if paymentDetailsList?has_content>
  
  <form name="listPayments" id="listPayments"  method="post" >
    <div align="right" width="100%">
    	
    	<table width="100%">
    		<tr>
    			<td><span class="label">Payment Method:</span><select class='h3' name="paymentMethodId" id="paymentMethodId"><#list paymentMethods as eachMethod><option value="${eachMethod.paymentMethodId?if_exists}">${eachMethod.description?if_exists}</option></#list></select></td>
    			<td><span class="label">Cheque No :</span><input class='h3' type='text' id='paymentRefNum' name='paymentRefNum'/></td>
    			<td><span class="label"> Cheque Date :</span><input class='h3' type='text' id='instrumentDate' name='instrumentDate' value='${defaultEffectiveDate?if_exists}' onmouseover='datepick()'/></td>
    			<td><span class="label">Cheque in Favor (<font color='red'>*</font>):</span><input class='h3' type='text' id='inFavor' name='inFavor'/></td>	
    		</tr>
    		<tr>
    			<td><span class="label"> Issuing Authority:</span>
    				<#if finAccountDetail?has_content>
    					<input type='hidden' name='finAccountId' id='finAccountId' value='${finAccountDetail.finAccountId?if_exists}'>
    					<input class='h3' type='text' id='issuingAuthority' name='issuingAuthority' size='30' value='${finAccountDetail.finAccountName?if_exists}' readonly/>
    				<#else>
    					<input class='h3' type='text' id='issuingAuthority' name='issuingAuthority'/>
    				</#if>
    			</td>
    			<input type="hidden" name="paymentGroupTypeId" id="paymentGroupTypeId" value="${roleTypeId?if_exists}">
    			<td><span class="label"> Amount:</span><input class='h3' type='text' id='payAmount' name='payAmount' readonly/></td>
    			<td><span class="label"> Payment Date (<font color='red'>*</font>):</span><input class='h3' type='text' id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td>
    			<#if security.hasEntityPermission("BATCHPAY", "_CREATE", session)>
    				<td><input id="submitButton" type="button"  onclick="javascript:processPayments(this);" value="Make Payment"/></td>
    			</#if>
    		</tr>
    	</table>
    	
    </div>
	<br/>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Party Code</td>
          <td>Party Name</td>
          <td>Payment Id</td>
          <td>Payment Date</td>
          <td>Amount</td>
		  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllPayments" name="checkAllPayments" onchange="javascript:togglePaymentId(this);"/></td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list paymentDetailsList as eachPay>
      		<tr <#if alt_row> class="alternate-row"</#if>>
      			<input type="hidden" name="paymentId" id="paymentId" value="${eachPay.paymentId?if_exists}"/>
            	<td>${eachPay.partyId?if_exists}</td>
            	<td>${eachPay.partyName?if_exists}</td>
            	<td>${eachPay.paymentId?if_exists}</td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachPay.paymentDate, "dd MMMM, yyyy")?if_exists}</td>
              	<td><input type="text" name="amount" id="amount" value="${eachPay.amount?if_exists}" align="middle" readonly/></td>
           		<td><input type="checkbox" id="paymentId_${eachPay_index}" name="paymentIds" value="${eachPay.paymentId}" onclick="javascript:recalcAmounts();"/></td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Payments Found</h3>
</#if>
