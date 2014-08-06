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
<html>
<head>
 <script>
  function leapTo (link)
   {
   var new_url=link;
   if (  (new_url != "")  &&  (new_url != null)  )
      window.location=new_url;
   else
      alert("\nYou must make a selection.");
   }

   function empclick(edate)
   {
     var current_date = new Date();
     month_value = current_date.getMonth()+1;
     day_value = current_date.getDate();
     year_value = current_date.getFullYear();

     if(month_value<10 && day_value<10)
     var todaydate = "0"+day_value+"/"+"0"+month_value+"/"+year_value;
     else if(day_value<10)
     var todaydate = "0"+day_value+"/"+month_value+"/"+year_value;
     else if(month_value<10)
     var todaydate = day_value+"/"+"0"+month_value+"/"+year_value;
     else
     var todaydate = day_value+"/"+month_value+"/"+year_value;
      
      if( edate == todaydate)
      document.getElementById('empdate').value=1;
      else
      document.getElementById('empdate').value=0;
    }
 </script>

<script type="text/javascript">

function inputDateConversion() {	
	 flag=true;
	var test = document.getElementById("sdate").value;	
	var current_date = new Date(test);
	var res = current_date.getTime();
	document.selectdate.start.value=res;	
	
}

function dateValidation(){
	flag=true;	
	var test = document.getElementById("sdate").value;
	var current_date = new Date(test);
	var res = current_date.getTime();	
	if(isNaN(res)){	
	 document.getElementById('response').innerHTML = 'incorrect Date format Please verify(Format: MM/dd/yyyy)'; 
	 flag=false;
	}	 
    return flag;
	
  }

</script> 


 </head>
 
<body onLoad="window.refresh()">
<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
    <a href="<@ofbizUrl>WeeklyInOut.csv</@ofbizUrl>">Daily Report</a> 
</#if>
<div class="bothclear">
	<div class="screenlet">
  		<div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.CommonWeek} ${start?date?string("w")}</li>
      <li><a href="<@ofbizUrl>week?start=${next.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>">${uiLabelMap.WorkEffortNextWeek}</a></li>
      <li><a href="<@ofbizUrl>week?start=${nowTimestamp.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>">${uiLabelMap.WorkEffortThisWeek}</a></li>
      <li><a href="<@ofbizUrl>week?start=${prev.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>">${uiLabelMap.WorkEffortPreviousWeek}</a></li>
    </ul>
    <br class="clear"/>
  </div>
  </div>
</div>

<form name="selectdate" onSubmit="inputDateConversion()"><b>DATE</b>

<input type="hidden" name="start" id="sdate"/><input type="hidden" name="partyId" id="partyId" value="${partyId}"/>
<@htmlTemplate.renderDateTimeField name="x" event="" action="" value="" className="" alert="" title="Format: MM/dd/yyyy" size="25" maxlength="30" id="temp" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
<div id="response" class="error"></div>
<input type="submit" value="Search" id="button1" class="smallSubmit" onClick="document.getElementById('sdate').value=document.getElementById('temp').value;return dateValidation()"/>
</form>


<#if periods?has_content> 
  <#if (maxConcurrentEntries < 2)>
    <#assign entryWidth = 100>
  <#else> 
    <#assign entryWidth = (100 / (maxConcurrentEntries))>
  </#if>
<table cellspacing="1" class="basic-table calendar">              
 

   <td style="background-color:#C0C0C0;"></td>
  <td class="centered" style="background-color:#C0C0C0;"><h4>DAYS</h4></td>
  <#list periods as period>
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>

 
  <td<#if currentPeriod> style="background-color:#C0C0C0;"  class="centered" class="current-period"<#else><#if (period.calendarEntries?size > 0)> class="active-period" style="background-color:#C0C0C0;"</#if></#if> align="center" style="background-color:#C0C0C0;">
  
     <h4>${period.start?date?string("EEEE")?cap_first}&nbsp;<br/>${period.start?date?string("dd/MM/yyyy")}</h4>
   </td> 

  </#list>
  

<#-- for in/out --------------------------------------------------------------------------------------------------------------->
 <#if data?exists >

 <#--  ------------------------------------- this is for punch type Normal --------------------------------------------------  -->

<#-- <input name="l" type="text" value="${partyId?if_exists}"/>  -->
 <#assign tempcnt=0>
   <#list periods as period>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Normal">
        <#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#if> 
      </#list>
        <#if (tempcnt<iocnt)>
        <#assign tempcnt=iocnt>
        </#if>
     </#list>

<#assign cnt=tempcnt>
<#list 1..tempcnt as i>
<#list punchTypeList as punchtype>

 <tr>  
  	<td class="centered" rowspan="3" style="background-color:#F0F8FF;"><h3>${punchtype.description?if_exists}</h3></td>
		  <#list inOutList as inOut>
		  <tr>
			  <td class="centered" style="background-color:#F0F8FF;"><h4>${inOut.description?if_exists}</h4></td>
			  	<#list periods as period>
			    	<#assign cellidt1=period.start?date>
			     	<#assign cellid2=cellidt1?substring(4,6)>
			       	<#if cellid2?ends_with(",")>
			            <#assign cellid2=cellidt1?substring(4,5)>  
			        </#if>    
			     	<#assign cellid = (cellid2?number % 7)>
			     	<#if cellid=0>
			      		<#assign cellid=7>
			     	</#if>
			     	<#if (i>1)>
			       		<#list 1..(i-1) as li>
			         		<#assign cellid=cellid+7>
			       		</#list>
			      	</#if>
			    	<#assign currentPeriod = false/>
			    	<#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
			   			<#assign x=0>
			 			<input name="empdate" id="empdate" type="hidden"/>
			   			<#assign x=0>
						  <td   class="centered" style="background-color:#F0F8FF;cursor:pointer" onMouseover="this.style.backgroundColor='lightgrey';"  
						onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
						 <#list data as Records>
							<#if Records.InOut="${inOut.enumId}">
								<#if period.start?date=Records.punchdate>   
									<#if Records.PunchType="${punchtype.enumId}">  
								  		<#if Records.emplPunchId=cellid?string>     
								  			<#assign x=1>
											<#break> 
								 		</#if>
								 	</#if>
							 	</#if>
							</#if> 
						</#list>
					   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=${punchtype.enumId?if_exists}&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
					   <#else>
							 <#list data as Records>
						     
						  		<#if period.start?date=Records.punchdate>   
						    		<#if Records.PunchType="${punchtype.enumId}">   
						      			<#if Records.emplPunchId=cellid?string>    
						      				onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=${punchtype.enumId?if_exists}&InOut=${inOut.enumId?if_exists}&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}&shiftType=${Records.shiftType?string}')" 
						     				<#assign x=0>
						        			<#break> 
						     			</#if>
						     		</#if>
						     	</#if>
						  	</#list>
					    </#if>
			
			  		<#else>
			
					   	<#list data as Records>
							<#if Records.InOut="${inOut.enumId}">
								<#if period.start?date=Records.punchdate>   
									<#if Records.PunchType="${punchtype.enumId}">  
							  			<#if Records.emplPunchId=cellid?string>     
							  				<#assign x=1>        
							 			</#if>
							 		</#if>
							 	</#if>
							 </#if> 
					   	</#list>
				   
						<#assign emp=period.start?string("dd/MM/yyyy")>
						<#if x!=1>
							onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=${punchtype.enumId?if_exists}&InOut=${inOut.enumId?if_exists}&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
							<#assign x=0>
						<#else>
							onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
							<#assign x=1>
						</#if>
			  		</#if> >  
			  
					  <#list data as Records>
						  <#-- changing tr to td -->
						      <#if Records.InOut="${inOut.enumId}">   
						  		<#if period.start?date=Records.punchdate>   
						    		<#if Records.PunchType="${punchtype.enumId}"> 
						       			<#if Records.emplPunchId=cellid?string>
						      				${Records.punchtime} 
						        		</#if>   
						     		</#if>
						     	</#if>
						     </#if>
					  </#list>
					</td>                                           
		  
				</#list> 
				</td>    
			</tr> 
		</#list>
  		</tr> 
	</#list>
		<#if tempcnt =0><#break></#if>
</#list>
</table>
<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
<#else>
		<input type="submit" value="Contact Admin" class="smallSubmit" onClick="leapTo('/humanres/control/NewMailemp?communicationEventTypeId=EMAIL_COMMUNICATION&donePage=${parameters.donePage?if_exists}&partyIdFrom=${userLogin.partyId}&statusId=COM_PENDING')">
</#if>
<#else>               
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
</#if>
</body>
</html>


