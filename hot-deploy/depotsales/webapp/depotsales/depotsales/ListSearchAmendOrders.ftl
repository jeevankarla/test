
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
	
	 $('div#orderSpinn').html('<img src="/images/gears.gif" height="70" width="70">');
     
    jQuery.ajax({
                url: 'getAmendOrderDetails',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderData = result["orderList"];
                        if(orderData.length != 0){
                        $('div#orderSpinn').html("");
                        $('div#blink').hide();
                        drawTable(orderData);   
                       }else{
                        $('div#orderSpinn').html("");
                         setInterval(blinker, 1000);
                        
                         }        
               		}
               	}							
		});
}
	

function blinker() {
    $('div#blink').show();
    $('.blink_me').fadeOut(500);
    $('.blink_me').fadeIn(500);
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
       
    var salesOrder = '\'' + rowData.orderId + '\'';
    var SalesOrderView;
    var PurchaseOrderView;
    if(rowData.orderNo != undefined && rowData.orderNo != "NA"){
           SalesOrderView ='<a class="buttontext" href="<@ofbizUrl>ViewIndentRequest?orderId='+rowData.orderId+'</@ofbizUrl>" target="_blank">'+rowData.orderNo+'</a>';
   
    }else{
          SalesOrderView ='<a class="buttontext" href="<@ofbizUrl>ViewIndentRequest?orderId='+rowData.orderId+'</@ofbizUrl>" target="_blank">'+rowData.orderId+' </a>';
    }
    row.append($("<td>"+ SalesOrderView +"</td>"));
    if(rowData.poOrder != undefined && rowData.poOrder != "NA"){
           PurchaseOrderView ='<a class="buttontext" href="<@ofbizUrl>POoverview?orderId='+rowData.PoOrderId+'</@ofbizUrl>" target="_blank">'+rowData.poOrder+'</a>';
    }
    else{
           PurchaseOrderView ='<a class="buttontext" href="<@ofbizUrl>POoverview?orderId='+rowData.PoOrderId+'</@ofbizUrl>" target="_blank">'+rowData.PoOrderId+'</a>';
    }    
    row.append($("<td>" + PurchaseOrderView + "</td>"));
    
    var indDateSplit = (rowData.orderDate).split("-");
    
    var indentDate = indDateSplit[2] + "/" + indDateSplit[1] + "/" + indDateSplit[0];
    
    row.append($("<td>" + indentDate + "</td>"));
    row.append($("<td>" + rowData.orderTotal + "</td>"));

    //For Indent View
    
    var salesOrder = '\'' + rowData.orderId + '\'';
    var purchaseOrder = '\'' + rowData.PoOrderId + '\'';
    
    var AmendOrder ='<a class="buttontext" href="<@ofbizUrl>amendOrder?orderId='+rowData.orderId+'&&partyId='+rowData.partyId+'</@ofbizUrl>" target="_blank">Amend Order</a>';
    row.append($("<td>"+ AmendOrder +"</td>"));    
    if(rowData.statusId == 'ORDER_APPROVED'){
     row.append($("<td>Approved</td>"));
    }
    totIndents = totIndents+1;
    
    $("#totIndents").html("<h10>"+totIndents+"</h10>");
    
}


    
  
</script>



</script>
<#include "viewOrderDetailsDepot.ftl"/>
 
 
 <div id = "firstDiv" style="border-width: 2px; padding-top: 20px;   border-radius: 10px; border-style: solid; border-color: grey; ">
  
    <div id = "secondDiv" align="center" style=" border-radius: 10px; width:1400;  height:22px;  font-size: larger; background-color: lightblue;">Total Indents : <label  align="center" id="totIndents"style="color: blue" ></label> </div>
  
      
  
  <form name="listOrders" id="listOrders"   method="post" >
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Party Code</td>
          <td>Party Name</td>
          <td>Indent Id</td>
          <td>Purchase Order</td>
          <td>Indent Date</td>
          <td>Grand Total</td>
          <td>Amend Order</td>
          <td>Indent Status</td>         
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign alt_row = !alt_row>
      </tbody>
    </table>
  </form>
        <div align='center' name ='displayMsg' id='orderSpinn'/></div>
         <div id="blink"  align='center'  style=" border-radius: 15px;  color:red; height:20px;   font-size: larger; background-color: lightblue;"><span class="blink_me"><b>NO More Orders...! </b></span></div>
  