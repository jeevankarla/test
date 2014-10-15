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
       $('input[name=submit]').click (function (){
    	  	var leaveTypeId = $('select[name=leaveTypeId]').val();
			if(leaveTypeId =="CH" || leaveTypeId =="CHGH" || leaveTypeId =="CHSS"){
				var chDate = $('select[name=chDate]').val();
				if(!chDate){
					alert("Please select GH/SS date");
				}
			 }
    	});
  });

  jQuery(document).ready(function () {
    
	jQuery("#fromDate").datepicker({dateFormat:'dd-mm-yy',
									onSelect: function( selectedDate ) {
									/* var leaveTypeId = $('select[name=leaveTypeId]').val();
									if(leaveTypeId =="CH" || leaveTypeId =="CHGH" || leaveTypeId =="CHSS"){
	     								return viewGHandSS();
     								 } */
									$( "#thruDate" ).datepicker( "option", {minDate: selectedDate});
								     }
						           }).datepicker("option", {});
	jQuery("#thruDate").datepicker({dateFormat:'dd-mm-yy' , beforeShow :displayPunchDetails}).datepicker("option", {} );
	
	  $('input[name=partyId]').focusout(function(){
	     displayLeaveBalance();
	  
	  });
	  
	  $('#ghssDropDown').hide();
	  
	  $("#chDate").multiselect({
   			minWidth : 150,
   			height: 80,
   			selectedList: 4,
   			show: ["bounce", 100],
   			position: {
      			my: 'left bottom',
      			at: 'left top'
      		},
      		
   		});
   	jQuery("#approverPartyId").focusout(function(){
   		var partyId=$('input[name=partyId]').val();
   		var apPartyId=$('input[name=approverPartyId]').val();
   		if(partyId==apPartyId){
   		alert("Employee Id And Approver Id Must Be Different");
   		location.reload(true);
   		}
   	});	
   		
});

function displayPunchDetails(){
      var leaveTypeId = $('select[name=leaveTypeId]').val();
      if(leaveTypeId !="CL"){
         return false;
        
      }
      $('[name="fromDate"]').datepicker( "option", "dateFormat", "yy-mm-dd");
       var punchDate = jQuery("#fromDate").val();
       $('[name="fromDate"]').datepicker( "option", "dateFormat", "dd-mm-yy");
       var employeeId = $('input[name=partyId]').val();
       if(employeeId ==""){
          return;
       }
      var data = "partyId="+employeeId+"&punchDate="+punchDate;
    $.ajax({
             type: "POST",
             url: "emplDailyPunchJson",
             data: data,
             dataType : 'json',
             success: function(result) {
            	 var punchDataList = result["punchDataList"];
            	 if(punchDataList !=""){
            	    $('#punchDetails').html('<span style="color:green; font-size:11pt; font-stlye:bold"> IN:'+punchDataList[0].inTime+' , OUT:'+ punchDataList[0].outTime +'   </span>');
            	 }else{
            	     $('#punchDetails').html('');
            	 }
            	 
            	},
            error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 } 	 
            	 
     }); 
}

function viewGHandSS(){
      var leaveTypeId = $('select[name=leaveTypeId]').val();
       var employeeId = $('input[name=partyId]').val();
       $('[name="fromDate"]').datepicker( "option", "dateFormat", "yy-mm-dd");
       var fromDate = jQuery("#fromDate").val();
       $('[name="fromDate"]').datepicker( "option", "dateFormat", "dd-mm-yy");
       var data = "partyId="+employeeId;
      if(leaveTypeId !="CH" && leaveTypeId !="CHGH" && leaveTypeId !="CHSS"){
         return false;
      }
      if(leaveTypeId =="CHGH"){
         data = data+"&isGH=Y";
      }
      if(leaveTypeId =="CHSS"){
      	data = data+"&isSS=Y";
      }
      if(leaveTypeId =="CH"){
      	data = data+"&isWeeklyOff=Y";
      }
      data = data+"&thruDate="+fromDate;
      
    $.ajax({
             type: "POST",
             url: "getGeneralHoliDayOrSSWorkedDaysJson",
             data: data,
             dataType: 'json',
             success: function(result) {
            	 var workedHolidaysList = result["workedHolidaysList"];
            	 if(workedHolidaysList != undefined){
            	    var innerHtmlStr ="";
            	    //alert(workedHolidaysList.length);
            	    var chDateDropDown ="";
            	   for(var i=0;i<workedHolidaysList.length;++i){
            	        tmepWork = workedHolidaysList[i];
                        innerHtmlStr += "Date:"+tmepWork.date+",PunchDetails: IN-"+ tmepWork.punchDetails.inTime +",OUT-"+ tmepWork.punchDetails.outTime +",Total-"+tmepWork.punchDetails.totalTime +"<br/>";
            	       chDateDropDown += "<option value="+ tmepWork.date+">"+tmepWork.date+"</option>";
            	    }
            	     jQuery('#ghssDropDown').show();
            	     jQuery('#chDate').append(chDateDropDown);
            	     $("#chDate").multiselect("refresh");
            	    
            	    $('#leaveBalance').html('<span style="color:green; font-size:11pt; font-stlye:bold">'+innerHtmlStr+'</span>');
            	 }else{
            	     $('#leaveBalance').html('');
            	 }
            	 
            	},
            error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 } 	 
            	 
     }); 
}




 function displayLeaveBalance(){
    var leaveTypeId = $('select[name=leaveTypeId]').val();
    var employeeId = $('input[name=partyId]').val();
    var data = "employeeId="+employeeId+"&leaveTypeId="+leaveTypeId;
    $('#leaveBalance').html('');
    jQuery('#ghssDropDown').hide();
    jQuery('#chDate').html('')
    if(employeeId =="" || leaveTypeId==""){
       
          return false;
     }
     if(leaveTypeId =="CH" || leaveTypeId =="CHGH" || leaveTypeId =="CHSS"){
           return viewGHandSS();
      }
     
     
    $.ajax({
             type: "POST",
             url: "getEmployeeLeaveBalance",
             data: data,
             dataType: 'json',
             success: function(result) {
            	 var emplLeaveBalance = result["leaveBalances"];
            	 if(emplLeaveBalance == undefined){
            	   return;
            	 }
            	 var leaveBalance = emplLeaveBalance[leaveTypeId];
            	 if(leaveBalance == 0){
            	   $('#leaveBalance').html('<span style="color:red; font-size:11pt; font-stlye:bold"> no leaves available, you can not apply for leave.</span>');
            	   $("#fromDate").attr("disabled", "disabled");
            	   $("#thruDate").attr("disabled", "disabled");
            	   $("#submit").attr("disabled", "disabled");
            	   return false;
            	 }
            	  $("#fromDate").attr("disabled", false);
            	  $("#thruDate").attr("disabled", false);
            	  $("#submit").attr("disabled", false);
            	  
            	 if(leaveBalance == undefined ){
            	    leaveBalance =0;
            	 }
            	 $('#leaveBalance').html('<span class="tooltip" style="color:green; font-size:11pt; font-stlye:bold">Balance as on '+result["leaveBalanceDateStr"]+" is : "+ leaveBalance +'   </span>');
            	   
            	 },
            error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
     });
         	 
 }
 
 function setFormParams(){
   $('[name="fromDate"]').datepicker( "option", "dateFormat", "yy-mm-dd 00:00:00");
   $('[name="thruDate"]').datepicker( "option", "dateFormat", "yy-mm-dd 00:00:00");
 }

  
</script>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3><left>Create New Leave</left></h3>
    </div>
    <div class="screenlet-body">

         <form name="EditEmplLeave" id = "EditEmplLeave" method="post" action="${target}" onsubmit="setFormParams()">
            <input type="hidden" name="leaveStatus" value ="LEAVE_CREATED"/>
             <input type="hidden" name="emplLeaveApplId" value ="${parameters.emplLeaveApplId?if_exists}"/>
            <input type="hidden" name="appliedBy" value =""/>
            <table class="basic-table" cellspacing="0">
              <tr>
                <td class="label">${uiLabelMap.EmployeeId} :</td>
                <td>
                   <#if !editFlag>
                      <@htmlTemplate.lookupField value="${parameters.partyId?if_exists}" formName="EditEmplLeave" name="partyId" id="partyId" fieldFormName="LookupEmployeeName"/>
                    <span class="tooltip"></span>
                    <#else>
                     <input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>
                     ${partyId?if_exists}
                  </#if> 
                    
                </td>
              </tr>
               <tr>
                <td class="label">From Date :</td>
                <td>
                  <#if !editFlag>
                   	 <input type="text" name="fromDate" id="fromDate" value="${fromDate?if_exists}" readonly/>
                    <#else>
                      <input type="hidden" name="fromDate" id="fromDate" value="${fromDate?if_exists}"/>
                     ${fromDate?if_exists}
                  </#if> 
                </td>
              </tr>
              <tr>
                <td class="label">Leave Type :</td>
                <td>
                   <#if !editFlag>
		               <select name="leaveTypeId" class='h4' onchange="displayLeaveBalance();">
		                   <option value=''></option>
							<#list leaveTypeList as leaveType>
							      <#if leaveTypeId?exists && (leaveTypeId == leaveType.leaveTypeId)>
		  					             <option  value="${leaveTypeId}" selected="selected">${leaveType.description}</option>
				      					<#else>
				      						<option value='${leaveType.leaveTypeId}'>${leaveType.description}</option>
				                  		</option>
		  				          </#if>    
							</#list>      
						</select>
					  <#else>
					      <input type="hidden" name="leaveTypeId" id="leaveTypeId" value="${leaveTypeId}"/>
						  ${leaveTypeId}
					</#if>	
					<span id="leaveBalance" name="leaveBalance" class="tooltip" wrap="wrap"></span>
                </td>
              </tr>
             <tr>
                <td class="label">Leave ReasonType</td>
                <td>
                    <option value=''></option>
                    <select name="emplLeaveReasonTypeId" class='h4'>
                       <option value=''></option>
						<#list emplLeaveReasonTypeList as emplLeaveReasonType>
						    <#if emplLeaveReasonTypeId?exists && (emplLeaveReasonTypeId == emplLeaveReasonType.emplLeaveReasonTypeId)>
      					          <option  value="${emplLeaveReasonTypeId}" selected="selected">${emplLeaveReasonType.description}</option>
			      				<#else>    
								<option value='${emplLeaveReasonType.emplLeaveReasonTypeId}'>${emplLeaveReasonType.description}</option>
							</#if>
						</#list>      
					</select>
                </td>
              </tr>
               <tr>
                <td class="label">Thru Date :</td>
                <td>
                   <input type="text" name="thruDate" id="thruDate" value="${thruDate?if_exists}" readonly/>
                </td>
              </tr>
              <tr id="ghssDropDown">
                <td class="label">GH/SS Days</td>
                <td>
                    <select name="chDate" id="chDate" class='h4' multiple="multiple">
                       <option value=''></option>
					</select>
                </td>
              </tr>
              <tr>
                <td class="label">First/Second Half :</td>
                <td>
                   <#list dayFractionList as dayFraction>
                       <#if dayFractionId?exists && (dayFractionId == dayFraction.enumId)>
      					     <input type="radio" name="dayFractionId" value="${dayFractionId}" checked="checked">${dayFraction.description}
			      		<#else> 
                       		<input type="radio" name="dayFractionId" value="${dayFraction.enumId}">${dayFraction.description}
                       </#if>
				   </#list>
				   <span class="tooltip" id="punchDetails" name="punchDetails"></span>
                </td>
              </tr>
              <tr>
                <td class="label">Approver PartyId :</td>
                <td>
                  <@htmlTemplate.lookupField value="${approverPartyId?if_exists}" formName="EditEmplLeave" name="approverPartyId" id="approverPartyId" fieldFormName="LookupEmployeeName"/>
                  <span class="tooltip"></span>
                </td>
              </tr>
               <tr>
                <td class="label">${uiLabelMap.Comments} :</td>
                <td>
                    <input type="text" name="comment" value="${comment?if_exists}" size="70"/>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td>
                  &nbsp;
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td>
                	<input type="submit" name="submit" id="submit" value="Create" style="buttontext"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;
                	<a name="cancel" id="cancel" value="Cancel" class="smallSubmit" href='<@ofbizUrl>FindEmplLeaves</@ofbizUrl>'>Cancel</a>
                </td>
              </tr>
            </table>
          </form>
   </div>
   </div>       