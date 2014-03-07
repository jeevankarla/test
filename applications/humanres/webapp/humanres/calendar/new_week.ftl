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
 <tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Normal</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>IN</h4></td>
   


 
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
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
  

   <#if x!=1>  onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">   
      <#if Records.emplPunchId=cellid?string>    
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if>
     </#if></#if></#if>
   
  </#list>
       </#if>

  <#else>

   <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>        
     </#if></#if>
     </#if></#if> 
   </#list>
   
<#assign emp=period.start?string("dd/MM/yyyy")>
<#if x!=1>
onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
<#assign x=0>
<#else>
onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
<#assign x=1>
</#if>





  </#if>    >  
  
  <#list data as Records>
  <#-- changing tr to td -->
      <#if Records.InOut!="OUT">   
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
       <#if Records.emplPunchId=cellid?string>
      ${Records.punchtime} 
        </#if>   
     </#if>
     </#if></#if>
  
  </#list>
   
    </td>                                           
  
   
  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>OUT</h4></td>
 
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
   
  <td class="centered" style="background-color:#F0F8FF;cursor:pointer" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';"<#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">    
      <#if Records.emplPunchId=cellid?string>   
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
       <#if Records.emplPunchId=cellid?string>     
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if>  

 <#else>
<#assign x=0>
   <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>        
     </#if></#if>
     </#if></#if> 
   </#list>
   
<#assign emp=period.start?string("dd/MM/yyyy")>
<#if x!=1>
onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Normal&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
<#assign x=0>
<#else>
onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
<#assign x=1>
</#if>



</#if>  >  

  
  <#list data as Records>
  <#-- changing tr to td -->
      <#if Records.InOut="OUT">   
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Normal"> 
     <#if Records.emplPunchId=cellid?string>  
      ${Records.punchtime}  
        </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  
 
  </#list>
  </tr> 
   <#if tempcnt = 0><#break></#if>

</#list>
<#--   ------------------------------------- this is for punch type Lunch -------------------------------------------------------   -->

 <#assign tempcnt=0>
   <#list periods as period>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Lunch">
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
<tr>  
  <td class="centered" rowspan="2"><h3>Lunch</h3></td>
  
  <td class="centered"><h4>STEP-OUT</h4></td>
 
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
  <td class="centered" style="background-color:white;cursor:pointer"  onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">   
      <#if Records.emplPunchId=cellid?string>    
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch"> 
      <#if Records.emplPunchId=cellid?string>      
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if> 

 <#else>

   <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>        
     </#if></#if>
     </#if></#if> 
   </#list>
   
<#assign emp=period.start?string("dd/MM/yyyy")>
<#if x!=1>
onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
<#assign x=0>
<#else>
onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
<#assign x=1>
</#if>


 </#if> > 

   <#--    <td class="centered" style="background-color:white" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';"> -->

  <#list data as Records>
   
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
    <#if Records.emplPunchId=cellid?string>
    <#--  <a href="adminPunch?partyId=${partyId}&punchdate=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${Records.emplPunchId}&dateTime=${Records.dateTime}&PunchType=${Records.PunchType}&InOut=${Records.InOut}">  -->
      ${Records.punchtime} 
  <#--   </a> -->
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  

  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered"><h4>RETURN</h4></td>
 
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
  <td class="centered" style="background-color:white;cursor:pointer" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='white';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">   
     <#if Records.emplPunchId=cellid?string>    
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">       
      <#if Records.emplPunchId=cellid?string>
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if> 

 <#else>

   <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Lunch">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>        
     </#if></#if>
     </#if></#if> 
   </#list>
   
<#assign emp=period.start?string("dd/MM/yyyy")>
<#if x!=1>
onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Lunch&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
<#assign x=0>
<#else>
onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
<#assign x=1>
</#if>


 </#if> >  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch">  
     <#if Records.emplPunchId=cellid?string>       
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  
 
  </#list>
    
  </tr> 
 <#if tempcnt = 0><#break></#if>

</#list>

<#--   ---------------------------------- this is for punch type Break ----------------------------------------------------------------   -->
 <#assign tempcnt=0>
   <#list periods as period>
       <#assign iocnt=0>
       <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#list data as Records>
        <#if period.start?date=Records.punchdate>
      <#if Records.PunchType="Break">
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
<tr>  
  <td class="centered" rowspan="2" style="background-color:#F0F8FF;"><h3>Break</h3></td>
  
  <td class="centered" style="background-color:#F0F8FF;"><h4>STEP-OUT</h4></td>
 
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
  <td class="centered" style="background-color:#F0F8FF;cursor:pointer" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
       <#if Records.emplPunchId=cellid?string>      
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">   
     <#if Records.emplPunchId=cellid?string>    
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if> 

 <#else>

   <#list data as Records>
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>        
     </#if></#if>
     </#if></#if> 
   </#list>
   
<#assign emp=period.start?string("dd/MM/yyyy")>
<#if x!=1>
onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=IN&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
<#assign x=0>
<#else>
onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
<#assign x=1>
</#if>


 </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut!="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">  
     <#if Records.emplPunchId=cellid?string>     
      ${Records.punchtime}   
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
    </td>                                           
  

  </#list>
  </tr> 

<#-- writing for out -->

<tr>  
  <td class="centered" style="background-color:#F0F8FF;"><h4>RETURN</h4></td>
 
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
  <td class="centered" style="background-color:#F0F8FF;cursor:pointer" onMouseover="this.style.backgroundColor='lightgrey';"  
onMouseout="this.style.backgroundColor='#F0F8FF';" <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session))>
 <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">    
     <#if Records.emplPunchId=cellid?string>   
      <#assign x=1>
        <#break> 
     </#if></#if>
     </#if></#if> 
   </#list>
   <#if x!=1> onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')"
<#else> <#list data as Records>
      <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break"> 
     <#if Records.emplPunchId=cellid?string>      
      onClick="leapTo('/humanres/control/admPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&punchtime=${Records.punchtime}&emplPunchId=${cellid?string}')" 
     <#assign x=0>
        <#break> 
     </#if></#if>
     </#if></#if>
   
  </#list>
       </#if> 

 <#else>

   <#list data as Records>
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate>   
    <#if Records.PunchType="Break">  
      <#if Records.emplPunchId=cellid?string>     
      <#assign x=1>        
     </#if></#if>
     </#if></#if> 
   </#list>
   
<#assign emp=period.start?string("dd/MM/yyyy")>
<#if x!=1>
onClick="empclick('${emp}');if(document.getElementById('empdate').value==1){leapTo('/humanres/control/EmpPunch?partyId=${partyId}&punchdate=${period.start?string}&PunchType=Break&InOut=OUT&dateTime=${period.start?string}&emplPunchId=${cellid?string}')}else{alert('You cant modify the record.\n\nPlease contact Admin');}"
<#assign x=0>
<#else>
onClick="alert('You cant modify the record.\n\nPlease contact Admin');"
<#assign x=1>
</#if>



 </#if>>  

  <#list data as Records>
  <#-- changing tr to td -->
 
    <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">    
     <#if Records.emplPunchId=cellid?string>     
      ${Records.punchtime}   
     <#break>
    </#if></#if>
   </#if></#if>
   
  </#list> 
    </td>                                           
  
 
  </#list>
  </tr> 
 <#if tempcnt = 0><#break></#if>

</#list>


<#--  this is to calculate no. of worked per day ---------------------------------------------------------------------->

<tr>  
  
  <td class="centered" style="background-color:#CCFFFF;" colspan="2"><h4>DAY TOTAL</h4></td>

 <#list periods as period>
  <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
   
  <td class="centered" style="background-color:#CCFFFF;">


<#-- this is for normal ---------------------------------------->
  <#assign normalcnt=0>
<#list data as Records>
 <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal"> 
      <#if Records.punchtime?has_content> 
         <#assign normalcnt=normalcnt+1>
      </#if>
     </#if>
  </#if>
</#list>

 <#assign daytotal=0>
 <#assign hrni=0>
 <#assign minni=0>
 <#assign secni=0>
 <#assign hrno=0>
 <#assign minno=0>
 <#assign secno=0>
  <#list data as Records>
     <#if Records.InOut="IN">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal"> 
      <#if Records.punchtime?has_content> 
      <#if (Records.punchtime?length>10)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#assign temphrni=Records.punchtime?substring(0,2)>
       <#assign tempminni=Records.punchtime?substring(3,5)>
        <#assign tempsecni=Records.punchtime?substring(6,8)>
       <#else>
       <#assign ampm=Records.punchtime?substring(8,10)>
      <#assign temphrni=Records.punchtime?substring(0,1)>
      <#assign tempminni=Records.punchtime?substring(2,4)>
      <#assign tempsecni=Records.punchtime?substring(5,7)>
       </#if>
         <#if ampm="PM" && (temphrni !="12")>
     <#--  <input name="k" type="text" value="${temphrni}"/>  -->
             <#assign hrni=hrni+temphrni?number+12>
         <#else>
        <#assign hrni=hrni+temphrni?number>
        </#if>
        <#assign minni=minni+tempminni?number>
        <#assign secni=secni+tempsecni?number>
         
    </#if></#if>
   </#if></#if>
   
    

  
     <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal">  
      <#if Records.punchtime?has_content>
      <#if (Records.punchtime?length>10)>
       <#assign temphrno=Records.punchtime?substring(0,2)>
       <#assign tempminno=Records.punchtime?substring(3,5)>
        <#assign tempsecno=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrno=Records.punchtime?substring(0,1)>
      <#assign tempminno=Records.punchtime?substring(2,4)>
        <#assign tempsecno=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>
        
        <#if ampm="PM">
        <#assign hrno=hrno+temphrno?number+12>
        <#else>
        <#assign hrno=hrno+temphrno?number>
        </#if>
        <#assign minno=minno+tempminno?number>
        <#assign secno=secno+tempsecno?number>

    </#if>

    </#if>
   </#if></#if>
   
  </#list> 

 <#if normalcnt%2 != 0>
        <#assign emplid=1>
        <#list data as Records>
     <#if Records.InOut="IN">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Normal"> 
      <#if Records.punchtime?has_content>   
      <#if (Records.emplPunchId?number>emplid)>
       <#assign emplid=Records.emplPunchId?number>   
      <#if (Records.punchtime?length>10)>       
       <#assign temphrni=Records.punchtime?substring(0,2)>
       <#assign tempminni=Records.punchtime?substring(3,5)>
        <#assign tempsecni=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrni=Records.punchtime?substring(0,1)>
      <#assign tempminni=Records.punchtime?substring(2,4)>
      <#assign tempsecni=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>
       </#if>
      
         
    </#if></#if>
   </#if></#if>
  
   </#list>
        <#if ampm="PM">
        <#assign hrni=hrni-temphrni?number-12>
        <#else>
        <#assign hrni=hrni-temphrni?number>
        </#if>
        <#assign minni=minni-tempminni?number>
        <#assign secni=secni-tempsecni?number>

 </#if>



  <#--   <input name="k" type="text" value="${hrno}"/>   -->
   <#assign tempdayhrn=hrno-hrni>
   <#assign tempdayminn=minno-minni>
   <#assign tempdaysecn=secno-secni>

   <#list 1..3 as x>
<#if (tempdaysecn<0)>
<#assign tempdaysecn=60+tempdaysecn>
<#assign tempdayminn=tempdayminn-1>
</#if>
</#list>

<#list 1..3 as x>
<#if (tempdayminn<0)>
<#assign tempdayminn=60+tempdayminn>
<#assign tempdayhrn=tempdayhrn-1>
</#if>
</#list>
    


   <#assign daysecn=tempdaysecn%60>
   <#assign tempdayminn=tempdayminn+(tempdaysecn/60)?int>
   <#assign dayminn=tempdayminn%60>
   <#assign dayhrn=tempdayhrn+(tempdayminn/60)?int>
  



<#-- this is for lunch ---------------------------------------->
  <#assign lunchcnt=0>
<#list data as Records>
 <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch"> 
      <#if Records.punchtime?has_content> 
         <#assign lunchcnt=lunchcnt+1>
      </#if>
     </#if>
  </#if>
</#list>

 <#assign daytotal=0>
 <#assign hrli=0>
 <#assign minli=0>
 <#assign secli=0>
 <#assign hrlo=0>
 <#assign minlo=0>
 <#assign seclo=0>
  <#list data as Records>
     <#if Records.InOut="IN">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch"> 
      <#if Records.punchtime?has_content> 
      <#if (Records.punchtime?length>10)>
       <#assign temphrli=Records.punchtime?substring(0,2)>
       <#assign tempminli=Records.punchtime?substring(3,5)>
        <#assign tempsecli=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrli=Records.punchtime?substring(0,1)>
      <#assign tempminli=Records.punchtime?substring(2,4)>
      <#assign tempsecli=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>

    <#--  <input name="k" type="text" value="${temphrli}"/>  -->
        <#if ampm="PM">
        <#assign hrli=hrli+temphrli?number+12>
        <#else>
        <#assign hrli=hrli+temphrli?number>
        </#if>
        <#assign minli=minli+tempminli?number>
        <#assign secli=secli+tempsecli?number>
         
    </#if></#if>
   </#if></#if>
   
    

  
     <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch">  
      <#if Records.punchtime?has_content>
      <#if (Records.punchtime?length>10)>
       <#assign temphrlo=Records.punchtime?substring(0,2)>
       <#assign tempminlo=Records.punchtime?substring(3,5)>
        <#assign tempseclo=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrlo=Records.punchtime?substring(0,1)>
      <#assign tempminlo=Records.punchtime?substring(2,4)>
        <#assign tempseclo=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>
        
        <#if ampm="PM">
        <#assign hrlo=hrlo+temphrlo?number+12>
        <#else>
        <#assign hrlo=hrlo+temphrlo?number>
        </#if>
        <#assign minlo=minlo+tempminlo?number>
        <#assign seclo=seclo+tempseclo?number>

    </#if>

    </#if>
   </#if></#if>
   
  </#list> 

 <#if lunchcnt%2 != 0>
        <#assign emplid=1>
        <#list data as Records>
     <#if Records.InOut="IN">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Lunch"> 
      <#if Records.punchtime?has_content>   
      <#if (Records.emplPunchId?number>emplid)>
       <#assign emplid=Records.emplPunchId?number>   
      <#if (Records.punchtime?length>10)>       
       <#assign temphrli=Records.punchtime?substring(0,2)>
       <#assign tempminli=Records.punchtime?substring(3,5)>
        <#assign tempsecli=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrli=Records.punchtime?substring(0,1)>
      <#assign tempminli=Records.punchtime?substring(2,4)>
      <#assign tempsecli=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>
       </#if>
      
         
    </#if></#if>
   </#if></#if>
  
   </#list>
        <#if ampm="PM">
        <#assign hrli=hrli-temphrli?number-12>
        <#else>
        <#assign hrli=hrli-temphrli?number>
        </#if>
        <#assign minli=minli-tempminli?number>
        <#assign secli=secli-tempsecli?number>

 </#if>



  <#--   <input name="k" type="text" value="${hrno}"/>   -->
   <#assign tempdayhrl=hrlo-hrli>
   <#assign tempdayminl=minlo-minli>
   <#assign tempdaysecl=seclo-secli>

 <#list 1..3 as x>
<#if (tempdaysecl<0)>
<#assign tempdaysecl=60+tempdaysecl>
<#assign tempdayminl=tempdayminl-1>
</#if>
</#list>

<#list 1..3 as x>
<#if (tempdayminl<0)>
<#assign tempdayminl=60+tempdayminl>
<#assign tempdayhrl=tempdayhrl-1>
</#if>
</#list>
    



   <#assign daysecl=tempdaysecl%60>
   <#assign tempdayminl=tempdayminl+(tempdaysecl/60)?int>
   <#assign dayminl=tempdayminl%60>
   <#assign dayhrl=tempdayhrl+(tempdayminl/60)?int>


<#-- this is for break ---------------------------------------->
  <#assign breakcnt=0>
<#list data as Records>
 <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break"> 
      <#if Records.punchtime?has_content> 
         <#assign breakcnt=breakcnt+1>
      </#if>
     </#if>
  </#if>
</#list>

 <#assign daytotal=0>
 <#assign hrbi=0>
 <#assign minbi=0>
 <#assign secbi=0>
 <#assign hrbo=0>
 <#assign minbo=0>
 <#assign secbo=0>
  <#list data as Records>
     <#if Records.InOut="IN">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break"> 
      <#if Records.punchtime?has_content> 
      <#if (Records.punchtime?length>10)>
       <#assign temphrbi=Records.punchtime?substring(0,2)>
       <#assign tempminbi=Records.punchtime?substring(3,5)>
        <#assign tempsecbi=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrbi=Records.punchtime?substring(0,1)>
      <#assign tempminbi=Records.punchtime?substring(2,4)>
      <#assign tempsecbi=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>

    <#--  <input name="k" type="text" value="${temphrli}"/>  -->
        <#if ampm="PM">
        <#assign hrbi=hrbi+temphrbi?number+12>
        <#else>
        <#assign hrbi=hrbi+temphrbi?number>
        </#if>
        <#assign minbi=minbi+tempminbi?number>
        <#assign secbi=secbi+tempsecbi?number>
         
    </#if></#if>
   </#if></#if>
   
    

  
     <#if Records.InOut="OUT">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break">  
      <#if Records.punchtime?has_content>
      <#if (Records.punchtime?length>10)>
       <#assign temphrbo=Records.punchtime?substring(0,2)>
       <#assign tempminbo=Records.punchtime?substring(3,5)>
        <#assign tempsecbo=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrbo=Records.punchtime?substring(0,1)>
      <#assign tempminbo=Records.punchtime?substring(2,4)>
        <#assign tempsecbo=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>
        
        <#if ampm="PM">
        <#assign hrbo=hrbo+temphrbo?number+12>
        <#else>
        <#assign hrbo=hrbo+temphrbo?number>
        </#if>
        <#assign minbo=minbo+tempminbo?number>
        <#assign secbo=secbo+tempsecbo?number>

    </#if>

    </#if>
   </#if></#if>
   
  </#list> 

 <#if breakcnt%2 != 0>
        <#assign emplid=1>
        <#list data as Records>
     <#if Records.InOut="IN">
  <#if period.start?date=Records.punchdate> 
    <#if Records.PunchType="Break"> 
      <#if Records.punchtime?has_content>   
      <#if (Records.emplPunchId?number>emplid)>
       <#assign emplid=Records.emplPunchId?number>   
      <#if (Records.punchtime?length>10)>       
       <#assign temphrbi=Records.punchtime?substring(0,2)>
       <#assign tempminbi=Records.punchtime?substring(3,5)>
        <#assign tempsecbi=Records.punchtime?substring(6,8)>
       <#assign ampm=Records.punchtime?substring(9,11)>
       <#else>
      <#assign temphrbi=Records.punchtime?substring(0,1)>
      <#assign tempminbi=Records.punchtime?substring(2,4)>
      <#assign tempsecbi=Records.punchtime?substring(5,7)>
       <#assign ampm=Records.punchtime?substring(8,10)>
       </#if>
       </#if>
      
         
    </#if></#if>
   </#if></#if>
  
   </#list>
        <#if ampm="PM">
        <#assign hrbi=hrbi-temphrbi?number-12>
        <#else>
        <#assign hrbi=hrbi-temphrbi?number>
        </#if>
        <#assign minbi=minbi-tempminbi?number>
        <#assign secbi=secbi-tempsecbi?number>

 </#if>



  <#--   <input name="k" type="text" value="${hrno}"/>   -->
   <#assign tempdayhrb=hrbo-hrbi>
   <#assign tempdayminb=minbo-minbi>
   <#assign tempdaysecb=secbo-secbi>

 <#list 1..3 as x>
<#if (tempdaysecb<0)>
<#assign tempdaysecb=60+tempdaysecb>
<#assign tempdayminb=tempdayminb-1>
</#if>
</#list>

<#list 1..3 as x>
<#if (tempdayminb<0)>
<#assign tempdayminb=60+tempdayminb>
<#assign tempdayhrb=tempdayhrb-1>
</#if>
</#list>
    


   <#assign daysecb=tempdaysecb%60>
   <#assign tempdayminb=tempdayminb+(tempdaysecb/60)?int>
   <#assign dayminb=tempdayminb%60>
   <#assign dayhrb=tempdayhrb+(tempdayminb/60)?int>
   
 <#-- <#assign daytotal=daytotalni-daytotalno>  -->

<#assign dayhr=dayhrn-dayhrl-dayhrb>
<#assign daymin=dayminn-dayminl-dayminb>
<#assign daysec=daysecn-daysecl-daysecb>
<#list 1..3 as x>
<#if (daysec<0)>
<#assign daysec=60+daysec>
<#assign daymin=daymin-1>
</#if>
</#list>

<#list 1..3 as x>
<#if (daymin<0)>
<#assign daymin=60+daymin>
<#assign dayhr=dayhr-1>
</#if>
</#list>


  ${dayhr?string?left_pad(2, "0")}:${daymin?string?left_pad(2, "0")}:${daysec?string?left_pad(2, "0")}

    </td>                                           
  
 </#list>
 
  </tr> 



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


