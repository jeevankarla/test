
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
	
	
	function toggleOrderId(master) {
        var orders = jQuery("#listOrders :checkbox[name='orderId']");
        jQuery.each(orders, function() {
            this.checked = master.checked;
        });
    }
    
    function datepick()
	{		
		$( "#shipDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
    function realizestatus(orderId){
    
		var formId = "#" + "realizeStatus";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		jQuery(formId).append(jQuery(param1));
        jQuery(formId).submit();
    }
    function processOrders(current){
    	jQuery(current).attr( "disabled", "disabled");
    	var orders = jQuery("#listOrders :checkbox[name='orderId']");
        var index = 0;
        var shipDate = $("#shipDate").val();
        //alert("==shipDate==="+shipDate);
        var vehicleId = $("#vehicleId").val();
        var carrierName = $("#carrierName").val();
        var lrNumber = $("#lrNumber").val();
        var modeOfDespatch = $("#modeOfDespatch").val();
        var shipmentTypeId = $("#shipmentTypeId").val();
         var orderStatusId = $("#orderStatusId").val();
        jQuery.each(orders, function() {
            if (jQuery(this).is(':checked')) {
            	var domObj = $(this).parent().parent();
            	var orderObj = $(domObj).find("[name='orderId']");
            	var orderId = $(orderObj).val();
            	var appendStr = "<input type=hidden name=orderId_o_"+index+" value="+orderId+" />";
                $("#processOrdersForm").append(appendStr);
            }
            index = index+1;
        });
        var appStr = "";
        if(shipDate != "undefined" && shipDate != null){
    		appStr += "<input type=hidden name=shipDate value='"+ shipDate +"' />";
    	}
    	appStr += "<input type=hidden name=vehicleId value='"+ vehicleId +"' />";
    	appStr += "<input type=hidden name=carrierName value='"+ carrierName +"' />";
    	appStr += "<input type=hidden name=lrNumber value='"+ lrNumber +"' />";
    	appStr += "<input type=hidden name=modeOfDespatch value='"+ modeOfDespatch +"' />";
    	appStr += "<input type=hidden name=shipmentTypeId value='"+ shipmentTypeId +"' />";
    	appStr += "<input type=hidden name=orderStatusId value='"+ orderStatusId +"' />";
    	$("#processOrdersForm").append(appStr);
    	var salesChannel = '${parameters.salesChannelEnumId?if_exists}';
    	var splStr = "<input type=hidden name=salesChannelEnumId value='"+ salesChannel +"' />";
        $("#processOrdersForm").append(splStr);
    	jQuery('#processOrdersForm').submit();
        
    }
    function getDCReport(orderId){
		var formId = "#" + "dcForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		jQuery(formId).append(jQuery(param1));
        jQuery(formId).submit();
    }
    /*
    function approveIceCreamOrder(orderId, salesChannel){
		var formId = "#" + "orderApproveForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
        jQuery(formId).submit();
    } */
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
    
    function cancelIceCreamOrder(orderId, salesChannel){
		var formId = "#" + "orderCancelForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
        jQuery(formId).submit();
    }
       
       <#--
     function realizestatus(orderId, salesChannel){
		var formId = "#" + "listOrders";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
		
		
		alert(param1);
		alert(param2);
		
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
        jQuery(formId).submit();
    }
          
       -->
       
       
       
        
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
<form name="realizeStatus" id="realizeStatus" method="post" action="raiseSalesInvoiceForDepotSales"> 
</form>
<form name="orderApproveForm" id="orderApproveForm" method="post" 
	
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="approveDepotSalesOrder"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="approveIUSTransferOrder"
	</#if>> 
</form>

<form name="processOrdersForm" id="processOrdersForm" method="post" 
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="createShipmentAndInvoiceForDepotSalesOrders"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="createShipAndInvForIUSTransferOrders"
	</#if>
</form>

<#if orderList?has_content>
  
  <form name="listOrders" id="listOrders"   method="post" >
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
          
          <td>shipmentId</td>
          <td>Party Code</td>
          <td>Party Name</td>
          <td>Indent Id</td>
          <td>Indent Date</td>
          <td>Grand Total</td>
         <td>View Indent</td>
          <#-- <td>Print Indent</td>
          <td>Edit Batch</td>
          <td>Approve</td>
          <td>DC Report</td>-->
           <td>Indent Payment</td>
          <#-- <td>Payment</td> -->
           <td>Payment Status</td>
          <#--> <td>Advance Payments</td> -->
             <td>Received Amount</td>
             <td>Sales Invoice</td>
              <#--<td>Edit Sales Invoice</td> -->
             <td>Cancel Indent</td>
             
             
            <#-- <td>Indent Status</td>-->
        <#--  <td>Edit</td>
          <td>Generate PO</td> -->
          <#--<td>Party Balance</td>
          <td>Cancel</td>
		   <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllOrders" name="checkAllOrders" onchange="javascript:toggleOrderId(this);"/></td>-->
          
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list orderList as eachOrder>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
      			<input type="hidden" name="paymentPreferenceId" value="${eachOrder.partyId}">
      			<input type="hidden" name="partyId" value="${eachOrder.partyId}">
      			<td>${eachOrder.shipmentId?if_exists}</td>
            	<td>${eachOrder.partyId?if_exists}</td>
              	<td>${eachOrder.partyName?if_exists}</td>
              	<#if eachOrder.salesOrder?has_content>
              	    <td>${eachOrder.salesOrder}</td>
              	<#else>    
                  	<td>${eachOrder.orderId?if_exists}</td>
                </#if>  	
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachOrder.orderDate, "dd/MM/yyyy")}</td>
                <td>${eachOrder.orderTotal?if_exists}</td>
              	<td><input type="button" name="viewOrder" id="viewOrder" value="View" onclick="javascript:fetchOrderInformation('${eachOrder.orderId?if_exists}');"/></td>
              <#--	<td><a class="buttontext" href="<@ofbizUrl>indentPrintReport.pdf?orderId=${eachOrder.orderId?if_exists}</@ofbizUrl>" target="_blank"/>Indent Report</td>-->
              	<#--<td><input type="button" name="editBatch" id="editBatch" value="Edit Batch" onclick="javascript:fetchOrderDetails('${eachOrder.orderId?if_exists}', 'batchEdit');"/></td>-->
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
              	
              	<#--<#if (eachOrder.get('statusId') == "ORDER_CREATED") ||( isCreditInstution=="Y" && eachOrder.get('statusId') == "ORDER_CREATED" ) >
              	<td><input type="button" name="approveOrder" id="approveOrder" value="Approve Order" onclick="javascript: approveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
              		<td></td>
              	<#else>
              		<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : eachOrder.statusId}, true) />
                	<td>${statusItem.description?default(eachOrder.statusId)}</td>
              		<td><a class="buttontext" href="<@ofbizUrl>nonRouteGatePass.pdf?orderId=${eachOrder.orderId?if_exists}&screenFlag=${screenFlag?if_exists}</@ofbizUrl>" target="_blank"/>Delivery Challan</td>
              	</#if>-->
					<#if (eachOrder.orderTotal) != (eachOrder.paidAmt) && (eachOrder.salesButton) != "Y" >
              	 <td><input type="button" name="Payment" id="Payment" value="Indent Payment" onclick="javascript:showPaymentEntryForDepotPayment('${eachOrder.orderId}','${eachOrder.partyId}','${eachOrder.partyName}','${eachOrder.orderTotal}','${eachOrder.balance}');"/></td>
                   <#else>
                     <td></td>
                   </#if>
           
                <#if (eachOrder.orderTotal) == (eachOrder.balance)>
                <td>Payment Realized</td>
                <#elseif (eachOrder.balance) == 0 >
                <td>Payment Not Received</td>
                <#elseif (eachOrder.orderTotal) != (eachOrder.balance)>
                <td>Payment Received</td>
                </#if>
           
                 <td>${eachOrder.paidAmt?if_exists}</td>
           
                <#if (eachOrder.salesButton) == "Y" >
              <#-- <td><input type="button" name="salesInvoice" id="salesInvoice" value="SalesInvoice" onclick="javascript: raiseDepotInvoiceFromBranch('${eachOrder.orderId}','${eachOrder.shipmentId}');"/></td>-->
                
                 <td><a class="buttontext" href="<@ofbizUrl>DepotMaterialSalesInvoiceInit?shipmentId=${eachOrder.shipmentId?if_exists}&orderId=${eachOrder.orderId?if_exists}&partyIdTo=${eachOrder.partyId?if_exists}</@ofbizUrl>" target="_blank"/>Sales Invoice</td>
                <#else>
                <td>${(eachOrder.salesNo)?if_exists}</td>
                </#if>
                
              <#--  <#if eachOrder.statusId != "INVOICE_CANCELLED" && eachOrder.invoiceId?has_content> <td><a class="buttontext" target='_blank' href="<@ofbizUrl>MaterialDepotSalesEditInvoiceInit?invoiceId=${eachOrder.invoiceId}&amp;partyId=${eachOrder.partyId}&amp;partyName=${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, eachOrder.partyId, false)}</@ofbizUrl>">Edit Invoice</a></td>
                <#else>
 		   	       <td></td>
              </#if>-->
                
                
       			<td><#if (eachOrder.salesButton) == "Y"><input type="button" name="viewOrder" id="viewOrder" value="cancel" onclick="javascript:cancelDepotSaleOrderCaution('${eachOrder.orderId}','${eachOrder.partyId}')"></#if></td>
           
            <#--<#if orderPreferenceMap.get(eachOrder.orderId)?exists>
              	<td><input type="button" name="Payment" id="Payment" value="Payment" onclick="javascript:showPayment('${orderPreferenceMap.get(eachOrder.orderId)}');"/></td>
              	<#else>
              	<td></td>
              	</#if> -->
               
              <#--  <#if ((statusConfirmMap.get(eachOrder.orderId))=="visible") >
                 <td><input type="button" name="realize" id="realize" value="Payment Received" onclick="javascript: realizeStatusChange('${eachOrder.orderId}');"/></td>
              	<#elseif (paymentSatusMap.get(eachOrder.orderId).get("statusId"))=="PMNT_RECEIVED" && ((eachOrder.orderTotal) == (paymentSatusMap.get(eachOrder.orderId).get("amount")))>
              	<td>Payment Realized</td>
              	<#else>
              	<td>Payment Not Received</td>
              	</#if>  -->
                
               
                <#-->
                <#if (eachOrder.orderTotal) == (eachPaymentOrderMap.get(eachOrder.orderId)).get("totAmount")>
                <td>Payment Realized</td>
                <#elseif (balanceAmountMap.get(eachOrder.orderId)).get("totAmount")==-1>
                <td>Payment Not Received</td>
                <#elseif (eachOrder.orderTotal) != (balanceAmountMap.get(eachOrder.orderId)).get("totAmount")>
                <td>Payment Received</td>
                </#if>
                
                <#if (advancePaymentVisible.get(eachOrder.orderId)) != "notVisible">
                <td><input type="button" name="realize" id="realize" value="Advance Payments" onclick="javascript: realizeStatusChange('${eachOrder.orderId}');"/></td>
                <#else>
                <td></td>
                </#if> 
                       
                
                
              	   
              	  <td>-->
              	  <#--<td><a class="buttontext" href="<@ofbizUrl>realizeStatus?userLogin=${userLogin}&&paymentPreferenceId=10000</@ofbizUrl>">Payment Received</a></td>
              	 <#if orderPreferenceMap.get(eachOrder.orderId)?exists>
              	 
              	<#else>
              	<td></td>
              	  </#if>-->
              	
              	<#--	<td><input type="button" name="editOrder" id="editOrder" value="Edit Order" onclick="javascript: editDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
				<td><input type="button" name="POOrder" id="POOrder" value="Po Order" onclick="javascript: purchaseOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}','${eachOrder.orderDate?if_exists}');"/></td>-->
              	<#--<td><input type="hidden" name="partyOBAmount"  value="${partyOb}" />${partyOb?string("#0.00")}</td>-->
        		<#--<td><input type="button" name="cancelOrder" id="cancelOrder" value="Cancel Order" onclick="javascript: cancelIceCreamOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}');"/></td>-->
              	<#--<td><input type="text" name="paymentAmount" id="paymentAmount" onchange="javascript: getPaymentTotal();"></td>-->
             <#-- 	<#if eachOrder.get('statusId') == "ORDER_APPROVED">
              		<td><input type="checkbox" id="orderId_${eachOrder_index}" name="orderId" value="${eachOrder.orderId?if_exists}"/></td>
              	</#if>-->
              	
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
