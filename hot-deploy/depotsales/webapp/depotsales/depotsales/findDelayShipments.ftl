	
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
		$('#spinner').hide();  
	});

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
<h2>LISTING OF DELIVERIES DELAYED BY MORE THAN 7 DAYS BY SUPPLIERS<h2>
<div class="screenlet">
  <div class="screenlet-body">
    <div id="findPartyParameters"  >
      <form method="post" name="findDelayShipments" id="findDelayShipments" action="<@ofbizUrl>findDelayShipments</@ofbizUrl> " class="basic-form">
        <table class="basic-table" >
          <tr>     
          		  <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Branch</td>
	              <td>
					 <select name="branchId2" id="branchId">
	              <#if branchIdName?has_content>
		 	             <option value='${branchId?if_exists}'>${branchIdName?if_exists}</option> 
 	              </#if>
				  <#if !branchIdName?has_content>
						 <option value=''>Select Branch</option>
				  </#if>
			      <#list  formatList as formatList>
					<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
				 </#list> 
				  </select>  
				  <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
				  <input  type="hidden" size="14pt" id="isFormSubmitted"   name="isFormSubmitted" value="Y"/>
				  </td>
		  </tr> 
		 
          <tr>
          	 	
      		 <td><b>Period Selection</b></td>
      		 <td>
      		 	 <select name="period" id="period">
      		 	    <#if period?has_content>
      		 	    	<option value='${period}'>${periodName}</option>
      		 	    </#if>
					<option value='One_Month'>Last One Month</option>
					<option value='Two_Month'>Last Two Months</option>
					<option value='Three_Month'>Last Three  Months</option>
					<option value='Six_Month'>Last  Six Months</option>
			  </select> 
      		 </td>
		  </tr>
          <tr>
          	    
				<td width="10%"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="Search" class="buttontext" onClick="javascript:callSpinner();"/> </td>
		  </tr>
		  
      </table>
      <div align="center" id="spinner"> </div>
   
</div>





