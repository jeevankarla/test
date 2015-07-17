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
	
	
	function toggleRequestId(master) {
        var receipts = jQuery("#listMilkReceipts :checkbox[name='receiptCheckBoxId']");
        jQuery.each(receipts, function() {
            this.checked = master.checked;
        });
    }
    
    function processReceipt(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var actionFlag = $(current).val();
    	var receipts = jQuery("#listMilkReceipts :checkbox[name='receiptCheckBoxId']");
        var index = 0;
        jQuery.each(receipts, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var milkTransferObj = $(domObj).find("[name='milkTransferId']");
            	var milkTransferId = $(milkTransferObj).val();
            	if(actionFlag == "Finalize"){
	            	var productIdObj = $(domObj).find("[name='productId']");
	            	var productId = productIdObj.val();
	            	var partyIdObj = $(domObj).find("[name='partyId']");
	            	var partyId = partyIdObj.val();
	            	var purposeObj = $(domObj).find("[name='purposeTypeId']");
	            	var purposeTypeId = purposeObj.val();
	            	
	            	var appendProductIdStr = "<input type=hidden name=productId_o_"+index+" value="+productId+" />";
	            	$("#updateMilkTransferForm").append(appendProductIdStr);
	            	var appendPartyIsStr = "<input type=hidden name=partyId_o_"+index+" value="+partyId+" />";
	            	$("#updateMilkTransferForm").append(appendPartyIsStr);
	            	var purposeTypeIdStr = "<input type=hidden name=purposeTypeId_o_"+index+" value="+purposeTypeId+" />";
	            	$("#updateMilkTransferForm").append(purposeTypeIdStr);
            	}
            	var appendStr = "<input type=hidden name=milkTransferId_o_"+index+" value="+milkTransferId+" />";
            	$("#updateMilkTransferForm").append(appendStr);
            	index = index+1;
            }
            
        });
        if(index == 0){
	       	 alert("Please Select Record(s) To Process!.");
	       	 jQuery(current).attr( "disabled", false);
	       	 return false;
	     }
        jQuery('#updateMilkTransferForm').submit();
    }
       
//]]>
</script>
<#if parameters.flag?has_content && parameters.flag == "APPROVE_RECEIPTS">
<#assign action="ApproveReceipts">
</#if>
<#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
<#assign action="FinalizeTankerReceipts">
</#if>
<form name="updateMilkTransferForm" id="updateMilkTransferForm" method="post" action=${action}></form>
<#if milkDetailslist?has_content>
  
  <form name="listMilkReceipts" id="listMilkReceipts"  method="post" >
  
    <div align="right" width="100%">
    	<table width="25%">
    		<tr>
    			<td></td>
    			<td></td>
				<#if parameters.flag?has_content && parameters.flag == "APPROVE_RECEIPTS">
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processReceipt(this);" value="Approve"</h2></td>
    			</#if>
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processReceipt(this);" value="Finalize"</h2></td>
    			</#if>
    		</tr>
    	</table>
    </div>
 
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Record No</td>
          <td>Recd Date</td>
          <td>From Union</td>
          <td>To</td>
          <td>Milk Type</td>
          <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
          <td>Milk Used For</td>
          </#if>
          <td>Silo</td>
          <td>Recd Qty(Kgs)</td>
          <td>Recd Qty(Ltrs)</td>
          <td>Recd Fat</td>
          <td>Recd Snf</td>
          <td>Recd KgFat</td>
          <td>Recd KgSnf</td>
          <td>Vehicle Number</td>
          <td>Status</td>
		  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllReceipts" name="checkAllReceipts" onchange="javascript:toggleRequestId(this);"/></td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign index = 0>
      <#list milkDetailslist as eachItem>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				<input type=hidden name=milkTransferId value='${eachItem.milkTransferId?if_exists}'>
              	<td>${eachItem.milkTransferId?if_exists}</td>
              	 <#if eachItem.receiveDate?has_content>
				<td>${eachItem.receiveDate}</td>
				<#else>
                <td></td>
                </#if>
              	
              	
              	<#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : eachItem.partyId}, false))!>
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
                <td> 
                <select name="partyId" class='h4' >
                    <option value='${eachItem.partyId}'>${partyName.groupName?if_exists}</option>
			       <#list unionsList as union>    
			          <option value='${union.partyId}' >
				         ${union.groupName?if_exists}
				      </option>
			       </#list>            
				</select>
				</td>
                <#else>  
                <td>${partyName.groupName?if_exists}[${eachItem.partyId}]</td>
                </#if>
              	<#assign partyIdToName = (delegator.findOne("PartyNameView", {"partyId" : eachItem.partyIdTo}, false))!>
                <td>${partyIdToName.groupName?if_exists}[${eachItem.partyIdTo}]</td>
                <#assign product = (delegator.findOne("Product", {"productId" : eachItem.productId}, false))!>
              	<#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
                <td>  
                <select name="productId" class='h4' >
                    <option value='${eachItem.productId}'>${product.description?if_exists}</option>
			       <#list productsList as product>    
			          <option value='${product.productId}' >
				         ${product.description?if_exists}
				      </option>
			       </#list>            
				</select>
				</td>
                <#else>  
                <td>${product.description?if_exists}</td>
                </#if>
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
                <#assign enumeration = delegator.findOne("Enumeration",{"enumId":eachItem.purposeTypeId},false)>
                <td>
                	<select name="purposeTypeId" class='h4' >
                    <option value='${eachItem.purposeTypeId}'>${enumeration.enumCode?if_exists}</option>
			       <#list milkPurchasePurposeTypeList as purpose>    
			          <option value='${purpose.enumId}' >
				         ${purpose.enumCode?if_exists}
				      </option>
			       </#list>            
				</select>
                </td>
                </#if>
                <td>${eachItem.siloId?if_exists}</td>
              	<td>${eachItem.receivedQuantity?if_exists}</td>
				<td>${eachItem.receivedQuantityLtrs?if_exists}</td>
				<td>${eachItem.receivedFat?if_exists}</td>
				<td>${eachItem.receivedSnf?if_exists}</td>
				<td>${eachItem.receivedKgFat?if_exists}</td>
				<td>${eachItem.receivedKgSnf?if_exists}</td>
				<td>${eachItem.containerId?if_exists}</td>
			   <#assign statusItem = (delegator.findOne("StatusItem",{"statusId" : eachItem.statusId},false))!>
                <td>${statusItem.description?if_exists}</td>
           		<td><input type="checkbox" id="receiptCheckBoxId_${eachItem_index}" name="receiptCheckBoxId"/></td>
            </tr>
            <#assign index = index+1>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
</#if>
