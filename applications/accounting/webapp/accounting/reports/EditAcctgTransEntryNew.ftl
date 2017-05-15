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
	//handle cancel event
	function cancelForm(){
		return false;
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
	
	function removeAcctTransEntry(acctgTransId, organizationPartyId,acctgTransEntrySeqId) {
	var acctgTransId=jQuery('input[name=acctgTransId]').val();
	var transactionDate=jQuery('input[name=transactionDate]').val();
	var partyId=jQuery('input[name=partyId]').val();
	var costCenterId = $('#costCenterId').val()  
	var purposeTypeId = $('#purposeTypeId :selected').val();
	
	var hasAcctgAtxEditPermission="true"
		$.ajax({
			 type: "POST",
             url: 'deleteAcctgTransEntrySer',
             data: {acctgTransId : acctgTransId,
             		acctgTransEntrySeqId : acctgTransEntrySeqId,
             		organizationPartyId : organizationPartyId},
             dataType: 'json',
	            
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
                    alert('Error Fetching available AcctTransEntry');
				}else{
					var pathname = window.location.pathname; // Returns path only
                         var url      = window.location.href;     // Returns full URL
                        if(url.length < 100)  
                          window.location.href =pathname+"?acctgTransId="+acctgTransId+"&hasAcctgAtxEditPermission="+hasAcctgAtxEditPermission+"&transactionDate="+transactionDate+"&partyId="+partyId+"&organizationPartyId="+organizationPartyId;
                        else
                          window.location.href =pathname+"?acctgTransId="+acctgTransId+"&hasAcctgAtxEditPermission="+hasAcctgAtxEditPermission+"&transactionDate="+transactionDate+"&partyId="+partyId+"&organizationPartyId="+organizationPartyId;
				}								 
			},
			error: function(){
				alert("error");
			}							
		});
		return false;
	}
	function updateAcctTransEntry(index,acctgTransId, organizationPartyId,acctgTransEntrySeqId,glAccountId,partyId,costCenterId,purposeTypeId) {
	
	var amountId="origAmount_"+index;
	var amount=$('input[id='+amountId+']').val();
	
	var hasAcctgAtxEditPermission="true"
		$.ajax({
			 type: "POST",
             url: 'updateAcctgTransEntrySer',
             data: {acctgTransId : acctgTransId,
             		costCenterId : costCenterId,
             		purposeTypeId : purposeTypeId,
             		acctgTransEntrySeqId : acctgTransEntrySeqId,
             		organizationPartyId : organizationPartyId,
             		partyId : partyId,
             		amount : amount,
                    glAccountId: glAccountId},
             dataType: 'json',
	            
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
                    alert('Error Fetching available AcctTransEntry');
				}else{
					var pathname = window.location.pathname; // Returns path only
                         var url      = window.location.href;     // Returns full URL
                        if(url.length < 100)  
                          window.location.href =pathname+"?acctgTransId="+acctgTransId+"&hasAcctgAtxEditPermission="+hasAcctgAtxEditPermission+"&transactionDate="+transactionDate+"&partyId="+partyId+"&organizationPartyId="+organizationPartyId;
                        else
                          window.location.href =pathname+"?acctgTransId="+acctgTransId+"&hasAcctgAtxEditPermission="+hasAcctgAtxEditPermission+"&transactionDate="+transactionDate+"&partyId="+partyId+"&organizationPartyId="+organizationPartyId;
				}								 
			},
			error: function(){
				alert("error");
			}							
		});
		return false;
	}
		
	function saveAcctTransEntry() {
		
		var acctgTransEntryTypeId="_NA_";
		var glAccountId=jQuery('input[name=glAccountId]').val();
		var organizationPartyId=jQuery('input[name=organizationPartyId]').val();
		var amount=jQuery('input[name=origAmount]').val();
		var glAccountId=jQuery('input[name=glAccountId]').val();
		var acctgTransId=jQuery('input[name=acctgTransId]').val();
		var glFiscalTypeId=jQuery('input[name=glFiscalTypeId]').val();
		var acctgTransTypeId=jQuery('input[name=acctgTransTypeId]').val();
		var debitCreditFlag = $('#debitCreditFlag :selected').val();  
		var transactionDate=jQuery('input[name=transactionDate]').val();
		var voucherRef=jQuery('input[name=voucherRef]').val();
		var description=jQuery('input[name=description1]').val();
		var origCurrencyUomId=jQuery('input[name=origCurrencyUomId]').val();
		var partyId=$("#3_lookupId_partyId").val();
		var costCenterId = $('#costCenterId1').val();
		var purposeTypeId = $('#purposeTypeId :selected').val();
		
		
		var data= {partyId : partyId,
             		origCurrencyUomId : origCurrencyUomId,
             		acctgTransEntryTypeId:acctgTransEntryTypeId,
             		transactionDate:transactionDate,
             		acctgTransTypeId:acctgTransTypeId,
             		organizationPartyId:organizationPartyId,
             		glFiscalTypeId:glFiscalTypeId,
             		acctgTransId:acctgTransId,
             		description : description,
             		amount : amount,
                    glAccountId: glAccountId,
                    debitCreditFlag:debitCreditFlag,
                    voucherRef:voucherRef,
                    costCenterId:costCenterId,
                    purposeTypeId:purposeTypeId};
		addGlaccount(data);
		return false;
	}
	
	function addGlaccount(data){
	var data1=data;
	var acctgTransId=jQuery('input[name=acctgTransId]').val();
	var transactionDate=jQuery('input[name=transactionDate]').val();
	var organizationPartyId=jQuery('input[name=organizationPartyId]').val();
	var partyId=jQuery('input[name=partyId]').val();
	var costCenterId = $('#costCenterId').val();
    var purposeTypeId = $('#purposeTypeId :selected').val();
	
	
	var hasAcctgAtxEditPermission="true"
	   $.ajax({
			 type: "POST",
             url: 'createAcctgTransEntrySer',
             
             data: data1,
             dataType: 'json',
			 success:function(result){
			 
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
                    alert('Error Creating Invoice Item');
				}else{
					//location.reload(true);
					var pathname = window.location.pathname; // Returns path only
                         var url      = window.location.href;     // Returns full URL
                        if(url.length < 100)  
                          window.location.href =pathname+"?acctgTransId="+acctgTransId+"&hasAcctgAtxEditPermission="+hasAcctgAtxEditPermission+"&transactionDate="+transactionDate+"&partyId="+partyId+"&organizationPartyId="+organizationPartyId;
                        else
                          window.location.href =pathname+"?acctgTransId="+acctgTransId+"&hasAcctgAtxEditPermission="+hasAcctgAtxEditPermission+"&transactionDate="+transactionDate+"&partyId="+partyId+"&organizationPartyId="+organizationPartyId;
				}								 
			},
			error:function(){
			return false;
			   alert("error");
			}
										
		});
		return false;
	
	}
	
	var glAccountType = ${StringUtil.wrapString(glAccountTypeJSON)!'[]'};
    var glAccountName = ${StringUtil.wrapString(glAccountName)!'[]'};

	$(document).ready(function(){
	    $("#glAccountId").autocomplete({ source: glAccountType }).keydown(function(e){});
	    getBOsForRO();	
	});

    var AccountName;
	function dispglAccountName(selection){
	   value = $("#glAccountId").val();
	   AccountName = glAccountName[value];
	   $("#AccountName").html("<h6>"+AccountName+"</h6>");
	}
	
	
	
</script>
	
	<div class="screenlet">
		 <div class="screenlet-title-bar">
		    <br class="clear"/>
		 </div>
		 <div class="screenlet-body">
		 		<form name="ListAcctgTransEntriesNew" id="ListAcctgTransEntriesNew"  method="post" action="">
				    <table id="customFields" class="basic-table hover-bar" cellspacing="0" width="10">
				      <#--<a id="addnew" href="" >add</a>-->
				      <thead>
				        <tr class="header-row-2">
				          <td>glAccountId</td>	
				          <td>partyId</td>
				         <#-- <td>productId</td>-->
						  <td>CostCenterId</td>
							<td>Segment Id</td>
				          <td>Debit/Credit</td>
				          <td>Amount</td>
				          <td>Voucher Ref</td>
				          <td>Description</td>
				          <td>Update</td>
				          <td>Action</td>
				        </tr>
				      </thead>
				      <tbody>
				        <#assign alt_row = false>
				        <#assign i = 1>
				        <#assign listVal=Static["org.ofbiz.base.util.UtilMisc"].toList("VAT_PUR", "CST_PUR","SERTAX_PUR","BED_PUR")>
				        <#list acctgTransEntries as acctgTransEntry>
				            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				              <td>
				              <#if acctgTransEntry.glAccountId?has_content>
					             
					              ${acctgTransEntry.glAccountId?if_exists}
					              <input id="glAccountId1" name="glAccountId1" type="hidden" value="${acctgTransEntry.glAccountId?if_exists}" size="12"/>
				              </#if></td>
				              <td><#if acctgTransEntry.partyId?has_content>
				              <#assign paymentPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, acctgTransEntry.partyId?if_exists, false)>
				              ${paymentPartyName}[${acctgTransEntry.partyId?if_exists}]
				              </#if></td>
				              <#--><td></td>-->
									 <td>	
									 	<#if acctgTransEntry.costCenterId?has_content>${acctgTransEntry.costCenterId?if_exists}</#if>						
									</td>															
									<td>
										<#if acctgTransEntry.purposeTypeId?has_content>${acctgTransEntry.purposeTypeId?if_exists}</#if>
									</td>

				              <td> <#if acctgTransEntry.debitCreditFlag?has_content>${acctgTransEntry.debitCreditFlag?if_exists}</#if></td>
				             <td><#if acctgTransEntry.amount?has_content>${acctgTransEntry.amount?if_exists}</#if></td>
				              <td><#if acctgTransEntry.voucherRef?has_content>${acctgTransEntry.voucherRef?if_exists}
				              </#if></td>
				               <td><#if acctgTransEntry.description?has_content>${acctgTransEntry.description?if_exists}
				               <input id="description1"  name="description1" type="hidden" value="${acctgTransEntry.description?if_exists}" size="10"/></#if>
				               <input id="acctgTransId" name="acctgTransId" type="hidden" value="${acctgTransEntry.acctgTransId?if_exists}" size="12"/>
					           <input id="organizationPartyId" name="organizationPartyId" type="hidden" value="${organizationPartyId}" size="12"/>
					          <input id="origCurrencyUomId" name="origCurrencyUomId" type="hidden" value="INR" size="12"/>
					          <input id="glFiscalTypeId" name="glFiscalTypeId" type="hidden" value="ACTUAL" size="12"/>
					          <input id="acctgTransTypeId" name="acctgTransTypeId" type="hidden" value="JOURNAL" size="12"/>
				               </td>
 
				             <td><a name="update" href="updateAcctngTransEntryNew?acctgTransId=${acctgTransEntry.acctgTransId?if_exists}&amp;acctgTransEntrySeqId=${acctgTransEntry.acctgTransEntrySeqId?if_exists}">Update</a></td>
				           <td><a name="remove" href="" onClick="return removeAcctTransEntry('${acctgTransEntry.acctgTransId?if_exists}', '${acctgTransEntry.organizationPartyId?if_exists}', '${acctgTransEntry.acctgTransEntrySeqId?if_exists}');">Remove</a></td>
				            </tr>
				            <#-- toggle the row color -->
				            <#assign alt_row = !alt_row>
				            <#assign i=i+1>
				        </#list>
				        <tr id="addItems" type="hidden" valign="middle"<#if alt_row> class="alternate-row"</#if>>
				          <td>
				            <input type="text" name="glAccountId" id="glAccountId" size="11" maxlength="60"  onblur='javascript:dispglAccountName(this);'/>
  					        <span  class="tooltip" id="AccountName"></span>
				          </td>
			              <td><#if acctgTrans?has_content>
					      	<@htmlTemplate.lookupField  formName="ListAcctgTransEntriesNew" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
			            </#if></td>
			             <#--> <td><@htmlTemplate.lookupField value="${productId?if_exists}" formName="ListAcctgTransEntriesNew" name="productId" id="productId" fieldFormName="LookupProduct"/></td>-->
			               
			              
			             
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
							
							
			               <td>							
							  <select name="purposeTypeId" id="purposeTypeId">
						          <option value="ALL">All</option>
				  	        <#list segmentList as seg> 
								<option value='${seg.enumId?if_exists}'>${seg.description?if_exists}</option>
	          		   		</#list>
							  </select> 							     
				          </td>
			               <td>
							  <select name="debitCreditFlag" id="debitCreditFlag">
						          <option value='C'>Credit</option>
						          <option value='D'>Debit</option>
							  </select>          
				          </td>
				          <td><input id="origAmount"  name="origAmount" type="text" size="10"/></td>
				          <td><input id="voucherRef"  name="voucherRef" type="text" size="10"/></td>
				          <td><input id="description1"  name="description1" type="text" size="10" value="${acctgTrans.description?if_exists}"/>
				          <input id="acctgTransId" name="acctgTransId" type="hidden" value="${acctgTrans.acctgTransId?if_exists}" size="12"/>
				          <input id="transactionDate" name="transactionDate" type="hidden" value="${acctgTrans.transactionDate?if_exists}" size="12"/>
				          <input id="organizationPartyId" name="organizationPartyId" type="hidden" value="${organizationPartyId}" size="12"/>
				          <input id="origCurrencyUomId" name="origCurrencyUomId" type="hidden" value="INR" size="12"/>
				          <input id="glFiscalTypeId" name="glFiscalTypeId" type="hidden" value="ACTUAL" size="12"/>
				          <input id="acctgTransTypeId" name="acctgTransTypeId" type="hidden" value="JOURNAL" size="12"/>
				          
				          </td>
				          <td><button id="save" name="save" onClick="return saveAcctTransEntry();" style="buttontext">Add</button></td>
				        </tr>
				      </tbody>
				    </table>
				  </form>
		 
		 </div>
		 	
	</div>
	
  
