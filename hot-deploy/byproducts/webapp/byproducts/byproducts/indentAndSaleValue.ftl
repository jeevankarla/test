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
<script type="application/javascript">
function checkForPrintProducts(msg,size){
	if(size == 0){
		showErrorAlert("Alert", 'No Products to Display');
	} else{
		printProducts(msg);
	}
	
	
	
	
}

</script>
<#if indentAndSaleValueMap?has_content>
 <div>  
 	 <div class="screenlet">
  		<div class="screenlet-title-bar">
 	 			<h2>Indents and Parlour Sale Entries Status For  ${indentDate}</h2>
 	 		</div>
			  <table class="basic-table" cellspacing="0" cellpadding="1" border="1" border-width=".1mm" style"width :">
			    <#-- Header Begins -->
			    <thead>
				    <tr class="alternate-row">
				      <th><h2>Location</h2></th>
				      <th><h2>Outlets</h2></th>
				      <th><h2>Entries</h2></th>
				    </tr>
			    </thead>
			    <tr>
			    	<td ><h3>Nandanam (Parlour Sale)</h3></td>
			    	<td border="1" border-width=".1mm"><b>${indentAndSaleValueMap.parlourOutlets}</b></td>
			    	<td border="1" border-width=".1mm"><b>${indentAndSaleValueMap.parlourSaleParty}</b></td>
			    </tr>
			     <tr></tr>
			    <tr></tr>
			    <tr>
			    	<td><h3>Control Room (Indents)</h3></td>
			    	<td><b>${indentAndSaleValueMap.totalOutlets}</b></td>
			    	<td><b>${indentAndSaleValueMap.indentedParty}</b></td>
			    </tr>
			  </table>
			  </div>
			 <div class="screenlet">
  			<div class="screenlet-title-bar">
 	 			<h2>Indented and NonIndented Products For ${indentDate}</h2>
 	 		</div>
			 
			 <table class="basic-table" cellspacing="1" cellpadding="1">
			    <thead>
				    <tr class="alternate-row">
				      <th><h2>No.Of Products</h2></th>
				      <th><h2>Indented</h2></th>
				      <th><h2>Not Indented</h2></th>
				    </tr>
			    </thead>
			    <tr>
			    	<td><b>${productsMap.noOfProducts-parlourProducts?if_exists}</b></td>
			    	<td><button name = "indented" id='indentsEntered' class='submit'  onclick="javascript:checkForPrintProducts('Indented','${productsMap.indentedProducts}');" >${productsMap.indentedProducts}</button> </td>
			    	<td><button name = "notIndented" id='notIndentsEntered' class='submit'  onclick="javascript:checkForPrintProducts('Not Indented','${productsMap.noOfProducts-productsMap.indentedProducts-parlourProducts}');" >${productsMap.noOfProducts-productsMap.indentedProducts-parlourProducts}</button> </td>
			    </tr>
			  </table>
 	</div>
 </div>  
<#else>
  <h3>${uiLabelMap.CommonNoRecordFound}</h3>
</#if>
