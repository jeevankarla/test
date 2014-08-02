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
        var vehicleId = $("#vehicleId").val();
        var modeOfDespatch = $("#modeOfDespatch").val();
        var shipmentTypeId = $("#shipmentTypeId").val();
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
    	appStr += "<input type=hidden name=modeOfDespatch value='"+ modeOfDespatch +"' />";
    	appStr += "<input type=hidden name=shipmentTypeId value='"+ shipmentTypeId +"' />";
    	$("#processOrdersForm").append(appStr);
    	var salesChannel = '${parameters.salesChannelEnumId?if_exists}';
    	var splStr = "<input type=hidden name=salesChannelEnumId value='"+ salesChannel +"' />";
        $("#processOrdersForm").append(splStr);
    	jQuery('#processOrdersForm').submit();
        
    }
    
    function approveIceCreamOrder(orderId, salesChannel){
		var formId = "#" + "orderApproveForm";
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
		var param2 = jQuery("<input>").attr("type", "hidden").attr("name", "salesChannelEnumId").val(salesChannel);
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
<set field="screenFlag" value=""/>
<form name="orderCancelForm" id="orderCancelForm" method="post" <#if screenFlag?exists && screenFlag=="icpSales">action="cancelICPNandiniOrder"<#elseif screenFlag?exists && screenFlag=="icpAmulSales">action="cancelICPAmulOrder"<#else></#if>>
</form>
<form name="orderApproveForm" id="orderApproveForm" method="post" <#if screenFlag?exists && screenFlag=="icpSales">action="approveICPNandiniOrder"<#elseif screenFlag?exists && screenFlag=="icpAmulSales">action="approveICPAmulOrder"<#else></#if>>
</form>
<form name="processOrdersForm" id="processOrdersForm" method="post" <#if screenFlag?exists && screenFlag=="icpSales">action="createShipmentAndInvoiceForNandiniOrders"<#elseif screenFlag?exists && screenFlag=="icpAmulSales">action="createShipmentAndInvoiceForAmulOrders"<#else></#if>>
</form>
<#if orderList?has_content>
  
  <form name="listOrders" id="listOrders"  method="post">
    <div align="right" width="100%">
    	<#if screenFlag?exists && screenFlag=="icpSales">
    		<input class='h3' type='hidden' id='shipmentTypeId' name='shipmentTypeId' value='ICP_NANDINI_SHIPMENT'/>
    	<#else>
    		<input class='h3' type='hidden' id='shipmentTypeId' name='shipmentTypeId' value='ICP_AMUL_SHIPMENT'/>
    	</#if>
    	
    	<table width="100%">
    		<tr>
    			<td><span class="label"> Vehicle Number:</span><input class='h3' type='text' id='vehicleId' name='vehicleId'/></td>
    			<td><span class="label">Mode of Despatch:</span><select name="modeOfDespatch" id="modeOfDespatch"><option value="By Road">By Road</option><option value="By Air">By Air</option><option value="By Sea">By Sea</option></select></td>
    			<td><span class="label"> Shipment Date:</span><input class='h3' type='text' id='shipDate' name='shipDate' value='${defaultEffectiveDate?if_exists}' onmouseover='datepick()'/></td>
    			<td><input id="submitButton" type="button"  onclick="javascript:processOrders(this);" value="Ship Orders"/></td>
    		</tr>
    	</table>
    	
    </div>
	<br/>
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Party Code</td>
          <td>Party Name</td>
          <td>Order Id</td>
          <td>Order Date</td>
          <td>Approve</td>
          <td>Cancel</td>
		  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllOrders" name="checkAllOrders" onchange="javascript:toggleOrderId(this);"/></td>
          
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#list orderList as eachOrder>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<td>${eachOrder.partyId?if_exists}</td>
              	<td>${eachOrder.partyName?if_exists}</td>
              	<td>${eachOrder.orderId?if_exists}</td>
              	<td>${eachOrder.orderDate?if_exists}</td>
              	<#if eachOrder.get('statusId') == "ORDER_CREATED">
              		<td><input type="button" name="approveOrder" id="approveOrder" value="Approve Order" onclick="javascript: approveIceCreamOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}');"/></td>
              	<#else>
              		<td>${eachOrder.statusId?if_exists}</td>
              	</#if>
        		
        		<td><input type="button" name="cancelOrder" id="cancelOrder" value="Cancel Order" onclick="javascript: cancelIceCreamOrder('${eachOrder.orderId?if_exists}', '${parameters.salesChannelEnumId}');"/></td>
              	<#--<td><input type="text" name="paymentAmount" id="paymentAmount" onchange="javascript: getPaymentTotal();"></td>-->
              	<#if eachOrder.get('statusId') == "ORDER_APPROVED">
              		<td><input type="checkbox" id="orderId_${eachOrder_index}" name="orderId" value="${eachOrder.orderId?if_exists}"/></td>
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
