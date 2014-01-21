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
<style type="text/css">
	.btn{
  		color: #000;
  		background: #C9C8CE;
  		font-weight: bold;
  		border: 1px solid #F2F2F2;
	}
 
	.btn:hover {
  		color: #FFF;
  		background: #F36506;
	}
</style>			
<script>

jQuery(function(){
	accumulateTotals();	
	calculateShortAmount();
});

function checkForm(form){
	var table = document.getElementById('dealerDues');
	var rowLength = table.rows.length;
	if(rowLength > 2){
		return true;
	}
	else{
		return false;
	}
}
function accumulateTotals(){
	var table = document.getElementById('dealerDues');
	var rowLength = table.rows.length;
	var totalAmount = 0;
	for(var i=2; i<rowLength; i+=1){
		var amountId = "amount_"+(i-2);
		var pastAmountId = "pastAmount_"+(i-2);
  		var eachAmount = document.getElementById(amountId).value;
  		var eachPastAmount = document.getElementById(pastAmountId).value;
  		var pastAmt = parseFloat(eachPastAmount);
  		if(pastAmt > 0 && !isNaN(pastAmt)){
  			totalAmount += pastAmt;
  		}
  		totalAmount += parseFloat(eachAmount);
  		
	}
	document.getElementById("paidAmt").value = totalAmount;
}

function calculateShortAmount(){
	var paidAmt = document.getElementById("paidAmt").value;
	var table = document.getElementById('dealerDues');
	var rowLength = table.rows.length;
	var totalAmount = 0;
	for(var i=2; i<rowLength; i+=1){
		var amountId = "amount_"+(i-2);
		var pastAmountId = "pastAmount_"+(i-2);
  		var eachAmount = document.getElementById(amountId).value;
  		var eachPastAmount = document.getElementById(pastAmountId).value;
  		var pastAmt = parseFloat(eachPastAmount);
  		if(pastAmt > 0 && !isNaN(pastAmt)){
  			totalAmount += pastAmt;
  		}
  		totalAmount += parseFloat(eachAmount);
	}
	if(totalAmount > paidAmt){
		document.getElementById("shortAmt").value = totalAmount-paidAmt;
	}
	else{
		document.getElementById("shortAmt").value = 0;
	}
}	      
       
</script>
</br>
<form action="<@ofbizUrl>makeTransporterPayment</@ofbizUrl>" name="EditTransporterPaymentForm" id="EditTransporterPaymentForm" method="post" style='margin: 0;'>
		
	<input type= "hidden" name="partyCode" value="${partyCode?if_exists}"/>
	<table class="basic-table hover-bar" cellspacing='0' width="100%" id="dealerDues">
		<tr>
			<td width="20%" style="text-align: center;"></td>
		    <td width="10%" style="text-align: center;"></td>
		    <td width="10%" style="text-align: center;font-size:8pt;font-weight:bold;">Transporter Remittance</td>
		    <td width="10%"><input type="text" name="paidAmt" id="paidAmt" size="20pt" onchange="calculateShortAmount()"/></td>
		    <td width="10%" style="text-align: center;font-size:8pt;font-weight:bold;">Short Payment</td>
		    <td width="10%"><input type="text" name="shortAmt" id="shortAmt" size="20pt" readonly/></td>
		    <td width="10%"><input type="submit" id="submitButton1" value="Make Payment" onClick="javascript: return checkForm(this);"></td>
		</tr>
		    
		<tr class="header-row-2">
			<td width="20%" style="text-align: center;">Dealer</td>
			<td width="10%" style="text-align: center;">Today's Due</td>
			<td width="10%" style="text-align: center;">Total Due</td>
			<td width="10%" style="text-align: center;">Details</td>
			<td width="10%" style="text-align: center;">Payment Method</td>
			<td width="10%" style="text-align: center;">Today Amount</td>
			<td width="10%" style="text-align: center;">Past Due Amount</td>
		</tr>
		<#assign alt_row = false>
		<#if boothPaymentsList?has_content>
			<#list boothPaymentsList as eachDealerDues>
				<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
					<#assign facility = (delegator.findOne("Facility", {"facilityId" : eachDealerDues.get("facilityId")}, true))?if_exists />
					<input type="hidden" name="facilityId_o_${eachDealerDues_index}" id="facilityId_${eachDealerDues_index}" value="${eachDealerDues.get("facilityId")?if_exists}">
					<td class="label" style="text-align: left;">${facility.get("facilityName")?if_exists}</td>
					<td class="label"><input type="hidden" id="todayDue" name="todayDue" value="${eachDealerDues.get('grandTotal')}"/>Rs. ${eachDealerDues.get("grandTotal")?if_exists?string("##0.00")}</td>
					<td class="label">Rs. ${eachDealerDues.get("totalDue")?if_exists?string("##0.00")}</td>
					<td style="text-align: center;"><a class="btn" href="javascript:showPaymentHistory(${eachDealerDues.get('facilityId')});">&nbsp;&nbsp; VIEW &nbsp;&nbsp;</a></td>
					<td class="label" style="text-align: center;"><input type="hidden" id="paymentMethodTypeId_${eachDealerDues_index}" name="paymentMethodTypeId_o_${eachDealerDues_index}" value="CASH_PAYIN" /><#if eachDealerDues.get("paymentMethodType")?has_content>${eachDealerDues.get("paymentMethodType")}<#else>CASH</#if></td>
					<td style="text-align: center;"><input type="text" name="amount_o_${eachDealerDues_index}" id="amount_${eachDealerDues_index}" size="20pt" onchange="accumulateTotals()" value ="${eachDealerDues.get("grandTotal")?if_exists}"/></td>
					<td style="text-align: center;"><input type="text" name="pastAmount_o_${eachDealerDues_index}" id="pastAmount_${eachDealerDues_index}" size="20pt" onchange="accumulateTotals()"/></td>
				</tr>
		 		<#assign alt_row = !alt_row>
			</#list>
		</#if>
		    
	</table>
</form>
