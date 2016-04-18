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
    
     function creditApproveDepotOrder(orderId, salesChannel,partyId){
		var formId = "#" + "creditOrderApproveForm";
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
    
    function cancelIceCreamOrder(orderId, salesChannel,partyId){
		var formId = "#" + "orderCancelForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
var param3 = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
		jQuery(formId).append(jQuery(param3));
        jQuery(formId).submit();
    }
   function approveDraftPO(orderId, statusId){
		var formId = "#" + "approveDraftPoForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "statusId").val(statusId);
		jQuery(formId).append(jQuery(param1));
		jQuery(formId).append(jQuery(param2));
        jQuery(formId).submit();
    }
        
//]]>
</script>
<#include "viewOrderDetailsDepot.ftl"/>

<form name="orderEditForm" id="orderEditForm" method="post" 
	
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="editBranchIndent"
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
		action="approveDepotSalesOrder"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="approveIUSTransferOrder"
	</#if>> 
</form>
<form name="creditOrderApproveForm" id="creditOrderApproveForm" method="post" 
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="CreditapproveDepotSalesOrder"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="approveIUSTransferOrder"
	</#if>
</form>

<form name="processOrdersForm" id="processOrdersForm" method="post" 
	<#if screenFlag?exists && screenFlag=="depotSales">
		action="createShipmentAndInvoiceForDepotSalesOrders"
	<#elseif screenFlag?exists && screenFlag=="InterUnitTransferSale">
		action="createShipAndInvForIUSTransferOrders"
	</#if>>
</form>
<form name="approveDraftPoForm" id="approveDraftPoForm" method="post" action="approvalLevelOfDraftPo"/>		
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
        <td>Indent Id</td>
         <td>Indent Date</td>
          <td>Party Name</td>
          <td>Supplier Name</td>
         <#--> <td>Print Indent</td>-->
          <td>Edit</td>
          <td>Minutes</td>
          <td>DraftPO</td>
          <td>P&S Approvals</td>
          <td>Minutes Hindi</td>
          <#--<td>Edit Batch</td>-->
          <td>Approve</td>

        <#-- <td>DC Report</td> -->
          <#-- <td>Payment</td> -->
         <#-- <td>Generate PO</td>-->
          <#--<td>Party Balance</td>-->
          <td>Cancel</td>
		<#--  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllOrders" name="checkAllOrders" onchange="javascript:toggleOrderId(this);"/></td>-->
          
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
       <#list orderList as eachOrder>
        
           <#assign supplierPartyId="">
				<#assign supplierPartyName="">
				<#assign isgeneratedPO="N">
				<#assign POorderId="">
				
		              	<#assign productStoreId="">
						<#if orderDetailsMap?has_content>
								<#assign orderDetails=orderDetailsMap.get(eachOrder.orderId)?if_exists>
								<#if orderDetails?has_content>
								<#assign supplierPartyId=orderDetails.get("supplierPartyId")>
								<#assign supplierPartyName=orderDetails.get("supplierPartyName")>
								<#assign isgeneratedPO=orderDetails.get("isgeneratedPO")>
								<#assign POorderId=orderDetails.get("POorder")>
								<#assign productStoreId=orderDetails.get("productStoreId")>
								</#if>
				</#if>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
      		<td><input type="button" name="viewOrder" id="viewOrder" value="${eachOrder.orderId?if_exists}" onclick="javascript:fetchOrderInformation('${eachOrder.orderId?if_exists}','${parameters.salesChannelEnumId}');"/></td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachOrder.orderDate, "dd/MM/yyyy")}</td>
              	<td>${eachOrder.partyName?if_exists}   [${eachOrder.partyId?if_exists}]</td>
              	<td>${supplierPartyName?if_exists}  [${supplierPartyId?if_exists}]</td>
              <#--	<td><a class="buttontext" href="<@ofbizUrl>indentPrintReport.pdf?orderId=${eachOrder.orderId?if_exists}&&partyName=${eachOrder.partyName?if_exists}&&partyId=${eachOrder.partyId?if_exists}</@ofbizUrl>" target="_blank"/>Indent Report</td>-->
             
             	<#if (eachOrder.get('statusId') == "ORDER_CREATED") && isgeneratedPO =="N">
              	<td><input type="button" name="editOrder" id="editOrder" value="Edit" onclick="javascript: editDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
				<#else>
				<td></td>
				</#if>
              	<td><a class="buttontext" href="<@ofbizUrl>minutesPdfReport.pdf?orderId=${eachOrder.orderId?if_exists}&&partyName=${eachOrder.partyName?if_exists}</@ofbizUrl>" target="_blank"/>Minutes</td>
              <#if (eachOrder.get('statusId') != "ORDER_APPROVED") && (isgeneratedPO =="N")>
              	<td><a class="buttontext" href="<@ofbizUrl>CreateBranchTransPO?orderId=${eachOrder.orderId?if_exists}&&partyName=${eachOrder.partyName?if_exists}</@ofbizUrl>" target="_blank"/>DraftPO</td>
              	<#else>
              	<td>${POorderId?if_exists}</td>
              	</#if>
                <#if (isgeneratedPO =="Y")> 
	                <#if (eachOrder.get('statusId') == "ORDER_CREATED")>          
	                       <#assign statusId ="APPROVE_LEVEL1">
	                       <#assign StatusItem = delegator.findOne("StatusItem", {"statusId" :statusId}, true)>
	                       <td><input type="button" name="approveDaftPO" id="approveDaftPO" value="${StatusItem.description}" onclick="javascript: approveDraftPO('${eachOrder.orderId?if_exists}', '${statusId}');"/></td>
	                </#if>
	                <#if (eachOrder.get('statusId') == "APPROVE_LEVEL1")>          
	                       <#assign statusId ="APPROVE_LEVEL2">
	                       <#assign StatusItem = delegator.findOne("StatusItem", {"statusId" :statusId}, true)>
	                       <td><input type="button" name="approveDaftPO" id="approveDaftPO" value="${StatusItem.description}" onclick="javascript: approveDraftPO('${eachOrder.orderId?if_exists}', '${statusId}');"/></td>
	                </#if>
	                <#if (eachOrder.get('statusId') == "APPROVE_LEVEL2")>          
	                       <#assign statusId ="APPROVE_LEVEL3">
	                       <#assign StatusItem = delegator.findOne("StatusItem", {"statusId" :statusId}, true)>
	                       <td><input type="button" name="approveDaftPO" id="approveDaftPO" value="${StatusItem.description}" onclick="javascript: approveDraftPO('${eachOrder.orderId?if_exists}', '${statusId}');"/></td>
	                </#if>
	                <#if eachOrder.get('statusId') == "APPROVE_LEVEL3" || eachOrder.get('statusId') == "ORDER_APPROVED">      
                             <td>P&S Approved</td>
                     </#if>
                <#else>
                   <td></td>
                </#if>
              	<td><a class="buttontext" href="<@ofbizUrl>minutesHindiPdfReport.pdf?orderId=${eachOrder.orderId?if_exists}&&partyName=${eachOrder.partyName?if_exists}&&flag=${"hindi"}</@ofbizUrl>" target="_blank"/>Minutes Hindi</td>
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
              	
              <#-->	<#if (paymentSatusMap.get(eachOrder.orderId).get("amount"))==0 && (eachOrder.get('statusId') != "ORDER_APPROVED")>-->
              	<#if (eachOrder.orderTotal) == (balanceAmountMap.get(eachOrder.orderId)).get("receivedAMT") && (eachOrder.get('statusId') == "APPROVE_LEVEL3") && (isgeneratedPO !="N")>
                     <#if ((eachOrder.orderTotal)>= 0) && ((eachOrder.orderTotal)<= 200000)>
              		       <td><input type="button" name="approveOrder" id="approveOrder" value="    BO Approve     " onclick="javascript: approveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                     <#else>
                           <#if ((eachOrder.orderTotal)>200000) && ((eachOrder.orderTotal)<= 5000000)>
                                <td><input type="button" name="approveOrder" id="approveOrder" value="    RO Approve     " onclick="javascript: approveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                           <#else>
                                  <#if ((eachOrder.orderTotal)>5000000) && ((eachOrder.orderTotal)<= 10000000)>
                                      <td><input type="button" name="approveOrder" id="approveOrder" value="    HO Approve     " onclick="javascript: approveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                                  <#else>  
                                        <td><input type="button" name="approveOrder" id="approveOrder" value="    MD Approve     " onclick="javascript: approveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                                   </#if>
                           </#if>
                    </#if> 
                 <#elseif (eachOrder.get('statusId') == "ORDER_APPROVED")>
              	  <td>Approved</td>
              	 <#elseif (balanceAmountMap.get(eachOrder.orderId)).get("receivedAMT") != -1 && (eachOrder.get('statusId') == "APPROVE_LEVEL3") && (isgeneratedPO !="N")>
					<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : eachOrder.statusId}, true) />
                    <#if ((eachOrder.orderTotal)>= 0) && ((eachOrder.orderTotal)<= 200000)>
	            	     <td><input type="button" name="approveOrder" id="approveOrder" value="BO Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
	            	<#else>
                           <#if ((eachOrder.orderTotal)>200000) && ((eachOrder.orderTotal)<= 5000000)>
                               <td><input type="button" name="approveOrder" id="approveOrder" value="RO Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                           <#else>
                                  <#if ((eachOrder.orderTotal)>5000000) && ((eachOrder.orderTotal)<= 10000000)>
                                       <td><input type="button" name="approveOrder" id="approveOrder" value="HO Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                                  <#else>
                                         <td><input type="button" name="approveOrder" id="approveOrder" value="MD Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                                  </#if>
                          </#if>  
                     </#if>      	
	          		<#else>
	          		<#if isgeneratedPO !="N" && (eachOrder.get('statusId') == "APPROVE_LEVEL3")>
	          		<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : eachOrder.statusId}, true) />
                      <#if ((eachOrder.orderTotal)>= 0) && ((eachOrder.orderTotal)<= 200000)>
                         <td><input type="button" name="approveOrder" id="approveOrder" value="BO Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
	            	  <#else>
                           <#if ((eachOrder.orderTotal)>200000) && ((eachOrder.orderTotal)<= 5000000)>
                         	  <td><input type="button" name="approveOrder" id="approveOrder" value="RO Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                           <#else>
                                  <#if ((eachOrder.orderTotal)>5000000) && ((eachOrder.orderTotal)<= 10000000)>
                         	          <td><input type="button" name="approveOrder" id="approveOrder" value="HO Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                                  <#else>
                         	          <td><input type="button" name="approveOrder" id="approveOrder" value="MD Credit Approve" onclick="javascript: creditApproveDepotOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId?if_exists}');"/></td>
                                  </#if>
                          </#if>  
                     </#if>    
	          	<#--	<td><a class="buttontext" href="<@ofbizUrl>nonRouteGatePass.pdf?orderId=${eachOrder.orderId?if_exists}&screenFlag=${screenFlag?if_exists}</@ofbizUrl>" target="_blank"/>Delivery Challan</td> -->
                 <#else>
                   <td></td>
              	</#if>
				</#if>
               
              <#--	<td><input type="button" name="Payment" id="Payment" value="Payment" onclick="javascript:showPaymentEntry('${eachOrder.orderId}','${eachOrder.partyId}','${eachOrder.partyName}');"/></td>-->
              	
              
              	<#--><#if (eachOrder.get('statusId') == "ORDER_APPROVED") && (isgeneratedPO =="N")>
					<td><input type="button" name="POOrder" id="POOrder" value="Generate PO" onclick="javascript: purchaseOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${supplierPartyId}','${supplierPartyName}','${productStoreId}','${eachOrder.partyId}','${eachOrder.orderDate?if_exists}', '${eachOrder.billFromVendorPartyId}');"/></td>
         		<#else>
					<td></td>
				</#if> -->  
              	<#--<td><input type="hidden" name="partyOBAmount"  value="${partyOb}" />${partyOb?string("#0.00")}</td>-->
        		<td><input type="button" name="cancelOrder" id="cancelOrder" value="Cancel" onclick="javascript: cancelIceCreamOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}','${eachOrder.partyId}');"/></td>
              	<#--<td><input type="text" name="paymentAmount" id="paymentAmount" onchange="javascript: getPaymentTotal();"></td>
              	<#if eachOrder.get('statusId') == "ORDER_APPROVED">
              		<td><input type="checkbox" id="orderId_${eachOrder_index}" name="orderId" value="${eachOrder.orderId?if_exists}"/></td>
              	</#if>
              	-->
              	
              	
              	
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
