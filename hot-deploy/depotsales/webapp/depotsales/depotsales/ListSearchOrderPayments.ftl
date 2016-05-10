
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

<input type="hidden" name="paramOrderId" id="paramOrderId" value="${paramOrderId}">
<input type="hidden" name="paramFacilityId" id="paramFacilityId" value="${paramFacilityId}">
<input type="hidden" name="paramEstimatedDeliveryDate" id="paramEstimatedDeliveryDate" value="${paramEstimatedDeliveryDate}">
<input type="hidden" name="paramStatusId" id="paramStatusId" value="${paramStatusId}">
<input type="hidden" name="paramBranch" id="paramBranch" value="${paramBranch}">
<input type="hidden" name="indentDateSort" id="indentDateSort" value="${indentDateSort}">


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
		var param1 = jQuery("<input>").attr("type", "hidden").attr("name", "paymentPreferenceId").val(orderId);
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



var orderId = $("#paramOrderId").val();
var paramFacilityId = $("#paramFacilityId").val();
var paramEstimatedDeliveryDate = $("#paramEstimatedDeliveryDate").val();
var paramStatusId = $("#paramStatusId").val();
var paramBranch = $("#paramBranch").val();
var indentDateSort = $("#indentDateSort").val();



var orderData;
var domOrderIds = "";
var low = 0, high = 20;
$(document).ready(function() {
   $(window).scroll(function() {
    	if($(window).scrollTop() == $(document).height() - $(window).height()) {
          
           low = high;
           high = high + 20;
           recursively_ajax();           
    	}
});


	recursively_ajax();

});

  function recursively_ajax(){
    
		var dataJson = {"orderId":orderId,"partyId":paramFacilityId,"estimatedDeliveryDate":paramEstimatedDeliveryDate,"statusId":paramStatusId,"partyIdFrom":paramBranch,"indentDateSort":indentDateSort,"low":low,"high":high};
	
     
    jQuery.ajax({
                url: 'getPaymentDetails',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderData = result["orderList"];
                        if(orderData.length != 0)
                        drawTable(orderData);   
                        else
                         alert("No Orders Found");        
               		}
               	}							
		});
}
	



function drawTable(data) {

    for (var i = 0; i < data.length; i++) {
    
        if(!domOrderIds.includes(data[i].orderId+" "))
         {
            drawRow(data[i]);
         domOrderIds = domOrderIds + data[i].orderId +" ";
         }
         
         
         
    }
}

var totIndents = 0;

function drawRow(rowData) {
    var row = $("<tr />")
    $("#coreTable").append(row); 
    row.append($("<td>" + rowData.partyId + "</td>"));
    row.append($("<td>" + rowData.partyName + "</td>"));
    row.append($("<td>" + rowData.orderId + "</td>"));
    
    var indDateSplit = (rowData.orderDate).split("-");
    
    var indentDate = indDateSplit[2] + "/" + indDateSplit[1] + "/" + indDateSplit[0];
    
    row.append($("<td>" + indentDate + "</td>"));
    row.append($("<td>" + rowData.orderTotal + "</td>"));

    //For Indent View
    
    var orderParam = '\'' + rowData.orderId + '\'';
    var orderCustomMethod = "javascript:fetchOrderInformation("+ orderParam + ")";
    var viewButton ='<input type=button name="viewOrder" id=viewOrder value="view Order" onclick="'+orderCustomMethod+'">';
    
    row.append($("<td>" +  viewButton  +"</td>"));
   
   //For indent Payment
   
   if(rowData.orderTotal != rowData.paidAmt)
   {
    var partyName = "'" + rowData.partyName + "'";
    var methodParam = '\'' + rowData.orderId + '\',\'' + rowData.partyId+'\','+partyName+','+rowData.orderTotal+','+rowData.balance;
    var customMethod = "javascript:showPaymentEntryForIndentPayment("+ methodParam + ")";
    var inputbox ='<input type=button name="Payment" id=Payment value="Indent Payment" onclick="'+customMethod+'">';
    row.append($("<td>" +  inputbox  +"</td>"));
    }else{
    row.append($("<td></td>"));
    }
    
    if(rowData.orderTotal != rowData.balance)
    {
     row.append($("<td>Payment Realized</td>"));
    }else if(rowData.balance == 0){
     row.append($("<td>Payment Not Received</td>"));
    }else{
     row.append($("<td>Payment Received</td>"));
    }
    row.append($("<td>" + rowData.paidAmt + "</td>"));
    
    totIndents = totIndents+1;
    
    $("#totIndents").html("<h10>"+totIndents+"</h10>");
    
}



    
  
</script>



</script>
<#include "viewOrderDetailsDepot.ftl"/>

<#-->
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
<form name="realizeStatus" id="realizeStatus" method="post" action="realizeStatus"> 
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

-->

<#include "viewOrderDetailsDepot.ftl"/>

<#-->
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
<form name="realizeStatus" id="realizeStatus" method="post" action="realizeStatus"> 
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
-->
 
 
 
 
 <div id = "firstDiv" style="border-width: 2px; padding-top: 20px;   border-radius: 10px; border-style: solid; border-color: grey; ">
  
    <div id = "secondDiv" align="center" style=" border-radius: 10px; width:1400;  height:22px;  font-size: larger; background-color: lightblue;">Total Indents : <label  align="center" id="totIndents"style="color: red" ></label> </div>
  
 
   
  
  <form name="listOrders" id="listOrders"   method="post" >
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
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
      <#assign alt_row = !alt_row>
      </tbody>
    </table>
  </form>
  </div>
