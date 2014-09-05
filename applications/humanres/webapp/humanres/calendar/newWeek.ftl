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

<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datetimepicker/jquery-ui-timepicker-addon-0.9.3.js</@ofbizContentUrl>"></script>

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
</head>
<body>
<script type="text/javascript">


function setTimepicker(){
		$('#punchtime').timepicker({ 
		showSecond: true,	
		timeFormat: 'hh:mm:ss',
		showOn: 'button',
	        buttonImage: '/vasista/images/cal.gif',
	 });
}

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
//qtip
   
    
	function dialogue(content, title) {
		$('<div />').qtip({
			content: {
				text: content,
				title: title
			},
			position: {
				my: 'center', at: 'center', // Center it...
				target: $(window) // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: true, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
//				    setTimepicker();
					$('button', api.elements.content).click(api.hide);
					
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 
	function Alert(message, title)	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
		dialogue(message, title );		
		
	}
	
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	
function showPunchEditForm(partyId,punchType,punchdate,InOut,emplPunchId,punchtime,shiftType) {
		var message ="";
		      message +="<form id='editAdminPunch' action='adminPunch' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10  cellpadding=20>"; 		
			message +="<tr class='h3'><td align='left' class='h3' width='30%'>PartyId:</td><td align='left' class='h3' width='30%'><input type='text' value="+partyId+" name='partyId' readonly size='5' /></td></tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'>Punch Type:</td><td align='left' class='h3' width='40%'><select name='PunchType'  allow-empty='false' id='PunchType' class='h4'><option >"+punchType+"</option>"+
	              		"<#list punchTypeList as ptl><option value='${ptl.enumId}' >${ptl.enumId?if_exists}</option></#list>"+            
						"</select></td></tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'>IN/OUT:</td><td align='left' class='h3' width='40%'><input type='text' value="+InOut+" name='InOut' readonly size='5' /></td></tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'>Shift Type:</td><td align='left' class='h3' width='40%'><select name='shiftType'  allow-empty='false' id='shiftType' class='h4'><option >"+shiftType+"</option>"+
	              		"<#list shiftTypeList as stl><option value='${stl.shiftTypeId}' >${stl.shiftTypeId?if_exists}</option></#list>"+            
						"</select></td></tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'>Date:</td><td align='left' class='h3' width='40%'><input type='text' value="+punchdate+" name='punchdate' readonly size='10' /></td></tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'>Punch Time:</td><td align='left' class='h3' width='40%'><input type='text'  value="+punchtime+" name='punchtime' size='10' required />HH:MM:SS</tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'></td><td align='left' class='h3' width='40%'><input type='hidden' value="+emplPunchId+" name='emplPunchId' readonly size='5' /></td></tr>"+
			"<tr class='h3'><td align='left' class='h3' width='40%'>Note:</td><td align='left' class='h3' width='40%'><input  type='textarea' value='' name='Note'></td></tr>"+
			"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='adminPunch' class='smallSubmit'/></span></td><td class='h3' width='80%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message +="</table></form>";			
		var title = "<h2><center>Edit Punch</center></h2>";
		Alert(message, title);
	}
	

</script> 

<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />



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
       <#list punchTypeList as punchtype> 
      <#if Records.PunchType="${punchtype.enumId}">
       	<#if Records.InOut!="OUT">
          <#assign iocnt=iocnt+1> 
        </#if> 
       </#if> 
       </#list>
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
						<#assign date=Static["org.ofbiz.base.util.UtilDateTime"].toDateString(period.start, "yyyy-MM-dd")>
						<#assign punchtime="00:00:00">
						<#assign shiftType="SHIFT_GEN">
					   <#if x!=1>  onClick="showPunchEditForm('${partyId}','${punchtype.enumId?if_exists}','${date}','${inOut.enumId?if_exists}','${cellid?string}','${punchtime}','${shiftType?if_exists}')";<#--onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=${punchtype.enumId?if_exists}&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"-->
					   <#else>
							 <#list data as Records>
						     	<#if Records.InOut="${inOut.enumId}">
						  		<#if period.start?date=Records.punchdate>   
						    		<#if Records.PunchType="${punchtype.enumId}">   
						      			<#if Records.emplPunchId=cellid?string>   
						      			<#assign date=Static["org.ofbiz.base.util.UtilDateTime"].toDateString(period.start, "yyyy-MM-dd")> 
						      				<#--onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=${punchtype.enumId?if_exists}&InOut=${inOut.enumId?if_exists}&dateTime=${period.start?string}&punchtime=${Records.getString('punchtime')}&emplPunchId=${cellid?string}&shiftType=${Records.shiftType?if_exists}')"-->
						      				onClick="showPunchEditForm('${partyId}','${punchtype.enumId?if_exists}','${date}','${inOut.enumId?if_exists}','${cellid?string}','${Records.getString('punchtime')}','${Records.shiftType?if_exists}')";
						     				<#assign x=0>
						        			<#break> 
						     			</#if>
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

