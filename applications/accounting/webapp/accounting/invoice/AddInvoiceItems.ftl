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
	
  	$(document).ready(function(){
  	
  		var productId;
  		var quantity;
  		
  		var description;
  		var amount;
  		var bedPercent;
  		var vatPercent;
  		var cstPercent;
  		var invoiceId = "${invoiceId}";
  		
  		<#--
  		
  		$('#save').click(function() {
  			var saveRow = $(this).parent().parent();
  			var invoiceItemTypeId = $('#invoiceItemTypeId :selected').val();  
  			saveRow.find('td').each (function() {
			    var eachTd = $(this);
			    eachTd.find('input').each (function() {
			   		
			   		var name = $(this).attr('name');
			   	//	if(name == "productId"){
			   	//		productId = $(this).val();
			   	//	}
			   	//	if(name == "quantity"){
			   	//		quantity = $(this).val();
			   	//	}
			   		
			   		if(name == "description"){
			   			description = $(this).val();
			   		}
			   		if(name == "amount"){
			   			amount = $(this).val();
			   		}
			   		if(name == "bedPercent"){
			   			bedPercent = $(this).val();
			   		}
			   		if(name == "vatPercent"){
			   			vatPercent = $(this).val();
			   		}
			   		if(name == "cstPercent"){
			   			cstPercent = $(this).val();
			   		}
			   
			    });
			   
			}); 
			
  			$.ajax({
				 type: "POST",
	             url: 'quickCreateInvoiceItemAndTaxAjax',
	             
	             data: {//productId : productId,
	             		//quantity : quantity,
	             		invoiceItemTypeId : invoiceItemTypeId,
	             		description : description,
	             		amount : amount,
	             		bedPercent : bedPercent,
	             		vatPercent : vatPercent,
	             		cstPercent : cstPercent,
	                    invoiceId: invoiceId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Creating Invoice Item');
					}else{
						alert('Invoice item successfully Created');
					}								 
				},
				success:function(json){
					if(!json.error) location.reload(true);
					$(document).ajaxStop(function() { location.reload(true); });
				},
				error: function(){
					alert("============================")
				}							
			});
			 return false;
		});	
			
		-->	
	
  		<#--
  			
  		$('#addnew').click(function() {
			
		    // increment the counter
		    newRowNum = $(customFields).children('tbody').children('tr').length + 1;
		    var addRow = $('#customFields tr:last');
			
		    var newRow = addRow.clone();
		    $('input', addRow).val('');
		
			//$('td:last-child', newRow).html('<a href="" class="remove"><i class="icon-minus">Remove<\/i><\/a>');
		
		    addRow.before(newRow);
		
		    // add the remove function to the new row
		    $('a.remove', newRow).click(function() {
		        $(this).closest('tr').remove();
		        return false;
		    });
		
		    return false;
		});
  		
  		-->
  	
  	}); 
  	
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
			var productId;
	  		var quantity;
	  		var description;
	  		var amount;
	  		var bedPercent;
	  		var vatPercent;
	  		var cstPercent;
	  		var invoiceId = "${invoiceId}";
	  		//alert($(this).id);
			var saveRow = $('#save').parent().parent();
  			var invoiceItemTypeId = $('#invoiceItemTypeId :selected').val();  
  			saveRow.find('td').each (function() {
			    var eachTd = $(this);
			    eachTd.find('input').each (function() {
			   		
			   		var name = $(this).attr('name');
			   	//	if(name == "productId"){
			   	//		productId = $(this).val();
			   	//	}
			   	//	if(name == "quantity"){
			   	//		quantity = $(this).val();
			   	//	}
			   		
			   		if(name == "description"){
			   			description = $(this).val();
			   		}
			   		if(name == "amount"){
			   			amount = $(this).val();
			   		}
			   		if(name == "bedPercent"){
			   			bedPercent = $(this).val();
			   		}
			   		if(name == "vatPercent"){
			   			vatPercent = $(this).val();
			   		}
			   		if(name == "cstPercent"){
			   			cstPercent = $(this).val();
			   		}
			   
			    });
			   
			}); 
			var data= {invoiceItemTypeId : invoiceItemTypeId,
	             		description : description,
	             		amount : amount,
	             		bedPercent : bedPercent,
	             		vatPercent : vatPercent,
	             		cstPercent : cstPercent,
	                    invoiceId: invoiceId};
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

  <form name="listInvoiceItems" id="listInvoiceItems"  method="post" action="">
    
    <table id="customFields" class="basic-table hover-bar" cellspacing="0" width="10">
      <#--<a id="addnew" href="" >add</a>-->
      <thead>
        <tr class="header-row-2">
          <td>Item No</td>	
          <#--<td>Quantity</td>-->
          <td>Invoice Item Type</td>
          <#--<td>ProductId</td>-->
          <td>Description</td>
          <td>Amount</td>
          <td>Excise(%)</td>
          <td>Vat(%)</td>
          <td>Cst(%)</td>
          <td>Action</td>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list invoiceItems as invoice>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><input class="input-medium" name="taxAuthPartyId" type="hidden" size="4"/>${invoice.invoiceItemSeqId}</td>
              <#--<td>${invoice.quantity}</td>-->
              <td>${invoice.invoiceItemTypeId}</td>
              <#--<td>${invoice.productId}</td>-->
              <td>${invoice.description?if_exists}</td>
              <td>${invoice.amount?if_exists}</td>
              <td>${invoice.bedPercent?if_exists}</td>
              <td>${invoice.vatPercent?if_exists}</td>
              <td>${invoice.cstPercent?if_exists}</td>
              <td><a name="remove" href="" onClick="return removeInvoiceItem('${invoice.invoiceItemSeqId}', '${invoice.taxAuthPartyId?if_exists}', '${invoice.invoiceId}');">Remove</a></td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
        <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
          <td></td>
          <#--<td><input id="quantity" class="input-medium" name="quantity" type="text" size="4"/></td>-->
          <td>
			  <select name="invoiceItemTypeId" id="invoiceItemTypeId">
		         <#list invoiceItemTypes as eachInvoiceItem>
		            <option value='${eachInvoiceItem.invoiceItemTypeId}' selected="selected">
		        	    ${eachInvoiceItem.description?if_exists}
		            </option>
		         </#list>
			  </select>          
          </td>
          <#--<td>
          	<@htmlTemplate.lookupField value="${productId?if_exists}" formName="listInvoiceItems" name="productId" id="productId" fieldFormName="LookupProduct"/>
          </td>-->
          <td><input id="description" class="input-medium" name="description" type="text" size="60"/></td>
          <td><input id="amount" class="input-medium" name="amount" type="text" size="6"/></td>
          <td><input id="bedPercent" class="input-medium" name="bedPercent" type="text" size="3"/></td>
          <td><input id="vatPercent" class="input-medium" name="vatPercent" type="text" size="3"/></td>
          <td><input id="cstPercent" class="input-medium" name="cstPercent" type="text" size="3"/></td>
          <td><button id="save" name="save" onClick="return saveInvoiceItem();" style="buttontext">Save</button></td>
        </tr>
      </tbody>
    </table>
  </form>
