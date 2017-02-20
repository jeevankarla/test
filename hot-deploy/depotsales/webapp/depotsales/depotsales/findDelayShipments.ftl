	
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
<#if screenFlag=="findDelayShipmentsAuth">
	<#assign action="findDelayShipmentsAuth">
<#else>
	<#assign action="findDelayShipments">
</#if>

<#-- <h2>LISTING OF DELIVERIES DELAYED BY MORE THAN 7 DAYS BY SUPPLIERS<h2>  -->
<div class="screenlet">
  <div class="screenlet-body">
    <div id="findPartyParameters"  >
      <form method="post" name="findDelayShipments" id="findDelayShipments" action="<@ofbizUrl>${action}</@ofbizUrl> " class="basic-form">
        <table class="basic-table" >
        <tr>
        	 <td><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Search By</b></td> 
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
  				  <td id="BranchFilterlabel"><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Branch</b></td>
	              <td id="BranchFilter">
					  <select name="branchId2" id="branchId">
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
  				  <td id="RegionFilterLabel"><b>&nbsp;&nbsp;&nbsp;Regional Office</b></td>
	              <td id="RegionFilter">
					  <select name="regionId" id="regionId">
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
  				  <td id="StateFilterLabel"><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;State</b></td>
	              <td id="StateFilter">
					  <select name="stateId" id="stateId">
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
          <tr>
          	    
				<td width="10%"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="Search" class="buttontext" onClick="javascript:callSpinner();"/> 
				<input  type="hidden" size="14pt" id="isFormSubmitted"   name="isFormSubmitted" value="Y"/>
				</td>
		  </tr>
		  
      </table>
      <div align="center" id="spinner"> </div>
   
</div>





