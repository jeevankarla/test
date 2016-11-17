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
	
  	  	
	function removeInvoiceItem(invoiceItemSeqId, taxAuthPartyId, invoiceId) {
		$.ajax({
			 type: "POST",
             url: 'quickRemoveInvoiceItemAndTaxAjax',
             data: {invoiceItemSeqId : invoiceItemSeqId,
             		taxAuthPartyId : taxAuthPartyId,
             		invoiceId : invoiceId},
             dataType: 'json',
	            
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
                    alert('Error Fetching available batches');
				}else{
					location.reload(true);				      	
				}								 
			},
			error: function(){
				alert("error");
			}							
		});
		return false;
	}
		
	function saveInvoiceItem() {
		
		if( ($("#amount").val()).length < 1 ) {
	    	$('#amount').css('background', 'red'); 
	       	setTimeout(function () {
	           	$('#amount').css('background', 'white').focus(); 
	       	}, 800);
	    	return false;
	    }
		
		var productId;
  		var quantity;
  		var description;
  		var amount;
  		var invoiceId = "${invoiceId}";
		var saveRow = $('#save').parent().parent();
		var invoiceItemTypeId = $('#invoiceItemTypeId :selected').val();
		var costCenterId = $('#costCenterId :selected').val();  
		saveRow.find('td').each (function() {
		    var eachTd = $(this);
		    eachTd.find('input').each (function() {
		   		var name = $(this).attr('name');
		   		if(name == "description"){
		   			description = $(this).val();
		   		}
		   		if(name == "amount"){
		   			amount = $(this).val();
		   		}
		    });
		   
		}); 
		var data= {invoiceItemTypeId : invoiceItemTypeId,
             		description : description,
             		amount : amount,
                    invoiceId: invoiceId,
                    costCenterId: costCenterId};
                    
		$.ajax({
			 type: "POST",
             url: 'quickCreateInvoiceItemAndTaxAjax',
             
             data: data,
             dataType: 'json',
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
                    alert('Error Creating Invoice Item');
				}else{
					location.reload(true);
				}								 
			},
			error:function(){
			return false;
			   alert("error");
			}
										
		});
		return false;
	}
	
	
	
	
	
</script>
	
	<div class="screenlet">
		 <div class="screenlet-title-bar">
		    <ul>
		      <li class="h3">Invoice Items</li>
		    </ul>
		    <br class="clear"/>
		 </div>
		 <div class="screenlet-body">
		 		<form name="listInvoiceItems" id="listInvoiceItems"  method="post" action="">
				    <table id="customFields" class="basic-table hover-bar" cellspacing="0" width="10">
				      <#--<a id="addnew" href="" >add</a>-->
				      <thead>
				        <tr class="header-row-2">
				          <td>Item No</td>	
				          <#--<td>Quantity</td>-->
				          <td>Invoice Item Type</td>
				          <#--<td>ProductId</td>-->
				          <td>Cost Center Id</td>
				          <td>Description</td>
				          <td>Amount</td>
				          <td>Action</td>
				        </tr>
				      </thead>
				      <tbody>
				        <#assign alt_row = false>
				        <#list invoiceItems as invoice>
				            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				              <td><input class="input-medium" name="taxAuthPartyId" type="hidden" size="4"/>${invoice.invoiceItemSeqId}</td>
				              <#assign invoiceItemType = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" : invoice.invoiceItemTypeId}, true) />	
				              <td>${invoiceItemType.description}</td>
				              <td>${invoice.costCenterId?if_exists}</td>
				              <td>${invoice.description?if_exists}</td>
				              <td>${invoice.amount?if_exists}</td>
				              <td><a name="remove" href="" onClick="return removeInvoiceItem('${invoice.invoiceItemSeqId}', '${invoice.taxAuthPartyId?if_exists}', '${invoice.invoiceId}');">Remove</a></td>
				            </tr>
				            <#-- toggle the row color -->
				            <#assign alt_row = !alt_row>
				        </#list>
				        <tr id="addItems" type="hidden" valign="middle"<#if alt_row> class="alternate-row"</#if>>
				          <td></td>
				          <td>
							  <select name="invoiceItemTypeId" id="invoiceItemTypeId">
						         <#list invoiceItemTypes as eachInvoiceItem>
						            <option value='${eachInvoiceItem.invoiceItemTypeId}'>
						        	    ${eachInvoiceItem.description?if_exists}
						            </option>
						         </#list>
							  </select>          
				          </td>
				          <td>
				          	<select name="costCenterId" id="costCenterId">
						         <#list partyAcPrefGrpList as partyAcPrefGrp>
						            <option value='${partyAcPrefGrp.partyId}'>
						        	    ${partyAcPrefGrp.groupName?if_exists}
						            </option>
						         </#list>
							  </select>  
				          </td>
				          <td><input id="description" class="input-medium" name="description" type="text" size="60"/></td>
				          <td><input id="amount" class="input-medium" name="amount" type="text" size="12"/></td>
				          <td><button id="save" name="save" onClick="return saveInvoiceItem();" style="buttontext">Add</button></td>
				        </tr>
				      </tbody>
				    </table>
				  </form>
		 
		 </div>
		 	
	</div>
	<a style="font-size: 16px" href="<@ofbizUrl>APInvoiceOverview?invoiceId=${invoice.invoiceId}&amp;subTabButtonValue=${tabButtonItem5}</@ofbizUrl>">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Save</a>
	
  
