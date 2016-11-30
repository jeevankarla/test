	
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

	function makeDatePicker(fromDateId ,thruDateId){
		$( "#"+fromDateId ).datepicker({
				dateFormat:'MM d, yy',
				changeMonth: true,
				numberOfMonths: 1,
				onSelect: function(selectedDate) {
				date = $(this).datepicker('getDate');
				var maxDate = new Date(date.getTime());
		        	maxDate.setDate(maxDate.getDate() + 31);
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

    function getbranchesByState(state ,branchId){
       	var stateId=state.value;
       	var optionList = '';
			var list= stateJSON[stateId];
			if (list && list.length>0) {	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	     
	                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
	      		}//end of main list for loop
	      	}
	      	jQuery("[name='"+branchId+"']").html(optionList);
       }	
	
	
</script>



<h2>FIND SUPPLIER SHIPMENT HISTORY<h2>
<div class="screenlet">
  <div class="screenlet-body">
    <div id="findPartyParameters"  >
      <form method="post" name="ShipmentHistory" action="<@ofbizUrl>ShipmentHistory</@ofbizUrl> " class="basic-form">
        <table class="basic-table" >
          <tr>
              <#if stateName?has_content>
			   <td >State 
				<select name="state" id="state"  size="8pt" onchange="javascript:getbranchesByState(this,'branchId2');" >
				 	<option value='${state?if_exists}'>${stateName?if_exists}</option>
				  </select> 
				 <input  type="hidden" size="14pt" id="isFormSubmitted"   name="isFormSubmitted" value="Y"/>
			   </td>
			  <#else>
				 <td >State
				 <select name="state" id="state" onchange="javascript:getbranchesByState(this,'branchId2');">
				 	
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select> 
				  <input  type="hidden" size="14pt" id="isFormSubmitted"   name="isFormSubmitted" value="Y"/>
			   </td>
			  </#if>
			  
			  <#if branchIdName?has_content>
	              <td>Branch <br><select name='branchId2'> <option value='${branchId}'>${branchIdName?if_exists}</option></select>  </td> 
				<#else>
				<td>Branch <br> <select name='branchId2'> </select>  </td>
				</#if>

				

            	
		  </tr>
		  <tr>
				 <#if cutomerName?has_content>
		         	<td>Agency <br> <input  type="text" size="14pt"    name="cutomerName" value="${cutomerName?if_exists}"/>
		            <input  type="hidden" size="14pt" id="cutomerId"   name="cutomer" value="${cutomerId?if_exists}"/></td>
				<#else>
					<td>Agency <br> <input  type="text" size="14pt" id="cutomerId"   name="customer"/></td>
				</#if>

				 <#if SupplierIdName?has_content>
					<td>Supplier <input  type="text" size="14pt"    name="SupplierName" value="${SupplierIdName?if_exists}"/>
					<input  type="hidden" size="14pt" id="SupplierId"   name="Supplier" value="${SupplierId?if_exists}"/></td>
				<#else>
					<td>Supplier <input  type="text" size="14pt" id="SupplierId"   name="Supplier"/></td>
				</#if>
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





