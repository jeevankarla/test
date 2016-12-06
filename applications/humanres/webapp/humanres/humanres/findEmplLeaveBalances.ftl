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
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>
<script language="JavaScript" type="text/javascript">

$(function() {
    $('input[name=partyId]').select(function (event){
    	 var employeeId = $('input[name=partyId]').val();
    	 //alert("employeeId============"+employeeId);
    	 var flag="emplLeaveBlnc";
    var data = "employeeId="+employeeId+"&flag="+flag;
   // $('#partyId').html('');
    if(employeeId ==""){
          return false;
     }
     
    $.ajax({
             type: "POST",
             url: "getEmployeeLeaveUpdatedDate",
             data: data,
             dataType: 'json',
             success: function(result) {
            	 var lastUpdatedDate = result["lastUpdatedDate"];
            	 
            	 if(lastUpdatedDate == undefined){
            	   return;
            	 }
            	 $("#dateDisp").html('<b>last updated on '+result["lastUpdatedDate"]+'</b>');
            	
            	 },
            error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
     });
     });    
});


</script>
     <div class="screenlet-body">
     	<form name="EmplLeaveBalanceStatus" id = "EmplLeaveBalanceStatus" method="post" action="EmplLeaveBalanceStatus">
     		 <table class="basic-table" cellspacing="0">
              <tr>
              	<td class="label">${uiLabelMap.EmployeeId} :</td>
                <td class= "h2">
                	<#if security.hasEntityPermission("HUMANRES", "_ADMIN", session)>
                      		<@htmlTemplate.lookupField value="${parameters.partyId?if_exists}" formName="EmplLeaveBalanceStatus" name="partyId" id="partyId" fieldFormName="LookupEmployeeName"/>
                    		<span class="tooltip"></span>
                    		<span class="dtooltip"> <label id="dateDisp" style="color:green; font-size:11pt; font-stlye:bold">  </label></span>
                  	<#else>
                  		<#assign partyName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userLogin.partyId, false)?if_exists/>
                		<input type="hidden" name="partyId" id="partyId" value="${userLogin.partyId?if_exists}" readonly onchange="updatedAsOnDate();"/>
                		${partyName}[${userLogin.partyId?if_exists}]
                	</#if>
                </td>
              </tr>
              <tr>
              	<td class="label">Custom Time Period Id :</td>
              	<td>
	               <select name="customTimePeriodId" class='h4' >
			           <option value=''></option>
						<#list customTimePeriodList as customTimePeriod>
					    	<option value='${customTimePeriod.customTimePeriodId}'>${customTimePeriod.fromDate}-${customTimePeriod.thruDate}</option>
						</#list>      
					</select> 
				</td>
			   </tr>
			      <tr>
                <td class="label">Leave Type :</td>
                <td>
		               <select name="leaveTypeId" class='h4'>
		                   <option value=''></option>
		  				   <option  value="EL">EL</option>
		  					<option  value="CL">CL</option>
		  					<option  value="RH">RH</option>
		  					<option  value="ML">ML</option>
		  					<option  value="TL">TL</option>
						</select>
					<span id="leaveBalance" name="leaveBalance" class="tooltip" wrap="wrap"></span>
                </td>
              </tr>
			   <tr>
                <td>&nbsp;</td>
                <td>
                	<input type="submit" name="find" id="find" value="Find" style="buttontext"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;
                </td>
              </tr>
             </table>
     	</form>
	</div>