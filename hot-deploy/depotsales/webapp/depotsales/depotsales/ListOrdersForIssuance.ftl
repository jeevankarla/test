
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
        
//]]>


/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	var organisationList;
	var partyName;
	var partyIdVal;
	function dialogue(content, title) {
		/* 
		 * Since the dialogue isn't really a tooltip as such, we'll use a dummy
		 * out-of-DOM element as our target instead of an actual element like document.body
		 */
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
				  //getEmploymentDetails(partyIdVal);
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	
 	function cancelForm(){                 
         return false;
 	}
 	
 	function datepick1()	{	
		$( "#saleOrderDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: false,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).val(selectedDate);
			}
			
			});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		//
	}
	
	function issueIndentItems(orderId, partyId) {
		
		var message = "";
		message += "<form action='processIndentItemIssuance' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>OrderId:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='orderId' name='orderId' value='"+orderId+"'/></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Issue to Party:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='partyId' name='partyId' value='"+partyId+"'/></td></tr>";
		
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Issue Indent Items<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	
	
	
	function fetchOrderInformation(order) {
		orderId = order;
		var dataJson = {"orderId": orderId};
		
		jQuery.ajax({
                url: 'getOrderInformation',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
					
						orderData = result["orderInformationDetails"];
						
						
						showOrderInformation();
						
               		}
               	}							
		});
	}
	
	
	
	function showOrderInformation() {
		var message = "";
		var title = "";
		if(orderData != undefined){
			var orderAmt = 0;
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Product Id</td><td align='center' class='h3'> Product Name</td><td align='center' class='h3'> Remarks</td><td align='center' class='h3'> Quantity</td><td align='center' class='h3'> Unit Price</td><td align='center' class='h3'> Amount</td><td align='center' class='h3'> Quota Qty</td><td align='center' class='h3'> Discount</td><td align='center' class='h3'> Payable</td>";
			for (i = 0; i < orderData.length; ++i) {
			  	message += "<tr><td align='center' class='h4'>" + orderData[i].productId + "</td><td align='left' class='h4'>" + orderData[i].prductName + "</td><td align='left' class='h4'>" + orderData[i].remarks + "</td><td align='center' class='h4'>"+ orderData[i].quantity +"</td><td align='center' class='h4'>"+ orderData[i].unitPrice +"</td><td align='center' class='h4'>"+ orderData[i].itemAmt +"</td><td align='center' class='h4'>"+ Math.round(orderData[i].quotaAvbl) +"</td><td align='center' class='h4'>"+ orderData[i].adjustmentAmount +"</td><td align='center' class='h4'>"+ orderData[i].payableAmt +"</td>";
			  	orderAmt = orderAmt+orderData[i].payableAmt;
			}
			message += "<tr class='h3'><td></td><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();' class='submit'>Close</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "<center><br /> Total Order Value = "+ orderAmt +" ";
			message += "</table>";
			Alert(message, title);
		}
		
	};
	
	
	
	
	
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
          <td>Issue to Party</td>
          <td>Order Id</td>
          <td>Order Date</td>
     <#-->     <td>Grand Total</td>  -->
         <td>View Order</td>
         <td>Payment Status</td>
         <td>Issue Indent Items</td>
          
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list orderList as eachOrder>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              	<td>${eachOrder.partyName?if_exists}[${eachOrder.partyId?if_exists}]</td>
              	<td>${eachOrder.orderId?if_exists}</td>
              	<td>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachOrder.orderDate, "dd/MM/yyyy")}</td>
            <#-->    <td>${eachOrder.orderTotal?if_exists}</td>  -->
              	<td><input type="button" name="viewOrder" id="viewOrder" value="View Order" onclick="javascript:fetchOrderInformation('${eachOrder.orderId?if_exists}');"/></td>
              
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
                  <#if ((paymentSatusMap.get(eachOrder.orderId).get("statusId"))=="PMNT_RECEIVED") && !((eachOrder.orderTotal) == (paymentSatusMap.get(eachOrder.orderId).get("amount")))>
                 <td>Payment Received</td>
              	<#elseif (paymentSatusMap.get(eachOrder.orderId).get("statusId"))=="PMNT_RECEIVED" && ((eachOrder.orderTotal) == (paymentSatusMap.get(eachOrder.orderId).get("amount")))>
              	<td>Payment Realized</td>
              	<#else>
              	<td>Payment Not Received</td>
              	</#if>
              	
              	<#if (paymentSatusMap.get(eachOrder.orderId).get("statusId"))=="PMNT_RECEIVED">
              		<td><input type="button" name="issuance" id="issuance" value="Issue Indent Items" onclick="javascript:issueIndentItems('${eachOrder.orderId}','${eachOrder.partyId}');"/></td>
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
