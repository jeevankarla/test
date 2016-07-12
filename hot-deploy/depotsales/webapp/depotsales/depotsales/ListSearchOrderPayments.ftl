
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

<style>
.scrollup {
    width: 35px;
    height: 35px;
    position: fixed;
    bottom: 50px;
    right: 70px;
    display: none;
    text-indent: 300px;
    background: url('/images/arrow66.png') no-repeat;
    background-color: orange;
}


</style>


<input type="hidden" name="paramOrderId" id="paramOrderId" value="${paramOrderId}">
<input type="hidden" name="paramFacilityId" id="paramFacilityId" value="${paramFacilityId}">
<input type="hidden" name="paramEstimatedDeliveryDate" id="paramEstimatedDeliveryDate" value="${paramEstimatedDeliveryDate}">
<input type="hidden" name="paramEstimatedDeliveryThruDate" id="paramEstimatedDeliveryThruDate" value="${paramEstimatedDeliveryThruDate}">
<input type="hidden" name="paramStatusId" id="paramStatusId" value="${paramStatusId}">
<input type="hidden" name="paramBranch" id="paramBranch" value="${paramBranch}">
<input type="hidden" name="indentDateSort" id="indentDateSort" value="${indentDateSort}">
<input type="hidden" name="noOFIndentsFlag" id="noOFIndentsFlag" >


<script type="text/javascript">



var orderId = $("#paramOrderId").val();
var paramFacilityId = $("#paramFacilityId").val();
var paramEstimatedDeliveryDate = $("#paramEstimatedDeliveryDate").val();
var paramEstimatedDeliveryThruDate = $("#paramEstimatedDeliveryThruDate").val();
var paramStatusId = $("#paramStatusId").val();
var paramBranch = $("#paramBranch").val();
var indentDateSort = $("#indentDateSort").val();


var displayedIndent = 0;
var uniqueOrderIdsList = [];
var orderData;
var domOrderIds = "";
var low = 0, high = 20;
$(document).ready(function() {
   $(window).scroll(function() {
    	if($(window).scrollTop() == $(document).height() - $(window).height()) {
          
           low = high;
           high = high + 30;
         
           recursively_ajax();          
                    
    	}
});


//========================page Top======================

    $('.scrollup').click(function () {
        $("html, body").animate({
            scrollTop: 0
        }, 600);
        return false;
    });


document.onkeydown = function(e) {
    switch (e.keyCode) {
        case 37:
            //alert('left');
            break;
        case 38:{
            $('.scrollup').fadeIn();
             var scroll = $(window).scrollTop();
             if(scroll < 50)
             $('.scrollup').fadeOut();
            break;
            }
        case 39:
           // alert('right');
            break;
        case 40:{
              $('.scrollup').fadeOut();
            break;
              }
          }
};

$(function(){
    var _top = $(window).scrollTop();
    var _direction;
    $(window).scroll(function(){
        var _cur_top = $(window).scrollTop();
        if(_top < _cur_top)
        {
            _direction = 'down';
             $('.scrollup').fadeOut();
        }
        else
        {
            _direction = 'up';
            var scroll = $(window).scrollTop();
            if(scroll < 50)
             $('.scrollup').fadeOut();
             else
             $('.scrollup').fadeIn();
            
        }
        _top = _cur_top;
        //console.log(_direction);
    });
});
//================================================





	recursively_ajax();
});

  function recursively_ajax(){
    
           var uniqueOrderId = JSON.stringify(uniqueOrderIdsList);
		var dataJson = {"orderId":orderId,"partyId":paramFacilityId,"estimatedDeliveryDate":paramEstimatedDeliveryDate,"estimatedDeliveryThruDate":paramEstimatedDeliveryThruDate,"statusId":paramStatusId,"partyIdFrom":paramBranch,"indentDateSort":indentDateSort,"uniqueOrderId":uniqueOrderId,"low":low,"high":high};
	
	 $('div#orderSpinn').html('<img src="/images/loadingImage.gif" height="70" width="70">');
     
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
                        if(orderData.length != 0){
                        $('div#orderSpinn').html("");
                         displayedIndent = displayedIndent+orderData.length;
                         $("#displayedIndent").html(displayedIndent);
                         
                         if(displayedIndent <=20)
                          $("#totIndents").html("<h10>"+orderData[0].totalIndents+"</h10>");
                         
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
    row.append($("<td>" + rowData.partyId + "</td>"));
    row.append($("<td>" + rowData.partyName + "</td>"));
    if(rowData.orderNo != undefined && rowData.orderNo != "NA"){
        row.append($("<td>" + rowData.orderNo + "</td>"));
    }
    else{
        row.append($("<td>" + rowData.orderId + "</td>"));
    }
    var indDateSplit = (rowData.orderDate).split("-");
    
    var indentDate = indDateSplit[2] + "/" + indDateSplit[1] + "/" + indDateSplit[0];
    
    row.append($("<td align=right>" + indentDate + "</td>"));
    row.append($("<td align=right>" + rowData.orderTotal + "</td>"));

    //For Indent View
    
    var orderParam = '\'' + rowData.orderId + '\'';
    var orderCustomMethod = "javascript:fetchOrderInformation("+ orderParam + ")";
    var viewButton ='<input type=button name="viewOrder" id=viewOrder value="view Order" onclick="'+orderCustomMethod+'">';
    
    row.append($("<td align=center>" +  viewButton  +"</td>"));
   
   //For indent Payment
   
   if(rowData.orderTotal != rowData.paidAmt)
   {
    var partyName = "'" + rowData.partyName + "'";
    var methodParam = '\'' + rowData.orderId + '\',\'' + rowData.partyId+'\','+partyName+','+rowData.orderTotal+','+rowData.balance;
    var customMethod = "javascript:showPaymentEntryForIndentPayment("+ methodParam + ")";
    var inputbox ='<input type=button name="Payment" id=Payment value="Indent Payment" onclick="'+customMethod+'">';
    row.append($("<td align=center>" +  inputbox  +"</td>"));
    }else{
    row.append($("<td></td>"));
    }
    
    if(rowData.orderTotal != rowData.balance)
    {
     row.append($("<td align=right>Payment Realized</td>"));
    }else if(rowData.balance == 0){
     row.append($("<td align=right>Payment Received</td>"));
    }else{
     row.append($("<td align=right>Payment Not Received</td>"));
    }
    row.append($("<td align=right>" + rowData.paidAmt + "</td>"));
    

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

  
</script>



</script>
<#include "viewOrderDetailsDepot.ftl"/>
 
 
 <div id = "firstDiv" style="border-width: 2px; padding-top: 20px;   border-radius: 10px; border-style: solid; border-color: grey; ">
     <font color="blue">Search In Displaying Indents:</font><input type="text"  style="border-radius: 5px;" class="light-table-filter" data-table="basic-table" placeholder="Filter by any">
        <div id = "secondDiv" align="center" style=" border-radius: 10px; width:1400;  height:22px;  font-size: larger; background-color: lightblue;">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total Indents : <label  align="center" id="totIndents"style="color: blue" ></label>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Displayed Indents : <label  align="center" id="displayedIndent"style="color: blue" ></label> </div>

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
        <div align='center' name ='displayMsg' id='orderSpinn'/></div>
         <div id="blink"  align='center'  style=" border-radius: 15px;  color:blue; height:20px;   font-size: larger; background-color: lightblue;"><span class="blink_me">NO More Orders..</span></div>
        <a href="#" class="scrollup">Top</a>