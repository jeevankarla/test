
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
.ui-datepicker-current{
	display:none;
}

</style>


<input type="hidden" name="branchId" id="branchId" value="${branchId}">
<input type="hidden" name="partyId1" id="partyId1" value="${partyId}">
<input type="hidden" name="passbookNumber" id="passbookNumber" value="${passbookNumber}">
<input type="hidden" name="partyClassification" id="partyClassification" value="${partyClassification}">
<input type="hidden" name="isDepot" id="isDepot" value="${isDepot}">
<input type="hidden" name="stateWise1" id="stateWise1" value="${stateWise}">
<input type="hidden" name="passGreater" id="passGreater" value="${passGreater}">
<input type="hidden" name="effectiveDate1" id="effectiveDate1" value="${effectiveDate}">



<input type="hidden" name="noOFIndentsFlag" id="noOFIndentsFlag" >

<input type="hidden" name="viewClicked" id="viewClicked" >


<script type="text/javascript">



var branchId = $("#branchId").val();
var partyId = $("#partyId1").val();
var passbookNumber = $("#passbookNumber").val();
var partyClassification = $("#partyClassification").val();

var isDepot = $("#isDepot").val();
var stateWise = $("#stateWise1").val();
var district = $("#district").val();
var passGreater = $("#passGreater").val();

var effectiveDate = $("#effectiveDate1").val();



var displayedIndent = 0;
var uniqueOrderIdsList = [];
var orderData;
var domOrderIds = "";
var low = 0, high = 50;
$(document).ready(function() {
   $(window).scroll(function() {
         var came = "";
    	 if ($(window).scrollTop() >= ($(document).height() - $(window).height())*0.99){
           low = high;
           high = high + 50;
           if(came != "YES")
           recursively_ajax();    
           came = "YES";
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




if(branchId.length == 0)
branchId = "INT10";


  function recursively_ajax(){
    
           var uniqueOrderId = JSON.stringify(uniqueOrderIdsList);
           
		var dataJson = {"branchId":branchId,"partyId":partyId,"passbookNumber":passbookNumber,"passGreater":passGreater,"partyClassification":partyClassification,"isDepot":isDepot,"district":district,"stateWise":stateWise,"effectiveDate":effectiveDate,"uniqueOrderId":uniqueOrderId,"low":low,"high":high};
	
	 $('div#orderSpinn').html('<img src="/images/loadingImage.gif" height="70" width="70">');
   //  alert(JSON.stringify(dataJson));
    jQuery.ajax({
                url: 'getWeaverQuotaDashboard',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderData = result["weaverDetailsList"];
                        if(orderData.length != 0){
                        $('div#orderSpinn').html("");
                         displayedIndent = displayedIndent+orderData.length;
                         $("#displayedIndent").html(displayedIndent);
                         
                         if(orderData[0].totalIndents >=10)
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
          uniqueOrderIdsList.push(data[i].partyId);
    }
    
}



function drawRow(rowData) {
    var row = $("<tr />")
    $("#coreTable").append(row); 
    $("#coreTable tr:even").css("background-color", "#F4F4F8");
    
    
    //var partyOverView = '<a class="buttontext" href="<@ofbizUrl>EditProcProducer?partyId='+rowData.partyId+'</@ofbizUrl>" target="_blank">'+rowData.partyId+'</a>';
    
    row.append($("<td>" +  rowData.partyId  +"</td>"));  
    
    row.append($("<td>" + rowData.partyName + "</td>"));
    
     row.append($("<td>" + rowData.passbookNo + "</td>"));
    
     row.append($("<td>" + rowData.branchName + "</td>"));
     
     
     var loomDeatilJson = rowData.loomDetail;
     
     var quotaRows = "";
      for(var i=0;i<loomDeatilJson.length;i++){
          quotaRows = quotaRows+ '<tr>'+loomDeatilJson[i].description+': '+loomDeatilJson[i].quantity+'</tr>'+'</br>';
     }
     
     row.append($("<td>"+"<table id='quotaTable'>"+quotaRows+"</table>"+ "</td>"));
     
      var partyLoomArrayJSON = rowData.partyLoomArrayJSON;
      
      var eligible = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          eligible = eligible+ '<tr>'+partyLoomArrayJSON[i].loomQuota+'</tr>'+'</br>';
     }
     
     row.append($("<td>"+"<table id='eligible'>"+eligible+"</table>"+ "</td>"));
     
      var usedQuota = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          usedQuota = usedQuota+ '<tr>'+partyLoomArrayJSON[i].usedQuota+'</tr>'+'</br>';
     }
     
     row.append($("<td>"+"<table id='usedQuota'>"+usedQuota+"</table>"+ "</td>"));
     
     var balnceQuota = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          balnceQuota = balnceQuota+ '<tr>'+partyLoomArrayJSON[i].availableQuota+'</tr>'+'</br>';
     }
     
     row.append($("<td>"+"<table id='balnceQuota'>"+balnceQuota+"</table>"+ "</td>"));
     
	 
	
	<#-- 
	 var quotaQuantity = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          quotaQuantity = quotaQuantity+ '<tr>'+partyLoomArrayJSON[i].quotaQuantity+'</tr>'+'</br>';
     }
     
     row.append($("<td align=right>"+"<table id='eligible'>"+quotaQuantity+"</table>"+ "</td>"));
     
       var invoiceValue = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          invoiceValue = invoiceValue+ '<tr>'+partyLoomArrayJSON[i].invoiceValue+'</tr>'+'</br>';
     }
     
     row.append($("<td align=right>"+"<table id='usedQuota'>"+invoiceValue+"</table>"+ "</td>"));
          
     
      var tenPerValue = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          tenPerValue = tenPerValue+ '<tr>'+partyLoomArrayJSON[i].tenPerValue+'</tr>'+'</br>';
     }
     
     row.append($("<td align=right>"+"<table id='balnceQuota'>"+tenPerValue+"</table>"+ "</td>"));
          
     
      var invoiceGrossValue = "";
      for(var i=0;i<partyLoomArrayJSON.length;i++){
          invoiceGrossValue = invoiceGrossValue+ '<tr>'+partyLoomArrayJSON[i].invoiceGrossValue+'</tr>'+'</br>';
     }
     
     row.append($("<td align=right>"+"<table id='invoiceGrossValue'>"+invoiceGrossValue+"</table>"+ "</td>"));
	 
	 -->
	 

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
 
 
 <div id = "firstDiv" style="border-width: 2px; padding-top: 20px;   border-radius: 10px; border-style: solid; border-color: grey; ">
     <font color="blue">Search In Displaying Weavers:</font><input type="text"  style="border-radius: 5px;" class="light-table-filter" data-table="basic-table" placeholder="Filter by any">
        <div id = "secondDiv" align="center" style=" border-radius: 10px; width:1400;  height:22px;  font-size: larger; background-color: lightblue;">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total Weavers : <label  align="center" id="totIndents"style="color: blue" ></label>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; <#--Displayed Weavers : <label  align="center" id="displayedIndent"style="color: blue" ></label>--> </div>

  <form name="listOrders" id="listOrders"   method="post" >
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Customer Id</td>
          <td>Customer Name</td>
          <td>Passbook Number</td>
          <td>Branch Name</td>
           <td>No Of Looms</td>
          <td>Eligible Quota</td>
          <td>Used Quota</td>
          <td>Balance Quota</td>
          
          <#--<td>Quota Quantity</td>
          <td>Invoice Gross Amount</td>
          <td>Subsidy Amount</td>
          <td>Invoice Amount</td>-->
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign alt_row = !alt_row>
      </tbody>
    </table>
  </form>
        <div align='center' name ='displayMsg' id='orderSpinn'/></div>
         <div id="blink"  align='center'  style=" border-radius: 15px;  color:blue; height:20px;   font-size: larger; background-color: lightblue;"><span class="blink_me">No More Weavers..</span></div>
        <a href="#" class="scrollup">Top</a>