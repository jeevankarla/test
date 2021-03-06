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
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>

<script type="text/javascript">

	function dialogue(content, title) {
		
		$('<div />').qtip(
		{
			content: {
				text: content,
				title: title
			},
			position: {
				my: 'center', at: 'center', // Center it...
				target: $(window) // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: true, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
	
	function Alert(message, title)	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	function cancelForm(){
		return false;
	}
	
	function setLessTaxParameters() {
	
		formName=document.forms['listInvoiceItems'];
		 var tdsTypeId=$( "#tdsTypeId option:selected" ).val();
		var tdsAmount=jQuery('input[name=tdsAmount]').val();
		 tdsAmount = parseFloat(tdsAmount);
		 tdsAmount=-1*tdsAmount;
		 if(isNaN(tdsAmount)){
		 	tdsAmount=0;
		 }	
		 
		if(tdsAmount!=0) {		
				if(tdsAmount==0) {
			    	$('#lessTaxAmount').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#tdsAmount').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter tdsAmount');
			    	return false;
			    }
		    }
		
		//formName="listInvoiceItems";	
		var hiddenVat = document.createElement("input");
	    hiddenVat.setAttribute("type", "hidden");
	    hiddenVat.setAttribute("name", "TDS_TYPE");
	    hiddenVat.setAttribute("value",tdsTypeId);
	    formName.appendChild(hiddenVat);
	    var hiddenVatAmt = document.createElement("input");
	    hiddenVatAmt.setAttribute("type", "hidden");
	    hiddenVatAmt.setAttribute("name", "TDS_AMT");
	    hiddenVatAmt.setAttribute("value", tdsAmount);
	    formName.appendChild(hiddenVatAmt);
	    
	   
	        $('button').click();
			return false;	
    }
	function setParameters() {

		formName=document.forms['listInvoiceItems'];
		var vatper=jQuery('input[name=vatPercent]').val();
		var vatAmt=jQuery('input[name=vatAmount]').val();
		var cstPer=jQuery('input[name=cstPercent]').val();
		var cstAmt=jQuery('input[name=cstAmount]').val();
		var servPer=jQuery('input[name=serviceTaxPer]').val();
		var servAmt=jQuery('input[name=serviceTaxAmt]').val();
		var bedPer=jQuery('input[name=exiseTaxPer]').val();
		var bedAmt=jQuery('input[name=exiseTaxAmt]').val();
		 vatper = parseFloat(vatper);
		 vatAmt = parseFloat(vatAmt);
		 cstPer = parseFloat(cstPer);
		 cstAmt = parseFloat(cstAmt);
		 servPer = parseFloat(servPer);
		 servAmt = parseFloat(servAmt);
		 bedPer = parseFloat(bedPer);
		 bedAmt = parseFloat(bedAmt);
		 if(isNaN(vatper)){
		 	vatper=0;
		 }	
		 if(isNaN(vatAmt)){
		 	vatAmt=0;
		 }
		 if(isNaN(cstPer)){
		 	cstPer=0;
		 }
		 if(isNaN(cstAmt)){
		 	cstAmt=0;
		 }
		  if(isNaN(servPer)){
		 	servPer=0;
		 }
		  if(isNaN(servAmt)){
		 	servAmt=0;
		 }
		  if(isNaN(bedPer)){
		 	bedPer=0;
		 }
		 if(isNaN(bedAmt)){
		 	bedAmt=0;
		 }
		if(vatper!=0) {		
				if(vatAmt==0) {
			    	$('#vatAmount').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#vatAmount').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter vatAmount');
			    	return false;
			    }
		    }
		  if(vatAmt!=0) {		
				if(vatper==0) {
			    	$('#vatPercent').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#vatPercent').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter vatPercent');
			    	return false;
			    }
		    }  
		
		if(cstPer!=0) {		
				if(cstAmt==0) {
			    	$('#cstAmount').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#cstAmount').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter cstAmount');
			    	return false;
			    }
		    }
		  if(cstAmt!=0) {		
				if(cstPer==0) {
			    	$('#cstPercent').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#cstPercent').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter cstPercent');
			    	return false;
			    }
		    }
		    if(servPer!=0) {		
				if(servAmt==0) {
			    	$('#serviceTaxAmt').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#serviceTaxAmt').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter serviceTax Amount');
			    	return false;
			    }
		    }
		  if(servAmt!=0) {		
				if(servPer==0) {
			    	$('#serviceTaxPer').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#serviceTaxPer').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter serviceTax Percent');
			    	return false;
			    }
		    }
		    if(bedPer!=0) {		
				if(bedAmt==0) {
			    	$('#exiseTaxAmt').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#exiseTaxAmt').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter exise Amount');
			    	return false;
			    }
		    }
		  if(bedAmt!=0) {		
				if(bedPer==0) {
			    	$('#exiseTaxPer').css('background', 'red'); 
			       	setTimeout(function () {
			           	$('#exiseTaxPer').css('background', 'white').focus(); 
			       	}, 800);
			       alert('Please enter exiseTax Percent');
			    	return false;
			    }
		    }
		    
		//formName="listInvoiceItems";	
		var hiddenVat = document.createElement("input");
	    hiddenVat.setAttribute("type", "hidden");
	    hiddenVat.setAttribute("name", "VAT_PUR");
	    hiddenVat.setAttribute("value", jQuery('input[name=vatPercent]').val());
	    formName.appendChild(hiddenVat);
	    var hiddenVatAmt = document.createElement("input");
	    hiddenVatAmt.setAttribute("type", "hidden");
	    hiddenVatAmt.setAttribute("name", "VAT_PUR_AMT");
	    hiddenVatAmt.setAttribute("value", jQuery('input[name=vatAmount]').val());
	    formName.appendChild(hiddenVatAmt);
	    var hiddenCst = document.createElement("input");
	    hiddenCst.setAttribute("type", "hidden");
	    hiddenCst.setAttribute("name", "CST_PUR");
	    hiddenCst.setAttribute("value", jQuery('input[name=cstPercent]').val());
	    formName.appendChild(hiddenCst);
	    var hiddenCstAmt = document.createElement("input");
	    hiddenCstAmt.setAttribute("type", "hidden");
	    hiddenCstAmt.setAttribute("name", "CST_PUR_AMT");
	    hiddenCstAmt.setAttribute("value", jQuery('input[name=cstAmount]').val());
	    formName.appendChild(hiddenCstAmt);
	    var hiddenServTax = document.createElement("input");
	    hiddenServTax.setAttribute("type", "hidden");
	    hiddenServTax.setAttribute("name", "SERTAX_PUR");
	    hiddenServTax.setAttribute("value", jQuery('input[name=serviceTaxPer]').val());
	    formName.appendChild(hiddenServTax);
	    var hiddenServTaxAmt = document.createElement("input");
	    hiddenServTaxAmt.setAttribute("type", "hidden");
	    hiddenServTaxAmt.setAttribute("name", "SERTAX_PUR_AMT");
	    hiddenServTaxAmt.setAttribute("value", jQuery('input[name=serviceTaxAmt]').val());
	    formName.appendChild(hiddenServTaxAmt);
	    var hiddenExise = document.createElement("input");
	    hiddenExise.setAttribute("type", "hidden");
	    hiddenExise.setAttribute("name", "BED_PUR");
	    hiddenExise.setAttribute("value", jQuery('input[name=exiseTaxPer]').val());
	    formName.appendChild(hiddenExise);
	    var hiddenExiseAmt = document.createElement("input");
	    hiddenExiseAmt.setAttribute("type", "hidden");
	    hiddenExiseAmt.setAttribute("name", "BED_PUR_AMT");
	    hiddenExiseAmt.setAttribute("value", jQuery('input[name=exiseTaxAmt]').val());
	    formName.appendChild(hiddenExiseAmt);
	        $('button').click();
			return false;	
  }
  
 	function disableAddButton(){    			
		   $("input[type=button]").attr("disabled", "disabled");		  	
	}
	
	function showTaxForm() {	
		var message = "";
		message += "<form name='addTaxTypes' method='post'><table cellspacing=10 cellpadding=10>" ; 		
			message += 	"<tr class='h3'><td align='left' class='h3' width='40%'>TaxType</td><td align='left' width='10%'>Percentage(%)  Amount</td></tr>";
			message += "<br/><br/>";
			message += 	"<tr class='h3'><td align='left' class='h3' width='40%'>Vat</td><td align='left' width='10%'><input class='h3' type='text' id='vatPercent' name='vatPercent'  size='5'/><input type='text' name='vatAmount' id='vatAmount' size='10'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Cst</td><td align='left' width='10%'><input class='h3' type='text' id='cstPercent' name='cstPercent'  size='5'/><input type='text' name='cstAmount' id='cstAmount' size='10'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Service Tax</td><td align='left' width='10%'><input class='h3' type='text' id='serviceTaxPer' name='serviceTaxPer'  size='5'/><input type='text' name='serviceTaxAmt' id='serviceTaxAmt' size='10'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Exise duty</td><td align='left' width='10%'><input class='h3' type='text' id='exiseTaxPer' name='exiseTaxPer'  size='5'/><input type='text' name='exiseTaxAmt' id='exiseTaxAmt' size='10'/></td></tr>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='button' name='addTaxItems' value='Add' id='addTaxItems' class='smallSubmit' onclick='javascript:setParameters();'/></span></td><td class='h3' width='80%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
		message += "</table></form>";				
		var title = "<h2><center>Add Taxes</center></h2>";
		Alert(message, title);
	}
	
	function showLessTaxForm(thisObj) {
	  var rowObjNew =$(thisObj).parent().parent();
	 var amount = $(rowObjNew).find('[name=amount]').val();
		var message = "";
		message += "<form name='addLessTaxTypes' method='post'><table cellspacing=10 cellpadding=10>" ; 		
			message += "<br/><br/>";
				message += 	"<tr class='h3'><td align='left' class='h3' width='40%'>Type:</td><td align='left' width='70%'><select name='tdsTypeId' id='tdsTypeId'>"+
						"<#list invoiceItemTypesList as invoiceItemType><option value='${invoiceItemType.invoiceItemTypeId?if_exists}' >${invoiceItemType.invoiceItemTypeId?if_exists}</option></#list></select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Taxable Amt</td><td align='left' width='10%'><input class='h3' type='text' id='taxableAmt' name='taxableAmt' value="+amount+"  size='5'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Tax</td><td align='left' width='10%'><input class='h3' type='text' id='TaxPer' name='TaxPer'  size='5' onChange='javascript:amountWithTaxCall(this);'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Amount</td><td align='left' width='10%'><input class='h3' type='text' id='tdsAmount' name='tdsAmount'  size='5'/></td></tr>"+
						
						"<tr class='h3'><td align='right'><span align='right'><input type='button' name='addTaxItems' value='Add' id='addTaxItems' class='smallSubmit' onclick='javascript:setLessTaxParameters();'/></span></td><td class='h3' width='80%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
		message += "</table></form>";				
		var title = "<h2><center>Less Taxes</center></h2>";
		Alert(message, title);
	}
	function amountWithTaxCall(thisObjNew) {
	   var rowObjNew =$(thisObjNew).parent().parent().parent();
       var TaxPer = $(rowObjNew).find('[name=TaxPer]').val();
	   var taxableAmt = $(rowObjNew).find('[name=taxableAmt]').val();
		
		if(TaxPer != "undefined" && TaxPer != null && TaxPer != "undefined" && TaxPer != null){
	 	  var tdsAmount =(TaxPer*taxableAmt)/100;
	  	    $("#tdsAmount").val(tdsAmount)
	  	  
	  	 }
	  	  
	  
	}
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
	var tempCostCenterId = "${tempCostCenterId}";
	
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
		var costCenterId = $('#costCenterId1 :selected').val();  
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
		
		var vatPerVal=jQuery('input[name=VAT_PUR]').val();
		var vatAmtVal=jQuery('input[name=VAT_PUR_AMT]').val();
		var cstPerVal=jQuery('input[name=CST_PUR]').val();
		var cstAmtVal=jQuery('input[name=CST_PUR_AMT]').val();
		var servPerVal=jQuery('input[name=SERTAX_PUR]').val();
		var servAmtVal=jQuery('input[name=SERTAX_PUR_AMT]').val();
		var bedPerVal=jQuery('input[name=BED_PUR]').val();
		var bedAmtVal=jQuery('input[name=BED_PUR_AMT]').val();
		var tdsAmtVal=jQuery('input[name=TDS_AMT]').val();
		var tdsType=jQuery('input[name=TDS_TYPE]').val();
		var data= {invoiceItemTypeId : invoiceItemTypeId,
             		description : description,
             		amount : amount,
                    invoiceId: invoiceId,
                    costCenterId: costCenterId,
                     vatType:"VAT_PUR",
                    vatPercent:vatPerVal,
                    tdsAmount:tdsAmtVal,
                    tdsType:tdsType,
                    vatAmount:vatAmtVal,
                    cstType:"CST_PUR",
                    cstPercent:cstPerVal,
                    cstAmount:cstAmtVal,
                    serviceTaxType:"SERTAX_PUR",
                    serviceTaxPercent:servPerVal,
                    serviceTaxAmount:servAmtVal,
                    bedTaxType:"BED_PUR",
                    bedPercent:bedPerVal,
                    bedAmount:bedAmtVal
                    };
                    
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
				           <td>Add Tax</td>
				           <td>Less Tax</td>
				          <td>Action</td>
				        </tr>
				      </thead>
				      <tbody>
				        <#assign alt_row = false>
				         <#assign listVal=Static["org.ofbiz.base.util.UtilMisc"].toList("VAT_PUR", "CST_PUR","SERTAX_PUR","BED_PUR")>
				        <#list invoiceItems as invoice>
				            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				              <td><input class="input-medium" name="taxAuthPartyId" type="hidden" size="4"/>${invoice.invoiceItemSeqId}</td>
				              <#assign invoiceItemType = delegator.findOne("InvoiceItemType", {"invoiceItemTypeId" : invoice.invoiceItemTypeId?if_exists}, true) />	
				              <td>${invoiceItemType.description}</td>
				              <td>${invoice.costCenterId?if_exists}</td>
				              <td>${invoice.description?if_exists}</td>
				              <td>${invoice.amount?if_exists}</td>
				              <#if !listVal.contains(invoiceItemType.invoiceItemTypeId)>
				              	<td><a name="remove" href="" onClick="return removeInvoiceItem('${invoice.invoiceItemSeqId}', '${invoice.taxAuthParyId?if_exists}', '${invoice.invoiceId}');">Remove</a></td>
				              </#if>
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
				          	<select name="costCenterId" id="costCenterId1">
				          	   <#list branchListOfRO as roWiseBranch>
					          	   <#if roWiseBranch.partyIdTo=="${tempCostCenterId}">
					          	   		<option selected value='${roWiseBranch.partyIdTo}'>
							        	    ${roWiseBranch.groupName?if_exists}
							            </option>
					          	   <#else>
							            <option value='${roWiseBranch.partyIdTo}'>
							        	    ${roWiseBranch.groupName?if_exists}
							            </option>
							        </#if>
						        </#list>
						        <#list branchesListOfRO as roWiseBranches>
						           <#if roWiseBranches.partyIdTo=="${tempCostCenterId}">
					          	   	    <option selected value='${roWiseBranches.partyIdTo}'>
							        	    ${roWiseBranches.groupName?if_exists}
							            </option>
				          	   
					          	   <#else>
							            <option value='${roWiseBranches.partyIdTo}'>
							        	    ${roWiseBranches.groupName?if_exists}
							            </option>
							        </#if>
						            
						        </#list>
							 </select>   
				          </td>
				          <td><input id="description" class="input-medium" name="description" type="text" size="60"/></td>
				          <td><input id="amount" class="input-medium" name="amount" type="text" size="12"/></td>
				          <td><input id="addTax" type="button"  onclick="javascript:showTaxForm();" value="Add Tax" /></td>
				          <td><input id="addLessTax" type="button"  onclick="javascript:showLessTaxForm(this);" value="Less Tax" /></td>
				          <td><button id="save" name="save" onClick="return saveInvoiceItem();" style="buttontext">Add</button></td>
				        </tr>
				      </tbody>
				    </table>
				  </form>
		 
		 </div>
		 	
	</div>
	<a style="font-size: 16px" href="<@ofbizUrl>APInvoiceOverview?invoiceId=${invoice.invoiceId}&amp;subTabButtonValue=${tabButtonItem5}</@ofbizUrl>">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Save</a>
	
  
