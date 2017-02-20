
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


<input type="hidden" name="branchId" id="branchId" value="${branchId}">
<input type="hidden" name="partyId" id="partyId" value="${partyId}">
<input type="hidden" name="passbookNumber" id="passbookNumber" value="${passbookNumber}">
<input type="hidden" name="partyClassification" id="partyClassification" value="${partyClassification}">
<input type="hidden" name="isDepot" id="isDepot" value="${isDepot}">
<input type="hidden" name="satate" id="satate" value="${satate}">
<input type="hidden" name="passGreater" id="passGreater" value="${passGreater}">
<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}">



<input type="hidden" name="noOFIndentsFlag" id="noOFIndentsFlag" >

<input type="hidden" name="viewClicked" id="viewClicked" >


<script type="text/javascript">



var branchId = $("#branchId").val();
var partyId = $("#partyId").val();
var passbookNumber = $("#passbookNumber").val();
var partyClassification = $("#partyClassification").val();

var isDepot = $("#isDepot").val();
var satate = $("#satate").val();
var district = $("#district").val();
var passGreater = $("#passGreater").val();

var effectiveDate = $("#effectiveDate").val();



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



var branchId = $("#branchId").val();
var partyId = $("#partyId").val();
var passbookNumber = $("#passbookNumber").val();


  function recursively_ajax(){
    
           var uniqueOrderId = JSON.stringify(uniqueOrderIdsList);
           
		var dataJson = {"branchId":branchId,"partyId":partyId,"passbookNumber":passbookNumber,"passGreater":passGreater,"partyClassification":partyClassification,"isDepot":isDepot,"district":district,"satate":satate,"effectiveDate":effectiveDate,"uniqueOrderId":uniqueOrderId,"low":low,"high":high};
	
	    $('div#blink').hide();
	 $('div#orderSpinn').html('<img src="/images/loadingImage.gif" height="70" width="70">');
   //  alert(JSON.stringify(dataJson));
    jQuery.ajax({
                url: 'getWeaverMasterDetails',
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
    
    var partyOverView = '<a class="buttontext" href="<@ofbizUrl>EditProcProducer?partyId='+rowData.partyId+'</@ofbizUrl>" target="_blank">'+rowData.partyId+'</a>';
    
    row.append($("<td>" +  partyOverView  +"</td>"));  
    
    row.append($("<td>" + rowData.partyName + "</td>"));
    row.append($("<td>" + rowData.partyClassification + "</td>"));
    row.append($("<td>" + rowData.branchName + "</td>"));
    row.append($("<td>" + rowData.state + "</td>"));
    row.append($("<td>" + rowData.district + "</td>"));
    row.append($("<td>" + rowData.isDepot + "</td>"));
    row.append($("<td>" + rowData.passNo + "</td>"));
    row.append($("<td>" + rowData.loomDetail + "</td>"));
    
      

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
        <div id = "secondDiv" align="center" style=" border-radius: 10px; width:1400;  height:22px;  font-size: larger; background-color: lightblue;">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Total Weavers : <label  align="center" id="totIndents"style="color: blue" ></label>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Displayed Weavers : <label  align="center" id="displayedIndent"style="color: blue" ></label> </div>

  <form name="listOrders" id="listOrders"   method="post" >
   
     <table id="coreTable" class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Weaver Id</td>
          <td>Weaver Name</td>
          <td>Party Classification</td>
          <td>Branch Name</td>
           <td>State</td>
           <td>District</td>
          <td>Depot Holder</td>
          <td>PassBook Number</td>
           <td>No Of Looms</td>
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