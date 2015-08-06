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

</script>


<#if parameters.flag?has_content && parameters.flag == "APPROVE_RECEIPTS">
<#assign action="ApproveReceipts">
</#if>
<#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
<#assign action="FinalizeTankerReceipts">
</#if>


<form name="updateMilkTransferForm" id="updateMilkTransferForm" method="post" action=${action}></form>
<#if milkDetailslist?has_content>
  
  
    
 
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Record No</td>
          <#--<td>From</td>-->
          <td>Ship To</td>
          <td>Milk Type</td>
          <#--<td>Silo</td>-->
          <td>Qty</td>
          <#--<td>Received Qty(Ltrs)</td>-->
          <td>Des Fat</td>
          <td>Des Snf</td>
          <td>Des KgFat</td>
          <td>Des KgSnf</td>
          <td>Ack Fat</td>
          <td>Ack Snf</td>
          <td>Ack KgFat</td>
          <td>Ack KgSnf</td>
          <td>Vehicle Number</td>
          <td>Received Date</td>
          <td>Status</td>
          <td>Raise Invoice</td>
          <td>Amount</td>
		  <#--<td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllReceipts" name="checkAllReceipts" onchange="javascript:toggleRequestId(this);"/></td>-->
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list milkDetailslist as eachItem>
      		<form name="listMilkReceipts_${eachItem_index}" id="listMilkReceipts_${eachItem_index}"  method="post" action="<@ofbizUrl>RaiseInvoiceFromShipment</@ofbizUrl>">
  
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				<input type=hidden name=milkTransferId value='${eachItem.milkTransferId?if_exists}'>
				<input type=hidden name=partyId value='${eachItem.partyId?if_exists}'>
				<input type=hidden name=partyIdTo value='${eachItem.partyIdTo?if_exists}'>
				<input type=hidden name=receivedKgFat value='${eachItem.receivedKgFat?if_exists}'>
				<input type=hidden name=receivedKgSnf value='${eachItem.receivedKgSnf?if_exists}'>
				<input type=hidden name=quantity value='${eachItem.receivedQuantity?if_exists}'>
				<input type=hidden name=productId value='${eachItem.productId?if_exists}'>
				<input type=hidden name=shipmentId value='${eachItem.shipmentId?if_exists}'>
				
				<input type=hidden name=fat value='${eachItem.fat?if_exists}'>
				<input type=hidden name=snf value='${eachItem.snf?if_exists}'>
				<input type=hidden name=receivedFat value='${eachItem.receivedFat?if_exists}'>
				<input type=hidden name=receivedSnf value='${eachItem.receivedSnf?if_exists}'>
				
              	<td>${eachItem.milkTransferId?if_exists}</td>
              	<#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : eachItem.partyId}, false))!>
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
                <#--<td>${eachItem.siloId?if_exists}</td>-->
              	<td>${eachItem.receivedQuantity?if_exists}</td>
				<#--<td>${eachItem.receivedQuantityLtrs?if_exists}</td>-->
				
				<td>${eachItem.fat?if_exists}</td>
				<td>${eachItem.snf?if_exists}</td>
				<td>${eachItem.sendKgFat?if_exists}</td>
				<td>${eachItem.sendKgSnf?if_exists}</td>
				
				<td>${eachItem.receivedFat?if_exists}</td>
				<td>${eachItem.receivedSnf?if_exists}</td>
				<td>${eachItem.receivedKgFat?if_exists}</td>
				<td>${eachItem.receivedKgSnf?if_exists}</td>
				<#assign vehicle = (delegator.findOne("Vehicle",{"vehicleId" : eachItem.containerId},false))!>
				<td>${vehicle.vehicleNumber?if_exists}[${eachItem.containerId?if_exists}]</td>
				<#if eachItem.receiveDate?has_content>
				<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachItem.receiveDate, "dd/MM/yyyy")}</td>
				<#else>
                <td></td>
                </#if>
                <#assign statusItem = (delegator.findOne("StatusItem",{"statusId" : eachItem.statusId},false))!>
                <td>${statusItem.description?if_exists}</td>
                
				
				<#if eachItem.shipmentId?has_content>
					<#assign shipmentId = eachItem.shipmentId>
					<#if xferInvoiceMap.get("${shipmentId}")?has_content>
						<#assign invMap = xferInvoiceMap.get("${shipmentId}")>
						<#assign invoiceId = invMap.get("invoiceId")>
						<#assign invAmount = invMap.get("invoiceAmount")>
						<td>${invoiceId}</td>
						<td>${invAmount}</td>
					<#else>
						<#if eachItem.statusId == "MXF_RECD">
		                	<td>
		                		<a href="javascript:document.listMilkReceipts_${eachItem_index}.submit()" class="buttontext">Raise Invoice</a>
		                	</td>
		                	<td>
		                		&#160;
		                	</td>
		                <#else>
		                	<td>
		                		&#160;
		                	</td>
		                	<td>
		                		&#160;
		                	</td>
		                	
						</#if>
					</#if>
				<#else>
                	<#if eachItem.statusId == "MXF_RECD">
	                	<td>
	                		<a href="javascript:document.listMilkReceipts_${eachItem_index}.submit()" class="buttontext">Raise Invoice</a>
	                	</td>
					</#if>
                </#if>
				
				
				<#--<td><input type="checkbox" id="receiptCheckBoxId_${eachItem_index}" name="receiptCheckBoxId"/></td>-->
            </tr>
            </form>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  
</#if>
