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
	
	
	
    
    function getDCReport(orderId){
		var formId = "#" + "dcForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		jQuery(formId).append(jQuery(param1));
        jQuery(formId).submit();
    }
    
    function approveDepotOrder(orderId, salesChannel,partyId){
		var formId = "#" + "orderApproveForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
		var param3 = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
		jQuery(formId).append(jQuery(param3));
        jQuery(formId).submit();
    }
 	function editDepotOrder(orderId, salesChannel,partyId){
		var formId = "#" + "orderEditForm"
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
		var param3 = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
		jQuery(formId).append(jQuery(param3));
        jQuery(formId).submit();
    }
    function changeOrderStatusToComplete(orderId, statusId){
		var formId = "#" + "orderCompleteForm"
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "statusId").val(statusId);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
        jQuery(formId).submit();
    }
    function cancelIceCreamOrder(orderId, salesChannel){
		var formId = "#" + "orderCancelForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
        jQuery(formId).submit();
    }
        
//]]>
</script>
<#include "viewOrderDetailsDepot.ftl"/>

<form name="orderEditForm" id="orderEditForm" method="post" 
	
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="editDepotOrder"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="editIUSTransferOrder"
	</#if>>
</form>
<form name="orderCancelForm" id="orderCancelForm" method="post" 
	
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="cancelDepotOrder"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="cancelIUSTransferOrder"
	</#if>>
</form>
<form name="orderApproveForm" id="orderApproveForm" method="post" 
	
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="approveByprodSalesOrder"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="approveIUSTransferOrder"
	</#if>> 
</form>
<form name="orderCompleteForm" id="orderCompleteForm" method="post" 
	action="CompleteOrder">
</form>
<form name="processOrdersForm" id="processOrdersForm" method="post" 
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="createShipmentAndInvoiceForDepotSalesOrders"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="createShipAndInvForIUSTransferOrders"
	</#if>>
</form>

<#if orderList?has_content>
  
  <form name="listOrders" id="listOrders"  method="post" >
    <div align="right" width="100%">
    	<#if screenFlag?exists && screenFlag=="depotSales">
    		<input class='h3' type='hidden' id='shipmentTypeId' name='shipmentTypeId' value='DEPOT_SHIPMENT'/>
    	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
    		<input class='h3' type='hidden' id='shipmentTypeId' name='shipmentTypeId' value='INTUNIT_TR_SHIPMENT'/>
    	</#if>
    		<input class='h3' type='hidden' id='orderStatusId' name='orderStatusId' value='ORDER_COMPLETED'/>
    </div>
	<br/>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Party Code</td>
          <td>Party Name</td>
          <td>Order Id</td>
          <td>Order Date</td>
          <td>View Order</td>
          <td>Print Indent</td>
          <#--<td>Edit Batch</td>-->
          <td>Approve</td>
          <td>DC Report</td>
          <#--<td>Edit</td>-->
          <#--<td>Party Balance</td>-->
          <td>Cancel</td>
		  <td>Generate Shipment</td>
		  <td>Complete Order</td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list orderList as eachOrder>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<td>${eachOrder.partyId?if_exists}</td>
              	<td>${eachOrder.partyName?if_exists}</td>
              	<td>${eachOrder.orderId?if_exists}</td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachOrder.orderDate, "dd/MM/yyyy")}</td>
              	<td><input type="button" name="viewOrder" id="viewOrder" value="View" onclick="javascript:fetchOrderDetails('${eachOrder.orderId?if_exists}', '');"/></td>
              	<td><a class="buttontext" href="<@ofbizUrl>indentPrintReport.pdf?orderId=${eachOrder.orderId?if_exists}</@ofbizUrl>" target="_blank"/>Download</td>
              	<#assign partyOb=0>
              	<#if partyOBMap?exists && eachOrder.partyId?exists && partyOBMap.get(eachOrder.partyId)?exists>
              	<#assign partyOb=partyOBMap.get(eachOrder.partyId)>
              	</#if>
              	<#assign orderTotal=0>
              	<#if eachOrder.orderTotal?exists>
              	<#assign orderTotal=eachOrder.orderTotal>
              	</#if>
              	<#assign isCreditInstution="N">
              	<#if eachOrder.isCreditInstution?exists>
              	<#assign isCreditInstution=eachOrder.isCreditInstution>
              	</#if>
              	
              	<#if (eachOrder.get('statusId') == "ORDER_CREATED" && partyOb &gt; 0  && (partyOb gte orderTotal)) ||( isCreditInstution=="Y" && eachOrder.get('statusId') == "ORDER_CREATED" ) >
              		<td><input type="button" name="approveOrder" id="approveOrder" value="Approve Order" onclick="javascript: approveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
              		<td></td>
              	<#--<#elseif eachOrder.get('statusId') == "ORDER_CREATED" && (partyOb==0 || partyOb lte orderTotal ) >
              		<td colspan="2"><font color="red">Unavailable Balance</font> </td>-->
              	<#else>
              		<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : eachOrder.statusId}, true) />
                	<td>${statusItem.description?default(eachOrder.statusId)}</td>
              		<td><a class="buttontext" href="<@ofbizUrl>nonRouteGatePass.pdf?orderId=${eachOrder.orderId?if_exists}&screenFlag=${screenFlag?if_exists}</@ofbizUrl>" target="_blank"/>Download</td>
              	</#if>
              	<#--
              	<#if eachOrder.get('statusId') != "ORDER_APPROVED">
              	  <td><input type="button" name="editOrder" id="editOrder" value="Edit Order" onclick="javascript: editDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
        		<#else>
        		    <td></td>
        		</#if>
        		-->
        		<#--<td><input type="hidden" name="partyOBAmount"  value="${partyOb}" />${partyOb?string("#0.00")}</td>-->
        		<td><input type="button" name="cancelOrder" id="cancelOrder" value="Cancel Order" onclick="javascript: cancelIceCreamOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}');"/></td>
              	<#--<td><input type="text" name="paymentAmount" id="paymentAmount" onchange="javascript: getPaymentTotal();"></td>-->
              	<#--
              	<#if eachOrder.get('statusId') == "ORDER_APPROVED">
              		<td><input type="checkbox" id="orderId_${eachOrder_index}" name="orderId" value="${eachOrder.orderId?if_exists}"/></td>
              	</#if>
              	-->
              	<#if eachOrder.get('statusId') == "ORDER_APPROVED">
              		<td><input type="button" name="generateShipment" id="generateShipment" value="Generate" onclick="javascript:captureShipmentDetails('${eachOrder.orderId?if_exists}', '${eachOrder.partyName?if_exists}', '${eachOrder.partyId?if_exists}');"/></td>
              		<#else>
              		<td></td>
              	</#if>
              	<#if eachOrder.get('statusId') == "ORDER_APPROVED">
              		<td><input type="button" name="completeOrder" id="completeOrder" value="Complete" onclick="javascript: changeOrderStatusToComplete('${eachOrder.orderId?if_exists}', 'ORDER_COMPLETED');"/></td>
              		<#else>
              		<td></td>
              	</#if>
              	
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Orders Found</h3>
</#if>
