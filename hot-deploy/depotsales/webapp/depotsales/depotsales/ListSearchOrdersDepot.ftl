
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
<input type="hidden" name="ApproveOrderId" id="ApproveOrderId">


<script type="text/javascript">



var orderId = $("#paramOrderId").val();
var paramFacilityId = $("#paramFacilityId").val();
var paramEstimatedDeliveryDate = $("#paramEstimatedDeliveryDate").val();
var paramStatusId = $("#paramStatusId").val();
var paramBranch = $("#paramBranch").val();
var indentDateSort = $("#indentDateSort").val();


var uniqueOrderIdsList = [];
var orderData;
var domOrderIds = "";
var low = 0, high = 30;
$(document).ready(function() {
   $(window).scroll(function() {
    	if($(window).scrollTop() == $(document).height() - $(window).height()) {
          
           low = high;
           high = high + 50;
         
           recursively_ajax();          
                    
    	}
});
	recursively_ajax();
});


  function recursively_ajax(orderIdFroApp){
    
        
     
         if(typeof(orderIdFroApp) != 'undefined' && orderIdFroApp != ''){
           orderId = orderIdFroApp;
            $("#coreTable").find("tr:not(:first)").remove();
           }
    
           var uniqueOrderId = JSON.stringify(uniqueOrderIdsList);
		var dataJson = {"orderId":orderId,"partyId":paramFacilityId,"estimatedDeliveryDate":paramEstimatedDeliveryDate,"statusId":paramStatusId,"partyIdFrom":paramBranch,"indentDateSort":indentDateSort,"uniqueOrderId":uniqueOrderId,"low":low,"high":high};
	
	
	 $('div#orderSpinn').html('<img src="/images/gears.gif" height="70" width="70">');
     
    jQuery.ajax({
                url: 'getIndentListingDetails',
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
            drawRow(data[i]);
          uniqueOrderIdsList.push(data[i].orderId);
    }
    
}


function drawRow(rowData) {
    var row = $("<tr />")
    $("#coreTable").append(row); 
    $("#coreTable tr:even").css("background-color", "#F4F4F8");
    
    
    //For Indent OverView
    
     if(rowData.orderNo != "NA"){
     var indentOVButton = '<a class="buttontext" href="<@ofbizUrl>ViewIndentRequest?orderId='+rowData.orderId+'&&partyName='+rowData.partyName+'</@ofbizUrl>" target="_blank">'+rowData.orderNo+'</a>';
     row.append($("<td>" +  indentOVButton  +"</td>"));
     }else{
     
      var indentOVButton = '<a class="buttontext" href="<@ofbizUrl>ViewIndentRequest?orderId='+rowData.orderId+'&&partyName='+rowData.partyName+'</@ofbizUrl>" target="_blank">'+rowData.orderId+'</a>';
     row.append($("<td>" +  indentOVButton  +"</td>"));
     }

    //Branch Name
    
     row.append($("<td>" + rowData.productStoreId + "</td>"));
   
    //party Name
    
     var partyNameCode = rowData.partyName+"["+rowData.partyId+"]";
    
    row.append($("<td>" + partyNameCode + "</td>"));
    
    //supplier Name
    
    var suppNameCode = rowData.supplierPartyName+"["+rowData.supplierPartyId+"]";
    row.append($("<td>" + suppNameCode + "</td>"));

     //indent Date  
    var indDateSplit = (rowData.orderDate).split("-");
    var indentDate = indDateSplit[2] + "/" + indDateSplit[1] + "/" + indDateSplit[0];
    row.append($("<td>" + indentDate + "</td>"));
    
    var editOrder = '<a class="buttontext" href="<@ofbizUrl>editBranchIndent?orderId='+rowData.orderId+'&&partyId='+rowData.partyId+'</@ofbizUrl>" target="_blank">Edit</a>';

   row.append($("<td>" +  editOrder  +"</td>"));  

    var minutesReport = '<a class="buttontext" href="<@ofbizUrl>minutesPdfReport.pdf?orderId='+rowData.orderId+'&&partyName='+rowData.partyName+'</@ofbizUrl>" target="_blank">Minutes</a>';
    
    row.append($("<td>" +  minutesReport  +"</td>"));  

     
     if(rowData.POorder != "NA"){
     
         if(rowData.poSquenceNo)
         row.append($("<td>" +  rowData.poSquenceNo  +"</td>"));  
         else
         row.append($("<td>" +  rowData.POorder  +"</td>"));
     
     }else{
     
       var DraftPoButton = '<a class="buttontext" href="<@ofbizUrl>CreateBranchTransPO?orderId='+rowData.orderId+'&&partyName='+rowData.partyName+'</@ofbizUrl>" target="_blank">DraftPO</a>'; 
       row.append($("<td>" +  DraftPoButton  +"</td>"));  
     
      }
       //For Indent View
    
    var orderParam = '\'' + rowData.orderId + '\'';
    var statusId = '\'' + rowData.statusId + '\'';
    var partyId = '\'' + rowData.partyId + '\'';
    
    
    
     
     if(rowData.statusId != "APPROVE_LEVEL3" && rowData.statusId != "ORDER_APPROVED" && rowData.statusId != "ORDER_CREATED" && rowData.statusId != "ORDER_CANCELLED"){
    var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
    
    var buutonName;
    
    if(rowData.statusId == "DRAFTPO_PROPOSAL")
     buutonName = "Commercial";
    if(rowData.statusId == "APPROVE_LEVEL1") 
      buutonName = "Account";
    if(rowData.statusId == "APPROVE_LEVEL2")  
       buutonName = "Regional";
    
    
    var approveButton ='<input type=button name="approveOrder" id=approveOrder value='+buutonName+'Head onclick="'+orderCustomMethod+'">';
    row.append($("<td>" +  approveButton  +"</td>"));
   }
   
   else if(rowData.statusId == "ORDER_CANCELLED"){
       row.append($("<td>Order Cancelled</td>"));
  }
   else{
        row.append($("<td>P&S Approved</td>"));
    }
    
    if(rowData.POorder != "NA"){
     var poReport = '<a class="buttontext" href="<@ofbizUrl>PurchaseOrderViewDepotSales.pdf?orderId='+rowData.POorder+'</@ofbizUrl>" target="_blank">PO Report</a>';
      row.append($("<td>" +  poReport  +"</td>")); 
    }else if(rowData.statusId == "ORDER_CANCELLED"){
	  
	      row.append($("<td>Order Cancelled</td>"));
	  }else{
       row.append($("<td></td>")); 
    }
    
    if ((rowData.orderTotal) <= (rowData.paidAmt) && (rowData.statusId == "APPROVE_LEVEL3") && (rowData.isgeneratedPO !="N")){
    
        if (((rowData.orderTotal)>= 0) && ((rowData.orderTotal)<= 200000))
        {
	    var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	    var approveButton ='<input type=button name="boapprove" id=boapprove value="BO Approve" onclick="'+orderCustomMethod+'">';
	    row.append($("<td>" +  approveButton  +"</td>"));
	    }else if (((rowData.orderTotal)>200000) && ((rowData.orderTotal)<= 5000000)){
	    var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	    var approveButton ='<input type=button name="boapprove" id=boapprove value="RO Approve" onclick="'+orderCustomMethod+'">';
	    row.append($("<td>" +  approveButton  +"</td>"));
	    }else if (((rowData.orderTotal)>5000000) && ((rowData.orderTotal)<= 10000000)){
	    var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	    var approveButton ='<input type=button name="boapprove" id=boapprove value="HO Approve" onclick="'+orderCustomMethod+'">';
	    row.append($("<td>" +  approveButton  +"</td>"));
	    }else{
	    var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	    var approveButton ='<input type=button name="boapprove" id=boapprove value="MD Approve" onclick="'+orderCustomMethod+'">';
	    row.append($("<td>" +  approveButton  +"</td>"));
	    }
    }
     if(rowData.statusId == "ORDER_APPROVED"){
	     row.append($("<td> Approved </td>"));
	  }else if ((rowData.paidAmt) != -1 && (rowData.statusId == "APPROVE_LEVEL3") && (rowData.isgeneratedPO !="N")){
	  
	     if (((rowData.orderTotal)>= 0) && ((rowData.orderTotal)<= 200000)){
	      
	    var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	    var approveButton ='<input type=button name="boapprove" id=boapprove value="BO Credit Approve" onclick="'+orderCustomMethod+'">';
	    row.append($("<td>" +  approveButton  +"</td>")); 
	     
	     }else if (((rowData.orderTotal)>200000) && ((rowData.orderTotal)<= 5000000)){
	      var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	      var approveButton ='<input type=button name="boapprove" id=boapprove value="RO Credit Approve" onclick="'+orderCustomMethod+'">';
	      row.append($("<td>" +  approveButton  +"</td>"));
	     } else if (((rowData.orderTotal)>5000000) && ((rowData.orderTotal)<= 10000000)){
	      var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	      var approveButton ='<input type=button name="boapprove" id=boapprove value="HO Credit Approve" onclick="'+orderCustomMethod+'">';
	      row.append($("<td>" +  approveButton  +"</td>"));
	     }else{
	      var orderCustomMethod = "javascript:forApprove("+ orderParam + ","+statusId+","+partyId+")";
	      var approveButton ='<input type=button name="boapprove" id=boapprove value="MD Credit Approve" onclick="'+orderCustomMethod+'">';
	      row.append($("<td>" +  approveButton  +"</td>"));
	     }
	  }else if(rowData.statusId == "ORDER_CANCELLED"){
	  
	      row.append($("<td>Order Cancelled</td>"));
	  }
	  else{
    
       row.append($("<td></td>"));
    }
    
       var orderParam = '\'' + rowData.orderId + '\'';
        var partyId = '\'' + rowData.partyId + '\'';
    var cancellorder = "javascript:cancelOrderCaution("+ orderParam + ","+ partyId +")";
    var viewButton ='<input type=button name="viewOrder" id=viewOrder value="cancel" onclick="'+cancellorder+'">';
    
    row.append($("<td>" +  viewButton  +"</td>"));
    
    
    
    
    
    
    
    
    
    $("#totIndents").html("<h10>"+rowData.totalIndents+"</h10>");

}

//==================For Search Fields==================

(function(document) {
	'use strict';

	var LightTableFilter = (function(Arr) {

		var _input;

		function _onInputEvent(e) {
			_input = e.target;
			var tables = document.getElementsByClassName(_input.getAttribute('data-table'));
			Arr.forEach.call(tables, function(table) {
				Arr.forEach.call(table.tBodies, function(tbody) {
					Arr.forEach.call(tbody.rows, _filter);
				});
			});
		}

		function _filter(row) {
			var text = row.textContent.toLowerCase(), val = _input.value.toLowerCase();
			row.style.display = text.indexOf(val) === -1 ? 'none' : 'table-row';
		}

		return {
			init: function() {
				var inputs = document.getElementsByClassName('light-table-filter');
				Arr.forEach.call(inputs, function(input) {
					input.oninput = _onInputEvent;
				});
			}
		};
	})(Array.prototype);

	document.addEventListener('readystatechange', function() {
		if (document.readyState === 'complete') {
			LightTableFilter.init();
		}
	});

})(document);



function forApprove(orderId,statusId,partyId){

$('div#orderSpinn').html('<img src="/images/gears.gif" height="70" width="70">');

   var response;
  $("#FindTankerSalesOrder_orderId").val(orderId);
  
  uniqueOrderIdsList = [];
    
    var statusMap = {};
    
    statusMap["DRAFTPO_PROPOSAL"] = "APPROVE_LEVEL1";
    statusMap["APPROVE_LEVEL1"] = "APPROVE_LEVEL2";
    statusMap["APPROVE_LEVEL2"] = "APPROVE_LEVEL3";
    statusMap["APPROVE_LEVEL3"] = "ORDER_APPROVED";
    
    
    var dataJson = {"orderId":orderId,"statusId":statusMap[statusId],"partyId":partyId};
    
     $('div#orderSpinn').html('<img src="/images/gears.gif" height="70" width="70">');
    
    jQuery.ajax({
                url: 'approveOrdersAjax',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderData = result["orderList"];
						
						
                        if(orderData[0].response == "success"){
                       
                          response =  orderData[0].response;
                            
                       }
                       
                       
               		}
               	}							
		});
        
     setTimeout(function(){ recursively_ajax(orderId);}, 2000);

}


  
</script>



</script>
<#include "viewOrderDetailsDepot.ftl"/>
 
 
 <div id = "firstDiv" style="border-width: 2px; padding-top: 20px;   border-radius: 10px; border-style: solid; border-color: grey; ">
  
     <font color="blue">Search:</font><input type="text"  style="border-radius: 5px;" class="light-table-filter" data-table="basic-table" placeholder="Filter by any">
  
  
    <div id = "secondDiv" align="center" style=" border-radius: 10px; width:1400;  height:22px;  font-size: larger; background-color: lightblue;">Total Indents : <label  align="center" id="totIndents"style="color: blue" ></label> </div>
  
  
  <form name="listOrders" id="listOrders"   method="post" >
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <th1ead>
        <tr class="header-row-2">
          <td>Indent Id</td>
          <td>Branch Name</td>
          <td>Weaver Name</td>
          <td>Supplier Name</td>
          <td>Indent Date</td>
          <td>Edit</td>
          <td>Minutes</td>
          <td>DraftPO</td>
          <td>P&S Approvals</td>
          <td>PO Report</td>
          <td>Approve</td>
           <td>Cancel</td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign alt_row = !alt_row>
      </tbody>
    </table>
  </form>
        <div align='center' name ='displayMsg' id='orderSpinn'/></div>
         <div id="blink"  align='center'  style=" border-radius: 15px;  color:blue; height:20px;   font-size: larger; background-color: lightblue;"><span class="blink_me">NO More Orders..</span></div>
  