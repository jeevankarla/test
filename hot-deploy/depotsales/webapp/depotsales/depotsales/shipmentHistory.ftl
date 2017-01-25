	
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
			$("#cutomerId").autocomplete({ source: cutomerJSON }).keydown(function(e){});3
			var currentLocation=window.location;
			if(currentLocation=='http://erp.nhdcltd.co.in/ViewShipmentHistory'){
				$("#ShipmentHistory").submit();
			}
		});

    function getbrancheCustomers(state,filterType){
       	var bId=state.value;
       	var dataMap = {};
      	$("#cutomerId").val("");
      	$("#SupplierId").val("");
      	$("#customerName").html("");
      	$("#SupplierName").html("");
		dataMap["bId"] = bId;
		dataMap["filterType"] = filterType;
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
	   function checkFields()
	   {	
	        var branch=$("#branchId").val();
	        if(branch==""){
	        	$("#dispComField").show();
	        	$("#dispComField").delay(1000).fadeOut('slow'); 
	        }
	   } 
	   function displayName(state,flag){
       	var id=state.value;
       	
       	if(flag=="customer"){
       	    if(id==""){
       		 	$("#customerName").html("");
       	    }else{
       	    	customerName=customerNameJson[id];
       		    $("#customerName").html(customerName);
       	    }
  
       	}else{
       		if(id==""){
       		 	$("#SupplierName").html("");
       	    }else{
       	    	supplierName=supplierNameJson[id];
       		    $("#SupplierName").html(supplierName);
       	    }
       	}      	
       }	
		$(document).ready(function(){
		var searchType='${searchType?if_exists}';
		$('#spinner').hide(); 
		$('#BranchFilter').hide();
		$('#BranchFilterlabel').hide();
		$('#RegionFilterLabel').hide();
		$('#RegionFilter').hide();
		$('#StateFilterLabel').hide();
		$('#StateFilter').hide();
		if(searchType=="BY_BO"){
			$('#BranchFilter').show();
			$('#BranchFilterlabel').show();
		}else if(searchType=="BY_RO"){
			$('#RegionFilter').show();
			$('#RegionFilterLabel').show();
		}else{
			$('#StateFilter').show();
			$('#StateFilterLabel').show();
		}
	});
    function showSearchFilter(obj){
       	var searchType=obj.value;
       	if(searchType=="BY_STATE"){
       		$('#BranchFilter').hide();
			$('#BranchFilterlabel').hide();
			$('#RegionFilterLabel').hide();
			$('#RegionFilter').hide();
			$('#StateFilterLabel').show();
			$('#StateFilter').show();
       	}else if (searchType=="BY_RO"){
        	$('#BranchFilter').hide();
			$('#BranchFilterlabel').hide();
			$('#RegionFilterLabel').show();
			$('#RegionFilter').show();
			$('#StateFilterLabel').hide();
			$('#StateFilter').hide();      		
       	}else{
       		$('#BranchFilter').show();
			$('#BranchFilterlabel').show();
			$('#RegionFilterLabel').hide();
			$('#RegionFilter').hide();
			$('#StateFilterLabel').hide();
			$('#StateFilter').hide();       		
       	}
    }	
    function callSpinner()
	{
		var branch=$("#branchId").val();
        if(branch==""){
        	$("#dispComField").show();
        	$("#dispComField").delay(50000).fadeOut('slow'); 
        }
		$('#spinner').show();
		$('div#spinner').html('<img src="/images/ajax-loader64.gif">');
	}	
</script>
<#assign action="">
<#if screenFlag=="ShipmentHistoryAuth">
	<#assign action="ShipmentHistoryAuth">
<#else>
	<#assign action="ShipmentHistory">
</#if>
<h2>SUPPLIERS TO USER AGENCY OPERATED DEPOTS<h2>
<div class="screenlet">
  <div class="screenlet-body">
    <div id="findPartyParameters"  >
      <form method="post" name="ShipmentHistory" id="ShipmentHistory" action="<@ofbizUrl>${action}</@ofbizUrl> " class="basic-form">
        <table class="basic-table" >
         <tr>
        	 <td>Search By</td> 
        	 <td>
      		 	 <select name="searchType" id="searchType" onchange="javascript:showSearchFilter(this);">
      		 	 	<#if period?has_content>
      		 	    	<option value='${searchType?if_exists}'>${searchTypeName?if_exists}</option>
      		 	    </#if>
					<option value='BY_STATE'>By State</option>
					<option value='BY_BO'>By Branch Office</option>
					<option value='BY_RO'>By Regional Office</option>
			  </select> 
      		 </td>
        </tr>
           <tr>    
  			  <div>
  				  <td id="BranchFilterlabel">Branch</td>
	              <td id="BranchFilter">
					  <select name="branchId2" id="branchId" onchange="javascript:getbrancheCustomers(this,'By_Branch');">
		              <#if branchIdName?has_content>
			 	             <option value='${branchId?if_exists}'>${branchIdName?if_exists}</option> 
	 	              </#if>
					  <#if !branchIdName?has_content>
							 <option value=''>Select Branch</option>
					  </#if>
				      <#list  formatBList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
					 </select>
				      <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
		  		   </td>
  		       </div>
  		       <div >
  				  <td id="RegionFilterLabel">Regional Office</td>
	              <td id="RegionFilter">
					  <select name="regionId" id="regionId" onchange="javascript:getbrancheCustomers(this,'By_Ro');">
		              <#if regionIdName?has_content>
			 	             <option value='${regionId?if_exists}'>${regionIdName?if_exists}</option> 
	 	              </#if>
					  <#if !regionIdName?has_content>
							 <option value=''>Select Region</option>
					  </#if>
				      <#list  formatRList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
					 </select>
				      <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
		  		   </td>
  		       </div>
  		       <div>
  				  <td id="StateFilterLabel">State</td>
	              <td id="StateFilter">
					  <select name="stateId" id="stateId" onchange="javascript:getbrancheCustomers(this,'By_State');">
		              <#if stateIdName?has_content>
			 	             <option value='${stateId?if_exists}'>${stateIdName?if_exists}</option> 
	 	              </#if>
					  <#if !stateIdName?has_content>
							 <option value=''>Select State</option>
					  </#if>
				       <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					  </#list> 
					 </select>
				      <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
		  		   </td>
  		       </div>
		  </tr> 
		  				  
			<tr>
				   <td>Agency/Depot</td> 
				   <td><input  type="text" size="25pt" placeholder="Select Agency" id="cutomerId"   name="customer" value="${customerId?if_exists}" onkeypress="javascript:checkFields();" onblur="javascript:displayName(this,'customer');" />
					   <div id="customerName"><#if customerName?has_content>${customerName?if_exists} </#if></div>
				   </td>
				   
				<#--   <td>Supplier <input  type="text" size="25pt" id="SupplierId"   name="Supplier"  value="${SupplierId?if_exists}" onblur="javascript:displayName(this,'supplier');" />
						<div id="SupplierName"><#if SupplierIdName?has_content> ${SupplierIdName?if_exists} </#if> </div>
				   </td>  -->
		  </tr> 
		  <#--<tr>
			  <#if customerName?has_content>
		         	<td>Agency <br> <input  type="text" size="25pt"  id="cutomerId"  name="cutomerName"  onblur="javascript:displayName(this,'customer');" />
		            <input  type="hidden" size="14pt"   name="customer" value="${customerId?if_exists}"/></td>
				<#else> -->
					
				<#-- </#if> --> 

				<#-- <#if SupplierIdName?has_content>
					<td>Supplier <input  type="text" size="25pt"  id="SupplierId"  name="SupplierName" value="${SupplierIdName?if_exists}" onblur="javascript:displayName(this,'supplier');"  />
					<input  type="hidden" size="14pt"    name="Supplier" value="${SupplierId?if_exists}"/></td>
				<#else>  -->
					
				<#-- </#if> 
           </tr> -->
         <#--  <tr>
          		<#if fromDateStr?has_content>
	                 <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;From Date</td>
					<td > <input  type="text" size="14pt" id="ShipmentHistoryfromDate" readonly  name="fromDate" value="${fromDateStr?if_exists}"/></td>

				<#else>
	                 <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          			&nbsp;&nbsp;&nbsp;From Date</td>
					<td > <input  type="text" size="14pt" id="ShipmentHistoryfromDate" readonly  name="fromDate"/></td>
				</#if>
	            
	            <#if thruDateStr?has_content>
					<td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;To Date&nbsp;<input type="text" size="14pt" id="ShipmentHistoryThruDate" readonly  name="thruDate" value="${thruDateStr?if_exists}"/></td>
	            <#else>
					<td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;To Date&nbsp;<input  type="text" size="14pt" id="ShipmentHistoryThruDate" readonly  name="thruDate"/></td>
				</#if>
			    
          </tr>  -->
      <tr>
          	 	
      		 <td><b>Period Selection</b></td>
      		 <td>
      		 	 <select name="period" id="period">
      		 	    <#if period?has_content>
      		 	    	<option value='${period?if_exists}'>${periodName?if_exists}</option>
      		 	    </#if>
					<option value='One_Month'>Last One Month</option>
					<option value='Two_Month'>Last Two Months</option>
					<option value='Three_Month'>Last Three  Months</option>
					<option value='Six_Month'>Last  Six Months</option>
			  </select> 
      		 </td>
		  </tr>
		  <tr><td>&nbsp; </td><td>&nbsp; </td><td> &nbsp;</td></tr>
      <tr>
          	    
				<td width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="Search" class="buttontext" onClick="javascript:callSpinner();"/> 
				<input  type="hidden" size="14pt" id="isFormSubmitted"   name="isFormSubmitted" value="Y"/>
				</td>
		  </tr>
      </table>
   
</div>





