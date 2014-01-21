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

<#if indentAndSaleValueMap?has_content>
<div>  
	<div class="screenlet">  
		 <div class="screenlet-title-bar">
 			<h2>Payment Values For ${indentDate}</h2>
 		 </div>
		 <table class="basic-table hover-bar" cellspacing="1" cellpadding="1">
		    <thead>
			    <tr class="alternate-row">
			      <th></th>
			      <th><h2>Total</h2></th>
			      <th><h2>Payments Entered</h2></th>
			      <#--<th><h2>Pending</h2></th>-->
			    </tr>
		    </thead>
		    <tr>
		    	<td><h3>Despatch Outlets</h3></td>
		    	<td><b>${indentAndSaleValueMap.indentedParty}</b></td>
		    	<td><b>${paymentsMap.indPayments}  </b></td>
		    	<#--<td><b>${indentAndSaleValueMap.indentedParty - paymentsMap.indPayments}</b></td>-->
		    </tr>
		    <tr></tr>
		    <tr>
		    	<td><h3>Despatch Value</h3></td>
		    	<td><b>Rs. ${paymentsMap.totIndentSaleValue}</b></td>
		    	<td><b>Rs. ${paymentsMap.paidIndentAmt}</b></td>
		    	<#--<#assign pendingAmt = paymentsMap.totIndentSaleValue-paymentsMap.paidIndentAmt>
		    	<#if pendingAmt &gt; 0>
		    		<td><b>Rs. ${pendingAmt}</b></td>
		    	<#else>
		    		<td><b>Rs. 0</b></td>
		    	</#if>-->
		    </tr>
		  </table>
 	</div>
</div>  
<#else>
  <h3>${uiLabelMap.CommonNoRecordFound}</h3>
</#if>
