	
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

<script type= "text/javascript">
	
	var stateJSON=${StringUtil.wrapString(stateJSON)!'[]'};
	var supplierJSON=${StringUtil.wrapString(supplierJSON)!'[]'};
	var cutomerJSON=${StringUtil.wrapString(cutomerJSON)!'[]'};
	var supplierNameJson=${StringUtil.wrapString(partyNameObj)!'[]'};
	var customerNameJson={}
	var customerNameJson=${StringUtil.wrapString(partyNameObj2)!'[]'};
	function makeDatePicker(fromDateId ,thruDateId){
		$( "#"+fromDateId ).datepicker({
				dateFormat:'MM d, yy',
				changeMonth: true,
				numberOfMonths: 1,
				onSelect: function(selectedDate) {
				date = $(this).datepicker('getDate');
				var maxDate = new Date(date.getTime());
		        	maxDate.setDate(maxDate.getDate() + 180);
					$("#"+thruDateId).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
					//$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
				}
			});
		$( "#"+thruDateId ).datepicker({
				dateFormat:'MM d, yy',
				changeMonth: true,
				numberOfMonths: 1,
				onSelect: function( selectedDate ) {
					//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
				}
			});
		}
	
	$(document).ready(function(){
		    makeDatePicker("ShipmentHistoryfromDate","ShipmentHistoryThruDate");
			$('#ui-datepicker-div').css('clip', 'auto'); 
			$("#SupplierId").autocomplete({ source: supplierJSON }).keydown(function(e){});
			$("#cutomerId").autocomplete({ source: cutomerJSON }).keydown(function(e){});
		});

    function getbrancheCustomers(state){
       	var bId=state.value;
       	var dataMap = {};
      	$("#cutomerId").val("");
      	$("#SupplierId").val("");
      	$("#customerName").html("");
      	$("#SupplierName").html("");
		dataMap["bId"] = bId;
		jQuery.ajax({
            url: 'getBranchCutomers',
            async: false,
            type: 'POST',
            data: dataMap,
            dataType: 'json',
            success: function(result) {
            var list = result["stateJSON"];
            customerNameJson=result["partyNameObj2"];
			$("#cutomerId").autocomplete({ source: list }).keydown(function(e){});
            },
            error: function (xhr, textStatus, thrownError){
				alert("record not found :: Error code:-  "+xhr.status);
			}
        });	
		
       }	
	
	   function displayName(state,flag){
       	var id=state.value;
       	if(flag=="customer"){
       		customerName=customerNameJson[id];
       		 $("#customerName").html(customerName);
       	}else{
       		supplierName=supplierNameJson[id];
       		 $("#SupplierName").html(supplierName);
       	}      	
       }	

</script>



<h2>FIND SUPPLIER SHIPMENT HISTORY<h2>
<div class="screenlet">
  <div class="screenlet-body">
    <div id="findPartyParameters"  >
      <form method="post" name="ShipmentHistory" action="<@ofbizUrl>ShipmentHistory</@ofbizUrl> " class="basic-form">
        <table class="basic-table" >
          <tr>
	              <td>Branch <br><select name="branchId2" id="branchId" onchange="javascript:getbrancheCustomers(this);">
	              <#if branchIdName?has_content>
		 	             <option value='${branchId?if_exists}'>${branchIdName?if_exists}</option> 
 	              </#if>
			      <#list  formatList as formatList>
					<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
				 </#list> 
				  </select>  
				  <input  type="hidden" size="14pt" id="isFormSubmitted"   name="isFormSubmitted" value="Y"/> </td> 
		  </tr> 
		  <tr>
				<#--  <#if customerName?has_content>
		         	<td>Agency <br> <input  type="text" size="25pt"  id="cutomerId"  name="cutomerName"  onblur="javascript:displayName(this,'customer');" />
		            <input  type="hidden" size="14pt"   name="customer" value="${customerId?if_exists}"/></td>
				<#else> -->
					<td>Agency <br> <input  type="text" size="25pt" id="cutomerId"   name="customer" value="${customerId?if_exists}" onblur="javascript:displayName(this,'customer');" />
					<div id="customerName"><#if customerName?has_content>  ${customerName?if_exists} </#if></div>
					</td>
				<#-- </#if> --> 

				<#-- <#if SupplierIdName?has_content>
					<td>Supplier <input  type="text" size="25pt"  id="SupplierId"  name="SupplierName" value="${SupplierIdName?if_exists}" onblur="javascript:displayName(this,'supplier');"  />
					<input  type="hidden" size="14pt"    name="Supplier" value="${SupplierId?if_exists}"/></td>
				<#else>  -->
					<td>Supplier <input  type="text" size="25pt" id="SupplierId"   name="Supplier"  value="${SupplierId?if_exists}" onblur="javascript:displayName(this,'supplier');" />
					<div id="SupplierName"><#if SupplierIdName?has_content> ${SupplierIdName?if_exists} </#if> </div></td>
				<#-- </#if> -->
           </tr> 
          <tr>
          		<#if fromDateStr?has_content>
					<td >From Date <br><input  type="text" size="14pt" id="ShipmentHistoryfromDate" readonly  name="fromDate" value="${fromDateStr?if_exists}"/></td>

				<#else>
					<td >From Date <br><input  type="text" size="14pt" id="ShipmentHistoryfromDate" readonly  name="fromDate"/></td>
				</#if>
	            
	            <#if thruDateStr?has_content>
					<td >To Date<input  type="text" size="14pt" id="ShipmentHistoryThruDate" readonly  name="thruDate" value="${thruDateStr?if_exists}"/></td>
	            <#else>
					<td >To Date<input  type="text" size="14pt" id="ShipmentHistoryThruDate" readonly  name="thruDate"/></td>
				</#if>
			    
          </tr>
          <tr>
				<td width="10%"><input type="submit" value="Search" class="buttontext"/>
		  </tr>
      </table>
   
</div>





